;; # Markov Chains and PageRank
;;
;; A [Markov chain](https://en.wikipedia.org/wiki/Markov_chain) is
;; a memoryless random process described by a **transition matrix**
;; — a matrix whose rows sum to 1. Multiplying a state vector by
;; the transition matrix advances the chain one step. After many
;; steps, the distribution converges to a **stationary distribution**
;; that we can find via eigendecomposition or power iteration.
;;
;; This chapter uses matrix multiply to simulate random walks,
;; eigendecomposition to find stationary distributions analytically,
;; and imperative power iteration to find them numerically.

(ns basis-book.markov-chains
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Arg-reduction operations (argmax, argmin, etc.):
   [tech.v3.datatype.argops :as argops]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## A weather model
;;
;; Consider a simple weather model with three states:
;;
;; - **Sunny** (S)
;; - **Cloudy** (C)
;; - **Rainy** (R)
;;
;; The transition matrix $P$ gives the probability of moving
;; from state $i$ (row) to state $j$ (column):
;;
;; |      | S   | C   | R   |
;; |:-----|:----|:----|:----|
;; | S    | 0.7 | 0.2 | 0.1 |
;; | C    | 0.3 | 0.4 | 0.3 |
;; | R    | 0.2 | 0.3 | 0.5 |

(def P
  (la/matrix [[0.7 0.2 0.1]
              [0.3 0.4 0.3]
              [0.2 0.3 0.5]]))

;; Each row sums to 1:

(la/mmul P (la/column (repeat 3 1.0)))

(kind/test-last
 [(fn [sums] (< (la/norm (la/sub sums (la/column (repeat 3 1.0)))) 1e-10))])

;; ## Simulating a random walk
;;
;; Starting from Sunny, what's the probability distribution
;; after $k$ steps? We represent the state as a row vector
;; and multiply by $P$ at each step:
;;
;; $\mathbf{s}_{k} = \mathbf{s}_0 \cdot P^k$
;;
;; This is purely functional — we use `iterate` to generate
;; the sequence of state distributions.

(def initial-state (la/row [1.0 0.0 0.0]))

(def walk-history
  (let [states (iterate (fn [s] (la/mmul s P)) initial-state)]
    (mapv (fn [k s]
            {:step k
             :sunny (tensor/mget s 0 0)
             :cloudy (tensor/mget s 0 1)
             :rainy (tensor/mget s 0 2)})
          (range 20)
          (take 20 states))))

;; After 20 steps, all states converge to the same distribution
;; regardless of starting state:

(-> (tc/dataset (mapcat (fn [{:keys [step sunny cloudy rainy]}]
                          [{:step step :probability sunny :state "Sunny"}
                           {:step step :probability cloudy :state "Cloudy"}
                           {:step step :probability rainy :state "Rainy"}])
                        walk-history))
    (plotly/base {:=x :step :=y :probability :=color :state})
    (plotly/layer-line)
    plotly/plot)

;; The distribution stabilizes quickly:

(let [last-state (last walk-history)]
  [(:sunny last-state)
   (:cloudy last-state)
   (:rainy last-state)])

(kind/test-last
 [(fn [v] (< (Math/abs (- (+ (v 0) (v 1) (v 2)) 1.0)) 1e-10))])

;; ## Stationary distribution via eigendecomposition
;;
;; The stationary distribution $\boldsymbol{\pi}$ satisfies
;; $\boldsymbol{\pi} P = \boldsymbol{\pi}$, meaning
;; $\boldsymbol{\pi}$ is a left eigenvector of $P$ with
;; eigenvalue 1. Equivalently, it's a right eigenvector
;; of $P^T$ with eigenvalue 1.

(def eigen-result (la/eigen (la/transpose P)))

;; Find the eigenvector corresponding to eigenvalue ≈ 1:

(def stationary-eigen
  (let [{:keys [eigenvalues eigenvectors]} eigen-result
        idx (->> eigenvalues
                 (map-indexed (fn [i [re _im]] [i re]))
                 (sort-by (fn [[_i re]] (Math/abs (- re 1.0))))
                 first
                 first)
        ev (nth eigenvectors idx)
        col (tensor/select ev :all 0)
        total (dfn/sum col)]
    (vec (dfn/* col (/ 1.0 total)))))

stationary-eigen

(kind/test-last
 [(fn [v] (and (< (Math/abs (- (dfn/sum (double-array v)) 1.0)) 1e-10)
               (every? pos? v)))])

;; ## Power iteration
;;
;; Power iteration is an **imperative** algorithm:
;; repeatedly multiply a vector by a matrix and normalize,
;; until convergence. Unlike the functional `iterate` above,
;; this version tracks convergence explicitly with a mutable
;; state.

(def power-iteration-history
  (let [n 3 iters 40]
    (loop [pi (la/row [0.333 0.334 0.333])
           k  0
           history []]
      (if (>= k iters)
        history
        (let [new-pi (la/mmul pi P)
              new-pi (la/scale (/ 1.0 (dfn/sum new-pi)) new-pi)
              change (la/norm (la/sub new-pi pi))]
          (recur new-pi (inc k)
                 (conj history {:iteration (inc k)
                                :change change})))))))

;; The change between iterations converges to zero:

(-> (tc/dataset power-iteration-history)
    (plotly/base {:=x :iteration :=y :change})
    (plotly/layer-line)
    plotly/plot)

;; After convergence, power iteration agrees with the eigendecomposition:

(:change (last power-iteration-history))

(kind/test-last [(fn [c] (< c 1e-10))])

;; ## PageRank
;;
;; [PageRank](https://en.wikipedia.org/wiki/PageRank) models the web
;; as a directed graph and finds the "importance" of each page.
;; A random surfer follows links with probability $d$, and jumps
;; to a random page with probability $1-d$.
;;
;; ### A small web graph
;;
;; Five pages with the following link structure:
;;
;; - Page 0 → 1, 2
;; - Page 1 → 2
;; - Page 2 → 0
;; - Page 3 → 0, 2
;; - Page 4 → 0, 1, 2, 3

;; Build the hyperlink matrix $H$ (row-stochastic):

(def H
  (la/matrix [[0    1/2  1/2  0    0]
              [0    0    1    0    0]
              [1    0    0    0    0]
              [1/2  0    1/2  0    0]
              [1/4  1/4  1/4  1/4  0]]))

;; The Google matrix with damping factor $d = 0.85$:
;;
;; $M = (1-d) \cdot \frac{1}{n} \mathbf{1}\mathbf{1}^T + d \cdot H$

(def damping 0.85)

(def n-pages 5)

(def google-matrix
  (la/add (la/scale (/ (- 1.0 damping) n-pages)
                    (la/matrix (repeat n-pages (repeat n-pages 1.0))))
          (la/scale damping H)))

;; Find PageRank via power iteration:

(def pagerank
  (let [iters 50]
    (loop [pi (la/scale (/ 1.0 n-pages) (la/row (repeat n-pages 1.0)))
           k  0]
      (if (>= k iters)
        pi
        (let [new-pi (la/mmul pi google-matrix)
              new-pi (la/scale (/ 1.0 (dfn/sum new-pi)) new-pi)]
          (recur new-pi (inc k)))))))

;; Page 0 and 2 receive the most links and have the highest rank:

(-> (tc/dataset {:page ["Page 0" "Page 1" "Page 2" "Page 3" "Page 4"]
                 :rank (vec (dtype/->reader pagerank))})
    (plotly/base {:=x :page :=y :rank})
    (plotly/layer-bar)
    plotly/plot)

;; PageRank values sum to 1:

(dfn/sum pagerank)

(kind/test-last [(fn [s] (< (Math/abs (- s 1.0)) 1e-10))])

;; Page 2 (which page 0 and 1 link to, and also receives links from 3 and 4)
;; has the highest rank:

(argops/argmax pagerank)

(kind/test-last [(fn [idx] (contains? #{0 2} idx))])
