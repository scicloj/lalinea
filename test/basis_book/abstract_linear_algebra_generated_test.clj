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
 v52_l308
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
 v54_l324
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


(def v56_l361 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t57_l364 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v56_l361)))


(def v59_l369 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t60_l372 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v59_l369)))


(def v62_l378 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t63_l382
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v62_l378)))


(def v65_l388 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t66_l392 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v65_l388)))


(def v68_l412 (def e1 (la/column [1 0 0])))


(def v69_l413 (def e2 (la/column [0 1 0])))


(def v70_l414 (def e3 (la/column [0 0 1])))


(def v72_l424 (def w (la/column [5 -3 7])))


(def
 v74_l428
 (la/close?
  w
  (la/add
   (la/scale 5.0 e1)
   (la/add (la/scale -3.0 e2) (la/scale 7.0 e3)))))


(deftest t75_l433 (is (true? v74_l428)))


(def v77_l477 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v79_l483 (la/mmul R90 (la/column [1 0])))


(deftest
 t80_l485
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v79_l483)))


(def v82_l491 (la/mmul R90 (la/column [0 1])))


(deftest
 t83_l493
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v82_l491)))


(def
 v85_l501
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t86_l504 (is (true? v85_l501)))


(def
 v88_l508
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t89_l511 (is (true? v88_l508)))


(def v91_l515 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v93_l522
 (let
  [angles
   (mapv
    (fn* [p1__247917#] (* 2.0 Math/PI (/ p1__247917# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__247918#] (Math/cos p1__247918#)) angles)
   circle-y
   (mapv (fn* [p1__247919#] (Math/sin p1__247919#)) angles)
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


(def v95_l547 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v97_l554 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t98_l556
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v97_l554)))


(def v100_l564 (la/det proj-xy))


(deftest t101_l566 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v100_l564)))


(def v103_l574 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v104_l578 (la/det shear-mat))


(deftest
 t105_l580
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v104_l578)))


(def v107_l591 (def AB (la/mmul stretch-mat R90)))


(def v108_l592 (def BA (la/mmul R90 stretch-mat)))


(def v109_l594 (la/norm (la/sub AB BA)))


(deftest t110_l596 (is ((fn [d] (> d 0.1)) v109_l594)))


(def v112_l639 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v114_l647 (la/mmul M (la/column [1 1 -1])))


(deftest t115_l649 (is ((fn [r] (< (la/norm r) 1.0E-10)) v114_l647)))


(def v117_l660 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t118_l662 (is ((fn [r] (< (la/norm r) 1.0E-10)) v117_l660)))


(def v120_l677 (def sv-M (vec (:S (la/svd M)))))


(def v121_l679 sv-M)


(deftest t122_l681 (is ((fn [v] (= 3 (count v))) v121_l679)))


(def
 v123_l684
 (def
  rank-M
  (count (filter (fn* [p1__247920#] (> p1__247920# 1.0E-10)) sv-M))))


(def v124_l686 rank-M)


(deftest t125_l688 (is ((fn [r] (= r 2)) v124_l686)))


(def v127_l710 (def svd-M (la/svd M)))


(def
 v128_l712
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


(def v130_l720 (la/norm (la/mmul M null-basis)))


(deftest t131_l722 (is ((fn [d] (< d 1.0E-10)) v130_l720)))


(def v133_l736 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v134_l738
 (count
  (filter
   (fn* [p1__247921#] (> p1__247921# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t135_l740 (is ((fn [r] (= r 2)) v134_l738)))


(def v137_l745 (la/solve A-full (la/column [5 7])))


(deftest t138_l747 (is ((fn [x] (some? x)) v137_l745)))


(def v140_l752 (la/solve M (la/column [1 2 3])))


(deftest t141_l754 (is (nil? v140_l752)))


(def
 v143_l778
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


(def v145_l786 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t146_l788 (is ((fn [r] (< (la/norm r) 1.0E-10)) v145_l786)))


(def v148_l806 (def a3 (la/column [1 2 3])))


(def v149_l807 (def b3 (la/column [4 5 6])))


(def v150_l809 (def dot-ab (dfn/sum (dfn/* a3 b3))))


(def v151_l812 dot-ab)


(deftest
 t152_l814
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v151_l812)))


(def v154_l823 (la/norm a3))


(deftest
 t155_l825
 (is
  ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v154_l823)))


(def v157_l831 (dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1]))))


(deftest t158_l833 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v157_l831)))


(def v160_l852 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v162_l860
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v164_l868 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t165_l870 (is (true? v164_l868)))


(def v167_l874 (def point3d (la/column [1 2 3])))


(def v168_l876 (def projected-pt (la/mmul P-proj point3d)))


(def v169_l878 projected-pt)


(def v171_l884 (def resid (la/sub point3d projected-pt)))


(def v172_l886 (la/mmul (la/transpose W-proj) resid))


(deftest t173_l888 (is ((fn [r] (< (la/norm r) 1.0E-10)) v172_l886)))


(def v175_l911 (def a-gs (la/column [1 1 0])))


(def v176_l912 (def b-gs (la/column [1 0 1])))


(def v178_l916 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v179_l918 q1-gs)


(def v181_l922 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v182_l925
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v184_l930
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v185_l933 q2-gs)


(def
 v187_l937
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t188_l941
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v187_l937)))


(def v190_l957 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v191_l961 (def qr-result (la/qr A-qr)))


(def v193_l965 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v194_l966
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v195_l967
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v197_l971
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t198_l973 (is ((fn [d] (< d 1.0E-10)) v197_l971)))


(def v200_l978 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t201_l980 (is ((fn [d] (< d 1.0E-10)) v200_l978)))


(def v203_l1003 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v205_l1011 (def eig-result (la/eigen A-eig)))


(def v206_l1013 (sort (mapv first (:eigenvalues eig-result))))


(deftest
 t207_l1015
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v206_l1013)))


(def
 v209_l1026
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


(deftest t210_l1034 (is (true? v209_l1026)))


(def v212_l1047 (def eig-reals (mapv first (:eigenvalues eig-result))))


(def
 v213_l1049
 (< (Math/abs (- (la/trace A-eig) (reduce + eig-reals))) 1.0E-10))


(deftest t214_l1051 (is (true? v213_l1049)))


(def
 v215_l1053
 (< (Math/abs (- (la/det A-eig) (reduce * eig-reals))) 1.0E-10))


(deftest t216_l1055 (is (true? v215_l1053)))


(def v218_l1084 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v220_l1090 (def eig-diag (la/eigen A-diag)))


(def
 v221_l1092
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


(def v223_l1103 (def P-cols (la/transpose P-diag)))


(def
 v225_l1107
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v226_l1110 D-result)


(deftest
 t227_l1112
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v226_l1110)))


(def v229_l1128 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v230_l1131
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


(def v231_l1138 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t232_l1140 (is (true? v231_l1138)))


(def v234_l1161 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v236_l1168 (la/close? S-sym (la/transpose S-sym)))


(deftest t237_l1170 (is (true? v236_l1168)))


(def v238_l1172 (def eig-S (la/eigen S-sym)))


(def
 v240_l1176
 (every? (fn [[_ im]] (< (Math/abs im) 1.0E-10)) (:eigenvalues eig-S)))


(deftest t241_l1179 (is (true? v240_l1176)))


(def
 v243_l1184
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v244_l1189 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v245_l1191 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t246_l1193 (is ((fn [d] (< d 1.0E-10)) v245_l1191)))


(def v248_l1232 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v249_l1236 (def svd-A (la/svd A-svd)))


(def v250_l1238 (vec (:S svd-A)))


(deftest
 t251_l1240
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v250_l1238)))


(def v253_l1266 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v254_l1270 (def svd-lr (la/svd A-lr)))


(def v255_l1272 (def sigmas (vec (:S svd-lr))))


(def v256_l1274 sigmas)


(def
 v258_l1278
 (def
  A-rank1
  (la/scale
   (first sigmas)
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all)))))


(def v260_l1285 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v261_l1287 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t262_l1289 (is (true? v261_l1287)))


(def v264_l1315 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v265_l1317
 (every? (fn [[re _]] (>= re -1.0E-10)) (:eigenvalues (la/eigen ATA))))


(deftest t266_l1320 (is (true? v265_l1317)))


(def
 v268_l1331
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v269_l1334 (def chol-L (la/cholesky spd-mat)))


(def
 v271_l1338
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t272_l1340 (is ((fn [d] (< d 1.0E-10)) v271_l1338)))


(def v274_l1345 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t275_l1347 (is (nil? v274_l1345)))


(def v277_l1359 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v279_l1369 (la/close? A-final (la/transpose A-final)))


(deftest t280_l1371 (is (true? v279_l1369)))


(def v282_l1375 (def eig-final (la/eigen A-final)))


(def
 v283_l1377
 (def final-eigenvalues (sort (mapv first (:eigenvalues eig-final)))))


(def v284_l1380 final-eigenvalues)


(deftest
 t285_l1382
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v284_l1380)))


(def
 v287_l1390
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t288_l1394 (is (true? v287_l1390)))


(def
 v290_l1398
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t291_l1402 (is (true? v290_l1398)))


(def v293_l1406 (def final-svd (la/svd A-final)))


(def
 v294_l1408
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t295_l1413 (is (true? v294_l1408)))


(def
 v297_l1417
 (count
  (filter
   (fn* [p1__247922#] (> p1__247922# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t298_l1419 (is ((fn [r] (= r 3)) v297_l1417)))


(def v299_l1422 (def A-inv (la/invert A-final)))


(def v300_l1424 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t301_l1426 (is (true? v300_l1424)))


(def v303_l1430 (def chol-final (la/cholesky A-final)))


(def
 v304_l1432
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t305_l1434 (is (true? v304_l1432)))
