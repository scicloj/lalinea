(ns
 lalinea-book.inner-products-and-orthogonality-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l61 (def a3 (t/column [1 2 3])))


(def v4_l62 (def b3 (t/column [4 5 6])))


(def v5_l64 (la/dot a3 b3))


(deftest t7_l68 (is ((fn [d] (< (abs (- d 32.0)) 1.0E-10)) v5_l64)))


(def v9_l77 (def c3 (t/column [7 8 9])))


(def
 v11_l81
 (la/close-scalar?
  (la/dot (la/add (la/scale a3 2.0) (la/scale b3 3.0)) c3)
  (+ (* 2.0 (la/dot a3 c3)) (* 3.0 (la/dot b3 c3)))))


(deftest t12_l86 (is (true? v11_l81)))


(def v14_l90 (la/close-scalar? (la/dot a3 b3) (la/dot b3 a3)))


(deftest t15_l92 (is (true? v14_l90)))


(def v17_l96 (> (la/dot a3 a3) 0.0))


(deftest t18_l98 (is (true? v17_l96)))


(def
 v19_l100
 (la/close-scalar? (la/dot (t/column [0 0 0]) (t/column [0 0 0])) 0.0))


(deftest t20_l102 (is (true? v19_l100)))


(def v22_l119 (def W-ip (t/matrix [[2 0] [0 1]])))


(def v23_l121 (def u-ip (t/column [1 1])))


(def v25_l125 (la/norm u-ip))


(deftest
 t26_l127
 (is ((fn [d] (la/close-scalar? d (math/sqrt 2.0))) v25_l125)))


(def
 v28_l132
 (math/sqrt ((la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l134
 (is ((fn [d] (la/close-scalar? d (math/sqrt 3.0))) v28_l132)))


(def v31_l156 (la/norm a3))


(deftest
 t32_l158
 (is ((fn [d] (< (abs (- d (math/sqrt 14.0))) 1.0E-10)) v31_l156)))


(def v34_l167 (la/dot (t/column [1 0]) (t/column [0 1])))


(deftest t35_l169 (is ((fn [d] (< (abs d) 1.0E-10)) v34_l167)))


(def v37_l191 (def p (t/column [1 0])))


(def v38_l192 (def q (t/column [1 1])))


(def
 v39_l194
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l198 cos-theta)


(deftest
 t41_l200
 (is
  ((fn [c] (< (abs (- c (/ 1.0 (math/sqrt 2.0)))) 1.0E-10)) v40_l198)))


(def v43_l205 (math/to-degrees (math/acos cos-theta)))


(deftest t44_l207 (is ((fn [d] (< (abs (- d 45.0)) 1.0E-10)) v43_l205)))


(def
 v46_l226
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


(def v48_l236 (def W-proj (t/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l244
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v51_l248 P-proj)


(def v53_l254 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t54_l256 (is (true? v53_l254)))


(def v56_l260 (def point3d (t/column [1 2 3])))


(def v57_l262 (def projected-pt (la/mmul P-proj point3d)))


(def v58_l264 projected-pt)


(def v60_l270 (def resid (la/sub point3d projected-pt)))


(def v61_l272 resid)


(def v62_l274 (la/mmul (la/transpose W-proj) resid))


(deftest t63_l276 (is ((fn [r] (< (la/norm r) 1.0E-10)) v62_l274)))


(def v65_l299 (def a-gs (t/column [1 1 0])))


(def v66_l300 (def b-gs (t/column [1 0 1])))


(def v68_l304 (def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs)))))


(def v69_l306 q1-gs)


(def v71_l310 (def proj-b-on-q1 (la/dot q1-gs b-gs)))


(def
 v72_l313
 (def orthogonal-part (la/sub b-gs (la/scale q1-gs proj-b-on-q1))))


(def
 v74_l318
 (def
  q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part)))))


(def v75_l321 q2-gs)


(def
 v77_l325
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (la/dot q1-gs q2-gs)})


(deftest
 t78_l329
 (is
  ((fn
    [m]
    (and
     (< (abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (abs (:dot m)) 1.0E-10)))
   v77_l325)))


(def
 v80_l339
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


(def v82_l355 (def A-qr (t/matrix [[1 1] [1 0] [0 1]])))


(def v83_l359 (def qr-result (la/qr A-qr)))


(def v85_l363 (def ncols-qr (second (t/shape A-qr))))


(def
 v86_l364
 (def Q-thin (t/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v87_l365
 (def R-thin (t/submatrix (:R qr-result) (range ncols-qr) :all)))


(def v89_l369 Q-thin)


(def v91_l373 R-thin)


(def
 v93_l377
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (t/eye 2))))


(deftest t94_l379 (is ((fn [d] (< d 1.0E-10)) v93_l377)))


(def v96_l384 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t97_l386 (is ((fn [d] (< d 1.0E-10)) v96_l384)))
