(ns
 basis-book.inner-products-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.datatype :as dtype]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (def a3 (la/column [1 2 3])))


(def v4_l32 (def b3 (la/column [4 5 6])))


(def v5_l34 (def dot-ab (dfn/sum (dfn/* a3 b3))))


(def v6_l37 dot-ab)


(deftest
 t7_l39
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v6_l37)))


(def v9_l48 (la/norm a3))


(deftest
 t10_l50
 (is ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v9_l48)))


(def v12_l56 (dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1]))))


(deftest t13_l58 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v12_l56)))


(def v15_l75 (def p (la/column [1 0])))


(def v16_l76 (def q (la/column [1 1])))


(def
 v17_l78
 (def cos-theta (/ (dfn/sum (dfn/* p q)) (* (la/norm p) (la/norm q)))))


(def v18_l82 cos-theta)


(deftest
 t19_l84
 (is
  ((fn [c] (< (Math/abs (- c (/ 1.0 (Math/sqrt 2.0)))) 1.0E-10))
   v18_l82)))


(def v21_l89 (Math/toDegrees (Math/acos cos-theta)))


(deftest
 t22_l91
 (is ((fn [d] (< (Math/abs (- d 45.0)) 1.0E-10)) v21_l89)))


(def
 v24_l110
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


(def v26_l120 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v28_l128
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v30_l136 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t31_l138 (is (true? v30_l136)))


(def v33_l142 (def point3d (la/column [1 2 3])))


(def v34_l144 (def projected-pt (la/mmul P-proj point3d)))


(def v35_l146 projected-pt)


(def v37_l152 (def resid (la/sub point3d projected-pt)))


(def v38_l154 (la/mmul (la/transpose W-proj) resid))


(deftest t39_l156 (is ((fn [r] (< (la/norm r) 1.0E-10)) v38_l154)))


(def v41_l179 (def a-gs (la/column [1 1 0])))


(def v42_l180 (def b-gs (la/column [1 0 1])))


(def v44_l184 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v45_l186 q1-gs)


(def v47_l190 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v48_l193
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v50_l198
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v51_l201 q2-gs)


(def
 v53_l205
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t54_l209
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v53_l205)))


(def v56_l225 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v57_l229 (def qr-result (la/qr A-qr)))


(def v59_l233 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v60_l234
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v61_l235
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v63_l239
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t64_l241 (is ((fn [d] (< d 1.0E-10)) v63_l239)))


(def v66_l246 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t67_l248 (is ((fn [d] (< d 1.0E-10)) v66_l246)))
