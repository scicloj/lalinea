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
   [scicloj.kindly.v4.kind :as kind]))

;; ### Plotting helper
;;
;; We will draw vectors as arrows in the plane throughout
;; this notebook. This helper renders a list of arrows
;; as an SVG diagram with grid lines and arrowheads.

(def arrow-plot
  (fn [arrows opts]
    (let [width (or (:width opts) 300)
          all-pts (mapcat (fn [{:keys [xy from]}]
                            (let [[fx fy] (or from [0 0])
                                  [tx ty] [(+ fx (xy 0)) (+ fy (xy 1))]]
                              [[fx fy] [tx ty]]))
                          arrows)
          all-xs (map first all-pts)
          all-ys (map second all-pts)
          x-min (apply min all-xs)
          x-max (apply max all-xs)
          y-min (apply min all-ys)
          y-max (apply max all-ys)
          dx (- x-max x-min)
          dy (- y-max y-min)
          span (max dx dy 1.0)
          pad (* 0.3 span)
          vb-x (- x-min pad)
          vb-w (+ dx (* 2 pad))
          vb-h (+ dy (* 2 pad))
          vb-y-top (+ y-max pad)
          height (* width (/ vb-h vb-w))
          px-per-unit (/ width vb-w)
          stroke-w (/ 2.0 px-per-unit)
          head-w (* 10 stroke-w)
          head-h (* 7 stroke-w)
          font-size (* 12 stroke-w)
          grid-lo-x (long (Math/floor (- x-min pad)))
          grid-hi-x (long (Math/ceil (+ x-max pad)))
          grid-lo-y (long (Math/floor (- y-min pad)))
          grid-hi-y (long (Math/ceil (+ y-max pad)))
          grid-lines (concat
                      (for [gx (range grid-lo-x (inc grid-hi-x))]
                        [:line {:x1 gx :y1 (- grid-lo-y) :x2 gx :y2 (- grid-hi-y)
                                :stroke (if (zero? gx) "#999" "#ddd")
                                :stroke-width (if (zero? gx) stroke-w (* 0.5 stroke-w))}])
                      (for [gy (range grid-lo-y (inc grid-hi-y))]
                        [:line {:x1 grid-lo-x :y1 (- gy) :x2 grid-hi-x :y2 (- gy)
                                :stroke (if (zero? gy) "#999" "#ddd")
                                :stroke-width (if (zero? gy) stroke-w (* 0.5 stroke-w))}]))
          defs (into [:defs]
                     (map (fn [{:keys [color]}]
                            [:marker {:id (str "ah-" (subs color 1))
                                      :markerWidth head-w :markerHeight head-h
                                      :refX head-w :refY (/ head-h 2)
                                      :orient "auto" :markerUnits "userSpaceOnUse"}
                             [:polygon {:points (str "0 0, " head-w " " (/ head-h 2) ", 0 " head-h)
                                        :fill color}]])
                          arrows))
          arrow-elts (mapcat
                      (fn [{:keys [label xy color from dashed?]}]
                        (let [[fx fy] (or from [0 0])
                              [tx ty] [(+ fx (xy 0)) (+ fy (xy 1))]
                              adx (- tx fx) ady (- ty fy)
                              len (Math/sqrt (+ (* adx adx) (* ady ady)))
                              nx (if (pos? len) (/ (- ady) len) 0)
                              ny (if (pos? len) (/ adx len) 0)
                              lx (+ fx (* 0.7 adx) (* font-size 0.7 nx))
                              ly (+ fy (* 0.7 ady) (* font-size 0.7 ny))]
                          (cond-> [[:line (cond-> {:x1 fx :y1 (- fy) :x2 tx :y2 (- ty)
                                                   :stroke color :stroke-width (* 1.5 stroke-w)
                                                   :marker-end (str "url(#ah-" (subs color 1) ")")}
                                            dashed? (assoc :stroke-dasharray (str (* 4 stroke-w) " " (* 3 stroke-w))))]]
                            label (conj [:text {:x lx :y (- ly)
                                                :fill color
                                                :font-size font-size
                                                :font-family "sans-serif"
                                                :font-weight "bold"
                                                :text-anchor "middle"
                                                :dominant-baseline "central"}
                                         label]))))
                      arrows)]
      (kind/hiccup
       (into [:svg {:width width :height height
                    :viewBox (str vb-x " " (- vb-y-top) " " vb-w " " vb-h)}]
             (concat [defs]
                     grid-lines
                     arrow-elts))))))

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

(arrow-plot [{:label "a" :xy [2 1] :color "#999999"}
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
