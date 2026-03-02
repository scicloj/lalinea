(ns scicloj.lalinea.impl.print
  "Tagged-literal printing for dtype-next tensors.

   Requiring this namespace installs print-method implementations
   that produce `#la/m` and `#la/v` tagged literals for small tensors,
   enabling round-trip through `pr-str` / `read-string`."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.tensor :as tensor]
            [scicloj.lalinea.impl.real-tensor :as rt])
  (:import [scicloj.lalinea.impl.real_tensor RealTensor]))

(def ^:dynamic *print-threshold*
  "Maximum number of elements per dimension before truncating.
   Tensors larger than this print in default dtype-next format."
  20)

(defn- tensor-tagged-str
  "Format a tensor as a tagged-literal string, or nil if too large."
  [t]
  (let [t (if (rt/real-tensor? t) (rt/->tensor t) t)]
    (let [shape (dtype/shape t)]
      (when (= 2 (count shape))
        (let [[r c] shape]
          (cond
          ;; Column vector [n 1] -> #la/v [...]
            (and (= 1 (long c)) (<= (long r) *print-threshold*))
            (str "#la/v " (pr-str (vec (dtype/->reader t :float64))))

          ;; General matrix [r c] -> #la/m [[...] [...]]
            (and (<= (long r) *print-threshold*)
                 (<= (long c) *print-threshold*))
            (str "#la/m "
                 (pr-str (mapv (fn [i]
                                 (vec (dtype/->reader (tensor/select t i :all) :float64)))
                               (range r))))

          ;; Too large -- fall through to default
            :else nil))))))

(defn tensor->str
  "Convert a tensor to a tagged-literal string representation,
   or nil if the tensor is too large or not 2D."
  [t]
  (tensor-tagged-str t))

(defmethod print-method RealTensor
  [^RealTensor t ^java.io.Writer w]
  (if-let [s (tensor-tagged-str t)]
    (.write w s)
    ;; Fall back to default tensor printing for large or non-2D tensors
    (.write w (str t))))
