;; # Automatic Differentiation

;; The computation tape records a DAG of operations. [Reverse-mode](https://en.wikipedia.org/wiki/Automatic_differentiation#Forward_and_reverse_accumulation)
;; [automatic differentiation](https://en.wikipedia.org/wiki/Automatic_differentiation) walks this DAG backwards to compute
;; gradients — the derivative of a scalar output with respect to
;; each input tensor.

;; ## Setup

(ns lalinea-book.autodiff
  (:require [scicloj.lalinea.linalg :as la]
            [scicloj.lalinea.tape :as tape]
            [scicloj.lalinea.grad :as grad]
            [scicloj.kindly.v4.kind :as kind]))

;; ## How it works

;; Each `la/` operation has a [VJP](https://en.wikipedia.org/wiki/Automatic_differentiation#Reverse_accumulation) (vector-Jacobian product) rule that
;; describes how gradients flow backwards through it. For example:
;;
;; | Operation | Gradient rule |
;; |-----------|--------------|
;; | `add(a,b)` | $\bar{g}, \bar{g}$ |
;; | `sub(a,b)` | $\bar{g}, -\bar{g}$ |
;; | `mmul(A,B)` | $\bar{g}B^T,\; A^T\bar{g}$ |
;; | `transpose(A)` | $\bar{g}^T$ |
;; | `trace(A)` | $\bar{g} \cdot I$ |
;; | `sq(a)` | $2a \odot \bar{g}$ |
;; | `sum(a)` | broadcast $\bar{g}$ |
;;
;; `grad/grad` takes a tape result and a scalar target value, then
;; walks entries in reverse to accumulate gradients for each input.

;; ## Example: derivative of $\text{trace}(A^T A)$

;; The derivative of $\text{trace}(A^T A)$ with respect to $A$ is $2A$. Let us
;; verify this with automatic differentiation.

(def A (la/matrix [[1 2]
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

;; Compute the gradient:

(def grads (grad/grad tape-result (:result tape-result)))

(def grad-A (.get grads A))

;; The gradient equals 2A:

(la/close? grad-A (la/scale A 2))

(kind/test-last [true?])

;; The computation graph:

(tape/mermaid tape-result (:result tape-result))

;; ## Example: least-squares gradient

;; For $\|Ax - b\|^2 = \text{sum}(\text{sq}(Ax - b))$, the gradient with respect to $x$
;; is $2A^T(Ax - b)$. This connects automatic differentiation to the
;; [normal equations](https://en.wikipedia.org/wiki/Ordinary_least_squares#Normal_equations): setting the gradient to zero gives $A^T A x = A^T b$.

(def A2 (la/matrix [[1 0]
                    [0 2]
                    [1 1]]))

(def b (la/column [3 2 4]))

(def x (la/column [1 1]))

(def ls-tape
  (tape/with-tape
    (la/sum (la/sq (la/sub (la/mmul A2 x) b)))))

(:result ls-tape)

(kind/test-last
 [(fn [v] (== 8.0 v))])

(def ls-grads (grad/grad ls-tape (:result ls-tape)))

(def grad-x (.get ls-grads x))

grad-x

(kind/test-last
 [(fn [g] (la/close? g (la/column [-8 -4])))])

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

(def grads-A (grad/grad ls-tape-A (:result ls-tape-A)))

(def grad-A2 (.get grads-A A2))

grad-A2

(kind/test-last
 [(fn [g] (la/close? g (la/matrix [[-4 -4] [0 0] [-4 -4]])))])

(def residual (la/sub (la/mmul A2 x) b))

(def expected-grad-A (la/scale (la/mmul residual (la/transpose x)) 2))

expected-grad-A

(la/close? grad-A2 expected-grad-A)

(kind/test-last [true?])

;; ## Gradients of det, invert, and norm
;;
;; The determinant, inverse, and Frobenius norm all have known
;; VJP rules. Let's verify them.

;; ### Determinant
;;
;; $\frac{\partial}{\partial A} \det(A) = \det(A) \cdot A^{-T}$

(let [A (la/matrix [[2 1] [1 3]])
      tape-result (tape/with-tape (la/det A))
      grads (grad/grad tape-result (:result tape-result))
      grad-A (.get grads A)
      expected (la/scale (la/transpose (la/invert A))
                         (la/det A))]
  (la/close? grad-A expected))

(kind/test-last [true?])

;; ### Inverse
;;
;; For a composite $f(A) = \text{tr}(A^{-1})$:
;;
;; $\frac{\partial f}{\partial A} = -(A^{-T})^2$

(let [A (la/matrix [[2 1] [1 3]])
      tape-result (tape/with-tape (la/trace (la/invert A)))
      grads (grad/grad tape-result (:result tape-result))
      grad-A (.get grads A)
      inv-t (la/transpose (la/invert A))
      expected (la/scale (la/mmul inv-t inv-t) -1.0)]
  (la/close? grad-A expected))

(kind/test-last [true?])

;; ### Frobenius norm
;;
;; $\frac{\partial}{\partial A} \|A\|_F = \frac{A}{\|A\|_F}$

(let [A (la/matrix [[3 0] [0 4]])
      tape-result (tape/with-tape (la/norm A))
      grads (grad/grad tape-result (:result tape-result))
      grad-A (.get grads A)
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
;; - `la/det` — matrix determinant
;; - `la/invert` — matrix inverse
;; - `la/norm` — Frobenius norm
;;
;; Operations without VJP rules (like `la/svd`, `la/eigen`)
;; are ignored during the backward pass. Their inputs will not receive
;; gradients.
