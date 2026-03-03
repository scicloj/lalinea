(ns
 lalinea-book.quickstart-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.complex :as cx]
  [scicloj.lalinea.transform :as ft]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (t/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l29 (is ((fn [m] (= [2 3] (t/shape m))) v3_l26)))


(def v6_l33 (t/eye 3))


(deftest t7_l35 (is ((fn [m] (= 1.0 (m 1 1))) v6_l33)))


(def v8_l37 (t/zeros 2 3))


(deftest t9_l39 (is ((fn [m] (= 0.0 (m 0 0))) v8_l37)))


(def v11_l43 (t/diag [1 2 3]))


(deftest
 t12_l45
 (is ((fn [m] (and (= [3 3] (t/shape m)) (= 2.0 (m 1 1)))) v11_l43)))


(def
 v14_l52
 (la/mmul (t/matrix [[1 2] [3 4]]) (t/matrix [[5 6] [7 8]])))


(deftest t16_l57 (is ((fn [m] (= 19.0 (m 0 0))) v14_l52)))


(def v17_l59 (la/transpose (t/matrix [[1 2] [3 4]])))


(deftest t18_l61 (is ((fn [m] (= 3.0 (m 0 1))) v17_l59)))


(def v20_l65 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t21_l67 (is ((fn [v] (< (abs (- v -2.0)) 1.0E-10)) v20_l65)))


(def v22_l69 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t23_l71 (is ((fn [v] (= v 5.0)) v22_l69)))


(def v24_l73 (la/norm (t/matrix [[1 2] [3 4]])))


(deftest
 t25_l75
 (is ((fn [v] (< (abs (- v 5.477225575051661)) 1.0E-10)) v24_l73)))


(def v27_l83 (la/solve (t/matrix [[2 1] [1 3]]) (t/matrix [[5] [10]])))


(deftest
 t29_l88
 (is
  ((fn
    [x]
    (and
     (< (abs (- (x 0 0) 1.0)) 1.0E-10)
     (< (abs (- (x 1 0) 3.0)) 1.0E-10)))
   v27_l83)))


(def v31_l95 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t33_l99
 (is
  ((fn
    [evs]
    (and
     (< (abs (- (first evs) 1.0)) 1.0E-10)
     (< (abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l95)))


(def v35_l104 (:S (la/svd (t/matrix [[1 2] [3 4]]))))


(deftest
 t36_l106
 (is ((fn [S] (< (abs (- (first S) 5.4649857)) 1.0E-4)) v35_l104)))


(def v38_l113 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t39_l115
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v38_l113)))


(def
 v41_l119
 (let
  [A (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A)))


(deftest
 t42_l123
 (is ((fn [ct] (= [2 2] (cx/complex-shape ct))) v41_l119)))


(def v44_l130 (ft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l134
 (is
  ((fn
    [ct]
    (and
     (= [4] (cx/complex-shape ct))
     (< (abs (- (cx/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l130)))


(def
 v48_l139
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (ft/inverse-real (ft/forward signal))]
  recovered))


(deftest
 t49_l143
 (is ((fn [v] (la/close? v (t/matrix [1.0 2.0 3.0 4.0]))) v48_l139)))


(def v51_l149 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t52_l151 (is ((fn [v] (= v 10.0)) v51_l149)))


(def v54_l155 ((la/scale (t/matrix [[1 2] [3 4]]) 2.0) 1 1))


(deftest t55_l157 (is (= v54_l155 8.0)))


(def v57_l163 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t58_l165 (is (= v57_l163 1)))


(def v59_l167 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t60_l169 (is ((fn [v] (> v 1.0)) v59_l167)))


(def
 v62_l173
 (la/close?
  (la/mmul (t/matrix [[2 1] [1 3]]) (la/pinv (t/matrix [[2 1] [1 3]])))
  (t/eye 2)))


(deftest t63_l177 (is (true? v62_l173)))


(def v65_l181 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t66_l183
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v65_l181)))


(def v68_l190 (pr-str (t/matrix [[1 2] [3 4]])))


(deftest
 t69_l192
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v68_l190)))


(def v70_l194 (pr-str (t/column [5 6 7])))


(deftest
 t71_l196
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v70_l194)))


(def v73_l203 (require '[scicloj.lalinea.elementwise :as elem]))


(def v74_l205 (elem/exp (t/column [0.0 1.0 2.0])))


(deftest
 t75_l207
 (is
  ((fn
    [v]
    (la/close? v (t/column [1.0 (math/exp 1.0) (math/exp 2.0)])))
   v74_l205)))


(def v76_l209 (elem/clip (t/column [-2 0.5 3]) -1 1))


(deftest
 t77_l211
 (is ((fn [v] (la/close? v (t/column [-1 0.5 1]))) v76_l209)))


(def v79_l217 (require '[scicloj.lalinea.tape :as tape]))


(def
 v80_l219
 (let
  [{:keys [entries]}
   (tape/with-tape
    (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [1 0])))]
  (mapv :op entries)))


(deftest
 t81_l224
 (is ((fn [ops] (= [:t/matrix :t/column :la/mmul] ops)) v80_l219)))


(def v83_l230 (require '[scicloj.lalinea.grad :as grad]))


(def
 v84_l232
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A A) (t/matrix [[1 0] [0 1]])))))
   grads
   (grad/grad tape-result (:result tape-result))]
  ((.get grads A) 0 0)))


(deftest t85_l239 (is ((fn [v] (number? v)) v84_l232)))
