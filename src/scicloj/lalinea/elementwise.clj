(ns scicloj.lalinea.elementwise
  "Tape-aware element-wise operations with complex dispatch.

   Each function records on the tape (when active) and dispatches
   on `ct/complex?`. Functions without meaningful complex analogues
   throw on complex input."
  (:refer-clojure :exclude [abs min max eq conj > < >= <= + - * /])
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
                         (let [base (-> idx (quot 2) (clojure.core/* 2))
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
  (tape/record! :el/sq [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-mul a a)
                    (rt/->rt (dfn/sq a))))))

(defn sqrt
  "Element-wise square root."
  [a]
  (tape/record! :el/sqrt [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sqrt(z) = sqrt(|z|) * e^(i*theta/2)
                    (complex-unary a (fn [x y]
                                       (let [r (Math/sqrt (Math/sqrt (clojure.core/+ (clojure.core/* x x) (clojure.core/* y y))))
                                             theta (clojure.core// (Math/atan2 y x) 2.0)]
                                         [(clojure.core/* r (Math/cos theta))
                                          (clojure.core/* r (Math/sin theta))])))
                    (rt/->rt (dfn/sqrt a))))))

(defn pow
  "Element-wise power. Real only."
  [a exponent]
  (tape/record! :el/pow [a exponent]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/pow)
                    (rt/->rt (dfn/pow a (double exponent)))))))

(defn cbrt
  "Element-wise cube root. Real only."
  [a]
  (tape/record! :el/cbrt [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/cbrt)
                    (rt/->rt (dfn/cbrt a))))))

;; ---------------------------------------------------------------------------
;; Exponential and logarithmic
;; ---------------------------------------------------------------------------

(defn exp
  "Element-wise exponential."
  [a]
  (tape/record! :el/exp [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; exp(x+iy) = e^x * (cos y + i sin y)
                    (complex-unary a (fn [x y]
                                       (let [ex (Math/exp x)]
                                         [(clojure.core/* ex (Math/cos y))
                                          (clojure.core/* ex (Math/sin y))])))
                    (rt/->rt (dfn/exp a))))))

(defn log
  "Element-wise natural logarithm."
  [a]
  (tape/record! :el/log [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; log(z) = ln|z| + i*arg(z)
                    (complex-unary a (fn [x y]
                                       [(Math/log (Math/sqrt (clojure.core/+ (clojure.core/* x x) (clojure.core/* y y))))
                                        (Math/atan2 y x)]))
                    (rt/->rt (dfn/log a))))))

(defn log10
  "Element-wise base-10 logarithm. Real only."
  [a]
  (tape/record! :el/log10 [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/log10)
                    (rt/->rt (dfn/log10 a))))))

;; ---------------------------------------------------------------------------
;; Trigonometric
;; ---------------------------------------------------------------------------

(defn sin
  "Element-wise sine."
  [a]
  (tape/record! :el/sin [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sin(x+iy) = sin(x)cosh(y) + i*cos(x)sinh(y)
                    (complex-unary a (fn [x y]
                                       [(clojure.core/* (Math/sin x) (Math/cosh y))
                                        (clojure.core/* (Math/cos x) (Math/sinh y))]))
                    (rt/->rt (dfn/sin a))))))

(defn cos
  "Element-wise cosine."
  [a]
  (tape/record! :el/cos [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; cos(x+iy) = cos(x)cosh(y) - i*sin(x)sinh(y)
                    (complex-unary a (fn [x y]
                                       [(clojure.core/* (Math/cos x) (Math/cosh y))
                                        (clojure.core/- (clojure.core/* (Math/sin x) (Math/sinh y)))]))
                    (rt/->rt (dfn/cos a))))))

(defn tan
  "Element-wise tangent. Real only."
  [a]
  (tape/record! :el/tan [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/tan)
                    (rt/->rt (dfn/tan a))))))

;; ---------------------------------------------------------------------------
;; Hyperbolic
;; ---------------------------------------------------------------------------

(defn sinh
  "Element-wise hyperbolic sine."
  [a]
  (tape/record! :el/sinh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; sinh(x+iy) = sinh(x)cos(y) + i*cosh(x)sin(y)
                    (complex-unary a (fn [x y]
                                       [(clojure.core/* (Math/sinh x) (Math/cos y))
                                        (clojure.core/* (Math/cosh x) (Math/sin y))]))
                    (rt/->rt (dfn/sinh a))))))

(defn cosh
  "Element-wise hyperbolic cosine."
  [a]
  (tape/record! :el/cosh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    ;; cosh(x+iy) = cosh(x)cos(y) + i*sinh(x)sin(y)
                    (complex-unary a (fn [x y]
                                       [(clojure.core/* (Math/cosh x) (Math/cos y))
                                        (clojure.core/* (Math/sinh x) (Math/sin y))]))
                    (rt/->rt (dfn/cosh a))))))

(defn tanh
  "Element-wise hyperbolic tangent. Real only."
  [a]
  (tape/record! :el/tanh [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/tanh)
                    (rt/->rt (dfn/tanh a))))))

;; ---------------------------------------------------------------------------
;; Absolute value
;; ---------------------------------------------------------------------------

(defn abs
  "Element-wise absolute value (magnitude for complex). Returns a RealTensor."
  [a]
  (tape/record! :el/abs [a]
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
  (tape/record! :el/sum [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-sum a)
                    (double (dfn/sum a))))))

(defn mean
  "Mean of all elements. Real only."
  [a]
  (tape/record! :el/mean [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/mean)
                    (double (dfn/mean a))))))

;; ---------------------------------------------------------------------------
;; Rounding and comparison (real only)
;; ---------------------------------------------------------------------------

(defn floor
  "Element-wise floor. Real only."
  [a]
  (tape/record! :el/floor [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/floor)
                    (rt/->rt (dfn/floor a))))))

(defn ceil
  "Element-wise ceiling. Real only."
  [a]
  (tape/record! :el/ceil [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/ceil)
                    (rt/->rt (dfn/ceil a))))))

(defn min
  "Element-wise minimum. Real only."
  [a b]
  (tape/record! :el/min [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/min)
                    (rt/->rt (dfn/min a b))))))

(defn max
  "Element-wise maximum. Real only."
  [a b]
  (tape/record! :el/max [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/max)
                    (rt/->rt (dfn/max a b))))))
(defn /
  "Element-wise division. Supports both real and complex inputs."
  [a b]
  (tape/record! :el// [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (ct/ct-div a b)
                    (rt/->rt (dfn// a b))))))

;; ---------------------------------------------------------------------------
;; Inverse trigonometric (real only)
;; ---------------------------------------------------------------------------

(defn asin
  "Element-wise arcsine. Real only."
  [a]
  (tape/record! :el/asin [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/asin)
                    (rt/->rt (dfn/asin a))))))

(defn acos
  "Element-wise arccosine. Real only."
  [a]
  (tape/record! :el/acos [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/acos)
                    (rt/->rt (dfn/acos a))))))

(defn atan
  "Element-wise arctangent. Real only."
  [a]
  (tape/record! :el/atan [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/atan)
                    (rt/->rt (dfn/atan a))))))

;; ---------------------------------------------------------------------------
;; Additional exponential/logarithmic (real only)
;; ---------------------------------------------------------------------------

(defn log1p
  "Element-wise log(1 + x), accurate for small x. Real only."
  [a]
  (tape/record! :el/log1p [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/log1p)
                    (rt/->rt (dfn/log1p a))))))

(defn expm1
  "Element-wise exp(x) - 1, accurate for small x. Real only."
  [a]
  (tape/record! :el/expm1 [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/expm1)
                    (rt/->rt (dfn/expm1 a))))))

;; ---------------------------------------------------------------------------
;; Additional rounding/clipping (real only)
;; ---------------------------------------------------------------------------

(defn round
  "Element-wise rounding to nearest integer. Real only."
  [a]
  (tape/record! :el/round [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/round)
                    (rt/->rt (dfn/rint a))))))

(defn clip
  "Element-wise clipping to [lo, hi]. Real only."
  [a lo hi]
  (tape/record! :el/clip [a lo hi]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/clip)
                    (let [lo (double lo)
                          hi (double hi)]
                      (rt/->rt (dfn/min (dfn/max a lo) hi)))))))

;; ---------------------------------------------------------------------------
;; Reductions (additional)
;; ---------------------------------------------------------------------------

(defn reduce-max
  "Maximum element value. Returns double. Real only."
  [a]
  (tape/record! :el/reduce-max [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/reduce-max)
                    (double (dfn/reduce-max a))))))

(defn reduce-min
  "Minimum element value. Returns double. Real only."
  [a]
  (tape/record! :el/reduce-min [a]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (unsupported-complex! :el/reduce-min)
                    (double (dfn/reduce-min a))))))

;; ---------------------------------------------------------------------------
;; Comparison (element-wise)
;; ---------------------------------------------------------------------------

(defn >
  "Element-wise greater-than. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/> [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/>)
                    (rt/->rt (dtype/elemwise-cast (dfn/> a b) :float64))))))

(defn <
  "Element-wise less-than. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/< [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/<)
                    (rt/->rt (dtype/elemwise-cast (dfn/< a b) :float64))))))

(defn >=
  "Element-wise greater-or-equal. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/>= [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/>=)
                    (rt/->rt (dtype/elemwise-cast (dfn/>= a b) :float64))))))

(defn <=
  "Element-wise less-or-equal. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/<= [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/<=)
                    (rt/->rt (dtype/elemwise-cast (dfn/<= a b) :float64))))))

(defn eq
  "Element-wise equality. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/eq [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/eq)
                    (rt/->rt (dtype/elemwise-cast (dfn/eq a b) :float64))))))

(defn not-eq
  "Element-wise not-equal. Returns a RealTensor of 0.0/1.0. Real only."
  [a b]
  (tape/record! :el/not-eq [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (or (ct/complex? a) (ct/complex? b))
                    (unsupported-complex! :el/not-eq)
                    (rt/->rt (dtype/elemwise-cast (dfn/not-eq a b) :float64))))))

;; ---------------------------------------------------------------------------
;; Element-wise multiply
;; ---------------------------------------------------------------------------

(defn *
  "Element-wise multiply (Hadamard product for real, pointwise complex multiply for complex)."
  [a b]
  (tape/record! :el/* [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (ct/ct-mul a b)
                    (rt/->rt (dfn/* a b))))))

;; ---------------------------------------------------------------------------
;; Complex-aware element-wise operations
;; ---------------------------------------------------------------------------

(defn re
  "Real part(s). For complex: returns a RealTensor view (or double for scalars).
   For real: returns the input unchanged."
  [a]
  (tape/record! :el/re [a]
                (if (ct/complex? a)
                  (ct/re a)
                  a)))

(defn im
  "Imaginary part(s). For complex: returns a RealTensor view (or double for scalars).
   For real: returns zeros matching the input shape."
  [a]
  (tape/record! :el/im [a]
                (if (ct/complex? a)
                  (ct/im a)
                  (let [shape (dtype/shape (rt/ensure-tensor a))]
                    (rt/->rt (dtt/compute-tensor (vec shape) (fn [& _] 0.0) :float64))))))

(defn conj
  "Complex conjugate. For complex: negates imaginary parts.
   For real: returns the input unchanged."
  [a]
  (tape/record! :el/conj [a]
                (if (ct/complex? a)
                  (ct/ct-conj a)
                  a)))

;; ---------------------------------------------------------------------------
;; Reduction
;; ---------------------------------------------------------------------------

(defn reduce-*
  "Product of all elements. Returns a double. Real only."
  [a]
  (tape/record! :el/reduce-* [a]
                (let [a (rt/ensure-tensor a)]
                  (reduce clojure.core/* (dtype/->reader a :float64)))))

;; ---------------------------------------------------------------------------
;; Vector-space operations (also available in la/)
;; ---------------------------------------------------------------------------

(defn +
  "Element-wise addition."
  [a b]
  (tape/record! :el/+ [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (ct/ct-add a b)
                    (rt/->rt (dfn/+ a b))))))

(defn -
  "Element-wise subtraction."
  [a b]
  (tape/record! :el/- [a b]
                (let [a (rt/ensure-tensor a) b (rt/ensure-tensor b)]
                  (if (ct/complex? a)
                    (ct/ct-sub a b)
                    (rt/->rt (dfn/- a b))))))

(defn scale
  "Scalar multiply. Returns α · a."
  [a alpha]
  (tape/record! :el/scale [a alpha]
                (let [a (rt/ensure-tensor a)]
                  (if (ct/complex? a)
                    (ct/ct-scale a alpha)
                    (rt/->rt (dfn/* a (double alpha)))))))
