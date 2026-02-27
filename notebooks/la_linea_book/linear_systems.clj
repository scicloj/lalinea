;; # Solving Linear Systems
;;
;; This chapter solves systems of equations in two contrasting
;; styles. First, a **high-level** approach: build the matrices,
;; call `la/solve`, and get back a result — the mutation happens
;; inside EJML, hidden behind a functional interface. Then, an
;; **imperative** approach: Gauss-Seidel iteration, where we
;; update each unknown in place and watch convergence step
;; by step.
;;
;; Both approaches solve the same physical problem — a
;; **steady-state heat equation** — so we can compare their
;; results directly.
;;
;; _Partly inspired by the
;; [CFD Python in Clojure](https://scicloj.github.io/cfd-python-in-clojure/)
;; project, which ports computational fluid dynamics
;; simulations to Clojure._

(ns la-linea-book.linear-systems
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Heat conduction in a rod
;;
;; A metal rod of length 1 is held at fixed temperatures:
;; $T(0) = 100°$ on the left and $T(1) = 0°$ on the right.
;; After enough time the temperature reaches a **steady state**
;; — it stops changing.
;;
;; The steady-state temperature satisfies
;; [Laplace's equation](https://en.wikipedia.org/wiki/Laplace%27s_equation)
;; in one dimension:
;;
;; $$\frac{d^2 T}{dx^2} = 0$$
;;
;; We discretize the rod into $n$ interior points with spacing
;; $h = 1/(n+1)$. Replacing the second derivative with a
;; [finite difference](https://en.wikipedia.org/wiki/Finite_difference)
;; gives, at each interior point $i$:
;;
;; $$-T_{i-1} + 2\,T_i - T_{i+1} = 0$$
;;
;; Stacking these equations for all interior points produces
;; a tridiagonal linear system $A \mathbf{T} = \mathbf{b}$,
;; where the known boundary temperatures enter the right-hand
;; side.

;; ### Building the system

(def n 20)

(def T-left 100.0)
(def T-right 0.0)

;; The coefficient matrix $A$ has 2 on the diagonal and $-1$
;; on each adjacent diagonal:

(def A-heat
  (dtype/clone
   (tensor/compute-tensor
    [n n]
    (fn [i j]
      (cond (= i j) 2.0
            (= (Math/abs (- i j)) 1) -1.0
            :else 0.0))
    :float64)))

A-heat

(kind/test-last
 [(fn [m] (= [n n] (vec (dtype/shape m))))])

;; The right-hand side absorbs the boundary conditions.
;; Only the first and last entries are nonzero:

(def b-heat
  (la/column (dtype/make-reader :float64 n
                                (cond (== idx 0) T-left
                                      (== idx (dec n)) T-right
                                      :else 0.0))))
;; ### Direct solution

(def T-direct (la/solve A-heat b-heat))

T-direct

(kind/test-last
 [(fn [t] (some? t))])

;; The solution is a straight line from 100° to 0° — exactly
;; what physical intuition predicts for uniform conduction
;; with no heat source.

;; ### Temperature profile

(def x-interior
  (mapv (fn [i] (/ (double (inc i)) (double (inc n))))
        (range n)))

(-> (tc/dataset {:x x-interior
                 :T (dtype/->reader
                     (tensor/select T-direct :all 0))})
    (plotly/base {:=x :x :=y :T})
    (plotly/layer-line)
    (plotly/layer-point {:=mark-size 5})
    plotly/plot)

;; The temperature decreases linearly — the left end is
;; close to 100° and the right end close to 0°:

(tensor/mget T-direct 0 0)

(kind/test-last [(fn [t] (> t 90.0))])

(tensor/mget T-direct (dec n) 0)

(kind/test-last [(fn [t] (< t 10.0))])

;; ---
;;
;; ## Gauss-Seidel iterative solver
;;
;; For large sparse systems, direct solvers are expensive.
;; [Gauss-Seidel iteration](https://en.wikipedia.org/wiki/Gauss%E2%80%93Seidel_method)
;; updates each unknown in place:
;;
;; $$T_i^{(k+1)} = \frac{1}{a_{ii}} \left( b_i - \sum_{j \ne i} a_{ij}\, T_j \right)$$
;;
;; For our tridiagonal heat matrix this simplifies to
;;
;; $$T_i^{(k+1)} = \frac{T_{i-1} + T_{i+1} + b_i}{2}$$
;;
;; Each temperature becomes the **average of its neighbors**
;; — the discrete version of the physical law that heat flows
;; from hot to cold until equilibrium.
;;
;; This is an **imperative** algorithm — each update reads the
;; latest values from a mutable array, and the order of updates
;; matters.

;; ### Iteration with history
;;
;; Starting from $\mathbf{T} = \mathbf{0}$, we iterate and
;; record the temperature profile and residual at each step.

(def gs-result
  (let [A-arr (dtype/->double-array A-heat)
        b-arr (dtype/->double-array b-heat)
        x     (double-array n 0.0)
        iters 500]
    (loop [k 0
           history []]
      (if (>= k iters)
        {:x-final (vec x)
         :history history}
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
          (let [x-tensor (tensor/reshape
                          (tensor/ensure-tensor (dtype/clone x))
                          [n 1])
                residual (la/norm (la/sub (la/mmul A-heat x-tensor) b-heat))]
            (recur (inc k)
                   (conj history {:iteration (inc k)
                                  :residual residual
                                  :profile (vec x)}))))))))

;; ### Watching convergence
;;
;; The temperature profile at selected iterations — the
;; initial guess (all zeros) gradually relaxes into the
;; steady-state line:

(let [snapshots [1 2 5 10 50 200 500]
      rows (mapcat
            (fn [iter]
              (let [profile (:profile (nth (:history gs-result) (dec iter)))]
                (map (fn [x T]
                       {:x x :T T :iteration (str "k=" iter)})
                     x-interior profile)))
            snapshots)]
  (-> (tc/dataset rows)
      (plotly/base {:=x :x :=y :T :=color :iteration})
      (plotly/layer-line)
      plotly/plot))

;; The residual $\|A\mathbf{T}^{(k)} - \mathbf{b}\|$ drops
;; over the iterations:

(-> (tc/dataset (:history gs-result))
    (plotly/base {:=x :iteration :=y :residual})
    (plotly/layer-line)
    plotly/plot)

;; After 500 iterations the residual is small:

(-> gs-result :history last :residual)

(kind/test-last [(fn [r] (< r 1e-3))])

;; ---
;;
;; ## Comparing the two solutions
;;
;; The iterative solution should be close to the direct one:

(let [x-iter (la/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct)))

(kind/test-last [(fn [d] (< d 0.01))])
