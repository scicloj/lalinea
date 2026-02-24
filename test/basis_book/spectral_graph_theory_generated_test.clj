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
 v3_l27
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
 v5_l35
 (def laplacian (fn [adj] (la/sub (la/diag (row-sums adj)) adj))))


(def
 v7_l41
 (def
  sorted-real-eigenvalues
  (fn [eig] (sort (mapv first (:eigenvalues eig))))))


(def
 v9_l54
 (def
  adj
  (la/matrix
   [[0 1 1 0 0 0]
    [1 0 1 0 0 0]
    [1 1 0 1 0 0]
    [0 0 1 0 1 1]
    [0 0 0 1 0 1]
    [0 0 0 1 1 0]])))


(def v11_l67 (vec (row-sums adj)))


(deftest
 t12_l69
 (is ((fn [v] (= v [2.0 2.0 3.0 3.0 2.0 2.0])) v11_l67)))


(def v14_l85 (def L (laplacian adj)))


(def v15_l87 L)


(deftest
 t16_l89
 (is
  ((fn
    [m]
    (and
     (= [6 6] (vec (dtype/shape m)))
     (= 2.0 (tensor/mget m 0 0))
     (= -1.0 (tensor/mget m 0 1))))
   v15_l87)))


(def v18_l104 (dfn/reduce-max (dfn/abs (row-sums L))))


(deftest t19_l106 (is ((fn [v] (< v 1.0E-10)) v18_l104)))


(def v21_l114 (def eig (la/eigen L)))


(def v22_l116 (def eigenvalues (sorted-real-eigenvalues eig)))


(def v23_l118 eigenvalues)


(deftest
 t24_l120
 (is
  ((fn [v] (and (< (Math/abs (first v)) 1.0E-10) (= 6 (count v))))
   v23_l118)))


(def v26_l133 (def fiedler-value (second eigenvalues)))


(def v27_l135 fiedler-value)


(deftest t28_l137 (is ((fn [v] (and (pos? v) (< v 2.0))) v27_l135)))


(def
 v30_l149
 (def
  sorted-eig-indices
  (let
   [vals (mapv first (:eigenvalues eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v31_l153
 (def
  fiedler-eigvec
  (nth (:eigenvectors eig) (second sorted-eig-indices))))


(def
 v33_l158
 (def fiedler-entries (vec (dtype/->reader fiedler-eigvec))))


(def v34_l161 fiedler-entries)


(deftest t35_l163 (is ((fn [v] (= 6 (count v))) v34_l161)))


(def
 v37_l169
 (def
  cluster-assignment
  (mapv (fn [x] (if (neg? x) :A :B)) fiedler-entries)))


(def v38_l172 cluster-assignment)


(deftest
 t39_l174
 (is
  ((fn
    [v]
    (let
     [freqs (frequencies v)]
     (and (= 3 (freqs :A)) (= 3 (freqs :B)))))
   v38_l172)))


(def
 v41_l186
 (->
  (tc/dataset
   {:vertex (range 6),
    :fiedler-value fiedler-entries,
    :cluster cluster-assignment})
  (plotly/base {:=x :vertex, :=y :fiedler-value, :=color :cluster})
  (plotly/layer-bar)
  plotly/plot))


(def
 v43_l200
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
 v44_l208
 (def
  disc-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian adj-disconnected)))))


(def v45_l211 disc-eigenvalues)


(deftest
 t46_l213
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (Math/abs (second v)) 1.0E-10)
     (> (nth v 2) 0.1)))
   v45_l211)))


(def v48_l227 (def kn 5))


(def
 v49_l229
 (def
  K5-adj
  (let
   [result (double-array (* kn kn))]
   (dotimes
    [i kn]
    (dotimes
     [j kn]
     (when (not= i j) (aset result (+ (* i kn) j) 1.0))))
   (tensor/reshape (tensor/ensure-tensor result) [kn kn]))))


(def
 v50_l237
 (def
  K5-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian K5-adj)))))


(def v51_l240 K5-eigenvalues)


(deftest
 t52_l242
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (every? (fn [x] (< (Math/abs (- x 5.0)) 1.0E-10)) (rest v))))
   v51_l240)))


(def v54_l259 (def cn 8))


(def
 v55_l261
 (def
  cycle-adj
  (let
   [result (double-array (* cn cn))]
   (dotimes
    [i cn]
    (aset result (+ (* i cn) (mod (inc i) cn)) 1.0)
    (aset result (+ (* i cn) (mod (+ i (dec cn)) cn)) 1.0))
   (tensor/reshape (tensor/ensure-tensor result) [cn cn]))))


(def
 v56_l268
 (def
  cycle-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian cycle-adj)))))


(def
 v57_l271
 (def
  cycle-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* 2.0 Math/PI k) cn)))))
    (range cn)))))


(def v59_l277 cycle-eigenvalues)


(def v61_l281 cycle-theoretical)


(def
 v63_l285
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array cycle-eigenvalues)
     (double-array cycle-theoretical))))
  1.0E-10))


(deftest t64_l290 (is (true? v63_l285)))


(def v66_l298 (def pn 6))


(def
 v67_l300
 (def
  path-adj
  (let
   [result (double-array (* pn pn))]
   (dotimes
    [i (dec pn)]
    (aset result (+ (* i pn) (inc i)) 1.0)
    (aset result (+ (* (inc i) pn) i) 1.0))
   (tensor/reshape (tensor/ensure-tensor result) [pn pn]))))


(def
 v68_l307
 (def
  path-eigenvalues
  (sorted-real-eigenvalues (la/eigen (laplacian path-adj)))))


(def
 v69_l310
 (def
  path-theoretical
  (sort
   (mapv
    (fn [k] (- 2.0 (* 2.0 (Math/cos (/ (* Math/PI k) pn)))))
    (range pn)))))


(def
 v70_l314
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array path-eigenvalues)
     (double-array path-theoretical))))
  1.0E-10))


(deftest t71_l319 (is (true? v70_l314)))


(def
 v73_l329
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


(def v74_l344 (def comm-eig (la/eigen (laplacian community-adj))))


(def v76_l348 (def comm-eigenvalues (sorted-real-eigenvalues comm-eig)))


(def v77_l350 (take 4 comm-eigenvalues))


(deftest
 t78_l352
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (first v)) 1.0E-10)
     (< (second v) 1.5)
     (< (nth v 2) 1.5)
     (> (nth v 3) 1.5)))
   v77_l350)))


(def
 v80_l365
 (def
  sorted-comm-indices
  (let
   [vals (mapv first (:eigenvalues comm-eig))]
   (sort-by (fn [i] (vals i)) (range (count vals))))))


(def
 v82_l372
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
       [p1__73523#]
       (cond (<= p1__73523# 2) "A" (<= p1__73523# 5) "B" :else "C"))
      (range 9))}))))


(def
 v83_l383
 (->
  embed-data
  (plotly/base {:=x :x, :=y :y, :=color :community})
  (plotly/layer-point {:=mark-size 14})
  plotly/plot))


(def v85_l405 (def conductance (/ 1.0 3.0)))


(def
 v86_l407
 (and
  (<= (/ fiedler-value 2.0) conductance)
  (<= conductance (Math/sqrt (* 2.0 fiedler-value)))))


(deftest t87_l410 (is (true? v86_l407)))
