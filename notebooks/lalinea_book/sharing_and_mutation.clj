;; # Sharing and Mutation
;;
;; dtype-next and La Linea follow a **functional, immutable-by-convention**
;; philosophy. Operations return lazy views and [zero-copy](https://en.wikipedia.org/wiki/Zero-copy) wrappers
;; rather than defensive copies. This is efficient — but it means
;; that multiple objects can share the same backing `double[]`.
;;
;; If you mutate through one handle, the change is visible through
;; every other handle that shares that memory. This notebook
;; demonstrates exactly when sharing happens and when it doesn't,
;; so you can make informed choices.
;;
;; The rule of thumb: **if you didn't call `t/clone`, you might
;; be sharing memory.**

(ns lalinea-book.sharing-and-mutation
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   ;; Complex tensors — interleaved [re im] layout:
   ;; Low-level tensor operations for memory demos:
   [tech.v3.tensor :as dtt]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## Tensors share memory across reshapes
;;
;; `t/reshape` is zero-copy — the new tensor wraps the same
;; flat `double[]` in a different shape. We use `dtt/ensure-tensor`
;; (from dtype-next) to wrap a raw array as a tensor without copying —
;; La Linea's `t/matrix` always copies, so we drop to the underlying
;; library here.

(let [flat (double-array [1 2 3 4 5 6])
      t1 (t/reshape (dtt/ensure-tensor flat) [2 3])
      t2 (t/reshape (dtt/ensure-tensor flat) [3 2])
      _ (aset flat 0 99.0)
      result {:t1-00 (t1 0 0)
              :t2-00 (t2 0 0)}]
  result)

(kind/test-last
 [(fn [{:keys [t1-00 t2-00]}]
    (and (== 99.0 t1-00)
         (== 99.0 t2-00)))])

;; Both tensors saw the mutation through the shared `double[]`.

^:kindly/hide-code
(kind/mermaid "graph TD
  flat[\"flat = double-array\"] --> backing[\"backing array\n1 2 3 4 5 6\"]
  t1[\"t1 = tensor 2x3\"] --> backing
  t2[\"t2 = tensor 3x2\"] --> backing
  style backing fill:#fff3cd,stroke:#856404")

;;
;; The idiomatic way — mutate through the tensor directly:

(let [flat (double-array [1 2 3 4 5 6])
      t1 (t/reshape (dtt/ensure-tensor flat) [2 3])
      t2 (t/reshape (dtt/ensure-tensor flat) [3 2])]
  (t/mset! t1 0 0 99.0)
  {:t1-00 (t1 0 0)
   :t2-00 (t2 0 0)})

(kind/test-last
 [(fn [{:keys [t1-00 t2-00]}]
    (and (== 99.0 t1-00)
         (== 99.0 t2-00)))])

;; ## t/select creates strided views

;; Selecting a row or column returns a **view** backed by the
;; same array, with a stride that skips over elements.

(let [A (t/matrix [[10 20 30]
                   [40 50 60]])
      row0 (t/select A 0 :all)
      arr (t/->double-array A)
      _ (aset arr 0 999.0)
      result {:A-00 (A 0 0)
              :row0-0 (double (row0 0))}]
  result)

(kind/test-last
 [(fn [{:keys [A-00 row0-0]}]
    (and (== 999.0 A-00)
         (== 999.0 row0-0)))])

;; The row view saw the same mutation. Using `t/mset!`:

(let [A (t/matrix [[10 20 30]
                   [40 50 60]])
      row0 (t/select A 0 :all)]
  (t/mset! A 0 0 999.0)
  {:A-00 (A 0 0)
   :row0-0 (double (row0 0))})

(kind/test-last
 [(fn [{:keys [A-00 row0-0]}]
    (and (== 999.0 A-00)
         (== 999.0 row0-0)))])

;; ## Tensor ↔ DMatrixRMaj: zero-copy both ways

;; `tensor->dmat` and `dmat->tensor` share the same `double[]`.
;; This is how La Linea achieves zero-overhead interop with EJML.

(let [M (t/matrix [[1 2] [3 4]])
      dm (t/tensor->dmat M)]
  (identical? (t/->double-array M)
              (.data dm)))

(kind/test-last [true?])

;; Mutating the DMatrixRMaj is visible in the tensor:

(let [M (t/matrix [[1 2] [3 4]])
      dm (t/tensor->dmat M)
      _ (.set dm 0 0 -1.0)]
  {:M-00 (M 0 0)
   :dm-00 (.get dm 0 0)})

(kind/test-last
 [(fn [{:keys [M-00 dm-00]}]
    (and (== -1.0 M-00)
         (== -1.0 dm-00)))])

;; And the other direction — mutating the tensor is visible in EJML:

(let [M (t/matrix [[1 2] [3 4]])
      dm (t/tensor->dmat M)]
  (t/mset! M 1 1 99.0)
  (.get dm 1 1))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## Extracting the backing double[]

;; `t/->double-array` is the idiomatic way to get a `double[]`
;; from a tensor. It is **zero-copy when possible, copying only
;; when necessary**:
;;
;; - Contiguous, full-array-backed tensor → returns the same `double[]`
;; - Subview or lazy tensor → allocates and copies

;; A matrix built with `t/matrix` is backed by a contiguous array.
;; `t/->double-array` returns the same object — zero-copy:

(let [M (t/matrix [[1 2] [3 4]])
      arr (t/->double-array M)]
  (aset arr 0 99.0)
  (M 0 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; A row selected with `t/select` is a strided view —
;; contiguous within the parent array, but not spanning all of it.
;; `t/->double-array` correctly returns a copy:

(let [M (t/matrix [[1 2 3] [4 5 6]])
      row0 (t/select M 0 :all)
      arr (t/->double-array row0)]
  {:length (alength arr)
   :values (seq arr)
   :shares-memory? (identical? arr (t/->double-array M))})

(kind/test-last
 [(fn [{:keys [length values shares-memory?]}]
    (and (== 3 length)
         (= [1.0 2.0 3.0] values)
         (not shares-memory?)))])

;; A lazy tensor (from `la/add` or `t/compute-tensor`) has
;; no backing array at all — `t/->double-array` allocates one:

(let [a (t/matrix [[1 2] [3 4]])
      b (t/matrix [[10 20] [30 40]])
      lazy-sum (la/add a b)
      arr (t/->double-array lazy-sum)]
  {:values (seq arr)
   :has-array-buffer? (some? (t/array-buffer lazy-sum))})

(kind/test-last
 [(fn [{:keys [values has-array-buffer?]}]
    (and (= [11.0 22.0 33.0 44.0] values)
         (not has-array-buffer?)))])

;; `t/->double-array` follows the same convention for
;; ComplexTensors — it delegates to `t/->double-array`
;; on the underlying `[... 2]` tensor:

(let [ct (t/complex-tensor
          (t/matrix [[1 2] [3 4] [5 6]]))
      arr (t/->double-array ct)]
  (aset arr 0 99.0)
  (la/re (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## ComplexTensor wraps a real tensor

;; A ComplexTensor wraps an `[... 2]` real tensor. The `t/->tensor`
;; accessor exposes the backing tensor, and they share memory.

(let [ct-data (t/matrix [[1 2] [3 4] [5 6]])
      ct (t/complex-tensor ct-data)]
  (identical? (t/->tensor ct-data) (t/->tensor ct)))

(kind/test-last [true?])

;; Mutating through the backing tensor changes the ComplexTensor:

(let [ct-data (t/matrix [[1 2] [3 4] [5 6]])
      ct (t/complex-tensor ct-data)
      arr (t/->double-array ct-data)
      _ (aset arr 1 99.0)]
  (la/im (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; The imaginary part of the first element changed to 99.
;; Using `t/mset!` on the backing tensor:

(let [ct-data (t/matrix [[1 2] [3 4] [5 6]])
      ct (t/complex-tensor ct-data)]
  (t/mset! ct-data 0 1 99.0)
  (la/im (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## re and im are strided views

;; `la/re` and `la/im` return views into the interleaved layout.
;; They share the same backing memory.

(let [ct (t/complex-tensor
          (t/matrix [[10 40] [20 50] [30 60]]))
      re-view (la/re ct)
      arr (t/->double-array (t/->tensor ct))
      _ (aset arr 0 -10.0)]
  (double (re-view 0)))

(kind/test-last [(fn [v] (== -10.0 v))])

;; Mutating the backing array was immediately visible in the `re` view.
;; Using `t/mset!`:

(let [ct (t/complex-tensor
          (t/matrix [[10 40] [20 50] [30 60]]))
      re-view (la/re ct)]
  (t/mset! (t/->tensor ct) 0 0 -10.0)
  (double (re-view 0)))

(kind/test-last [(fn [v] (== -10.0 v))])

;; ## Lazy operations: no new memory, no new mutation handle

;; `la/add`, `la/mul`, etc. return **lazy noncaching readers**.
;; They allocate no new memory — they recompute on every access,
;; reading through to the original source buffers. This means
;; they don't create a new mutable handle, but they still
;; depend on the source data.

(let [x (t/matrix [1 2 3])
      y (t/matrix [10 20 30])
      lazy-sum (la/add x y)]
  (seq lazy-sum))

(kind/test-last [(fn [v] (= [11.0 22.0 33.0] v))])

;; Mutating `x` changes what `lazy-sum` computes — it reads from `x`
;; on every access:

(let [x (t/matrix [1 2 3])
      y (t/matrix [10 20 30])
      lazy-sum (la/add x y)
      arr (t/->double-array x)
      _ (aset arr 0 100.0)]
  (seq lazy-sum))

(kind/test-last [(fn [v] (= [110.0 22.0 33.0] v))])

;; A lazy reader doesn't have its own array to mutate.
;; It always reads through to the source. Using `t/mset!`:

(let [x (t/matrix [1 2 3])
      y (t/matrix [10 20 30])
      lazy-sum (la/add x y)]
  (t/mset! x 0 100.0)
  (seq lazy-sum))

(kind/test-last [(fn [v] (= [110.0 22.0 33.0] v))])

;; ## Complex arithmetic: lazy results

;; `la/add`, `la/sub`, `la/scale` return lazy ComplexTensors.
;; Like `la/add`, they read through to the source on every access.

(let [ca (t/complex-tensor
          (t/matrix [[1 3] [2 4]]))
      cb (t/complex-tensor
          (t/matrix [[10 30] [20 40]]))
      lazy-sum (la/add ca cb)]
  {:re (seq (la/re lazy-sum))
   :im (seq (la/im lazy-sum))})

(kind/test-last
 [(fn [{:keys [re im]}]
    (and (= [11.0 22.0] re)
         (= [33.0 44.0] im)))])

;; Mutating `ca`'s backing array propagates through the lazy result:

(let [ca (t/complex-tensor
          (t/matrix [[1 3] [2 4]]))
      cb (t/complex-tensor
          (t/matrix [[10 30] [20 40]]))
      lazy-sum (la/add ca cb)
      arr (t/->double-array (t/->tensor ca))
      _ (aset arr 0 100.0)]
  (seq (la/re lazy-sum)))

(kind/test-last [(fn [v] (= [110.0 22.0] v))])

;; Using `t/mset!`:

(let [ca (t/complex-tensor
          (t/matrix [[1 3] [2 4]]))
      cb (t/complex-tensor
          (t/matrix [[10 30] [20 40]]))
      lazy-sum (la/add ca cb)]
  (t/mset! (t/->tensor ca) 0 0 100.0)
  (seq (la/re lazy-sum)))

(kind/test-last [(fn [v] (= [110.0 22.0] v))])

;; ## t/clone breaks sharing

;; `t/clone` is the standard way to get an independent copy.
;; After cloning, the two objects have separate backing arrays.

(let [original (t/matrix [[1 2] [3 4]])
      cloned (t/clone original)]
  (identical? (t/->double-array original)
              (t/->double-array cloned)))

(kind/test-last [false?])

;; Mutating the original does not affect the clone:

(let [original (t/matrix [[1 2] [3 4]])
      cloned (t/clone original)
      arr (t/->double-array original)
      _ (aset arr 0 -999.0)]
  {:original-00 (original 0 0)
   :cloned-00 (cloned 0 0)})

(kind/test-last
 [(fn [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00)
         (== 1.0 cloned-00)))])

;; Using `t/mset!`:

(let [original (t/matrix [[1 2] [3 4]])
      cloned (t/clone original)]
  (t/mset! original 0 0 -999.0)
  {:original-00 (original 0 0)
   :cloned-00 (cloned 0 0)})

(kind/test-last
 [(fn [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00)
         (== 1.0 cloned-00)))])

;; ## t/clone on ComplexTensors

;; Cloning a ComplexTensor produces an independent ComplexTensor
;; with its own backing array.

(let [ct-orig (t/complex-tensor
               (t/matrix [[1 4] [2 5] [3 6]]))
      ct-clone (t/clone ct-orig)
      orig-arr (t/->double-array (t/->tensor ct-orig))
      _ (aset orig-arr 0 -1.0)]
  {:orig-re (la/re (ct-orig 0))
   :clone-re (la/re (ct-clone 0))})

(kind/test-last
 [(fn [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re)
         (== 1.0 clone-re)))])

;; Using `t/mset!`:

(let [ct-orig (t/complex-tensor
               (t/matrix [[1 4] [2 5] [3 6]]))
      ct-clone (t/clone ct-orig)]
  (t/mset! (t/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (la/re (ct-orig 0))
   :clone-re (la/re (ct-clone 0))})

(kind/test-last
 [(fn [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re)
         (== 1.0 clone-re)))])

;; ## Clone also materializes lazy results

;; Cloning a lazy ComplexTensor (from `la/add`, `la/scale`, etc.)
;; materializes it into a contiguous array. The result is independent
;; of the sources.

(let [p (t/complex-tensor
         (t/matrix [[1 3] [2 4]]))
      q (t/complex-tensor
         (t/matrix [[10 30] [20 40]]))
      lazy-pq (la/add p q)
      materialized-pq (t/clone lazy-pq)]
  (some? (t/array-buffer (t/->tensor materialized-pq))))

(kind/test-last [true?])

;; Mutating `p` affects the lazy result but not the materialized copy:

(let [p (t/complex-tensor
         (t/matrix [[1 3] [2 4]]))
      q (t/complex-tensor
         (t/matrix [[10 30] [20 40]]))
      lazy-pq (la/add p q)
      materialized-pq (t/clone lazy-pq)
      arr (t/->double-array (t/->tensor p))
      _ (aset arr 0 999.0)]
  {:lazy-re (seq (la/re lazy-pq))
   :materialized-re (seq (la/re materialized-pq))})

(kind/test-last
 [(fn [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re)
         (= [11.0 22.0] materialized-re)))])

;; Using `t/mset!`:

(let [p (t/complex-tensor
         (t/matrix [[1 3] [2 4]]))
      q (t/complex-tensor
         (t/matrix [[10 30] [20 40]]))
      lazy-pq (la/add p q)
      materialized-pq (t/clone lazy-pq)]
  (t/mset! (t/->tensor p) 0 0 999.0)
  {:lazy-re (seq (la/re lazy-pq))
   :materialized-re (seq (la/re materialized-pq))})

(kind/test-last
 [(fn [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re)
         (= [11.0 22.0] materialized-re)))])

;; ## t/submatrix clones

;; `t/submatrix` always returns a contiguous, independent copy.
;; This is necessary because `t/select` returns non-contiguous
;; views that EJML cannot use directly.

(let [big (t/matrix [[1 2 3] [4 5 6] [7 8 9]])
      sub (t/submatrix big (range 2) (range 2))
      arr (t/->double-array big)
      _ (aset arr 0 -1.0)]
  {:big-00 (big 0 0)
   :sub-00 (sub 0 0)})

(kind/test-last
 [(fn [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00)
         (== 1.0 sub-00)))])

;; Using `t/mset!`:

(let [big (t/matrix [[1 2 3] [4 5 6] [7 8 9]])
      sub (t/submatrix big (range 2) (range 2))]
  (t/mset! big 0 0 -1.0)
  {:big-00 (big 0 0)
   :sub-00 (sub 0 0)})

(kind/test-last
 [(fn [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00)
         (== 1.0 sub-00)))])

;; ## t/column and t/row wrap without copying

;; `t/column` and `t/row` wrap their input as a `[n 1]` or `[1 n]`
;; tensor without copying. When the input is a `double[]` or a
;; float64 tensor, the result shares the same backing memory:

(let [arr (double-array [1 2 3])
      col (t/column arr)]
  (aset arr 0 99.0)
  (col 0 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; A lazy dfn result stays lazy through `t/column` — no copy,
;; no materialization:

(let [a (t/matrix [1 2 3])
      b (t/matrix [10 20 30])
      col (t/column (la/add a b))]
  {:shape (t/shape col)
   :contiguous? (some? (t/array-buffer col))
   :values (seq (t/flatten col))})

(kind/test-last
 [(fn [{:keys [shape contiguous? values]}]
    (and (= [3 1] shape)
         (not contiguous?)
         (= [11.0 22.0 33.0] values)))])

;; Copies are deferred to the EJML boundary — `la/mmul` and
;; other decompositions copy when they need to:

(let [col (t/column (la/add (t/matrix [1 0])
                            (t/matrix [0 1])))
      A (t/matrix [[2 0] [0 3]])]
  (la/mmul A col))

(kind/test-last [(fn [r] (and (== 2.0 (r 0 0))
                              (== 3.0 (r 1 0))))])

;; ## t/matrix passes through existing tensors

;; When the input is already a float64 rank-2 tensor,
;; `t/matrix` returns it unchanged:

(let [A (t/matrix [[1 2] [3 4]])
      B (t/matrix A)]
  (identical? A B))

(kind/test-last [true?])

;; For nested sequences or non-float64 data, it allocates
;; as usual:

(let [A (t/matrix [[1 2] [3 4]])]
  (identical? A (t/matrix [[1 2] [3 4]])))

(kind/test-last [false?])

;; ## la/transpose is a zero-copy view

;; `la/transpose` returns a strided view — no allocation,
;; same backing memory. Mutating the original changes
;; the transpose:

(let [E (t/matrix [[1 2] [3 4]])
      Et (la/transpose E)]
  (t/mset! E 0 1 99.0)
  (Et 1 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; And vice versa — mutating the transpose changes the original:

(let [E (t/matrix [[1 2] [3 4]])
      Et (la/transpose E)]
  (t/mset! Et 0 0 -1.0)
  (E 0 0))

(kind/test-last [(fn [v] (== -1.0 v))])

;; This is consistent with `t/reshape` and `t/select` —
;; views share memory. Use `t/clone` to get an independent copy:

(let [E (t/matrix [[1 2] [3 4]])
      Et (t/clone (la/transpose E))]
  (t/mset! E 0 0 -1.0)
  (Et 0 0))

(kind/test-last [(fn [v] (== 1.0 v))])

;; ## EJML results are independent

;; EJML operations (`la/mmul`, `la/invert`, etc.)
;; allocate new result matrices. The output does not share memory
;; with the input.

(let [E (t/matrix [[1 2] [3 4]])
      P (la/mmul E E)]
  (t/mset! E 0 0 -1.0)
  (P 0 0))

(kind/test-last [(fn [v] (== 7.0 v))])

;; ## Noncaching tensors from compute-tensor
;;
;; `t/compute-tensor` returns a **lazy, noncaching** tensor.
;; Each time you read an element, it calls the function again.
;; With a pure function this is fine — but with a mutable RNG,
;; reading the *same* tensor twice produces different values.
;; The tensor is not even close to itself:

(let [rng (java.util.Random. 42)
      t (t/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64)]
  (la/close? t t))

(kind/test-last [false?])

;; `t/clone` materializes the lazy tensor into a contiguous
;; array, fixing this problem:

(let [rng (java.util.Random. 42)
      t (t/clone
         (t/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64))]
  (la/close? t t))

(kind/test-last [true?])

;; ## Non-deterministic evaluation order
;;
;; There is a second, separate problem. `compute-tensor` may
;; evaluate its function **out of element order** — it can
;; parallelise across chunks. With a mutable RNG, the order
;; of `.nextGaussian` calls depends on thread scheduling, so
;; two calls with the same seed produce different tensors:

(let [make-random-tensor
      (fn []
        (let [rng (java.util.Random. 42)]
          (t/clone
           (t/compute-tensor [100 100] (fn [_ _] (.nextGaussian rng)) :float64))))]
  (la/close? (make-random-tensor) (make-random-tensor)))

(kind/test-last [false?])

;; `t/clone` fixed the noncaching issue but cannot fix this —
;; the scrambling happens *during* evaluation, before `clone`
;; sees the result.
;;
;; The safe alternative: generate values sequentially with
;; `repeatedly`, then materialize into a tensor:

(let [make-random-tensor
      (fn []
        (let [rng (java.util.Random. 42)]
          (->> (repeatedly (* 4 4) #(.nextGaussian rng))
               (t/make-container :float64)
               (t/reshape [4 4]))))]
  (la/close? (make-random-tensor) (make-random-tensor)))

(kind/test-last [true?])

;; **Rule of thumb**: never pass mutable state into `compute-tensor`.
;; Generate values sequentially (e.g., with `repeatedly`), materialize
;; with `t/make-container`, then reshape.

;; ## Summary
;;
;; | Operation | New allocation? | Mutable handle? | Notes |
;; |:----------|:----------------|:----------------|:------|
;; | `t/reshape` | No | Yes — same `double[]` | Different shape, same backing |
;; | `t/select` | No | Yes — strided view | View into same `double[]` |
;; | `tensor->dmat` / `dmat->tensor` | No | Yes — same `double[]` | Zero-copy EJML interop |
;; | `t/complex-tensor` (1-arity wrap) | No | Yes — wraps tensor | Shares the interleaved array |
;; | `la/re` / `la/im` | No | Yes — strided view | Views into interleaved layout |
;; | `la/add`, `la/mul`, etc. | No | No — lazy reader | Reads through to sources |
;; | `la/add`, `la/sub`, `la/scale` | No | No — lazy reader | Lazy ComplexTensors |
;; | `t/compute-tensor` | No | No — lazy, noncaching | May evaluate out of element order |
;; | `t/clone` | Yes | Yes — independent | Breaks all links to source |
;; | `t/submatrix` | Yes | Yes — independent | Always clones |
;; | `t/column`, `t/row` | No | Yes — wraps input | Zero-copy for arrays/buffers; lazy for seqs |
;; | `t/matrix` | Only for nested seqs | Yes | Pass-through for existing float64 tensors |
;; | `la/transpose` (real) | No | Yes — strided view | Zero-copy, shares memory with input |
;; | `la/mmul`, `la/invert`, etc. | Yes | Yes — independent | EJML allocates new result |
;; | `t/->double-array` | Only if needed | N/A — raw `double[]` | Zero-copy when contiguous; copies for subviews/lazy |
;; | `t/->double-array` | Only if needed | N/A — raw `double[]` | Same convention, on ComplexTensor |
;;
;; Lazy readers have no array of their own, but they **read through**
;; to the source arrays — mutating a source changes what the lazy
;; reader computes. Use `t/clone` to materialize and break the link.
;;
;; **The guideline**: treat all data as immutable. When you need to
;; mutate (e.g., in a performance-critical inner loop), use
;; `t/clone` first to ensure you own the backing array.
;;
;; That said, sharing and mutation can be a **deliberate technique**
;; when used with care. `t/mset!` lets you mutate a tensor
;; element in place, and `t/clone` ensures you own the backing
;; array when you need to. The key is to keep the mutable scope
;; small and focused, so that nothing outside the function can
;; mutate the result.
