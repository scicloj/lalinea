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


(def v6_l57 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l63 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l65
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l63)))


(def v11_l71 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l73
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v11_l71)))


(def
 v14_l80
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l90
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l93 (is (true? v16_l90)))


(def
 v19_l97
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t20_l100 (is (true? v19_l97)))


(def v22_l104 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l111
 (let
  [angles
   (mapv
    (fn* [p1__108580#] (* 2.0 Math/PI (/ p1__108580# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__108581#] (Math/cos p1__108581#)) angles)
   circle-y
   (mapv (fn* [p1__108582#] (Math/sin p1__108582#)) angles)
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


(def v26_l136 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l143 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l145
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l143)))


(def v31_l153 (la/det proj-xy))


(deftest t32_l155 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v31_l153)))


(def v34_l163 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l167 (la/det shear-mat))


(deftest
 t36_l169
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v35_l167)))


(def
 v38_l175
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l189 (def AB (la/mmul stretch-mat R90)))


(def v41_l190 (def BA (la/mmul R90 stretch-mat)))


(def v42_l192 (la/norm (la/sub AB BA)))


(deftest t43_l194 (is ((fn [d] (> d 0.1)) v42_l192)))


(def
 v45_l200
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l245 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l253 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l255 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l253)))


(def v52_l266 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t53_l268 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l266)))


(def v55_l283 (def sv-M (vec (:S (la/svd M)))))


(def v56_l285 sv-M)


(deftest t57_l287 (is ((fn [v] (= 3 (count v))) v56_l285)))


(def
 v58_l290
 (def
  rank-M
  (count (filter (fn* [p1__108583#] (> p1__108583# 1.0E-10)) sv-M))))


(def v59_l292 rank-M)


(deftest t60_l294 (is ((fn [r] (= r 2)) v59_l292)))


(def v62_l316 (def svd-M (la/svd M)))


(def
 v63_l318
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


(def v65_l326 (la/norm (la/mmul M null-basis)))


(deftest t66_l328 (is ((fn [d] (< d 1.0E-10)) v65_l326)))


(def v68_l342 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v69_l344
 (count
  (filter
   (fn* [p1__108584#] (> p1__108584# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t70_l346 (is ((fn [r] (= r 2)) v69_l344)))


(def v72_l351 (la/solve A-full (la/column [5 7])))


(deftest t73_l353 (is ((fn [x] (some? x)) v72_l351)))


(def v75_l358 (la/solve M (la/column [1 2 3])))


(deftest t76_l360 (is (nil? v75_l358)))


(def
 v78_l385
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
 v80_l393
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
 v82_l402
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
 v84_l412
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t85_l417
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v84_l412)))


(def v87_l431 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t88_l433 (is ((fn [r] (< (la/norm r) 1.0E-10)) v87_l431)))


(def v90_l439 (la/mmul (la/transpose col-space-basis) left-null-basis))


(deftest t91_l441 (is ((fn [r] (< (la/norm r) 1.0E-10)) v90_l439)))
