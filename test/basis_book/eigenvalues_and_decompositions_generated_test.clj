(ns
 basis-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
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


(def
 v5_l132
 (arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v7_l140 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v9_l148 (def eig-result (la/eigen A-eig)))


(def v10_l150 (la/real-eigenvalues A-eig))


(deftest
 t11_l152
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v10_l150)))


(def
 v13_l163
 (every?
  (fn
   [i]
   (let
    [lam
     (cx/re ((:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale lam ev)))
     1.0E-10)))
  (range 3)))


(deftest t14_l171 (is (true? v13_l163)))


(def v16_l184 (def eig-reals (cx/re (:eigenvalues eig-result))))


(def
 v17_l186
 (< (Math/abs (- (la/trace A-eig) (dfn/sum eig-reals))) 1.0E-10))


(deftest t18_l188 (is (true? v17_l186)))


(def
 v19_l190
 (< (Math/abs (- (la/det A-eig) (reduce * (seq eig-reals)))) 1.0E-10))


(deftest t20_l192 (is (true? v19_l190)))


(def v22_l221 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v24_l227 (def eig-diag (la/eigen A-diag)))


(def
 v25_l229
 (def
  P-diag
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by (fn [i] (cx/re ((:eigenvalues eig-diag) i))) (range 2))]
   (la/matrix
    (mapv
     (fn [j] (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
     (range 2))))))


(def v27_l240 (def P-cols (la/transpose P-diag)))


(def
 v29_l244
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v30_l247 D-result)


(deftest
 t31_l249
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v30_l247)))


(def v33_l265 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v34_l268
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


(def v35_l275 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t36_l277 (is (true? v35_l275)))


(def v38_l298 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v40_l305 (la/close? S-sym (la/transpose S-sym)))


(deftest t41_l307 (is (true? v40_l305)))


(def v42_l309 (def eig-S (la/eigen S-sym)))


(def
 v44_l313
 (< (dfn/reduce-max (dfn/abs (cx/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t45_l315 (is (true? v44_l313)))


(def
 v47_l320
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v48_l325 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v49_l327 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t50_l329 (is ((fn [d] (< d 1.0E-10)) v49_l327)))


(def v52_l368 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v53_l372 (def svd-A (la/svd A-svd)))


(def v54_l374 (vec (:S svd-A)))


(deftest
 t55_l376
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v54_l374)))


(def v57_l402 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v58_l406 (def svd-lr (la/svd A-lr)))


(def v59_l408 (def sigmas (vec (:S svd-lr))))


(def v60_l410 sigmas)


(def
 v62_l414
 (def
  A-rank1
  (la/scale
   (first sigmas)
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all)))))


(def v64_l421 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v65_l423 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t66_l425 (is (true? v65_l423)))


(def v68_l451 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v69_l453
 (every?
  (fn* [p1__79902#] (>= p1__79902# -1.0E-10))
  (cx/re (:eigenvalues (la/eigen ATA)))))


(deftest t70_l455 (is (true? v69_l453)))


(def
 v72_l466
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v73_l469 (def chol-L (la/cholesky spd-mat)))


(def
 v75_l473
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t76_l475 (is ((fn [d] (< d 1.0E-10)) v75_l473)))


(def v78_l480 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t79_l482 (is (nil? v78_l480)))


(def v81_l494 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v83_l504 (la/close? A-final (la/transpose A-final)))


(deftest t84_l506 (is (true? v83_l504)))


(def v86_l510 (def eig-final (la/eigen A-final)))


(def v87_l512 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v88_l515 final-eigenvalues)


(deftest
 t89_l517
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v88_l515)))


(def
 v91_l525
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t92_l529 (is (true? v91_l525)))


(def
 v94_l533
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t95_l537 (is (true? v94_l533)))


(def v97_l541 (def final-svd (la/svd A-final)))


(def
 v98_l543
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t99_l548 (is (true? v98_l543)))


(def
 v101_l552
 (count
  (filter
   (fn* [p1__79903#] (> p1__79903# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t102_l554 (is ((fn [r] (= r 3)) v101_l552)))


(def v103_l557 (def A-inv (la/invert A-final)))


(def v104_l559 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t105_l561 (is (true? v104_l559)))


(def v107_l565 (def chol-final (la/cholesky A-final)))


(def
 v108_l567
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t109_l569 (is (true? v108_l567)))
