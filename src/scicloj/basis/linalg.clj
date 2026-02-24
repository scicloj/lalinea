(ns scicloj.basis.linalg
  "Linear algebra operations on dtype-next tensors, backed by EJML.

   Matrices are dtype-next tensors of shape [r c]. All operations
   accept tensors and return tensors — EJML is used internally for
   computation, with zero-copy conversion.

   For complex matrices, functions accept and return ComplexTensors."
  (:require [scicloj.basis.impl.tensor :as bt]
            [scicloj.basis.impl.complex :as cx]
            [scicloj.basis.impl.ejml :as ejml]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]))

;; ---------------------------------------------------------------------------
;; Matrix construction
;; ---------------------------------------------------------------------------

(defn matrix
  "Create a matrix (rank-2 tensor) from nested sequences.

   ```clojure
   (matrix [[1 2] [3 4]])
   ;; => #tech.v3.tensor<float64>[2 2]
   ;; [[1.000 2.000]
   ;;  [3.000 4.000]]
   ```"
  [rows]
  (bt/matrix rows))

(defn eye
  "Identity matrix of size n × n."
  [n]
  (bt/eye n))

(defn zeros
  "Zero matrix of size r × c."
  [r c]
  (bt/zeros r c))

(defn diag
  "Create a diagonal matrix from a sequence of diagonal values."
  [values]
  (let [v (dtype/->reader (dtype/make-container :float64 values))
        n (count v)]
    (tensor/compute-tensor [n n]
      (fn [i j] (if (== i j) (double (v i)) 0.0))
      :float64)))

(defn column
  "Create a column vector (shape [n 1]) from a sequence."
  [xs]
  (bt/col-vector xs))

(defn row
  "Create a row vector (shape [1 n]) from a sequence."
  [xs]
  (bt/row-vector xs))

;; ---------------------------------------------------------------------------
(defn submatrix
  "Extract a contiguous submatrix. rows and cols can be :all or a range.

   tensor/select returns a non-contiguous view; this function clones
   it into a contiguous tensor suitable for EJML operations.

   For ComplexTensors, operates on the complex dimensions and preserves
   the ComplexTensor type.

   ```clojure
   (submatrix U :all (range k))   ;; first k columns
   (submatrix Vt (range k) :all)  ;; first k rows
   ```"
  [m rows cols]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor m)
    (cx/complex-tensor
     (dtype/clone (tensor/select (cx/->tensor m) rows cols :all)))
    (dtype/clone (tensor/select m rows cols))))

;; ---------------------------------------------------------------------------
;; Matrix multiply
;; ---------------------------------------------------------------------------

(defn mmul
  "Matrix multiply. Accepts dtype-next tensors or ComplexTensors.

   For real matrices: C = A * B
   For complex matrices: delegates to EJML's ZMatrixRMaj multiply."
  [a b]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    ;; Complex path
    (let [za (ejml/ct->zmat a)
          zb (ejml/ct->zmat b)
          zc (ejml/zmul za zb)]
      (ejml/zmat->ct zc))
    ;; Real path
    (let [da (bt/tensor->dmat a)
          db (bt/tensor->dmat b)
          dc (ejml/dmul da db)]
      (bt/dmat->tensor dc))))

;; ---------------------------------------------------------------------------
;; Transpose
;; ---------------------------------------------------------------------------

(defn transpose
  "Matrix transpose (real) or conjugate transpose (complex).

   For real matrices: B = A^T
   For complex matrices: B = A† (Hermitian adjoint)"
  [a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (let [za (ejml/ct->zmat a)]
      (ejml/zmat->ct (ejml/ztranspose-conj za)))
    (let [da (bt/tensor->dmat a)]
      (bt/dmat->tensor (ejml/dtranspose da)))))

;; ---------------------------------------------------------------------------
;; Addition and subtraction
;; ---------------------------------------------------------------------------

(defn add
  "Matrix addition."
  [a b]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (cx/add a b)
    (dfn/+ a b)))

(defn sub
  "Matrix subtraction."
  [a b]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (cx/sub a b)
    (dfn/- a b)))

(defn scale
  "Scalar multiply: alpha * A."
  [alpha a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (cx/scale a alpha)
    (dfn/* a (double alpha))))

;; ---------------------------------------------------------------------------
;; Scalar properties
;; ---------------------------------------------------------------------------

(defn trace
  "Matrix trace. Returns a double for real matrices, a scalar ComplexTensor
   for complex matrices."
  [a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (let [[re im] (ejml/ztrace (ejml/ct->zmat a))]
      (cx/complex re im))
    (let [shape (dtype/shape a)
          n (min (long (first shape)) (long (second shape)))]
      (dfn/sum (dtype/make-reader :float64 n
                                  (tensor/mget a idx idx))))))

(defn det
  "Matrix determinant. Returns a double for real matrices, a scalar
   ComplexTensor for complex matrices."
  [a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (let [[re im] (ejml/zdet (ejml/ct->zmat a))]
      (cx/complex re im))
    (ejml/ddet (bt/tensor->dmat a))))

(defn norm
  "Frobenius norm."
  ^double [a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (ejml/znorm-f (ejml/ct->zmat a))
    (Math/sqrt (double (dfn/sum (dfn/* a a))))))

;; ---------------------------------------------------------------------------
;; Inverse and solve
;; ---------------------------------------------------------------------------

(defn invert
  "Matrix inverse. Returns nil if singular."
  [a]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (when-let [inv (ejml/zinvert (ejml/ct->zmat a))]
      (ejml/zmat->ct inv))
    (when-let [inv (ejml/dinvert (bt/tensor->dmat a))]
      (bt/dmat->tensor inv))))

(defn solve
  "Solve A * X = B for X. Returns nil if singular.
   A is [n n], B is [n m]. Returns X as tensor or ComplexTensor."
  [a b]
  (if (instance? scicloj.basis.impl.complex.ComplexTensor a)
    (let [za (ejml/ct->zmat a)
          zb (ejml/ct->zmat b)]
      (when-let [zx (ejml/zsolve za zb)]
        (ejml/zmat->ct zx)))
    (let [da (bt/tensor->dmat a)
          db (bt/tensor->dmat b)]
      (when-let [x (ejml/dsolve da db)]
        (bt/dmat->tensor x)))))

;; ---------------------------------------------------------------------------
;; Decompositions
;; ---------------------------------------------------------------------------

(defn eigen
  "Eigendecomposition. Returns a map:
   :eigenvalues  — vector of [re im] pairs
   :eigenvectors — vector of column eigenvectors as tensors (or nil)"
  [a]
  (let [result (ejml/deig (bt/tensor->dmat a))]
    (when result
      (cond-> {:eigenvalues (:eigenvalues result)}
        (:eigenvectors result)
        (assoc :eigenvectors
               (mapv (fn [ev]
                       (when ev (bt/dmat->tensor ev)))
                     (:eigenvectors result)))))))

(defn svd
  "Singular value decomposition: A = U * diag(S) * V^T.
   Returns a map:
   :U  — left singular vectors as tensor
   :S  — singular values as vector of doubles
   :Vt — right singular vectors (transposed) as tensor"
  [a]
  (let [result (ejml/dsvd (bt/tensor->dmat a))]
    (when result
      {:U (bt/dmat->tensor (:U result))
       :S (:S result)
       :Vt (bt/dmat->tensor (:Vt result))})))

(defn qr
  "QR decomposition: A = Q * R.
   Returns a map with :Q and :R as tensors."
  [a]
  (let [result (ejml/dqr (bt/tensor->dmat a))]
    (when result
      {:Q (bt/dmat->tensor (:Q result))
       :R (bt/dmat->tensor (:R result))})))

(defn cholesky
  "Cholesky decomposition: A = L * L^T (for symmetric positive definite A).
   Returns the lower-triangular L as a tensor, or nil if A is not SPD."
  [a]
  (when-let [L (ejml/dcholesky (bt/tensor->dmat a))]
    (bt/dmat->tensor L)))
