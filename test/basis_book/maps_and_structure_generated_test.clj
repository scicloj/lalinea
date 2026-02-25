(ns
 basis-book.maps-and-structure-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l31
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


(def v5_l113 (def u (la/column [3 1])))


(def v6_l114 (def v (la/column [1 2])))


(def v8_l141 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v10_l147 (la/mmul R90 (la/column [1 0])))


(deftest
 t11_l149
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v10_l147)))


(def v13_l155 (la/mmul R90 (la/column [0 1])))


(deftest
 t14_l157
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v13_l155)))


(def
 v16_l164
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v18_l174
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t19_l177 (is (true? v18_l174)))


(def
 v21_l181
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t22_l184 (is (true? v21_l181)))


(def v24_l188 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v26_l195
 (let
  [angles
   (mapv
    (fn* [p1__96357#] (* 2.0 Math/PI (/ p1__96357# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__96358#] (Math/cos p1__96358#)) angles)
   circle-y
   (mapv (fn* [p1__96359#] (Math/sin p1__96359#)) angles)
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


(def v28_l220 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v30_l227 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t31_l229
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v30_l227)))


(def v33_l237 (la/det proj-xy))


(deftest t34_l239 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v33_l237)))


(def v36_l247 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v37_l251 (la/det shear-mat))


(deftest
 t38_l253
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v37_l251)))


(def
 v40_l259
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v42_l273 (def AB (la/mmul stretch-mat R90)))


(def v43_l274 (def BA (la/mmul R90 stretch-mat)))


(def v44_l276 (la/norm (la/sub AB BA)))


(deftest t45_l278 (is ((fn [d] (> d 0.1)) v44_l276)))


(def
 v47_l284
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v49_l329 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v51_l337 (la/mmul M (la/column [1 1 -1])))


(deftest t52_l339 (is ((fn [r] (< (la/norm r) 1.0E-10)) v51_l337)))


(def v54_l350 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t55_l352 (is ((fn [r] (< (la/norm r) 1.0E-10)) v54_l350)))


(def v57_l367 (def sv-M (vec (:S (la/svd M)))))


(def v58_l369 sv-M)


(deftest t59_l371 (is ((fn [v] (= 3 (count v))) v58_l369)))


(def
 v60_l374
 (def
  rank-M
  (count (filter (fn* [p1__96360#] (> p1__96360# 1.0E-10)) sv-M))))


(def v61_l376 rank-M)


(deftest t62_l378 (is ((fn [r] (= r 2)) v61_l376)))


(def v64_l400 (def svd-M (la/svd M)))


(def
 v65_l402
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


(def v67_l410 (la/norm (la/mmul M null-basis)))


(deftest t68_l412 (is ((fn [d] (< d 1.0E-10)) v67_l410)))


(def v70_l426 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v71_l428
 (count
  (filter
   (fn* [p1__96361#] (> p1__96361# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t72_l430 (is ((fn [r] (= r 2)) v71_l428)))


(def v74_l435 (la/solve A-full (la/column [5 7])))


(deftest t75_l437 (is ((fn [x] (some? x)) v74_l435)))


(def v77_l442 (la/solve M (la/column [1 2 3])))


(deftest t78_l444 (is (nil? v77_l442)))


(def
 v80_l469
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
 v82_l477
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
 v84_l486
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
 v86_l496
 {:col-space (second (dtype/shape col-space-basis)),
  :left-null (second (dtype/shape left-null-basis)),
  :row-space (second (dtype/shape row-space-basis)),
  :null-space (second (dtype/shape null-basis))})


(deftest
 t87_l501
 (is
  ((fn
    [m]
    (and
     (= 2 (:col-space m))
     (= 1 (:left-null m))
     (= 2 (:row-space m))
     (= 1 (:null-space m))))
   v86_l496)))


(def v89_l515 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t90_l517 (is ((fn [r] (< (la/norm r) 1.0E-10)) v89_l515)))


(def v92_l523 (la/mmul (la/transpose col-space-basis) left-null-basis))


(deftest t93_l525 (is ((fn [r] (< (la/norm r) 1.0E-10)) v92_l523)))
