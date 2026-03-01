(ns
 la-linea-book.autodiff-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.grad :as grad]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l39 (def A (la/matrix [[1 2] [3 4]])))


(def
 v4_l42
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v5_l46 (:result tape-result))


(deftest t6_l48 (is ((fn [v] (== 30.0 v)) v5_l46)))


(def v8_l53 (mapv :op (:entries tape-result)))


(deftest t9_l55 (is (= v8_l53 [:la/transpose :la/mmul :la/trace])))


(def v11_l60 (def grads (grad/grad tape-result (:result tape-result))))


(def v12_l62 (def grad-A (.get grads A)))


(def v14_l66 (la/close? grad-A (la/scale A 2)))


(deftest t15_l68 (is (true? v14_l66)))


(def v17_l72 (tape/mermaid tape-result (:result tape-result)))


(def v19_l80 (def A2 (la/matrix [[1 0] [0 2] [1 1]])))


(def v20_l84 (def b (la/column [3 2 4])))


(def v21_l86 (def x (la/column [1 1])))


(def
 v22_l88
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v23_l92 (:result ls-tape))


(deftest t24_l94 (is ((fn [v] (== 8.0 v)) v23_l92)))


(def v25_l97 (def ls-grads (grad/grad ls-tape (:result ls-tape))))


(def v26_l99 (def grad-x (.get ls-grads x)))


(def v27_l101 grad-x)


(deftest
 t28_l103
 (is ((fn [g] (la/close? g (la/column [-8 -4]))) v27_l101)))


(def
 v30_l108
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v31_l113 expected-grad)


(def v32_l115 (la/close? grad-x expected-grad))


(deftest t33_l117 (is (true? v32_l115)))


(def v35_l121 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v37_l129
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v38_l133 (def grads-A (grad/grad ls-tape-A (:result ls-tape-A))))


(def v39_l135 (def grad-A2 (.get grads-A A2)))


(def v40_l137 grad-A2)


(deftest
 t41_l139
 (is
  ((fn [g] (la/close? g (la/matrix [[-4 -4] [0 0] [-4 -4]])))
   v40_l137)))


(def v42_l142 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v43_l144
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v44_l146 expected-grad-A)


(def v45_l148 (la/close? grad-A2 expected-grad-A))


(deftest t46_l150 (is (true? v45_l148)))
