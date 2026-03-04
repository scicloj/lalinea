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
 v3_l95
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
 t4_l103
 (is
  ((fn
    [{:keys [grad-a grad-b]}]
    (and
     (< (abs (- grad-a 12.0)) 1.0E-10)
     (< (abs (- grad-b 9.0)) 1.0E-10)))
   v3_l95)))


(def v6_l179 (def A (t/matrix [[1 2] [3 4]])))


(def
 v7_l182
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v8_l186 (:result tape-result))


(deftest t9_l188 (is ((fn [v] (== 30.0 v)) v8_l186)))


(def v11_l193 (mapv :op (:entries tape-result)))


(deftest t12_l195 (is (= v11_l193 [:la/transpose :la/mmul :la/trace])))


(def v14_l200 (def grads (grad/grad tape-result (:result tape-result))))


(def v16_l203 grads)


(def v17_l205 (def grad-A (.get grads A)))


(def v18_l207 grad-A)


(def v20_l211 (la/close? grad-A (la/scale A 2)))


(deftest t21_l213 (is (true? v20_l211)))


(def v23_l217 (tape/mermaid tape-result (:result tape-result)))


(def v25_l225 (def A2 (t/matrix [[1 0] [0 2] [1 1]])))


(def v26_l229 (def b (t/column [3 2 4])))


(def v27_l231 (def x (t/column [1 1])))


(def
 v28_l233
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v29_l237 (:result ls-tape))


(deftest t30_l239 (is ((fn [v] (== 8.0 v)) v29_l237)))


(def v31_l242 (def ls-grads (grad/grad ls-tape (:result ls-tape))))


(def v32_l245 ls-grads)


(def v33_l246 (def grad-x (.get ls-grads x)))


(def v34_l248 grad-x)


(deftest
 t35_l250
 (is ((fn [g] (la/close? g (t/column [-8 -4]))) v34_l248)))


(def
 v37_l255
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v38_l260 expected-grad)


(def v39_l262 (la/close? grad-x expected-grad))


(deftest t40_l264 (is (true? v39_l262)))


(def v42_l268 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v44_l276
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v45_l280 (def grads-A (grad/grad ls-tape-A (:result ls-tape-A))))


(def v46_l282 grads-A)


(def v47_l284 (def grad-A2 (.get grads-A A2)))


(def v48_l286 grad-A2)


(deftest
 t49_l288
 (is
  ((fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]]))) v48_l286)))


(def v50_l291 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v51_l293
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v52_l295 expected-grad-A)


(def v53_l297 (la/close? grad-A2 expected-grad-A))


(deftest t54_l299 (is (true? v53_l297)))


(def
 v56_l310
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


(deftest t57_l318 (is (true? v56_l310)))


(def
 v59_l326
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


(deftest t60_l334 (is (true? v59_l326)))


(def
 v62_l340
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


(deftest t63_l347 (is (true? v62_l340)))
