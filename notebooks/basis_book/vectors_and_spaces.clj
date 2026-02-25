;; # Vectors and Spaces
;;
;; This chapter develops the foundations of linear algebra from
;; first principles. We start with vectors as ordered lists of
;; numbers, define addition and scaling, and then build up to
;; the key concepts of linear independence, basis, and dimension.

(ns basis-book.vectors-and-spaces
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
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

;; ## Vectors
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

;; **Axiom 1 — Commutativity:**

(la/close? (la/add u v) (la/add v u))

(kind/test-last [true?])

;; **Axiom 2 — Associativity:**

(la/close? (la/add (la/add u v) w-ax)
           (la/add u (la/add v w-ax)))

(kind/test-last [true?])

;; **Axiom 3 — Zero vector:**

(la/close? (la/add u zero2) u)

(kind/test-last [true?])

;; **Axiom 4 — Additive inverse:**

(la/close? (la/add u (la/scale -1.0 u)) zero2)

(kind/test-last [true?])

;; **Axiom 5 — Scalar compatibility:**

(la/close? (la/scale 2.0 (la/scale 3.0 u))
           (la/scale 6.0 u))

(kind/test-last [true?])

;; **Axiom 6 — Scalar identity:**

(la/close? (la/scale 1.0 u) u)

(kind/test-last [true?])

;; **Axiom 7 — Distributivity over vectors:**

(la/close? (la/scale 5.0 (la/add u v))
           (la/add (la/scale 5.0 u) (la/scale 5.0 v)))

(kind/test-last [true?])

;; **Axiom 8 — Distributivity over scalars:**

(la/close? (la/scale (+ 2.0 3.0) u)
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
;; ## Linear combinations and span
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
;; Visually: start with $2\mathbf{u}$, then add $-\mathbf{v}$
;; (shown dashed) tip-to-tail:

(arrow-plot [{:label "2u" :xy [6 2] :color "#2266cc"}
             {:label "-v" :xy [-1 -2] :color "#cc4422" :from [6 2] :dashed? true}
             {:label "2u-v" :xy [5 0] :color "#228833"}]
            {})

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
;; ## Linear independence
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

;; The difference is visible geometrically — independent vectors
;; point in genuinely different directions, while dependent
;; vectors are parallel:

(arrow-plot [{:label "[3,1]" :xy [3 1] :color "#2266cc"}
             {:label "[1,2]" :xy [1 2] :color "#cc4422"}]
            {:width 250})

;; These point in different directions — they are independent
;; ($\det \neq 0$):

;; Independent — two non-parallel directions:

(la/det (la/matrix [[3 1]
                    [1 2]]))

(kind/test-last
 [(fn [d] (> (Math/abs d) 1e-10))])

;; Now two parallel vectors — one is $2\times$ the other:

(arrow-plot [{:label "[3,1]" :xy [3 1] :color "#2266cc"}
             {:label "[6,2]" :xy [6 2] :color "#cc4422"}]
            {:width 250})

;; They lie on the same line — the second adds no new direction.

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
;; ## Basis and dimension
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

(la/close? w
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
;;
;; Here are three independent vectors in $\mathbb{R}^3$ (they
;; form a basis because the determinant is non-zero):

(def v1 (la/column [1 0 0]))
(def v2 (la/column [0 1 0]))
(def v3 (la/column [0 0 1]))

(la/det (la/matrix [[1 0 0]
                    [0 1 0]
                    [0 0 1]]))

(kind/test-last
 [(fn [d] (< (Math/abs (- d 1.0)) 1e-10))])

;; Now introduce a fourth vector. It must be expressible as
;; a linear combination of the first three — because three
;; independent vectors already fill all of $\mathbb{R}^3$:

(def v4 (la/column [2 3 1]))

(la/close? v4
           (la/add (la/scale 2.0 v1)
                   (la/add (la/scale 3.0 v2)
                           (la/scale 1.0 v3))))

(kind/test-last [true?])

;; No matter what fourth vector you choose, it will always be
;; a combination of any basis for the space. This is the essence
;; of dimension — it is an intrinsic property of the space, not
;; of any particular set of vectors.

;; ---
;;
;; These foundational ideas — vectors, linear combinations,
;; independence, basis, dimension — are the vocabulary for
;; everything that follows. In the next chapters we build on
;; them to study linear maps and their matrix representations,
;; inner products and projections, and the eigenvalue
;; decompositions that reveal the structure of linear
;; transformations.
