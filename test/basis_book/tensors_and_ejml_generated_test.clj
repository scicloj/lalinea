(ns
 basis-book.tensors-and-ejml-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l42 (tensor/->tensor [[1 2 3] [4 5 6]] {:datatype :float64}))


(deftest t4_l45 (is ((fn [t] (= [2 3] (vec (dtype/shape t)))) v3_l42)))


(def
 v6_l50
 (tensor/compute-tensor
  [3 3]
  (fn [i j] (if (== i j) 1.0 0.0))
  :float64))


(deftest t7_l54 (is ((fn [t] (= 1.0 (tensor/mget t 1 1))) v6_l50)))


(def
 v9_l60
 (let
  [t (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  [(t 0 1) (t 1 0)]))


(deftest t10_l63 (is ((fn [v] (= [20.0 30.0] v)) v9_l60)))


(def
 v12_l67
 (let
  [t (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})]
  (tensor/mset! t 0 1 99.0)
  (t 0 1)))


(deftest t13_l71 (is (= v12_l67 99.0)))


(def
 v15_l80
 (let
  [a
   (tensor/->tensor [[1 2] [3 4]] {:datatype :float64})
   b
   (tensor/->tensor [[10 20] [30 40]] {:datatype :float64})]
  (dfn/+ a b)))


(deftest t16_l84 (is ((fn [t] (= 44.0 (tensor/mget t 1 1))) v15_l80)))


(def
 v18_l88
 (let
  [x (tensor/->tensor [[1 4] [9 16]] {:datatype :float64})]
  (tensor/mget (dfn/sqrt x) 1 0)))


(deftest t20_l93 (is (= v18_l88 3.0)))


(def
 v22_l125
 (let
  [t (la/matrix [[1.0 2.0] [3.0 4.0]]) dm (la/tensor->dmat t)]
  {:identical?
   (identical? (.ary-data (dtype/as-array-buffer t)) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t23_l132
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v22_l125)))


(def
 v25_l138
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (tensor/mget t 0 1)))


(deftest t26_l143 (is (= v25_l138 99.0)))


(def
 v28_l147
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (tensor/mset! t 0 1 42.0)
  (.get dm 0 1)))


(deftest t29_l152 (is (= v28_l147 42.0)))


(def v31_l159 (la/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest
 t32_l163
 (is ((fn [m] (= [3 3] (vec (dtype/shape m)))) v31_l159)))


(def v34_l175 (la/mmul (la/matrix [[1 2] [3 4]]) (la/eye 2)))


(deftest t35_l178 (is ((fn [m] (= 1.0 (tensor/mget m 0 0))) v34_l175)))


(def v37_l182 (la/invert (la/matrix [[1 2] [3 4]])))


(deftest
 t38_l184
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v37_l182)))


(def v40_l188 (la/norm (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l190
 (is ((fn [v] (< (Math/abs (- v (Math/sqrt 91.0))) 1.0E-10)) v40_l188)))


(def v43_l198 (la/real-eigenvalues (la/matrix [[4 1] [1 3]])))


(deftest t44_l200 (is ((fn [evs] (= 2 (count evs))) v43_l198)))


(def v46_l204 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest t47_l206 (is ((fn [S] (= 2 (count S))) v46_l204)))


(def v49_l210 (la/qr (la/matrix [[1 2] [3 4]])))


(deftest
 t50_l212
 (is ((fn [{:keys [Q R]}] (and (some? Q) (some? R))) v49_l210)))


(def v52_l216 (la/cholesky (la/matrix [[4 2] [2 3]])))


(deftest
 t53_l218
 (is ((fn [L] (= [2 2] (vec (dtype/shape L)))) v52_l216)))
