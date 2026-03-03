(ns
 lalinea-book.tensors-generated-test
 (:require
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l22 (t/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l25 (is ((fn [m] (= [2 3] (t/shape m))) v3_l22)))


(def v6_l30 (t/column [1 2 3]))


(deftest t7_l32 (is ((fn [c] (= [3 1] (t/shape c))) v6_l30)))


(def v8_l34 (t/row [1 2 3]))


(deftest t9_l36 (is ((fn [r] (= [1 3] (t/shape r))) v8_l34)))


(def v11_l40 (t/eye 3))


(deftest t12_l42 (is ((fn [I] (= 1.0 (I 0 0))) v11_l40)))


(def v13_l44 (t/zeros 2 3))


(deftest t14_l46 (is ((fn [Z] (= 0.0 (Z 1 2))) v13_l44)))


(def v16_l50 (t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0))))


(deftest
 t17_l52
 (is ((fn [m] (and (= 1.0 (m 2 2)) (= 0.0 (m 0 1)))) v16_l50)))


(def
 v19_l57
 (t/compute-tensor [3 3] (fn [i j] (if (== i j) 1.0 0.0)) :float64))


(deftest t20_l61 (is ((fn [m] (= 1.0 (m 1 1))) v19_l57)))


(def v22_l68 (t/matrix [[1 2] [3 4]]))


(deftest t23_l70 (is ((fn [m] (= m (read-string (pr-str m)))) v22_l68)))


(def v25_l76 (let [m (t/matrix [[10 20] [30 40]])] [(m 0 1) (m 1 0)]))


(deftest t26_l79 (is ((fn [v] (= [20.0 30.0] v)) v25_l76)))


(def v28_l85 (t/shape (t/matrix [[1 2 3] [4 5 6]])))


(deftest t29_l87 (is ((fn [s] (= [2 3] s)) v28_l85)))


(def v31_l92 (t/reshape (t/matrix [1 2 3 4 5 6]) [2 3]))


(deftest t32_l94 (is ((fn [m] (= [2 3] (t/shape m))) v31_l92)))


(def
 v34_l100
 (let [A (t/matrix [[1 2 3] [4 5 6]])] (t/select A 0 :all)))


(deftest t35_l104 (is ((fn [r] (= [1.0 2.0 3.0] (seq r))) v34_l100)))


(def
 v37_l108
 (let
  [A (t/matrix [[1 2 3] [4 5 6] [7 8 9]])]
  (t/submatrix A (range 2) (range 2))))


(deftest t38_l111 (is ((fn [s] (= [2 2] (t/shape s))) v37_l108)))


(def v40_l117 (t/flatten (t/matrix [[1 2] [3 4]])))


(deftest t41_l119 (is ((fn [v] (= [4] (t/shape v))) v40_l117)))


(def
 v43_l126
 (let
  [a (t/matrix [1 4 9]) s (elem/sqrt a) cloned (t/clone s)]
  {:lazy-array? (some? (t/array-buffer s)),
   :cloned-array? (some? (t/array-buffer cloned))}))


(deftest
 t44_l132
 (is
  ((fn
    [{:keys [lazy-array? cloned-array?]}]
    (and (not lazy-array?) cloned-array?))
   v43_l126)))


(def
 v46_l141
 (let [m (t/matrix [[1 2] [3 4]])] (t/mset! m 0 1 99.0) (m 0 1)))


(deftest t47_l145 (is (= v46_l141 99.0)))


(def v49_l155 (let [x (t/matrix [[1 4] [9 16]])] (elem/sqrt x)))


(deftest t50_l158 (is ((fn [r] (= 2.0 (r 0 1))) v49_l155)))


(def
 v52_l163
 (let
  [a (t/matrix [[1 2] [3 4]]) b (t/matrix [[10 20] [30 40]])]
  (la/add a b)))


(deftest t53_l167 (is ((fn [r] (= 44.0 (r 1 1))) v52_l163)))


(def
 v55_l174
 (let
  [m (t/matrix [[1.0 2.0] [3.0 4.0]]) dm (t/tensor->dmat m)]
  {:identical? (identical? (t/->double-array m) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t56_l181
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v55_l174)))


(def
 v58_l187
 (let
  [m (t/matrix [[1.0 0.0] [0.0 1.0]]) dm (t/tensor->dmat m)]
  (.set dm 0 1 99.0)
  (m 0 1)))


(deftest t59_l192 (is (= v58_l187 99.0)))


(def v61_l201 (la/mmul (t/matrix [[1 2] [3 4]]) (t/eye 2)))


(deftest t62_l204 (is ((fn [m] (= 1.0 (m 0 0))) v61_l201)))


(def v64_l208 (la/invert (t/matrix [[1 2] [3 4]])))


(deftest t65_l210 (is ((fn [m] (= [2 2] (t/shape m))) v64_l208)))


(def v67_l214 (la/norm (t/matrix [[1 2 3] [4 5 6]])))


(deftest
 t68_l216
 (is ((fn [v] (< (abs (- v (math/sqrt 91.0))) 1.0E-10)) v67_l214)))
