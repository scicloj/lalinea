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


(def
 v18_l96
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   Ainv
   (la/invert A)
   product
   (la/mmul A Ainv)]
  {:diag [(tensor/mget product 0 0) (tensor/mget product 1 1)],
   :off [(tensor/mget product 0 1) (tensor/mget product 1 0)]}))


(deftest
 t19_l104
 (is
  ((fn
    [v]
    (and
     (every?
      (fn* [p1__243850#] (< (Math/abs (- p1__243850# 1.0)) 1.0E-10))
      (:diag v))
     (every?
      (fn* [p1__243851#] (< (Math/abs p1__243851#) 1.0E-10))
      (:off v))))
   v18_l96)))


(def
 v21_l111
 (let
  [A
   (la/matrix [[1 2 3] [4 5 6]])
   AtA
   (la/mmul (la/transpose A) A)
   nf
   (la/norm A)]
  (< (Math/abs (- (la/trace AtA) (* nf nf))) 1.0E-10)))


(deftest t22_l116 (is (true? v21_l111)))


(def v24_l123 (tensor/mget (dfn/sqrt (la/matrix [[1 4] [9 16]])) 1 0))


(deftest t26_l127 (is (= v24_l123 3.0)))


(def
 v28_l131
 (tensor/mget
  (tensor/ensure-tensor
   (dfn/* (la/matrix [[1 2] [3 4]]) (la/matrix [[5 6] [7 8]])))
  0
  0))


(deftest t29_l136 (is (= v28_l131 5.0)))


(def
 v31_l144
 (let
  [{:keys [eigenvalues]} (la/eigen (la/matrix [[4 1] [1 3]]))]
  (sort (map first eigenvalues))))


(deftest
 t32_l147
 (is
  ((fn
    [evs]
    (let
     [expected [2.381966011250105 4.618033988749895]]
     (every?
      identity
      (map (fn [a b] (< (Math/abs (- a b)) 1.0E-10)) evs expected))))
   v31_l144)))


(def
 v34_l156
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   {:keys [U S Vt]}
   (la/svd A)
   reconstructed
   (la/mmul (la/mmul U (la/diag S)) Vt)]
  (< (la/norm (la/sub A reconstructed)) 1.0E-10)))


(deftest t35_l161 (is (true? v34_l156)))


(def
 v37_l167
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   {:keys [Q R]}
   (la/qr A)
   QtQ
   (la/mmul (la/transpose Q) Q)]
  (< (la/norm (la/sub QtQ (la/eye 2))) 1.0E-10)))


(deftest t38_l172 (is (true? v37_l167)))


(def
 v40_l176
 (let
  [A (la/matrix [[1 2] [3 4]]) {:keys [Q R]} (la/qr A)]
  (< (la/norm (la/sub A (la/mmul Q R))) 1.0E-10)))


(deftest t41_l180 (is (true? v40_l176)))


(def
 v43_l186
 (let
  [A
   (la/matrix [[4 2] [2 3]])
   L
   (la/cholesky A)
   reconstructed
   (la/mmul L (la/transpose L))]
  (< (la/norm (la/sub A reconstructed)) 1.0E-10)))


(deftest t44_l191 (is (true? v43_l186)))
