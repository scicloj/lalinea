(ns
 basis-book.markov-chains-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.datatype.argops :as argops]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l50
 (def P (la/matrix [[0.7 0.2 0.1] [0.3 0.4 0.3] [0.2 0.3 0.5]])))


(def v5_l57 (la/mmul P (la/column (repeat 3 1.0))))


(deftest
 t6_l59
 (is
  ((fn
    [sums]
    (< (la/norm (la/sub sums (la/column (repeat 3 1.0)))) 1.0E-10))
   v5_l57)))


(def v8_l73 (def initial-state (la/row [1.0 0.0 0.0])))


(def
 v9_l75
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
 v11_l88
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
 v13_l99
 (let
  [last-state (last walk-history)]
  [(:sunny last-state) (:cloudy last-state) (:rainy last-state)]))


(deftest
 t14_l104
 (is
  ((fn [v] (< (Math/abs (- (+ (v 0) (v 1) (v 2)) 1.0)) 1.0E-10))
   v13_l99)))


(def v16_l115 (def eigen-result (la/eigen (la/transpose P))))


(def
 v18_l119
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


(def v19_l131 stationary-eigen)


(deftest
 t20_l133
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (dfn/sum (double-array v)) 1.0)) 1.0E-10)
     (every? pos? v)))
   v19_l131)))


(def
 v22_l145
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
 v24_l161
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def
 v26_l168
 (let
  [last-change (:change (last power-iteration-history))]
  last-change))


(deftest t27_l171 (is ((fn [c] (< c 1.0E-10)) v26_l168)))


(def
 v29_l192
 (def
  H
  (la/matrix
   [[0 1/2 1/2 0 0]
    [0 0 1 0 0]
    [1 0 0 0 0]
    [1/2 0 1/2 0 0]
    [1/4 1/4 1/4 1/4 0]])))


(def v31_l203 (def damping 0.85))


(def v32_l205 (def n-pages 5))


(def
 v33_l207
 (def
  google-matrix
  (la/add
   (la/scale
    (/ (- 1.0 damping) n-pages)
    (la/matrix (repeat n-pages (repeat n-pages 1.0))))
   (la/scale damping H))))


(def
 v35_l214
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
 v37_l226
 (let
  [labels ["Page 0" "Page 1" "Page 2" "Page 3" "Page 4"]]
  (->
   (tc/dataset {:page labels, :rank (vec (dtype/->reader pagerank))})
   (plotly/base {:=x :page, :=y :rank})
   (plotly/layer-bar)
   plotly/plot)))


(def v39_l234 (dfn/sum pagerank))


(deftest
 t40_l236
 (is ((fn [s] (< (Math/abs (- s 1.0)) 1.0E-10)) v39_l234)))


(def v42_l241 (argops/argmax pagerank))


(deftest t43_l243 (is ((fn [idx] (contains? #{0 2} idx)) v42_l241)))
