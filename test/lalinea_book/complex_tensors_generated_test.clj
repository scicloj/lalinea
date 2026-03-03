(ns
 lalinea-book.complex-tensors-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.complex :as cx]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l36 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t4_l38 (is ((fn [v] (= [3] (cx/complex-shape v))) v3_l36)))


(def
 v5_l40
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (cx/re ct), :im (cx/im ct)}))


(deftest
 t6_l44
 (is
  ((fn [v] (and (= (:re v) [1.0 2.0 3.0]) (= (:im v) [4.0 5.0 6.0])))
   v5_l40)))


(def v8_l49 (cx/complex-tensor (t/matrix [[1.0 2.0] [3.0 4.0]])))


(deftest
 t9_l51
 (is
  ((fn [v] (and (= [2] (cx/complex-shape v)) (= [1.0 3.0] (cx/re v))))
   v8_l49)))


(def v11_l56 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t12_l58 (is ((fn [v] (= [0.0 0.0 0.0] (cx/im v))) v11_l56)))


(def v14_l62 (cx/complex 3.0 4.0))


(deftest t15_l64 (is ((fn [v] (cx/scalar? v)) v14_l62)))


(def
 v16_l66
 [(cx/re (cx/complex 3.0 4.0)) (cx/im (cx/complex 3.0 4.0))])


(deftest t17_l68 (is (= v16_l66 [3.0 4.0])))


(def
 v19_l72
 (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]]))


(deftest t20_l75 (is ((fn [v] (= [2 2] (cx/complex-shape v))) v19_l72)))


(def
 v22_l83
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(cx/re (ct 0)) (cx/im (ct 0))]))


(deftest t23_l86 (is (= v22_l83 [1.0 4.0])))


(def
 v25_l90
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (cx/re (ct 0))))


(deftest t26_l94 (is (= v25_l90 [1.0 2.0])))


(def
 v28_l98
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  [(cx/re ((ct 1) 1)) (cx/im ((ct 1) 1))]))


(deftest t29_l102 (is (= v28_l98 [4.0 8.0])))


(def
 v31_l110
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (cx/re (la/mul a b)), :im (cx/im (la/mul a b))}))


(deftest
 t33_l117
 (is
  ((fn [v] (and (= (:re v) [-16.0 -20.0]) (= (:im v) [22.0 40.0])))
   v31_l110)))


(def
 v35_l123
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
 v37_l135
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (cx/re ct), :im (cx/im ct)}))


(deftest t38_l139 (is ((fn [v] (= (:im v) [-3.0 4.0])) v37_l135)))


(def
 v40_l143
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
 v42_l152
 (let
  [m (la/abs (cx/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))]))


(deftest
 t44_l157
 (is
  ((fn
    [v]
    (and
     (< (abs (- (first v) 5.0)) 1.0E-10)
     (< (abs (- (second v) 1.0)) 1.0E-10)))
   v42_l152)))


(def
 v46_l164
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) d (la/dot a a)]
  {:norm-sq (double (cx/re d)), :im-part (double (cx/im d))}))


(deftest
 t48_l170
 (is
  ((fn
    [v]
    (and
     (< (abs (- (:norm-sq v) 30.0)) 1.0E-10)
     (< (abs (:im-part v)) 1.0E-10)))
   v46_l164)))


(def
 v50_l180
 (la/mmul
  (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])
  (cx/complex-tensor [[0.0 1.0] [1.0 0.0]] [[0.0 0.0] [0.0 0.0]])))


(deftest
 t51_l185
 (is
  ((fn
    [ct]
    (and
     (= [2 2] (cx/complex-shape ct))
     (= (cx/re ct) [[0.0 1.0] [1.0 0.0]])
     (= (cx/im ct) [[0.0 0.0] [0.0 0.0]])))
   v50_l180)))


(def
 v53_l191
 (la/transpose
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest
 t55_l196
 (is
  ((fn
    [ct]
    (and
     (= (cx/re ct) [[1.0 3.0] [2.0 4.0]])
     (= (cx/im ct) [[-5.0 -7.0] [-6.0 -8.0]])))
   v53_l191)))


(def
 v57_l201
 (la/det
  (cx/complex-tensor [[1.0 3.0] [5.0 7.0]] [[2.0 4.0] [6.0 8.0]])))


(deftest
 t59_l206
 (is
  ((fn
    [d]
    (and
     (< (abs (cx/re d)) 1.0E-10)
     (< (abs (- (cx/im d) -16.0)) 1.0E-10)))
   v57_l201)))


(def
 v61_l211
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
   (< (elem/reduce-max (elem/abs (la/sub re-part (t/eye 2)))) 1.0E-10)
   (< (elem/reduce-max (elem/abs im-part)) 1.0E-10))))


(deftest t62_l220 (is (true? v61_l211)))
