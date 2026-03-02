(ns
 la-linea-book.maps-and-structure-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.la-linea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l32 (def u (la/column [3 1])))


(def v4_l33 (def v (la/column [1 2])))


(def v6_l85 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l91 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l93
 (is
  ((fn
    [r]
    (and
     (< (abs (tensor/mget r 0 0)) 1.0E-10)
     (< (abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l91)))


(def v11_l99 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l101
 (is
  ((fn
    [r]
    (and
     (< (abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (abs (tensor/mget r 1 0)) 1.0E-10)))
   v11_l99)))


(def
 v14_l108
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l118
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l121 (is (true? v16_l118)))


(def
 v19_l125
 (la/close?
  (la/mmul R90 (la/scale u 3.0))
  (la/scale (la/mmul R90 u) 3.0)))


(deftest t20_l128 (is (true? v19_l125)))


(def v22_l132 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l139
 (let
  [angles
   (dfn/* (/ (* 2.0 math/PI) 40.0) (dtype/make-reader :float64 41 idx))
   circle-x
   (dfn/cos angles)
   circle-y
   (dfn/sin angles)
   stretched
   (mapv
    (fn
     [cx cy]
     (let
      [out (la/mmul stretch-mat (la/column [cx cy]))]
      [(tensor/mget out 0 0) (tensor/mget out 1 0)]))
    circle-x
    circle-y)]
  (->
   (tc/dataset
    {:x (mapv first stretched),
     :y (mapv second stretched),
     :shape (repeat 41 "stretched")})
   (tc/concat
    (tc/dataset
     {:x circle-x, :y circle-y, :shape (repeat 41 "original")}))
   (plotly/base {:=x :x, :=y :y, :=color :shape})
   (plotly/layer-line)
   plotly/plot)))
