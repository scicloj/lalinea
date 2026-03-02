(ns
 lalinea-book.first-class-tensors-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (= (la/column [1 2 3]) (la/column [1 2 3])))


(deftest t4_l28 (is (true? v3_l26)))


(def v6_l32 (identical? (la/column [1 2 3]) (la/column [1 2 3])))


(deftest t7_l34 (is (false? v6_l32)))


(def v9_l38 (= (la/matrix [[1 2] [3 4]]) (la/matrix [[1 2] [3 4]])))


(deftest t10_l40 (is (true? v9_l38)))


(def v12_l44 (= (la/add (la/eye 2) (la/eye 2)) (la/scale (la/eye 2) 2)))


(deftest t13_l47 (is (true? v12_l44)))


(def v15_l51 (get {(la/column [1 2 3]) :found} (la/column [1 2 3])))


(deftest t16_l54 (is (= v15_l51 :found)))


(def
 v18_l58
 (count
  (hash-set
   (la/column [1 2 3])
   (la/column [1 2 3])
   (la/column [4 5 6]))))


(deftest t19_l62 (is (= v18_l58 2)))


(def
 v21_l71
 (def
  model
  {:weights (la/matrix [[0.5 0.3] [0.2 0.8]]),
   :bias (la/column [0.1 0.2]),
   :name "simple-model"}))


(def v22_l76 (:name model))


(deftest t23_l78 (is (= v22_l76 "simple-model")))


(def v24_l80 (la/close? (:bias model) (la/column [0.1 0.2])))


(deftest t25_l82 (is (true? v24_l80)))


(def v27_l88 (la/add (la/column [1 2 3]) (la/column [10 20 30])))


(deftest
 t28_l90
 (is ((fn [v] (la/close? v (la/column [11 22 33]))) v27_l88)))


(def v29_l93 (la/scale (la/matrix [[1 2] [3 4]]) 2))


(deftest
 t30_l95
 (is ((fn [m] (la/close? m (la/matrix [[2 4] [6 8]]))) v29_l93)))


(def
 v32_l100
 (-> (la/matrix [[1 0] [0 1]]) (la/scale 3) (la/add (la/eye 2))))


(deftest
 t33_l104
 (is ((fn [m] (la/close? m (la/scale (la/eye 2) 4))) v32_l100)))


(def v35_l112 (la/matrix [[1 2] [3 4]]))


(def v37_l116 (pr-str [[1 2] [3 4]]))


(deftest t38_l118 (is (= v37_l116 "[[1 2] [3 4]]")))


(def
 v40_l123
 (read-string (pr-str {:weights (la/matrix [[1 2] [3 4]])})))


(deftest
 t41_l126
 (is ((fn [v] (= (:weights v) (la/matrix [[1 2] [3 4]]))) v40_l123)))


(def v43_l132 (def original [1 2 3]))


(def v44_l133 (def appended (conj original 4)))


(def v45_l135 (= original [1 2 3]))


(deftest t46_l137 (is (true? v45_l135)))


(def v48_l141 (def mat (la/matrix [[1 2] [3 4]])))


(def v49_l142 (def mat-t (la/transpose mat)))


(def
 v51_l146
 (let
  [arr (dtype/->double-array mat)]
  (aset arr 0 99.0)
  [(tensor/mget mat 0 0) (tensor/mget mat-t 0 0)]))


(deftest t52_l150 (is (= v51_l146 [99.0 99.0])))


(def
 v54_l160
 (let
  [[a b c] (tensor/->tensor [10.0 20.0 30.0] :datatype :float64)]
  (+ a b c)))


(deftest t55_l163 (is ((fn [v] (== 60.0 v)) v54_l160)))


(def
 v57_l169
 (let
  [v (la/column [10 20 30])]
  (+ (tensor/mget v 0 0) (tensor/mget v 1 0) (tensor/mget v 2 0))))


(deftest t58_l174 (is ((fn [v] (== 60.0 v)) v57_l169)))


(def
 v60_l180
 (reduce + 0 (tensor/->tensor [1.0 2.0 3.0 4.0] :datatype :float64)))


(deftest t61_l182 (is ((fn [v] (== 10.0 v)) v60_l180)))


(def v63_l186 (dfn/sum (la/column [1 2 3 4])))


(deftest t64_l188 (is ((fn [v] (== 10.0 v)) v63_l186)))


(def v66_l198 (defn read-matrix [form] (la/matrix form)))


(def v67_l201 (defn read-column [form] (la/column form)))


(def v69_l206 (read-matrix [[1 2] [3 4]]))


(deftest t70_l208 (is ((fn [m] (= [2 2] (dtype/shape m))) v69_l206)))


(def v71_l210 (read-column [1 2 3]))


(deftest t72_l212 (is ((fn [v] (= [3 1] (dtype/shape v))) v71_l210)))


(def
 v74_l216
 (defn
  tensor->tagged-str
  [t]
  (let
   [shape (dtype/shape t)]
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


(def v75_l233 (tensor->tagged-str (la/matrix [[1 2] [3 4]])))


(deftest
 t76_l235
 (is ((fn [s] (clojure.string/starts-with? s "#la/m")) v75_l233)))


(def v77_l237 (tensor->tagged-str (la/column [5 6 7])))


(deftest
 t78_l239
 (is ((fn [s] (clojure.string/starts-with? s "#la/v")) v77_l237)))


(def v80_l250 (defn v [& xs] (la/column xs)))


(def v81_l253 (defn m [& rows] (la/matrix rows)))


(def v82_l256 (v 1 2 3))


(deftest t83_l258 (is ((fn [c] (= [3 1] (dtype/shape c))) v82_l256)))


(def v84_l260 (m [1 2] [3 4]))


(deftest
 t85_l262
 (is ((fn [mat] (= [2 2] (dtype/shape mat))) v84_l260)))
