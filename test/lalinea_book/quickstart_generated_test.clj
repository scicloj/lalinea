(ns
 lalinea-book.quickstart-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.transform :as ft]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l24 (t/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l27 (is ((fn [m] (= [2 3] (t/shape m))) v3_l24)))


(def v6_l31 (t/eye 3))


(deftest t7_l33 (is ((fn [m] (= 1.0 (m 1 1))) v6_l31)))


(def v8_l35 (t/zeros 2 3))


(deftest t9_l37 (is ((fn [m] (= 0.0 (m 0 0))) v8_l35)))


(def v11_l41 (t/diag [1 2 3]))


(deftest
 t12_l43
 (is ((fn [m] (and (= [3 3] (t/shape m)) (= 2.0 (m 1 1)))) v11_l41)))


(def
 v14_l50
 (la/mmul (t/matrix [[1 2] [3 4]]) (t/matrix [[5 6] [7 8]])))


(deftest t16_l55 (is ((fn [m] (= 19.0 (m 0 0))) v14_l50)))


(def v17_l57 (la/transpose (t/matrix [[1 2] [3 4]])))


(deftest t18_l59 (is ((fn [m] (= 3.0 (m 0 1))) v17_l57)))


(def v20_l63 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t21_l65 (is ((fn [v] (< (abs (- v -2.0)) 1.0E-10)) v20_l63)))


(def v22_l67 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t23_l69 (is ((fn [v] (= v 5.0)) v22_l67)))


(def v24_l71 (la/norm (t/matrix [[1 2] [3 4]])))


(deftest
 t25_l73
 (is ((fn [v] (< (abs (- v 5.477225575051661)) 1.0E-10)) v24_l71)))


(def v27_l81 (la/solve (t/matrix [[2 1] [1 3]]) (t/matrix [[5] [10]])))


(deftest
 t29_l86
 (is
  ((fn
    [x]
    (and
     (< (abs (- (x 0 0) 1.0)) 1.0E-10)
     (< (abs (- (x 1 0) 3.0)) 1.0E-10)))
   v27_l81)))


(def v31_l93 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t33_l97
 (is
  ((fn
    [evs]
    (and
     (< (abs (- (first evs) 1.0)) 1.0E-10)
     (< (abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l93)))


(def v35_l102 (:S (la/svd (t/matrix [[1 2] [3 4]]))))


(deftest
 t36_l104
 (is ((fn [S] (< (abs (- (first S) 5.4649857)) 1.0E-4)) v35_l102)))


(def v38_l114 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t39_l116
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v38_l114)))


(def
 v41_l120
 (let
  [A (t/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A)))


(deftest
 t42_l124
 (is ((fn [ct] (= [2 2] (t/complex-shape ct))) v41_l120)))


(def v44_l131 (ft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l135
 (is
  ((fn
    [ct]
    (and
     (= [4] (t/complex-shape ct))
     (< (abs (- (la/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l131)))


(def
 v48_l140
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (ft/inverse-real (ft/forward signal))]
  recovered))


(deftest
 t49_l144
 (is ((fn [v] (la/close? v (t/matrix [1.0 2.0 3.0 4.0]))) v48_l140)))


(def v51_l150 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t52_l152 (is ((fn [v] (= v 10.0)) v51_l150)))


(def v54_l156 ((la/scale (t/matrix [[1 2] [3 4]]) 2.0) 1 1))


(deftest t55_l158 (is (= v54_l156 8.0)))


(def v57_l164 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t58_l166 (is (= v57_l164 1)))


(def v59_l168 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t60_l170 (is ((fn [v] (> v 1.0)) v59_l168)))


(def
 v62_l174
 (la/close?
  (la/mmul (t/matrix [[2 1] [1 3]]) (la/pinv (t/matrix [[2 1] [1 3]])))
  (t/eye 2)))


(deftest t63_l178 (is (true? v62_l174)))


(def v65_l182 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t66_l184
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v65_l182)))


(def v68_l191 (pr-str (t/matrix [[1 2] [3 4]])))


(deftest
 t69_l193
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v68_l191)))


(def v70_l195 (pr-str (t/column [5 6 7])))


(deftest
 t71_l197
 (is ((fn [s] (clojure.string/starts-with? s "#la/R")) v70_l195)))


(def v73_l207 (require '[scicloj.lalinea.elementwise :as elem]))


(def v74_l209 (elem/exp (t/column [0.0 1.0 2.0])))


(deftest
 t75_l211
 (is
  ((fn
    [v]
    (la/close? v (t/column [1.0 (math/exp 1.0) (math/exp 2.0)])))
   v74_l209)))


(def v76_l213 (elem/clip (t/column [-2 0.5 3]) -1 1))


(deftest
 t77_l215
 (is ((fn [v] (la/close? v (t/column [-1 0.5 1]))) v76_l213)))


(def v79_l221 (require '[scicloj.lalinea.tape :as tape]))


(def
 v80_l223
 (let
  [{:keys [entries]}
   (tape/with-tape
    (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [1 0])))]
  (mapv :op entries)))


(deftest
 t81_l228
 (is ((fn [ops] (= [:t/matrix :t/column :la/mmul] ops)) v80_l223)))


(def v83_l234 (require '[scicloj.lalinea.grad :as grad]))


(def
 v84_l236
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A A) (t/matrix [[1 0] [0 1]])))))]
  ((grad/grad tape-result (:result tape-result) A) 0 0)))


(deftest t85_l242 (is ((fn [v] (number? v)) v84_l236)))
