(ns
 la-linea-book.sharing-and-mutation-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
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
 v6_l54
 (let
  [flat
   (double-array [1 2 3 4 5 6])
   t1
   (tensor/reshape (tensor/ensure-tensor flat) [2 3])
   t2
   (tensor/reshape (tensor/ensure-tensor flat) [3 2])]
  (tensor/mset! t1 0 0 99.0)
  {:t1-00 (tensor/mget t1 0 0), :t2-00 (tensor/mget t2 0 0)}))


(deftest
 t7_l61
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v6_l54)))


(def
 v9_l71
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
 t10_l80
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v9_l71)))


(def
 v12_l87
 (let
  [A
   (tensor/->tensor [[10 20 30] [40 50 60]] {:datatype :float64})
   row0
   (tensor/select A 0 :all)]
  (tensor/mset! A 0 0 999.0)
  {:A-00 (tensor/mget A 0 0), :row0-0 (double (row0 0))}))


(deftest
 t13_l94
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v12_l87)))


(def
 v15_l104
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat M)]
  (identical? (.ary-data (dtype/as-array-buffer M)) (.data dm))))


(deftest t16_l109 (is (true? v15_l104)))


(def
 v18_l113
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (la/tensor->dmat M)
   _
   (.set dm 0 0 -1.0)]
  {:M-00 (tensor/mget M 0 0), :dm-00 (.get dm 0 0)}))


(deftest
 t19_l119
 (is
  ((fn [{:keys [M-00 dm-00]}] (and (== -1.0 M-00) (== -1.0 dm-00)))
   v18_l113)))


(def
 v21_l126
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat M)]
  (tensor/mset! M 1 1 99.0)
  (.get dm 1 1)))


(deftest t22_l131 (is ((fn [v] (== 99.0 v)) v21_l126)))


(def
 v24_l138
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct))))


(deftest t25_l142 (is (true? v24_l138)))


(def
 v27_l146
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


(deftest t28_l152 (is ((fn [v] (== 99.0 v)) v27_l146)))


(def
 v30_l157
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (tensor/mset! ct-data 0 1 99.0)
  (cx/im (ct 0))))


(deftest t31_l162 (is ((fn [v] (== 99.0 v)) v30_l157)))


(def
 v33_l169
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


(deftest t34_l176 (is ((fn [v] (== -10.0 v)) v33_l169)))


(def
 v36_l181
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
   re-view
   (cx/re ct)]
  (tensor/mset! (cx/->tensor ct) 0 0 -10.0)
  (double (re-view 0))))


(deftest t37_l187 (is ((fn [v] (== -10.0 v)) v36_l181)))


(def
 v39_l197
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (vec lazy-sum)))


(deftest t40_l202 (is ((fn [v] (= [11.0 22.0 33.0] v)) v39_l197)))


(def
 v42_l207
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


(deftest t43_l214 (is ((fn [v] (= [110.0 22.0 33.0] v)) v42_l207)))


(def
 v45_l219
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (tensor/mset! x 0 100.0)
  (vec lazy-sum)))


(deftest t46_l225 (is ((fn [v] (= [110.0 22.0 33.0] v)) v45_l219)))


(def
 v48_l232
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
 t49_l240
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v48_l232)))


(def
 v51_l247
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


(deftest t52_l256 (is ((fn [v] (= [110.0 22.0] v)) v51_l247)))


(def
 v54_l260
 (let
  [ca
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   cb
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-sum
   (cx/add ca cb)]
  (tensor/mset! (cx/->tensor ca) 0 0 100.0)
  (vec (dtype/->reader (cx/re lazy-sum)))))


(deftest t55_l268 (is ((fn [v] (= [110.0 22.0] v)) v54_l260)))


(def
 v57_l275
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (identical?
   (.ary-data (dtype/as-array-buffer original))
   (.ary-data (dtype/as-array-buffer cloned)))))


(deftest t58_l280 (is (false? v57_l275)))


(def
 v60_l284
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
 t61_l291
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v60_l284)))


(def
 v63_l298
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (tensor/mset! original 0 0 -999.0)
  {:original-00 (tensor/mget original 0 0),
   :cloned-00 (tensor/mget cloned 0 0)}))


(deftest
 t64_l304
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v63_l298)))


(def
 v66_l314
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
 t67_l322
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v66_l314)))


(def
 v69_l329
 (let
  [ct-orig
   (cx/complex-tensor
    (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
   ct-clone
   (dtype/clone ct-orig)]
  (tensor/mset! (cx/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (cx/re (ct-orig 0)), :clone-re (cx/re (ct-clone 0))}))


(deftest
 t70_l336
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v69_l329)))


(def
 v72_l347
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


(deftest t73_l355 (is (true? v72_l347)))


(def
 v75_l359
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
 t76_l370
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v75_l359)))


(def
 v78_l377
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
  (tensor/mset! (cx/->tensor p) 0 0 999.0)
  {:lazy-re (vec (dtype/->reader (cx/re lazy-pq))),
   :materialized-re (vec (dtype/->reader (cx/re materialized-pq)))}))


(deftest
 t79_l387
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v78_l377)))


(def
 v81_l398
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
 t82_l405
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v81_l398)))


(def
 v84_l412
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))]
  (tensor/mset! big 0 0 -1.0)
  {:big-00 (tensor/mget big 0 0), :sub-00 (tensor/mget sub 0 0)}))


(deftest
 t85_l418
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v84_l412)))


(def
 v87_l429
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (identical?
   (.ary-data (dtype/as-array-buffer E))
   (.ary-data (dtype/as-array-buffer Et)))))


(deftest t88_l434 (is (false? v87_l429)))


(def
 v90_l438
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
 t91_l445
 (is
  ((fn [{:keys [E-00 Et-00]}] (and (== -1.0 E-00) (== 1.0 Et-00)))
   v90_l438)))


(def
 v93_l452
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (tensor/mset! E 0 0 -1.0)
  {:E-00 (tensor/mget E 0 0), :Et-00 (tensor/mget Et 0 0)}))


(deftest
 t94_l458
 (is
  ((fn [{:keys [E-00 Et-00]}] (and (== -1.0 E-00) (== 1.0 Et-00)))
   v93_l452)))


(def
 v96_l471
 (let
  [rng
   (java.util.Random. 42)
   t
   (tensor/compute-tensor
    [4 4]
    (fn [_ _] (.nextGaussian rng))
    :float64)]
  (la/close? t t)))


(deftest t97_l475 (is (false? v96_l471)))


(def
 v99_l480
 (let
  [rng
   (java.util.Random. 42)
   t
   (dtype/clone
    (tensor/compute-tensor
     [4 4]
     (fn [_ _] (.nextGaussian rng))
     :float64))]
  (la/close? t t)))


(deftest t100_l485 (is (true? v99_l480)))


(def
 v102_l495
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42)]
     (dtype/clone
      (tensor/compute-tensor
       [100 100]
       (fn [_ _] (.nextGaussian rng))
       :float64))))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(deftest t103_l502 (is (false? v102_l495)))


(def
 v105_l511
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42)]
     (->>
      (repeatedly (* 4 4) (fn* [] (.nextGaussian rng)))
      (dtype/make-container :float64)
      (tensor/reshape [4 4]))))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(deftest t106_l519 (is (true? v105_l511)))
