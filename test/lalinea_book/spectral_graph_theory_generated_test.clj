(ns
 lalinea-book.spectral-graph-theory-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l39
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
 v5_l47
 (def laplacian (fn [adj] (la/sub (la/diag (row-sums adj)) adj))))


(def
 v7_l60
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v9_l71 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def
 v10_l74
 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def
 v11_l77
 (vis/graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v13_l82 (vec (row-sums adj)))


(deftest
 t14_l84
 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v13_l82)))


(def v16_l100 (def L (laplacian adj)))


(def v17_l102 L)


(deftest
 t18_l104
 (is
  ((fn
    [m]
    (and
     (= [6 6] (dtype/shape m))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v17_l102)))


(def v20_l119 (dfn/reduce-max (dfn/abs (row-sums L))))


(deftest t21_l121 (is ((fn [v] (< v 1.0E-10)) v20_l119)))


(def v23_l136 (def eig (la/eigen L)))


(def v24_l138 (def eigenvalues (la/real-eigenvalues (laplacian adj))))


(def v25_l140 eigenvalues)


(deftest
 t26_l142
 (is
  ((fn [v] (and (< (abs (first v)) 1.0E-10) (= 6 (count v))))
   v25_l140)))


(def v28_l155 (def fiedler-value (second eigenvalues)))


(def v29_l157 fiedler-value)


(deftest t30_l159 (is ((fn [v] (and (pos? v) (< v 2.0))) v29_l157)))


(def
 v32_l171
 (def
  sorted-eig-indices
  (let
   [vals (cx/re (:eigenvalues eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues eig)))))))


(def
 v33_l175
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v35_l180
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v36_l183 fiedler-entries)


(deftest t37_l185 (is ((fn [v] (= 6 (count v))) v36_l183)))


(def v39_l191 (< (abs (reduce + fiedler-entries)) 1.0E-10))


(deftest t40_l193 (is (true? v39_l191)))


(def
 v42_l198
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v43_l201 cluster-assignment)


(deftest
 t44_l203
 (is
  ((fn
    [v]
    (and
     (apply = (subvec v 0 3))
     (apply = (subvec v 3 6))
     (not= (v 0) (v 3))))
   v43_l201)))


(def
 v46_l213
 (vis/graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v48_l221
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v50_l235
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
 v51_l243
 (vis/graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v52_l248
 (def
  disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected))))


(def v53_l251 disc-eigenvalues)


(deftest
 t54_l253
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (< (abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v53_l251)))


(def v56_l267 (def kn 5))


(def
 v57_l269
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v58_l274
 (vis/graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v59_l278
 (def K5-eigenvalues (la/real-eigenvalues (laplacian K5-adj))))


(def v60_l281 K5-eigenvalues)


(deftest
 t61_l283
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (every? (fn [x] (< (abs (- x 5.0)) 1.0E-10)) (rest v))))
   v60_l281)))


(def v63_l300 (def cn 8))


(def
 v64_l302
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
 v65_l309
 (vis/graph-plot
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
 v66_l313
 (def cycle-eigenvalues (la/real-eigenvalues (laplacian cycle-adj))))


(def
 v67_l316
 (def
  cycle-theoretical
  (sort
   (dtype/make-reader
    :float64
    cn
    (- 2.0 (* 2.0 (math/cos (/ (* 2.0 math/PI idx) cn))))))))


(def v69_l322 cycle-eigenvalues)


(def v71_l326 cycle-theoretical)


(def
 v73_l330
 (<
  (dfn/reduce-max
   (dfn/abs (dfn/- cycle-eigenvalues cycle-theoretical)))
  1.0E-10))


(deftest t74_l334 (is (true? v73_l330)))


(def v76_l342 (def pn 6))


(def
 v77_l344
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v78_l349
 (vis/graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v79_l353
 (def path-eigenvalues (la/real-eigenvalues (laplacian path-adj))))


(def v80_l356 path-eigenvalues)


(def
 v81_l358
 (def
  path-theoretical
  (sort
   (dtype/make-reader
    :float64
    pn
    (- 2.0 (* 2.0 (math/cos (/ (* math/PI idx) pn))))))))


(def v82_l362 path-theoretical)


(def
 v83_l364
 (<
  (dfn/reduce-max (dfn/abs (dfn/- path-eigenvalues path-theoretical)))
  1.0E-10))


(deftest t84_l368 (is (true? v83_l364)))


(def
 v86_l378
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
 v87_l393
 (vis/graph-plot
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


(def v88_l407 (def comm-eig (la/eigen (laplacian community-adj))))


(def
 v90_l411
 (def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj))))


(def v91_l413 comm-eigenvalues)


(deftest
 t92_l415
 (is
  ((fn
    [v]
    (and
     (= 9 (count v))
     (< (abs (first v)) 1.0E-10)
     (< (nth v 2) 1.0)
     (> (nth v 3) (* 2.0 (nth v 2)))))
   v91_l413)))


(def
 v93_l422
 (->
  (tc/dataset
   {:index (range (count comm-eigenvalues)),
    :eigenvalue comm-eigenvalues})
  (plotly/base {:=x :index, :=y :eigenvalue})
  (plotly/layer-bar)
  plotly/plot))


(def
 v95_l434
 (def
  sorted-comm-indices
  (let
   [vals (cx/re (:eigenvalues comm-eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues comm-eig)))))))


(def
 v97_l441
 (def
  embed-data
  (let
   [ev2
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 1))
    ev3
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 2))]
   (tc/dataset
    {:vertex (range 9),
     :x (dtype/->reader ev2),
     :y (dtype/->reader ev3),
     :community
     (mapv
      (fn*
       [p1__74495#]
       (cond (<= p1__74495# 2) "A" (<= p1__74495# 5) "B" :else "C"))
      (range 9))}))))


(def
 v98_l452
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def
 v100_l462
 (let
  [xs
   (vec (:x embed-data))
   ys
   (vec (:y embed-data))
   dist
   (fn
    [i j]
    (math/sqrt
     (+
      (let [d (- (xs i) (xs j))] (* d d))
      (let [d (- (ys i) (ys j))] (* d d)))))
   within-A
   (dist 0 1)
   across-AB
   (apply min (for [a [0 1 2] b [3 4 5]] (dist a b)))]
  (< within-A across-AB)))


(deftest t101_l470 (is (true? v100_l462)))


(def v103_l487 (def conductance (/ 1.0 3.0)))


(def
 v104_l489
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (math/sqrt (* 2.0 fiedler-value)))))


(deftest t105_l492 (is (true? v104_l489)))
