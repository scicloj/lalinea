;; # API Reference
;;
;; Complete reference for the `scicloj.basis` public API:
;;
;; - `scicloj.basis.linalg` — matrix construction, arithmetic, decompositions
;; - `scicloj.basis.complex` — complex tensors
;; - `scicloj.basis.transform` — FFT and real-valued transforms
;; - `scicloj.basis.vis` — visualization helpers

^{:kindly/hide-code true
  :kindly/options {:kinds-that-hide-code #{:kind/doc}}}
(ns basis-book.api-reference
  (:require
   [scicloj.basis.linalg :as la]
   [scicloj.basis.complex :as cx]
   [scicloj.basis.transform :as bfft]
   [scicloj.basis.vis :as vis]
   [tech.v3.tensor :as tensor]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.libs.buffered-image :as bufimg]
   [scicloj.kindly.v4.kind :as kind]))

;; ## `scicloj.basis.linalg`


(kind/doc #'la/matrix)

(la/matrix [[1 2] [3 4]])

(kind/test-last [(fn [m] (= [2 2] (vec (dtype/shape m))))])

(kind/doc #'la/eye)

(la/eye 3)

(kind/test-last [(fn [m] (and (= [3 3] (vec (dtype/shape m)))
                              (== 1.0 (tensor/mget m 0 0))
                              (== 0.0 (tensor/mget m 0 1))))])

(kind/doc #'la/zeros)

(la/zeros 2 3)

(kind/test-last [(fn [m] (= [2 3] (vec (dtype/shape m))))])

(kind/doc #'la/diag)

(la/diag [3 5 7])

(kind/test-last [(fn [m] (and (= [3 3] (vec (dtype/shape m)))
                              (== 5.0 (tensor/mget m 1 1))
                              (== 0.0 (tensor/mget m 0 1))))])

(kind/doc #'la/column)

(la/column [1 2 3])

(kind/test-last [(fn [v] (= [3 1] (vec (dtype/shape v))))])

(kind/doc #'la/row)

(la/row [1 2 3])

(kind/test-last [(fn [v] (= [1 3] (vec (dtype/shape v))))])


(kind/doc #'la/add)

(la/add (la/matrix [[1 2] [3 4]])
        (la/matrix [[10 20] [30 40]]))

(kind/test-last [(fn [m] (== 11.0 (tensor/mget m 0 0)))])

(kind/doc #'la/sub)

(la/sub (la/matrix [[10 20] [30 40]])
        (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (== 9.0 (tensor/mget m 0 0)))])

(kind/doc #'la/scale)

(la/scale 3.0 (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (== 6.0 (tensor/mget m 0 1)))])

(kind/doc #'la/mul)

(la/mul (la/matrix [[2 3] [4 5]])
        (la/matrix [[10 20] [30 40]]))

(kind/test-last [(fn [m] (and (== 20.0 (tensor/mget m 0 0))
                              (== 60.0 (tensor/mget m 0 1))))])

(kind/doc #'la/abs)

(la/abs (la/matrix [[-3 2] [-1 4]]))

(kind/test-last [(fn [m] (== 3.0 (tensor/mget m 0 0)))])


(kind/doc #'la/mmul)

(la/mmul (la/matrix [[1 2] [3 4]])
         (la/column [5 6]))

(kind/test-last [(fn [m] (and (= [2 1] (vec (dtype/shape m)))
                              (== 17.0 (tensor/mget m 0 0))))])


(kind/doc #'la/transpose)

(la/transpose (la/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [m] (= [3 2] (vec (dtype/shape m))))])


(kind/doc #'la/submatrix)

(la/submatrix (la/eye 4) :all (range 2))

(kind/test-last [(fn [m] (= [4 2] (vec (dtype/shape m))))])


(kind/doc #'la/trace)

(la/trace (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (== 5.0 v))])

(kind/doc #'la/det)

(la/det (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (la/close-scalar? v -2.0))])

(kind/doc #'la/norm)

(la/norm (la/matrix [[3 0] [0 4]]))

(kind/test-last [(fn [v] (la/close-scalar? v 5.0))])

(kind/doc #'la/dot)

(la/dot (la/column [1 2 3]) (la/column [4 5 6]))

(kind/test-last [(fn [v] (== 32.0 v))])


(kind/doc #'la/close?)

(la/close? (la/eye 2) (la/eye 2))

(kind/test-last [true?])

(la/close? (la/eye 2) (la/zeros 2 2))

(kind/test-last [false?])

(kind/doc #'la/close-scalar?)

(la/close-scalar? 1.00000000001 1.0)

(kind/test-last [true?])


(kind/doc #'la/invert)

(let [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2)))

(kind/test-last [true?])

(kind/doc #'la/solve)

;; Solve $Ax = b$:
(let [A (la/matrix [[2 1] [1 3]])
      b (la/column [5 7])]
  (la/solve A b))

(kind/test-last [(fn [x] (and (la/close-scalar? (tensor/mget x 0 0) 1.6)
                              (la/close-scalar? (tensor/mget x 1 0) 1.8)))])


(kind/doc #'la/eigen)

(let [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))])

(kind/test-last [(fn [[n-evecs ev-shape]]
                   (and (= 2 n-evecs)
                        (= [2] ev-shape)))])

(kind/doc #'la/real-eigenvalues)

(la/real-eigenvalues (la/matrix [[2 1] [1 2]]))

(kind/test-last [(fn [evs] (and (la/close-scalar? (evs 0) 1.0)
                                (la/close-scalar? (evs 1) 3.0)))])

(kind/doc #'la/svd)

(let [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U))
   (count S)
   (vec (dtype/shape Vt))])

(kind/test-last [(fn [[u-shape n-s vt-shape]]
                   (and (= [3 3] u-shape)
                        (= 2 n-s)
                        (= [2 2] vt-shape)))])

(kind/doc #'la/qr)

(let [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]])))

(kind/test-last [true?])

(kind/doc #'la/cholesky)

(let [A (la/matrix [[4 2] [2 3]])
      L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A))

(kind/test-last [true?])


(kind/doc #'la/tensor->dmat)

(let [t (la/matrix [[1 2] [3 4]])
      dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm)))

(kind/test-last [true?])

(kind/doc #'la/dmat->tensor)

(let [dm (la/tensor->dmat (la/eye 2))
      t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t))))

(kind/test-last [true?])

(kind/doc #'la/complex-tensor->zmat)

(let [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])
      zm (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm)))

(kind/test-last [true?])

(kind/doc #'la/zmat->complex-tensor)

(let [zm (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
      ct (la/zmat->complex-tensor zm)]
  (cx/complex? ct))

(kind/test-last [true?])

;; ## `scicloj.basis.complex`
;;
;; A ComplexTensor wraps a dtype-next tensor whose last dimension
;; is 2 (interleaved real/imaginary pairs).


(kind/doc #'cx/complex-tensor)

(cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])

(kind/test-last [(fn [ct] (= [3] (cx/complex-shape ct)))])

(kind/doc #'cx/complex-tensor-real)

(cx/complex-tensor-real [5.0 6.0 7.0])

(kind/test-last [(fn [ct] (every? zero? (seq (cx/im ct))))])

(kind/doc #'cx/complex)

(cx/complex 3.0 4.0)

(kind/test-last [(fn [ct] (and (cx/scalar? ct)
                               (== 3.0 (cx/re ct))
                               (== 4.0 (cx/im ct))))])


(kind/doc #'cx/re)

(vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))

(kind/test-last [= [1.0 2.0]])

(kind/doc #'cx/im)

(vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))

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

(cx/complex? (la/eye 2))

(kind/test-last [false?])

(kind/doc #'cx/->tensor)

(vec (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))

(kind/test-last [= [2 2]])

(kind/doc #'cx/->double-array)

(let [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct)))

(kind/test-last [= [1.0 3.0 2.0 4.0]])


(kind/doc #'cx/add)

(let [a (cx/complex-tensor [1.0 2.0] [3.0 4.0])
      b (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b))))

(kind/test-last [= [11.0 22.0]])

(kind/doc #'cx/sub)

(let [a (cx/complex-tensor [10.0 20.0] [30.0 40.0])
      b (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b))))

(kind/test-last [= [9.0 18.0]])

(kind/doc #'cx/scale)

(let [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))])

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
  (vec (cx/im ct)))

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

;; ## `scicloj.basis.transform`
;;
;; Bridge between Fastmath transforms and basis tensors.
;; The FFT takes a real signal and returns a ComplexTensor spectrum.


(kind/doc #'bfft/forward)

(let [signal [1.0 0.0 0.0 0.0]
      spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum))

(kind/test-last [= [4]])

(kind/doc #'bfft/inverse)

(let [spectrum (bfft/forward [1.0 2.0 3.0 4.0])
      roundtrip (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0))

(kind/test-last [true?])

(kind/doc #'bfft/inverse-real)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'bfft/forward-complex)

(let [ct (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
      spectrum (bfft/forward-complex ct)]
  (cx/complex-shape spectrum))

(kind/test-last [= [4]])


(kind/doc #'bfft/dct-forward)

(bfft/dct-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'bfft/dct-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'bfft/dst-forward)

(bfft/dst-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'bfft/dst-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

(kind/doc #'bfft/dht-forward)

(bfft/dht-forward [1.0 2.0 3.0 4.0])

(kind/test-last [(fn [v] (= 4 (count v)))])

(kind/doc #'bfft/dht-inverse)

(let [signal [1.0 2.0 3.0 4.0]
      roundtrip (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0))

(kind/test-last [true?])

;; ## `scicloj.basis.vis`
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

(let [m (tensor/compute-tensor [50 50]
                               (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
                               :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])

(kind/doc #'vis/extract-channel)

(let [img (tensor/compute-tensor [50 50 3]
                                 (fn [r c ch]
                                   (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
                                 :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0)))

(kind/test-last [(fn [img] (= java.awt.image.BufferedImage (type img)))])
