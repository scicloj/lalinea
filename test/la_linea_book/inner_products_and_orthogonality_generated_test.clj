(ns
 la-linea-book.inner-products-and-orthogonality-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.la-linea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l60 (def a3 (la/column [1 2 3])))


(def v4_l61 (def b3 (la/column [4 5 6])))


(def v5_l63 (la/dot a3 b3))


(deftest
 t7_l67
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v5_l63)))


(def v9_l76 (def c3 (la/column [7 8 9])))


(def
 v11_l80
 (la/close-scalar?
  (la/dot (la/add (la/scale a3 2.0) (la/scale b3 3.0)) c3)
  (+ (* 2.0 (la/dot a3 c3)) (* 3.0 (la/dot b3 c3)))))


(deftest t12_l85 (is (true? v11_l80)))


(def v14_l89 (la/close-scalar? (la/dot a3 b3) (la/dot b3 a3)))


(deftest t15_l91 (is (true? v14_l89)))


(def v17_l95 (> (la/dot a3 a3) 0.0))


(deftest t18_l97 (is (true? v17_l95)))


(def
 v19_l99
 (la/close-scalar?
  (la/dot (la/column [0 0 0]) (la/column [0 0 0]))
  0.0))


(deftest t20_l101 (is (true? v19_l99)))


(def v22_l114 (def W-ip (la/matrix [[2 0] [0 1]])))


(def v23_l116 (def u-ip (la/column [1 1])))


(def v25_l120 (la/norm u-ip))


(deftest
 t26_l122
 (is ((fn [d] (la/close-scalar? d (Math/sqrt 2.0))) v25_l120)))


(def
 v28_l127
 (Math/sqrt
  (tensor/mget (la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l129
 (is ((fn [d] (la/close-scalar? d (Math/sqrt 3.0))) v28_l127)))


(def v31_l151 (la/norm a3))


(deftest
 t32_l153
 (is ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v31_l151)))


(def v34_l162 (la/dot (la/column [1 0]) (la/column [0 1])))


(deftest t35_l164 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v34_l162)))


(def v37_l186 (def p (la/column [1 0])))


(def v38_l187 (def q (la/column [1 1])))


(def
 v39_l189
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l193 cos-theta)


(deftest
 t41_l195
 (is
  ((fn [c] (< (Math/abs (- c (/ 1.0 (Math/sqrt 2.0)))) 1.0E-10))
   v40_l193)))


(def v43_l200 (Math/toDegrees (Math/acos cos-theta)))


(deftest
 t44_l202
 (is ((fn [d] (< (Math/abs (- d 45.0)) 1.0E-10)) v43_l200)))


(def
 v46_l221
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


(def v48_l231 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l239
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v51_l243 P-proj)


(def v53_l249 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t54_l251 (is (true? v53_l249)))


(def v56_l255 (def point3d (la/column [1 2 3])))


(def v57_l257 (def projected-pt (la/mmul P-proj point3d)))


(def v58_l259 projected-pt)


(def v60_l265 (def resid (la/sub point3d projected-pt)))


(def v61_l267 resid)


(def v62_l269 (la/mmul (la/transpose W-proj) resid))


(deftest t63_l271 (is ((fn [r] (< (la/norm r) 1.0E-10)) v62_l269)))


(def v65_l294 (def a-gs (la/column [1 1 0])))


(def v66_l295 (def b-gs (la/column [1 0 1])))


(def v68_l299 (def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs)))))


(def v69_l301 q1-gs)


(def v71_l305 (def proj-b-on-q1 (la/dot q1-gs b-gs)))


(def
 v72_l308
 (def orthogonal-part (la/sub b-gs (la/scale q1-gs proj-b-on-q1))))


(def
 v74_l313
 (def
  q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part)))))


(def v75_l316 q2-gs)


(def
 v77_l320
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (la/dot q1-gs q2-gs)})


(deftest
 t78_l324
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v77_l320)))


(def
 v80_l334
 (vis/arrow-plot
  [{:label "a", :xy [1 1], :color "#999999"}
   {:label "b", :xy [1 0], :color "#2266cc"}
   {:label "q1",
    :xy [(/ 1 (Math/sqrt 2)) (/ 1 (Math/sqrt 2))],
    :color "#228833"}
   {:label "q2",
    :xy [(/ 1 (Math/sqrt 6)) (/ -1 (Math/sqrt 6))],
    :color "#cc4422"}]
  {}))


(def v82_l350 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v83_l354 (def qr-result (la/qr A-qr)))


(def v85_l358 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v86_l359
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v87_l360
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def v89_l364 Q-thin)


(def v91_l368 R-thin)


(def
 v93_l372
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t94_l374 (is ((fn [d] (< d 1.0E-10)) v93_l372)))


(def v96_l379 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t97_l381 (is ((fn [d] (< d 1.0E-10)) v96_l379)))
