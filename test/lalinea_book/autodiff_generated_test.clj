(ns
 lalinea-book.autodiff-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.grad :as grad]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l99
 (let
  [a
   (t/matrix [3.0])
   b
   (t/matrix [2.0])
   tape-result
   (tape/with-tape (el/sum (el/mul (el/sq a) b)))
   grads
   (grad/grad tape-result (:result tape-result) [a b])]
  {:grad-a ((grads a) 0), :grad-b ((grads b) 0)}))


(deftest
 t4_l109
 (is
  ((fn
    [{:keys [grad-a grad-b]}]
    (and
     (< (abs (- grad-a 12.0)) 1.0E-10)
     (< (abs (- grad-b 9.0)) 1.0E-10)))
   v3_l99)))


(def v6_l185 (def A (t/matrix [[1 2] [3 4]])))


(def
 v7_l188
 (def
  tape-result
  (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))))


(def v8_l192 (:result tape-result))


(deftest t9_l194 (is ((fn [v] (== 30.0 v)) v8_l192)))


(def v11_l199 (mapv :op (:entries tape-result)))


(deftest t12_l201 (is (= v11_l199 [:la/transpose :la/mmul :la/trace])))


(def
 v14_l206
 (def grad-A (grad/grad tape-result (:result tape-result) A)))


(def v15_l209 grad-A)


(def v17_l213 (la/close? grad-A (la/scale A 2)))


(deftest t18_l215 (is (true? v17_l213)))


(def v20_l219 (tape/mermaid tape-result (:result tape-result)))


(def v22_l227 (def A2 (t/matrix [[1 0] [0 2] [1 1]])))


(def v23_l231 (def b (t/column [3 2 4])))


(def v24_l233 (def x (t/column [1 1])))


(def
 v25_l235
 (def
  ls-tape
  (tape/with-tape (el/sum (el/sq (la/sub (la/mmul A2 x) b))))))


(def v26_l239 (:result ls-tape))


(deftest t27_l241 (is ((fn [v] (== 8.0 v)) v26_l239)))


(def v28_l244 (def grad-x (grad/grad ls-tape (:result ls-tape) x)))


(def v29_l247 grad-x)


(deftest
 t30_l249
 (is ((fn [g] (la/close? g (t/column [-8 -4]))) v29_l247)))


(def
 v32_l254
 (def
  expected-grad
  (la/scale (la/mmul (la/transpose A2) (la/sub (la/mmul A2 x) b)) 2)))


(def v33_l259 expected-grad)


(def v34_l261 (la/close? grad-x expected-grad))


(deftest t35_l263 (is (true? v34_l261)))


(def v37_l267 (tape/mermaid ls-tape (:result ls-tape)))


(def
 v39_l275
 (def
  ls-tape-A
  (tape/with-tape (el/sum (el/sq (la/sub (la/mmul A2 x) b))))))


(def
 v40_l279
 (def grad-A2 (grad/grad ls-tape-A (:result ls-tape-A) A2)))


(def v41_l282 grad-A2)


(deftest
 t42_l284
 (is
  ((fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]]))) v41_l282)))


(def v43_l287 (def residual (la/sub (la/mmul A2 x) b)))


(def
 v44_l289
 (def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2)))


(def v45_l292 expected-grad-A)


(def v46_l294 (la/close? grad-A2 expected-grad-A))


(deftest t47_l296 (is (true? v46_l294)))


(def
 v49_l307
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


(deftest t50_l315 (is (true? v49_l307)))


(def
 v52_l323
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


(deftest t53_l332 (is (true? v52_l323)))


(def
 v55_l338
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


(deftest t56_l345 (is (true? v55_l338)))


(def v58_l375 (def A-gd (t/matrix [[1 0] [0 2] [1 1]])))


(def v59_l379 (def b-gd (t/column [3 2 4])))


(def
 v60_l381
 (defn
  ls-step
  "One gradient descent step for ||Ax - b||²."
  [x lr]
  (let
   [tape-result
    (tape/with-tape (el/sum (el/sq (la/sub (la/mmul A-gd x) b-gd))))
    g
    (grad/grad tape-result (:result tape-result) x)]
   (la/sub x (la/scale g lr)))))


(def
 v61_l392
 (def
  x-gd
  (reduce (fn [x _] (ls-step x 0.05)) (t/column [0 0]) (range 200))))


(def v62_l397 x-gd)


(def v64_l401 (def x-exact (:x (la/lstsq A-gd b-gd))))


(def v65_l403 x-exact)


(def v66_l405 (la/close? x-gd x-exact 1.0E-4))


(deftest t67_l407 (is (true? v66_l405)))
