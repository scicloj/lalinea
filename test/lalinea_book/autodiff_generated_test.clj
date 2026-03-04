(ns
 lalinea-book.autodiff-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.grad :as grad]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l87
 (let
  [a
   (t/matrix [3.0])
   b
   (t/matrix [2.0])
   tape-result
   (tape/with-tape (la/sum (la/mul (la/sq a) b)))
   grads
   (grad/grad tape-result (:result tape-result))]
  {:grad-a ((.get grads a) 0), :grad-b ((.get grads b) 0)}))


(deftest
 t4_l95
 (is
  ((fn
    [{:keys [grad-a grad-b]}]
    (and
     (< (abs (- grad-a 12.0)) 1.0E-10)
     (< (abs (- grad-b 9.0)) 1.0E-10)))
   v3_l87)))


(def v6_l171 (def A (t/matrix [[1 2] [3 4]])))


(def
 v7_l174
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v8_l178 (:result tape-result))


(deftest t9_l180 (is ((fn [v] (== 30.0 v)) v8_l178)))


(def v11_l185 (mapv :op (:entries tape-result)))


(deftest t12_l187 (is (= v11_l185 [:la/transpose :la/mmul :la/trace])))


(def v14_l192 (def grads (grad/grad tape-result (:result tape-result))))


(def v15_l194 (def grad-A (.get grads A)))


(def v17_l198 (la/close? grad-A (la/scale A 2)))


(deftest t18_l200 (is (true? v17_l198)))


(def v20_l204 (tape/mermaid tape-result (:result tape-result)))


(def v22_l212 (def A2 (t/matrix [[1 0] [0 2] [1 1]])))


(def v23_l216 (def b (t/column [3 2 4])))


(def v24_l218 (def x (t/column [1 1])))


(def
 v25_l220
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v26_l224 (:result ls-tape))


(deftest t27_l226 (is ((fn [v] (== 8.0 v)) v26_l224)))


(def v28_l229 (def ls-grads (grad/grad ls-tape (:result ls-tape))))


(def v29_l231 (def grad-x (.get ls-grads x)))


(def v30_l233 grad-x)


(deftest
 t31_l235
 (is ((fn [g] (la/close? g (t/column [-8 -4]))) v30_l233)))


(def
 v33_l240
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v34_l245 expected-grad)


(def v35_l247 (la/close? grad-x expected-grad))


(deftest t36_l249 (is (true? v35_l247)))


(def v38_l253 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v40_l261
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v41_l265 (def grads-A (grad/grad ls-tape-A (:result ls-tape-A))))


(def v42_l267 (def grad-A2 (.get grads-A A2)))


(def v43_l269 grad-A2)


(deftest
 t44_l271
 (is
  ((fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]]))) v43_l269)))


(def v45_l274 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v46_l276
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v47_l278 expected-grad-A)


(def v48_l280 (la/close? grad-A2 expected-grad-A))


(deftest t49_l282 (is (true? v48_l280)))


(def
 v51_l293
 (let
  [A
   (t/matrix [[2 1] [1 3]])
   tape-result
   (tape/with-tape (la/det A))
   grads
   (grad/grad tape-result (:result tape-result))
   grad-A
   (.get grads A)
   expected
   (la/scale (la/transpose (la/invert A)) (la/det A))]
  (la/close? grad-A expected)))


(deftest t52_l301 (is (true? v51_l293)))


(def
 v54_l309
 (let
  [A
   (t/matrix [[2 1] [1 3]])
   tape-result
   (tape/with-tape (la/trace (la/invert A)))
   grads
   (grad/grad tape-result (:result tape-result))
   grad-A
   (.get grads A)
   inv-t
   (la/transpose (la/invert A))
   expected
   (la/scale (la/mmul inv-t inv-t) -1.0)]
  (la/close? grad-A expected)))


(deftest t55_l317 (is (true? v54_l309)))


(def
 v57_l323
 (let
  [A
   (t/matrix [[3 0] [0 4]])
   tape-result
   (tape/with-tape (la/norm A))
   grads
   (grad/grad tape-result (:result tape-result))
   grad-A
   (.get grads A)
   expected
   (la/scale A (/ 1.0 (la/norm A)))]
  (la/close? grad-A expected)))


(deftest t58_l330 (is (true? v57_l323)))
