(ns
 basis-book.inner-products-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.datatype :as dtype]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l25
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


(def v5_l115 (def a3 (la/column [1 2 3])))


(def v6_l116 (def b3 (la/column [4 5 6])))


(def v7_l118 (def dot-ab (dfn/sum (dfn/* a3 b3))))


(def v8_l121 dot-ab)


(deftest
 t9_l123
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v8_l121)))


(def v11_l132 (la/norm a3))


(deftest
 t12_l134
 (is ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v11_l132)))


(def v14_l140 (dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1]))))


(deftest t15_l142 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v14_l140)))


(def v17_l159 (def p (la/column [1 0])))


(def v18_l160 (def q (la/column [1 1])))


(def
 v19_l162
 (def cos-theta (/ (dfn/sum (dfn/* p q)) (* (la/norm p) (la/norm q)))))


(def v20_l166 cos-theta)


(deftest
 t21_l168
 (is
  ((fn [c] (< (Math/abs (- c (/ 1.0 (Math/sqrt 2.0)))) 1.0E-10))
   v20_l166)))


(def v23_l173 (Math/toDegrees (Math/acos cos-theta)))


(deftest
 t24_l175
 (is ((fn [d] (< (Math/abs (- d 45.0)) 1.0E-10)) v23_l173)))


(def
 v26_l194
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


(def v28_l204 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v30_l212
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v32_l220 (la/close? (la/mmul P-proj P-proj) P-proj))


(deftest t33_l222 (is (true? v32_l220)))


(def v35_l226 (def point3d (la/column [1 2 3])))


(def v36_l228 (def projected-pt (la/mmul P-proj point3d)))


(def v37_l230 projected-pt)


(def v39_l236 (def resid (la/sub point3d projected-pt)))


(def v40_l238 (la/mmul (la/transpose W-proj) resid))


(deftest t41_l240 (is ((fn [r] (< (la/norm r) 1.0E-10)) v40_l238)))


(def v43_l263 (def a-gs (la/column [1 1 0])))


(def v44_l264 (def b-gs (la/column [1 0 1])))


(def v46_l268 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v47_l270 q1-gs)


(def v49_l274 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v50_l277
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v52_l282
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v53_l285 q2-gs)


(def
 v55_l289
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t56_l293
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v55_l289)))


(def v58_l309 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v59_l313 (def qr-result (la/qr A-qr)))


(def v61_l317 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v62_l318
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v63_l319
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v65_l323
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t66_l325 (is ((fn [d] (< d 1.0E-10)) v65_l323)))


(def v68_l330 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t69_l332 (is ((fn [d] (< d 1.0E-10)) v68_l330)))
