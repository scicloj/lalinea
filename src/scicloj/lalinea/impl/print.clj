(ns scicloj.lalinea.impl.print
  "Tagged-literal printing for La Linea tensors.

   Requiring this namespace installs print-method implementations
   that produce `#la/R` and `#la/C` tagged literals, enabling
   round-trip through `pr-str` / `read-string` for small tensors."
  (:require [scicloj.kindly-advice.v1.api :as kindly-advice]
            [tech.v3.datatype :as dtype]
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

(defn- print-real-tensor
  "Print a RealTensor in #la/R [:float64 [shape] data] format."
  [^RealTensor rt ^java.io.Writer w]
  (let [t     (rt/->tensor rt)
        shape (vec (dtype/shape t))
        ndims (count shape)
        rdr   (dtype/->reader t :float64)]
    (.write w "#la/R [:float64 ")
    (.write w (pr-str shape))
    (cond
      ;; Scalar []: single value
      (zero? ndims)
      (do
        (.write w " ")
        (.write w (str (double (rdr 0))))
        (.write w "]"))

      ;; Vector [n]
      (= 1 ndims)
      (let [n     (long (first shape))
            max-n (if (truncated? shape) (min n (long *print-threshold*)) n)]
        (.write w " [")
        (dotimes [k max-n]
          (when (pos? k) (.write w " "))
          (.write w (str (double (rdr k)))))
        (when (< max-n n)
          (.write w " ..."))
        (.write w "]]"))

      ;; Matrix [r c]
      :else
      (let [[r c] shape
            max-r (if (truncated? shape) (min (long r) (long *print-threshold*)) (long r))
            max-c (if (truncated? shape) (min (long c) (long *print-threshold*)) (long c))
            c     (long c)]
        (.write w "\n       [")
        (dotimes [i max-r]
          (when (pos? i) (.write w "\n        "))
          (.write w "[")
          (dotimes [j max-c]
            (when (pos? j) (.write w " "))
            (.write w (str (double (rdr (+ (* i c) j))))))
          (when (< max-c c)
            (.write w " ..."))
          (.write w "]"))
        (when (< max-r (long r))
          (.write w "\n        ..."))
        (.write w "]]")))))

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

(defn- print-complex-tensor
  "Print a ComplexTensor in #la/C [:float64 [shape] data] format."
  [^ComplexTensor ct ^java.io.Writer w]
  (let [raw    (cx/->tensor ct)
        cshape (cx/complex-shape ct)
        ndims  (count cshape)
        rdr    (dtype/->reader raw :float64)]
    (.write w "#la/C [:float64 ")
    (.write w (pr-str cshape))
    (cond
      ;; Scalar
      (zero? ndims)
      (do
        (.write w " [")
        (.write w (format-complex-tokens (rdr 0) (rdr 1)))
        (.write w "]]"))

      ;; Vector [n]
      (= 1 ndims)
      (let [n     (long (first cshape))
            max-n (if (truncated? cshape) (min n (long *print-threshold*)) n)]
        (.write w " [")
        (dotimes [k max-n]
          (when (pos? k) (.write w "  "))
          (let [base (* k 2)]
            (.write w (format-complex-tokens (rdr base) (rdr (inc base))))))
        (when (< max-n n)
          (.write w "  ..."))
        (.write w "]]"))

      ;; Matrix [r c]
      :else
      (let [[r c] cshape
            max-r (if (truncated? cshape) (min (long r) (long *print-threshold*)) (long r))
            max-c (if (truncated? cshape) (min (long c) (long *print-threshold*)) (long c))
            stride (* (long c) 2)]
        (.write w "\n       [")
        (dotimes [i max-r]
          (when (pos? i) (.write w "\n        "))
          (.write w "[")
          (dotimes [j max-c]
            (when (pos? j) (.write w "  "))
            (let [base (+ (* i stride) (* j 2))]
              (.write w (format-complex-tokens (rdr base) (rdr (inc base))))))
          (when (< max-c (long c))
            (.write w "  ..."))
          (.write w "]"))
        (when (< max-r (long r))
          (.write w "\n        ..."))
        (.write w "]]")))))

(defmethod print-method ComplexTensor
  [^ComplexTensor ct ^java.io.Writer w]
  (print-complex-tensor ct w))

;; ---------------------------------------------------------------------------
;; Kindly advisor — tell Clay to use pprint (print-method) for La Linea types
;; ---------------------------------------------------------------------------

(kindly-advice/add-advisor!
 (fn [{:keys [value]}]
   (when (or (instance? RealTensor value)
             (instance? ComplexTensor value))
     {:kind :kind/pprint})))
