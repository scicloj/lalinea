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


(def v3_l34 (ft/forward [1.0 0.0 -1.0 0.0]))


(deftest
 t5_l38
 (is ((fn [ct] (< (abs (double (la/re (ct 0)))) 1.0E-10)) v3_l34)))


(def
 v7_l44
 (let
  [signal
   [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
   spectrum
   (ft/forward signal)
   recovered
   (ft/inverse-real spectrum)]
  (elem/reduce-max (elem/abs (la/sub recovered signal)))))


(deftest t8_l49 (is ((fn [v] (< v 1.0E-10)) v7_l44)))


(def
 v10_l58
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


(deftest t11_l66 (is (true? v10_l58)))


(def
 v13_l72
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


(deftest t14_l83 (is (true? v13_l72)))


(def
 v16_l92
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


(deftest t17_l110 (is (true? v16_l92)))


(def v19_l116 (def N-vis 64))


(def
 v20_l118
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
 v22_l128
 (->
  (tc/dataset
   {:t (t/make-reader :float64 N-vis (/ (double idx) N-vis)),
    :amplitude signal-composed})
  (plotly/base {:=x :t, :=y :amplitude})
  (plotly/layer-line)
  plotly/plot))


(def
 v24_l137
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
 v26_l147
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


(deftest t27_l154 (is (true? v26_l147)))


(def
 v29_l161
 (let
  [spectrum (ft/forward [3.0 3.0 3.0 3.0])]
  {:dc (la/re (spectrum 0)),
   :others
   [(la/abs (spectrum 1))
    (la/abs (spectrum 2))
    (la/abs (spectrum 3))]}))


(deftest
 t30_l165
 (is
  ((fn
    [v]
    (and
     (< (abs (- (double (:dc v)) 12.0)) 1.0E-10)
     (every? (fn* [p1__83473#] (< p1__83473# 1.0E-10)) (:others v))))
   v29_l161)))


(def
 v32_l170
 (let
  [spectrum (ft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0))),
   :nyquist (double (la/re (spectrum 2)))}))


(deftest
 t33_l174
 (is
  ((fn
    [v]
    (and (< (:dc v) 1.0E-10) (< (abs (- (:nyquist v) 4.0)) 1.0E-10)))
   v32_l170)))


(def
 v35_l181
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


(deftest t36_l187 (is (true? v35_l181)))


(def
 v38_l193
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   dct
   (ft/dct-forward signal)
   recovered
   (ft/dct-inverse dct)]
  (< (elem/reduce-max (elem/abs (la/sub recovered signal))) 1.0E-10)))


(deftest t39_l198 (is (true? v38_l193)))
