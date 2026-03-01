(ns
 la-linea-book.fourier-transform-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [scicloj.la-linea.transform :as bfft]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l32 (bfft/forward [1.0 0.0 -1.0 0.0]))


(deftest
 t5_l36
 (is ((fn [ct] (< (Math/abs (double (cx/re (ct 0)))) 1.0E-10)) v3_l32)))


(def
 v7_l42
 (let
  [signal
   [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
   spectrum
   (bfft/forward signal)
   recovered
   (bfft/inverse-real spectrum)]
  (dfn/reduce-max (dfn/abs (dfn/- recovered signal)))))


(deftest t8_l47 (is ((fn [v] (< v 1.0E-10)) v7_l42)))


(def
 v10_l56
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   n
   (count signal)
   spectrum
   (bfft/forward signal)
   time-energy
   (dfn/sum (dfn/* signal signal))
   magnitudes
   (la/abs spectrum)
   freq-energy
   (/ (dfn/sum (dfn/* magnitudes magnitudes)) n)]
  (< (Math/abs (- time-energy freq-energy)) 1.0E-10)))


(deftest t11_l64 (is (true? v10_l56)))


(def
 v13_l70
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
   (dfn/+ (dfn/* alpha x) (dfn/* beta y))
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


(deftest t14_l81 (is (true? v13_l70)))


(def
 v16_l90
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
   manual-conv
   (let
    [out (dtype/make-container :float64 n)]
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
      (dtype/set-value! out k s)))
    out)]
  (<
   (dfn/reduce-max (dfn/abs (dfn/- conv-result manual-conv)))
   1.0E-10)))


(deftest t17_l108 (is (true? v16_l90)))


(def v19_l114 (def N-vis 64))


(def
 v20_l116
 (def
  signal-composed
  (let
   [t
    (mapv
     (fn* [p1__71715#] (/ (double p1__71715#) N-vis))
     (range N-vis))]
   (mapv
    (fn
     [ti]
     (+
      (Math/sin (* 2 Math/PI 3 ti))
      (* 0.5 (Math/sin (* 2 Math/PI 7 ti)))))
    t))))


(def
 v22_l124
 (->
  (tc/dataset
   {:t
    (mapv
     (fn* [p1__71716#] (/ (double p1__71716#) N-vis))
     (range N-vis)),
    :amplitude (vec signal-composed)})
  (plotly/base {:=x :t, :=y :amplitude})
  (plotly/layer-line)
  plotly/plot))


(def
 v24_l133
 (let
  [spectrum (bfft/forward signal-composed) mags (la/abs spectrum)]
  (->
   (tc/dataset
    {:frequency (range (/ N-vis 2)),
     :magnitude (vec (take (/ N-vis 2) mags))})
   (plotly/base {:=x :frequency, :=y :magnitude})
   (plotly/layer-bar)
   plotly/plot)))


(def
 v26_l143
 (let
  [spectrum
   (bfft/forward signal-composed)
   mags
   (la/abs spectrum)
   half-mags
   (vec (take (/ N-vis 2) mags))
   peak-idx
   (sort-by
    (fn [i] (- (double (nth half-mags i))))
    (range (count half-mags)))]
  (= [3 7] (vec (sort (take 2 peak-idx))))))


(deftest t27_l150 (is (true? v26_l143)))


(def
 v29_l157
 (let
  [spectrum (bfft/forward [3.0 3.0 3.0 3.0])]
  {:dc (cx/re (spectrum 0)),
   :others
   [(la/abs (spectrum 1))
    (la/abs (spectrum 2))
    (la/abs (spectrum 3))]}))


(deftest
 t30_l161
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (double (:dc v)) 12.0)) 1.0E-10)
     (every? (fn* [p1__71717#] (< p1__71717# 1.0E-10)) (:others v))))
   v29_l157)))


(def
 v32_l166
 (let
  [spectrum (bfft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0))),
   :nyquist (double (cx/re (spectrum 2)))}))


(deftest
 t33_l170
 (is
  ((fn
    [v]
    (and
     (< (:dc v) 1.0E-10)
     (< (Math/abs (- (:nyquist v) 4.0)) 1.0E-10)))
   v32_l166)))


(def
 v35_l177
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


(deftest t36_l183 (is (true? v35_l177)))


(def
 v38_l189
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   dct
   (bfft/dct-forward signal)
   recovered
   (bfft/dct-inverse dct)]
  (< (dfn/reduce-max (dfn/abs (dfn/- recovered signal))) 1.0E-10)))


(deftest t39_l194 (is (true? v38_l189)))
