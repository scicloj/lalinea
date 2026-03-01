(ns scicloj.la-linea.elementwise
  "Tape-aware element-wise operations with complex dispatch.

   Each function records on the tape (when active) and dispatches
   on `cx/complex?`. Functions without meaningful complex analogues
   throw on complex input."
  (:refer-clojure :exclude [abs min max])
  (:require [scicloj.la-linea.tape :as tape]
            [scicloj.la-linea.complex :as cx]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.tensor :as tensor]))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- unsupported-complex! [op]
  (throw (ex-info (str op " is not supported for complex input")
                  {:op op})))

(defn- complex-unary
  "Apply a unary function element-wise to a ComplexTensor.
   f takes (re, im) and returns [new-re, new-im]."
  [ct f]
  (let [t (cx/->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)]
    (cx/complex-tensor
     (tensor/reshape
      (dtype/make-reader :float64 n
                         (let [base (-> idx (quot 2) (* 2))
                               x (double (flat base))
                               y (double (flat (unchecked-inc base)))
                               [r i] (f x y)]
                           (if (even? idx) (double r) (double i))))
      (dtype/shape t)))))

;; ---------------------------------------------------------------------------
;; Powers
;; ---------------------------------------------------------------------------

(defn sq
  "Element-wise square."
  [a]
  (tape/record! :elem/sq [a]
                (if (cx/complex? a)
                  (cx/mul a a)
                  (dfn/sq a))))

(defn sqrt
  "Element-wise square root."
  [a]
  (tape/record! :elem/sqrt [a]
                (if (cx/complex? a)
      ;; sqrt(z) = sqrt(|z|) * e^(i*theta/2)
                  (complex-unary a (fn [x y]
                                     (let [r (Math/sqrt (Math/sqrt (+ (* x x) (* y y))))
                                           theta (/ (Math/atan2 y x) 2.0)]
                                       [(* r (Math/cos theta))
                                        (* r (Math/sin theta))])))
                  (dfn/sqrt a))))

(defn pow
  "Element-wise power. Real only."
  [a exponent]
  (tape/record! :elem/pow [a exponent]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/pow)
                  (tensor/reshape (dfn/pow a (double exponent)) (dtype/shape a)))))

(defn cbrt
  "Element-wise cube root. Real only."
  [a]
  (tape/record! :elem/cbrt [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/cbrt)
                  (dfn/cbrt a))))

;; ---------------------------------------------------------------------------
;; Exponential and logarithmic
;; ---------------------------------------------------------------------------

(defn exp
  "Element-wise exponential."
  [a]
  (tape/record! :elem/exp [a]
                (if (cx/complex? a)
      ;; exp(x+iy) = e^x * (cos y + i sin y)
                  (complex-unary a (fn [x y]
                                     (let [ex (Math/exp x)]
                                       [(* ex (Math/cos y))
                                        (* ex (Math/sin y))])))
                  (dfn/exp a))))

(defn log
  "Element-wise natural logarithm."
  [a]
  (tape/record! :elem/log [a]
                (if (cx/complex? a)
      ;; log(z) = ln|z| + i*arg(z)
                  (complex-unary a (fn [x y]
                                     [(Math/log (Math/sqrt (+ (* x x) (* y y))))
                                      (Math/atan2 y x)]))
                  (dfn/log a))))

(defn log10
  "Element-wise base-10 logarithm. Real only."
  [a]
  (tape/record! :elem/log10 [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/log10)
                  (dfn/log10 a))))

;; ---------------------------------------------------------------------------
;; Trigonometric
;; ---------------------------------------------------------------------------

(defn sin
  "Element-wise sine."
  [a]
  (tape/record! :elem/sin [a]
                (if (cx/complex? a)
      ;; sin(x+iy) = sin(x)cosh(y) + i*cos(x)sinh(y)
                  (complex-unary a (fn [x y]
                                     [(* (Math/sin x) (Math/cosh y))
                                      (* (Math/cos x) (Math/sinh y))]))
                  (dfn/sin a))))

(defn cos
  "Element-wise cosine."
  [a]
  (tape/record! :elem/cos [a]
                (if (cx/complex? a)
      ;; cos(x+iy) = cos(x)cosh(y) - i*sin(x)sinh(y)
                  (complex-unary a (fn [x y]
                                     [(* (Math/cos x) (Math/cosh y))
                                      (- (* (Math/sin x) (Math/sinh y)))]))
                  (dfn/cos a))))

(defn tan
  "Element-wise tangent. Real only."
  [a]
  (tape/record! :elem/tan [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/tan)
                  (dfn/tan a))))

;; ---------------------------------------------------------------------------
;; Hyperbolic
;; ---------------------------------------------------------------------------

(defn sinh
  "Element-wise hyperbolic sine."
  [a]
  (tape/record! :elem/sinh [a]
                (if (cx/complex? a)
      ;; sinh(x+iy) = sinh(x)cos(y) + i*cosh(x)sin(y)
                  (complex-unary a (fn [x y]
                                     [(* (Math/sinh x) (Math/cos y))
                                      (* (Math/cosh x) (Math/sin y))]))
                  (dfn/sinh a))))

(defn cosh
  "Element-wise hyperbolic cosine."
  [a]
  (tape/record! :elem/cosh [a]
                (if (cx/complex? a)
      ;; cosh(x+iy) = cosh(x)cos(y) + i*sinh(x)sin(y)
                  (complex-unary a (fn [x y]
                                     [(* (Math/cosh x) (Math/cos y))
                                      (* (Math/sinh x) (Math/sin y))]))
                  (dfn/cosh a))))

(defn tanh
  "Element-wise hyperbolic tangent. Real only."
  [a]
  (tape/record! :elem/tanh [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/tanh)
                  (dfn/tanh a))))

;; ---------------------------------------------------------------------------
;; Absolute value
;; ---------------------------------------------------------------------------

(defn abs
  "Element-wise absolute value (magnitude for complex)."
  [a]
  (tape/record! :elem/abs [a]
                (if (cx/complex? a)
                  (cx/abs a)
                  (dfn/abs a))))

;; ---------------------------------------------------------------------------
;; Reductions
;; ---------------------------------------------------------------------------

(defn sum
  "Sum of all elements. Returns double for real, scalar ComplexTensor for complex."
  [a]
  (tape/record! :elem/sum [a]
                (if (cx/complex? a)
                  (cx/sum a)
                  (double (dfn/sum a)))))

(defn mean
  "Mean of all elements. Real only."
  [a]
  (tape/record! :elem/mean [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/mean)
                  (double (dfn/mean a)))))

;; ---------------------------------------------------------------------------
;; Rounding and comparison (real only)
;; ---------------------------------------------------------------------------

(defn floor
  "Element-wise floor. Real only."
  [a]
  (tape/record! :elem/floor [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/floor)
                  (dfn/floor a))))

(defn ceil
  "Element-wise ceiling. Real only."
  [a]
  (tape/record! :elem/ceil [a]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/ceil)
                  (dfn/ceil a))))

(defn min
  "Element-wise minimum. Real only."
  [a b]
  (tape/record! :elem/min [a b]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/min)
                  (tensor/reshape (dfn/min a b) (dtype/shape a)))))

(defn max
  "Element-wise maximum. Real only."
  [a b]
  (tape/record! :elem/max [a b]
                (if (cx/complex? a)
                  (unsupported-complex! :elem/max)
                  (tensor/reshape (dfn/max a b) (dtype/shape a)))))
