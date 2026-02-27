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


(def v13_l95 (def T-direct (la/solve A-heat b-heat)))


(def v14_l97 T-direct)


(deftest t15_l99 (is ((fn [t] (some? t)) v14_l97)))


(def
 v17_l108
 (def
  x-interior
  (mapv (fn [i] (/ (double (inc i)) (double (inc n)))) (range n))))


(def
 v18_l112
 (->
  (tc/dataset
   {:x x-interior,
    :T (dtype/->reader (tensor/select T-direct :all 0))})
  (plotly/base {:=x :x, :=y :T})
  (plotly/layer-line)
  (plotly/layer-point {:=mark-size 5})
  plotly/plot))


(def v20_l123 (tensor/mget T-direct 0 0))


(deftest t21_l125 (is ((fn [t] (> t 90.0)) v20_l123)))


(def v22_l127 (tensor/mget T-direct (dec n) 0))


(deftest t23_l129 (is ((fn [t] (< t 10.0)) v22_l127)))


(def
 v25_l158
 (def
  gs-result
  (let
   [A-arr
    (dtype/->double-array A-heat)
    b-arr
    (dtype/->double-array b-heat)
    x
    (double-array n 0.0)
    iters
    500]
   (loop
    [k 0 history []]
    (if
     (>= k iters)
     {:x-final (vec x), :history history}
     (do
      (dotimes
       [i n]
       (let
        [sigma
         (loop
          [j 0 s 0.0]
          (if
           (>= j n)
           s
           (recur
            (inc j)
            (if
             (= i j)
             s
             (+ s (* (aget A-arr (+ (* i n) j)) (aget x j)))))))]
        (aset
         x
         i
         (/ (- (aget b-arr i) sigma) (aget A-arr (+ (* i n) i))))))
      (let
       [x-tensor
        (tensor/reshape (tensor/ensure-tensor (dtype/clone x)) [n 1])
        residual
        (la/norm (la/sub (la/mmul A-heat x-tensor) b-heat))]
       (recur
        (inc k)
        (conj
         history
         {:iteration (inc k),
          :residual residual,
          :profile (vec x)})))))))))


(def
 v27_l193
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
 v29_l209
 (->
  (tc/dataset (:history gs-result))
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v31_l216 (-> gs-result :history last :residual))


(deftest t32_l218 (is ((fn [r] (< r 0.001)) v31_l216)))


(def
 v34_l226
 (let
  [x-iter (la/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct))))


(deftest t35_l229 (is ((fn [d] (< d 0.01)) v34_l226)))
