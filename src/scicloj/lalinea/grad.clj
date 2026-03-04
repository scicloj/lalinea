(ns scicloj.lalinea.grad
  "Reverse-mode automatic differentiation on the computation tape.

   Provides VJP (vector-Jacobian product) rules for la/ operations
   and a `grad` function that walks the tape backwards to compute
   gradients of a scalar output with respect to specified inputs.

   Lives in a separate namespace to avoid circular dependencies:
   linalg requires tape requires complex, and grad needs both tape
   and linalg."
  (:require [scicloj.lalinea.linalg :as la]
            [scicloj.lalinea.tensor :as t]
            [scicloj.lalinea.tape :as tape]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.tensor :as dtt])
  (:import [java.util IdentityHashMap]))

;; -----------------------------------------------------------
;; VJP rules
;; -----------------------------------------------------------

(def ^:private vjp-rules
  "Op keyword -> fn of [adjoint, inputs, output] -> vector of
   input adjoints (nil for non-differentiable inputs)."
  {:la/add
   (fn [g [_a _b] _out] [g g])

   :la/sub
   (fn [g [_a _b] _out] [g (dfn/* -1.0 g)])

   :la/scale
   (fn [g [_a alpha] _out]
     [(dfn/* g (double alpha)) nil])

   :la/mmul
   (fn [g [a b] _out]
     [(la/mmul g (la/transpose b))
      (la/mmul (la/transpose a) g)])

   :la/transpose
   (fn [g [_a] _out]
     [(la/transpose g)])

   :el/mul
   (fn [g [a b] _out]
     [(dfn/* g b) (dfn/* g a)])

   :la/trace
   (fn [g [a] _out]
     (let [n (first (dtype/shape a))]
       [(la/scale (t/eye n) (double g))]))

   :el/sq
   (fn [g [a] _out]
     [(dfn/* g (dfn/* 2.0 a))])

   :el/sum
   (fn [g [a] _out]
     (let [shape (dtype/shape a)]
       [(dtype/clone
         (dtt/compute-tensor
          shape
          (fn [& _] (double g))
          :float64))]))

   :la/det
   (fn [g [a] out]
     [(la/scale (la/transpose (la/invert a))
                (* (double g) (double out)))])

   :la/invert
   (fn [g [_a] out]
     (let [inv-t (la/transpose out)]
       [(la/scale (la/mmul inv-t (la/mmul g inv-t))
                  -1.0)]))

   :la/norm
   (fn [g [a] out]
     [(la/scale a (/ (double g) (double out)))])

   :la/dot
   (fn [g [u v] _out]
     [(dfn/* (double g) v)
      (dfn/* (double g) u)])})

;; -----------------------------------------------------------
;; Gradient accumulation
;; -----------------------------------------------------------

(defn- add-grad
  "Accumulate gradient: existing + new.
   Materializes lazy tensors to avoid deep nesting."
  [existing new-grad]
  (let [new-grad (rt/ensure-tensor new-grad)]
    (if (nil? existing)
      (if (dtt/tensor? new-grad)
        (dtype/clone new-grad)
        new-grad)
      (dtype/clone (dfn/+ existing new-grad)))))

;; -----------------------------------------------------------
;; Backward pass
;; -----------------------------------------------------------

(defn- compute-adjoints
  "Run the backward pass over tape entries, returning an
   IdentityHashMap from each external input tensor to its
   gradient (as a RealTensor)."
  [tape-result target]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries  (:entries tape-result)
        ;; idx: tape-produced ids — used later to
        ;; distinguish external inputs from intermediates
        idx       (into {} (map (juxt :id identity))
                        entries)
        target-id (.get registry target)]
    (when (nil? target-id)
      (throw (ex-info "Target not found on tape" {})))
    ;; Backward pass: walk the tape in reverse,
    ;; propagating cotangents via VJP rules.
    (let [adjoints (java.util.HashMap.)]
      ;; Seed: d(target)/d(target) = 1
      (.put adjoints target-id 1.0)
      (doseq [entry (rseq entries)]
        (let [eid (:id entry)
              g   (.get adjoints eid)]
          ;; Only process entries that received
          ;; a gradient from downstream.
          (when (some? g)
            (when-let [rule (get vjp-rules (:op entry))]
              (let [ins  (mapv rt/ensure-tensor
                               (:input-tensors entry))
                    refs (:inputs entry)
                    g*   (rt/ensure-tensor g)
                    out  (rt/ensure-tensor
                          (:output entry))
                    igs  (rule g* ins out)]
                ;; Distribute to each input (additive —
                ;; a value used by multiple ops receives
                ;; contributions from each).
                (dotimes [i (count refs)]
                  (when-let [ig (nth igs i nil)]
                    (when-let [rid (:id (nth refs i))]
                      (.put adjoints rid
                            (add-grad
                             (.get adjoints rid)
                             ig))))))))))
      ;; Collect: return gradients for external inputs
      ;; only (ids starting with "x", not in the tape).
      (let [result (IdentityHashMap.)]
        (doseq [^java.util.Map$Entry me
                (.entrySet registry)]
          (let [obj    (.getKey me)
                ^String id (str (.getValue me))]
            (when (and (.startsWith id "x")
                       (not (contains? idx id)))
              (when-let [g (.get adjoints id)]
                (.put result obj (rt/->rt g))))))
        result))))

;; -----------------------------------------------------------
;; grad
;; -----------------------------------------------------------

(defn grad
  "Gradient of a scalar w.r.t. one or more inputs.

   `tape-result` — map returned by `tape/with-tape`
   `target`      — scalar output (must be on the tape)
   `wrt`         — input tensor, or vector of input tensors

   For a single input, returns its gradient tensor directly.
   For a vector of inputs, returns a map `{input gradient}`.

   Example:
   ```clojure
   (let [A (t/matrix [[1 2] [3 4]])
         tape-result (tape/with-tape
                       (la/trace (la/mmul A A)))]
     (grad/grad tape-result (:result tape-result) A))
   ```"
  [tape-result target wrt]
  (let [^IdentityHashMap adjoints
        (compute-adjoints tape-result target)]
    (if (vector? wrt)
      (into {}
            (map (fn [input]
                   [input (.get adjoints input)]))
            wrt)
      (.get adjoints wrt))))
