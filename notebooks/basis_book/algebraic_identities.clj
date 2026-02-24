;; # Algebraic Identities — A Verification Suite
;;
;; Linear algebra rests on a web of identities that every correct
;; implementation must satisfy. This notebook verifies them
;; systematically, serving as both a regression test and a
;; tutorial in abstract linear algebra.
;;
;; Each section states a theorem, explains why it matters, and
;; then verifies it numerically on several test matrices.

(ns basis-book.algebraic-identities
  (:require
   [scicloj.basis.linalg :as la]
   [scicloj.basis.impl.complex :as cx]
   [tech.v3.tensor :as tensor]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [scicloj.kindly.v4.kind :as kind]))

;; ## Test matrices
;;
;; We use a diverse set of matrices so that identities are tested
;; on both nice and awkward inputs.

(def A (la/matrix [[2 1 0]
                             [1 3 1]
                             [0 1 2]]))

(def B (la/matrix [[1 -1 2]
                             [0  2 1]
                             [3  0 1]]))

(def C (la/matrix [[-1 4 2]
                             [3 0 1]
                             [2 1 5]]))

(def I3 (la/eye 3))

(def v (la/column [1 2 3]))

;; A helper to check that two matrices are approximately equal:

(def close?
  (fn [x y]
    (let [diff (la/sub x y)]
      (< (la/norm diff) 1e-10))))

(def close-scalar?
  (fn [a b]
    (< (Math/abs (- (double a) (double b))) 1e-10)))

;; ---
;;
;; ## Matrix arithmetic
;;
;; ### Commutativity of addition: $A + B = B + A$
;;
;; Matrix addition is commutative because it operates element-wise.

(close? (la/add A B) (la/add B A))

(kind/test-last [true?])

;; ### Associativity of addition: $(A + B) + C = A + (B + C)$

(close? (la/add (la/add A B) C)
        (la/add A (la/add B C)))

(kind/test-last [true?])

;; ### Additive identity: $A + 0 = A$

(close? (la/add A (la/zeros 3 3)) A)

(kind/test-last [true?])

;; ### Additive inverse: $A - A = 0$

(< (la/norm (la/sub A A)) 1e-10)

(kind/test-last [true?])

;; ### Scalar distribution: $\alpha(A + B) = \alpha A + \alpha B$

(let [alpha 3.5]
  (close? (la/scale alpha (la/add A B))
          (la/add (la/scale alpha A) (la/scale alpha B))))

(kind/test-last [true?])

;; ### Scalar associativity: $(\alpha \beta) A = \alpha (\beta A)$

(let [alpha 2.0 beta 3.0]
  (close? (la/scale (* alpha beta) A)
          (la/scale alpha (la/scale beta A))))

(kind/test-last [true?])

;; ---
;;
;; ## Matrix multiplication
;;
;; ### Associativity: $(AB)C = A(BC)$
;;
;; Matrix multiplication is associative — the order of evaluation
;; doesn't matter, only the order of the factors.

(close? (la/mmul (la/mmul A B) C)
        (la/mmul A (la/mmul B C)))

(kind/test-last [true?])

;; ### Left identity: $IA = A$

(close? (la/mmul I3 A) A)

(kind/test-last [true?])

;; ### Right identity: $AI = A$

(close? (la/mmul A I3) A)

(kind/test-last [true?])

;; ### Distributivity: $A(B + C) = AB + AC$

(close? (la/mmul A (la/add B C))
        (la/add (la/mmul A B) (la/mmul A C)))

(kind/test-last [true?])

;; ### Right distributivity: $(A + B)C = AC + BC$

(close? (la/mmul (la/add A B) C)
        (la/add (la/mmul A C) (la/mmul B C)))

(kind/test-last [true?])

;; ### Scalar compatibility: $\alpha(AB) = (\alpha A)B = A(\alpha B)$

(let [alpha 2.5]
  (and (close? (la/scale alpha (la/mmul A B))
               (la/mmul (la/scale alpha A) B))
       (close? (la/scale alpha (la/mmul A B))
               (la/mmul A (la/scale alpha B)))))

(kind/test-last [true?])

;; ### Non-commutativity: $AB \neq BA$ in general
;;
;; Unlike addition, matrix multiplication is **not** commutative.
;; This is one of the most important facts in linear algebra.

(> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01)

(kind/test-last [true?])

;; ---
;;
;; ## Transpose
;;
;; The transpose $A^T$ flips rows and columns. It interacts with
;; other operations in predictable ways.
;;
;; ### Involution: $(A^T)^T = A$
;;
;; Transposing twice returns to the original.

(close? (la/transpose (la/transpose A)) A)

(kind/test-last [true?])

;; ### Sum: $(A + B)^T = A^T + B^T$

(close? (la/transpose (la/add A B))
        (la/add (la/transpose A) (la/transpose B)))

(kind/test-last [true?])

;; ### Product reversal: $(AB)^T = B^T A^T$
;;
;; The transpose of a product reverses the order. This is because
;; the transpose "reads" the matrix backwards.

(close? (la/transpose (la/mmul A B))
        (la/mmul (la/transpose B) (la/transpose A)))

(kind/test-last [true?])

;; ### Scalar: $(\alpha A)^T = \alpha A^T$

(let [alpha 4.0]
  (close? (la/transpose (la/scale alpha A))
          (la/scale alpha (la/transpose A))))

(kind/test-last [true?])

;; ---
;;
;; ## Trace
;;
;; The trace $\operatorname{tr}(A)$ is the sum of diagonal entries.
;; It is the simplest matrix invariant — unchanged under similarity
;; transformations.
;;
;; ### Linearity: $\operatorname{tr}(\alpha A + \beta B) = \alpha\operatorname{tr}(A) + \beta\operatorname{tr}(B)$

(let [alpha 2.0 beta 3.0]
  (close-scalar? (la/trace (la/add (la/scale alpha A)
                                   (la/scale beta B)))
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
  (and (close-scalar? trABC trBCA)
       (close-scalar? trBCA trCAB)))

(kind/test-last [true?])

;; ### Transpose: $\operatorname{tr}(A^T) = \operatorname{tr}(A)$

(close-scalar? (la/trace (la/transpose A)) (la/trace A))

(kind/test-last [true?])

;; ### Trace of identity: $\operatorname{tr}(I_n) = n$

(close-scalar? (la/trace I3) 3.0)

(kind/test-last [true?])

;; ---
;;
;; ## Determinant
;;
;; The determinant $\det(A)$ measures how $A$ scales volume.
;; It encodes invertibility: $\det(A) = 0$ iff $A$ is singular.
;;
;; ### Multiplicativity: $\det(AB) = \det(A) \det(B)$
;;
;; This is the most fundamental property: the determinant is a
;; **group homomorphism** from invertible matrices to nonzero reals.

(close-scalar? (la/det (la/mmul A B))
               (* (la/det A) (la/det B)))

(kind/test-last [true?])

;; ### Transpose: $\det(A^T) = \det(A)$

(close-scalar? (la/det (la/transpose A)) (la/det A))

(kind/test-last [true?])

;; ### Identity: $\det(I) = 1$

(close-scalar? (la/det I3) 1.0)

(kind/test-last [true?])

;; ### Inverse: $\det(A^{-1}) = 1 / \det(A)$

(close-scalar? (la/det (la/invert A))
               (/ 1.0 (la/det A)))

(kind/test-last [true?])

;; ### Scalar: $\det(\alpha A) = \alpha^n \det(A)$ for $n \times n$ matrix

(let [alpha 2.0 n 3]
  (close-scalar? (la/det (la/scale alpha A))
                 (* (Math/pow alpha n) (la/det A))))

(kind/test-last [true?])

;; ---
;;
;; ## Inverse
;;
;; A matrix $A$ is invertible when there exists $A^{-1}$ such that
;; $AA^{-1} = A^{-1}A = I$. The inverse encodes the ability to
;; "undo" a linear transformation.
;;
;; ### Definition: $A A^{-1} = I$

(close? (la/mmul A (la/invert A)) I3)

(kind/test-last [true?])

;; ### Right inverse: $A^{-1} A = I$

(close? (la/mmul (la/invert A) A) I3)

(kind/test-last [true?])

;; ### Involution: $(A^{-1})^{-1} = A$

(close? (la/invert (la/invert A)) A)

(kind/test-last [true?])

;; ### Product reversal: $(AB)^{-1} = B^{-1} A^{-1}$
;;
;; Like transpose, the inverse reverses the order of products.
;; To undo $A$ then $B$, you must undo $B$ first.

(close? (la/invert (la/mmul A B))
        (la/mmul (la/invert B) (la/invert A)))

(kind/test-last [true?])

;; ### Transpose commutation: $(A^{-1})^T = (A^T)^{-1}$

(close? (la/transpose (la/invert A))
        (la/invert (la/transpose A)))

(kind/test-last [true?])

;; ### Scalar: $(\alpha A)^{-1} = \frac{1}{\alpha} A^{-1}$

(let [alpha 2.0]
  (close? (la/invert (la/scale alpha A))
          (la/scale (/ 1.0 alpha) (la/invert A))))

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
     (< (la/norm (la/zeros 3 3)) 1e-10))

(kind/test-last [true?])

;; ### Scale: $\|\alpha A\|_F = |\alpha| \|A\|_F$

(let [alpha -2.5]
  (close-scalar? (la/norm (la/scale alpha A))
                 (* (Math/abs alpha) (la/norm A))))

(kind/test-last [true?])

;; ### Trace connection: $\|A\|_F^2 = \operatorname{tr}(A^T A)$
;;
;; The Frobenius norm squared equals the trace of $A^T A$.

(close-scalar? (* (la/norm A) (la/norm A))
               (la/trace (la/mmul (la/transpose A) A)))

(kind/test-last [true?])

;; ---
;;
;; ## QR decomposition
;;
;; Every matrix $A$ can be written as $A = QR$ where $Q$ is
;; orthogonal ($Q^T Q = I$) and $R$ is upper triangular.
;;
;; ### Reconstruction: $QR = A$

(let [{:keys [Q R]} (la/qr A)]
  (close? (la/mmul Q R) A))

(kind/test-last [true?])

;; ### Orthogonality: $Q^T Q = I$

(let [{:keys [Q]} (la/qr A)]
  (close? (la/mmul (la/transpose Q) Q) I3))

(kind/test-last [true?])

;; ### Triangularity: R is upper triangular
;;
;; All entries below the diagonal should be zero.

(let [{:keys [R]} (la/qr A)]
  (every? (fn [i]
            (every? (fn [j]
                      (< (Math/abs (tensor/mget R i j)) 1e-10))
                    (range 0 i)))
          (range 1 3)))

(kind/test-last [true?])

;; ---
;;
;; ## Eigendecomposition
;;
;; For a matrix $A$ with eigenvalue $\lambda$ and eigenvector $v$:
;; $Av = \lambda v$.
;;
;; ### Eigenvalue equation: $Av = \lambda v$

(let [{:keys [eigenvalues eigenvectors]} (la/eigen A)]
  (every? (fn [[[lam-re _lam-im] evec]]
            (when evec
              (let [Av (la/mmul A evec)
                    lam-v (la/scale lam-re evec)]
                (close? Av lam-v))))
          (map vector eigenvalues eigenvectors)))

(kind/test-last [true?])

;; ### Trace equals sum of eigenvalues: $\operatorname{tr}(A) = \sum \lambda_i$

(let [{:keys [eigenvalues]} (la/eigen A)
      eig-sum (reduce + (map first eigenvalues))]
  (close-scalar? (la/trace A) eig-sum))

(kind/test-last [true?])

;; ### Determinant equals product of eigenvalues: $\det(A) = \prod \lambda_i$

(let [{:keys [eigenvalues]} (la/eigen A)
      eig-prod (reduce * (map first eigenvalues))]
  (close-scalar? (la/det A) eig-prod))

(kind/test-last [true?])

;; ---
;;
;; ## SVD (Singular Value Decomposition)
;;
;; Any matrix $A$ can be written as $A = U \Sigma V^T$ where
;; $U$ and $V$ are orthogonal and $\Sigma$ is diagonal with
;; non-negative entries (the singular values).
;;
;; ### Reconstruction: $U \Sigma V^T = A$

(let [{:keys [U S Vt]} (la/svd A)
      Sigma (la/diag S)]
  (close? (la/mmul U (la/mmul Sigma Vt)) A))

(kind/test-last [true?])

;; ### U is orthogonal: $U^T U = I$

(let [{:keys [U]} (la/svd A)]
  (close? (la/mmul (la/transpose U) U) I3))

(kind/test-last [true?])

;; ### V is orthogonal: $V^T V = I$

(let [{:keys [Vt]} (la/svd A)]
  (close? (la/mmul Vt (la/transpose Vt)) I3))

(kind/test-last [true?])

;; ### Singular values equal square roots of eigenvalues of $A^T A$

(let [{:keys [S]} (la/svd A)
      AtA-eigs (->> (:eigenvalues (la/eigen (la/mmul (la/transpose A) A)))
                    (map first)
                    sort
                    reverse
                    vec)
      sv-squared (mapv #(* % %) (sort > S))]
  (every? identity
          (map (fn [a b] (< (Math/abs (- a b)) 1e-8))
               sv-squared AtA-eigs)))

(kind/test-last [true?])

;; ### Frobenius norm from singular values: $\|A\|_F = \sqrt{\sum \sigma_i^2}$

(let [{:keys [S]} (la/svd A)
      sv-norm (Math/sqrt (reduce + (map #(* % %) S)))]
  (close-scalar? (la/norm A) sv-norm))

(kind/test-last [true?])

;; ---
;;
;; ## Cholesky decomposition
;;
;; A symmetric positive definite (SPD) matrix $M$ can be written
;; as $M = L L^T$ where $L$ is lower triangular. This is the
;; matrix analogue of taking a square root.
;;
;; ### Reconstruction: $L L^T = M$ for SPD matrix $A^T A + I$

(let [M (la/add (la/mmul (la/transpose A) A) I3)
      L (la/cholesky M)]
  (close? (la/mmul L (la/transpose L)) M))

(kind/test-last [true?])

;; ### Returns nil for non-SPD matrices

(nil? (la/cholesky (la/matrix [[1 2] [2 1]])))

(kind/test-last [true?])

;; ---
;;
;; ## Linear solve
;;
;; Solving $Ax = b$ is the central computation of linear algebra.
;;
;; ### Solution satisfies $Ax = b$

(let [b (la/column [1 2 3])
      x (la/solve A b)]
  (close? (la/mmul A x) b))

(kind/test-last [true?])

;; ### Inverse solves: $A^{-1} b = x$

(let [b (la/column [1 2 3])
      x-solve (la/solve A b)
      x-inv (la/mmul (la/invert A) b)]
  (close? x-solve x-inv))

(kind/test-last [true?])

;; ---
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
      product (cx/cmul det-A det-B)]
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
;; - **Complex matrices**: associativity, Hermitian property,
;;   multiplicative determinant, solve, inverse
