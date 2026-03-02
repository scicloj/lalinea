;; # Complex Tensors
;;
;; **ComplexTensor** wraps a dtype-next tensor whose last dimension
;; is 2 — interleaved real/imaginary pairs. The `re` and `im`
;; functions always slice the last axis, returning **zero-copy**
;; tensor views.
;;
;; | Underlying shape | Complex interpretation | `re` / `im` returns |
;; |:-----------------|:----------------------|:---------------------|
;; | `[2]` | scalar complex number | `double` |
;; | `[n 2]` | complex vector, length n | `[n]` tensor view |
;; | `[r c 2]` | complex r * c matrix | `[r c]` tensor view |
;;
;; The interleaved layout matches EJML's `ZMatrixRMaj`, enabling
;; zero-copy bridging to complex linear algebra.

(ns lalinea-book.complex-tensors
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.lalinea.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Construction

;; ### From separate real and imaginary parts

(cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [v] (= [3] (cx/complex-shape v)))])

(let [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (cx/re ct)
   :im (cx/im ct)})

(kind/test-last [(fn [v] (and (= (:re v) [1.0 2.0 3.0])
                              (= (:im v) [4.0 5.0 6.0])))])

;; ### Wrapping an existing tensor (zero-copy)

(cx/complex-tensor (tensor/->tensor [[1.0 2.0] [3.0 4.0]]))

(kind/test-last [(fn [v] (and (= [2] (cx/complex-shape v))
                              (= [1.0 3.0] (cx/re v))))])

;; ### Real-only construction

(cx/complex-tensor-real [5.0 6.0 7.0])

(kind/test-last [(fn [v] (= [0.0 0.0 0.0] (cx/im v)))])

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
  (cx/re (ct 0)))

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
  {:re (cx/re (la/mul a b))
   :im (cx/im (la/mul a b))})

;; $(1+3i)(5+7i) = -16 + 22i$, $(2+4i)(6+8i) = -20 + 40i$

(kind/test-last [(fn [v] (and (= (:re v) [-16.0 -20.0])
                              (= (:im v) [22.0 40.0])))])

;; Complex numbers live in the plane. Multiplying by $i$
;; rotates 90° counterclockwise:

(let [z-re 3.0 z-im 1.0
      ;; z * i = (3+i)(0+i) = -1+3i
      p-re -1.0 p-im 3.0]
  (-> (tc/dataset {:re [z-re 0.0 p-re]
                   :im [z-im 1.0 p-im]
                   :label ["z = 3+i" "w = i" "z*w = -1+3i"]})
      (plotly/base {:=x :re :=y :im :=color :label})
      (plotly/layer-point {:=mark-size 12})
      plotly/plot))

;; ### Conjugate

(let [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (cx/re ct)
   :im (cx/im ct)})

(kind/test-last [(fn [v] (= (:im v) [-3.0 4.0]))])

;; Conjugation reflects a point across the real axis:

(let [z-re 2.0 z-im 3.0]
  (-> (tc/dataset {:re [z-re z-re]
                   :im [z-im (- z-im)]
                   :label ["z = 2+3i" "conj(z) = 2-3i"]})
      (plotly/base {:=x :re :=y :im :=color :label})
      (plotly/layer-point {:=mark-size 12})
      plotly/plot))
;; ### Magnitude

(let [m (la/abs (cx/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))])

;; $|3+4i| = 5$, $|0+i| = 1$

(kind/test-last [(fn [v] (and (< (abs (- (first v) 5.0)) 1e-10)
                              (< (abs (- (second v) 1.0)) 1e-10)))])

;; ## [Hermitian inner product](https://en.wikipedia.org/wiki/Inner_product_space#Hermitian_inner_product)
;;
;; $\langle a, b \rangle_H = \sum_i a_i \cdot \overline{b_i}$

(let [a (cx/complex-tensor [3.0 1.0] [4.0 2.0])
      d (la/dot a a)]
  {:norm-sq (double (cx/re d)) :im-part (double (cx/im d))})

;; $|3+4i|^2 + |1+2i|^2 = 25 + 5 = 30$

(kind/test-last [(fn [v] (and (< (abs (- (:norm-sq v) 30.0)) 1e-10)
                              (< (abs (:im-part v)) 1e-10)))])

;; ## Complex matrix operations via EJML
;;
;; ComplexTensors plug into `la/mmul`, `la/transpose`, `la/det`,
;; `la/trace`, `la/invert` — all backed by EJML's `ZMatrixRMaj`.

;; Matrix multiply:

(la/mmul (cx/complex-tensor [[1.0 0.0] [0.0 1.0]]
                            [[0.0 0.0] [0.0 0.0]])
         (cx/complex-tensor [[0.0 1.0] [1.0 0.0]]
                            [[0.0 0.0] [0.0 0.0]]))

(kind/test-last [(fn [ct] (and (= [2 2] (cx/complex-shape ct))
                               (= (cx/re ct) [[0.0 1.0] [1.0 0.0]])
                               (= (cx/im ct) [[0.0 0.0] [0.0 0.0]])))])

;; Conjugate transpose (Hermitian adjoint):

(la/transpose (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                                 [[5.0 6.0] [7.0 8.0]]))

;; Re is transposed; Im is negated and transposed:

(kind/test-last [(fn [ct] (and (= (cx/re ct) [[1.0 3.0] [2.0 4.0]])
                               (= (cx/im ct) [[-5.0 -7.0] [-6.0 -8.0]])))])

;; Determinant:

(la/det (cx/complex-tensor [[1.0 3.0] [5.0 7.0]]
                           [[2.0 4.0] [6.0 8.0]]))

;; $\det(A) = (1+2i)(7+8i) - (3+4i)(5+6i) = -16i$

(kind/test-last [(fn [d] (and (< (abs (cx/re d)) 1e-10)
                              (< (abs (- (cx/im d) -16.0)) 1e-10)))])

;; Inverse:

(let [A (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                           [[0.5 1.0] [1.5 2.5]])
      Ainv (la/invert A)
      product (la/mmul A Ainv)
      re-part (cx/re product)
      im-part (cx/im product)]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- re-part (la/eye 2)))) 1e-10)
       (< (dfn/reduce-max (dfn/abs im-part)) 1e-10)))

(kind/test-last [true?])
