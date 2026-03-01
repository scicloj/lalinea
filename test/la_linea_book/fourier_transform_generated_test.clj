(ns
 la-linea-book.fourier-transform-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [scicloj.la-linea.transform :as bfft]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (bfft/forward [1.0 0.0 -1.0 0.0]))


(deftest
 t5_l30
 (is ((fn [ct] (< (Math/abs (double (cx/re (ct 0)))) 1.0E-10)) v3_l26)))


(def
 v7_l36
 (let
  [signal
   [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
   spectrum
   (bfft/forward signal)
   recovered
   (bfft/inverse-real spectrum)]
  (dfn/reduce-max (dfn/abs (dfn/- recovered (double-array signal))))))


(deftest t8_l41 (is ((fn [v] (< v 1.0E-10)) v7_l36)))


(def
 v10_l50
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


(deftest t11_l58 (is (true? v10_l50)))


(def
 v13_l64
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
    (la/scale (bfft/forward x) alpha)
    (la/scale (bfft/forward y) beta))]
  (and
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/re lhs) (cx/re rhs))))
    1.0E-10)
   (<
    (dfn/reduce-max (dfn/abs (dfn/- (cx/im lhs) (cx/im rhs))))
    1.0E-10))))


(deftest t14_l75 (is (true? v13_l64)))


(def
 v16_l84
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


(deftest t17_l104 (is (true? v16_l84)))


(def
 v19_l111
 (let
  [spectrum (bfft/forward [3.0 3.0 3.0 3.0])]
  {:dc (cx/re (spectrum 0)),
   :others
   [(la/abs (spectrum 1))
    (la/abs (spectrum 2))
    (la/abs (spectrum 3))]}))


(deftest
 t20_l115
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (double (:dc v)) 12.0)) 1.0E-10)
     (every? (fn* [p1__70307#] (< p1__70307# 1.0E-10)) (:others v))))
   v19_l111)))


(def
 v22_l120
 (let
  [spectrum (bfft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0))),
   :nyquist (double (cx/re (spectrum 2)))}))


(deftest
 t23_l124
 (is
  ((fn
    [v]
    (and
     (< (:dc v) 1.0E-10)
     (< (Math/abs (- (:nyquist v) 4.0)) 1.0E-10)))
   v22_l120)))


(def
 v25_l131
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


(deftest t26_l137 (is (true? v25_l131)))


(def
 v28_l143
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


(deftest t29_l148 (is (true? v28_l143)))
