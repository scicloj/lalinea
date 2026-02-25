(ns
 basis-book.sharing-and-mutation-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def
 v3_l37
 (let
  [flat
   (double-array [1 2 3 4 5 6])
   t1
   (tensor/reshape (tensor/ensure-tensor flat) [2 3])
   t2
   (tensor/reshape (tensor/ensure-tensor flat) [3 2])
   _
   (aset flat 0 99.0)
   result
   {:t1-00 (tensor/mget t1 0 0), :t2-00 (tensor/mget t2 0 0)}]
  result))


(deftest
 t4_l45
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v3_l37)))


(def
 v6_l57
 (let
  [A
   (tensor/->tensor [[10 20 30] [40 50 60]] {:datatype :float64})
   row0
   (tensor/select A 0 :all)
   arr
   (.ary-data (dtype/as-array-buffer A))
   _
   (aset arr 0 999.0)
   result
   {:A-00 (tensor/mget A 0 0), :row0-0 (double (row0 0))}]
  result))


(deftest
 t7_l66
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v6_l57)))


(def
 v9_l78
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat M)]
  (identical? (.ary-data (dtype/as-array-buffer M)) (.data dm))))


(deftest t10_l83 (is (true? v9_l78)))


(def
 v12_l87
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (la/tensor->dmat M)
   _
   (.set dm 0 0 -1.0)]
  {:M-00 (tensor/mget M 0 0), :dm-00 (.get dm 0 0)}))


(deftest
 t13_l93
 (is
  ((fn [{:keys [M-00 dm-00]}] (and (== -1.0 M-00) (== -1.0 dm-00)))
   v12_l87)))


(def
 v15_l100
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (la/tensor->dmat M)
   _
   (aset (.data dm) 3 99.0)]
  (tensor/mget M 1 1)))


(deftest t16_l105 (is ((fn [v] (== 99.0 v)) v15_l100)))


(def
 v18_l112
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct))))


(deftest t19_l116 (is (true? v18_l112)))


(def
 v21_l120
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)
   arr
   (.ary-data (dtype/as-array-buffer ct-data))
   _
   (aset arr 1 99.0)]
  (cx/im (ct 0))))


(deftest t22_l126 (is ((fn [v] (== 99.0 v)) v21_l120)))


(def
 v24_l135
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
   re-view
   (cx/re ct)
   arr
   (.ary-data (dtype/as-array-buffer (cx/->tensor ct)))
   _
   (aset arr 0 -10.0)]
  (double (re-view 0))))


(deftest t25_l142 (is ((fn [v] (== -10.0 v)) v24_l135)))


(def
 v27_l153
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (vec lazy-sum)))


(deftest t28_l158 (is ((fn [v] (= [11.0 22.0 33.0] v)) v27_l153)))


(def
 v30_l163
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)
   arr
   (.ary-data (dtype/as-array-buffer x))
   _
   (aset arr 0 100.0)]
  (vec lazy-sum)))


(deftest t31_l170 (is ((fn [v] (= [110.0 22.0 33.0] v)) v30_l163)))


(def
 v33_l180
 (let
  [ca
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   cb
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-sum
   (cx/add ca cb)]
  {:re (vec (dtype/->reader (cx/re lazy-sum))),
   :im (vec (dtype/->reader (cx/im lazy-sum)))}))


(deftest
 t34_l188
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v33_l180)))


(def
 v36_l195
 (let
  [ca
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   cb
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-sum
   (cx/add ca cb)
   arr
   (.ary-data (dtype/as-array-buffer (cx/->tensor ca)))
   _
   (aset arr 0 100.0)]
  (vec (dtype/->reader (cx/re lazy-sum)))))


(deftest t37_l204 (is ((fn [v] (= [110.0 22.0] v)) v36_l195)))


(def
 v39_l211
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (identical?
   (.ary-data (dtype/as-array-buffer original))
   (.ary-data (dtype/as-array-buffer cloned)))))


(deftest t40_l216 (is (false? v39_l211)))


(def
 v42_l220
 (let
  [original
   (la/matrix [[1 2] [3 4]])
   cloned
   (dtype/clone original)
   arr
   (.ary-data (dtype/as-array-buffer original))
   _
   (aset arr 0 -999.0)]
  {:original-00 (tensor/mget original 0 0),
   :cloned-00 (tensor/mget cloned 0 0)}))


(deftest
 t43_l227
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v42_l220)))


(def
 v45_l237
 (let
  [ct-orig
   (cx/complex-tensor
    (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
   ct-clone
   (dtype/clone ct-orig)
   orig-arr
   (.ary-data (dtype/as-array-buffer (cx/->tensor ct-orig)))
   _
   (aset orig-arr 0 -1.0)]
  {:orig-re (cx/re (ct-orig 0)), :clone-re (cx/re (ct-clone 0))}))


(deftest
 t46_l245
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v45_l237)))


(def
 v48_l256
 (let
  [p
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   q
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-pq
   (cx/add p q)
   materialized-pq
   (dtype/clone lazy-pq)]
  (some? (dtype/as-array-buffer (cx/->tensor materialized-pq)))))


(deftest t49_l264 (is (true? v48_l256)))


(def
 v51_l268
 (let
  [p
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   q
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-pq
   (cx/add p q)
   materialized-pq
   (dtype/clone lazy-pq)
   arr
   (.ary-data (dtype/as-array-buffer (cx/->tensor p)))
   _
   (aset arr 0 999.0)]
  {:lazy-re (vec (dtype/->reader (cx/re lazy-pq))),
   :materialized-re (vec (dtype/->reader (cx/re materialized-pq)))}))


(deftest
 t52_l279
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v51_l268)))


(def
 v54_l290
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))
   arr
   (.ary-data (dtype/as-array-buffer big))
   _
   (aset arr 0 -1.0)]
  {:big-00 (tensor/mget big 0 0), :sub-00 (tensor/mget sub 0 0)}))


(deftest
 t55_l297
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v54_l290)))


(def
 v57_l308
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (identical?
   (.ary-data (dtype/as-array-buffer E))
   (.ary-data (dtype/as-array-buffer Et)))))


(deftest t58_l313 (is (false? v57_l308)))


(def
 v60_l317
 (let
  [E
   (la/matrix [[1 2] [3 4]])
   Et
   (la/transpose E)
   arr
   (.ary-data (dtype/as-array-buffer E))
   _
   (aset arr 0 -1.0)]
  {:E-00 (tensor/mget E 0 0), :Et-00 (tensor/mget Et 0 0)}))


(deftest
 t61_l324
 (is
  ((fn [{:keys [E-00 Et-00]}] (and (== -1.0 E-00) (== 1.0 Et-00)))
   v60_l317)))


(def
 v63_l340
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42)]
     (tensor/compute-tensor
      [4 4]
      (fn [_ _] (.nextGaussian rng))
      :float64)))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(def
 v65_l353
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42) arr (double-array 16)]
     (dotimes [i 16] (aset arr i (.nextGaussian rng)))
     (tensor/reshape (tensor/ensure-tensor arr) [4 4])))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(deftest t66_l362 (is (true? v65_l353)))
