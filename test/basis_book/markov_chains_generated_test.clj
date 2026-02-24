(ns
 basis-book.markov-chains-generated-test
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
 v3_l41
 (def P (la/matrix [[0.7 0.2 0.1] [0.3 0.4 0.3] [0.2 0.3 0.5]])))


(def v5_l48 (la/mmul P (la/column (repeat 3 1.0))))


(deftest
 t6_l50
 (is
  ((fn
    [sums]
    (< (la/norm (la/sub sums (la/column (repeat 3 1.0)))) 1.0E-10))
   v5_l48)))


(def v8_l64 (def initial-state (la/row [1.0 0.0 0.0])))


(def
 v9_l66
 (def
  walk-history
  (let
   [states (iterate (fn [s] (la/mmul s P)) initial-state)]
   (mapv
    (fn
     [k s]
     {:step k,
      :sunny (tensor/mget s 0 0),
      :cloudy (tensor/mget s 0 1),
      :rainy (tensor/mget s 0 2)})
    (range 20)
    (take 20 states)))))


(def
 v11_l79
 (->
  (tc/dataset
   (mapcat
    (fn
     [{:keys [step sunny cloudy rainy]}]
     [{:step step, :probability sunny, :state "Sunny"}
      {:step step, :probability cloudy, :state "Cloudy"}
      {:step step, :probability rainy, :state "Rainy"}])
    walk-history))
  (plotly/base {:=x :step, :=y :probability, :=color :state})
  (plotly/layer-line)
  plotly/plot))


(def
 v13_l90
 (let
  [last-state (last walk-history)]
  [(:sunny last-state) (:cloudy last-state) (:rainy last-state)]))


(deftest
 t14_l95
 (is
  ((fn [v] (< (Math/abs (- (+ (v 0) (v 1) (v 2)) 1.0)) 1.0E-10))
   v13_l90)))


(def v16_l106 (def eigen-result (la/eigen (la/transpose P))))


(def
 v18_l110
 (def
  stationary-eigen
  (let
   [{:keys [eigenvalues eigenvectors]}
    eigen-result
    idx
    (->>
     eigenvalues
     (map-indexed (fn [i [re _im]] [i re]))
     (sort-by (fn [[_i re]] (Math/abs (- re 1.0))))
     first
     first)
    ev
    (nth eigenvectors idx)
    col
    (tensor/select ev :all 0)
    total
    (dfn/sum col)]
   (vec (dfn/* col (/ 1.0 total))))))


(def v19_l122 stationary-eigen)


(deftest
 t20_l124
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (dfn/sum (double-array v)) 1.0)) 1.0E-10)
     (every? pos? v)))
   v19_l122)))


(def
 v22_l136
 (def
  power-iteration-history
  (let
   [n 3 iters 40]
   (loop
    [pi (la/row [0.333 0.334 0.333]) k 0 history []]
    (if
     (>= k iters)
     history
     (let
      [new-pi
       (la/mmul pi P)
       new-pi
       (la/scale (/ 1.0 (dfn/sum new-pi)) new-pi)
       change
       (la/norm (la/sub new-pi pi))]
      (recur
       new-pi
       (inc k)
       (conj history {:iteration (inc k), :change change}))))))))


(def
 v24_l152
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def
 v26_l159
 (let
  [last-change (:change (last power-iteration-history))]
  last-change))


(deftest t27_l162 (is ((fn [c] (< c 1.0E-10)) v26_l159)))


(def
 v29_l183
 (def
  H
  (la/matrix
   [[0 1/2 1/2 0 0]
    [0 0 1 0 0]
    [1 0 0 0 0]
    [1/2 0 1/2 0 0]
    [1/4 1/4 1/4 1/4 0]])))


(def v31_l194 (def damping 0.85))


(def v32_l196 (def n-pages 5))


(def
 v33_l198
 (def
  google-matrix
  (la/add
   (la/scale
    (/ (- 1.0 damping) n-pages)
    (la/matrix (repeat n-pages (repeat n-pages 1.0))))
   (la/scale damping H))))


(def
 v35_l205
 (def
  pagerank
  (let
   [iters 50]
   (loop
    [pi (la/scale (/ 1.0 n-pages) (la/row (repeat n-pages 1.0))) k 0]
    (if
     (>= k iters)
     pi
     (let
      [new-pi
       (la/mmul pi google-matrix)
       new-pi
       (la/scale (/ 1.0 (dfn/sum new-pi)) new-pi)]
      (recur new-pi (inc k))))))))


(def
 v37_l217
 (let
  [labels ["Page 0" "Page 1" "Page 2" "Page 3" "Page 4"]]
  (->
   (tc/dataset {:page labels, :rank (vec (dtype/->reader pagerank))})
   (plotly/base {:=x :page, :=y :rank})
   (plotly/layer-bar)
   plotly/plot)))


(def v39_l225 (dfn/sum pagerank))


(deftest
 t40_l227
 (is ((fn [s] (< (Math/abs (- s 1.0)) 1.0E-10)) v39_l225)))


(def
 v42_l232
 (let
  [pr-arr
   (dtype/->double-array pagerank)
   max-idx
   (loop
    [i 1 best 0]
    (if
     (>= i (alength pr-arr))
     best
     (recur
      (inc i)
      (if (> (aget pr-arr i) (aget pr-arr best)) i best))))]
  max-idx))


(deftest t43_l238 (is ((fn [idx] (contains? #{0 2} idx)) v42_l232)))
