(ns
 basis-book.quickstart-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.impl.complex :as cx]
  [scicloj.basis.transform :as bfft]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (la/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l33 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v3_l30)))


(def v6_l37 (la/eye 3))


(deftest t7_l39 (is ((fn [m] (= 1.0 (tensor/mget m 1 1))) v6_l37)))


(def v8_l41 (la/zeros 2 3))


(deftest t9_l43 (is ((fn [m] (= 0.0 (tensor/mget m 0 0))) v8_l41)))


(def v11_l47 (la/diag [1 2 3]))


(deftest
 t12_l49
 (is
  ((fn
    [m]
    (and (= [3 3] (vec (dtype/shape m))) (= 2.0 (tensor/mget m 1 1))))
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


(def
 v31_l99
 (sort (map first (:eigenvalues (la/eigen (la/matrix [[2 1] [1 2]]))))))


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
     (fn* [p1__22107#] (< (Math/abs p1__22107#) 1.0E-10))
     (map - v [1.0 2.0 3.0 4.0])))
   v48_l143)))


(def v51_l154 (dfn/sum (la/matrix [[1 2] [3 4]])))


(deftest t52_l156 (is ((fn [v] (= v 10.0)) v51_l154)))


(def
 v54_l160
 (tensor/mget (la/scale 2.0 (la/matrix [[1 2] [3 4]])) 1 1))


(deftest t55_l162 (is (= v54_l160 8.0)))
