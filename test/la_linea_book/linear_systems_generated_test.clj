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


(deftest t9_l82 (is ((fn [m] (= [n n] (vec (dtype/shape m)))) v8_l80)))


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


(deftest
 t13_l96
 (is ((fn [b] (= [n 1] (vec (dtype/shape b)))) v12_l94)))


(def v15_l101 (def T-direct (la/solve A-heat b-heat)))


(def v16_l103 T-direct)


(deftest t17_l105 (is ((fn [t] (some? t)) v16_l103)))


(def
 v19_l114
 (def
  x-interior
  (mapv (fn [i] (/ (double (inc i)) (double (inc n)))) (range n))))


(def
 v20_l118
 (->
  (tc/dataset
   {:x x-interior,
    :T (dtype/->reader (tensor/select T-direct :all 0))})
  (plotly/base {:=x :x, :=y :T})
  (plotly/layer-line)
  (plotly/layer-point {:=mark-size 5})
  plotly/plot))


(def v22_l129 (tensor/mget T-direct 0 0))


(deftest t23_l131 (is ((fn [t] (> t 90.0)) v22_l129)))


(def v24_l133 (tensor/mget T-direct (dec n) 0))


(deftest t25_l135 (is ((fn [t] (< t 10.0)) v24_l133)))


(def
 v27_l167
 (def
  gs-result
  (let
   [b-arr
    (dtype/->double-array b-heat)
    x
    (double-array n 0.0)
    iters
    500]
   (loop
    [k 0 history []]
    (if
     (>= k iters)
     {:x-final (dtype/make-container :float64 x), :history history}
     (do
      (dotimes
       [i n]
       (let
        [left
         (if (pos? i) (aget x (dec i)) 0.0)
         right
         (if (< i (dec n)) (aget x (inc i)) 0.0)]
        (aset x i (/ (+ left right (aget b-arr i)) 2.0))))
      (let
       [x-col
        (la/column (dtype/make-container :float64 x))
        residual
        (la/norm (la/sub (la/mmul A-heat x-col) b-heat))]
       (recur
        (inc k)
        (conj
         history
         {:iteration (inc k),
          :residual residual,
          :profile (dtype/make-container :float64 x)})))))))))


(def
 v29_l193
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
 v31_l209
 (->
  (tc/dataset (:history gs-result))
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v33_l216 (-> gs-result :history last :residual))


(deftest t34_l218 (is ((fn [r] (< r 0.001)) v33_l216)))


(def
 v36_l226
 (let
  [x-iter (la/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct))))


(deftest t37_l229 (is ((fn [d] (< d 0.01)) v36_l226)))
