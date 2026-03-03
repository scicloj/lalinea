(ns scicloj.lalinea.complex
  "Backward-compatibility shim for the complex API.

   Complex constructors and type operations now live in `scicloj.lalinea.tensor` (`t/`).
   Complex-aware arithmetic now lives in `scicloj.lalinea.linalg` (`la/`).

   This namespace re-exports them for existing code that uses `cx/`."
  (:refer-clojure :exclude [abs conj])
  (:require [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.tensor :as t]
            [scicloj.lalinea.linalg :as la]))

;; ---------------------------------------------------------------------------
;; Type API → t/
;; ---------------------------------------------------------------------------

(def complex?      "True if x is a ComplexTensor." t/complex?)
(def scalar?       "True if this ComplexTensor represents a scalar." t/scalar?)
(def ->tensor      "Access the underlying [... 2] tensor." ct/->tensor)
(def ->double-array "Access the underlying interleaved double[]." ct/->double-array)
(def wrap-tensor   "Wrap a raw interleaved [... 2] tensor as a ComplexTensor." t/wrap-tensor)
(def complex-shape "The complex shape (underlying shape without trailing 2)." t/complex-shape)

;; ---------------------------------------------------------------------------
;; Constructors → t/
;; ---------------------------------------------------------------------------

(def complex-tensor      "Create a ComplexTensor." t/complex-tensor)
(def complex-tensor-real "Create a ComplexTensor from real data only." t/complex-tensor-real)
(def complex             "Create a scalar ComplexTensor." t/complex)

;; ---------------------------------------------------------------------------
;; Operations → la/
;; ---------------------------------------------------------------------------

(def re       "Real part(s)." la/re)
(def im       "Imaginary part(s)." la/im)
(def conj     "Complex conjugate." la/conj)
(def add      "Pointwise complex addition." la/add)
(def sub      "Pointwise complex subtraction." la/sub)
(def mul      "Pointwise complex multiply." la/mul)
(def scale    "Scale by a real scalar." la/scale)
(def abs      "Element-wise absolute value / magnitude." la/abs)
(def dot      "Inner product." la/dot)
(def dot-conj "Hermitian inner product." la/dot-conj)
(def sum      "Sum of all elements." la/sum)

;; ---------------------------------------------------------------------------
;; Tagged literal reader
;; ---------------------------------------------------------------------------

(defn- parse-complex-tokens
  "Parse a flat sequence of tokens [re +/- im i ...] into [re im] pairs."
  [tokens]
  (loop [ts (seq tokens) pairs (transient [])]
    (if (nil? ts)
      (persistent! pairs)
      (let [[re-val sign im-val i-sym & rest-ts] ts]
        (when (or (nil? re-val) (nil? sign) (nil? im-val) (nil? i-sym))
          (throw (ex-info "Incomplete complex number in #la/C literal"
                          {:remaining (vec ts)})))
        (when-not (= 'i i-sym)
          (throw (ex-info (str "Expected 'i' symbol, got: " i-sym)
                          {:token i-sym})))
        (let [im-val (double im-val)
              im (case sign
                   + im-val
                   - (- im-val)
                   (throw (ex-info (str "Expected + or -, got: " sign)
                                   {:sign sign})))]
          (recur rest-ts (conj! pairs [(double re-val) im])))))))

(defn read-complex-tensor
  "Reader function for `#la/C` tagged literal.

   Format: `#la/C [:float64 [shape] data]` where data uses
   `re + im i` / `re - im i` notation.

   Scalar: `#la/C [:float64 [] [3.0 + 4.0 i]]`
   Vector: `#la/C [:float64 [2] [1.0 + 2.0 i 3.0 + 4.0 i]]`
   Matrix: `#la/C [:float64 [2 2] [[1.0 + 2.0 i 3.0 + 4.0 i] [5.0 + 6.0 i 7.0 + 8.0 i]]]`"
  [[_dtype shape data]]
  (when (some #{'...} (flatten data))
    (throw (ex-info "Cannot read truncated #la/C literal" {:shape shape})))
  (let [ndims (count shape)]
    (cond
      ;; Scalar: shape [], data = [re + im i]
      (zero? ndims)
      (let [[[r i]] (parse-complex-tokens data)]
        (ct/complex r i))

      ;; Vector: shape [n], data = [re + im i  re + im i ...]
      (= 1 ndims)
      (let [pairs (parse-complex-tokens data)]
        (ct/complex-tensor
         (tech.v3.tensor/ensure-tensor (double-array (mapv first pairs)))
         (tech.v3.tensor/ensure-tensor (double-array (mapv second pairs)))))

      ;; Matrix: shape [r c], data = [[row1-tokens] [row2-tokens] ...]
      :else
      (let [row-pairs (mapv parse-complex-tokens data)
            all-pairs (vec (apply concat row-pairs))
            re-arr (double-array (mapv first all-pairs))
            im-arr (double-array (mapv second all-pairs))]
        (ct/complex-tensor
         (tech.v3.tensor/reshape (tech.v3.tensor/ensure-tensor re-arr) shape)
         (tech.v3.tensor/reshape (tech.v3.tensor/ensure-tensor im-arr) shape))))))
