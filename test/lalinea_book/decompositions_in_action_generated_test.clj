(ns
 lalinea-book.decompositions-in-action-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [tech.v3.libs.buffered-image :as bufimg]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [fastmath.random :as frand]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l49 (def img-size 100))


(def
 v4_l51
 (def
  test-image
  (t/->tensor
   (t/clone
    (t/compute-tensor
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
        (* 100.0 (+ 0.5 (* 0.5 (math/sin (* 3.0 x)))))]
       (+ (* 0.6 circle) (* 0.4 gradient))))
     :float64)))))


(def
 v6_l65
 (let
  [t
   (t/compute-tensor
    [img-size img-size 3]
    (fn [r c _ch] (int (max 0 (min 255 (test-image r c)))))
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
     (t/submatrix U :all (range k))
     Sk
     (t/diag (take k S))
     Vtk
     (t/submatrix Vt (range k) :all)]
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
 v28_l151
 (let
  [errors
   (mapv
    (fn
     [k]
     (la/norm (la/sub test-image (reconstruct-rank-k svd-result k))))
    [1 5 10 20 50])]
  (every? (fn [[a b]] (> a b)) (partition 2 1 errors))))


(deftest t29_l155 (is (true? v28_l151)))


(def v31_l168 (def n-points 200))


(def
 v32_l170
 (def
  data-tensor
  (let
   [theta
    (/ math/PI 6)
    cos-t
    (math/cos theta)
    sin-t
    (math/sin theta)
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
     (t/make-container :float64))]
   (t/reshape flat [n-points 2]))))


(def
 v34_l185
 (def
  X
  (let
   [col0
    (t/select data-tensor :all 0)
    col1
    (t/select data-tensor :all 1)
    mean0
    (/ (la/sum col0) n-points)
    mean1
    (/ (la/sum col1) n-points)
    means
    (t/compute-tensor
     [n-points 2]
     (fn [_i j] (if (zero? j) mean0 mean1))
     :float64)]
   (t/clone (la/sub data-tensor means)))))


(def
 v36_l197
 (def
  cov-matrix
  (la/scale (la/mmul (la/transpose X) X) (/ 1.0 (dec n-points)))))


(def v37_l200 cov-matrix)


(deftest t38_l202 (is ((fn [m] (= [2 2] (t/shape m))) v37_l200)))


(def v40_l207 (la/close? cov-matrix (la/transpose cov-matrix)))


(deftest t41_l209 (is (true? v40_l207)))


(def v42_l211 (def pca-eigen (la/eigen cov-matrix)))


(def v44_l215 (reverse (la/real-eigenvalues cov-matrix)))


(deftest
 t45_l217
 (is
  ((fn [evs] (and (every? pos? evs) (> (first evs) (second evs))))
   v44_l215)))


(def
 v47_l227
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   reals
   (la/re eigenvalues)
   sorted-idx
   (sort-by
    (fn [i] (- (double (reals i))))
    (range (count eigenvectors)))
   lam1
   (double (reals (first sorted-idx)))
   ev1
   (nth eigenvectors (first sorted-idx))
   lam2
   (double (reals (second sorted-idx)))
   ev2
   (nth eigenvectors (second sorted-idx))
   pc1-x
   (* (math/sqrt lam1) (ev1 0 0))
   pc1-y
   (* (math/sqrt lam1) (ev1 1 0))
   pc2-x
   (* (math/sqrt lam2) (ev2 0 0))
   pc2-y
   (* (math/sqrt lam2) (ev2 1 0))
   pts
   (mapv
    (fn [i] {:x (X i 0), :y (X i 1), :type "data"})
    (range n-points))
   pc1-pts
   [{:x 0.0, :y 0.0, :type "PC1"} {:x pc1-x, :y pc1-y, :type "PC1"}]
   pc2-pts
   [{:x 0.0, :y 0.0, :type "PC2"} {:x pc2-x, :y pc2-y, :type "PC2"}]]
  (->
   (tc/dataset (concat pts pc1-pts pc2-pts))
   (plotly/base {:=x :x, :=y :y, :=color :type})
   (plotly/layer-point {:=mark-size 4, :=mark-opacity 0.4})
   (plotly/layer-line)
   plotly/plot)))


(deftest t48_l250 (is ((fn [_] true) v47_l227)))


(def
 v50_l257
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   reals
   (la/re eigenvalues)
   sorted-idx
   (sort-by
    (fn [i] (- (double (reals i))))
    (range (count eigenvectors)))
   ev1
   (nth eigenvectors (first sorted-idx))
   projected
   (la/mmul (la/mmul X ev1) (la/transpose ev1))
   variances
   (sort > reals)
   explained
   (/ (first variances) (la/sum variances))]
  explained))


(deftest t52_l270 (is ((fn [v] (> v 0.8)) v50_l257)))


(def v54_l283 (def test-matrix (t/matrix [[4 1 0] [1 3 1] [0 1 2]])))


(def v56_l290 (def true-eigenvalues (la/real-eigenvalues test-matrix)))


(def v57_l293 true-eigenvalues)


(deftest t58_l295 (is ((fn [evs] (= 3 (count evs))) v57_l293)))


(def
 v60_l300
 (def
  qr-history
  (loop
   [A test-matrix k 0 history []]
   (if
    (>= k 50)
    history
    (let
     [{:keys [Q R]}
      (la/qr A)
      A-next
      (la/mmul R Q)
      diag
      (sort (t/diag A-next))
      off-diag
      (math/sqrt
       (+
        (let [v (A-next 0 1)] (* v v))
        (let [v (A-next 1 0)] (* v v))
        (let [v (A-next 0 2)] (* v v))
        (let [v (A-next 2 0)] (* v v))
        (let [v (A-next 1 2)] (* v v))
        (let [v (A-next 2 1)] (* v v))))]
     (recur
      A-next
      (inc k)
      (conj
       history
       {:iteration (inc k),
        :off-diagonal off-diag,
        :eig-1 (nth diag 0),
        :eig-2 (nth diag 1),
        :eig-3 (nth diag 2)})))))))


(def
 v62_l328
 (->
  (tc/dataset qr-history)
  (plotly/base {:=x :iteration, :=y :off-diagonal})
  (plotly/layer-line)
  plotly/plot))


(def v63_l333 (:off-diagonal (last qr-history)))


(deftest t64_l335 (is ((fn [v] (< v 1.0E-6)) v63_l333)))


(def
 v66_l340
 (let
  [final
   (last qr-history)
   computed
   (sort [(:eig-1 final) (:eig-2 final) (:eig-3 final)])]
  (la/close?
   (t/->real-tensor computed)
   (t/->real-tensor true-eigenvalues)
   1.0E-4)))


(deftest t67_l345 (is (true? v66_l340)))


(def
 v69_l349
 (->
  (tc/dataset
   (mapcat
    (fn
     [{:keys [iteration eig-1 eig-2 eig-3]}]
     [{:iteration iteration, :value eig-1, :eigenvalue "λ₁"}
      {:iteration iteration, :value eig-2, :eigenvalue "λ₂"}
      {:iteration iteration, :value eig-3, :eigenvalue "λ₃"}])
    qr-history))
  (plotly/base {:=x :iteration, :=y :value, :=color :eigenvalue})
  (plotly/layer-line)
  plotly/plot))


(deftest t70_l358 (is ((fn [_] true) v69_l349)))
