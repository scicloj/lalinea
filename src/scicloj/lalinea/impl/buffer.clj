(ns scicloj.lalinea.impl.buffer
  "Low-level buffer inspection utilities shared by
   tensor.clj and tape.clj."
  (:require [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.datatype :as dtype]
            [tech.v3.tensor :as dtt]))

(defn backing-array
  "Return the shared backing `double[]` of a tensor,
   or nil if the tensor is lazy or not array-backed.
   Never copies. Works for RealTensor, ComplexTensor,
   and raw dtype-next tensors."
  ^doubles [t]
  (let [t (cond (ct/complex? t) (ct/->tensor t)
                (rt/real-tensor? t) (rt/->tensor t)
                :else t)]
    (if-let [ab (dtype/as-array-buffer t)]
      (.ary-data ab)
      (when (dtt/tensor? t)
        (when-let [buf (.buffer t)]
          (when-let [ab (dtype/as-array-buffer buf)]
            (.ary-data ab)))))))
