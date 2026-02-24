(ns
 basis-book.linear-systems-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l39 (def xs (vec (range -3.0 3.1 0.3))))


(def
 v4_l41
 (def
  ys
  (mapv
   (fn [x] (+ (* 0.5 x x) (* -1.2 x) 3.0 (* 0.5 (- (rand) 0.5))))
   xs)))


(def v6_l51 (def degree 2))


(def
 v7_l53
 (def
  vandermonde
  (tensor/compute-tensor
   [(count xs) (inc degree)]
   (fn [r c] (Math/pow (nth xs r) (double c)))
   :float64)))


(def v8_l58 vandermonde)


(deftest
 t9_l60
 (is
  ((fn [m] (= [(count xs) (inc degree)] (vec (dtype/shape m))))
   v8_l58)))


(def v11_l68 (def y-col (la/column ys)))


(def
 v12_l70
 (def
  beta
  (la/solve
   (la/mmul (la/transpose vandermonde) vandermonde)
   (la/mmul (la/transpose vandermonde) y-col))))


(def v13_l74 beta)


(deftest
 t15_l79
 (is
  ((fn
    [b]
    (let
     [b0
      (tensor/mget b 0 0)
      b1
      (tensor/mget b 1 0)
      b2
      (tensor/mget b 2 0)]
     (and
      (< (Math/abs (- b0 3.0)) 0.5)
      (< (Math/abs (- b1 -1.2)) 0.5)
      (< (Math/abs (- b2 0.5)) 0.3))))
   v13_l74)))


(def
 v17_l89
 (def
  fitted-ys
  (vec
   (dtype/->reader (tensor/select (la/mmul vandermonde beta) :all 0)))))


(def
 v18_l92
 (->
  (tc/dataset
   {:x (concat xs xs),
    :y (concat ys fitted-ys),
    :series
    (concat (repeat (count xs) "data") (repeat (count xs) "fit"))})
  (plotly/base {:=x :x, :=y :y, :=color :series})
  (plotly/layer-point {:=mark-size 6})
  (plotly/layer-line)
  plotly/plot))


(def
 v20_l111
 (def weights (mapv (fn [x] (Math/exp (- (* 0.5 x x)))) xs)))


(def v21_l114 (def W (la/diag weights)))


(def
 v22_l116
 (def
  beta-weighted
  (la/solve
   (la/mmul (la/transpose vandermonde) (la/mmul W vandermonde))
   (la/mmul (la/transpose vandermonde) (la/mmul W y-col)))))


(def v23_l120 beta-weighted)


(deftest
 t24_l122
 (is
  ((fn
    [b]
    (let [b0 (tensor/mget b 0 0)] (< (Math/abs (- b0 3.0)) 0.5)))
   v23_l120)))


(def v26_l142 (def A-gs (la/matrix [[10 1 2] [1 12 3] [2 3 15]])))


(def v27_l147 (def b-gs (la/column [13 16 20])))


(def v29_l151 (def x-direct (la/solve A-gs b-gs)))


(def v30_l153 x-direct)


(deftest t31_l155 (is ((fn [x] (some? x)) v30_l153)))


(def
 v33_l159
 (def
  gauss-seidel-history
  (let
   [n
    3
    A-arr
    (dtype/->double-array A-gs)
    b-arr
    (dtype/->double-array b-gs)
    x
    (double-array n 0.0)
    iters
    30]
   (loop
    [k 0 history []]
    (if
     (>= k iters)
     history
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
        (la/norm (la/sub (la/mmul A-gs x-tensor) b-gs))]
       (recur
        (inc k)
        (conj history {:iteration (inc k), :residual residual})))))))))


(def
 v35_l187
 (->
  (tc/dataset gauss-seidel-history)
  (plotly/base {:=x :iteration, :=y :residual})
  (plotly/layer-line)
  plotly/plot))


(def v37_l194 (-> gauss-seidel-history last :residual))


(deftest t38_l196 (is ((fn [r] (< r 1.0E-10)) v37_l194)))


(def
 v40_l202
 (let
  [x-iter
   (let
    [n
     3
     A-arr
     (dtype/->double-array A-gs)
     b-arr
     (dtype/->double-array b-gs)
     x
     (double-array n 0.0)]
    (dotimes
     [_ 50]
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
        (/ (- (aget b-arr i) sigma) (aget A-arr (+ (* i n) i)))))))
    (tensor/reshape (tensor/ensure-tensor (dtype/clone x)) [n 1]))]
  (la/norm (la/sub x-iter x-direct))))


(deftest t41_l219 (is ((fn [d] (< d 1.0E-10)) v40_l202)))
