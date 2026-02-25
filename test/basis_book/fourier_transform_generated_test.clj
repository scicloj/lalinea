(ns
 basis-book.fourier-transform-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [scicloj.basis.transform :as bfft]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (bfft/forward [1.0 0.0 -1.0 0.0]))


(deftest
 t5_l34
 (is ((fn [ct] (< (Math/abs (double (cx/re (ct 0)))) 1.0E-10)) v3_l30)))


(def
 v7_l40
 (let
  [signal
   [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
   spectrum
   (bfft/forward signal)
   recovered
   (bfft/inverse-real spectrum)]
  (dfn/reduce-max (dfn/abs (dfn/- recovered (double-array signal))))))


(deftest t8_l45 (is ((fn [v] (< v 1.0E-10)) v7_l40)))


(def
 v10_l54
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   n
   (count signal)
   spectrum
   (bfft/forward signal)
   time-energy
   (dfn/sum (dfn/* (double-array signal) (double-array signal)))
   magnitudes
   (la/abs spectrum)
   freq-energy
   (/ (dfn/sum (dfn/* magnitudes magnitudes)) n)]
  (< (Math/abs (- time-energy freq-energy)) 1.0E-10)))


(deftest t11_l62 (is (true? v10_l54)))


(def
 v13_l68
 (let
  [x
   [1.0 2.0 3.0 4.0]
   y
   [5.0 6.0 7.0 8.0]
   alpha
   2.0
   beta
   -1.5
   combined
   (double-array
    (dfn/+
     (dfn/* alpha (double-array x))
     (dfn/* beta (double-array y))))
   lhs
   (bfft/forward combined)
   rhs
   (la/add
    (la/scale alpha (bfft/forward x))
    (la/scale beta (bfft/forward y)))]
  (and
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/re lhs) (cx/re rhs))))
    1.0E-10)
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/im lhs) (cx/im rhs))))
    1.0E-10))))


(deftest t14_l79 (is (true? v13_l68)))


(def
 v16_l88
 (let
  [x
   [1.0 2.0 0.0 0.0]
   y
   [1.0 0.0 1.0 0.0]
   Fx
   (bfft/forward x)
   Fy
   (bfft/forward y)
   product-spectrum
   (la/mul Fx Fy)
   conv-result
   (bfft/inverse-real product-spectrum)
   n
   (count x)
   xarr
   (double-array x)
   yarr
   (double-array y)
   manual-conv
   (let
    [out (double-array n)]
    (dotimes
     [k n]
     (let
      [s
       (loop
        [j 0 acc 0.0]
        (if
         (>= j n)
         acc
         (recur
          (inc j)
          (+ acc (* (aget xarr j) (aget yarr (mod (- k j) n)))))))]
      (aset out k s)))
    out)]
  (<
   (dfn/reduce-max (dfn/abs (dfn/- conv-result manual-conv)))
   1.0E-10)))


(deftest t17_l108 (is (true? v16_l88)))


(def
 v19_l115
 (let
  [spectrum (bfft/forward [3.0 3.0 3.0 3.0])]
  {:dc (cx/re (spectrum 0)),
   :others
   [(la/abs (spectrum 1))
    (la/abs (spectrum 2))
    (la/abs (spectrum 3))]}))


(deftest
 t20_l119
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (double (:dc v)) 12.0)) 1.0E-10)
     (every? (fn* [p1__90549#] (< p1__90549# 1.0E-10)) (:others v))))
   v19_l115)))


(def
 v22_l124
 (let
  [spectrum (bfft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0))),
   :nyquist (double (cx/re (spectrum 2)))}))


(deftest
 t23_l128
 (is
  ((fn
    [v]
    (and
     (< (:dc v) 1.0E-10)
     (< (Math/abs (- (:nyquist v) 4.0)) 1.0E-10)))
   v22_l124)))


(def
 v25_l135
 (let
  [signal
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   spectrum
   (bfft/forward-complex signal)
   recovered
   (bfft/inverse spectrum)]
  (and
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/re recovered) (cx/re signal))))
    1.0E-10)
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/im recovered) (cx/im signal))))
    1.0E-10))))


(deftest t26_l141 (is (true? v25_l135)))


(def
 v28_l147
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   dct
   (bfft/dct-forward signal)
   recovered
   (bfft/dct-inverse dct)]
  (<
   (dfn/reduce-max (dfn/abs (dfn/- recovered (double-array signal))))
   1.0E-10)))


(deftest t29_l152 (is (true? v28_l147)))
