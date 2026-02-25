;; # Tensors and EJML
;;
;; **basis** uses dtype-next tensors as the matrix type and EJML
;; as the computational backend. The two share the same memory
;; layout — row-major `double[]` — enabling **zero-copy** interop.
;;
;; | Type | Backing | Layout |
;; |:-----|:--------|:-------|
;; | dtype-next tensor `[r c]` | `double[r*c]` | row-major |
;; | EJML `DMatrixRMaj` `[r*c]` | `double[r*c]` | row-major |
;;
;; This means converting between the two involves no allocation
;; and no copying — just wrapping the same `double[]` in a
;; different view.

(ns basis-book.tensors-and-ejml
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## Zero-copy round-trip
;;
;; `tensor->dmat` and `dmat->tensor` share the identical Java array.

(let [t (la/matrix [[1.0 2.0] [3.0 4.0]])
      dm (la/tensor->dmat t)]
  {:identical? (identical? (.ary-data (dtype/as-array-buffer t))
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
;; The `scicloj.basis.linalg` namespace provides a clean functional
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

;; ## Composing tensors with dfn
;;
;; Since matrices are dtype-next tensors, all `dfn` operations work
;; element-wise.

(tensor/mget (dfn/sqrt (la/matrix [[1 4] [9 16]])) 1 0)

;; $\sqrt{9} = 3$

(kind/test-last [= 3.0])

;; Element-wise multiply (Hadamard product):

(tensor/mget (tensor/ensure-tensor
              (dfn/* (la/matrix [[1 2] [3 4]])
                     (la/matrix [[5 6] [7 8]])))
             0 0)

(kind/test-last [= 5.0])

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
