(ns scicloj.lalinea.linalg
  "Linear algebra: arithmetic, decompositions, solve, and related operations.

   All operations accept RealTensors or ComplexTensors and return
   the appropriate type. EJML is used internally for computation,
   with zero-copy conversion.

   For tensor construction and structural operations, see
   `scicloj.lalinea.tensor`."

  (:require [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.ejml :as ejml]
            [scicloj.lalinea.tape :as tape]
            [scicloj.lalinea.tensor :as t]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn])
  (:import [java.util Arrays]
           [org.ejml.data DMatrixRMaj ZMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; Matrix multiply
;; ---------------------------------------------------------------------------

(defn mmul
  "Matrix multiply. Accepts dtype-next tensors or ComplexTensors.

   For real matrices: $C = A B$.
   For complex matrices: delegates to EJML's ZMatrixRMaj multiply."
  [a b]
  (tape/record! :la/mmul [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (let [za (ejml/ct->zmat a)
                          zb (ejml/ct->zmat b)
                          zc (ejml/zmul za zb)]
                      (ejml/zmat->ct zc))
                    (let [da (ejml/tensor->dmat a)
                          db (ejml/tensor->dmat b)
                          dc (ejml/dmul da db)]
                      (rt/->rt (ejml/dmat->tensor dc)))))))

(defn mpow
  "Matrix power $A^k$ for non-negative integer $k$.
   Uses exponentiation by squaring — $O(\\log k)$ multiplications.
   Returns the identity for $k=0$."
  [a k]
  (tape/record! :la/mpow [a k]
                (let [k (long k)]
                  (cond
                    (neg? k) (throw (ex-info "mpow requires non-negative k" {:k k}))
                    (zero? k) (t/eye (first (dtype/shape (rt/ensure-tensor a))))
                    (== k 1) a
                    :else (loop [base a result nil k k]
                            (let [result (if (odd? k)
                                           (if result (mmul result base) base)
                                           result)
                                  k (quot k 2)]
                              (if (zero? k)
                                result
                                (recur (mmul base base) result k))))))))

;; ---------------------------------------------------------------------------
;; Transpose
;; ---------------------------------------------------------------------------

(defn transpose
  "Matrix transpose (real) or conjugate transpose (complex).

   For real matrices: returns a zero-copy strided view sharing the
   same backing data. For complex matrices: $B = A^\\dagger$ (Hermitian adjoint)."
  [a]
  (tape/record! :la/transpose [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (let [za (ejml/ct->zmat a)]
                      (ejml/zmat->ct (ejml/ztranspose-conj za)))
                    (rt/->rt (dtt/transpose a [1 0]))))))

;; ---------------------------------------------------------------------------
;; Arithmetic
;; ---------------------------------------------------------------------------

(defn add
  "Matrix addition."
  [a b]
  (tape/record! :la/add [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (ct/ct-add a b)
                    (rt/->rt (dfn/+ a b))))))

(defn sub
  "Matrix subtraction."
  [a b]
  (tape/record! :la/sub [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (ct/ct-sub a b)
                    (rt/->rt (dfn/- a b))))))

(defn scale
  "Scalar multiply. Returns $\\alpha \\cdot a$."
  [a alpha]
  (tape/record! :la/scale [a alpha]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-scale a alpha)
                    (rt/->rt (dfn/* a (double alpha)))))))

(defn trace
  "Matrix trace. Returns a double for real matrices, a scalar ComplexTensor
   for complex matrices."
  [a]
  (tape/record! :la/trace [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (let [[re im] (ejml/ztrace (ejml/ct->zmat a))]
                      (ct/complex re im))
                    (let [shape (dtype/shape a)
                          n (min (long (first shape)) (long (second shape)))]
                      (double (dfn/sum (dtype/make-reader :float64 n
                                                          (dtt/mget a idx idx)))))))))

(defn det
  "Matrix determinant. Returns a double for real matrices, a scalar
   ComplexTensor for complex matrices."
  [a]
  (tape/record! :la/det [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (let [[re im] (ejml/zdet (ejml/ct->zmat a))]
                      (ct/complex re im))
                    (ejml/ddet (ejml/tensor->dmat a))))))

(defn norm
  "Frobenius norm."
  [a]
  (tape/record! :la/norm [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ejml/znorm-f (ejml/ct->zmat a))
                    (Math/sqrt (double (dfn/sum (dfn/* a a))))))))

(defn dot
  "Inner product. For real vectors: $\\sum u_i v_i$ (returns double).
   For complex vectors: Hermitian $\\langle u,v \\rangle = \\sum u_i \\overline{v_i}$
   (returns scalar ComplexTensor)."
  [u v]
  (tape/record! :la/dot [u v]
                (let [u (rt/ensure-tensor u) v (rt/ensure-tensor v)]
                  (if (ct/complex? u)
                    (ct/ct-dot-conj u v)
                    (double (dfn/sum (dfn/* u v)))))))

(defn dot-conj
  "Hermitian inner product: $\\sum u_i \\overline{v_i}$.
   For real vectors: same as `dot` ($\\sum u_i v_i$).
   For complex vectors: $\\sum u_i \\overline{v_i}$ (returns scalar ComplexTensor)."
  [u v]
  (tape/record! :la/dot-conj [u v]
                (let [u (rt/ensure-tensor u) v (rt/ensure-tensor v)]
                  (if (ct/complex? u)
                    (ct/ct-dot-conj u v)
                    (double (dfn/sum (dfn/* u v)))))))

;; ---------------------------------------------------------------------------
;; Approximate equality
;; ---------------------------------------------------------------------------

(defn close?
  "True when two matrices (or ComplexTensors) are approximately equal:
   $\\|a - b\\|_F < \\mathrm{tol}$. Default tolerance is 1e-10."
  ([a b] (close? a b 1e-10))
  ([a b tol] (< (norm (sub a b)) (double tol))))

(defn close-scalar?
  "True when two scalars are approximately equal:
   $|a - b| < \\mathrm{tol}$. Default tolerance is 1e-10."
  ([a b] (close-scalar? a b 1e-10))
  ([a b tol] (< (Math/abs (- (double a) (double b))) (double tol))))

;; ---------------------------------------------------------------------------
;; Inverse and solve
;; ---------------------------------------------------------------------------

(defn invert
  "Matrix inverse. Returns nil if singular."
  [a]
  (tape/record! :la/invert [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (when-let [inv (ejml/zinvert (ejml/ct->zmat a))]
                      (ejml/zmat->ct inv))
                    (when-let [inv (ejml/dinvert (ejml/tensor->dmat a))]
                      (rt/->rt (ejml/dmat->tensor inv)))))))

(defn solve
  "Solve $AX = B$ for $X$. Returns nil if singular.
   $A$ is `[n n]`, $B$ is `[n m]`. Returns $X$ as a RealTensor or ComplexTensor."
  [a b]
  (tape/record! :la/solve [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (let [za (ejml/ct->zmat a)
                          zb (ejml/ct->zmat b)]
                      (when-let [zx (ejml/zsolve za zb)]
                        (ejml/zmat->ct zx)))
                    (let [da (ejml/tensor->dmat a)
                          db (ejml/tensor->dmat b)]
                      (when-let [x (ejml/dsolve da db)]
                        (rt/->rt (ejml/dmat->tensor x))))))))

;; ---------------------------------------------------------------------------
;; Decompositions
;; ---------------------------------------------------------------------------

(defn eigen
  "Eigendecomposition of a real matrix. Returns a map:
   `:eigenvalues`  — ComplexTensor of shape `[n]` (complex vector)
   `:eigenvectors` — vector of column eigenvectors as real tensors (or nil)

   Use `(el/re (:eigenvalues result))` for the real parts, or
   `real-eigenvalues` for a sorted real tensor."
  [a]
  (tape/record! :la/eigen [a]
                (let [a (rt/ensure-tensor a)
                      result (ejml/deig (ejml/tensor->dmat a))]
                  (when result
                    (let [pairs (:eigenvalues result)
                          n (count pairs)
                          arr (double-array (* 2 n))]
                      (dotimes [i n]
                        (let [[re im] (nth pairs i)]
                          (aset arr (* 2 i) (double re))
                          (aset arr (inc (* 2 i)) (double im))))
                      (cond-> {:eigenvalues (ct/complex-tensor
                                             (dtt/reshape (dtt/ensure-tensor arr) [n 2]))}
                        (:eigenvectors result)
                        (assoc :eigenvectors
                               (mapv (fn [ev]
                                       (when ev (rt/->rt (ejml/dmat->tensor ev))))
                                     (:eigenvectors result)))))))))

(defn real-eigenvalues
  "Sorted real eigenvalues of a symmetric/Hermitian matrix.
   Returns a real `[n]` tensor, sorted ascending."
  [a]
  (let [evals (:eigenvalues (eigen a))
        arr (dtype/->double-array (ct/re evals))]
    (Arrays/sort arr)
    (rt/->rt (dtt/ensure-tensor arr))))

(defn svd
  "Singular value decomposition of a real matrix: $A = U \\Sigma V^T$.
   Returns a map with `:U`, `:S` (singular values), `:Vt`."
  [a]
  (tape/record! :la/svd [a]
                (let [a (rt/ensure-tensor a)
                      result (ejml/dsvd (ejml/tensor->dmat a))]
                  (when result
                    {:U (rt/->rt (ejml/dmat->tensor (:U result)))
                     :S (rt/->rt (:S result))
                     :Vt (rt/->rt (ejml/dmat->tensor (:Vt result)))}))))

(defn qr
  "QR decomposition: $A = QR$.
   Returns a map with `:Q` and `:R`."
  [a]
  (tape/record! :la/qr [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (let [result (ejml/zqr (ejml/ct->zmat a))]
                      (when result
                        {:Q (ejml/zmat->ct (:Q result))
                         :R (ejml/zmat->ct (:R result))}))
                    (let [result (ejml/dqr (ejml/tensor->dmat a))]
                      (when result
                        {:Q (rt/->rt (ejml/dmat->tensor (:Q result)))
                         :R (rt/->rt (ejml/dmat->tensor (:R result)))}))))))

(defn cholesky
  "Cholesky decomposition: $A = LL^T$ (real) or $A = LL^\\dagger$ (complex).
   Returns the lower-triangular $L$, or nil if not SPD/HPD."
  [a]
  (tape/record! :la/cholesky [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (when-let [L (ejml/zcholesky (ejml/ct->zmat a))]
                      (ejml/zmat->ct L))
                    (when-let [L (ejml/dcholesky (ejml/tensor->dmat a))]
                      (rt/->rt (ejml/dmat->tensor L)))))))

;; ---------------------------------------------------------------------------
;; SVD-based convenience wrappers (real-only)
;; ---------------------------------------------------------------------------

(defn- sorted-svd
  "SVD with singular values sorted in descending order."
  [a]
  (when-let [{:keys [U S Vt]} (svd a)]
    (let [n (count S)
          order (vec (sort-by (fn [i] (- (double (S i)))) (range n)))]
      (if (= order (vec (range n)))
        {:U U :S S :Vt Vt}
        {:U  (t/submatrix U :all order)
         :S  (rt/->rt (dtt/ensure-tensor (dtype/make-container :float64 (mapv #(S %) order))))
         :Vt (t/submatrix Vt order :all)}))))

(defn rank
  "Matrix rank: number of singular values above `tol` (default 1e-10)."
  ([a] (rank a 1e-10))
  ([a tol]
   (let [sv (:S (sorted-svd a))]
     (long (dfn/sum (dfn/> sv (double tol)))))))

(defn condition-number
  "Condition number $\\kappa(A) = \\sigma_{\\max} / \\sigma_{\\min}$ from the SVD."
  [a]
  (let [sv (:S (sorted-svd a))]
    (/ (double (first sv))
       (double (sv (dec (count sv)))))))

(defn pinv
  "Moore-Penrose pseudoinverse via SVD: $V \\Sigma^{-1} U^T$.
   Singular values below `tol` (default 1e-10) are treated as zero."
  ([a] (pinv a 1e-10))
  ([a tol]
   (let [{:keys [U S Vt]} (sorted-svd a)
         r (long (dfn/sum (dfn/> S (double tol))))
         U-thin (t/submatrix U :all (range r))
         Vt-thin (t/submatrix Vt (range r) :all)
         S-inv (t/diag (mapv #(/ 1.0 (double %)) (take r S)))]
     (mmul (transpose Vt-thin) (mmul S-inv (transpose U-thin))))))

(defn lstsq
  "Least-squares solve: minimise $\\|Ax - b\\|_2$.
   Returns a map with `:x`, `:residuals` ($\\|Ax-b\\|^2$),
   and `:rank`."
  ([a b] (lstsq a b 1e-10))
  ([a b tol]
   (let [x (mmul (pinv a tol) b)
         residual (sub (mmul a x) b)
         r (rank a tol)]
     {:x x
      :residuals (double (dfn/sum (dfn/* (rt/ensure-tensor residual) (rt/ensure-tensor residual))))
      :rank r})))

(defn null-space
  "Null space basis: columns of $V$ corresponding to singular values $\\approx 0$.
   Returns a matrix whose columns span $\\mathrm{Null}(A)$, or nil if full rank."
  ([a] (null-space a 1e-10))
  ([a tol]
   (let [{:keys [S Vt]} (sorted-svd a)
         n (second (dtype/shape (rt/ensure-tensor a)))
         r (long (dfn/sum (dfn/> S (double tol))))]
     (when (< r (long n))
       (transpose (t/submatrix Vt (range r n) :all))))))

(defn col-space
  "Column space basis: first $r$ columns of $U$ from the SVD,
   where $r$ is the number of singular values above `tol`."
  ([a] (col-space a 1e-10))
  ([a tol]
   (let [{:keys [U S]} (sorted-svd a)
         r (long (dfn/sum (dfn/> S (double tol))))]
     (t/submatrix U :all (range r)))))

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
        (ct/complex? x)     (ct/->tensor x)
        :else               x))

(defn- rewrap-result
  "Re-wrap a result based on the dominant input type."
  [result orig-args ref-shape]
  (if-not (or (dtt/tensor? result) (dtype/reader? result))
    result
    (let [had-real?    (some rt/real-tensor? orig-args)
          had-complex? (and (some ct/complex? orig-args) (not had-real?))
          rshape       (dtype/shape result)]
      (cond
        (and had-complex? (= 2 (last rshape)))
        (ct/wrap-tensor result)

        :else
        (let [r (if (and ref-shape
                         (not= (vec rshape) (vec ref-shape))
                         (= (dtype/ecount result) (apply * ref-shape)))
                  (dtt/reshape result ref-shape)
                  result)]
          (rt/->rt r))))))

(defn- ref-shape-from-args
  "Find the shape of the first tensor-like argument."
  [args]
  (some (fn [a]
          (cond (rt/real-tensor? a) (vec (dtype/shape (rt/->tensor a)))
                (ct/complex? a)     (vec (dtype/shape (ct/->tensor a)))
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
   the result. Pass a Var for tape recording:
     (la/lift #'dfn/sqrt A)   ;; tape-recorded
     (la/lift dfn/sqrt A)     ;; not recorded"
  [f-or-var & args]
  (let [v?  (var? f-or-var)
        f   (if v? @f-or-var f-or-var)
        op  (when v? (var->op-key f-or-var))]
    (apply-lifted f op args)))

(defn lifted
  "Return a lifted version of an external function (curried).

   Pass a Var for tape recording:
     (def my-sqrt (la/lifted #'dfn/sqrt))   ;; tape-aware
     (def my-sqrt (la/lifted dfn/sqrt))     ;; not recorded"
  [f-or-var]
  (let [v?  (var? f-or-var)
        f   (if v? @f-or-var f-or-var)
        op  (when v? (var->op-key f-or-var))]
    (fn [& args]
      (apply-lifted f op args))))
