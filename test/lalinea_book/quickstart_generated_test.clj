(ns
 lalinea-book.quickstart-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.transform :as ft]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l25 (t/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l28 (is ((fn [m] (= [2 3] (t/shape m))) v3_l25)))


(def v6_l32 (t/eye 3))


(deftest t7_l34 (is ((fn [m] (= 1.0 (m 1 1))) v6_l32)))


(def v8_l36 (t/zeros 2 3))


(deftest t9_l38 (is ((fn [m] (= 0.0 (m 0 0))) v8_l36)))


(def v11_l42 (t/diag [1 2 3]))


(deftest
 t12_l44
 (is ((fn [m] (and (= [3 3] (t/shape m)) (= 2.0 (m 1 1)))) v11_l42)))


(def
 v14_l51
 (la/mmul (t/matrix [[1 2] [3 4]]) (t/matrix [[5 6] [7 8]])))


(deftest t16_l56 (is ((fn [m] (= 19.0 (m 0 0))) v14_l51)))


(def v17_l58 (la/transpose (t/matrix [[1 2] [3 4]])))


(deftest t18_l60 (is ((fn [m] (= 3.0 (m 0 1))) v17_l58)))


(def v20_l64 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t21_l66 (is ((fn [v] (< (abs (- v -2.0)) 1.0E-10)) v20_l64)))


(def v22_l68 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t23_l70 (is ((fn [v] (= v 5.0)) v22_l68)))


(def v24_l72 (la/norm (t/matrix [[1 2] [3 4]])))


(deftest
 t25_l74
 (is ((fn [v] (< (abs (- v 5.477225575051661)) 1.0E-10)) v24_l72)))


(def v27_l82 (la/solve (t/matrix [[2 1] [1 3]]) (t/matrix [[5] [10]])))


(deftest
 t29_l87
 (is
  ((fn
    [x]
    (and
     (< (abs (- (x 0 0) 1.0)) 1.0E-10)
     (< (abs (- (x 1 0) 3.0)) 1.0E-10)))
   v27_l82)))


(def v31_l94 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t33_l98
 (is
  ((fn
    [evs]
    (and
     (< (abs (- (first evs) 1.0)) 1.0E-10)
     (< (abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l94)))


(def v35_l103 (:S (la/svd (t/matrix [[1 2] [3 4]]))))


(deftest
 t36_l105
 (is ((fn [S] (< (abs (- (first S) 5.4649857)) 1.0E-4)) v35_l103)))


(def v38_l115 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t39_l117
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v38_l115)))


(def
 v41_l121
 (let
  [A (t/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A)))


(deftest
 t42_l125
 (is ((fn [ct] (= [2 2] (t/complex-shape ct))) v41_l121)))


(def v44_l132 (ft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l136
 (is
  ((fn
    [ct]
    (and
     (= [4] (t/complex-shape ct))
     (< (abs (- (la/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l132)))


(def
 v48_l141
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (ft/inverse-real (ft/forward signal))]
  recovered))


(deftest
 t49_l145
 (is ((fn [v] (la/close? v (t/matrix [1.0 2.0 3.0 4.0]))) v48_l141)))


(def v51_l151 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t52_l153 (is ((fn [v] (= v 10.0)) v51_l151)))


(def v54_l157 ((la/scale (t/matrix [[1 2] [3 4]]) 2.0) 1 1))


(deftest t55_l159 (is (= v54_l157 8.0)))


(def v57_l165 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t58_l167 (is (= v57_l165 1)))


(def v59_l169 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t60_l171 (is ((fn [v] (> v 1.0)) v59_l169)))


(def
 v62_l175
 (la/close?
  (la/mmul (t/matrix [[2 1] [1 3]]) (la/pinv (t/matrix [[2 1] [1 3]])))
  (t/eye 2)))


(deftest t63_l179 (is (true? v62_l175)))


(def v65_l183 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t66_l185
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v65_l183)))


(def v68_l192 (pr-str (t/matrix [[1 2] [3 4]])))


(deftest
 t69_l194
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v68_l192)))


(def v70_l196 (pr-str (t/column [5 6 7])))


(deftest
 t71_l198
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v70_l196)))


(def v73_l208 (require '[scicloj.lalinea.elementwise :as elem]))


(def v74_l210 (elem/exp (t/column [0.0 1.0 2.0])))


(deftest
 t75_l212
 (is
  ((fn
    [v]
    (la/close? v (t/column [1.0 (math/exp 1.0) (math/exp 2.0)])))
   v74_l210)))


(def v76_l214 (elem/clip (t/column [-2 0.5 3]) -1 1))


(deftest
 t77_l216
 (is ((fn [v] (la/close? v (t/column [-1 0.5 1]))) v76_l214)))


(def v79_l222 (require '[scicloj.lalinea.tape :as tape]))


(def
 v80_l224
 (let
  [{:keys [entries]}
   (tape/with-tape
    (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [1 0])))]
  (mapv :op entries)))


(deftest
 t81_l229
 (is ((fn [ops] (= [:t/matrix :t/column :la/mmul] ops)) v80_l224)))


(def v83_l235 (require '[scicloj.lalinea.grad :as grad]))


(def
 v84_l237
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A A) (t/matrix [[1 0] [0 1]])))))
   grads
   (grad/grad tape-result (:result tape-result))]
  ((.get grads A) 0 0)))


(deftest t85_l244 (is ((fn [v] (number? v)) v84_l237)))
