(ns
 lalinea-book.maps-and-structure-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
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
   data-mat
   (la/hstack [(la/column circle-x) (la/column circle-y)])
   stretched-mat
   (la/transpose (la/mmul stretch-mat (la/transpose data-mat)))]
  (->
   (tc/dataset
    {:x (tensor/select stretched-mat :all 0),
     :y (tensor/select stretched-mat :all 1),
     :shape (repeat 41 "stretched")})
   (tc/concat
    (tc/dataset
     {:x circle-x, :y circle-y, :shape (repeat 41 "original")}))
   (plotly/base {:=x :x, :=y :y, :=color :shape})
   (plotly/layer-line)
   plotly/plot)))


(def v26_l162 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l169 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l171
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l169)))


(def v31_l179 (la/det proj-xy))


(deftest t32_l181 (is ((fn [d] (< (abs d) 1.0E-10)) v31_l179)))


(def v34_l189 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l193 (la/det shear-mat))


(deftest t36_l195 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v35_l193)))


(def
 v38_l201
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l215 (def AB (la/mmul stretch-mat R90)))


(def v41_l216 (def BA (la/mmul R90 stretch-mat)))


(def v42_l218 (la/norm (la/sub AB BA)))


(deftest t43_l220 (is ((fn [d] (> d 0.1)) v42_l218)))


(def
 v45_l226
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l271 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l279 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l281 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l279)))


(def v52_l292 (la/mmul M (la/scale (la/column [1 1 -1]) 7.0)))


(deftest t53_l294 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l292)))


(def v55_l309 (def sv-M (:S (la/svd M))))


(def v56_l311 sv-M)


(deftest t57_l313 (is ((fn [v] (= 3 (count v))) v56_l311)))


(def v58_l316 (def rank-M (la/rank M)))


(def v59_l318 rank-M)


(deftest t60_l320 (is ((fn [r] (= r 2)) v59_l318)))


(def v62_l331 (def nullity-M (- (second (dtype/shape M)) rank-M)))


(def v63_l333 nullity-M)


(deftest t64_l335 (is ((fn [n] (= n 1)) v63_l333)))


(def v66_l347 (= (+ rank-M nullity-M) (second (dtype/shape M))))


(deftest t67_l350 (is (true? v66_l347)))


(def v69_l355 (def null-basis (la/null-space M)))


(def v70_l357 null-basis)


(def v72_l361 (la/norm (la/mmul M null-basis)))


(deftest t73_l363 (is ((fn [d] (< d 1.0E-10)) v72_l361)))


(def v75_l377 (def A-full (la/matrix [[2 1] [1 3]])))


(def v76_l379 (la/rank A-full))


(deftest t77_l381 (is ((fn [r] (= r 2)) v76_l379)))


(def v79_l386 (la/solve A-full (la/column [5 7])))


(deftest t80_l388 (is ((fn [x] (some? x)) v79_l386)))


(def v82_l393 (la/solve M (la/column [1 2 3])))


(deftest t83_l395 (is (nil? v82_l393)))


(def v85_l421 (def col-space-basis (la/col-space M)))


(def v86_l423 col-space-basis)


(def v88_l427 (def svd-M (la/svd M)))


(def
 v89_l429
 (def
  left-null-basis
  (let
   [r (la/rank M) U (:U svd-M)]
   (la/submatrix U :all (range r (first (dtype/shape M)))))))


(def v90_l434 left-null-basis)


(def
 v92_l439
 (def
  row-space-basis
  (let
   [r (la/rank M) Vt (:Vt svd-M)]
   (la/transpose (la/submatrix Vt (range r) :all)))))


(def v93_l444 row-space-basis)


(def
 v95_l450
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t96_l455
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v95_l450)))


(def
 v98_l463
 (< (la/norm (la/mmul (la/transpose M) left-null-basis)) 1.0E-10))


(deftest t99_l465 (is (true? v98_l463)))


(def
 v101_l469
 (<
  (la/norm (la/mmul (la/transpose col-space-basis) left-null-basis))
  1.0E-10))


(deftest t102_l471 (is (true? v101_l469)))


(def
 v104_l475
 (<
  (la/norm (la/mmul (la/transpose row-space-basis) null-basis))
  1.0E-10))


(deftest t105_l477 (is (true? v104_l475)))
