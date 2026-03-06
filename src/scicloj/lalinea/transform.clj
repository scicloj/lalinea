(ns scicloj.lalinea.transform
  "FFT and real-valued transforms backed by JTransforms.

   JTransforms' `complexForward` / `complexInverse` operate on flat
   interleaved `double[]` with `[re₀ im₀ re₁ im₁ ...]` — exactly
   the memory layout of ComplexTensor. This namespace provides
   zero-copy wrappers around JTransforms for both 1-D and 2-D transforms."
  (:require [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype])
  (:import [org.jtransforms.fft DoubleFFT_1D DoubleFFT_2D]
           [org.jtransforms.dct DoubleDCT_1D]
           [org.jtransforms.dst DoubleDST_1D]
           [org.jtransforms.dht DoubleDHT_1D]))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- ->double-array-input
  "Coerce a signal (seq, double[], RealTensor, or dtype buffer) to double[]."
  ^doubles [signal]
  (dtype/->double-array (rt/ensure-tensor signal)))

;; ---------------------------------------------------------------------------
;; 1-D FFT
;; ---------------------------------------------------------------------------

(defn forward
  "Forward FFT of a real signal. Returns a ComplexTensor spectrum.

   The input can be a Clojure seq, double[], RealTensor, or dtype-next buffer.
   The output is a ComplexTensor of shape `[n]` where n = length of input."
  [signal]
  (let [buf (ct/->double-array (ct/complex-tensor-real signal))
        n   (quot (alength buf) 2)
        fft (DoubleFFT_1D. n)]
    (.complexForward fft buf)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [n 2]))))

(defn inverse
  "Inverse FFT from a ComplexTensor spectrum back to a ComplexTensor signal."
  [spectrum]
  (let [[n]  (ct/complex-shape spectrum)
        buf  (aclone (ct/->double-array spectrum))
        fft  (DoubleFFT_1D. n)]
    (.complexInverse fft buf true)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [n 2]))))

(defn inverse-real
  "Inverse FFT from a ComplexTensor spectrum, returning only the real part.
   Useful when you know the result should be purely real."
  [spectrum]
  (rt/->real-tensor (ct/re (inverse spectrum))))

(defn forward-complex
  "Forward FFT of a ComplexTensor signal. Returns a ComplexTensor spectrum."
  [signal]
  (let [[n]  (ct/complex-shape signal)
        buf  (aclone (ct/->double-array signal))
        fft  (DoubleFFT_1D. n)]
    (.complexForward fft buf)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [n 2]))))

;; ---------------------------------------------------------------------------
;; 2-D FFT
;; ---------------------------------------------------------------------------

(defn forward-2d
  "Forward 2-D FFT of a real `[r c]` matrix.
   Returns a ComplexTensor spectrum of shape `[r c]`."
  [matrix]
  (let [[rows cols] (dtype/shape (rt/ensure-tensor matrix))
        buf  (ct/->double-array (ct/complex-tensor-real matrix))
        fft  (DoubleFFT_2D. rows cols)]
    (.complexForward fft buf)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [rows cols 2]))))

(defn inverse-2d
  "Inverse 2-D FFT from a ComplexTensor spectrum back to a ComplexTensor."
  [spectrum]
  (let [[rows cols] (ct/complex-shape spectrum)
        buf  (aclone (ct/->double-array spectrum))
        fft  (DoubleFFT_2D. rows cols)]
    (.complexInverse fft buf true)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [rows cols 2]))))

(defn inverse-real-2d
  "Inverse 2-D FFT from a ComplexTensor spectrum, returning only the real part.
   Useful when you know the result should be purely real."
  [spectrum]
  (rt/->real-tensor (ct/re (inverse-2d spectrum))))

(defn forward-complex-2d
  "Forward 2-D FFT of a ComplexTensor signal. Returns a ComplexTensor spectrum."
  [signal]
  (let [[rows cols] (ct/complex-shape signal)
        buf  (aclone (ct/->double-array signal))
        fft  (DoubleFFT_2D. rows cols)]
    (.complexForward fft buf)
    (ct/->complex-tensor (dtt/reshape (dtt/ensure-tensor buf) [rows cols 2]))))

;; ---------------------------------------------------------------------------
;; DCT / DST / DHT (1-D, real → real)
;; ---------------------------------------------------------------------------

(defn dct-forward
  "Forward Discrete Cosine Transform. Real → real."
  [signal]
  (let [buf (aclone (->double-array-input signal))
        dct (DoubleDCT_1D. (alength buf))]
    (.forward dct buf true)
    (rt/->real-tensor (dtt/ensure-tensor buf))))

(defn dct-inverse
  "Inverse Discrete Cosine Transform. Real → real."
  [spectrum]
  (let [buf (aclone (->double-array-input spectrum))
        dct (DoubleDCT_1D. (alength buf))]
    (.inverse dct buf true)
    (rt/->real-tensor (dtt/ensure-tensor buf))))

(defn dst-forward
  "Forward Discrete Sine Transform. Real → real."
  [signal]
  (let [buf (aclone (->double-array-input signal))
        dst (DoubleDST_1D. (alength buf))]
    (.forward dst buf true)
    (rt/->real-tensor (dtt/ensure-tensor buf))))

(defn dst-inverse
  "Inverse Discrete Sine Transform. Real → real."
  [spectrum]
  (let [buf (aclone (->double-array-input spectrum))
        dst (DoubleDST_1D. (alength buf))]
    (.inverse dst buf true)
    (rt/->real-tensor (dtt/ensure-tensor buf))))

(defn dht-forward
  "Forward Discrete Hartley Transform. Real → real."
  [signal]
  (let [buf (aclone (->double-array-input signal))
        dht (DoubleDHT_1D. (alength buf))]
    (.forward dht buf)
    (rt/->real-tensor (dtt/ensure-tensor buf))))

(defn dht-inverse
  "Inverse Discrete Hartley Transform. Real → real."
  [spectrum]
  (let [buf (aclone (->double-array-input spectrum))
        dht (DoubleDHT_1D. (alength buf))]
    (.inverse dht buf true)
    (rt/->real-tensor (dtt/ensure-tensor buf))))
