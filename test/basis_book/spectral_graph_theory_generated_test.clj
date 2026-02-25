(ns
 basis-book.spectral-graph-theory-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l36
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
 v5_l44
 (def laplacian (fn [adj] (la/sub (la/diag (row-sums adj)) adj))))


(def
 v7_l53
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
 v9_l111
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v11_l122 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def
 v12_l125
 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def v13_l128 (graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v15_l133 (vec (row-sums adj)))


(deftest
 t16_l135
 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v15_l133)))


(def v18_l151 (def L (laplacian adj)))


(def v19_l153 L)


(deftest
 t20_l155
 (is
  ((fn
    [m]
    (and
     (= [6 6] (vec (dtype/shape m)))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v19_l153)))


(def v22_l170 (dfn/reduce-max (dfn/abs (row-sums L))))


(deftest t23_l172 (is ((fn [v] (< v 1.0E-10)) v22_l170)))


(def v25_l187 (def eig (la/eigen L)))


(def v26_l189 (def eigenvalues (la/real-eigenvalues (laplacian adj))))


(def v27_l191 eigenvalues)


(deftest
 t28_l193
 (is
  ((fn [v] (and (< (Math/abs (first v)) 1.0E-10) (= 6 (count v))))
   v27_l191)))


(def v30_l206 (def fiedler-value (second eigenvalues)))


(def v31_l208 fiedler-value)


(deftest t32_l210 (is ((fn [v] (and (pos? v) (< v 2.0))) v31_l208)))


(def
 v34_l222
 (def
  sorted-eig-indices
  (let
   [vals (cx/re (:eigenvalues eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues eig)))))))


(def
 v35_l226
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v37_l231
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v38_l234 fiedler-entries)


(deftest t39_l236 (is ((fn [v] (= 6 (count v))) v38_l234)))


(def
 v41_l242
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v42_l245 cluster-assignment)


(deftest
 t43_l247
 (is
  ((fn
    [v]
    (and
     (apply = (subvec v 0 3))
     (apply = (subvec v 3 6))
     (not= (v 0) (v 3))))
   v42_l245)))


(def
 v45_l257
 (graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v47_l265
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v49_l279
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
 v50_l287
 (graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v51_l292
 (def
  disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected))))


(def v52_l295 disc-eigenvalues)


(deftest
 t53_l297
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (Math/abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v52_l295)))


(def v55_l311 (def kn 5))


(def
 v56_l313
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v57_l318
 (graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v58_l322
 (def K5-eigenvalues (la/real-eigenvalues (laplacian K5-adj))))


(def v59_l325 K5-eigenvalues)


(deftest
 t60_l327
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (every? (fn [x] (< (Math/abs (- x 5.0)) 1.0E-10)) (rest v))))
   v59_l325)))


(def v62_l344 (def cn 8))


(def
 v63_l346
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
 v64_l353
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
 v65_l357
 (def cycle-eigenvalues (la/real-eigenvalues (laplacian cycle-adj))))


(def
 v66_l360
 (def
  cycle-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* 2.0 Math/PI k) cn)))))
    (range cn)))))


(def v68_l366 cycle-eigenvalues)


(def v70_l370 cycle-theoretical)


(def
 v72_l374
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array cycle-eigenvalues)
     (double-array cycle-theoretical))))
  1.0E-10))


(deftest t73_l379 (is (true? v72_l374)))


(def v75_l387 (def pn 6))


(def
 v76_l389
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (Math/abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v77_l394
 (graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v78_l398
 (def path-eigenvalues (la/real-eigenvalues (laplacian path-adj))))


(def
 v79_l401
 (def
  path-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* Math/PI k) pn)))))
    (range pn)))))


(def
 v80_l405
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array path-eigenvalues)
     (double-array path-theoretical))))
  1.0E-10))


(deftest t81_l410 (is (true? v80_l405)))


(def
 v83_l420
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
 v84_l435
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


(def v85_l449 (def comm-eig (la/eigen (laplacian community-adj))))


(def
 v87_l453
 (def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj))))


(def v88_l455 comm-eigenvalues)


(deftest
 t89_l457
 (is
  ((fn
    [v]
    (and
     (= 9 (count v))
     (< (Math/abs (first v)) 1.0E-10)
     (< (nth v 2) 1.0)
     (> (nth v 3) (* 2.0 (nth v 2)))))
   v88_l455)))


(def
 v90_l464
 (->
  (tc/dataset
   {:index (range (count comm-eigenvalues)),
    :eigenvalue comm-eigenvalues})
  (plotly/base {:=x :index, :=y :eigenvalue})
  (plotly/layer-bar)
  plotly/plot))


(def
 v92_l476
 (def
  sorted-comm-indices
  (let
   [vals (cx/re (:eigenvalues comm-eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues comm-eig)))))))


(def
 v94_l483
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
       [p1__80728#]
       (cond (<= p1__80728# 2) "A" (<= p1__80728# 5) "B" :else "C"))
      (range 9))}))))


(def
 v95_l494
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def v97_l516 (def conductance (/ 1.0 3.0)))


(def
 v98_l518
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (Math/sqrt (* 2.0 fiedler-value)))))


(deftest t99_l521 (is (true? v98_l518)))
