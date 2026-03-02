(ns
 lalinea-book.quickstart-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [scicloj.lalinea.transform :as ft]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (la/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l34 (is ((fn [m] (= [2 3] (dtype/shape m))) v3_l31)))


(def v6_l38 (la/eye 3))


(deftest t7_l40 (is ((fn [m] (= 1.0 (tensor/mget m 1 1))) v6_l38)))


(def v8_l42 (la/zeros 2 3))


(deftest t9_l44 (is ((fn [m] (= 0.0 (tensor/mget m 0 0))) v8_l42)))


(def v11_l48 (la/diag [1 2 3]))


(deftest
 t12_l50
 (is
  ((fn [m] (and (= [3 3] (dtype/shape m)) (= 2.0 (tensor/mget m 1 1))))
   v11_l48)))


(def
 v14_l57
 (la/mmul (la/matrix [[1 2] [3 4]]) (la/matrix [[5 6] [7 8]])))


(deftest t16_l62 (is ((fn [m] (= 19.0 (tensor/mget m 0 0))) v14_l57)))


(def v17_l64 (la/transpose (la/matrix [[1 2] [3 4]])))


(deftest t18_l66 (is ((fn [m] (= 3.0 (tensor/mget m 0 1))) v17_l64)))


(def v20_l70 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t21_l72 (is ((fn [v] (< (abs (- v -2.0)) 1.0E-10)) v20_l70)))


(def v22_l74 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t23_l76 (is ((fn [v] (= v 5.0)) v22_l74)))


(def v24_l78 (la/norm (la/matrix [[1 2] [3 4]])))


(deftest
 t25_l80
 (is ((fn [v] (< (abs (- v 5.477225575051661)) 1.0E-10)) v24_l78)))


(def
 v27_l88
 (la/solve (la/matrix [[2 1] [1 3]]) (la/matrix [[5] [10]])))


(deftest
 t29_l93
 (is
  ((fn
    [x]
    (and
     (< (abs (- (tensor/mget x 0 0) 1.0)) 1.0E-10)
     (< (abs (- (tensor/mget x 1 0) 3.0)) 1.0E-10)))
   v27_l88)))


(def v31_l100 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t33_l104
 (is
  ((fn
    [evs]
    (and
     (< (abs (- (first evs) 1.0)) 1.0E-10)
     (< (abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l100)))


(def v35_l109 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest
 t36_l111
 (is ((fn [S] (< (abs (- (first S) 5.4649857)) 1.0E-4)) v35_l109)))


(def v38_l118 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t39_l120
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v38_l118)))


(def
 v41_l124
 (let
  [A (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A)))


(deftest
 t42_l128
 (is ((fn [ct] (= [2 2] (cx/complex-shape ct))) v41_l124)))


(def v44_l135 (ft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l139
 (is
  ((fn
    [ct]
    (and
     (= [4] (cx/complex-shape ct))
     (< (abs (- (cx/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l135)))


(def
 v48_l144
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (ft/inverse-real (ft/forward signal))]
  recovered))


(deftest
 t49_l148
 (is
  ((fn
    [v]
    (every?
     (fn* [p1__25531#] (< (abs p1__25531#) 1.0E-10))
     (map - v [1.0 2.0 3.0 4.0])))
   v48_l144)))


(def v51_l155 (dfn/sum (la/matrix [[1 2] [3 4]])))


(deftest t52_l157 (is ((fn [v] (= v 10.0)) v51_l155)))


(def
 v54_l161
 (tensor/mget (la/scale (la/matrix [[1 2] [3 4]]) 2.0) 1 1))


(deftest t55_l163 (is (= v54_l161 8.0)))


(def v57_l169 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t58_l171 (is (= v57_l169 1)))


(def v59_l173 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t60_l175 (is ((fn [v] (> v 1.0)) v59_l173)))


(def
 v62_l179
 (la/close?
  (la/mmul
   (la/matrix [[2 1] [1 3]])
   (la/pinv (la/matrix [[2 1] [1 3]])))
  (la/eye 2)))


(deftest t63_l183 (is (true? v62_l179)))


(def v65_l187 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t66_l189
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v65_l187)))


(def v68_l196 (pr-str (la/matrix [[1 2] [3 4]])))


(deftest
 t69_l198
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v68_l196)))


(def v70_l200 (pr-str (la/column [5 6 7])))


(deftest
 t71_l202
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v70_l200)))


(def v73_l209 (require '[scicloj.lalinea.elementwise :as elem]))


(def v74_l211 (elem/exp (la/column [0.0 1.0 2.0])))


(deftest
 t75_l213
 (is
  ((fn
    [v]
    (la/close? v (la/column [1.0 (math/exp 1.0) (math/exp 2.0)])))
   v74_l211)))


(def v76_l215 (elem/clip (la/column [-2 0.5 3]) -1 1))


(deftest
 t77_l217
 (is ((fn [v] (la/close? v (la/column [-1 0.5 1]))) v76_l215)))


(def v79_l223 (require '[scicloj.lalinea.tape :as tape]))


(def
 v80_l225
 (let
  [{:keys [entries]}
   (tape/with-tape
    (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [1 0])))]
  (mapv :op entries)))


(deftest
 t81_l230
 (is ((fn [ops] (= [:la/matrix :la/column :la/mmul] ops)) v80_l225)))


(def v83_l236 (require '[scicloj.lalinea.grad :as grad]))


(def
 v84_l238
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A A) (la/matrix [[1 0] [0 1]])))))
   grads
   (grad/grad tape-result (:result tape-result))]
  (tensor/mget (.get grads A) 0 0)))


(deftest t85_l245 (is ((fn [v] (number? v)) v84_l238)))
