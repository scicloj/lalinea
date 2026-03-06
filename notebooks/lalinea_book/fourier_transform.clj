;; # Fourier Transform
;;
;; *Prerequisites: the [discrete Fourier transform](https://en.wikipedia.org/wiki/Discrete_Fourier_transform)
;; and [complex numbers](https://en.wikipedia.org/wiki/Complex_number).
;; The chapter uses standard concepts (spectrum, frequency bins,
;; convolution theorem) without introduction.*
;;
;; **La Linea** wraps [JTransforms](https://github.com/wendykierp/JTransforms)
;; for both 1-D and 2-D FFT. JTransforms' `complexForward` /
;; `complexInverse` operate on interleaved `double[]`
;; with `[re₀ im₀ re₁ im₁ ...]` — **exactly** the memory layout
;; of ComplexTensor. So the bridge is zero-copy.

(ns lalinea-book.fourier-transform
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.elementwise :as el]
   ;; FFT bridge — JTransforms <-> ComplexTensor:
   [scicloj.lalinea.transform :as ft]
   ;; Dataset manipulation (https://scicloj.github.io/tablecloth/):
   [tablecloth.api :as tc]
   ;; Interactive Plotly charts (https://scicloj.github.io/tableplot/):
   [scicloj.tableplot.v1.plotly :as plotly]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Forward [FFT](https://en.wikipedia.org/wiki/Fast_Fourier_transform)
;;
;; A real signal produces a complex spectrum.

(ft/dft-fwd [1.0 0.0 -1.0 0.0])

;; The DC component ($k=0$) is $\sum x_n = 0$.

(kind/test-last [(fn [ct] (< (abs (double (el/re (ct 0)))) 1e-10))])

;; ## Round-trip
;;
;; FFT followed by inverse FFT recovers the original signal.

(let [signal [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
      spectrum (ft/dft-fwd signal)
      recovered (ft/dft-inv-real spectrum)]
  (el/reduce-max (el/abs (el/- recovered signal))))

(kind/test-last [(fn [v] (< v 1e-10))])

;; ## [Parseval's theorem](https://en.wikipedia.org/wiki/Parseval%27s_theorem)
;;
;; Energy in time domain equals energy in frequency domain
;; (up to a scale factor of $N$):
;;
;; $$\sum_n |x_n|^2 = \frac{1}{N} \sum_k |\hat{x}_k|^2$$

(let [signal [1.0 2.0 3.0 4.0]
      n (count signal)
      spectrum (ft/dft-fwd signal)
      time-energy (el/sum (el/* signal signal))
      magnitudes (el/abs spectrum)
      freq-energy (/ (el/sum (el/* magnitudes magnitudes)) n)]
  (< (abs (- time-energy freq-energy)) 1e-10))

(kind/test-last [true?])

;; ## Linearity
;;
;; $\mathcal{F}(\alpha x + \beta y) = \alpha \mathcal{F}(x) + \beta \mathcal{F}(y)$

(let [x [1.0 2.0 3.0 4.0]
      y [5.0 6.0 7.0 8.0]
      alpha 2.0
      beta -1.5
      combined (el/+ (el/* alpha x) (el/* beta y))
      lhs (ft/dft-fwd combined)
      rhs (el/+ (el/scale (ft/dft-fwd x) alpha)
                (el/scale (ft/dft-fwd y) beta))]
  (and (< (el/reduce-max (el/abs (el/- (el/re lhs) (el/re rhs)))) 1e-10)
       (< (el/reduce-max (el/abs (el/- (el/im lhs) (el/im rhs)))) 1e-10)))

(kind/test-last [true?])

;; ## [Convolution theorem](https://en.wikipedia.org/wiki/Convolution_theorem)
;;
;; Pointwise multiplication in frequency domain corresponds to
;; circular convolution in time domain:
;;
;; $$\mathcal{F}(x * y) = \mathcal{F}(x) \cdot \mathcal{F}(y)$$

(let [x [1.0 2.0 0.0 0.0]
      y [1.0 0.0 1.0 0.0]
      Fx (ft/dft-fwd x)
      Fy (ft/dft-fwd y)
      product-spectrum (el/* Fx Fy)
      conv-result (ft/dft-inv-real product-spectrum)
      n (count x)
      manual-conv (let [out (t/make-container :float64 n)]
                    (dotimes [k n]
                      (let [s (loop [j 0 acc 0.0]
                                (if (>= j n) acc
                                    (recur (inc j)
                                           (+ acc (* (double (x j))
                                                     (double (y (mod (- k j) n))))))))]
                        (t/set-value! out k s)))
                    out)]
  (< (el/reduce-max (el/abs (el/- conv-result manual-conv))) 1e-10))

(kind/test-last [true?])

;; ## Visualising a spectrum
;;
;; A signal made of two sine waves — 3 Hz and 7 Hz:

(def N-vis 64)

(def signal-composed
  (t/->real-tensor
   (t/materialize
    (t/make-reader :float64 N-vis
                   (let [ti (/ (double idx) N-vis)]
                     (+ (math/sin (* 2 math/PI 3 ti))
                        (* 0.5 (math/sin (* 2 math/PI 7 ti)))))))))

;; The time-domain waveform:

(-> (tc/dataset {:t (t/make-reader :float64 N-vis (/ (double idx) N-vis))
                 :amplitude signal-composed})
    (plotly/base {:=x :t :=y :amplitude})
    (plotly/layer-line)
    plotly/plot)

;; The magnitude spectrum reveals two peaks at frequencies
;; 3 and 7:

(let [spectrum (ft/dft-fwd signal-composed)
      mags (el/abs spectrum)]
  (-> (tc/dataset {:frequency (range (/ N-vis 2))
                   :magnitude (take (/ N-vis 2) mags)})
      (plotly/base {:=x :frequency :=y :magnitude})
      (plotly/layer-bar)
      plotly/plot))

;; The two largest magnitude bins are at frequencies 3 and 7:

(let [spectrum (ft/dft-fwd signal-composed)
      mags (el/abs spectrum)
      half-n (/ N-vis 2)
      peak-idx (el/argsort > (t/select mags (range half-n)))]
  (= [3 7] (sort (take 2 peak-idx))))

(kind/test-last [true?])

;; ## Known transform pairs
;;
;; The DFT of a constant signal $x_n = c$ is $\hat{x}_0 = Nc$
;; with all other bins zero.

(let [spectrum (ft/dft-fwd [3.0 3.0 3.0 3.0])]
  {:dc (el/re (spectrum 0))
   :others [(el/abs (spectrum 1)) (el/abs (spectrum 2)) (el/abs (spectrum 3))]})

(kind/test-last [(fn [v] (and (< (abs (- (double (:dc v)) 12.0)) 1e-10)
                              (every? #(< % 1e-10) (:others v))))])

;; The DFT of $x = [1, -1, 1, -1]$ has energy only at Nyquist ($k = N/2$).

(let [spectrum (ft/dft-fwd [1.0 -1.0 1.0 -1.0])]
  {:dc (double (el/abs (spectrum 0)))
   :nyquist (double (el/re (spectrum 2)))})

(kind/test-last [(fn [v] (and (< (:dc v) 1e-10)
                              (< (abs (- (:nyquist v) 4.0)) 1e-10)))])

;; ## Complex-to-complex FFT
;;
;; When the input is already complex, use `forward-complex`.

(let [signal (t/complex-tensor [1.0 0.0] [0.0 1.0])
      spectrum (ft/dft-fwd-complex signal)
      recovered (ft/dft-inv spectrum)]
  (and (< (el/reduce-max (el/abs (el/- (el/re recovered) (el/re signal)))) 1e-10)
       (< (el/reduce-max (el/abs (el/- (el/im recovered) (el/im signal)))) 1e-10)))

(kind/test-last [true?])

;; ## Other transforms
;;
;; [DCT](https://en.wikipedia.org/wiki/Discrete_cosine_transform), [DST](https://en.wikipedia.org/wiki/Discrete_sine_transform), and [DHT](https://en.wikipedia.org/wiki/Discrete_Hartley_transform) are also available, returning real tensors.

(let [signal [1.0 2.0 3.0 4.0]
      dct (ft/dct-fwd signal)
      recovered (ft/dct-inv dct)]
  (< (el/reduce-max (el/abs (el/- recovered signal))) 1e-10))

(kind/test-last [true?])

;; ## 2-D FFT
;;
;; The [2-D DFT](https://en.wikipedia.org/wiki/Discrete_Fourier_transform#Multidimensional_DFT)
;; extends the Fourier transform to matrices.
;; `forward-2d` takes a real `[r c]` matrix and returns a
;; ComplexTensor spectrum of shape `[r c]`.

;; ### Forward and inverse

(ft/dft-fwd-2d (t/matrix [[1 2] [3 4]]))

;; Round-trip recovers the original matrix:

(let [A (t/matrix [[1 2 3] [4 5 6] [7 8 9]])
      recovered (ft/dft-inv-real-2d (ft/dft-fwd-2d A))]
  (la/close? recovered A))

(kind/test-last [true?])

;; ### 2-D [Parseval's theorem](https://en.wikipedia.org/wiki/Parseval%27s_theorem)
;;
;; $$\sum_{m,n} |x_{m,n}|^2 = \frac{1}{MN} \sum_{k,l} |\hat{x}_{k,l}|^2$$

(let [A (t/matrix [[1 2 3 4]
                    [5 6 7 8]
                    [9 10 11 12]
                    [13 14 15 16]])
      mn (* 4 4)
      spectrum (ft/dft-fwd-2d A)
      space-energy (el/sum (el/* A A))
      mags (el/abs spectrum)
      freq-energy (/ (el/sum (el/* mags mags)) mn)]
  (< (abs (- space-energy freq-energy)) 1e-10))

(kind/test-last [true?])

;; ### Spatial frequencies
;;
;; A matrix with a known horizontal pattern — columns alternate
;; between 1 and −1 — has energy at the horizontal Nyquist
;; frequency.

(let [A (t/matrix [[ 1 -1  1 -1]
                    [ 1 -1  1 -1]
                    [ 1 -1  1 -1]
                    [ 1 -1  1 -1]])
      spectrum (ft/dft-fwd-2d A)
      mags (el/abs spectrum)]
  {:dc (double (mags 0 0))
   :h-nyquist (double (mags 0 2))})

(kind/test-last [(fn [v] (and (< (:dc v) 1e-10)
                              (< (abs (- (:h-nyquist v) 16.0)) 1e-10)))])

;; A vertical pattern — rows alternate — puts energy at
;; the vertical Nyquist:

(let [A (t/matrix [[ 1  1  1  1]
                    [-1 -1 -1 -1]
                    [ 1  1  1  1]
                    [-1 -1 -1 -1]])
      spectrum (ft/dft-fwd-2d A)
      mags (el/abs spectrum)]
  {:dc (double (mags 0 0))
   :v-nyquist (double (mags 2 0))})

(kind/test-last [(fn [v] (and (< (:dc v) 1e-10)
                              (< (abs (- (:v-nyquist v) 16.0)) 1e-10)))])
