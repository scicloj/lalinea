(ns
 basis-book.maps-and-structure-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (def u (la/column [3 1])))


(def v4_l30 (def v (la/column [1 2])))


(def v6_l68 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l74 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l76
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l74)))


(def v11_l82 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l84
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v11_l82)))


(def
 v14_l91
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l101
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l104 (is (true? v16_l101)))


(def
 v19_l108
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t20_l111 (is (true? v19_l108)))


(def v22_l115 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l122
 (let
  [angles
   (mapv
    (fn* [p1__137281#] (* 2.0 Math/PI (/ p1__137281# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__137282#] (Math/cos p1__137282#)) angles)
   circle-y
   (mapv (fn* [p1__137283#] (Math/sin p1__137283#)) angles)
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


(def v26_l147 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l154 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l156
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l154)))


(def v31_l164 (la/det proj-xy))


(deftest t32_l166 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v31_l164)))


(def v34_l174 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l178 (la/det shear-mat))


(deftest
 t36_l180
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v35_l178)))


(def
 v38_l186
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l200 (def AB (la/mmul stretch-mat R90)))


(def v41_l201 (def BA (la/mmul R90 stretch-mat)))


(def v42_l203 (la/norm (la/sub AB BA)))


(deftest t43_l205 (is ((fn [d] (> d 0.1)) v42_l203)))


(def
 v45_l211
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l256 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l264 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l266 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l264)))


(def v52_l277 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t53_l279 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l277)))


(def v55_l294 (def sv-M (vec (:S (la/svd M)))))


(def v56_l296 sv-M)


(deftest t57_l298 (is ((fn [v] (= 3 (count v))) v56_l296)))


(def
 v58_l301
 (def
  rank-M
  (count (filter (fn* [p1__137284#] (> p1__137284# 1.0E-10)) sv-M))))


(def v59_l303 rank-M)


(deftest t60_l305 (is ((fn [r] (= r 2)) v59_l303)))


(def
 v62_l316
 (def
  nullity-M
  (count (filter (fn* [p1__137285#] (<= p1__137285# 1.0E-10)) sv-M))))


(def v63_l318 nullity-M)


(deftest t64_l320 (is ((fn [n] (= n 1)) v63_l318)))


(def v66_l334 (= (+ rank-M nullity-M) (second (dtype/shape M))))


(deftest t67_l337 (is (true? v66_l334)))


(def v69_l342 (def svd-M (la/svd M)))


(def
 v70_l344
 (def
  null-basis
  (let
   [sv
    (vec (:S svd-M))
    Vt
    (:Vt svd-M)
    null-idx
    (vec (keep-indexed (fn [i s] (when (< s 1.0E-10) i)) sv))]
   (la/submatrix (la/transpose Vt) :all null-idx))))


(def v72_l352 (la/norm (la/mmul M null-basis)))


(deftest t73_l354 (is ((fn [d] (< d 1.0E-10)) v72_l352)))


(def v75_l368 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v76_l370
 (count
  (filter
   (fn* [p1__137286#] (> p1__137286# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t77_l372 (is ((fn [r] (= r 2)) v76_l370)))


(def v79_l377 (la/solve A-full (la/column [5 7])))


(deftest t80_l379 (is ((fn [x] (some? x)) v79_l377)))


(def v82_l384 (la/solve M (la/column [1 2 3])))


(deftest t83_l386 (is (nil? v82_l384)))


(def
 v85_l412
 (def
  col-space-basis
  (let
   [sv
    (vec (:S svd-M))
    U
    (:U svd-M)
    col-idx
    (vec (keep-indexed (fn [i s] (when (> s 1.0E-10) i)) sv))]
   (la/submatrix U :all col-idx))))


(def
 v87_l420
 (def
  left-null-basis
  (let
   [sv
    (vec (:S svd-M))
    U
    (:U svd-M)
    null-idx
    (vec (keep-indexed (fn [i s] (when (< s 1.0E-10) i)) sv))]
   (la/submatrix U :all null-idx))))


(def
 v89_l429
 (def
  row-space-basis
  (let
   [sv
    (vec (:S svd-M))
    Vt
    (:Vt svd-M)
    row-idx
    (vec (keep-indexed (fn [i s] (when (> s 1.0E-10) i)) sv))]
   (la/submatrix (la/transpose Vt) :all row-idx))))


(def
 v91_l439
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t92_l444
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v91_l439)))
