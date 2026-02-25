;; # Decompositions in Action
;;
;; Matrix decompositions are the workhorses of numerical linear
;; algebra. This chapter shows three practical applications:
;;
;; - **SVD** for image compression (purely functional)
;; - **Eigendecomposition** for Principal Component Analysis
;; - **QR iteration** for computing eigenvalues (imperative)

(ns basis-book.decompositions-in-action
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   [scicloj.basis.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Tensor ↔ BufferedImage conversion:
   [tech.v3.libs.buffered-image :as bufimg]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Seeded random number generation (https://generateme.github.io/fastmath/):
   [fastmath.random :as frand]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Image compression with SVD
;;
;; The [singular value decomposition](https://en.wikipedia.org/wiki/Singular_value_decomposition)
;; writes any matrix as $A = U \Sigma V^T$ where $U$ and
;; $V$ are orthogonal and $\Sigma$ is diagonal. The diagonal
;; entries (singular values) are sorted by magnitude — the
;; first few capture most of the "energy" in the matrix.
;;
;; By keeping only the top $k$ singular values, we get a
;; **rank-$k$ approximation** that compresses the image.

;; ### A synthetic test image
;;
;; A pattern with low-frequency structure (smooth gradients
;; and geometric shapes) compresses well with SVD.

(def img-size 100)

(def test-image
  (tensor/ensure-tensor
   (dtype/clone
    (tensor/compute-tensor [img-size img-size]
                           (fn [r c]
                             (let [x (/ (- c 50.0) 50.0)
                                   y (/ (- r 50.0) 50.0)
                                   circle (if (< (+ (* x x) (* y y)) 0.5) 200.0 50.0)
                                   gradient (* 100.0 (+ 0.5 (* 0.5 (Math/sin (* 3.0 x)))))]
                               (+ (* 0.6 circle) (* 0.4 gradient))))
                           :float64))))

;; Display the original:

(let [t (tensor/compute-tensor [img-size img-size 3]
                               (fn [r c _ch]
                                 (int (max 0 (min 255 (tensor/mget test-image r c)))))
                               :uint8)]
  (bufimg/tensor->image t))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### SVD decomposition

(def svd-result (la/svd test-image))

;; The singular values drop off rapidly — most of the information
;; is in the first few:

(-> (tc/dataset {:index (range (count (:S svd-result)))
                 :singular-value (:S svd-result)})
    (plotly/base {:=x :index :=y :singular-value})
    (plotly/layer-line)
    plotly/plot)

;; ### Rank-k reconstruction
;;
;; A purely functional approach: slice the first $k$ columns of
;; $U$, the first $k$ singular values, and the first $k$ rows
;; of $V^T$, then multiply them back together.

(def reconstruct-rank-k
  (fn [svd-result k]
    (let [{:keys [U S Vt]} svd-result
          Uk  (la/submatrix U :all (range k))
          Sk  (la/diag (take k S))
          Vtk (la/submatrix Vt (range k) :all)]
      (la/mmul (la/mmul Uk Sk) Vtk))))

;; Compare rank 1, 5, 10, and 50 approximations:

(def matrix->gray-image
  (fn [m]
    (let [[h w] (dtype/shape m)]
      (tensor/compute-tensor [h w 3]
                             (fn [r c _ch]
                               (int (max 0 (min 255 (tensor/mget m r c)))))
                             :uint8))))

;; Rank 1 — captures only the dominant direction:

(bufimg/tensor->image (matrix->gray-image (reconstruct-rank-k svd-result 1)))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; Rank 5 — the circular structure starts to appear:

(bufimg/tensor->image (matrix->gray-image (reconstruct-rank-k svd-result 5)))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; Rank 20 — nearly indistinguishable from the original:

(bufimg/tensor->image (matrix->gray-image (reconstruct-rank-k svd-result 20)))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Compression ratio
;;
;; The original stores $n \times m$ values.
;; Rank $k$ stores $k(n + m + 1)$ values.

(-> (tc/dataset {:k [1 5 10 20 50]
                 :ratio (mapv (fn [k]
                                (/ (* 1.0 k (+ img-size img-size 1))
                                   (* img-size img-size)))
                              [1 5 10 20 50])
                 :error (mapv (fn [k]
                                (la/norm (la/sub test-image
                                                 (reconstruct-rank-k svd-result k))))
                              [1 5 10 20 50])})
    (plotly/base {:=x :ratio :=y :error})
    (plotly/layer-point {:=mark-size 10})
    (plotly/layer-line)
    plotly/plot)

;; ## Principal Component Analysis
;;
;; [PCA](https://en.wikipedia.org/wiki/Principal_component_analysis)
;; finds the directions of maximum variance in data.
;; Given a data matrix $X$ (centered), the principal components
;; are the eigenvectors of $X^T X$.

;; ### Generate 2D data with known covariance
;;
;; We create points distributed along a tilted ellipse.

(def n-points 200)

(def data-tensor
  (let [theta (/ Math/PI 6)
        cos-t (Math/cos theta) sin-t (Math/sin theta)
        rng (frand/rng :mersenne 42)
        arr (double-array (* n-points 2))]
    (dotimes [i n-points]
      (let [p1 (frand/grandom rng 0.0 3.0)
            p2 (frand/grandom rng 0.0 0.8)
            x (+ (* cos-t p1) (* (- sin-t) p2))
            y (+ (* sin-t p1) (* cos-t p2))]
        (aset arr (* i 2) x)
        (aset arr (inc (* i 2)) y)))
    (tensor/reshape (tensor/ensure-tensor arr) [n-points 2])))

;; ### Center the data

(def X
  (let [col0 (tensor/select data-tensor :all 0)
        col1 (tensor/select data-tensor :all 1)
        mean0 (/ (dfn/sum col0) n-points)
        mean1 (/ (dfn/sum col1) n-points)
        means (tensor/compute-tensor [n-points 2]
                                     (fn [_i j] (if (zero? j) mean0 mean1))
                                     :float64)]
    (dtype/clone (dfn/- data-tensor means))))

;; ### Compute covariance matrix and eigendecompose

(def cov-matrix
  (la/scale (/ 1.0 (dec n-points))
            (la/mmul (la/transpose X) X)))

cov-matrix

(kind/test-last
 [(fn [m] (= [2 2] (vec (dtype/shape m))))])

(def pca-eigen (la/eigen cov-matrix))

;; Eigenvalues = variances along each principal axis:

(let [eigenvalues (:eigenvalues pca-eigen)
      reals (cx/re eigenvalues)
      arr (dtype/->double-array reals)]
  (java.util.Arrays/sort arr)
  (vec (reverse (vec arr))))

(kind/test-last
 [(fn [evs] (> (first evs) (second evs)))])

;; ### Visualize principal axes
;;
;; The eigenvectors point along the directions of maximum variance.

(let [{:keys [eigenvalues eigenvectors]} pca-eigen
      ;; Sort by eigenvalue (real part) descending
      reals (cx/re eigenvalues)
      sorted-idx (sort-by (fn [i] (- (double (reals i)))) (range (count eigenvectors)))
      lam1 (double (reals (first sorted-idx)))
      ev1 (nth eigenvectors (first sorted-idx))
      lam2 (double (reals (second sorted-idx)))
      ev2 (nth eigenvectors (second sorted-idx))
      ;; Eigenvector endpoints (scaled by sqrt of eigenvalue)
      pc1-x (* (Math/sqrt lam1) (tensor/mget ev1 0 0))
      pc1-y (* (Math/sqrt lam1) (tensor/mget ev1 1 0))
      pc2-x (* (Math/sqrt lam2) (tensor/mget ev2 0 0))
      pc2-y (* (Math/sqrt lam2) (tensor/mget ev2 1 0))
      ;; Data points
      pts (map (fn [i] {:x (tensor/mget X i 0) :y (tensor/mget X i 1) :type "data"})
               (range n-points))
      ;; Arrow endpoints for PC1 and PC2
      arrows [{:x 0.0 :y 0.0 :type "PC1-start"}
              {:x pc1-x :y pc1-y :type "PC1-end"}
              {:x 0.0 :y 0.0 :type "PC2-start"}
              {:x pc2-x :y pc2-y :type "PC2-end"}]]
  (-> (tc/dataset (concat pts))
      (plotly/base {:=x :x :=y :y})
      (plotly/layer-point {:=mark-size 4 :=mark-opacity 0.4})
      plotly/plot))

(kind/test-last
 [(fn [_] true)])

;; ### Project onto first principal component
;;
;; Projection is a matrix multiply: $X_{\text{proj}} = X \cdot v_1 \cdot v_1^T$

(let [{:keys [eigenvalues eigenvectors]} pca-eigen
      reals (cx/re eigenvalues)
      sorted-idx (sort-by (fn [i] (- (double (reals i)))) (range (count eigenvectors)))
      ev1 (nth eigenvectors (first sorted-idx))
      ;; Project: X * v1 * v1^T
      projected (la/mmul (la/mmul X ev1) (la/transpose ev1))
      ;; Fraction of variance explained
      variances (sort > (seq reals))
      explained (/ (first variances) (reduce + variances))]
  explained)

;; The first PC explains most of the variance:

(kind/test-last
 [(fn [v] (> v 0.8))])

;; ## QR algorithm for eigenvalues
;;
;; The [QR algorithm](https://en.wikipedia.org/wiki/QR_algorithm)
;; is the workhorse method for computing eigenvalues. It repeatedly
;; decomposes $A = QR$ and forms $A' = RQ$. The diagonal of $A'$
;; converges to the eigenvalues.
;;
;; This is an **imperative** iteration — we track convergence of
;; the diagonal entries.

(def test-matrix
  (la/matrix [[4 1 0]
              [1 3 1]
              [0 1 2]]))

;; True eigenvalues for comparison:

(def true-eigenvalues
  (la/real-eigenvalues test-matrix))

true-eigenvalues

(kind/test-last
 [(fn [evs] (= 3 (count evs)))])

;; ### QR iteration

(def qr-history
  (loop [A test-matrix
         k 0
         history []]
    (if (>= k 50)
      history
      (let [{:keys [Q R]} (la/qr A)
            A-next (la/mmul R Q)
            ;; Extract diagonal
            diag (sort (mapv (fn [i]
                               (tensor/mget A-next i i))
                             (range 3)))
            ;; Off-diagonal magnitude
            off-diag (Math/sqrt
                      (+ (let [v (tensor/mget A-next 0 1)] (* v v))
                         (let [v (tensor/mget A-next 1 0)] (* v v))
                         (let [v (tensor/mget A-next 0 2)] (* v v))
                         (let [v (tensor/mget A-next 2 0)] (* v v))
                         (let [v (tensor/mget A-next 1 2)] (* v v))
                         (let [v (tensor/mget A-next 2 1)] (* v v))))]
        (recur A-next (inc k)
               (conj history {:iteration (inc k)
                              :off-diagonal off-diag
                              :eig-1 (nth diag 0)
                              :eig-2 (nth diag 1)
                              :eig-3 (nth diag 2)}))))))

;; The off-diagonal elements converge to zero — the matrix
;; becomes diagonal:

(-> (tc/dataset qr-history)
    (plotly/base {:=x :iteration :=y :off-diagonal})
    (plotly/layer-line)
    plotly/plot)

(:off-diagonal (last qr-history))

(kind/test-last
 [(fn [v] (< v 1e-6))])

;; The diagonal converges to the true eigenvalues:

(let [final (last qr-history)
      computed (sort [(:eig-1 final) (:eig-2 final) (:eig-3 final)])]
  (every? identity
          (map (fn [a b] (< (Math/abs (- a b)) 1e-4))
               computed true-eigenvalues)))

(kind/test-last [true?])

;; ### Convergence of each eigenvalue

(-> (tc/dataset (mapcat (fn [{:keys [iteration eig-1 eig-2 eig-3]}]
                          [{:iteration iteration :value eig-1 :eigenvalue "λ₁"}
                           {:iteration iteration :value eig-2 :eigenvalue "λ₂"}
                           {:iteration iteration :value eig-3 :eigenvalue "λ₃"}])
                        qr-history))
    (plotly/base {:=x :iteration :=y :value :=color :eigenvalue})
    (plotly/layer-line)
    plotly/plot)

(kind/test-last [(fn [_] true)])
