(ns
 lalinea-book.inner-products-and-orthogonality-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l57 (def a3 (t/column [1 2 3])))


(def v4_l58 (def b3 (t/column [4 5 6])))


(def v5_l60 (la/dot a3 b3))


(deftest t7_l64 (is ((fn [d] (< (abs (- d 32.0)) 1.0E-10)) v5_l60)))


(def v9_l73 (def c3 (t/column [7 8 9])))


(def
 v11_l77
 (la/close-scalar?
  (la/dot (la/add (la/scale a3 2.0) (la/scale b3 3.0)) c3)
  (+ (* 2.0 (la/dot a3 c3)) (* 3.0 (la/dot b3 c3)))))


(deftest t12_l82 (is (true? v11_l77)))


(def v14_l86 (la/close-scalar? (la/dot a3 b3) (la/dot b3 a3)))


(deftest t15_l88 (is (true? v14_l86)))


(def v17_l92 (> (la/dot a3 a3) 0.0))


(deftest t18_l94 (is (true? v17_l92)))


(def
 v19_l96
 (la/close-scalar? (la/dot (t/column [0 0 0]) (t/column [0 0 0])) 0.0))


(deftest t20_l98 (is (true? v19_l96)))


(def v22_l111 (def W-ip (t/matrix [[2 0] [0 1]])))


(def v23_l113 (def u-ip (t/column [1 1])))


(def v25_l117 (la/norm u-ip))


(deftest
 t26_l119
 (is ((fn [d] (la/close-scalar? d (math/sqrt 2.0))) v25_l117)))


(def
 v28_l124
 (math/sqrt ((la/mmul (la/transpose u-ip) (la/mmul W-ip u-ip)) 0 0)))


(deftest
 t29_l126
 (is ((fn [d] (la/close-scalar? d (math/sqrt 3.0))) v28_l124)))


(def v31_l148 (la/norm a3))


(deftest
 t32_l150
 (is ((fn [d] (< (abs (- d (math/sqrt 14.0))) 1.0E-10)) v31_l148)))


(def v34_l159 (la/dot (t/column [1 0]) (t/column [0 1])))


(deftest t35_l161 (is ((fn [d] (< (abs d) 1.0E-10)) v34_l159)))


(def v37_l183 (def p (t/column [1 0])))


(def v38_l184 (def q (t/column [1 1])))


(def
 v39_l186
 (def cos-theta (/ (la/dot p q) (* (la/norm p) (la/norm q)))))


(def v40_l190 cos-theta)


(deftest
 t41_l192
 (is
  ((fn [c] (< (abs (- c (/ 1.0 (math/sqrt 2.0)))) 1.0E-10)) v40_l190)))


(def v43_l197 (math/to-degrees (math/acos cos-theta)))


(deftest t44_l199 (is ((fn [d] (< (abs (- d 45.0)) 1.0E-10)) v43_l197)))


(def
 v46_l218
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


(def v48_l228 (def W-proj (t/matrix [[1 0] [0 1] [1 1]])))


(def
 v50_l236
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v51_l240 P-proj)


(def v53_l246 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t54_l248 (is (true? v53_l246)))


(def v56_l252 (def point3d (t/column [1 2 3])))


(def v57_l254 (def projected-pt (la/mmul P-proj point3d)))


(def v58_l256 projected-pt)


(def v60_l262 (def resid (la/sub point3d projected-pt)))


(def v61_l264 resid)


(def v62_l266 (la/mmul (la/transpose W-proj) resid))


(deftest t63_l268 (is ((fn [r] (< (la/norm r) 1.0E-10)) v62_l266)))


(def v65_l291 (def a-gs (t/column [1 1 0])))


(def v66_l292 (def b-gs (t/column [1 0 1])))


(def v68_l296 (def q1-gs (la/scale a-gs (/ 1.0 (la/norm a-gs)))))


(def v69_l298 q1-gs)


(def v71_l302 (def proj-b-on-q1 (la/dot q1-gs b-gs)))


(def
 v72_l305
 (def orthogonal-part (la/sub b-gs (la/scale q1-gs proj-b-on-q1))))


(def
 v74_l310
 (def
  q2-gs
  (la/scale orthogonal-part (/ 1.0 (la/norm orthogonal-part)))))


(def v75_l313 q2-gs)


(def
 v77_l317
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (la/dot q1-gs q2-gs)})


(deftest
 t78_l321
 (is
  ((fn
    [m]
    (and
     (< (abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (abs (:dot m)) 1.0E-10)))
   v77_l317)))


(def
 v80_l331
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


(def v82_l347 (def A-qr (t/matrix [[1 1] [1 0] [0 1]])))


(def v83_l351 (def qr-result (la/qr A-qr)))


(def v85_l355 (def ncols-qr (second (t/shape A-qr))))


(def
 v86_l356
 (def Q-thin (t/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v87_l357
 (def R-thin (t/submatrix (:R qr-result) (range ncols-qr) :all)))


(def v89_l361 Q-thin)


(def v91_l365 R-thin)


(def
 v93_l369
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (t/eye 2))))


(deftest t94_l371 (is ((fn [d] (< d 1.0E-10)) v93_l369)))


(def v96_l376 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t97_l378 (is ((fn [d] (< d 1.0E-10)) v96_l376)))
