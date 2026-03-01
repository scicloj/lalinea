(ns
 la-linea-book.computation-tape-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as tensor]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l30 (def A (la/matrix [[1 2] [3 4]])))


(def v4_l32 (tape/memory-status A))


(deftest t5_l34 (is ((fn [s] (= :contiguous s)) v4_l32)))


(def v7_l40 (tape/memory-status (la/transpose A)))


(deftest t8_l42 (is ((fn [s] (= :strided s)) v7_l40)))


(def v10_l48 (def B (la/matrix [[5 6] [7 8]])))


(def v11_l50 (tape/memory-status (la/add A B)))


(deftest t12_l52 (is ((fn [s] (= :lazy s)) v11_l50)))


(def v14_l57 (tape/memory-status (la/mmul A B)))


(deftest t15_l59 (is ((fn [s] (= :contiguous s)) v14_l57)))


(def v17_l68 (tape/shares-memory? A (la/transpose A)))


(deftest t18_l70 (is ((fn [b] (true? b)) v17_l68)))


(def v20_l75 (tape/shares-memory? A B))


(deftest t21_l77 (is ((fn [b] (false? b)) v20_l75)))


(def v23_l83 (tape/shares-memory? A (la/add A B)))


(deftest t24_l85 (is ((fn [b] (false? b)) v23_l83)))


(def v26_l90 (def arr (double-array [10 20 30])))


(def
 v27_l92
 (tape/shares-memory? (la/column arr) (tensor/ensure-tensor arr)))


(deftest t28_l94 (is ((fn [b] (true? b)) v27_l92)))


(def
 v30_l102
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


(def v31_l111 (:result tape-result))


(def v33_l115 (count (:entries tape-result)))


(deftest t34_l117 (is ((fn [n] (= 6 n)) v33_l115)))


(def
 v36_l123
 (mapv
  (fn [e] (select-keys e [:id :op :inputs :shape]))
  (:entries tape-result)))


(def
 v38_l139
 (def
  array-tape
  (tape/with-tape
   (let [v (la/column (double-array [1 2 3])) w (la/scale v 5.0)] w))))


(def
 v39_l145
 (mapv
  (fn [e] (select-keys e [:id :op :inputs]))
  (:entries array-tape)))


(deftest
 t40_l148
 (is
  ((fn
    [entries]
    (and
     (= :la/column (:op (first entries)))
     (= [{:external true}] (:inputs (first entries)))
     (= {:id "t1"} (first (:inputs (second entries))))))
   v39_l145)))


(def
 v42_l159
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


(def v43_l167 (mapv :op (:entries seq-tape)))


(deftest
 t44_l169
 (is ((fn [ops] (= [:la/matrix :la/column :la/mmul] ops)) v43_l167)))


(def
 v46_l178
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
 v47_l185
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries dfn-tape)))


(deftest
 t49_l192
 (is
  ((fn
    [entries]
    (= [:la/matrix :la/matrix :la/add] (mapv :op entries)))
   v47_l185)))


(def
 v51_l204
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
 v52_l212
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries ejml-tape)))


(deftest
 t54_l218
 (is
  ((fn
    [entries]
    (and
     (= [:la/matrix :la/add] (mapv :op entries))
     (= {:external true} (second (:inputs (second entries))))))
   v52_l212)))


(def
 v56_l228
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


(def v57_l235 (:complex? (last (:entries complex-tape))))


(deftest t58_l237 (is ((fn [c?] (true? c?)) v57_l235)))


(def v60_l245 (mapv :op (:entries complex-tape)))


(deftest
 t61_l247
 (is ((fn [ops] (= [:la/matrix :la/matrix :la/add] ops)) v60_l245)))


(def v63_l255 (tape/summary tape-result))


(def v65_l267 (tape/origin tape-result (:result tape-result)))


(def
 v67_l283
 (kind/mermaid (tape/mermaid tape-result (:result tape-result))))


(def
 v69_l290
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


(def v70_l303 (tape/summary pipeline-result))


(def
 v72_l308
 (kind/mermaid
  (tape/mermaid pipeline-result (:result pipeline-result))))
