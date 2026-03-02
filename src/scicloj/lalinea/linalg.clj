(ns scicloj.lalinea.linalg
  "Linear algebra operations on dtype-next tensors, backed by EJML.

   Matrices are dtype-next tensors of shape [r c]. All operations
   accept tensors and return tensors — EJML is used internally for
   computation, with zero-copy conversion.

   For complex matrices, functions accept and return ComplexTensors."
  (:refer-clojure :exclude [abs flatten])
  (:require [scicloj.lalinea.impl.tensor :as bt]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.complex :as cx]
            [scicloj.lalinea.impl.ejml :as ejml]
            [scicloj.lalinea.tape :as tape]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [scicloj.lalinea.impl.print])
  (:import [java.util Arrays]
           [org.ejml.data DMatrixRMaj ZMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; RealTensor helpers
;; ---------------------------------------------------------------------------

(defn- ensure-tensor
  "Unwrap RealTensor to bare tensor; pass through everything else."
  [x]
  (if (rt/real-tensor? x) (rt/->tensor x) x))

(defn- ->rt
  "Wrap a bare tensor result in RealTensor. Returns scalars as-is."
  [t]
  (if (or (nil? t) (number? t))
    t
    (rt/->real-tensor t)))

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
  (tape/record! :la/matrix [rows]
                (if (rt/real-tensor? rows)
                  rows
                  (->rt (bt/matrix rows)))))

(defn eye
  "Identity matrix of size n × n."
  [n]
  (tape/record! :la/eye [n]
                (->rt (bt/eye n))))

(defn zeros
  "Zero matrix of size r × c."
  [r c]
  (tape/record! :la/zeros [r c]
                (->rt (bt/zeros r c))))

(defn ones
  "Matrix of ones, size r × c."
  [r c]
  (->rt (tensor/compute-tensor [r c] (fn [& _] 1.0) :float64)))

(defn diag
  "Create a diagonal matrix from a sequence of diagonal values."
  [values]
  (tape/record! :la/diag [values]
                (->rt (let [v (dtype/->reader (dtype/make-container :float64 values))
                            n (count v)]
                        (tensor/compute-tensor [n n]
                                               (fn [i j] (if (== i j) (double (v i)) 0.0))
                                               :float64)))))

(defn column
  "Create a column vector (shape [n 1]) from a sequence."
  [xs]
  (tape/record! :la/column [xs]
                (->rt (bt/col-vector xs))))

(defn row
  "Create a row vector (shape [1 n]) from a sequence."
  [xs]
  (tape/record! :la/row [xs]
                (->rt (bt/row-vector xs))))

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
  (tape/record! :la/submatrix [m rows cols]
                (let [m (ensure-tensor m)]
                  (if (cx/complex? m)
                    (cx/complex-tensor
                     (dtype/clone (tensor/select (cx/->tensor m) rows cols :all)))
                    (->rt (dtype/clone (tensor/select m rows cols)))))))

;; ---------------------------------------------------------------------------
;; Matrix multiply
;; ---------------------------------------------------------------------------

(defn mmul
  "Matrix multiply. Accepts dtype-next tensors or ComplexTensors.

   For real matrices: C = A * B
   For complex matrices: delegates to EJML's ZMatrixRMaj multiply."
  [a b]
  (tape/record! :la/mmul [a b]
                (let [a (ensure-tensor a) b (ensure-tensor b)]
                  (if (cx/complex? a)
                    (let [za (ejml/ct->zmat a)
                          zb (ejml/ct->zmat b)
                          zc (ejml/zmul za zb)]
                      (ejml/zmat->ct zc))
                    (let [da (bt/tensor->dmat a)
                          db (bt/tensor->dmat b)
                          dc (ejml/dmul da db)]
                      (->rt (bt/dmat->tensor dc)))))))

;; See also: https://www.mathworks.com/help/matlab/ref/mpower.html
(defn mpow
  "Matrix power $A^k$ for non-negative integer k.
   Uses exponentiation by squaring — $O(\\log k)$ multiplications.
   Returns the identity for k=0."
  [a k]
  (let [k (long k)]
    (cond
      (neg? k) (throw (ex-info "mpow requires non-negative k" {:k k}))
      (zero? k) (eye (first (dtype/shape a)))
      (== k 1) a
      :else (loop [base a result nil k k]
              (let [result (if (odd? k)
                             (if result (mmul result base) base)
                             result)
                    k (quot k 2)]
                (if (zero? k)
                  result
                  (recur (mmul base base) result k)))))))

;; ---------------------------------------------------------------------------
;; Transpose
;; ---------------------------------------------------------------------------

(defn transpose
  "Matrix transpose (real) or conjugate transpose (complex).

   For real matrices: returns a zero-copy strided view sharing the
   same backing data. For complex matrices: B = A† (Hermitian adjoint)."
  [a]
  (tape/record! :la/transpose [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (let [za (ejml/ct->zmat a)]
                      (ejml/zmat->ct (ejml/ztranspose-conj za)))
                    (->rt (tensor/transpose a [1 0]))))))

;; ---------------------------------------------------------------------------
;; Addition and subtraction
;; ---------------------------------------------------------------------------

(defn add
  "Matrix addition."
  [a b]
  (tape/record! :la/add [a b]
                (let [a (ensure-tensor a) b (ensure-tensor b)]
                  (if (cx/complex? a)
                    (cx/add a b)
                    (->rt (tensor/reshape (dfn/+ a b) (dtype/shape a)))))))

(defn sub
  "Matrix subtraction."
  [a b]
  (tape/record! :la/sub [a b]
                (let [a (ensure-tensor a) b (ensure-tensor b)]
                  (if (cx/complex? a)
                    (cx/sub a b)
                    (->rt (tensor/reshape (dfn/- a b) (dtype/shape a)))))))

(defn scale
  "Scalar multiply. Returns alpha * a."
  [a alpha]
  (tape/record! :la/scale [a alpha]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/scale a alpha)
                    (->rt (tensor/reshape (dfn/* a (double alpha)) (dtype/shape a)))))))

(defn mul
  "Element-wise multiply (Hadamard product for real, pointwise complex multiply for complex)."
  [a b]
  (tape/record! :la/mul [a b]
                (let [a (ensure-tensor a) b (ensure-tensor b)]
                  (if (cx/complex? a)
                    (cx/mul a b)
                    (->rt (tensor/reshape (dfn/* a b) (dtype/shape a)))))))

(defn abs
  "Element-wise absolute value (magnitude for complex). Returns a real tensor."
  [a]
  (tape/record! :la/abs [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (->rt (cx/abs a))
                    (->rt (tensor/reshape (dfn/abs a) (dtype/shape a)))))))

(defn sq
  "Element-wise square."
  [a]
  (tape/record! :la/sq [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/mul a a)
                    (->rt (tensor/reshape (dfn/* a a) (dtype/shape a)))))))

(defn sum
  "Sum of all elements. Returns a double for real tensors,
   a scalar ComplexTensor for complex."
  [a]
  (tape/record! :la/sum [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/sum a)
                    (double (dfn/sum a))))))

(defn reduce-axis
  "Reduce a tensor along an axis.

   `reduce-fn` is applied to each slice along the given axis
   (e.g. `dfn/sum`, `dfn/reduce-max`).

   For a 2D matrix: axis 0 reduces across rows (one result per column),
   axis 1 reduces across columns (one result per row).

   Returns a RealTensor for real input, ComplexTensor for complex input."
  [a reduce-fn axis]
  (tape/record! :la/reduce-axis [a reduce-fn axis]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/wrap-tensor
                     (tensor/reduce-axis (cx/->tensor a) reduce-fn (int axis)))
                    (->rt (tensor/reduce-axis a reduce-fn (int axis)))))))

(defn flatten
  "Reshape a tensor to 1D, preserving element order.
   Column vectors `[n 1]` become `[n]`, matrices `[r c]` become `[r*c]`.
   For ComplexTensors, flattens the logical dimensions (trailing 2 preserved)."
  [a]
  (let [a (ensure-tensor a)]
    (if (cx/complex? a)
      (let [raw (cx/->tensor a)
            n (apply * (cx/complex-shape a))]
        (cx/wrap-tensor (tensor/reshape raw [n 2])))
      (let [n (dtype/ecount a)]
        (->rt (tensor/reshape a [n]))))))

(defn hstack
  "Assemble a matrix by placing column vectors side by side.
   Each element should be a column vector `[n 1]` or a 1D tensor `[n]`.
   Returns an `[n k]` matrix where k is the number of columns."
  [cols]
  (let [ts (mapv ensure-tensor cols)
        n (long (first (dtype/shape (first ts))))
        k (count ts)
        buf (dtype/make-container :float64 (* n k))
        out (tensor/reshape buf [n k])]
    (dotimes [j k]
      (let [reader (dtype/->reader (nth ts j) :float64)]
        (dotimes [i n]
          (tensor/mset! out i j (double (reader i))))))
    (->rt out)))

;; ---------------------------------------------------------------------------
;; Scalar properties
;; ---------------------------------------------------------------------------

(defn trace
  "Matrix trace. Returns a double for real matrices, a scalar ComplexTensor
   for complex matrices."
  [a]
  (tape/record! :la/trace [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (let [[re im] (ejml/ztrace (ejml/ct->zmat a))]
                      (cx/complex re im))
                    (let [shape (dtype/shape a)
                          n (min (long (first shape)) (long (second shape)))]
                      (double (dfn/sum (dtype/make-reader :float64 n
                                                          (tensor/mget a idx idx)))))))))

(defn det
  "Matrix determinant. Returns a double for real matrices, a scalar
   ComplexTensor for complex matrices."
  [a]
  (tape/record! :la/det [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (let [[re im] (ejml/zdet (ejml/ct->zmat a))]
                      (cx/complex re im))
                    (ejml/ddet (bt/tensor->dmat a))))))

(defn norm
  "Frobenius norm."
  [a]
  (tape/record! :la/norm [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (ejml/znorm-f (ejml/ct->zmat a))
                    (Math/sqrt (double (dfn/sum (dfn/* a a))))))))

(defn dot
  "Inner product. For real vectors: Σ u_i v_i (returns double).
   For complex vectors: Hermitian ⟨u,v⟩ = Σ u_i conj(v_i) (returns scalar ComplexTensor)."
  [u v]
  (let [u (ensure-tensor u) v (ensure-tensor v)]
    (if (cx/complex? u)
      (cx/dot-conj u v)
      (double (dfn/sum (dfn/* u v))))))

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
  (tape/record! :la/invert [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (when-let [inv (ejml/zinvert (ejml/ct->zmat a))]
                      (ejml/zmat->ct inv))
                    (when-let [inv (ejml/dinvert (bt/tensor->dmat a))]
                      (->rt (bt/dmat->tensor inv)))))))

(defn solve
  "Solve A * X = B for X. Returns nil if singular.
   A is [n n], B is [n m]. Returns X as tensor or ComplexTensor."
  [a b]
  (tape/record! :la/solve [a b]
                (let [a (ensure-tensor a) b (ensure-tensor b)]
                  (if (cx/complex? a)
                    (let [za (ejml/ct->zmat a)
                          zb (ejml/ct->zmat b)]
                      (when-let [zx (ejml/zsolve za zb)]
                        (ejml/zmat->ct zx)))
                    (let [da (bt/tensor->dmat a)
                          db (bt/tensor->dmat b)]
                      (when-let [x (ejml/dsolve da db)]
                        (->rt (bt/dmat->tensor x))))))))

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
  (tape/record! :la/eigen [a]
                (let [a (ensure-tensor a)
                      result (ejml/deig (bt/tensor->dmat a))]
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
                                       (when ev (->rt (bt/dmat->tensor ev))))
                                     (:eigenvectors result)))))))))

(defn real-eigenvalues
  "Sorted real eigenvalues of a symmetric/Hermitian matrix.
   Returns a real [n] tensor, sorted ascending.

   Equivalent to extracting real parts from `eigen` and sorting,
   but more concise and avoids intermediate allocations."
  [a]
  (let [evals (:eigenvalues (eigen a))
        arr (dtype/->double-array (cx/re evals))]
    (Arrays/sort arr)
    (->rt (tensor/ensure-tensor arr))))

(defn svd
  "Singular value decomposition: A = U * diag(S) * V^T.
   Returns a map:
   :U  — left singular vectors as tensor
   :S  — singular values as 1D tensor
   :Vt — right singular vectors (transposed) as tensor

   Currently accepts real matrices only (EJML limitation)."
  [a]
  (tape/record! :la/svd [a]
                (let [a (ensure-tensor a)
                      result (ejml/dsvd (bt/tensor->dmat a))]
                  (when result
                    {:U (->rt (bt/dmat->tensor (:U result)))
                     :S (->rt (:S result))
                     :Vt (->rt (bt/dmat->tensor (:Vt result)))}))))

(defn qr
  "QR decomposition: A = Q * R.
   Returns a map with :Q and :R as tensors (or ComplexTensors)."
  [a]
  (tape/record! :la/qr [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (let [result (ejml/zqr (ejml/ct->zmat a))]
                      (when result
                        {:Q (ejml/zmat->ct (:Q result))
                         :R (ejml/zmat->ct (:R result))}))
                    (let [result (ejml/dqr (bt/tensor->dmat a))]
                      (when result
                        {:Q (->rt (bt/dmat->tensor (:Q result)))
                         :R (->rt (bt/dmat->tensor (:R result)))}))))))

(defn cholesky
  "Cholesky decomposition: A = L * L^T (real) or A = L * L† (complex).
   Returns the lower-triangular L, or nil if not SPD/HPD."
  [a]
  (tape/record! :la/cholesky [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (when-let [L (ejml/zcholesky (ejml/ct->zmat a))]
                      (ejml/zmat->ct L))
                    (when-let [L (ejml/dcholesky (bt/tensor->dmat a))]
                      (->rt (bt/dmat->tensor L)))))))

;; ---------------------------------------------------------------------------
;; SVD-based convenience wrappers (real-only)
;; ---------------------------------------------------------------------------

(defn- sorted-svd
  "SVD with singular values sorted in descending order.
   EJML does not guarantee sort order, so we sort and
   permute U/Vt columns/rows accordingly."
  [a]
  (when-let [{:keys [U S Vt]} (svd a)]
    (let [n (count S)
          order (vec (sort-by (fn [i] (- (double (S i)))) (range n)))]
      (if (= order (vec (range n)))
        {:U U :S S :Vt Vt}
        {:U  (submatrix U :all order)
         :S  (->rt (tensor/ensure-tensor (dtype/make-container :float64 (mapv #(S %) order))))
         :Vt (submatrix Vt order :all)}))))

(defn rank
  "Matrix rank: number of singular values above `tol` (default 1e-10)."
  ([a] (rank a 1e-10))
  ([a tol]
   (let [sv (:S (sorted-svd a))]
     (long (dfn/sum (dfn/> sv (double tol)))))))

(defn condition-number
  "Condition number κ(A) = σ_max / σ_min from the SVD."
  [a]
  (let [sv (:S (sorted-svd a))]
    (/ (double (first sv))
       (double (sv (dec (count sv)))))))

(defn pinv
  "Moore-Penrose pseudoinverse via SVD: V Σ⁻¹ Uᵀ.
   Singular values below `tol` (default 1e-10) are treated as zero."
  ([a] (pinv a 1e-10))
  ([a tol]
   (let [{:keys [U S Vt]} (sorted-svd a)
         r (long (dfn/sum (dfn/> S (double tol))))
         U-thin (submatrix U :all (range r))
         Vt-thin (submatrix Vt (range r) :all)
         S-inv (diag (mapv #(/ 1.0 (double %)) (take r S)))]
     (mmul (transpose Vt-thin) (mmul S-inv (transpose U-thin))))))

(defn lstsq
  "Least-squares solve: minimise ‖Ax − b‖₂.
   Returns a map with :x (solution), :residuals (‖Ax−b‖²),
   and :rank (effective rank of A).
   Uses the pseudoinverse via SVD."
  ([a b] (lstsq a b 1e-10))
  ([a b tol]
   (let [x (mmul (pinv a tol) b)
         residual (sub (mmul a x) b)
         r (rank a tol)]
     {:x x
      :residuals (double (dfn/sum (dfn/* residual residual)))
      :rank r})))

(defn null-space
  "Null space basis: columns of V corresponding to singular values ≈ 0.
   Returns a matrix whose columns span Null(A), or nil if the null
   space is trivial (full column rank).
   Singular values below `tol` (default 1e-10) are treated as zero."
  ([a] (null-space a 1e-10))
  ([a tol]
   (let [{:keys [S Vt]} (sorted-svd a)
         n (second (dtype/shape a))
         r (long (dfn/sum (dfn/> S (double tol))))]
     (when (< r (long n))
       (transpose (submatrix Vt (range r n) :all))))))

(defn col-space
  "Column space basis: first r columns of U from the SVD,
   where r is the number of singular values above `tol` (default 1e-10).
   Returns a matrix whose columns span Col(A)."
  ([a] (col-space a 1e-10))
  ([a tol]
   (let [{:keys [U S]} (sorted-svd a)
         r (long (dfn/sum (dfn/> S (double tol))))]
     (submatrix U :all (range r)))))

;; ---------------------------------------------------------------------------
;; Interop: wrap / unwrap
;; ---------------------------------------------------------------------------

(defn ->real-tensor
  "Wrap a dtype-next tensor in a RealTensor."
  [t]
  (rt/->real-tensor t))

(defn ->tensor
  "Extract the underlying dtype-next tensor from a RealTensor.
   Returns x unchanged if it is not a RealTensor."
  [x]
  (ensure-tensor x))

(defn real-tensor?
  "True if x is a RealTensor."
  [x]
  (rt/real-tensor? x))

;; ---------------------------------------------------------------------------
;; Lifting external functions
;; ---------------------------------------------------------------------------

(defn- var->op-key
  "Extract a namespaced keyword from a Var's metadata."
  [v]
  (let [m (meta v)]
    (keyword (str (:ns m)) (str (:name m)))))

(defn- unwrap-arg
  "Unwrap RealTensor or ComplexTensor to bare tensor."
  [x]
  (cond (rt/real-tensor? x) (rt/->tensor x)
        (cx/complex? x)     (cx/->tensor x)
        :else               x))

(defn- rewrap-result
  "Re-wrap a result based on the dominant input type."
  [result orig-args ref-shape]
  (if-not (or (tensor/tensor? result) (dtype/reader? result))
    result
    (let [had-real?    (some rt/real-tensor? orig-args)
          had-complex? (and (some cx/complex? orig-args) (not had-real?))
          rshape       (dtype/shape result)]
      (cond
        (and had-complex? (= 2 (last rshape)))
        (cx/wrap-tensor result)

        :else
        (let [r (if (and ref-shape
                         (not= (vec rshape) (vec ref-shape))
                         (= (dtype/ecount result) (apply * ref-shape)))
                  (tensor/reshape result ref-shape)
                  result)]
          (->rt r))))))

(defn- ref-shape-from-args
  "Find the shape of the first tensor-like argument."
  [args]
  (some (fn [a]
          (cond (rt/real-tensor? a) (vec (dtype/shape (rt/->tensor a)))
                (cx/complex? a)     (vec (dtype/shape (cx/->tensor a)))
                :else               nil))
        args))

(defn- apply-lifted
  "Core logic shared by lift and lifted."
  [f op args]
  (let [ref-sh (ref-shape-from-args args)]
    (if (and op tape/*tape* (not tape/*inside-record*))
      (let [raw (binding [tape/*inside-record* true]
                  (apply f (mapv unwrap-arg args)))
            result (rewrap-result raw args ref-sh)]
        (tape/do-record! op (vec args) result))
      (rewrap-result (apply f (mapv unwrap-arg args)) args ref-sh))))

(defn lift
  "Apply an external function to La Linea tensors (one-shot).

   Unwraps RealTensor/ComplexTensor arguments, applies f, and re-wraps
   the result. Preserves tensor shape.

   Pass a Var for tape recording:
     (la/lift #'dfn/sqrt A)   ;; tape-recorded
     (la/lift dfn/sqrt A)     ;; not recorded"
  [f-or-var & args]
  (let [v?  (var? f-or-var)
        f   (if v? @f-or-var f-or-var)
        op  (when v? (var->op-key f-or-var))]
    (apply-lifted f op args)))

(defn lifted
  "Return a lifted version of an external function (curried).

   The returned function unwraps La Linea types, applies f, and
   re-wraps the result.

   Pass a Var for tape recording:
     (def my-sqrt (la/lifted #'dfn/sqrt))   ;; tape-aware
     (def my-sqrt (la/lifted dfn/sqrt))     ;; not recorded"
  [f-or-var]
  (let [v?  (var? f-or-var)
        f   (if v? @f-or-var f-or-var)
        op  (when v? (var->op-key f-or-var))]
    (fn [& args]
      (apply-lifted f op args))))

;; ---------------------------------------------------------------------------
;; Tagged literal readers
;; ---------------------------------------------------------------------------

(defn read-real-tensor
  "Reader function for `#la/R` tagged literal.

   Format: `#la/R [:float64 [shape] data]`

   Truncated literals (containing `...`) cannot be read back."
  [[_dtype _shape data]]
  (when (some #{'...} (flatten data))
    (throw (ex-info "Cannot read truncated #la/R literal" {:shape _shape})))
  (->rt (tensor/->tensor data {:datatype :float64})))

(defn read-matrix
  "Reader function for `#la/m` tagged literal (deprecated, use `#la/R`)."
  [form]
  (matrix form))

(defn read-column
  "Reader function for `#la/v` tagged literal (deprecated, use `#la/R`)."
  [form]
  (column form))
