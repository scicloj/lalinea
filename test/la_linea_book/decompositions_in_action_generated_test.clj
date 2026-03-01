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


(def v28_l160 (def n-points 200))


(def
 v29_l162
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
 v31_l177
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
 v33_l189
 (def
  cov-matrix
  (la/scale (la/mmul (la/transpose X) X) (/ 1.0 (dec n-points)))))


(def v34_l192 cov-matrix)


(deftest t35_l194 (is ((fn [m] (= [2 2] (dtype/shape m))) v34_l192)))


(def v36_l197 (def pca-eigen (la/eigen cov-matrix)))


(def
 v38_l201
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
 t39_l207
 (is ((fn [evs] (> (first evs) (second evs))) v38_l201)))


(def
 v41_l216
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


(deftest t42_l239 (is ((fn [_] true) v41_l216)))


(def
 v44_l246
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
   (sort > reals)
   explained
   (/ (first variances) (dfn/sum variances))]
  explained))


(deftest t46_l259 (is ((fn [v] (> v 0.8)) v44_l246)))


(def v48_l272 (def test-matrix (la/matrix [[4 1 0] [1 3 1] [0 1 2]])))


(def v50_l279 (def true-eigenvalues (la/real-eigenvalues test-matrix)))


(def v51_l282 true-eigenvalues)


(deftest t52_l284 (is ((fn [evs] (= 3 (count evs))) v51_l282)))


(def
 v54_l289
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
 v56_l319
 (->
  (tc/dataset qr-history)
  (plotly/base {:=x :iteration, :=y :off-diagonal})
  (plotly/layer-line)
  plotly/plot))


(def v57_l324 (:off-diagonal (last qr-history)))


(deftest t58_l326 (is ((fn [v] (< v 1.0E-6)) v57_l324)))


(def
 v60_l331
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


(deftest t61_l337 (is (true? v60_l331)))


(def
 v63_l341
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


(deftest t64_l350 (is ((fn [_] true) v63_l341)))
