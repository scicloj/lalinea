;; # Quickstart
;;
;; A minimal introduction to **La Linea** — [linear algebra](https://en.wikipedia.org/wiki/Linear_algebra) with tensor
;; abstractions. Matrices are dtype-next tensors, backed by
;; [EJML](https://ejml.org/) for computation and shared via zero-copy
;; interop.

(ns lalinea-book.quickstart
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   ;; Complex tensors — interleaved [re im] layout:
   ;; FFT bridge — Fastmath transforms ↔ ComplexTensor:
   [scicloj.lalinea.transform :as ft]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Creating matrices
;;
;; Matrices are dtype-next tensors of shape $[r, c]$, backed by
;; a flat `double[]` in [row-major order](https://en.wikipedia.org/wiki/Row-_and_column-major_order).

(t/matrix [[1 2 3]
           [4 5 6]])

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

;; Identity and zero matrices:

(t/eye 3)

(kind/test-last [(fn [m] (= 1.0 (m 1 1)))])

(t/zeros 2 3)

(kind/test-last [(fn [m] (= 0.0 (m 0 0)))])

;; Diagonal matrices:

(t/diag [1 2 3])

(kind/test-last [(fn [m] (and (= [3 3] (t/shape m))
                              (= 2.0 (m 1 1))))])

;; ## Matrix arithmetic
;;
;; All operations accept tensors and return tensors.

(la/mmul (t/matrix [[1 2] [3 4]])
         (t/matrix [[5 6] [7 8]]))

;; $AB = \begin{pmatrix} 19 & 22 \\ 43 & 50 \end{pmatrix}$

(kind/test-last [(fn [m] (= 19.0 (m 0 0)))])

(la/transpose (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (= 3.0 (m 0 1)))])

;; ## Scalar properties

(la/det (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (abs (- v -2.0)) 1e-10))])

(la/trace (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= v 5.0))])

(la/norm (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (< (abs (- v 5.477225575051661)) 1e-10))])

;; ## Solving linear systems
;;
;; Solve $Ax = b$ where
;; $A = \begin{pmatrix} 2 & 1 \\ 1 & 3 \end{pmatrix}$,
;; $b = \begin{pmatrix} 5 \\ 10 \end{pmatrix}$.

(la/solve (t/matrix [[2 1] [1 3]])
          (t/matrix [[5] [10]]))

;; $x = \begin{pmatrix} 1 \\ 3 \end{pmatrix}$

(kind/test-last [(fn [x] (and (< (abs (- (x 0 0) 1.0)) 1e-10)
                              (< (abs (- (x 1 0) 3.0)) 1e-10)))])

;; ## Decompositions
;;
;; [Eigendecomposition](https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix) of a symmetric matrix:

(la/real-eigenvalues (t/matrix [[2 1] [1 2]]))

;; Eigenvalues are $3$ and $1$.

(kind/test-last [(fn [evs] (and (< (abs (- (first evs) 1.0)) 1e-10)
                                (< (abs (- (second evs) 3.0)) 1e-10)))])

;; [SVD](https://en.wikipedia.org/wiki/Singular_value_decomposition):

(:S (la/svd (t/matrix [[1 2] [3 4]])))

(kind/test-last [(fn [S] (< (abs (- (first S) 5.4649857)) 1e-4))])

;; ## Complex tensors
;;
;; ComplexTensors wrap a dtype-next tensor whose last dimension is 2
;; (interleaved re/im pairs).
;;
;; The same `la/` functions work for both real and complex inputs — `la/add`,
;; `la/mmul`, `la/transpose` are polymorphic over the number field.

(t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [ct] (= [3] (t/complex-shape ct)))])

;; Complex matrix multiply through the same `mmul`:

(let [A (t/complex-tensor [[1.0 0.0] [0.0 1.0]]
                           [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A A))

(kind/test-last [(fn [ct] (= [2 2] (t/complex-shape ct)))])

;; ## FFT bridge
;;
;; The forward FFT takes a real signal and returns a ComplexTensor
;; spectrum — zero-copy from Fastmath's interleaved output.

(ft/forward [1.0 0.0 1.0 0.0])

;; $\hat{f} = [2, 0, 2, 0]$ — a signal with energy at DC and Nyquist.

(kind/test-last [(fn [ct] (and (= [4] (t/complex-shape ct))
                               (< (abs (- (la/re (ct 0)) 2.0)) 1e-10)))])

;; Round-trip:

(let [signal [1.0 2.0 3.0 4.0]
      recovered (ft/inverse-real (ft/forward signal))]
  recovered)

(kind/test-last [(fn [v] (la/close? v (t/matrix [1.0 2.0 3.0 4.0])))])

;; ## Composing with dtype-next
;;
;; Since matrices are tensors, all dtype-next operations work.

(la/sum (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= v 10.0))])

;; Element-wise operations preserve tensor structure:

((la/scale (t/matrix [[1 2] [3 4]]) 2.0) 1 1)

(kind/test-last [= 8.0])

;; ## Convenience wrappers

;; Common SVD-based analyses are available as one-liners:

(la/rank (t/matrix [[1 2] [2 4]]))

(kind/test-last [= 1])

(la/condition-number (t/matrix [[2 1] [1 3]]))

(kind/test-last [(fn [v] (> v 1.0))])

;; Pseudoinverse:

(la/close? (la/mmul (t/matrix [[2 1] [1 3]])
                    (la/pinv (t/matrix [[2 1] [1 3]])))
           (t/eye 2))

(kind/test-last [true?])

;; Matrix power:

(la/mpow (t/matrix [[1 1] [0 1]]) 5)

(kind/test-last [(fn [m] (la/close? m (t/matrix [[1 5] [0 1]])))])

;; ## Tagged literals
;;
;; La Linea tensors print as `#la/R` and `#la/C` tagged literals,
;; enabling round-trip through `pr-str` / `read-string`:

(pr-str (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

(pr-str (t/column [5 6 7]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

;; ## Element-wise functions
;;
;; `la/` covers linear algebra; `elem/` covers element-wise math — both
;; are tape-aware and work with both real and complex inputs.
;;
;; `scicloj.lalinea.elementwise` provides wrappers
;; around `dfn/` with complex dispatch:

(require '[scicloj.lalinea.elementwise :as elem])

(elem/exp (t/column [0.0 1.0 2.0]))

(kind/test-last [(fn [v] (la/close? v (t/column [1.0 (math/exp 1.0) (math/exp 2.0)])))])

(elem/clip (t/column [-2 0.5 3]) -1 1)

(kind/test-last [(fn [v] (la/close? v (t/column [-1 0.5 1])))])

;; ## Computation tape
;;
;; Record operations as a DAG:

(require '[scicloj.lalinea.tape :as tape])

(let [{:keys [entries]} (tape/with-tape
                          (la/mmul (t/matrix [[1 2] [3 4]])
                                   (t/column [1 0])))]
  (mapv :op entries))

(kind/test-last [(fn [ops] (= [:t/matrix :t/column :la/mmul] ops))])

;; ## Automatic differentiation
;;
;; Reverse-mode autodiff computes gradients via VJP rules:

(require '[scicloj.lalinea.grad :as grad])

(let [A (t/matrix [[1 2] [3 4]])
      tape-result (tape/with-tape
                    (la/sum (la/sq (la/sub (la/mmul A A)
                                           (t/matrix [[1 0] [0 1]])))))]
  ((grad/grad tape-result (:result tape-result) A) 0 0))

(kind/test-last [(fn [v] (number? v))])
