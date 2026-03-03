;; # Algebraic Identities — A Verification Suite
;;
;; Linear algebra rests on a web of identities that every correct
;; implementation must satisfy. This notebook verifies them
;; systematically, serving as both a regression test and a
;; tutorial in abstract linear algebra.
;;
;; Each section states a theorem, explains why it matters, and
;; then verifies it numerically on several test matrices.

(ns lalinea-book.algebraic-identities
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.elementwise :as elem]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.lalinea.complex :as cx]   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Test matrices
;;
;; We use a diverse set of matrices so that identities are tested
;; on both nice and awkward inputs.

(def A (t/matrix [[2 1 0]
                  [1 3 1]
                  [0 1 2]]))

(def B (t/matrix [[1 -1 2]
                  [0  2 1]
                  [3  0 1]]))

(def C (t/matrix [[-1 4 2]
                  [3 0 1]
                  [2 1 5]]))

(def I3 (t/eye 3))

(def v (t/column [1 2 3]))

;; ---
;;
;; ## Matrix arithmetic
;;
;; ### Commutativity of addition: $A + B = B + A$
;;
;; Matrix addition is commutative because it operates element-wise.

(la/close? (la/add A B) (la/add B A))

(kind/test-last [true?])

;; ### Associativity of addition: $(A + B) + C = A + (B + C)$

(la/close? (la/add (la/add A B) C)
           (la/add A (la/add B C)))

(kind/test-last [true?])

;; ### Additive identity: $A + 0 = A$

(la/close? (la/add A (t/zeros 3 3)) A)

(kind/test-last [true?])

;; ### Additive inverse: $A - A = 0$

(< (la/norm (la/sub A A)) 1e-10)

(kind/test-last [true?])

;; ### Scalar distribution: $\alpha(A + B) = \alpha A + \alpha B$

(let [alpha 3.5]
  (la/close? (la/scale (la/add A B) alpha)
             (la/add (la/scale A alpha) (la/scale B alpha))))

(kind/test-last [true?])

;; ### Scalar associativity: $(\alpha \beta) A = \alpha (\beta A)$

(let [alpha 2.0 beta 3.0]
  (la/close? (la/scale A (* alpha beta))
             (la/scale (la/scale A beta) alpha)))

(kind/test-last [true?])

;; ---
;;
;; ## Matrix multiplication
;;
;; ### Associativity: $(AB)C = A(BC)$
;;
;; Matrix multiplication is associative — the order of evaluation
;; doesn't matter, only the order of the factors.

(la/close? (la/mmul (la/mmul A B) C)
           (la/mmul A (la/mmul B C)))

(kind/test-last [true?])

;; ### Left identity: $IA = A$

(la/close? (la/mmul I3 A) A)

(kind/test-last [true?])

;; ### Right identity: $AI = A$

(la/close? (la/mmul A I3) A)

(kind/test-last [true?])

;; ### Distributivity: $A(B + C) = AB + AC$

(la/close? (la/mmul A (la/add B C))
           (la/add (la/mmul A B) (la/mmul A C)))

(kind/test-last [true?])

;; ### Right distributivity: $(A + B)C = AC + BC$

(la/close? (la/mmul (la/add A B) C)
           (la/add (la/mmul A C) (la/mmul B C)))

(kind/test-last [true?])

;; ### Scalar compatibility: $\alpha(AB) = (\alpha A)B = A(\alpha B)$

(let [alpha 2.5]
  (and (la/close? (la/scale (la/mmul A B) alpha)
                  (la/mmul (la/scale A alpha) B))
       (la/close? (la/scale (la/mmul A B) alpha)
                  (la/mmul A (la/scale B alpha)))))

(kind/test-last [true?])

;; ### Non-commutativity: $AB \neq BA$ in general
;;
;; Unlike addition, matrix multiplication is **not** commutative.

(> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01)

(kind/test-last [true?])

;; ---
;;
;; ## [Transpose](https://en.wikipedia.org/wiki/Transpose)
;;
;; The transpose $A^T$ flips rows and columns. It interacts with
;; other operations in predictable ways.
;;
;; ### Involution: $(A^T)^T = A$
;;
;; Transposing twice returns to the original.

(la/close? (la/transpose (la/transpose A)) A)

(kind/test-last [true?])

;; ### Sum: $(A + B)^T = A^T + B^T$

(la/close? (la/transpose (la/add A B))
           (la/add (la/transpose A) (la/transpose B)))

(kind/test-last [true?])

;; ### Product reversal: $(AB)^T = B^T A^T$
;;
;; The transpose of a product reverses the order. This is because
;; the transpose "reads" the matrix backwards.

(la/close? (la/transpose (la/mmul A B))
           (la/mmul (la/transpose B) (la/transpose A)))

(kind/test-last [true?])

;; ### Scalar: $(\alpha A)^T = \alpha A^T$

(let [alpha 4.0]
  (la/close? (la/transpose (la/scale A alpha))
             (la/scale (la/transpose A) alpha)))

(kind/test-last [true?])

;; ---
;;
;; ## [Trace](https://en.wikipedia.org/wiki/Trace_(linear_algebra))
;;
;; The trace $\operatorname{tr}(A)$ is the sum of diagonal entries.
;; It is the simplest matrix invariant — unchanged under similarity
;; transformations.
;;
;; ### Linearity: $\operatorname{tr}(\alpha A + \beta B) = \alpha\operatorname{tr}(A) + \beta\operatorname{tr}(B)$

(let [alpha 2.0 beta 3.0]
  (la/close-scalar? (la/trace (la/add (la/scale A alpha)
                                      (la/scale B beta)))
                    (+ (* alpha (la/trace A))
                       (* beta (la/trace B)))))

(kind/test-last [true?])

;; ### Cyclic property: $\operatorname{tr}(ABC) = \operatorname{tr}(BCA) = \operatorname{tr}(CAB)$
;;
;; You can rotate factors under the trace. This is because
;; $\operatorname{tr}(XY) = \operatorname{tr}(YX)$ for any compatible $X$, $Y$.

(let [trABC (la/trace (la/mmul A (la/mmul B C)))
      trBCA (la/trace (la/mmul B (la/mmul C A)))
      trCAB (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA)
       (la/close-scalar? trBCA trCAB)))

(kind/test-last [true?])

;; ### Transpose: $\operatorname{tr}(A^T) = \operatorname{tr}(A)$

(la/close-scalar? (la/trace (la/transpose A)) (la/trace A))

(kind/test-last [true?])

;; ### Trace of identity: $\operatorname{tr}(I_n) = n$

(la/close-scalar? (la/trace I3) 3.0)

(kind/test-last [true?])

;; ---
;;
;; ## [Determinant](https://en.wikipedia.org/wiki/Determinant)
;;
;; The determinant $\det(A)$ measures how $A$ scales volume.
;; It encodes invertibility: $\det(A) = 0$ iff $A$ is singular.
;;
;; ### Multiplicativity: $\det(AB) = \det(A) \det(B)$
;;
;; This is the most fundamental property: the determinant is a
;; **group homomorphism** from invertible matrices to nonzero reals.

(la/close-scalar? (la/det (la/mmul A B))
                  (* (la/det A) (la/det B)))

(kind/test-last [true?])

;; ### Transpose: $\det(A^T) = \det(A)$

(la/close-scalar? (la/det (la/transpose A)) (la/det A))

(kind/test-last [true?])

;; ### Identity: $\det(I) = 1$

(la/close-scalar? (la/det I3) 1.0)

(kind/test-last [true?])

;; ### Inverse: $\det(A^{-1}) = 1 / \det(A)$

(la/close-scalar? (la/det (la/invert A))
                  (/ 1.0 (la/det A)))

(kind/test-last [true?])

;; ### Scalar: $\det(\alpha A) = \alpha^n \det(A)$ for $n \times n$ matrix

(let [alpha 2.0 n 3]
  (la/close-scalar? (la/det (la/scale A alpha))
                    (* (math/pow alpha n) (la/det A))))

(kind/test-last [true?])

;; ---
;;
;; ## [Inverse](https://en.wikipedia.org/wiki/Invertible_matrix)
;;
;; A matrix $A$ is invertible when there exists $A^{-1}$ such that
;; $AA^{-1} = A^{-1}A = I$. The inverse encodes the ability to
;; "undo" a linear transformation.
;;
;; ### Definition: $A A^{-1} = I$

(la/close? (la/mmul A (la/invert A)) I3)

(kind/test-last [true?])

;; ### Right inverse: $A^{-1} A = I$

(la/close? (la/mmul (la/invert A) A) I3)

(kind/test-last [true?])

;; ### Involution: $(A^{-1})^{-1} = A$

(la/close? (la/invert (la/invert A)) A)

(kind/test-last [true?])

;; ### Product reversal: $(AB)^{-1} = B^{-1} A^{-1}$
;;
;; Like transpose, the inverse reverses the order of products.
;; To undo $A$ then $B$, you must undo $B$ first.

(la/close? (la/invert (la/mmul A B))
           (la/mmul (la/invert B) (la/invert A)))

(kind/test-last [true?])

;; ### Transpose commutation: $(A^{-1})^T = (A^T)^{-1}$

(la/close? (la/transpose (la/invert A))
           (la/invert (la/transpose A)))

(kind/test-last [true?])

;; ### Scalar: $(\alpha A)^{-1} = \frac{1}{\alpha} A^{-1}$

(let [alpha 2.0]
  (la/close? (la/invert (la/scale A alpha))
             (la/scale (la/invert A) (/ 1.0 alpha))))

(kind/test-last [true?])

;; ---
;;
;; ## Frobenius norm
;;
;; The Frobenius norm $\|A\|_F = \sqrt{\sum_{i,j} a_{ij}^2}$
;; generalizes the Euclidean vector norm to matrices.
;;
;; ### Non-negativity: $\|A\|_F \geq 0$, with equality iff $A = 0$

(and (>= (la/norm A) 0)
     (> (la/norm A) 0)
     (< (la/norm (t/zeros 3 3)) 1e-10))

(kind/test-last [true?])

;; ### Scale: $\|\alpha A\|_F = |\alpha| \|A\|_F$

(let [alpha -2.5]
  (la/close-scalar? (la/norm (la/scale A alpha))
                    (* (abs alpha) (la/norm A))))

(kind/test-last [true?])

;; ### Trace connection: $\|A\|_F^2 = \operatorname{tr}(A^T A)$
;;
;; The Frobenius norm squared equals the trace of $A^T A$.

(la/close-scalar? (* (la/norm A) (la/norm A))
                  (la/trace (la/mmul (la/transpose A) A)))

(kind/test-last [true?])

;; ---
;;
;; ## [QR decomposition](https://en.wikipedia.org/wiki/QR_decomposition)
;;
;; Every matrix $A$ can be written as $A = QR$ where $Q$ is
;; orthogonal ($Q^T Q = I$) and $R$ is upper triangular.
;;
;; ### Reconstruction: $QR = A$

(let [{:keys [Q R]} (la/qr A)]
  (la/close? (la/mmul Q R) A))

(kind/test-last [true?])

;; ### Orthogonality: $Q^T Q = I$

(let [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3))

(kind/test-last [true?])

;; ### Triangularity: R is upper triangular
;;
;; All entries below the diagonal should be zero.

(let [{:keys [R]} (la/qr A)]
  (every? (fn [i]
            (every? (fn [j]
                      (< (abs (R i j)) 1e-10))
                    (range 0 i)))
          (range 1 3)))

(kind/test-last [true?])

;; ---
;;
;; ## [Eigendecomposition](https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix)
;;
;; For a matrix $A$ with eigenvalue $\lambda$ and eigenvector $v$:
;; $Av = \lambda v$.
;;
;; ### Eigenvalue equation: $Av = \lambda v$

(let [{:keys [eigenvalues eigenvectors]} (la/eigen A)
      reals (cx/re eigenvalues)]
  (every? (fn [[i evec]]
            (when evec
              (let [Av (la/mmul A evec)
                    lam-v (la/scale evec (double (reals i)))]
                (la/close? Av lam-v))))
          (map-indexed vector eigenvectors)))

(kind/test-last [true?])

;; ### Trace equals sum of eigenvalues: $\operatorname{tr}(A) = \sum \lambda_i$

(let [{:keys [eigenvalues]} (la/eigen A)
      eig-sum (la/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum))

(kind/test-last [true?])

;; ### Determinant equals product of eigenvalues: $\det(A) = \prod \lambda_i$

(let [{:keys [eigenvalues]} (la/eigen A)
      eig-prod (la/prod (cx/re eigenvalues))]
  (la/close-scalar? (la/det A) eig-prod))

(kind/test-last [true?])

;; ---
;;
;; ## [SVD](https://en.wikipedia.org/wiki/Singular_value_decomposition) (Singular Value Decomposition)
;;
;; Any matrix $A$ can be written as $A = U \Sigma V^T$ where
;; $U$ and $V$ are orthogonal and $\Sigma$ is diagonal with
;; non-negative entries (the singular values).
;;
;; ### Reconstruction: $U \Sigma V^T = A$

(let [{:keys [U S Vt]} (la/svd A)
      Sigma (t/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A))

(kind/test-last [true?])

;; ### U is orthogonal: $U^T U = I$

(let [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3))

(kind/test-last [true?])

;; ### V is orthogonal: $V^T V = I$

(let [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3))

(kind/test-last [true?])

;; ### Singular values equal square roots of eigenvalues of $A^T A$

(let [{:keys [S]} (la/svd A)
      AtA-eigs (la/real-eigenvalues (la/mmul (la/transpose A) A))
      sv-squared (sort > (la/sq S))]
  (la/close? (t/->real-tensor sv-squared)
             (t/->real-tensor (reverse AtA-eigs)) 1e-8))

(kind/test-last [true?])

;; ### [Frobenius norm](https://en.wikipedia.org/wiki/Matrix_norm#Frobenius_norm) from singular values: $\|A\|_F = \sqrt{\sum \sigma_i^2}$

(let [{:keys [S]} (la/svd A)
      sv-norm (math/sqrt (la/sum (la/mul S S)))]
  (la/close-scalar? (la/norm A) sv-norm))

(kind/test-last [true?])

;; ---
;;
;; ## [Cholesky decomposition](https://en.wikipedia.org/wiki/Cholesky_decomposition)
;;
;; A symmetric positive definite (SPD) matrix $M$ can be written
;; as $M = L L^T$ where $L$ is lower triangular. This is the
;; matrix analogue of taking a square root.
;;
;; ### Reconstruction: $L L^T = M$ for SPD matrix $A^T A + I$

(let [M (la/add (la/mmul (la/transpose A) A) I3)
      L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M))

(kind/test-last [true?])

;; ### Returns nil for non-SPD matrices

(nil? (la/cholesky (t/matrix [[1 2] [2 1]])))

(kind/test-last [true?])

;; ---
;;
;; ## [Linear solve](https://en.wikipedia.org/wiki/System_of_linear_equations)
;;
;; Solving $Ax = b$ is the central computation of linear algebra.
;;
;; ### Solution satisfies $Ax = b$

(let [b (t/column [1 2 3])
      x (la/solve A b)]
  (la/close? (la/mmul A x) b))

(kind/test-last [true?])

;; ### Inverse solves: $A^{-1} b = x$

(let [b (t/column [1 2 3])
      x-solve (la/solve A b)
      x-inv (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv))

(kind/test-last [true?])

;; ---
;;
;; ## Complex vector arithmetic
;;
;; Element-wise complex operations satisfy the same algebraic
;; rules as scalar complex arithmetic.

(def ca (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0]))
(def cb (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0]))

;; ### Commutativity: $a \cdot b = b \cdot a$

(la/close? (la/mul ca cb) (la/mul cb ca))

(kind/test-last [true?])

;; ### Conjugate is an involution: $\overline{\overline{a}} = a$

(la/close? (cx/conj (cx/conj ca)) ca)

(kind/test-last [true?])

;; ### Conjugate distributes: $\overline{a \cdot b} = \bar{a} \cdot \bar{b}$

(la/close? (cx/conj (la/mul ca cb))
           (la/mul (cx/conj ca) (cx/conj cb)))

(kind/test-last [true?])

;; ### Magnitude is multiplicative: $|a \cdot b| = |a| \cdot |b|$

(< (elem/reduce-max
    (elem/abs (la/sub (la/abs (la/mul ca cb))
                      (la/mul (la/abs ca) (la/abs cb)))))
   1e-10)

(kind/test-last [true?])

;; ### [Cauchy-Schwarz](https://en.wikipedia.org/wiki/Cauchy%E2%80%93Schwarz_inequality): $|\langle a, b \rangle_H|^2 \leq \langle a, a \rangle_H \cdot \langle b, b \rangle_H$

(let [d-ab (la/dot ca cb)
      re-ab (double (cx/re d-ab))
      im-ab (double (cx/im d-ab))
      re-aa (double (cx/re (la/dot ca ca)))
      re-bb (double (cx/re (la/dot cb cb)))]
  (<= (- (+ (* re-ab re-ab) (* im-ab im-ab)) 1e-10)
      (* re-aa re-bb)))

(kind/test-last [true?])

;;
;; ## Complex matrices
;;
;; All the identities above should hold for complex matrices too.
;; Note that transpose becomes conjugate transpose ($A^\dagger$)
;; in the complex case.
;;
;; ### Complex multiplication is associative

(let [CA (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
      CB (cx/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
      CC (cx/complex-tensor [[0 1] [2 -1]] [[3 0] [1 1]])]
  (< (la/norm (la/sub (la/mmul (la/mmul CA CB) CC)
                      (la/mmul CA (la/mmul CB CC))))
     1e-10))

(kind/test-last [true?])

;; ### $A A^\dagger$ is Hermitian (equals its own conjugate transpose)

(let [CA (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
      AAdag (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1e-10))

(kind/test-last [true?])

;; ### Complex determinant is multiplicative

(let [CA (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
      CB (cx/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
      det-AB (la/det (la/mmul CA CB))
      det-A (la/det CA)
      det-B (la/det CB)
      product (la/mul det-A det-B)]
  (< (la/norm (la/sub det-AB product)) 1e-10))

(kind/test-last [true?])

;; ### Complex solve: $Ax = b$ verified

(let [CA (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
      Cb (cx/complex-tensor [[1] [2]] [[1] [0]])
      Cx (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1e-10))

(kind/test-last [true?])

;; ### Complex inverse: $A A^{-1} = I$

(let [CA (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
      CI (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1e-10))

(kind/test-last [true?])

;; ---
;;
;; ## Summary
;;
;; This notebook verified:
;;
;; - **Matrix arithmetic**: commutativity and associativity of addition,
;;   scalar distribution, additive identity and inverse
;; - **Matrix multiplication**: associativity, identity, distributivity,
;;   scalar compatibility, non-commutativity
;; - **Transpose**: involution, linearity, product reversal
;; - **Trace**: linearity, cyclic property, transpose invariance
;; - **Determinant**: multiplicativity, transpose invariance, inverse,
;;   scalar power, identity
;; - **Inverse**: definition, involution, product reversal, transpose
;;   commutation, scalar
;; - **Frobenius norm**: non-negativity, scaling, trace connection
;; - **QR**: reconstruction, orthogonality, triangularity
;; - **Eigendecomposition**: eigenvalue equation, trace = sum of eigenvalues,
;;   determinant = product of eigenvalues
;; - **SVD**: reconstruction, orthogonality, connection to eigenvalues,
;;   Frobenius norm
;; - **Cholesky**: reconstruction for SPD, nil for non-SPD
;; - **Linear solve**: solution satisfies equation, agrees with inverse
;; - **Complex vector arithmetic**: commutativity, conjugate involution,
;;   conjugate distribution, magnitude multiplicativity, Cauchy-Schwarz
;; - **Complex matrices**: associativity, Hermitian property,
;;   multiplicative determinant, solve, inverse
