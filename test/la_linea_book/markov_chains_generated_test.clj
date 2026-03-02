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
     (< (abs (- (+ (v 0) (v 1) (v 2)) 1.0)) 1.0E-10)
     (let
      [prev (nth walk-history 18)]
      (<
       (+
        (abs (- (v 0) (:sunny prev)))
        (abs (- (v 1) (:cloudy prev)))
        (abs (- (v 2) (:rainy prev))))
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
      (fn [i] (abs (- (double (reals i)) 1.0)))
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
  ((fn [v] (and (< (abs (- (dfn/sum v) 1.0)) 1.0E-10) (every? pos? v)))
   v21_l151)))


(def
 v24_l160
 (every?
  (fn [[eigen walk]] (< (abs (- (double eigen) (double walk))) 1.0E-4))
  (map
   vector
   stationary-eigen
   (let [s (last walk-history)] [(:sunny s) (:cloudy s) (:rainy s)]))))


(deftest t25_l166 (is (true? v24_l160)))


(def
 v27_l176
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
 v29_l192
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def v31_l199 (:change (last power-iteration-history)))


(deftest t32_l201 (is ((fn [c] (< c 1.0E-10)) v31_l199)))


(def
 v34_l217
 (def
  course-names
  ["Calculus"
   "Linear Algebra"
   "Statistics"
   "Intro Programming"
   "Data Structures"
   "Machine Learning"
   "Databases"
   "AI"]))


(def v35_l222 (def n-pages (count course-names)))


(def
 v36_l224
 (kind/mermaid
  "graph LR\n  subgraph Math\n    Calc[\"Calculus\"]\n    LA[\"Linear Algebra\"]\n    Stats[\"Statistics\"]\n  end\n  subgraph CS\n    IP[\"Intro Programming\"]\n    DS[\"Data Structures\"]\n    DB[\"Databases\"]\n  end\n  ML[\"Machine Learning\"]\n  AI[\"AI\"]\n  Calc --> LA\n  LA --> Calc\n  LA --> Stats\n  Stats --> Calc\n  Stats --> LA\n  IP --> DS\n  DS --> IP\n  DS --> DB\n  ML --> LA\n  ML --> Stats\n  ML --> IP\n  ML --> AI\n  DB --> DS\n  DB --> IP\n  DB --> ML\n  AI --> ML\n  AI --> DS\n  AI --> Stats\n  AI --> LA"))


(def
 v38_l262
 (def
  H
  (la/matrix
   [[0 1 0 0 0 0 0 0]
    [1/2 0 1/2 0 0 0 0 0]
    [1/2 1/2 0 0 0 0 0 0]
    [0 0 0 0 1 0 0 0]
    [0 0 0 1/2 0 0 1/2 0]
    [0 1/4 1/4 1/4 0 0 0 1/4]
    [0 0 0 1/3 1/3 1/3 0 0]
    [0 1/4 1/4 0 1/4 1/4 0 0]])))


(def v40_l276 (def damping 0.85))


(def
 v41_l278
 (def
  google-matrix
  (la/add
   (la/scale
    (la/matrix (repeat n-pages (repeat n-pages 1.0)))
    (/ (- 1.0 damping) n-pages))
   (la/scale H damping))))


(def v43_l285 (la/mmul google-matrix (la/column (repeat n-pages 1.0))))


(deftest
 t44_l287
 (is
  ((fn
    [sums]
    (<
     (la/norm (la/sub sums (la/column (repeat n-pages 1.0))))
     1.0E-10))
   v43_l285)))


(def
 v46_l292
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
 v48_l305
 (->
  (tc/dataset {:course course-names, :rank (dtype/->reader pagerank)})
  (plotly/base {:=x :course, :=y :rank})
  (plotly/layer-bar)
  plotly/plot))


(def v50_l313 (dfn/sum pagerank))


(deftest t51_l315 (is ((fn [s] (< (abs (- s 1.0)) 1.0E-10)) v50_l313)))


(def v53_l320 (nth course-names (argops/argmax pagerank)))


(deftest t54_l322 (is ((fn [name] (= "Linear Algebra" name)) v53_l320)))
