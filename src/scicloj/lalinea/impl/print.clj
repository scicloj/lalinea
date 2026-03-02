(ns scicloj.lalinea.impl.print
  "Tagged-literal printing for La Linea tensors.

   Requiring this namespace installs print-method implementations
   that produce `#la/R` and `#la/C` tagged literals, enabling
   round-trip through `pr-str` / `read-string` for small tensors."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.tensor :as tensor]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.complex :as cx])
  (:import [scicloj.lalinea.impl.real_tensor RealTensor]
           [scicloj.lalinea.complex ComplexTensor]))

(def ^:dynamic *print-threshold*
  "Maximum number of elements per dimension before truncating.
   Tensors larger than this print in truncated format (not readable)."
  20)

(defn- truncated?
  "True if any dimension exceeds the print threshold."
  [shape]
  (some #(> (long %) *print-threshold*) shape))

;; ---------------------------------------------------------------------------
;; #la/R — RealTensor printing
;; ---------------------------------------------------------------------------

(defn- tensor-row-vec
  "Extract row i of a 2D tensor as a Clojure vector of doubles."
  [t i]
  (vec (dtype/->reader (tensor/select t i :all) :float64)))

(defn- print-real-tensor
  "Print a RealTensor in #la/R [:float64 [shape] data] format."
  [^RealTensor rt ^java.io.Writer w]
  (let [t (rt/->tensor rt)
        shape (vec (dtype/shape t))]
    (if (not= 2 (count shape))
      ;; Non-2D: fall back to basic str
      (.write w (str "#la/R [:float64 " (pr-str shape) " " (pr-str (str t)) "]"))
      (let [[r c] shape]
        (if (truncated? shape)
          ;; Truncated: include ... marker
          (let [max-r (min (long r) *print-threshold*)
                max-c (min (long c) *print-threshold*)]
            (.write w "#la/R [:float64 ")
            (.write w (pr-str shape))
            (.write w "\n       [")
            (dotimes [i max-r]
              (when (pos? i) (.write w "\n        "))
              (.write w "[")
              (dotimes [j max-c]
                (when (pos? j) (.write w " "))
                (.write w (str (double (tensor/mget t i j)))))
              (when (< max-c (long c))
                (.write w " ..."))
              (.write w "]"))
            (when (< max-r (long r))
              (.write w "\n        ..."))
            (.write w "]]"))
          ;; Full: readable format
          (do
            (.write w "#la/R [:float64 ")
            (.write w (pr-str shape))
            (.write w "\n       ")
            (.write w (pr-str (mapv #(tensor-row-vec t %) (range r))))
            (.write w "]")))))))

(defmethod print-method RealTensor
  [^RealTensor t ^java.io.Writer w]
  (print-real-tensor t w))

;; ---------------------------------------------------------------------------
;; #la/C — ComplexTensor printing
;; ---------------------------------------------------------------------------

(defn- format-complex-tokens
  "Format a single complex number as '3.0 + 4.0 i' or '1.0 - 2.0 i'."
  [re im]
  (let [re (double re)
        im (double im)]
    (if (neg? im)
      (str re " - " (Math/abs im) " i")
      (str re " + " im " i"))))

(defn- cx-element-str
  "Format element at position [indices...] in the underlying complex tensor."
  [raw-tensor indices]
  (let [re (double (apply tensor/mget raw-tensor (concat indices [0])))
        im (double (apply tensor/mget raw-tensor (concat indices [1])))]
    (format-complex-tokens re im)))

(defn- print-complex-tensor
  "Print a ComplexTensor in #la/C [:float64 [shape] data] format."
  [^ComplexTensor ct ^java.io.Writer w]
  (let [raw (cx/->tensor ct)
        cshape (cx/complex-shape ct)
        ndims (count cshape)]
    (.write w "#la/C [:float64 ")
    (.write w (pr-str cshape))
    (cond
      ;; Scalar
      (zero? ndims)
      (do
        (.write w " [")
        (.write w (cx-element-str raw []))
        (.write w "]]"))

      ;; Vector [n]
      (= 1 ndims)
      (let [n (long (first cshape))]
        (if (truncated? cshape)
          (let [max-n (min n (long *print-threshold*))]
            (.write w "\n       [")
            (dotimes [k max-n]
              (when (pos? k) (.write w "  "))
              (.write w (cx-element-str raw [k])))
            (when (< max-n n)
              (.write w "  ..."))
            (.write w "]]"))
          (do
            (.write w "\n       [")
            (dotimes [k n]
              (when (pos? k) (.write w "  "))
              (.write w (cx-element-str raw [k])))
            (.write w "]]"))))

      ;; Matrix [r c]
      :else
      (let [[r c] cshape]
        (if (truncated? cshape)
          (let [max-r (min (long r) (long *print-threshold*))
                max-c (min (long c) (long *print-threshold*))]
            (.write w "\n       [")
            (dotimes [i max-r]
              (when (pos? i) (.write w "\n        "))
              (.write w "[")
              (dotimes [j max-c]
                (when (pos? j) (.write w "  "))
                (.write w (cx-element-str raw [i j])))
              (when (< max-c (long c))
                (.write w "  ..."))
              (.write w "]"))
            (when (< max-r (long r))
              (.write w "\n        ..."))
            (.write w "]]"))
          (do
            (.write w "\n       [")
            (dotimes [i r]
              (when (pos? i) (.write w "\n        "))
              (.write w "[")
              (dotimes [j c]
                (when (pos? j) (.write w "  "))
                (.write w (cx-element-str raw [i j])))
              (.write w "]"))
            (.write w "]]")))))))

(defmethod print-method ComplexTensor
  [^ComplexTensor ct ^java.io.Writer w]
  (print-complex-tensor ct w))
