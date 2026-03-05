(ns
 lalinea-book.ecosystem-generated-test
 (:require
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as el]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as dtt]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [fastmath.vector :as fv]
  [fastmath.matrix :as fm]
  [fastmath.stats :as stats]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l42 (let [v (t/column [1 2 3 4 5])] (tc/dataset {:x v})))


(deftest t4_l45 (is ((fn [ds] (= 5 (tc/row-count ds))) v3_l42)))


(def
 v6_l49
 (let [v (t/column [1 2 3 4 5])] (tc/dataset {:x v, :y (el/sq v)})))


(deftest
 t7_l52
 (is
  ((fn [ds] (= [5 2] [(tc/row-count ds) (tc/column-count ds)]))
   v6_l49)))


(def
 v9_l56
 (let
  [v (t/column [1 2 3 4 5])]
  (-> (tc/dataset {:x v}) (tc/add-column :y (el/sq v)))))


(deftest t10_l60 (is ((fn [ds] (= 2 (tc/column-count ds))) v9_l56)))


(def
 v12_l69
 (let
  [{:keys [eigenvectors]}
   (la/eigen (t/matrix [[4 1 1] [1 3 0] [1 0 2]]))]
  (->
   (tc/dataset
    {:v1 (t/select eigenvectors :all 0),
     :v2 (t/select eigenvectors :all 1),
     :component ["x" "y" "z"]})
   (plotly/base {:=x :v1, :=y :v2, :=color :component})
   (plotly/layer-point {:=mark-size 12})
   plotly/plot)))


(def v14_l90 (t/column (fv/vec3 1 2 3)))


(deftest t15_l92 (is ((fn [rt] (= [3 1] (t/shape rt))) v14_l90)))


(def
 v17_l96
 (let
  [a (fv/vec3 1 2 3) b (fv/vec3 4 5 6)]
  [(fv/dot a b) (la/dot (t/column a) (t/column b))]))


(deftest
 t18_l101
 (is ((fn [[fm-dot la-dot]] (== fm-dot la-dot 32.0)) v17_l96)))


(def
 v20_l108
 (let [m (fm/mat2x2 1 2 3 4)] (t/matrix (mapv seq (fm/rows m)))))


(deftest t21_l111 (is ((fn [rt] (= [2 2] (t/shape rt))) v20_l108)))


(def
 v23_l115
 (let
  [a
   (fm/mat2x2 1 2 3 4)
   b
   (fm/mat2x2 5 6 7 8)
   ->la
   (fn [m] (t/matrix (mapv seq (fm/rows m))))]
  (= (la/mmul (->la a) (->la b)) (->la (fm/mulm a b)))))


(deftest t24_l121 (is (true? v23_l115)))


(def
 v26_l131
 (let
  [data (t/column [3 1 4 1 5 9 2 6])]
  (stats/mean (t/flatten data))))


(deftest t27_l134 (is ((fn [v] (== 3.875 v)) v26_l131)))


(def v29_l138 (el/mean (t/column [3 1 4 1 5 9 2 6])))


(deftest t30_l140 (is ((fn [v] (== 3.875 v)) v29_l138)))


(def
 v32_l149
 (let
  [m (t/matrix [[1 2] [3 4]])]
  [(dtype/elemwise-datatype m) (dtype/ecount m) (dtype/shape m)]))


(deftest t33_l154 (is ((fn [v] (= [:float64 4 [2 2]] v)) v32_l149)))


(def
 v35_l158
 (let
  [c (dtype/clone (t/matrix [[1 2] [3 4]]))]
  [(t/real-tensor? c) (t/shape c)]))


(deftest t36_l161 (is ((fn [v] (= [true [2 2]] v)) v35_l158)))


(def
 v38_l165
 (let
  [m (t/matrix [[1 2] [3 4]])]
  [(dfn/sum m) (dfn/mean m) (dfn/reduce-max m)]))


(deftest
 t39_l168
 (is
  ((fn [[s mean mx]] (and (== 10.0 s) (== 2.5 mean) (== 4.0 mx)))
   v38_l165)))


(def
 v41_l179
 (let
  [a (t/column [1 2 3]) b (t/column [10 20 30]) c (dfn/+ a b)]
  {:real-tensor? (t/real-tensor? c),
   :input-shape (dtype/shape a),
   :output-shape (dtype/shape c)}))


(deftest
 t42_l186
 (is
  ((fn
    [{:keys [real-tensor? input-shape output-shape]}]
    (and
     (not real-tensor?)
     (= [3 1] input-shape)
     (= [3] output-shape)))
   v41_l179)))


(def
 v44_l193
 (let
  [bare-a
   (dtt/->tensor [[1] [2] [3]] {:datatype :float64})
   bare-b
   (dtt/->tensor [[10] [20] [30]] {:datatype :float64})]
  (dtype/shape (dfn/+ bare-a bare-b))))


(deftest t45_l197 (is ((fn [s] (= [3 1] s)) v44_l193)))


(def
 v47_l202
 (let
  [a (t/column [1 2 3]) b (t/column [10 20 30]) c (el/+ a b)]
  [(t/real-tensor? c) (t/shape c) (vec (t/flatten c))]))


(deftest
 t48_l207
 (is
  ((fn
    [[rt? sh vals]]
    (and rt? (= [3 1] sh) (= [11.0 22.0 33.0] vals)))
   v47_l202)))


(def v49_l210 (t/shape (t/reshape (t/matrix [[1 2] [3 4]]) [1 4])))


(deftest t50_l212 (is ((fn [s] (= [1 4] s)) v49_l210)))
