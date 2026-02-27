(ns scicloj.la-linea.transform
  "Bridge between Fastmath transforms and La Linea tensors.

   Fastmath's (:complex :fftr) transformer takes a real signal and
   returns an interleaved double[] with [re₀ im₀ re₁ im₁ ...] —
   exactly the memory layout of ComplexTensor. This namespace
   provides zero-copy wrappers around Fastmath's transform API."
  (:require [scicloj.la-linea.complex :as cx]
            [fastmath.transform :as ft]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]))

;; ---------------------------------------------------------------------------
;; Cached transformers (lazy, thread-safe)
;; ---------------------------------------------------------------------------

(def ^:private fft-complex*     (delay (ft/transformer :complex :fftr)))
(def ^:private fft-complex-io*  (delay (ft/transformer :complex :fft)))
(def ^:private dct*             (delay (ft/transformer :real :dct)))
(def ^:private dst*             (delay (ft/transformer :real :dst)))
(def ^:private dht*             (delay (ft/transformer :real :dht)))

;; ---------------------------------------------------------------------------
;; Forward / inverse with ComplexTensor bridge
;; ---------------------------------------------------------------------------

(defn forward
  "Forward FFT of a real signal. Returns a ComplexTensor of the spectrum.

   The input can be a Clojure seq, double[], or dtype-next buffer.
   The output is a ComplexTensor of shape [n] where n = length of input."
  [signal]
  (let [result (ft/forward-1d @fft-complex* signal)]
    (cx/complex-tensor (tensor/reshape (tensor/ensure-tensor result)
                                       [(/ (count result) 2) 2]))))

(defn inverse
  "Inverse FFT from a ComplexTensor spectrum back to a ComplexTensor signal."
  [^scicloj.la_linea.complex.ComplexTensor spectrum]
  (let [arr (cx/->double-array spectrum)
        result (ft/reverse-1d @fft-complex-io* arr)]
    (cx/complex-tensor (tensor/reshape (tensor/ensure-tensor result)
                                       [(/ (count result) 2) 2]))))

(defn inverse-real
  "Inverse FFT from a ComplexTensor spectrum, returning only the real part.
   Useful when you know the result should be purely real."
  [spectrum]
  (let [ct (inverse spectrum)]
    (cx/re ct)))

;; ---------------------------------------------------------------------------
;; Complex-to-complex transforms
;; ---------------------------------------------------------------------------

(defn forward-complex
  "Forward FFT of a ComplexTensor signal. Returns a ComplexTensor spectrum."
  [^scicloj.la_linea.complex.ComplexTensor signal]
  (let [arr (cx/->double-array signal)
        result (ft/forward-1d @fft-complex-io* arr)]
    (cx/complex-tensor (tensor/reshape (tensor/ensure-tensor result)
                                       [(/ (count result) 2) 2]))))

;; ---------------------------------------------------------------------------
;; Other transforms (DCT, DST, DHT)
;; ---------------------------------------------------------------------------

(defn dct-forward
  "Forward Discrete Cosine Transform. Real -> real."
  [signal]
  (tensor/ensure-tensor (ft/forward-1d @dct* signal)))

(defn dct-inverse
  "Inverse Discrete Cosine Transform. Real -> real."
  [spectrum]
  (tensor/ensure-tensor (ft/reverse-1d @dct* spectrum)))

(defn dst-forward
  "Forward Discrete Sine Transform. Real -> real."
  [signal]
  (tensor/ensure-tensor (ft/forward-1d @dst* signal)))

(defn dst-inverse
  "Inverse Discrete Sine Transform. Real -> real."
  [spectrum]
  (tensor/ensure-tensor (ft/reverse-1d @dst* spectrum)))

(defn dht-forward
  "Forward Discrete Hartley Transform. Real -> real."
  [signal]
  (tensor/ensure-tensor (ft/forward-1d @dht* signal)))

(defn dht-inverse
  "Inverse Discrete Hartley Transform. Real -> real."
  [spectrum]
  (tensor/ensure-tensor (ft/reverse-1d @dht* spectrum)))
