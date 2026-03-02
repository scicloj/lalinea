;; # Quickstart
;;
;; A minimal introduction to **La Linea** — linear algebra with tensor
;; abstractions. Matrices are dtype-next tensors, backed by
;; [EJML](https://ejml.org/) for computation and shared via zero-copy
;; interop.

(ns lalinea-book.quickstart
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.lalinea.complex :as cx]
   ;; FFT bridge — Fastmath transforms ↔ ComplexTensor:
   [scicloj.lalinea.transform :as ft]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Creating matrices
;;
;; Matrices are dtype-next tensors of shape $[r, c]$, backed by
;; a flat `double[]` in row-major order.

(la/matrix [[1 2 3]
            [4 5 6]])

(kind/test-last [(fn [m] (= [2 3] (dtype/shape m)))])

;; Identity and zero matrices:

(la/eye 3)

(kind/test-last [(fn [m] (= 1.0 (tensor/mget m 1 1)))])

(la/zeros 2 3)

(kind/test-last [(fn [m] (= 0.0 (tensor/mget m 0 0)))])

;; Diagonal matrices:

(la/diag [1 2 3])

(kind/test-last [(fn [m] (and (= [3 3] (dtype/shape m))
                              (= 2.0 (tensor/mget m 1 1))))])

;; ## Matrix arithmetic
;;
;; All operations accept tensors and return tensors.

(la/mmul (la/matrix [[1 2] [3 4]])
         (la/matrix [[5 6] [7 8]]))

;; $AB = \begin{pmatrix} 19 & 22 \\ 43 & 50 \end{pmatrix}$

(kind/test-last [(fn [m] (= 19.0 (tensor/mget m 0 0)))])

(la/transpose (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (= 3.0 (tensor/mget m 0 1)))])

;; ## Scalar properties

(la/det (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (abs (- v -2.0)) 1e-10))])

(la/trace (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= v 5.0))])

(la/norm (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (abs (- v 5.477225575051661)) 1e-10))])

;; ## Solving linear systems
;;
;; Solve $Ax = b$ where
;; $A = \begin{pmatrix} 2 & 1 \\ 1 & 3 \end{pmatrix}$,
;; $b = \begin{pmatrix} 5 \\ 10 \end{pmatrix}$.

(la/solve (la/matrix [[2 1] [1 3]])
          (la/matrix [[5] [10]]))

;; $x = \begin{pmatrix} 1 \\ 3 \end{pmatrix}$

(kind/test-last [(fn [x] (and (< (abs (- (tensor/mget x 0 0) 1.0)) 1e-10)
                              (< (abs (- (tensor/mget x 1 0) 3.0)) 1e-10)))])

;; ## Decompositions
;;
;; Eigendecomposition of a symmetric matrix:

(la/real-eigenvalues (la/matrix [[2 1] [1 2]]))

;; Eigenvalues are $3$ and $1$.

(kind/test-last [(fn [evs] (and (< (abs (- (first evs) 1.0)) 1e-10)
                                (< (abs (- (second evs) 3.0)) 1e-10)))])

;; SVD:

(:S (la/svd (la/matrix [[1 2] [3 4]])))

(kind/test-last [(fn [S] (< (abs (- (first S) 5.4649857)) 1e-4))])

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

(ft/forward [1.0 0.0 1.0 0.0])

;; $\hat{f} = [2, 0, 2, 0]$ — a signal with energy at DC and Nyquist.

(kind/test-last [(fn [ct] (and (= [4] (cx/complex-shape ct))
                               (< (abs (- (cx/re (ct 0)) 2.0)) 1e-10)))])

;; Round-trip:

(let [signal [1.0 2.0 3.0 4.0]
      recovered (vec (ft/inverse-real (ft/forward signal)))]
  recovered)

(kind/test-last [(fn [v] (every? #(< (abs %) 1e-10)
                                 (map - v [1.0 2.0 3.0 4.0])))])

;; ## Composing with dtype-next
;;
;; Since matrices are tensors, all dtype-next operations work.

(dfn/sum (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= v 10.0))])

;; Element-wise operations preserve tensor structure:

(tensor/mget (la/scale (la/matrix [[1 2] [3 4]]) 2.0) 1 1)

(kind/test-last [= 8.0])

;; ## Convenience wrappers

;; Common SVD-based analyses are available as one-liners:

(la/rank (la/matrix [[1 2] [2 4]]))

(kind/test-last [= 1])

(la/condition-number (la/matrix [[2 1] [1 3]]))

(kind/test-last [(fn [v] (> v 1.0))])

;; Pseudoinverse:

(la/close? (la/mmul (la/matrix [[2 1] [1 3]])
                    (la/pinv (la/matrix [[2 1] [1 3]])))
           (la/eye 2))

(kind/test-last [true?])

;; Matrix power:

(la/mpow (la/matrix [[1 1] [0 1]]) 5)

(kind/test-last [(fn [m] (la/close? m (la/matrix [[1 5] [0 1]])))])

;; ## Tagged literals
;;
;; Requiring `scicloj.lalinea.impl.print` installs `#la/R` and `#la/C`
;; tagged literals for round-trip printing:

(require '[scicloj.lalinea.impl.print])

(pr-str (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

(pr-str (la/column [5 6 7]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

;; ## Element-wise functions
;;
;; `scicloj.lalinea.elementwise` provides tape-aware wrappers
;; around `dfn/` with complex dispatch:

(require '[scicloj.lalinea.elementwise :as elem])

(elem/exp (la/column [0.0 1.0 2.0]))

(kind/test-last [(fn [v] (la/close? v (la/column [1.0 (math/exp 1.0) (math/exp 2.0)])))])

(elem/clip (la/column [-2 0.5 3]) -1 1)

(kind/test-last [(fn [v] (la/close? v (la/column [-1 0.5 1])))])

;; ## Computation tape
;;
;; Record operations as a DAG:

(require '[scicloj.lalinea.tape :as tape])

(let [{:keys [entries]} (tape/with-tape
                          (la/mmul (la/matrix [[1 2] [3 4]])
                                   (la/column [1 0])))]
  (mapv :op entries))

(kind/test-last [(fn [ops] (= [:la/matrix :la/column :la/mmul] ops))])

;; ## Automatic differentiation
;;
;; Reverse-mode autodiff computes gradients via VJP rules:

(require '[scicloj.lalinea.grad :as grad])

(let [A (la/matrix [[1 2] [3 4]])
      tape-result (tape/with-tape
                    (la/sum (la/sq (la/sub (la/mmul A A)
                                           (la/matrix [[1 0] [0 1]])))))
      grads (grad/grad tape-result (:result tape-result))]
  (tensor/mget (.get grads A) 0 0))

(kind/test-last [(fn [v] (number? v))])
