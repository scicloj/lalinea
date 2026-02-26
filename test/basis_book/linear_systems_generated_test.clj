(ns
 basis-book.linear-systems-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l63 (def n 20))


(def v4_l65 (def T-left 100.0))


(def v5_l66 (def T-right 0.0))


(def
 v7_l71
 (def
  A-heat
  (dtype/clone
   (tensor/compute-tensor
    [n n]
    (fn
     [i j]
     (cond (= i j) 2.0 (= (Math/abs (- i j)) 1) -1.0 :else 0.0))
    :float64))))


(def v8_l81 A-heat)


(deftest t9_l83 (is ((fn [m] (= [n n] (vec (dtype/shape m)))) v8_l81)))


(def
 v11_l89
 (def
  b-heat
  (let
   [b (double-array n 0.0)]
   (aset b 0 T-left)
   (aset b (dec n) T-right)
   (la/column (vec b)))))


(def v13_l97 (def T-direct (la/solve A-heat b-heat)))


(def v14_l99 T-direct)


(deftest t15_l101 (is ((fn [t] (some? t)) v14_l99)))


(def
 v17_l110
 (def
  x-interior
  (mapv (fn [i] (/ (double (inc i)) (double (inc n)))) (range n))))


(def
 v18_l114
 (->
  (tc/dataset
   {:x x-interior,
    :T (vec (dtype/->reader (tensor/select T-direct :all 0)))})
  (plotly/base {:=x :x, :=y :T})
  (plotly/layer-line)
  (plotly/layer-point {:=mark-size 5})
  plotly/plot))


(def v20_l125 (tensor/mget T-direct 0 0))


(deftest t21_l127 (is ((fn [t] (> t 90.0)) v20_l125)))


(def v22_l129 (tensor/mget T-direct (dec n) 0))


(deftest t23_l131 (is ((fn [t] (< t 10.0)) v22_l129)))


(def
 v25_l160
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
 v27_l195
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
 v29_l211
 (->
  (tc/dataset (:history gs-result))
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v31_l218 (-> gs-result :history last :residual))


(deftest t32_l220 (is ((fn [r] (< r 0.001)) v31_l218)))


(def
 v34_l228
 (let
  [x-iter (la/column (:x-final gs-result))]
  (la/norm (la/sub x-iter T-direct))))


(deftest t35_l231 (is ((fn [d] (< d 0.01)) v34_l228)))
