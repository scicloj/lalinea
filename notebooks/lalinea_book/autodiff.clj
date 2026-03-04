;; # Automatic Differentiation
;;
;; This chapter assumes familiarity with [partial derivatives](https://en.wikipedia.org/wiki/Derivative)
;; and the [chain rule](https://en.wikipedia.org/wiki/Chain_rule).
;; For a broader introduction to autodiff, the
;; [JAX autodiff cookbook](https://docs.jax.dev/en/latest/notebooks/autodiff_cookbook.html)
;; is a good companion resource.

;; ## Setup

(ns lalinea-book.autodiff
  (:require [scicloj.lalinea.linalg :as la]
            [scicloj.lalinea.tensor :as t]
            [scicloj.lalinea.tape :as tape]
            [scicloj.lalinea.grad :as grad]
            [scicloj.kindly.v4.kind :as kind]))

;; ## What is autodiff?
;;
;; Given a function $f$ composed from elementary operations, how
;; do we compute its derivatives?
;;
;; Three approaches:
;;
;; - **Finite differences**: approximate $f'(x) \approx \frac{f(x+h) - f(x)}{h}$.
;;   Simple, but noisy (rounding errors) and slow: computing the gradient
;;   of $f : \mathbb{R}^n \to \mathbb{R}$ requires $n$ evaluations.
;; - **Symbolic differentiation**: apply differentiation rules to the
;;   expression tree. Exact, but expressions swell exponentially
;;   for deep compositions.
;; - **[Automatic differentiation](https://en.wikipedia.org/wiki/Automatic_differentiation)**:
;;   decompose the computation into elementary steps and apply
;;   the chain rule at each step. Exact, efficient, and mechanical.
;;
;; For scalar-valued functions, reverse-mode autodiff is both
;; exact (like symbolic differentiation) and efficient (one backward
;; pass for the full gradient, regardless of input dimension).
;; La Linea implements reverse mode.
;;
;; ## Forward vs reverse mode
;;
;; The chain rule decomposes the derivative of a composition into
;; a product of [Jacobians](https://en.wikipedia.org/wiki/Jacobian_matrix_and_determinant).
;; The two modes differ in which direction they multiply this chain:
;;
;; - **Forward mode** (JVP — Jacobian-vector product): propagates
;;   tangent vectors forward through the computation. Efficient
;;   when there are **few inputs** and many outputs.
;; - **[Reverse mode](https://en.wikipedia.org/wiki/Automatic_differentiation#Reverse_accumulation)** (VJP — vector-Jacobian product): propagates
;;   cotangent vectors backward through the computation. Efficient
;;   when there are many inputs and **few outputs**.
;;
;; In machine learning and optimisation, we typically have a loss
;; function $f : \mathbb{R}^n \to \mathbb{R}$ — many parameters, one scalar
;; output. Reverse mode computes the full gradient $\nabla f$ in
;; **one backward pass**, regardless of $n$. This is why La Linea
;; implements reverse mode.
;;
;; ## The VJP
;;
;; Some terminology. A function $f : \mathbb{R}^n \to \mathbb{R}^m$
;; maps between two vector spaces: the **input space** $\mathbb{R}^n$
;; and the **output space** $\mathbb{R}^m$. Vectors in the input space
;; are called **tangents** — they represent small perturbations to the
;; input. Vectors in the output space that multiply the Jacobian from
;; the left are called **cotangents** — they represent how sensitive a
;; downstream scalar quantity is to each component of the output.
;;
;; Given a function $f : \mathbb{R}^n \to \mathbb{R}^m$, the Jacobian
;; $J$ is the $m \times n$ matrix of partial derivatives:
;;
;; $$J_{ij} = \frac{\partial f_i}{\partial x_j}$$
;;
;; The **vector-Jacobian product** (VJP) takes a cotangent vector
;; $\bar{v} \in \mathbb{R}^m$ (the same shape as the output) and returns
;; $\bar{v}^T J \in \mathbb{R}^n$ (the same shape as the input).
;;
;; For a scalar function ($m = 1$), the Jacobian is just the gradient
;; row vector $\nabla f^T$. Setting $\bar{v} = 1$ gives:
;;
;; $$\bar{v}^T J = 1 \cdot \nabla f^T = \nabla f^T$$
;;
;; So for scalar outputs, the VJP with $\bar{v} = 1$ **is** the gradient.
;;
;; ### A concrete example
;;
;; Consider $f(a, b) = a^2 b$. The Jacobian has one row (scalar output):
;;
;; $$J = \begin{bmatrix} 2ab & a^2 \end{bmatrix}$$
;;
;; The VJP with $\bar{v} = 1$ gives the gradient $[2ab, \; a^2]$.
;; At $a = 3, b = 2$: gradient is $[12, \; 9]$.
;;
;; We can verify this with La Linea. `grad/grad` takes a tape result,
;; a target scalar, and the inputs to differentiate with respect to.
;; For multiple inputs, pass a vector — it returns a map:

(let [a (t/matrix [3.0])
      b (t/matrix [2.0])
      tape-result (tape/with-tape
                    (la/sum (la/mul (la/sq a) b)))
      grads (grad/grad tape-result
                       (:result tape-result)
                       [a b])]
  {:grad-a ((grads a) 0)
   :grad-b ((grads b) 0)})

(kind/test-last
 [(fn [{:keys [grad-a grad-b]}]
    (and (< (abs (- grad-a 12.0)) 1e-10)
         (< (abs (- grad-b 9.0)) 1e-10)))])

;; ## VJP rules in La Linea
;;
;; Each elementary operation has a VJP rule that describes how
;; cotangents (gradients) flow backward through it. Given the
;; cotangent $\bar{g}$ of the output, the rule computes the
;; cotangents of the inputs.
;;
;; | Operation | VJP: cotangent of each input |
;; |-----------|---------------------------|
;; | `add(a,b)` | $\bar{g}, \; \bar{g}$ |
;; | `sub(a,b)` | $\bar{g}, \; -\bar{g}$ |
;; | `scale(a,\alpha)` | $\alpha \bar{g}$ |
;; | `mul(a,b)` | $\bar{g} \odot b, \; \bar{g} \odot a$ |
;; | `mmul(A,B)` | $\bar{g} B^T, \; A^T \bar{g}$ |
;; | `transpose(A)` | $\bar{g}^T$ |
;; | `trace(A)` | $\bar{g} \cdot I$ |
;; | `sq(a)` | $2a \odot \bar{g}$ |
;; | `sum(a)` | broadcast $\bar{g}$ |
;; | `dot(u,v)` | $\bar{g} \cdot v, \; \bar{g} \cdot u$ |
;; | `det(A)` | $\bar{g} \cdot \det(A) \cdot A^{-T}$ |
;; | `invert(A)` | $-A^{-T} \bar{g} A^{-T}$ |
;; | `norm(A)` | $\bar{g} \cdot A / \|A\|_F$ |
;;
;; ### Why these rules work
;;
;; Consider `add(a,b) = a + b`. The Jacobian with respect to each
;; input is the identity $I$. So the VJP is $\bar{g}^T I = \bar{g}$
;; for both inputs.
;;
;; For `mmul(A,B) = AB`, the derivative with respect to $A$ is
;; $\bar{g} B^T$ (apply the cotangent to the right factor's transpose)
;; and with respect to $B$ is $A^T \bar{g}$ (apply the left factor's
;; transpose to the cotangent).
;;
;; For `scale(a, \alpha) = \alpha a`, the derivative with respect to $a$
;; is $\alpha \bar{g}$. The scalar $\alpha$ is not differentiated.
;;
;; ## How La Linea implements it
;;
;; The implementation has two phases:
;;
;; **Forward pass** — `tape/with-tape` records each `la/`, `t/`, and
;; `elem/` operation as an entry in a DAG (directed acyclic graph).
;; Each entry stores:
;;
;; - The operation keyword (e.g. `:la/mmul`)
;; - References to the input values (tracked by identity)
;; - The output value
;;
;; **Backward pass** — `grad/grad` walks the tape entries in reverse
;; order, starting from the target scalar. For each entry that has a
;; VJP rule, it:
;;
;; 1. Looks up the cotangent of the output (initially 1.0 for the target)
;; 2. Applies the VJP rule to get cotangents for each input
;; 3. Accumulates these into the inputs' cotangent slots (additive, because
;;    a value used by multiple operations receives gradient contributions
;;    from each)
;;
;; The third argument to `grad/grad` specifies which inputs to return
;; gradients for — a single tensor or a vector of tensors.

;; ## Example: derivative of $\text{trace}(A^T A)$

;; The derivative of $\text{trace}(A^T A)$ with respect to $A$ is $2A$.
;; The VJP rules compose:
;; trace → identity scaled by cotangent,
;; mmul → cotangent times each factor's transpose,
;; transpose → transpose of cotangent.
;; Together they yield $2A$.

(def A (t/matrix [[1 2]
                  [3 4]]))

(def tape-result
  (tape/with-tape
    (la/trace (la/mmul (la/transpose A) A))))

(:result tape-result)

(kind/test-last
 [(fn [v] (== 30.0 v))])

;; The tape records three operations:

(mapv :op (:entries tape-result))

(kind/test-last
 [= [:la/transpose :la/mmul :la/trace]])

;; Compute the gradient — pass `A` as the input of interest:

(def grad-A
  (grad/grad tape-result (:result tape-result) A))

grad-A

;; The gradient equals 2A:

(la/close? grad-A (la/scale A 2))

(kind/test-last [true?])

;; The computation graph:

(tape/mermaid tape-result (:result tape-result))

;; ## Example: least-squares gradient

;; For $\|Ax - b\|^2 = \text{sum}(\text{sq}(Ax - b))$, the gradient with respect to $x$
;; is $2A^T(Ax - b)$. This connects automatic differentiation to the
;; [normal equations](https://en.wikipedia.org/wiki/Ordinary_least_squares#Normal_equations): setting the gradient to zero gives $A^T A x = A^T b$.

(def A2 (t/matrix [[1 0]
                   [0 2]
                   [1 1]]))

(def b (t/column [3 2 4]))

(def x (t/column [1 1]))

(def ls-tape
  (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A2 x) b)))))

(:result ls-tape)

(kind/test-last
 [(fn [v] (== 8.0 v))])

(def grad-x
  (grad/grad ls-tape (:result ls-tape) x))

grad-x

(kind/test-last
 [(fn [g] (la/close? g (t/column [-8 -4])))])

;; Verify against the analytic gradient $2A^T(Ax - b)$:

(def expected-grad
  (la/scale (la/mmul (la/transpose A2)
                     (la/sub (la/mmul A2 x) b))
            2))

expected-grad

(la/close? grad-x expected-grad)

(kind/test-last [true?])

;; The computation graph for this loss:

(tape/mermaid ls-tape (:result ls-tape))

;; ## Example: gradient with respect to a matrix

;; We can also differentiate with respect to the matrix A itself.
;; For $\text{loss} = \text{sum}(\text{sq}(Ax - b))$, the gradient with respect to $A$ is
;; $2(Ax - b)x^T$.

(def ls-tape-A
  (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A2 x) b)))))

(def grad-A2
  (grad/grad ls-tape-A (:result ls-tape-A) A2))

grad-A2

(kind/test-last
 [(fn [g] (la/close? g (t/matrix [[-4 -4] [0 0] [-4 -4]])))])

(def residual (la/sub (la/mmul A2 x) b))

(def expected-grad-A
  (la/scale (la/mmul residual (la/transpose x)) 2))

expected-grad-A

(la/close? grad-A2 expected-grad-A)

(kind/test-last [true?])

;; ## Gradients of det, invert, and norm
;;
;; The determinant, inverse, and Frobenius norm all have known
;; VJP rules (see the table above). Let's verify them.

;; ### Determinant
;;
;; $\frac{\partial}{\partial A} \det(A) = \det(A) \cdot A^{-T}$

(let [A (t/matrix [[2 1] [1 3]])
      tape-result (tape/with-tape (la/det A))
      grad-A (grad/grad tape-result
                        (:result tape-result) A)
      expected (la/scale (la/transpose (la/invert A))
                         (la/det A))]
  (la/close? grad-A expected))

(kind/test-last [true?])

;; ### Inverse
;;
;; For a composite $f(A) = \text{tr}(A^{-1})$:
;;
;; $\frac{\partial f}{\partial A} = -(A^{-T})^2$

(let [A (t/matrix [[2 1] [1 3]])
      tape-result (tape/with-tape
                    (la/trace (la/invert A)))
      grad-A (grad/grad tape-result
                        (:result tape-result) A)
      inv-t (la/transpose (la/invert A))
      expected (la/scale (la/mmul inv-t inv-t) -1.0)]
  (la/close? grad-A expected))

(kind/test-last [true?])

;; ### Frobenius norm
;;
;; $\frac{\partial}{\partial A} \|A\|_F = \frac{A}{\|A\|_F}$

(let [A (t/matrix [[3 0] [0 4]])
      tape-result (tape/with-tape (la/norm A))
      grad-A (grad/grad tape-result
                        (:result tape-result) A)
      expected (la/scale A (/ 1.0 (la/norm A)))]
  (la/close? grad-A expected))

(kind/test-last [true?])

;; ## Supported operations

;; The autodiff system supports these operations:
;;
;; - `la/add`, `la/sub` — addition and subtraction
;; - `la/scale` — scalar multiplication
;; - `la/mmul` — matrix multiplication
;; - `la/transpose` — transpose
;; - `la/mul` — element-wise multiplication
;; - `la/trace` — matrix trace
;; - `la/sq` — element-wise square
;; - `la/sum` — sum of all elements
;; - `la/dot` — inner product
;; - `la/det` — matrix determinant
;; - `la/invert` — matrix inverse
;; - `la/norm` — Frobenius norm
;;
;; Operations without VJP rules (like `la/svd`, `la/eigen`)
;; are ignored during the backward pass. Their inputs will not receive
;; gradients.

;; ## Example: gradient descent
;;
;; Putting it together — a toy gradient descent loop that
;; minimises $\|Ax - b\|^2$ by following the gradient.
;; Each iteration records a fresh tape, computes the gradient
;; of the loss with respect to $x$, and takes a step.

(def A-gd (t/matrix [[1 0]
                     [0 2]
                     [1 1]]))

(def b-gd (t/column [3 2 4]))

(defn ls-step
  "One gradient descent step for ||Ax - b||²."
  [x lr]
  (let [tape-result (tape/with-tape
                      (la/sum (la/sq (la/sub
                                      (la/mmul A-gd x)
                                      b-gd))))
        g (grad/grad tape-result
                     (:result tape-result) x)]
    (la/sub x (la/scale g lr))))

(def x-gd
  (reduce (fn [x _] (ls-step x 0.05))
          (t/column [0 0])
          (range 200)))

x-gd

;; Compare with the exact least-squares solution:

(def x-exact (:x (la/lstsq A-gd b-gd)))

x-exact

(la/close? x-gd x-exact 1e-4)

(kind/test-last [true?])
