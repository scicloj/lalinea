(ns
 lalinea-book.inner-products-and-orthogonality-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l61 (def a3 (la/column [1 2 3])))


(def v4_l62 (def b3 (la/column [4 5 6])))


(def v5_l64 (la/dot a3 b3))


(deftest t7_l68 (is ((fn [d] (< (abs (- d 32.0)) 1.0E-10)) v5_l64)))


(def v9_l77 (def c3 (la/column [7 8 9])))


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
 (la/close-scalar?
  (la/dot (la/column [0 0 0]) (la/column [0 0 0]))
  0.0))


(deftest t20_l102 (is (true? v19_l100)))


(def v22_l115 (def W-ip (la/matrix [[2 0] [0 1]])))


(def v23_l117 (def u-ip (la/column [1 1])))


(def v25_l121 (la/norm u-ip))


(deftest
 t26_l123
 (is ((fn [d] (la/close-scalar? d (math/sqrt 2.0))) v25_l121)))


(def
 v28_l128
 (math/sqrt ((la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l130
 (is ((fn [d] (la/close-scalar? d (math/sqrt 3.0))) v28_l128)))


(def v31_l152 (la/norm a3))


(deftest
 t32_l154
 (is ((fn [d] (< (abs (- d (math/sqrt 14.0))) 1.0E-10)) v31_l152)))


(def v34_l163 (la/dot (la/column [1 0]) (la/column [0 1])))


(deftest t35_l165 (is ((fn [d] (< (abs d) 1.0E-10)) v34_l163)))


(def v37_l187 (def p (la/column [1 0])))


(def v38_l188 (def q (la/column [1 1])))


(def
 v39_l190
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l194 cos-theta)


(deftest
 t41_l196
 (is
  ((fn [c] (< (abs (- c (/ 1.0 (math/sqrt 2.0)))) 1.0E-10)) v40_l194)))


(def v43_l201 (math/to-degrees (math/acos cos-theta)))


(deftest t44_l203 (is ((fn [d] (< (abs (- d 45.0)) 1.0E-10)) v43_l201)))


(def
 v46_l222
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


(def v48_l232 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l240
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v51_l244 P-proj)


(def v53_l250 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t54_l252 (is (true? v53_l250)))


(def v56_l256 (def point3d (la/column [1 2 3])))


(def v57_l258 (def projected-pt (la/mmul P-proj point3d)))


(def v58_l260 projected-pt)


(def v60_l266 (def resid (la/sub point3d projected-pt)))


(def v61_l268 resid)


(def v62_l270 (la/mmul (la/transpose W-proj) resid))


(deftest t63_l272 (is ((fn [r] (< (la/norm r) 1.0E-10)) v62_l270)))


(def v65_l295 (def a-gs (la/column [1 1 0])))


(def v66_l296 (def b-gs (la/column [1 0 1])))


(def v68_l300 (def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs)))))


(def v69_l302 q1-gs)


(def v71_l306 (def proj-b-on-q1 (la/dot q1-gs b-gs)))


(def
 v72_l309
 (def orthogonal-part (la/sub b-gs (la/scale q1-gs proj-b-on-q1))))


(def
 v74_l314
 (def
  q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part)))))


(def v75_l317 q2-gs)


(def
 v77_l321
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (la/dot q1-gs q2-gs)})


(deftest
 t78_l325
 (is
  ((fn
    [m]
    (and
     (< (abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (abs (:dot m)) 1.0E-10)))
   v77_l321)))


(def
 v80_l335
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


(def v82_l351 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v83_l355 (def qr-result (la/qr A-qr)))


(def v85_l359 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v86_l360
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v87_l361
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def v89_l365 Q-thin)


(def v91_l369 R-thin)


(def
 v93_l373
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t94_l375 (is ((fn [d] (< d 1.0E-10)) v93_l373)))


(def v96_l380 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t97_l382 (is ((fn [d] (< d 1.0E-10)) v96_l380)))
