;; # Tensors and EJML
;;
;; **basis** uses dtype-next tensors as the matrix type and EJML
;; as the computational backend. The two share the same memory
;; layout — row-major `double[]` — enabling **zero-copy** interop.
;;
;; | Type | Backing | Layout |
;; |:-----|:--------|:-------|
;; | dtype-next tensor `[r c]` | `double[r*c]` | row-major |
;; | EJML `DMatrixRMaj` [r×c] | `double[r*c]` | row-major |
;;
;; This means converting between the two involves no allocation
;; and no copying — just wrapping the same `double[]` in a
;; different view.

(ns basis-book.tensors-and-ejml
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Tensor ↔ EJML zero-copy bridge:
   [scicloj.basis.impl.tensor :as bt]
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
      dm (bt/tensor->dmat t)]
  {:identical? (identical? (dtype/->double-array (.buffer t))
                           (.data ^DMatrixRMaj dm))
   :rows (.numRows ^DMatrixRMaj dm)
   :cols (.numCols ^DMatrixRMaj dm)})

(kind/test-last [(fn [v] (and (:identical? v)
                              (= (:rows v) 2)
                              (= (:cols v) 2)))])

;; Mutations through the EJML view are visible in the tensor:

(let [t (la/matrix [[1.0 0.0] [0.0 1.0]])
      dm (bt/tensor->dmat t)]
  (.set ^DMatrixRMaj dm 0 1 99.0)
  (tensor/mget t 0 1))

(kind/test-last [= 99.0])

;; And mutations through the tensor are visible in EJML:

(let [t (la/matrix [[1.0 0.0] [0.0 1.0]])
      dm (bt/tensor->dmat t)
      arr (dtype/->double-array (.buffer t))]
  (aset arr 1 42.0)
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

(let [A (la/matrix [[1 2] [3 4]])
      I (la/eye 2)]
  (la/mmul A I))

(kind/test-last [(fn [m] (= 1.0 (tensor/mget m 0 0)))])

;; ### Inverse
;;
;; $A \cdot A^{-1} = I$

(let [A (la/matrix [[1 2] [3 4]])
      Ainv (la/invert A)
      product (la/mmul A Ainv)]
  {:diag [(tensor/mget product 0 0)
          (tensor/mget product 1 1)]
   :off [(tensor/mget product 0 1)
         (tensor/mget product 1 0)]})

(kind/test-last [(fn [v] (and (every? #(< (Math/abs (- % 1.0)) 1e-10) (:diag v))
                              (every? #(< (Math/abs %) 1e-10) (:off v))))])

;; ### Frobenius norm identity
;;
;; $\|A\|_F^2 = \operatorname{tr}(A^T A)$

(let [A (la/matrix [[1 2 3] [4 5 6]])
      AtA (la/mmul (la/transpose A) A)
      nf (la/norm A)]
  (< (Math/abs (- (la/trace AtA) (* nf nf))) 1e-10))

(kind/test-last [true?])

;; ## Composing tensors with dfn
;;
;; Since matrices are dtype-next tensors, all `dfn` operations work
;; element-wise.

(let [A (la/matrix [[1 4] [9 16]])]
  (tensor/mget (dfn/sqrt A) 1 0))

;; $\sqrt{9} = 3$

(kind/test-last [= 3.0])

;; Element-wise multiply (Hadamard product):

(let [A (la/matrix [[1 2] [3 4]])
      B (la/matrix [[5 6] [7 8]])]
  (tensor/mget (tensor/ensure-tensor (dfn/* A B)) 0 0))

(kind/test-last [= 5.0])

;; ## Decompositions

;; ### Eigendecomposition
;;
;; For a symmetric matrix, eigenvalues are real.

(let [A (la/matrix [[4 1] [1 3]])
      {:keys [eigenvalues]} (la/eigen A)]
  (sort (map first eigenvalues)))

(kind/test-last [(fn [evs] (let [expected [2.381966011250105 4.618033988749895]]
                              (every? identity
                                      (map (fn [a b] (< (Math/abs (- a b)) 1e-10))
                                           evs expected))))])

;; ### SVD reconstruction
;;
;; $A = U \cdot \operatorname{diag}(S) \cdot V^T$

(let [A (la/matrix [[1 2] [3 4]])
      {:keys [U S Vt]} (la/svd A)
      reconstructed (la/mmul (la/mmul U (la/diag S)) Vt)]
  (< (la/norm (la/sub A reconstructed)) 1e-10))

(kind/test-last [true?])

;; ### QR decomposition
;;
;; $A = QR$ with $Q$ orthogonal and $R$ upper-triangular.

(let [A (la/matrix [[1 2] [3 4]])
      {:keys [Q R]} (la/qr A)
      QtQ (la/mmul (la/transpose Q) Q)]
  (< (la/norm (la/sub QtQ (la/eye 2))) 1e-10))

(kind/test-last [true?])

;; Verify reconstruction: $QR = A$.

(let [A (la/matrix [[1 2] [3 4]])
      {:keys [Q R]} (la/qr A)]
  (< (la/norm (la/sub A (la/mmul Q R))) 1e-10))

(kind/test-last [true?])

;; ### Cholesky decomposition
;;
;; For a symmetric positive definite matrix, $A = L L^T$.

(let [A (la/matrix [[4 2] [2 3]])
      L (la/cholesky A)
      reconstructed (la/mmul L (la/transpose L))]
  (< (la/norm (la/sub A reconstructed)) 1e-10))

(kind/test-last [true?])
