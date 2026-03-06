;; # API Reference
;;
;; Complete reference for the `scicloj.lalinea` public API:
;;
;; - `scicloj.lalinea.tensor` — tensor construction, structural operations, EJML interop
;; - `scicloj.lalinea.linalg` — products, decompositions, solve
;; - `scicloj.lalinea.elementwise` — tape-aware element-wise functions
;; - `scicloj.lalinea.transform` — 1-D and 2-D FFT, DCT, DST, DHT
;; - `scicloj.lalinea.tape` — computation tape and memory inspection
;; - `scicloj.lalinea.grad` — reverse-mode automatic differentiation
;; - `scicloj.lalinea.vis` — visualization helpers

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns lalinea-book.api-reference
  (:require
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.transform :as ft]
   [scicloj.lalinea.tape :as tape]
   [scicloj.lalinea.elementwise :as el]
   [scicloj.lalinea.grad :as grad]
   [scicloj.lalinea.vis :as vis]
   [tech.v3.libs.buffered-image :as bufimg]
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## `scicloj.lalinea.tensor`

(kind/doc #'t/matrix)

(t/matrix [[1 2] [3 4]])

(kind/test-last [(fn [m] (= [2 2] (t/shape m)))])

(kind/doc #'t/eye)

(t/eye 3)

(kind/test-last [(fn [m] (and (= [3 3] (t/shape m))
                              (== 1.0 (m 0 0))
                              (== 0.0 (m 0 1))))])

(kind/doc #'t/zeros)

(t/zeros 2 3)

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

(kind/doc #'t/ones)

(t/ones 2 3)

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

(kind/doc #'t/diag)

(t/diag [3 5 7])

(kind/test-last [(fn [m] (and (= [3 3] (t/shape m))
                              (== 5.0 (m 1 1))
                              (== 0.0 (m 0 1))))])

;; Extract diagonal from a 2D matrix:

(t/diag (t/matrix [[1 2 3] [4 5 6] [7 8 9]]))

(kind/test-last [(fn [v] (= [1.0 5.0 9.0] v))])

(kind/doc #'t/column)

(t/column [1 2 3])

(kind/test-last [(fn [v] (= [3 1] (t/shape v)))])

(kind/doc #'t/row)

(t/row [1 2 3])

(kind/test-last [(fn [v] (= [1 3] (t/shape v)))])

(kind/doc #'t/compute-matrix)

(t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0)))

(kind/test-last [(fn [m] (= (t/eye 3) m))])

(kind/doc #'t/compute-tensor)

(t/compute-tensor [2 3] (fn [i j] (+ (* 10.0 i) j)) :float64)

(kind/test-last [(fn [m] (and (= [2 3] (t/shape m))
                              (== 12.0 (m 1 2))))])

(kind/doc #'t/complex-tensor)

(t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [ct] (= [3] (t/complex-shape ct)))])

(kind/doc #'t/complex-tensor-real)

(t/complex-tensor-real [5.0 6.0 7.0])

(kind/test-last [(fn [ct] (every? zero? (el/im ct)))])

(kind/doc #'t/complex)

(t/complex 3.0 4.0)

(kind/test-last [(fn [ct] (and (t/scalar? ct)
                               (== 3.0 (el/re ct))
                               (== 4.0 (el/im ct))))])

(kind/doc #'t/wrap-tensor)

(let [raw (t/matrix [[1.0 2.0] [3.0 4.0]])
      ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)])

(kind/test-last [(fn [[c? shape]] (and c? (= [2] shape)))])

(kind/doc #'t/real-tensor?)

(t/real-tensor? (t/matrix [[1 2] [3 4]]))

(kind/test-last [true?])

(t/real-tensor? [1 2 3])

(kind/test-last [false?])

(kind/doc #'t/complex?)

(t/complex? (t/complex 3.0 4.0))

(kind/test-last [true?])

(t/complex? (t/eye 2))

(kind/test-last [false?])

(kind/doc #'t/scalar?)

(t/scalar? (t/complex 3.0 4.0))

(kind/test-last [true?])

(kind/doc #'t/shape)

(t/shape (t/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [s] (= [2 3] s))])

;; For ComplexTensors, returns the logical shape
;; (without the trailing interleaved dimension):

(t/shape (t/complex-tensor [1.0 2.0] [3.0 4.0]))

(kind/test-last [= [2]])

(kind/doc #'t/complex-shape)

(t/complex-shape (t/complex-tensor [[1.0 2.0] [3.0 4.0]]
                                   [[5.0 6.0] [7.0 8.0]]))

(kind/test-last [= [2 2]])

(kind/doc #'t/reshape)

(t/reshape (t/matrix [[1 2] [3 4]]) [4])

(kind/test-last [(fn [v] (= [1.0 2.0 3.0 4.0] v))])

(kind/doc #'t/select)

;; Row 0 of a matrix:
(t/select (t/matrix [[1 2] [3 4] [5 6]]) 0 :all)

(kind/test-last [(fn [v] (= [1.0 2.0] v))])

(kind/doc #'t/submatrix)

(t/submatrix (t/eye 4) :all (range 2))

(kind/test-last [(fn [m] (= [4 2] (t/shape m)))])

(kind/doc #'t/flatten)

(t/flatten (t/column [1 2 3]))

(kind/test-last [(fn [v] (= [1.0 2.0 3.0] v))])

(kind/doc #'t/hstack)

(t/hstack [(t/column [1 2]) (t/column [3 4])])

(kind/test-last [(fn [m] (= [[1.0 3.0] [2.0 4.0]] m))])

(kind/doc #'t/reduce-axis)

;; Row sums (axis 1) and column sums (axis 0):
(t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) el/sum 1)

(kind/test-last [(fn [v] (and (= [2] (t/shape v))
                              (la/close-scalar? (v 0) 6.0)
                              (la/close-scalar? (v 1) 15.0)))])

(kind/doc #'t/clone)

;; Always allocates a fresh copy:
(let [m (t/matrix [[1 2] [3 4]])]
  (identical? m (t/clone m)))

(kind/test-last [false?])

(kind/doc #'t/concrete?)

;; Concrete (backed by array):
(t/concrete? (t/matrix [[1 2] [3 4]]))

(kind/test-last [true?])

;; Lazy (reader chain from `el/+`):
(t/concrete? (el/+ (t/matrix [[1 2] [3 4]])
                   (t/matrix [[10 20] [30 40]])))

(kind/test-last [false?])

(kind/doc #'t/materialize)

;; No-op on concrete:
(let [m (t/matrix [[1 2] [3 4]])]
  (identical? m (t/materialize m)))

(kind/test-last [true?])

;; Materializes lazy results:
(t/materialize (el/+ (t/matrix [[1 2] [3 4]])
                     (t/matrix [[10 20] [30 40]])))

(kind/test-last [(fn [m] (= [[11.0 22.0] [33.0 44.0]] m))])

(kind/doc #'t/->tensor)

(t/->tensor (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [t] (not (t/real-tensor? t)))])

;; For ComplexTensors, returns the underlying `[... 2]` tensor:

(t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0])))

(kind/test-last [= [2 2]])

(kind/doc #'t/->real-tensor)

(t/->real-tensor (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [rt] (t/real-tensor? rt))])

(kind/doc #'t/->double-array)

(let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))]
  (alength arr))

(kind/test-last [(fn [n] (= 4 n))])

;; For ComplexTensors, returns the interleaved `[re im ...]` array:

(let [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct)))

(kind/test-last [= [1.0 3.0 2.0 4.0]])

(kind/doc #'t/backing-array)

(let [A (t/matrix [[1 2] [3 4]])
      B (t/clone A)]
  [(some? (t/backing-array A))
   (identical? (t/backing-array A)
               (t/backing-array B))])

(kind/test-last [= [true false]])

(kind/doc #'t/->reader)

(let [rdr (t/->reader (t/column [10 20 30]))]
  (rdr 2))

(kind/test-last [(fn [v] (== 30.0 v))])

(kind/doc #'t/array-buffer)

(some? (t/array-buffer (t/clone (t/eye 3))))

(kind/test-last [true?])

(kind/doc #'t/make-reader)

(t/make-reader :float64 5 (* idx idx))

(kind/test-last [(fn [r] (= 16.0 (r 4)))])

(kind/doc #'t/make-container)

(t/make-container :float64 4)

(kind/test-last [(fn [c] (= 4 (count c)))])

(kind/doc #'t/elemwise-cast)

(t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32)

(kind/test-last [(fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))])

(kind/doc #'t/mset!)

(let [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0))

(kind/test-last [(fn [v] (== 99.0 v))])

(kind/doc #'t/set-value!)

(let [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1))

(kind/test-last [(fn [v] (== 42.0 v))])

(kind/doc #'t/tensor->dmat)

(let [t (t/matrix [[1 2] [3 4]])
      dm (t/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm)))

(kind/test-last [true?])

(kind/doc #'t/dmat->tensor)

(let [dm (t/tensor->dmat (t/eye 2))
      t (t/dmat->tensor dm)]
  (= [2 2] (t/shape t)))

(kind/test-last [true?])

(kind/doc #'t/complex-tensor->zmat)

(let [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])
      zm (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm)))

(kind/test-last [true?])

(kind/doc #'t/zmat->complex-tensor)

(let [zm (t/complex-tensor->zmat (t/complex-tensor [1.0 2.0] [3.0 4.0]))
      ct (t/zmat->complex-tensor zm)]
  (t/complex? ct))

(kind/test-last [true?])

;; ## `scicloj.lalinea.linalg`
;;
;; Most `la/` functions are polymorphic — they accept both real tensors
;; and ComplexTensors.

(kind/doc #'la/mmul)

(la/mmul (t/matrix [[1 2] [3 4]])
         (t/column [5 6]))

(kind/test-last [(fn [m] (and (= [2 1] (t/shape m))
                              (== 17.0 (m 0 0))))])

(kind/doc #'la/dot)

(la/dot (t/column [1 2 3]) (t/column [4 5 6]))

(kind/test-last [(fn [v] (== 32.0 v))])

(kind/doc #'la/dot-conj)

;; Hermitian inner product: $\langle a, a \rangle = \|a\|^2$.

(let [a (t/complex-tensor [3.0 1.0] [4.0 2.0])
      result (la/dot-conj a a)]
  (la/close-scalar? (el/re result) 30.0))

(kind/test-last [true?])

(kind/doc #'la/mpow)

(la/mpow (t/matrix [[1 1] [0 1]]) 5)

(kind/test-last [(fn [m] (la/close? m (t/matrix [[1 5] [0 1]])))])

(kind/doc #'la/transpose)

(la/transpose (t/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [m] (= [3 2] (t/shape m)))])

(kind/doc #'la/trace)

(la/trace (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (== 5.0 v))])

(kind/doc #'la/det)

(la/det (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (la/close-scalar? v -2.0))])

(kind/doc #'la/norm)

(la/norm (t/matrix [[3 0] [0 4]]))

(kind/test-last [(fn [v] (la/close-scalar? v 5.0))])

(kind/doc #'la/rank)

(la/rank (t/matrix [[1 2] [2 4]]))

(kind/test-last [(fn [r] (= 1 r))])

(kind/doc #'la/condition-number)

(la/condition-number (t/matrix [[2 1] [1 3]]))

(kind/test-last [(fn [v] (> v 1.0))])

(kind/doc #'la/solve)

;; Solve $Ax = b$:
(let [A (t/matrix [[2 1] [1 3]])
      b (t/column [5 7])]
  (la/solve A b))

(kind/test-last [(fn [x] (and (la/close-scalar? (x 0 0) 1.6)
                              (la/close-scalar? (x 1 0) 1.8)))])

(kind/doc #'la/invert)

(let [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2)))

(kind/test-last [true?])

(kind/doc #'la/lstsq)

(let [{:keys [x rank]} (la/lstsq (t/matrix [[1 1] [1 2] [1 3]])
                                 (t/column [1 2 3]))]
  {:rank rank :close? (la/close? x (t/column [0 1]))})

(kind/test-last [(fn [m] (and (= 2 (:rank m)) (:close? m)))])

(kind/doc #'la/pinv)

(let [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2)))

(kind/test-last [true?])

(kind/doc #'la/eigen)

(let [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))])

(kind/test-last [(fn [[n-evecs ev-shape]]
                   (and (= 2 n-evecs)
                        (= [2] ev-shape)))])

(kind/doc #'la/real-eigenvalues)

(la/real-eigenvalues (t/matrix [[2 1] [1 2]]))

(kind/test-last [(fn [evs] (and (la/close-scalar? (evs 0) 1.0)
                                (la/close-scalar? (evs 1) 3.0)))])

(kind/doc #'la/svd)

(let [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U)
   (count S)
   (t/shape Vt)])

(kind/test-last [(fn [[u-shape n-s vt-shape]]
                   (and (= [3 3] u-shape)
                        (= 2 n-s)
                        (= [2 2] vt-shape)))])

(kind/doc #'la/qr)

(let [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]])))

(kind/test-last [true?])

(kind/doc #'la/cholesky)

(let [A (t/matrix [[4 2] [2 3]])
      L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A))

(kind/test-last [true?])

(kind/doc #'la/null-space)

(let [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns)
             (t/zeros 2 1)))

(kind/test-last [true?])

(kind/doc #'la/col-space)

(second (t/shape (la/col-space (t/matrix [[1 2] [2 4]]))))

(kind/test-last [(fn [r] (= 1 r))])

(kind/doc #'la/close?)

(la/close? (t/eye 2) (t/eye 2))

(kind/test-last [true?])

(la/close? (t/eye 2) (t/zeros 2 2))

(kind/test-last [false?])

(kind/doc #'la/close-scalar?)

(la/close-scalar? 1.00000000001 1.0)

(kind/test-last [true?])

(kind/doc #'la/lift)

;; One-shot bridge: unwrap, apply, re-wrap. Pass a Var for tape recording.
(la/lift el/sqrt (t/matrix [[4 9] [16 25]]))

(kind/test-last [(fn [m] (and (la/close-scalar? (m 0 0) 2.0)
                              (la/close-scalar? (m 0 1) 3.0)))])

(kind/doc #'la/lifted)

;; Curried version — returns a reusable function.
(let [my-sqrt (la/lifted el/sqrt)]
  (my-sqrt (t/column [4 9 16])))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 2.0))])

;; ## `scicloj.lalinea.elementwise`
;;
;; The canonical namespace for element-wise operations. All functions
;; are tape-aware and dispatch on `t/complex?` for complex inputs.
;; Naming follows dtype-next's `dfn/` conventions (`+`, `-`, `*`, `/`).

(kind/doc #'el/+)

(el/+ (t/column [1 2 3]) (t/column [10 20 30]))

(kind/test-last [(fn [v] (== 11.0 (v 0 0)))])

;; Complex addition:

(let [a (t/complex-tensor [1.0 2.0] [3.0 4.0])
      b (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (el/re (el/+ a b)))

(kind/test-last [= [11.0 22.0]])

(kind/doc #'el/-)

(el/- (t/column [10 20 30]) (t/column [1 2 3]))

(kind/test-last [(fn [v] (== 9.0 (v 0 0)))])

(kind/doc #'el/scale)

(el/scale (t/column [2 3 4]) 5.0)

(kind/test-last [(fn [v] (== 10.0 (v 0 0)))])

(kind/doc #'el/*)

(el/* (t/column [2 3 4]) (t/column [10 20 30]))

(kind/test-last [(fn [v] (== 20.0 (v 0 0)))])

;; Complex multiplication: $(1+3i)(2+4i) = -10 + 10i$

(let [a (t/complex-tensor [1.0] [3.0])
      b (t/complex-tensor [2.0] [4.0])
      c (el/* a b)]
  [(el/re (c 0)) (el/im (c 0))])

(kind/test-last [= [-10.0 10.0]])

(kind/doc #'el//)

(el// (t/column [10 20 30]) (t/column [2 4 5]))

(kind/test-last [(fn [v] (= [5.0 5.0 6.0] (t/flatten v)))])

;; Complex division:

(el// (t/complex 3.0 4.0) (t/complex 1.0 2.0))

(kind/test-last [(fn [v] (and (< (abs (- (el/re v) 2.2)) 1e-10)
                              (< (abs (- (el/im v) -0.4)) 1e-10)))])

(kind/doc #'el/re)

(el/re (t/complex-tensor [1.0 2.0] [3.0 4.0]))

(kind/test-last [= [1.0 2.0]])

(kind/doc #'el/im)

(el/im (t/complex-tensor [1.0 2.0] [3.0 4.0]))

(kind/test-last [= [3.0 4.0]])

(kind/doc #'el/conj)

(let [z (t/complex 3.0 4.0)]
  (el/im (el/conj z)))

(kind/test-last [(fn [v] (la/close-scalar? v -4.0))])

(kind/doc #'el/sq)

(el/sq (t/column [2 3 4]))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 4.0))])

(kind/doc #'el/sqrt)

(el/sqrt (t/column [4 9 16]))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 2.0))])

(kind/doc #'el/pow)

((el/pow (t/column [2]) 3) 0 0)

(kind/test-last [(fn [v] (== 8.0 v))])

(kind/doc #'el/cbrt)

(la/close-scalar? ((el/cbrt (t/column [27])) 0 0) 3.0)

(kind/test-last [true?])

(kind/doc #'el/exp)

(la/close-scalar? ((el/exp (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/log)

(la/close-scalar? ((el/log (t/column [math/E])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/log10)

(la/close-scalar? ((el/log10 (t/column [100])) 0 0) 2.0)

(kind/test-last [true?])

(kind/doc #'el/log1p)

((el/log1p (t/column [0.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v 0.0))])

(kind/doc #'el/expm1)

((el/expm1 (t/column [0.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v 0.0))])

(kind/doc #'el/sin)

(la/close-scalar? ((el/sin (t/column [(/ math/PI 2)])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/cos)

(la/close-scalar? ((el/cos (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/tan)

(la/close-scalar? ((el/tan (t/column [(/ math/PI 4)])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/asin)

((el/asin (t/column [0.5])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/asin 0.5)))])

(kind/doc #'el/acos)

((el/acos (t/column [0.5])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/acos 0.5)))])

(kind/doc #'el/atan)

((el/atan (t/column [1.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/atan 1.0)))])

(kind/doc #'el/sinh)

(la/close-scalar? ((el/sinh (t/column [0])) 0 0) 0.0)

(kind/test-last [true?])

(kind/doc #'el/cosh)

(la/close-scalar? ((el/cosh (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'el/tanh)

(la/close-scalar? ((el/tanh (t/column [0])) 0 0) 0.0)

(kind/test-last [true?])

(kind/doc #'el/floor)

((el/floor (t/column [2.7])) 0 0)

(kind/test-last [(fn [v] (== 2.0 v))])

(kind/doc #'el/ceil)

((el/ceil (t/column [2.3])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'el/round)

((el/round (t/column [2.7])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'el/clip)

(t/flatten (el/clip (t/column [-2 0.5 3]) -1 1))

(kind/test-last [(fn [v] (= [-1.0 0.5 1.0] v))])

(kind/doc #'el/abs)

((el/abs (t/column [-5])) 0 0)

(kind/test-last [(fn [v] (== 5.0 v))])

;; Complex magnitude: $|3+4i| = 5$

(let [m (el/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0))

(kind/test-last [true?])

(kind/doc #'el/sum)

(el/sum (t/column [1 2 3 4]))

(kind/test-last [(fn [v] (== 10.0 v))])

;; Complex sum:

(let [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])
      s (el/sum ct)]
  [(el/re s) (el/im s)])

(kind/test-last [= [6.0 15.0]])

(kind/doc #'el/mean)

(el/mean (t/column [2 4 6]))

(kind/test-last [(fn [v] (== 4.0 v))])

(kind/doc #'el/reduce-*)

(el/reduce-* (t/column [2 3 4]))

(kind/test-last [(fn [v] (== 24.0 v))])

(kind/doc #'el/reduce-max)

(el/reduce-max (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (== 9.0 v))])

(kind/doc #'el/reduce-min)

(el/reduce-min (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (== 1.0 v))])

(kind/doc #'el/>)

(el/> (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [0.0 1.0 0.0] (t/flatten v)))])

(kind/doc #'el/<)

(el/< (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [1.0 0.0 0.0] (t/flatten v)))])

(kind/doc #'el/>=)

(el/>= (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [0.0 1.0 1.0] (t/flatten v)))])

(kind/doc #'el/<=)

(el/<= (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [1.0 0.0 1.0] (t/flatten v)))])

(kind/doc #'el/eq)

(el/eq (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [0.0 0.0 1.0] (t/flatten v)))])

(kind/doc #'el/not-eq)

(el/not-eq (t/column [1 5 3]) (t/column [2 4 3]))

(kind/test-last [(fn [v] (= [1.0 1.0 0.0] (t/flatten v)))])

(kind/doc #'el/min)

((el/min (t/column [3]) (t/column [5])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'el/max)

((el/max (t/column [3]) (t/column [5])) 0 0)

(kind/test-last [(fn [v] (== 5.0 v))])

(kind/doc #'el/argmax)

(el/argmax (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (== 3 v))])

(kind/doc #'el/argmin)

(el/argmin (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (== 4 v))])

(kind/doc #'el/argsort)

;; Ascending:

(el/argsort (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (= [4 2 0 1 3] v))])

;; Descending:

(el/argsort > (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (= [3 1 0 2 4] v))])

(kind/doc #'el/sort)

;; Ascending:

(el/sort (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (= [1.0 2.0 3.0 7.0 9.0] (t/flatten v)))])

;; Descending:

(el/sort > (t/column [3 7 2 9 1]))

(kind/test-last [(fn [v] (= [9.0 7.0 3.0 2.0 1.0] (t/flatten v)))])


;; ## `scicloj.lalinea.transform`
;;
;; FFT and real-valued transforms backed by
;; [JTransforms](https://github.com/wendykierp/JTransforms).
;; 1-D and 2-D FFT return ComplexTensor spectra; DCT, DST, and
;; DHT return real tensors.

(kind/doc #'ft/forward)

(let [signal [1.0 0.0 0.0 0.0]
      spectrum (ft/forward signal)]
  (t/complex-shape spectrum))

(kind/test-last [= [4]])

(kind/doc #'ft/inverse)

(let [spectrum (ft/forward [1.0 2.0 3.0 4.0])
      roundtrip (ft/inverse spectrum)]
  (la/close-scalar? (el/re (roundtrip 0)) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/inverse-real)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/forward-complex)

(let [ct (t/complex-tensor-real [1.0 0.0 0.0 0.0])
      spectrum (ft/forward-complex ct)]
  (t/complex-shape spectrum))

(kind/test-last [= [4]])

(kind/doc #'ft/forward-2d)

(let [A (t/matrix [[1 2] [3 4]])
      spectrum (ft/forward-2d A)]
  (t/complex-shape spectrum))

(kind/test-last [= [2 2]])

(kind/doc #'ft/inverse-2d)

(let [A (t/matrix [[1 2] [3 4]])
      roundtrip (ft/inverse-2d (ft/forward-2d A))]
  (la/close-scalar? (el/re ((roundtrip 0) 0)) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/inverse-real-2d)

(let [A (t/matrix [[1 2] [3 4]])
      roundtrip (ft/inverse-real-2d (ft/forward-2d A))]
  (la/close? roundtrip A))

(kind/test-last [true?])

(kind/doc #'ft/forward-complex-2d)

(let [ct (t/complex-tensor-real [[1 2] [3 4]])
      spectrum (ft/forward-complex-2d ct)]
  (t/complex-shape spectrum))

(kind/test-last [= [2 2]])

(kind/doc #'ft/dct-forward)

(ft/dct-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'ft/dct-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/dst-forward)

(ft/dst-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'ft/dst-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/dht-forward)

(ft/dht-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'ft/dht-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

;; ## `scicloj.lalinea.tape`
;;
;; Computation DAG recording and memory inspection.

(kind/doc #'tape/memory-status)

(tape/memory-status (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [s] (= :contiguous s))])

(tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]])))

(kind/test-last [(fn [s] (= :strided s))])

(tape/memory-status (el/+ (t/eye 2) (t/eye 2)))

(kind/test-last [(fn [s] (= :lazy s))])

(kind/doc #'tape/memory-relation)

(let [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A)))

(kind/test-last [(fn [r] (= :shared r))])

(tape/memory-relation (t/matrix [[1 0] [0 1]]) (t/matrix [[5 6] [7 8]]))

(kind/test-last [(fn [r] (= :independent r))])

(tape/memory-relation (t/matrix [[1 2] [3 4]]) (el/+ (t/eye 2) (t/eye 2)))

(kind/test-last [(fn [r] (= :unknown-lazy r))])

(kind/doc #'tape/with-tape)

(def tape-example
  (tape/with-tape
    (let [A (t/matrix [[1 2] [3 4]])
          B (el/scale A 2.0)]
      (la/mmul B (la/transpose A)))))

(select-keys tape-example [:result :entries])

(kind/test-last [(fn [m] (and (contains? m :result)
                              (contains? m :entries)))])

(kind/doc #'tape/summary)

(tape/summary tape-example)

(kind/test-last [(fn [s] (= 4 (:total s)))])

(kind/doc #'tape/origin)

(tape/origin tape-example (:result tape-example))

(kind/test-last [(fn [dag] (= :la/mmul (:op dag)))])

(kind/doc #'tape/mermaid)

;; Returns a renderable Mermaid diagram.

(tape/mermaid tape-example (:result tape-example))

(kind/doc #'tape/detect-memory-status)

;; Classifies a tape entry's output relative to its inputs:
;; `:reads-through`, `:shared`, or `:independent`.

(mapv tape/detect-memory-status (:entries tape-example))

(kind/test-last [(fn [v] (every? #{:reads-through :shared :independent} v))])

;; ## `scicloj.lalinea.grad`
;;
;; Reverse-mode automatic differentiation on the computation tape.

(kind/doc #'grad/grad)

(let [A (t/matrix [[1 2] [3 4]])
      tape-result (tape/with-tape
                    (la/trace (la/mmul (la/transpose A) A)))]
  (la/close? (grad/grad tape-result
                        (:result tape-result) A)
             (el/scale A 2)))

(kind/test-last [true?])

;; ## `scicloj.lalinea.vis`
;;
;; SVG and image helpers for visual linear algebra.

(kind/doc #'vis/arrow-plot)

(vis/arrow-plot [{:xy [2 1] :color "#2266cc" :label "u"}
                 {:xy [-1 1.5] :color "#cc4422" :label "v"}]
                {:width 600})

(kind/doc #'vis/graph-plot)

(vis/graph-plot [[0 0] [1 0] [0.5 0.87]]
                [[0 1] [1 2] [2 0]]
                {:width 600 :labels ["A" "B" "C"]})

(kind/doc #'vis/matrix->gray-image)

(let [m (t/compute-tensor [200 200]
                          (fn [r c] (* 255.0 (/ (+ r c) 400.0)))
                          :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])

(kind/doc #'vis/extract-channel)

(let [img (t/compute-tensor [200 200 3]
                            (fn [r c ch]
                              (case (int ch) 0 (int (* 255 (/ r 200.0))) 1 128 2 64))
                            :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])
