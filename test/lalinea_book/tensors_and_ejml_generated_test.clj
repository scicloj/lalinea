(ns
 lalinea-book.tensors-and-ejml-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l43 (tensor/->tensor [[1 2 3] [4 5 6]] {:datatype :float64}))


(deftest t4_l46 (is ((fn [t] (= [2 3] (dtype/shape t))) v3_l43)))


(def
 v6_l53
 (tensor/compute-tensor
  [3 3]
  (fn [i j] (if (== i j) 1.0 0.0))
  :float64))


(deftest t7_l57 (is ((fn [t] (= 1.0 (tensor/mget t 1 1))) v6_l53)))


(def
 v9_l63
 (let
  [t (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  [(t 0 1) (t 1 0)]))


(deftest t10_l66 (is ((fn [v] (= [20.0 30.0] v)) v9_l63)))


(def
 v12_l70
 (let
  [t (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})]
  (tensor/mset! t 0 1 99.0)
  (t 0 1)))


(deftest t13_l74 (is (= v12_l70 99.0)))


(def
 v15_l83
 (let
  [a
   (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})
   b
   (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  (tensor/reshape (dfn/+ a b) (dtype/shape a))))


(deftest t16_l87 (is ((fn [t] (= 44.0 (tensor/mget t 1 1))) v15_l83)))


(def
 v18_l91
 (let
  [x (tensor/->tensor [[1 4] [9 16]] {:datatype :float64})]
  (tensor/mget (dfn/sqrt x) 1 0)))


(deftest t20_l96 (is (= v18_l91 3.0)))


(def
 v22_l150
 (let
  [t (la/matrix [[1.0 2.0] [3.0 4.0]]) dm (la/tensor->dmat t)]
  {:identical? (identical? (dtype/->double-array t) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t23_l157
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v22_l150)))


(def
 v25_l163
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (tensor/mget t 0 1)))


(deftest t26_l168 (is (= v25_l163 99.0)))


(def
 v28_l172
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (tensor/mset! t 0 1 42.0)
  (.get dm 0 1)))


(deftest t29_l177 (is (= v28_l172 42.0)))


(def v31_l184 (la/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest t32_l188 (is ((fn [m] (= [3 3] (dtype/shape m))) v31_l184)))


(def v34_l200 (la/mmul (la/matrix [[1 2] [3 4]]) (la/eye 2)))


(deftest t35_l203 (is ((fn [m] (= 1.0 (tensor/mget m 0 0))) v34_l200)))


(def v37_l207 (la/invert (la/matrix [[1 2] [3 4]])))


(deftest t38_l209 (is ((fn [m] (= [2 2] (dtype/shape m))) v37_l207)))


(def v40_l213 (la/norm (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l215
 (is ((fn [v] (< (abs (- v (math/sqrt 91.0))) 1.0E-10)) v40_l213)))


(def v43_l223 (la/real-eigenvalues (la/matrix [[4 1] [1 3]])))


(deftest t44_l225 (is ((fn [evs] (= 2 (count evs))) v43_l223)))


(def v46_l229 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest t47_l231 (is ((fn [S] (= 2 (count S))) v46_l229)))


(def v49_l235 (la/qr (la/matrix [[1 2] [3 4]])))


(deftest
 t50_l237
 (is ((fn [{:keys [Q R]}] (and (some? Q) (some? R))) v49_l235)))


(def v52_l241 (la/cholesky (la/matrix [[4 2] [2 3]])))


(deftest t53_l243 (is ((fn [L] (= [2 2] (dtype/shape L))) v52_l241)))
