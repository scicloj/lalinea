(ns
 lalinea-book.sharing-and-mutation-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [tech.v3.tensor :as dtt]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def
 v3_l35
 (let
  [flat
   (double-array [1 2 3 4 5 6])
   t1
   (t/reshape (dtt/ensure-tensor flat) [2 3])
   t2
   (t/reshape (dtt/ensure-tensor flat) [3 2])
   _
   (aset flat 0 99.0)
   result
   {:t1-00 (t1 0 0), :t2-00 (t2 0 0)}]
  result))


(deftest
 t4_l43
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v3_l35)))


(def
 v6_l50
 (kind/mermaid
  "graph TD\n  flat[\"flat = double-array\"] --> backing[\"backing array\n1 2 3 4 5 6\"]\n  t1[\"t1 = tensor 2x3\"] --> backing\n  t2[\"t2 = tensor 3x2\"] --> backing\n  style backing fill:#fff3cd,stroke:#856404"))


(def
 v8_l60
 (let
  [flat
   (double-array [1 2 3 4 5 6])
   t1
   (t/reshape (dtt/ensure-tensor flat) [2 3])
   t2
   (t/reshape (dtt/ensure-tensor flat) [3 2])]
  (t/mset! t1 0 0 99.0)
  {:t1-00 (t1 0 0), :t2-00 (t2 0 0)}))


(deftest
 t9_l67
 (is
  ((fn [{:keys [t1-00 t2-00]}] (and (== 99.0 t1-00) (== 99.0 t2-00)))
   v8_l60)))


(def
 v11_l77
 (let
  [A
   (t/matrix [[10 20 30] [40 50 60]])
   row0
   (t/select A 0 :all)
   arr
   (t/->double-array A)
   _
   (aset arr 0 999.0)
   result
   {:A-00 (A 0 0), :row0-0 (double (row0 0))}]
  result))


(deftest
 t12_l86
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v11_l77)))


(def
 v14_l93
 (let
  [A (t/matrix [[10 20 30] [40 50 60]]) row0 (t/select A 0 :all)]
  (t/mset! A 0 0 999.0)
  {:A-00 (A 0 0), :row0-0 (double (row0 0))}))


(deftest
 t15_l100
 (is
  ((fn [{:keys [A-00 row0-0]}] (and (== 999.0 A-00) (== 999.0 row0-0)))
   v14_l93)))


(def
 v17_l110
 (let
  [M (t/matrix [[1 2] [3 4]]) dm (t/tensor->dmat M)]
  (identical? (t/->double-array M) (.data dm))))


(deftest t18_l115 (is (true? v17_l110)))


(def
 v20_l119
 (let
  [M
   (t/matrix [[1 2] [3 4]])
   dm
   (t/tensor->dmat M)
   _
   (.set dm 0 0 -1.0)]
  {:M-00 (M 0 0), :dm-00 (.get dm 0 0)}))


(deftest
 t21_l125
 (is
  ((fn [{:keys [M-00 dm-00]}] (and (== -1.0 M-00) (== -1.0 dm-00)))
   v20_l119)))


(def
 v23_l132
 (let
  [M (t/matrix [[1 2] [3 4]]) dm (t/tensor->dmat M)]
  (t/mset! M 1 1 99.0)
  (.get dm 1 1)))


(deftest t24_l137 (is ((fn [v] (== 99.0 v)) v23_l132)))


(def
 v26_l151
 (let
  [M (t/matrix [[1 2] [3 4]]) arr (t/->double-array M)]
  (aset arr 0 99.0)
  (M 0 0)))


(deftest t27_l156 (is ((fn [v] (== 99.0 v)) v26_l151)))


(def
 v29_l162
 (let
  [M
   (t/matrix [[1 2 3] [4 5 6]])
   row0
   (t/select M 0 :all)
   arr
   (t/->double-array row0)]
  {:length (alength arr),
   :values (seq arr),
   :shares-memory? (identical? arr (t/->double-array M))}))


(deftest
 t30_l169
 (is
  ((fn
    [{:keys [length values shares-memory?]}]
    (and (== 3 length) (= [1.0 2.0 3.0] values) (not shares-memory?)))
   v29_l162)))


(def
 v32_l178
 (let
  [a
   (t/matrix [[1 2] [3 4]])
   b
   (t/matrix [[10 20] [30 40]])
   lazy-sum
   (la/add a b)
   arr
   (t/->double-array lazy-sum)]
  {:values (seq arr),
   :has-array-buffer? (some? (t/array-buffer lazy-sum))}))


(deftest
 t33_l185
 (is
  ((fn
    [{:keys [values has-array-buffer?]}]
    (and (= [11.0 22.0 33.0 44.0] values) (not has-array-buffer?)))
   v32_l178)))


(def
 v35_l194
 (let
  [ct
   (t/complex-tensor (t/matrix [[1 2] [3 4] [5 6]]))
   arr
   (t/->double-array ct)]
  (aset arr 0 99.0)
  (la/re (ct 0))))


(deftest t36_l200 (is ((fn [v] (== 99.0 v)) v35_l194)))


(def
 v38_l207
 (let
  [ct-data
   (t/matrix [[1 2] [3 4] [5 6]])
   ct
   (t/complex-tensor ct-data)]
  (identical? (t/->tensor ct-data) (t/->tensor ct))))


(deftest t39_l211 (is (true? v38_l207)))


(def
 v41_l215
 (let
  [ct-data
   (t/matrix [[1 2] [3 4] [5 6]])
   ct
   (t/complex-tensor ct-data)
   arr
   (t/->double-array ct-data)
   _
   (aset arr 1 99.0)]
  (la/im (ct 0))))


(deftest t42_l221 (is ((fn [v] (== 99.0 v)) v41_l215)))


(def
 v44_l226
 (let
  [ct-data
   (t/matrix [[1 2] [3 4] [5 6]])
   ct
   (t/complex-tensor ct-data)]
  (t/mset! ct-data 0 1 99.0)
  (la/im (ct 0))))


(deftest t45_l231 (is ((fn [v] (== 99.0 v)) v44_l226)))


(def
 v47_l238
 (let
  [ct
   (t/complex-tensor (t/matrix [[10 40] [20 50] [30 60]]))
   re-view
   (la/re ct)
   arr
   (t/->double-array (t/->tensor ct))
   _
   (aset arr 0 -10.0)]
  (double (re-view 0))))


(deftest t48_l245 (is ((fn [v] (== -10.0 v)) v47_l238)))


(def
 v50_l250
 (let
  [ct
   (t/complex-tensor (t/matrix [[10 40] [20 50] [30 60]]))
   re-view
   (la/re ct)]
  (t/mset! (t/->tensor ct) 0 0 -10.0)
  (double (re-view 0))))


(deftest t51_l256 (is ((fn [v] (== -10.0 v)) v50_l250)))


(def
 v53_l266
 (let
  [x (t/matrix [1 2 3]) y (t/matrix [10 20 30]) lazy-sum (la/add x y)]
  (seq lazy-sum)))


(deftest t54_l271 (is ((fn [v] (= [11.0 22.0 33.0] v)) v53_l266)))


(def
 v56_l276
 (let
  [x
   (t/matrix [1 2 3])
   y
   (t/matrix [10 20 30])
   lazy-sum
   (la/add x y)
   arr
   (t/->double-array x)
   _
   (aset arr 0 100.0)]
  (seq lazy-sum)))


(deftest t57_l283 (is ((fn [v] (= [110.0 22.0 33.0] v)) v56_l276)))


(def
 v59_l288
 (let
  [x (t/matrix [1 2 3]) y (t/matrix [10 20 30]) lazy-sum (la/add x y)]
  (t/mset! x 0 100.0)
  (seq lazy-sum)))


(deftest t60_l294 (is ((fn [v] (= [110.0 22.0 33.0] v)) v59_l288)))


(def
 v62_l301
 (let
  [ca
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   cb
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-sum
   (la/add ca cb)]
  {:re (seq (la/re lazy-sum)), :im (seq (la/im lazy-sum))}))


(deftest
 t63_l309
 (is
  ((fn [{:keys [re im]}] (and (= [11.0 22.0] re) (= [33.0 44.0] im)))
   v62_l301)))


(def
 v65_l316
 (let
  [ca
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   cb
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-sum
   (la/add ca cb)
   arr
   (t/->double-array (t/->tensor ca))
   _
   (aset arr 0 100.0)]
  (seq (la/re lazy-sum))))


(deftest t66_l325 (is ((fn [v] (= [110.0 22.0] v)) v65_l316)))


(def
 v68_l329
 (let
  [ca
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   cb
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-sum
   (la/add ca cb)]
  (t/mset! (t/->tensor ca) 0 0 100.0)
  (seq (la/re lazy-sum))))


(deftest t69_l337 (is ((fn [v] (= [110.0 22.0] v)) v68_l329)))


(def
 v71_l344
 (let
  [original (t/matrix [[1 2] [3 4]]) cloned (t/clone original)]
  (identical? (t/->double-array original) (t/->double-array cloned))))


(deftest t72_l349 (is (false? v71_l344)))


(def
 v74_l353
 (let
  [original
   (t/matrix [[1 2] [3 4]])
   cloned
   (t/clone original)
   arr
   (t/->double-array original)
   _
   (aset arr 0 -999.0)]
  {:original-00 (original 0 0), :cloned-00 (cloned 0 0)}))


(deftest
 t75_l360
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v74_l353)))


(def
 v77_l367
 (let
  [original (t/matrix [[1 2] [3 4]]) cloned (t/clone original)]
  (t/mset! original 0 0 -999.0)
  {:original-00 (original 0 0), :cloned-00 (cloned 0 0)}))


(deftest
 t78_l373
 (is
  ((fn
    [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00) (== 1.0 cloned-00)))
   v77_l367)))


(def
 v80_l383
 (let
  [ct-orig
   (t/complex-tensor (t/matrix [[1 4] [2 5] [3 6]]))
   ct-clone
   (t/clone ct-orig)
   orig-arr
   (t/->double-array (t/->tensor ct-orig))
   _
   (aset orig-arr 0 -1.0)]
  {:orig-re (la/re (ct-orig 0)), :clone-re (la/re (ct-clone 0))}))


(deftest
 t81_l391
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v80_l383)))


(def
 v83_l398
 (let
  [ct-orig
   (t/complex-tensor (t/matrix [[1 4] [2 5] [3 6]]))
   ct-clone
   (t/clone ct-orig)]
  (t/mset! (t/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (la/re (ct-orig 0)), :clone-re (la/re (ct-clone 0))}))


(deftest
 t84_l405
 (is
  ((fn
    [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re) (== 1.0 clone-re)))
   v83_l398)))


(def
 v86_l416
 (let
  [p
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   q
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-pq
   (la/add p q)
   materialized-pq
   (t/clone lazy-pq)]
  (some? (t/array-buffer (t/->tensor materialized-pq)))))


(deftest t87_l424 (is (true? v86_l416)))


(def
 v89_l428
 (let
  [p
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   q
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-pq
   (la/add p q)
   materialized-pq
   (t/clone lazy-pq)
   arr
   (t/->double-array (t/->tensor p))
   _
   (aset arr 0 999.0)]
  {:lazy-re (seq (la/re lazy-pq)),
   :materialized-re (seq (la/re materialized-pq))}))


(deftest
 t90_l439
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v89_l428)))


(def
 v92_l446
 (let
  [p
   (t/complex-tensor (t/matrix [[1 3] [2 4]]))
   q
   (t/complex-tensor (t/matrix [[10 30] [20 40]]))
   lazy-pq
   (la/add p q)
   materialized-pq
   (t/clone lazy-pq)]
  (t/mset! (t/->tensor p) 0 0 999.0)
  {:lazy-re (seq (la/re lazy-pq)),
   :materialized-re (seq (la/re materialized-pq))}))


(deftest
 t93_l456
 (is
  ((fn
    [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re) (= [11.0 22.0] materialized-re)))
   v92_l446)))


(def
 v95_l467
 (let
  [big
   (t/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (t/submatrix big (range 2) (range 2))
   arr
   (t/->double-array big)
   _
   (aset arr 0 -1.0)]
  {:big-00 (big 0 0), :sub-00 (sub 0 0)}))


(deftest
 t96_l474
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v95_l467)))


(def
 v98_l481
 (let
  [big
   (t/matrix [[1 2 3] [4 5 6] [7 8 9]])
   sub
   (t/submatrix big (range 2) (range 2))]
  (t/mset! big 0 0 -1.0)
  {:big-00 (big 0 0), :sub-00 (sub 0 0)}))


(deftest
 t99_l487
 (is
  ((fn
    [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00) (== 1.0 sub-00)))
   v98_l481)))


(def
 v101_l498
 (let
  [arr (double-array [1 2 3]) col (t/column arr)]
  (aset arr 0 99.0)
  (col 0 0)))


(deftest t102_l503 (is ((fn [v] (== 99.0 v)) v101_l498)))


(def
 v104_l508
 (let
  [a
   (t/matrix [1 2 3])
   b
   (t/matrix [10 20 30])
   col
   (t/column (la/add a b))]
  {:shape (t/shape col),
   :contiguous? (some? (t/array-buffer col)),
   :values (seq (t/flatten col))}))


(deftest
 t105_l515
 (is
  ((fn
    [{:keys [shape contiguous? values]}]
    (and
     (= [3 1] shape)
     (not contiguous?)
     (= [11.0 22.0 33.0] values)))
   v104_l508)))


(def
 v107_l524
 (let
  [col
   (t/column (la/add (t/matrix [1 0]) (t/matrix [0 1])))
   A
   (t/matrix [[2 0] [0 3]])]
  (la/mmul A col)))


(deftest
 t108_l529
 (is ((fn [r] (and (== 2.0 (r 0 0)) (== 3.0 (r 1 0)))) v107_l524)))


(def
 v110_l537
 (let [A (t/matrix [[1 2] [3 4]]) B (t/matrix A)] (identical? A B)))


(deftest t111_l541 (is (true? v110_l537)))


(def
 v113_l546
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (identical? A (t/matrix [[1 2] [3 4]]))))


(deftest t114_l549 (is (false? v113_l546)))


(def
 v116_l557
 (let
  [E (t/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (t/mset! E 0 1 99.0)
  (Et 1 0)))


(deftest t117_l562 (is ((fn [v] (== 99.0 v)) v116_l557)))


(def
 v119_l566
 (let
  [E (t/matrix [[1 2] [3 4]]) Et (la/transpose E)]
  (t/mset! Et 0 0 -1.0)
  (E 0 0)))


(deftest t120_l571 (is ((fn [v] (== -1.0 v)) v119_l566)))


(def
 v122_l576
 (let
  [E (t/matrix [[1 2] [3 4]]) Et (t/clone (la/transpose E))]
  (t/mset! E 0 0 -1.0)
  (Et 0 0)))


(deftest t123_l581 (is ((fn [v] (== 1.0 v)) v122_l576)))


(def
 v125_l589
 (let
  [E (t/matrix [[1 2] [3 4]]) P (la/mmul E E)]
  (t/mset! E 0 0 -1.0)
  (P 0 0)))


(deftest t126_l594 (is ((fn [v] (== 7.0 v)) v125_l589)))


(def
 v128_l604
 (let
  [rng
   (java.util.Random. 42)
   t
   (t/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64)]
  (la/close? t t)))


(deftest t129_l608 (is (false? v128_l604)))


(def
 v131_l613
 (let
  [rng
   (java.util.Random. 42)
   t
   (t/clone
    (t/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64))]
  (la/close? t t)))


(deftest t132_l618 (is (true? v131_l613)))


(def
 v134_l628
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42)]
     (t/clone
      (t/compute-tensor
       [100 100]
       (fn [_ _] (.nextGaussian rng))
       :float64))))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(deftest t135_l635 (is (false? v134_l628)))


(def
 v137_l644
 (let
  [make-random-tensor
   (fn
    []
    (let
     [rng (java.util.Random. 42)]
     (->>
      (repeatedly (* 4 4) (fn* [] (.nextGaussian rng)))
      (t/make-container :float64)
      (t/reshape [4 4]))))]
  (la/close? (make-random-tensor) (make-random-tensor))))


(deftest t138_l652 (is (true? v137_l644)))
