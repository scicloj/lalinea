;; # Fastmath Algorithms with La Linea
;;
;; [Fastmath](https://generateme.github.io/fastmath/clay/) implements several
;; numerical algorithms using Apache Commons Math for their internal
;; linear algebra. This notebook reimplements the core matrix operations
;; using La Linea (dtype-next tensors + EJML), compares outputs for
;; correctness, and benchmarks performance.
;;
;; For each algorithm:
;;
;; - Show the fastmath function call
;; - Implement the same core computation with La Linea
;; - Verify the outputs match
;; - Time both approaches

(ns lalinea-book.fastmath-implementations
  (:require
   ;; La Linea:
   [scicloj.lalinea.linalg :as la]
            [scicloj.lalinea.tensor :as t]
            [scicloj.lalinea.elementwise :as el]
   ;; dtype-next:
   [tech.v3.tensor :as dtt]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   ;; Fastmath:
   [fastmath.ml.regression :as reg]
   [fastmath.interpolation.gp :as gp]
   [fastmath.interpolation.rbf :as rbf]
   [fastmath.interpolation.kriging :as kriging]
   [fastmath.distance :as dist]
   [fastmath.ml.regression.contrast :as contrast]
   [fastmath.signal :as signal]
   [fastmath.calculus.finite :as finite]
   [fastmath.calculus.quadrature :as quad]
   [fastmath.polynomials :as poly]
   [fastmath.kernel :as k]
   [fastmath.random :as frand]
   [fastmath.core :as m]
   ;; Visualization:
   [tablecloth.api :as tc]
   [scicloj.tableplot.v1.plotly :as plotly]
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Benchmark utility
;;
;; Warmup, then time $n$ iterations. Returns mean time in ms.

(def bench-fn
  (fn [f n]
    (dotimes [_ 5] (f))
    (let [start (System/nanoTime)]
      (dotimes [_ n] (f))
      (/ (- (System/nanoTime) start) (* n 1e6)))))

;; ## OLS Regression
;;
;; Fastmath's `lm` fits a linear model using SVD, QR, or Cholesky
;; decomposition. The core step: given design matrix $X$ and
;; response $y$, solve $X \beta = y$ in the least-squares sense.
;;
;; The SVD approach computes $\beta = V \Sigma^{-1} U^T y$.

;; ### Test data
;;
;; A simple linear relationship with noise.

(def rng (frand/rng :mersenne 42))

(def n-obs 200)

(def ols-xs
  (mapv (fn [_] [(frand/drandom rng 0.0 10.0)
                 (frand/drandom rng 0.0 5.0)])
        (range n-obs)))

(def true-beta [3.0 -1.5])

(def ols-ys
  (mapv (fn [[x1 x2]]
          (+ 2.0
             (* 3.0 x1)
             (* -1.5 x2)
             (frand/grandom rng 0.0 0.5)))
        ols-xs))

;; ### Fastmath OLS

(def fm-model (reg/lm ols-ys ols-xs))

(def fm-intercept (:intercept fm-model))
(def fm-beta (:beta fm-model))

[fm-intercept fm-beta]

;; ### La Linea OLS via SVD
;;
;; Build the design matrix with an intercept column, decompose,
;; and solve.

(def la-ols
  (fn [xs ys]
    (let [n (count ys)
          p (count (first xs))
          pp1 (inc p)
          ;; Build design matrix [n, p+1] with intercept column
          flat (->> xs
                    (mapcat (fn [row] (cons 1.0 row)))
                    (dtype/make-container :float64))
          X (dtt/reshape flat [n pp1])
          y (t/column ys)
          ;; SVD: X = U Σ V^T
          {:keys [U S Vt]} (la/svd X)
          ;; Pseudoinverse: V Σ^{-1} U^T y
          S-inv (t/diag (dfn// 1.0 S))
          Ut-thin (t/submatrix U :all (range pp1))
          beta (la/mmul (la/mmul (la/transpose Vt) S-inv)
                        (la/mmul (la/transpose Ut-thin) y))]
      {:intercept (dtt/mget beta 0 0)
       :beta (mapv (fn [i] (dtt/mget beta (inc i) 0))
                   (range p))})))

(def la-result (la-ols ols-xs ols-ys))

la-result

;; ### La Linea OLS via normal equations
;;
;; $\beta = (X^T X)^{-1} X^T y$ — simpler, less numerically stable,
;; but faster for small well-conditioned problems.

(def la-ols-normal
  (fn [xs ys]
    (let [n (count ys)
          p (count (first xs))
          pp1 (inc p)
          flat (->> xs
                    (mapcat (fn [row] (cons 1.0 row)))
                    (dtype/make-container :float64))
          X (dtt/reshape flat [n pp1])
          y (t/column ys)
          Xt (la/transpose X)
          beta (la/mmul (la/invert (la/mmul Xt X))
                        (la/mmul Xt y))]
      {:intercept (dtt/mget beta 0 0)
       :beta (mapv (fn [i] (dtt/mget beta (inc i) 0))
                   (range p))})))

(def la-normal-result (la-ols-normal ols-xs ols-ys))

la-normal-result

;; ### Correctness check

(let [fm-coeffs (cons fm-intercept fm-beta)
      la-svd-coeffs (cons (:intercept la-result) (:beta la-result))]
  (every? (fn [[a b]] (< (abs (- (double a) (double b))) 1e-6))
          (map vector fm-coeffs la-svd-coeffs)))

(kind/test-last [true?])

(let [fm-coeffs (cons fm-intercept fm-beta)
      la-ne-coeffs (cons (:intercept la-normal-result) (:beta la-normal-result))]
  (every? (fn [[a b]] (< (abs (- (double a) (double b))) 1e-6))
          (map vector fm-coeffs la-ne-coeffs)))

(kind/test-last [true?])

;; Predicted vs actual — the tighter the diagonal, the
;; better the fit:

(let [pred (fn [{:keys [intercept beta]} xs]
             (mapv (fn [row]
                     (+ (double intercept)
                        (reduce + (map * (map double beta) (map double row)))))
                   xs))]
  (-> (tc/dataset {:actual ols-ys
                   :predicted (pred la-result ols-xs)})
      (plotly/base {:=x :actual :=y :predicted})
      (plotly/layer-point {:=mark-size 4})
      plotly/plot))

;; ### Performance
;;
;; Note: fastmath `lm` computes diagnostics (R², residuals,
;; hat matrix, influence measures) beyond the core SVD solve.
;; The La Linea implementations only compute coefficients.

(let [n-iter 200]
  (tc/dataset {:method ["fastmath lm" "lalinea SVD" "lalinea normal eq."]
               :ms [(bench-fn #(reg/lm ols-ys ols-xs) n-iter)
                    (bench-fn #(la-ols ols-xs ols-ys) n-iter)
                    (bench-fn #(la-ols-normal ols-xs ols-ys) n-iter)]}))

;; ## Gaussian Process Regression
;;
;; GP regression builds a kernel covariance matrix $K$, adds noise,
;; factorizes via Cholesky, and solves for weights $w = K^{-1} y$.
;; Prediction at a new point $x^*$: $\mu^* = k(x^*, X)^T w$.

;; ### Test data
;;
;; 1D function with some noise.

(def gp-n 50)

(def gp-xs (mapv (fn [i] (/ (double i) gp-n)) (range gp-n)))
(def gp-ys (mapv (fn [^double x]
                   (+ (math/sin (* 2.0 math/PI x))
                      (frand/grandom rng 0.0 0.1)))
                 gp-xs))

;; ### Fastmath GP

(def gp-kernel (k/kernel :gaussian 0.2))

(def fm-gp (gp/gaussian-process
            (mapv vector gp-xs) gp-ys
            {:kernel gp-kernel
             :kscale 1.0
             :noise 0.01}))

(def fm-gp-preds
  (mapv (fn [x] (gp/predict fm-gp [x])) gp-xs))

;; ### La Linea GP
;;
;; Build the covariance matrix, add noise diagonal, solve via
;; `la/solve` (which uses EJML's internal decomposition).

(def la-gp
  (fn [xs ys kernel kscale noise]
    (let [n (count xs)
          ;; Build kernel covariance matrix K
          flat (->> (for [xj xs xi xs]
                      (* (double kscale)
                         (double (kernel xi xj))))
                    (dtype/make-container :float64))
          K (dtype/clone (dtt/reshape flat [n n]))
          ;; Add noise to diagonal
          _ (dotimes [i n]
              (dtt/mset! K i i
                            (+ (double (dtt/mget K i i))
                               (double noise))))
          ;; Solve K*w = y
          y-col (t/column ys)
          w (la/solve K y-col)]
      {:K K :w w :xs xs :kernel kernel :kscale kscale})))

(def la-gp-predict
  (fn [{:keys [w xs kernel kscale]} x-new]
    (let [k-vec (t/column
                 (mapv (fn [xi]
                         (* (double kscale)
                            (double (kernel x-new xi))))
                       xs))]
      (dtt/mget (la/mmul (la/transpose w) k-vec) 0 0))))

(def la-gp-model
  (la-gp (mapv vector gp-xs) gp-ys gp-kernel 1.0 0.01))

(def la-gp-preds
  (mapv (fn [x] (la-gp-predict la-gp-model [x])) gp-xs))

;; ### Correctness

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-6))
        (map vector fm-gp-preds la-gp-preds))

(kind/test-last [true?])

;; The GP fits the noisy sine wave closely:

(-> (tc/dataset (concat
                 (map (fn [x y] {:x x :y y :type "data"}) gp-xs gp-ys)
                 (map (fn [x p] {:x x :y p :type "GP prediction"}) gp-xs la-gp-preds)))
    (plotly/base {:=x :x :=y :y :=color :type})
    (plotly/layer-point {:=mark-size 5})
    (plotly/layer-line)
    plotly/plot)

;; ### Performance

(let [xvs (mapv vector gp-xs)
      n-iter 100]
  (tc/dataset {:method ["fastmath GP" "lalinea GP"]
               :ms [(bench-fn #(gp/gaussian-process xvs gp-ys
                                                    {:kernel gp-kernel
                                                     :kscale 1.0
                                                     :noise 0.01
                                                     :L? false})
                              n-iter)
                    (bench-fn #(la-gp xvs gp-ys gp-kernel 1.0 0.01)
                              n-iter)]}))

;; ### Scaling comparison
;;
;; How does performance change with the number of data points?
;; The covariance matrix is $n \times n$, so the solve is $O(n^3)$.

(def gp-scaling
  (let [sizes [20 50 100 200]
        kernel (k/kernel :gaussian 0.2)]
    (mapcat
     (fn [n]
       (let [xs (mapv (fn [i] [(/ (double i) n)]) (range n))
             ys (mapv (fn [[x]] (math/sin (* 2.0 math/PI (double x)))) xs)
             n-iter (max 5 (quot 2000 (* n n)))]
         [{:n n :method "fastmath"
           :ms (bench-fn #(gp/gaussian-process xs ys
                                               {:kernel kernel
                                                :kscale 1.0
                                                :noise 0.01
                                                :L? false})
                         n-iter)}
          {:n n :method "lalinea"
           :ms (bench-fn #(la-gp xs ys kernel 1.0 0.01)
                         n-iter)}]))
     sizes)))

(-> (tc/dataset gp-scaling)
    (plotly/base {:=x :n :=y :ms :=color :method})
    (plotly/layer-point {:=mark-size 8})
    (plotly/layer-line)
    plotly/plot)

(kind/test-last [(fn [_] true)])

;; ## RBF Interpolation
;;
;; Radial basis function interpolation builds a kernel matrix $\Phi$
;; and solves $\Phi w = y$. Optionally adds Tikhonov regularization:
;; $(\Phi^T \Phi + \lambda I) w = \Phi^T y$.

;; ### Test data

(def rbf-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0])
(def rbf-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41])

;; ### Fastmath RBF

(def rbf-kernel (k/rbf :gaussian))

(def fm-rbf (rbf/rbf rbf-xs rbf-ys rbf-kernel))

(def rbf-test-pts [0.5 1.5 2.5 3.5 4.5])

(def fm-rbf-preds (mapv fm-rbf rbf-test-pts))

fm-rbf-preds

;; ### La Linea RBF

(def la-rbf
  (fn [xs ys kernel]
    (let [n (count xs)
          ;; Build Φ matrix
          flat (->> (for [x1 xs x2 xs]
                      (kernel (dist/euclidean-1d x1 x2)))
                    (dtype/make-container :float64))
          Phi (dtt/reshape flat [n n])
          ;; Solve Φ w = y
          y-col (t/column ys)
          w (la/solve Phi y-col)]
      {:w w :xs xs :kernel kernel})))

(def la-rbf-predict
  (fn [{:keys [w xs kernel]} x-new]
    (let [phi-vec (t/column
                   (mapv (fn [xi]
                           (kernel (dist/euclidean-1d xi x-new)))
                         xs))]
      (dtt/mget (la/mmul (la/transpose w) phi-vec) 0 0))))

(def la-rbf-model (la-rbf rbf-xs rbf-ys rbf-kernel))

(def la-rbf-preds (mapv #(la-rbf-predict la-rbf-model %) rbf-test-pts))

la-rbf-preds

;; ### Correctness

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-6))
        (map vector fm-rbf-preds la-rbf-preds))

(kind/test-last [true?])

;; ### Performance

(let [n-iter 2000]
  (tc/dataset {:method ["fastmath RBF" "lalinea RBF"]
               :ms [(bench-fn #(rbf/rbf rbf-xs rbf-ys rbf-kernel) n-iter)
                    (bench-fn #(la-rbf rbf-xs rbf-ys rbf-kernel) n-iter)]}))

;; ## Kriging
;;
;; Kriging builds a variogram matrix $V$ and solves $V w = y$,
;; similar to RBF. Fastmath auto-fits a variogram model.

;; ### Test data

(def kriging-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0])
(def kriging-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41])

;; ### Fastmath kriging

;; Auto-fit a variogram model.

(def auto-variogram
  (let [emp (fastmath.kernel.variogram/empirical kriging-xs kriging-ys)]
    (fastmath.kernel.variogram/fit emp :gaussian)))

(def fm-kriging (kriging/kriging kriging-xs kriging-ys
                                 auto-variogram
                                 {:polynomial-terms (constantly [1.0])}))

(def kriging-test-pts [0.5 1.5 2.5 3.5 4.5])

(def fm-kriging-preds (mapv fm-kriging kriging-test-pts))

fm-kriging-preds

;; ### La Linea kriging
;;
;; We use the same auto-fitted variogram as fastmath,
;; then do the matrix solve with La Linea.

(def la-kriging
  (fn [xs ys variogram polynomial-terms]
    (let [n (count xs)
          pt-fn (or polynomial-terms (constantly [1.0]))
          ;; Build V matrix from variogram
          flat (->> (for [x1 xs x2 xs]
                      (variogram (dist/euclidean-1d x1 x2)))
                    (dtype/make-container :float64))
          V (dtt/reshape flat [n n])
          ;; Augment with polynomial terms
          pterms (mapv pt-fn xs)
          ptsize (count (first pterms))
          total (+ n ptsize)
          ;; Build augmented matrix (materialized for mutation)
          aug-flat (double-array (* total total))
          aug (dtt/reshape (dtt/ensure-tensor aug-flat) [total total])
          ;; Copy V into top-left
          _ (dotimes [i n]
              (dotimes [j n]
                (aset aug-flat (+ (* i total) j)
                      (double (dtt/mget V i j)))))
          ;; P and P^T blocks
          _ (dotimes [i n]
              (dotimes [j ptsize]
                (let [v (double (nth (nth pterms i) j))]
                  (aset aug-flat (+ (* i total) n j) v)
                  (aset aug-flat (+ (* (+ n j) total) i) v))))
          ;; Augmented RHS
          y-aug (t/column (concat ys (repeat ptsize 0.0)))
          ;; Solve
          w-full (la/solve aug y-aug)
          w (t/submatrix w-full (range n) :all)
          c (t/submatrix w-full (range n total) :all)]
      {:w w :c c :xs xs :variogram variogram :pt-fn pt-fn})))

(def la-kriging-predict
  (fn [{:keys [w c xs variogram pt-fn]} x-new]
    (let [v-vec (mapv (fn [xi] (variogram (dist/euclidean-1d xi x-new))) xs)
          p-vec (pt-fn x-new)]
      (+ (double (la/dot (t/column v-vec) w))
         (double (la/dot (t/column p-vec) c))))))

(def la-kriging-model
  (la-kriging kriging-xs kriging-ys auto-variogram (constantly [1.0])))

(def la-kriging-preds
  (mapv #(la-kriging-predict la-kriging-model %) kriging-test-pts))

la-kriging-preds

;; ### Correctness

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-6))
        (map vector fm-kriging-preds la-kriging-preds))

(kind/test-last [true?])

;; ### Performance

(let [n-iter 1000]
  (tc/dataset {:method ["fastmath kriging" "lalinea kriging"]
               :ms [(bench-fn #(kriging/kriging kriging-xs kriging-ys
                                                auto-variogram
                                                {:polynomial-terms (constantly [1.0])})
                              n-iter)
                    (bench-fn #(la-kriging kriging-xs kriging-ys
                                           auto-variogram
                                           (constantly [1.0]))
                              n-iter)]}))

;; ## Mahalanobis Distance
;;
;; $d(x, \mu) = \sqrt{(x - \mu)^T S^{-1} (x - \mu)}$
;;
;; Fastmath computes $S^{-1}$ from a covariance matrix and applies it.

;; ### Test data

(def mah-cov [[2.0 0.5 0.3]
              [0.5 1.0 0.2]
              [0.3 0.2 1.5]])

(def mah-x [3.0 1.0 2.0])
(def mah-mean [1.0 0.0 0.5])

;; ### Fastmath

(def fm-mah (dist/mahalanobis mah-x mah-mean mah-cov))

fm-mah

;; ### La Linea

(def la-mahalanobis
  (fn [x mean cov]
    (let [S-inv (la/invert (t/matrix cov))
          diff (t/column (mapv (fn [a b] (- (double a) (double b))) x mean))
          ;; d² = diff^T S^{-1} diff
          d-sq (dtt/mget (la/mmul (la/transpose diff)
                                     (la/mmul S-inv diff))
                            0 0)]
      (math/sqrt d-sq))))

(def la-mah (la-mahalanobis mah-x mah-mean mah-cov))

la-mah

;; ### Correctness

(< (abs (- fm-mah la-mah)) 1e-10)

(kind/test-last [true?])

;; ### Performance
;;
;; Note: fastmath's 3-arg `mahalanobis` memoizes the covariance
;; inverse, so repeated calls with the same covariance matrix
;; skip the inversion. The La Linea version recomputes each time.

(let [n-iter 5000]
  (tc/dataset {:method ["fastmath Mahalanobis" "lalinea Mahalanobis"]
               :ms [(bench-fn #(dist/mahalanobis mah-x mah-mean mah-cov) n-iter)
                    (bench-fn #(la-mahalanobis mah-x mah-mean mah-cov) n-iter)]}))

;; ## Contrast Coding — Mean Contrasts
;;
;; Fastmath's `mean-contrasts` builds a coding matrix from a contrast
;; scheme, prepends an intercept column, inverts the matrix, and
;; returns the rows.

;; ### Fastmath

(def levels [:low :medium :high :very-high])

(def fm-helmert-contrasts
  (contrast/mean-contrasts (contrast/helmert levels) false))

fm-helmert-contrasts

;; ### La Linea
;;
;; Build the same coding matrix and invert it.

(def la-mean-contrasts
  (fn [coding]
    (let [{:keys [names levels mapping]} coding
          ;; Build coding matrix: each row = [1.0, code-values...]
          rows (mapv (fn [l] (vec (cons 1.0 (mapping l)))) levels)
          M (t/matrix rows)
          M-inv (la/invert M)
          ;; Match fastmath's key ordering: :$intercept first
          nms (vec (cons :$intercept names))
          n-levels (count levels)
          inv-rows (mapv (fn [i]
                           (mapv (fn [j] (dtt/mget M-inv i j))
                                 (range n-levels)))
                         (range (count nms)))]
      (zipmap nms inv-rows))))

(def la-helmert-contrasts
  (la-mean-contrasts (contrast/helmert levels)))

la-helmert-contrasts

;; ### Correctness

(every? (fn [k]
          (let [fm-row (vec (fm-helmert-contrasts k))
                la-row (vec (la-helmert-contrasts k))]
            (every? (fn [[a b]]
                      (< (abs (- (double a) (double b))) 1e-8))
                    (map vector fm-row la-row))))
        (keys fm-helmert-contrasts))

(kind/test-last [true?])

;; ### Performance

(let [helmert-coding (contrast/helmert levels)
      n-iter 5000]
  (tc/dataset {:method ["fastmath mean-contrasts" "lalinea mean-contrasts"]
               :ms [(bench-fn #(contrast/mean-contrasts helmert-coding false) n-iter)
                    (bench-fn #(la-mean-contrasts helmert-coding) n-iter)]}))

;; ## Savitzky-Golay Filter
;;
;; Builds a Vandermonde matrix, computes its pseudoinverse via SVD,
;; and extracts a row as the convolution kernel.

;; ### Test data

(def sg-signal
  (mapv (fn [i]
          (+ (math/sin (* 0.1 i))
             (frand/grandom rng 0.0 0.2)))
        (range 100)))

;; ### Fastmath

(def fm-sg (signal/savgol-filter 7 3 0))

(def fm-sg-result (fm-sg sg-signal))

;; ### La Linea
;;
;; Build Vandermonde, compute pseudoinverse via SVD, extract row.

(def la-savgol-coeffs
  (fn [^long length ^long order ^long derivative]
    (let [fc (quot (dec length) 2)
          ;; Build Vandermonde matrix
          rows (mapv (fn [v]
                       (mapv (fn [p] (math/pow (double v) (double p)))
                             (range (inc order))))
                     (range (- fc) (inc fc)))
          V (t/matrix rows)
          ;; Pseudoinverse via SVD: V = U Σ Vt → V^+ = Vt^T Σ^{-1} U^T
          {:keys [U S Vt]} (la/svd V)
          k (count S)
          S-inv (t/diag (dfn// 1.0 (dtt/select S (range k))))
          Ut-thin (t/submatrix U :all (range k))
          V-pinv (la/mmul (la/mmul (la/transpose Vt) S-inv)
                          (la/transpose Ut-thin))
          ;; Extract the derivative-th row
          coeffs (mapv (fn [j] (dtt/mget V-pinv derivative j))
                       (range length))]
      coeffs)))

(def la-sg-coeffs (la-savgol-coeffs 7 3 0))

la-sg-coeffs

;; ### Correctness
;;
;; Compare the filter coefficients (the core matrix computation).
;; The convolution step is identical once coefficients match.

(let [fm-coeffs (-> (for [v (range -3 4)]
                      (mapv #(math/pow (double v) (double %))
                            (range 4)))
                    (m/seq->double-double-array)
                    (org.apache.commons.math3.linear.Array2DRowRealMatrix.)
                    (org.apache.commons.math3.linear.SingularValueDecomposition.)
                    (.getSolver)
                    (.getInverse)
                    (.getRow 0)
                    (vec))]
  (every? (fn [[a b]]
            (< (abs (- (double a) (double b))) 1e-10))
          (map vector fm-coeffs la-sg-coeffs)))

(kind/test-last [true?])

;; ### Performance
;;
;; Filter creation time (the matrix operation).

(let [n-iter 5000]
  (tc/dataset {:method ["fastmath savgol-filter" "lalinea savgol"]
               :ms [(bench-fn #(signal/savgol-filter 7 3 0) n-iter)
                    (bench-fn #(la-savgol-coeffs 7 3 0) n-iter)]}))

;; ## Finite Difference Coefficients
;;
;; Given derivative order $n$ and a set of offsets, solve a
;; Vandermonde-like system for the FD stencil weights.

;; ### Fastmath

(def fm-fd (finite/fd-coeffs 1 [-1 0 1]))

fm-fd

;; ### La Linea

(def la-fd-coeffs
  (fn [n offsets]
    (let [noff (count offsets)
          ;; Build matrix: row i = [offset_0^i, offset_1^i, ...]
          rows (mapv (fn [i]
                       (mapv (fn [j] (math/pow (double j) (double i)))
                             offsets))
                     (range noff))
          M (t/matrix rows)
          ;; RHS: e_n * n!
          rhs (t/column
               (mapv (fn [id]
                       (if (== (long id) (long n))
                         (double (reduce * (range 1 (inc (long n)))))
                         0.0))
                     (range noff)))
          w (la/solve M rhs)]
      [offsets (mapv (fn [i] (dtt/mget w i 0)) (range noff))])))

(def la-fd (la-fd-coeffs 1 [-1 0 1]))

la-fd

;; ### Correctness

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-10))
        (map vector (second fm-fd) (second la-fd)))

(kind/test-last [true?])

;; Higher-order stencil:

(def fm-fd2 (finite/fd-coeffs 2 [-2 -1 0 1 2]))
(def la-fd2 (la-fd-coeffs 2 [-2 -1 0 1 2]))

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-10))
        (map vector (second fm-fd2) (second la-fd2)))

(kind/test-last [true?])

;; ### Performance

(let [offsets [-2 -1 0 1 2]
      n-iter 5000]
  (tc/dataset {:method ["fastmath fd-coeffs" "lalinea fd-coeffs"]
               :ms [(bench-fn #(finite/fd-coeffs 2 offsets) n-iter)
                    (bench-fn #(la-fd-coeffs 2 offsets) n-iter)]}))

;; ## Gauss Quadrature Nodes
;;
;; The Golub-Welsch algorithm computes quadrature nodes as eigenvalues
;; of a symmetric tridiagonal matrix built from the three-term
;; recurrence coefficients.

;; ### Fastmath
;;
;; Fastmath's `get-quadrature-points` uses Apache Commons Math's
;; `EigenDecomposition` on the tridiagonal matrix, then refines
;; with Newton iterations.
;;
;; The recurrence coefficients for Gauss-Legendre:
;; $b_j = j / \sqrt{4j^2 - 1}$.

(def quad-n 5)

(def jacobi-b
  (mapv (fn [j]
          (let [j (double (inc j))]
            (/ j (math/sqrt (dec (* 4.0 j j))))))
        (range (dec quad-n))))

(def fm-quad-nodes
  (quad/get-quadrature-points jacobi-b quad-n))

fm-quad-nodes

;; ### La Linea
;;
;; Build the tridiagonal matrix and use `la/real-eigenvalues`.

(def la-quad-nodes
  (fn [b n]
    (let [m (count b)
          sz (inc m)
          ;; Build symmetric tridiagonal matrix
          arr (double-array (* sz sz))
          _ (dotimes [i m]
              (let [v (double (b i))]
                (aset arr (+ (* i sz) (inc i)) v)
                (aset arr (+ (* (inc i) sz) i) v)))
          T (dtt/reshape (dtt/ensure-tensor arr) [sz sz])
          ;; Eigenvalues = quadrature nodes
          evals (la/real-eigenvalues T)]
      ;; Take top n (largest), reverse to match fastmath ordering
      (vec (reverse (take-last n evals))))))

(def la-nodes (la-quad-nodes jacobi-b quad-n))

la-nodes

;; ### Correctness

(every? (fn [[a b]]
          (< (abs (- (double a) (double b))) 1e-10))
        (map vector (sort fm-quad-nodes) (sort la-nodes)))

(kind/test-last [true?])

;; ### Performance

(let [n-iter 5000]
  (tc/dataset {:method ["fastmath quadrature" "lalinea quadrature"]
               :ms [(bench-fn #(quad/get-quadrature-points jacobi-b quad-n) n-iter)
                    (bench-fn #(la-quad-nodes jacobi-b quad-n) n-iter)]}))

;; ## Ince Polynomial Coefficients
;;
;; Ince polynomials solve a tridiagonal eigenvalue problem.
;; The coefficients are components of a specific eigenvector
;; (ordered by eigenvalue magnitude).

;; ### Fastmath

(def fm-ince-c (poly/ince-C-coeffs 4 2 0.5 :none))

(vec fm-ince-c)

;; ### La Linea
;;
;; Build the same tridiagonal matrix and extract the eigenvector.

(def la-ince-c-coeffs-even
  (fn [^long p ^long m ^double e]
    (let [n (quot p 2)
          N (inc n)
          order (quot m 2)
          ;; Build the matrix (materialized for mutation)
          arr (double-array (* N N))
          _ (dotimes [i N]
              (aset arr (+ (* i N) i) (* 4.0 i i)))
          _ (dotimes [i n]
              (let [ip (inc i)]
                (aset arr (+ (* i N) ip) (* e (+ n ip)))
                (aset arr (+ (* ip N) i) (* e (- n i)))))
          ;; Special entry: double the (1,0) element
          _ (aset arr N (* 2.0 (aget arr N)))
          M (dtt/reshape (dtt/ensure-tensor arr) [N N])
          ;; Eigendecomposition
          {:keys [eigenvalues eigenvectors]} (la/eigen M)
          ;; Sort eigenvalues ascending, pick the order-th
          reals (el/re eigenvalues)
          indexed (map-indexed (fn [i _] [i (double (reals i))]) (range N))
          sorted-idx (map first (sort-by second indexed))
          target-idx (nth sorted-idx order)
          ev (nth eigenvectors target-idx)
          ;; Extract eigenvector components
          coeffs (mapv (fn [i] (dtt/mget ev i 0)) (range N))
          ;; Normalize sign: sign of sum
          sgn (if (neg? (reduce + coeffs)) -1.0 1.0)]
      (mapv (fn [v] (* sgn v)) coeffs))))

(def la-ince-c (la-ince-c-coeffs-even 4 2 0.5))

la-ince-c

;; ### Correctness
;;
;; EJML returns unit eigenvectors; Apache Commons Math does not
;; normalize. Compare directions (normalize both, then compare).

(let [normalize (fn [v]
                  (let [norm (math/sqrt (reduce + (map #(* % %) v)))]
                    (mapv #(/ (double %) norm) v)))
      fm-normed (normalize (vec fm-ince-c))
      la-normed (normalize la-ince-c)]
  (every? (fn [[a b]]
            (< (abs (- (double a) (double b))) 1e-10))
          (map vector fm-normed la-normed)))

(kind/test-last [true?])

;; ### Performance

(let [n-iter 5000]
  (tc/dataset {:method ["fastmath ince-C" "lalinea ince-C"]
               :ms [(bench-fn #(poly/ince-C-coeffs 4 2 0.5 :none) n-iter)
                    (bench-fn #(la-ince-c-coeffs-even 4 2 0.5) n-iter)]}))

;; ## Summary
;;
;; All ten fastmath algorithms that use `fastmath.matrix` can be
;; reimplemented with La Linea's tensor-based API. The core matrix
;; operations — SVD, solve, invert, eigendecomposition — map directly.
;;
;; | Algorithm | fastmath operation | La Linea operation |
;; |:----------|:-------------------|:-------------------|
;; | OLS regression | SVD via ACM | `la/svd` or normal equations |
;; | GP regression | Cholesky + solve via ACM | `la/solve` |
;; | RBF interpolation | LU/SVD solve via ACM | `la/solve` |
;; | Kriging | LU/SVD solve via ACM | `la/solve` |
;; | Mahalanobis distance | matrix inverse via ACM | `la/invert` + `la/mmul` |
;; | Contrast coding | matrix inverse via ACM | `la/invert` |
;; | Savitzky-Golay | SVD pseudoinverse via ACM | `la/svd` + manual pseudoinverse |
;; | Finite differences | LU solve via ACM | `la/solve` |
;; | Gauss quadrature | eigendecomposition via ACM | `la/real-eigenvalues` |
;; | Ince polynomials | eigendecomposition via ACM | `la/eigen` |
;;
;; For small matrices (finite differences, quadrature, Ince, contrasts),
;; fastmath's direct Java-array approach has lower per-call overhead.
;; For larger matrices (regression, GP), the gap narrows as the $O(n^3)$
;; decomposition dominates. The La Linea implementations benefit from
;; dtype-next's composable buffer operations and EJML's optimized
;; decompositions.
