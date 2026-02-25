(ns
 basis-book.tensors-and-ejml-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.impl.tensor :as bt]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def
 v3_l36
 (let
  [t (la/matrix [[1.0 2.0] [3.0 4.0]]) dm (bt/tensor->dmat t)]
  {:identical?
   (identical? (dtype/->double-array (.buffer t)) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t4_l43
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v3_l36)))


(def
 v6_l49
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (bt/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (tensor/mget t 0 1)))


(deftest t7_l54 (is (= v6_l49 99.0)))


(def
 v9_l58
 (let
  [t
   (la/matrix [[1.0 0.0] [0.0 1.0]])
   dm
   (bt/tensor->dmat t)
   arr
   (dtype/->double-array (.buffer t))]
  (aset arr 1 42.0)
  (.get dm 0 1)))


(deftest t10_l64 (is (= v9_l58 42.0)))


(def v12_l71 (la/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest
 t13_l75
 (is ((fn [m] (= [3 3] (vec (dtype/shape m)))) v12_l71)))


(def v15_l87 (la/mmul (la/matrix [[1 2] [3 4]]) (la/eye 2)))


(deftest t16_l90 (is ((fn [m] (= 1.0 (tensor/mget m 0 0))) v15_l87)))


(def v18_l94 (la/invert (la/matrix [[1 2] [3 4]])))


(deftest
 t19_l96
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v18_l94)))


(def v21_l100 (la/norm (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t22_l102
 (is ((fn [v] (< (Math/abs (- v (Math/sqrt 91.0))) 1.0E-10)) v21_l100)))


(def v24_l109 (tensor/mget (dfn/sqrt (la/matrix [[1 4] [9 16]])) 1 0))


(deftest t26_l113 (is (= v24_l109 3.0)))


(def
 v28_l117
 (tensor/mget
  (tensor/ensure-tensor
   (dfn/* (la/matrix [[1 2] [3 4]]) (la/matrix [[5 6] [7 8]])))
  0
  0))


(deftest t29_l122 (is (= v28_l117 5.0)))


(def v31_l130 (la/real-eigenvalues (la/matrix [[4 1] [1 3]])))


(deftest t32_l132 (is ((fn [evs] (= 2 (count evs))) v31_l130)))


(def v34_l136 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest t35_l138 (is ((fn [S] (= 2 (count S))) v34_l136)))


(def v37_l142 (la/qr (la/matrix [[1 2] [3 4]])))


(deftest
 t38_l144
 (is ((fn [{:keys [Q R]}] (and (some? Q) (some? R))) v37_l142)))


(def v40_l148 (la/cholesky (la/matrix [[4 2] [2 3]])))


(deftest
 t41_l150
 (is ((fn [L] (= [2 2] (vec (dtype/shape L)))) v40_l148)))
