(ns
 basis-book.sharing-and-mutation-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.impl.complex :as cx]
  [scicloj.basis.impl.tensor :as bt]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def
 v2_l32
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
 t3_l40
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v2_l32)))


(def
 v5_l52
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
 t6_l61
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v5_l52)))


(def
 v8_l73
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (bt/tensor->dmat M)]
  (identical? (.ary-data (dtype/as-array-buffer M)) (.data dm))))


(deftest t9_l78 (is (true? v8_l73)))


(def
 v11_l82
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (bt/tensor->dmat M)
   _
   (.set dm 0 0 -1.0)]
  {:M-00 (tensor/mget M 0 0), :dm-00 (.get dm 0 0)}))


(deftest
 t12_l88
 (is
  ((fn [{:keys [M-00 dm-00]}] (and (== -1.0 M-00) (== -1.0 dm-00)))
   v11_l82)))


(def
 v14_l95
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (bt/tensor->dmat M)
   _
   (aset (.data dm) 3 99.0)]
  (tensor/mget M 1 1)))


(deftest t15_l100 (is ((fn [v] (== 99.0 v)) v14_l95)))


(def
 v17_l107
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct))))


(deftest t18_l111 (is (true? v17_l107)))


(def
 v20_l115
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


(deftest t21_l121 (is ((fn [v] (== 99.0 v)) v20_l115)))


(def
 v23_l130
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


(deftest t24_l137 (is ((fn [v] (== -10.0 v)) v23_l130)))


(def
 v26_l148
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (vec lazy-sum)))


(deftest t27_l153 (is ((fn [v] (= [11.0 22.0 33.0] v)) v26_l148)))


(def
 v29_l158
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


(deftest t30_l165 (is ((fn [v] (= [110.0 22.0 33.0] v)) v29_l158)))


(def
 v32_l175
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
 t33_l183
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v32_l175)))


(def
 v35_l190
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


(deftest t36_l199 (is ((fn [v] (= [110.0 22.0] v)) v35_l190)))


(def
 v38_l206
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (identical?
   (.ary-data (dtype/as-array-buffer original))
   (.ary-data (dtype/as-array-buffer cloned)))))


(deftest t39_l211 (is (false? v38_l206)))


(def
 v41_l215
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
 t42_l222
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v41_l215)))


(def
 v44_l232
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
 t45_l240
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v44_l232)))


(def
 v47_l251
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


(deftest t48_l259 (is (true? v47_l251)))


(def
 v50_l263
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
 t51_l274
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v50_l263)))


(def
 v53_l285
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
 t54_l292
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v53_l285)))


(def
 v56_l303
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (identical?
   (.ary-data (dtype/as-array-buffer E))
   (.ary-data (dtype/as-array-buffer Et)))))


(deftest t57_l308 (is (false? v56_l303)))


(def
 v59_l312
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
 t60_l319
 (is
  ((fn [{:keys [E-00 Et-00]}] (and (== -1.0 E-00) (== 1.0 Et-00)))
   v59_l312)))
