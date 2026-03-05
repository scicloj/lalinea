;; # Ecosystem Composability
;;
;; *Prerequisites: [tensors](tensors.html), [element-wise operations](api_reference.html),
;; and [eigendecomposition](eigenvalues_and_decompositions.html).*
;;
;; La Linea matrices are backed by [dtype-next](https://github.com/cnuernber/dtype-next)
;; tensors. This chapter passes RealTensors to
;; [Tablecloth](https://scicloj.github.io/tablecloth/)/[tech.ml.dataset](https://github.com/techascent/tech.ml.dataset),
;; [Tableplot](https://scicloj.github.io/tableplot/),
;; [Fastmath](https://generateme.github.io/fastmath/), and dtype-next
;; — showing what works directly and where the boundaries are.

(ns lalinea-book.ecosystem
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.elementwise :as el]
   ;; dtype-next — the shared foundation:
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.tensor :as dtt]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Fastmath (https://generateme.github.io/fastmath/):
   [fastmath.vector :as fv]
   [fastmath.matrix :as fm]
   [fastmath.stats :as stats]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Tablecloth/tech.ml.dataset
;;
;; RealTensors implement `Seqable`, `Indexed`, and dtype-next's
;; `PToReader` — so Tablecloth/tech.ml.dataset can use them as
;; column data.

;; A column vector as a dataset column:

(let [v (t/column [1 2 3 4 5])]
  (tc/dataset {:x v}))

(kind/test-last [(fn [ds] (= 5 (tc/row-count ds)))])

;; Multiple RealTensor columns:

(let [v (t/column [1 2 3 4 5])]
  (tc/dataset {:x v :y (el/sq v)}))

(kind/test-last [(fn [ds] (= [5 2] [(tc/row-count ds) (tc/column-count ds)]))])

;; Adding a RealTensor column to an existing dataset:

(let [v (t/column [1 2 3 4 5])]
  (-> (tc/dataset {:x v})
      (tc/add-column :y (el/sq v))))

(kind/test-last [(fn [ds] (= 2 (tc/column-count ds)))])

;; ## Tableplot
;;
;; Since Tableplot builds on Tablecloth/tech.ml.dataset datasets,
;; RealTensors flow through to Plotly charts.

;; Eigenvectors of a symmetric matrix as a scatter plot:

(let [{:keys [eigenvectors]} (la/eigen (t/matrix [[4 1 1]
                                                  [1 3 0]
                                                  [1 0 2]]))]
  (-> (tc/dataset {:v1 (t/select eigenvectors :all 0)
                   :v2 (t/select eigenvectors :all 1)
                   :component ["x" "y" "z"]})
      (plotly/base {:=x :v1 :=y :v2 :=color :component})
      (plotly/layer-point {:=mark-size 12})
      plotly/plot))

;; ## Fastmath
;;
;; Fastmath has its own vector (`fastmath.vector`) and matrix
;; (`fastmath.matrix`) types. These are not dtype-next tensors,
;; but converting between them is straightforward.

;; ### Vectors
;;
;; Fastmath vectors are seqable, so `t/column` and `t/row` accept
;; them directly:

(t/column (fv/vec3 1 2 3))

(kind/test-last [(fn [rt] (= [3 1] (t/shape rt)))])

;; Dot products agree:

(let [a (fv/vec3 1 2 3)
      b (fv/vec3 4 5 6)]
  [(fv/dot a b)
   (la/dot (t/column a) (t/column b))])

(kind/test-last [(fn [[fm-dot la-dot]] (== fm-dot la-dot 32.0))])

;; ### Matrices
;;
;; Fastmath matrices seq as flat values, not rows. Use `fm/rows`
;; and seq each row:

(let [m (fm/mat2x2 1 2 3 4)]
  (t/matrix (mapv seq (fm/rows m))))

(kind/test-last [(fn [rt] (= [2 2] (t/shape rt)))])

;; Matrix multiply agrees:

(let [a (fm/mat2x2 1 2 3 4)
      b (fm/mat2x2 5 6 7 8)
      ->la (fn [m] (t/matrix (mapv seq (fm/rows m))))]
  (= (la/mmul (->la a) (->la b))
     (->la (fm/mulm a b))))

(kind/test-last [true?])

;; ### Statistics
;;
;; Fastmath's statistical functions expect flat sequences of numbers.
;; A `[8 1]` column is a 2D matrix — iterating its rows yields
;; sub-tensors, not scalars. This is a dimensionality issue, not
;; a wrapper issue — a bare `[8 1]` dtype-next tensor behaves
;; the same way. `t/flatten` converts to 1D:

(let [data (t/column [3 1 4 1 5 9 2 6])]
  (stats/mean (t/flatten data)))

(kind/test-last [(fn [v] (== 3.875 v))])

;; La Linea's `el/mean` handles any shape directly:

(el/mean (t/column [3 1 4 1 5 9 2 6]))

(kind/test-last [(fn [v] (== 3.875 v))])

;; ## dtype-next
;;
;; RealTensors implement dtype-next's protocols. Most dtype-next
;; functions work on them directly.

;; Protocol queries:

(let [m (t/matrix [[1 2] [3 4]])]
  [(dtype/elemwise-datatype m)
   (dtype/ecount m)
   (dtype/shape m)])

(kind/test-last [(fn [v] (= [:float64 4 [2 2]] v))])

;; `dtype/clone` preserves the RealTensor wrapper:

(let [c (dtype/clone (t/matrix [[1 2] [3 4]]))]
  [(t/real-tensor? c) (t/shape c)])

(kind/test-last [(fn [v] (= [true [2 2]] v))])

;; `dfn/` reductions work directly:

(let [m (t/matrix [[1 2] [3 4]])]
  [(dfn/sum m) (dfn/mean m) (dfn/reduce-max m)])

(kind/test-last [(fn [[s mean mx]] (and (== 10.0 s) (== 2.5 mean) (== 4.0 mx)))])

;; ### Constructive operations
;;
;; dtype-next's constructive operations (`dfn/+`, `dtt/reshape`,
;; `dtt/select`) return bare dtype-next values, not RealTensors.
;; RealTensor implements the reader protocol but is not an `NDBuffer`,
;; so these functions fall back to a reader path that loses shape.

;; `dfn/+` returns a lazy reader — a `[3 1]` column becomes `[3]`:

(let [a (t/column [1 2 3])
      b (t/column [10 20 30])
      c (dfn/+ a b)]
  {:real-tensor? (t/real-tensor? c)
   :input-shape (dtype/shape a)
   :output-shape (dtype/shape c)})

(kind/test-last [(fn [{:keys [real-tensor? input-shape output-shape]}]
                   (and (not real-tensor?)
                        (= [3 1] input-shape)
                        (= [3] output-shape)))])

;; Bare `[3 1]` dtype-next tensors keep their shape through `dfn/+`:

(let [bare-a (dtt/->tensor [[1] [2] [3]] {:datatype :float64})
      bare-b (dtt/->tensor [[10] [20] [30]] {:datatype :float64})]
  (dtype/shape (dfn/+ bare-a bare-b)))

(kind/test-last [(fn [s] (= [3 1] s))])

;; Use La Linea's `el/` and `t/` instead — they preserve
;; the wrapper and the shape:

(let [a (t/column [1 2 3])
      b (t/column [10 20 30])
      c (el/+ a b)]
  [(t/real-tensor? c) (t/shape c) (vec (t/flatten c))])

(kind/test-last [(fn [[rt? sh vals]]
                   (and rt? (= [3 1] sh) (= [11.0 22.0 33.0] vals)))])

(t/shape (t/reshape (t/matrix [[1 2] [3 4]]) [1 4]))

(kind/test-last [(fn [s] (= [1 4] s))])

;; ## The wrapper tradeoff
;;
;; La Linea wraps dtype-next tensors in a `RealTensor` type. The wrapper
;; provides tagged printing (`#la/R`), type-based dispatch for `el/` and
;; `la/`, structural equality with vectors, and tape recording for autodiff.
;;
;; The cost: dtype-next functions that *read* RealTensors (reductions,
;; element access, cloning) work without friction, because RealTensor
;; implements the relevant protocols. But functions that *construct new
;; values* (`dfn/+`, `dtt/reshape`) return bare dtype-next objects —
;; they don't know about our wrapper.
;;
;; The rule of thumb: **use `el/` and `t/` for element-wise math and
;; structural operations.** They preserve the wrapper and the shape.
;; Use `dfn/` and `dtt/` only for reading and querying, or when you
;; intentionally want to drop into raw dtype-next.
;;
;; | Operation | dtype-next | La Linea |
;; |:----------|:-----------|:---------|
;; | Element-wise add | `dfn/+` (loses shape) | `el/+` |
;; | Reshape | `dtt/reshape` (unwraps) | `t/reshape` |
;; | Select | `dtt/select` (unwraps) | `t/select` |
;; | Argmax/argmin | `argops/argmax` | `el/argmax`, `el/argmin` |
;; | Sort/argsort | `argops/argsort` | `el/sort`, `el/argsort` |
;; | Sum | `dfn/sum` (works) | `el/sum` |
;; | Clone | `dtype/clone` (works) | `t/clone` |
;; | Shape | `dtype/shape` (works) | `t/shape` |
