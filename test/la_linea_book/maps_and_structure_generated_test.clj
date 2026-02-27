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
  [clojure.test :refer [deftest is]]))


(def v3_l31 (def u (la/column [3 1])))


(def v4_l32 (def v (la/column [1 2])))


(def v6_l84 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v8_l90 (la/mmul R90 (la/column [1 0])))


(deftest
 t9_l92
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v8_l90)))


(def v11_l98 (la/mmul R90 (la/column [0 1])))


(deftest
 t12_l100
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v11_l98)))


(def
 v14_l107
 (vis/arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v16_l117
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t17_l120 (is (true? v16_l117)))


(def
 v19_l124
 (la/close?
  (la/mmul R90 (la/scale u 3.0))
  (la/scale (la/mmul R90 u) 3.0)))


(deftest t20_l127 (is (true? v19_l124)))


(def v22_l131 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v24_l138
 (let
  [angles
   (dfn/* (/ (* 2.0 Math/PI) 40.0) (dtype/make-reader :float64 41 idx))
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


(def v26_l163 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v28_l170 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t29_l172
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v28_l170)))


(def v31_l180 (la/det proj-xy))


(deftest t32_l182 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v31_l180)))


(def v34_l190 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v35_l194 (la/det shear-mat))


(deftest
 t36_l196
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v35_l194)))


(def
 v38_l202
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v40_l216 (def AB (la/mmul stretch-mat R90)))


(def v41_l217 (def BA (la/mmul R90 stretch-mat)))


(def v42_l219 (la/norm (la/sub AB BA)))


(deftest t43_l221 (is ((fn [d] (> d 0.1)) v42_l219)))


(def
 v45_l227
 (vis/arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v47_l272 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v49_l280 (la/mmul M (la/column [1 1 -1])))


(deftest t50_l282 (is ((fn [r] (< (la/norm r) 1.0E-10)) v49_l280)))


(def v52_l293 (la/mmul M (la/scale (la/column [1 1 -1]) 7.0)))


(deftest t53_l295 (is ((fn [r] (< (la/norm r) 1.0E-10)) v52_l293)))


(def v55_l310 (def sv-M (:S (la/svd M))))


(def v56_l312 sv-M)


(deftest t57_l314 (is ((fn [v] (= 3 (count v))) v56_l312)))


(def v58_l317 (def rank-M (long (dfn/sum (dfn/> sv-M 1.0E-10)))))


(def v59_l319 rank-M)


(deftest t60_l321 (is ((fn [r] (= r 2)) v59_l319)))


(def v62_l332 (def nullity-M (long (dfn/sum (dfn/<= sv-M 1.0E-10)))))


(def v63_l334 nullity-M)


(deftest t64_l336 (is ((fn [n] (= n 1)) v63_l334)))


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


(def v71_l364 null-basis)


(def v73_l368 (la/norm (la/mmul M null-basis)))


(deftest t74_l370 (is ((fn [d] (< d 1.0E-10)) v73_l368)))


(def v76_l384 (def A-full (la/matrix [[2 1] [1 3]])))


(def v77_l386 (long (dfn/sum (dfn/> (:S (la/svd A-full)) 1.0E-10))))


(deftest t78_l388 (is ((fn [r] (= r 2)) v77_l386)))


(def v80_l393 (la/solve A-full (la/column [5 7])))


(deftest t81_l395 (is ((fn [x] (some? x)) v80_l393)))


(def v83_l400 (la/solve M (la/column [1 2 3])))


(deftest t84_l402 (is (nil? v83_l400)))


(def
 v86_l428
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


(def v87_l434 col-space-basis)


(def
 v89_l438
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


(def v90_l444 left-null-basis)


(def
 v92_l449
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


(def v93_l455 row-space-basis)


(def
 v95_l461
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t96_l466
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v95_l461)))
