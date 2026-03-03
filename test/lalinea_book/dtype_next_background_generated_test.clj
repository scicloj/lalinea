(ns
 lalinea-book.dtype-next-background-generated-test
 (:require
  [tech.v3.tensor :as dtt]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l22
 (def t (dtt/->tensor [[1 2 3] [4 5 6]] {:datatype :float64})))


(def v4_l25 (dtype/shape t))


(deftest t5_l27 (is ((fn [s] (= [2 3] s)) v4_l25)))


(def v7_l31 [(t 0 1) (t 1 0)])


(deftest t8_l33 (is ((fn [v] (= [2.0 4.0] v)) v7_l31)))


(def
 v10_l41
 (let
  [a
   (dtt/->tensor [1 2 3] {:datatype :float64})
   b
   (dtt/->tensor [10 20 30] {:datatype :float64})
   s
   (dfn/+ a b)]
  {:values (vec s), :has-array? (some? (dtype/as-array-buffer s))}))


(deftest
 t11_l47
 (is
  ((fn
    [{:keys [values has-array?]}]
    (and (= [11.0 22.0 33.0] values) (not has-array?)))
   v10_l41)))


(def
 v13_l59
 (let
  [s
   (dfn/+
    (dtt/->tensor [1 2 3] {:datatype :float64})
    (dtt/->tensor [10 20 30] {:datatype :float64}))
   materialized
   (dtype/clone s)]
  (some? (dtype/as-array-buffer materialized))))


(deftest t14_l64 (is (true? v13_l59)))
