;; # Fourier Transform
;;
;; **La Linea** bridges [Fastmath](https://generateme.github.io/fastmath/clay)'s
;; transform API to ComplexTensors. Fastmath's
;; `(:complex :fftr)` transformer outputs interleaved `double[]`
;; with `[re₀ im₀ re₁ im₁ ...]` — **exactly** the memory layout
;; of ComplexTensor. So the bridge is zero-copy.

(ns la-linea-book.fourier-transform
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.la-linea.complex :as cx]
   ;; FFT bridge — Fastmath transforms ↔ ComplexTensor:
   [scicloj.la-linea.transform :as bfft]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Forward FFT
;;
;; A real signal produces a complex spectrum.

(bfft/forward [1.0 0.0 -1.0 0.0])

;; The DC component ($k=0$) is $\sum x_n = 0$.

(kind/test-last [(fn [ct] (< (Math/abs (double (cx/re (ct 0)))) 1e-10))])

;; ## Round-trip
;;
;; FFT followed by inverse FFT recovers the original signal.

(let [signal [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
      spectrum (bfft/forward signal)
      recovered (bfft/inverse-real spectrum)]
  (dfn/reduce-max (dfn/abs (dfn/- recovered (double-array signal)))))

(kind/test-last [(fn [v] (< v 1e-10))])

;; ## Parseval's theorem
;;
;; Energy in time domain equals energy in frequency domain
;; (up to a scale factor of $N$):
;;
;; $$\sum_n |x_n|^2 = \frac{1}{N} \sum_k |\hat{x}_k|^2$$

(let [signal [1.0 2.0 3.0 4.0]
      n (count signal)
      spectrum (bfft/forward signal)
      time-energy (dfn/sum (dfn/* (double-array signal) (double-array signal)))
      magnitudes (la/abs spectrum)
      freq-energy (/ (dfn/sum (dfn/* magnitudes magnitudes)) n)]
  (< (Math/abs (- time-energy freq-energy)) 1e-10))

(kind/test-last [true?])

;; ## Linearity
;;
;; $\mathcal{F}(\alpha x + \beta y) = \alpha \mathcal{F}(x) + \beta \mathcal{F}(y)$

(let [x [1.0 2.0 3.0 4.0]
      y [5.0 6.0 7.0 8.0]
      alpha 2.0
      beta -1.5
      combined (double-array (dfn/+ (dfn/* alpha (double-array x)) (dfn/* beta (double-array y))))
      lhs (bfft/forward combined)
      rhs (la/add (la/scale (bfft/forward x) alpha)
                  (la/scale (bfft/forward y) beta))]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- (cx/re lhs) (cx/re rhs)))) 1e-10)
       (< (dfn/reduce-max (dfn/abs (dfn/- (cx/im lhs) (cx/im rhs)))) 1e-10)))

(kind/test-last [true?])

;; ## Convolution theorem
;;
;; Pointwise multiplication in frequency domain corresponds to
;; circular convolution in time domain:
;;
;; $$\mathcal{F}(x * y) = \mathcal{F}(x) \cdot \mathcal{F}(y)$$

(let [x [1.0 2.0 0.0 0.0]
      y [1.0 0.0 1.0 0.0]
      Fx (bfft/forward x)
      Fy (bfft/forward y)
      product-spectrum (la/mul Fx Fy)
      conv-result (bfft/inverse-real product-spectrum)
      n (count x)
      xarr (double-array x)
      yarr (double-array y)
      manual-conv (let [out (double-array n)]
                    (dotimes [k n]
                      (let [s (loop [j 0 acc 0.0]
                                (if (>= j n) acc
                                    (recur (inc j)
                                           (+ acc (* (aget xarr j)
                                                     (aget yarr (mod (- k j) n)))))))]
                        (aset out k s)))
                    out)]
  (< (dfn/reduce-max (dfn/abs (dfn/- conv-result manual-conv))) 1e-10))

(kind/test-last [true?])

;; ## Known transform pairs
;;
;; The DFT of a constant signal $x_n = c$ is $\hat{x}_0 = Nc$
;; with all other bins zero.

(let [spectrum (bfft/forward [3.0 3.0 3.0 3.0])]
  {:dc (cx/re (spectrum 0))
   :others [(la/abs (spectrum 1)) (la/abs (spectrum 2)) (la/abs (spectrum 3))]})

(kind/test-last [(fn [v] (and (< (Math/abs (- (double (:dc v)) 12.0)) 1e-10)
                              (every? #(< % 1e-10) (:others v))))])

;; The DFT of $x = [1, -1, 1, -1]$ has energy only at Nyquist ($k = N/2$).

(let [spectrum (bfft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0)))
   :nyquist (double (cx/re (spectrum 2)))})

(kind/test-last [(fn [v] (and (< (:dc v) 1e-10)
                              (< (Math/abs (- (:nyquist v) 4.0)) 1e-10)))])

;; ## Complex-to-complex FFT
;;
;; When the input is already complex, use `forward-complex`.

(let [signal (cx/complex-tensor [1.0 0.0] [0.0 1.0])
      spectrum (bfft/forward-complex signal)
      recovered (bfft/inverse spectrum)]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- (cx/re recovered) (cx/re signal)))) 1e-10)
       (< (dfn/reduce-max (dfn/abs (dfn/- (cx/im recovered) (cx/im signal)))) 1e-10)))

(kind/test-last [true?])

;; ## Other transforms
;;
;; DCT, DST, and DHT are also available, returning real tensors.

(let [signal [1.0 2.0 3.0 4.0]
      dct (bfft/dct-forward signal)
      recovered (bfft/dct-inverse dct)]
  (< (dfn/reduce-max (dfn/abs (dfn/- recovered (double-array signal)))) 1e-10))

(kind/test-last [true?])
