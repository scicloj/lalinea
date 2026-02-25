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


(def v3_l33 (def u (la/column [3 1])))


(def v4_l34 (def v (la/column [1 2])))


(def
 v6_l39
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v8_l49 (la/add u v))


(deftest
 t9_l51
 (is
  ((fn
    [r]
    (and (= 4.0 (tensor/mget r 0 0)) (= 3.0 (tensor/mget r 1 0))))
   v8_l49)))


(def
 v10_l55
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v12_l65 (la/scale 2.0 u))


(deftest
 t13_l67
 (is
  ((fn
    [r]
    (and (= 6.0 (tensor/mget r 0 0)) (= 2.0 (tensor/mget r 1 0))))
   v12_l65)))


(def
 v14_l71
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v16_l77 (la/scale -1.0 u))


(deftest
 t17_l79
 (is
  ((fn
    [r]
    (and (= -3.0 (tensor/mget r 0 0)) (= -1.0 (tensor/mget r 1 0))))
   v16_l77)))


(def
 v18_l83
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v20_l117 (def w-ax (la/column [-1 4])))


(def v21_l118 (def zero2 (la/column [0 0])))


(def v23_l122 (la/close? (la/add u v) (la/add v u)))


(deftest t24_l124 (is (true? v23_l122)))


(def
 v26_l128
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t27_l131 (is (true? v26_l128)))


(def v29_l135 (la/close? (la/add u zero2) u))


(deftest t30_l137 (is (true? v29_l135)))


(def v32_l141 (la/close? (la/add u (la/scale -1.0 u)) zero2))


(deftest t33_l143 (is (true? v32_l141)))


(def
 v35_l147
 (la/close? (la/scale 2.0 (la/scale 3.0 u)) (la/scale 6.0 u)))


(deftest t36_l150 (is (true? v35_l147)))


(def v38_l154 (la/close? (la/scale 1.0 u) u))


(deftest t39_l156 (is (true? v38_l154)))


(def
 v41_l160
 (la/close?
  (la/scale 5.0 (la/add u v))
  (la/add (la/scale 5.0 u) (la/scale 5.0 v))))


(deftest t42_l163 (is (true? v41_l160)))


(def
 v44_l167
 (la/close?
  (la/scale (+ 2.0 3.0) u)
  (la/add (la/scale 2.0 u) (la/scale 3.0 u))))


(deftest t45_l170 (is (true? v44_l167)))


(def v47_l195 (la/add (la/scale 2.0 u) (la/scale -1.0 v)))


(deftest
 t48_l197
 (is
  ((fn
    [r]
    (and (= 5.0 (tensor/mget r 0 0)) (= 0.0 (tensor/mget r 1 0))))
   v47_l195)))


(def
 v50_l206
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
 v52_l222
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
 v54_l247
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
 v56_l296
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v58_l305 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t59_l308 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v58_l305)))


(def
 v61_l313
 (vis/arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v63_l321 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t64_l324 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v63_l321)))


(def v66_l330 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t67_l334
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v66_l330)))


(def v69_l340 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t70_l344 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v69_l340)))


(def v72_l364 (def e1 (la/column [1 0 0])))


(def v73_l365 (def e2 (la/column [0 1 0])))


(def v74_l366 (def e3 (la/column [0 0 1])))


(def v76_l376 (def w (la/column [5 -3 7])))


(def
 v78_l380
 (la/close?
  w
  (la/add
   (la/scale 5.0 e1)
   (la/add (la/scale -3.0 e2) (la/scale 7.0 e3)))))


(deftest t79_l385 (is (true? v78_l380)))


(def v81_l407 (def v1 (la/column [1 0 0])))


(def v82_l408 (def v2 (la/column [0 1 0])))


(def v83_l409 (def v3 (la/column [0 0 1])))


(def v84_l411 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t85_l415
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v84_l411)))


(def v87_l422 (def v4 (la/column [2 3 1])))


(def
 v88_l424
 (la/close?
  v4
  (la/add
   (la/scale 2.0 v1)
   (la/add (la/scale 3.0 v2) (la/scale 1.0 v3)))))


(deftest t89_l429 (is (true? v88_l424)))
