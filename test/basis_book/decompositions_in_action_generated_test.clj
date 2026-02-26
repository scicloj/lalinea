(ns
 basis-book.decompositions-in-action-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [fastmath.random :as frand]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
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


(def
 v11_l81
 (->
  (tc/dataset
   {:index (range (count (:S svd-result))),
    :singular-value (:S svd-result)})
  (plotly/base {:=x :index, :=y :singular-value})
  (plotly/layer-line)
  plotly/plot))


(def
 v13_l93
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
 v15_l105
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 1))))


(deftest
 t16_l107
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v15_l105)))


(def
 v18_l112
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 5))))


(deftest
 t19_l114
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v18_l112)))


(def
 v21_l119
 (bufimg/tensor->image
  (vis/matrix->gray-image (reconstruct-rank-k svd-result 20))))


(deftest
 t22_l121
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v21_l119)))


(def
 v24_l129
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


(def v26_l154 (def n-points 200))


(def
 v27_l156
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
    arr
    (double-array (* n-points 2))]
   (dotimes
    [i n-points]
    (let
     [p1
      (frand/grandom rng 0.0 3.0)
      p2
      (frand/grandom rng 0.0 0.8)
      x
      (+ (* cos-t p1) (* (- sin-t) p2))
      y
      (+ (* sin-t p1) (* cos-t p2))]
     (aset arr (* i 2) x)
     (aset arr (inc (* i 2)) y)))
   (tensor/reshape (tensor/ensure-tensor arr) [n-points 2]))))


(def
 v29_l172
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
 v31_l184
 (def
  cov-matrix
  (la/scale (la/mmul (la/transpose X) X) (/ 1.0 (dec n-points)))))


(def v32_l187 cov-matrix)


(deftest
 t33_l189
 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v32_l187)))


(def v34_l192 (def pca-eigen (la/eigen cov-matrix)))


(def
 v36_l196
 (let
  [eigenvalues
   (:eigenvalues pca-eigen)
   reals
   (cx/re eigenvalues)
   arr
   (dtype/->double-array reals)]
  (java.util.Arrays/sort arr)
  (vec (reverse (vec arr)))))


(deftest
 t37_l202
 (is ((fn [evs] (> (first evs) (second evs))) v36_l196)))


(def
 v39_l211
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   reals
   (cx/re eigenvalues)
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
   (* (Math/sqrt lam1) (tensor/mget ev1 0 0))
   pc1-y
   (* (Math/sqrt lam1) (tensor/mget ev1 1 0))
   pc2-x
   (* (Math/sqrt lam2) (tensor/mget ev2 0 0))
   pc2-y
   (* (Math/sqrt lam2) (tensor/mget ev2 1 0))
   pts
   (mapv
    (fn
     [i]
     {:x (tensor/mget X i 0), :y (tensor/mget X i 1), :type "data"})
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


(deftest t40_l234 (is ((fn [_] true) v39_l211)))


(def
 v42_l241
 (let
  [{:keys [eigenvalues eigenvectors]}
   pca-eigen
   reals
   (cx/re eigenvalues)
   sorted-idx
   (sort-by
    (fn [i] (- (double (reals i))))
    (range (count eigenvectors)))
   ev1
   (nth eigenvectors (first sorted-idx))
   projected
   (la/mmul (la/mmul X ev1) (la/transpose ev1))
   variances
   (sort > (seq reals))
   explained
   (/ (first variances) (reduce + variances))]
  explained))


(deftest t44_l254 (is ((fn [v] (> v 0.8)) v42_l241)))


(def v46_l267 (def test-matrix (la/matrix [[4 1 0] [1 3 1] [0 1 2]])))


(def v48_l274 (def true-eigenvalues (la/real-eigenvalues test-matrix)))


(def v49_l277 true-eigenvalues)


(deftest t50_l279 (is ((fn [evs] (= 3 (count evs))) v49_l277)))


(def
 v52_l284
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
 v54_l314
 (->
  (tc/dataset qr-history)
  (plotly/base {:=x :iteration, :=y :off-diagonal})
  (plotly/layer-line)
  plotly/plot))


(def v55_l319 (:off-diagonal (last qr-history)))


(deftest t56_l321 (is ((fn [v] (< v 1.0E-6)) v55_l319)))


(def
 v58_l326
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


(deftest t59_l332 (is (true? v58_l326)))


(def
 v61_l336
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


(deftest t62_l345 (is ((fn [_] true) v61_l336)))
