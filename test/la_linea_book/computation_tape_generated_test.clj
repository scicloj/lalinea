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


(def v17_l67 (tape/shares-memory? A (la/transpose A)))


(deftest t18_l69 (is ((fn [b] (true? b)) v17_l67)))


(def v20_l74 (tape/shares-memory? A B))


(deftest t21_l76 (is ((fn [b] (false? b)) v20_l74)))


(def v23_l82 (tape/shares-memory? A (la/add A B)))


(deftest t24_l84 (is ((fn [b] (false? b)) v23_l82)))


(def v26_l89 (def arr (double-array [10 20 30])))


(def
 v27_l91
 (tape/shares-memory? (la/column arr) (tensor/ensure-tensor arr)))


(deftest t28_l93 (is ((fn [b] (true? b)) v27_l91)))


(def
 v30_l101
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


(def v31_l110 (:result tape-result))


(deftest t32_l112 (is ((fn [r] (tensor/tensor? r)) v31_l110)))


(def v34_l117 (count (:entries tape-result)))


(deftest t35_l119 (is ((fn [n] (= 6 n)) v34_l117)))


(def
 v37_l125
 (mapv
  (fn [e] (select-keys e [:id :op :inputs :shape]))
  (:entries tape-result)))


(def
 v39_l141
 (def
  array-tape
  (tape/with-tape (let [v (la/column [1 2 3]) w (la/scale v 5.0)] w))))


(def
 v40_l147
 (mapv
  (fn [e] (select-keys e [:id :op :inputs]))
  (:entries array-tape)))


(deftest
 t41_l150
 (is
  ((fn
    [entries]
    (and
     (= :la/column (:op (first entries)))
     (= [{:external true}] (:inputs (first entries)))
     (= {:id "t1"} (first (:inputs (second entries))))))
   v40_l147)))


(def
 v43_l161
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


(def v44_l169 (mapv :op (:entries seq-tape)))


(deftest
 t45_l171
 (is ((fn [ops] (= [:la/matrix :la/column :la/mmul] ops)) v44_l169)))


(def
 v47_l180
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
 v48_l187
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries dfn-tape)))


(deftest
 t50_l194
 (is
  ((fn
    [entries]
    (= [:la/matrix :la/matrix :la/add] (mapv :op entries)))
   v48_l187)))


(def
 v52_l206
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
 v53_l214
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries ejml-tape)))


(deftest
 t55_l220
 (is
  ((fn
    [entries]
    (and
     (= [:la/matrix :la/add] (mapv :op entries))
     (:external (second (:inputs (second entries))))))
   v53_l214)))


(def
 v57_l230
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


(def v58_l237 (:complex? (last (:entries complex-tape))))


(deftest t59_l239 (is ((fn [c?] (true? c?)) v58_l237)))


(def v61_l247 (mapv :op (:entries complex-tape)))


(deftest
 t62_l249
 (is ((fn [ops] (= [:la/matrix :la/matrix :la/add] ops)) v61_l247)))


(def v64_l257 (tape/summary tape-result))


(deftest t65_l259 (is ((fn [s] (= 6 (:total s))) v64_l257)))


(def v67_l271 (tape/origin tape-result (:result tape-result)))


(def v69_l287 (tape/mermaid tape-result (:result tape-result)))


(def
 v71_l294
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


(def v72_l307 (tape/summary pipeline-result))


(deftest t73_l309 (is ((fn [s] (= 9 (:total s))) v72_l307)))


(def v75_l314 (tape/mermaid pipeline-result (:result pipeline-result)))
