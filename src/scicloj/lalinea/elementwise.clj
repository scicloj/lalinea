(ns scicloj.lalinea.elementwise
  "Tape-aware element-wise operations with complex dispatch.

   Each function records on the tape (when active) and dispatches
   on `ct/complex?`. Functions without meaningful complex analogues
   throw on complex input."
  (:refer-clojure :exclude [abs min max])
  (:require [scicloj.lalinea.tape :as tape]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.impl.complex-tensor :as ct]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.tensor :as dtt]))

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
  (let [t (ct/->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)]
    (ct/complex-tensor
     (dtt/reshape
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
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-mul a a)
                    (rt/->rt (dfn/sq a))))))

(defn sqrt
  "Element-wise square root."
  [a]
  (tape/record! :elem/sqrt [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sqrt(z) = sqrt(|z|) * e^(i*theta/2)
                    (complex-unary a (fn [x y]
                                       (let [r (Math/sqrt (Math/sqrt (+ (* x x) (* y y))))
                                             theta (/ (Math/atan2 y x) 2.0)]
                                         [(* r (Math/cos theta))
                                          (* r (Math/sin theta))])))
                    (rt/->rt (dfn/sqrt a))))))

(defn pow
  "Element-wise power. Real only."
  [a exponent]
  (tape/record! :elem/pow [a exponent]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/pow)
                    (rt/->rt (dfn/pow a (double exponent)))))))

(defn cbrt
  "Element-wise cube root. Real only."
  [a]
  (tape/record! :elem/cbrt [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/cbrt)
                    (rt/->rt (dfn/cbrt a))))))

;; ---------------------------------------------------------------------------
;; Exponential and logarithmic
;; ---------------------------------------------------------------------------

(defn exp
  "Element-wise exponential."
  [a]
  (tape/record! :elem/exp [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; exp(x+iy) = e^x * (cos y + i sin y)
                    (complex-unary a (fn [x y]
                                       (let [ex (Math/exp x)]
                                         [(* ex (Math/cos y))
                                          (* ex (Math/sin y))])))
                    (rt/->rt (dfn/exp a))))))

(defn log
  "Element-wise natural logarithm."
  [a]
  (tape/record! :elem/log [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; log(z) = ln|z| + i*arg(z)
                    (complex-unary a (fn [x y]
                                       [(Math/log (Math/sqrt (+ (* x x) (* y y))))
                                        (Math/atan2 y x)]))
                    (rt/->rt (dfn/log a))))))

(defn log10
  "Element-wise base-10 logarithm. Real only."
  [a]
  (tape/record! :elem/log10 [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/log10)
                    (rt/->rt (dfn/log10 a))))))

;; ---------------------------------------------------------------------------
;; Trigonometric
;; ---------------------------------------------------------------------------

(defn sin
  "Element-wise sine."
  [a]
  (tape/record! :elem/sin [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sin(x+iy) = sin(x)cosh(y) + i*cos(x)sinh(y)
                    (complex-unary a (fn [x y]
                                       [(* (Math/sin x) (Math/cosh y))
                                        (* (Math/cos x) (Math/sinh y))]))
                    (rt/->rt (dfn/sin a))))))

(defn cos
  "Element-wise cosine."
  [a]
  (tape/record! :elem/cos [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; cos(x+iy) = cos(x)cosh(y) - i*sin(x)sinh(y)
                    (complex-unary a (fn [x y]
                                       [(* (Math/cos x) (Math/cosh y))
                                        (- (* (Math/sin x) (Math/sinh y)))]))
                    (rt/->rt (dfn/cos a))))))

(defn tan
  "Element-wise tangent. Real only."
  [a]
  (tape/record! :elem/tan [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/tan)
                    (rt/->rt (dfn/tan a))))))

;; ---------------------------------------------------------------------------
;; Hyperbolic
;; ---------------------------------------------------------------------------

(defn sinh
  "Element-wise hyperbolic sine."
  [a]
  (tape/record! :elem/sinh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sinh(x+iy) = sinh(x)cos(y) + i*cosh(x)sin(y)
                    (complex-unary a (fn [x y]
                                       [(* (Math/sinh x) (Math/cos y))
                                        (* (Math/cosh x) (Math/sin y))]))
                    (rt/->rt (dfn/sinh a))))))

(defn cosh
  "Element-wise hyperbolic cosine."
  [a]
  (tape/record! :elem/cosh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; cosh(x+iy) = cosh(x)cos(y) + i*sinh(x)sin(y)
                    (complex-unary a (fn [x y]
                                       [(* (Math/cosh x) (Math/cos y))
                                        (* (Math/sinh x) (Math/sin y))]))
                    (rt/->rt (dfn/cosh a))))))

(defn tanh
  "Element-wise hyperbolic tangent. Real only."
  [a]
  (tape/record! :elem/tanh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/tanh)
                    (rt/->rt (dfn/tanh a))))))

;; ---------------------------------------------------------------------------
;; Absolute value
;; ---------------------------------------------------------------------------

(defn abs
  "Element-wise absolute value (magnitude for complex). Returns a RealTensor."
  [a]
  (tape/record! :elem/abs [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (rt/->rt (ct/ct-abs a))
                    (rt/->rt (dfn/abs a))))))

;; ---------------------------------------------------------------------------
;; Reductions
;; ---------------------------------------------------------------------------

(defn sum
  "Sum of all elements. Returns double for real, scalar ComplexTensor for complex."
  [a]
  (tape/record! :elem/sum [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-sum a)
                    (double (dfn/sum a))))))

(defn mean
  "Mean of all elements. Real only."
  [a]
  (tape/record! :elem/mean [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/mean)
                    (double (dfn/mean a))))))

;; ---------------------------------------------------------------------------
;; Rounding and comparison (real only)
;; ---------------------------------------------------------------------------

(defn floor
  "Element-wise floor. Real only."
  [a]
  (tape/record! :elem/floor [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/floor)
                    (rt/->rt (dfn/floor a))))))

(defn ceil
  "Element-wise ceiling. Real only."
  [a]
  (tape/record! :elem/ceil [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/ceil)
                    (rt/->rt (dfn/ceil a))))))

(defn min
  "Element-wise minimum. Real only."
  [a b]
  (tape/record! :elem/min [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/min)
                    (rt/->rt (dfn/min a b))))))

(defn max
  "Element-wise maximum. Real only."
  [a b]
  (tape/record! :elem/max [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/max)
                    (rt/->rt (dfn/max a b))))))
(defn div
  "Element-wise division. Real only."
  [a b]
  (tape/record! :elem/div [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :elem/div)
                    (rt/->rt (dfn// a b))))))

;; ---------------------------------------------------------------------------
;; Inverse trigonometric (real only)
;; ---------------------------------------------------------------------------

(defn asin
  "Element-wise arcsine. Real only."
  [a]
  (tape/record! :elem/asin [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/asin)
                    (rt/->rt (dfn/asin a))))))

(defn acos
  "Element-wise arccosine. Real only."
  [a]
  (tape/record! :elem/acos [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/acos)
                    (rt/->rt (dfn/acos a))))))

(defn atan
  "Element-wise arctangent. Real only."
  [a]
  (tape/record! :elem/atan [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/atan)
                    (rt/->rt (dfn/atan a))))))

;; ---------------------------------------------------------------------------
;; Additional exponential/logarithmic (real only)
;; ---------------------------------------------------------------------------

(defn log1p
  "Element-wise log(1 + x), accurate for small x. Real only."
  [a]
  (tape/record! :elem/log1p [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/log1p)
                    (rt/->rt (dfn/log1p a))))))

(defn expm1
  "Element-wise exp(x) - 1, accurate for small x. Real only."
  [a]
  (tape/record! :elem/expm1 [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/expm1)
                    (rt/->rt (dfn/expm1 a))))))

;; ---------------------------------------------------------------------------
;; Additional rounding/clipping (real only)
;; ---------------------------------------------------------------------------

(defn round
  "Element-wise rounding to nearest integer. Real only."
  [a]
  (tape/record! :elem/round [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/round)
                    (rt/->rt (dfn/rint a))))))

(defn clip
  "Element-wise clipping to [lo, hi]. Real only."
  [a lo hi]
  (tape/record! :elem/clip [a lo hi]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/clip)
                    (let [lo (double lo)
                          hi (double hi)]
                      (rt/->rt (dfn/min (dfn/max a lo) hi)))))))

;; ---------------------------------------------------------------------------
;; Reductions (additional)
;; ---------------------------------------------------------------------------

(defn reduce-max
  "Maximum element value. Returns double. Real only."
  [a]
  (tape/record! :elem/reduce-max [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/reduce-max)
                    (double (dfn/reduce-max a))))))

(defn reduce-min
  "Minimum element value. Returns double. Real only."
  [a]
  (tape/record! :elem/reduce-min [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :elem/reduce-min)
                    (double (dfn/reduce-min a))))))

;; ---------------------------------------------------------------------------
;; Comparison (element-wise)
;; ---------------------------------------------------------------------------

(defn gt
  "Element-wise greater-than. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :elem/gt [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :elem/gt)
                    (rt/->rt (dtype/elemwise-cast (dfn/> a b) :float64))))))
