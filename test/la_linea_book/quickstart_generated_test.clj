(ns
 la-linea-book.quickstart-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [scicloj.la-linea.transform :as bfft]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (la/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l33 (is ((fn [m] (= [2 3] (dtype/shape m))) v3_l30)))


(def v6_l37 (la/eye 3))


(deftest t7_l39 (is ((fn [m] (= 1.0 (tensor/mget m 1 1))) v6_l37)))


(def v8_l41 (la/zeros 2 3))


(deftest t9_l43 (is ((fn [m] (= 0.0 (tensor/mget m 0 0))) v8_l41)))


(def v11_l47 (la/diag [1 2 3]))


(deftest
 t12_l49
 (is
  ((fn [m] (and (= [3 3] (dtype/shape m)) (= 2.0 (tensor/mget m 1 1))))
   v11_l47)))


(def
 v14_l56
 (la/mmul (la/matrix [[1 2] [3 4]]) (la/matrix [[5 6] [7 8]])))


(deftest t16_l61 (is ((fn [m] (= 19.0 (tensor/mget m 0 0))) v14_l56)))


(def v17_l63 (la/transpose (la/matrix [[1 2] [3 4]])))


(deftest t18_l65 (is ((fn [m] (= 3.0 (tensor/mget m 0 1))) v17_l63)))


(def v20_l69 (la/det (la/matrix [[1 2] [3 4]])))


(deftest
 t21_l71
 (is ((fn [v] (< (Math/abs (- v -2.0)) 1.0E-10)) v20_l69)))


(def v22_l73 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t23_l75 (is ((fn [v] (= v 5.0)) v22_l73)))


(def v24_l77 (la/norm (la/matrix [[1 2] [3 4]])))


(deftest
 t25_l79
 (is ((fn [v] (< (Math/abs (- v 5.477225575051661)) 1.0E-10)) v24_l77)))


(def
 v27_l87
 (la/solve (la/matrix [[2 1] [1 3]]) (la/matrix [[5] [10]])))


(deftest
 t29_l92
 (is
  ((fn
    [x]
    (and
     (< (Math/abs (- (tensor/mget x 0 0) 1.0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget x 1 0) 3.0)) 1.0E-10)))
   v27_l87)))


(def v31_l99 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t33_l103
 (is
  ((fn
    [evs]
    (and
     (< (Math/abs (- (first evs) 1.0)) 1.0E-10)
     (< (Math/abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l99)))


(def v35_l108 (:S (la/svd (la/matrix [[1 2] [3 4]]))))


(deftest
 t36_l110
 (is ((fn [S] (< (Math/abs (- (first S) 5.4649857)) 1.0E-4)) v35_l108)))


(def v38_l117 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t39_l119
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v38_l117)))


(def
 v41_l123
 (let
  [A (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A)))


(deftest
 t42_l127
 (is ((fn [ct] (= [2 2] (cx/complex-shape ct))) v41_l123)))


(def v44_l134 (bfft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l138
 (is
  ((fn
    [ct]
    (and
     (= [4] (cx/complex-shape ct))
     (< (Math/abs (- (cx/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l134)))


(def
 v48_l143
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (vec (bfft/inverse-real (bfft/forward signal)))]
  recovered))


(deftest
 t49_l147
 (is
  ((fn
    [v]
    (every?
     (fn* [p1__186193#] (< (Math/abs p1__186193#) 1.0E-10))
     (map - v [1.0 2.0 3.0 4.0])))
   v48_l143)))


(def v51_l154 (dfn/sum (la/matrix [[1 2] [3 4]])))


(deftest t52_l156 (is ((fn [v] (= v 10.0)) v51_l154)))


(def
 v54_l160
 (tensor/mget (la/scale (la/matrix [[1 2] [3 4]]) 2.0) 1 1))


(deftest t55_l162 (is (= v54_l160 8.0)))


(def v57_l168 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t58_l170 (is (= v57_l168 1)))


(def v59_l172 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t60_l174 (is ((fn [v] (> v 1.0)) v59_l172)))


(def
 v62_l178
 (la/close?
  (la/mmul
   (la/matrix [[2 1] [1 3]])
   (la/pinv (la/matrix [[2 1] [1 3]])))
  (la/eye 2)))


(deftest t63_l182 (is (true? v62_l178)))


(def v65_l186 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t66_l188
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v65_l186)))


(def v68_l195 (require '[scicloj.la-linea.print]))


(def v69_l197 (pr-str (la/matrix [[1 2] [3 4]])))


(deftest t70_l199 (is (= v69_l197 "#la/m [[1.0 2.0] [3.0 4.0]]")))


(def v71_l201 (pr-str (la/column [5 6 7])))


(deftest t72_l203 (is (= v71_l201 "#la/v [5.0 6.0 7.0]")))


(def v74_l210 (require '[scicloj.la-linea.elementwise :as elem]))


(def v75_l212 (elem/exp (la/column [0.0 1.0 2.0])))


(deftest
 t76_l214
 (is
  ((fn
    [v]
    (la/close? v (la/column [1.0 (Math/exp 1.0) (Math/exp 2.0)])))
   v75_l212)))


(def v77_l216 (elem/clip (la/column [-2 0.5 3]) -1 1))


(deftest
 t78_l218
 (is ((fn [v] (la/close? v (la/column [-1 0.5 1]))) v77_l216)))


(def v80_l224 (require '[scicloj.la-linea.tape :as tape]))


(def
 v81_l226
 (let
  [{:keys [entries]}
   (tape/with-tape
    (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [1 0])))]
  (mapv :op entries)))


(deftest
 t82_l231
 (is ((fn [ops] (= [:la/matrix :la/column :la/mmul] ops)) v81_l226)))


(def v84_l237 (require '[scicloj.la-linea.grad :as grad]))


(def
 v85_l239
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A A) (la/matrix [[1 0] [0 1]])))))
   grads
   (grad/grad tape-result (:result tape-result))]
  (tensor/mget (.get grads A) 0 0)))


(deftest t86_l246 (is ((fn [v] (number? v)) v85_l239)))
