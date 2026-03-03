(ns
 lalinea-book.maps-and-structure-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l28 (def u (t/column [3 1])))


(def v4_l29 (def v (t/column [1 2])))


(def v6_l81 (def R90 (t/matrix [[0 -1] [1 0]])))


(def v8_l87 (la/mmul R90 (t/column [1 0])))


(deftest
 t9_l89
 (is
  ((fn
    [r]
    (and (< (abs (r 0 0)) 1.0E-10) (< (abs (- (r 1 0) 1.0)) 1.0E-10)))
   v8_l87)))


(def v11_l95 (la/mmul R90 (t/column [0 1])))


(deftest
 t12_l97
 (is
  ((fn
    [r]
    (and (< (abs (- (r 0 0) -1.0)) 1.0E-10) (< (abs (r 1 0)) 1.0E-10)))
   v11_l95)))


(def
 v14_l104
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l114
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l117 (is (true? v16_l114)))


(def
 v19_l121
 (la/close?
  (la/mmul R90 (la/scale u 3.0))
  (la/scale (la/mmul R90 u) 3.0)))


(deftest t20_l124 (is (true? v19_l121)))


(def v22_l128 (def stretch-mat (t/matrix [[3 0] [0 1]])))


(def
 v24_l135
 (let
  [angles
   (la/mul (/ (* 2.0 math/PI) 40.0) (t/make-reader :float64 41 idx))
   circle-x
   (elem/cos angles)
   circle-y
   (elem/sin angles)
   data-mat
   (t/hstack [(t/column circle-x) (t/column circle-y)])
   stretched-mat
   (la/transpose (la/mmul stretch-mat (la/transpose data-mat)))]
  (->
   (tc/dataset
    {:x (t/select stretched-mat :all 0),
     :y (t/select stretched-mat :all 1),
     :shape (repeat 41 "stretched")})
   (tc/concat
    (tc/dataset
     {:x circle-x, :y circle-y, :shape (repeat 41 "original")}))
   (plotly/base {:=x :x, :=y :y, :=color :shape})
   (plotly/layer-line)
   plotly/plot)))


(def v26_l158 (def proj-xy (t/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l165 (la/mmul proj-xy (t/column [5 3 7])))


(deftest
 t29_l167
 (is
  ((fn [r] (and (= 5.0 (r 0 0)) (= 3.0 (r 1 0)) (= 0.0 (r 2 0))))
   v28_l165)))


(def v31_l175 (la/det proj-xy))


(deftest t32_l177 (is ((fn [d] (< (abs d) 1.0E-10)) v31_l175)))


(def v34_l185 (def shear-mat (t/matrix [[1 2] [0 1]])))


(def v35_l189 (la/det shear-mat))


(deftest t36_l191 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v35_l189)))


(def
 v38_l197
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l211 (def AB (la/mmul stretch-mat R90)))


(def v41_l212 (def BA (la/mmul R90 stretch-mat)))


(def v42_l214 (la/norm (la/sub AB BA)))


(deftest t43_l216 (is ((fn [d] (> d 0.1)) v42_l214)))


(def
 v45_l222
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l267 (def M (t/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l275 (la/mmul M (t/column [1 1 -1])))


(deftest t50_l277 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l275)))


(def v52_l288 (la/mmul M (la/scale (t/column [1 1 -1]) 7.0)))


(deftest t53_l290 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l288)))


(def v55_l305 (def sv-M (:S (la/svd M))))


(def v56_l307 sv-M)


(deftest t57_l309 (is ((fn [v] (= 3 (count v))) v56_l307)))


(def v58_l312 (def rank-M (la/rank M)))


(def v59_l314 rank-M)


(deftest t60_l316 (is ((fn [r] (= r 2)) v59_l314)))


(def v62_l327 (def nullity-M (- (second (t/shape M)) rank-M)))


(def v63_l329 nullity-M)


(deftest t64_l331 (is ((fn [n] (= n 1)) v63_l329)))


(def v66_l343 (= (+ rank-M nullity-M) (second (t/shape M))))


(deftest t67_l346 (is (true? v66_l343)))


(def v69_l351 (def null-basis (la/null-space M)))


(def v70_l353 null-basis)


(def v72_l357 (la/norm (la/mmul M null-basis)))


(deftest t73_l359 (is ((fn [d] (< d 1.0E-10)) v72_l357)))


(def v75_l373 (def A-full (t/matrix [[2 1] [1 3]])))


(def v76_l375 (la/rank A-full))


(deftest t77_l377 (is ((fn [r] (= r 2)) v76_l375)))


(def v79_l382 (la/solve A-full (t/column [5 7])))


(deftest t80_l384 (is ((fn [x] (some? x)) v79_l382)))


(def v82_l389 (la/solve M (t/column [1 2 3])))


(deftest t83_l391 (is (nil? v82_l389)))


(def v85_l417 (def col-space-basis (la/col-space M)))


(def v86_l419 col-space-basis)


(def v88_l423 (def svd-M (la/svd M)))


(def
 v89_l425
 (def
  left-null-basis
  (let
   [r (la/rank M) U (:U svd-M)]
   (t/submatrix U :all (range r (first (t/shape M)))))))


(def v90_l430 left-null-basis)


(def
 v92_l435
 (def
  row-space-basis
  (let
   [r (la/rank M) Vt (:Vt svd-M)]
   (la/transpose (t/submatrix Vt (range r) :all)))))


(def v93_l440 row-space-basis)


(def
 v95_l446
 {:col-space (second (t/shape col-space-basis)),
  :left-null (second (t/shape left-null-basis)),
  :row-space (second (t/shape row-space-basis)),
  :null-space (second (t/shape null-basis))})


(deftest
 t96_l451
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v95_l446)))


(def
 v98_l459
 (< (la/norm (la/mmul (la/transpose M) left-null-basis)) 1.0E-10))


(deftest t99_l461 (is (true? v98_l459)))


(def
 v101_l465
 (<
  (la/norm (la/mmul (la/transpose col-space-basis) left-null-basis))
  1.0E-10))


(deftest t102_l467 (is (true? v101_l465)))


(def
 v104_l471
 (<
  (la/norm (la/mmul (la/transpose row-space-basis) null-basis))
  1.0E-10))


(deftest t105_l473 (is (true? v104_l471)))
