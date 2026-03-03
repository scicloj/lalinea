(ns scicloj.lalinea.tensor
  "Tensor construction, structural operations, and low-level buffer access.

   This namespace provides La Linea's tensor layer: creating matrices and
   vectors, reshaping, slicing, cloning, and bridging to raw dtype-next
   buffers and EJML matrices.

   All construction functions return RealTensors (printed as `#la/R`).
   Structural operations preserve the RealTensor/ComplexTensor type."
  (:refer-clojure :exclude [flatten reshape select clone])
  (:require [scicloj.lalinea.impl.tensor :as bt]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.complex :as cx]
            [scicloj.lalinea.impl.ejml :as ejml]
            [scicloj.lalinea.tape :as tape]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [scicloj.lalinea.impl.print])
  (:import [org.ejml.data DMatrixRMaj ZMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; Internal helpers
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
;; Construction
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
                  (->rt (bt/matrix rows)))))

(defn eye
  "Identity matrix of size $n \\times n$."
  [n]
  (tape/record! :t/eye [n]
                (->rt (bt/eye n))))

(defn zeros
  "Zero matrix of size $r \\times c$."
  [r c]
  (tape/record! :t/zeros [r c]
                (->rt (bt/zeros r c))))

(defn ones
  "Matrix of ones, size $r \\times c$."
  [r c]
  (tape/record! :t/ones [r c]
                (->rt (dtt/compute-tensor [r c] (fn [& _] 1.0) :float64))))

(defn column
  "Create a column vector (shape `[n 1]`) from a sequence."
  [xs]
  (tape/record! :t/column [xs]
                (->rt (bt/col-vector xs))))

(defn row
  "Create a row vector (shape `[1 n]`) from a sequence."
  [xs]
  (tape/record! :t/row [xs]
                (->rt (bt/row-vector xs))))

(defn diag
  "Diagonal operations:
   - Given a 2D matrix: extract the main diagonal as a 1D tensor.
   - Given a 1D sequence/tensor: create a diagonal matrix."
  [a]
  (tape/record! :t/diag [a]
                (let [a (ensure-tensor a)
                      ndims (count (dtype/shape a))]
                  (if (= 2 ndims)
                    (let [n (long (apply min (dtype/shape a)))]
                      (->rt (dtype/clone
                             (dtype/make-reader :float64 n
                                                (double (dtt/mget a idx idx))))))
                    (let [v (dtype/->reader (dtype/make-container :float64 a))
                          n (count v)]
                      (->rt (dtt/compute-tensor
                             [n n]
                             (fn [i j] (if (== i j) (double (v i)) 0.0))
                             :float64)))))))

(defn compute-matrix
  "Build an `[r c]` matrix from a function of row and column indices.
   `f` takes two longs (row, col) and returns a double."
  [r c f]
  (tape/record! :t/compute-matrix [r c f]
                (->rt (dtt/compute-tensor [(long r) (long c)]
                                              (fn [i j] (double (f i j)))
                                              :float64))))

;; ---------------------------------------------------------------------------
;; Structural operations
;; ---------------------------------------------------------------------------

(defn shape
  "Return the shape of a tensor as a vector of longs."
  [a]
  (vec (dtype/shape (ensure-tensor a))))

(defn reshape
  "Reshape a tensor to a new shape. Zero-copy when possible.
   Returns a RealTensor for real input, ComplexTensor for complex."
  [a new-shape]
  (tape/record! :t/reshape [a new-shape]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/wrap-tensor (dtt/reshape (cx/->tensor a)
                                                     (conj (vec new-shape) 2)))
                    (->rt (dtt/reshape a new-shape))))))

(defn select
  "Slice a tensor along dimensions. Each argument selects along one axis:
   an index (long), `:all`, or a sequence of indices.
   Returns a RealTensor for real input, ComplexTensor for complex."
  [a & args]
  (tape/record! :t/select (into [a] args)
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/complex-tensor
                     (dtype/clone (apply dtt/select (cx/->tensor a)
                                         (concat args [:all]))))
                    (let [result (apply dtt/select a args)]
                      (if (number? result)
                        result
                        (->rt result)))))))

(defn submatrix
  "Extract a contiguous submatrix. `rows` and `cols` can be `:all` or a range.

   For ComplexTensors, operates on the complex dimensions and preserves
   the ComplexTensor type."
  [m rows cols]
  (tape/record! :t/submatrix [m rows cols]
                (let [m (ensure-tensor m)]
                  (if (cx/complex? m)
                    (cx/complex-tensor
                     (dtype/clone (dtt/select (cx/->tensor m) rows cols :all)))
                    (->rt (dtype/clone (dtt/select m rows cols)))))))

(defn flatten
  "Reshape a tensor to 1D, preserving element order.
   Column vectors `[n 1]` become `[n]`, matrices `[r c]` become `[r*c]`.
   For ComplexTensors, flattens the logical dimensions (trailing 2 preserved)."
  [a]
  (tape/record! :t/flatten [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (let [raw (cx/->tensor a)
                          n (apply * (cx/complex-shape a))]
                      (cx/wrap-tensor (dtt/reshape raw [n 2])))
                    (let [n (dtype/ecount a)]
                      (->rt (dtt/reshape a [n])))))))

(defn hstack
  "Assemble a matrix by placing column vectors side by side.
   Each element should be a column vector `[n 1]` or a 1D tensor `[n]`.
   Returns an `[n k]` matrix where k is the number of columns."
  [cols]
  (tape/record! :t/hstack [cols]
                (let [ts (mapv ensure-tensor cols)
                      n (long (first (dtype/shape (first ts))))
                      k (count ts)
                      buf (dtype/make-container :float64 (* n k))
                      out (dtt/reshape buf [n k])]
                  (dotimes [j k]
                    (let [reader (dtype/->reader (nth ts j) :float64)]
                      (dotimes [i n]
                        (dtt/mset! out i j (double (reader i))))))
                  (->rt out))))

(defn reduce-axis
  "Reduce a tensor along an axis.

   `reduce-fn` is applied to each slice along the given axis
   (e.g. `elem/sum`, `elem/reduce-max`).

   For a 2D matrix: axis 0 reduces across rows (one result per column),
   axis 1 reduces across columns (one result per row).

   Returns a RealTensor for real input, ComplexTensor for complex input."
  [a reduce-fn axis]
  (tape/record! :t/reduce-axis [a reduce-fn axis]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/wrap-tensor
                     (dtt/reduce-axis (cx/->tensor a) reduce-fn (int axis)))
                    (->rt (dtt/reduce-axis a reduce-fn (int axis)))))))

;; ---------------------------------------------------------------------------
;; Materialization and mutation
;; ---------------------------------------------------------------------------

(defn clone
  "Materialize a tensor into a contiguous copy. Breaks all links to
   the source — the returned tensor owns its own `double[]`.
   Returns a RealTensor for real input, ComplexTensor for complex."
  [a]
  (tape/record! :t/clone [a]
                (let [a (ensure-tensor a)]
                  (if (cx/complex? a)
                    (cx/wrap-tensor (dtype/clone (cx/->tensor a)))
                    (->rt (dtype/clone a))))))

(defn mset!
  "Mutate a tensor element in place. Takes a tensor, indices, and a value.
   For RealTensors, unwraps to the backing tensor before mutation."
  [a & args-and-val]
  (let [raw (ensure-tensor a)]
    (apply dtt/mset! raw args-and-val)
    a))

(defn set-value!
  "Set a value at a flat (linear) index. Mutates in place."
  [a idx val]
  (let [raw (ensure-tensor a)]
    (dtype/set-value! raw idx val)
    a))

;; ---------------------------------------------------------------------------
;; Low-level buffer access
;; ---------------------------------------------------------------------------

(defn ->double-array
  "Get the backing `double[]` of a tensor. Zero-copy when the tensor
   is backed by a contiguous array; copies for subviews or lazy tensors."
  ^doubles [a]
  (dtype/->double-array (ensure-tensor a)))

(defn ->reader
  "Get a read-only indexed view of a tensor's elements."
  ([a] (dtype/->reader (ensure-tensor a)))
  ([a datatype] (dtype/->reader (ensure-tensor a) datatype)))

(defn compute-tensor
  "Create a tensor by calling a function at each index.
   Returns a RealTensor for 1D and 2D shapes, raw tensor for 3D+.
   Use `clone` to materialize the lazy result when needed."
  [shape-vec f datatype]
  (tape/record! :t/compute-tensor [shape-vec f datatype]
                (let [result (dtt/compute-tensor shape-vec f datatype)]
                  (if (<= (count shape-vec) 2)
                    (->rt result)
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
  (->rt (dtype/make-container datatype n-or-data)))

(defn elemwise-cast
  "Cast a tensor or buffer to a different element type."
  [a datatype]
  (dtype/elemwise-cast (ensure-tensor a) datatype))

(defn array-buffer
  "Return the array buffer backing a tensor, or nil if none exists
   (e.g. for lazy or strided tensors)."
  [a]
  (dtype/as-array-buffer (ensure-tensor a)))

;; ---------------------------------------------------------------------------
;; Type conversion
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
;; EJML interop
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: convert a `[r c]` tensor to an EJML `DMatrixRMaj` sharing
   the same `double[]`. Falls back to a copy for lazy/strided tensors."
  ^DMatrixRMaj [tensor]
  (bt/tensor->dmat (ensure-tensor tensor)))

(defn dmat->tensor
  "Zero-copy: convert an EJML `DMatrixRMaj` to a `[r c]` tensor sharing
   the same `double[]`."
  [^DMatrixRMaj dm]
  (bt/dmat->tensor dm))

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
  (->rt (dtt/->tensor data {:datatype :float64})))
