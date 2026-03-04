(ns
 lalinea-book.complex-tensors-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l42 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t4_l44 (is ((fn [v] (= [3] (t/complex-shape v))) v3_l42)))


(def
 v5_l46
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (la/re ct), :im (la/im ct)}))


(deftest
 t6_l50
 (is
  ((fn [v] (and (= (:re v) [1.0 2.0 3.0]) (= (:im v) [4.0 5.0 6.0])))
   v5_l46)))


(def v8_l55 (t/complex-tensor (t/matrix [[1.0 2.0] [3.0 4.0]])))


(deftest
 t9_l57
 (is
  ((fn [v] (and (= [2] (t/complex-shape v)) (= [1.0 3.0] (la/re v))))
   v8_l55)))


(def v11_l62 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t12_l64 (is ((fn [v] (= [0.0 0.0 0.0] (la/im v))) v11_l62)))


(def v14_l68 (t/complex 3.0 4.0))


(deftest t15_l70 (is ((fn [v] (t/scalar? v)) v14_l68)))


(def v16_l72 [(la/re (t/complex 3.0 4.0)) (la/im (t/complex 3.0 4.0))])


(deftest t17_l74 (is (= v16_l72 [3.0 4.0])))


(def
 v19_l78
 (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]]))


(deftest t20_l81 (is ((fn [v] (= [2 2] (t/complex-shape v))) v19_l78)))


(def
 v22_l89
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(la/re (ct 0)) (la/im (ct 0))]))


(deftest t23_l92 (is (= v22_l89 [1.0 4.0])))


(def
 v25_l96
 (let
  [ct (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (la/re (ct 0))))


(deftest t26_l100 (is (= v25_l96 [1.0 2.0])))


(def
 v28_l104
 (let
  [ct (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  [(la/re ((ct 1) 1)) (la/im ((ct 1) 1))]))


(deftest t29_l108 (is (= v28_l104 [4.0 8.0])))


(def
 v31_l116
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (la/re (la/mul a b)), :im (la/im (la/mul a b))}))


(deftest
 t33_l123
 (is
  ((fn [v] (and (= (:re v) [-16.0 -20.0]) (= (:im v) [22.0 40.0])))
   v31_l116)))


(def
 v35_l129
 (let
  [z-re 3.0 z-im 1.0 p-re -1.0 p-im 3.0]
  (->
   (tc/dataset
    {:re [z-re 0.0 p-re],
     :im [z-im 1.0 p-im],
     :label ["z = 3+i" "w = i" "z*w = -1+3i"]})
   (plotly/base {:=x :re, :=y :im, :=color :label})
   (plotly/layer-point {:=mark-size 12})
   plotly/plot)))


(def
 v37_l141
 (let
  [ct (la/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (la/re ct), :im (la/im ct)}))


(deftest t38_l145 (is ((fn [v] (= (:im v) [-3.0 4.0])) v37_l141)))


(def
 v40_l149
 (let
  [z-re 2.0 z-im 3.0]
  (->
   (tc/dataset
    {:re [z-re z-re],
     :im [z-im (- z-im)],
     :label ["z = 2+3i" "conj(z) = 2-3i"]})
   (plotly/base {:=x :re, :=y :im, :=color :label})
   (plotly/layer-point {:=mark-size 12})
   plotly/plot)))


(def
 v42_l158
 (let
  [m (la/abs (t/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))]))


(deftest
 t44_l163
 (is
  ((fn
    [v]
    (and
     (< (abs (- (first v) 5.0)) 1.0E-10)
     (< (abs (- (second v) 1.0)) 1.0E-10)))
   v42_l158)))


(def
 v46_l170
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) d (la/dot a a)]
  {:norm-sq (double (la/re d)), :im-part (double (la/im d))}))


(deftest
 t48_l176
 (is
  ((fn
    [v]
    (and
     (< (abs (- (:norm-sq v) 30.0)) 1.0E-10)
     (< (abs (:im-part v)) 1.0E-10)))
   v46_l170)))


(def
 v50_l186
 (la/mmul
  (t/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])
  (t/complex-tensor [[0.0 1.0] [1.0 0.0]] [[0.0 0.0] [0.0 0.0]])))


(deftest
 t51_l191
 (is
  ((fn
    [ct]
    (and
     (= [2 2] (t/complex-shape ct))
     (= (la/re ct) [[0.0 1.0] [1.0 0.0]])
     (= (la/im ct) [[0.0 0.0] [0.0 0.0]])))
   v50_l186)))


(def
 v53_l197
 (la/transpose
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest
 t55_l202
 (is
  ((fn
    [ct]
    (and
     (= (la/re ct) [[1.0 3.0] [2.0 4.0]])
     (= (la/im ct) [[-5.0 -7.0] [-6.0 -8.0]])))
   v53_l197)))


(def
 v57_l207
 (la/det
  (t/complex-tensor [[1.0 3.0] [5.0 7.0]] [[2.0 4.0] [6.0 8.0]])))


(deftest
 t59_l212
 (is
  ((fn
    [d]
    (and
     (< (abs (la/re d)) 1.0E-10)
     (< (abs (- (la/im d) -16.0)) 1.0E-10)))
   v57_l207)))


(def
 v61_l217
 (let
  [A
   (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[0.5 1.0] [1.5 2.5]])
   Ainv
   (la/invert A)
   product
   (la/mmul A Ainv)
   re-part
   (la/re product)
   im-part
   (la/im product)]
  (and
   (< (elem/reduce-max (elem/abs (la/sub re-part (t/eye 2)))) 1.0E-10)
   (< (elem/reduce-max (elem/abs im-part)) 1.0E-10))))


(deftest t62_l226 (is (true? v61_l217)))
