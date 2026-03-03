(ns scicloj.lalinea.impl.ejml
  "EJML operations and interop for real and complex matrices.

   Includes zero-copy conversion between dtype-next tensors and
   EJML types (DMatrixRMaj, ZMatrixRMaj), plus all EJML-backed
   computation: multiply, transpose, solve, decompose, etc."
  (:require [scicloj.lalinea.impl.complex-tensor :as ct]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype])
  (:import [org.ejml.data DMatrixRMaj ZMatrixRMaj Complex_F64]
           [org.ejml.dense.row CommonOps_DDRM MatrixFeatures_DDRM]
           [org.ejml.dense.row CommonOps_ZDRM NormOps_ZDRM MatrixFeatures_ZDRM]
           [org.ejml.dense.row.factory DecompositionFactory_DDRM DecompositionFactory_ZDRM]))
;; ---------------------------------------------------------------------------
;; Zero-copy interop: dtype-next tensor ↔ DMatrixRMaj
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: [r c] tensor -> DMatrixRMaj sharing the same double[].

   The tensor must be rank 2 with :float64 datatype. Contiguous
   tensors share the same array; lazy or strided tensors are copied.
   Mutations through the returned DMatrixRMaj are visible in the
   original for contiguous tensors."
  ^DMatrixRMaj [tensor]
  (let [shape (dtype/shape tensor)
        _ (when-not (= 2 (count shape))
            (throw (ex-info (str "Expected rank-2 tensor, got shape " (vec shape))
                            {:shape (vec shape)})))
        [r c] shape
        arr (dtype/->double-array tensor)
        dm (DMatrixRMaj. (int r) (int c))]
    (.setData dm arr)
    dm))

(defn dmat->tensor
  "Zero-copy: DMatrixRMaj -> [r c] tensor sharing the same double[].

   Mutations through either view are visible in the other."
  [^DMatrixRMaj dm]
  (let [r (.numRows dm)
        c (.numCols dm)
        arr (.data dm)]
    (dtt/reshape (dtt/ensure-tensor arr) [r c])))


;; ===========================================================================
;; Real matrix operations (DMatrixRMaj)
;; ===========================================================================

;; ---------------------------------------------------------------------------
;; Arithmetic
;; ---------------------------------------------------------------------------

(defn dmul
  "Real matrix multiply: C = A * B. Returns a new DMatrixRMaj."
  ^DMatrixRMaj [^DMatrixRMaj a ^DMatrixRMaj b]
  (let [c (DMatrixRMaj. (.numRows a) (.numCols b))]
    (CommonOps_DDRM/mult a b c)
    c))

(defn dtranspose
  "Matrix transpose: B = A^T. Returns a new DMatrixRMaj."
  ^DMatrixRMaj [^DMatrixRMaj a]
  (let [b (DMatrixRMaj. (.numCols a) (.numRows a))]
    (CommonOps_DDRM/transpose a b)
    b))

;; ---------------------------------------------------------------------------
;; Scalar queries
;; ---------------------------------------------------------------------------

(defn ddet
  "Matrix determinant."
  ^double [^DMatrixRMaj a]
  (CommonOps_DDRM/det a))

;; ---------------------------------------------------------------------------
;; Inverse and solve
;; ---------------------------------------------------------------------------

(defn dinvert
  "Matrix inverse. Returns a new DMatrixRMaj, or nil if singular."
  [^DMatrixRMaj a]
  (let [inv (DMatrixRMaj. (.numRows a) (.numCols a))]
    (when (and (CommonOps_DDRM/invert a inv)
               (not (MatrixFeatures_DDRM/hasUncountable inv)))
      inv)))

(defn dsolve
  "Solve A * X = B for X. Returns a new DMatrixRMaj, or nil if singular.
   A is [n n], B is [n m], result X is [n m]."
  [^DMatrixRMaj a ^DMatrixRMaj b]
  (let [x (DMatrixRMaj. (.numCols a) (.numCols b))]
    (when (and (CommonOps_DDRM/solve a b x)
               (not (MatrixFeatures_DDRM/hasUncountable x)))
      x)))

;; ---------------------------------------------------------------------------
;; Decompositions
;; ---------------------------------------------------------------------------

(defn deig
  "Eigendecomposition of a real matrix.
   Returns a map with :eigenvalues (vector of [re im] pairs)
   and :eigenvectors (when available, as DMatrixRMaj or nil)."
  [^DMatrixRMaj a]
  (let [n (.numRows a)
        eig (DecompositionFactory_DDRM/eig n true)]
    (when (.decompose eig a)
      (let [num (.getNumberOfEigenvalues eig)
            eigenvalues (mapv (fn [i]
                                (let [^Complex_F64 ev (.getEigenvalue eig i)]
                                  [(.real ev) (.imaginary ev)]))
                              (range num))
            eigenvectors (try
                           (mapv (fn [i] (.getEigenVector eig i))
                                 (range num))
                           (catch Exception _ nil))]
        {:eigenvalues eigenvalues
         :eigenvectors eigenvectors}))))

(defn dsvd
  "Singular value decomposition: A = U * S * V^T.
   Returns a map with :U, :S (singular values as 1D tensor), :Vt."
  [^DMatrixRMaj a]
  (let [svd (DecompositionFactory_DDRM/svd (.numRows a) (.numCols a) true true false)]
    (when (.decompose svd (.copy a))
      (let [U (.getU svd nil false)
            Vt (.getV svd nil true)
            S (.getSingularValues svd)]
        {:U U
         :S (dtt/ensure-tensor S)
         :Vt Vt}))))

(defn dqr
  "QR decomposition: A = Q * R.
   Returns a map with :Q and :R as DMatrixRMaj."
  [^DMatrixRMaj a]
  (let [qr (DecompositionFactory_DDRM/qr (.numRows a) (.numCols a))]
    (when (.decompose qr (.copy a))
      {:Q (.getQ qr nil false)
       :R (.getR qr nil false)})))

(defn dcholesky
  "Cholesky decomposition: A = L * L^T (for symmetric positive definite A).
   Returns the lower-triangular L, or nil if A is not SPD."
  [^DMatrixRMaj a]
  (let [chol (DecompositionFactory_DDRM/chol (.numRows a) true)]
    (when (.decompose chol (.copy a))
      (.getT chol nil))))

;; ===========================================================================
;; Complex matrix operations (ZMatrixRMaj)
;; ===========================================================================

;; ---------------------------------------------------------------------------
;; Zero-copy conversion
;; ---------------------------------------------------------------------------

(defn ct->zmat
  "Zero-copy: ComplexTensor -> ZMatrixRMaj sharing the same double[].

   For a matrix ComplexTensor [r c], creates an r×c ZMatrixRMaj.
   For a vector ComplexTensor [n], creates an n×1 column vector.
   For a scalar ComplexTensor [], creates a 1×1 matrix."
  ^ZMatrixRMaj [ct]
  (let [shape (ct/complex-shape ct)
        [r c] (case (count shape)
                0 [1 1]
                1 [(first shape) 1]
                shape)
        arr (ct/->double-array ct)
        zm (ZMatrixRMaj. (int r) (int c))]
    (.setData zm arr)
    zm))

(defn zmat->ct
  "Zero-copy: ZMatrixRMaj -> ComplexTensor [r c] sharing the same double[]."
  [^ZMatrixRMaj zm]
  (let [r (.numRows zm)
        c (.numCols zm)
        arr (.data zm)]
    (ct/complex-tensor (dtt/reshape (dtt/ensure-tensor arr) [r c 2]))))

;; ---------------------------------------------------------------------------
;; Complex matrix arithmetic
;; ---------------------------------------------------------------------------

(defn zmul
  "Complex matrix multiply: C = A * B."
  ^ZMatrixRMaj [^ZMatrixRMaj a ^ZMatrixRMaj b]
  (let [c (ZMatrixRMaj. (.numRows a) (.numCols b))]
    (CommonOps_ZDRM/mult a b c)
    c))

(defn ztranspose-conj
  "Conjugate transpose (Hermitian adjoint): B = A†."
  ^ZMatrixRMaj [^ZMatrixRMaj a]
  (CommonOps_ZDRM/transposeConjugate a nil))

(defn ztrace
  "Complex trace. Returns [re im]."
  [^ZMatrixRMaj a]
  (let [^Complex_F64 c (CommonOps_ZDRM/trace a nil)]
    [(.real c) (.imaginary c)]))

(defn zdet
  "Complex determinant. Returns [re im]."
  [^ZMatrixRMaj a]
  (let [^Complex_F64 c (CommonOps_ZDRM/det a)]
    [(.real c) (.imaginary c)]))

(defn znorm-f
  "Frobenius norm of a complex matrix."
  ^double [^ZMatrixRMaj a]
  (NormOps_ZDRM/normF a))

(defn zinvert
  "Complex matrix inverse. Returns nil if singular."
  [^ZMatrixRMaj a]
  (let [inv (ZMatrixRMaj. (.numRows a) (.numCols a))]
    (when (and (CommonOps_ZDRM/invert a inv)
               (not (MatrixFeatures_ZDRM/hasUncountable inv)))
      inv)))

(defn zsolve
  "Complex solve: A * X = B for X. Returns ZMatrixRMaj or nil if singular.
   A is [n n], B is [n m], result X is [n m]."
  [^ZMatrixRMaj a ^ZMatrixRMaj b]
  (let [x (ZMatrixRMaj. (.numCols a) (.numCols b))]
    (when (and (CommonOps_ZDRM/solve a b x)
               (not (MatrixFeatures_ZDRM/hasUncountable x)))
      x)))

;; ---------------------------------------------------------------------------
;; Complex decompositions
;; ---------------------------------------------------------------------------

(defn zqr
  "Complex QR decomposition: A = Q * R.
   Returns a map with :Q and :R as ZMatrixRMaj."
  [^ZMatrixRMaj a]
  (let [qr (DecompositionFactory_ZDRM/qr (.numRows a) (.numCols a))]
    (when (.decompose qr (.copy a))
      {:Q (.getQ qr nil false)
       :R (.getR qr nil false)})))

(defn zcholesky
  "Complex Cholesky decomposition: A = L * L† (for Hermitian positive definite A).
   Returns the lower-triangular L as ZMatrixRMaj, or nil if not HPD."
  [^ZMatrixRMaj a]
  (let [chol (DecompositionFactory_ZDRM/chol (.numRows a) true)]
    (when (.decompose chol (.copy a))
      (.getT chol nil))))
