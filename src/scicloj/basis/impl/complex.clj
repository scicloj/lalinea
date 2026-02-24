(ns scicloj.basis.impl.complex
  "Complex tensors backed by dtype-next.

   A ComplexTensor wraps a real tensor whose last dimension is 2
   (interleaved re/im pairs). The `re` and `im` functions always
   slice the last axis, returning zero-copy tensor views.

   Supported ranks:
     [2]       scalar complex number
     [n 2]     complex vector of length n
     [r c 2]   complex r×c matrix
     [... 2]   arbitrary rank

   Adapted from scicloj.harmonica.linalg.complex."
  (:refer-clojure :exclude [abs conj])
  (:require [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.protocols :as dtype-proto]
            [tech.v3.tensor.pprint :as tpp]
            [tech.v3.datatype.pprint :as dtype-pprint]))

;; ---------------------------------------------------------------------------
;; Protocol
;; ---------------------------------------------------------------------------

(defprotocol PComplex
  (re [x] "Real part(s). Returns a tensor view (last dim removed) or a double for scalars.")
  (im [x] "Imaginary part(s). Returns a tensor view (last dim removed) or a double for scalars."))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- select-part
  "Select real (idx=0) or imaginary (idx=1) part from a tensor whose last dim is 2."
  [tensor idx]
  (let [ndims (count (dtype/shape tensor))]
    (apply tensor/select tensor
           (concat (repeat (dec ndims) :all) [idx]))))

;; ---------------------------------------------------------------------------
;; Printing — mirrors tech.v3.tensor.pprint conventions
;; ---------------------------------------------------------------------------

(def ^:private ^String NL (System/getProperty "line.separator"))

(defn- format-complex
  "Format a single complex number as a string, using dtype-next's number formatter."
  [re im]
  (let [re-s (dtype-pprint/format-object re)
        im-s (dtype-pprint/format-object (Math/abs (double im)))
        im-zero? (zero? im)
        re-zero? (zero? re)]
    (cond
      im-zero? re-s
      re-zero? (if (neg? im)
                 (str "-" im-s "i")
                 (str im-s "i"))
      (neg? im) (str re-s "-" im-s "i")
      :else (str re-s "+" im-s "i"))))

(defn- truncate-indices
  "For a dimension of size n, return the indices to show.
   If elipsis? is true, shows first max-dim-count and last max-dim-count."
  [n elipsis?]
  (if elipsis?
    (let [mdc (long tpp/*max-dim-count)]
      (vec (concat (range mdc) (range (- n mdc) n))))
    (vec (range n))))

(defn- format-and-truncate
  "Format complex elements from a raw [... 2] tensor, applying elipsis truncation.
   Returns a nested structure of formatted strings matching the truncated shape.
   complex-shape is the shape without the trailing 2."
  [tensor complex-shape elipsis-vec]
  (let [ndims (count complex-shape)]
    (cond
      ;; Scalar
      (zero? ndims)
      (format-complex (double (tensor 0)) (double (tensor 1)))

      ;; Vector [n 2]
      (= 1 ndims)
      (let [n (first complex-shape)
            indices (truncate-indices n (first elipsis-vec))]
        (mapv (fn [i]
                (let [pair (tensor i)]
                  (format-complex (double (pair 0)) (double (pair 1)))))
              indices))

      ;; Matrix or higher
      :else
      (let [n (first complex-shape)
            indices (truncate-indices n (first elipsis-vec))]
        (mapv (fn [i]
                (format-and-truncate (tensor i)
                                     (vec (rest complex-shape))
                                     (vec (rest elipsis-vec))))
              indices)))))

(defn- leaf-rows
  "Collect all leaf rows (vectors of strings) from nested formatted structure."
  [fmt-tens]
  (cond
    (string? fmt-tens) []
    (string? (first fmt-tens)) [fmt-tens]
    :else (mapcat leaf-rows fmt-tens)))

(defn- complex-column-lengths
  "Compute max formatted string width per column position."
  [fmt-tens complex-shape]
  (if (or (string? fmt-tens)
          (<= (count complex-shape) 1))
    ;; Scalar or vector — each element is its own column
    (if (string? fmt-tens)
      [(count fmt-tens)]
      (mapv count fmt-tens))
    ;; Matrix or higher — columns are the last dim
    (let [rows (leaf-rows fmt-tens)
          n-cols (count (first rows))]
      (mapv (fn [col-idx]
              (apply max 0 (map #(count (nth % col-idx "")) rows)))
            (range n-cols)))))

(defn- append-elem
  "Append a right-padded element to sb."
  [^StringBuilder sb ^String elem ^long col-width]
  (let [pad (- col-width (count elem))]
    (dotimes [_ pad]
      (.append sb \space))
    (.append sb elem)))

(defn- append-row
  "Append a row of formatted complex strings with column alignment and elipsis."
  [^StringBuilder sb row col-widths elipsis?]
  (let [n (count row)
        half (quot n 2)]
    (.append sb \[)
    (dotimes [i n]
      (when (pos? i) (.append sb \space))
      (append-elem sb (nth row i) (nth col-widths i))
      (when (and elipsis? (= (inc i) half))
        (.append sb " ...")))
    (.append sb \])))

(defn- rprint-complex
  "Recursively print formatted complex tensor with alignment and elipsis."
  [^StringBuilder sb fmt-tens col-widths prefix elipsis-vec]
  (let [elipsis? (first elipsis-vec)]
    (cond
      ;; Scalar — just a string
      (string? fmt-tens)
      (.append sb fmt-tens)

      ;; Leaf row — vector of strings
      (string? (first fmt-tens))
      (append-row sb fmt-tens col-widths elipsis?)

      ;; Higher rank — recurse
      :else
      (let [n (count fmt-tens)
            half (quot n 2)
            inner-prefix (str prefix " ")]
        (.append sb \[)
        (dotimes [i n]
          (when (pos? i)
            (.append sb NL)
            (.append sb inner-prefix))
          (rprint-complex sb (nth fmt-tens i) col-widths inner-prefix (rest elipsis-vec))
          (when (and elipsis? (= (inc i) half))
            (.append sb NL)
            (.append sb inner-prefix)
            (.append sb "...")))
        (.append sb \])))))

(defn- complex-tensor->string
  "Pretty-print a ComplexTensor to a string, following dtype-next tensor conventions."
  [tensor]
  (let [raw-shape (vec (dtype/shape tensor))
        complex-shape (vec (butlast raw-shape))
        elipsis-vec (tpp/shape->elipsis-vec complex-shape)
        fmt-tens (format-and-truncate tensor complex-shape elipsis-vec)
        col-widths (complex-column-lengths fmt-tens complex-shape)
        sb (StringBuilder.)]
    (rprint-complex sb fmt-tens col-widths "" elipsis-vec)
    (.toString sb)))

;; ---------------------------------------------------------------------------
;; Non-printing internal helpers
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; ComplexTensor deftype
;; ---------------------------------------------------------------------------

(declare ->ComplexTensor)

(deftype ComplexTensor [tensor]
  PComplex
  (re [_]
    (if (= 1 (count (dtype/shape tensor)))
      (double (tensor 0))
      (select-part tensor 0)))
  (im [_]
    (if (= 1 (count (dtype/shape tensor)))
      (double (tensor 1))
      (select-part tensor 1)))

  clojure.lang.Counted
  (count [_]
    (let [s (dtype/shape tensor)]
      (if (<= (count s) 1)
        0
        (long (first s)))))

  clojure.lang.Indexed
  (nth [_ i]
    (->ComplexTensor (tensor i)))
  (nth [this i not-found]
    (if (and (>= i 0) (< i (.count this)))
      (.nth this i)
      not-found))

  clojure.lang.IFn
  (invoke [this i]
    (.nth this (int i)))
  (applyTo [this args]
    (let [n (clojure.lang.RT/boundedLength args 1)]
      (case n
        1 (.invoke this (first args))
        (throw (clojure.lang.ArityException. n (str (class this)))))))

  clojure.lang.Seqable
  (seq [this]
    (when (pos? (.count this))
      (map #(.nth this %) (range (.count this)))))

  Object
  (toString [_]
    (let [complex-shape (vec (butlast (dtype/shape tensor)))]
      (format "#ComplexTensor<float64>%s%s%s"
              (str complex-shape)
              NL
              (complex-tensor->string tensor)))))

(defmethod print-method ComplexTensor [^ComplexTensor ct ^java.io.Writer w]
  (.write w (.toString ct)))

;; ---------------------------------------------------------------------------
;; Constructors
;; ---------------------------------------------------------------------------

(defn complex-tensor
  "Create a ComplexTensor.

   Arities:
     (complex-tensor tensor)       — wrap an existing tensor with last dim = 2
     (complex-tensor re-data im-data) — from separate real and imaginary parts

   re-data and im-data can be: double arrays, seqs, dtype readers, or tensors.
   They must have the same shape.

   When wrapping a 1-arg input whose last dimension is not 2 (e.g., a flat
   reader from dfn operations on ComplexTensors), it is reshaped to [n/2 2]."
  ([tensor-or-re]
   (let [t (if (instance? ComplexTensor tensor-or-re)
             (.-tensor ^ComplexTensor tensor-or-re)
             (tensor/ensure-tensor tensor-or-re))
         shape (vec (dtype/shape t))]
     (if (= 2 (last shape))
       (->ComplexTensor t)
       ;; Flat input — reshape to [n/2, 2]
       (let [n (long (reduce * shape))]
         (when-not (even? n)
           (throw (ex-info (str "Cannot interpret odd-length data as complex: " n)
                           {:shape shape :n n})))
         (->ComplexTensor (tensor/reshape t [(/ n 2) 2]))))))
  ([re-data im-data]
   (let [re-t (tensor/ensure-tensor re-data)
         im-t (tensor/ensure-tensor im-data)
         re-shape (vec (dtype/shape re-t))
         im-shape (vec (dtype/shape im-t))]
     (when-not (= re-shape im-shape)
       (throw (ex-info (str "Shape mismatch: re=" re-shape " im=" im-shape)
                       {:re-shape re-shape :im-shape im-shape})))
     (let [n (long (reduce * re-shape))
           re-flat (dtype/->reader (tensor/reshape re-t [n]))
           im-flat (dtype/->reader (tensor/reshape im-t [n]))
           interleaved (dtype/make-reader :float64 (* 2 n)
                                          (if (even? idx)
                                            (double (re-flat (quot idx 2)))
                                            (double (im-flat (quot idx 2)))))
           full-shape (clojure.core/conj re-shape 2)]
       (->ComplexTensor (tensor/reshape interleaved full-shape))))))

(defn complex-tensor-real
  "Create a ComplexTensor from real data only (imaginary parts = 0)."
  [re-data]
  (let [re-t (tensor/ensure-tensor re-data)
        re-shape (vec (dtype/shape re-t))
        n (long (reduce * re-shape))
        re-flat (dtype/->reader (tensor/reshape re-t [n]))
        interleaved (dtype/make-reader :float64 (* 2 n)
                                       (if (even? idx)
                                         (double (re-flat (quot idx 2)))
                                         0.0))
        full-shape (clojure.core/conj re-shape 2)]
    (->ComplexTensor (tensor/reshape interleaved full-shape))))

;; ---------------------------------------------------------------------------
;; Accessors
;; ---------------------------------------------------------------------------

(defn ->tensor
  "Access the underlying [... 2] tensor."
  [^ComplexTensor ct]
  (.-tensor ct))

(defn ->double-array
  "Access the underlying interleaved double[].
   Zero-copy when the tensor is backed by a contiguous array;
   falls back to copying otherwise."
  [^ComplexTensor ct]
  (let [ab (dtype/as-array-buffer (.-tensor ct))]
    (if ab
      (.ary-data ab)
      (dtype/->double-array (.-tensor ct)))))

(defn complex-shape
  "The complex shape (underlying shape without trailing 2)."
  [^ComplexTensor ct]
  (vec (butlast (dtype/shape (.-tensor ct)))))

(defn complex?
  "True if x is a ComplexTensor."
  [x]
  (instance? ComplexTensor x))

(defn scalar?
  "True if this ComplexTensor represents a scalar complex number."
  [^ComplexTensor ct]
  (= 1 (count (dtype/shape (.-tensor ct)))))

;; ---------------------------------------------------------------------------
;; Complex arithmetic
;; ---------------------------------------------------------------------------

(defn complex
  "Create a scalar ComplexTensor from real and imaginary parts."
  [re im]
  (let [arr (double-array 2)]
    (aset arr 0 (double re))
    (aset arr 1 (double im))
    (->ComplexTensor (tensor/ensure-tensor arr))))

(defn mul
  "Pointwise complex multiply: (a+bi)(c+di) = (ac-bd) + (ad+bc)i"
  [^ComplexTensor a ^ComplexTensor b]
  (if (and (scalar? a) (scalar? b))
    (let [ar (double (re a)) ai (double (im a))
          br (double (re b)) bi (double (im b))]
      (complex (- (* ar br) (* ai bi))
               (+ (* ar bi) (* ai br))))
    (let [a-flat (dtype/->reader (->tensor a))
          b-flat (dtype/->reader (->tensor b))
          n (dtype/ecount (->tensor a))]
      (->ComplexTensor
       (tensor/reshape
        (dtype/make-reader :float64 n
                           (let [base (-> idx (quot 2) (* 2))
                                 ar (double (a-flat base))
                                 ai (double (a-flat (unchecked-inc base)))
                                 br (double (b-flat base))
                                 bi (double (b-flat (unchecked-inc base)))]
                             (if (even? idx)
                               (- (* ar br) (* ai bi))
                               (+ (* ar bi) (* ai br)))))
        (dtype/shape (->tensor a)))))))

(defn conj
  "Complex conjugate: negate imaginary part."
  [^ComplexTensor ct]
  (let [t (->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)]
    (->ComplexTensor
     (tensor/reshape
      (dtype/make-reader :float64 n
                         (let [v (double (flat idx))]
                           (if (even? idx) v (- v))))
      (dtype/shape t)))))

(defn scale
  "Scale by a real scalar."
  [^ComplexTensor ct alpha]
  (->ComplexTensor (dfn/* (double alpha) (->tensor ct))))

(defn abs
  "Element-wise complex magnitude: sqrt(re² + im²).
   Returns a real tensor (or double for scalar)."
  [^ComplexTensor ct]
  (let [r (re ct) i (im ct)]
    (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i)))))

(defn dot
  "Complex dot product: Σ a_i * b_i.
   Returns a [re im] pair."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)]
    [(- (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
     (+ (dfn/sum (dfn/* ar bi)) (dfn/sum (dfn/* ai br)))]))

(defn dot-conj
  "Hermitian inner product: Σ a_i * conj(b_i).
   Returns a [re im] pair."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)]
    [(+ (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
     (- (dfn/sum (dfn/* ai br)) (dfn/sum (dfn/* ar bi)))]))

(defn add
  "Pointwise complex addition."
  [^ComplexTensor a ^ComplexTensor b]
  (if (and (scalar? a) (scalar? b))
    (complex (+ (double (re a)) (double (re b)))
             (+ (double (im a)) (double (im b))))
    (->ComplexTensor (dfn/+ (->tensor a) (->tensor b)))))

(defn sub
  "Pointwise complex subtraction."
  [^ComplexTensor a ^ComplexTensor b]
  (if (and (scalar? a) (scalar? b))
    (complex (- (double (re a)) (double (re b)))
             (- (double (im a)) (double (im b))))
    (->ComplexTensor (dfn/- (->tensor a) (->tensor b)))))

(defn sum
  "Complex-aware summation. Returns a scalar ComplexTensor."
  [^ComplexTensor ct]
  (complex (dfn/sum (re ct)) (dfn/sum (im ct))))

;; ---------------------------------------------------------------------------
;; dtype-next protocol extensions
;; ---------------------------------------------------------------------------

(extend-type ComplexTensor
  dtype-proto/PElemwiseDatatype
  (elemwise-datatype [_ct] :float64)

  dtype-proto/PECount
  (ecount [ct] (dtype/ecount (->tensor ct)))

  dtype-proto/PShape
  (shape [ct] (dtype/shape (->tensor ct)))

  dtype-proto/PClone
  (clone [ct] (->ComplexTensor (dtype/clone (->tensor ct))))

  dtype-proto/PToReader
  (convertible-to-reader? [_ct] true)
  (->reader [ct] (dtype/->reader (->tensor ct))))













