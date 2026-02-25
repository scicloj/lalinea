(ns
 basis-book.inner-products-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.datatype :as dtype]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l62 (def a3 (la/column [1 2 3])))


(def v4_l63 (def b3 (la/column [4 5 6])))


(def v5_l65 (la/dot a3 b3))


(deftest
 t7_l69
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v5_l65)))


(def v9_l78 (def c3 (la/column [7 8 9])))


(def
 v11_l82
 (la/close-scalar?
  (la/dot (la/add (la/scale 2.0 a3) (la/scale 3.0 b3)) c3)
  (+ (* 2.0 (la/dot a3 c3)) (* 3.0 (la/dot b3 c3)))))


(deftest t12_l87 (is (true? v11_l82)))


(def v14_l91 (la/close-scalar? (la/dot a3 b3) (la/dot b3 a3)))


(deftest t15_l93 (is (true? v14_l91)))


(def v17_l97 (> (la/dot a3 a3) 0.0))


(deftest t18_l99 (is (true? v17_l97)))


(def
 v19_l101
 (la/close-scalar?
  (la/dot (la/column [0 0 0]) (la/column [0 0 0]))
  0.0))


(deftest t20_l103 (is (true? v19_l101)))


(def v22_l116 (def W-ip (la/matrix [[2 0] [0 1]])))


(def v23_l118 (def u-ip (la/column [1 1])))


(def v25_l122 (la/norm u-ip))


(deftest
 t26_l124
 (is ((fn [d] (la/close-scalar? d (Math/sqrt 2.0))) v25_l122)))


(def
 v28_l129
 (Math/sqrt
  (tensor/mget (la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l131
 (is ((fn [d] (la/close-scalar? d (Math/sqrt 3.0))) v28_l129)))


(def v31_l153 (la/norm a3))


(deftest
 t32_l155
 (is ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v31_l153)))


(def v34_l164 (la/dot (la/column [1 0]) (la/column [0 1])))


(deftest t35_l166 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v34_l164)))


(def v37_l188 (def p (la/column [1 0])))


(def v38_l189 (def q (la/column [1 1])))


(def
 v39_l191
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l195 cos-theta)


(deftest
 t41_l197
 (is
  ((fn [c] (< (Math/abs (- c (/ 1.0 (Math/sqrt 2.0)))) 1.0E-10))
   v40_l195)))


(def v43_l202 (Math/toDegrees (Math/acos cos-theta)))


(deftest
 t44_l204
 (is ((fn [d] (< (Math/abs (- d 45.0)) 1.0E-10)) v43_l202)))


(def
 v46_l223
 (vis/arrow-plot
  [{:label "a", :xy [2 1], :color "#999999"}
   {:label "b", :xy [1 3], :color "#2266cc"}
   {:label "proj", :xy [2 1], :color "#228833"}
   {:label "resid",
    :xy [-1 2],
    :color "#cc4422",
    :from [2 1],
    :dashed? true}]
  {}))


(def v48_l233 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l241
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v52_l249 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t53_l251 (is (true? v52_l249)))


(def v55_l255 (def point3d (la/column [1 2 3])))


(def v56_l257 (def projected-pt (la/mmul P-proj point3d)))


(def v57_l259 projected-pt)


(def v59_l265 (def resid (la/sub point3d projected-pt)))


(def v60_l267 (la/mmul (la/transpose W-proj) resid))


(deftest t61_l269 (is ((fn [r] (< (la/norm r) 1.0E-10)) v60_l267)))


(def v63_l292 (def a-gs (la/column [1 1 0])))


(def v64_l293 (def b-gs (la/column [1 0 1])))


(def v66_l297 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v67_l299 q1-gs)


(def v69_l303 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v70_l306
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v72_l311
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v73_l314 q2-gs)


(def
 v75_l318
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t76_l322
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v75_l318)))


(def v78_l338 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v79_l342 (def qr-result (la/qr A-qr)))


(def v81_l346 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v82_l347
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v83_l348
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v85_l352
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t86_l354 (is ((fn [d] (< d 1.0E-10)) v85_l352)))


(def v88_l359 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t89_l361 (is ((fn [d] (< d 1.0E-10)) v88_l359)))
