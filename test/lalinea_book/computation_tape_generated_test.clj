(ns
 lalinea-book.computation-tape-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.tape :as tape]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l28 (def A (t/matrix [[1 2] [3 4]])))


(def v4_l30 (tape/memory-status A))


(deftest t5_l32 (is ((fn [s] (= :contiguous s)) v4_l30)))


(def v7_l38 (tape/memory-status (la/transpose A)))


(deftest t8_l40 (is ((fn [s] (= :strided s)) v7_l38)))


(def v10_l46 (def B (t/matrix [[5 6] [7 8]])))


(def v11_l48 (tape/memory-status (la/add A B)))


(deftest t12_l50 (is ((fn [s] (= :lazy s)) v11_l48)))


(def v14_l55 (tape/memory-status (la/mmul A B)))


(deftest t15_l57 (is ((fn [s] (= :contiguous s)) v14_l55)))


(def v17_l71 (tape/memory-relation A (la/transpose A)))


(deftest t18_l73 (is ((fn [r] (= :shared r)) v17_l71)))


(def v20_l78 (tape/memory-relation A B))


(deftest t21_l80 (is ((fn [r] (= :independent r)) v20_l78)))


(def v23_l85 (def arr (double-array [10 20 30])))


(def v24_l87 (tape/memory-relation (t/column arr) (t/column arr)))


(deftest t25_l89 (is ((fn [r] (= :shared r)) v24_l87)))


(def v27_l96 (tape/memory-relation A (la/add A B)))


(deftest t28_l98 (is ((fn [r] (= :unknown-lazy r)) v27_l96)))


(def
 v30_l105
 (let
  [tr
   (tape/with-tape
    (let [M (t/matrix [[1 2] [3 4]]) S (la/add M M)] S))]
  (tape/detect-memory-status (last (:entries tr)))))


(deftest t31_l111 (is ((fn [s] (= :reads-through s)) v30_l105)))


(def
 v33_l119
 (def
  tape-result
  (tape/with-tape
   (let
    [M
     (t/matrix [[1 2] [3 4]])
     S
     (la/scale M 2.0)
     I
     (t/eye 2)
     C
     (la/add S I)
     D
     (la/mmul C (la/transpose M))]
    D))))


(def v34_l128 (dissoc tape-result :registry))


(deftest
 t35_l130
 (is
  ((fn
    [tr]
    (and (t/real-tensor? (:result tr)) (= 6 (count (:entries tr)))))
   v34_l128)))


(def
 v37_l150
 (def
  array-tape
  (tape/with-tape (let [v (t/column [1 2 3]) w (la/scale v 5.0)] w))))


(def
 v38_l156
 (mapv
  (fn [e] (select-keys e [:id :op :inputs]))
  (:entries array-tape)))


(deftest
 t39_l159
 (is
  ((fn
    [entries]
    (and
     (= :t/column (:op (first entries)))
     (= [{:external true}] (:inputs (first entries)))
     (= {:id "t1"} (first (:inputs (second entries))))))
   v38_l156)))


(def
 v41_l170
 (def
  seq-tape
  (tape/with-tape
   (let
    [M
     (t/matrix
      (for [i (range 3)] (for [j (range 3)] (* (inc i) (inc j)))))
     v
     (t/column (repeat 3 1.0))]
    (la/mmul M v)))))


(def v42_l178 (mapv :op (:entries seq-tape)))


(deftest
 t43_l180
 (is ((fn [ops] (= [:t/matrix :t/column :la/mmul] ops)) v42_l178)))


(def
 v45_l189
 (def
  dfn-tape
  (tape/with-tape
   (let
    [A
     (t/matrix [[1 2] [3 4]])
     doubled
     (la/mul A 2.0)
     result
     (la/add (t/matrix doubled) A)]
    result))))


(def
 v46_l196
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries dfn-tape)))


(deftest
 t48_l203
 (is
  ((fn
    [entries]
    (= [:t/matrix :la/mul :t/matrix :la/add] (mapv :op entries)))
   v46_l196)))


(def
 v50_l215
 (def
  ejml-tape
  (tape/with-tape
   (let
    [dm
     (doto (DMatrixRMaj. 2 2) (.setData (double-array [1 0 0 1])))
     I
     (t/dmat->tensor dm)
     result
     (la/add (t/matrix [[5 6] [7 8]]) I)]
    result))))


(def
 v51_l223
 (mapv (fn [e] (select-keys e [:id :op :inputs])) (:entries ejml-tape)))


(deftest
 t53_l229
 (is
  ((fn
    [entries]
    (and
     (= [:t/matrix :la/add] (mapv :op entries))
     (:external (second (:inputs (second entries))))))
   v51_l223)))


(def
 v55_l239
 (def
  complex-tape
  (tape/with-tape
   (let
    [z1
     (t/complex-tensor (t/matrix [[1 0] [0 1]]))
     z2
     (t/complex-tensor (t/matrix [[0 1] [1 0]]))
     s
     (la/add z1 z2)]
    s))))


(def v56_l246 (mapv :op (:entries complex-tape)))


(deftest
 t57_l248
 (is
  ((fn
    [ops]
    (=
     [:t/matrix :t/complex-tensor :t/matrix :t/complex-tensor :la/add]
     ops))
   v56_l246)))


(def
 v59_l257
 (mapv
  :op
  (:entries
   (tape/with-tape
    (la/add (t/complex-tensor [1 2]) (t/complex-tensor [3 4]))))))


(deftest
 t60_l261
 (is
  ((fn [ops] (= [:t/complex-tensor :t/complex-tensor :la/add] ops))
   v59_l257)))


(def v62_l269 (tape/summary tape-result))


(deftest t63_l271 (is ((fn [s] (= 6 (:total s))) v62_l269)))


(def v65_l283 (tape/origin tape-result (:result tape-result)))


(def v67_l299 (tape/mermaid tape-result (:result tape-result)))


(def
 v69_l306
 (def
  pipeline-result
  (tape/with-tape
   (let
    [data
     (t/matrix [[1 0 2] [0 3 0] [4 0 5]])
     centered
     (la/sub
      data
      (la/scale
       (t/matrix [[1 1 1] [1 1 1] [1 1 1]])
       (/ (double (la/trace data)) 3.0)))
     {:keys [U S Vt]}
     (la/svd centered)
     projection
     (la/mmul (la/transpose Vt) (t/column [1 0 0]))]
    projection))))


(def v70_l319 (tape/summary pipeline-result))


(deftest t71_l321 (is ((fn [s] (= 9 (:total s))) v70_l319)))


(def v73_l326 (tape/mermaid pipeline-result (:result pipeline-result)))
