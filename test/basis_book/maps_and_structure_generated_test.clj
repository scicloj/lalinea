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


(def v6_l82 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l88 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l90
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l88)))


(def v11_l96 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l98
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
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


(def v22_l129 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l136
 (let
  [angles
   (mapv
    (fn* [p1__77834#] (* 2.0 Math/PI (/ p1__77834# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__77835#] (Math/cos p1__77835#)) angles)
   circle-y
   (mapv (fn* [p1__77836#] (Math/sin p1__77836#)) angles)
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


(def v26_l161 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l168 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l170
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l168)))


(def v31_l178 (la/det proj-xy))


(deftest t32_l180 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v31_l178)))


(def v34_l188 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l192 (la/det shear-mat))


(deftest
 t36_l194
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v35_l192)))


(def
 v38_l200
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l214 (def AB (la/mmul stretch-mat R90)))


(def v41_l215 (def BA (la/mmul R90 stretch-mat)))


(def v42_l217 (la/norm (la/sub AB BA)))


(deftest t43_l219 (is ((fn [d] (> d 0.1)) v42_l217)))


(def
 v45_l225
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l270 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l278 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l280 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l278)))


(def v52_l291 (la/mmul M (la/scale (la/column [1 1 -1]) 7.0)))


(deftest t53_l293 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l291)))


(def v55_l308 (def sv-M (vec (:S (la/svd M)))))


(def v56_l310 sv-M)


(deftest t57_l312 (is ((fn [v] (= 3 (count v))) v56_l310)))


(def
 v58_l315
 (def
  rank-M
  (count (filter (fn* [p1__77837#] (> p1__77837# 1.0E-10)) sv-M))))


(def v59_l317 rank-M)


(deftest t60_l319 (is ((fn [r] (= r 2)) v59_l317)))


(def
 v62_l330
 (def
  nullity-M
  (count (filter (fn* [p1__77838#] (<= p1__77838# 1.0E-10)) sv-M))))


(def v63_l332 nullity-M)


(deftest t64_l334 (is ((fn [n] (= n 1)) v63_l332)))


(def v66_l348 (= (+ rank-M nullity-M) (second (dtype/shape M))))


(deftest t67_l351 (is (true? v66_l348)))


(def v69_l356 (def svd-M (la/svd M)))


(def
 v70_l358
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


(def v72_l366 (la/norm (la/mmul M null-basis)))


(deftest t73_l368 (is ((fn [d] (< d 1.0E-10)) v72_l366)))


(def v75_l382 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v76_l384
 (count
  (filter
   (fn* [p1__77839#] (> p1__77839# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t77_l386 (is ((fn [r] (= r 2)) v76_l384)))


(def v79_l391 (la/solve A-full (la/column [5 7])))


(deftest t80_l393 (is ((fn [x] (some? x)) v79_l391)))


(def v82_l398 (la/solve M (la/column [1 2 3])))


(deftest t83_l400 (is (nil? v82_l398)))


(def
 v85_l426
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
 v87_l434
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
 v89_l443
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
 v91_l453
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t92_l458
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v91_l453)))
