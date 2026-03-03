(ns scicloj.lalinea.complex
  "Complex tensors: constructors, arithmetic, and reader.

   A ComplexTensor wraps a real tensor whose last dimension is 2
   (interleaved re/im pairs). The `re` and `im` functions always
   slice the last axis, returning zero-copy tensor views.

   Supported ranks:
     [2]       scalar complex number
     [n 2]     complex vector of length n
     [r c 2]   complex r×c matrix
     [... 2]   arbitrary rank

   For the ComplexTensor deftype and structural accessors, see
   `scicloj.lalinea.impl.complex-tensor`."
  (:refer-clojure :exclude [abs conj])
  (:require [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn])
  (:import [scicloj.lalinea.impl.complex_tensor ComplexTensor]))

;; ---------------------------------------------------------------------------
;; Re-export type-level API from impl/complex_tensor
;; ---------------------------------------------------------------------------

(def complex?      "True if x is a ComplexTensor." ct/complex?)
(def scalar?       "True if this ComplexTensor represents a scalar." ct/scalar?)
(def ->tensor      "Access the underlying [... 2] tensor." ct/->tensor)
(def ->double-array "Access the underlying interleaved double[]." ct/->double-array)
(def wrap-tensor   "Wrap a raw interleaved [... 2] tensor as a ComplexTensor." ct/wrap-tensor)
(def complex-shape "The complex shape (underlying shape without trailing 2)." ct/complex-shape)

(defn re
  "Real part(s). Returns a RealTensor view or a double for scalars."
  [x]
  (ct/re x))

(defn im
  "Imaginary part(s). Returns a RealTensor view or a double for scalars."
  [x]
  (ct/im x))

;; ---------------------------------------------------------------------------
;; Tape recording (lazy resolution to avoid circular dep with tape ns)
;; ---------------------------------------------------------------------------

(def ^:private tape-var   (delay (requiring-resolve 'scicloj.lalinea.tape/*tape*)))
(def ^:private inside-var (delay (requiring-resolve 'scicloj.lalinea.tape/*inside-record*)))
(def ^:private record-fn  (delay (requiring-resolve 'scicloj.lalinea.tape/do-record!)))

(defn- tape-record!
  "Record to tape if active. No-op when tape is nil or inside a parent record."
  [op inputs result]
  (when (and @@tape-var (not @@inside-var))
    (@@record-fn op inputs result))
  result)

;; ---------------------------------------------------------------------------
;; Constructors
;; ---------------------------------------------------------------------------

(defn complex-tensor
  "Create a ComplexTensor.

   Arities:
     (complex-tensor tensor)       — wrap an existing tensor with last dim = 2
     (complex-tensor re-data im-data) — from separate real and imaginary parts

   re-data and im-data can be: double arrays, seqs, dtype readers, or tensors.
   They must have the same shape.

   When wrapping a 1-arg input whose last dimension is not 2 (e.g., a flat
   reader from dfn operations on ComplexTensors), it is reshaped to [n/2 2]."
  ([tensor-or-re]
   (let [t (cond
             (instance? ComplexTensor tensor-or-re)
             (.-tensor ^ComplexTensor tensor-or-re)
             :else
             (dtt/ensure-tensor (rt/ensure-tensor tensor-or-re)))
         shape (vec (dtype/shape t))
         result (if (= 2 (last shape))
                  (ComplexTensor. t)
                  ;; Flat input — reshape to [n/2, 2]
                  (let [n (long (reduce * shape))]
                    (when-not (even? n)
                      (throw (ex-info (str "Cannot interpret odd-length data as complex: " n)
                                      {:shape shape :n n})))
                    (ComplexTensor. (dtt/reshape t [(/ n 2) 2]))))]
     (tape-record! :cx/complex-tensor [tensor-or-re] result)))
  ([re-data im-data]
   (let [re-t (dtt/ensure-tensor (rt/ensure-tensor re-data))
         im-t (dtt/ensure-tensor (rt/ensure-tensor im-data))
         re-shape (vec (dtype/shape re-t))
         im-shape (vec (dtype/shape im-t))]
     (when-not (= re-shape im-shape)
       (throw (ex-info (str "Shape mismatch: re=" re-shape " im=" im-shape)
                       {:re-shape re-shape :im-shape im-shape})))
     (let [n (long (reduce * re-shape))
           re-flat (dtype/->reader (dtt/reshape re-t [n]))
           im-flat (dtype/->reader (dtt/reshape im-t [n]))
           interleaved (dtype/make-reader :float64 (* 2 n)
                                          (if (even? idx)
                                            (double (re-flat (quot idx 2)))
                                            (double (im-flat (quot idx 2)))))
           full-shape (clojure.core/conj re-shape 2)
           result (ComplexTensor. (dtt/reshape interleaved full-shape))]
       (tape-record! :cx/complex-tensor [re-data im-data] result)))))

(defn complex-tensor-real
  "Create a ComplexTensor from real data only (imaginary parts = 0)."
  [re-data]
  (let [re-t (dtt/ensure-tensor (rt/ensure-tensor re-data))
        re-shape (vec (dtype/shape re-t))
        n (long (reduce * re-shape))
        re-flat (dtype/->reader (dtt/reshape re-t [n]))
        interleaved (dtype/make-reader :float64 (* 2 n)
                                       (if (even? idx)
                                         (double (re-flat (quot idx 2)))
                                         0.0))
        full-shape (clojure.core/conj re-shape 2)
        result (ComplexTensor. (dtt/reshape interleaved full-shape))]
    (tape-record! :cx/complex-tensor-real [re-data] result)))

;; ---------------------------------------------------------------------------
;; Complex arithmetic
;; ---------------------------------------------------------------------------

(defn complex
  "Create a scalar ComplexTensor from real and imaginary parts."
  [re im]
  (let [arr (double-array 2)]
    (aset arr 0 (double re))
    (aset arr 1 (double im))
    (ComplexTensor. (dtt/ensure-tensor arr))))

(defn mul
  "Pointwise complex multiply: (a+bi)(c+di) = (ac-bd) + (ad+bc)i"
  [^ComplexTensor a ^ComplexTensor b]
  (let [result
        (if (and (scalar? a) (scalar? b))
          (let [ar (double (re a)) ai (double (im a))
                br (double (re b)) bi (double (im b))]
            (complex (- (* ar br) (* ai bi))
                     (+ (* ar bi) (* ai br))))
          (let [a-flat (dtype/->reader (->tensor a))
                b-flat (dtype/->reader (->tensor b))
                n (dtype/ecount (->tensor a))]
            (ComplexTensor.
             (dtt/reshape
              (dtype/make-reader :float64 n
                                 (let [base (-> idx (quot 2) (* 2))
                                       ar (double (a-flat base))
                                       ai (double (a-flat (unchecked-inc base)))
                                       br (double (b-flat base))
                                       bi (double (b-flat (unchecked-inc base)))]
                                   (if (even? idx)
                                     (- (* ar br) (* ai bi))
                                     (+ (* ar bi) (* ai br)))))
              (dtype/shape (->tensor a))))))]
    (tape-record! :cx/mul [a b] result)))

(defn conj
  "Complex conjugate: negate imaginary part."
  [^ComplexTensor ct]
  (let [t (->tensor ct)
        flat (dtype/->reader t)
        n (dtype/ecount t)
        result (ComplexTensor.
                (dtt/reshape
                 (dtype/make-reader :float64 n
                                    (let [v (double (flat idx))]
                                      (if (even? idx) v (- v))))
                 (dtype/shape t)))]
    (tape-record! :cx/conj [ct] result)))

(defn scale
  "Scale by a real scalar."
  [^ComplexTensor ct alpha]
  (let [t (->tensor ct)
        result (ComplexTensor. (dfn/* (double alpha) t))]
    (tape-record! :cx/scale [ct alpha] result)))

(defn abs
  "Element-wise complex magnitude: sqrt(re² + im²).
   Returns a RealTensor (or double for scalar)."
  [^ComplexTensor ct]
  (let [r (re ct) i (im ct)
        result (if (number? r)
                 (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i)))
                 (let [r (dtt/ensure-tensor r)
                       i (dtt/ensure-tensor i)]
                   (rt/->real-tensor (dfn/sqrt (dfn/+ (dfn/* r r) (dfn/* i i))))))]
    (tape-record! :cx/abs [ct] result)))

(defn dot
  "Complex dot product: Σ a_i * b_i.
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)
        result (complex (- (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
                        (+ (dfn/sum (dfn/* ar bi)) (dfn/sum (dfn/* ai br))))]
    (tape-record! :cx/dot [a b] result)))

(defn dot-conj
  "Hermitian inner product: Σ a_i * conj(b_i).
   Returns a scalar ComplexTensor."
  [^ComplexTensor a ^ComplexTensor b]
  (let [ar (re a) ai (im a)
        br (re b) bi (im b)
        result (complex (+ (dfn/sum (dfn/* ar br)) (dfn/sum (dfn/* ai bi)))
                        (- (dfn/sum (dfn/* ai br)) (dfn/sum (dfn/* ar bi))))]
    (tape-record! :cx/dot-conj [a b] result)))

(defn add
  "Pointwise complex addition."
  [^ComplexTensor a ^ComplexTensor b]
  (let [result
        (if (and (scalar? a) (scalar? b))
          (complex (+ (double (re a)) (double (re b)))
                   (+ (double (im a)) (double (im b))))
          (let [ta (->tensor a)]
            (ComplexTensor. (dfn/+ ta (->tensor b)))))]
    (tape-record! :cx/add [a b] result)))

(defn sub
  "Pointwise complex subtraction."
  [^ComplexTensor a ^ComplexTensor b]
  (let [result
        (if (and (scalar? a) (scalar? b))
          (complex (- (double (re a)) (double (re b)))
                   (- (double (im a)) (double (im b))))
          (let [ta (->tensor a)]
            (ComplexTensor. (dfn/- ta (->tensor b)))))]
    (tape-record! :cx/sub [a b] result)))

(defn sum
  "Complex-aware summation. Returns a scalar ComplexTensor."
  [^ComplexTensor ct]
  (let [result (complex (dfn/sum (re ct)) (dfn/sum (im ct)))]
    (tape-record! :cx/sum [ct] result)))

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
        (complex r i))

      ;; Vector: shape [n], data = [re + im i  re + im i ...]
      (= 1 ndims)
      (let [pairs (parse-complex-tokens data)
            re-arr (double-array (mapv first pairs))
            im-arr (double-array (mapv second pairs))]
        (complex-tensor (dtt/ensure-tensor re-arr)
                        (dtt/ensure-tensor im-arr)))

      ;; Matrix: shape [r c], data = [[row1-tokens] [row2-tokens] ...]
      :else
      (let [row-pairs (mapv parse-complex-tokens data)
            all-pairs (vec (apply concat row-pairs))
            re-arr (double-array (mapv first all-pairs))
            im-arr (double-array (mapv second all-pairs))]
        (complex-tensor (dtt/reshape (dtt/ensure-tensor re-arr) shape)
                        (dtt/reshape (dtt/ensure-tensor im-arr) shape))))))
