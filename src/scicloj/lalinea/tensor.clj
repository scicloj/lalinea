(ns scicloj.lalinea.tensor
  "Tensor construction, structural operations, and low-level buffer access.

   This namespace provides La Linea's tensor layer: creating real and
   complex matrices and vectors, reshaping, slicing, cloning, and bridging
   to raw dtype-next buffers and EJML matrices.

   Real construction functions return RealTensors (printed as `#la/R`).
   Complex construction functions return ComplexTensors (printed as `#la/C`).
   Structural operations preserve the RealTensor/ComplexTensor type."
  (:refer-clojure :exclude [flatten reshape select clone])
  (:require [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.impl.buffer :as buf]
            [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.ejml :as ejml]
            [scicloj.lalinea.tape :as tape]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.protocols :as dtype-proto]
            [scicloj.lalinea.impl.print])
  (:import [org.ejml.data DMatrixRMaj ZMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; Real construction
;; ---------------------------------------------------------------------------

(defn matrix
  "Create a matrix (rank-2 tensor) from nested sequences.

   ```clojure
   (matrix [[1 2] [3 4]])
   ```"
  [rows]
  (tape/record! :t/matrix [rows]
                (if (rt/real-tensor? rows)
                  rows
                  (rt/->rt (rt/matrix rows)))))

(defn eye
  "Identity matrix of size $n \\times n$."
  [n]
  (tape/record! :t/eye [n]
                (rt/->rt (rt/eye n))))

(defn zeros
  "Zero matrix of size $r \\times c$."
  [r c]
  (tape/record! :t/zeros [r c]
                (rt/->rt (rt/zeros r c))))

(defn ones
  "Matrix of ones, size $r \\times c$."
  [r c]
  (tape/record! :t/ones [r c]
                (rt/->rt (dtt/compute-tensor [r c] (fn [& _] 1.0) :float64))))

(defn column
  "Create a column vector (shape `[n 1]`) from a sequence."
  [xs]
  (tape/record! :t/column [xs]
                (rt/->rt (rt/col-vector xs))))

(defn row
  "Create a row vector (shape `[1 n]`) from a sequence."
  [xs]
  (tape/record! :t/row [xs]
                (rt/->rt (rt/row-vector xs))))

(defn diag
  "Diagonal operations:
   - Given a 2D matrix: extract the main diagonal as a 1D tensor.
   - Given a 1D sequence/tensor: create a diagonal matrix."
  [a]
  (tape/record! :t/diag [a]
                (let [a (rt/ensure-tensor a)
                      ndims (count (dtype/shape a))]
                  (if (= 2 ndims)
                    (let [n (long (apply min (dtype/shape a)))]
                      (rt/->rt (dtype/clone
                                (dtype/make-reader :float64 n
                                                   (double (dtt/mget a idx idx))))))
                    (let [v (dtype/->reader (dtype/make-container :float64 a))
                          n (count v)]
                      (rt/->rt (dtt/compute-tensor
                                [n n]
                                (fn [i j] (if (== i j) (double (v i)) 0.0))
                                :float64)))))))

(defn compute-matrix
  "Build an `[r c]` matrix from a function of row and column indices.
   `f` takes two longs (row, col) and returns a double."
  [r c f]
  (tape/record! :t/compute-matrix [r c f]
                (rt/->rt (dtt/compute-tensor [(long r) (long c)]
                                             (fn [i j] (double (f i j)))
                                             :float64))))

;; ---------------------------------------------------------------------------
;; Complex construction
;; ---------------------------------------------------------------------------

(defn complex-tensor
  "Create a ComplexTensor.

   Arities:
     (complex-tensor tensor)       — wrap an existing tensor with last dim = 2
     (complex-tensor re-data im-data) — from separate real and imaginary parts

   re-data and im-data can be: double arrays, seqs, dtype readers, or tensors.
   They must have the same shape."
  ([tensor-or-re]
   (tape/record! :t/complex-tensor [tensor-or-re]
                 (ct/complex-tensor tensor-or-re)))
  ([re-data im-data]
   (tape/record! :t/complex-tensor [re-data im-data]
                 (ct/complex-tensor re-data im-data))))

(defn complex-tensor-real
  "Create a ComplexTensor from real data only (imaginary parts = 0)."
  [re-data]
  (tape/record! :t/complex-tensor-real [re-data]
                (ct/complex-tensor-real re-data)))

(defn complex
  "Create a scalar ComplexTensor from real and imaginary parts."
  [re im]
  (ct/complex re im))

;; ---------------------------------------------------------------------------
;; Complex type API
;; ---------------------------------------------------------------------------

(def complex?
  "True if x is a ComplexTensor."
  ct/complex?)

(def scalar?
  "True if this ComplexTensor represents a scalar complex number."
  ct/scalar?)

(def complex-shape
  "The complex shape (underlying shape without trailing 2)."
  ct/complex-shape)

(def wrap-tensor
  "Wrap a raw interleaved [... 2] tensor as a ComplexTensor.
   Inverse of `->tensor` on ComplexTensors."
  ct/wrap-tensor)

;; ---------------------------------------------------------------------------
;; Structural operations
;; ---------------------------------------------------------------------------

(defn shape
  "Return the logical shape of a tensor as a vector.
   For ComplexTensors, returns the complex shape
   (without the trailing interleaved dimension)."
  [a]
  (buf/tensor-shape a))

(defn reshape
  "Reshape a tensor to a new shape. Zero-copy when possible.
   Returns a RealTensor for real input, ComplexTensor for complex."
  [a new-shape]
  (tape/record! :t/reshape [a new-shape]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/wrap-tensor (dtt/reshape (ct/->tensor a)
                                                 (conj (vec new-shape) 2)))
                    (rt/->rt (dtt/reshape a new-shape))))))

(defn select
  "Slice a tensor along dimensions. Each argument selects along one axis:
   an index (long), `:all`, or a sequence of indices.
   Returns a RealTensor for real input, ComplexTensor for complex."
  [a & args]
  (tape/record! :t/select (into [a] args)
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/complex-tensor
                     (dtype/clone (apply dtt/select (ct/->tensor a)
                                         (concat args [:all]))))
                    (let [result (apply dtt/select a args)]
                      (if (number? result)
                        result
                        (rt/->rt result)))))))

(defn submatrix
  "Extract a contiguous submatrix. `rows` and `cols` can be `:all` or a range.

   For ComplexTensors, operates on the complex dimensions and preserves
   the ComplexTensor type."
  [m rows cols]
  (tape/record! :t/submatrix [m rows cols]
                (let [m (rt/ensure-tensor m)]
                  (if (ct/complex? m)
                    (ct/complex-tensor
                     (dtype/clone (dtt/select (ct/->tensor m) rows cols :all)))
                    (rt/->rt (dtype/clone (dtt/select m rows cols)))))))

(defn flatten
  "Reshape a tensor to 1D, preserving element order.
   Column vectors `[n 1]` become `[n]`, matrices `[r c]` become `[r*c]`.
   For ComplexTensors, flattens the logical dimensions (trailing 2 preserved)."
  [a]
  (tape/record! :t/flatten [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (let [raw (ct/->tensor a)
                          n (apply * (ct/complex-shape a))]
                      (ct/wrap-tensor (dtt/reshape raw [n 2])))
                    (let [n (dtype/ecount a)]
                      (rt/->rt (dtt/reshape a [n])))))))

(defn hstack
  "Assemble a matrix by placing column vectors side by side.
   Each element should be a column vector `[n 1]` or a 1D tensor `[n]`.
   Returns an `[n k]` matrix where k is the number of columns."
  [cols]
  (tape/record! :t/hstack [cols]
                (let [ts (mapv rt/ensure-tensor cols)
                      n (long (first (dtype/shape (first ts))))
                      k (count ts)
                      buf (dtype/make-container :float64 (* n k))
                      out (dtt/reshape buf [n k])]
                  (dotimes [j k]
                    (let [reader (dtype/->reader (nth ts j) :float64)]
                      (dotimes [i n]
                        (dtt/mset! out i j (double (reader i))))))
                  (rt/->rt out))))

(defn reduce-axis
  "Reduce a tensor along an axis.

   `reduce-fn` is applied to each slice along the given axis
   (e.g. `elem/sum`, `elem/reduce-max`).

   For a 2D matrix: axis 0 reduces across rows (one result per column),
   axis 1 reduces across columns (one result per row).

   Returns a RealTensor for real input, ComplexTensor for complex input."
  [a reduce-fn axis]
  (tape/record! :t/reduce-axis [a reduce-fn axis]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/wrap-tensor
                     (dtt/reduce-axis (ct/->tensor a) reduce-fn (int axis)))
                    (rt/->rt (dtt/reduce-axis a reduce-fn (int axis)))))))

;; ---------------------------------------------------------------------------
;; Materialization, cloning, and mutation
;; ---------------------------------------------------------------------------

(defn concrete?
  "True if the tensor is backed by a contiguous array (not a lazy reader chain)."
  [a]
  (let [raw (rt/ensure-tensor a)]
    (dtype-proto/convertible-to-array-buffer?
     (dtype/->buffer (if (ct/complex? raw) (ct/->tensor raw) raw)))))

(defn clone
  "Deep-copy a tensor into a fresh contiguous array.  Always allocates,
   even when the input is already concrete.  Use `materialize` when you
   only need to ensure the result is concrete without a redundant copy."
  [a]
  (tape/record! :t/clone [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/wrap-tensor (dtype/clone (ct/->tensor a)))
                    (rt/->rt (dtype/clone a))))))

(defn materialize
  "Ensure a tensor is backed by a contiguous array.
   Returns the input unchanged if already concrete; clones if lazy.
   Use `clone` when you need a guaranteed independent copy."
  [a]
  (if (concrete? a) a (clone a)))

(defn mset!
  "Mutate a tensor element in place. Takes a tensor, indices, and a value.
   For RealTensors, unwraps to the backing tensor before mutation."
  [a & args-and-val]
  (let [raw (rt/ensure-tensor a)]
    (apply dtt/mset! raw args-and-val)
    a))

(defn set-value!
  "Set a value at a flat (linear) index. Mutates in place."
  [a idx val]
  (let [raw (rt/ensure-tensor a)]
    (dtype/set-value! raw idx val)
    a))

;; ---------------------------------------------------------------------------
;; Low-level buffer access
;; ---------------------------------------------------------------------------

(defn ->double-array
  "Get the backing `double[]` of a tensor. Zero-copy when the tensor
   is backed by a contiguous array; copies for subviews or lazy tensors.
   Works for both RealTensors and ComplexTensors."
  ^doubles [a]
  (if (ct/complex? a)
    (ct/->double-array a)
    (dtype/->double-array (rt/ensure-tensor a))))

(defn backing-array
  "Return the shared backing `double[]` of a tensor,
   or nil if the tensor is lazy or not array-backed.
   Never copies. Useful for checking whether two tensors
   share memory."
  ^doubles [t]
  (buf/backing-array t))

(defn ->reader
  "Get a read-only indexed view of a tensor's elements."
  ([a] (dtype/->reader (rt/ensure-tensor a)))
  ([a datatype] (dtype/->reader (rt/ensure-tensor a) datatype)))

(defn compute-tensor
  "Create a tensor by calling a function at each index.
   Returns a RealTensor for 1D and 2D shapes, raw tensor for 3D+.
   Use `materialize` or `clone` to force evaluation when needed."
  [shape-vec f datatype]
  (tape/record! :t/compute-tensor [shape-vec f datatype]
                (let [result (dtt/compute-tensor shape-vec f datatype)]
                  (if (<= (count shape-vec) 2)
                    (rt/->rt result)
                    result))))

(defmacro make-reader
  "Create a lazy functional buffer that computes values on the fly.
   `idx` is bound in the body expression. Returns a raw dtype-next
   reader (not a RealTensor) for compatibility with dataset pipelines.

   ```clojure
   (t/make-reader :float64 10 (* 2.0 idx))
   ```"
  [datatype n body]
  `(tech.v3.datatype/make-reader ~datatype ~n ~body))

(defn make-container
  "Create a mutable buffer of the given dtype and size (or from data).
   Returns a RealTensor wrapping a concrete buffer."
  [datatype n-or-data]
  (rt/->rt (dtype/make-container datatype n-or-data)))

(defn elemwise-cast
  "Cast a tensor or buffer to a different element type. Returns a raw dtype-next buffer."
  [a datatype]
  (dtype/elemwise-cast (rt/ensure-tensor a) datatype))

(defn array-buffer
  "Return the array buffer backing a tensor, or nil if none exists
   (e.g. for lazy or strided tensors)."
  [a]
  (dtype/as-array-buffer (rt/ensure-tensor a)))

;; ---------------------------------------------------------------------------
;; Type conversion
;; ---------------------------------------------------------------------------

(defn ->real-tensor
  "Wrap a dtype-next tensor in a RealTensor."
  [t]
  (rt/->real-tensor t))

(defn ->tensor
  "Extract the underlying dtype-next tensor from a RealTensor or ComplexTensor.
   Returns x unchanged if it is neither."
  [x]
  (cond
    (rt/real-tensor? x) (rt/->tensor x)
    (ct/complex? x)     (ct/->tensor x)
    :else               x))

(defn real-tensor?
  "True if x is a RealTensor."
  [x]
  (rt/real-tensor? x))

;; ---------------------------------------------------------------------------
;; EJML interop
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: convert a `[r c]` tensor to an EJML `DMatrixRMaj` sharing
   the same `double[]`. Falls back to a copy for lazy/strided tensors."
  ^DMatrixRMaj [tensor]
  (ejml/tensor->dmat (rt/ensure-tensor tensor)))

(defn dmat->tensor
  "Zero-copy: convert an EJML `DMatrixRMaj` to a raw `[r c]` dtype-next tensor
   sharing the same `double[]`. Wrap with `->real-tensor` if needed."
  [^DMatrixRMaj dm]
  (ejml/dmat->tensor dm))

(defn complex-tensor->zmat
  "Zero-copy: convert a ComplexTensor to an EJML `ZMatrixRMaj` sharing
   the same `double[]`. Falls back to a copy for lazy ComplexTensors."
  ^ZMatrixRMaj [ct]
  (ejml/ct->zmat ct))

(defn zmat->complex-tensor
  "Zero-copy: convert an EJML `ZMatrixRMaj` to a ComplexTensor `[r c]`
   sharing the same `double[]`."
  [^ZMatrixRMaj zm]
  (ejml/zmat->ct zm))

;; ---------------------------------------------------------------------------
;; Tagged literal reader
;; ---------------------------------------------------------------------------

(defn read-real-tensor
  "Reader function for `#la/R` tagged literal.

   Format: `#la/R [:float64 [shape] data]`

   Truncated literals (containing `...`) cannot be read back."
  [[_dtype _shape data]]
  (when (some #{'...} (clojure.core/flatten data))
    (throw (ex-info "Cannot read truncated #la/R literal" {:shape _shape})))
  (rt/->rt (dtt/->tensor data {:datatype :float64})))

(defn- parse-complex-tokens
  "Parse a flat sequence of tokens [re +/- im i ...] into [re im] pairs."
  [tokens]
  (loop [ts (seq tokens) pairs (transient [])]
    (if (nil? ts)
      (persistent! pairs)
      (let [[re-val sign im-val i-sym & rest-ts] ts]
        (when (or (nil? re-val) (nil? sign) (nil? im-val) (nil? i-sym))
          (throw (ex-info "Incomplete complex number in #la/C literal"
                          {:remaining (vec ts)})))
        (when-not (= 'i i-sym)
          (throw (ex-info (str "Expected 'i' symbol, got: " i-sym)
                          {:token i-sym})))
        (let [im-val (double im-val)
              im (case sign
                   + im-val
                   - (- im-val)
                   (throw (ex-info (str "Expected + or -, got: " sign)
                                   {:sign sign})))]
          (recur rest-ts (conj! pairs [(double re-val) im])))))))

(defn read-complex-tensor
  "Reader function for `#la/C` tagged literal.

   Format: `#la/C [:float64 [shape] data]` where data uses
   `re + im i` / `re - im i` notation.

   Scalar: `#la/C [:float64 [] [3.0 + 4.0 i]]`
   Vector: `#la/C [:float64 [2] [1.0 + 2.0 i 3.0 + 4.0 i]]`
   Matrix: `#la/C [:float64 [2 2] [[1.0 + 2.0 i 3.0 + 4.0 i] [5.0 + 6.0 i 7.0 + 8.0 i]]]`"
  [[_dtype shape data]]
  (when (some #{'...} (flatten data))
    (throw (ex-info "Cannot read truncated #la/C literal" {:shape shape})))
  (let [ndims (count shape)]
    (cond
      ;; Scalar: shape [], data = [re + im i]
      (zero? ndims)
      (let [[[r i]] (parse-complex-tokens data)]
        (ct/complex r i))

      ;; Vector: shape [n], data = [re + im i  re + im i ...]
      (= 1 ndims)
      (let [pairs (parse-complex-tokens data)]
        (ct/complex-tensor
         (dtt/ensure-tensor (double-array (mapv first pairs)))
         (dtt/ensure-tensor (double-array (mapv second pairs)))))

      ;; Matrix: shape [r c], data = [[row1-tokens] [row2-tokens] ...]
      :else
      (let [row-pairs (mapv parse-complex-tokens data)
            all-pairs (vec (apply concat row-pairs))
            re-arr (double-array (mapv first all-pairs))
            im-arr (double-array (mapv second all-pairs))]
        (ct/complex-tensor
         (dtt/reshape (dtt/ensure-tensor re-arr) shape)
         (dtt/reshape (dtt/ensure-tensor im-arr) shape))))))
