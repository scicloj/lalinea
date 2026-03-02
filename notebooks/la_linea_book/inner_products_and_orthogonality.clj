;; # Inner Products and Orthogonality
;;
;; A vector space gives us addition and scaling, but nothing more.
;; There is no built-in notion of **length**, **angle**, or
;; **perpendicularity** — these require extra structure. An
;; **[inner product](https://en.wikipedia.org/wiki/Inner_product_space)** provides exactly that, and this chapter
;; develops the consequences: orthogonality, projections, and
;; the [Gram-Schmidt process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process) that leads to the [QR decomposition](https://en.wikipedia.org/wiki/QR_decomposition).

(ns la-linea-book.inner-products-and-orthogonality
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Arrow diagrams for 2D vectors:
   [scicloj.la-linea.vis :as vis]
   [clojure.math :as math]))

;; ## Inner products
;;
;; ### The abstract definition
;;
;; An **inner product** on a vector space $V$ is a function
;; $\langle \cdot, \cdot \rangle : V \times V \to \mathbb{R}$
;; satisfying three axioms:
;;
;; 1. **Linearity**: for all vectors $\mathbf{u}, \mathbf{v}, \mathbf{w}$ and scalars $\alpha, \beta$: $\langle \alpha\mathbf{u} + \beta\mathbf{v},\, \mathbf{w} \rangle = \alpha\langle \mathbf{u}, \mathbf{w}\rangle + \beta\langle \mathbf{v}, \mathbf{w}\rangle$
;; 2. **Symmetry**: for all vectors $\mathbf{u}, \mathbf{v}$: $\langle \mathbf{u}, \mathbf{v} \rangle = \langle \mathbf{v}, \mathbf{u} \rangle$
;; 3. **Positive definiteness**: for every vector $\mathbf{v}$: $\langle \mathbf{v}, \mathbf{v} \rangle \geq 0$, with equality only when $\mathbf{v} = \mathbf{0}$
;;
;; Linearity plus symmetry together give **bilinearity** — the
;; function is linear in both arguments. A vector space equipped
;; with an inner product is called an **inner product space**.
;;
;; The inner product is what gives a vector space geometry.
;; Length, angle, and orthogonality are all defined in terms of
;; it — they do not exist in a bare vector space.
;;
;; The concept applies far beyond columns of numbers:
;;
;; - For **continuous functions** on $[a,b]$: $\langle f, g \rangle = \int_a^b f(x)\,g(x)\,dx$
;; - For **matrices**: the Frobenius inner product $\langle A, B \rangle = \text{tr}(A^T B)$
;;
;; Different inner products on the same vector space give different
;; notions of length and angle. The choice of inner product matters.

;; ### The standard inner product on $\mathbb{R}^n$
;;
;; The most familiar inner product on $\mathbb{R}^n$ is the
;; **dot product**:
;;
;; $$\langle \mathbf{u}, \mathbf{v} \rangle = \sum_i u_i v_i$$
;;
;; This is computed by `la/dot`:

(def a3 (la/column [1 2 3]))
(def b3 (la/column [4 5 6]))

(la/dot a3 b3)

;; $1 \cdot 4 + 2 \cdot 5 + 3 \cdot 6 = 32$.

(kind/test-last
 [(fn [d] (< (abs (- d 32.0)) 1e-10))])

;; ### Verifying the axioms
;;
;; Just as we verified the vector space axioms in the
;; Vectors and Spaces chapter, let us check that the dot product satisfies the
;; three inner product axioms.

(def c3 (la/column [7 8 9]))

;; **Axiom 1 — Linearity:**

(la/close-scalar?
 (la/dot (la/add (la/scale a3 2.0) (la/scale b3 3.0)) c3)
 (+ (* 2.0 (la/dot a3 c3))
    (* 3.0 (la/dot b3 c3))))

(kind/test-last [true?])

;; **Axiom 2 — Symmetry:**

(la/close-scalar? (la/dot a3 b3) (la/dot b3 a3))

(kind/test-last [true?])

;; **Axiom 3 — Positive definiteness:**

(> (la/dot a3 a3) 0.0)

(kind/test-last [true?])

(la/close-scalar? (la/dot (la/column [0 0 0]) (la/column [0 0 0])) 0.0)

(kind/test-last [true?])

;; ### A different inner product: weighted dot product
;;
;; The standard dot product is not the only inner product on
;; $\mathbb{R}^n$. Given a positive definite matrix $W$, the
;; **weighted inner product** is:
;;
;; $$\langle \mathbf{u}, \mathbf{v} \rangle_W = \mathbf{u}^T W \mathbf{v}$$
;;
;; This also satisfies the three axioms (because $W$ is symmetric
;; and positive definite), but it gives a different geometry.

(def W-ip (la/matrix [[2 0] [0 1]]))

(def u-ip (la/column [1 1]))

;; Standard length:

(la/norm u-ip)

(kind/test-last
 [(fn [d] (la/close-scalar? d (math/sqrt 2.0)))])

;; Weighted length ($\sqrt{\mathbf{u}^T W \mathbf{u}}$):

(math/sqrt (tensor/mget (la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0))

(kind/test-last
 [(fn [d] (la/close-scalar? d (math/sqrt 3.0)))])

;; The same vector has length $\sqrt{2}$ under the standard
;; inner product but $\sqrt{3}$ under the weighted one. The
;; weight matrix stretches the $x$-direction, making $[1,1]^T$
;; longer in that geometry.

;; ---
;;
;; ## Length, angle, and orthogonality
;;
;; With an inner product in hand, we can define geometric concepts.
;; Everything below follows from the axioms — the specific choice
;; of inner product determines the geometry.
;;
;; ### Length
;;
;; The **length** (or **norm**) of a vector is:
;;
;; $$\|\mathbf{v}\| = \sqrt{\langle \mathbf{v}, \mathbf{v} \rangle}$$

(la/norm a3)

(kind/test-last
 [(fn [d] (< (abs (- d (math/sqrt 14.0))) 1e-10))])

;; ### Orthogonality
;;
;; Two vectors are **orthogonal** when their inner product is zero:
;;
;; $$\langle \mathbf{u}, \mathbf{v} \rangle = 0$$

(la/dot (la/column [1 0]) (la/column [0 1]))

(kind/test-last
 [(fn [d] (< (abs d) 1e-10))])

;; A set of vectors is **orthonormal** if they are all unit
;; length and mutually orthogonal. The standard basis is the
;; canonical example.
;;
;; Recall from the previous chapter that the four fundamental
;; subspaces of a matrix are complementary. With the standard
;; inner product, they are in fact **orthogonal complements** —
;; every vector in one subspace is orthogonal to every vector
;; in the other.

;; ### Angle between vectors
;;
;; The inner product determines the angle $\theta$ between
;; two vectors:
;;
;; $$\cos \theta = \frac{\langle \mathbf{u}, \mathbf{v} \rangle}{\|\mathbf{u}\| \, \|\mathbf{v}\|}$$
;;
;; $\cos \theta = 1$ for parallel, $0$ for orthogonal, $-1$ for opposite.

(def p (la/column [1 0]))
(def q (la/column [1 1]))

(def cos-theta
  (/ (la/dot p q)
     (* (la/norm p) (la/norm q))))

cos-theta

(kind/test-last
 [(fn [c] (< (abs (- c (/ 1.0 (math/sqrt 2.0)))) 1e-10))])

;; $\cos \theta = 1/\sqrt{2}$, so $\theta = 45°$:

(math/to-degrees (math/acos cos-theta))

(kind/test-last
 [(fn [d] (< (abs (- d 45.0)) 1e-10))])

;; ### [Orthogonal projection](https://en.wikipedia.org/wiki/Projection_(linear_algebra))
;;
;; Given a subspace and a vector
;; $\mathbf{b}$, the **orthogonal projection** of $\mathbf{b}$
;; onto that subspace is the closest point in it to $\mathbf{b}$.
;;
;; We represent a subspace by a matrix $W$ whose columns form
;; a basis for it (recall that the column space of a matrix is
;; the span of its columns). The projection matrix is then:
;;
;; $$P = W (W^T W)^{-1} W^T$$
;;
;; In two dimensions, projecting $\mathbf{b}$ onto a line
;; spanned by $\mathbf{a}$ is easy to visualise. The projection
;; sits on the line, and the residual is perpendicular:

(vis/arrow-plot [{:label "a" :xy [2 1] :color "#999999"}
                 {:label "b" :xy [1 3] :color "#2266cc"}
                 {:label "proj" :xy [2 1] :color "#228833"}
                 {:label "resid" :xy [-1 2] :color "#cc4422" :from [2 1] :dashed? true}]
                {})

;; The green arrow (projection) lies on the direction of
;; $\mathbf{a}$, and the dashed red residual is perpendicular
;; to it. The general formula for higher dimensions:

(def W-proj
  (la/matrix [[1 0]
              [0 1]
              [1 1]]))

;; This defines a 2D subspace of $\mathbb{R}^3$ (the span
;; of the two columns).

(def P-proj
  (la/mmul W-proj (la/mmul (la/invert (la/mmul (la/transpose W-proj) W-proj))
                           (la/transpose W-proj))))

P-proj

;; A projection matrix is **idempotent**: applying it twice
;; is the same as applying it once ($P^2 = P$). Once you
;; are on the subspace, projecting again does nothing.

(la/close? (la/mmul P-proj P-proj) P-proj)

(kind/test-last [true?])

;; Project a point:

(def point3d (la/column [1 2 3]))

(def projected-pt (la/mmul P-proj point3d))

projected-pt

;; The **residual** $\mathbf{b} - P\mathbf{b}$ is orthogonal
;; to the subspace — that is what makes the projection the
;; closest point:

(def resid (la/sub point3d projected-pt))

resid

(la/mmul (la/transpose W-proj) resid)

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; This principle — that the best approximation has an
;; orthogonal residual — is the geometric core of
;; least squares regression.

;; ---
;;
;; ## Gram-Schmidt and QR
;;
;; ### Gram-Schmidt orthogonalisation
;;
;; Given any set of linearly independent vectors, the
;; **Gram-Schmidt process** produces an orthonormal set
;; spanning the same subspace.
;;
;; The idea is simple: take each vector, subtract its
;; projections onto all previous vectors (to make it
;; orthogonal), then normalise.
;;
;; Start with two non-orthogonal vectors in $\mathbb{R}^3$:

(def a-gs (la/column [1 1 0]))
(def b-gs (la/column [1 0 1]))

;; Step 1 — normalise $\mathbf{a}$:

(def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs))))

q1-gs

;; Step 2 — subtract the projection of $\mathbf{b}$ onto $\mathbf{q}_1$:

(def proj-b-on-q1
  (la/dot q1-gs b-gs))

(def orthogonal-part
  (la/sub b-gs (la/scale q1-gs proj-b-on-q1)))

;; Normalise:

(def q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part))))

q2-gs

;; Verify orthonormality — unit length and zero dot product:

{:q1-norm (la/norm q1-gs)
 :q2-norm (la/norm q2-gs)
 :dot (la/dot q1-gs q2-gs)}

(kind/test-last
 [(fn [m]
    (and (< (abs (- (:q1-norm m) 1.0)) 1e-10)
         (< (abs (- (:q2-norm m) 1.0)) 1e-10)
         (< (abs (:dot m)) 1e-10)))])

;; The first two components of each vector, showing the
;; original pair (gray/blue) and the orthogonalised pair
;; (green/red):

(vis/arrow-plot [{:label "a" :xy [1 1] :color "#999999"}
                 {:label "b" :xy [1 0] :color "#2266cc"}
                 {:label "q1" :xy [(/ 1 (math/sqrt 2)) (/ 1 (math/sqrt 2))] :color "#228833"}
                 {:label "q2" :xy [(/ 1 (math/sqrt 6)) (/ -1 (math/sqrt 6))] :color "#cc4422"}]
                {})

;; ### Connection to QR decomposition
;;
;; Gram-Schmidt on all columns of a matrix $A$ produces $A = QR$:
;;
;; - $Q$: orthonormal columns (the Gram-Schmidt outputs)
;; - $R$: upper triangular (the projection coefficients)
;;
;; This decomposition is fundamental for solving least squares
;; problems and computing eigenvalues.

(def A-qr (la/matrix [[1 1]
                      [1 0]
                      [0 1]]))

(def qr-result (la/qr A-qr))

;; Extract the thin Q (first $n$ columns):

(def ncols-qr (second (dtype/shape A-qr)))
(def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr)))
(def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all))

;; $Q$ (orthonormal columns):

Q-thin

;; $R$ (upper triangular):

R-thin

;; Verify $Q^T Q = I$ (orthonormal columns):

(la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2)))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; Verify $QR = A$:

(la/norm (la/sub (la/mmul Q-thin R-thin) A-qr))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; In practice, the manual Gram-Schmidt process we showed above
;; can lose orthogonality due to floating-point rounding —
;; especially for nearly dependent vectors. EJML's QR (via
;; Householder reflections) is numerically stable and should
;; always be preferred over hand-coded Gram-Schmidt.
;; The manual version here is for building intuition.
