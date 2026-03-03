;; # Vectors and Spaces
;;
;; A **[vector space](https://en.wikipedia.org/wiki/Vector_space)** is an abstract concept: any collection of
;; objects that can be added together and scaled by numbers, following
;; a short list of axioms. Theorems proved from these axioms
;; apply to every vector space simultaneously — columns of numbers,
;; polynomials, functions, matrices, and more.
;;
;; This chapter develops the foundations using $\mathbb{R}^n$ — columns
;; of real numbers — as our main concrete example. We define addition
;; and scaling, state the axioms, and build up to the key concepts
;; of [linear independence](https://en.wikipedia.org/wiki/Linear_independence), [basis](https://en.wikipedia.org/wiki/Basis_(linear_algebra)), and dimension.

(ns lalinea-book.vectors-and-spaces
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Arrow diagrams for 2D vectors:
   [scicloj.lalinea.vis :as vis]))

;; ## Vectors
;;
;; ### Vectors in $\mathbb{R}^n$
;;
;; Our concrete setting is $\mathbb{R}^n$ — the space of ordered
;; lists of $n$ real numbers. A vector in $\mathbb{R}^2$ (the plane)
;; has two entries; a vector in $\mathbb{R}^3$ (space) has three.

(def u (t/column [3 1]))
(def v (t/column [1 2]))

;; We can think of these as arrows from the origin to a point,
;; or as displacements — "go 3 units right and 1 unit up."

(vis/arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
                 {:label "v" :xy [1 2] :color "#cc4422"}]
                {:width 300})

;; ### Vector addition
;;
;; Adding two vectors means adding their entries position by position.
;; Geometrically, it is "tip to tail" — walk along $\mathbf{u}$,
;; then walk along $\mathbf{v}$.

(la/add u v)

(kind/test-last
 [(fn [r] (and (= 4.0 (r 0 0))
               (= 3.0 (r 1 0))))])

(vis/arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
                 {:label "v" :xy [1 2] :color "#cc4422" :from [3 1]}
                 {:label "u+v" :xy [4 3] :color "#228833" :dashed? true}]
                {:width 300})

;; ### Scalar multiplication
;;
;; Multiplying a vector by a number (a **[scalar](https://en.wikipedia.org/wiki/Scalar_(mathematics))**) scales every entry.
;; Geometrically, it stretches (or shrinks, or reverses) the arrow.

(la/scale u 2.0)

(kind/test-last
 [(fn [r] (and (= 6.0 (r 0 0))
               (= 2.0 (r 1 0))))])

(vis/arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
                 {:label "2u" :xy [6 2] :color "#8844cc"}]
                {:width 300})

;; Scaling by $-1$ reverses the direction:

(la/scale u -1.0)

(kind/test-last
 [(fn [r] (and (= -3.0 (r 0 0))
               (= -1.0 (r 1 0))))])

(vis/arrow-plot [{:label "u" :xy [3 1] :color "#2266cc"}
                 {:label "\u2212u" :xy [-3 -1] :color "#cc4422"}]
                {:width 300})

;; ### The vector space axioms
;;
;; These two operations — addition and scalar multiplication —
;; satisfy a list of rules that feel obvious for columns of
;; numbers, but they define a useful abstraction.
;; Any collection of objects obeying these rules is called
;; a **vector space**.
;;
;; The axioms are:
;;
;; **Addition axioms:**
;;
;; 1. **Commutativity**: for all vectors $\mathbf{u}, \mathbf{v}$: $\mathbf{u} + \mathbf{v} = \mathbf{v} + \mathbf{u}$
;; 2. **Associativity**: for all vectors $\mathbf{u}, \mathbf{v}, \mathbf{w}$: $(\mathbf{u} + \mathbf{v}) + \mathbf{w} = \mathbf{u} + (\mathbf{v} + \mathbf{w})$
;; 3. **Zero vector**: there exists $\mathbf{0}$ such that for every vector $\mathbf{u}$: $\mathbf{u} + \mathbf{0} = \mathbf{u}$
;; 4. **Additive inverse**: for every $\mathbf{u}$, there exists $-\mathbf{u}$ with $\mathbf{u} + (-\mathbf{u}) = \mathbf{0}$
;;
;; **Scalar multiplication axioms:**
;;
;; 5. **Compatibility**: for all scalars $\alpha, \beta$ and every vector $\mathbf{u}$: $\alpha(\beta \mathbf{u}) = (\alpha \beta) \mathbf{u}$
;; 6. **Identity**: for every vector $\mathbf{u}$: $1 \cdot \mathbf{u} = \mathbf{u}$
;; 7. **Distributivity over vectors**: for every scalar $\alpha$ and all vectors $\mathbf{u}, \mathbf{v}$: $\alpha(\mathbf{u} + \mathbf{v}) = \alpha\mathbf{u} + \alpha\mathbf{v}$
;; 8. **Distributivity over scalars**: for all scalars $\alpha, \beta$ and every vector $\mathbf{u}$: $(\alpha + \beta)\mathbf{u} = \alpha\mathbf{u} + \beta\mathbf{u}$
;;
;; Let us verify each one on our concrete vectors
;; $\mathbf{u} = [3,1]^T$, $\mathbf{v} = [1,2]^T$ in the plane.
;; These are not proofs — the axioms hold by the definition of
;; entry-wise addition and scaling — but checking them builds
;; intuition for what each rule says.

(def w-ax (t/column [-1 4]))
(def zero2 (t/column [0 0]))

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

(la/close? (la/add u (la/scale u -1.0)) zero2)

(kind/test-last [true?])

;; **Axiom 5 — Scalar compatibility:**

(la/close? (la/scale (la/scale u 3.0) 2.0)
           (la/scale u 6.0))

(kind/test-last [true?])

;; **Axiom 6 — Scalar identity:**

(la/close? (la/scale u 1.0) u)

(kind/test-last [true?])

;; **Axiom 7 — Distributivity over vectors:**

(la/close? (la/scale (la/add u v) 5.0)
           (la/add (la/scale u 5.0) (la/scale v 5.0)))

(kind/test-last [true?])

;; **Axiom 8 — Distributivity over scalars:**

(la/close? (la/scale u (+ 2.0 3.0))
           (la/add (la/scale u 2.0) (la/scale u 3.0)))

(kind/test-last [true?])

;; Any collection of objects satisfying these rules is a vector
;; space, and theorems proved from these axioms apply to all of
;; them.
;;
;; The rules feel unsurprising for number columns, but the same
;; structure appears in surprising places:
;;
;; - **Polynomials** of degree $\leq n$: add coefficients, scale by constants. Dimension $n+1$.
;; - **Continuous functions** $f: [a,b] \to \mathbb{R}$: pointwise addition $(f+g)(x) = f(x)+g(x)$ and scaling $(\alpha f)(x) = \alpha f(x)$. Infinite-dimensional.
;; - **Matrices** of size $m \times n$: entry-by-entry addition and scaling. Dimension $mn$.
;; - **Solutions to a linear ODE** like $y'' + y = 0$: any linear combination of solutions is again a solution.
;;
;; Working abstractly means proving something once — say, that every
;; basis has the same number of elements — and having it apply to
;; all of these simultaneously. The rest of this chapter works in
;; $\mathbb{R}^n$, but the concepts (span, independence, basis,
;; dimension) are defined by the axioms and hold in every vector space.

;; ---
;;
;; ## [Linear combinations](https://en.wikipedia.org/wiki/Linear_combination) and span
;;
;; ### Linear combinations
;;
;; A **linear combination** of vectors $\mathbf{v}_1, \ldots, \mathbf{v}_k$
;; is any expression $\alpha_1 \mathbf{v}_1 + \cdots + \alpha_k \mathbf{v}_k$
;; where the $\alpha_i$ are scalars. This is the most fundamental
;; operation in linear algebra — everything else builds on it.

(la/add (la/scale u 2.0) (la/scale v -1.0))

(kind/test-last
 [(fn [r] (and (= 5.0 (r 0 0))
               (= 0.0 (r 1 0))))])

;; $2 \cdot [3,1]^T + (-1) \cdot [1,2]^T = [5,0]^T$.
;; We combined $\mathbf{u}$ and $\mathbf{v}$ to reach a new point.
;; Visually: start with $2\mathbf{u}$, then add $-\mathbf{v}$
;; (shown dashed) tip-to-tail:

(vis/arrow-plot [{:label "2u" :xy [6 2] :color "#2266cc"}
                 {:label "-v" :xy [-1 -2] :color "#cc4422" :from [6 2] :dashed? true}
                 {:label "2u-v" :xy [5 0] :color "#228833"}]
                {})

;; ### Span
;;
;; The **[span](https://en.wikipedia.org/wiki/Linear_span)** of a set of vectors is the collection of all
;; their linear combinations — every point you can reach by
;; adding and scaling them.
;;
;; Our vectors $\mathbf{u} = [3,1]^T$ and $\mathbf{v} = [1,2]^T$
;; point in different directions. Their span should be the entire
;; plane — let us check by plotting many combinations
;; $\alpha \mathbf{u} + \beta \mathbf{v}$:

(let [coeffs (for [a (range -2.0 2.1 0.5)
                   b (range -2.0 2.1 0.5)]
               [a b])
      n (count coeffs)
      points (t/clone
              (t/compute-tensor [n 2]
                                (fn [i j]
                                  (let [[a b] (nth coeffs i)]
                                    (+ (* a (u j 0))
                                       (* b (v j 0)))))
                                :float64))
      xs (t/select points :all 0)
      ys (t/select points :all 1)]
  (-> (tc/dataset {:x xs :y ys})
      (plotly/base {:=x :x :=y :y})
      (plotly/layer-point {:=mark-size 6})
      plotly/plot))

;; The points fill a grid that covers the whole plane.
;; Every point in $\mathbb{R}^2$ can be expressed as
;; $\alpha \mathbf{u} + \beta \mathbf{v}$ for some $\alpha, \beta$.
;;
;; What if the vectors point in the **same** direction?
;; Take $[1,2]^T$ and $[2,4]^T$ — one is just $2\times$ the other:

(let [s1 (t/column [1 2])
      s2 (t/column [2 4])
      coeffs (for [a (range -2.0 2.1 0.5)
                   b (range -2.0 2.1 0.5)]
               [a b])
      n (count coeffs)
      points (t/clone
              (t/compute-tensor [n 2]
                                (fn [i j]
                                  (let [[a b] (nth coeffs i)]
                                    (+ (* a (s1 j 0))
                                       (* b (s2 j 0)))))
                                :float64))
      xs (t/select points :all 0)
      ys (t/select points :all 1)]
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

(vis/arrow-plot [{:label "[3,1]" :xy [3 1] :color "#2266cc"}
                 {:label "[1,2]" :xy [1 2] :color "#cc4422"}]
                {:width 250})

;; These point in different directions — they are independent
;; ($\det \neq 0$):

;; Independent — two non-parallel directions:

(la/det (t/matrix [[3 1]
                   [1 2]]))

(kind/test-last
 [(fn [d] (> (abs d) 1e-10))])

;; Now two parallel vectors — one is $2\times$ the other:

(vis/arrow-plot [{:label "[3,1]" :xy [3 1] :color "#2266cc"}
                 {:label "[6,2]" :xy [6 2] :color "#cc4422"}]
                {:width 250})

;; They lie on the same line — the second adds no new direction.

;; Dependent — second column is $2 \times$ the first:

(la/det (t/matrix [[3 6]
                   [1 2]]))

(kind/test-last
 [(fn [d] (< (abs d) 1e-10))])

;; In $\mathbb{R}^3$, three vectors are independent when they
;; do not all lie in the same plane:

(la/det (t/matrix [[1 0 0]
                   [0 1 0]
                   [0 0 1]]))

(kind/test-last
 [(fn [d] (< (abs (- d 1.0)) 1e-10))])

;; But if the third is the sum of the first two, they lie in a
;; plane — the determinant is zero:

(la/det (t/matrix [[1 0 1]
                   [0 1 1]
                   [0 0 0]]))

(kind/test-last
 [(fn [d] (< (abs d) 1e-10))])

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

(def e1 (t/column [1 0 0]))
(def e2 (t/column [0 1 0]))
(def e3 (t/column [0 0 1]))

;; ### Coordinates
;;
;; Once you fix a basis, every vector has a unique
;; representation as a linear combination of basis vectors.
;; The coefficients are the **coordinates**.
;;
;; For the standard basis, coordinates are just the entries:

(def w (t/column [5 -3 7]))

;; $\mathbf{w} = 5\mathbf{e}_1 + (-3)\mathbf{e}_2 + 7\mathbf{e}_3$:

(la/close? w
           (la/add (la/scale e1 5.0)
                   (la/add (la/scale e2 -3.0)
                           (la/scale e3 7.0))))

(kind/test-last [true?])

;; But $\{\mathbf{e}_1, \mathbf{e}_2, \mathbf{e}_3\}$ is not the
;; only basis. Any three linearly independent vectors in
;; $\mathbb{R}^3$ form a basis — and the coordinates change.

;; ### Dimension
;;
;; **Every basis for a given vector space has the same number
;; of vectors.** This number is the **[dimension](https://en.wikipedia.org/wiki/Dimension_(vector_space))**.
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

(def v1 (t/column [1 0 0]))
(def v2 (t/column [0 1 0]))
(def v3 (t/column [0 0 1]))

(la/det (t/matrix [[1 0 0]
                   [0 1 0]
                   [0 0 1]]))

(kind/test-last
 [(fn [d] (< (abs (- d 1.0)) 1e-10))])

;; Now introduce a fourth vector. It must be expressible as
;; a linear combination of the first three — because three
;; independent vectors already fill all of $\mathbb{R}^3$:

(def v4 (t/column [2 3 1]))

(la/close? v4
           (la/add (la/scale v1 2.0)
                   (la/add (la/scale v2 3.0)
                           (la/scale v3 1.0))))

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
