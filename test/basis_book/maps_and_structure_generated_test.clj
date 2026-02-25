(ns
 basis-book.maps-and-structure-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
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


(def v5_l111 (def u (la/column [3 1])))


(def v6_l112 (def v (la/column [1 2])))


(def v8_l139 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v10_l145 (la/mmul R90 (la/column [1 0])))


(deftest
 t11_l147
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v10_l145)))


(def v13_l153 (la/mmul R90 (la/column [0 1])))


(deftest
 t14_l155
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v13_l153)))


(def
 v16_l162
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "Ru", :xy [-1 3], :color "#2266cc", :dashed? true}
   {:label "v", :xy [1 2], :color "#cc4422"}
   {:label "Rv", :xy [-2 1], :color "#cc4422", :dashed? true}]
  {}))


(def
 v18_l172
 (la/close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t19_l175 (is (true? v18_l172)))


(def
 v21_l179
 (la/close?
  (la/mmul R90 (la/scale 3.0 u))
  (la/scale 3.0 (la/mmul R90 u))))


(deftest t22_l182 (is (true? v21_l179)))


(def v24_l186 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v26_l193
 (let
  [angles
   (mapv
    (fn* [p1__72377#] (* 2.0 Math/PI (/ p1__72377# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__72378#] (Math/cos p1__72378#)) angles)
   circle-y
   (mapv (fn* [p1__72379#] (Math/sin p1__72379#)) angles)
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


(def v28_l218 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v30_l225 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t31_l227
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v30_l225)))


(def v33_l235 (la/det proj-xy))


(deftest t34_l237 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v33_l235)))


(def v36_l245 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v37_l249 (la/det shear-mat))


(deftest
 t38_l251
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v37_l249)))


(def
 v40_l257
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#2266cc"}
   {:label "e₂", :xy [0 1], :color "#cc4422"}
   {:label "Se₁", :xy [1 0], :color "#2266cc", :dashed? true}
   {:label "Se₂", :xy [2 1], :color "#cc4422", :dashed? true}]
  {}))


(def v42_l271 (def AB (la/mmul stretch-mat R90)))


(def v43_l272 (def BA (la/mmul R90 stretch-mat)))


(def v44_l274 (la/norm (la/sub AB BA)))


(deftest t45_l276 (is ((fn [d] (> d 0.1)) v44_l274)))


(def
 v47_l282
 (arrow-plot
  [{:label "e₁", :xy [1 0], :color "#999999"}
   {:label "R then S", :xy [0 1], :color "#2266cc"}
   {:label "S then R", :xy [0 3], :color "#cc4422"}]
  {:width 200}))


(def v49_l327 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v51_l335 (la/mmul M (la/column [1 1 -1])))


(deftest t52_l337 (is ((fn [r] (< (la/norm r) 1.0E-10)) v51_l335)))


(def v54_l348 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t55_l350 (is ((fn [r] (< (la/norm r) 1.0E-10)) v54_l348)))


(def v57_l365 (def sv-M (vec (:S (la/svd M)))))


(def v58_l367 sv-M)


(deftest t59_l369 (is ((fn [v] (= 3 (count v))) v58_l367)))


(def
 v60_l372
 (def
  rank-M
  (count (filter (fn* [p1__72380#] (> p1__72380# 1.0E-10)) sv-M))))


(def v61_l374 rank-M)


(deftest t62_l376 (is ((fn [r] (= r 2)) v61_l374)))


(def v64_l398 (def svd-M (la/svd M)))


(def
 v65_l400
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


(def v67_l408 (la/norm (la/mmul M null-basis)))


(deftest t68_l410 (is ((fn [d] (< d 1.0E-10)) v67_l408)))


(def v70_l424 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v71_l426
 (count
  (filter
   (fn* [p1__72381#] (> p1__72381# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t72_l428 (is ((fn [r] (= r 2)) v71_l426)))


(def v74_l433 (la/solve A-full (la/column [5 7])))


(deftest t75_l435 (is ((fn [x] (some? x)) v74_l433)))


(def v77_l440 (la/solve M (la/column [1 2 3])))


(deftest t78_l442 (is (nil? v77_l440)))


(def
 v80_l466
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


(def v82_l474 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t83_l476 (is ((fn [r] (< (la/norm r) 1.0E-10)) v82_l474)))
