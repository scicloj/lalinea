(ns
 la-linea-book.first-class-tensors-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l27 (= (la/column [1 2 3]) (la/column [1 2 3])))


(deftest t4_l29 (is (true? v3_l27)))


(def v6_l33 (identical? (la/column [1 2 3]) (la/column [1 2 3])))


(deftest t7_l35 (is (false? v6_l33)))


(def v9_l39 (= (la/matrix [[1 2] [3 4]]) (la/matrix [[1 2] [3 4]])))


(deftest t10_l41 (is (true? v9_l39)))


(def v12_l45 (= (la/add (la/eye 2) (la/eye 2)) (la/scale (la/eye 2) 2)))


(deftest t13_l48 (is (true? v12_l45)))


(def v15_l52 (get {(la/column [1 2 3]) :found} (la/column [1 2 3])))


(deftest t16_l55 (is (= v15_l52 :found)))


(def
 v18_l59
 (count
  (hash-set
   (la/column [1 2 3])
   (la/column [1 2 3])
   (la/column [4 5 6]))))


(deftest t19_l63 (is (= v18_l59 2)))


(def
 v21_l72
 (def
  model
  {:weights (la/matrix [[0.5 0.3] [0.2 0.8]]),
   :bias (la/column [0.1 0.2]),
   :name "simple-model"}))


(def v22_l77 (:name model))


(deftest t23_l79 (is (= v22_l77 "simple-model")))


(def v24_l81 (la/close? (:bias model) (la/column [0.1 0.2])))


(deftest t25_l83 (is (true? v24_l81)))


(def v27_l89 (la/add (la/column [1 2 3]) (la/column [10 20 30])))


(def v28_l91 (la/scale (la/matrix [[1 2] [3 4]]) 2))


(def
 v30_l95
 (-> (la/matrix [[1 0] [0 1]]) (la/scale 3) (la/add (la/eye 2))))


(deftest
 t31_l99
 (is ((fn [m] (la/close? m (la/scale (la/eye 2) 4))) v30_l95)))


(def v33_l107 (la/matrix [[1 2] [3 4]]))


(def v35_l111 (pr-str [[1 2] [3 4]]))


(deftest t36_l113 (is (= v35_l111 "[[1 2] [3 4]]")))


(def
 v38_l121
 (try
  (read-string (pr-str {:weights (la/matrix [[1 2] [3 4]])}))
  (catch Exception _ :read-failed)))


(deftest t39_l127 (is (= v38_l121 :read-failed)))


(def v41_l133 (def original [1 2 3]))


(def v42_l134 (def appended (conj original 4)))


(def v43_l136 (= original [1 2 3]))


(deftest t44_l138 (is (true? v43_l136)))


(def v46_l142 (def mat (la/matrix [[1 2] [3 4]])))


(def v47_l143 (def mat-t (la/transpose mat)))


(def
 v49_l147
 (let
  [arr (dtype/->double-array mat)]
  (aset arr 0 99.0)
  [(tensor/mget mat 0 0) (tensor/mget mat-t 0 0)]))


(deftest t50_l151 (is (= v49_l147 [99.0 99.0])))


(def
 v52_l161
 (let
  [[a b c] (tensor/->tensor [10.0 20.0 30.0] :datatype :float64)]
  (+ a b c)))


(deftest t53_l164 (is ((fn [v] (== 60.0 v)) v52_l161)))


(def
 v55_l170
 (let
  [v (la/column [10 20 30])]
  (+ (tensor/mget v 0 0) (tensor/mget v 1 0) (tensor/mget v 2 0))))


(deftest t56_l175 (is ((fn [v] (== 60.0 v)) v55_l170)))


(def
 v58_l181
 (reduce + 0 (tensor/->tensor [1.0 2.0 3.0 4.0] :datatype :float64)))


(deftest t59_l183 (is ((fn [v] (== 10.0 v)) v58_l181)))


(def v61_l187 (dfn/sum (la/column [1 2 3 4])))


(deftest t62_l189 (is ((fn [v] (== 10.0 v)) v61_l187)))


(def v64_l198 (defn read-matrix [form] (la/matrix form)))


(def v65_l201 (defn read-column [form] (la/column form)))


(def v67_l206 (read-matrix [[1 2] [3 4]]))


(deftest
 t68_l208
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v67_l206)))


(def v69_l210 (read-column [1 2 3]))


(deftest
 t70_l212
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v69_l210)))


(def
 v72_l216
 (defn
  tensor->tagged-str
  [t]
  (let
   [shape (vec (dtype/shape t))]
   (cond
    (and (= 2 (count shape)) (= 1 (second shape)))
    (str "#la/v " (vec (dtype/->reader t :float64)))
    (= 2 (count shape))
    (let
     [[r c] shape]
     (str
      "#la/m "
      (vec
       (for
        [i (range r)]
        (vec (for [j (range c)] (tensor/mget t i j)))))))
    :else
    (str t)))))


(def v73_l233 (tensor->tagged-str (la/matrix [[1 2] [3 4]])))


(deftest t74_l235 (is (= v73_l233 "#la/m [[1.0 2.0] [3.0 4.0]]")))


(def v75_l237 (tensor->tagged-str (la/column [5 6 7])))


(deftest t76_l239 (is (= v75_l237 "#la/v [5.0 6.0 7.0]")))


(def v78_l250 (defn v [& xs] (la/column xs)))


(def v79_l253 (defn m [& rows] (la/matrix rows)))


(def v80_l256 (v 1 2 3))


(deftest
 t81_l258
 (is ((fn [c] (= [3 1] (vec (dtype/shape c)))) v80_l256)))


(def v82_l260 (m [1 2] [3 4]))


(deftest
 t83_l262
 (is ((fn [mat] (= [2 2] (vec (dtype/shape mat)))) v82_l260)))
