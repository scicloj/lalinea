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
   (dtype/->double-array A)
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
  (identical? (dtype/->double-array M) (.data dm))))


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
 v24_l146
 (let
  [M (la/matrix [[1 2] [3 4]]) arr (dtype/->double-array M)]
  (aset arr 0 99.0)
  (tensor/mget M 0 0)))


(deftest t25_l151 (is ((fn [v] (== 99.0 v)) v24_l146)))


(def
 v27_l157
 (let
  [M
   (la/matrix [[1 2 3] [4 5 6]])
   row0
   (tensor/select M 0 :all)
   arr
   (dtype/->double-array row0)]
  {:length (alength arr),
   :values (vec arr),
   :shares-memory? (identical? arr (dtype/->double-array M))}))


(deftest
 t28_l164
 (is
  ((fn
    [{:keys [length values shares-memory?]}]
    (and (== 3 length) (= [1.0 2.0 3.0] values) (not shares-memory?)))
   v27_l157)))


(def
 v30_l173
 (let
  [a
   (la/matrix [[1 2] [3 4]])
   b
   (la/matrix [[10 20] [30 40]])
   lazy-sum
   (dfn/+ a b)
   arr
   (dtype/->double-array lazy-sum)]
  {:values (vec arr),
   :has-array-buffer? (some? (dtype/as-array-buffer lazy-sum))}))


(deftest
 t31_l180
 (is
  ((fn
    [{:keys [values has-array-buffer?]}]
    (and (= [11.0 22.0 33.0 44.0] values) (not has-array-buffer?)))
   v30_l173)))


(def
 v33_l189
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64}))
   arr
   (cx/->double-array ct)]
  (aset arr 0 99.0)
  (cx/re (ct 0))))


(deftest t34_l195 (is ((fn [v] (== 99.0 v)) v33_l189)))


(def
 v36_l202
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct))))


(deftest t37_l206 (is (true? v36_l202)))


(def
 v39_l210
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)
   arr
   (dtype/->double-array ct-data)
   _
   (aset arr 1 99.0)]
  (cx/im (ct 0))))


(deftest t40_l216 (is ((fn [v] (== 99.0 v)) v39_l210)))


(def
 v42_l221
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (tensor/mset! ct-data 0 1 99.0)
  (cx/im (ct 0))))


(deftest t43_l226 (is ((fn [v] (== 99.0 v)) v42_l221)))


(def
 v45_l233
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
   re-view
   (cx/re ct)
   arr
   (dtype/->double-array (cx/->tensor ct))
   _
   (aset arr 0 -10.0)]
  (double (re-view 0))))


(deftest t46_l240 (is ((fn [v] (== -10.0 v)) v45_l233)))


(def
 v48_l245
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
   re-view
   (cx/re ct)]
  (tensor/mset! (cx/->tensor ct) 0 0 -10.0)
  (double (re-view 0))))


(deftest t49_l251 (is ((fn [v] (== -10.0 v)) v48_l245)))


(def
 v51_l261
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (vec lazy-sum)))


(deftest t52_l266 (is ((fn [v] (= [11.0 22.0 33.0] v)) v51_l261)))


(def
 v54_l271
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)
   arr
   (dtype/->double-array x)
   _
   (aset arr 0 100.0)]
  (vec lazy-sum)))


(deftest t55_l278 (is ((fn [v] (= [110.0 22.0 33.0] v)) v54_l271)))


(def
 v57_l283
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (tensor/mset! x 0 100.0)
  (vec lazy-sum)))


(deftest t58_l289 (is ((fn [v] (= [110.0 22.0 33.0] v)) v57_l283)))


(def
 v60_l296
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
 t61_l304
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v60_l296)))


(def
 v63_l311
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
   (dtype/->double-array (cx/->tensor ca))
   _
   (aset arr 0 100.0)]
  (vec (dtype/->reader (cx/re lazy-sum)))))


(deftest t64_l320 (is ((fn [v] (= [110.0 22.0] v)) v63_l311)))


(def
 v66_l324
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


(deftest t67_l332 (is ((fn [v] (= [110.0 22.0] v)) v66_l324)))


(def
 v69_l339
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (identical?
   (dtype/->double-array original)
   (dtype/->double-array cloned))))


(deftest t70_l344 (is (false? v69_l339)))


(def
 v72_l348
 (let
  [original
   (la/matrix [[1 2] [3 4]])
   cloned
   (dtype/clone original)
   arr
   (dtype/->double-array original)
   _
   (aset arr 0 -999.0)]
  {:original-00 (tensor/mget original 0 0),
   :cloned-00 (tensor/mget cloned 0 0)}))


(deftest
 t73_l355
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v72_l348)))


(def
 v75_l362
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (tensor/mset! original 0 0 -999.0)
  {:original-00 (tensor/mget original 0 0),
   :cloned-00 (tensor/mget cloned 0 0)}))


(deftest
 t76_l368
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v75_l362)))


(def
 v78_l378
 (let
  [ct-orig
   (cx/complex-tensor
    (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
   ct-clone
   (dtype/clone ct-orig)
   orig-arr
   (dtype/->double-array (cx/->tensor ct-orig))
   _
   (aset orig-arr 0 -1.0)]
  {:orig-re (cx/re (ct-orig 0)), :clone-re (cx/re (ct-clone 0))}))


(deftest
 t79_l386
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v78_l378)))


(def
 v81_l393
 (let
  [ct-orig
   (cx/complex-tensor
    (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
   ct-clone
   (dtype/clone ct-orig)]
  (tensor/mset! (cx/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (cx/re (ct-orig 0)), :clone-re (cx/re (ct-clone 0))}))


(deftest
 t82_l400
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v81_l393)))


(def
 v84_l411
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


(deftest t85_l419 (is (true? v84_l411)))


(def
 v87_l423
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
   (dtype/->double-array (cx/->tensor p))
   _
   (aset arr 0 999.0)]
  {:lazy-re (vec (dtype/->reader (cx/re lazy-pq))),
   :materialized-re (vec (dtype/->reader (cx/re materialized-pq)))}))


(deftest
 t88_l434
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v87_l423)))


(def
 v90_l441
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
 t91_l451
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v90_l441)))


(def
 v93_l462
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))
   arr
   (dtype/->double-array big)
   _
   (aset arr 0 -1.0)]
  {:big-00 (tensor/mget big 0 0), :sub-00 (tensor/mget sub 0 0)}))


(deftest
 t94_l469
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v93_l462)))


(def
 v96_l476
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))]
  (tensor/mset! big 0 0 -1.0)
  {:big-00 (tensor/mget big 0 0), :sub-00 (tensor/mget sub 0 0)}))


(deftest
 t97_l482
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v96_l476)))


(def
 v99_l493
 (let
  [arr (double-array [1 2 3]) col (la/column arr)]
  (aset arr 0 99.0)
  (tensor/mget col 0 0)))


(deftest t100_l498 (is ((fn [v] (== 99.0 v)) v99_l493)))


(def
 v102_l503
 (let
  [a
   (tensor/->tensor [1 2 3] {:datatype :float64})
   b
   (tensor/->tensor [10 20 30] {:datatype :float64})
   col
   (la/column (dfn/+ a b))]
  {:shape (vec (dtype/shape col)),
   :contiguous? (some? (dtype/as-array-buffer col)),
   :values (vec (dtype/->reader col))}))


(deftest
 t103_l510
 (is
  ((fn
    [{:keys [shape contiguous? values]}]
    (and
     (= [3 1] shape)
     (not contiguous?)
     (= [11.0 22.0 33.0] values)))
   v102_l503)))


(def
 v105_l519
 (let
  [col
   (la/column
    (dfn/+
     (tensor/->tensor [1 0] {:datatype :float64})
     (tensor/->tensor [0 1] {:datatype :float64})))
   A
   (la/matrix [[2 0] [0 3]])]
  (la/mmul A col)))


(deftest
 t106_l524
 (is
  ((fn
    [r]
    (and (== 2.0 (tensor/mget r 0 0)) (== 3.0 (tensor/mget r 1 0))))
   v105_l519)))


(def
 v108_l532
 (let [A (la/matrix [[1 2] [3 4]]) B (la/matrix A)] (identical? A B)))


(deftest t109_l536 (is (true? v108_l532)))


(def
 v111_l541
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (identical? A (la/matrix [[1 2] [3 4]]))))


(deftest t112_l544 (is (false? v111_l541)))


(def
 v114_l552
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (tensor/mset! E 0 1 99.0)
  (tensor/mget Et 1 0)))


(deftest t115_l557 (is ((fn [v] (== 99.0 v)) v114_l552)))


(def
 v117_l561
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (tensor/mset! Et 0 0 -1.0)
  (tensor/mget E 0 0)))


(deftest t118_l566 (is ((fn [v] (== -1.0 v)) v117_l561)))


(def
 v120_l571
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (dtype/clone (la/transpose E))]
  (tensor/mset! E 0 0 -1.0)
  (tensor/mget Et 0 0)))


(deftest t121_l576 (is ((fn [v] (== 1.0 v)) v120_l571)))


(def
 v123_l584
 (let
  [E (la/matrix [[1 2] [3 4]]) P (la/mmul E E)]
  (tensor/mset! E 0 0 -1.0)
  (tensor/mget P 0 0)))


(deftest t124_l589 (is ((fn [v] (== 7.0 v)) v123_l584)))


(def
 v126_l599
 (let
  [rng
   (java.util.Random. 42)
   t
   (tensor/compute-tensor
    [4 4]
    (fn [_ _] (.nextGaussian rng))
    :float64)]
  (la/close? t t)))


(deftest t127_l603 (is (false? v126_l599)))


(def
 v129_l608
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


(deftest t130_l613 (is (true? v129_l608)))


(def
 v132_l623
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


(deftest t133_l630 (is (false? v132_l623)))


(def
 v135_l639
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


(deftest t136_l647 (is (true? v135_l639)))
