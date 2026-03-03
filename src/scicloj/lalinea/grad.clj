(ns scicloj.lalinea.grad
  "Reverse-mode automatic differentiation on the computation tape.

   Provides VJP (vector-Jacobian product) rules for la/ operations
   and a `grad` function that walks the tape backwards to compute
   gradients of a scalar output with respect to external inputs.

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

;; ---------------------------------------------------------------------------
;; VJP rules
;; ---------------------------------------------------------------------------

(defn- ensure-tensor
  "Unwrap RealTensor to bare tensor; pass through everything else."
  [x]
  (if (rt/real-tensor? x) (rt/->tensor x) x))

(defn- ->rt
  "Wrap a bare tensor in RealTensor. Returns scalars/nil as-is."
  [t]
  (if (or (nil? t) (number? t))
    t
    (rt/->real-tensor t)))

(def ^:private vjp-rules
  "VJP rules: op keyword -> fn of [adjoint, inputs, output] -> vector of
   input adjoints (nil for non-differentiable inputs like scalars)."
  {:la/add       (fn [g [_a _b] _out] [g g])
   :la/sub       (fn [g [_a _b] _out] [g (dfn/* -1.0 g)])
   :la/scale     (fn [g [_a alpha] _out]
                   [(dfn/* g (double alpha))
                    nil])
   :la/mmul      (fn [g [a b] _out]
                   [(la/mmul g (la/transpose b))
                    (la/mmul (la/transpose a) g)])
   :la/transpose (fn [g [_a] _out]
                   [(la/transpose g)])
   :la/mul       (fn [g [a b] _out]
                   [(dfn/* g b)
                    (dfn/* g a)])
   :la/trace     (fn [g [a] _out]
                   (let [n (first (dtype/shape a))]
                     [(la/scale (t/eye n) (double g))]))
   :la/sq        (fn [g [a] _out]
                   [(dfn/* g (dfn/* 2.0 a))])
   :la/sum       (fn [g [a] _out]
                   (let [shape (dtype/shape a)]
                     [(dtype/clone
                       (dtt/compute-tensor
                        shape
                        (fn [& _] (double g))
                        :float64))]))
   :la/det       (fn [g [a] out]
                   [(la/scale (la/transpose (la/invert a))
                              (* (double g) (double out)))])
   :la/invert    (fn [g [_a] out]
                   (let [inv-t (la/transpose out)]
                     [(la/scale (la/mmul inv-t (la/mmul g inv-t)) -1.0)]))
   :la/norm      (fn [g [a] out]
                   [(la/scale a (/ (double g) (double out)))])
   :la/dot       (fn [g [u v] _out]
                   [(dfn/* (double g) v)
                    (dfn/* (double g) u)])})

;; ---------------------------------------------------------------------------
;; Gradient accumulation
;; ---------------------------------------------------------------------------

(defn- add-grad
  "Accumulate gradient: existing + new. If existing is nil, returns new.
   Materializes lazy tensors to avoid deep nesting."
  [existing new-grad]
  (let [new-grad (ensure-tensor new-grad)]
    (if (nil? existing)
      (if (dtt/tensor? new-grad)
        (dtype/clone new-grad)
        new-grad)
      (dtype/clone (dfn/+ existing new-grad)))))

;; ---------------------------------------------------------------------------
;; grad
;; ---------------------------------------------------------------------------

(defn grad
  "Compute gradients of a scalar value with respect to external inputs.

   `tape-result` — the map returned by `tape/with-tape`
   `target`      — the scalar output value (must be tracked on the tape)

   Returns an IdentityHashMap from input tensors to their gradients.

   Example:
   ```clojure
   (let [A (t/matrix [[1 2] [3 4]])
         b (t/column [5 6])
         tape-result (tape/with-tape
                       (la/sum (la/sq (la/sub (la/mmul A A) b))))
         grads (grad/grad tape-result (:result tape-result))]
     (.get grads A))
   ```"
  [tape-result target]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries (:entries tape-result)
        idx (into {} (map (juxt :id identity)) entries)
        target-id (.get registry target)]
    (when (nil? target-id)
      (throw (ex-info "Target value not found on tape" {})))
    (let [adjoints (java.util.HashMap.)
          _ (.put adjoints target-id 1.0)
          rev-entries (rseq (vec entries))]
      (doseq [entry rev-entries]
        (let [entry-id (:id entry)
              g (.get adjoints entry-id)]
          (when (some? g)
            (when-let [rule (get vjp-rules (:op entry))]
              (let [input-tensors (mapv ensure-tensor (:input-tensors entry))
                    input-refs (:inputs entry)
                    input-grads (rule (ensure-tensor g) input-tensors (ensure-tensor (:output entry)))]
                (dotimes [i (count input-refs)]
                  (when-let [ig (nth input-grads i nil)]
                    (when-let [ref-id (:id (nth input-refs i))]
                      (.put adjoints ref-id
                            (add-grad (.get adjoints ref-id) ig))))))))))
      (let [result (IdentityHashMap.)]
        (doseq [^java.util.Map$Entry me (.entrySet registry)]
          (let [tensor-obj (.getKey me)
                ^String id (str (.getValue me))]
            (when (and (.startsWith id "x")
                       (not (contains? idx id)))
              (when-let [g (.get adjoints id)]
                (.put result tensor-obj (->rt g))))))
        result))))
