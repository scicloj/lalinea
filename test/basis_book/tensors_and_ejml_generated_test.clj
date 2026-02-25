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


(def
 v3_l34
 (let
  [t (la/matrix [[1.0 2.0] [3.0 4.0]]) dm (la/tensor->dmat t)]
  {:identical?
   (identical? (.ary-data (dtype/as-array-buffer t)) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t4_l41
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v3_l34)))


(def
 v6_l47
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (tensor/mget t 0 1)))


(deftest t7_l52 (is (= v6_l47 99.0)))


(def
 v9_l56
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (la/tensor->dmat t)]
  (tensor/mset! t 0 1 42.0)
  (.get dm 0 1)))


(deftest t10_l61 (is (= v9_l56 42.0)))


(def v12_l68 (la/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest
 t13_l72
 (is ((fn [m] (= [3 3] (vec (dtype/shape m)))) v12_l68)))


(def v15_l84 (la/mmul (la/matrix [[1 2] [3 4]]) (la/eye 2)))


(deftest t16_l87 (is ((fn [m] (= 1.0 (tensor/mget m 0 0))) v15_l84)))


(def v18_l91 (la/invert (la/matrix [[1 2] [3 4]])))


(deftest
 t19_l93
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v18_l91)))


(def v21_l97 (la/norm (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t22_l99
 (is ((fn [v] (< (Math/abs (- v (Math/sqrt 91.0))) 1.0E-10)) v21_l97)))


(def v24_l106 (tensor/mget (dfn/sqrt (la/matrix [[1 4] [9 16]])) 1 0))


(deftest t26_l110 (is (= v24_l106 3.0)))


(def
 v28_l114
 (tensor/mget
  (tensor/ensure-tensor
   (dfn/* (la/matrix [[1 2] [3 4]]) (la/matrix [[5 6] [7 8]])))
  0
  0))


(deftest t29_l119 (is (= v28_l114 5.0)))


(def v31_l127 (la/real-eigenvalues (la/matrix [[4 1] [1 3]])))


(deftest t32_l129 (is ((fn [evs] (= 2 (count evs))) v31_l127)))


(def v34_l133 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest t35_l135 (is ((fn [S] (= 2 (count S))) v34_l133)))


(def v37_l139 (la/qr (la/matrix [[1 2] [3 4]])))


(deftest
 t38_l141
 (is ((fn [{:keys [Q R]}] (and (some? Q) (some? R))) v37_l139)))


(def v40_l145 (la/cholesky (la/matrix [[4 2] [2 3]])))


(deftest
 t41_l147
 (is ((fn [L] (= [2 2] (vec (dtype/shape L)))) v40_l145)))
