(ns
 la-linea-book.computation-tape-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.datatype :as dtype]
  [tech.v3.tensor :as tensor]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l28 (def A (la/matrix [[1 2] [3 4]])))


(def v4_l30 (tape/memory-status A))


(deftest t5_l32 (is ((fn [s] (= :contiguous s)) v4_l30)))


(def v7_l38 (tape/memory-status (la/transpose A)))


(deftest t8_l40 (is ((fn [s] (= :strided s)) v7_l38)))


(def v10_l46 (def B (la/matrix [[5 6] [7 8]])))


(def v11_l48 (tape/memory-status (la/add A B)))


(deftest t12_l50 (is ((fn [s] (= :lazy s)) v11_l48)))


(def v14_l55 (tape/memory-status (la/mmul A B)))


(deftest t15_l57 (is ((fn [s] (= :contiguous s)) v14_l55)))


(def v17_l66 (tape/shares-memory? A (la/transpose A)))


(deftest t18_l68 (is ((fn [b] (true? b)) v17_l66)))


(def v20_l73 (tape/shares-memory? A B))


(deftest t21_l75 (is ((fn [b] (false? b)) v20_l73)))


(def v23_l81 (tape/shares-memory? A (la/add A B)))


(deftest t24_l83 (is ((fn [b] (false? b)) v23_l81)))


(def
 v26_l91
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


(def v27_l100 (:result tape-result))


(def v29_l104 (count (:entries tape-result)))


(deftest t30_l106 (is ((fn [n] (= 6 n)) v29_l104)))


(def
 v32_l112
 (mapv
  (fn [e] (select-keys e [:id :op :shape]))
  (:entries tape-result)))


(def v34_l120 (tape/summary tape-result))


(def v36_l132 (tape/origin tape-result (:result tape-result)))


(def
 v38_l148
 (kind/mermaid (tape/mermaid tape-result (:result tape-result))))


(def
 v40_l155
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


(def v41_l168 (tape/summary pipeline-result))


(def
 v43_l173
 (kind/mermaid
  (tape/mermaid pipeline-result (:result pipeline-result))))
