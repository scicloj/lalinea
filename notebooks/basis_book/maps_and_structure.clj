;; # Maps and Structure
;;
;; A matrix is more than a grid of numbers — it is a **linear map**,
;; a function that transforms vectors while preserving the rules of
;; addition and scaling. This chapter explores what matrices do
;; geometrically and introduces the structural concepts — subspaces,
;; rank, and the four fundamental subspaces — that reveal the full
;; picture of a linear map.

(ns basis-book.maps-and-structure
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Arrow diagrams for 2D vectors:
   [scicloj.basis.vis :as vis]))

;; We re-use the standard example vectors from the previous chapter:

(def u (la/column [3 1]))
(def v (la/column [1 2]))

;; ---
;;
;; ## Linear maps
;;
;; ### Matrices as transformations
;;
;; A **linear map** (or **linear transformation**) is a function
;; between vector spaces that respects the structure:
;;
;; - $T(\mathbf{u} + \mathbf{v}) = T(\mathbf{u}) + T(\mathbf{v})$
;; - $T(\alpha \mathbf{v}) = \alpha\, T(\mathbf{v})$
;;
;; Informally: the map commutes with addition and scaling.
;; These two rules have a profound consequence:
;;
;; **Every linear map from $\mathbb{R}^n$ to $\mathbb{R}^m$ can
;; be written as multiplication by an $m \times n$ matrix.**
;;
;; This is why matrices are so important — they are the
;; universal representation of linear maps.

;; ### Example: rotation
;;
;; Rotation by 90° counter-clockwise:

(def R90
  (la/matrix [[0 -1]
              [1  0]]))

;; It maps the x-axis to the y-axis:

(la/mmul R90 (la/column [1 0]))

(kind/test-last
 [(fn [r] (and (< (Math/abs (tensor/mget r 0 0)) 1e-10)
               (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1e-10)))])

;; And the y-axis to the negative x-axis:

(la/mmul R90 (la/column [0 1]))

(kind/test-last
 [(fn [r] (and (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1e-10)
               (< (Math/abs (tensor/mget r 1 0)) 1e-10)))])

;; Visualising the effect on our vectors — each arrow
;; rotates 90° counter-clockwise:

(vis/arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
                 {:label "Ru" :xy [-1 3] :color "#2266cc" :dashed? true}
                 {:label "v" :xy [1 2] :color "#cc4422"}
                 {:label "Rv" :xy [-2 1] :color "#cc4422" :dashed? true}]
                {})

;; Let us verify the linearity properties.
;;
;; **Additivity**: $R(\mathbf{u} + \mathbf{v}) = R(\mathbf{u}) + R(\mathbf{v})$:

(la/close? (la/mmul R90 (la/add u v))
           (la/add (la/mmul R90 u) (la/mmul R90 v)))

(kind/test-last [true?])

;; **Homogeneity**: $R(3\mathbf{u}) = 3 R(\mathbf{u})$:

(la/close? (la/mmul R90 (la/scale 3.0 u))
           (la/scale 3.0 (la/mmul R90 u)))

(kind/test-last [true?])

;; ### Example: stretching

(def stretch-mat
  (la/matrix [[3 0]
              [0 1]]))

;; Stretches the x-direction by 3, leaves y unchanged.
;; Let us see what it does to a set of points on the unit circle:

(let [angles (mapv #(* 2.0 Math/PI (/ % 40.0)) (range 41))
      circle-x (mapv #(Math/cos %) angles)
      circle-y (mapv #(Math/sin %) angles)
      stretched (mapv (fn [cx cy]
                        (let [out (la/mmul stretch-mat (la/column [cx cy]))]
                          [(tensor/mget out 0 0) (tensor/mget out 1 0)]))
                      circle-x circle-y)]
  (-> (tc/dataset {:x (mapv first stretched)
                   :y (mapv second stretched)
                   :shape (repeat 41 "stretched")})
      (tc/concat (tc/dataset {:x circle-x
                              :y circle-y
                              :shape (repeat 41 "original")}))
      (plotly/base {:=x :x :=y :y :=color :shape})
      (plotly/layer-line)
      plotly/plot))

;; The unit circle becomes an ellipse — the matrix stretches
;; one axis more than the other.

;; ### Example: projection
;;
;; Not all linear maps are invertible. A **projection** collapses
;; one dimension, losing information:

(def proj-xy
  (la/matrix [[1 0 0]
              [0 1 0]
              [0 0 0]]))

;; This projects 3D space onto the $xy$-plane by zeroing out $z$:

(la/mmul proj-xy (la/column [5 3 7]))

(kind/test-last
 [(fn [r] (and (= 5.0 (tensor/mget r 0 0))
               (= 3.0 (tensor/mget r 1 0))
               (= 0.0 (tensor/mget r 2 0))))])

;; The $z$-component is gone and cannot be recovered.
;; The matrix is not invertible — its determinant is zero:

(la/det proj-xy)

(kind/test-last
 [(fn [d] (< (Math/abs d) 1e-10))])

;; ### Example: shear
;;
;; A **shear** slides one direction proportionally to another.
;; It changes shape but preserves area (determinant = 1):

(def shear-mat
  (la/matrix [[1 2]
              [0 1]]))

(la/det shear-mat)

(kind/test-last
 [(fn [d] (< (Math/abs (- d 1.0)) 1e-10))])

;; The shear fixes $\mathbf{e}_1$ but slides $\mathbf{e}_2$
;; sideways — it tilts the vertical axis:

(vis/arrow-plot [{:label "e₁" :xy [1 0] :color "#2266cc"}
                 {:label "e₂" :xy [0 1] :color "#cc4422"}
                 {:label "Se₁" :xy [1 0] :color "#2266cc" :dashed? true}
                 {:label "Se₂" :xy [2 1] :color "#cc4422" :dashed? true}]
                {})

;; ### Composition of maps
;;
;; Applying one linear map and then another is the same as
;; multiplying their matrices. The order matters —
;; matrix multiplication is **not commutative** in general.
;;
;; Rotate then stretch $\neq$ stretch then rotate:

(def AB (la/mmul stretch-mat R90))
(def BA (la/mmul R90 stretch-mat))

(la/norm (la/sub AB BA))

(kind/test-last
 [(fn [d] (> d 0.1))])

;; Applying both orderings to $\mathbf{e}_1 = [1,0]^T$ shows
;; different results:

(vis/arrow-plot [{:label "e₁" :xy [1 0] :color "#999999"}
                 {:label "R then S" :xy [0 1] :color "#2266cc"}
                 {:label "S then R" :xy [0 3] :color "#cc4422"}]
                {:width 200})

;; The result depends on the order — just like "put on socks,
;; then shoes" is different from "put on shoes, then socks."

;; ---
;;
;; ## Subspaces
;;
;; ### What is a subspace?
;;
;; A **subspace** is a subset of a vector space that is itself
;; a vector space — it is closed under addition and scaling.
;; Every subspace must contain the zero vector.
;;
;; Examples in $\mathbb{R}^3$:
;;
;; - The zero vector alone (dimension 0)
;; - Any line through the origin (dimension 1)
;; - Any plane through the origin (dimension 2)
;; - All of $\mathbb{R}^3$ (dimension 3)
;;
;; These are the **only** subspaces of $\mathbb{R}^3$.
;; Notably, a line or plane that does not pass through the
;; origin is **not** a subspace.

;; ### Column space and null space
;;
;; Every matrix $A$ defines two important subspaces:
;;
;; - The **column space** (or **range**) $\text{Col}(A)$: the set
;;   of all vectors $\mathbf{b}$ such that $A\mathbf{x} = \mathbf{b}$
;;   has a solution. It is the span of $A$'s columns — the
;;   set of outputs the map can produce.
;;
;; - The **null space** (or **kernel**) $\text{Null}(A)$: the set
;;   of all vectors $\mathbf{x}$ with $A\mathbf{x} = \mathbf{0}$.
;;   These are the inputs that the map annihilates.
;;
;; Consider a matrix whose third column equals the sum of
;; the first two:

(def M
  (la/matrix [[1 2 3]
              [4 5 9]
              [7 8 15]]))

;; Since column 3 = column 1 + column 2, the vector $[1, 1, -1]^T$
;; is in the null space:

(la/mmul M (la/column [1 1 -1]))

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; Think of it this way: if we walk 1 unit along column 1,
;; 1 unit along column 2, and $-1$ unit along column 3, we
;; get back to the origin — because column 3 is redundant.

;; The null space is not just $\{[1,1,-1]^T\}$ but all its
;; scalar multiples. Any scalar times a null space vector
;; is again a null space vector (that is the subspace property):

(la/mmul M (la/scale 7.0 (la/column [1 1 -1])))

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; ---
;;
;; ## Rank and the rank-nullity theorem
;;
;; ### Rank
;;
;; The **rank** of a matrix is the dimension of its column space —
;; the number of genuinely independent columns.
;;
;; The SVD reveals the rank: it equals the number of non-zero
;; singular values.

(def sv-M (vec (:S (la/svd M))))

sv-M

(kind/test-last
 [(fn [v] (= 3 (count v)))])

(def rank-M (count (filter #(> % 1e-10) sv-M)))

rank-M

(kind/test-last
 [(fn [r] (= r 2))])

;; Although $M$ is $3 \times 3$, it has rank 2 — only two
;; of its three columns carry independent information.

;; ### The rank-nullity theorem
;;
;; One of the most important theorems in linear algebra:
;;
;; $$\text{rank}(A) + \text{nullity}(A) = n$$
;;
;; where $n$ is the number of columns. The rank counts
;; the dimensions "used" by the map, and the nullity counts
;; the dimensions "collapsed" to zero. Together they account
;; for all $n$ input dimensions.
;;
;; For $M$: rank $2$ + nullity $1$ = $3$ columns.

;; We can extract the null space from the SVD. The columns
;; of $V$ corresponding to zero singular values span it:

(def svd-M (la/svd M))

(def null-basis
  (let [sv (vec (:S svd-M))
        Vt (:Vt svd-M)
        null-idx (vec (keep-indexed (fn [i s] (when (< s 1e-10) i)) sv))]
    (la/submatrix (la/transpose Vt) :all null-idx)))

;; Verify it lies in the null space:

(la/norm (la/mmul M null-basis))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; ### What rank tells you about $A\mathbf{x} = \mathbf{b}$
;;
;; - Full column rank ($r = n$): the system has **at most one**
;;   solution for any $\mathbf{b}$ (the null space is trivial)
;; - Full row rank ($r = m$): the system has **at least one**
;;   solution for every $\mathbf{b}$ (the column space is all of $\mathbb{R}^m$)
;; - Full rank ($r = m = n$): **exactly one** solution — the
;;   matrix is invertible

;; Full-rank square matrix:

(def A-full (la/matrix [[2 1] [1 3]]))

(count (filter #(> % 1e-10) (vec (:S (la/svd A-full)))))

(kind/test-last
 [(fn [r] (= r 2))])

;; Invertible — unique solution for any right-hand side:

(la/solve A-full (la/column [5 7]))

(kind/test-last
 [(fn [x] (some? x))])

;; Rank-deficient matrix — `la/solve` returns nil:

(la/solve M (la/column [1 2 3]))

(kind/test-last [nil?])

;; ---
;;
;; ## The four fundamental subspaces
;;
;; Every $m \times n$ matrix $A$ defines not two but **four**
;; subspaces, arranged in a beautiful symmetric pattern:
;;
;; | Subspace | Dimension | Lives in |
;; |:---------|:----------|:---------|
;; | Column space $\text{Col}(A)$ | $r$ | $\mathbb{R}^m$ |
;; | Left null space $\text{Null}(A^T)$ | $m - r$ | $\mathbb{R}^m$ |
;; | Row space $\text{Row}(A) = \text{Col}(A^T)$ | $r$ | $\mathbb{R}^n$ |
;; | Null space $\text{Null}(A)$ | $n - r$ | $\mathbb{R}^n$ |
;;
;; The key insight: in each ambient space, the two subspaces
;; are **orthogonal complements** — they are perpendicular
;; and together they fill the whole space.
;;
;; Let us compute all four for our rank-2 matrix $M$:

;; **Column space** — the columns of $U$ corresponding to
;; non-zero singular values:

(def col-space-basis
  (let [sv (vec (:S svd-M))
        U (:U svd-M)
        col-idx (vec (keep-indexed (fn [i s] (when (> s 1e-10) i)) sv))]
    (la/submatrix U :all col-idx)))

;; **Left null space** — the remaining columns of $U$:

(def left-null-basis
  (let [sv (vec (:S svd-M))
        U (:U svd-M)
        null-idx (vec (keep-indexed (fn [i s] (when (< s 1e-10) i)) sv))]
    (la/submatrix U :all null-idx)))

;; **Row space** — the columns of $V$ corresponding to
;; non-zero singular values:

(def row-space-basis
  (let [sv (vec (:S svd-M))
        Vt (:Vt svd-M)
        row-idx (vec (keep-indexed (fn [i s] (when (> s 1e-10) i)) sv))]
    (la/submatrix (la/transpose Vt) :all row-idx)))

;; **Null space** — already computed above as `null-basis`.

;; Let us verify the dimensions match the table:

{:col-space (second (dtype/shape col-space-basis))
 :left-null (second (dtype/shape left-null-basis))
 :row-space (second (dtype/shape row-space-basis))
 :null-space (second (dtype/shape null-basis))}

(kind/test-last
 [(fn [m] (and (= 2 (:col-space m))
               (= 1 (:left-null m))
               (= 2 (:row-space m))
               (= 1 (:null-space m))))])

;; rank = 2, nullity = 1, and the dimensions add up:
;;
;; - In $\mathbb{R}^3$ (output): col space (2) + left null (1) = 3
;; - In $\mathbb{R}^3$ (input): row space (2) + null space (1) = 3

;; The **orthogonality** is what makes this structure beautiful.
;; In the input space, the row space and null space are perpendicular:

(la/mmul (la/transpose row-space-basis) null-basis)

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; And in the output space, the column space and left null space
;; are perpendicular:

(la/mmul (la/transpose col-space-basis) left-null-basis)

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; This means every vector in $\mathbb{R}^3$ splits uniquely
;; into a row-space part and a null-space part (input side),
;; or a column-space part and a left-null-space part (output side).
;; The matrix $M$ maps the row space onto the column space
;; and annihilates the null space.
