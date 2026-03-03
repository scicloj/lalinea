(ns scicloj.lalinea.impl.real-tensor
  "RealTensor wrapper for dtype-next tensors.

   Provides La Linea real values with type identity, scoped printing,
   and structural equality — mirroring ComplexTensor for complex values."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.datatype.protocols :as dtype-proto]
            [tech.v3.tensor :as dtt]
            [ham-fisted.defprotocol :as hamf])
  (:import [clojure.lang IHashEq IPersistentCollection Sequential RT ArityException Util]
           [tech.v3.datatype.protocols
            PElemwiseDatatype PECount PShape PClone PToReader PToBuffer]))

(declare ->RealTensor)

(defn- tensor-nth
  "Index into a tensor along the first axis.
   For 1D tensors returns a scalar; for 2D+ returns a sub-tensor."
  [t i]
  (if (== 1 (count (dtype/shape t)))
    (t i)
    (dtt/select t (int i))))

(deftype RealTensor [tensor]
  PElemwiseDatatype
  (elemwise_datatype [_] :float64)

  PECount
  (ecount [_] (dtype/ecount tensor))

  PShape
  (shape [_] (dtype/shape tensor))

  PClone
  (clone [_] (->RealTensor (dtype/clone tensor)))

  PToReader
  (convertible_to_reader_QMARK_ [_] true)
  (__GT_reader [_] (dtype/->reader tensor))

  IHashEq
  (hasheq [_] (.hasheq ^IHashEq tensor))

  clojure.lang.Indexed
  (nth [_ i] (tensor-nth tensor i))
  (nth [_ i not-found]
    (if (and (>= i 0) (< i (long (first (dtype/shape tensor)))))
      (tensor-nth tensor i) not-found))

  clojure.lang.IFn
  (invoke [_ i] (tensor (int i)))
  (invoke [_ i j] (tensor (int i) (int j)))
  (applyTo [this args]
    (case (RT/boundedLength args 2)
      1 (.invoke this (first args))
      2 (.invoke this (first args) (second args))
      (throw (ArityException. (clojure.core/count args) (str (class this))))))

  clojure.lang.Seqable
  (seq [this]
    (when (pos? (.count this))
      (map #(.nth this %) (range (.count this)))))
  Iterable
  (iterator [this]
    (.iterator ^Iterable (or (.seq this) ())))

  IPersistentCollection
  (count [_] (long (first (dtype/shape tensor))))
  (cons [_ o] (.cons ^IPersistentCollection tensor o))
  (empty [_] (.empty ^IPersistentCollection tensor))
  (equiv [this other]
    (cond
      ;; RealTensor-to-RealTensor: compare shapes + flat readers
      (instance? RealTensor other)
      (let [ot (.-tensor ^RealTensor other)]
        (and (= (dtype/shape tensor) (dtype/shape ot))
             (let [r1 (dtype/->reader tensor :float64)
                   r2 (dtype/->reader ot :float64)
                   n (long (clojure.core/count r1))]
               (loop [i (long 0)]
                 (if (< i n)
                   (if (== (double (r1 i)) (double (r2 i)))
                     (recur (unchecked-inc i))
                     false)
                   true)))))
      ;; 1D: delegate to underlying tensor (works with vectors)
      (== 1 (clojure.core/count (dtype/shape tensor)))
      (.equiv ^IPersistentCollection tensor other)
      ;; 2D+: decompose via seq to avoid dtype-next 2D equiv bug
      :else
      (Util/equiv (.seq this) other)))

  Sequential

  Object
  (equals [this other] (.equiv ^IPersistentCollection this other))
  (hashCode [_] (.hashCode tensor))
  (toString [_] (str tensor)))

(defn real-tensor?
  "True if x is a RealTensor."
  [x]
  (instance? RealTensor x))

(defn ->tensor
  "Extract the underlying dtype-next tensor from a RealTensor."
  [^RealTensor rt]
  (.-tensor rt))

(defn ->real-tensor
  "Wrap a dtype-next tensor in a RealTensor.
   Returns t unchanged if already a RealTensor."
  [t]
  (if (instance? RealTensor t) t (->RealTensor t)))

;; hamf protocol extensions — deftype body implements the Java interfaces,
;; but ham-fisted dispatch also needs explicit extension for these protocols.
(hamf/extend-type RealTensor
  dtype-proto/PTensor
  (mget [t idx-seq]
    (apply dtt/mget (->tensor t) idx-seq))
  (mset! [t idx-seq value]
    (apply dtt/mset! (->tensor t) (concat idx-seq [value])))
  (reshape [t new-shape]
    (dtt/reshape (->tensor t) new-shape))
  (select [t select-args]
    (apply dtt/select (->tensor t) select-args))
  (transpose [t reorder-vec]
    (dtt/transpose (->tensor t) reorder-vec))
  (broadcast [t new-shape]
    (dtt/broadcast (->tensor t) new-shape))
  (rotate [t offset-vec]
    (dtt/rotate (->tensor t) offset-vec))
  (slice [t n-dims right?]
    (dtype-proto/slice (->tensor t) n-dims right?)))

(hamf/extend-type RealTensor
  dtype-proto/PToArrayBuffer
  (convertible-to-array-buffer? [buf]
    (dtype-proto/convertible-to-array-buffer? (->tensor buf)))
  (->array-buffer [buf]
    (dtype-proto/->array-buffer (->tensor buf))))

(hamf/extend-type RealTensor
  dtype-proto/PToBuffer
  (convertible-to-buffer? [buf] true)
  (->buffer [buf]
    (let [t (->tensor buf)]
      (if (dtype-proto/convertible-to-buffer? t)
        (dtype-proto/->buffer t)
        (dtype-proto/->buffer (dtype-proto/->reader t))))))
