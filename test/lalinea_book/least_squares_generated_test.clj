(ns
 lalinea-book.least-squares-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l46 (def x-data (t/make-reader :float64 20 (* 0.1 idx))))


(def
 v4_l50
 (def
  noise-linear
  (t/matrix
   [0.343
    0.276
    -0.285
    -0.332
    0.084
    0.205
    -0.245
    -0.419
    -0.057
    0.446
    0.241
    -0.036
    0.423
    -0.192
    -0.363
    0.106
    -0.147
    0.165
    -0.361
    0.096])))


(def
 v5_l55
 (def y-linear (la/add (la/add 2.0 (la/mul 3.0 x-data)) noise-linear)))


(def
 v7_l60
 (def
  A-linear
  (t/compute-matrix
   (count x-data)
   2
   (fn [i j] (if (zero? j) 1.0 (double (x-data i)))))))


(def v8_l64 A-linear)


(deftest t9_l66 (is ((fn [m] (= [20 2] (t/shape m))) v8_l64)))


(def v11_l71 (def y-col (t/column y-linear)))


(def
 v13_l81
 (def
  c-linear
  (la/solve
   (la/mmul (la/transpose A-linear) A-linear)
   (la/mmul (la/transpose A-linear) y-col))))


(def v14_l85 c-linear)


(deftest
 t15_l87
 (is
  ((fn
    [c]
    (and (< (abs (- (c 0 0) 2.0)) 0.5) (< (abs (- (c 1 0) 3.0)) 0.5)))
   v14_l85)))


(def
 v17_l97
 (def residual-linear (la/sub (la/mmul A-linear c-linear) y-col)))


(def
 v18_l100
 (def
  rms-linear
  (math/sqrt
   (/
    (la/sum (la/mul residual-linear residual-linear))
    (count x-data)))))


(def v19_l104 rms-linear)


(deftest t20_l106 (is ((fn [v] (< v 0.5)) v19_l104)))


(def
 v22_l111
 (let
  [c0
   (c-linear 0 0)
   c1
   (c-linear 1 0)
   x-fit
   (t/make-reader :float64 100 (* 0.019 idx))
   y-fit
   (la/add c0 (la/mul c1 x-fit))]
  (->
   (tc/dataset
    {:x x-data, :y y-linear, :type (repeat (count x-data) "data")})
   (tc/concat
    (tc/dataset {:x x-fit, :y y-fit, :type (repeat 100 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 8, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def
 v24_l136
 (def x-poly (t/make-reader :float64 30 (- (* 0.2 idx) 3.0))))


(def
 v25_l140
 (def
  noise-poly
  (t/matrix
   [0.132
    -0.541
    0.269
    0.37
    0.067
    0.284
    -0.096
    -0.651
    -0.228
    0.055
    0.483
    -0.301
    0.088
    -0.104
    0.612
    0.073
    -0.318
    0.247
    -0.428
    0.518
    -0.092
    0.146
    -0.19
    0.398
    0.031
    -0.263
    0.195
    -0.416
    0.347
    0.01])))


(def
 v26_l146
 (def
  y-poly
  (la/add
   (la/add 1.0 (la/mul -2.0 x-poly))
   (la/add (la/mul x-poly x-poly) noise-poly))))


(def
 v27_l150
 (def
  vandermonde
  (fn
   [xs degree]
   (t/compute-matrix
    (count xs)
    (inc degree)
    (fn [i j] (math/pow (double (xs i)) (double j)))))))


(def v28_l155 (def A-poly (vandermonde x-poly 2)))


(def
 v29_l157
 (def
  c-poly
  (la/solve
   (la/mmul (la/transpose A-poly) A-poly)
   (la/mmul (la/transpose A-poly) (t/column y-poly)))))


(def v30_l161 c-poly)


(deftest
 t31_l163
 (is
  ((fn
    [c]
    (and
     (< (abs (- (c 0 0) 1.0)) 1.0)
     (< (abs (- (c 1 0) -2.0)) 1.0)
     (< (abs (- (c 2 0) 1.0)) 1.0)))
   v30_l161)))


(def
 v33_l173
 (let
  [c0
   (c-poly 0 0)
   c1
   (c-poly 1 0)
   c2
   (c-poly 2 0)
   x-fit
   (t/make-reader :float64 100 (- (* 0.06 idx) 3.0))
   y-fit
   (la/add
    c0
    (la/add (la/mul c1 x-fit) (la/mul c2 (la/mul x-fit x-fit))))]
  (->
   (tc/dataset {:x x-poly, :y y-poly, :type (repeat 30 "data")})
   (tc/concat
    (tc/dataset {:x x-fit, :y y-fit, :type (repeat 100 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 8, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v35_l200 (def qr-result (la/qr A-poly)))


(def v37_l205 (def ncols (second (t/shape A-poly))))


(def v38_l207 (def Q1 (t/submatrix (:Q qr-result) :all (range ncols))))


(def v39_l208 (def R1 (t/submatrix (:R qr-result) (range ncols) :all)))


(def v41_l212 (la/norm (la/sub (la/mmul Q1 R1) A-poly)))


(deftest t42_l214 (is ((fn [v] (< v 1.0E-10)) v41_l212)))


(def
 v44_l219
 (def c-qr (la/solve R1 (la/mmul (la/transpose Q1) (t/column y-poly)))))


(def v46_l224 (la/norm (la/sub c-qr c-poly)))


(deftest t47_l226 (is ((fn [v] (< v 1.0E-10)) v46_l224)))


(def v49_l240 (def svd-result (la/svd A-poly)))


(def v51_l245 (def S-svd (:S svd-result)))


(def
 v52_l246
 (def U-thin (t/submatrix (:U svd-result) :all (range (count S-svd)))))


(def v53_l247 (def Vt-svd (:Vt svd-result)))


(def v55_l251 S-svd)


(deftest t56_l253 (is ((fn [v] (every? pos? v)) v55_l251)))


(def
 v58_l259
 (def
  c-svd
  (let
   [k
    (count S-svd)
    S-inv
    (t/diag (t/make-reader :float64 k (/ 1.0 (S-svd idx))))
    Ut-y
    (la/mmul (la/transpose U-thin) (t/column y-poly))]
   (la/mmul (la/transpose Vt-svd) (la/mmul S-inv Ut-y)))))


(def v60_l268 (la/norm (la/sub c-svd c-poly)))


(deftest t61_l270 (is ((fn [v] (< v 1.0E-8)) v60_l268)))


(def
 v63_l276
 (la/close? c-svd (la/mmul (la/pinv A-poly) (t/column y-poly))))


(deftest t64_l278 (is (true? v63_l276)))


(def
 v66_l290
 (def
  x-trig
  (t/make-reader :float64 40 (* (/ (* 2.0 math/PI) 40.0) idx))))


(def
 v67_l294
 (def
  noise-trig
  (t/matrix
   [-0.058
    0.218
    -0.109
    0.034
    0.192
    -0.277
    0.138
    0.065
    -0.193
    0.145
    -0.044
    0.291
    -0.18
    0.077
    0.253
    -0.116
    0.162
    -0.207
    0.098
    -0.151
    0.184
    -0.023
    0.211
    -0.138
    0.073
    -0.246
    0.119
    0.047
    -0.183
    0.264
    -0.091
    0.156
    -0.228
    0.103
    0.189
    -0.144
    0.268
    -0.076
    0.131
    -0.201])))


(def
 v68_l301
 (def
  y-trig
  (la/add
   (la/add 3.0 (la/mul 2.0 (elem/cos x-trig)))
   (la/add
    (la/mul -1.5 (elem/sin x-trig))
    (la/add (la/mul 0.5 (elem/cos (la/mul 2.0 x-trig))) noise-trig)))))


(def
 v69_l307
 (def
  A-trig
  (t/compute-matrix
   (count x-trig)
   5
   (fn
    [i j]
    (let
     [xi (double (x-trig i))]
     (case
      (int j)
      0
      1.0
      1
      (math/cos xi)
      2
      (math/sin xi)
      3
      (math/cos (* 2.0 xi))
      4
      (math/sin (* 2.0 xi))))))))


(def
 v70_l318
 (def
  c-trig
  (la/solve
   (la/mmul (la/transpose A-trig) A-trig)
   (la/mmul (la/transpose A-trig) (t/column y-trig)))))


(def v71_l322 c-trig)


(deftest
 t72_l324
 (is
  ((fn
    [c]
    (and
     (< (abs (- (c 0 0) 3.0)) 0.3)
     (< (abs (- (c 1 0) 2.0)) 0.3)
     (< (abs (- (c 2 0) -1.5)) 0.3)
     (< (abs (- (c 3 0) 0.5)) 0.3)))
   v71_l322)))


(def
 v74_l336
 (let
  [x-fit
   (t/make-reader :float64 200 (* (/ (* 2.0 math/PI) 200.0) idx))
   y-fit
   (la/add
    (la/mul (c-trig 0 0) 1.0)
    (la/add
     (la/mul (c-trig 1 0) (elem/cos x-fit))
     (la/add
      (la/mul (c-trig 2 0) (elem/sin x-fit))
      (la/add
       (la/mul (c-trig 3 0) (elem/cos (la/mul 2.0 x-fit)))
       (la/mul (c-trig 4 0) (elem/sin (la/mul 2.0 x-fit)))))))]
  (->
   (tc/dataset {:x x-trig, :y y-trig, :type (repeat 40 "data")})
   (tc/concat
    (tc/dataset {:x x-fit, :y y-fit, :type (repeat 200 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 6, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v76_l363 (def condition-number (la/condition-number A-poly)))


(def v77_l365 condition-number)


(deftest
 t78_l367
 (is ((fn [v] (and (> v 1.0) (Double/isFinite v))) v77_l365)))


(def
 v80_l373
 (def A-high (vandermonde (t/make-reader :float64 30 (* 1.0 idx)) 8)))


(def v81_l376 (la/condition-number A-high))


(deftest t82_l378 (is ((fn [v] (> v 1000000.0)) v81_l376)))
