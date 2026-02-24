(ns
 basis-book.decompositions-in-action-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l37 (def img-size 100))


(def
 v4_l39
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
 v6_l53
 (let
  [t
   (tensor/compute-tensor
    [img-size img-size 3]
    (fn [r c _ch] (int (max 0 (min 255 (tensor/mget test-image r c)))))
    :uint8)]
  (bufimg/tensor->image t)))


(deftest
 t7_l59
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v6_l53)))


(def v9_l64 (def svd-result (la/svd test-image)))


(def
 v11_l69
 (let
  [S (:S svd-result)]
  (->
   (tc/dataset {:index (range (count S)), :singular-value S})
   (plotly/base {:=x :index, :=y :singular-value})
   (plotly/layer-line)
   plotly/plot)))


(def
 v13_l82
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
 v15_l92
 (def
  matrix->gray-image
  (fn
   [m]
   (let
    [[h w] (dtype/shape m)]
    (tensor/compute-tensor
     [h w 3]
     (fn [r c _ch] (int (max 0 (min 255 (tensor/mget m r c)))))
     :uint8)))))


(def
 v17_l102
 (bufimg/tensor->image
  (matrix->gray-image (reconstruct-rank-k svd-result 1))))


(deftest
 t18_l104
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v17_l102)))


(def
 v20_l109
 (bufimg/tensor->image
  (matrix->gray-image (reconstruct-rank-k svd-result 5))))


(deftest
 t21_l111
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v20_l109)))


(def
 v23_l116
 (bufimg/tensor->image
  (matrix->gray-image (reconstruct-rank-k svd-result 20))))


(deftest
 t24_l118
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v23_l116)))


(def
 v26_l126
 (let
  [n img-size m img-size]
  (->
   (tc/dataset
    {:k [1 5 10 20 50],
     :ratio
     (mapv (fn [k] (/ (* 1.0 k (+ n m 1)) (* n m))) [1 5 10 20 50]),
     :error
     (mapv
      (fn
       [k]
       (la/norm (la/sub test-image (reconstruct-rank-k svd-result k))))
      [1 5 10 20 50])})
   (plotly/base {:=x :ratio, :=y :error})
   (plotly/layer-point {:=mark-size 10})
   (plotly/layer-line)
   plotly/plot)))


(def v28_l152 (def n-points 200))


(def
 v29_l154
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
    (java.util.Random. 42)
    arr
    (double-array (* n-points 2))]
   (dotimes
    [i n-points]
    (let
     [p1
      (* 3.0 (.nextGaussian rng))
      p2
      (* 0.8 (.nextGaussian rng))
      x
      (+ (* cos-t p1) (* (- sin-t) p2))
      y
      (+ (* sin-t p1) (* cos-t p2))]
     (aset arr (* i 2) x)
     (aset arr (inc (* i 2)) y)))
   (tensor/reshape (tensor/ensure-tensor arr) [n-points 2]))))


(def
 v31_l170
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
   (dtype/clone (dfn/- data-tensor means)))))


(def
 v33_l182
 (def
  cov-matrix
  (la/scale (/ 1.0 (dec n-points)) (la/mmul (la/transpose X) X))))


(def v34_l186 cov-matrix)


(deftest
 t35_l188
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v34_l186)))


(def v36_l191 (def pca-eigen (la/eigen cov-matrix)))


(def
 v38_l195
 (let [{:keys [eigenvalues]} pca-eigen] (sort-by first > eigenvalues)))


(deftest
 t39_l198
 (is ((fn [evs] (> (ffirst evs) (first (second evs)))) v38_l195)))


(def
 v41_l205
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   sorted
   (sort-by first > (map vector (map first eigenvalues) eigenvectors))
   [lam1 ev1]
   (first sorted)
   [lam2 ev2]
   (second sorted)
   pc1-x
   (* (Math/sqrt lam1) (tensor/mget ev1 0 0))
   pc1-y
   (* (Math/sqrt lam1) (tensor/mget ev1 1 0))
   pc2-x
   (* (Math/sqrt lam2) (tensor/mget ev2 0 0))
   pc2-y
   (* (Math/sqrt lam2) (tensor/mget ev2 1 0))
   pts
   (map
    (fn
     [i]
     {:x (tensor/mget X i 0), :y (tensor/mget X i 1), :type "data"})
    (range n-points))
   arrows
   [{:x 0.0, :y 0.0, :type "PC1-start"}
    {:x pc1-x, :y pc1-y, :type "PC1-end"}
    {:x 0.0, :y 0.0, :type "PC2-start"}
    {:x pc2-x, :y pc2-y, :type "PC2-end"}]]
  (->
   (tc/dataset (concat pts))
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 4, :=mark-opacity 0.4})
   plotly/plot)))


(deftest t42_l231 (is ((fn [_] true) v41_l205)))


(def
 v44_l238
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   sorted
   (sort-by first > (map vector (map first eigenvalues) eigenvectors))
   ev1
   (second (first sorted))
   projected
   (la/mmul (la/mmul X ev1) (la/transpose ev1))
   variances
   (sort > (map first eigenvalues))
   explained
   (/ (first variances) (reduce + variances))]
  explained))


(deftest t46_l251 (is ((fn [v] (> v 0.8)) v44_l238)))


(def v48_l264 (def test-matrix (la/matrix [[4 1 0] [1 3 1] [0 1 2]])))


(def
 v50_l271
 (def
  true-eigenvalues
  (sort (map first (:eigenvalues (la/eigen test-matrix))))))


(def v51_l274 true-eigenvalues)


(deftest t52_l276 (is ((fn [evs] (= 3 (count evs))) v51_l274)))


(def
 v54_l281
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
      (sort (mapv (fn [i] (tensor/mget A-next i i)) (range 3)))
      off-diag
      (Math/sqrt
       (+
        (let [v (tensor/mget A-next 0 1)] (* v v))
        (let [v (tensor/mget A-next 1 0)] (* v v))
        (let [v (tensor/mget A-next 0 2)] (* v v))
        (let [v (tensor/mget A-next 2 0)] (* v v))
        (let [v (tensor/mget A-next 1 2)] (* v v))
        (let [v (tensor/mget A-next 2 1)] (* v v))))]
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
 v56_l311
 (->
  (tc/dataset qr-history)
  (plotly/base {:=x :iteration, :=y :off-diagonal})
  (plotly/layer-line)
  plotly/plot))


(def
 v58_l318
 (let
  [final
   (last qr-history)
   computed
   (sort [(:eig-1 final) (:eig-2 final) (:eig-3 final)])]
  (every?
   identity
   (map
    (fn [a b] (< (Math/abs (- a b)) 1.0E-4))
    computed
    true-eigenvalues))))


(deftest t59_l324 (is (true? v58_l318)))


(def
 v61_l328
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


(deftest t62_l337 (is ((fn [_] true) v61_l328)))
