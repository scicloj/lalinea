(ns
 basis-book.spectral-graph-theory-generated-test
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
 v3_l34
 (def
  row-sums
  (fn
   [m]
   (let
    [nrows (first (dtype/shape m))]
    (dtype/make-reader
     :float64
     nrows
     (dfn/sum (tensor/select m idx :all)))))))


(def
 v5_l42
 (def laplacian (fn [adj] (la/sub (la/diag (row-sums adj)) adj))))


(def
 v7_l48
 (def
  sorted-real-eigenvalues
  (fn [eig] (sort (mapv first (:eigenvalues eig))))))


(def
 v9_l57
 (def
  graph-plot
  (fn
   [positions edges opts]
   (let
    [width
     (or (:width opts) 300)
     n
     (count positions)
     labels
     (or (:labels opts) (mapv str (range n)))
     node-colors
     (or (:node-colors opts) (vec (repeat n "#2266cc")))
     edge-hl
     (or (:edge-highlight opts) #{})
     xs
     (mapv first positions)
     ys
     (mapv second positions)
     x-min
     (apply min xs)
     x-max
     (apply max xs)
     y-min
     (apply min ys)
     y-max
     (apply max ys)
     dx
     (- x-max x-min)
     dy
     (- y-max y-min)
     span
     (max dx dy 1.0)
     pad
     (* 0.4 span)
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
     r
     (* 0.22 span (min 1.0 (/ 3.0 (max n 1))))
     stroke-w
     (/ 1.5 px-per-unit)
     font-size
     (* 0.7 r)
     edge-elts
     (mapv
      (fn
       [[i j]]
       (let
        [[x1 y1]
         (positions i)
         [x2 y2]
         (positions j)
         hl?
         (or (edge-hl [i j]) (edge-hl [j i]))]
        [:line
         {:x1 x1,
          :y1 (- y1),
          :x2 x2,
          :y2 (- y2),
          :stroke (if hl? "#cc4422" "#999"),
          :stroke-width (if hl? (* 3 stroke-w) (* 1.5 stroke-w))}]))
      edges)
     node-elts
     (mapcat
      (fn
       [i]
       (let
        [[cx cy] (positions i)]
        [[:circle
          {:cx cx,
           :cy (- cy),
           :r r,
           :fill (node-colors i),
           :stroke "#333",
           :stroke-width stroke-w}]
         [:text
          {:x cx,
           :y (- cy),
           :fill "white",
           :font-size font-size,
           :font-family "sans-serif",
           :font-weight "bold",
           :text-anchor "middle",
           :dominant-baseline "central"}
          (labels i)]]))
      (range n))]
    (kind/hiccup
     (into
      [:svg
       {:width width,
        :height height,
        :viewBox (str vb-x " " (- vb-y-top) " " vb-w " " vb-h)}]
      (concat edge-elts node-elts)))))))


(def
 v11_l115
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v13_l126 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def
 v14_l129
 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def v15_l132 (graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v17_l137 (vec (row-sums adj)))


(deftest
 t18_l139
 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v17_l137)))


(def v20_l155 (def L (laplacian adj)))


(def v21_l157 L)


(deftest
 t22_l159
 (is
  ((fn
    [m]
    (and
     (= [6 6] (vec (dtype/shape m)))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v21_l157)))


(def v24_l174 (dfn/reduce-max (dfn/abs (row-sums L))))


(deftest t25_l176 (is ((fn [v] (< v 1.0E-10)) v24_l174)))


(def v27_l184 (def eig (la/eigen L)))


(def v28_l186 (def eigenvalues (sorted-real-eigenvalues eig)))


(def v29_l188 eigenvalues)


(deftest
 t30_l190
 (is
  ((fn [v] (and (< (Math/abs (first v)) 1.0E-10) (= 6 (count v))))
   v29_l188)))


(def v32_l203 (def fiedler-value (second eigenvalues)))


(def v33_l205 fiedler-value)


(deftest t34_l207 (is ((fn [v] (and (pos? v) (< v 2.0))) v33_l205)))


(def
 v36_l219
 (def
  sorted-eig-indices
  (let
   [vals (mapv first (:eigenvalues eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v37_l223
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v39_l228
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v40_l231 fiedler-entries)


(deftest t41_l233 (is ((fn [v] (= 6 (count v))) v40_l231)))


(def
 v43_l239
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v44_l242 cluster-assignment)


(deftest
 t45_l244
 (is
  ((fn
    [v]
    (let
     [freqs (frequencies v)]
     (and (= 3 (freqs :A)) (= 3 (freqs :B)))))
   v44_l242)))


(def
 v47_l254
 (graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v49_l262
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v51_l276
 (def
  adj-disconnected
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 0 0 0]
    [0 0 0 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def
 v52_l284
 (graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v53_l289
 (def
  disc-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian adj-disconnected)))))


(def v54_l292 disc-eigenvalues)


(deftest
 t55_l294
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (Math/abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v54_l292)))


(def v57_l308 (def kn 5))


(def
 v58_l310
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v59_l315
 (graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v60_l319
 (def
  K5-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian K5-adj)))))


(def v61_l322 K5-eigenvalues)


(deftest
 t62_l324
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (every? (fn [x] (< (Math/abs (- x 5.0)) 1.0E-10)) (rest v))))
   v61_l322)))


(def v64_l341 (def cn 8))


(def
 v65_l343
 (def
  cycle-adj
  (tensor/compute-tensor
   [cn cn]
   (fn
    [i j]
    (if
     (or (= j (mod (inc i) cn)) (= j (mod (+ i (dec cn)) cn)))
     1.0
     0.0))
   :float64)))


(def
 v66_l350
 (graph-plot
  [[0.0 1.0]
   [-0.707 0.707]
   [-1.0 0.0]
   [-0.707 -0.707]
   [-0.0 -1.0]
   [0.707 -0.707]
   [1.0 -0.0]
   [0.707 0.707]]
  [[0 1] [1 2] [2 3] [3 4] [4 5] [5 6] [6 7] [7 0]]
  {}))


(def
 v67_l354
 (def
  cycle-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian cycle-adj)))))


(def
 v68_l357
 (def
  cycle-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* 2.0 Math/PI k) cn)))))
    (range cn)))))


(def v70_l363 cycle-eigenvalues)


(def v72_l367 cycle-theoretical)


(def
 v74_l371
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array cycle-eigenvalues)
     (double-array cycle-theoretical))))
  1.0E-10))


(deftest t75_l376 (is (true? v74_l371)))


(def v77_l384 (def pn 6))


(def
 v78_l386
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (Math/abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v79_l391
 (graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v80_l395
 (def
  path-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian path-adj)))))


(def
 v81_l398
 (def
  path-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* Math/PI k) pn)))))
    (range pn)))))


(def
 v82_l402
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array path-eigenvalues)
     (double-array path-theoretical))))
  1.0E-10))


(deftest t83_l407 (is (true? v82_l402)))


(def
 v85_l417
 (def
  community-adj
  (la/matrix
   [[0 1 1 0 0 0 0 0 0]
    [1 0 1 0 0 0 0 0 0]
    [1 1 0 1 0 0 0 0 0]
    [0 0 1 0 1 1 0 0 0]
    [0 0 0 1 0 1 0 0 0]
    [0 0 0 1 1 0 1 0 0]
    [0 0 0 0 0 1 0 1 1]
    [0 0 0 0 0 0 1 0 1]
    [0 0 0 0 0 0 1 1 0]])))


(def
 v86_l432
 (graph-plot
  [[0 0.8]
   [0 -0.8]
   [1.5 0]
   [3.5 0]
   [5 0.8]
   [5 -0.8]
   [6.5 0]
   [8 0.8]
   [8 -0.8]]
  [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5] [5 6] [6 7] [6 8] [7 8]]
  {:node-colors
   ["#2266cc"
    "#2266cc"
    "#2266cc"
    "#228833"
    "#228833"
    "#228833"
    "#dd8800"
    "#dd8800"
    "#dd8800"],
   :edge-highlight #{[2 3] [5 6]},
   :width 400}))


(def v87_l446 (def comm-eig (la/eigen (laplacian community-adj))))


(def v89_l450 (def comm-eigenvalues (sorted-real-eigenvalues comm-eig)))


(def v90_l452 (take 4 comm-eigenvalues))


(deftest
 t91_l454
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (second v) 1.5)
     (< (nth v 2) 1.5)
     (> (nth v 3) 1.5)))
   v90_l452)))


(def
 v93_l467
 (def
  sorted-comm-indices
  (let
   [vals (mapv first (:eigenvalues comm-eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v95_l474
 (def
  embed-data
  (let
   [ev2
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 1))
    ev3
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 2))]
   (tc/dataset
    {:vertex (range 9),
     :x (vec (dtype/->reader ev2)),
     :y (vec (dtype/->reader ev3)),
     :community
     (mapv
      (fn*
       [p1__91805#]
       (cond (<= p1__91805# 2) "A" (<= p1__91805# 5) "B" :else "C"))
      (range 9))}))))


(def
 v96_l485
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def v98_l507 (def conductance (/ 1.0 3.0)))


(def
 v99_l509
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (Math/sqrt (* 2.0 fiedler-value)))))


(deftest t100_l512 (is (true? v99_l509)))
