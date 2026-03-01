(ns
 la-linea-book.markov-chains-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.datatype.argops :as argops]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l51
 (def P (la/matrix [[0.7 0.2 0.1] [0.3 0.4 0.3] [0.2 0.3 0.5]])))


(def
 v5_l58
 (kind/mermaid
  "graph LR\n  S[\"Sunny\"] -->|0.7| S\n  S -->|0.2| C[\"Cloudy\"]\n  S -->|0.1| R[\"Rainy\"]\n  C -->|0.3| S\n  C -->|0.4| C\n  C -->|0.3| R\n  R -->|0.2| S\n  R -->|0.3| C\n  R -->|0.5| R"))


(def v7_l72 (la/mmul P (la/column (repeat 3 1.0))))


(deftest
 t8_l74
 (is
  ((fn
    [sums]
    (< (la/norm (la/sub sums (la/column (repeat 3 1.0)))) 1.0E-10))
   v7_l72)))


(def v10_l88 (def initial-state (la/row [1.0 0.0 0.0])))


(def
 v11_l90
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
 v13_l103
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
 v15_l114
 (let
  [last-state (last walk-history)]
  [(:sunny last-state) (:cloudy last-state) (:rainy last-state)]))


(deftest
 t16_l119
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (+ (v 0) (v 1) (v 2)) 1.0)) 1.0E-10)
     (let
      [prev (nth walk-history 18)]
      (<
       (+
        (Math/abs (- (v 0) (:sunny prev)))
        (Math/abs (- (v 1) (:cloudy prev)))
        (Math/abs (- (v 2) (:rainy prev))))
       1.0E-6))))
   v15_l114)))


(def v18_l137 (def eigen-result (la/eigen (la/transpose P))))


(def
 v20_l141
 (def
  stationary-eigen
  (let
   [{:keys [eigenvalues eigenvectors]}
    eigen-result
    reals
    (cx/re eigenvalues)
    idx
    (first
     (sort-by
      (fn [i] (Math/abs (- (double (reals i)) 1.0)))
      (range (count eigenvectors))))
    ev
    (nth eigenvectors idx)
    col
    (tensor/select ev :all 0)
    total
    (dfn/sum col)]
   (vec (dfn/* col (/ 1.0 total))))))


(def v21_l151 stationary-eigen)


(deftest
 t22_l153
 (is
  ((fn
    [v]
    (and (< (Math/abs (- (dfn/sum v) 1.0)) 1.0E-10) (every? pos? v)))
   v21_l151)))


(def
 v24_l165
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
       (la/scale new-pi (/ 1.0 (dfn/sum new-pi)))
       change
       (la/norm (la/sub new-pi pi))]
      (recur
       new-pi
       (inc k)
       (conj history {:iteration (inc k), :change change}))))))))


(def
 v26_l181
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def v28_l188 (:change (last power-iteration-history)))


(deftest t29_l190 (is ((fn [c] (< c 1.0E-10)) v28_l188)))


(def
 v31_l209
 (kind/mermaid
  "graph LR\n  P0[\"Page 0\"] --> P1[\"Page 1\"]\n  P0 --> P2[\"Page 2\"]\n  P1 --> P2\n  P2 --> P0\n  P3[\"Page 3\"] --> P0\n  P3 --> P2\n  P4[\"Page 4\"] --> P0\n  P4 --> P1\n  P4 --> P2\n  P4 --> P3"))


(def
 v33_l224
 (def
  H
  (la/matrix
   [[0 1/2 1/2 0 0]
    [0 0 1 0 0]
    [1 0 0 0 0]
    [1/2 0 1/2 0 0]
    [1/4 1/4 1/4 1/4 0]])))


(def v35_l235 (def damping 0.85))


(def v36_l237 (def n-pages 5))


(def
 v37_l239
 (def
  google-matrix
  (la/add
   (la/scale
    (la/matrix (repeat n-pages (repeat n-pages 1.0)))
    (/ (- 1.0 damping) n-pages))
   (la/scale H damping))))


(def v38_l243 google-matrix)


(deftest
 t39_l245
 (is
  ((fn
    [m]
    (let
     [row-sums (la/mmul m (la/column (repeat 5 1.0)))]
     (<
      (la/norm (la/sub row-sums (la/column (repeat 5 1.0))))
      1.0E-10)))
   v38_l243)))


(def
 v41_l251
 (def
  pagerank
  (let
   [iters 50]
   (loop
    [pi (la/scale (la/row (repeat n-pages 1.0)) (/ 1.0 n-pages)) k 0]
    (if
     (>= k iters)
     pi
     (let
      [new-pi
       (la/mmul pi google-matrix)
       new-pi
       (la/scale new-pi (/ 1.0 (dfn/sum new-pi)))]
      (recur new-pi (inc k))))))))


(def
 v43_l263
 (->
  (tc/dataset
   {:page ["Page 0" "Page 1" "Page 2" "Page 3" "Page 4"],
    :rank (dtype/->reader pagerank)})
  (plotly/base {:=x :page, :=y :rank})
  (plotly/layer-bar)
  plotly/plot))


(def v45_l271 (dfn/sum pagerank))


(deftest
 t46_l273
 (is ((fn [s] (< (Math/abs (- s 1.0)) 1.0E-10)) v45_l271)))


(def v48_l277 (argops/argmax pagerank))


(deftest t49_l279 (is ((fn [idx] (contains? #{0 2} idx)) v48_l277)))
