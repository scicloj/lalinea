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
 v3_l98
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
 t4_l108
 (is
  ((fn
    [{:keys [grad-a grad-b]}]
    (and
     (< (abs (- grad-a 12.0)) 1.0E-10)
     (< (abs (- grad-b 9.0)) 1.0E-10)))
   v3_l98)))


(def v6_l184 (def A (t/matrix [[1 2] [3 4]])))


(def
 v7_l187
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v8_l191 (:result tape-result))


(deftest t9_l193 (is ((fn [v] (== 30.0 v)) v8_l191)))


(def v11_l198 (mapv :op (:entries tape-result)))


(deftest t12_l200 (is (= v11_l198 [:la/transpose :la/mmul :la/trace])))


(def
 v14_l205
 (def grad-A (grad/grad tape-result (:result tape-result) A)))


(def v15_l208 grad-A)


(def v17_l212 (la/close? grad-A (la/scale A 2)))


(deftest t18_l214 (is (true? v17_l212)))


(def v20_l218 (tape/mermaid tape-result (:result tape-result)))


(def v22_l226 (def A2 (t/matrix [[1 0] [0 2] [1 1]])))


(def v23_l230 (def b (t/column [3 2 4])))


(def v24_l232 (def x (t/column [1 1])))


(def
 v25_l234
 (def
  ls-tape
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def v26_l238 (:result ls-tape))


(deftest t27_l240 (is ((fn [v] (== 8.0 v)) v26_l238)))


(def v28_l243 (def grad-x (grad/grad ls-tape (:result ls-tape) x)))


(def v29_l246 grad-x)


(deftest
 t30_l248
 (is ((fn [g] (la/close? g (t/column [-8 -4]))) v29_l246)))


(def
 v32_l253
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v33_l258 expected-grad)


(def v34_l260 (la/close? grad-x expected-grad))


(deftest t35_l262 (is (true? v34_l260)))


(def v37_l266 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v39_l274
 (def
  ls-tape-A
  (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A2 x) b))))))


(def
 v40_l278
 (def grad-A2 (grad/grad ls-tape-A (:result ls-tape-A) A2)))


(def v41_l281 grad-A2)


(deftest
 t42_l283
 (is
  ((fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]]))) v41_l281)))


(def v43_l286 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v44_l288
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v45_l291 expected-grad-A)


(def v46_l293 (la/close? grad-A2 expected-grad-A))


(deftest t47_l295 (is (true? v46_l293)))


(def
 v49_l306
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


(deftest t50_l314 (is (true? v49_l306)))


(def
 v52_l322
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


(deftest t53_l331 (is (true? v52_l322)))


(def
 v55_l337
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


(deftest t56_l344 (is (true? v55_l337)))


(def v58_l374 (def A-gd (t/matrix [[1 0] [0 2] [1 1]])))


(def v59_l378 (def b-gd (t/column [3 2 4])))


(def
 v60_l380
 (defn
  ls-step
  "One gradient descent step for ||Ax - b||²."
  [x lr]
  (let
   [tape-result
    (tape/with-tape (la/sum (la/sq (la/sub (la/mmul A-gd x) b-gd))))
    g
    (grad/grad tape-result (:result tape-result) x)]
   (la/sub x (la/scale g lr)))))


(def
 v61_l391
 (def
  x-gd
  (reduce (fn [x _] (ls-step x 0.05)) (t/column [0 0]) (range 200))))


(def v62_l396 x-gd)


(def v64_l400 (def x-exact (:x (la/lstsq A-gd b-gd))))


(def v65_l402 x-exact)


(def v66_l404 (la/close? x-gd x-exact 1.0E-4))


(deftest t67_l406 (is (true? v66_l404)))
