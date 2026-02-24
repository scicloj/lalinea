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
 v9_l61
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v11_l74 (vec (row-sums adj)))


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
     (= [6 6] (vec (dtype/shape m)))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v15_l94)))


(def v18_l111 (dfn/reduce-max (dfn/abs (row-sums L))))


(deftest t19_l113 (is ((fn [v] (< v 1.0E-10)) v18_l111)))


(def v21_l121 (def eig (la/eigen L)))


(def v22_l123 (def eigenvalues (sorted-real-eigenvalues eig)))


(def v23_l125 eigenvalues)


(deftest
 t24_l127
 (is
  ((fn [v] (and (< (Math/abs (first v)) 1.0E-10) (= 6 (count v))))
   v23_l125)))


(def v26_l140 (def fiedler-value (second eigenvalues)))


(def v27_l142 fiedler-value)


(deftest t28_l144 (is ((fn [v] (and (pos? v) (< v 2.0))) v27_l142)))


(def
 v30_l156
 (def
  sorted-eig-indices
  (let
   [vals (mapv first (:eigenvalues eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v31_l160
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v33_l165
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v34_l168 fiedler-entries)


(deftest t35_l170 (is ((fn [v] (= 6 (count v))) v34_l168)))


(def
 v37_l176
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v38_l179 cluster-assignment)


(deftest
 t39_l181
 (is
  ((fn
    [v]
    (let
     [freqs (frequencies v)]
     (and (= 3 (freqs :A)) (= 3 (freqs :B)))))
   v38_l179)))


(def
 v41_l193
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v43_l207
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
 v44_l215
 (def
  disc-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian adj-disconnected)))))


(def v45_l218 disc-eigenvalues)


(deftest
 t46_l220
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (Math/abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v45_l218)))


(def v48_l234 (def kn 5))


(def
 v49_l236
 (def
  K5-adj
  (tensor/compute-tensor
   [kn kn]
   (fn [i j] (if (not= i j) 1.0 0.0))
   :float64)))


(def
 v50_l241
 (def
  K5-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian K5-adj)))))


(def v51_l244 K5-eigenvalues)


(deftest
 t52_l246
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (every? (fn [x] (< (Math/abs (- x 5.0)) 1.0E-10)) (rest v))))
   v51_l244)))


(def v54_l263 (def cn 8))


(def
 v55_l265
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
 v56_l272
 (def
  cycle-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian cycle-adj)))))


(def
 v57_l275
 (def
  cycle-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* 2.0 Math/PI k) cn)))))
    (range cn)))))


(def v59_l281 cycle-eigenvalues)


(def v61_l285 cycle-theoretical)


(def
 v63_l289
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array cycle-eigenvalues)
     (double-array cycle-theoretical))))
  1.0E-10))


(deftest t64_l294 (is (true? v63_l289)))


(def v66_l302 (def pn 6))


(def
 v67_l304
 (def
  path-adj
  (tensor/compute-tensor
   [pn pn]
   (fn [i j] (if (= 1 (Math/abs (- i j))) 1.0 0.0))
   :float64)))


(def
 v68_l309
 (def
  path-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian path-adj)))))


(def
 v69_l312
 (def
  path-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* Math/PI k) pn)))))
    (range pn)))))


(def
 v70_l316
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array path-eigenvalues)
     (double-array path-theoretical))))
  1.0E-10))


(deftest t71_l321 (is (true? v70_l316)))


(def
 v73_l331
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


(def v74_l346 (def comm-eig (la/eigen (laplacian community-adj))))


(def v76_l350 (def comm-eigenvalues (sorted-real-eigenvalues comm-eig)))


(def v77_l352 (take 4 comm-eigenvalues))


(deftest
 t78_l354
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (second v) 1.5)
     (< (nth v 2) 1.5)
     (> (nth v 3) 1.5)))
   v77_l352)))


(def
 v80_l367
 (def
  sorted-comm-indices
  (let
   [vals (mapv first (:eigenvalues comm-eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v82_l374
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
       [p1__84090#]
       (cond (<= p1__84090# 2) "A" (<= p1__84090# 5) "B" :else "C"))
      (range 9))}))))


(def
 v83_l385
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def v85_l407 (def conductance (/ 1.0 3.0)))


(def
 v86_l409
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (Math/sqrt (* 2.0 fiedler-value)))))


(deftest t87_l412 (is (true? v86_l409)))
