;; # Eigenvalues and Decompositions
;;
;; Some vectors survive a matrix transformation with only a change
;; in scale — these **eigenvectors** and their **eigenvalues** reveal
;; the natural axes of a linear map. This chapter builds from
;; eigenvalues through diagonalisation, the spectral theorem, the SVD,
;; and positive definite matrices to a capstone example tying
;; everything together.

(ns la-linea-book.eigenvalues-and-decompositions
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.la-linea.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Arrow diagrams for 2D vectors:
   [scicloj.la-linea.vis :as vis]))

;; ## Eigenvalues and eigenvectors
;;
;; ### The idea
;;
;; Most vectors change direction when multiplied by a matrix.
;; But some special vectors only get scaled — they keep pointing
;; the same way (or flip to the opposite direction). These are
;; the **eigenvectors**.
;;
;; Formally, $\mathbf{v}$ is an eigenvector of $A$ if:
;;
;; $$A\mathbf{v} = \lambda \mathbf{v}$$
;;
;; for some scalar $\lambda$ (the **eigenvalue**). The map $A$
;; acts on $\mathbf{v}$ by merely scaling it by $\lambda$.

;; In two dimensions we can see this directly. The matrix
;; $\begin{bmatrix} 2 & 1 \\ 0 & 3 \end{bmatrix}$ has
;; eigenvectors $[1,0]^T$ (eigenvalue 2) and $[1,1]^T$ (eigenvalue 3).
;; Each eigenvector only gets scaled — it stays on the same line:

(vis/arrow-plot [{:label "v₁" :xy [1 0] :color "#2266cc"}
                 {:label "Av₁=2v₁" :xy [2 0] :color "#2266cc" :dashed? true}
                 {:label "v₂" :xy [1 1] :color "#cc4422"}
                 {:label "Av₂=3v₂" :xy [3 3] :color "#cc4422" :dashed? true}]
                {})

;; Consider:

(def A-eig
  (la/matrix [[4 1 2]
              [0 3 1]
              [0 0 2]]))

;; This upper-triangular matrix has eigenvalues on the diagonal:
;; 4, 3, and 2.

(def eig-result (la/eigen A-eig))

;; `la/eigen` returns eigenvalues as a **ComplexTensor**, because
;; eigenvalues of a real matrix can be complex — the roots of the
;; characteristic polynomial may leave $\mathbb{R}$.  For this
;; matrix they happen to be real.  When we know all eigenvalues
;; are real (e.g., for symmetric matrices), `la/real-eigenvalues`
;; is a convenience that extracts the real parts and returns them
;; as a sorted real tensor:

(la/real-eigenvalues A-eig)

(kind/test-last
 [(fn [v]
    (and (< (Math/abs (- (nth v 0) 2.0)) 1e-10)
         (< (Math/abs (- (nth v 1) 3.0)) 1e-10)
         (< (Math/abs (- (nth v 2) 4.0)) 1e-10)))])

;; ### Verifying the eigenvalue equation
;;
;; For each eigenpair $(\lambda, \mathbf{v})$, the residual
;; $A\mathbf{v} - \lambda\mathbf{v}$ should be zero:

(every? (fn [i]
          (let [lam (cx/re ((:eigenvalues eig-result) i))
                ev (nth (:eigenvectors eig-result) i)]
            (< (la/norm (la/sub (la/mmul A-eig ev)
                                (la/scale ev lam)))
               1e-10)))
        (range 3))

(kind/test-last [true?])

;; ### Why eigenvalues matter
;;
;; Eigenvalues encode essential properties of a matrix:
;;
;; - **Trace** = sum of eigenvalues
;; - **Determinant** = product of eigenvalues
;; - **Invertibility**: a matrix is invertible iff no eigenvalue is zero
;; - **Stability** (in differential equations): negative eigenvalues mean decay
;;
;; Let us verify the trace and determinant connections:

(def eig-reals (cx/re (:eigenvalues eig-result)))

(< (Math/abs (- (la/trace A-eig) (dfn/sum eig-reals))) 1e-10)

(kind/test-last [true?])

(< (Math/abs (- (la/det A-eig) (reduce * (seq eig-reals)))) 1e-10)

(kind/test-last [true?])

;; ---
;;
;; ## Change of basis and diagonalisation
;;
;; ### The idea
;;
;; A matrix represents a linear map *with respect to a chosen basis*.
;; Choose a different basis and you get a different matrix for
;; the **same map**. This is a **change of basis**.
;;
;; If $P$ is the matrix whose columns are the new basis vectors,
;; then the matrix of the map in the new basis is:
;;
;; $$B = P^{-1} A P$$
;;
;; This is called a **similarity transform**. Two matrices
;; related this way represent the same linear map — they
;; share eigenvalues, trace, determinant, and rank.

;; ### Diagonalisation
;;
;; The most illuminating basis change uses the eigenvectors.
;; If $A$ has $n$ linearly independent eigenvectors, we can
;; diagonalise it: $A = P D P^{-1}$ where $D$ is diagonal.
;;
;; Consider:

(def A-diag
  (la/matrix [[2 1]
              [0 3]]))

;; Eigenvalues are 2 and 3. Build $P$ from the eigenvectors:

(def eig-diag (la/eigen A-diag))

(def P-diag
  (let [evecs (:eigenvectors eig-diag)
        sorted-idx (sort-by (fn [i] (cx/re ((:eigenvalues eig-diag) i)))
                            (range 2))]
    (la/matrix (mapv (fn [j]
                       (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
                     (range 2)))))

;; The columns of $P$ are the eigenvectors. Transpose to get
;; them as columns:

(def P-cols (la/transpose P-diag))

;; The similarity transform yields a diagonal matrix:

(def D-result
  (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols)))

D-result

(kind/test-last
 [(fn [d]
    (and (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1e-10)
         (< (Math/abs (tensor/mget d 0 1)) 1e-10)
         (< (Math/abs (tensor/mget d 1 0)) 1e-10)
         (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1e-10)))])

;; In the eigenvector basis, the map is just scaling along
;; each axis. This is the simplest possible form.

;; ### Powers of a matrix
;;
;; Diagonalisation makes it easy to compute powers:
;; $A^k = P D^k P^{-1}$, and $D^k$ just raises each
;; diagonal entry to the $k$th power.

(def A-diag-sq
  (la/mmul A-diag A-diag))

(def A-diag-sq-via-eigen
  (let [Pinv (la/invert P-cols)
        D2 (la/diag (dtype/make-reader :float64 2
                                       (let [lam (tensor/mget D-result idx idx)]
                                         (* lam lam))))]
    (la/mmul P-cols (la/mmul D2 Pinv))))

(la/close? A-diag-sq A-diag-sq-via-eigen)

(kind/test-last [true?])

;; ---
;;
;; ## Symmetric matrices and the spectral theorem
;;
;; ### Symmetric matrices
;;
;; A matrix $A$ is **symmetric** if $A = A^T$. Symmetric matrices
;; are everywhere in applications:
;;
;; - Covariance matrices in statistics
;; - Graph Laplacians in network analysis
;; - Hessians of scalar-valued functions in optimization
;;
;; They have three properties that general matrices lack:
;;
;; 1. **All eigenvalues are real** (no imaginary parts)
;; 2. **Eigenvectors for distinct eigenvalues are orthogonal**
;; 3. **The matrix can always be diagonalised** (by an orthogonal matrix)

(def S-sym
  (la/matrix [[4 2 0]
              [2 5 1]
              [0 1 3]]))

;; Verify symmetry:

(la/close? S-sym (la/transpose S-sym))

(kind/test-last [true?])

(def eig-S (la/eigen S-sym))

;; All eigenvalues are real (imaginary parts zero):

(< (dfn/reduce-max (dfn/abs (cx/im (:eigenvalues eig-S)))) 1e-10)

(kind/test-last [true?])

;; Eigenvectors are orthonormal. Build a matrix from them
;; and check $Q^T Q = I$:

(def Q-eig
  (let [evecs (:eigenvectors eig-S)]
    (la/matrix (mapv (fn [i] (vec (dtype/->reader (nth evecs i))))
                     (range 3)))))

(def QtQ (la/mmul Q-eig (la/transpose Q-eig)))

(la/norm (la/sub QtQ (la/eye 3)))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; ### The spectral theorem
;;
;; Every real symmetric matrix $A$ can be written:
;;
;; $$A = Q \Lambda Q^T$$
;;
;; where $Q$ is orthogonal (its columns are the eigenvectors)
;; and $\Lambda$ is diagonal (the eigenvalues on the diagonal).
;;
;; It says every symmetric matrix is just scaling along
;; orthogonal axes.
;;
;; The word "spectrum" for the eigenvalues comes from this
;; theorem — the eigenvalues are the "spectral lines" of the matrix.
;;
;; Symmetry ($A = A^T$) is the matrix expression of a deeper
;; abstract property: the operator is **self-adjoint** with
;; respect to the inner product:
;;
;; $$\langle A\mathbf{u}, \mathbf{v} \rangle = \langle \mathbf{u}, A\mathbf{v} \rangle \quad \text{for all vectors } \mathbf{u}, \mathbf{v}$$
;;
;; The spectral theorem is really about self-adjoint operators
;; on inner product spaces. In different settings it takes
;; different forms: for the integral inner product
;; $\langle f, g \rangle = \int f\,g\,dx$ on function spaces,
;; the self-adjoint condition leads to **Sturm-Liouville theory**
;; — the same spectral structure (real eigenvalues, orthogonal
;; eigenfunctions) appears in differential equations.

;; ---
;;
;; ## The SVD
;;
;; ### Beyond square matrices
;;
;; Eigendecomposition only applies to square matrices. The
;; **Singular Value Decomposition** (SVD) works for **any**
;; $m \times n$ matrix:
;;
;; $$A = U \Sigma V^T$$
;;
;; - $U$ ($m \times m$, orthogonal): left singular vectors
;; - $\Sigma$ ($m \times n$, diagonal): singular values $\sigma_i \geq 0$
;; - $V^T$ ($n \times n$, orthogonal): right singular vectors
;;
;; The singular values are always non-negative and arranged
;; in decreasing order. They measure how much the matrix
;; stretches each direction.

(def A-svd
  (la/matrix [[1 0 1]
              [0 1 1]]))

(def svd-A (la/svd A-svd))

(vec (:S svd-A))

(kind/test-last
 [(fn [s] (and (= 2 (count s))
               (every? pos? s)))])

;; ### What the SVD reveals
;;
;; - **Rank** = number of non-zero singular values
;; - **Condition number** $\kappa = \sigma_1 / \sigma_r$ — how
;;   sensitive the matrix is to perturbations
;; - **Column space** = span of the first $r$ columns of $U$
;; - **Row space** = span of the first $r$ rows of $V^T$
;; - **Null space** = span of the last $n - r$ rows of $V^T$
;;
;; The SVD unifies all four fundamental subspaces in one
;; factorisation.

;; ### Low-rank approximation
;;
;; The **Eckart-Young theorem** says the best rank-$k$
;; approximation to $A$ (minimising the Frobenius norm of
;; the error) is obtained by keeping only the $k$ largest
;; singular values and zeroing the rest.
;;
;; This is the mathematical foundation of dimensionality
;; reduction, image compression, and latent semantic analysis.

(def A-lr
  (la/matrix [[3 2 2]
              [2 3 -2]]))

(def svd-lr (la/svd A-lr))

(def sigmas (vec (:S svd-lr)))

sigmas

;; Rank-1 approximation — keep only $\sigma_1$:

(def A-rank1
  (la/scale (la/mmul (la/submatrix (:U svd-lr) :all [0])
                     (la/submatrix (:Vt svd-lr) [0] :all)) (first sigmas)))

;; The approximation error equals $\sigma_2$:

(def approx-err (la/norm (la/sub A-lr A-rank1)))

(< (Math/abs (- approx-err (second sigmas))) 1e-10)

(kind/test-last [true?])

;; ---
;;
;; ## Positive definite matrices
;;
;; ### Definition
;;
;; A symmetric matrix $M$ is **positive definite** (PD) if,
;; for every non-zero vector $\mathbf{x}$: $\mathbf{x}^T M \mathbf{x} > 0$.
;;
;; Think of it as a bowl shape — the quadratic form always
;; curves upward. Equivalent conditions:
;;
;; - All eigenvalues are positive
;; - All pivots are positive
;; - $M = R^T R$ for some invertible $R$
;;
;; Positive definite matrices arise as:
;;
;; - **Covariance matrices** in statistics
;; - **Gram matrices** $A^T A$ (always positive semi-definite)
;; - **Hessians** of strictly convex functions

;; $A^T A$ is always positive semi-definite:

(def ATA (la/mmul (la/transpose A-svd) A-svd))

(every? #(>= % -1e-10) (cx/re (:eigenvalues (la/eigen ATA))))

(kind/test-last [true?])

;; ### Cholesky decomposition
;;
;; Positive definite matrices have a special factorisation:
;; $M = L L^T$ where $L$ is lower triangular. This is the
;; **Cholesky decomposition** — like a "square root" for matrices.
;;
;; It is about twice as fast as general LU decomposition
;; and is the standard method for solving PD systems.

(def spd-mat
  (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3)))

(def chol-L (la/cholesky spd-mat))

chol-L

;; Verify $L L^T = M$:

(la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat))

(kind/test-last
 [(fn [d] (< d 1e-10))])

;; Non-positive-definite matrices have no Cholesky factor:

(la/cholesky (la/matrix [[1 2] [2 1]]))

(kind/test-last [nil?])

;; (The eigenvalues of that matrix are 3 and $-1$ — the
;; negative eigenvalue prevents a Cholesky factorisation.)

;; ---
;;
;; ## Putting it all together
;;
;; Let us explore a single matrix through all the lenses
;; we have developed.

(def A-final
  (la/matrix [[2 1 0]
              [1 3 1]
              [0 1 2]]))

;; This matrix is:
;;
;; - **Symmetric**: $A = A^T$
;; - **Positive definite**: all eigenvalues > 0

(la/close? A-final (la/transpose A-final))

(kind/test-last [true?])

;; ### Eigenvalues and eigenvectors

(def eig-final (la/eigen A-final))

(def final-eigenvalues
  (la/real-eigenvalues A-final))

final-eigenvalues

(kind/test-last
 [(fn [v] (and (= 3 (count v))
               (every? pos? v)))])

;; All eigenvalues are positive — the matrix is positive definite.

;; ### Trace = sum of eigenvalues

(< (Math/abs (- (la/trace A-final)
                (dfn/sum final-eigenvalues)))
   1e-10)

(kind/test-last [true?])

;; ### Determinant = product of eigenvalues

(< (Math/abs (- (la/det A-final)
                (reduce * final-eigenvalues)))
   1e-10)

(kind/test-last [true?])

;; ### SVD
;;
;; For a symmetric positive definite matrix, the singular values
;; equal the eigenvalues — the spectral theorem and the SVD
;; coincide.

(def final-svd (la/svd A-final))

(< (dfn/reduce-max
    (dfn/abs (dfn/- (double-array (sort (vec (:S final-svd))))
                    (double-array final-eigenvalues))))
   1e-10)

(kind/test-last [true?])

;; ### Full rank — invertible

(long (dfn/sum (dfn/> (:S final-svd) 1e-10)))

(kind/test-last
 [(fn [r] (= r 3))])

(def A-inv (la/invert A-final))

A-inv

(la/close? (la/mmul A-final A-inv) (la/eye 3))

(kind/test-last [true?])

;; ### Cholesky factorisation
;;
;; Since the matrix is symmetric and positive definite, it has
;; a Cholesky factorisation $A = LL^T$ where $L$ is lower triangular.

(def chol-final (la/cholesky A-final))

chol-final

(la/close? (la/mmul chol-final (la/transpose chol-final)) A-final)

(kind/test-last [true?])

;; These concepts are more connected than they may first
;; appear. Eigenvalues show up in the trace, determinant,
;; and singular values. Symmetry leads to orthogonal
;; eigenvectors and the spectral theorem. Positive
;; definiteness gives us Cholesky. The threads run
;; through most of what follows in the book.
