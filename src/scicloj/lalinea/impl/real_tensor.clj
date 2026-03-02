(ns scicloj.lalinea.impl.real-tensor
  "RealTensor wrapper for dtype-next tensors.

   Provides La Linea real values with type identity, scoped printing,
   and structural equality — mirroring ComplexTensor for complex values."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.datatype.protocols :as dtype-proto]
            [tech.v3.tensor :as tensor]
            [ham-fisted.defprotocol :as hamf])
  (:import [clojure.lang IHashEq RT ArityException]
           [tech.v3.datatype.protocols
            PElemwiseDatatype PECount PShape PClone PToReader]))

(declare ->RealTensor)

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

  clojure.lang.Counted
  (count [_] (long (first (dtype/shape tensor))))

  clojure.lang.Indexed
  (nth [_ i] (tensor i))
  (nth [_ i not-found]
    (if (and (>= i 0) (< i (long (first (dtype/shape tensor)))))
      (tensor i) not-found))

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

  Object
  (equals [_ other]
    (and (instance? RealTensor other)
         (.equiv ^IHashEq tensor (.-tensor ^RealTensor other))))
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
  "Wrap a dtype-next tensor in a RealTensor."
  [t]
  (->RealTensor t))

;; hamf protocol extensions — deftype body implements the Java interfaces,
;; but ham-fisted dispatch also needs explicit extension for these protocols.
(hamf/extend-type RealTensor
  dtype-proto/PTensor
  (mget [t idx-seq]
    (apply tensor/mget (->tensor t) idx-seq))
  (mset! [t idx-seq value]
    (apply tensor/mset! (->tensor t) (concat idx-seq [value])))
  (reshape [t new-shape]
    (tensor/reshape (->tensor t) new-shape))
  (select [t select-args]
    (apply tensor/select (->tensor t) select-args))
  (transpose [t reorder-vec]
    (tensor/transpose (->tensor t) reorder-vec))
  (broadcast [t new-shape]
    (tensor/broadcast (->tensor t) new-shape))
  (rotate [t offset-vec]
    (tensor/rotate (->tensor t) offset-vec))
  (slice [t n-dims right?]
    (tensor/slice (->tensor t) n-dims right?)))

(hamf/extend-type RealTensor
  dtype-proto/PToArrayBuffer
  (convertible-to-array-buffer? [buf]
    (dtype-proto/convertible-to-array-buffer? (->tensor buf)))
  (->array-buffer [buf]
    (dtype-proto/->array-buffer (->tensor buf))))
