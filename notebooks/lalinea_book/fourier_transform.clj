;; # Fourier Transform
;;
;; **La Linea** bridges [Fastmath](https://generateme.github.io/fastmath/clay)'s
;; transform API to ComplexTensors. Fastmath's
;; `(:complex :fftr)` transformer outputs interleaved `double[]`
;; with `[re₀ im₀ re₁ im₁ ...]` — **exactly** the memory layout
;; of ComplexTensor. So the bridge is zero-copy.

(ns lalinea-book.fourier-transform
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.lalinea.complex :as cx]
   ;; FFT bridge — Fastmath transforms ↔ ComplexTensor:
   [scicloj.lalinea.transform :as ft]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
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

(ft/forward [1.0 0.0 -1.0 0.0])

;; The DC component ($k=0$) is $\sum x_n = 0$.

(kind/test-last [(fn [ct] (< (abs (double (cx/re (ct 0)))) 1e-10))])

;; ## Round-trip
;;
;; FFT followed by inverse FFT recovers the original signal.

(let [signal [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0]
      spectrum (ft/forward signal)
      recovered (ft/inverse-real spectrum)]
  (dfn/reduce-max (dfn/abs (dfn/- recovered signal))))

(kind/test-last [(fn [v] (< v 1e-10))])

;; ## [Parseval's theorem](https://en.wikipedia.org/wiki/Parseval%27s_theorem)
;;
;; Energy in time domain equals energy in frequency domain
;; (up to a scale factor of $N$):
;;
;; $$\sum_n |x_n|^2 = \frac{1}{N} \sum_k |\hat{x}_k|^2$$

(let [signal [1.0 2.0 3.0 4.0]
      n (count signal)
      spectrum (ft/forward signal)
      time-energy (dfn/sum (dfn/* signal signal))
      magnitudes (la/abs spectrum)
      freq-energy (/ (dfn/sum (dfn/* magnitudes magnitudes)) n)]
  (< (abs (- time-energy freq-energy)) 1e-10))

(kind/test-last [true?])

;; ## Linearity
;;
;; $\mathcal{F}(\alpha x + \beta y) = \alpha \mathcal{F}(x) + \beta \mathcal{F}(y)$

(let [x [1.0 2.0 3.0 4.0]
      y [5.0 6.0 7.0 8.0]
      alpha 2.0
      beta -1.5
      combined (dfn/+ (dfn/* alpha x) (dfn/* beta y))
      lhs (ft/forward combined)
      rhs (la/add (la/scale (ft/forward x) alpha)
                  (la/scale (ft/forward y) beta))]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- (cx/re lhs) (cx/re rhs)))) 1e-10)
       (< (dfn/reduce-max (dfn/abs (dfn/- (cx/im lhs) (cx/im rhs)))) 1e-10)))

(kind/test-last [true?])

;; ## [Convolution theorem](https://en.wikipedia.org/wiki/Convolution_theorem)
;;
;; Pointwise multiplication in frequency domain corresponds to
;; circular convolution in time domain:
;;
;; $$\mathcal{F}(x * y) = \mathcal{F}(x) \cdot \mathcal{F}(y)$$

(let [x [1.0 2.0 0.0 0.0]
      y [1.0 0.0 1.0 0.0]
      Fx (ft/forward x)
      Fy (ft/forward y)
      product-spectrum (la/mul Fx Fy)
      conv-result (ft/inverse-real product-spectrum)
      n (count x)
      manual-conv (let [out (dtype/make-container :float64 n)]
                    (dotimes [k n]
                      (let [s (loop [j 0 acc 0.0]
                                (if (>= j n) acc
                                    (recur (inc j)
                                           (+ acc (* (double (x j))
                                                     (double (y (mod (- k j) n))))))))]
                        (dtype/set-value! out k s)))
                    out)]
  (< (dfn/reduce-max (dfn/abs (dfn/- conv-result manual-conv))) 1e-10))

(kind/test-last [true?])

;; ## Visualising a spectrum
;;
;; A signal made of two sine waves — 3 Hz and 7 Hz:

(def N-vis 64)

(def signal-composed
  (la/->real-tensor
   (dtype/clone
    (dtype/make-reader :float64 N-vis
      (let [ti (/ (double idx) N-vis)]
        (+ (math/sin (* 2 math/PI 3 ti))
           (* 0.5 (math/sin (* 2 math/PI 7 ti)))))))))

;; The time-domain waveform:

(-> (tc/dataset {:t (dtype/make-reader :float64 N-vis (/ (double idx) N-vis))
                 :amplitude signal-composed})
    (plotly/base {:=x :t :=y :amplitude})
    (plotly/layer-line)
    plotly/plot)

;; The magnitude spectrum reveals two peaks at frequencies
;; 3 and 7:

(let [spectrum (ft/forward signal-composed)
      mags (la/abs spectrum)]
  (-> (tc/dataset {:frequency (range (/ N-vis 2))
                   :magnitude (take (/ N-vis 2) mags)})
      (plotly/base {:=x :frequency :=y :magnitude})
      (plotly/layer-bar)
      plotly/plot))

;; The two largest magnitude bins are at frequencies 3 and 7:

(let [spectrum (ft/forward signal-composed)
      mags (la/abs spectrum)
      half-n (/ N-vis 2)
      peak-idx (sort-by (fn [i] (- (double (mags i))))
                        (range half-n))]
  (= [3 7] (sort (take 2 peak-idx))))

(kind/test-last [true?])

;; ## Known transform pairs
;;
;; The DFT of a constant signal $x_n = c$ is $\hat{x}_0 = Nc$
;; with all other bins zero.

(let [spectrum (ft/forward [3.0 3.0 3.0 3.0])]
  {:dc (cx/re (spectrum 0))
   :others [(la/abs (spectrum 1)) (la/abs (spectrum 2)) (la/abs (spectrum 3))]})

(kind/test-last [(fn [v] (and (< (abs (- (double (:dc v)) 12.0)) 1e-10)
                              (every? #(< % 1e-10) (:others v))))])

;; The DFT of $x = [1, -1, 1, -1]$ has energy only at Nyquist ($k = N/2$).

(let [spectrum (ft/forward [1.0 -1.0 1.0 -1.0])]
  {:dc (double (la/abs (spectrum 0)))
   :nyquist (double (cx/re (spectrum 2)))})

(kind/test-last [(fn [v] (and (< (:dc v) 1e-10)
                              (< (abs (- (:nyquist v) 4.0)) 1e-10)))])

;; ## Complex-to-complex FFT
;;
;; When the input is already complex, use `forward-complex`.

(let [signal (cx/complex-tensor [1.0 0.0] [0.0 1.0])
      spectrum (ft/forward-complex signal)
      recovered (ft/inverse spectrum)]
  (and (< (dfn/reduce-max (dfn/abs (dfn/- (cx/re recovered) (cx/re signal)))) 1e-10)
       (< (dfn/reduce-max (dfn/abs (dfn/- (cx/im recovered) (cx/im signal)))) 1e-10)))

(kind/test-last [true?])

;; ## Other transforms
;;
;; [DCT](https://en.wikipedia.org/wiki/Discrete_cosine_transform), [DST](https://en.wikipedia.org/wiki/Discrete_sine_transform), and [DHT](https://en.wikipedia.org/wiki/Discrete_Hartley_transform) are also available, returning real tensors.

(let [signal [1.0 2.0 3.0 4.0]
      dct (ft/dct-forward signal)
      recovered (ft/dct-inverse dct)]
  (< (dfn/reduce-max (dfn/abs (dfn/- recovered signal))) 1e-10))

(kind/test-last [true?])
