(ns
 lalinea-book.linear-systems-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.lalinea.tensor :as t]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l60 (def n 20))


(def v4_l62 (def T-left 100.0))


(def v5_l63 (def T-right 0.0))


(def
 v7_l68
 (def
  A-heat
  (t/clone
   (t/compute-tensor
    [n n]
    (fn [i j] (cond (= i j) 2.0 (= (abs (- i j)) 1) -1.0 :else 0.0))
    :float64))))


(def v8_l78 A-heat)


(deftest t9_l80 (is ((fn [m] (= [n n] (t/shape m))) v8_l78)))


(def
 v11_l86
 (def
  b-heat
  (t/column
   (t/make-reader
    :float64
    n
    (cond (== idx 0) T-left (== idx (dec n)) T-right :else 0.0)))))


(def v12_l92 b-heat)


(deftest t13_l94 (is ((fn [b] (= [n 1] (t/shape b))) v12_l92)))


(def v15_l99 (def T-direct (la/solve A-heat b-heat)))


(def v16_l101 T-direct)


(deftest t17_l103 (is ((fn [t] (some? t)) v16_l101)))


(def
 v19_l108
 (let
  [xs
   (t/make-reader :float64 n (/ (double (inc idx)) (double (inc n))))
   expected
   (t/column
    (t/make-reader :float64 n (* 100.0 (- 1.0 (double (xs idx))))))]
  (la/close? T-direct expected)))


(deftest t20_l112 (is (true? v19_l108)))


(def
 v22_l120
 (def
  x-interior
  (t/->real-tensor
   (t/matrix
    (t/make-reader
     :float64
     n
     (/ (double (inc idx)) (double (inc n))))))))


(def
 v23_l125
 (->
  (tc/dataset
   {:x x-interior, :T (t/->reader (t/select T-direct :all 0))})
  (plotly/base {:=x :x, :=y :T})
  (plotly/layer-line)
  (plotly/layer-point {:=mark-size 5})
  plotly/plot))


(def v25_l136 (T-direct 0 0))


(deftest t26_l138 (is ((fn [t] (> t 90.0)) v25_l136)))


(def v27_l140 (T-direct (dec n) 0))


(deftest t28_l142 (is ((fn [t] (< t 10.0)) v27_l140)))


(def
 v30_l182
 (def
  gs-result
  (let
   [b-buf
    (t/->reader b-heat)
    x
    (t/make-container :float64 n)
    iters
    500]
   (loop
    [k 0 history []]
    (if
     (>= k iters)
     {:x-final (t/clone x), :history history}
     (do
      (dotimes
       [i n]
       (let
        [left
         (if (pos? i) (x (dec i)) 0.0)
         right
         (if (< i (dec n)) (x (inc i)) 0.0)]
        (t/set-value! x i (/ (+ left right (b-buf i)) 2.0))))
      (let
       [x-col
        (t/column (t/clone x))
        residual
        (la/norm (el/- (la/mmul A-heat x-col) b-heat))]
       (recur
        (inc k)
        (conj
         history
         {:iteration (inc k),
          :residual residual,
          :profile (t/clone x)})))))))))


(def
 v32_l208
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
 v34_l224
 (->
  (tc/dataset (:history gs-result))
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v36_l231 (-> gs-result :history last :residual))


(deftest t37_l233 (is ((fn [r] (< r 0.001)) v36_l231)))


(def
 v39_l241
 (let
  [x-iter (t/column (:x-final gs-result))]
  (la/norm (el/- x-iter T-direct))))


(deftest t40_l244 (is ((fn [d] (< d 0.01)) v39_l241)))
