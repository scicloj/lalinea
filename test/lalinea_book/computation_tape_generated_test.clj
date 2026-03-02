(ns
 lalinea-book.computation-tape-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.complex :as cx]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as tensor]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]])
 (:import [org.ejml.data DMatrixRMaj]))


(def v3_l29 (def A (la/matrix [[1 2] [3 4]])))


(def v4_l31 (tape/memory-status A))


(deftest t5_l33 (is ((fn [s] (= :contiguous s)) v4_l31)))


(def v7_l39 (tape/memory-status (la/transpose A)))
