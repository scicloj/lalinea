(ns scicloj.la-linea.grad
  "Reverse-mode automatic differentiation on the computation tape.

   Provides VJP (vector-Jacobian product) rules for la/ operations
   and a `grad` function that walks the tape backwards to compute
   gradients of a scalar output with respect to external inputs.

   Lives in a separate namespace to avoid circular dependencies:
   linalg requires tape requires complex, and grad needs both tape
   and linalg."
  (:require [scicloj.la-linea.linalg :as la]
            [scicloj.la-linea.tape :as tape]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.tensor :as tensor])
  (:import [java.util IdentityHashMap]))

;; ---------------------------------------------------------------------------
;; VJP rules
;; ---------------------------------------------------------------------------

(def ^:private vjp-rules
  "VJP rules: op keyword -> fn of [adjoint, inputs, output] -> vector of
   input adjoints (nil for non-differentiable inputs like scalars)."
  {:la/add       (fn [g [_a _b] _out] [g g])
   :la/sub       (fn [g [_a _b] _out] [g (tensor/reshape (dfn/* -1.0 g) (dtype/shape g))])
   :la/scale     (fn [g [_a alpha] _out]
                   [(tensor/reshape (dfn/* g (double alpha)) (dtype/shape g))
                    nil])
   :la/mmul      (fn [g [a b] _out]
                   [(la/mmul g (la/transpose b))
                    (la/mmul (la/transpose a) g)])
   :la/transpose (fn [g [_a] _out]
                   [(la/transpose g)])
   :la/mul       (fn [g [a b] _out]
                   [(tensor/reshape (dfn/* g b) (dtype/shape g))
                    (tensor/reshape (dfn/* g a) (dtype/shape g))])
   :la/trace     (fn [g [a] _out]
                   (let [n (first (dtype/shape a))]
                     [(la/scale (la/eye n) (double g))]))
   :la/sq        (fn [g [a] _out]
                   [(tensor/reshape (dfn/* g (dfn/* 2.0 a)) (dtype/shape g))])
   :la/sum       (fn [g [a] _out]
                   (let [shape (dtype/shape a)]
                     [(dtype/clone
                       (tensor/compute-tensor
                        shape
                        (fn [& _] (double g))
                        :float64))]))})

;; ---------------------------------------------------------------------------
;; Gradient accumulation
;; ---------------------------------------------------------------------------

(defn- add-grad
  "Accumulate gradient: existing + new. If existing is nil, returns new.
   Materializes lazy tensors to avoid deep nesting."
  [existing new-grad]
  (if (nil? existing)
    (if (tensor/tensor? new-grad)
      (dtype/clone new-grad)
      new-grad)
    (dtype/clone (tensor/reshape (dfn/+ existing new-grad) (dtype/shape existing)))))

;; ---------------------------------------------------------------------------
;; grad
;; ---------------------------------------------------------------------------

(defn grad
  "Compute gradients of a scalar value with respect to external inputs.

   `tape-result` — the map returned by `tape/with-tape`
   `target`      — the scalar output value (must be tracked on the tape)

   Returns an IdentityHashMap from input tensors to their gradients.
   Keys are identity-matched to the original input tensors.

   Only external inputs (tensors not produced by a recorded op)
   appear in the result.

   Example:
   ```clojure
   (let [A (la/matrix [[1 2] [3 4]])
         b (la/column [5 6])
         tape-result (tape/with-tape
                       (la/sum (la/sq (la/sub (la/mmul A A) b))))
         grads (grad/grad tape-result (:result tape-result))]
     (.get grads A))  ;; gradient of loss w.r.t. A
   ```"
  [tape-result target]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries (:entries tape-result)
        idx (into {} (map (juxt :id identity)) entries)
        target-id (.get registry target)]
    (when (nil? target-id)
      (throw (ex-info "Target value not found on tape" {})))
    (let [;; Adjoint map: entry-id -> gradient
          adjoints (java.util.HashMap.)
          _ (.put adjoints target-id 1.0)
          ;; Walk entries in reverse (tape order = topological order)
          rev-entries (rseq (vec entries))]
      (doseq [entry rev-entries]
        (let [entry-id (:id entry)
              g (.get adjoints entry-id)]
          (when (some? g)
            (when-let [rule (get vjp-rules (:op entry))]
              (let [input-tensors (:input-tensors entry)
                    input-refs (:inputs entry)
                    input-grads (rule g input-tensors (:output entry))]
                (dotimes [i (count input-refs)]
                  (when-let [ig (nth input-grads i nil)]
                    (when-let [ref-id (:id (nth input-refs i))]
                      (.put adjoints ref-id
                            (add-grad (.get adjoints ref-id) ig))))))))))
      ;; Collect gradients for external inputs.
      ;; External tensors have x-prefixed IDs and no tape entry.
      (let [result (IdentityHashMap.)]
        (doseq [^java.util.Map$Entry me (.entrySet registry)]
          (let [tensor-obj (.getKey me)
                ^String id (str (.getValue me))]
            (when (and (.startsWith id "x")
                       (not (contains? idx id)))
              (when-let [g (.get adjoints id)]
                (.put result tensor-obj g)))))
        result))))
