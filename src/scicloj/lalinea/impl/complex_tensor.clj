(ns scicloj.lalinea.impl.complex-tensor
  "ComplexTensor wrapper for dtype-next tensors.

   Provides La Linea complex values with type identity, scoped printing,
   and structural equality — mirroring RealTensor for real values.

   Also contains constructors and pure arithmetic operations (no tape recording)."
  (:require [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.datatype.protocols :as dtype-proto]))

;; ---------------------------------------------------------------------------
;; Protocol
;; ---------------------------------------------------------------------------

(defprotocol PComplex
  (re [x] "Real part(s). Returns a RealTensor view (last dim removed) or a double for scalars.")
  (im [x] "Imaginary part(s). Returns a RealTensor view (last dim removed) or a double for scalars."))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- select-part
  "Select real (idx=0) or imaginary (idx=1) part from a tensor whose last dim is 2."
  [tensor idx]
  (let [ndims (count (dtype/shape tensor))]
    (apply dtt/select tensor
           (concat (repeat (dec ndims) :all) [idx]))))

;; ---------------------------------------------------------------------------
;; ComplexTensor deftype
;; ---------------------------------------------------------------------------

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
      (rt/->real-tensor (select-part tensor 0))))
  (im [_]
    (if (= 1 (count (dtype/shape tensor)))
      (double (tensor 1))
      (rt/->real-tensor (select-part tensor 1))))

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

;; print-method installed by impl/print.clj (loaded via tensor.clj)

;; ---------------------------------------------------------------------------
;; Type API
;; ---------------------------------------------------------------------------

(defn complex?
  "True if x is a ComplexTensor."
  [x]
  (instance? ComplexTensor x))

(defn scalar?
  "True if this ComplexTensor represents a scalar complex number."
  [^ComplexTensor ct]
  (= 1 (count (dtype/shape (.-tensor ct)))))

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

(defn ->complex-tensor
  "Wrap a raw interleaved tensor as a ComplexTensor.
   Returns ct unchanged if already a ComplexTensor."
  [ct]
  (if (instance? ComplexTensor ct) ct (wrap-tensor ct)))

;; ---------------------------------------------------------------------------
;; Scalar constructor
;; ---------------------------------------------------------------------------

(defn complex
  "Create a scalar ComplexTensor from real and imaginary parts."
  [re im]
  (let [arr (double-array 2)]
    (aset arr 0 (double re))
    (aset arr 1 (double im))
    (ComplexTensor. (dtt/ensure-tensor arr))))

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
   (let [t (cond
             (instance? ComplexTensor tensor-or-re)
             (.-tensor ^ComplexTensor tensor-or-re)
             :else
             (dtt/ensure-tensor (rt/ensure-tensor tensor-or-re)))
         shape (vec (dtype/shape t))]
     (if (= 2 (last shape))
       (ComplexTensor. t)
       ;; Flat input — reshape to [n/2, 2]
       (let [n (long (reduce * shape))]
         (when-not (even? n)
           (throw (ex-info (str "Cannot interpret odd-length data as complex: " n)
                           {:shape shape :n n})))
         (ComplexTensor. (dtt/reshape t [(/ n 2) 2]))))))
  ([re-data im-data]
   (let [re-t (dtt/ensure-tensor (rt/ensure-tensor re-data))
         im-t (dtt/ensure-tensor (rt/ensure-tensor im-data))
         re-shape (vec (dtype/shape re-t))
         im-shape (vec (dtype/shape im-t))]
     (when-not (= re-shape im-shape)
       (throw (ex-info (str "Shape mismatch: re=" re-shape " im=" im-shape)
                       {:re-shape re-shape :im-shape im-shape})))
     (let [n (long (reduce * re-shape))
           re-flat (dtype/->reader (dtt/reshape re-t [n]))
           im-flat (dtype/->reader (dtt/reshape im-t [n]))
           interleaved (dtype/make-reader :float64 (* 2 n)
                                          (if (even? idx)
                                            (double (re-flat (quot idx 2)))
                                            (double (im-flat (quot idx 2)))))
           full-shape (clojure.core/conj re-shape 2)]
       (ComplexTensor. (dtt/reshape interleaved full-shape))))))

(defn complex-tensor-real
  "Create a ComplexTensor from real data only (imaginary parts = 0)."
  [re-data]
  (let [re-t (dtt/ensure-tensor (rt/ensure-tensor re-data))
        re-shape (vec (dtype/shape re-t))
        n (long (reduce * re-shape))
        re-flat (dtype/->reader (dtt/reshape re-t [n]))
        interleaved (dtype/make-reader :float64 (* 2 n)
                                       (if (even? idx)
                                         (double (re-flat (quot idx 2)))
                                         0.0))
        full-shape (clojure.core/conj re-shape 2)]
    (ComplexTensor. (dtt/reshape interleaved full-shape))))

;; ---------------------------------------------------------------------------
;; Complex arithmetic (pure — no tape recording)
;; ---------------------------------------------------------------------------

(defn ct-mul
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
      (ComplexTensor.
       (dtt/reshape
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

(defn ct-conj
  "Complex conjugate: negate imaginary part."
  [^ComplexTensor ct]
  (let [t (->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)]
    (ComplexTensor.
     (dtt/reshape
      (dtype/make-reader :float64 n
                         (let [v (double (flat idx))]
                           (if (even? idx) v (- v))))
      (dtype/shape t)))))

(defn ct-scale
  "Scale by a real scalar."
  [^ComplexTensor ct alpha]
  (let [t (->tensor ct)]
    (ComplexTensor. (dfn/* (double alpha) t))))

(defn ct-abs
  "Element-wise complex magnitude: sqrt(re² + im²).
   Returns a RealTensor (or double for scalar)."
  [^ComplexTensor ct]
  (let [r (re ct) i (im ct)]
    (if (number? r)
      (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i)))
      (let [r (dtt/ensure-tensor r)
            i (dtt/ensure-tensor i)]
        (rt/->real-tensor (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i))))))))

(defn ct-dot
  "Complex dot product: Σ a_i * b_i.
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)]
    (complex (- (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
             (+ (dfn/sum (dfn/* ar bi)) (dfn/sum (dfn/* ai br))))))

(defn ct-dot-conj
  "Hermitian inner product: Σ a_i * conj(b_i).
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)]
    (complex (+ (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
             (- (dfn/sum (dfn/* ai br)) (dfn/sum (dfn/* ar bi))))))

(defn ct-add
  "Pointwise complex addition."
  [^ComplexTensor a ^ComplexTensor b]
  (if (and (scalar? a) (scalar? b))
    (complex (+ (double (re a)) (double (re b)))
             (+ (double (im a)) (double (im b))))
    (let [ta (->tensor a)]
      (ComplexTensor. (dfn/+ ta (->tensor b))))))

(defn ct-sub
  "Pointwise complex subtraction."
  [^ComplexTensor a ^ComplexTensor b]
  (if (and (scalar? a) (scalar? b))
    (complex (- (double (re a)) (double (re b)))
             (- (double (im a)) (double (im b))))
    (let [ta (->tensor a)]
      (ComplexTensor. (dfn/- ta (->tensor b))))))

(defn ct-sum
  "Complex-aware summation. Returns a scalar ComplexTensor."
  [^ComplexTensor ct]
  (complex (dfn/sum (re ct)) (dfn/sum (im ct))))
