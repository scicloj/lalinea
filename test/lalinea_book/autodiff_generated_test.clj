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
 v3_l97
 (let
  [a
   (t/matrix [3.0])
   b
   (t/matrix [2.0])
   tape-result
   (tape/with-tape (la/sum (la/mul (la/sq a) b)))
   grads
   (grad/grad tape-result (:result tape-result) [a b])]
  {:grad-a ((grads a) 0), :grad-b ((grads b) 0)}))


(deftest
 t4_l107
 (is
  ((fn
    [{:keys [grad-a grad-b]}]
    (and
     (< (abs (- grad-a 12.0)) 1.0E-10)
     (< (abs (- grad-b 9.0)) 1.0E-10)))
   v3_l97)))


(def v6_l183 (def A (t/matrix [[1 2] [3 4]])))


(def
 v7_l186
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v8_l190 (:result tape-result))


(deftest t9_l192 (is ((fn [v] (== 30.0 v)) v8_l190)))


(def v11_l197 (mapv :op (:entries tape-result)))


(deftest t12_l199 (is (= v11_l197 [:la/transpose :la/mmul :la/trace])))


(def
 v14_l204
 (def grad-A (grad/grad tape-result (:result tape-result) A)))


(def v15_l207 grad-A)


(def v17_l211 (la/close? grad-A (la/scale A 2)))


(deftest t18_l213 (is (true? v17_l211)))


(def v20_l217 (tape/mermaid tape-result (:result tape-result)))


(def v22_l225 (def A2 (t/matrix [[1 0] [0 2] [1 1]])))


(def v23_l229 (def b (t/column [3 2 4])))


(def v24_l231 (def x (t/column [1 1])))


(def
 v25_l233
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v26_l237 (:result ls-tape))


(deftest t27_l239 (is ((fn [v] (== 8.0 v)) v26_l237)))


(def v28_l242 (def grad-x (grad/grad ls-tape (:result ls-tape) x)))


(def v29_l245 grad-x)


(deftest
 t30_l247
 (is ((fn [g] (la/close? g (t/column [-8 -4]))) v29_l245)))


(def
 v32_l252
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v33_l257 expected-grad)


(def v34_l259 (la/close? grad-x expected-grad))


(deftest t35_l261 (is (true? v34_l259)))


(def v37_l265 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v39_l273
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def
 v40_l277
 (def grad-A2 (grad/grad ls-tape-A (:result ls-tape-A) A2)))


(def v41_l280 grad-A2)


(deftest
 t42_l282
 (is
  ((fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]]))) v41_l280)))


(def v43_l285 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v44_l287
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v45_l290 expected-grad-A)


(def v46_l292 (la/close? grad-A2 expected-grad-A))


(deftest t47_l294 (is (true? v46_l292)))


(def
 v49_l305
 (let
  [A
   (t/matrix [[2 1] [1 3]])
   tape-result
   (tape/with-tape (la/det A))
   grad-A
   (grad/grad tape-result (:result tape-result) A)
   expected
   (la/scale (la/transpose (la/invert A)) (la/det A))]
  (la/close? grad-A expected)))


(deftest t50_l313 (is (true? v49_l305)))


(def
 v52_l321
 (let
  [A
   (t/matrix [[2 1] [1 3]])
   tape-result
   (tape/with-tape (la/trace (la/invert A)))
   grad-A
   (grad/grad tape-result (:result tape-result) A)
   inv-t
   (la/transpose (la/invert A))
   expected
   (la/scale (la/mmul inv-t inv-t) -1.0)]
  (la/close? grad-A expected)))


(deftest t53_l330 (is (true? v52_l321)))


(def
 v55_l336
 (let
  [A
   (t/matrix [[3 0] [0 4]])
   tape-result
   (tape/with-tape (la/norm A))
   grad-A
   (grad/grad tape-result (:result tape-result) A)
   expected
   (la/scale A (/ 1.0 (la/norm A)))]
  (la/close? grad-A expected)))


(deftest t56_l343 (is (true? v55_l336)))
