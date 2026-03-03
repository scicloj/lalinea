(ns
 lalinea-book.vectors-and-spaces-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l35 (def u (t/column [3 1])))


(def v4_l36 (def v (t/column [1 2])))


(def
 v6_l41
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v8_l51 (la/add u v))


(deftest
 t9_l53
 (is ((fn [r] (and (= 4.0 (r 0 0)) (= 3.0 (r 1 0)))) v8_l51)))


(def
 v10_l57
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v12_l67 (la/scale u 2.0))


(deftest
 t13_l69
 (is ((fn [r] (and (= 6.0 (r 0 0)) (= 2.0 (r 1 0)))) v12_l67)))


(def
 v14_l73
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v16_l79 (la/scale u -1.0))


(deftest
 t17_l81
 (is ((fn [r] (and (= -3.0 (r 0 0)) (= -1.0 (r 1 0)))) v16_l79)))


(def
 v18_l85
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v20_l119 (def w-ax (t/column [-1 4])))


(def v21_l120 (def zero2 (t/column [0 0])))


(def v23_l124 (la/close? (la/add u v) (la/add v u)))


(deftest t24_l126 (is (true? v23_l124)))


(def
 v26_l130
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t27_l133 (is (true? v26_l130)))


(def v29_l137 (la/close? (la/add u zero2) u))


(deftest t30_l139 (is (true? v29_l137)))


(def v32_l143 (la/close? (la/add u (la/scale u -1.0)) zero2))


(deftest t33_l145 (is (true? v32_l143)))


(def
 v35_l149
 (la/close? (la/scale (la/scale u 3.0) 2.0) (la/scale u 6.0)))


(deftest t36_l152 (is (true? v35_l149)))


(def v38_l156 (la/close? (la/scale u 1.0) u))


(deftest t39_l158 (is (true? v38_l156)))


(def
 v41_l162
 (la/close?
  (la/scale (la/add u v) 5.0)
  (la/add (la/scale u 5.0) (la/scale v 5.0))))


(deftest t42_l165 (is (true? v41_l162)))


(def
 v44_l169
 (la/close?
  (la/scale u (+ 2.0 3.0))
  (la/add (la/scale u 2.0) (la/scale u 3.0))))


(deftest t45_l172 (is (true? v44_l169)))


(def v47_l203 (la/add (la/scale u 2.0) (la/scale v -1.0)))


(deftest
 t48_l205
 (is ((fn [r] (and (= 5.0 (r 0 0)) (= 0.0 (r 1 0)))) v47_l203)))


(def
 v50_l214
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
 v52_l230
 (let
  [coeffs
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] [a b])
   n
   (count coeffs)
   points
   (t/clone
    (t/compute-tensor
     [n 2]
     (fn
      [i j]
      (let [[a b] (nth coeffs i)] (+ (* a (u j 0)) (* b (v j 0)))))
     :float64))
   xs
   (t/select points :all 0)
   ys
   (t/select points :all 1)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v54_l255
 (let
  [s1
   (t/column [1 2])
   s2
   (t/column [2 4])
   coeffs
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] [a b])
   n
   (count coeffs)
   points
   (t/clone
    (t/compute-tensor
     [n 2]
     (fn
      [i j]
      (let [[a b] (nth coeffs i)] (+ (* a (s1 j 0)) (* b (s2 j 0)))))
     :float64))
   xs
   (t/select points :all 0)
   ys
   (t/select points :all 1)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v56_l304
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v58_l313 (la/det (t/matrix [[3 1] [1 2]])))


(deftest t59_l316 (is ((fn [d] (> (abs d) 1.0E-10)) v58_l313)))


(def
 v61_l321
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v63_l329 (la/det (t/matrix [[3 6] [1 2]])))


(deftest t64_l332 (is ((fn [d] (< (abs d) 1.0E-10)) v63_l329)))


(def v66_l338 (la/det (t/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest t67_l342 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v66_l338)))


(def v69_l348 (la/det (t/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t70_l352 (is ((fn [d] (< (abs d) 1.0E-10)) v69_l348)))


(def v72_l372 (def e1 (t/column [1 0 0])))


(def v73_l373 (def e2 (t/column [0 1 0])))


(def v74_l374 (def e3 (t/column [0 0 1])))


(def v76_l384 (def w (t/column [5 -3 7])))


(def
 v78_l388
 (la/close?
  w
  (la/add
   (la/scale e1 5.0)
   (la/add (la/scale e2 -3.0) (la/scale e3 7.0)))))


(deftest t79_l393 (is (true? v78_l388)))


(def v81_l415 (def v1 (t/column [1 0 0])))


(def v82_l416 (def v2 (t/column [0 1 0])))


(def v83_l417 (def v3 (t/column [0 0 1])))


(def v84_l419 (la/det (t/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest t85_l423 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v84_l419)))


(def v87_l430 (def v4 (t/column [2 3 1])))


(def
 v88_l432
 (la/close?
  v4
  (la/add
   (la/scale v1 2.0)
   (la/add (la/scale v2 3.0) (la/scale v3 1.0)))))


(deftest t89_l437 (is (true? v88_l432)))
