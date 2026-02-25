;; # Solving Linear Systems
;;
;; Linear algebra shines brightest when it meets real problems.
;; This chapter solves systems of equations in two contrasting
;; styles: **purely functional** (build the matrices, call `solve`)
;; and **imperative** (iterate in-place until convergence).

(ns basis-book.linear-systems
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Seeded random number generation (https://generateme.github.io/fastmath/):
   [fastmath.random :as frand]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Least-squares polynomial fitting
;;
;; Given noisy measurements $(x_i, y_i)$, fit a polynomial
;; $y = \beta_0 + \beta_1 x + \cdots + \beta_d x^d$
;; by solving the normal equations
;;
;; $X^T X \, \boldsymbol{\beta} = X^T \mathbf{y}$
;;
;; where $X$ is the [Vandermonde matrix](https://en.wikipedia.org/wiki/Vandermonde_matrix).
;; This is a purely functional approach — build data, build matrices,
;; call `la/solve`.

;; ### Generate noisy data

(def xs (vec (range -3.0 3.1 0.3)))

(def ys
  (let [rng (frand/rng :mersenne 42)]
    (mapv (fn [x]
            (+ (* 0.5 x x) (* -1.2 x) 3.0
               (* 0.5 (frand/drandom rng -0.5 0.5))))
          xs)))

;; ### Build the Vandermonde matrix
;;
;; For degree $d$, row $i$ of $X$ is $[1, x_i, x_i^2, \ldots, x_i^d]$.

(def degree 2)

(def vandermonde
  (tensor/compute-tensor [(count xs) (inc degree)]
                         (fn [r c] (Math/pow (nth xs r) (double c)))
                         :float64))

vandermonde

(kind/test-last
 [(fn [m] (= [(count xs) (inc degree)]
             (vec (dtype/shape m))))])

;; ### Solve the normal equations
;;
;; $\boldsymbol{\beta} = (X^T X)^{-1} X^T \mathbf{y}$

(def y-col (la/column ys))

(def beta
  (la/solve (la/mmul (la/transpose vandermonde) vandermonde)
            (la/mmul (la/transpose vandermonde) y-col)))

beta

;; The fitted coefficients should be close to
;; $\beta_0 \approx 3$, $\beta_1 \approx -1.2$, $\beta_2 \approx 0.5$.

(kind/test-last
 [(fn [b] (let [b0 (tensor/mget b 0 0)
                b1 (tensor/mget b 1 0)
                b2 (tensor/mget b 2 0)]
            (and (< (Math/abs (- b0 3.0)) 0.5)
                 (< (Math/abs (- b1 -1.2)) 0.5)
                 (< (Math/abs (- b2 0.5)) 0.3))))])

;; ### Plot data and fitted curve

(def fitted-ys
  (vec (dtype/->reader (tensor/select (la/mmul vandermonde beta) :all 0))))

(-> (tc/dataset {:x (concat xs xs)
                 :y (concat ys fitted-ys)
                 :series (concat (repeat (count xs) "data")
                                 (repeat (count xs) "fit"))})
    (plotly/base {:=x :x :=y :y :=color :series})
    (plotly/layer-point {:=mark-size 6})
    (plotly/layer-line)
    plotly/plot)

;; ## Weighted least squares
;;
;; Sometimes measurements have different reliabilities.
;; Weighted least squares solves
;;
;; $X^T W X \, \boldsymbol{\beta} = X^T W \mathbf{y}$
;;
;; where $W$ is a diagonal weight matrix. Points near the center
;; get higher weight.

(def weights
  (mapv (fn [x] (Math/exp (- (* 0.5 x x)))) xs))

(def W (la/diag weights))

(def beta-weighted
  (la/solve (la/mmul (la/transpose vandermonde) (la/mmul W vandermonde))
            (la/mmul (la/transpose vandermonde) (la/mmul W y-col))))

beta-weighted

(kind/test-last
 [(fn [b] (let [b0 (tensor/mget b 0 0)]
            (< (Math/abs (- b0 3.0)) 0.5)))])

;; ## Gauss-Seidel iterative solver
;;
;; For large sparse systems, direct solvers are expensive.
;; [Gauss-Seidel iteration](https://en.wikipedia.org/wiki/Gauss%E2%80%93Seidel_method)
;; updates each unknown in place:
;;
;; $x_i^{(k+1)} = \frac{1}{a_{ii}} \left( b_i - \sum_{j \ne i} a_{ij} x_j \right)$
;;
;; This is an **imperative** algorithm — each update reads the
;; latest values from a mutable array.

;; ### A diagonally dominant test system
;;
;; Gauss-Seidel converges when $A$ is
;; [diagonally dominant](https://en.wikipedia.org/wiki/Diagonally_dominant_matrix).

(def A-gs
  (la/matrix [[10  1  2]
              [1 12  3]
              [2  3 15]]))

(def b-gs (la/column [13 16 20]))

;; Direct solution for comparison:

(def x-direct (la/solve A-gs b-gs))

x-direct

(kind/test-last [(fn [x] (some? x))])

;; ### Imperative iteration

(def gauss-seidel-history
  (let [n     3
        A-arr (dtype/->double-array A-gs)
        b-arr (dtype/->double-array b-gs)
        x     (double-array n 0.0)
        iters 30]
    (loop [k 0
           history []]
      (if (>= k iters)
        history
        (do
          (dotimes [i n]
            (let [sigma (loop [j 0 s 0.0]
                          (if (>= j n) s
                              (recur (inc j)
                                     (if (= i j) s
                                         (+ s (* (aget A-arr (+ (* i n) j))
                                                 (aget x j)))))))]
              (aset x i (/ (- (aget b-arr i) sigma)
                           (aget A-arr (+ (* i n) i))))))
          (let [x-tensor (tensor/reshape (tensor/ensure-tensor (dtype/clone x)) [n 1])
                residual (la/norm (la/sub (la/mmul A-gs x-tensor) b-gs))]
            (recur (inc k)
                   (conj history {:iteration (inc k)
                                  :residual residual}))))))))

;; Convergence — the residual drops rapidly:

(-> (tc/dataset gauss-seidel-history)
    (plotly/base {:=x :iteration :=y :residual})
    (plotly/layer-line)
    plotly/plot)

;; After 30 iterations the residual is tiny:

(-> gauss-seidel-history last :residual)

(kind/test-last [(fn [r] (< r 1e-10))])

;; ## Comparing direct and iterative solutions
;;
;; The iterative solution should converge to the direct one.

(let [x-iter (let [n 3
                   A-arr (dtype/->double-array A-gs)
                   b-arr (dtype/->double-array b-gs)
                   x (double-array n 0.0)]
               (dotimes [_ 50]
                 (dotimes [i n]
                   (let [sigma (loop [j 0 s 0.0]
                                 (if (>= j n) s
                                     (recur (inc j)
                                            (if (= i j) s
                                                (+ s (* (aget A-arr (+ (* i n) j))
                                                        (aget x j)))))))]
                     (aset x i (/ (- (aget b-arr i) sigma)
                                  (aget A-arr (+ (* i n) i)))))))
               (tensor/reshape (tensor/ensure-tensor (dtype/clone x)) [n 1]))]
  (la/norm (la/sub x-iter x-direct)))

(kind/test-last [(fn [d] (< d 1e-10))])
