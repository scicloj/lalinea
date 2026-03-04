(ns
 lalinea-book.complex-tensors-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l41 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t4_l43 (is ((fn [v] (= [3] (t/complex-shape v))) v3_l41)))


(def
 v5_l45
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (el/re ct), :im (el/im ct)}))


(deftest
 t6_l49
 (is
  ((fn [v] (and (= (:re v) [1.0 2.0 3.0]) (= (:im v) [4.0 5.0 6.0])))
   v5_l45)))


(def v8_l54 (t/complex-tensor (t/matrix [[1.0 2.0] [3.0 4.0]])))


(deftest
 t9_l56
 (is
  ((fn [v] (and (= [2] (t/complex-shape v)) (= [1.0 3.0] (el/re v))))
   v8_l54)))


(def v11_l61 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t12_l63 (is ((fn [v] (= [0.0 0.0 0.0] (el/im v))) v11_l61)))


(def v14_l67 (t/complex 3.0 4.0))


(deftest t15_l69 (is ((fn [v] (t/scalar? v)) v14_l67)))


(def v16_l71 [(el/re (t/complex 3.0 4.0)) (el/im (t/complex 3.0 4.0))])


(deftest t17_l73 (is (= v16_l71 [3.0 4.0])))


(def
 v19_l77
 (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]]))


(deftest t20_l80 (is ((fn [v] (= [2 2] (t/complex-shape v))) v19_l77)))


(def
 v22_l88
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(el/re (ct 0)) (el/im (ct 0))]))


(deftest t23_l91 (is (= v22_l88 [1.0 4.0])))


(def
 v25_l95
 (let
  [ct (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (el/re (ct 0))))


(deftest t26_l99 (is (= v25_l95 [1.0 2.0])))


(def
 v28_l103
 (let
  [ct (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  [(el/re ((ct 1) 1)) (el/im ((ct 1) 1))]))


(deftest t29_l107 (is (= v28_l103 [4.0 8.0])))


(def
 v31_l115
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (el/re (el/mul a b)), :im (el/im (el/mul a b))}))


(deftest
 t33_l122
 (is
  ((fn [v] (and (= (:re v) [-16.0 -20.0]) (= (:im v) [22.0 40.0])))
   v31_l115)))


(def
 v35_l128
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
 v37_l140
 (let
  [ct (el/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (el/re ct), :im (el/im ct)}))


(deftest t38_l144 (is ((fn [v] (= (:im v) [-3.0 4.0])) v37_l140)))


(def
 v40_l148
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
 v42_l157
 (let
  [m (el/abs (t/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))]))


(deftest
 t44_l162
 (is
  ((fn
    [v]
    (and
     (< (abs (- (first v) 5.0)) 1.0E-10)
     (< (abs (- (second v) 1.0)) 1.0E-10)))
   v42_l157)))


(def
 v46_l169
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) d (la/dot a a)]
  {:norm-sq (double (el/re d)), :im-part (double (el/im d))}))


(deftest
 t48_l175
 (is
  ((fn
    [v]
    (and
     (< (abs (- (:norm-sq v) 30.0)) 1.0E-10)
     (< (abs (:im-part v)) 1.0E-10)))
   v46_l169)))


(def
 v50_l185
 (la/mmul
  (t/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])
  (t/complex-tensor [[0.0 1.0] [1.0 0.0]] [[0.0 0.0] [0.0 0.0]])))


(deftest
 t51_l190
 (is
  ((fn
    [ct]
    (and
     (= [2 2] (t/complex-shape ct))
     (= (el/re ct) [[0.0 1.0] [1.0 0.0]])
     (= (el/im ct) [[0.0 0.0] [0.0 0.0]])))
   v50_l185)))


(def
 v53_l196
 (la/transpose
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest
 t55_l201
 (is
  ((fn
    [ct]
    (and
     (= (el/re ct) [[1.0 3.0] [2.0 4.0]])
     (= (el/im ct) [[-5.0 -7.0] [-6.0 -8.0]])))
   v53_l196)))


(def
 v57_l206
 (la/det
  (t/complex-tensor [[1.0 3.0] [5.0 7.0]] [[2.0 4.0] [6.0 8.0]])))


(deftest
 t59_l211
 (is
  ((fn
    [d]
    (and
     (< (abs (el/re d)) 1.0E-10)
     (< (abs (- (el/im d) -16.0)) 1.0E-10)))
   v57_l206)))


(def
 v61_l216
 (let
  [A
   (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[0.5 1.0] [1.5 2.5]])
   Ainv
   (la/invert A)
   product
   (la/mmul A Ainv)
   re-part
   (el/re product)
   im-part
   (el/im product)]
  (and
   (< (el/reduce-max (el/abs (la/sub re-part (t/eye 2)))) 1.0E-10)
   (< (el/reduce-max (el/abs im-part)) 1.0E-10))))


(deftest t62_l225 (is (true? v61_l216)))
