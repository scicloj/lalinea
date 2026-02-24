(ns scicloj.basis.api
  "Public API for basis — linear algebra with tensor abstractions.

   This namespace re-exports the key functions from the library.
   Users typically require this namespace plus the specific modules
   they need:

   ```clojure
   (require '[scicloj.basis.linalg :as la])       ;; matrix ops
   (require '[scicloj.basis.impl.complex :as cx])  ;; complex tensors
   (require '[scicloj.basis.transform :as bfft])   ;; DFT bridge
   ```

   Or require this namespace for a curated subset:
   ```clojure
   (require '[scicloj.basis.api :as basis])
   ```"
  (:require [scicloj.basis.linalg :as la]
            [scicloj.basis.impl.complex :as cx]
            [scicloj.basis.impl.tensor :as bt]
            [scicloj.basis.transform :as bfft]))

;; ---------------------------------------------------------------------------
;; Re-exports: matrix construction
;; ---------------------------------------------------------------------------

(def matrix "Create a matrix from nested sequences." la/matrix)
(def eye "Identity matrix of size n × n." la/eye)
(def zeros "Zero matrix of size r × c." la/zeros)
(def diag "Diagonal matrix from values." la/diag)
(def column "Column vector from sequence." la/column)
(def row "Row vector from sequence." la/row)

;; ---------------------------------------------------------------------------
;; Re-exports: linear algebra
;; ---------------------------------------------------------------------------

(def mmul "Matrix multiply." la/mmul)
(def transpose "Transpose (real) or conjugate transpose (complex)." la/transpose)
(def add "Matrix addition." la/add)
(def sub "Matrix subtraction." la/sub)
(def scale "Scalar multiply." la/scale)
(def trace "Matrix trace." la/trace)
(def det "Matrix determinant." la/det)
(def norm "Frobenius norm." la/norm)
(def close? "Approximate matrix equality." la/close?)
(def close-scalar? "Approximate scalar equality." la/close-scalar?)
(def invert "Matrix inverse." la/invert)
(def solve "Solve A*X=B for X." la/solve)
(def eigen "Eigendecomposition." la/eigen)
(def svd "Singular value decomposition." la/svd)
(def qr "QR decomposition." la/qr)
(def cholesky "Cholesky decomposition." la/cholesky)

;; ---------------------------------------------------------------------------
;; Re-exports: complex tensors
;; ---------------------------------------------------------------------------

(def complex-tensor "Create a ComplexTensor." cx/complex-tensor)
(def complex-tensor-real "ComplexTensor from real data (im=0)." cx/complex-tensor-real)
(def complex "Scalar complex number." cx/complex)
(def re "Real part (zero-copy view)." cx/re)
(def im "Imaginary part (zero-copy view)." cx/im)
(def complex-shape "Shape without trailing 2." cx/complex-shape)

;; ---------------------------------------------------------------------------
;; Re-exports: transforms
;; ---------------------------------------------------------------------------

(def fft "Forward FFT: real signal -> ComplexTensor spectrum." bfft/forward)
(def ifft "Inverse FFT: ComplexTensor -> ComplexTensor." bfft/inverse)
(def ifft-real "Inverse FFT returning real part only." bfft/inverse-real)
