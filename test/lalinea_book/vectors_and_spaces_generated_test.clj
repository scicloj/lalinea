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


(def v3_l36 (def u (t/column [3 1])))


(def v4_l37 (def v (t/column [1 2])))


(def
 v6_l42
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v8_l52 (la/add u v))


(deftest
 t9_l54
 (is ((fn [r] (and (= 4.0 (r 0 0)) (= 3.0 (r 1 0)))) v8_l52)))


(def
 v10_l58
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v12_l68 (la/scale u 2.0))


(deftest
 t13_l70
 (is ((fn [r] (and (= 6.0 (r 0 0)) (= 2.0 (r 1 0)))) v12_l68)))


(def
 v14_l74
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v16_l80 (la/scale u -1.0))


(deftest
 t17_l82
 (is ((fn [r] (and (= -3.0 (r 0 0)) (= -1.0 (r 1 0)))) v16_l80)))


(def
 v18_l86
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v20_l120 (def w-ax (t/column [-1 4])))


(def v21_l121 (def zero2 (t/column [0 0])))


(def v23_l125 (la/close? (la/add u v) (la/add v u)))


(deftest t24_l127 (is (true? v23_l125)))


(def
 v26_l131
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t27_l134 (is (true? v26_l131)))


(def v29_l138 (la/close? (la/add u zero2) u))


(deftest t30_l140 (is (true? v29_l138)))


(def v32_l144 (la/close? (la/add u (la/scale u -1.0)) zero2))


(deftest t33_l146 (is (true? v32_l144)))


(def
 v35_l150
 (la/close? (la/scale (la/scale u 3.0) 2.0) (la/scale u 6.0)))


(deftest t36_l153 (is (true? v35_l150)))


(def v38_l157 (la/close? (la/scale u 1.0) u))


(deftest t39_l159 (is (true? v38_l157)))


(def
 v41_l163
 (la/close?
  (la/scale (la/add u v) 5.0)
  (la/add (la/scale u 5.0) (la/scale v 5.0))))


(deftest t42_l166 (is (true? v41_l163)))


(def
 v44_l170
 (la/close?
  (la/scale u (+ 2.0 3.0))
  (la/add (la/scale u 2.0) (la/scale u 3.0))))


(deftest t45_l173 (is (true? v44_l170)))


(def v47_l204 (la/add (la/scale u 2.0) (la/scale v -1.0)))


(deftest
 t48_l206
 (is ((fn [r] (and (= 5.0 (r 0 0)) (= 0.0 (r 1 0)))) v47_l204)))


(def
 v50_l215
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
 v52_l231
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
 v54_l256
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
 v56_l305
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v58_l314 (la/det (t/matrix [[3 1] [1 2]])))


(deftest t59_l317 (is ((fn [d] (> (abs d) 1.0E-10)) v58_l314)))


(def
 v61_l322
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v63_l330 (la/det (t/matrix [[3 6] [1 2]])))


(deftest t64_l333 (is ((fn [d] (< (abs d) 1.0E-10)) v63_l330)))


(def v66_l339 (la/det (t/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest t67_l343 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v66_l339)))


(def v69_l349 (la/det (t/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t70_l353 (is ((fn [d] (< (abs d) 1.0E-10)) v69_l349)))


(def v72_l373 (def e1 (t/column [1 0 0])))


(def v73_l374 (def e2 (t/column [0 1 0])))


(def v74_l375 (def e3 (t/column [0 0 1])))


(def v76_l385 (def w (t/column [5 -3 7])))


(def
 v78_l389
 (la/close?
  w
  (la/add
   (la/scale e1 5.0)
   (la/add (la/scale e2 -3.0) (la/scale e3 7.0)))))


(deftest t79_l394 (is (true? v78_l389)))


(def v81_l416 (def v1 (t/column [1 0 0])))


(def v82_l417 (def v2 (t/column [0 1 0])))


(def v83_l418 (def v3 (t/column [0 0 1])))


(def v84_l420 (la/det (t/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest t85_l424 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v84_l420)))


(def v87_l431 (def v4 (t/column [2 3 1])))


(def
 v88_l433
 (la/close?
  v4
  (la/add
   (la/scale v1 2.0)
   (la/add (la/scale v2 3.0) (la/scale v3 1.0)))))


(deftest t89_l438 (is (true? v88_l433)))
