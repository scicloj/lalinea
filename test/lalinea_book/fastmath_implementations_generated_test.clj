(ns
 lalinea-book.fastmath-implementations-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tech.v3.tensor :as dtt]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
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
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l50
 (def
  bench-fn
  (fn
   [f n]
   (dotimes [_ 5] (f))
   (let
    [start (System/nanoTime)]
    (dotimes [_ n] (f))
    (/ (- (System/nanoTime) start) (* n 1000000.0))))))


(def v5_l69 (def rng (frand/rng :mersenne 42)))


(def v6_l71 (def n-obs 200))


(def
 v7_l73
 (def
  ols-xs
  (mapv
   (fn [_] [(frand/drandom rng 0.0 10.0) (frand/drandom rng 0.0 5.0)])
   (range n-obs))))


(def v8_l78 (def true-beta [3.0 -1.5]))


(def
 v9_l80
 (def
  ols-ys
  (mapv
   (fn
    [[x1 x2]]
    (+ 2.0 (* 3.0 x1) (* -1.5 x2) (frand/grandom rng 0.0 0.5)))
   ols-xs)))


(def v11_l90 (def fm-model (reg/lm ols-ys ols-xs)))


(def v12_l92 (def fm-intercept (:intercept fm-model)))


(def v13_l93 (def fm-beta (:beta fm-model)))


(def v14_l95 [fm-intercept fm-beta])


(def
 v16_l102
 (def
  la-ols
  (fn
   [xs ys]
   (let
    [n
     (count ys)
     p
     (count (first xs))
     pp1
     (inc p)
     flat
     (->>
      xs
      (mapcat (fn [row] (cons 1.0 row)))
      (dtype/make-container :float64))
     X
     (dtt/reshape flat [n pp1])
     y
     (t/column ys)
     {:keys [U S Vt]}
     (la/svd X)
     S-inv
     (t/diag (dfn// 1.0 S))
     Ut-thin
     (t/submatrix U :all (range pp1))
     beta
     (la/mmul
      (la/mmul (la/transpose Vt) S-inv)
      (la/mmul (la/transpose Ut-thin) y))]
    {:intercept (dtt/mget beta 0 0),
     :beta (mapv (fn [i] (dtt/mget beta (inc i) 0)) (range p))}))))


(def v17_l124 (def la-result (la-ols ols-xs ols-ys)))


(def v18_l126 la-result)


(def
 v20_l133
 (def
  la-ols-normal
  (fn
   [xs ys]
   (let
    [n
     (count ys)
     p
     (count (first xs))
     pp1
     (inc p)
     flat
     (->>
      xs
      (mapcat (fn [row] (cons 1.0 row)))
      (dtype/make-container :float64))
     X
     (dtt/reshape flat [n pp1])
     y
     (t/column ys)
     Xt
     (la/transpose X)
     beta
     (la/mmul (la/invert (la/mmul Xt X)) (la/mmul Xt y))]
    {:intercept (dtt/mget beta 0 0),
     :beta (mapv (fn [i] (dtt/mget beta (inc i) 0)) (range p))}))))


(def v21_l150 (def la-normal-result (la-ols-normal ols-xs ols-ys)))


(def v22_l152 la-normal-result)


(def
 v24_l156
 (let
  [fm-coeffs
   (cons fm-intercept fm-beta)
   la-svd-coeffs
   (cons (:intercept la-result) (:beta la-result))]
  (every?
   (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-6))
   (map vector fm-coeffs la-svd-coeffs))))


(deftest t25_l161 (is (true? v24_l156)))


(def
 v26_l163
 (let
  [fm-coeffs
   (cons fm-intercept fm-beta)
   la-ne-coeffs
   (cons (:intercept la-normal-result) (:beta la-normal-result))]
  (every?
   (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-6))
   (map vector fm-coeffs la-ne-coeffs))))


(deftest t27_l168 (is (true? v26_l163)))


(def
 v29_l173
 (let
  [pred
   (fn
    [{:keys [intercept beta]} xs]
    (mapv
     (fn
      [row]
      (+
       (double intercept)
       (reduce + (map * (map double beta) (map double row)))))
     xs))]
  (->
   (tc/dataset {:actual ols-ys, :predicted (pred la-result ols-xs)})
   (plotly/base {:=x :actual, :=y :predicted})
   (plotly/layer-point {:=mark-size 4})
   plotly/plot)))


(def
 v31_l190
 (let
  [n-iter 200]
  (tc/dataset
   {:method ["fastmath lm" "lalinea SVD" "lalinea normal eq."],
    :ms
    [(bench-fn (fn* [] (reg/lm ols-ys ols-xs)) n-iter)
     (bench-fn (fn* [] (la-ols ols-xs ols-ys)) n-iter)
     (bench-fn (fn* [] (la-ols-normal ols-xs ols-ys)) n-iter)]})))


(def v33_l206 (def gp-n 50))


(def
 v34_l208
 (def gp-xs (mapv (fn [i] (/ (double i) gp-n)) (range gp-n))))


(def
 v35_l209
 (def
  gp-ys
  (mapv
   (fn
    [x]
    (+ (math/sin (* 2.0 math/PI x)) (frand/grandom rng 0.0 0.1)))
   gp-xs)))


(def v37_l216 (def gp-kernel (k/kernel :gaussian 0.2)))


(def
 v38_l218
 (def
  fm-gp
  (gp/gaussian-process
   (mapv vector gp-xs)
   gp-ys
   {:kernel gp-kernel, :kscale 1.0, :noise 0.01})))


(def
 v39_l224
 (def fm-gp-preds (mapv (fn [x] (gp/predict fm-gp [x])) gp-xs)))


(def
 v41_l232
 (def
  la-gp
  (fn
   [xs ys kernel kscale noise]
   (let
    [n
     (count xs)
     flat
     (->>
      (for [xj xs xi xs] (* (double kscale) (double (kernel xi xj))))
      (dtype/make-container :float64))
     K
     (dtype/clone (dtt/reshape flat [n n]))
     _
     (dotimes
      [i n]
      (dtt/mset! K i i (+ (double (dtt/mget K i i)) (double noise))))
     y-col
     (t/column ys)
     w
     (la/solve K y-col)]
    {:K K, :w w, :xs xs, :kernel kernel, :kscale kscale}))))


(def
 v42_l251
 (def
  la-gp-predict
  (fn
   [{:keys [w xs kernel kscale]} x-new]
   (let
    [k-vec
     (t/column
      (mapv
       (fn [xi] (* (double kscale) (double (kernel x-new xi))))
       xs))]
    (dtt/mget (la/mmul (la/transpose w) k-vec) 0 0)))))


(def
 v43_l260
 (def la-gp-model (la-gp (mapv vector gp-xs) gp-ys gp-kernel 1.0 0.01)))


(def
 v44_l263
 (def
  la-gp-preds
  (mapv (fn [x] (la-gp-predict la-gp-model [x])) gp-xs)))


(def
 v46_l268
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-gp-preds la-gp-preds)))


(deftest t47_l272 (is (true? v46_l268)))


(def
 v49_l276
 (->
  (tc/dataset
   (concat
    (map (fn [x y] {:x x, :y y, :type "data"}) gp-xs gp-ys)
    (map
     (fn [x p] {:x x, :y p, :type "GP prediction"})
     gp-xs
     la-gp-preds)))
  (plotly/base {:=x :x, :=y :y, :=color :type})
  (plotly/layer-point {:=mark-size 5})
  (plotly/layer-line)
  plotly/plot))


(def
 v51_l286
 (let
  [xvs (mapv vector gp-xs) n-iter 100]
  (tc/dataset
   {:method ["fastmath GP" "lalinea GP"],
    :ms
    [(bench-fn
      (fn*
       []
       (gp/gaussian-process
        xvs
        gp-ys
        {:kernel gp-kernel, :kscale 1.0, :noise 0.01, :L? false}))
      n-iter)
     (bench-fn
      (fn* [] (la-gp xvs gp-ys gp-kernel 1.0 0.01))
      n-iter)]})))


(def
 v53_l303
 (def
  gp-scaling
  (let
   [sizes [20 50 100 200] kernel (k/kernel :gaussian 0.2)]
   (mapcat
    (fn
     [n]
     (let
      [xs
       (mapv (fn [i] [(/ (double i) n)]) (range n))
       ys
       (mapv (fn [[x]] (math/sin (* 2.0 math/PI (double x)))) xs)
       n-iter
       (max 5 (quot 2000 (* n n)))]
      [{:n n,
        :method "fastmath",
        :ms
        (bench-fn
         (fn*
          []
          (gp/gaussian-process
           xs
           ys
           {:kernel kernel, :kscale 1.0, :noise 0.01, :L? false}))
         n-iter)}
       {:n n,
        :method "lalinea",
        :ms
        (bench-fn (fn* [] (la-gp xs ys kernel 1.0 0.01)) n-iter)}]))
    sizes))))


(def
 v54_l323
 (->
  (tc/dataset gp-scaling)
  (plotly/base {:=x :n, :=y :ms, :=color :method})
  (plotly/layer-point {:=mark-size 8})
  (plotly/layer-line)
  plotly/plot))


(deftest t55_l329 (is ((fn [_] true) v54_l323)))


(def v57_l339 (def rbf-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]))


(def
 v58_l340
 (def rbf-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41]))


(def v60_l344 (def rbf-kernel (k/rbf :gaussian)))


(def v61_l346 (def fm-rbf (rbf/rbf rbf-xs rbf-ys rbf-kernel)))


(def v62_l348 (def rbf-test-pts [0.5 1.5 2.5 3.5 4.5]))


(def v63_l350 (def fm-rbf-preds (mapv fm-rbf rbf-test-pts)))


(def v64_l352 fm-rbf-preds)


(def
 v66_l356
 (def
  la-rbf
  (fn
   [xs ys kernel]
   (let
    [n
     (count xs)
     flat
     (->>
      (for [x1 xs x2 xs] (kernel (dist/euclidean-1d x1 x2)))
      (dtype/make-container :float64))
     Phi
     (dtt/reshape flat [n n])
     y-col
     (t/column ys)
     w
     (la/solve Phi y-col)]
    {:w w, :xs xs, :kernel kernel}))))


(def
 v67_l369
 (def
  la-rbf-predict
  (fn
   [{:keys [w xs kernel]} x-new]
   (let
    [phi-vec
     (t/column
      (mapv (fn [xi] (kernel (dist/euclidean-1d xi x-new))) xs))]
    (dtt/mget (la/mmul (la/transpose w) phi-vec) 0 0)))))


(def v68_l377 (def la-rbf-model (la-rbf rbf-xs rbf-ys rbf-kernel)))


(def
 v69_l379
 (def
  la-rbf-preds
  (mapv
   (fn* [p1__123823#] (la-rbf-predict la-rbf-model p1__123823#))
   rbf-test-pts)))


(def v70_l381 la-rbf-preds)


(def
 v72_l385
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-rbf-preds la-rbf-preds)))


(deftest t73_l389 (is (true? v72_l385)))


(def
 v75_l393
 (let
  [n-iter 2000]
  (tc/dataset
   {:method ["fastmath RBF" "lalinea RBF"],
    :ms
    [(bench-fn (fn* [] (rbf/rbf rbf-xs rbf-ys rbf-kernel)) n-iter)
     (bench-fn (fn* [] (la-rbf rbf-xs rbf-ys rbf-kernel)) n-iter)]})))


(def
 v77_l405
 (def kriging-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]))


(def
 v78_l406
 (def kriging-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41]))


(def
 v80_l412
 (def
  auto-variogram
  (let
   [emp (fastmath.kernel.variogram/empirical kriging-xs kriging-ys)]
   (fastmath.kernel.variogram/fit emp :gaussian))))


(def
 v81_l416
 (def
  fm-kriging
  (kriging/kriging
   kriging-xs
   kriging-ys
   auto-variogram
   {:polynomial-terms (constantly [1.0])})))


(def v82_l420 (def kriging-test-pts [0.5 1.5 2.5 3.5 4.5]))


(def v83_l422 (def fm-kriging-preds (mapv fm-kriging kriging-test-pts)))


(def v84_l424 fm-kriging-preds)


(def
 v86_l431
 (def
  la-kriging
  (fn
   [xs ys variogram polynomial-terms]
   (let
    [n
     (count xs)
     pt-fn
     (or polynomial-terms (constantly [1.0]))
     flat
     (->>
      (for [x1 xs x2 xs] (variogram (dist/euclidean-1d x1 x2)))
      (dtype/make-container :float64))
     V
     (dtt/reshape flat [n n])
     pterms
     (mapv pt-fn xs)
     ptsize
     (count (first pterms))
     total
     (+ n ptsize)
     aug-flat
     (double-array (* total total))
     aug
     (dtt/reshape (dtt/ensure-tensor aug-flat) [total total])
     _
     (dotimes
      [i n]
      (dotimes
       [j n]
       (aset aug-flat (+ (* i total) j) (double (dtt/mget V i j)))))
     _
     (dotimes
      [i n]
      (dotimes
       [j ptsize]
       (let
        [v (double (nth (nth pterms i) j))]
        (aset aug-flat (+ (* i total) n j) v)
        (aset aug-flat (+ (* (+ n j) total) i) v))))
     y-aug
     (t/column (concat ys (repeat ptsize 0.0)))
     w-full
     (la/solve aug y-aug)
     w
     (t/submatrix w-full (range n) :all)
     c
     (t/submatrix w-full (range n total) :all)]
    {:w w, :c c, :xs xs, :variogram variogram, :pt-fn pt-fn}))))


(def
 v87_l466
 (def
  la-kriging-predict
  (fn
   [{:keys [w c xs variogram pt-fn]} x-new]
   (let
    [v-vec
     (mapv (fn [xi] (variogram (dist/euclidean-1d xi x-new))) xs)
     p-vec
     (pt-fn x-new)]
    (+
     (double (la/dot (t/column v-vec) w))
     (double (la/dot (t/column p-vec) c)))))))


(def
 v88_l473
 (def
  la-kriging-model
  (la-kriging kriging-xs kriging-ys auto-variogram (constantly [1.0]))))


(def
 v89_l476
 (def
  la-kriging-preds
  (mapv
   (fn*
    [p1__123824#]
    (la-kriging-predict la-kriging-model p1__123824#))
   kriging-test-pts)))


(def v90_l479 la-kriging-preds)


(def
 v92_l483
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-kriging-preds la-kriging-preds)))


(deftest t93_l487 (is (true? v92_l483)))


(def
 v95_l491
 (let
  [n-iter 1000]
  (tc/dataset
   {:method ["fastmath kriging" "lalinea kriging"],
    :ms
    [(bench-fn
      (fn*
       []
       (kriging/kriging
        kriging-xs
        kriging-ys
        auto-variogram
        {:polynomial-terms (constantly [1.0])}))
      n-iter)
     (bench-fn
      (fn*
       []
       (la-kriging
        kriging-xs
        kriging-ys
        auto-variogram
        (constantly [1.0])))
      n-iter)]})))


(def v97_l510 (def mah-cov [[2.0 0.5 0.3] [0.5 1.0 0.2] [0.3 0.2 1.5]]))


(def v98_l514 (def mah-x [3.0 1.0 2.0]))


(def v99_l515 (def mah-mean [1.0 0.0 0.5]))


(def v101_l519 (def fm-mah (dist/mahalanobis mah-x mah-mean mah-cov)))


(def v102_l521 fm-mah)


(def
 v104_l525
 (def
  la-mahalanobis
  (fn
   [x mean cov]
   (let
    [S-inv
     (la/invert (t/matrix cov))
     diff
     (t/column (mapv (fn [a b] (- (double a) (double b))) x mean))
     d-sq
     (dtt/mget (la/mmul (la/transpose diff) (la/mmul S-inv diff)) 0 0)]
    (math/sqrt d-sq)))))


(def v105_l535 (def la-mah (la-mahalanobis mah-x mah-mean mah-cov)))


(def v106_l537 la-mah)


(def v108_l541 (< (abs (- fm-mah la-mah)) 1.0E-10))


(deftest t109_l543 (is (true? v108_l541)))


(def
 v111_l551
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath Mahalanobis" "lalinea Mahalanobis"],
    :ms
    [(bench-fn
      (fn* [] (dist/mahalanobis mah-x mah-mean mah-cov))
      n-iter)
     (bench-fn
      (fn* [] (la-mahalanobis mah-x mah-mean mah-cov))
      n-iter)]})))


(def v113_l564 (def levels [:low :medium :high :very-high]))


(def
 v114_l566
 (def
  fm-helmert-contrasts
  (contrast/mean-contrasts (contrast/helmert levels) false)))


(def v115_l569 fm-helmert-contrasts)


(def
 v117_l575
 (def
  la-mean-contrasts
  (fn
   [coding]
   (let
    [{:keys [names levels mapping]}
     coding
     rows
     (mapv (fn [l] (vec (cons 1.0 (mapping l)))) levels)
     M
     (t/matrix rows)
     M-inv
     (la/invert M)
     nms
     (vec (cons :$intercept names))
     n-levels
     (count levels)
     inv-rows
     (mapv
      (fn [i] (mapv (fn [j] (dtt/mget M-inv i j)) (range n-levels)))
      (range (count nms)))]
    (zipmap nms inv-rows)))))


(def
 v118_l591
 (def
  la-helmert-contrasts
  (la-mean-contrasts (contrast/helmert levels))))


(def v119_l594 la-helmert-contrasts)


(def
 v121_l598
 (every?
  (fn
   [k]
   (let
    [fm-row
     (vec (fm-helmert-contrasts k))
     la-row
     (vec (la-helmert-contrasts k))]
    (every?
     (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-8))
     (map vector fm-row la-row))))
  (keys fm-helmert-contrasts)))


(deftest t122_l606 (is (true? v121_l598)))


(def
 v124_l610
 (let
  [helmert-coding (contrast/helmert levels) n-iter 5000]
  (tc/dataset
   {:method ["fastmath mean-contrasts" "lalinea mean-contrasts"],
    :ms
    [(bench-fn
      (fn* [] (contrast/mean-contrasts helmert-coding false))
      n-iter)
     (bench-fn (fn* [] (la-mean-contrasts helmert-coding)) n-iter)]})))


(def
 v126_l623
 (def
  sg-signal
  (mapv
   (fn [i] (+ (math/sin (* 0.1 i)) (frand/grandom rng 0.0 0.2)))
   (range 100))))


(def v128_l631 (def fm-sg (signal/savgol-filter 7 3 0)))


(def v129_l633 (def fm-sg-result (fm-sg sg-signal)))


(def
 v131_l639
 (def
  la-savgol-coeffs
  (fn
   [length order derivative]
   (let
    [fc
     (quot (dec length) 2)
     rows
     (mapv
      (fn
       [v]
       (mapv
        (fn [p] (math/pow (double v) (double p)))
        (range (inc order))))
      (range (- fc) (inc fc)))
     V
     (t/matrix rows)
     {:keys [U S Vt]}
     (la/svd V)
     k
     (count S)
     S-inv
     (t/diag (dfn// 1.0 (dtt/select S (range k))))
     Ut-thin
     (t/submatrix U :all (range k))
     V-pinv
     (la/mmul (la/mmul (la/transpose Vt) S-inv) (la/transpose Ut-thin))
     coeffs
     (mapv (fn [j] (dtt/mget V-pinv derivative j)) (range length))]
    coeffs))))


(def v132_l660 (def la-sg-coeffs (la-savgol-coeffs 7 3 0)))


(def v133_l662 la-sg-coeffs)


(def
 v135_l669
 (let
  [fm-coeffs
   (->
    (for
     [v (range -3 4)]
     (mapv
      (fn* [p1__123825#] (math/pow (double v) (double p1__123825#)))
      (range 4)))
    (m/seq->double-double-array)
    (org.apache.commons.math3.linear.Array2DRowRealMatrix.)
    (org.apache.commons.math3.linear.SingularValueDecomposition.)
    (.getSolver)
    (.getInverse)
    (.getRow 0)
    (vec))]
  (every?
   (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-10))
   (map vector fm-coeffs la-sg-coeffs))))


(deftest t136_l683 (is (true? v135_l669)))


(def
 v138_l689
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath savgol-filter" "lalinea savgol"],
    :ms
    [(bench-fn (fn* [] (signal/savgol-filter 7 3 0)) n-iter)
     (bench-fn (fn* [] (la-savgol-coeffs 7 3 0)) n-iter)]})))


(def v140_l701 (def fm-fd (finite/fd-coeffs 1 [-1 0 1])))


(def v141_l703 fm-fd)


(def
 v143_l707
 (def
  la-fd-coeffs
  (fn
   [n offsets]
   (let
    [noff
     (count offsets)
     rows
     (mapv
      (fn [i] (mapv (fn [j] (math/pow (double j) (double i))) offsets))
      (range noff))
     M
     (t/matrix rows)
     rhs
     (t/column
      (mapv
       (fn
        [id]
        (if
         (== (long id) (long n))
         (double (reduce * (range 1 (inc (long n)))))
         0.0))
       (range noff)))
     w
     (la/solve M rhs)]
    [offsets (mapv (fn [i] (dtt/mget w i 0)) (range noff))]))))


(def v144_l726 (def la-fd (la-fd-coeffs 1 [-1 0 1])))


(def v145_l728 la-fd)


(def
 v147_l732
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-10))
  (map vector (second fm-fd) (second la-fd))))


(deftest t148_l736 (is (true? v147_l732)))


(def v150_l740 (def fm-fd2 (finite/fd-coeffs 2 [-2 -1 0 1 2])))


(def v151_l741 (def la-fd2 (la-fd-coeffs 2 [-2 -1 0 1 2])))


(def
 v152_l743
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-10))
  (map vector (second fm-fd2) (second la-fd2))))


(deftest t153_l747 (is (true? v152_l743)))


(def
 v155_l751
 (let
  [offsets [-2 -1 0 1 2] n-iter 5000]
  (tc/dataset
   {:method ["fastmath fd-coeffs" "lalinea fd-coeffs"],
    :ms
    [(bench-fn (fn* [] (finite/fd-coeffs 2 offsets)) n-iter)
     (bench-fn (fn* [] (la-fd-coeffs 2 offsets)) n-iter)]})))


(def v157_l772 (def quad-n 5))


(def
 v158_l774
 (def
  jacobi-b
  (mapv
   (fn
    [j]
    (let [j (double (inc j))] (/ j (math/sqrt (dec (* 4.0 j j))))))
   (range (dec quad-n)))))


(def
 v159_l780
 (def fm-quad-nodes (quad/get-quadrature-points jacobi-b quad-n)))


(def v160_l783 fm-quad-nodes)


(def
 v162_l789
 (def
  la-quad-nodes
  (fn
   [b n]
   (let
    [m
     (count b)
     sz
     (inc m)
     arr
     (double-array (* sz sz))
     _
     (dotimes
      [i m]
      (let
       [v (double (b i))]
       (aset arr (+ (* i sz) (inc i)) v)
       (aset arr (+ (* (inc i) sz) i) v)))
     T
     (dtt/reshape (dtt/ensure-tensor arr) [sz sz])
     evals
     (la/real-eigenvalues T)]
    (vec (reverse (take-last n evals)))))))


(def v163_l805 (def la-nodes (la-quad-nodes jacobi-b quad-n)))


(def v164_l807 la-nodes)


(def
 v166_l811
 (every?
  (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-10))
  (map vector (sort fm-quad-nodes) (sort la-nodes))))


(deftest t167_l815 (is (true? v166_l811)))


(def
 v169_l819
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath quadrature" "lalinea quadrature"],
    :ms
    [(bench-fn
      (fn* [] (quad/get-quadrature-points jacobi-b quad-n))
      n-iter)
     (bench-fn (fn* [] (la-quad-nodes jacobi-b quad-n)) n-iter)]})))


(def v171_l832 (def fm-ince-c (poly/ince-C-coeffs 4 2 0.5 :none)))


(def v172_l834 (vec fm-ince-c))


(def
 v174_l840
 (def
  la-ince-c-coeffs-even
  (fn
   [p m e]
   (let
    [n
     (quot p 2)
     N
     (inc n)
     order
     (quot m 2)
     arr
     (double-array (* N N))
     _
     (dotimes [i N] (aset arr (+ (* i N) i) (* 4.0 i i)))
     _
     (dotimes
      [i n]
      (let
       [ip (inc i)]
       (aset arr (+ (* i N) ip) (* e (+ n ip)))
       (aset arr (+ (* ip N) i) (* e (- n i)))))
     _
     (aset arr N (* 2.0 (aget arr N)))
     M
     (dtt/reshape (dtt/ensure-tensor arr) [N N])
     {:keys [eigenvalues eigenvectors]}
     (la/eigen M)
     reals
     (la/re eigenvalues)
     indexed
     (map-indexed (fn [i _] [i (double (reals i))]) (range N))
     sorted-idx
     (map first (sort-by second indexed))
     target-idx
     (nth sorted-idx order)
     ev
     (nth eigenvectors target-idx)
     coeffs
     (mapv (fn [i] (dtt/mget ev i 0)) (range N))
     sgn
     (if (neg? (reduce + coeffs)) -1.0 1.0)]
    (mapv (fn [v] (* sgn v)) coeffs)))))


(def v175_l870 (def la-ince-c (la-ince-c-coeffs-even 4 2 0.5)))


(def v176_l872 la-ince-c)


(def
 v178_l879
 (let
  [normalize
   (fn
    [v]
    (let
     [norm
      (math/sqrt
       (reduce
        +
        (map (fn* [p1__123826#] (* p1__123826# p1__123826#)) v)))]
     (mapv (fn* [p1__123827#] (/ (double p1__123827#) norm)) v)))
   fm-normed
   (normalize (vec fm-ince-c))
   la-normed
   (normalize la-ince-c)]
  (every?
   (fn [[a b]] (< (abs (- (double a) (double b))) 1.0E-10))
   (map vector fm-normed la-normed))))


(deftest t179_l888 (is (true? v178_l879)))


(def
 v181_l892
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath ince-C" "lalinea ince-C"],
    :ms
    [(bench-fn (fn* [] (poly/ince-C-coeffs 4 2 0.5 :none)) n-iter)
     (bench-fn (fn* [] (la-ince-c-coeffs-even 4 2 0.5)) n-iter)]})))
