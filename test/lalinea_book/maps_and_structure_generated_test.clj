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


(def v6_l82 (def R90 (t/matrix [[0 -1] [1 0]])))


(def v8_l88 (la/mmul R90 (t/column [1 0])))


(deftest
 t9_l90
 (is
  ((fn
    [r]
    (and (< (abs (r 0 0)) 1.0E-10) (< (abs (- (r 1 0) 1.0)) 1.0E-10)))
   v8_l88)))


(def v11_l96 (la/mmul R90 (t/column [0 1])))


(deftest
 t12_l98
 (is
  ((fn
    [r]
    (and (< (abs (- (r 0 0) -1.0)) 1.0E-10) (< (abs (r 1 0)) 1.0E-10)))
   v11_l96)))


(def
 v14_l105
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l115
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l118 (is (true? v16_l115)))


(def
 v19_l122
 (la/close?
  (la/mmul R90 (la/scale u 3.0))
  (la/scale (la/mmul R90 u) 3.0)))


(deftest t20_l125 (is (true? v19_l122)))


(def v22_l129 (def stretch-mat (t/matrix [[3 0] [0 1]])))


(def
 v24_l136
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


(def v26_l159 (def proj-xy (t/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l166 (la/mmul proj-xy (t/column [5 3 7])))


(deftest
 t29_l168
 (is
  ((fn [r] (and (= 5.0 (r 0 0)) (= 3.0 (r 1 0)) (= 0.0 (r 2 0))))
   v28_l166)))


(def v31_l176 (la/det proj-xy))


(deftest t32_l178 (is ((fn [d] (< (abs d) 1.0E-10)) v31_l176)))


(def v34_l186 (def shear-mat (t/matrix [[1 2] [0 1]])))


(def v35_l190 (la/det shear-mat))


(deftest t36_l192 (is ((fn [d] (< (abs (- d 1.0)) 1.0E-10)) v35_l190)))


(def
 v38_l198
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l212 (def AB (la/mmul stretch-mat R90)))


(def v41_l213 (def BA (la/mmul R90 stretch-mat)))


(def v42_l215 (la/norm (la/sub AB BA)))


(deftest t43_l217 (is ((fn [d] (> d 0.1)) v42_l215)))


(def
 v45_l223
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l268 (def M (t/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l276 (la/mmul M (t/column [1 1 -1])))


(deftest t50_l278 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l276)))


(def v52_l289 (la/mmul M (la/scale (t/column [1 1 -1]) 7.0)))


(deftest t53_l291 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l289)))


(def v55_l307 (def sv-M (:S (la/svd M))))


(def v56_l309 sv-M)


(deftest t57_l311 (is ((fn [v] (= 3 (count v))) v56_l309)))


(def v58_l314 (def rank-M (la/rank M)))


(def v59_l316 rank-M)


(deftest t60_l318 (is ((fn [r] (= r 2)) v59_l316)))


(def v62_l329 (def nullity-M (- (second (t/shape M)) rank-M)))


(def v63_l331 nullity-M)


(deftest t64_l333 (is ((fn [n] (= n 1)) v63_l331)))


(def v66_l345 (= (+ rank-M nullity-M) (second (t/shape M))))


(deftest t67_l348 (is (true? v66_l345)))


(def v69_l353 (def null-basis (la/null-space M)))


(def v70_l355 null-basis)


(def v72_l359 (la/norm (la/mmul M null-basis)))


(deftest t73_l361 (is ((fn [d] (< d 1.0E-10)) v72_l359)))


(def v75_l375 (def A-full (t/matrix [[2 1] [1 3]])))


(def v76_l377 (la/rank A-full))


(deftest t77_l379 (is ((fn [r] (= r 2)) v76_l377)))


(def v79_l384 (la/solve A-full (t/column [5 7])))


(deftest t80_l386 (is ((fn [x] (some? x)) v79_l384)))


(def v82_l391 (la/solve M (t/column [1 2 3])))


(deftest t83_l393 (is (nil? v82_l391)))


(def v85_l419 (def col-space-basis (la/col-space M)))


(def v86_l421 col-space-basis)


(def v88_l425 (def svd-M (la/svd M)))


(def
 v89_l427
 (def
  left-null-basis
  (let
   [r (la/rank M) U (:U svd-M)]
   (t/submatrix U :all (range r (first (t/shape M)))))))


(def v90_l432 left-null-basis)


(def
 v92_l437
 (def
  row-space-basis
  (let
   [r (la/rank M) Vt (:Vt svd-M)]
   (la/transpose (t/submatrix Vt (range r) :all)))))


(def v93_l442 row-space-basis)


(def
 v95_l448
 {:col-space (second (t/shape col-space-basis)),
  :left-null (second (t/shape left-null-basis)),
  :row-space (second (t/shape row-space-basis)),
  :null-space (second (t/shape null-basis))})


(deftest
 t96_l453
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v95_l448)))


(def
 v98_l461
 (< (la/norm (la/mmul (la/transpose M) left-null-basis)) 1.0E-10))


(deftest t99_l463 (is (true? v98_l461)))


(def
 v101_l467
 (<
  (la/norm (la/mmul (la/transpose col-space-basis) left-null-basis))
  1.0E-10))


(deftest t102_l469 (is (true? v101_l467)))


(def
 v104_l473
 (<
  (la/norm (la/mmul (la/transpose row-space-basis) null-basis))
  1.0E-10))


(deftest t105_l475 (is (true? v104_l473)))
