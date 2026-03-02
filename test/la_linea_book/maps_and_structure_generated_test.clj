(ns
 la-linea-book.maps-and-structure-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.la-linea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l32 (def u (la/column [3 1])))


(def v4_l33 (def v (la/column [1 2])))


(def v6_l85 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l91 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l93
 (is
  ((fn
    [r]
    (and
     (< (abs (tensor/mget r 0 0)) 1.0E-10)
     (< (abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l91)))


(def v11_l99 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l101
 (is
  ((fn
    [r]
    (and
     (< (abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (abs (tensor/mget r 1 0)) 1.0E-10)))
   v11_l99)))


(def
 v14_l108
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l118
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l121 (is (true? v16_l118)))


(def
 v19_l125
 (la/close?
  (la/mmul R90 (la/scale u 3.0))
  (la/scale (la/mmul R90 u) 3.0)))


(deftest t20_l128 (is (true? v19_l125)))


(def v22_l132 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l139
 (let
  [angles
   (dfn/* (/ (* 2.0 math/PI) 40.0) (dtype/make-reader :float64 41 idx))
   circle-x
   (dfn/cos angles)
   circle-y
   (dfn/sin angles)
   stretched
   (mapv
    (fn
     [cx cy]
     (let
      [out (la/mmul stretch-mat (la/column [cx cy]))]
      [(tensor/mget out 0 0) (tensor/mget out 1 0)]))
    circle-x
    circle-y)]
  (->
   (tc/dataset
    {:x (mapv first stretched),
     :y (mapv second stretched),
     :shape (repeat 41 "stretched")})
   (tc/concat
    (tc/dataset
     {:x circle-x, :y circle-y, :shape (repeat 41 "original")}))
   (plotly/base {:=x :x, :=y :y, :=color :shape})
   (plotly/layer-line)
   plotly/plot)))


(def v26_l164 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l171 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l173
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l171)))


(def v31_l181 (la/det proj-xy))


(deftest t32_l183 (is ((fn [d] (< (abs d) 1.0E-10)) v31_l181)))


(def v34_l191 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l195 (la/det shear-mat))


(deftest t36_l197 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v35_l195)))


(def
 v38_l203
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l217 (def AB (la/mmul stretch-mat R90)))


(def v41_l218 (def BA (la/mmul R90 stretch-mat)))


(def v42_l220 (la/norm (la/sub AB BA)))


(deftest t43_l222 (is ((fn [d] (> d 0.1)) v42_l220)))


(def
 v45_l228
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l273 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l281 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l283 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l281)))


(def v52_l294 (la/mmul M (la/scale (la/column [1 1 -1]) 7.0)))


(deftest t53_l296 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l294)))


(def v55_l311 (def sv-M (:S (la/svd M))))


(def v56_l313 sv-M)


(deftest t57_l315 (is ((fn [v] (= 3 (count v))) v56_l313)))


(def v58_l318 (def rank-M (la/rank M)))


(def v59_l320 rank-M)


(deftest t60_l322 (is ((fn [r] (= r 2)) v59_l320)))


(def v62_l333 (def nullity-M (- (second (dtype/shape M)) rank-M)))


(def v63_l335 nullity-M)


(deftest t64_l337 (is ((fn [n] (= n 1)) v63_l335)))


(def v66_l349 (= (+ rank-M nullity-M) (second (dtype/shape M))))


(deftest t67_l352 (is (true? v66_l349)))


(def v69_l357 (def null-basis (la/null-space M)))


(def v70_l359 null-basis)


(def v72_l363 (la/norm (la/mmul M null-basis)))


(deftest t73_l365 (is ((fn [d] (< d 1.0E-10)) v72_l363)))


(def v75_l379 (def A-full (la/matrix [[2 1] [1 3]])))


(def v76_l381 (la/rank A-full))


(deftest t77_l383 (is ((fn [r] (= r 2)) v76_l381)))


(def v79_l388 (la/solve A-full (la/column [5 7])))


(deftest t80_l390 (is ((fn [x] (some? x)) v79_l388)))


(def v82_l395 (la/solve M (la/column [1 2 3])))


(deftest t83_l397 (is (nil? v82_l395)))


(def v85_l423 (def col-space-basis (la/col-space M)))


(def v86_l425 col-space-basis)


(def v88_l429 (def svd-M (la/svd M)))


(def
 v89_l431
 (def
  left-null-basis
  (let
   [r (la/rank M) U (:U svd-M)]
   (la/submatrix U :all (range r (first (dtype/shape M)))))))


(def v90_l436 left-null-basis)


(def
 v92_l441
 (def
  row-space-basis
  (let
   [r (la/rank M) Vt (:Vt svd-M)]
   (la/transpose (la/submatrix Vt (range r) :all)))))


(def v93_l446 row-space-basis)


(def
 v95_l452
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t96_l457
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v95_l452)))


(def
 v98_l465
 (< (la/norm (la/mmul (la/transpose M) left-null-basis)) 1.0E-10))


(deftest t99_l467 (is (true? v98_l465)))


(def
 v101_l471
 (<
  (la/norm (la/mmul (la/transpose col-space-basis) left-null-basis))
  1.0E-10))


(deftest t102_l473 (is (true? v101_l471)))


(def
 v104_l477
 (<
  (la/norm (la/mmul (la/transpose row-space-basis) null-basis))
  1.0E-10))


(deftest t105_l479 (is (true? v104_l477)))
