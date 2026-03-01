(ns
 la-linea-book.autodiff-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.grad :as grad]
  [tech.v3.datatype :as dtype]
  [tech.v3.tensor :as tensor]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l41 (def A (la/matrix [[1 2] [3 4]])))


(def
 v4_l44
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v5_l48 (:result tape-result))


(deftest t6_l50 (is ((fn [v] (== 30.0 v)) v5_l48)))


(def v8_l55 (mapv :op (:entries tape-result)))


(deftest t9_l57 (is (= v8_l55 [:la/transpose :la/mmul :la/trace])))


(def v11_l62 (def grads (grad/grad tape-result (:result tape-result))))


(def v12_l64 (def grad-A (.get grads A)))


(def v14_l68 (la/close? grad-A (la/scale A 2)))


(deftest t15_l70 (is (true? v14_l68)))


(def v17_l74 (tape/mermaid tape-result (:result tape-result)))


(def v19_l82 (def A2 (la/matrix [[1 0] [0 2] [1 1]])))


(def v20_l86 (def b (la/column [3 2 4])))


(def v21_l88 (def x (la/column [1 1])))


(def
 v22_l90
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v23_l94 (:result ls-tape))


(deftest t24_l96 (is ((fn [v] (== 8.0 v)) v23_l94)))


(def v25_l99 (def ls-grads (grad/grad ls-tape (:result ls-tape))))


(def v26_l101 (def grad-x (.get ls-grads x)))


(def v27_l103 grad-x)


(def
 v29_l107
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v30_l112 expected-grad)


(def v31_l114 (la/close? grad-x expected-grad))


(deftest t32_l116 (is (true? v31_l114)))


(def v34_l120 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v36_l128
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v37_l132 (def grads-A (grad/grad ls-tape-A (:result ls-tape-A))))


(def v38_l134 (def grad-A2 (.get grads-A A2)))


(def v39_l136 grad-A2)


(def v40_l138 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v41_l140
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v42_l142 expected-grad-A)


(def v43_l144 (la/close? grad-A2 expected-grad-A))


(deftest t44_l146 (is (true? v43_l144)))
