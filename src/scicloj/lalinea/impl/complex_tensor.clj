(ns scicloj.lalinea.impl.complex-tensor
  "ComplexTensor wrapper for dtype-next tensors.

   Provides La Linea complex values with type identity, scoped printing,
   and structural equality — mirroring RealTensor for real values."
  (:require [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
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
;; Public API
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
