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
  laplacian
  (fn [adj] (la/sub (la/diag (la/reduce-axis adj dfn/sum 1)) adj))))


(def
 v5_l52
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v7_l63 (def six-pos [[0 1] [0 -1] [1.5 0] [3.5 0] [5 1] [5 -1]]))


(def v8_l66 (def six-edges [[0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]]))


(def
 v9_l69
 (vis/graph-plot six-pos six-edges {:edge-highlight #{[2 3]}}))


(def v11_l74 (vec (la/reduce-axis adj dfn/sum 1)))


(deftest
 t12_l76
 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v11_l74)))


(def v14_l92 (def L (laplacian adj)))


(def v15_l94 L)


(deftest
 t16_l96
 (is
  ((fn
    [m]
    (and
     (= [6 6] (dtype/shape m))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v15_l94)))


(def v18_l111 (dfn/reduce-max (dfn/abs (la/reduce-axis L dfn/sum 1))))


(deftest t19_l113 (is ((fn [v] (< v 1.0E-10)) v18_l111)))


(def v21_l128 (def eig (la/eigen L)))


(def v22_l130 (def eigenvalues (la/real-eigenvalues (laplacian adj))))


(def v23_l132 eigenvalues)


(deftest
 t24_l134
 (is
  ((fn [v] (and (< (abs (first v)) 1.0E-10) (= 6 (count v))))
   v23_l132)))


(def v26_l147 (def fiedler-value (second eigenvalues)))


(def v27_l149 fiedler-value)


(deftest t28_l151 (is ((fn [v] (and (pos? v) (< v 2.0))) v27_l149)))


(def
 v30_l163
 (def
  sorted-eig-indices
  (let
   [vals (cx/re (:eigenvalues eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues eig)))))))


(def
 v31_l167
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v33_l172
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v34_l175 fiedler-entries)


(deftest t35_l177 (is ((fn [v] (= 6 (count v))) v34_l175)))


(def v37_l183 (< (abs (reduce + fiedler-entries)) 1.0E-10))


(deftest t38_l185 (is (true? v37_l183)))


(def
 v40_l190
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v41_l193 cluster-assignment)


(deftest
 t42_l195
 (is
  ((fn
    [v]
    (and
     (apply = (subvec v 0 3))
     (apply = (subvec v 3 6))
     (not= (v 0) (v 3))))
   v41_l193)))


(def
 v44_l205
 (vis/graph-plot
  six-pos
  six-edges
  {:node-colors (mapv {:A "#2266cc", :B "#dd8800"} cluster-assignment),
   :edge-highlight #{[2 3]}}))


(def
 v46_l213
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v48_l227
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
 v49_l235
 (vis/graph-plot
  six-pos
  [[0 1] [0 2] [1 2] [3 4] [3 5] [4 5]]
  {:node-colors
   ["#2266cc" "#2266cc" "#2266cc" "#dd8800" "#dd8800" "#dd8800"]}))


(def
 v50_l240
 (def
  disc-eigenvalues
  (la/real-eigenvalues (laplacian adj-disconnected))))


(def v51_l243 disc-eigenvalues)


(deftest
 t52_l245
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (< (abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v51_l243)))


(def v54_l259 (def kn 5))


(def
 v55_l261
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v56_l266
 (vis/graph-plot
  [[0.0 1.0]
   [-0.951 0.309]
   [-0.588 -0.809]
   [0.588 -0.809]
   [0.951 0.309]]
  [[0 1] [0 2] [0 3] [0 4] [1 2] [1 3] [1 4] [2 3] [2 4] [3 4]]
  {}))


(def
 v57_l270
 (def K5-eigenvalues (la/real-eigenvalues (laplacian K5-adj))))


(def v58_l273 K5-eigenvalues)


(deftest
 t59_l275
 (is
  ((fn
    [v]
    (and
     (< (abs (first v)) 1.0E-10)
     (every? (fn [x] (< (abs (- x 5.0)) 1.0E-10)) (rest v))))
   v58_l273)))


(def v61_l292 (def cn 8))


(def
 v62_l294
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
 v63_l301
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
 v64_l305
 (def cycle-eigenvalues (la/real-eigenvalues (laplacian cycle-adj))))


(def
 v65_l308
 (def
  cycle-theoretical
  (sort
   (dtype/make-reader
    :float64
    cn
    (- 2.0 (* 2.0 (math/cos (/ (* 2.0 math/PI idx) cn))))))))


(def v67_l314 cycle-eigenvalues)


(def v69_l318 cycle-theoretical)


(def
 v71_l322
 (<
  (dfn/reduce-max
   (dfn/abs (dfn/- cycle-eigenvalues cycle-theoretical)))
  1.0E-10))


(deftest t72_l326 (is (true? v71_l322)))


(def v74_l334 (def pn 6))


(def
 v75_l336
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v76_l341
 (vis/graph-plot
  [[0 0] [1 0] [2 0] [3 0] [4 0] [5 0]]
  [[0 1] [1 2] [2 3] [3 4] [4 5]]
  {:width 350}))


(def
 v77_l345
 (def path-eigenvalues (la/real-eigenvalues (laplacian path-adj))))


(def v78_l348 path-eigenvalues)


(def
 v79_l350
 (def
  path-theoretical
  (sort
   (dtype/make-reader
    :float64
    pn
    (- 2.0 (* 2.0 (math/cos (/ (* math/PI idx) pn))))))))


(def v80_l354 path-theoretical)


(def
 v81_l356
 (<
  (dfn/reduce-max (dfn/abs (dfn/- path-eigenvalues path-theoretical)))
  1.0E-10))


(deftest t82_l360 (is (true? v81_l356)))


(def
 v84_l370
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
 v85_l385
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


(def v86_l399 (def comm-eig (la/eigen (laplacian community-adj))))


(def
 v88_l403
 (def comm-eigenvalues (la/real-eigenvalues (laplacian community-adj))))


(def v89_l405 comm-eigenvalues)


(deftest
 t90_l407
 (is
  ((fn
    [v]
    (and
     (= 9 (count v))
     (< (abs (first v)) 1.0E-10)
     (< (nth v 2) 1.0)
     (> (nth v 3) (* 2.0 (nth v 2)))))
   v89_l405)))


(def
 v91_l414
 (->
  (tc/dataset
   {:index (range (count comm-eigenvalues)),
    :eigenvalue comm-eigenvalues})
  (plotly/base {:=x :index, :=y :eigenvalue})
  (plotly/layer-bar)
  plotly/plot))


(def
 v93_l426
 (def
  sorted-comm-indices
  (let
   [vals (cx/re (:eigenvalues comm-eig))]
   (sort-by
    (fn [i] (double (vals i)))
    (range (count (:eigenvalues comm-eig)))))))


(def
 v95_l433
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
       [p1__11110#]
       (cond (<= p1__11110# 2) "A" (<= p1__11110# 5) "B" :else "C"))
      (range 9))}))))


(def
 v96_l444
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def
 v98_l454
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


(deftest t99_l462 (is (true? v98_l454)))


(def v101_l479 (def conductance (/ 1.0 3.0)))


(def
 v102_l481
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (math/sqrt (* 2.0 fiedler-value)))))


(deftest t103_l484 (is (true? v102_l481)))
