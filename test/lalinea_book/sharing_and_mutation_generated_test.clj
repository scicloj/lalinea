(ns
 lalinea-book.sharing-and-mutation-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
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
   {:t1-00 (t1 0 0), :t2-00 (t2 0 0)}]
  result))


(deftest
 t4_l45
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v3_l37)))


(def
 v6_l52
 (kind/mermaid
  "graph TD\n  flat[\"flat = double-array\"] --> backing[\"backing array\n1 2 3 4 5 6\"]\n  t1[\"t1 = tensor 2x3\"] --> backing\n  t2[\"t2 = tensor 3x2\"] --> backing\n  style backing fill:#fff3cd,stroke:#856404"))


(def
 v8_l62
 (let
  [flat
   (double-array [1 2 3 4 5 6])
   t1
   (tensor/reshape (tensor/ensure-tensor flat) [2 3])
   t2
   (tensor/reshape (tensor/ensure-tensor flat) [3 2])]
  (tensor/mset! t1 0 0 99.0)
  {:t1-00 (t1 0 0), :t2-00 (t2 0 0)}))


(deftest
 t9_l69
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v8_l62)))


(def
 v11_l79
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
   {:A-00 (A 0 0), :row0-0 (double (row0 0))}]
  result))


(deftest
 t12_l88
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v11_l79)))


(def
 v14_l95
 (let
  [A
   (tensor/->tensor [[10 20 30] [40 50 60]] {:datatype :float64})
   row0
   (tensor/select A 0 :all)]
  (tensor/mset! A 0 0 999.0)
  {:A-00 (A 0 0), :row0-0 (double (row0 0))}))


(deftest
 t15_l102
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v14_l95)))


(def
 v17_l112
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat M)]
  (identical? (dtype/->double-array M) (.data dm))))


(deftest t18_l117 (is (true? v17_l112)))


(def
 v20_l121
 (let
  [M
   (la/matrix [[1 2] [3 4]])
   dm
   (la/tensor->dmat M)
   _
   (.set dm 0 0 -1.0)]
  {:M-00 (M 0 0), :dm-00 (.get dm 0 0)}))


(deftest
 t21_l127
 (is
  ((fn [{:keys [M-00 dm-00]}] (and (== -1.0 M-00) (== -1.0 dm-00)))
   v20_l121)))


(def
 v23_l134
 (let
  [M (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat M)]
  (tensor/mset! M 1 1 99.0)
  (.get dm 1 1)))


(deftest t24_l139 (is ((fn [v] (== 99.0 v)) v23_l134)))


(def
 v26_l153
 (let
  [M (la/matrix [[1 2] [3 4]]) arr (dtype/->double-array M)]
  (aset arr 0 99.0)
  (M 0 0)))


(deftest t27_l158 (is ((fn [v] (== 99.0 v)) v26_l153)))


(def
 v29_l164
 (let
  [M
   (la/matrix [[1 2 3] [4 5 6]])
   row0
   (tensor/select M 0 :all)
   arr
   (dtype/->double-array row0)]
  {:length (alength arr),
   :values (seq arr),
   :shares-memory? (identical? arr (dtype/->double-array M))}))


(deftest
 t30_l171
 (is
  ((fn
    [{:keys [length values shares-memory?]}]
    (and (== 3 length) (= [1.0 2.0 3.0] values) (not shares-memory?)))
   v29_l164)))


(def
 v32_l180
 (let
  [a
   (la/matrix [[1 2] [3 4]])
   b
   (la/matrix [[10 20] [30 40]])
   lazy-sum
   (dfn/+ a b)
   arr
   (dtype/->double-array lazy-sum)]
  {:values (seq arr),
   :has-array-buffer? (some? (dtype/as-array-buffer lazy-sum))}))


(deftest
 t33_l187
 (is
  ((fn
    [{:keys [values has-array-buffer?]}]
    (and (= [11.0 22.0 33.0 44.0] values) (not has-array-buffer?)))
   v32_l180)))


(def
 v35_l196
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64}))
   arr
   (cx/->double-array ct)]
  (aset arr 0 99.0)
  (cx/re (ct 0))))


(deftest t36_l202 (is ((fn [v] (== 99.0 v)) v35_l196)))


(def
 v38_l209
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct))))


(deftest t39_l213 (is (true? v38_l209)))


(def
 v41_l217
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


(deftest t42_l223 (is ((fn [v] (== 99.0 v)) v41_l217)))


(def
 v44_l228
 (let
  [ct-data
   (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
   ct
   (cx/complex-tensor ct-data)]
  (tensor/mset! ct-data 0 1 99.0)
  (cx/im (ct 0))))


(deftest t45_l233 (is ((fn [v] (== 99.0 v)) v44_l228)))


(def
 v47_l240
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


(deftest t48_l247 (is ((fn [v] (== -10.0 v)) v47_l240)))


(def
 v50_l252
 (let
  [ct
   (cx/complex-tensor
    (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
   re-view
   (cx/re ct)]
  (tensor/mset! (cx/->tensor ct) 0 0 -10.0)
  (double (re-view 0))))


(deftest t51_l258 (is ((fn [v] (== -10.0 v)) v50_l252)))


(def
 v53_l268
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (seq lazy-sum)))


(deftest t54_l273 (is ((fn [v] (= [11.0 22.0 33.0] v)) v53_l268)))


(def
 v56_l278
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
  (seq lazy-sum)))


(deftest t57_l285 (is ((fn [v] (= [110.0 22.0 33.0] v)) v56_l278)))


(def
 v59_l290
 (let
  [x
   (tensor/->tensor [1 2 3] {:datatype :float64})
   y
   (tensor/->tensor [10 20 30] {:datatype :float64})
   lazy-sum
   (dfn/+ x y)]
  (tensor/mset! x 0 100.0)
  (seq lazy-sum)))


(deftest t60_l296 (is ((fn [v] (= [110.0 22.0 33.0] v)) v59_l290)))


(def
 v62_l303
 (let
  [ca
   (cx/complex-tensor
    (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
   cb
   (cx/complex-tensor
    (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
   lazy-sum
   (cx/add ca cb)]
  {:re (seq (cx/re lazy-sum)), :im (seq (cx/im lazy-sum))}))


(deftest
 t63_l311
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v62_l303)))


(def
 v65_l318
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
  (seq (cx/re lazy-sum))))


(deftest t66_l327 (is ((fn [v] (= [110.0 22.0] v)) v65_l318)))


(def
 v68_l331
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
  (seq (cx/re lazy-sum))))


(deftest t69_l339 (is ((fn [v] (= [110.0 22.0] v)) v68_l331)))


(def
 v71_l346
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (identical?
   (dtype/->double-array original)
   (dtype/->double-array cloned))))


(deftest t72_l351 (is (false? v71_l346)))


(def
 v74_l355
 (let
  [original
   (la/matrix [[1 2] [3 4]])
   cloned
   (dtype/clone original)
   arr
   (dtype/->double-array original)
   _
   (aset arr 0 -999.0)]
  {:original-00 (original 0 0), :cloned-00 (cloned 0 0)}))


(deftest
 t75_l362
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v74_l355)))


(def
 v77_l369
 (let
  [original (la/matrix [[1 2] [3 4]]) cloned (dtype/clone original)]
  (tensor/mset! original 0 0 -999.0)
  {:original-00 (original 0 0), :cloned-00 (cloned 0 0)}))


(deftest
 t78_l375
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v77_l369)))


(def
 v80_l385
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
 t81_l393
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v80_l385)))


(def
 v83_l400
 (let
  [ct-orig
   (cx/complex-tensor
    (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
   ct-clone
   (dtype/clone ct-orig)]
  (tensor/mset! (cx/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (cx/re (ct-orig 0)), :clone-re (cx/re (ct-clone 0))}))


(deftest
 t84_l407
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v83_l400)))


(def
 v86_l418
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


(deftest t87_l426 (is (true? v86_l418)))


(def
 v89_l430
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
  {:lazy-re (seq (cx/re lazy-pq)),
   :materialized-re (seq (cx/re materialized-pq))}))


(deftest
 t90_l441
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v89_l430)))


(def
 v92_l448
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
  {:lazy-re (seq (cx/re lazy-pq)),
   :materialized-re (seq (cx/re materialized-pq))}))


(deftest
 t93_l458
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v92_l448)))


(def
 v95_l469
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))
   arr
   (dtype/->double-array big)
   _
   (aset arr 0 -1.0)]
  {:big-00 (big 0 0), :sub-00 (sub 0 0)}))


(deftest
 t96_l476
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v95_l469)))


(def
 v98_l483
 (let
  [big
   (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (la/submatrix big (range 2) (range 2))]
  (tensor/mset! big 0 0 -1.0)
  {:big-00 (big 0 0), :sub-00 (sub 0 0)}))


(deftest
 t99_l489
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v98_l483)))


(def
 v101_l500
 (let
  [arr (double-array [1 2 3]) col (la/column arr)]
  (aset arr 0 99.0)
  (col 0 0)))


(deftest t102_l505 (is ((fn [v] (== 99.0 v)) v101_l500)))


(def
 v104_l510
 (let
  [a
   (tensor/->tensor [1 2 3] {:datatype :float64})
   b
   (tensor/->tensor [10 20 30] {:datatype :float64})
   col
   (la/column (dfn/+ a b))]
  {:shape (dtype/shape col),
   :contiguous? (some? (dtype/as-array-buffer col)),
   :values (seq (la/flatten col))}))


(deftest
 t105_l517
 (is
  ((fn
    [{:keys [shape contiguous? values]}]
    (and
     (= [3 1] shape)
     (not contiguous?)
     (= [11.0 22.0 33.0] values)))
   v104_l510)))


(def
 v107_l526
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
 t108_l531
 (is ((fn [r] (and (== 2.0 (r 0 0)) (== 3.0 (r 1 0)))) v107_l526)))


(def
 v110_l539
 (let [A (la/matrix [[1 2] [3 4]]) B (la/matrix A)] (identical? A B)))


(deftest t111_l543 (is (true? v110_l539)))


(def
 v113_l548
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (identical? A (la/matrix [[1 2] [3 4]]))))


(deftest t114_l551 (is (false? v113_l548)))


(def
 v116_l559
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (tensor/mset! E 0 1 99.0)
  (Et 1 0)))


(deftest t117_l564 (is ((fn [v] (== 99.0 v)) v116_l559)))


(def
 v119_l568
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (tensor/mset! Et 0 0 -1.0)
  (E 0 0)))


(deftest t120_l573 (is ((fn [v] (== -1.0 v)) v119_l568)))


(def
 v122_l578
 (let
  [E (la/matrix [[1 2] [3 4]]) Et (dtype/clone (la/transpose E))]
  (tensor/mset! E 0 0 -1.0)
  (Et 0 0)))


(deftest t123_l583 (is ((fn [v] (== 1.0 v)) v122_l578)))


(def
 v125_l591
 (let
  [E (la/matrix [[1 2] [3 4]]) P (la/mmul E E)]
  (tensor/mset! E 0 0 -1.0)
  (P 0 0)))


(deftest t126_l596 (is ((fn [v] (== 7.0 v)) v125_l591)))


(def
 v128_l606
 (let
  [rng
   (java.util.Random. 42)
   t
   (tensor/compute-tensor
    [4 4]
    (fn [_ _] (.nextGaussian rng))
    :float64)]
  (la/close? t t)))


(deftest t129_l610 (is (false? v128_l606)))


(def
 v131_l615
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


(deftest t132_l620 (is (true? v131_l615)))


(def
 v134_l630
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


(deftest t135_l637 (is (false? v134_l630)))


(def
 v137_l646
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


(deftest t138_l654 (is (true? v137_l646)))
