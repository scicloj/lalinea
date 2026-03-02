(ns
 la-linea-book.least-squares-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l50 (def x-data (dtype/make-reader :float64 20 (* 0.1 idx))))


(def
 v4_l54
 (def
  noise-linear
  (tensor/->tensor
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
    0.096]
   :datatype
   :float64)))


(def
 v5_l60
 (def y-linear (dfn/+ (dfn/+ 2.0 (dfn/* 3.0 x-data)) noise-linear)))


(def
 v7_l65
 (def
  A-linear
  (let
   [m (count x-data)]
   (la/matrix (mapv (fn [i] [1.0 (x-data i)]) (range m))))))


(def v8_l71 A-linear)


(deftest t9_l73 (is ((fn [m] (= [20 2] (dtype/shape m))) v8_l71)))


(def v11_l78 (def y-col (la/column y-linear)))


(def
 v13_l88
 (def
  c-linear
  (la/solve
   (la/mmul (la/transpose A-linear) A-linear)
   (la/mmul (la/transpose A-linear) y-col))))


(def v14_l92 c-linear)


(deftest
 t15_l94
 (is
  ((fn
    [c]
    (and
     (< (abs (- (tensor/mget c 0 0) 2.0)) 0.5)
     (< (abs (- (tensor/mget c 1 0) 3.0)) 0.5)))
   v14_l92)))


(def
 v17_l104
 (def residual-linear (la/sub (la/mmul A-linear c-linear) y-col)))


(def
 v18_l107
 (def
  rms-linear
  (math/sqrt
   (/
    (dfn/sum (dfn/* residual-linear residual-linear))
    (count x-data)))))


(def v19_l111 rms-linear)


(deftest t20_l113 (is ((fn [v] (< v 0.5)) v19_l111)))


(def
 v22_l118
 (let
  [c0
   (tensor/mget c-linear 0 0)
   c1
   (tensor/mget c-linear 1 0)
   x-fit
   (dtype/make-reader :float64 100 (* 0.019 idx))
   y-fit
   (dfn/+ c0 (dfn/* c1 x-fit))]
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
 v24_l143
 (def x-poly (dtype/make-reader :float64 30 (- (* 0.2 idx) 3.0))))


(def
 v25_l147
 (def
  noise-poly
  (tensor/->tensor
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
    0.01]
   :datatype
   :float64)))


(def
 v26_l154
 (def
  y-poly
  (dfn/+
   (dfn/+ 1.0 (dfn/* -2.0 x-poly))
   (dfn/+ (dfn/* x-poly x-poly) noise-poly))))


(def
 v27_l158
 (def
  vandermonde
  (fn
   [xs degree]
   (let
    [m (count xs)]
    (la/matrix
     (mapv
      (fn [i] (mapv (fn [j] (math/pow (xs i) j)) (range (inc degree))))
      (range m)))))))


(def v28_l167 (def A-poly (vandermonde x-poly 2)))


(def
 v29_l169
 (def
  c-poly
  (la/solve
   (la/mmul (la/transpose A-poly) A-poly)
   (la/mmul (la/transpose A-poly) (la/column y-poly)))))


(def v30_l173 c-poly)


(deftest
 t31_l175
 (is
  ((fn
    [c]
    (and
     (< (abs (- (tensor/mget c 0 0) 1.0)) 1.0)
     (< (abs (- (tensor/mget c 1 0) -2.0)) 1.0)
     (< (abs (- (tensor/mget c 2 0) 1.0)) 1.0)))
   v30_l173)))


(def
 v33_l185
 (let
  [c0
   (tensor/mget c-poly 0 0)
   c1
   (tensor/mget c-poly 1 0)
   c2
   (tensor/mget c-poly 2 0)
   x-fit
   (dtype/make-reader :float64 100 (- (* 0.06 idx) 3.0))
   y-fit
   (dfn/+ c0 (dfn/+ (dfn/* c1 x-fit) (dfn/* c2 (dfn/* x-fit x-fit))))]
  (->
   (tc/dataset {:x x-poly, :y y-poly, :type (repeat 30 "data")})
   (tc/concat
    (tc/dataset {:x x-fit, :y y-fit, :type (repeat 100 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 8, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v35_l212 (def qr-result (la/qr A-poly)))


(def v37_l217 (def ncols (second (dtype/shape A-poly))))


(def v38_l219 (def Q1 (la/submatrix (:Q qr-result) :all (range ncols))))


(def v39_l220 (def R1 (la/submatrix (:R qr-result) (range ncols) :all)))


(def v41_l224 (la/norm (la/sub (la/mmul Q1 R1) A-poly)))


(deftest t42_l226 (is ((fn [v] (< v 1.0E-10)) v41_l224)))


(def
 v44_l231
 (def
  c-qr
  (la/solve R1 (la/mmul (la/transpose Q1) (la/column y-poly)))))


(def v46_l236 (la/norm (la/sub c-qr c-poly)))


(deftest t47_l238 (is ((fn [v] (< v 1.0E-10)) v46_l236)))


(def v49_l252 (def svd-result (la/svd A-poly)))


(def v51_l257 (def S-svd (:S svd-result)))


(def
 v52_l258
 (def U-thin (la/submatrix (:U svd-result) :all (range (count S-svd)))))


(def v53_l259 (def Vt-svd (:Vt svd-result)))


(def v55_l263 S-svd)


(deftest t56_l265 (is ((fn [v] (every? pos? v)) v55_l263)))


(def
 v58_l271
 (def
  c-svd
  (let
   [k
    (count S-svd)
    S-inv
    (la/diag (dtype/make-reader :float64 k (/ 1.0 (S-svd idx))))
    Ut-y
    (la/mmul (la/transpose U-thin) (la/column y-poly))]
   (la/mmul (la/transpose Vt-svd) (la/mmul S-inv Ut-y)))))


(def v60_l280 (la/norm (la/sub c-svd c-poly)))


(deftest t61_l282 (is ((fn [v] (< v 1.0E-8)) v60_l280)))


(def
 v63_l288
 (la/close? c-svd (la/mmul (la/pinv A-poly) (la/column y-poly))))


(deftest t64_l290 (is (true? v63_l288)))


(def
 v66_l302
 (def
  x-trig
  (dtype/make-reader :float64 40 (* (/ (* 2.0 math/PI) 40.0) idx))))


(def
 v67_l306
 (def
  noise-trig
  (tensor/->tensor
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
    -0.201]
   :datatype
   :float64)))


(def
 v68_l314
 (def
  y-trig
  (dfn/+
   (dfn/+ 3.0 (dfn/* 2.0 (dfn/cos x-trig)))
   (dfn/+
    (dfn/* -1.5 (dfn/sin x-trig))
    (dfn/+ (dfn/* 0.5 (dfn/cos (dfn/* 2.0 x-trig))) noise-trig)))))


(def
 v69_l320
 (def
  A-trig
  (let
   [m (count x-trig)]
   (la/matrix
    (mapv
     (fn
      [i]
      (let
       [xi (x-trig i)]
       [1.0
        (math/cos xi)
        (math/sin xi)
        (math/cos (* 2.0 xi))
        (math/sin (* 2.0 xi))]))
     (range m))))))


(def
 v70_l332
 (def
  c-trig
  (la/solve
   (la/mmul (la/transpose A-trig) A-trig)
   (la/mmul (la/transpose A-trig) (la/column y-trig)))))


(def v71_l336 c-trig)


(deftest
 t72_l338
 (is
  ((fn
    [c]
    (and
     (< (abs (- (tensor/mget c 0 0) 3.0)) 0.3)
     (< (abs (- (tensor/mget c 1 0) 2.0)) 0.3)
     (< (abs (- (tensor/mget c 2 0) -1.5)) 0.3)
     (< (abs (- (tensor/mget c 3 0) 0.5)) 0.3)))
   v71_l336)))


(def
 v74_l350
 (let
  [x-fit
   (dtype/make-reader :float64 200 (* (/ (* 2.0 math/PI) 200.0) idx))
   y-fit
   (dfn/+
    (dfn/* (tensor/mget c-trig 0 0) 1.0)
    (dfn/+
     (dfn/* (tensor/mget c-trig 1 0) (dfn/cos x-fit))
     (dfn/+
      (dfn/* (tensor/mget c-trig 2 0) (dfn/sin x-fit))
      (dfn/+
       (dfn/* (tensor/mget c-trig 3 0) (dfn/cos (dfn/* 2.0 x-fit)))
       (dfn/*
        (tensor/mget c-trig 4 0)
        (dfn/sin (dfn/* 2.0 x-fit)))))))]
  (->
   (tc/dataset {:x x-trig, :y y-trig, :type (repeat 40 "data")})
   (tc/concat
    (tc/dataset {:x x-fit, :y y-fit, :type (repeat 200 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 6, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v76_l377 (def condition-number (la/condition-number A-poly)))


(def v77_l379 condition-number)


(deftest
 t78_l381
 (is ((fn [v] (and (> v 1.0) (Double/isFinite v))) v77_l379)))


(def
 v80_l387
 (def
  A-high
  (vandermonde (dtype/make-reader :float64 30 (* 1.0 idx)) 8)))


(def v81_l390 (la/condition-number A-high))


(deftest t82_l392 (is ((fn [v] (> v 1000000.0)) v81_l390)))
