(ns
 la-linea-book.computation-tape-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as tensor]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l29 (def A (la/matrix [[1 2] [3 4]])))


(def v4_l31 (tape/memory-status A))


(deftest t5_l33 (is ((fn [s] (= :contiguous s)) v4_l31)))


(def v7_l39 (tape/memory-status (la/transpose A)))


(deftest t8_l41 (is ((fn [s] (= :strided s)) v7_l39)))


(def v10_l47 (def B (la/matrix [[5 6] [7 8]])))


(def v11_l49 (tape/memory-status (la/add A B)))


(deftest t12_l51 (is ((fn [s] (= :lazy s)) v11_l49)))


(def v14_l56 (tape/memory-status (la/mmul A B)))


(deftest t15_l58 (is ((fn [s] (= :contiguous s)) v14_l56)))


(def v17_l72 (tape/memory-relation A (la/transpose A)))


(deftest t18_l74 (is ((fn [r] (= :shared r)) v17_l72)))


(def v20_l79 (tape/memory-relation A B))


(deftest t21_l81 (is ((fn [r] (= :independent r)) v20_l79)))


(def v23_l86 (def arr (double-array [10 20 30])))


(def
 v24_l88
 (tape/memory-relation (la/column arr) (tensor/ensure-tensor arr)))


(deftest t25_l90 (is ((fn [r] (= :shared r)) v24_l88)))


(def v27_l97 (tape/memory-relation A (la/add A B)))


(deftest t28_l99 (is ((fn [r] (= :unknown-lazy r)) v27_l97)))


(def
 v30_l106
 (let
  [tr
   (tape/with-tape
    (let [M (la/matrix [[1 2] [3 4]]) S (la/add M M)] S))]
  (tape/detect-memory-status (last (:entries tr)))))


(deftest t31_l112 (is ((fn [s] (= :reads-through s)) v30_l106)))


(def
 v33_l120
 (def
  tape-result
  (tape/with-tape
   (let
    [M
     (la/matrix [[1 2] [3 4]])
     S
     (la/scale M 2.0)
     I
     (la/eye 2)
     C
     (la/add S I)
     D
     (la/mmul C (la/transpose M))]
    D))))


(def v34_l129 (dissoc tape-result :registry))


(deftest
 t35_l131
 (is
  ((fn
    [tr]
    (and (tensor/tensor? (:result tr)) (= 6 (count (:entries tr)))))
   v34_l129)))


(def
 v37_l151
 (def
  array-tape
  (tape/with-tape (let [v (la/column [1 2 3]) w (la/scale v 5.0)] w))))


(def
 v38_l157
 (mapv
  (fn [e] (select-keys e [:id :op :inputs]))
  (:entries array-tape)))


(deftest
 t39_l160
 (is
  ((fn
    [entries]
    (and
     (= :la/column (:op (first entries)))
     (= [{:external true}] (:inputs (first entries)))
     (= {:id "t1"} (first (:inputs (second entries))))))
   v38_l157)))


(def
 v41_l171
 (def
  seq-tape
  (tape/with-tape
   (let
    [M
     (la/matrix
      (for [i (range 3)] (for [j (range 3)] (* (inc i) (inc j)))))
     v
     (la/column (repeat 3 1.0))]
    (la/mmul M v)))))


(def v42_l179 (mapv :op (:entries seq-tape)))


(deftest
 t43_l181
 (is ((fn [ops] (= [:la/matrix :la/column :la/mmul] ops)) v42_l179)))


(def
 v45_l190
 (def
  dfn-tape
  (tape/with-tape
   (let
    [A
     (la/matrix [[1 2] [3 4]])
     doubled
     (dfn/* A 2.0)
     result
     (la/add (la/matrix doubled) A)]
    result))))


(def
 v46_l197
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries dfn-tape)))


(deftest
 t48_l204
 (is
  ((fn
    [entries]
    (= [:la/matrix :la/matrix :la/add] (mapv :op entries)))
   v46_l197)))


(def
 v50_l216
 (def
  ejml-tape
  (tape/with-tape
   (let
    [dm
     (doto (DMatrixRMaj. 2 2) (.setData (double-array [1 0 0 1])))
     I
     (la/dmat->tensor dm)
     result
     (la/add (la/matrix [[5 6] [7 8]]) I)]
    result))))


(def
 v51_l224
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries ejml-tape)))


(deftest
 t53_l230
 (is
  ((fn
    [entries]
    (and
     (= [:la/matrix :la/add] (mapv :op entries))
     (:external (second (:inputs (second entries))))))
   v51_l224)))


(def
 v55_l240
 (def
  complex-tape
  (tape/with-tape
   (let
    [z1
     (cx/complex-tensor (la/matrix [[1 0] [0 1]]))
     z2
     (cx/complex-tensor (la/matrix [[0 1] [1 0]]))
     s
     (la/add z1 z2)]
    s))))


(def v56_l247 (mapv :op (:entries complex-tape)))


(deftest
 t57_l249
 (is
  ((fn
    [ops]
    (=
     [:la/matrix
      :cx/complex-tensor
      :la/matrix
      :cx/complex-tensor
      :la/add]
     ops))
   v56_l247)))


(def
 v59_l258
 (mapv
  :op
  (:entries
   (tape/with-tape
    (cx/add (cx/complex-tensor [1 2]) (cx/complex-tensor [3 4]))))))


(deftest
 t60_l262
 (is
  ((fn [ops] (= [:cx/complex-tensor :cx/complex-tensor :cx/add] ops))
   v59_l258)))


(def v62_l270 (tape/summary tape-result))


(deftest t63_l272 (is ((fn [s] (= 6 (:total s))) v62_l270)))


(def v65_l284 (tape/origin tape-result (:result tape-result)))


(def v67_l300 (tape/mermaid tape-result (:result tape-result)))


(def
 v69_l307
 (def
  pipeline-result
  (tape/with-tape
   (let
    [data
     (la/matrix [[1 0 2] [0 3 0] [4 0 5]])
     centered
     (la/sub
      data
      (la/scale
       (la/matrix [[1 1 1] [1 1 1] [1 1 1]])
       (/ (double (la/trace data)) 3.0)))
     {:keys [U S Vt]}
     (la/svd centered)
     projection
     (la/mmul (la/transpose Vt) (la/column [1 0 0]))]
    projection))))


(def v70_l320 (tape/summary pipeline-result))


(deftest t71_l322 (is ((fn [s] (= 9 (:total s))) v70_l320)))


(def v73_l327 (tape/mermaid pipeline-result (:result pipeline-result)))
