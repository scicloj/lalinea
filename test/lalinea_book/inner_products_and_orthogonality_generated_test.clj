(ns
 lalinea-book.inner-products-and-orthogonality-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l58 (def a3 (t/column [1 2 3])))


(def v4_l59 (def b3 (t/column [4 5 6])))


(def v5_l61 (la/dot a3 b3))


(deftest t7_l65 (is ((fn [d] (< (abs (- d 32.0)) 1.0E-10)) v5_l61)))


(def v9_l74 (def c3 (t/column [7 8 9])))


(def
 v11_l78
 (la/close-scalar?
  (la/dot (la/add (la/scale a3 2.0) (la/scale b3 3.0)) c3)
  (+ (* 2.0 (la/dot a3 c3)) (* 3.0 (la/dot b3 c3)))))


(deftest t12_l83 (is (true? v11_l78)))


(def v14_l87 (la/close-scalar? (la/dot a3 b3) (la/dot b3 a3)))


(deftest t15_l89 (is (true? v14_l87)))


(def v17_l93 (> (la/dot a3 a3) 0.0))


(deftest t18_l95 (is (true? v17_l93)))


(def
 v19_l97
 (la/close-scalar? (la/dot (t/column [0 0 0]) (t/column [0 0 0])) 0.0))


(deftest t20_l99 (is (true? v19_l97)))


(def v22_l112 (def W-ip (t/matrix [[2 0] [0 1]])))


(def v23_l114 (def u-ip (t/column [1 1])))


(def v25_l118 (la/norm u-ip))


(deftest
 t26_l120
 (is ((fn [d] (la/close-scalar? d (math/sqrt 2.0))) v25_l118)))


(def
 v28_l125
 (math/sqrt ((la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l127
 (is ((fn [d] (la/close-scalar? d (math/sqrt 3.0))) v28_l125)))


(def v31_l149 (la/norm a3))


(deftest
 t32_l151
 (is ((fn [d] (< (abs (- d (math/sqrt 14.0))) 1.0E-10)) v31_l149)))


(def v34_l160 (la/dot (t/column [1 0]) (t/column [0 1])))


(deftest t35_l162 (is ((fn [d] (< (abs d) 1.0E-10)) v34_l160)))


(def v37_l184 (def p (t/column [1 0])))


(def v38_l185 (def q (t/column [1 1])))


(def
 v39_l187
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l191 cos-theta)


(deftest
 t41_l193
 (is
  ((fn [c] (< (abs (- c (/ 1.0 (math/sqrt 2.0)))) 1.0E-10)) v40_l191)))


(def v43_l198 (math/to-degrees (math/acos cos-theta)))


(deftest t44_l200 (is ((fn [d] (< (abs (- d 45.0)) 1.0E-10)) v43_l198)))


(def
 v46_l219
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


(def v48_l229 (def W-proj (t/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l237
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v51_l241 P-proj)


(def v53_l247 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t54_l249 (is (true? v53_l247)))


(def v56_l253 (def point3d (t/column [1 2 3])))


(def v57_l255 (def projected-pt (la/mmul P-proj point3d)))


(def v58_l257 projected-pt)


(def v60_l263 (def resid (la/sub point3d projected-pt)))


(def v61_l265 resid)


(def v62_l267 (la/mmul (la/transpose W-proj) resid))


(deftest t63_l269 (is ((fn [r] (< (la/norm r) 1.0E-10)) v62_l267)))


(def v65_l292 (def a-gs (t/column [1 1 0])))


(def v66_l293 (def b-gs (t/column [1 0 1])))


(def v68_l297 (def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs)))))


(def v69_l299 q1-gs)


(def v71_l303 (def proj-b-on-q1 (la/dot q1-gs b-gs)))


(def
 v72_l306
 (def orthogonal-part (la/sub b-gs (la/scale q1-gs proj-b-on-q1))))


(def
 v74_l311
 (def
  q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part)))))


(def v75_l314 q2-gs)


(def
 v77_l318
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (la/dot q1-gs q2-gs)})


(deftest
 t78_l322
 (is
  ((fn
    [m]
    (and
     (< (abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (abs (:dot m)) 1.0E-10)))
   v77_l318)))


(def
 v80_l332
 (vis/arrow-plot
  [{:label "a", :xy [1 1], :color "#999999"}
   {:label "b", :xy [1 0], :color "#2266cc"}
   {:label "q1",
    :xy [(/ 1 (math/sqrt 2)) (/ 1 (math/sqrt 2))],
    :color "#228833"}
   {:label "q2",
    :xy [(/ 1 (math/sqrt 6)) (/ -1 (math/sqrt 6))],
    :color "#cc4422"}]
  {}))


(def v82_l348 (def A-qr (t/matrix [[1 1] [1 0] [0 1]])))


(def v83_l352 (def qr-result (la/qr A-qr)))


(def v85_l356 (def ncols-qr (second (t/shape A-qr))))


(def
 v86_l357
 (def Q-thin (t/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v87_l358
 (def R-thin (t/submatrix (:R qr-result) (range ncols-qr) :all)))


(def v89_l362 Q-thin)


(def v91_l366 R-thin)


(def
 v93_l370
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (t/eye 2))))


(deftest t94_l372 (is ((fn [d] (< d 1.0E-10)) v93_l370)))


(def v96_l377 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t97_l379 (is ((fn [d] (< d 1.0E-10)) v96_l377)))
