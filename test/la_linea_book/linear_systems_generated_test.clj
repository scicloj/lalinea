(ns
 la-linea-book.linear-systems-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l62 (def n 20))


(def v4_l64 (def T-left 100.0))


(def v5_l65 (def T-right 0.0))


(def
 v7_l70
 (def
  A-heat
  (dtype/clone
   (tensor/compute-tensor
    [n n]
    (fn
     [i j]
     (cond (= i j) 2.0 (= (Math/abs (- i j)) 1) -1.0 :else 0.0))
    :float64))))


(def v8_l80 A-heat)


(deftest t9_l82 (is ((fn [m] (= [n n] (dtype/shape m))) v8_l80)))


(def
 v11_l88
 (def
  b-heat
  (la/column
   (dtype/make-reader
    :float64
    n
    (cond (== idx 0) T-left (== idx (dec n)) T-right :else 0.0)))))


(def v12_l94 b-heat)


(deftest t13_l96 (is ((fn [b] (= [n 1] (dtype/shape b))) v12_l94)))


(def v15_l101 (def T-direct (la/solve A-heat b-heat)))


(def v16_l103 T-direct)


(deftest t17_l105 (is ((fn [t] (some? t)) v16_l103)))


(def
 v19_l110
 (let
  [xs
   (mapv (fn [i] (/ (double (inc i)) (double (inc n)))) (range n))
   expected
   (la/column (mapv (fn [x] (* 100.0 (- 1.0 x))) xs))]
  (la/close? T-direct expected)))


(deftest t20_l114 (is (true? v19_l110)))


(def
 v22_l122
 (def
  x-interior
  (mapv (fn [i] (/ (double (inc i)) (double (inc n)))) (range n))))


(def
 v23_l126
 (->
  (tc/dataset
   {:x x-interior,
    :T (dtype/->reader (tensor/select T-direct :all 0))})
  (plotly/base {:=x :x, :=y :T})
  (plotly/layer-line)
  (plotly/layer-point {:=mark-size 5})
  plotly/plot))


(def v25_l137 (tensor/mget T-direct 0 0))


(deftest t26_l139 (is ((fn [t] (> t 90.0)) v25_l137)))


(def v27_l141 (tensor/mget T-direct (dec n) 0))


(deftest t28_l143 (is ((fn [t] (< t 10.0)) v27_l141)))


(def
 v30_l183
 (def
  gs-result
  (let
   [b-buf
    (dtype/->reader b-heat)
    x
    (dtype/make-container :float64 n)
    iters
    500]
   (loop
    [k 0 history []]
    (if
     (>= k iters)
     {:x-final (dtype/clone x), :history history}
     (do
      (dotimes
       [i n]
       (let
        [left
         (if (pos? i) (x (dec i)) 0.0)
         right
         (if (< i (dec n)) (x (inc i)) 0.0)]
        (dtype/set-value! x i (/ (+ left right (b-buf i)) 2.0))))
      (let
       [x-col
        (la/column (dtype/clone x))
        residual
        (la/norm (la/sub (la/mmul A-heat x-col) b-heat))]
       (recur
        (inc k)
        (conj
         history
         {:iteration (inc k),
          :residual residual,
          :profile (dtype/clone x)})))))))))


(def
 v32_l209
 (let
  [snapshots
   [1 2 5 10 50 200 500]
   rows
   (mapcat
    (fn
     [iter]
     (let
      [profile (:profile (nth (:history gs-result) (dec iter)))]
      (map
       (fn [x T] {:x x, :T T, :iteration (str "k=" iter)})
       x-interior
       profile)))
    snapshots)]
  (->
   (tc/dataset rows)
   (plotly/base {:=x :x, :=y :T, :=color :iteration})
   (plotly/layer-line)
   plotly/plot)))


(def
 v34_l225
 (->
  (tc/dataset (:history gs-result))
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v36_l232 (-> gs-result :history last :residual))


(deftest t37_l234 (is ((fn [r] (< r 0.001)) v36_l232)))


(def
 v39_l242
 (let
  [x-iter (la/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct))))


(deftest t40_l245 (is ((fn [d] (< d 0.01)) v39_l242)))
