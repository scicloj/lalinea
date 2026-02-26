(ns
 basis-book.vectors-and-spaces-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l40 (def u (la/column [3 1])))


(def v4_l41 (def v (la/column [1 2])))


(def
 v6_l46
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v8_l56 (la/add u v))


(deftest
 t9_l58
 (is
  ((fn
    [r]
    (and (= 4.0 (tensor/mget r 0 0)) (= 3.0 (tensor/mget r 1 0))))
   v8_l56)))


(def
 v10_l62
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v12_l72 (la/scale u 2.0))


(deftest
 t13_l74
 (is
  ((fn
    [r]
    (and (= 6.0 (tensor/mget r 0 0)) (= 2.0 (tensor/mget r 1 0))))
   v12_l72)))


(def
 v14_l78
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v16_l84 (la/scale u -1.0))


(deftest
 t17_l86
 (is
  ((fn
    [r]
    (and (= -3.0 (tensor/mget r 0 0)) (= -1.0 (tensor/mget r 1 0))))
   v16_l84)))


(def
 v18_l90
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v20_l124 (def w-ax (la/column [-1 4])))


(def v21_l125 (def zero2 (la/column [0 0])))


(def v23_l129 (la/close? (la/add u v) (la/add v u)))


(deftest t24_l131 (is (true? v23_l129)))


(def
 v26_l135
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t27_l138 (is (true? v26_l135)))


(def v29_l142 (la/close? (la/add u zero2) u))


(deftest t30_l144 (is (true? v29_l142)))


(def v32_l148 (la/close? (la/add u (la/scale u -1.0)) zero2))


(deftest t33_l150 (is (true? v32_l148)))


(def
 v35_l154
 (la/close? (la/scale (la/scale u 3.0) 2.0) (la/scale u 6.0)))


(deftest t36_l157 (is (true? v35_l154)))


(def v38_l161 (la/close? (la/scale u 1.0) u))


(deftest t39_l163 (is (true? v38_l161)))


(def
 v41_l167
 (la/close?
  (la/scale (la/add u v) 5.0)
  (la/add (la/scale u 5.0) (la/scale v 5.0))))


(deftest t42_l170 (is (true? v41_l167)))


(def
 v44_l174
 (la/close?
  (la/scale u (+ 2.0 3.0))
  (la/add (la/scale u 2.0) (la/scale u 3.0))))


(deftest t45_l177 (is (true? v44_l174)))


(def v47_l208 (la/add (la/scale u 2.0) (la/scale v -1.0)))


(deftest
 t48_l210
 (is
  ((fn
    [r]
    (and (= 5.0 (tensor/mget r 0 0)) (= 0.0 (tensor/mget r 1 0))))
   v47_l208)))


(def
 v50_l219
 (vis/arrow-plot
  [{:label "2u", :xy [6 2], :color "#2266cc"}
   {:label "-v",
    :xy [-1 -2],
    :color "#cc4422",
    :from [6 2],
    :dashed? true}
   {:label "2u-v", :xy [5 0], :color "#228833"}]
  {}))


(def
 v52_l235
 (let
  [coeffs
   (vec (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] [a b]))
   n
   (count coeffs)
   points
   (dtype/clone
    (tensor/compute-tensor
     [n 2]
     (fn
      [i j]
      (let
       [[a b] (nth coeffs i)]
       (+ (* a (tensor/mget u j 0)) (* b (tensor/mget v j 0)))))
     :float64))
   xs
   (tensor/select points :all 0)
   ys
   (tensor/select points :all 1)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v54_l260
 (let
  [s1
   (la/column [1 2])
   s2
   (la/column [2 4])
   coeffs
   (vec (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] [a b]))
   n
   (count coeffs)
   points
   (dtype/clone
    (tensor/compute-tensor
     [n 2]
     (fn
      [i j]
      (let
       [[a b] (nth coeffs i)]
       (+ (* a (tensor/mget s1 j 0)) (* b (tensor/mget s2 j 0)))))
     :float64))
   xs
   (tensor/select points :all 0)
   ys
   (tensor/select points :all 1)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v56_l309
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v58_l318 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t59_l321 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v58_l318)))


(def
 v61_l326
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v63_l334 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t64_l337 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v63_l334)))


(def v66_l343 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t67_l347
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v66_l343)))


(def v69_l353 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t70_l357 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v69_l353)))


(def v72_l377 (def e1 (la/column [1 0 0])))


(def v73_l378 (def e2 (la/column [0 1 0])))


(def v74_l379 (def e3 (la/column [0 0 1])))


(def v76_l389 (def w (la/column [5 -3 7])))


(def
 v78_l393
 (la/close?
  w
  (la/add
   (la/scale e1 5.0)
   (la/add (la/scale e2 -3.0) (la/scale e3 7.0)))))


(deftest t79_l398 (is (true? v78_l393)))


(def v81_l420 (def v1 (la/column [1 0 0])))


(def v82_l421 (def v2 (la/column [0 1 0])))


(def v83_l422 (def v3 (la/column [0 0 1])))


(def v84_l424 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t85_l428
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v84_l424)))


(def v87_l435 (def v4 (la/column [2 3 1])))


(def
 v88_l437
 (la/close?
  v4
  (la/add
   (la/scale v1 2.0)
   (la/add (la/scale v2 3.0) (la/scale v3 1.0)))))


(deftest t89_l442 (is (true? v88_l437)))
