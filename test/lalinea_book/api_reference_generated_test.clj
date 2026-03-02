(ns
 lalinea-book.api-reference-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [scicloj.lalinea.transform :as ft]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.grad :as grad]
  [scicloj.lalinea.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (kind/doc #'la/matrix))


(def v4_l35 (la/matrix [[1 2] [3 4]]))


(deftest t5_l37 (is ((fn [m] (= [2 2] (dtype/shape m))) v4_l35)))


(def v6_l39 (kind/doc #'la/eye))


(def v7_l41 (la/eye 3))


(deftest
 t8_l43
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l41)))


(def v9_l47 (kind/doc #'la/zeros))


(def v10_l49 (la/zeros 2 3))


(deftest t11_l51 (is ((fn [m] (= [2 3] (dtype/shape m))) v10_l49)))


(def v12_l53 (kind/doc #'la/diag))


(def v13_l55 (la/diag [3 5 7]))


(deftest
 t14_l57
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l55)))


(def v15_l61 (kind/doc #'la/column))


(def v16_l63 (la/column [1 2 3]))


(deftest t17_l65 (is ((fn [v] (= [3 1] (dtype/shape v))) v16_l63)))


(def v18_l67 (kind/doc #'la/row))


(def v19_l69 (la/row [1 2 3]))


(deftest t20_l71 (is ((fn [v] (= [1 3] (dtype/shape v))) v19_l69)))


(def v21_l73 (kind/doc #'la/add))


(def
 v22_l75
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l78 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l75)))


(def v24_l80 (kind/doc #'la/sub))


(def
 v25_l82
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l85 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l82)))


(def v27_l87 (kind/doc #'la/scale))


(def v28_l89 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l91 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l89)))


(def v30_l93 (kind/doc #'la/mul))


(def
 v31_l95
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l98
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l95)))


(def v33_l101 (kind/doc #'la/abs))


(def v34_l103 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l105 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l103)))


(def v36_l107 (kind/doc #'la/sq))


(def v37_l109 (la/sq (la/matrix [[2 3] [4 5]])))


(deftest t38_l111 (is ((fn [m] (== 4.0 (tensor/mget m 0 0))) v37_l109)))


(def v39_l113 (kind/doc #'la/sum))


(def v40_l115 (la/sum (la/matrix [[1 2] [3 4]])))


(deftest t41_l117 (is ((fn [v] (== 10.0 v)) v40_l115)))


(def v42_l119 (kind/doc #'la/reduce-axis))
