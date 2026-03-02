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
