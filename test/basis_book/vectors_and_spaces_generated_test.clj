(ns
 basis-book.vectors-and-spaces-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l27
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


(def v5_l115 (def u (la/column [3 1])))


(def v6_l116 (def v (la/column [1 2])))


(def
 v8_l121
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422"}]
  {:width 300}))


(def v10_l131 (la/add u v))


(deftest
 t11_l133
 (is
  ((fn
    [r]
    (and (= 4.0 (tensor/mget r 0 0)) (= 3.0 (tensor/mget r 1 0))))
   v10_l131)))


(def
 v12_l137
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "v", :xy [1 2], :color "#cc4422", :from [3 1]}
   {:label "u+v", :xy [4 3], :color "#228833", :dashed? true}]
  {:width 300}))


(def v14_l147 (la/scale 2.0 u))


(deftest
 t15_l149
 (is
  ((fn
    [r]
    (and (= 6.0 (tensor/mget r 0 0)) (= 2.0 (tensor/mget r 1 0))))
   v14_l147)))


(def
 v16_l153
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "2u", :xy [6 2], :color "#8844cc"}]
  {:width 300}))


(def v18_l159 (la/scale -1.0 u))


(deftest
 t19_l161
 (is
  ((fn
    [r]
    (and (= -3.0 (tensor/mget r 0 0)) (= -1.0 (tensor/mget r 1 0))))
   v18_l159)))


(def
 v20_l165
 (arrow-plot
  [{:label "u", :xy [3 1], :color "#2266cc"}
   {:label "−u", :xy [-3 -1], :color "#cc4422"}]
  {:width 300}))


(def v22_l199 (def w-ax (la/column [-1 4])))


(def v23_l200 (def zero2 (la/column [0 0])))


(def v25_l204 (la/close? (la/add u v) (la/add v u)))


(deftest t26_l206 (is (true? v25_l204)))


(def
 v28_l210
 (la/close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t29_l213 (is (true? v28_l210)))


(def v31_l217 (la/close? (la/add u zero2) u))


(deftest t32_l219 (is (true? v31_l217)))


(def v34_l223 (la/close? (la/add u (la/scale -1.0 u)) zero2))


(deftest t35_l225 (is (true? v34_l223)))


(def
 v37_l229
 (la/close? (la/scale 2.0 (la/scale 3.0 u)) (la/scale 6.0 u)))


(deftest t38_l232 (is (true? v37_l229)))


(def v40_l236 (la/close? (la/scale 1.0 u) u))


(deftest t41_l238 (is (true? v40_l236)))


(def
 v43_l242
 (la/close?
  (la/scale 5.0 (la/add u v))
  (la/add (la/scale 5.0 u) (la/scale 5.0 v))))


(deftest t44_l245 (is (true? v43_l242)))


(def
 v46_l249
 (la/close?
  (la/scale (+ 2.0 3.0) u)
  (la/add (la/scale 2.0 u) (la/scale 3.0 u))))


(deftest t47_l252 (is (true? v46_l249)))


(def v49_l277 (la/add (la/scale 2.0 u) (la/scale -1.0 v)))


(deftest
 t50_l279
 (is
  ((fn
    [r]
    (and (= 5.0 (tensor/mget r 0 0)) (= 0.0 (tensor/mget r 1 0))))
   v49_l277)))


(def
 v52_l288
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
 v54_l303
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
 v56_l319
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
 v58_l358
 (arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[1,2]", :xy [1 2], :color "#cc4422"}]
  {:width 250}))


(def v60_l367 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t61_l370 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v60_l367)))


(def
 v63_l375
 (arrow-plot
  [{:label "[3,1]", :xy [3 1], :color "#2266cc"}
   {:label "[6,2]", :xy [6 2], :color "#cc4422"}]
  {:width 250}))


(def v65_l383 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t66_l386 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v65_l383)))


(def v68_l392 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t69_l396
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v68_l392)))


(def v71_l402 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t72_l406 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v71_l402)))


(def v74_l426 (def e1 (la/column [1 0 0])))


(def v75_l427 (def e2 (la/column [0 1 0])))


(def v76_l428 (def e3 (la/column [0 0 1])))


(def v78_l438 (def w (la/column [5 -3 7])))


(def
 v80_l442
 (la/close?
  w
  (la/add
   (la/scale 5.0 e1)
   (la/add (la/scale -3.0 e2) (la/scale 7.0 e3)))))


(deftest t81_l447 (is (true? v80_l442)))
