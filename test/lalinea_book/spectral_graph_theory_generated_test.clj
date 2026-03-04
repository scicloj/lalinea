(ns
 lalinea-book.spectral-graph-theory-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l45
 (def
  adj
  (t/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v5_l56 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def v6_l59 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def
 v7_l62
 (vis/graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v9_l67 (t/reduce-axis adj la/sum 1))


(deftest t10_l69 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v9_l67)))


(def
 v12_l85
 (def
  laplacian
  (fn [adj] (la/sub (t/diag (t/reduce-axis adj la/sum 1)) adj))))


(def v13_l89 (def L (laplacian adj)))


(def v14_l91 L)


(deftest
 t15_l93
 (is
  ((fn
    [m]
    (and (= [6 6] (t/shape m)) (= 2.0 (m 0 0)) (= -1.0 (m 0 1))))
   v14_l91)))


(def v17_l108 (elem/reduce-max (elem/abs (t/reduce-axis L la/sum 1))))


(deftest t18_l110 (is ((fn [v] (< v 1.0E-10)) v17_l108)))


(def v20_l125 (def eig (la/eigen L)))


(def v21_l127 (def eigenvalues (la/real-eigenvalues (laplacian adj))))


(def v22_l129 eigenvalues)


(deftest
 t23_l131
 (is
  ((fn [v] (and (< (abs (first v)) 1.0E-10) (= 6 (count v))))
   v22_l129)))


(def v25_l144 (def fiedler-value (second eigenvalues)))


(def v26_l146 fiedler-value)


(deftest t27_l148 (is ((fn [v] (and (pos? v) (< v 2.0))) v26_l146)))


(def
 v29_l161
 (def
  sorted-eig-indices
  (let
   [vals (la/re (:eigenvalues eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues eig)))))))


(def
 v30_l165
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def v32_l170 (def fiedler-entries (t/flatten fiedler-eigvec)))


(def v33_l173 fiedler-entries)


(deftest t34_l175 (is ((fn [v] (= 6 (count v))) v33_l173)))


(def v36_l181 (< (abs (la/sum fiedler-entries)) 1.0E-10))


(deftest t37_l183 (is (true? v36_l181)))


(def
 v39_l188
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v40_l191 cluster-assignment)


(deftest
 t41_l193
 (is
  ((fn
    [v]
    (and
     (apply = (subvec v 0 3))
     (apply = (subvec v 3 6))
     (not= (v 0) (v 3))))
   v40_l191)))


(def
 v43_l203
 (vis/graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v45_l211
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v47_l225
 (def
  adj-disconnected
  (t/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 0 0 0]
    [0 0 0 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def
 v48_l233
 (vis/graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v49_l238
 (def
  disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected))))


(def v50_l241 disc-eigenvalues)


(deftest
 t51_l243
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (< (abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v50_l241)))


(def v53_l257 (def kn 5))


(def
 v54_l259
 (def
  K5-adj
  (t/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v55_l264
 (vis/graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v56_l268
 (def K5-eigenvalues (la/real-eigenvalues (laplacian K5-adj))))


(def v57_l271 K5-eigenvalues)


(deftest
 t58_l273
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (every? (fn [x] (< (abs (- x 5.0)) 1.0E-10)) (rest v))))
   v57_l271)))


(def v60_l290 (def cn 8))


(def
 v61_l292
 (def
  cycle-adj
  (t/compute-tensor
   [cn cn]
   (fn
    [i j]
    (if
     (or (= j (mod (inc i) cn)) (= j (mod (+ i (dec cn)) cn)))
     1.0
     0.0))
   :float64)))


(def
 v62_l299
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
 v63_l303
 (def cycle-eigenvalues (la/real-eigenvalues (laplacian cycle-adj))))


(def
 v64_l306
 (def
  cycle-theoretical
  (sort
   (t/make-reader
    :float64
    cn
    (- 2.0 (* 2.0 (math/cos (/ (* 2.0 math/PI idx) cn))))))))


(def v66_l312 cycle-eigenvalues)


(def v68_l316 cycle-theoretical)


(def
 v70_l320
 (<
  (elem/reduce-max
   (elem/abs (la/sub cycle-eigenvalues cycle-theoretical)))
  1.0E-10))


(deftest t71_l324 (is (true? v70_l320)))


(def v73_l332 (def pn 6))


(def
 v74_l334
 (def
  path-adj
  (t/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v75_l339
 (vis/graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v76_l343
 (def path-eigenvalues (la/real-eigenvalues (laplacian path-adj))))


(def v77_l346 path-eigenvalues)


(def
 v78_l348
 (def
  path-theoretical
  (sort
   (t/make-reader
    :float64
    pn
    (- 2.0 (* 2.0 (math/cos (/ (* math/PI idx) pn))))))))


(def v79_l352 path-theoretical)


(def
 v80_l354
 (<
  (elem/reduce-max
   (elem/abs (la/sub path-eigenvalues path-theoretical)))
  1.0E-10))


(deftest t81_l358 (is (true? v80_l354)))


(def
 v83_l368
 (def
  community-adj
  (t/matrix
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
 v84_l383
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


(def v85_l397 (def comm-eig (la/eigen (laplacian community-adj))))


(def
 v87_l401
 (def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj))))


(def v88_l403 comm-eigenvalues)


(deftest
 t89_l405
 (is
  ((fn
    [v]
    (and
     (= 9 (count v))
     (< (abs (first v)) 1.0E-10)
     (< (nth v 2) 1.0)
     (> (nth v 3) (* 2.0 (nth v 2)))))
   v88_l403)))


(def
 v90_l412
 (->
  (tc/dataset
   {:index (range (count comm-eigenvalues)),
    :eigenvalue comm-eigenvalues})
  (plotly/base {:=x :index, :=y :eigenvalue})
  (plotly/layer-bar)
  plotly/plot))


(def
 v92_l424
 (def
  sorted-comm-indices
  (let
   [vals (la/re (:eigenvalues comm-eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues comm-eig)))))))


(def
 v94_l431
 (def
  embed-data
  (let
   [ev2
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 1))
    ev3
    (nth (:eigenvectors comm-eig) (nth sorted-comm-indices 2))]
   (tc/dataset
    {:vertex (range 9),
     :x (t/->reader ev2),
     :y (t/->reader ev3),
     :community
     (mapv
      (fn*
       [p1__86811#]
       (cond (<= p1__86811# 2) "A" (<= p1__86811# 5) "B" :else "C"))
      (range 9))}))))


(def
 v95_l442
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def
 v97_l452
 (let
  [xs
   (:x embed-data)
   ys
   (:y embed-data)
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


(deftest t98_l460 (is (true? v97_l452)))


(def v100_l477 (def conductance (/ 1.0 3.0)))


(def
 v101_l479
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (math/sqrt (* 2.0 fiedler-value)))))


(deftest t102_l482 (is (true? v101_l479)))
