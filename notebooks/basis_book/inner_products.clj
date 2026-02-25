;; # Inner Products and Orthogonality
;;
;; So far, vectors can be added and scaled — but we have no notion
;; of **length** or **angle**. The dot product fills this gap,
;; unlocking orthogonality, projections, and the Gram-Schmidt
;; process that leads to the QR decomposition.

(ns basis-book.inner-products
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Arrow diagrams for 2D vectors:
   [scicloj.basis.vis :as vis]))

;; ## Dot product and orthogonality
;;
;; ### The dot product
;;
;; The **dot product** (or **inner product**) of two vectors is:
;;
;; $$\mathbf{u} \cdot \mathbf{v} = \mathbf{u}^T \mathbf{v} = \sum_i u_i v_i$$
;;
;; It measures how much two vectors point in the same direction.

(def a3 (la/column [1 2 3]))
(def b3 (la/column [4 5 6]))

(def dot-ab
  (dfn/sum (dfn/* a3 b3)))

dot-ab

(kind/test-last
 [(fn [d] (< (Math/abs (- d 32.0)) 1e-10))])

;; $1 \cdot 4 + 2 \cdot 5 + 3 \cdot 6 = 32$.

;; ### Length and angle
;;
;; The **length** (or **norm**) of a vector is $\|\mathbf{v}\| = \sqrt{\mathbf{v} \cdot \mathbf{v}}$:

(la/norm a3)

(kind/test-last
 [(fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1e-10))])

;; Two vectors are **orthogonal** (perpendicular) when their dot
;; product is zero:

(dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1])))

(kind/test-last
 [(fn [d] (< (Math/abs d) 1e-10))])

;; A set of vectors is **orthonormal** if they are all unit
;; length and mutually orthogonal. The standard basis is the
;; canonical example.

;; ### Angle between vectors
;;
;; The dot product is related to the angle $\theta$ between
;; two vectors by:
;;
;; $$\cos \theta = \frac{\mathbf{u} \cdot \mathbf{v}}{\|\mathbf{u}\| \, \|\mathbf{v}\|}$$
;;
;; This gives a precise measure of alignment: $\cos \theta = 1$
;; for parallel vectors, $0$ for orthogonal, $-1$ for opposite.

(def p (la/column [1 0]))
(def q (la/column [1 1]))

(def cos-theta
  (/ (dfn/sum (dfn/* p q))
     (* (la/norm p) (la/norm q))))

cos-theta

(kind/test-last
 [(fn [c] (< (Math/abs (- c (/ 1.0 (Math/sqrt 2.0)))) 1e-10))])

;; $\cos \theta = 1/\sqrt{2}$, so $\theta = 45°$:

(Math/toDegrees (Math/acos cos-theta))

(kind/test-last
 [(fn [d] (< (Math/abs (- d 45.0)) 1e-10))])

;; ### Orthogonal projection
;;
;; **Projection** is one of the most useful operations in
;; applied mathematics. Given a subspace $W$ and a vector
;; $\mathbf{b}$, the **orthogonal projection** of $\mathbf{b}$
;; onto $W$ is the closest point in $W$ to $\mathbf{b}$.
;;
;; If $W$ has columns forming a basis for the subspace,
;; the projection matrix is:
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

(def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs))

q1-gs

;; Step 2 — subtract the projection of $\mathbf{b}$ onto $\mathbf{q}_1$:

(def proj-b-on-q1
  (dfn/sum (dfn/* q1-gs b-gs)))

(def orthogonal-part
  (la/sub b-gs (la/scale proj-b-on-q1 q1-gs)))

;; Normalise:

(def q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part))

q2-gs

;; Verify orthonormality — unit length and zero dot product:

{:q1-norm (la/norm q1-gs)
 :q2-norm (la/norm q2-gs)
 :dot (dfn/sum (dfn/* q1-gs q2-gs))}

(kind/test-last
 [(fn [m]
    (and (< (Math/abs (- (:q1-norm m) 1.0)) 1e-10)
         (< (Math/abs (- (:q2-norm m) 1.0)) 1e-10)
         (< (Math/abs (:dot m)) 1e-10)))])

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
