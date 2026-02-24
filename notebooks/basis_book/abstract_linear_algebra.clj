;; # Abstract Linear Algebra
;;
;; This notebook develops linear algebra from the ground up.
;; No prior mathematical background is assumed beyond basic
;; arithmetic — we build everything from first principles,
;; using computation to make each idea tangible.
;;
;; The central insight of linear algebra is that a huge variety
;; of things — arrows in space, columns of data, pixel intensities,
;; audio signals — all obey the same rules of addition and scaling.
;; By studying these rules abstractly, we discover theorems that
;; apply to all of them at once.

(ns basis-book.abstract-linear-algebra
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

;; ---
;;
;; ## Part I: Vectors
;;
;; ### What is a vector?
;;
;; In the most concrete sense, a vector is an ordered list of
;; numbers. A vector in $\mathbb{R}^2$ (the plane) has two entries;
;; a vector in $\mathbb{R}^3$ (space) has three.

(def u (la/column [3 1]))
(def v (la/column [1 2]))

;; We can think of these as arrows from the origin to a point,
;; or as displacements — "go 3 units right and 1 unit up."

(arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
             {:label "v" :xy [1 2] :color "#cc4422"}]
            {:width 300})

;; ### Vector addition
;;
;; Adding two vectors means adding their entries position by position.
;; Geometrically, it is "tip to tail" — walk along $\mathbf{u}$,
;; then walk along $\mathbf{v}$.

(la/add u v)

(kind/test-last
 [(fn [r] (and (= 4.0 (tensor/mget r 0 0))
               (= 3.0 (tensor/mget r 1 0))))])

(arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
             {:label "v" :xy [1 2] :color "#cc4422" :from [3 1]}
             {:label "u+v" :xy [4 3] :color "#228833" :dashed? true}]
            {:width 300})

;; ### Scalar multiplication
;;
;; Multiplying a vector by a number (a **scalar**) scales every entry.
;; Geometrically, it stretches (or shrinks, or reverses) the arrow.

(la/scale 2.0 u)

(kind/test-last
 [(fn [r] (and (= 6.0 (tensor/mget r 0 0))
               (= 2.0 (tensor/mget r 1 0))))])

(arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
             {:label "2u" :xy [6 2] :color "#8844cc"}]
            {:width 300})

;; Scaling by $-1$ reverses the direction:

(la/scale -1.0 u)

(kind/test-last
 [(fn [r] (and (= -3.0 (tensor/mget r 0 0))
               (= -1.0 (tensor/mget r 1 0))))])

(arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
             {:label "\u2212u" :xy [-3 -1] :color "#cc4422"}]
            {:width 300})

;; ### The vector space axioms
;;
;; These two operations — addition and scalar multiplication —
;; satisfy a list of rules that feel obvious for columns of
;; numbers, but turn out to define a powerful abstraction.
;; Any collection of objects obeying these rules is called
;; a **vector space**.
;;
;; The axioms are:
;;
;; **Addition axioms:**
;;
;; 1. **Commutativity**: $\mathbf{u} + \mathbf{v} = \mathbf{v} + \mathbf{u}$
;; 2. **Associativity**: $(\mathbf{u} + \mathbf{v}) + \mathbf{w} = \mathbf{u} + (\mathbf{v} + \mathbf{w})$
;; 3. **Zero vector**: there exists $\mathbf{0}$ such that $\mathbf{u} + \mathbf{0} = \mathbf{u}$
;; 4. **Additive inverse**: for every $\mathbf{u}$, there exists $-\mathbf{u}$ with $\mathbf{u} + (-\mathbf{u}) = \mathbf{0}$
;;
;; **Scalar multiplication axioms:**
;;
;; 5. **Compatibility**: $\alpha(\beta \mathbf{u}) = (\alpha \beta) \mathbf{u}$
;; 6. **Identity**: $1 \cdot \mathbf{u} = \mathbf{u}$
;; 7. **Distributivity over vectors**: $\alpha(\mathbf{u} + \mathbf{v}) = \alpha\mathbf{u} + \alpha\mathbf{v}$
;; 8. **Distributivity over scalars**: $(\alpha + \beta)\mathbf{u} = \alpha\mathbf{u} + \beta\mathbf{u}$
;;
;; Let us verify each one on our concrete vectors
;; $\mathbf{u} = [3,1]^T$, $\mathbf{v} = [1,2]^T$ in the plane.
;; These are not proofs — the axioms hold by the definition of
;; entry-wise addition and scaling — but checking them builds
;; intuition for what each rule says.

(def w-ax (la/column [-1 4]))
(def zero2 (la/column [0 0]))

;; A helper to check that two vectors are approximately equal:

(def close?
  (fn [a b] (< (la/norm (la/sub a b)) 1e-10)))

;; **Axiom 1 — Commutativity:**

(close? (la/add u v) (la/add v u))

(kind/test-last [true?])

;; **Axiom 2 — Associativity:**

(close? (la/add (la/add u v) w-ax)
        (la/add u (la/add v w-ax)))

(kind/test-last [true?])

;; **Axiom 3 — Zero vector:**

(close? (la/add u zero2) u)

(kind/test-last [true?])

;; **Axiom 4 — Additive inverse:**

(close? (la/add u (la/scale -1.0 u)) zero2)

(kind/test-last [true?])

;; **Axiom 5 — Scalar compatibility:**

(close? (la/scale 2.0 (la/scale 3.0 u))
        (la/scale 6.0 u))

(kind/test-last [true?])

;; **Axiom 6 — Scalar identity:**

(close? (la/scale 1.0 u) u)

(kind/test-last [true?])

;; **Axiom 7 — Distributivity over vectors:**

(close? (la/scale 5.0 (la/add u v))
        (la/add (la/scale 5.0 u) (la/scale 5.0 v)))

(kind/test-last [true?])

;; **Axiom 8 — Distributivity over scalars:**

(close? (la/scale (+ 2.0 3.0) u)
        (la/add (la/scale 2.0 u) (la/scale 3.0 u)))

(kind/test-last [true?])

;; These eight rules are all we need. Every theorem in linear
;; algebra follows from them. The rules feel unsurprising for
;; number columns, but the same structure appears in:
;;
;; - Polynomials (add coefficients, scale by constants)
;; - Functions $f: \mathbb{R} \to \mathbb{R}$ (pointwise addition and scaling)
;; - Matrices (entry-by-entry addition and scaling)
;; - Solutions to linear differential equations
;;
;; Working abstractly means proving something once and having it
;; apply to all of these simultaneously.

;; ---
;;
;; ## Part II: Linear combinations and span
;;
;; ### Linear combinations
;;
;; A **linear combination** of vectors $\mathbf{v}_1, \ldots, \mathbf{v}_k$
;; is any expression $\alpha_1 \mathbf{v}_1 + \cdots + \alpha_k \mathbf{v}_k$
;; where the $\alpha_i$ are scalars. This is the most fundamental
;; operation in linear algebra — everything else builds on it.

(la/add (la/scale 2.0 u) (la/scale -1.0 v))

(kind/test-last
 [(fn [r] (and (= 5.0 (tensor/mget r 0 0))
               (= 0.0 (tensor/mget r 1 0))))])

;; $2 \cdot [3,1]^T + (-1) \cdot [1,2]^T = [5,0]^T$.
;; We combined $\mathbf{u}$ and $\mathbf{v}$ to reach a new point.

;; ### Span
;;
;; The **span** of a set of vectors is the collection of all
;; their linear combinations — every point you can reach by
;; adding and scaling them.
;;
;; If two vectors in $\mathbb{R}^2$ point in different directions,
;; their span is the entire plane. Let us visualise this by
;; plotting many combinations:

(let [params (for [a (range -2.0 2.1 0.5)
                   b (range -2.0 2.1 0.5)]
               {:a a :b b})
      xs (mapv (fn [{:keys [a b]}] (+ (* a 3.0) (* b 1.0))) params)
      ys (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/base {:=x :x :=y :y})
      (plotly/layer-point {:=mark-size 6})
      plotly/plot))

;; The points fill a grid that covers the whole plane.
;; Every point in $\mathbb{R}^2$ can be expressed as
;; $\alpha [3,1]^T + \beta [1,2]^T$ for some $\alpha, \beta$.

;; What if the vectors point in the **same** direction?

(let [params (for [a (range -2.0 2.1 0.5)
                   b (range -2.0 2.1 0.5)]
               {:a a :b b})
      xs (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)
      ys (mapv (fn [{:keys [a b]}] (+ (* a 2.0) (* b 4.0))) params)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/base {:=x :x :=y :y})
      (plotly/layer-point {:=mark-size 6})
      plotly/plot))

;; Now the points all lie on a single line through the origin.
;; Two parallel vectors can only reach points along that line —
;; their span is one-dimensional.

;; ---
;;
;; ## Part III: Linear independence
;;
;; ### The key question
;;
;; Given a set of vectors, do they each contribute something
;; genuinely new? Or could some be built from the others?
;;
;; Vectors $\mathbf{v}_1, \ldots, \mathbf{v}_k$ are **linearly
;; independent** if none of them is a linear combination of the rest.
;; The formal definition: the only way to get zero is with all
;; coefficients zero:
;;
;; $$\alpha_1 \mathbf{v}_1 + \cdots + \alpha_k \mathbf{v}_k = \mathbf{0}
;; \quad \Longrightarrow \quad \alpha_1 = \cdots = \alpha_k = 0$$
;;
;; For two vectors in $\mathbb{R}^2$, this is the same as
;; "not parallel." The **determinant** of the matrix with these
;; vectors as columns is non-zero precisely when they are independent.

;; Independent — two non-parallel directions:

(la/det (la/matrix [[3 1]
                    [1 2]]))

(kind/test-last
 [(fn [d] (> (Math/abs d) 1e-10))])

;; Dependent — second column is $2 \times$ the first:

(la/det (la/matrix [[3 6]
                    [1 2]]))

(kind/test-last
 [(fn [d] (< (Math/abs d) 1e-10))])

;; In $\mathbb{R}^3$, three vectors are independent when they
;; do not all lie in the same plane:

(la/det (la/matrix [[1 0 0]
                    [0 1 0]
                    [0 0 1]]))

(kind/test-last
 [(fn [d] (< (Math/abs (- d 1.0)) 1e-10))])

;; But if the third is the sum of the first two, they lie in a
;; plane — the determinant is zero:

(la/det (la/matrix [[1 0 1]
                    [0 1 1]
                    [0 0 0]]))

(kind/test-last
 [(fn [d] (< (Math/abs d) 1e-10))])

;; ---
;;
;; ## Part IV: Basis and dimension
;;
;; ### Basis
;;
;; A **basis** is a set of vectors that is both:
;;
;; - **Spanning**: their linear combinations reach every point in the space
;; - **Linearly independent**: none of them is redundant
;;
;; A basis is a "minimal coordinate system" — it has exactly
;; the right number of vectors. Not too many (which would mean
;; redundancy), not too few (which would leave gaps).
;;
;; The **standard basis** for $\mathbb{R}^3$ has three vectors:

(def e1 (la/column [1 0 0]))
(def e2 (la/column [0 1 0]))
(def e3 (la/column [0 0 1]))

;; ### Coordinates
;;
;; Once you fix a basis, every vector has a unique
;; representation as a linear combination of basis vectors.
;; The coefficients are the **coordinates**.
;;
;; For the standard basis, coordinates are just the entries:

(def w (la/column [5 -3 7]))

;; $\mathbf{w} = 5\mathbf{e}_1 + (-3)\mathbf{e}_2 + 7\mathbf{e}_3$:

(close? w
        (la/add (la/scale 5.0 e1)
                (la/add (la/scale -3.0 e2)
                        (la/scale 7.0 e3))))

(kind/test-last [true?])

;; But $\{\mathbf{e}_1, \mathbf{e}_2, \mathbf{e}_3\}$ is not the
;; only basis. Any three linearly independent vectors in
;; $\mathbb{R}^3$ form a basis — and the coordinates change.

;; ### Dimension
;;
;; A remarkable fact: **every basis for a given vector space has
;; the same number of vectors**. This number is the **dimension**.
;;
;; - $\mathbb{R}^2$ has dimension 2 (every basis has 2 vectors)
;; - $\mathbb{R}^3$ has dimension 3
;; - The set of $2 \times 3$ matrices has dimension 6 (it takes 6 numbers to specify one)
;;
;; If you try to fit 4 vectors into $\mathbb{R}^3$, at least
;; one must be a linear combination of the others — you cannot
;; have more independent vectors than the dimension.

;; ---
;;
;; ## Part V: Linear maps
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

;; Let us verify the linearity properties.
;;
;; **Additivity**: $R(\mathbf{u} + \mathbf{v}) = R(\mathbf{u}) + R(\mathbf{v})$:

(close? (la/mmul R90 (la/add u v))
        (la/add (la/mmul R90 u) (la/mmul R90 v)))

(kind/test-last [true?])

;; **Homogeneity**: $R(3\mathbf{u}) = 3 R(\mathbf{u})$:

(close? (la/mmul R90 (la/scale 3.0 u))
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

;; The result depends on the order — just like "put on socks,
;; then shoes" is different from "put on shoes, then socks."

;; ---
;;
;; ## Part VI: Subspaces
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
;; ## Part VII: Rank and the rank-nullity theorem
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
;; ## Part VIII: The four fundamental subspaces
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
;; For our matrix $M$ ($3 \times 3$, rank 2):

;; Row space vectors (from the SVD):

(def row-space-basis
  (let [sv (vec (:S svd-M))
        Vt (:Vt svd-M)
        row-idx (vec (keep-indexed (fn [i s] (when (> s 1e-10) i)) sv))]
    (la/submatrix (la/transpose Vt) :all row-idx)))

;; The null space and row space are orthogonal:

(la/mmul (la/transpose row-space-basis) null-basis)

(kind/test-last
 [(fn [r] (< (la/norm r) 1e-10))])

;; This means every vector in $\mathbb{R}^3$ splits uniquely
;; into a row-space part and a null-space part.

;; ---
;;
;; ## Part IX: Dot product and orthogonality
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

(close? (la/mmul P-proj P-proj) P-proj)

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
;; ## Part X: Gram-Schmidt and QR
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

;; ---
;;
;; ## Part XI: Eigenvalues and eigenvectors
;;
;; ### The idea
;;
;; Most vectors change direction when multiplied by a matrix.
;; But some special vectors only get scaled — they keep pointing
;; the same way (or flip to the opposite direction). These are
;; the **eigenvectors**.
;;
;; Formally, $\mathbf{v}$ is an eigenvector of $A$ if:
;;
;; $$A\mathbf{v} = \lambda \mathbf{v}$$
;;
;; for some scalar $\lambda$ (the **eigenvalue**). The map $A$
;; acts on $\mathbf{v}$ by merely scaling it by $\lambda$.

;; Consider:

(def A-eig
  (la/matrix [[4 1 2]
              [0 3 1]
              [0 0 2]]))

;; This upper-triangular matrix has eigenvalues on the diagonal:
;; 4, 3, and 2.

(def eig-result (la/eigen A-eig))

(sort (mapv first (:eigenvalues eig-result)))

(kind/test-last
 [(fn [v]
    (and (< (Math/abs (- (nth v 0) 2.0)) 1e-10)
         (< (Math/abs (- (nth v 1) 3.0)) 1e-10)
         (< (Math/abs (- (nth v 2) 4.0)) 1e-10)))])

;; ### Verifying the eigenvalue equation
;;
;; For each eigenpair $(\lambda, \mathbf{v})$, the residual
;; $A\mathbf{v} - \lambda\mathbf{v}$ should be zero:

(every? (fn [i]
          (let [lam (first (nth (:eigenvalues eig-result) i))
                ev (nth (:eigenvectors eig-result) i)]
            (< (la/norm (la/sub (la/mmul A-eig ev)
                                (la/scale lam ev)))
               1e-10)))
        (range 3))

(kind/test-last [true?])

;; ### Why eigenvalues matter
;;
;; Eigenvalues encode essential properties of a matrix:
;;
;; - **Trace** = sum of eigenvalues
;; - **Determinant** = product of eigenvalues
;; - **Invertibility**: a matrix is invertible iff no eigenvalue is zero
;; - **Stability** (in differential equations): negative eigenvalues mean decay
;;
;; Let us verify the trace and determinant connections:

(def eig-reals (mapv first (:eigenvalues eig-result)))

(< (Math/abs (- (la/trace A-eig) (reduce + eig-reals))) 1e-10)

(kind/test-last [true?])

(< (Math/abs (- (la/det A-eig) (reduce * eig-reals))) 1e-10)

(kind/test-last [true?])

;; ---
;;
;; ## Part XII: Change of basis and diagonalisation
;;
;; ### The idea
;;
;; A matrix represents a linear map *with respect to a chosen basis*.
;; Choose a different basis and you get a different matrix for
;; the **same map**. This is a **change of basis**.
;;
;; If $P$ is the matrix whose columns are the new basis vectors,
;; then the matrix of the map in the new basis is:
;;
;; $$B = P^{-1} A P$$
;;
;; This is called a **similarity transform**. Two matrices
;; related this way represent the same linear map — they
;; share eigenvalues, trace, determinant, and rank.

;; ### Diagonalisation
;;
;; The most illuminating basis change uses the eigenvectors.
;; If $A$ has $n$ linearly independent eigenvectors, we can
;; diagonalise it: $A = P D P^{-1}$ where $D$ is diagonal.
;;
;; Consider:

(def A-diag
  (la/matrix [[2 1]
              [0 3]]))

;; Eigenvalues are 2 and 3. Build $P$ from the eigenvectors:

(def eig-diag (la/eigen A-diag))

(def P-diag
  (let [evecs (:eigenvectors eig-diag)
        sorted-idx (sort-by (fn [i] (first (nth (:eigenvalues eig-diag) i)))
                            (range 2))]
    (la/matrix (mapv (fn [j]
                       (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
                     (range 2)))))

;; The columns of $P$ are the eigenvectors. Transpose to get
;; them as columns:

(def P-cols (la/transpose P-diag))

;; The similarity transform yields a diagonal matrix:

(def D-result
  (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols)))

D-result

(kind/test-last
 [(fn [d]
    (and (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1e-10)
         (< (Math/abs (tensor/mget d 0 1)) 1e-10)
         (< (Math/abs (tensor/mget d 1 0)) 1e-10)
         (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1e-10)))])

;; In the eigenvector basis, the map is just scaling along
;; each axis. This is the simplest possible form.

;; ### Powers of a matrix
;;
;; Diagonalisation makes it easy to compute powers:
;; $A^k = P D^k P^{-1}$, and $D^k$ just raises each
;; diagonal entry to the $k$th power.

(def A-diag-sq
  (la/mmul A-diag A-diag))

(def A-diag-sq-via-eigen
  (let [Pinv (la/invert P-cols)
        D2 (la/diag (dtype/make-reader :float64 2
                                       (let [lam (tensor/mget D-result idx idx)]
                                         (* lam lam))))]
    (la/mmul P-cols (la/mmul D2 Pinv))))

(close? A-diag-sq A-diag-sq-via-eigen)

(kind/test-last [true?])

;; ---
;;
;; ## Part XIII: Symmetric matrices and the spectral theorem
;;
;; ### Symmetric matrices
;;
;; A matrix $A$ is **symmetric** if $A = A^T$. Symmetric matrices
;; are everywhere in applications:
;;
;; - Covariance matrices in statistics
;; - Graph Laplacians in network analysis
;; - Hessians of scalar-valued functions in optimization
;;
;; They have three remarkable properties that general matrices lack:
;;
;; 1. **All eigenvalues are real** (no imaginary parts)
;; 2. **Eigenvectors for distinct eigenvalues are orthogonal**
;; 3. **The matrix can always be diagonalised** (by an orthogonal matrix)

(def S-sym
  (la/matrix [[4 2 0]
              [2 5 1]
              [0 1 3]]))

;; Verify symmetry:

(close? S-sym (la/transpose S-sym))

(kind/test-last [true?])

(def eig-S (la/eigen S-sym))

;; All eigenvalues are real (imaginary parts zero):

(every? (fn [[_ im]] (< (Math/abs im) 1e-10))
        (:eigenvalues eig-S))

(kind/test-last [true?])

;; Eigenvectors are orthonormal. Build a matrix from them
;; and check $Q^T Q = I$:

(def Q-eig
  (let [evecs (:eigenvectors eig-S)]
    (la/matrix (mapv (fn [i] (vec (dtype/->reader (nth evecs i))))
                     (range 3)))))

(def QtQ (la/mmul Q-eig (la/transpose Q-eig)))

(la/norm (la/sub QtQ (la/eye 3)))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; ### The spectral theorem
;;
;; Every real symmetric matrix $A$ can be written:
;;
;; $$A = Q \Lambda Q^T$$
;;
;; where $Q$ is orthogonal (its columns are the eigenvectors)
;; and $\Lambda$ is diagonal (the eigenvalues on the diagonal).
;;
;; This is the most important decomposition in applied linear
;; algebra. It says every symmetric matrix is just scaling
;; along orthogonal axes.
;;
;; The word "spectrum" for the eigenvalues comes from this
;; theorem — the eigenvalues are the "spectral lines" of the matrix.

;; ---
;;
;; ## Part XIV: The SVD
;;
;; ### Beyond square matrices
;;
;; Eigendecomposition only applies to square matrices. The
;; **Singular Value Decomposition** (SVD) works for **any**
;; $m \times n$ matrix:
;;
;; $$A = U \Sigma V^T$$
;;
;; - $U$ ($m \times m$, orthogonal): left singular vectors
;; - $\Sigma$ ($m \times n$, diagonal): singular values $\sigma_i \geq 0$
;; - $V^T$ ($n \times n$, orthogonal): right singular vectors
;;
;; The singular values are always non-negative and arranged
;; in decreasing order. They measure how much the matrix
;; stretches each direction.

(def A-svd
  (la/matrix [[1 0 1]
              [0 1 1]]))

(def svd-A (la/svd A-svd))

(vec (:S svd-A))

(kind/test-last
 [(fn [s] (and (= 2 (count s))
               (every? pos? s)))])

;; ### What the SVD reveals
;;
;; - **Rank** = number of non-zero singular values
;; - **Condition number** $\kappa = \sigma_1 / \sigma_r$ — how
;;   sensitive the matrix is to perturbations
;; - **Column space** = span of the first $r$ columns of $U$
;; - **Row space** = span of the first $r$ rows of $V^T$
;; - **Null space** = span of the last $n - r$ rows of $V^T$
;;
;; The SVD unifies all four fundamental subspaces in one
;; factorisation.

;; ### Low-rank approximation
;;
;; The **Eckart-Young theorem** says the best rank-$k$
;; approximation to $A$ (minimising the Frobenius norm of
;; the error) is obtained by keeping only the $k$ largest
;; singular values and zeroing the rest.
;;
;; This is the mathematical foundation of dimensionality
;; reduction, image compression, and latent semantic analysis.

(def A-lr
  (la/matrix [[3 2 2]
              [2 3 -2]]))

(def svd-lr (la/svd A-lr))

(def sigmas (vec (:S svd-lr)))

sigmas

;; Rank-1 approximation — keep only $\sigma_1$:

(def A-rank1
  (la/scale (first sigmas)
            (la/mmul (la/submatrix (:U svd-lr) :all [0])
                     (la/submatrix (:Vt svd-lr) [0] :all))))

;; The approximation error equals $\sigma_2$:

(def approx-err (la/norm (la/sub A-lr A-rank1)))

(< (Math/abs (- approx-err (second sigmas))) 1e-10)

(kind/test-last [true?])

;; ---
;;
;; ## Part XV: Positive definite matrices
;;
;; ### Definition
;;
;; A symmetric matrix $M$ is **positive definite** (PD) if
;; $\mathbf{x}^T M \mathbf{x} > 0$ for every non-zero $\mathbf{x}$.
;;
;; Think of it as a bowl shape — the quadratic form always
;; curves upward. Equivalent conditions:
;;
;; - All eigenvalues are positive
;; - All pivots are positive
;; - $M = R^T R$ for some invertible $R$
;;
;; Positive definite matrices arise as:
;;
;; - **Covariance matrices** in statistics
;; - **Gram matrices** $A^T A$ (always positive semi-definite)
;; - **Hessians** of strictly convex functions

;; $A^T A$ is always positive semi-definite:

(def ATA (la/mmul (la/transpose A-svd) A-svd))

(every? (fn [[re _]] (>= re -1e-10))
        (:eigenvalues (la/eigen ATA)))

(kind/test-last [true?])

;; ### Cholesky decomposition
;;
;; Positive definite matrices have a special factorisation:
;; $M = L L^T$ where $L$ is lower triangular. This is the
;; **Cholesky decomposition** — like a "square root" for matrices.
;;
;; It is about twice as fast as general LU decomposition
;; and is the standard method for solving PD systems.

(def spd-mat
  (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3)))

(def chol-L (la/cholesky spd-mat))

;; Verify $L L^T = M$:

(la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; Non-positive-definite matrices have no Cholesky factor:

(la/cholesky (la/matrix [[1 2] [2 1]]))

(kind/test-last [nil?])

;; (The eigenvalues of that matrix are 3 and $-1$ — the
;; negative eigenvalue prevents a Cholesky factorisation.)

;; ---
;;
;; ## Part XVI: Putting it all together
;;
;; Let us explore a single matrix through all the lenses
;; we have developed.

(def A-final
  (la/matrix [[2 1 0]
              [1 3 1]
              [0 1 2]]))

;; This matrix is:
;;
;; - **Symmetric**: $A = A^T$
;; - **Positive definite**: all eigenvalues > 0

(close? A-final (la/transpose A-final))

(kind/test-last [true?])

;; ### Eigenvalues and eigenvectors

(def eig-final (la/eigen A-final))

(def final-eigenvalues
  (sort (mapv first (:eigenvalues eig-final))))

final-eigenvalues

(kind/test-last
 [(fn [v] (and (= 3 (count v))
               (every? pos? v)))])

;; All eigenvalues are positive — the matrix is positive definite.

;; ### Trace = sum of eigenvalues

(< (Math/abs (- (la/trace A-final)
                (reduce + final-eigenvalues)))
   1e-10)

(kind/test-last [true?])

;; ### Determinant = product of eigenvalues

(< (Math/abs (- (la/det A-final)
                (reduce * final-eigenvalues)))
   1e-10)

(kind/test-last [true?])

;; ### SVD — singular values = eigenvalues (since SPD)

(def final-svd (la/svd A-final))

(< (dfn/reduce-max
    (dfn/abs (dfn/- (double-array (sort (vec (:S final-svd))))
                    (double-array final-eigenvalues))))
   1e-10)

(kind/test-last [true?])

;; ### Full rank — invertible

(count (filter #(> % 1e-10) (vec (:S final-svd))))

(kind/test-last
 [(fn [r] (= r 3))])

(def A-inv (la/invert A-final))

(close? (la/mmul A-final A-inv) (la/eye 3))

(kind/test-last [true?])

;; ### Cholesky — since positive definite

(def chol-final (la/cholesky A-final))

(close? (la/mmul chol-final (la/transpose chol-final)) A-final)

(kind/test-last [true?])

;; Every concept in this notebook connects to the others.
;; The eigenvalues control the trace, determinant, singular
;; values, and positive definiteness. The rank controls
;; invertibility. The symmetry guarantees orthogonal
;; eigenvectors and the spectral theorem. The positive
;; definiteness guarantees Cholesky. It is all one coherent
;; theory.

;; ---
;;
;; ## Summary
;;
;; This notebook developed abstract linear algebra from
;; first principles:
;;
;; **Part I–IV — The language:**
;;
;; - **Vectors**: objects that can be added and scaled
;; - **Vector space axioms**: the 8 rules everything rests on
;; - **Linear combinations and span**: building blocks of subspaces
;; - **Linear independence**: when vectors carry new information
;; - **Basis and dimension**: minimal coordinate systems
;;
;; **Part V–VIII — Linear maps and subspaces:**
;;
;; - **Linear maps**: matrices as transformations (rotation, stretching, projection, shear)
;; - **Column space and null space**: what a map reaches and what it kills
;; - **Rank-nullity theorem**: dimensions used + dimensions lost = total
;; - **Four fundamental subspaces**: the complete geometric picture
;;
;; **Part IX–X — Inner product geometry:**
;;
;; - **Dot product**: measuring alignment, length, and angles
;; - **Orthogonality and projection**: closest points in subspaces
;; - **Gram-Schmidt and QR**: constructing orthonormal bases
;;
;; **Part XI–XVI — Decomposition theorems:**
;;
;; - **Eigenvalues and eigenvectors**: the natural axes of a linear map
;; - **Change of basis and diagonalisation**: simplifying a map by choosing the right coordinates
;; - **Spectral theorem**: symmetric matrices have orthogonal eigenbases
;; - **SVD**: the universal decomposition for any matrix, low-rank approximation
;; - **Positive definite matrices and Cholesky**: structure in quadratic forms
;;
;; Together, these ideas form the foundation for nearly all
;; of computational science — from solving systems of equations
;; to machine learning, signal processing, and quantum mechanics.
