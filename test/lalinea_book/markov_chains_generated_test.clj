(ns
 lalinea-book.markov-chains-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.lalinea.tensor :as t]
  [tech.v3.datatype.argops :as argops]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l52
 (def P (t/matrix [[0.7 0.2 0.1] [0.3 0.4 0.3] [0.2 0.3 0.5]])))


(def
 v5_l59
 (kind/mermaid
  "graph LR\n  S[\"Sunny\"] -->|0.7| S\n  S -->|0.2| C[\"Cloudy\"]\n  S -->|0.1| R[\"Rainy\"]\n  C -->|0.3| S\n  C -->|0.4| C\n  C -->|0.3| R\n  R -->|0.2| S\n  R -->|0.3| C\n  R -->|0.5| R"))


(def v7_l73 (la/mmul P (t/ones 3 1)))


(deftest
 t8_l75
 (is
  ((fn [sums] (< (la/norm (el/- sums (t/ones 3 1))) 1.0E-10)) v7_l73)))


(def v10_l89 (def initial-state (t/row [1.0 0.0 0.0])))


(def
 v11_l91
 (def
  walk-history
  (let
   [states (iterate (fn [s] (la/mmul s P)) initial-state)]
   (mapv
    (fn
     [k s]
     {:step k, :sunny (s 0 0), :cloudy (s 0 1), :rainy (s 0 2)})
    (range 20)
    (take 20 states)))))


(def
 v13_l104
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
 v15_l115
 (let
  [last-state (last walk-history)]
  [(:sunny last-state) (:cloudy last-state) (:rainy last-state)]))


(deftest
 t16_l120
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
   v15_l115)))


(def v18_l138 (def eigen-result (la/eigen (la/transpose P))))


(def
 v20_l142
 (def
  stationary-eigen
  (let
   [{:keys [eigenvalues eigenvectors]}
    eigen-result
    reals
    (el/re eigenvalues)
    idx
    (first
     (sort-by
      (fn [i] (abs (- (double (reals i)) 1.0)))
      (range (count eigenvectors))))
    ev
    (nth eigenvectors idx)
    total
    (el/sum (t/flatten ev))]
   (t/flatten (el/scale ev (/ 1.0 total))))))


(def v21_l151 stationary-eigen)


(deftest
 t22_l153
 (is
  ((fn [v] (and (< (abs (- (el/sum v) 1.0)) 1.0E-10) (every? pos? v)))
   v21_l151)))


(def
 v24_l160
 (let
  [s (last walk-history)]
  (la/close?
   stationary-eigen
   (t/matrix [(:sunny s) (:cloudy s) (:rainy s)])
   1.0E-4)))


(deftest t25_l165 (is (true? v24_l160)))


(def
 v27_l173
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
 v29_l189
 (->
  (tc/dataset power-iteration-history)
  (plotly/base {:=x :iteration, :=y :change})
  (plotly/layer-line)
  plotly/plot))


(def v31_l196 (:change (last power-iteration-history)))


(deftest t32_l198 (is ((fn [c] (< c 1.0E-10)) v31_l196)))


(def
 v34_l214
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


(def v35_l219 (def n-pages (count course-names)))


(def
 v36_l221
 (kind/mermaid
  "graph LR\n  subgraph Math\n    Calc[\"Calculus\"]\n    LA[\"Linear Algebra\"]\n    Stats[\"Statistics\"]\n  end\n  subgraph CS\n    IP[\"Intro Programming\"]\n    DS[\"Data Structures\"]\n    DB[\"Databases\"]\n  end\n  ML[\"Machine Learning\"]\n  AI[\"AI\"]\n  Calc --> LA\n  LA --> Calc\n  LA --> Stats\n  Stats --> Calc\n  Stats --> LA\n  IP --> DS\n  DS --> IP\n  DS --> DB\n  ML --> LA\n  ML --> Stats\n  ML --> IP\n  ML --> AI\n  DB --> DS\n  DB --> IP\n  DB --> ML\n  AI --> ML\n  AI --> DS\n  AI --> Stats\n  AI --> LA"))


(def
 v38_l259
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


(def v40_l273 (def damping 0.85))


(def
 v41_l275
 (def
  google-matrix
  (el/+
   (el/scale (t/ones n-pages n-pages) (/ (- 1.0 damping) n-pages))
   (el/scale H damping))))


(def v43_l282 (la/mmul google-matrix (t/ones n-pages 1)))


(deftest
 t44_l284
 (is
  ((fn [sums] (< (la/norm (el/- sums (t/ones n-pages 1))) 1.0E-10))
   v43_l282)))


(def
 v46_l289
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
 v48_l302
 (->
  (tc/dataset {:course course-names, :rank (t/->reader pagerank)})
  (plotly/base {:=x :course, :=y :rank})
  (plotly/layer-bar)
  plotly/plot))


(def v50_l310 (el/sum pagerank))


(deftest t51_l312 (is ((fn [s] (< (abs (- s 1.0)) 1.0E-10)) v50_l310)))


(def v53_l317 (nth course-names (argops/argmax pagerank)))


(deftest t54_l319 (is ((fn [name] (= "Linear Algebra" name)) v53_l317)))
