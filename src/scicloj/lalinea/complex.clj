(ns scicloj.lalinea.complex
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
            [tech.v3.datatype.protocols :as dtype-proto]))

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
;; ComplexTensor deftype
;; ---------------------------------------------------------------------------

(def ^:private ->rt-fn (delay (requiring-resolve 'scicloj.lalinea.impl.real-tensor/->real-tensor)))

(declare ->ComplexTensor)

(deftype ComplexTensor [tensor]
  dtype-proto/PElemwiseDatatype
  (elemwise-datatype [_ct] :float64)

  dtype-proto/PECount
  (ecount [ct] (dtype/ecount tensor))

  dtype-proto/PShape
  (shape [ct] (dtype/shape tensor))

  dtype-proto/PClone
  (clone [ct] (->ComplexTensor (dtype/clone tensor)))

  dtype-proto/PToReader
  (convertible-to-reader? [_ct] true)
  (->reader [ct] (dtype/->reader tensor))

  PComplex
  (re [_]
    (if (= 1 (count (dtype/shape tensor)))
      (double (tensor 0))
      (@@->rt-fn (select-part tensor 0))))
  (im [_]
    (if (= 1 (count (dtype/shape tensor)))
      (double (tensor 1))
      (@@->rt-fn (select-part tensor 1))))

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

  clojure.lang.IHashEq
  (hasheq [_] (.hasheq ^clojure.lang.IHashEq tensor))

  Object
  (equals [_ other]
    (and (instance? ComplexTensor other)
         (.equiv ^clojure.lang.IHashEq tensor (.-tensor ^ComplexTensor other))))
  (hashCode [_] (.hashCode tensor))
  (toString [_]
    (let [complex-shape (vec (butlast (dtype/shape tensor)))]
      (format "ComplexTensor<float64>%s" (str complex-shape)))))

;; print-method installed by impl/print.clj (loaded via linalg.clj)

;; ---------------------------------------------------------------------------
;; Tape recording (lazy resolution to avoid circular dep with tape ns)
;; ---------------------------------------------------------------------------

(def ^:private tape-var   (delay (requiring-resolve 'scicloj.lalinea.tape/*tape*)))
(def ^:private inside-var (delay (requiring-resolve 'scicloj.lalinea.tape/*inside-record*)))
(def ^:private record-fn  (delay (requiring-resolve 'scicloj.lalinea.tape/do-record!)))

(defn- tape-record!
  "Record to tape if active. No-op when tape is nil or inside a parent record."
  [op inputs result]
  (when (and @@tape-var (not @@inside-var))
    (@@record-fn op inputs result))
  result)

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
         shape (vec (dtype/shape t))
         result (if (= 2 (last shape))
                  (->ComplexTensor t)
                  ;; Flat input — reshape to [n/2, 2]
                  (let [n (long (reduce * shape))]
                    (when-not (even? n)
                      (throw (ex-info (str "Cannot interpret odd-length data as complex: " n)
                                      {:shape shape :n n})))
                    (->ComplexTensor (tensor/reshape t [(/ n 2) 2]))))]
     (tape-record! :cx/complex-tensor [tensor-or-re] result)))
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
           full-shape (clojure.core/conj re-shape 2)
           result (->ComplexTensor (tensor/reshape interleaved full-shape))]
       (tape-record! :cx/complex-tensor [re-data im-data] result)))))

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
        full-shape (clojure.core/conj re-shape 2)
        result (->ComplexTensor (tensor/reshape interleaved full-shape))]
    (tape-record! :cx/complex-tensor-real [re-data] result)))

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
  ^doubles [^ComplexTensor ct]
  (dtype/->double-array (.-tensor ct)))

(defn wrap-tensor
  "Wrap a raw interleaved [... 2] tensor as a ComplexTensor.
   Inverse of `->tensor`."
  [t]
  (let [s (dtype/shape t)]
    (assert (= 2 (last s)) "Last dimension must be 2 for complex interleaved layout")
    (ComplexTensor. t)))

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
  (let [result
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
              (dtype/shape (->tensor a))))))]
    (tape-record! :cx/mul [a b] result)))

(defn conj
  "Complex conjugate: negate imaginary part."
  [^ComplexTensor ct]
  (let [t (->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)
        result (->ComplexTensor
                (tensor/reshape
                 (dtype/make-reader :float64 n
                                    (let [v (double (flat idx))]
                                      (if (even? idx) v (- v))))
                 (dtype/shape t)))]
    (tape-record! :cx/conj [ct] result)))

(defn scale
  "Scale by a real scalar."
  [^ComplexTensor ct alpha]
  (let [t (->tensor ct)
        result (->ComplexTensor (dfn/* (double alpha) t))]
    (tape-record! :cx/scale [ct alpha] result)))

(defn abs
  "Element-wise complex magnitude: sqrt(re² + im²).
   Returns a real tensor (or double for scalar)."
  [^ComplexTensor ct]
  (let [r (re ct) i (im ct)
        result (if (number? r)
                 (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i)))
                 (let [r (tensor/ensure-tensor r)
                       i (tensor/ensure-tensor i)]
                   (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i)))))]
    (tape-record! :cx/abs [ct] result)))

(defn dot
  "Complex dot product: Σ a_i * b_i.
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)
        result (complex (- (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
                        (+ (dfn/sum (dfn/* ar bi)) (dfn/sum (dfn/* ai br))))]
    (tape-record! :cx/dot [a b] result)))

(defn dot-conj
  "Hermitian inner product: Σ a_i * conj(b_i).
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)
        result (complex (+ (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
                        (- (dfn/sum (dfn/* ai br)) (dfn/sum (dfn/* ar bi))))]
    (tape-record! :cx/dot-conj [a b] result)))

(defn add
  "Pointwise complex addition."
  [^ComplexTensor a ^ComplexTensor b]
  (let [result
        (if (and (scalar? a) (scalar? b))
          (complex (+ (double (re a)) (double (re b)))
                   (+ (double (im a)) (double (im b))))
          (let [ta (->tensor a)]
            (->ComplexTensor (dfn/+ ta (->tensor b)))))]
    (tape-record! :cx/add [a b] result)))

(defn sub
  "Pointwise complex subtraction."
  [^ComplexTensor a ^ComplexTensor b]
  (let [result
        (if (and (scalar? a) (scalar? b))
          (complex (- (double (re a)) (double (re b)))
                   (- (double (im a)) (double (im b))))
          (let [ta (->tensor a)]
            (->ComplexTensor (dfn/- ta (->tensor b)))))]
    (tape-record! :cx/sub [a b] result)))

(defn sum
  "Complex-aware summation. Returns a scalar ComplexTensor."
  [^ComplexTensor ct]
  (let [result (complex (dfn/sum (re ct)) (dfn/sum (im ct)))]
    (tape-record! :cx/sum [ct] result)))

;; ---------------------------------------------------------------------------
;; Tagged literal reader
;; ---------------------------------------------------------------------------

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
        (complex r i))

      ;; Vector: shape [n], data = [re + im i  re + im i ...]
      (= 1 ndims)
      (let [pairs (parse-complex-tokens data)
            re-arr (double-array (mapv first pairs))
            im-arr (double-array (mapv second pairs))]
        (complex-tensor (tensor/ensure-tensor re-arr)
                        (tensor/ensure-tensor im-arr)))

      ;; Matrix: shape [r c], data = [[row1-tokens] [row2-tokens] ...]
      :else
      (let [row-pairs (mapv parse-complex-tokens data)
            all-pairs (vec (apply concat row-pairs))
            re-arr (double-array (mapv first all-pairs))
            im-arr (double-array (mapv second all-pairs))]
        (complex-tensor (tensor/reshape (tensor/ensure-tensor re-arr) shape)
                        (tensor/reshape (tensor/ensure-tensor im-arr) shape))))))

;; ---------------------------------------------------------------------------
;; dtype-next protocol extensions
;; ---------------------------------------------------------------------------

;; dtype-next protocol implementations moved into deftype (v11 compatibility)
