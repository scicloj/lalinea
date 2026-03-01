;; # Spectral Graph Theory
;;
;; A graph is a set of vertices connected by edges.
;; The *spectrum* — the eigenvalues of matrices derived from a graph
;; — reveals structural properties: how many connected
;; components there are, which vertices form tightly-knit clusters,
;; and how quickly information spreads across the network.
;;
;; This notebook uses linear algebra to move from the
;; **adjacency matrix** (which edges exist?) through the
;; **Laplacian matrix** (a discrete analogue of the Laplacian
;; operator from calculus) to **spectral clustering** (which vertices
;; belong together?).

(ns la-linea-book.spectral-graph-theory
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.la-linea.complex :as cx]
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
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Graph and arrow diagrams:
   [scicloj.la-linea.vis :as vis]))

;; A helper for computing row sums of a matrix:

(def row-sums
  (fn [m]
    (let [nrows (first (dtype/shape m))]
      (dtype/make-reader :float64 nrows
                         (dfn/sum (tensor/select m idx :all))))))

;; Build a graph Laplacian $L = D - A$:

(def laplacian
  (fn [adj]
    (la/sub (la/diag (row-sums adj)) adj)))

;; ## From graphs to matrices
;;
;; Consider a small social network with 6 people.
;; An edge between two people means they know each other.
;;
;; The **[adjacency matrix](https://en.wikipedia.org/wiki/Adjacency_matrix)** $A$ has $A_{ij} = 1$ if vertices $i$ and
;; $j$ are connected, and $0$ otherwise. For an undirected graph,
;; $A$ is symmetric.

(def adj
  (la/matrix [[0 1 1 0 0 0]
              [1 0 1 0 0 0]
              [1 1 0 1 0 0]
              [0 0 1 0 1 1]
              [0 0 0 1 0 1]
              [0 0 0 1 1 0]]))

;; The graph has two clusters: vertices {0, 1, 2} and {3, 4, 5},
;; connected through a single bridge edge (2–3).

(def six-pos
  [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]])

(def six-edges
  [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]])

(vis/graph-plot six-pos six-edges
                {:edge-highlight #{[2 3]}})

;; The **degree** of each vertex — how many neighbours it has:

(vec (row-sums adj))

(kind/test-last
 [(fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0]))])

;; Vertex 2 and vertex 3 have degree 3 — they serve as hubs within
;; their respective clusters and as the bridge between them.

;; ## The graph Laplacian
;;
;; The **[Laplacian matrix](https://en.wikipedia.org/wiki/Laplacian_matrix)** is defined as:
;;
;; $$L = D - A$$
;;
;; where $D$ is the diagonal degree matrix.
;; The Laplacian encodes the same connectivity as $A$, but its eigenvalues
;; have direct structural meaning.

(def L (laplacian adj))

L

(kind/test-last
 [(fn [m]
    (and (= [6 6] (dtype/shape m))
         (= 2.0 (tensor/mget m 0 0))
         (= -1.0 (tensor/mget m 0 1))))])

;; Key properties of $L$:
;;
;; - Symmetric and positive semi-definite
;; - Every row sums to zero
;; - Smallest eigenvalue is always $0$
;; - The number of zero eigenvalues equals the number of connected components

;; Let us verify that each row sums to zero:

(dfn/reduce-max (dfn/abs (row-sums L)))

(kind/test-last
 [(fn [v] (< v 1e-10))])

;; ## Eigenvalues of the Laplacian
;;
;; The eigenvalues $0 = \lambda_1 \leq \lambda_2 \leq \cdots \leq \lambda_n$
;; are called the **spectrum** of the graph.
;;
;; Note the ascending order: in spectral graph theory the convention
;; is to number eigenvalues from smallest to largest, because the
;; interesting structure lives at the **bottom** of the spectrum —
;; zero eigenvalues count connected components, and $\lambda_2$
;; measures how tightly the graph is connected. (This is the
;; opposite of PCA and SVD, where the largest values come first.)

(def eig (la/eigen L))

(def eigenvalues (la/real-eigenvalues (laplacian adj)))

eigenvalues

(kind/test-last
 [(fn [v]
    (and (< (Math/abs (first v)) 1e-10)
         (= 6 (count v))))])

;; The smallest eigenvalue is $0$ (confirming the graph is connected —
;; there is exactly one zero eigenvalue).
;;
;; The second-smallest eigenvalue $\lambda_2$ is called the
;; **[algebraic connectivity](https://en.wikipedia.org/wiki/Algebraic_connectivity)** (or **Fiedler value**). It measures
;; how well-connected the graph is — a larger $\lambda_2$ means
;; the graph is harder to disconnect.

(def fiedler-value (second eigenvalues))

fiedler-value

(kind/test-last
 [(fn [v] (and (pos? v) (< v 2.0)))])

;; The Fiedler value is small (close to 0), reflecting the fact
;; that our graph has a bottleneck: a single bridge edge at vertex 2–3.

;; ## The Fiedler vector
;;
;; The eigenvector corresponding to $\lambda_2$ is the **Fiedler vector**.
;; Its sign pattern partitions the vertices into two groups, giving
;; a spectral bisection of the graph.

(def sorted-eig-indices
  (let [vals (cx/re (:eigenvalues eig))]
    (sort-by (fn [i] (double (vals i))) (range (count (:eigenvalues eig))))))

(def fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices)))

;; The Fiedler vector entries:

(def fiedler-entries
  (vec (dtype/->reader fiedler-eigvec)))

fiedler-entries

(kind/test-last
 [(fn [v] (= 6 (count v)))])

;; Vertices with negative entries belong to one cluster,
;; vertices with positive entries belong to the other.

(def cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries))

cluster-assignment

(kind/test-last
 [(fn [v]
    (and (apply = (subvec v 0 3))
         (apply = (subvec v 3 6))
         (not= (v 0) (v 3))))])

;; The spectral bisection perfectly recovers the two clusters
;; {0, 1, 2} and {3, 4, 5}. Here is the graph with vertices
;; coloured by cluster:

(vis/graph-plot six-pos six-edges
                {:node-colors (mapv {:A "#2266cc" :B "#dd8800"}
                                    cluster-assignment)
                 :edge-highlight #{[2 3]}})

;; Let us visualize the Fiedler vector. Each vertex's value
;; shows which side of the partition it falls on.

(-> (tc/dataset {:vertex (range 6)
                 :fiedler-value fiedler-entries
                 :cluster cluster-assignment})
    (plotly/base {:=x :vertex
                  :=y :fiedler-value
                  :=color :cluster})
    (plotly/layer-bar)
    plotly/plot)

;; ## Disconnected graphs
;;
;; When we remove the bridge edge (2–3), the graph splits into
;; two components. The Laplacian then has **two** zero eigenvalues.

(def adj-disconnected
  (la/matrix [[0 1 1 0 0 0]
              [1 0 1 0 0 0]
              [1 1 0 0 0 0]
              [0 0 0 0 1 1]
              [0 0 0 1 0 1]
              [0 0 0 1 1 0]]))

(vis/graph-plot six-pos
                [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
                {:node-colors ["#2266cc" "#2266cc" "#2266cc"
                               "#dd8800" "#dd8800" "#dd8800"]})

(def disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected)))

disc-eigenvalues

(kind/test-last
 [(fn [v]
    (and (< (Math/abs (first v)) 1e-10)
         (< (Math/abs (second v)) 1e-10)
         (> (nth v 2) 0.1)))])

;; Two zero eigenvalues confirm two connected components.

;; ## The complete graph
;;
;; At the other extreme, $K_n$ (the complete graph) connects every
;; vertex to every other. All non-zero eigenvalues of its Laplacian
;; equal $n$.

(def kn 5)

(def K5-adj
  (tensor/compute-tensor [kn kn]
                         (fn [i j] (if (not= i j) 1.0 0.0))
                         :float64))

(vis/graph-plot [[0.0 1.0] [-0.951 0.309] [-0.588 -0.809] [0.588 -0.809] [0.951 0.309]]
                [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
                {})

(def K5-eigenvalues
  (la/real-eigenvalues (laplacian K5-adj)))

K5-eigenvalues

(kind/test-last
 [(fn [v]
    (and (< (Math/abs (first v)) 1e-10)
         (every? (fn [x] (< (Math/abs (- x 5.0)) 1e-10))
                 (rest v))))])

;; Eigenvalue $0$ (multiplicity 1) and eigenvalue $5$ (multiplicity 4).
;; The high algebraic connectivity ($\lambda_2 = 5$) reflects
;; maximal connectivity.

;; ## The cycle graph
;;
;; $C_n$ connects each vertex to its two neighbours in a ring.
;; Its Laplacian eigenvalues have a known formula:
;;
;; $$\lambda_k = 2 - 2\cos\!\left(\frac{2\pi k}{n}\right),\quad k = 0,\ldots,n-1$$

(def cn 8)

(def cycle-adj
  (tensor/compute-tensor [cn cn]
                         (fn [i j] (if (or (= j (mod (inc i) cn))
                                           (= j (mod (+ i (dec cn)) cn)))
                                     1.0 0.0))
                         :float64))

(vis/graph-plot [[0.0 1.0] [-0.707 0.707] [-1.0 0.0] [-0.707 -0.707] [-0.0 -1.0] [0.707 -0.707] [1.0 -0.0] [0.707 0.707]]
                [[0 1] [1 2] [2 3] [3 4] [4 5] [5 6] [6 7] [7 0]]
                {})

(def cycle-eigenvalues
  (la/real-eigenvalues (laplacian cycle-adj)))

(def cycle-theoretical
  (sort (dtype/make-reader :float64 cn
                           (- 2.0 (* 2.0 (Math/cos (/ (* 2.0 Math/PI idx) cn)))))))

;; Computed eigenvalues:

cycle-eigenvalues

;; Theoretical eigenvalues:

cycle-theoretical

;; They agree to machine precision:

(< (dfn/reduce-max
    (dfn/abs (dfn/- cycle-eigenvalues cycle-theoretical)))
   1e-10)

(kind/test-last [true?])

;; ## The path graph
;;
;; A path $P_n$ is a chain of $n$ vertices. Its Laplacian eigenvalues are:
;;
;; $$\lambda_k = 2 - 2\cos\!\left(\frac{\pi k}{n}\right),\quad k = 0,\ldots,n-1$$

(def pn 6)

(def path-adj
  (tensor/compute-tensor [pn pn]
                         (fn [i j] (if (= 1 (Math/abs (- i j))) 1.0 0.0))
                         :float64))

(vis/graph-plot [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
                [[0 1] [1 2] [2 3] [3 4] [4 5]]
                {:width 350})

(def path-eigenvalues
  (la/real-eigenvalues (laplacian path-adj)))

path-eigenvalues

(def path-theoretical
  (sort (dtype/make-reader :float64 pn
                           (- 2.0 (* 2.0 (Math/cos (/ (* Math/PI idx) pn)))))))

path-theoretical

(< (dfn/reduce-max
    (dfn/abs (dfn/- path-eigenvalues path-theoretical)))
   1e-10)

(kind/test-last [true?])

;; ## [Spectral clustering](https://en.wikipedia.org/wiki/Spectral_clustering)
;;
;; The Fiedler vector gives a 2-way partition. For $k$-way clustering,
;; we use the first $k$ eigenvectors. Each vertex becomes a point in
;; $\mathbb{R}^k$, and we cluster these points.
;;
;; Consider a graph with three clear communities:

(def community-adj
  (la/matrix
   [;; Community 1: vertices 0-2
    [0 1 1 0 0 0 0 0 0]
    [1 0 1 0 0 0 0 0 0]
    [1 1 0 1 0 0 0 0 0]
    ;; Community 2: vertices 3-5
    [0 0 1 0 1 1 0 0 0]
    [0 0 0 1 0 1 0 0 0]
    [0 0 0 1 1 0 1 0 0]
    ;; Community 3: vertices 6-8
    [0 0 0 0 0 1 0 1 1]
    [0 0 0 0 0 0 1 0 1]
    [0 0 0 0 0 0 1 1 0]]))

(vis/graph-plot [[0 0.8] [0 -0.8] [1.5 0]
                 [3.5 0] [5 0.8] [5 -0.8]
                 [6.5 0] [8 0.8] [8 -0.8]]
                [[0 1] [0 2] [1 2]
                 [2 3]
                 [3 4] [3 5] [4 5]
                 [5 6]
                 [6 7] [6 8] [7 8]]
                {:node-colors ["#2266cc" "#2266cc" "#2266cc"
                               "#228833" "#228833" "#228833"
                               "#dd8800" "#dd8800" "#dd8800"]
                 :edge-highlight #{[2 3] [5 6]}
                 :width 400})

(def comm-eig (la/eigen (laplacian community-adj)))

;; The eigenvalues sorted ascending:

(def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj)))

comm-eigenvalues

(kind/test-last
 [(fn [v]
    (and (= 9 (count v))
         (< (Math/abs (first v)) 1e-10)
         (< (nth v 2) 1.0)
         (> (nth v 3) (* 2.0 (nth v 2)))))])

(-> (tc/dataset {:index (range (count comm-eigenvalues))
                 :eigenvalue comm-eigenvalues})
    (plotly/base {:=x :index :=y :eigenvalue})
    (plotly/layer-bar)
    plotly/plot)

;; The three smallest eigenvalues are small, then a jump to the fourth.
;; This **spectral gap** signals three communities.

;; To cluster, we use the eigenvectors corresponding to the three
;; smallest eigenvalues:

(def sorted-comm-indices
  (let [vals (cx/re (:eigenvalues comm-eig))]
    (sort-by (fn [i] (double (vals i))) (range (count (:eigenvalues comm-eig))))))

;; The spectral embedding — each vertex gets coordinates from
;; the second and third smallest eigenvectors:

(def embed-data
  (let [ev2 (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 1))
        ev3 (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 2))]
    (tc/dataset {:vertex (range 9)
                 :x (dtype/->reader ev2)
                 :y (dtype/->reader ev3)
                 :community (mapv #(cond (<= % 2) "A"
                                         (<= % 5) "B"
                                         :else "C")
                                  (range 9))})))

(-> embed-data
    (plotly/base {:=x :x :=y :y :=color :community})
    (plotly/layer-point {:=mark-size 14})
    plotly/plot)

;; Vertices from the same community cluster tightly in spectral space,
;; even though we only used the raw Laplacian eigenvectors.

;; ## [Cheeger's inequality](https://en.wikipedia.org/wiki/Cheeger%27s_inequality)
;;
;; Cheeger's inequality links the algebraic connectivity $\lambda_2$ to
;; the **edge expansion** (or conductance) $h(G)$ of the graph:
;;
;; $$\frac{\lambda_2}{2} \leq h(G) \leq \sqrt{2 \lambda_2}$$
;;
;; The conductance measures the minimum ratio of boundary
;; edges to the smaller side of a partition.
;;
;; For our original 6-vertex graph, the optimal cut is the single
;; bridge edge (2–3), dividing into equal halves of size 3.
;; So $h(G) = 1/3$.

(def conductance (/ 1.0 3.0))

(and (<= (/ fiedler-value 2.0) conductance)
     (<= conductance (Math/sqrt (* 2.0 fiedler-value))))

(kind/test-last [true?])

;; Cheeger's inequality is satisfied — the spectral gap
;; and the combinatorial bottleneck are in agreement.

;; ---
;;
;; ## Summary
;;
;; This notebook demonstrated:
;;
;; - **Adjacency matrix** $\to$ **Laplacian** $L = D - A$
;; - **Eigenvalues of $L$**: zero count = connected components,
;;   $\lambda_2$ = algebraic connectivity
;; - **Fiedler vector**: sign pattern gives spectral bisection
;; - **Disconnected graphs**: multiple zero eigenvalues
;; - **Complete and cycle graphs**: closed-form eigenvalue formulas verified
;; - **Spectral clustering**: $k$ eigenvectors $\to$ $k$-way partition
;; - **Cheeger's inequality**: spectral gap bounds edge expansion
