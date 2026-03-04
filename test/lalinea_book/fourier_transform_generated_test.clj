(ns
 lalinea-book.fourier-transform-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.transform :as ft]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l35 (ft/forward [1.0 0.0 -1.0 0.0]))


(deftest
 t5_l39
 (is ((fn [ct] (< (abs (double (la/re (ct 0)))) 1.0E-10)) v3_l35)))


(def
 v7_l45
 (let
  [signal
   [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
   spectrum
   (ft/forward signal)
   recovered
   (ft/inverse-real spectrum)]
  (elem/reduce-max (elem/abs (la/sub recovered signal)))))


(deftest t8_l50 (is ((fn [v] (< v 1.0E-10)) v7_l45)))


(def
 v10_l59
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   n
   (count signal)
   spectrum
   (ft/forward signal)
   time-energy
   (la/sum (la/mul signal signal))
   magnitudes
   (la/abs spectrum)
   freq-energy
   (/ (la/sum (la/mul magnitudes magnitudes)) n)]
  (< (abs (- time-energy freq-energy)) 1.0E-10)))


(deftest t11_l67 (is (true? v10_l59)))


(def
 v13_l73
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
   (la/add (la/mul alpha x) (la/mul beta y))
   lhs
   (ft/forward combined)
   rhs
   (la/add
    (la/scale (ft/forward x) alpha)
    (la/scale (ft/forward y) beta))]
  (and
   (<
    (elem/reduce-max (elem/abs (la/sub (la/re lhs) (la/re rhs))))
    1.0E-10)
   (<
    (elem/reduce-max (elem/abs (la/sub (la/im lhs) (la/im rhs))))
    1.0E-10))))


(deftest t14_l84 (is (true? v13_l73)))


(def
 v16_l93
 (let
  [x
   [1.0 2.0 0.0 0.0]
   y
   [1.0 0.0 1.0 0.0]
   Fx
   (ft/forward x)
   Fy
   (ft/forward y)
   product-spectrum
   (la/mul Fx Fy)
   conv-result
   (ft/inverse-real product-spectrum)
   n
   (count x)
   manual-conv
   (let
    [out (t/make-container :float64 n)]
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
          (+ acc (* (double (x j)) (double (y (mod (- k j) n))))))))]
      (t/set-value! out k s)))
    out)]
  (<
   (elem/reduce-max (elem/abs (la/sub conv-result manual-conv)))
   1.0E-10)))


(deftest t17_l111 (is (true? v16_l93)))


(def v19_l117 (def N-vis 64))


(def
 v20_l119
 (def
  signal-composed
  (t/->real-tensor
   (t/clone
    (t/make-reader
     :float64
     N-vis
     (let
      [ti (/ (double idx) N-vis)]
      (+
       (math/sin (* 2 math/PI 3 ti))
       (* 0.5 (math/sin (* 2 math/PI 7 ti))))))))))


(def
 v22_l129
 (->
  (tc/dataset
   {:t (t/make-reader :float64 N-vis (/ (double idx) N-vis)),
    :amplitude signal-composed})
  (plotly/base {:=x :t, :=y :amplitude})
  (plotly/layer-line)
  plotly/plot))


(def
 v24_l138
 (let
  [spectrum (ft/forward signal-composed) mags (la/abs spectrum)]
  (->
   (tc/dataset
    {:frequency (range (/ N-vis 2)),
     :magnitude (take (/ N-vis 2) mags)})
   (plotly/base {:=x :frequency, :=y :magnitude})
   (plotly/layer-bar)
   plotly/plot)))


(def
 v26_l148
 (let
  [spectrum
   (ft/forward signal-composed)
   mags
   (la/abs spectrum)
   half-n
   (/ N-vis 2)
   peak-idx
   (sort-by (fn [i] (- (double (mags i)))) (range half-n))]
  (= [3 7] (sort (take 2 peak-idx)))))


(deftest t27_l155 (is (true? v26_l148)))


(def
 v29_l162
 (let
  [spectrum (ft/forward [3.0 3.0 3.0 3.0])]
  {:dc (la/re (spectrum 0)),
   :others
   [(la/abs (spectrum 1))
    (la/abs (spectrum 2))
    (la/abs (spectrum 3))]}))


(deftest
 t30_l166
 (is
  ((fn
    [v]
    (and
     (< (abs (- (double (:dc v)) 12.0)) 1.0E-10)
     (every? (fn* [p1__77275#] (< p1__77275# 1.0E-10)) (:others v))))
   v29_l162)))


(def
 v32_l171
 (let
  [spectrum (ft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0))),
   :nyquist (double (la/re (spectrum 2)))}))


(deftest
 t33_l175
 (is
  ((fn
    [v]
    (and (< (:dc v) 1.0E-10) (< (abs (- (:nyquist v) 4.0)) 1.0E-10)))
   v32_l171)))


(def
 v35_l182
 (let
  [signal
   (t/complex-tensor [1.0 0.0] [0.0 1.0])
   spectrum
   (ft/forward-complex signal)
   recovered
   (ft/inverse spectrum)]
  (and
   (<
    (elem/reduce-max
     (elem/abs (la/sub (la/re recovered) (la/re signal))))
    1.0E-10)
   (<
    (elem/reduce-max
     (elem/abs (la/sub (la/im recovered) (la/im signal))))
    1.0E-10))))


(deftest t36_l188 (is (true? v35_l182)))


(def
 v38_l194
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   dct
   (ft/dct-forward signal)
   recovered
   (ft/dct-inverse dct)]
  (< (elem/reduce-max (elem/abs (la/sub recovered signal))) 1.0E-10)))


(deftest t39_l199 (is (true? v38_l194)))
