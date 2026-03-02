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
 v3_l46
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v5_l57 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def v6_l60 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def
 v7_l63
 (vis/graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v9_l68 (la/reduce-axis adj dfn/sum 1))


(deftest t10_l70 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v9_l68)))


(def
 v12_l86
 (def
  laplacian
  (fn [adj] (la/sub (la/diag (la/reduce-axis adj dfn/sum 1)) adj))))


(def v13_l90 (def L (laplacian adj)))


(def v14_l92 L)


(deftest
 t15_l94
 (is
  ((fn
    [m]
    (and (= [6 6] (dtype/shape m)) (= 2.0 (m 0 0)) (= -1.0 (m 0 1))))
   v14_l92)))


(def v17_l109 (dfn/reduce-max (dfn/abs (la/reduce-axis L dfn/sum 1))))


(deftest t18_l111 (is ((fn [v] (< v 1.0E-10)) v17_l109)))


(def v20_l126 (def eig (la/eigen L)))


(def v21_l128 (def eigenvalues (la/real-eigenvalues (laplacian adj))))


(def v22_l130 eigenvalues)


(deftest
 t23_l132
 (is
  ((fn [v] (and (< (abs (first v)) 1.0E-10) (= 6 (count v))))
   v22_l130)))


(def v25_l145 (def fiedler-value (second eigenvalues)))


(def v26_l147 fiedler-value)


(deftest t27_l149 (is ((fn [v] (and (pos? v) (< v 2.0))) v26_l147)))


(def
 v29_l162
 (def
  sorted-eig-indices
  (let
   [vals (cx/re (:eigenvalues eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues eig)))))))


(def
 v30_l166
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def v32_l171 (def fiedler-entries (la/flatten fiedler-eigvec)))


(def v33_l174 fiedler-entries)


(deftest t34_l176 (is ((fn [v] (= 6 (count v))) v33_l174)))


(def v36_l182 (< (abs (la/sum fiedler-entries)) 1.0E-10))


(deftest t37_l184 (is (true? v36_l182)))


(def
 v39_l189
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v40_l192 cluster-assignment)


(deftest
 t41_l194
 (is
  ((fn
    [v]
    (and
     (apply = (subvec v 0 3))
     (apply = (subvec v 3 6))
     (not= (v 0) (v 3))))
   v40_l192)))


(def
 v43_l204
 (vis/graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v45_l212
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v47_l226
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
 v48_l234
 (vis/graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v49_l239
 (def
  disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected))))


(def v50_l242 disc-eigenvalues)


(deftest
 t51_l244
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (< (abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v50_l242)))


(def v53_l258 (def kn 5))


(def
 v54_l260
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v55_l265
 (vis/graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v56_l269
 (def K5-eigenvalues (la/real-eigenvalues (laplacian K5-adj))))


(def v57_l272 K5-eigenvalues)


(deftest
 t58_l274
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (every? (fn [x] (< (abs (- x 5.0)) 1.0E-10)) (rest v))))
   v57_l272)))


(def v60_l291 (def cn 8))


(def
 v61_l293
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
 v62_l300
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
 v63_l304
 (def cycle-eigenvalues (la/real-eigenvalues (laplacian cycle-adj))))


(def
 v64_l307
 (def
  cycle-theoretical
  (sort
   (dtype/make-reader
    :float64
    cn
    (- 2.0 (* 2.0 (math/cos (/ (* 2.0 math/PI idx) cn))))))))


(def v66_l313 cycle-eigenvalues)


(def v68_l317 cycle-theoretical)


(def
 v70_l321
 (<
  (dfn/reduce-max
   (dfn/abs (dfn/- cycle-eigenvalues cycle-theoretical)))
  1.0E-10))


(deftest t71_l325 (is (true? v70_l321)))


(def v73_l333 (def pn 6))


(def
 v74_l335
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v75_l340
 (vis/graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v76_l344
 (def path-eigenvalues (la/real-eigenvalues (laplacian path-adj))))


(def v77_l347 path-eigenvalues)


(def
 v78_l349
 (def
  path-theoretical
  (sort
   (dtype/make-reader
    :float64
    pn
    (- 2.0 (* 2.0 (math/cos (/ (* math/PI idx) pn))))))))


(def v79_l353 path-theoretical)


(def
 v80_l355
 (<
  (dfn/reduce-max (dfn/abs (dfn/- path-eigenvalues path-theoretical)))
  1.0E-10))


(deftest t81_l359 (is (true? v80_l355)))


(def
 v83_l369
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
 v84_l384
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


(def v85_l398 (def comm-eig (la/eigen (laplacian community-adj))))


(def
 v87_l402
 (def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj))))


(def v88_l404 comm-eigenvalues)


(deftest
 t89_l406
 (is
  ((fn
    [v]
    (and
     (= 9 (count v))
     (< (abs (first v)) 1.0E-10)
     (< (nth v 2) 1.0)
     (> (nth v 3) (* 2.0 (nth v 2)))))
   v88_l404)))


(def
 v90_l413
 (->
  (tc/dataset
   {:index (range (count comm-eigenvalues)),
    :eigenvalue comm-eigenvalues})
  (plotly/base {:=x :index, :=y :eigenvalue})
  (plotly/layer-bar)
  plotly/plot))


(def
 v92_l425
 (def
  sorted-comm-indices
  (let
   [vals (cx/re (:eigenvalues comm-eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues comm-eig)))))))


(def
 v94_l432
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
       [p1__85489#]
       (cond (<= p1__85489# 2) "A" (<= p1__85489# 5) "B" :else "C"))
      (range 9))}))))


(def
 v95_l443
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def
 v97_l453
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


(deftest t98_l461 (is (true? v97_l453)))


(def v100_l478 (def conductance (/ 1.0 3.0)))


(def
 v101_l480
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (math/sqrt (* 2.0 fiedler-value)))))


(deftest t102_l483 (is (true? v101_l480)))
