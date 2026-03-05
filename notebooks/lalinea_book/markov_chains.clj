;; # Markov Chains and PageRank
;;
;; *Prerequisites: [matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication)
;; and [eigenvalues](https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors)
;; (covered in the [Eigenvalues and Decompositions](eigenvalues_and_decompositions.html) chapter).
;; The chapter uses eigendecomposition to find stationary
;; distributions without re-deriving it.*
;;
;; A [Markov chain](https://en.wikipedia.org/wiki/Markov_chain) is
;; a memoryless random process described by a **transition matrix**
;; — a matrix whose rows sum to 1. Multiplying a state vector by
;; the transition matrix advances the chain one step. Under mild conditions
;; ([irreducibility](https://en.wikipedia.org/wiki/Markov_chain#Reducibility) and [aperiodicity](https://en.wikipedia.org/wiki/Markov_chain#Periodicity)), after many
;; steps, the distribution converges to a **[stationary distribution](https://en.wikipedia.org/wiki/Stationary_distribution)**
;; that we can find via eigendecomposition or [power iteration](https://en.wikipedia.org/wiki/Power_iteration).
;;
;; This chapter uses matrix multiply to propagate state distributions,
;; eigendecomposition to find stationary distributions analytically,
;; and power iteration to find them numerically.

(ns lalinea-book.markov-chains
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.elementwise :as el]
   [scicloj.lalinea.tensor :as t]
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
  (t/matrix [[0.7 0.2 0.1]
             [0.3 0.4 0.3]
             [0.2 0.3 0.5]]))

;; The transition structure as a directed graph:

^:kindly/hide-code
(kind/mermaid "graph LR
  S[\"Sunny\"] -->|0.7| S
  S -->|0.2| C[\"Cloudy\"]
  S -->|0.1| R[\"Rainy\"]
  C -->|0.3| S
  C -->|0.4| C
  C -->|0.3| R
  R -->|0.2| S
  R -->|0.3| C
  R -->|0.5| R")

;; Each row sums to 1:

(la/mmul P (t/ones 3 1))

(kind/test-last
 [(fn [sums] (< (la/norm (el/- sums (t/ones 3 1))) 1e-10))])

;; ## Propagating state distributions
;;
;; Starting from Sunny, what's the probability distribution
;; after $k$ steps? We represent the state as a row vector
;; and multiply by $P$ at each step:
;;
;; $\mathbf{s}_{k} = \mathbf{s}_0 \cdot P^k$
;;
;; This is purely functional — we use `iterate` to generate
;; the sequence of state distributions.

(def initial-state (t/row [1.0 0.0 0.0]))

(def n-steps 20)

(def walk-history
  (vec (take n-steps
             (iterate (fn [s] (la/mmul s P)) initial-state))))

;; After 20 steps, all states converge to the same distribution
;; regardless of starting state:

(-> (tc/dataset
     (mapcat (fn [k s]
               [{:step k :probability (s 0 0) :state "Sunny"}
                {:step k :probability (s 0 1) :state "Cloudy"}
                {:step k :probability (s 0 2) :state "Rainy"}])
             (range n-steps)
             walk-history))
    (plotly/base {:=x :step :=y :probability :=color :state})
    (plotly/layer-line)
    plotly/plot)

;; The distribution stabilizes quickly:

(let [final (last walk-history)]
  (el/sum final))

(kind/test-last [(fn [s] (< (abs (- s 1.0)) 1e-10))])

;; The last two steps are nearly identical:

(la/close? (last walk-history) (nth walk-history (- n-steps 2)) 1e-6)

(kind/test-last [true?])

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
        reals (el/re eigenvalues)
        idx (el/argmin (el/abs (el/- reals 1.0)))
        ev (nth eigenvectors idx)
        total (el/sum (t/flatten ev))]
    (t/flatten (el/scale ev (/ 1.0 total)))))

stationary-eigen

(kind/test-last
 [(fn [v] (and (< (abs (- (el/sum v) 1.0)) 1e-10)
               (every? pos? v)))])

;; The stationary distribution from eigendecomposition should
;; agree with the random walk convergence:

(la/close? stationary-eigen
           (t/flatten (last walk-history))
           1e-4)

(kind/test-last [true?])

;; ## Power iteration
;; [Power iteration](https://en.wikipedia.org/wiki/Power_iteration)
;; repeatedly multiplies a vector by a matrix and normalizes.
;; Unlike the `iterate` version above, this one uses `loop` to
;; track the convergence rate at each step.

(def power-iteration-history
  (let [n 3 iters 40]
    (loop [pi (t/row [0.333 0.334 0.333])
           k  0
           history []]
      (if (>= k iters)
        history
        (let [new-pi (la/mmul pi P)
              new-pi (el/scale new-pi (/ 1.0 (el/sum new-pi)))
              change (la/norm (el/- new-pi pi))]
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
;; ### A course catalog graph
;;
;; Imagine a university website where each course page links to
;; its prerequisites and related courses. Eight courses form
;; two clusters — a math track and a CS track — bridged by
;; Machine Learning and AI.

(def course-names
  ["Calculus" "Linear Algebra" "Statistics"
   "Intro Programming" "Data Structures"
   "Machine Learning" "Databases" "AI"])

(def n-pages (count course-names))

^:kindly/hide-code
(kind/mermaid "graph LR
  subgraph Math
    Calc[\"Calculus\"]
    LA[\"Linear Algebra\"]
    Stats[\"Statistics\"]
  end
  subgraph CS
    IP[\"Intro Programming\"]
    DS[\"Data Structures\"]
    DB[\"Databases\"]
  end
  ML[\"Machine Learning\"]
  AI[\"AI\"]
  Calc --> LA
  LA --> Calc
  LA --> Stats
  Stats --> Calc
  Stats --> LA
  IP --> DS
  DS --> IP
  DS --> DB
  ML --> LA
  ML --> Stats
  ML --> IP
  ML --> AI
  DB --> DS
  DB --> IP
  DB --> ML
  AI --> ML
  AI --> DS
  AI --> Stats
  AI --> LA")

;; Build the hyperlink matrix $H$ (row-stochastic).
;; Each row sums to 1: the weight of each outbound link
;; is $1 / \text{(number of outbound links)}$.

(def H
  (t/matrix [[0    1    0    0    0    0    0    0]   ;; Calculus → LA
             [1/2  0    1/2  0    0    0    0    0]   ;; LA → Calc, Stats
             [1/2  1/2  0    0    0    0    0    0]   ;; Stats → Calc, LA
             [0    0    0    0    1    0    0    0]   ;; IntroP → DS
             [0    0    0    1/2  0    0    1/2  0]   ;; DS → IntroP, DB
             [0    1/4  1/4  1/4  0    0    0    1/4]   ;; ML → LA, Stats, IntroP, AI
             [0    0    0    1/3  1/3  1/3  0    0]   ;; DB → DS, IntroP, ML
             [0    1/4  1/4  0    1/4  1/4  0    0]]));; AI → LA, Stats, DS, ML

;; The Google matrix with damping factor $d = 0.85$:
;;
;; $M = (1-d) \cdot \frac{1}{n} \mathbf{1}\mathbf{1}^T + d \cdot H$

(def damping 0.85)

(def google-matrix
  (el/+ (el/scale (t/ones n-pages n-pages)
                  (/ (- 1.0 damping) n-pages))
        (el/scale H damping)))

;; Verify that each row sums to 1:

(la/mmul google-matrix (t/ones n-pages 1))

(kind/test-last
 [(fn [sums] (< (la/norm (el/- sums (t/ones n-pages 1))) 1e-10))])

;; Find PageRank via power iteration:

(def pagerank
  (let [iters 50]
    (loop [pi (el/scale (t/ones 1 n-pages) (/ 1.0 n-pages))
           k  0]
      (if (>= k iters)
        pi
        (let [new-pi (la/mmul pi google-matrix)
              new-pi (el/scale new-pi (/ 1.0 (el/sum new-pi)))]
          (recur new-pi (inc k)))))))

;; Foundational courses rank highest — they are referenced across
;; many syllabi:

(-> (tc/dataset {:course course-names
                 :rank (t/->reader pagerank)})
    (plotly/base {:=x :course :=y :rank})
    (plotly/layer-bar)
    plotly/plot)

;; PageRank values sum to 1:

(el/sum pagerank)

(kind/test-last [(fn [s] (< (abs (- s 1.0)) 1e-10))])

;; Linear Algebra receives the most inbound links (from Calculus,
;; Statistics, Machine Learning, and AI) and ranks highest:

(nth course-names (el/argmax pagerank))

(kind/test-last [(fn [name] (= "Linear Algebra" name))])
