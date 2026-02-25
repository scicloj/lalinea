(ns
 basis-book.abstract-linear-algebra-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l37
 (def
  arrow-plot
  (fn
   [arrows opts]
   (let
    [width
     (or (:width opts) 300)
     all-pts
     (mapcat
      (fn
       [{:keys [xy from]}]
       (let
        [[fx fy] (or from [0 0]) [tx ty] [(+ fx (xy 0)) (+ fy (xy 1))]]
        [[fx fy] [tx ty]]))
      arrows)
     all-xs
     (map first all-pts)
     all-ys
     (map second all-pts)
     x-min
     (apply min all-xs)
     x-max
     (apply max all-xs)
     y-min
     (apply min all-ys)
     y-max
     (apply max all-ys)
     dx
     (- x-max x-min)
     dy
     (- y-max y-min)
     span
     (max dx dy 1.0)
     pad
     (* 0.3 span)
     vb-x
     (- x-min pad)
     vb-w
     (+ dx (* 2 pad))
     vb-h
     (+ dy (* 2 pad))
     vb-y-top
     (+ y-max pad)
     height
     (* width (/ vb-h vb-w))
     px-per-unit
     (/ width vb-w)
     stroke-w
     (/ 2.0 px-per-unit)
     head-w
     (* 10 stroke-w)
     head-h
     (* 7 stroke-w)
     font-size
     (* 12 stroke-w)
     grid-lo-x
     (long (Math/floor (- x-min pad)))
     grid-hi-x
     (long (Math/ceil (+ x-max pad)))
     grid-lo-y
     (long (Math/floor (- y-min pad)))
     grid-hi-y
     (long (Math/ceil (+ y-max pad)))
     grid-lines
     (concat
      (for
       [gx (range grid-lo-x (inc grid-hi-x))]
       [:line
        {:x1 gx,
         :y1 (- grid-lo-y),
         :x2 gx,
         :y2 (- grid-hi-y),
         :stroke (if (zero? gx) "#999" "#ddd"),
         :stroke-width (if (zero? gx) stroke-w (* 0.5 stroke-w))}])
      (for
       [gy (range grid-lo-y (inc grid-hi-y))]
       [:line
        {:x1 grid-lo-x,
         :y1 (- gy),
         :x2 grid-hi-x,
         :y2 (- gy),
         :stroke (if (zero? gy) "#999" "#ddd"),
         :stroke-width (if (zero? gy) stroke-w (* 0.5 stroke-w))}]))
     defs
     (into
      [:defs]
      (map
       (fn
        [{:keys [color]}]
        [:marker
         {:id (str "ah-" (subs color 1)),
          :markerWidth head-w,
          :markerHeight head-h,
          :refX head-w,
          :refY (/ head-h 2),
          :orient "auto",
          :markerUnits "userSpaceOnUse"}
         [:polygon
          {:points (str "0 0, " head-w " " (/ head-h 2) ", 0 " head-h),
           :fill color}]])
       arrows))
     arrow-elts
     (mapcat
      (fn
       [{:keys [label xy color from dashed?]}]
       (let
        [[fx fy]
         (or from [0 0])
         [tx ty]
         [(+ fx (xy 0)) (+ fy (xy 1))]
         adx
         (- tx fx)
         ady
         (- ty fy)
         len
         (Math/sqrt (+ (* adx adx) (* ady ady)))
         nx
         (if (pos? len) (/ (- ady) len) 0)
         ny
         (if (pos? len) (/ adx len) 0)
         lx
         (+ fx (* 0.7 adx) (* font-size 0.7 nx))
         ly
         (+ fy (* 0.7 ady) (* font-size 0.7 ny))]
        (cond->
         [[:line
           (cond->
            {:x1 fx,
             :y1 (- fy),
             :x2 tx,
             :y2 (- ty),
             :stroke color,
             :stroke-width (* 1.5 stroke-w),
             :marker-end (str "url(#ah-" (subs color 1) ")")}
            dashed?
            (assoc
             :stroke-dasharray
             (str (* 4 stroke-w) " " (* 3 stroke-w))))]]
         label
         (conj
          [:text
           {:x lx,
            :y (- ly),
            :fill color,
            :font-size font-size,
            :font-family "sans-serif",
            :font-weight "bold",
            :text-anchor "middle",
            :dominant-baseline "central"}
           label]))))
      arrows)]
    (kind/hiccup
     (into
      [:svg
       {:width width,
        :height height,
        :viewBox (str vb-x " " (- vb-y-top) " " vb-w " " vb-h)}]
      (concat [defs] grid-lines arrow-elts)))))))


(def v5_l127 (def u (la/column [3 1])))


(def v6_l128 (def v (la/column [1 2])))


(def
 v8_l133
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v10_l143 (la/add u v))


(deftest
 t11_l145
 (is
  ((fn
    [r]
    (and (= 4.0 (tensor/mget r 0 0)) (= 3.0 (tensor/mget r 1 0))))
   v10_l143)))


(def
 v12_l149
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v14_l159 (la/scale 2.0 u))


(deftest
 t15_l161
 (is
  ((fn
    [r]
    (and (= 6.0 (tensor/mget r 0 0)) (= 2.0 (tensor/mget r 1 0))))
   v14_l159)))


(def
 v16_l165
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v18_l171 (la/scale -1.0 u))


(deftest
 t19_l173
 (is
  ((fn
    [r]
    (and (= -3.0 (tensor/mget r 0 0)) (= -1.0 (tensor/mget r 1 0))))
   v18_l171)))


(def
 v20_l177
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v22_l211 (def w-ax (la/column [-1 4])))


(def v23_l212 (def zero2 (la/column [0 0])))


(def v25_l216 (la/close? (la/add u v) (la/add v u)))


(deftest t26_l218 (is (true? v25_l216)))


(def
 v28_l222
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t29_l225 (is (true? v28_l222)))


(def v31_l229 (la/close? (la/add u zero2) u))


(deftest t32_l231 (is (true? v31_l229)))


(def v34_l235 (la/close? (la/add u (la/scale -1.0 u)) zero2))


(deftest t35_l237 (is (true? v34_l235)))


(def
 v37_l241
 (la/close? (la/scale 2.0 (la/scale 3.0 u)) (la/scale 6.0 u)))


(deftest t38_l244 (is (true? v37_l241)))


(def v40_l248 (la/close? (la/scale 1.0 u) u))


(deftest t41_l250 (is (true? v40_l248)))


(def
 v43_l254
 (la/close?
  (la/scale 5.0 (la/add u v))
  (la/add (la/scale 5.0 u) (la/scale 5.0 v))))


(deftest t44_l257 (is (true? v43_l254)))


(def
 v46_l261
 (la/close?
  (la/scale (+ 2.0 3.0) u)
  (la/add (la/scale 2.0 u) (la/scale 3.0 u))))


(deftest t47_l264 (is (true? v46_l261)))


(def v49_l289 (la/add (la/scale 2.0 u) (la/scale -1.0 v)))


(deftest
 t50_l291
 (is
  ((fn
    [r]
    (and (= 5.0 (tensor/mget r 0 0)) (= 0.0 (tensor/mget r 1 0))))
   v49_l289)))


(def
 v52_l300
 (arrow-plot
  [{:label "2u", :xy [6 2], :color "#2266cc"}
   {:label "-v",
    :xy [-1 -2],
    :color "#cc4422",
    :from [6 2],
    :dashed? true}
   {:label "2u-v", :xy [5 0], :color "#228833"}]
  {}))


(def
 v54_l315
 (let
  [params
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] {:a a, :b b})
   xs
   (mapv (fn [{:keys [a b]}] (+ (* a 3.0) (* b 1.0))) params)
   ys
   (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v56_l331
 (let
  [params
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] {:a a, :b b})
   xs
   (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)
   ys
   (mapv (fn [{:keys [a b]}] (+ (* a 2.0) (* b 4.0))) params)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v58_l370
 (arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v60_l379 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t61_l382 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v60_l379)))


(def
 v63_l387
 (arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v65_l395 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t66_l398 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v65_l395)))


(def v68_l404 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t69_l408
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v68_l404)))


(def v71_l414 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t72_l418 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v71_l414)))


(def v74_l438 (def e1 (la/column [1 0 0])))


(def v75_l439 (def e2 (la/column [0 1 0])))


(def v76_l440 (def e3 (la/column [0 0 1])))


(def v78_l450 (def w (la/column [5 -3 7])))


(def
 v80_l454
 (la/close?
  w
  (la/add
   (la/scale 5.0 e1)
   (la/add (la/scale -3.0 e2) (la/scale 7.0 e3)))))


(deftest t81_l459 (is (true? v80_l454)))


(def v83_l503 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v85_l509 (la/mmul R90 (la/column [1 0])))


(deftest
 t86_l511
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v85_l509)))


(def v88_l517 (la/mmul R90 (la/column [0 1])))


(deftest
 t89_l519
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v88_l517)))


(def
 v91_l526
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v93_l536
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t94_l539 (is (true? v93_l536)))


(def
 v96_l543
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t97_l546 (is (true? v96_l543)))


(def v99_l550 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v101_l557
 (let
  [angles
   (mapv
    (fn* [p1__46856#] (* 2.0 Math/PI (/ p1__46856# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__46857#] (Math/cos p1__46857#)) angles)
   circle-y
   (mapv (fn* [p1__46858#] (Math/sin p1__46858#)) angles)
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


(def v103_l582 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v105_l589 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t106_l591
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v105_l589)))


(def v108_l599 (la/det proj-xy))


(deftest t109_l601 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v108_l599)))


(def v111_l609 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v112_l613 (la/det shear-mat))


(deftest
 t113_l615
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v112_l613)))


(def
 v115_l621
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v117_l635 (def AB (la/mmul stretch-mat R90)))


(def v118_l636 (def BA (la/mmul R90 stretch-mat)))


(def v119_l638 (la/norm (la/sub AB BA)))


(deftest t120_l640 (is ((fn [d] (> d 0.1)) v119_l638)))


(def
 v122_l646
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v124_l691 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v126_l699 (la/mmul M (la/column [1 1 -1])))


(deftest t127_l701 (is ((fn [r] (< (la/norm r) 1.0E-10)) v126_l699)))


(def v129_l712 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t130_l714 (is ((fn [r] (< (la/norm r) 1.0E-10)) v129_l712)))


(def v132_l729 (def sv-M (vec (:S (la/svd M)))))


(def v133_l731 sv-M)


(deftest t134_l733 (is ((fn [v] (= 3 (count v))) v133_l731)))


(def
 v135_l736
 (def
  rank-M
  (count (filter (fn* [p1__46859#] (> p1__46859# 1.0E-10)) sv-M))))


(def v136_l738 rank-M)


(deftest t137_l740 (is ((fn [r] (= r 2)) v136_l738)))


(def v139_l762 (def svd-M (la/svd M)))


(def
 v140_l764
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


(def v142_l772 (la/norm (la/mmul M null-basis)))


(deftest t143_l774 (is ((fn [d] (< d 1.0E-10)) v142_l772)))


(def v145_l788 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v146_l790
 (count
  (filter
   (fn* [p1__46860#] (> p1__46860# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t147_l792 (is ((fn [r] (= r 2)) v146_l790)))


(def v149_l797 (la/solve A-full (la/column [5 7])))


(deftest t150_l799 (is ((fn [x] (some? x)) v149_l797)))


(def v152_l804 (la/solve M (la/column [1 2 3])))


(deftest t153_l806 (is (nil? v152_l804)))


(def
 v155_l830
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


(def v157_l838 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t158_l840 (is ((fn [r] (< (la/norm r) 1.0E-10)) v157_l838)))


(def v160_l858 (def a3 (la/column [1 2 3])))


(def v161_l859 (def b3 (la/column [4 5 6])))


(def v162_l861 (def dot-ab (dfn/sum (dfn/* a3 b3))))


(def v163_l864 dot-ab)


(deftest
 t164_l866
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v163_l864)))


(def v166_l875 (la/norm a3))


(deftest
 t167_l877
 (is
  ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v166_l875)))


(def v169_l883 (dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1]))))


(deftest t170_l885 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v169_l883)))


(def
 v172_l908
 (arrow-plot
  [{:label "a", :xy [2 1], :color "#999999"}
   {:label "b", :xy [1 3], :color "#2266cc"}
   {:label "proj", :xy [2 1], :color "#228833"}
   {:label "resid",
    :xy [-1 2],
    :color "#cc4422",
    :from [2 1],
    :dashed? true}]
  {}))


(def v174_l918 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v176_l926
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v178_l934 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t179_l936 (is (true? v178_l934)))


(def v181_l940 (def point3d (la/column [1 2 3])))


(def v182_l942 (def projected-pt (la/mmul P-proj point3d)))


(def v183_l944 projected-pt)


(def v185_l950 (def resid (la/sub point3d projected-pt)))


(def v186_l952 (la/mmul (la/transpose W-proj) resid))


(deftest t187_l954 (is ((fn [r] (< (la/norm r) 1.0E-10)) v186_l952)))


(def v189_l977 (def a-gs (la/column [1 1 0])))


(def v190_l978 (def b-gs (la/column [1 0 1])))


(def v192_l982 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v193_l984 q1-gs)


(def v195_l988 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v196_l991
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v198_l996
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v199_l999 q2-gs)


(def
 v201_l1003
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t202_l1007
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v201_l1003)))


(def v204_l1023 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v205_l1027 (def qr-result (la/qr A-qr)))


(def v207_l1031 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v208_l1032
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v209_l1033
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v211_l1037
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t212_l1039 (is ((fn [d] (< d 1.0E-10)) v211_l1037)))


(def v214_l1044 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t215_l1046 (is ((fn [d] (< d 1.0E-10)) v214_l1044)))


(def
 v217_l1072
 (arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v219_l1080 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v221_l1088 (def eig-result (la/eigen A-eig)))


(def v222_l1090 (sort (mapv first (:eigenvalues eig-result))))


(deftest
 t223_l1092
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v222_l1090)))


(def
 v225_l1103
 (every?
  (fn
   [i]
   (let
    [lam
     (first (nth (:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale lam ev)))
     1.0E-10)))
  (range 3)))


(deftest t226_l1111 (is (true? v225_l1103)))


(def v228_l1124 (def eig-reals (mapv first (:eigenvalues eig-result))))


(def
 v229_l1126
 (< (Math/abs (- (la/trace A-eig) (reduce + eig-reals))) 1.0E-10))


(deftest t230_l1128 (is (true? v229_l1126)))


(def
 v231_l1130
 (< (Math/abs (- (la/det A-eig) (reduce * eig-reals))) 1.0E-10))


(deftest t232_l1132 (is (true? v231_l1130)))


(def v234_l1161 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v236_l1167 (def eig-diag (la/eigen A-diag)))


(def
 v237_l1169
 (def
  P-diag
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by
     (fn [i] (first (nth (:eigenvalues eig-diag) i)))
     (range 2))]
   (la/matrix
    (mapv
     (fn [j] (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
     (range 2))))))


(def v239_l1180 (def P-cols (la/transpose P-diag)))


(def
 v241_l1184
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v242_l1187 D-result)


(deftest
 t243_l1189
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v242_l1187)))


(def v245_l1205 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v246_l1208
 (def
  A-diag-sq-via-eigen
  (let
   [Pinv
    (la/invert P-cols)
    D2
    (la/diag
     (dtype/make-reader
      :float64
      2
      (let [lam (tensor/mget D-result idx idx)] (* lam lam))))]
   (la/mmul P-cols (la/mmul D2 Pinv)))))


(def v247_l1215 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t248_l1217 (is (true? v247_l1215)))


(def v250_l1238 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v252_l1245 (la/close? S-sym (la/transpose S-sym)))


(deftest t253_l1247 (is (true? v252_l1245)))


(def v254_l1249 (def eig-S (la/eigen S-sym)))


(def
 v256_l1253
 (every? (fn [[_ im]] (< (Math/abs im) 1.0E-10)) (:eigenvalues eig-S)))


(deftest t257_l1256 (is (true? v256_l1253)))


(def
 v259_l1261
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v260_l1266 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v261_l1268 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t262_l1270 (is ((fn [d] (< d 1.0E-10)) v261_l1268)))


(def v264_l1309 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v265_l1313 (def svd-A (la/svd A-svd)))


(def v266_l1315 (vec (:S svd-A)))


(deftest
 t267_l1317
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v266_l1315)))


(def v269_l1343 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v270_l1347 (def svd-lr (la/svd A-lr)))


(def v271_l1349 (def sigmas (vec (:S svd-lr))))


(def v272_l1351 sigmas)


(def
 v274_l1355
 (def
  A-rank1
  (la/scale
   (first sigmas)
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all)))))


(def v276_l1362 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v277_l1364 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t278_l1366 (is (true? v277_l1364)))


(def v280_l1392 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v281_l1394
 (every? (fn [[re _]] (>= re -1.0E-10)) (:eigenvalues (la/eigen ATA))))


(deftest t282_l1397 (is (true? v281_l1394)))


(def
 v284_l1408
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v285_l1411 (def chol-L (la/cholesky spd-mat)))


(def
 v287_l1415
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t288_l1417 (is ((fn [d] (< d 1.0E-10)) v287_l1415)))


(def v290_l1422 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t291_l1424 (is (nil? v290_l1422)))


(def v293_l1436 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v295_l1446 (la/close? A-final (la/transpose A-final)))


(deftest t296_l1448 (is (true? v295_l1446)))


(def v298_l1452 (def eig-final (la/eigen A-final)))


(def
 v299_l1454
 (def final-eigenvalues (sort (mapv first (:eigenvalues eig-final)))))


(def v300_l1457 final-eigenvalues)


(deftest
 t301_l1459
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v300_l1457)))


(def
 v303_l1467
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t304_l1471 (is (true? v303_l1467)))


(def
 v306_l1475
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t307_l1479 (is (true? v306_l1475)))


(def v309_l1483 (def final-svd (la/svd A-final)))


(def
 v310_l1485
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t311_l1490 (is (true? v310_l1485)))


(def
 v313_l1494
 (count
  (filter
   (fn* [p1__46861#] (> p1__46861# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t314_l1496 (is ((fn [r] (= r 3)) v313_l1494)))


(def v315_l1499 (def A-inv (la/invert A-final)))


(def v316_l1501 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t317_l1503 (is (true? v316_l1501)))


(def v319_l1507 (def chol-final (la/cholesky A-final)))


(def
 v320_l1509
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t321_l1511 (is (true? v320_l1509)))
