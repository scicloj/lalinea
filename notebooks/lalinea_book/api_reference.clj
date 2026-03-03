;; # API Reference
;;
;; Complete reference for the `scicloj.lalinea` public API:
;;
;; - `scicloj.lalinea.linalg` — matrix construction, arithmetic, decompositions
;; - `scicloj.lalinea.complex` — complex tensors
;; - `scicloj.lalinea.transform` — FFT and real-valued transforms
;; - `scicloj.lalinea.tape` — computation tape and memory inspection
;; - `scicloj.lalinea.elementwise` — tape-aware element-wise functions
;; - `scicloj.lalinea.grad` — reverse-mode automatic differentiation
;; - `scicloj.lalinea.vis` — visualization helpers

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns lalinea-book.api-reference
  (:require
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.complex :as cx]
   [scicloj.lalinea.transform :as ft]
   [scicloj.lalinea.tape :as tape]
   [scicloj.lalinea.elementwise :as elem]
   [scicloj.lalinea.grad :as grad]
   [scicloj.lalinea.vis :as vis]   [tech.v3.libs.buffered-image :as bufimg]
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

(kind/doc #'t/reduce-axis)

;; Row sums (axis 1) and column sums (axis 0):
(t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) la/sum 1)

(kind/test-last [(fn [v] (and (= [2] (t/shape v))
                              (la/close-scalar? (v 0) 6.0)
                              (la/close-scalar? (v 1) 15.0)))])

(kind/doc #'t/flatten)

(t/flatten (t/column [1 2 3]))

(kind/test-last [(fn [v] (= [1.0 2.0 3.0] v))])

(kind/doc #'t/hstack)

(t/hstack [(t/column [1 2]) (t/column [3 4])])

(kind/test-last [(fn [m] (= [[1.0 3.0] [2.0 4.0]] m))])

(kind/doc #'t/submatrix)

(t/submatrix (t/eye 4) :all (range 2))

(kind/test-last [(fn [m] (= [4 2] (t/shape m)))])

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

(let [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])
      zm (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm)))

(kind/test-last [true?])

(kind/doc #'t/zmat->complex-tensor)

(let [zm (t/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
      ct (t/zmat->complex-tensor zm)]
  (cx/complex? ct))

(kind/test-last [true?])

(kind/doc #'t/ones)

(t/ones 2 3)

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

(kind/doc #'t/real-tensor?)

(t/real-tensor? (t/matrix [[1 2] [3 4]]))

(kind/test-last [true?])

(t/real-tensor? [1 2 3])

(kind/test-last [false?])

(kind/doc #'t/->real-tensor)

(t/->real-tensor (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [rt] (t/real-tensor? rt))])

(kind/doc #'t/->tensor)

(t/->tensor (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [t] (not (t/real-tensor? t)))])

;; ## `scicloj.lalinea.linalg`

(kind/doc #'la/add)

(la/add (t/matrix [[1 2] [3 4]])
        (t/matrix [[10 20] [30 40]]))

(kind/test-last [(fn [m] (== 11.0 (m 0 0)))])

(kind/doc #'la/sub)

(la/sub (t/matrix [[10 20] [30 40]])
        (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (== 9.0 (m 0 0)))])

(kind/doc #'la/scale)

(la/scale (t/matrix [[1 2] [3 4]]) 3.0)

(kind/test-last [(fn [m] (== 6.0 (m 0 1)))])

(kind/doc #'la/mul)

(la/mul (t/matrix [[2 3] [4 5]])
        (t/matrix [[10 20] [30 40]]))

(kind/test-last [(fn [m] (and (== 20.0 (m 0 0))
                              (== 60.0 (m 0 1))))])

(kind/doc #'la/abs)

(la/abs (t/matrix [[-3 2] [-1 4]]))

(kind/test-last [(fn [m] (== 3.0 (m 0 0)))])

(kind/doc #'la/sq)

(la/sq (t/matrix [[2 3] [4 5]]))

(kind/test-last [(fn [m] (== 4.0 (m 0 0)))])

(kind/doc #'la/sum)

(la/sum (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (== 10.0 v))])

(kind/doc #'la/prod)

(la/prod (t/matrix [2 3 4]))

(kind/test-last [(fn [v] (== 24.0 v))])

(kind/doc #'la/mmul)

(la/mmul (t/matrix [[1 2] [3 4]])
         (t/column [5 6]))

(kind/test-last [(fn [m] (and (= [2 1] (t/shape m))
                              (== 17.0 (m 0 0))))])

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

(kind/doc #'la/dot)

(la/dot (t/column [1 2 3]) (t/column [4 5 6]))

(kind/test-last [(fn [v] (== 32.0 v))])

(kind/doc #'la/close?)

(la/close? (t/eye 2) (t/eye 2))

(kind/test-last [true?])

(la/close? (t/eye 2) (t/zeros 2 2))

(kind/test-last [false?])

(kind/doc #'la/close-scalar?)

(la/close-scalar? 1.00000000001 1.0)

(kind/test-last [true?])

(kind/doc #'la/invert)

(let [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2)))

(kind/test-last [true?])

(kind/doc #'la/solve)

;; Solve $Ax = b$:
(let [A (t/matrix [[2 1] [1 3]])
      b (t/column [5 7])]
  (la/solve A b))

(kind/test-last [(fn [x] (and (la/close-scalar? (x 0 0) 1.6)
                              (la/close-scalar? (x 1 0) 1.8)))])

(kind/doc #'la/eigen)

(let [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))])

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

(kind/doc #'la/mpow)

(la/mpow (t/matrix [[1 1] [0 1]]) 5)

(kind/test-last [(fn [m] (la/close? m (t/matrix [[1 5] [0 1]])))])

(kind/doc #'la/rank)

(la/rank (t/matrix [[1 2] [2 4]]))

(kind/test-last [(fn [r] (= 1 r))])

(kind/doc #'la/condition-number)

(la/condition-number (t/matrix [[2 1] [1 3]]))

(kind/test-last [(fn [v] (> v 1.0))])

(kind/doc #'la/pinv)

(let [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2)))

(kind/test-last [true?])

(kind/doc #'la/lstsq)

(let [{:keys [x rank]} (la/lstsq (t/matrix [[1 1] [1 2] [1 3]])
                                 (t/column [1 2 3]))]
  {:rank rank :close? (la/close? x (t/column [0 1]))})

(kind/test-last [(fn [m] (and (= 2 (:rank m)) (:close? m)))])

(kind/doc #'la/null-space)

(let [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns)
             (t/zeros 2 1)))

(kind/test-last [true?])

(kind/doc #'la/col-space)

(second (t/shape (la/col-space (t/matrix [[1 2] [2 4]]))))

(kind/test-last [(fn [r] (= 1 r))])

(kind/doc #'la/lift)

;; One-shot bridge: unwrap, apply, re-wrap. Pass a Var for tape recording.
(la/lift elem/sqrt (t/matrix [[4 9] [16 25]]))

(kind/test-last [(fn [m] (and (la/close-scalar? (m 0 0) 2.0)
                              (la/close-scalar? (m 0 1) 3.0)))])

(kind/doc #'la/lifted)

;; Curried version — returns a reusable function.
(let [my-sqrt (la/lifted elem/sqrt)]
  (my-sqrt (t/column [4 9 16])))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 2.0))])

;; ## `scicloj.lalinea.complex`
;;
;; A ComplexTensor wraps a dtype-next tensor whose last dimension
;; is 2 (interleaved real/imaginary pairs).

(kind/doc #'cx/complex-tensor)

(cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [ct] (= [3] (cx/complex-shape ct)))])

(kind/doc #'cx/complex-tensor-real)

(cx/complex-tensor-real [5.0 6.0 7.0])

(kind/test-last [(fn [ct] (every? zero? (cx/im ct)))])

(kind/doc #'cx/complex)

(cx/complex 3.0 4.0)

(kind/test-last [(fn [ct] (and (cx/scalar? ct)
                               (== 3.0 (cx/re ct))
                               (== 4.0 (cx/im ct))))])

(kind/doc #'cx/re)

(cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))

(kind/test-last [= [1.0 2.0]])

(kind/doc #'cx/im)

(cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))

(kind/test-last [= [3.0 4.0]])

(kind/doc #'cx/complex-shape)

(cx/complex-shape (cx/complex-tensor [[1.0 2.0] [3.0 4.0]]
                                     [[5.0 6.0] [7.0 8.0]]))

(kind/test-last [= [2 2]])

(kind/doc #'cx/scalar?)

(cx/scalar? (cx/complex 3.0 4.0))

(kind/test-last [true?])

(kind/doc #'cx/complex?)

(cx/complex? (cx/complex 3.0 4.0))

(kind/test-last [true?])

(cx/complex? (t/eye 2))

(kind/test-last [false?])

(kind/doc #'cx/->tensor)

(t/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))

(kind/test-last [= [2 2]])

(kind/doc #'cx/->double-array)

(let [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct)))

(kind/test-last [= [1.0 3.0 2.0 4.0]])

(kind/doc #'cx/wrap-tensor)

(let [raw (t/matrix [[1.0 2.0] [3.0 4.0]])
      ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)])

(kind/test-last [(fn [[c? shape]] (and c? (= [2] shape)))])

(kind/doc #'cx/add)

(let [a (cx/complex-tensor [1.0 2.0] [3.0 4.0])
      b (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b)))

(kind/test-last [= [11.0 22.0]])

(kind/doc #'cx/sub)

(let [a (cx/complex-tensor [10.0 20.0] [30.0 40.0])
      b (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b)))

(kind/test-last [= [9.0 18.0]])

(kind/doc #'cx/scale)

(let [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)])

(kind/test-last [= [[2.0 4.0] [6.0 8.0]]])

(kind/doc #'cx/mul)

;; $(1+3i)(2+4i) = (2-12) + (4+6)i = -10 + 10i$
(let [a (cx/complex-tensor [1.0] [3.0])
      b (cx/complex-tensor [2.0] [4.0])
      c (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))])

(kind/test-last [= [-10.0 10.0]])

(kind/doc #'cx/conj)

(let [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct))

(kind/test-last [= [-3.0 4.0]])

(kind/doc #'cx/abs)

;; $|3+4i| = 5$
(let [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0))

(kind/test-last [true?])

(kind/doc #'cx/dot)

(let [a (cx/complex-tensor [1.0 0.0] [0.0 1.0])
      b (cx/complex-tensor [0.0 1.0] [1.0 0.0])
      result (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0))

(kind/test-last [true?])

(kind/doc #'cx/dot-conj)

;; Hermitian inner product: $\langle a, a \rangle = \|a\|^2$.
(let [a (cx/complex-tensor [3.0 1.0] [4.0 2.0])
      result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0))

(kind/test-last [true?])

(kind/doc #'cx/sum)

(let [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])
      s (cx/sum ct)]
  [(cx/re s) (cx/im s)])

(kind/test-last [= [6.0 15.0]])

;; ## `scicloj.lalinea.transform`
;;
;; Bridge between Fastmath transforms and La Linea tensors.
;; The FFT takes a real signal and returns a ComplexTensor spectrum.

(kind/doc #'ft/forward)

(let [signal [1.0 0.0 0.0 0.0]
      spectrum (ft/forward signal)]
  (cx/complex-shape spectrum))

(kind/test-last [= [4]])

(kind/doc #'ft/inverse)

(let [spectrum (ft/forward [1.0 2.0 3.0 4.0])
      roundtrip (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/inverse-real)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'ft/forward-complex)

(let [ct (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
      spectrum (ft/forward-complex ct)]
  (cx/complex-shape spectrum))

(kind/test-last [= [4]])

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

(tape/memory-status (la/add (t/eye 2) (t/eye 2)))

(kind/test-last [(fn [s] (= :lazy s))])

(kind/doc #'tape/memory-relation)

(let [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A)))

(kind/test-last [(fn [r] (= :shared r))])

(tape/memory-relation (t/matrix [[1 0] [0 1]]) (t/matrix [[5 6] [7 8]]))

(kind/test-last [(fn [r] (= :independent r))])

(tape/memory-relation (t/matrix [[1 2] [3 4]]) (la/add (t/eye 2) (t/eye 2)))

(kind/test-last [(fn [r] (= :unknown-lazy r))])

(kind/doc #'tape/with-tape)

(def tape-example
  (tape/with-tape
    (let [A (t/matrix [[1 2] [3 4]])
          B (la/scale A 2.0)]
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

;; ## `scicloj.lalinea.elementwise`
;;
;; Tape-aware element-wise operations with complex dispatch.
;; Each function records on the tape (when active) and dispatches
;; on `cx/complex?`.

(kind/doc #'elem/sq)

(elem/sq (t/column [2 3 4]))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 4.0))])

(kind/doc #'elem/sqrt)

(elem/sqrt (t/column [4 9 16]))

(kind/test-last [(fn [v] (la/close-scalar? (v 0 0) 2.0))])

(kind/doc #'elem/exp)

(la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/log)

(la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/log10)

(la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0)

(kind/test-last [true?])

(kind/doc #'elem/sin)

(la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/cos)

(la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/tan)

(la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/sinh)

(la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0)

(kind/test-last [true?])

(kind/doc #'elem/cosh)

(la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0)

(kind/test-last [true?])

(kind/doc #'elem/tanh)

(la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0)

(kind/test-last [true?])

(kind/doc #'elem/abs)

((elem/abs (t/column [-5])) 0 0)

(kind/test-last [(fn [v] (== 5.0 v))])

(kind/doc #'elem/sum)

(elem/sum (t/column [1 2 3 4]))

(kind/test-last [(fn [v] (== 10.0 v))])

(kind/doc #'elem/mean)

(elem/mean (t/column [2 4 6]))

(kind/test-last [(fn [v] (== 4.0 v))])

(kind/doc #'elem/pow)

((elem/pow (t/column [2]) 3) 0 0)

(kind/test-last [(fn [v] (== 8.0 v))])

(kind/doc #'elem/cbrt)

(la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0)

(kind/test-last [true?])

(kind/doc #'elem/floor)

((elem/floor (t/column [2.7])) 0 0)

(kind/test-last [(fn [v] (== 2.0 v))])

(kind/doc #'elem/ceil)

((elem/ceil (t/column [2.3])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'elem/min)

((elem/min (t/column [3]) (t/column [5])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'elem/max)

((elem/max (t/column [3]) (t/column [5])) 0 0)

(kind/test-last [(fn [v] (== 5.0 v))])

(kind/doc #'elem/asin)

((elem/asin (t/column [0.5])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/asin 0.5)))])

(kind/doc #'elem/acos)

((elem/acos (t/column [0.5])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/acos 0.5)))])

(kind/doc #'elem/atan)

((elem/atan (t/column [1.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v (math/atan 1.0)))])

(kind/doc #'elem/log1p)

((elem/log1p (t/column [0.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v 0.0))])

(kind/doc #'elem/expm1)

((elem/expm1 (t/column [0.0])) 0 0)

(kind/test-last [(fn [v] (la/close-scalar? v 0.0))])

(kind/doc #'elem/round)

((elem/round (t/column [2.7])) 0 0)

(kind/test-last [(fn [v] (== 3.0 v))])

(kind/doc #'elem/clip)

(t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1))

(kind/test-last [(fn [v] (= [-1.0 0.5 1.0] v))])

;; ## `scicloj.lalinea.grad`
;;
;; Reverse-mode automatic differentiation on the computation tape.

(kind/doc #'grad/grad)

(let [A (t/matrix [[1 2] [3 4]])
      tape-result (tape/with-tape
                    (la/trace (la/mmul (la/transpose A) A)))
      grads (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2)))

(kind/test-last [true?])

;; ## `scicloj.lalinea.vis`
;;
;; SVG and image helpers for visual linear algebra.

(kind/doc #'vis/arrow-plot)

(vis/arrow-plot [{:xy [2 1] :color "#2266cc" :label "u"}
                 {:xy [-1 1.5] :color "#cc4422" :label "v"}]
                {:width 250})

(kind/doc #'vis/graph-plot)

(vis/graph-plot [[0 0] [1 0] [0.5 0.87]]
                [[0 1] [1 2] [2 0]]
                {:width 250 :labels ["A" "B" "C"]})

(kind/doc #'vis/matrix->gray-image)

(let [m (t/compute-tensor [50 50]
                          (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
                          :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])

(kind/doc #'vis/extract-channel)

(let [img (t/compute-tensor [50 50 3]
                            (fn [r c ch]
                              (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
                            :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])
