(ns
 lalinea-book.markov-chains-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.lalinea.tensor :as t]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l50
 (def P (t/matrix [[0.7 0.2 0.1] [0.3 0.4 0.3] [0.2 0.3 0.5]])))


(def
 v5_l57
 (kind/mermaid
  "graph LR\n  S[\"Sunny\"] -->|0.7| S\n  S -->|0.2| C[\"Cloudy\"]\n  S -->|0.1| R[\"Rainy\"]\n  C -->|0.3| S\n  C -->|0.4| C\n  C -->|0.3| R\n  R -->|0.2| S\n  R -->|0.3| C\n  R -->|0.5| R"))


(def v7_l71 (la/mmul P (t/ones 3 1)))


(deftest
 t8_l73
 (is
  ((fn [sums] (< (la/norm (el/- sums (t/ones 3 1))) 1.0E-10)) v7_l71)))


(def v10_l87 (def initial-state (t/row [1.0 0.0 0.0])))


(def v11_l89 (def n-steps 20))


(def
 v12_l91
 (def
  walk-history
  (vec (take n-steps (iterate (fn [s] (la/mmul s P)) initial-state)))))


(def
 v14_l98
 (->
  (tc/dataset
   (mapcat
    (fn
     [k s]
     [{:step k, :probability (s 0 0), :state "Sunny"}
      {:step k, :probability (s 0 1), :state "Cloudy"}
      {:step k, :probability (s 0 2), :state "Rainy"}])
    (range n-steps)
    walk-history))
  (plotly/base {:=x :step, :=y :probability, :=color :state})
  (plotly/layer-line)
  plotly/plot))


(def v16_l111 (let [final (last walk-history)] (el/sum final)))


(deftest t17_l114 (is ((fn [s] (< (abs (- s 1.0)) 1.0E-10)) v16_l111)))


(def
 v19_l118
 (la/close?
  (last walk-history)
  (nth walk-history (- n-steps 2))
  1.0E-6))


(deftest t20_l120 (is (true? v19_l118)))


(def v22_l130 (def eigen-result (la/eigen (la/transpose P))))


(def
 v24_l134
 (def
  stationary-eigen
  (let
   [{:keys [eigenvalues eigenvectors]}
    eigen-result
    reals
    (el/re eigenvalues)
    idx
    (el/argmin (el/abs (el/- reals 1.0)))
    ev
    (nth eigenvectors idx)
    total
    (el/sum (t/flatten ev))]
   (t/flatten (el/scale ev (/ 1.0 total))))))


(def v25_l142 stationary-eigen)


(deftest
 t26_l144
 (is
  ((fn [v] (and (< (abs (- (el/sum v) 1.0)) 1.0E-10) (every? pos? v)))
   v25_l142)))


(def
 v28_l151
 (la/close? stationary-eigen (t/flatten (last walk-history)) 1.0E-4))


(deftest t29_l155 (is (true? v28_l151)))


(def
 v31_l163
 (def
  power-iteration-history
  (let
   [n 3 iters 40]
   (loop
    [pi (t/row [0.333 0.334 0.333]) k 0 history []]
    (if
     (>= k iters)
     history
     (let
      [new-pi
       (la/mmul pi P)
       new-pi
       (el/scale new-pi (/ 1.0 (el/sum new-pi)))
       change
       (la/norm (el/- new-pi pi))]
      (recur
       new-pi
       (inc k)
       (conj history {:iteration (inc k), :change change}))))))))


(def
 v33_l179
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def v35_l186 (:change (last power-iteration-history)))


(deftest t36_l188 (is ((fn [c] (< c 1.0E-10)) v35_l186)))


(def
 v38_l204
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


(def v39_l209 (def n-pages (count course-names)))


(def
 v40_l211
 (kind/mermaid
  "graph LR\n  subgraph Math\n    Calc[\"Calculus\"]\n    LA[\"Linear Algebra\"]\n    Stats[\"Statistics\"]\n  end\n  subgraph CS\n    IP[\"Intro Programming\"]\n    DS[\"Data Structures\"]\n    DB[\"Databases\"]\n  end\n  ML[\"Machine Learning\"]\n  AI[\"AI\"]\n  Calc --> LA\n  LA --> Calc\n  LA --> Stats\n  Stats --> Calc\n  Stats --> LA\n  IP --> DS\n  DS --> IP\n  DS --> DB\n  ML --> LA\n  ML --> Stats\n  ML --> IP\n  ML --> AI\n  DB --> DS\n  DB --> IP\n  DB --> ML\n  AI --> ML\n  AI --> DS\n  AI --> Stats\n  AI --> LA"))


(def
 v42_l249
 (def
  H
  (t/matrix
   [[0 1 0 0 0 0 0 0]
    [1/2 0 1/2 0 0 0 0 0]
    [1/2 1/2 0 0 0 0 0 0]
    [0 0 0 0 1 0 0 0]
    [0 0 0 1/2 0 0 1/2 0]
    [0 1/4 1/4 1/4 0 0 0 1/4]
    [0 0 0 1/3 1/3 1/3 0 0]
    [0 1/4 1/4 0 1/4 1/4 0 0]])))


(def v44_l263 (def damping 0.85))


(def
 v45_l265
 (def
  google-matrix
  (el/+
   (el/scale (t/ones n-pages n-pages) (/ (- 1.0 damping) n-pages))
   (el/scale H damping))))


(def v47_l272 (la/mmul google-matrix (t/ones n-pages 1)))


(deftest
 t48_l274
 (is
  ((fn [sums] (< (la/norm (el/- sums (t/ones n-pages 1))) 1.0E-10))
   v47_l272)))


(def
 v50_l279
 (def
  pagerank
  (let
   [iters 50]
   (loop
    [pi (el/scale (t/ones 1 n-pages) (/ 1.0 n-pages)) k 0]
    (if
     (>= k iters)
     pi
     (let
      [new-pi
       (la/mmul pi google-matrix)
       new-pi
       (el/scale new-pi (/ 1.0 (el/sum new-pi)))]
      (recur new-pi (inc k))))))))


(def
 v52_l292
 (->
  (tc/dataset {:course course-names, :rank (t/->reader pagerank)})
  (plotly/base {:=x :course, :=y :rank})
  (plotly/layer-bar)
  plotly/plot))


(def v54_l300 (el/sum pagerank))


(deftest t55_l302 (is ((fn [s] (< (abs (- s 1.0)) 1.0E-10)) v54_l300)))


(def v57_l307 (nth course-names (el/argmax pagerank)))


(deftest t58_l309 (is ((fn [name] (= "Linear Algebra" name)) v57_l307)))
