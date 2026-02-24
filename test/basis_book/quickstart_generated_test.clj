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


(def v3_l23 (la/matrix [[1 2 3] [4 5 6]]))


(deftest t4_l26 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v3_l23)))


(def v6_l30 (la/eye 3))


(deftest t7_l32 (is ((fn [m] (= 1.0 (tensor/mget m 1 1))) v6_l30)))


(def v8_l34 (la/zeros 2 3))


(deftest t9_l36 (is ((fn [m] (= 0.0 (tensor/mget m 0 0))) v8_l34)))


(def v11_l40 (la/diag [1 2 3]))


(deftest
 t12_l42
 (is
  ((fn
    [m]
    (and (= [3 3] (vec (dtype/shape m))) (= 2.0 (tensor/mget m 1 1))))
   v11_l40)))


(def
 v14_l49
 (let
  [A (la/matrix [[1 2] [3 4]]) B (la/matrix [[5 6] [7 8]])]
  (la/mmul A B)))


(deftest t16_l55 (is ((fn [m] (= 19.0 (tensor/mget m 0 0))) v14_l49)))


(def v17_l57 (la/transpose (la/matrix [[1 2] [3 4]])))


(deftest t18_l59 (is ((fn [m] (= 3.0 (tensor/mget m 0 1))) v17_l57)))


(def v20_l63 (la/det (la/matrix [[1 2] [3 4]])))


(deftest
 t21_l65
 (is ((fn [v] (< (Math/abs (- v -2.0)) 1.0E-10)) v20_l63)))


(def v22_l67 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t23_l69 (is ((fn [v] (= v 5.0)) v22_l67)))


(def v24_l71 (la/norm (la/matrix [[1 2] [3 4]])))


(deftest
 t25_l73
 (is ((fn [v] (< (Math/abs (- v 5.477225575051661)) 1.0E-10)) v24_l71)))


(def
 v27_l81
 (la/solve (la/matrix [[2 1] [1 3]]) (la/matrix [[5] [10]])))


(deftest
 t29_l86
 (is
  ((fn
    [x]
    (and
     (< (Math/abs (- (tensor/mget x 0 0) 1.0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget x 1 0) 3.0)) 1.0E-10)))
   v27_l81)))


(def
 v31_l93
 (let
  [{:keys [eigenvalues]} (la/eigen (la/matrix [[2 1] [1 2]]))]
  (sort (map first eigenvalues))))


(deftest
 t33_l98
 (is
  ((fn
    [evs]
    (and
     (< (Math/abs (- (first evs) 1.0)) 1.0E-10)
     (< (Math/abs (- (second evs) 3.0)) 1.0E-10)))
   v31_l93)))


(def v35_l103 (let [{:keys [S]} (la/svd (la/matrix [[1 2] [3 4]]))] S))


(deftest
 t36_l106
 (is ((fn [S] (< (Math/abs (- (first S) 5.4649857)) 1.0E-4)) v35_l103)))


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


(def v44_l130 (bfft/forward [1.0 0.0 1.0 0.0]))


(deftest
 t46_l134
 (is
  ((fn
    [ct]
    (and
     (= [4] (cx/complex-shape ct))
     (< (Math/abs (- (cx/re (ct 0)) 2.0)) 1.0E-10)))
   v44_l130)))


(def
 v48_l139
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   recovered
   (vec (bfft/inverse-real (bfft/forward signal)))]
  recovered))


(deftest
 t49_l143
 (is
  ((fn
    [v]
    (every?
     (fn* [p1__73968#] (< (Math/abs p1__73968#) 1.0E-10))
     (map - v [1.0 2.0 3.0 4.0])))
   v48_l139)))


(def v51_l150 (let [A (la/matrix [[1 2] [3 4]])] (dfn/sum A)))


(deftest t52_l153 (is ((fn [v] (= v 10.0)) v51_l150)))


(def
 v54_l157
 (let [A (la/matrix [[1 2] [3 4]])] (tensor/mget (la/scale 2.0 A) 1 1)))


(deftest t55_l160 (is (= v54_l157 8.0)))
