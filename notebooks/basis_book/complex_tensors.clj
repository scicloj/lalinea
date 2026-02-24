;; # Complex Tensors
;;
;; **ComplexTensor** wraps a dtype-next tensor whose last dimension
;; is 2 — interleaved real/imaginary pairs. The `re` and `im`
;; functions always slice the last axis, returning **zero-copy**
;; tensor views.
;;
;; | Underlying shape | Complex interpretation | `re` / `im` returns |
;; |:-----------------|:----------------------|:---------------------|
;; | `[2]` | scalar complex number | double |
;; | `[n 2]` | complex vector, length n | `[n]` tensor view |
;; | `[r c 2]` | complex r × c matrix | `[r c]` tensor view |
;;
;; The interleaved layout matches EJML's `ZMatrixRMaj`, enabling
;; zero-copy bridging to complex linear algebra.

(ns basis-book.complex-tensors
  (:require
   [scicloj.basis.linalg :as la]
   [scicloj.basis.impl.complex :as cx]
   [tech.v3.tensor :as tensor]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [scicloj.kindly.v4.kind :as kind]))

;; ## Construction

;; ### From separate real and imaginary parts

(cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [v] (= [3] (cx/complex-shape v)))])

(let [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (vec (cx/re ct))
   :im (vec (cx/im ct))})

(kind/test-last [(fn [v] (and (= (:re v) [1.0 2.0 3.0])
                              (= (:im v) [4.0 5.0 6.0])))])

;; ### Wrapping an existing tensor (zero-copy)

(cx/complex-tensor (tensor/->tensor [[1.0 2.0] [3.0 4.0]]))

(kind/test-last [(fn [v] (and (= [2] (cx/complex-shape v))
                              (= [1.0 3.0] (vec (cx/re v)))))])

;; ### Real-only construction

(cx/complex-tensor-real [5.0 6.0 7.0])

(kind/test-last [(fn [v] (= [0.0 0.0 0.0] (vec (cx/im v))))])

;; ### Scalar complex numbers

(cx/complex 3.0 4.0)

(kind/test-last [(fn [v] (cx/scalar? v))])

[(cx/re (cx/complex 3.0 4.0)) (cx/im (cx/complex 3.0 4.0))]

(kind/test-last [= [3.0 4.0]])

;; ### Matrix construction

(cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                   [[5.0 6.0] [7.0 8.0]])

(kind/test-last [(fn [v] (= [2 2] (cx/complex-shape v)))])

;; ## Element access
;;
;; ComplexTensors implement `Counted`, `Indexed`, `IFn`, and `Seqable`.

;; Indexing a vector returns a scalar:

(let [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(cx/re (ct 0)) (cx/im (ct 0))])

(kind/test-last [= [1.0 4.0]])

;; Indexing a matrix returns a vector (one row):

(let [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                            [[5.0 6.0] [7.0 8.0]])]
  (vec (cx/re (ct 0))))

(kind/test-last [= [1.0 2.0]])

;; Nested access reaches scalars:

(let [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                            [[5.0 6.0] [7.0 8.0]])]
  [(cx/re ((ct 1) 1)) (cx/im ((ct 1) 1))])

(kind/test-last [= [4.0 8.0]])

;; ## Complex arithmetic

;; ### Pointwise multiply
;;
;; $(a+bi)(c+di) = (ac - bd) + (ad + bc)i$

(let [a (cx/complex-tensor [1.0 2.0] [3.0 4.0])
      b (cx/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (vec (cx/re (cx/mul a b)))
   :im (vec (cx/im (cx/mul a b)))})

;; $(1+3i)(5+7i) = -16 + 22i$, $(2+4i)(6+8i) = -20 + 40i$

(kind/test-last [(fn [v] (and (= (:re v) [-16.0 -20.0])
                              (= (:im v) [22.0 40.0])))])

;; ### Conjugate

(let [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (vec (cx/re ct))
   :im (vec (cx/im ct))})

(kind/test-last [(fn [v] (= (:im v) [-3.0 4.0]))])

;; ### Magnitude

(let [m (cx/abs (cx/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))])

;; $|3+4i| = 5$, $|0+i| = 1$

(kind/test-last [(fn [v] (and (< (Math/abs (- (first v) 5.0)) 1e-10)
                              (< (Math/abs (- (second v) 1.0)) 1e-10)))])

;; ## Hermitian inner product
;;
;; $\langle a, b \rangle_H = \sum_i a_i \cdot \overline{b_i}$

(let [a (cx/complex-tensor [3.0 1.0] [4.0 2.0])
      [re-aa im-aa] (cx/dot-conj a a)]
  {:norm-sq re-aa :im-part im-aa})

;; $|3+4i|^2 + |1+2i|^2 = 25 + 5 = 30$

(kind/test-last [(fn [v] (and (< (Math/abs (- (:norm-sq v) 30.0)) 1e-10)
                              (< (Math/abs (:im-part v)) 1e-10)))])

;; ## Complex matrix operations via EJML
;;
;; ComplexTensors plug into `la/mmul`, `la/transpose`, `la/det`,
;; `la/trace`, `la/invert` — all backed by EJML's `ZMatrixRMaj`.

;; Matrix multiply:

(let [A (cx/complex-tensor [[1.0 0.0] [0.0 1.0]]
                           [[0.0 0.0] [0.0 0.0]])
      B (cx/complex-tensor [[0.0 1.0] [1.0 0.0]]
                           [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A B))

(kind/test-last [(fn [ct] (= [2 2] (cx/complex-shape ct)))])

;; Conjugate transpose (Hermitian adjoint):

(let [A (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                           [[5.0 6.0] [7.0 8.0]])]
  (la/transpose A))

(kind/test-last [(fn [ct] (let [r (cx/re ct)]
                            (= 3.0 (tensor/mget r 0 1))))])

;; Determinant:

(la/det (cx/complex-tensor [[1.0 3.0] [5.0 7.0]]
                           [[2.0 4.0] [6.0 8.0]]))

;; $\det(A) = (1+2i)(7+8i) - (3+4i)(5+6i) = -16i$

(kind/test-last [(fn [d] (and (< (Math/abs (cx/re d)) 1e-10)
                              (< (Math/abs (- (cx/im d) -16.0)) 1e-10)))])

;; Inverse:

(let [A (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                           [[0.5 1.0] [1.5 2.5]])
      Ainv (la/invert A)
      product (la/mmul A Ainv)
      re-part (cx/re product)
      im-part (cx/im product)]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- (dtype/->double-array re-part)
                                          (double-array [1 0 0 1])))) 1e-10)
       (< (dfn/reduce-max (dfn/abs (dtype/->double-array im-part))) 1e-10)))

(kind/test-last [true?])

;; ## Algebraic identities

(def a (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0]))
(def b (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0]))

(defn approx=
  "Check that two ComplexTensors are approximately equal."
  [x y tol]
  (let [re-diff (dfn/- (cx/re x) (cx/re y))
        im-diff (dfn/- (cx/im x) (cx/im y))]
    (and (< (dfn/reduce-max (dfn/abs re-diff)) tol)
         (< (dfn/reduce-max (dfn/abs im-diff)) tol))))

;; Commutativity: $a \cdot b = b \cdot a$

(approx= (cx/mul a b) (cx/mul b a) 1e-10)

(kind/test-last [true?])

;; Conjugate is an involution: $\overline{\overline{a}} = a$

(approx= (cx/conj (cx/conj a)) a 1e-10)

(kind/test-last [true?])

;; Conjugate distributes: $\overline{a \cdot b} = \bar{a} \cdot \bar{b}$

(approx= (cx/conj (cx/mul a b))
         (cx/mul (cx/conj a) (cx/conj b))
         1e-10)

(kind/test-last [true?])

;; Magnitude is multiplicative: $|a \cdot b| = |a| \cdot |b|$

(let [lhs (cx/abs (cx/mul a b))
      rhs (dfn/* (cx/abs a) (cx/abs b))]
  (< (dfn/reduce-max (dfn/abs (dfn/- lhs rhs))) 1e-10))

(kind/test-last [true?])

;; Cauchy-Schwarz: $|\langle a, b \rangle_H|^2 \leq \langle a, a \rangle_H \cdot \langle b, b \rangle_H$

(let [[re-ab im-ab] (cx/dot-conj a b)
      [re-aa _] (cx/dot-conj a a)
      [re-bb _] (cx/dot-conj b b)]
  (<= (- (+ (* re-ab re-ab) (* im-ab im-ab)) 1e-10)
      (* re-aa re-bb)))

(kind/test-last [true?])
