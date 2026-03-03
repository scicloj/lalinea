(ns
 lalinea-book.tensors-and-ejml-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l38 (t/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l41 (is ((fn [t] (= [2 3] (t/shape t))) v3_l38)))


(def
 v6_l48
 (t/compute-tensor [3 3] (fn [i j] (if (== i j) 1.0 0.0)) :float64))


(deftest t7_l52 (is ((fn [t] (= 1.0 (t 1 1))) v6_l48)))


(def v9_l58 (let [t (t/matrix [[10 20] [30 40]])] [(t 0 1) (t 1 0)]))


(deftest t10_l61 (is ((fn [v] (= [20.0 30.0] v)) v9_l58)))


(def
 v12_l65
 (let [t (t/matrix [[1 2] [3 4]])] (t/mset! t 0 1 99.0) (t 0 1)))


(deftest t13_l69 (is (= v12_l65 99.0)))


(def
 v15_l78
 (let
  [a (t/matrix [[1 2] [3 4]]) b (t/matrix [[10 20] [30 40]])]
  (la/add a b)))


(deftest t16_l82 (is ((fn [t] (= 44.0 (t 1 1))) v15_l78)))


(def v18_l86 (let [x (t/matrix [[1 4] [9 16]])] ((elem/sqrt x) 1 0)))


(deftest t20_l91 (is (= v18_l86 3.0)))


(def
 v22_l145
 (let
  [t (t/matrix [[1.0 2.0] [3.0 4.0]]) dm (t/tensor->dmat t)]
  {:identical? (identical? (t/->double-array t) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t23_l152
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v22_l145)))


(def
 v25_l158
 (let
  [t (t/matrix [[1.0 0.0] [0.0 1.0]]) dm (t/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (t 0 1)))


(deftest t26_l163 (is (= v25_l158 99.0)))


(def
 v28_l167
 (let
  [t (t/matrix [[1.0 0.0] [0.0 1.0]]) dm (t/tensor->dmat t)]
  (t/mset! t 0 1 42.0)
  (.get dm 0 1)))


(deftest t29_l172 (is (= v28_l167 42.0)))


(def v31_l179 (t/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest t32_l183 (is ((fn [m] (= [3 3] (t/shape m))) v31_l179)))


(def v34_l195 (la/mmul (t/matrix [[1 2] [3 4]]) (t/eye 2)))


(deftest t35_l198 (is ((fn [m] (= 1.0 (m 0 0))) v34_l195)))


(def v37_l202 (la/invert (t/matrix [[1 2] [3 4]])))


(deftest t38_l204 (is ((fn [m] (= [2 2] (t/shape m))) v37_l202)))


(def v40_l208 (la/norm (t/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l210
 (is ((fn [v] (< (abs (- v (math/sqrt 91.0))) 1.0E-10)) v40_l208)))


(def v43_l218 (la/real-eigenvalues (t/matrix [[4 1] [1 3]])))


(deftest t44_l220 (is ((fn [evs] (= 2 (count evs))) v43_l218)))


(def v46_l224 (:S (la/svd (t/matrix [[1 2] [3 4]]))))


(deftest t47_l226 (is ((fn [S] (= 2 (count S))) v46_l224)))


(def v49_l230 (la/qr (t/matrix [[1 2] [3 4]])))


(deftest
 t50_l232
 (is ((fn [{:keys [Q R]}] (and (some? Q) (some? R))) v49_l230)))


(def v52_l236 (la/cholesky (t/matrix [[4 2] [2 3]])))


(deftest t53_l238 (is ((fn [L] (= [2 2] (t/shape L))) v52_l236)))
