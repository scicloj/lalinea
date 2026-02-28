(ns scicloj.la-linea.linalg
  "Linear algebra operations on dtype-next tensors, backed by EJML.

   Matrices are dtype-next tensors of shape [r c]. All operations
   accept tensors and return tensors — EJML is used internally for
   computation, with zero-copy conversion.

   For complex matrices, functions accept and return ComplexTensors."
  (:refer-clojure :exclude [abs])
  (:require [scicloj.la-linea.impl.tensor :as bt]
            [scicloj.la-linea.complex :as cx]
            [scicloj.la-linea.impl.ejml :as ejml]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn])
  (:import [java.util Arrays]
           [org.ejml.data DMatrixRMaj ZMatrixRMaj]))

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
;; EJML interop
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: convert a [r c] tensor to an EJML DMatrixRMaj sharing
   the same double[]. Mutations through either view are visible in
   the other.

   Falls back to a copy if the tensor is not backed by a contiguous
   double[] (e.g. a lazy reader or a strided view)."
  ^DMatrixRMaj [tensor]
  (bt/tensor->dmat tensor))

(defn dmat->tensor
  "Zero-copy: convert an EJML DMatrixRMaj to a [r c] tensor sharing
   the same double[]. Mutations through either view are visible in
   the other."
  [^DMatrixRMaj dm]
  (bt/dmat->tensor dm))

(defn complex-tensor->zmat
  "Zero-copy: convert a ComplexTensor to an EJML ZMatrixRMaj sharing
   the same double[]. Mutations through either view are visible in
   the other.

   For a matrix ComplexTensor [r c], creates an r*c ZMatrixRMaj.
   For a vector ComplexTensor [n], creates an n*1 column vector.
   For a scalar ComplexTensor [], creates a 1*1 matrix.

   Falls back to a copy if the tensor is not backed by a contiguous
   double[] (e.g. a lazy ComplexTensor from arithmetic operations)."
  ^ZMatrixRMaj [ct]
  (ejml/ct->zmat ct))

(defn zmat->complex-tensor
  "Zero-copy: convert an EJML ZMatrixRMaj to a ComplexTensor [r c]
   sharing the same double[]. Mutations through either view are
   visible in the other."
  [^ZMatrixRMaj zm]
  (ejml/zmat->ct zm))

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
  (if (cx/complex? m)
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
  (if (cx/complex? a)
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

   For real matrices: returns a zero-copy strided view sharing the
   same backing data. For complex matrices: B = A† (Hermitian adjoint)."
  [a]
  (if (cx/complex? a)
    (let [za (ejml/ct->zmat a)]
      (ejml/zmat->ct (ejml/ztranspose-conj za)))
    (tensor/transpose a [1 0])))

;; ---------------------------------------------------------------------------
;; Addition and subtraction
;; ---------------------------------------------------------------------------

(defn add
  "Matrix addition."
  [a b]
  (if (cx/complex? a)
    (cx/add a b)
    (dfn/+ a b)))

(defn sub
  "Matrix subtraction."
  [a b]
  (if (cx/complex? a)
    (cx/sub a b)
    (dfn/- a b)))

(defn scale
  "Scalar multiply. Returns alpha * a."
  [a alpha]
  (if (cx/complex? a)
    (cx/scale a alpha)
    (dfn/* a (double alpha))))

(defn mul
  "Element-wise multiply (Hadamard product for real, pointwise complex multiply for complex)."
  [a b]
  (if (cx/complex? a)
    (cx/mul a b)
    (dfn/* a b)))

(defn abs
  "Element-wise absolute value (magnitude for complex). Returns a real tensor."
  [a]
  (if (cx/complex? a)
    (cx/abs a)
    (dfn/abs a)))
;; ---------------------------------------------------------------------------
;; Scalar properties
;; ---------------------------------------------------------------------------

(defn trace
  "Matrix trace. Returns a double for real matrices, a scalar ComplexTensor
   for complex matrices."
  [a]
  (if (cx/complex? a)
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
  (if (cx/complex? a)
    (let [[re im] (ejml/zdet (ejml/ct->zmat a))]
      (cx/complex re im))
    (ejml/ddet (bt/tensor->dmat a))))

(defn norm
  "Frobenius norm."
  ^double [a]
  (if (cx/complex? a)
    (ejml/znorm-f (ejml/ct->zmat a))
    (Math/sqrt (double (dfn/sum (dfn/* a a))))))

(defn dot
  "Inner product. For real vectors: Σ u_i v_i (returns double).
   For complex vectors: Hermitian ⟨u,v⟩ = Σ u_i conj(v_i) (returns scalar ComplexTensor)."
  [u v]
  (if (cx/complex? u)
    (cx/dot-conj u v)
    (double (dfn/sum (dfn/* u v)))))
;; ---------------------------------------------------------------------------
;; Approximate equality
;; ---------------------------------------------------------------------------

(defn close?
  "True when two matrices (or ComplexTensors) are approximately equal:
   ‖a − b‖_F < tol. Default tolerance is 1e-10."
  ([a b] (close? a b 1e-10))
  ([a b tol] (< (norm (sub a b)) (double tol))))

(defn close-scalar?
  "True when two scalars are approximately equal:
   |a − b| < tol. Default tolerance is 1e-10."
  ([a b] (close-scalar? a b 1e-10))
  ([a b tol] (< (Math/abs (- (double a) (double b))) (double tol))))

;; ---------------------------------------------------------------------------
;; Inverse and solve
;; ---------------------------------------------------------------------------

(defn invert
  "Matrix inverse. Returns nil if singular."
  [a]
  (if (cx/complex? a)
    (when-let [inv (ejml/zinvert (ejml/ct->zmat a))]
      (ejml/zmat->ct inv))
    (when-let [inv (ejml/dinvert (bt/tensor->dmat a))]
      (bt/dmat->tensor inv))))

(defn solve
  "Solve A * X = B for X. Returns nil if singular.
   A is [n n], B is [n m]. Returns X as tensor or ComplexTensor."
  [a b]
  (if (cx/complex? a)
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
  "Eigendecomposition of a real matrix. Returns a map:
   :eigenvalues  — ComplexTensor of shape [n] (complex vector of eigenvalues)
   :eigenvectors — vector of column eigenvectors as real tensors (or nil)

   Eigenvalues are returned as a ComplexTensor even for real-eigenvalue
   matrices. Use `(cx/re (:eigenvalues result))` for the real parts, or
   `real-eigenvalues` for a sorted real tensor.

   Currently accepts real matrices only (EJML limitation)."
  [a]
  (let [result (ejml/deig (bt/tensor->dmat a))]
    (when result
      (let [pairs (:eigenvalues result)
            n (count pairs)
            arr (double-array (* 2 n))]
        (dotimes [i n]
          (let [[re im] (nth pairs i)]
            (aset arr (* 2 i) (double re))
            (aset arr (inc (* 2 i)) (double im))))
        (cond-> {:eigenvalues (cx/complex-tensor
                               (tensor/reshape (tensor/ensure-tensor arr) [n 2]))}
          (:eigenvectors result)
          (assoc :eigenvectors
                 (mapv (fn [ev]
                         (when ev (bt/dmat->tensor ev)))
                       (:eigenvectors result))))))))

(defn real-eigenvalues
  "Sorted real eigenvalues of a symmetric/Hermitian matrix.
   Returns a real [n] tensor, sorted ascending.

   Equivalent to extracting real parts from `eigen` and sorting,
   but more concise and avoids intermediate allocations."
  [a]
  (let [evals (:eigenvalues (eigen a))
        arr (dtype/->double-array (cx/re evals))]
    (Arrays/sort arr)
    (tensor/ensure-tensor arr)))

(defn svd
  "Singular value decomposition: A = U * diag(S) * V^T.
   Returns a map:
   :U  — left singular vectors as tensor
   :S  — singular values as 1D tensor
   :Vt — right singular vectors (transposed) as tensor

   Currently accepts real matrices only (EJML limitation)."
  [a]
  (let [result (ejml/dsvd (bt/tensor->dmat a))]
    (when result
      {:U (bt/dmat->tensor (:U result))
       :S (:S result)
       :Vt (bt/dmat->tensor (:Vt result))})))

(defn qr
  "QR decomposition: A = Q * R.
   Returns a map with :Q and :R as tensors (or ComplexTensors)."
  [a]
  (if (cx/complex? a)
    (let [result (ejml/zqr (ejml/ct->zmat a))]
      (when result
        {:Q (ejml/zmat->ct (:Q result))
         :R (ejml/zmat->ct (:R result))}))
    (let [result (ejml/dqr (bt/tensor->dmat a))]
      (when result
        {:Q (bt/dmat->tensor (:Q result))
         :R (bt/dmat->tensor (:R result))}))))

(defn cholesky
  "Cholesky decomposition: A = L * L^T (real) or A = L * L† (complex).
   Returns the lower-triangular L, or nil if not SPD/HPD."
  [a]
  (if (cx/complex? a)
    (when-let [L (ejml/zcholesky (ejml/ct->zmat a))]
      (ejml/zmat->ct L))
    (when-let [L (ejml/dcholesky (bt/tensor->dmat a))]
      (bt/dmat->tensor L))))
