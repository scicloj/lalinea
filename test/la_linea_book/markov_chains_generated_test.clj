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


(def v5_l58 (la/mmul P (la/column (repeat 3 1.0))))


(deftest
 t6_l60
 (is
  ((fn
    [sums]
    (< (la/norm (la/sub sums (la/column (repeat 3 1.0)))) 1.0E-10))
   v5_l58)))


(def v8_l74 (def initial-state (la/row [1.0 0.0 0.0])))


(def
 v9_l76
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
 v11_l89
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
 v13_l100
 (let
  [last-state (last walk-history)]
  [(:sunny last-state) (:cloudy last-state) (:rainy last-state)]))


(deftest
 t14_l105
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
   v13_l100)))


(def v16_l123 (def eigen-result (la/eigen (la/transpose P))))


(def
 v18_l127
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


(def v19_l137 stationary-eigen)


(deftest
 t20_l139
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (dfn/sum (double-array v)) 1.0)) 1.0E-10)
     (every? pos? v)))
   v19_l137)))


(def
 v22_l151
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
 v24_l167
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def v26_l174 (:change (last power-iteration-history)))


(deftest t27_l176 (is ((fn [c] (< c 1.0E-10)) v26_l174)))


(def
 v29_l197
 (def
  H
  (la/matrix
   [[0 1/2 1/2 0 0]
    [0 0 1 0 0]
    [1 0 0 0 0]
    [1/2 0 1/2 0 0]
    [1/4 1/4 1/4 1/4 0]])))


(def v31_l208 (def damping 0.85))


(def v32_l210 (def n-pages 5))


(def
 v33_l212
 (def
  google-matrix
  (la/add
   (la/scale
    (la/matrix (repeat n-pages (repeat n-pages 1.0)))
    (/ (- 1.0 damping) n-pages))
   (la/scale H damping))))


(def v34_l216 google-matrix)


(deftest
 t35_l218
 (is
  ((fn
    [m]
    (let
     [row-sums (la/mmul m (la/column (repeat 5 1.0)))]
     (<
      (la/norm (la/sub row-sums (la/column (repeat 5 1.0))))
      1.0E-10)))
   v34_l216)))


(def
 v37_l224
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
 v39_l236
 (->
  (tc/dataset
   {:page ["Page 0" "Page 1" "Page 2" "Page 3" "Page 4"],
    :rank (dtype/->reader pagerank)})
  (plotly/base {:=x :page, :=y :rank})
  (plotly/layer-bar)
  plotly/plot))


(def v41_l244 (dfn/sum pagerank))


(deftest
 t42_l246
 (is ((fn [s] (< (Math/abs (- s 1.0)) 1.0E-10)) v41_l244)))


(def v44_l250 (argops/argmax pagerank))


(deftest t45_l252 (is ((fn [idx] (contains? #{0 2} idx)) v44_l250)))
