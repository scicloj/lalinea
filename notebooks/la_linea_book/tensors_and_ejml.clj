;; # Tensors and EJML
;;
;; **La Linea** represents matrices as
;; [dtype-next](https://github.com/cnuernber/dtype-next) tensors
;; and uses [EJML](https://ejml.org/) for the heavy numerical work.
;; This chapter introduces both libraries and shows how they
;; fit together through zero-copy interop.

(ns la-linea-book.tensors-and-ejml
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## dtype-next: typed numerical arrays
;;
;; [dtype-next](https://github.com/cnuernber/dtype-next) is a Clojure
;; library for working with typed numerical data. Its core abstraction
;; is the **buffer** — a typed, indexed container that can be backed
;; by a Java array, an off-heap memory region, or a functional reader
;; that computes values on the fly. All three look the same to user
;; code: they share a common interface for element access, type queries,
;; and functional transformations.
;;
;; A [tensor](https://cnuernber.github.io/dtype-next/tech.v3.tensor.html)
;; is a **multi-dimensional view** over a buffer. Reshaping or slicing
;; a tensor creates a new view without copying data — the same backing
;; storage is simply indexed differently.

;; ### Creating tensors

;; `tensor/->tensor` creates a tensor from nested sequences:

(tensor/->tensor [[1 2 3]
                  [4 5 6]] {:datatype :float64})

(kind/test-last [(fn [t] (= [2 3] (vec (dtype/shape t))))])

;; `tensor/compute-tensor` creates a lazy tensor that calls a function
;; at each index on every read — useful for procedural construction.
;; With a pure function this is transparent; use `dtype/clone` to
;; materialize when you need a concrete array:

(tensor/compute-tensor [3 3]
                       (fn [i j] (if (== i j) 1.0 0.0))
                       :float64)

(kind/test-last [(fn [t] (= 1.0 (tensor/mget t 1 1)))])

;; ### Indexing and mutation

;; Tensors are callable — `(t i j)` reads element `[i,j]`:

(let [t (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  [(t 0 1) (t 1 0)])

(kind/test-last [(fn [v] (= [20.0 30.0] v))])

;; `tensor/mget` does the same, and `tensor/mset!` mutates in place:

(let [t (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})]
  (tensor/mset! t 0 1 99.0)
  (t 0 1))

(kind/test-last [= 99.0])

;; ### Element-wise operations with dfn
;;
;; The `tech.v3.datatype.functional` namespace (aliased `dfn`) provides
;; element-wise arithmetic on tensors. These operations return **lazy
;; readers** — they compute on access without allocating intermediate
;; arrays.

(let [a (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})
      b (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  (dfn/+ a b))

(kind/test-last [(fn [t] (= 44.0 (tensor/mget t 1 1)))])

;; Chaining is free — no intermediate copies:

(let [x (tensor/->tensor [[1 4] [9 16]] {:datatype :float64})]
  (tensor/mget (dfn/sqrt x) 1 0))

;; $\sqrt{9} = 3$

(kind/test-last [= 3.0])

;; ## EJML: efficient Java matrix library
;;
;; [EJML](https://ejml.org/) is a pure-Java linear algebra library.
;; Its workhorse type for real matrices is
;; [`DMatrixRMaj`](http://ejml.org/javadoc/org/ejml/data/DMatrixRMaj.html)
;; — a dense matrix stored as a flat `double[]` in **row-major** order.
;;
;; EJML provides the algorithms that La Linea wraps: matrix multiply,
;; decompositions (eigen, SVD, QR, Cholesky), inverse, solve, and
;; determinant.

;; ### Convenient coincidence: same memory layout
;;
;; A dtype-next `[r c]` tensor of `:float64` values is backed by a
;; `double[]` in row-major order. An EJML `DMatrixRMaj` is also
;; backed by a `double[]` in row-major order.
;;
;; | Type | Backing | Layout |
;; |:-----|:--------|:-------|
;; | dtype-next tensor `[r c]` | `double[r*c]` | row-major |
;; | EJML `DMatrixRMaj` | `double[r*c]` | row-major |
;;
;; This matching layout is convenient — it lets us convert between
;; the two with no allocation and no copying, just wrapping the
;; same `double[]` in a different view. With a different layout
;; we could still bridge the two libraries using `compute-tensor`,
;; but the shared row-major convention makes it free.

;; ## Zero-copy round-trip
;;
;; `tensor->dmat` and `dmat->tensor` share the identical Java array.

(let [t (la/matrix [[1.0 2.0] [3.0 4.0]])
      dm (la/tensor->dmat t)]
  {:identical? (identical? (dtype/->double-array t)
                           (.data ^DMatrixRMaj dm))
   :rows (.numRows ^DMatrixRMaj dm)
   :cols (.numCols ^DMatrixRMaj dm)})

(kind/test-last [(fn [v] (and (:identical? v)
                              (= (:rows v) 2)
                              (= (:cols v) 2)))])

;; Mutations through the EJML view are visible in the tensor:

(let [t (la/matrix [[1.0 0.0] [0.0 1.0]])
      dm (la/tensor->dmat t)]
  (.set ^DMatrixRMaj dm 0 1 99.0)
  (tensor/mget t 0 1))

(kind/test-last [= 99.0])

;; And mutations through the tensor are visible in EJML:

(let [t (la/matrix [[1.0 0.0] [0.0 1.0]])
      dm (la/tensor->dmat t)]
  (tensor/mset! t 0 1 42.0)
  (.get ^DMatrixRMaj dm 0 1))

(kind/test-last [= 42.0])

;; ## Tensor printing
;;
;; dtype-next tensors print with shape and formatted values — much
;; nicer than raw `double[]` or EJML's default output.

(la/matrix [[1 2 3]
            [4 5 6]
            [7 8 9]])

(kind/test-last [(fn [m] (= [3 3] (vec (dtype/shape m))))])

;; ## Matrix operations
;;
;; The `scicloj.la-linea.linalg` namespace provides a clean functional
;; API. Under the hood, each call converts to EJML, performs the
;; operation, and converts back — all zero-copy.

;; ### Matrix multiply
;;
;; $A \cdot I = A$

(la/mmul (la/matrix [[1 2] [3 4]])
         (la/eye 2))

(kind/test-last [(fn [m] (= 1.0 (tensor/mget m 0 0)))])

;; ### Inverse

(la/invert (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (= [2 2] (vec (dtype/shape m))))])

;; ### Frobenius norm

(la/norm (la/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [v] (< (Math/abs (- v (Math/sqrt 91.0))) 1e-10))])

;; ## Decompositions

;; ### Eigendecomposition
;;
;; For a symmetric matrix, eigenvalues are real.

(la/real-eigenvalues (la/matrix [[4 1] [1 3]]))

(kind/test-last [(fn [evs] (= 2 (count evs)))])

;; ### SVD

(:S (la/svd (la/matrix [[1 2] [3 4]])))

(kind/test-last [(fn [S] (= 2 (count S)))])

;; ### QR decomposition

(la/qr (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [{:keys [Q R]}] (and (some? Q) (some? R)))])

;; ### Cholesky decomposition

(la/cholesky (la/matrix [[4 2] [2 3]]))

(kind/test-last [(fn [L] (= [2 2] (vec (dtype/shape L))))])
