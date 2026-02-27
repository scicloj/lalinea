;; # Least Squares and Curve Fitting
;;
;; Given noisy data, how do we find the "best" curve that fits it?
;; **Least squares** minimises the sum of squared residuals.
;; The solution is a linear algebra problem.
;;
;; This notebook derives the **normal equations**, solves them with
;; `la/solve`, connects the solution to QR and SVD decompositions,
;; and applies the technique to polynomial and trigonometric fitting.

(ns la-linea-book.least-squares
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## The problem
;;
;; We observe $m$ data points $(x_i, y_i)$ and want to fit a model
;; $y = c_0 \phi_0(x) + c_1 \phi_1(x) + \cdots + c_{n-1} \phi_{n-1}(x)$
;; where $\phi_j$ are known basis functions and $c_j$ are unknown coefficients.
;;
;; Stacking all observations into a matrix equation:
;;
;; $$A \mathbf{c} = \mathbf{y}$$
;;
;; where $A_{ij} = \phi_j(x_i)$. When $m > n$ (more data than unknowns),
;; the system is overdetermined. The **least squares** solution minimises
;; $\|A\mathbf{c} - \mathbf{y}\|^2$.

;; ## Linear fit: the simplest case
;;
;; For a line $y = c_0 + c_1 x$, the basis functions are
;; $\phi_0(x) = 1$ and $\phi_1(x) = x$.

;; Some noisy data along the line $y = 2 + 3x$:

(def x-data
  (dtype/make-reader :float64 20
                     (* 0.1 idx)))

(def noise-linear
  (tensor/->tensor [0.343 0.276 -0.285 -0.332 0.084 0.205 -0.245 -0.419
                    -0.057 0.446 0.241 -0.036 0.423 -0.192 -0.363 0.106
                    -0.147 0.165 -0.361 0.096]
                   :datatype :float64))

(def y-linear
  (dfn/+ (dfn/+ 2.0 (dfn/* 3.0 x-data)) noise-linear))

;; Build the design matrix $A$:

(def A-linear
  (let [m (count x-data)]
    (la/matrix (mapv (fn [i]
                       [1.0 (x-data i)])
                     (range m)))))

A-linear

;; The observation vector $\mathbf{y}$ as a column:

(def y-col (la/column y-linear))

;; ## The normal equations
;;
;; The least-squares solution satisfies:
;;
;; $$A^T A \mathbf{c} = A^T \mathbf{y}$$
;;
;; This is a square system that we solve with `la/solve`:

(def c-linear
  (la/solve (la/mmul (la/transpose A-linear) A-linear)
            (la/mmul (la/transpose A-linear) y-col)))

c-linear

(kind/test-last
 [(fn [c]
    (and (< (Math/abs (- (tensor/mget c 0 0) 2.0)) 0.5)
         (< (Math/abs (- (tensor/mget c 1 0) 3.0)) 0.5)))])

;; The fitted coefficients are close to the true values
;; $c_0 \approx 2$, $c_1 \approx 3$.

;; The residual — how well does the fit match the data?

(def residual-linear
  (la/sub (la/mmul A-linear c-linear) y-col))

(def rms-linear
  (Math/sqrt (/ (dfn/sum (dfn/* residual-linear residual-linear))
                (count x-data))))

rms-linear

(kind/test-last
 [(fn [v] (< v 0.5))])

;; Visualise the fit:

(let [c0 (tensor/mget c-linear 0 0)
      c1 (tensor/mget c-linear 1 0)
      x-fit (dtype/make-reader :float64 100 (* 0.019 idx))
      y-fit (dfn/+ c0 (dfn/* c1 x-fit))]
  (-> (tc/dataset {:x x-data
                   :y y-linear
                   :type (repeat (count x-data) "data")})
      (tc/concat (tc/dataset {:x x-fit
                              :y y-fit
                              :type (repeat 100 "fit")}))
      (plotly/base {:=x :x :=y :y :=color :type})
      (plotly/layer-point {:=mark-size 8
                           :=mark-opacity 0.6})
      (plotly/layer-line)
      plotly/plot))

;; ## Polynomial fit
;;
;; For a polynomial of degree $d$, the basis functions are
;; $\phi_j(x) = x^j$, $j = 0, \ldots, d$.
;;
;; The **Vandermonde matrix** has $A_{ij} = x_i^j$.

;; Some data along $y = 1 - 2x + x^2$:

(def x-poly
  (dtype/make-reader :float64 30
                     (- (* 0.2 idx) 3.0)))

(def noise-poly
  (tensor/->tensor [0.132 -0.541  0.269  0.370  0.067  0.284 -0.096 -0.651
                    -0.228  0.055  0.483 -0.301  0.088 -0.104  0.612  0.073
                    -0.318  0.247 -0.428  0.518 -0.092  0.146 -0.190  0.398
                    0.031 -0.263  0.195 -0.416  0.347  0.010]
                   :datatype :float64))

(def y-poly
  (dfn/+ (dfn/+ 1.0 (dfn/* -2.0 x-poly))
         (dfn/+ (dfn/* x-poly x-poly) noise-poly)))

(def vandermonde
  (fn [xs degree]
    (let [m (count xs)]
      (la/matrix
       (mapv (fn [i]
               (mapv (fn [j] (Math/pow (xs i) j))
                     (range (inc degree))))
             (range m))))))

(def A-poly (vandermonde x-poly 2))

(def c-poly
  (la/solve (la/mmul (la/transpose A-poly) A-poly)
            (la/mmul (la/transpose A-poly) (la/column y-poly))))

c-poly

(kind/test-last
 [(fn [c]
    (and (< (Math/abs (- (tensor/mget c 0 0) 1.0)) 1.0)
         (< (Math/abs (- (tensor/mget c 1 0) -2.0)) 1.0)
         (< (Math/abs (- (tensor/mget c 2 0) 1.0)) 1.0)))])

;; The fitted coefficients recover the true polynomial $1 - 2x + x^2$.

;; Visualise:

(let [c0 (tensor/mget c-poly 0 0)
      c1 (tensor/mget c-poly 1 0)
      c2 (tensor/mget c-poly 2 0)
      x-fit (dtype/make-reader :float64 100 (- (* 0.06 idx) 3.0))
      y-fit (dfn/+ c0 (dfn/+ (dfn/* c1 x-fit) (dfn/* c2 (dfn/* x-fit x-fit))))]
  (-> (tc/dataset {:x x-poly
                   :y y-poly
                   :type (repeat 30 "data")})
      (tc/concat (tc/dataset {:x x-fit
                              :y y-fit
                              :type (repeat 100 "fit")}))
      (plotly/base {:=x :x :=y :y :=color :type})
      (plotly/layer-point {:=mark-size 8
                           :=mark-opacity 0.6})
      (plotly/layer-line)
      plotly/plot))

;; ## QR-based least squares
;;
;; The normal equations $A^T A \mathbf{c} = A^T \mathbf{y}$ can be
;; numerically unstable when $A$ is ill-conditioned. A better approach
;; uses the **QR decomposition**: $A = QR$ where $Q$ is orthogonal
;; and $R$ is upper triangular.
;;
;; Substituting gives $R \mathbf{c} = Q^T \mathbf{y}$, a triangular
;; system that is easy to solve.

(def qr-result (la/qr A-poly))

;; EJML returns the full QR. For least squares we extract the
;; "thin" (economy) form: $Q_1$ is $m \times n$, $R_1$ is $n \times n$.

(def ncols (second (dtype/shape A-poly)))

(def Q1 (la/submatrix (:Q qr-result) :all (range ncols)))
(def R1 (la/submatrix (:R qr-result) (range ncols) :all))

;; Verify $A = Q_1 R_1$:

(la/norm (la/sub (la/mmul Q1 R1) A-poly))

(kind/test-last
 [(fn [v] (< v 1e-10))])

;; Solve $R_1 \mathbf{c} = Q_1^T \mathbf{y}$:

(def c-qr
  (la/solve R1 (la/mmul (la/transpose Q1) (la/column y-poly))))

;; Compare with the normal-equation solution:

(la/norm (la/sub c-qr c-poly))

(kind/test-last
 [(fn [v] (< v 1e-10))])

;; Both methods give the same answer (to machine precision),
;; but QR is numerically more stable.

;; ## SVD-based least squares
;;
;; The **pseudoinverse** via SVD provides the most robust solution,
;; especially when columns of $A$ are nearly linearly dependent.
;;
;; If $A = U \Sigma V^T$, the least-squares solution is
;; $\mathbf{c} = V \Sigma^{-1} U^T \mathbf{y}$.

(def svd-result (la/svd A-poly))

;; EJML returns the full SVD. We extract the thin form
;; (first $n$ columns of $U$) for the pseudoinverse.

(def S-svd (:S svd-result))
(def U-thin (la/submatrix (:U svd-result) :all (range (count S-svd))))
(def Vt-svd (:Vt svd-result))

;; The singular values:

S-svd

(kind/test-last
 [(fn [v] (every? pos? v))])

;; Compute the pseudoinverse solution:
;; $\mathbf{c} = V \Sigma^{-1} U_1^T \mathbf{y}$

(def c-svd
  (let [k (count S-svd)
        S-inv (la/diag (dtype/make-reader :float64 k
                                          (/ 1.0 (S-svd idx))))
        Ut-y (la/mmul (la/transpose U-thin) (la/column y-poly))]
    (la/mmul (la/transpose Vt-svd) (la/mmul S-inv Ut-y))))

;; Compare:

(la/norm (la/sub c-svd c-poly))

(kind/test-last
 [(fn [v] (< v 1e-8))])

;; All three methods — normal equations, QR, and SVD — agree.

;; ## Trigonometric fitting
;;
;; Least squares is not limited to polynomials. We can fit any
;; linear combination of basis functions.
;;
;; Let us fit a sum of sines and cosines to a periodic signal:
;; $y = a_0 + a_1 \cos(x) + b_1 \sin(x) + a_2 \cos(2x) + b_2 \sin(2x)$

(def x-trig
  (dtype/make-reader :float64 40
                     (* (/ (* 2.0 Math/PI) 40.0) idx)))

(def noise-trig
  (tensor/->tensor [-0.058  0.218 -0.109  0.034  0.192 -0.277  0.138  0.065
                    -0.193  0.145 -0.044  0.291 -0.180  0.077  0.253 -0.116
                    0.162 -0.207  0.098 -0.151  0.184 -0.023  0.211 -0.138
                    0.073 -0.246  0.119  0.047 -0.183  0.264 -0.091  0.156
                    -0.228  0.103  0.189 -0.144  0.268 -0.076  0.131 -0.201]
                   :datatype :float64))

(def y-trig
  (dfn/+ (dfn/+ 3.0 (dfn/* 2.0 (dfn/cos x-trig)))
         (dfn/+ (dfn/* -1.5 (dfn/sin x-trig))
                (dfn/+ (dfn/* 0.5 (dfn/cos (dfn/* 2.0 x-trig)))
                       noise-trig))))

(def A-trig
  (let [m (count x-trig)]
    (la/matrix
     (mapv (fn [i]
             (let [xi (x-trig i)]
               [1.0
                (Math/cos xi)
                (Math/sin xi)
                (Math/cos (* 2.0 xi))
                (Math/sin (* 2.0 xi))]))
           (range m)))))

(def c-trig
  (la/solve (la/mmul (la/transpose A-trig) A-trig)
            (la/mmul (la/transpose A-trig) (la/column y-trig))))

c-trig

(kind/test-last
 [(fn [c]
    (and (< (Math/abs (- (tensor/mget c 0 0) 3.0)) 0.3)
         (< (Math/abs (- (tensor/mget c 1 0) 2.0)) 0.3)
         (< (Math/abs (- (tensor/mget c 2 0) -1.5)) 0.3)
         (< (Math/abs (- (tensor/mget c 3 0) 0.5)) 0.3)))])

;; The recovered coefficients are close to the true values:
;; $a_0 \approx 3$, $a_1 \approx 2$, $b_1 \approx -1.5$, $a_2 \approx 0.5$.

;; Visualise:

(let [x-fit (dtype/make-reader :float64 200
                               (* (/ (* 2.0 Math/PI) 200.0) idx))
      y-fit (dfn/+ (dfn/* (tensor/mget c-trig 0 0) 1.0)
                   (dfn/+ (dfn/* (tensor/mget c-trig 1 0) (dfn/cos x-fit))
                          (dfn/+ (dfn/* (tensor/mget c-trig 2 0) (dfn/sin x-fit))
                                 (dfn/+ (dfn/* (tensor/mget c-trig 3 0) (dfn/cos (dfn/* 2.0 x-fit)))
                                        (dfn/* (tensor/mget c-trig 4 0) (dfn/sin (dfn/* 2.0 x-fit)))))))]
  (-> (tc/dataset {:x x-trig
                   :y y-trig
                   :type (repeat 40 "data")})
      (tc/concat (tc/dataset {:x x-fit
                              :y y-fit
                              :type (repeat 200 "fit")}))
      (plotly/base {:=x :x :=y :y :=color :type})
      (plotly/layer-point {:=mark-size 6
                           :=mark-opacity 0.6})
      (plotly/layer-line)
      plotly/plot))

;; ## Condition number and numerical stability
;;
;; The **condition number** $\kappa(A) = \sigma_{\max} / \sigma_{\min}$
;; measures how sensitive the solution is to perturbations in the data.
;; A large condition number means the problem is ill-conditioned.

;; The Vandermonde matrix for our polynomial fit:

(def poly-svd (la/svd A-poly))

(def condition-number
  (let [sv (:S poly-svd)]
    (/  (dfn/reduce-max sv) (dfn/reduce-min sv))))

condition-number

(kind/test-last
 [(fn [v] (> v 1.0))])

;; For comparison, a higher-degree Vandermonde matrix on a wider
;; interval has a much larger condition number:

(def A-high
  (vandermonde (dtype/make-reader :float64 30 (* 1.0 idx)) 8))

(def high-sv (:S (la/svd A-high)))

(/  (dfn/reduce-max high-sv) (dfn/reduce-min high-sv))

(kind/test-last
 [(fn [v] (> v 1e6))])

;; Condition numbers above $10^6$ suggest the normal equations
;; will lose accuracy — QR or SVD should be preferred.

;; ---
;;
;; ## Summary
;;
;; This notebook covered:
;;
;; - The **normal equations** $A^T A \mathbf{c} = A^T \mathbf{y}$
;; - **Linear** and **polynomial** fitting via Vandermonde matrices
;; - **QR-based** least squares for numerical stability
;; - **SVD-based** least squares (pseudoinverse)
;; - **Trigonometric fitting** with custom basis functions
;; - **Condition number** as a diagnostic for ill-conditioned problems
