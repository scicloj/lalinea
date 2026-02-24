(ns
 basis-book.least-squares-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l42 (def x-data (dtype/make-reader :float64 20 (* 0.1 idx))))


(def
 v4_l46
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
 v5_l52
 (def y-linear (dfn/+ (dfn/+ 2.0 (dfn/* 3.0 x-data)) noise-linear)))


(def
 v7_l57
 (def
  A-linear
  (let
   [m (count x-data)]
   (la/matrix (mapv (fn [i] [1.0 (x-data i)]) (range m))))))


(def v9_l65 (def y-col (la/column (vec y-linear))))


(def
 v11_l75
 (def
  c-linear
  (la/solve
   (la/mmul (la/transpose A-linear) A-linear)
   (la/mmul (la/transpose A-linear) y-col))))


(def v12_l79 c-linear)


(deftest
 t13_l81
 (is
  ((fn
    [c]
    (and
     (< (Math/abs (- (tensor/mget c 0 0) 2.0)) 0.5)
     (< (Math/abs (- (tensor/mget c 1 0) 3.0)) 0.5)))
   v12_l79)))


(def
 v15_l91
 (def residual-linear (la/sub (la/mmul A-linear c-linear) y-col)))


(def
 v16_l94
 (def
  rms-linear
  (Math/sqrt
   (/
    (dfn/sum (dfn/* residual-linear residual-linear))
    (count x-data)))))


(def v17_l98 rms-linear)


(deftest t18_l100 (is ((fn [v] (< v 0.5)) v17_l98)))


(def
 v20_l105
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
    {:x (vec x-data),
     :y (vec y-linear),
     :type (repeat (count x-data) "data")})
   (tc/concat
    (tc/dataset
     {:x (vec x-fit), :y (vec y-fit), :type (repeat 100 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 8, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def
 v22_l130
 (def x-poly (dtype/make-reader :float64 30 (- (* 0.2 idx) 3.0))))


(def
 v23_l134
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
 v24_l141
 (def
  y-poly
  (dfn/+
   (dfn/+ 1.0 (dfn/* -2.0 x-poly))
   (dfn/+ (dfn/* x-poly x-poly) noise-poly))))


(def
 v25_l145
 (def
  vandermonde
  (fn
   [xs degree]
   (let
    [m (count xs)]
    (la/matrix
     (mapv
      (fn [i] (mapv (fn [j] (Math/pow (xs i) j)) (range (inc degree))))
      (range m)))))))


(def v26_l154 (def A-poly (vandermonde x-poly 2)))


(def
 v27_l156
 (def
  c-poly
  (la/solve
   (la/mmul (la/transpose A-poly) A-poly)
   (la/mmul (la/transpose A-poly) (la/column (vec y-poly))))))


(def v28_l160 c-poly)


(deftest
 t29_l162
 (is
  ((fn
    [c]
    (and
     (< (Math/abs (- (tensor/mget c 0 0) 1.0)) 1.0)
     (< (Math/abs (- (tensor/mget c 1 0) -2.0)) 1.0)
     (< (Math/abs (- (tensor/mget c 2 0) 1.0)) 1.0)))
   v28_l160)))


(def
 v31_l172
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
   (tc/dataset
    {:x (vec x-poly), :y (vec y-poly), :type (repeat 30 "data")})
   (tc/concat
    (tc/dataset
     {:x (vec x-fit), :y (vec y-fit), :type (repeat 100 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 8, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v33_l199 (def qr-result (la/qr A-poly)))


(def v35_l204 (def ncols (second (dtype/shape A-poly))))


(def v36_l206 (def Q1 (la/submatrix (:Q qr-result) :all (range ncols))))


(def v37_l207 (def R1 (la/submatrix (:R qr-result) (range ncols) :all)))


(def v39_l211 (la/norm (la/sub (la/mmul Q1 R1) A-poly)))


(deftest t40_l213 (is ((fn [v] (< v 1.0E-10)) v39_l211)))


(def
 v42_l218
 (def
  c-qr
  (la/solve R1 (la/mmul (la/transpose Q1) (la/column (vec y-poly))))))


(def v44_l223 (la/norm (la/sub c-qr c-poly)))


(deftest t45_l225 (is ((fn [v] (< v 1.0E-10)) v44_l223)))


(def v47_l239 (def svd-result (la/svd A-poly)))


(def v49_l244 (def S-svd (:S svd-result)))


(def
 v50_l245
 (def U-thin (la/submatrix (:U svd-result) :all (range (count S-svd)))))


(def v51_l246 (def Vt-svd (:Vt svd-result)))


(def v53_l250 (vec S-svd))


(deftest t54_l252 (is ((fn [v] (every? pos? v)) v53_l250)))


(def
 v56_l258
 (def
  c-svd
  (let
   [k
    (count S-svd)
    S-inv
    (la/diag (dtype/make-reader :float64 k (/ 1.0 (S-svd idx))))
    Ut-y
    (la/mmul (la/transpose U-thin) (la/column (vec y-poly)))]
   (la/mmul (la/transpose Vt-svd) (la/mmul S-inv Ut-y)))))


(def v58_l267 (la/norm (la/sub c-svd c-poly)))


(deftest t59_l269 (is ((fn [v] (< v 1.0E-8)) v58_l267)))


(def
 v61_l282
 (def
  x-trig
  (dtype/make-reader :float64 40 (* (/ (* 2.0 Math/PI) 40.0) idx))))


(def
 v62_l286
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
 v63_l294
 (def
  y-trig
  (dfn/+
   (dfn/+ 3.0 (dfn/* 2.0 (dfn/cos x-trig)))
   (dfn/+
    (dfn/* -1.5 (dfn/sin x-trig))
    (dfn/+ (dfn/* 0.5 (dfn/cos (dfn/* 2.0 x-trig))) noise-trig)))))


(def
 v64_l300
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
        (Math/cos xi)
        (Math/sin xi)
        (Math/cos (* 2.0 xi))
        (Math/sin (* 2.0 xi))]))
     (range m))))))


(def
 v65_l312
 (def
  c-trig
  (la/solve
   (la/mmul (la/transpose A-trig) A-trig)
   (la/mmul (la/transpose A-trig) (la/column (vec y-trig))))))


(def v66_l316 c-trig)


(deftest
 t67_l318
 (is
  ((fn
    [c]
    (and
     (< (Math/abs (- (tensor/mget c 0 0) 3.0)) 0.3)
     (< (Math/abs (- (tensor/mget c 1 0) 2.0)) 0.3)
     (< (Math/abs (- (tensor/mget c 2 0) -1.5)) 0.3)
     (< (Math/abs (- (tensor/mget c 3 0) 0.5)) 0.3)))
   v66_l316)))


(def
 v69_l330
 (let
  [x-fit
   (dtype/make-reader :float64 200 (* (/ (* 2.0 Math/PI) 200.0) idx))
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
   (tc/dataset
    {:x (vec x-trig), :y (vec y-trig), :type (repeat 40 "data")})
   (tc/concat
    (tc/dataset
     {:x (vec x-fit), :y (vec y-fit), :type (repeat 200 "fit")}))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 6, :=mark-opacity 0.6})
   (plotly/layer-line)
   plotly/plot)))


(def v71_l357 (def poly-svd (la/svd A-poly)))


(def
 v72_l359
 (def
  condition-number
  (let [sv (vec (:S poly-svd))] (/ (apply max sv) (apply min sv)))))


(def v73_l363 condition-number)


(deftest t74_l365 (is ((fn [v] (> v 1.0)) v73_l363)))


(def
 v76_l371
 (def
  A-high
  (vandermonde (dtype/make-reader :float64 30 (* 1.0 idx)) 8)))


(def v77_l374 (def high-sv (vec (:S (la/svd A-high)))))


(def v78_l376 (/ (apply max high-sv) (apply min high-sv)))


(deftest t79_l378 (is ((fn [v] (> v 1000000.0)) v78_l376)))
