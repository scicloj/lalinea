;; # Computation tape

;; dtype-next's laziness is useful but opaque. When you chain
;; operations like `la/add`, `la/scale`, and `la/transpose`, it is
;; not obvious which results share memory, which are lazy, and where
;; copies happen. The tape namespace provides tools to answer these
;; questions.

;; ## Setup

(ns la-linea-book.computation-tape
  (:require [scicloj.la-linea.linalg :as la]
            [scicloj.la-linea.tape :as tape]
            [scicloj.la-linea.complex :as cx]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [tech.v3.tensor :as tensor]
            [scicloj.kindly.v4.kind :as kind])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## Inspecting memory status

;; `tape/memory-status` classifies a tensor's memory backing without
;; needing a tape. Three states:
;;
;; - `:contiguous` — backed by a `double[]`, fast, mutable
;; - `:strided` — shares a `double[]` but with reordered strides
;; - `:lazy` — no backing array, recomputes on each access

(def A (la/matrix [[1 2] [3 4]]))

(tape/memory-status A)

(kind/test-last
 [(fn [s] (= :contiguous s))])

;; `la/transpose` returns a strided view — same backing data,
;; different stride order.

(tape/memory-status (la/transpose A))

(kind/test-last
 [(fn [s] (= :strided s))])

;; `la/add` returns a lazy reader — no allocation, recomputes
;; on every access.

(def B (la/matrix [[5 6] [7 8]]))

(tape/memory-status (la/add A B))

(kind/test-last
 [(fn [s] (= :lazy s))])

;; `la/mmul` goes through EJML and returns a fresh contiguous tensor.

(tape/memory-status (la/mmul A B))

(kind/test-last
 [(fn [s] (= :contiguous s))])

;; ## Detecting shared memory

;; `tape/shares-memory?` checks whether two tensors share the same
;; backing `double[]`. This detects sharing through transpose, column
;; construction, and reshape.

(tape/shares-memory? A (la/transpose A))

(kind/test-last
 [(fn [b] (true? b))])

;; Independent allocations do not share memory.

(tape/shares-memory? A B)

(kind/test-last
 [(fn [b] (false? b))])

;; Lazy tensors have no backing array, so they do not share memory
;; with anything (even their inputs).

(tape/shares-memory? A (la/add A B))

(kind/test-last
 [(fn [b] (false? b))])

;; `la/column` on a `double[]` shares memory with it.

(def arr (double-array [10 20 30]))

(tape/shares-memory? (la/column arr) (tensor/ensure-tensor arr))

(kind/test-last
 [(fn [b] (true? b))])

;; ## Recording a computation tape

;; `tape/with-tape` records all `la/` operations within its scope.
;; It returns `{:result ... :entries ...}`.

(def tape-result
  (tape/with-tape
    (let [M (la/matrix [[1 2] [3 4]])
          S (la/scale M 2.0)
          I (la/eye 2)
          C (la/add S I)
          D (la/mmul C (la/transpose M))]
      D)))

(:result tape-result)

;; The tape captured 6 operations.

(count (:entries tape-result))

(kind/test-last
 [(fn [n] (= 6 n))])

;; Each entry records the operation, its inputs (linked by ID or
;; marked `:external`), and the output shape.

(mapv (fn [e] (select-keys e [:id :op :inputs :shape]))
      (:entries tape-result))

;; Inputs that went through `la/` have IDs; everything else
;; (scalars, raw data) is `:external`.

;; ## External inputs

;; The tape only tracks `la/` operations. Inputs that originate
;; outside La Linea appear as `{:external true}`.

;; ### Double arrays

;; A `double[]` passed to `la/column` is external — the tape
;; records `la/column` but not the array construction.

(def array-tape
  (tape/with-tape
    (let [v (la/column (double-array [1 2 3]))
          w (la/scale v 5.0)]
      w)))

(mapv (fn [e] (select-keys e [:id :op :inputs]))
      (:entries array-tape))

(kind/test-last
 [(fn [entries]
    (and (= :la/column (:op (first entries)))
         (= [{:external true}] (:inputs (first entries)))
         (= {:id "t1"} (first (:inputs (second entries))))))])

;; ### Clojure vectors and sequences

;; Clojure data structures are external too. `la/matrix` wraps
;; them into tensors — the tape records that wrapping.

(def seq-tape
  (tape/with-tape
    (let [M (la/matrix (for [i (range 3)]
                         (for [j (range 3)]
                           (* (inc i) (inc j)))))
          v (la/column (repeat 3 1.0))]
      (la/mmul M v))))

(mapv :op (:entries seq-tape))

(kind/test-last
 [(fn [ops] (= [:la/matrix :la/column :la/mmul] ops))])

;; ### dtype-next operations

;; `dfn/` operations are not `la/` functions, so the tape does not
;; record them. If a `dfn` result is fed into an `la/` function,
;; it appears as external.

(def dfn-tape
  (tape/with-tape
    (let [A (la/matrix [[1 2] [3 4]])
          doubled (dfn/* A 2.0)
          result (la/add (la/matrix doubled) A)]
      result)))

(mapv (fn [e] (select-keys e [:id :op :inputs]))
      (:entries dfn-tape))

;; `la/matrix` wraps the `dfn/*` result. The `dfn/*` output is
;; external to the tape — but `la/matrix` and the subsequent
;; `la/add` are tracked.

(kind/test-last
 [(fn [entries]
    (= [:la/matrix :la/matrix :la/add]
       (mapv :op entries)))])

;; ### EJML structures

;; EJML's `DMatrixRMaj` can be converted to a tensor via
;; `la/dmat->tensor`. The conversion itself is not instrumented
;; (it is a low-level interop function), so the resulting tensor
;; is external to the tape.

(def ejml-tape
  (tape/with-tape
    (let [dm (doto (DMatrixRMaj. 2 2)
               (.setData (double-array [1 0 0 1])))
          I  (la/dmat->tensor dm)
          result (la/add (la/matrix [[5 6] [7 8]]) I)]
      result)))

(mapv (fn [e] (select-keys e [:id :op :inputs]))
      (:entries ejml-tape))

;; The tensor from `dmat->tensor` enters the tape as external input
;; to `la/add`.

(kind/test-last
 [(fn [entries]
    (and (= [:la/matrix :la/add] (mapv :op entries))
         (= {:external true} (second (:inputs (second entries))))))])

;; ### Complex tensors

;; Complex operations are tracked too. `cx/complex-tensor` is not
;; an `la/` function, but `la/add` on complex inputs is recorded.

(def complex-tape
  (tape/with-tape
    (let [z1 (cx/complex-tensor (la/matrix [[1 0] [0 1]]))
          z2 (cx/complex-tensor (la/matrix [[0 1] [1 0]]))
          s  (la/add z1 z2)]
      s)))

(:complex? (last (:entries complex-tape)))

(kind/test-last
 [(fn [c?] (true? c?))])

;; The matrices inside the ComplexTensors were built with `la/matrix`,
;; so they are on the tape. The ComplexTensor wrapping is external.
;; The `la/add` is recorded with the ComplexTensors as external inputs,
;; since `cx/complex-tensor` is not an `la/` function.

(mapv :op (:entries complex-tape))

(kind/test-last
 [(fn [ops] (= [:la/matrix :la/matrix :la/add] ops))])

;; ## Tape summary

;; `tape/summary` aggregates statistics about the tape:
;; operation counts and memory status breakdown.

(tape/summary tape-result)

;; The summary shows:
;;
;; - `:by-op` — one of each operation
;; - `:by-memory` — which operations are lazy, shared, or independent

;; ## Origin DAG

;; `tape/origin` walks the tape backwards from a value
;; to reconstruct its computation DAG.

(tape/origin tape-result (:result tape-result))

;; The DAG shows that `mmul` depends on `add` and `transpose`,
;; which both trace back to the original `matrix`. The shared
;; reference to `matrix` appears as `{:ref "t1"}` in the
;; transpose branch — a proper DAG, not a tree.

;; ## Mermaid visualization

;; `tape/mermaid` renders the DAG as a Mermaid flowchart.
;; Memory status annotations:
;;
;; - `~` lazy
;; - `=` shared (same backing array as an input)
;; - `+` independent (fresh allocation)

(kind/mermaid (tape/mermaid tape-result (:result tape-result)))

;; ## Practical example: tracing a pipeline

;; A common question: "after this chain of operations, what is
;; materialized and what is lazy?"

(def pipeline-result
  (tape/with-tape
    (let [data (la/matrix [[1 0 2]
                           [0 3 0]
                           [4 0 5]])
          centered (la/sub data (la/scale (la/matrix [[1 1 1]
                                                      [1 1 1]
                                                      [1 1 1]])
                                          (/ (double (la/trace data)) 3.0)))
          {:keys [U S Vt]} (la/svd centered)
          projection (la/mmul (la/transpose Vt) (la/column [1 0 0]))]
      projection)))

(tape/summary pipeline-result)

;; The summary reveals the mix of lazy intermediate operations
;; and EJML-backed materializations.

(kind/mermaid (tape/mermaid pipeline-result (:result pipeline-result)))
