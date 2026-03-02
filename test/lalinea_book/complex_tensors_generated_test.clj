(ns
 lalinea-book.complex-tensors-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l38 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t4_l40 (is ((fn [v] (= [3] (cx/complex-shape v))) v3_l38)))


(def
 v5_l42
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (cx/re ct), :im (cx/im ct)}))


(deftest
 t6_l46
 (is
  ((fn [v] (and (= (:re v) [1.0 2.0 3.0]) (= (:im v) [4.0 5.0 6.0])))
   v5_l42)))


(def v8_l51 (cx/complex-tensor (tensor/->tensor [[1.0 2.0] [3.0 4.0]])))


(deftest
 t9_l53
 (is
  ((fn [v] (and (= [2] (cx/complex-shape v)) (= [1.0 3.0] (cx/re v))))
   v8_l51)))


(def v11_l58 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t12_l60 (is ((fn [v] (= [0.0 0.0 0.0] (cx/im v))) v11_l58)))


(def v14_l64 (cx/complex 3.0 4.0))


(deftest t15_l66 (is ((fn [v] (cx/scalar? v)) v14_l64)))


(def
 v16_l68
 [(cx/re (cx/complex 3.0 4.0)) (cx/im (cx/complex 3.0 4.0))])


(deftest t17_l70 (is (= v16_l68 [3.0 4.0])))


(def
 v19_l74
 (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]]))


(deftest t20_l77 (is ((fn [v] (= [2 2] (cx/complex-shape v))) v19_l74)))


(def
 v22_l85
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(cx/re (ct 0)) (cx/im (ct 0))]))


(deftest t23_l88 (is (= v22_l85 [1.0 4.0])))


(def
 v25_l92
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (cx/re (ct 0))))


(deftest t26_l96 (is (= v25_l92 [1.0 2.0])))


(def
 v28_l100
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  [(cx/re ((ct 1) 1)) (cx/im ((ct 1) 1))]))


(deftest t29_l104 (is (= v28_l100 [4.0 8.0])))


(def
 v31_l112
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (cx/re (la/mul a b)), :im (cx/im (la/mul a b))}))


(deftest
 t33_l119
 (is
  ((fn [v] (and (= (:re v) [-16.0 -20.0]) (= (:im v) [22.0 40.0])))
   v31_l112)))


(def
 v35_l125
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
 v37_l137
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (cx/re ct), :im (cx/im ct)}))


(deftest t38_l141 (is ((fn [v] (= (:im v) [-3.0 4.0])) v37_l137)))


(def
 v40_l145
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
 v42_l154
 (let
  [m (la/abs (cx/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))]))


(deftest
 t44_l159
 (is
  ((fn
    [v]
    (and
     (< (abs (- (first v) 5.0)) 1.0E-10)
     (< (abs (- (second v) 1.0)) 1.0E-10)))
   v42_l154)))


(def
 v46_l166
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) d (la/dot a a)]
  {:norm-sq (double (cx/re d)), :im-part (double (cx/im d))}))


(deftest
 t48_l172
 (is
  ((fn
    [v]
    (and
     (< (abs (- (:norm-sq v) 30.0)) 1.0E-10)
     (< (abs (:im-part v)) 1.0E-10)))
   v46_l166)))


(def
 v50_l182
 (la/mmul
  (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])
  (cx/complex-tensor [[0.0 1.0] [1.0 0.0]] [[0.0 0.0] [0.0 0.0]])))


(deftest
 t51_l187
 (is
  ((fn
    [ct]
    (and
     (= [2 2] (cx/complex-shape ct))
     (= (cx/re ct) [[0.0 1.0] [1.0 0.0]])
     (= (cx/im ct) [[0.0 0.0] [0.0 0.0]])))
   v50_l182)))


(def
 v53_l193
 (la/transpose
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest
 t55_l198
 (is
  ((fn
    [ct]
    (and
     (= (cx/re ct) [[1.0 3.0] [2.0 4.0]])
     (= (cx/im ct) [[-5.0 -7.0] [-6.0 -8.0]])))
   v53_l193)))


(def
 v57_l203
 (la/det
  (cx/complex-tensor [[1.0 3.0] [5.0 7.0]] [[2.0 4.0] [6.0 8.0]])))


(deftest
 t59_l208
 (is
  ((fn
    [d]
    (and
     (< (abs (cx/re d)) 1.0E-10)
     (< (abs (- (cx/im d) -16.0)) 1.0E-10)))
   v57_l203)))


(def
 v61_l213
 (let
  [A
   (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[0.5 1.0] [1.5 2.5]])
   Ainv
   (la/invert A)
   product
   (la/mmul A Ainv)
   re-part
   (cx/re product)
   im-part
   (cx/im product)]
  (and
   (< (dfn/reduce-max (dfn/abs (dfn/- re-part (la/eye 2)))) 1.0E-10)
   (< (dfn/reduce-max (dfn/abs im-part)) 1.0E-10))))


(deftest t62_l222 (is (true? v61_l213)))
