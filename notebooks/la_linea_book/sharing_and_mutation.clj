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
;; The rule of thumb: **if you didn't call `dtype/clone`, you might
;; be sharing memory.**

(ns la-linea-book.sharing-and-mutation
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.la-linea.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## Tensors share memory across reshapes

;; `tensor/reshape` is zero-copy — the new tensor wraps the same
;; flat `double[]` in a different shape.

(let [flat (double-array [1 2 3 4 5 6])
      t1 (tensor/reshape (tensor/ensure-tensor flat) [2 3])
      t2 (tensor/reshape (tensor/ensure-tensor flat) [3 2])
      _ (aset flat 0 99.0)
      result {:t1-00 (tensor/mget t1 0 0)
              :t2-00 (tensor/mget t2 0 0)}]
  result)

(kind/test-last
 [(fn [{:keys [t1-00 t2-00]}]
    (and (== 99.0 t1-00)
         (== 99.0 t2-00)))])

;; Both tensors saw the mutation through the shared `double[]`.
;;
;; The idiomatic way — mutate through the tensor directly:

(let [flat (double-array [1 2 3 4 5 6])
      t1 (tensor/reshape (tensor/ensure-tensor flat) [2 3])
      t2 (tensor/reshape (tensor/ensure-tensor flat) [3 2])]
  (tensor/mset! t1 0 0 99.0)
  {:t1-00 (tensor/mget t1 0 0)
   :t2-00 (tensor/mget t2 0 0)})

(kind/test-last
 [(fn [{:keys [t1-00 t2-00]}]
    (and (== 99.0 t1-00)
         (== 99.0 t2-00)))])

;; ## tensor/select creates strided views

;; Selecting a row or column returns a **view** backed by the
;; same array, with a stride that skips over elements.

(let [A (tensor/->tensor [[10 20 30]
                          [40 50 60]] {:datatype :float64})
      row0 (tensor/select A 0 :all)
      arr (dtype/->double-array A)
      _ (aset arr 0 999.0)
      result {:A-00 (tensor/mget A 0 0)
              :row0-0 (double (row0 0))}]
  result)

(kind/test-last
 [(fn [{:keys [A-00 row0-0]}]
    (and (== 999.0 A-00)
         (== 999.0 row0-0)))])

;; The row view saw the same mutation. Using `tensor/mset!`:

(let [A (tensor/->tensor [[10 20 30]
                          [40 50 60]] {:datatype :float64})
      row0 (tensor/select A 0 :all)]
  (tensor/mset! A 0 0 999.0)
  {:A-00 (tensor/mget A 0 0)
   :row0-0 (double (row0 0))})

(kind/test-last
 [(fn [{:keys [A-00 row0-0]}]
    (and (== 999.0 A-00)
         (== 999.0 row0-0)))])

;; ## Tensor ↔ DMatrixRMaj: zero-copy both ways

;; `tensor->dmat` and `dmat->tensor` share the same `double[]`.
;; This is how La Linea achieves zero-overhead interop with EJML.

(let [M (la/matrix [[1 2] [3 4]])
      dm (la/tensor->dmat M)]
  (identical? (dtype/->double-array M)
              (.data dm)))

(kind/test-last [true?])

;; Mutating the DMatrixRMaj is visible in the tensor:

(let [M (la/matrix [[1 2] [3 4]])
      dm (la/tensor->dmat M)
      _ (.set dm 0 0 -1.0)]
  {:M-00 (tensor/mget M 0 0)
   :dm-00 (.get dm 0 0)})

(kind/test-last
 [(fn [{:keys [M-00 dm-00]}]
    (and (== -1.0 M-00)
         (== -1.0 dm-00)))])

;; And the other direction — mutating the tensor is visible in EJML:

(let [M (la/matrix [[1 2] [3 4]])
      dm (la/tensor->dmat M)]
  (tensor/mset! M 1 1 99.0)
  (.get dm 1 1))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## Extracting the backing double[]

;; `dtype/->double-array` is the idiomatic way to get a `double[]`
;; from a tensor. It is **zero-copy when possible, copying only
;; when necessary**:
;;
;; - Contiguous, full-array-backed tensor → returns the same `double[]`
;; - Subview or lazy tensor → allocates and copies

;; A matrix built with `la/matrix` is backed by a contiguous array.
;; `dtype/->double-array` returns the same object — zero-copy:

(let [M (la/matrix [[1 2] [3 4]])
      arr (dtype/->double-array M)]
  (aset arr 0 99.0)
  (tensor/mget M 0 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; A row selected with `tensor/select` is a strided view —
;; contiguous within the parent array, but not spanning all of it.
;; `dtype/->double-array` correctly returns a copy:

(let [M (la/matrix [[1 2 3] [4 5 6]])
      row0 (tensor/select M 0 :all)
      arr (dtype/->double-array row0)]
  {:length (alength arr)
   :values (vec arr)
   :shares-memory? (identical? arr (dtype/->double-array M))})

(kind/test-last
 [(fn [{:keys [length values shares-memory?]}]
    (and (== 3 length)
         (= [1.0 2.0 3.0] values)
         (not shares-memory?)))])

;; A lazy tensor (from `dfn/+` or `tensor/compute-tensor`) has
;; no backing array at all — `dtype/->double-array` allocates one:

(let [a (la/matrix [[1 2] [3 4]])
      b (la/matrix [[10 20] [30 40]])
      lazy-sum (dfn/+ a b)
      arr (dtype/->double-array lazy-sum)]
  {:values (vec arr)
   :has-array-buffer? (some? (dtype/as-array-buffer lazy-sum))})

(kind/test-last
 [(fn [{:keys [values has-array-buffer?]}]
    (and (= [11.0 22.0 33.0 44.0] values)
         (not has-array-buffer?)))])

;; `cx/->double-array` follows the same convention for
;; ComplexTensors — it delegates to `dtype/->double-array`
;; on the underlying `[... 2]` tensor:

(let [ct (cx/complex-tensor
          (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64}))
      arr (cx/->double-array ct)]
  (aset arr 0 99.0)
  (cx/re (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## ComplexTensor wraps a real tensor

;; A ComplexTensor wraps an `[... 2]` real tensor. The `cx/->tensor`
;; accessor exposes the backing tensor, and they share memory.

(let [ct-data (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
      ct (cx/complex-tensor ct-data)]
  (identical? ct-data (cx/->tensor ct)))

(kind/test-last [true?])

;; Mutating through the backing tensor changes the ComplexTensor:

(let [ct-data (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
      ct (cx/complex-tensor ct-data)
      arr (dtype/->double-array ct-data)
      _ (aset arr 1 99.0)]
  (cx/im (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; The imaginary part of the first element changed to 99.
;; Using `tensor/mset!` on the backing tensor:

(let [ct-data (tensor/->tensor [[1 2] [3 4] [5 6]] {:datatype :float64})
      ct (cx/complex-tensor ct-data)]
  (tensor/mset! ct-data 0 1 99.0)
  (cx/im (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; ## re and im are strided views

;; `cx/re` and `cx/im` return views into the interleaved layout.
;; They share the same backing memory.

(let [ct (cx/complex-tensor
          (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
      re-view (cx/re ct)
      arr (dtype/->double-array (cx/->tensor ct))
      _ (aset arr 0 -10.0)]
  (double (re-view 0)))

(kind/test-last [(fn [v] (== -10.0 v))])

;; Mutating the backing array was immediately visible in the `re` view.
;; Using `tensor/mset!`:

(let [ct (cx/complex-tensor
          (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
      re-view (cx/re ct)]
  (tensor/mset! (cx/->tensor ct) 0 0 -10.0)
  (double (re-view 0)))

(kind/test-last [(fn [v] (== -10.0 v))])

;; ## Lazy operations: no new memory, no new mutation handle

;; `dfn/+`, `dfn/*`, etc. return **lazy noncaching readers**.
;; They allocate no new memory — they recompute on every access,
;; reading through to the original source buffers. This means
;; they don't create a new mutable handle, but they still
;; depend on the source data.

(let [x (tensor/->tensor [1 2 3] {:datatype :float64})
      y (tensor/->tensor [10 20 30] {:datatype :float64})
      lazy-sum (dfn/+ x y)]
  (vec lazy-sum))

(kind/test-last [(fn [v] (= [11.0 22.0 33.0] v))])

;; Mutating `x` changes what `lazy-sum` computes — it reads from `x`
;; on every access:

(let [x (tensor/->tensor [1 2 3] {:datatype :float64})
      y (tensor/->tensor [10 20 30] {:datatype :float64})
      lazy-sum (dfn/+ x y)
      arr (dtype/->double-array x)
      _ (aset arr 0 100.0)]
  (vec lazy-sum))

(kind/test-last [(fn [v] (= [110.0 22.0 33.0] v))])

;; A lazy reader doesn't have its own array to mutate.
;; It always reads through to the source. Using `tensor/mset!`:

(let [x (tensor/->tensor [1 2 3] {:datatype :float64})
      y (tensor/->tensor [10 20 30] {:datatype :float64})
      lazy-sum (dfn/+ x y)]
  (tensor/mset! x 0 100.0)
  (vec lazy-sum))

(kind/test-last [(fn [v] (= [110.0 22.0 33.0] v))])

;; ## Complex arithmetic: lazy results

;; `cx/add`, `cx/sub`, `cx/scale` return lazy ComplexTensors.
;; Like `dfn/+`, they read through to the source on every access.

(let [ca (cx/complex-tensor
          (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      cb (cx/complex-tensor
          (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-sum (cx/add ca cb)]
  {:re (vec (dtype/->reader (cx/re lazy-sum)))
   :im (vec (dtype/->reader (cx/im lazy-sum)))})

(kind/test-last
 [(fn [{:keys [re im]}]
    (and (= [11.0 22.0] re)
         (= [33.0 44.0] im)))])

;; Mutating `ca`'s backing array propagates through the lazy result:

(let [ca (cx/complex-tensor
          (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      cb (cx/complex-tensor
          (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-sum (cx/add ca cb)
      arr (dtype/->double-array (cx/->tensor ca))
      _ (aset arr 0 100.0)]
  (vec (dtype/->reader (cx/re lazy-sum))))

(kind/test-last [(fn [v] (= [110.0 22.0] v))])

;; Using `tensor/mset!`:

(let [ca (cx/complex-tensor
          (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      cb (cx/complex-tensor
          (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-sum (cx/add ca cb)]
  (tensor/mset! (cx/->tensor ca) 0 0 100.0)
  (vec (dtype/->reader (cx/re lazy-sum))))

(kind/test-last [(fn [v] (= [110.0 22.0] v))])

;; ## dtype/clone breaks sharing

;; `dtype/clone` is the standard way to get an independent copy.
;; After cloning, the two objects have separate backing arrays.

(let [original (la/matrix [[1 2] [3 4]])
      cloned (dtype/clone original)]
  (identical? (dtype/->double-array original)
              (dtype/->double-array cloned)))

(kind/test-last [false?])

;; Mutating the original does not affect the clone:

(let [original (la/matrix [[1 2] [3 4]])
      cloned (dtype/clone original)
      arr (dtype/->double-array original)
      _ (aset arr 0 -999.0)]
  {:original-00 (tensor/mget original 0 0)
   :cloned-00 (tensor/mget cloned 0 0)})

(kind/test-last
 [(fn [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00)
         (== 1.0 cloned-00)))])

;; Using `tensor/mset!`:

(let [original (la/matrix [[1 2] [3 4]])
      cloned (dtype/clone original)]
  (tensor/mset! original 0 0 -999.0)
  {:original-00 (tensor/mget original 0 0)
   :cloned-00 (tensor/mget cloned 0 0)})

(kind/test-last
 [(fn [{:keys [original-00 cloned-00]}]
    (and (== -999.0 original-00)
         (== 1.0 cloned-00)))])

;; ## dtype/clone on ComplexTensors

;; Cloning a ComplexTensor produces an independent ComplexTensor
;; with its own backing array.

(let [ct-orig (cx/complex-tensor
               (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
      ct-clone (dtype/clone ct-orig)
      orig-arr (dtype/->double-array (cx/->tensor ct-orig))
      _ (aset orig-arr 0 -1.0)]
  {:orig-re (cx/re (ct-orig 0))
   :clone-re (cx/re (ct-clone 0))})

(kind/test-last
 [(fn [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re)
         (== 1.0 clone-re)))])

;; Using `tensor/mset!`:

(let [ct-orig (cx/complex-tensor
               (tensor/->tensor [[1 4] [2 5] [3 6]] {:datatype :float64}))
      ct-clone (dtype/clone ct-orig)]
  (tensor/mset! (cx/->tensor ct-orig) 0 0 -1.0)
  {:orig-re (cx/re (ct-orig 0))
   :clone-re (cx/re (ct-clone 0))})

(kind/test-last
 [(fn [{:keys [orig-re clone-re]}]
    (and (== -1.0 orig-re)
         (== 1.0 clone-re)))])

;; ## Clone also materializes lazy results

;; Cloning a lazy ComplexTensor (from `cx/add`, `cx/scale`, etc.)
;; materializes it into a contiguous array. The result is independent
;; of the sources.

(let [p (cx/complex-tensor
         (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      q (cx/complex-tensor
         (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-pq (cx/add p q)
      materialized-pq (dtype/clone lazy-pq)]
  (some? (dtype/as-array-buffer (cx/->tensor materialized-pq))))

(kind/test-last [true?])

;; Mutating `p` affects the lazy result but not the materialized copy:

(let [p (cx/complex-tensor
         (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      q (cx/complex-tensor
         (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-pq (cx/add p q)
      materialized-pq (dtype/clone lazy-pq)
      arr (dtype/->double-array (cx/->tensor p))
      _ (aset arr 0 999.0)]
  {:lazy-re (vec (dtype/->reader (cx/re lazy-pq)))
   :materialized-re (vec (dtype/->reader (cx/re materialized-pq)))})

(kind/test-last
 [(fn [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re)
         (= [11.0 22.0] materialized-re)))])

;; Using `tensor/mset!`:

(let [p (cx/complex-tensor
         (tensor/->tensor [[1 3] [2 4]] {:datatype :float64}))
      q (cx/complex-tensor
         (tensor/->tensor [[10 30] [20 40]] {:datatype :float64}))
      lazy-pq (cx/add p q)
      materialized-pq (dtype/clone lazy-pq)]
  (tensor/mset! (cx/->tensor p) 0 0 999.0)
  {:lazy-re (vec (dtype/->reader (cx/re lazy-pq)))
   :materialized-re (vec (dtype/->reader (cx/re materialized-pq)))})

(kind/test-last
 [(fn [{:keys [lazy-re materialized-re]}]
    (and (= [1009.0 22.0] lazy-re)
         (= [11.0 22.0] materialized-re)))])

;; ## la/submatrix clones

;; `la/submatrix` always returns a contiguous, independent copy.
;; This is necessary because `tensor/select` returns non-contiguous
;; views that EJML cannot use directly.

(let [big (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
      sub (la/submatrix big (range 2) (range 2))
      arr (dtype/->double-array big)
      _ (aset arr 0 -1.0)]
  {:big-00 (tensor/mget big 0 0)
   :sub-00 (tensor/mget sub 0 0)})

(kind/test-last
 [(fn [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00)
         (== 1.0 sub-00)))])

;; Using `tensor/mset!`:

(let [big (la/matrix [[1 2 3] [4 5 6] [7 8 9]])
      sub (la/submatrix big (range 2) (range 2))]
  (tensor/mset! big 0 0 -1.0)
  {:big-00 (tensor/mget big 0 0)
   :sub-00 (tensor/mget sub 0 0)})

(kind/test-last
 [(fn [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00)
         (== 1.0 sub-00)))])

;; ## la/column and la/row wrap without copying

;; `la/column` and `la/row` wrap their input as a `[n 1]` or `[1 n]`
;; tensor without copying. When the input is a `double[]` or a
;; float64 tensor, the result shares the same backing memory:

(let [arr (double-array [1 2 3])
      col (la/column arr)]
  (aset arr 0 99.0)
  (tensor/mget col 0 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; A lazy dfn result stays lazy through `la/column` — no copy,
;; no materialization:

(let [a (tensor/->tensor [1 2 3] {:datatype :float64})
      b (tensor/->tensor [10 20 30] {:datatype :float64})
      col (la/column (dfn/+ a b))]
  {:shape (vec (dtype/shape col))
   :contiguous? (some? (dtype/as-array-buffer col))
   :values (vec (dtype/->reader col))})

(kind/test-last
 [(fn [{:keys [shape contiguous? values]}]
    (and (= [3 1] shape)
         (not contiguous?)
         (= [11.0 22.0 33.0] values)))])

;; Copies are deferred to the EJML boundary — `la/mmul` and
;; other decompositions copy when they need to:

(let [col (la/column (dfn/+ (tensor/->tensor [1 0] {:datatype :float64})
                            (tensor/->tensor [0 1] {:datatype :float64})))
      A (la/matrix [[2 0] [0 3]])]
  (la/mmul A col))

(kind/test-last [(fn [r] (and (== 2.0 (tensor/mget r 0 0))
                              (== 3.0 (tensor/mget r 1 0))))])

;; ## la/matrix passes through existing tensors

;; When the input is already a float64 rank-2 tensor,
;; `la/matrix` returns it unchanged:

(let [A (la/matrix [[1 2] [3 4]])
      B (la/matrix A)]
  (identical? A B))

(kind/test-last [true?])

;; For nested sequences or non-float64 data, it allocates
;; as usual:

(let [A (la/matrix [[1 2] [3 4]])]
  (identical? A (la/matrix [[1 2] [3 4]])))

(kind/test-last [false?])

;; ## la/transpose is a zero-copy view

;; `la/transpose` returns a strided view — no allocation,
;; same backing memory. Mutating the original changes
;; the transpose:

(let [E (la/matrix [[1 2] [3 4]])
      Et (la/transpose E)]
  (tensor/mset! E 0 1 99.0)
  (tensor/mget Et 1 0))

(kind/test-last [(fn [v] (== 99.0 v))])

;; And vice versa — mutating the transpose changes the original:

(let [E (la/matrix [[1 2] [3 4]])
      Et (la/transpose E)]
  (tensor/mset! Et 0 0 -1.0)
  (tensor/mget E 0 0))

(kind/test-last [(fn [v] (== -1.0 v))])

;; This is consistent with `tensor/reshape` and `tensor/select` —
;; views share memory. Use `dtype/clone` to get an independent copy:

(let [E (la/matrix [[1 2] [3 4]])
      Et (dtype/clone (la/transpose E))]
  (tensor/mset! E 0 0 -1.0)
  (tensor/mget Et 0 0))

(kind/test-last [(fn [v] (== 1.0 v))])

;; ## EJML results are independent

;; EJML operations (`la/mmul`, `la/invert`, etc.)
;; allocate new result matrices. The output does not share memory
;; with the input.

(let [E (la/matrix [[1 2] [3 4]])
      P (la/mmul E E)]
  (tensor/mset! E 0 0 -1.0)
  (tensor/mget P 0 0))

(kind/test-last [(fn [v] (== 7.0 v))])

;; ## Noncaching tensors from compute-tensor
;;
;; `tensor/compute-tensor` returns a **lazy, noncaching** tensor.
;; Each time you read an element, it calls the function again.
;; With a pure function this is fine — but with a mutable RNG,
;; reading the *same* tensor twice produces different values.
;; The tensor is not even close to itself:

(let [rng (java.util.Random. 42)
      t (tensor/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64)]
  (la/close? t t))

(kind/test-last [false?])

;; `dtype/clone` materializes the lazy tensor into a contiguous
;; array, fixing this problem:

(let [rng (java.util.Random. 42)
      t (dtype/clone
         (tensor/compute-tensor [4 4] (fn [_ _] (.nextGaussian rng)) :float64))]
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
          (dtype/clone
           (tensor/compute-tensor [100 100] (fn [_ _] (.nextGaussian rng)) :float64))))]
  (la/close? (make-random-tensor) (make-random-tensor)))

(kind/test-last [false?])

;; `dtype/clone` fixed the noncaching issue but cannot fix this —
;; the scrambling happens *during* evaluation, before `clone`
;; sees the result.
;;
;; The safe alternative: generate values sequentially with
;; `repeatedly`, then materialize into a tensor:

(let [make-random-tensor
      (fn []
        (let [rng (java.util.Random. 42)]
          (->> (repeatedly (* 4 4) #(.nextGaussian rng))
               (dtype/make-container :float64)
               (tensor/reshape [4 4]))))]
  (la/close? (make-random-tensor) (make-random-tensor)))

(kind/test-last [true?])

;; **Rule of thumb**: never pass mutable state into `compute-tensor`.
;; Generate values sequentially (e.g., with `repeatedly`), materialize
;; with `dtype/make-container`, then reshape.

;; ## Summary
;;
;; | Operation | New allocation? | Mutable handle? | Notes |
;; |:----------|:----------------|:----------------|:------|
;; | `tensor/reshape` | No | Yes — same `double[]` | Different shape, same backing |
;; | `tensor/select` | No | Yes — strided view | View into same `double[]` |
;; | `tensor->dmat` / `dmat->tensor` | No | Yes — same `double[]` | Zero-copy EJML interop |
;; | `cx/complex-tensor` (1-arity wrap) | No | Yes — wraps tensor | Shares the interleaved array |
;; | `cx/re` / `cx/im` | No | Yes — strided view | Views into interleaved layout |
;; | `dfn/+`, `dfn/*`, etc. | No | No — lazy reader | Reads through to sources |
;; | `cx/add`, `cx/sub`, `cx/scale` | No | No — lazy reader | Lazy ComplexTensors |
;; | `tensor/compute-tensor` | No | No — lazy, noncaching | May evaluate out of element order |
;; | `dtype/clone` | Yes | Yes — independent | Breaks all links to source |
;; | `la/submatrix` | Yes | Yes — independent | Always clones |
;; | `la/column`, `la/row` | No | Yes — wraps input | Zero-copy for arrays/buffers; lazy for seqs |
;; | `la/matrix` | Only for nested seqs | Yes | Pass-through for existing float64 tensors |
;; | `la/transpose` (real) | No | Yes — strided view | Zero-copy, shares memory with input |
;; | `la/mmul`, `la/invert`, etc. | Yes | Yes — independent | EJML allocates new result |
;; | `dtype/->double-array` | Only if needed | N/A — raw `double[]` | Zero-copy when contiguous; copies for subviews/lazy |
;; | `cx/->double-array` | Only if needed | N/A — raw `double[]` | Same convention, on ComplexTensor |
;;
;; Lazy readers have no array of their own, but they **read through**
;; to the source arrays — mutating a source changes what the lazy
;; reader computes. Use `dtype/clone` to materialize and break the link.
;;
;; **The guideline**: treat all data as immutable. When you need to
;; mutate (e.g., in a performance-critical inner loop), use
;; `dtype/clone` first to ensure you own the backing array.
;;
;; That said, sharing and mutation can be a **deliberate technique**
;; when used with care. `tensor/mset!` lets you mutate a tensor
;; element in place, and `dtype/clone` ensures you own the backing
;; array when you need to. The key is to keep the mutable scope
;; small and focused, so that nothing outside the function can
;; mutate the result.
