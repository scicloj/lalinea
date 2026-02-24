;; # Quickstart
;;
;; A minimal introduction to **basis** — linear algebra with tensor
;; abstractions. Matrices are dtype-next tensors, backed by
;; [EJML](https://ejml.org/) for computation and shared via zero-copy
;; interop.

(ns basis-book.quickstart
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.basis.impl.complex :as cx]
   ;; FFT bridge — Fastmath transforms ↔ ComplexTensor:
   [scicloj.basis.transform :as bfft]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Creating matrices
;;
;; Matrices are dtype-next tensors of shape $[r, c]$, backed by
;; a flat `double[]` in row-major order.

(la/matrix [[1 2 3]
            [4 5 6]])

(kind/test-last [(fn [m] (= [2 3] (vec (dtype/shape m))))])

;; Identity and zero matrices:

(la/eye 3)

(kind/test-last [(fn [m] (= 1.0 (tensor/mget m 1 1)))])

(la/zeros 2 3)

(kind/test-last [(fn [m] (= 0.0 (tensor/mget m 0 0)))])

;; Diagonal matrices:

(la/diag [1 2 3])

(kind/test-last [(fn [m] (and (= [3 3] (vec (dtype/shape m)))
                              (= 2.0 (tensor/mget m 1 1))))])

;; ## Matrix arithmetic
;;
;; All operations accept tensors and return tensors.

(let [A (la/matrix [[1 2] [3 4]])
      B (la/matrix [[5 6] [7 8]])]
  (la/mmul A B))

;; $AB = \begin{pmatrix} 19 & 22 \\ 43 & 50 \end{pmatrix}$

(kind/test-last [(fn [m] (= 19.0 (tensor/mget m 0 0)))])

(la/transpose (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (= 3.0 (tensor/mget m 0 1)))])

;; ## Scalar properties

(la/det (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (Math/abs (- v -2.0)) 1e-10))])

(la/trace (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= v 5.0))])

(la/norm (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (Math/abs (- v 5.477225575051661)) 1e-10))])

;; ## Solving linear systems
;;
;; Solve $Ax = b$ where
;; $A = \begin{pmatrix} 2 & 1 \\ 1 & 3 \end{pmatrix}$,
;; $b = \begin{pmatrix} 5 \\ 10 \end{pmatrix}$.

(la/solve (la/matrix [[2 1] [1 3]])
          (la/matrix [[5] [10]]))

;; $x = \begin{pmatrix} 1 \\ 3 \end{pmatrix}$

(kind/test-last [(fn [x] (and (< (Math/abs (- (tensor/mget x 0 0) 1.0)) 1e-10)
                              (< (Math/abs (- (tensor/mget x 1 0) 3.0)) 1e-10)))])

;; ## Decompositions
;;
;; Eigendecomposition of a symmetric matrix:

(let [{:keys [eigenvalues]} (la/eigen (la/matrix [[2 1] [1 2]]))]
  (sort (map first eigenvalues)))

;; Eigenvalues are $3$ and $1$.

(kind/test-last [(fn [evs] (and (< (Math/abs (- (first evs) 1.0)) 1e-10)
                                (< (Math/abs (- (second evs) 3.0)) 1e-10)))])

;; SVD:

(let [{:keys [S]} (la/svd (la/matrix [[1 2] [3 4]]))]
  S)

(kind/test-last [(fn [S] (< (Math/abs (- (first S) 5.4649857)) 1e-4))])

;; ## Complex tensors
;;
;; ComplexTensors wrap a dtype-next tensor whose last dimension is 2
;; (interleaved re/im pairs).

(cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [ct] (= [3] (cx/complex-shape ct)))])

;; Complex matrix multiply through the same `mmul`:

(let [A (cx/complex-tensor [[1.0 0.0] [0.0 1.0]]
                           [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A))

(kind/test-last [(fn [ct] (= [2 2] (cx/complex-shape ct)))])

;; ## FFT bridge
;;
;; The forward FFT takes a real signal and returns a ComplexTensor
;; spectrum — zero-copy from Fastmath's interleaved output.

(bfft/forward [1.0 0.0 1.0 0.0])

;; $\hat{f} = [2, 0, 2, 0]$ — a signal with energy at DC and Nyquist.

(kind/test-last [(fn [ct] (and (= [4] (cx/complex-shape ct))
                              (< (Math/abs (- (cx/re (ct 0)) 2.0)) 1e-10)))])

;; Round-trip:

(let [signal [1.0 2.0 3.0 4.0]
      recovered (vec (bfft/inverse-real (bfft/forward signal)))]
  recovered)

(kind/test-last [(fn [v] (every? #(< (Math/abs %) 1e-10)
                                 (map - v [1.0 2.0 3.0 4.0])))])

;; ## Composing with dtype-next
;;
;; Since matrices are tensors, all dtype-next operations work.

(let [A (la/matrix [[1 2] [3 4]])]
  (dfn/sum A))

(kind/test-last [(fn [v] (= v 10.0))])

;; Element-wise operations preserve tensor structure:

(let [A (la/matrix [[1 2] [3 4]])]
  (tensor/mget (la/scale 2.0 A) 1 1))

(kind/test-last [= 8.0])
