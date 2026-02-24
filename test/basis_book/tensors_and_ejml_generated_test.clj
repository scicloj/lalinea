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
 v3_l30
 (let
  [t (la/matrix [[1.0 2.0] [3.0 4.0]]) dm (bt/tensor->dmat t)]
  {:identical?
   (identical? (dtype/->double-array (.buffer t)) (.data dm)),
   :rows (.numRows dm),
   :cols (.numCols dm)}))


(deftest
 t4_l37
 (is
  ((fn [v] (and (:identical? v) (= (:rows v) 2) (= (:cols v) 2)))
   v3_l30)))


(def
 v6_l43
 (let
  [t (la/matrix [[1.0 0.0] [0.0 1.0]]) dm (bt/tensor->dmat t)]
  (.set dm 0 1 99.0)
  (tensor/mget t 0 1)))


(deftest t7_l48 (is (= v6_l43 99.0)))


(def
 v9_l52
 (let
  [t
   (la/matrix [[1.0 0.0] [0.0 1.0]])
   dm
   (bt/tensor->dmat t)
   arr
   (dtype/->double-array (.buffer t))]
  (aset arr 1 42.0)
  (.get dm 0 1)))


(deftest t10_l58 (is (= v9_l52 42.0)))


(def v12_l65 (la/matrix [[1 2 3] [4 5 6] [7 8 9]]))


(deftest
 t13_l69
 (is ((fn [m] (= [3 3] (vec (dtype/shape m)))) v12_l65)))


(def
 v15_l81
 (let [A (la/matrix [[1 2] [3 4]]) I (la/eye 2)] (la/mmul A I)))


(deftest t16_l85 (is ((fn [m] (= 1.0 (tensor/mget m 0 0))) v15_l81)))


(def
 v18_l91
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
 t19_l99
 (is
  ((fn
    [v]
    (and
     (every?
      (fn* [p1__46884#] (< (Math/abs (- p1__46884# 1.0)) 1.0E-10))
      (:diag v))
     (every?
      (fn* [p1__46885#] (< (Math/abs p1__46885#) 1.0E-10))
      (:off v))))
   v18_l91)))


(def
 v21_l106
 (let
  [A
   (la/matrix [[1 2 3] [4 5 6]])
   AtA
   (la/mmul (la/transpose A) A)
   nf
   (la/norm A)]
  (< (Math/abs (- (la/trace AtA) (* nf nf))) 1.0E-10)))


(deftest t22_l111 (is (true? v21_l106)))


(def
 v24_l118
 (let [A (la/matrix [[1 4] [9 16]])] (tensor/mget (dfn/sqrt A) 1 0)))


(deftest t26_l123 (is (= v24_l118 3.0)))


(def
 v28_l127
 (let
  [A (la/matrix [[1 2] [3 4]]) B (la/matrix [[5 6] [7 8]])]
  (tensor/mget (tensor/ensure-tensor (dfn/* A B)) 0 0)))


(deftest t29_l131 (is (= v28_l127 5.0)))


(def
 v31_l139
 (let
  [A (la/matrix [[4 1] [1 3]]) {:keys [eigenvalues]} (la/eigen A)]
  (sort (map first eigenvalues))))


(deftest
 t32_l143
 (is
  ((fn
    [evs]
    (let
     [expected [2.381966011250105 4.618033988749895]]
     (every?
      identity
      (map (fn [a b] (< (Math/abs (- a b)) 1.0E-10)) evs expected))))
   v31_l139)))


(def
 v34_l152
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   {:keys [U S Vt]}
   (la/svd A)
   reconstructed
   (la/mmul (la/mmul U (la/diag S)) Vt)]
  (< (la/norm (la/sub A reconstructed)) 1.0E-10)))


(deftest t35_l157 (is (true? v34_l152)))


(def
 v37_l163
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   {:keys [Q R]}
   (la/qr A)
   QtQ
   (la/mmul (la/transpose Q) Q)]
  (< (la/norm (la/sub QtQ (la/eye 2))) 1.0E-10)))


(deftest t38_l168 (is (true? v37_l163)))


(def
 v40_l172
 (let
  [A (la/matrix [[1 2] [3 4]]) {:keys [Q R]} (la/qr A)]
  (< (la/norm (la/sub A (la/mmul Q R))) 1.0E-10)))


(deftest t41_l176 (is (true? v40_l172)))


(def
 v43_l182
 (let
  [A
   (la/matrix [[4 2] [2 3]])
   L
   (la/cholesky A)
   reconstructed
   (la/mmul L (la/transpose L))]
  (< (la/norm (la/sub A reconstructed)) 1.0E-10)))


(deftest t44_l187 (is (true? v43_l182)))
