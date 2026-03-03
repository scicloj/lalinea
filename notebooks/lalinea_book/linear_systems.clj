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

(ns lalinea-book.linear-systems
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
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
  (t/clone
   (t/compute-tensor
    [n n]
    (fn [i j]
      (cond (= i j) 2.0
            (= (abs (- i j)) 1) -1.0
            :else 0.0))
    :float64)))

A-heat

(kind/test-last
 [(fn [m] (= [n n] (t/shape m)))])

;; The right-hand side absorbs the boundary conditions.
;; Only the first entry is nonzero (since $T(1) = 0$):

(def b-heat
  (t/column (t/make-reader :float64 n
                           (cond (== idx 0) T-left
                                 (== idx (dec n)) T-right
                                 :else 0.0))))

b-heat

(kind/test-last
 [(fn [b] (= [n 1] (t/shape b)))])

;; ### Direct solution

(def T-direct (la/solve A-heat b-heat))

T-direct

(kind/test-last
 [(fn [t] (some? t))])

;; The exact solution is linear: $T(x) = 100(1-x)$

(let [xs (t/make-reader :float64 n (/ (double (inc idx)) (double (inc n))))
      expected (t/column (t/make-reader :float64 n (* 100.0 (- 1.0 (double (xs idx))))))]
  (la/close? T-direct expected))

(kind/test-last [true?])

;; The solution is a straight line from 100° to 0° — exactly
;; what physical intuition predicts for uniform conduction
;; with no heat source.

;; ### Temperature profile

(def x-interior
  (t/->real-tensor
   (t/matrix
    (t/make-reader :float64 n (/ (double (inc idx)) (double (inc n)))))))

(-> (tc/dataset {:x x-interior
                 :T (t/->reader
                     (t/select T-direct :all 0))})
    (plotly/base {:=x :x :=y :T})
    (plotly/layer-line)
    (plotly/layer-point {:=mark-size 5})
    plotly/plot)

;; The temperature decreases linearly — the left end is
;; close to 100° and the right end close to 0°:

(T-direct 0 0)

(kind/test-last [(fn [t] (> t 90.0))])

(T-direct (dec n) 0)

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
;; latest values from a mutable buffer, and the order of updates
;; matters. It is a natural case for in-place computation:
;; the algorithm is inherently sequential and mutating, so
;; allocating new tensors per step would waste memory and time.
;; As discussed in the Sharing and Mutation chapter, we keep
;; the mutation in narrow scope — wrapped in a function that
;; returns an immutable result.

;; ### Iteration with history
;;
;; Starting from $\mathbf{T} = \mathbf{0}$, we iterate and
;; record the temperature profile and residual at each step.
;; We clone snapshots into a history vector for the plots
;; below — in production code we would skip the history
;; and just iterate to convergence in place.
;;
;; Since $A$ is tridiagonal, we implement the simplified update
;; directly rather than the general Gauss-Seidel row sum.

(def gs-result
  (let [b-buf (t/->reader b-heat)
        x     (t/make-container :float64 n)
        iters 500]
    (loop [k 0, history []]
      (if (>= k iters)
        {:x-final (t/clone x)
         :history history}
        (do
          (dotimes [i n]
            (let [left  (if (pos? i) (x (dec i)) 0.0)
                  right (if (< i (dec n)) (x (inc i)) 0.0)]
              (t/set-value! x i (/ (+ left right (b-buf i)) 2.0))))
          (let [x-col    (t/column (t/clone x))
                residual (la/norm (la/sub (la/mmul A-heat x-col) b-heat))]
            (recur (inc k)
                   (conj history {:iteration (inc k)
                                  :residual  residual
                                  :profile   (t/clone x)}))))))))

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

(let [x-iter (t/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct)))

(kind/test-last [(fn [d] (< d 0.01))])
