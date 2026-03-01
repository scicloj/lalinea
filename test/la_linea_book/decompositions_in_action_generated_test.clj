(ns
 la-linea-book.decompositions-in-action-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [fastmath.random :as frand]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.la-linea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def v3_l49 (def img-size 100))


(def
 v4_l51
 (def
  test-image
  (tensor/ensure-tensor
   (dtype/clone
    (tensor/compute-tensor
     [img-size img-size]
     (fn
      [r c]
      (let
       [x
        (/ (- c 50.0) 50.0)
        y
        (/ (- r 50.0) 50.0)
        circle
        (if (< (+ (* x x) (* y y)) 0.5) 200.0 50.0)
        gradient
        (* 100.0 (+ 0.5 (* 0.5 (Math/sin (* 3.0 x)))))]
       (+ (* 0.6 circle) (* 0.4 gradient))))
     :float64)))))


(def
 v6_l65
 (let
  [t
   (tensor/compute-tensor
    [img-size img-size 3]
    (fn [r c _ch] (int (max 0 (min 255 (tensor/mget test-image r c)))))
    :uint8)]
  (bufimg/tensor->image t)))


(deftest
 t7_l71
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v6_l65)))


(def v9_l76 (def svd-result (la/svd test-image)))


(def v11_l81 (:S svd-result))


(deftest
 t12_l83
 (is
  ((fn
    [sv]
    (and
     (> (first sv) (nth sv 5) (nth sv 20))
     (> (first sv) (* 10 (nth sv 5)))))
   v11_l81)))


(def
 v13_l87
 (->
  (tc/dataset
   {:index (range (count (:S svd-result))),
    :singular-value (:S svd-result)})
  (plotly/base {:=x :index, :=y :singular-value})
  (plotly/layer-line)
  plotly/plot))


(def
 v15_l99
 (def
  reconstruct-rank-k
  (fn
   [svd-result k]
   (let
    [{:keys [U S Vt]}
     svd-result
     Uk
     (la/submatrix U :all (range k))
     Sk
     (la/diag (take k S))
     Vtk
     (la/submatrix Vt (range k) :all)]
    (la/mmul (la/mmul Uk Sk) Vtk)))))


(def
 v17_l111
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 1))))


(deftest
 t18_l113
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v17_l111)))


(def
 v20_l118
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 5))))


(deftest
 t21_l120
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v20_l118)))


(def
 v23_l125
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 20))))


(deftest
 t24_l127
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v23_l125)))


(def
 v26_l135
 (->
  (tc/dataset
   {:k [1 5 10 20 50],
    :ratio
    (mapv
     (fn
      [k]
      (/ (* 1.0 k (+ img-size img-size 1)) (* img-size img-size)))
     [1 5 10 20 50]),
    :error
    (mapv
     (fn
      [k]
      (la/norm (la/sub test-image (reconstruct-rank-k svd-result k))))
     [1 5 10 20 50])})
  (plotly/base {:=x :ratio, :=y :error})
  (plotly/layer-point {:=mark-size 10})
  (plotly/layer-line)
  plotly/plot))


(def
 v28_l153
 (let
  [errors
   (mapv
    (fn
     [k]
     (la/norm (la/sub test-image (reconstruct-rank-k svd-result k))))
    [1 5 10 20 50])]
  (every? (fn [[a b]] (> a b)) (partition 2 1 errors))))


(deftest t29_l157 (is (true? v28_l153)))


(def v31_l168 (def n-points 200))


(def
 v32_l170
 (def
  data-tensor
  (let
   [theta
    (/ Math/PI 6)
    cos-t
    (Math/cos theta)
    sin-t
    (Math/sin theta)
    rng
    (frand/rng :mersenne 42)
    flat
    (->>
     (range n-points)
     (mapcat
      (fn
       [_]
       (let
        [p1 (frand/grandom rng 0.0 3.0) p2 (frand/grandom rng 0.0 0.8)]
        [(+ (* cos-t p1) (* (- sin-t) p2))
         (+ (* sin-t p1) (* cos-t p2))])))
     (dtype/make-container :float64))]
   (tensor/reshape flat [n-points 2]))))


(def
 v34_l185
 (def
  X
  (let
   [col0
    (tensor/select data-tensor :all 0)
    col1
    (tensor/select data-tensor :all 1)
    mean0
    (/ (dfn/sum col0) n-points)
    mean1
    (/ (dfn/sum col1) n-points)
    means
    (tensor/compute-tensor
     [n-points 2]
     (fn [_i j] (if (zero? j) mean0 mean1))
     :float64)]
   (dtype/clone (la/sub data-tensor means)))))


(def
 v36_l197
 (def
  cov-matrix
  (la/scale (la/mmul (la/transpose X) X) (/ 1.0 (dec n-points)))))
