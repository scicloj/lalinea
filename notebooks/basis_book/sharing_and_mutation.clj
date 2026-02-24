;; # Sharing and Mutation
;;
;; dtype-next and basis follow a **functional, immutable-by-convention**
;; philosophy. Operations return lazy views and zero-copy wrappers
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

(ns basis-book.sharing-and-mutation
  (:require
   ;; Basis linear algebra API (https://github.com/scicloj/basis):
   [scicloj.basis.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.basis.impl.complex :as cx]
   ;; Tensor ↔ EJML zero-copy bridge:
   [scicloj.basis.impl.tensor :as bt]
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

;; ## tensor/select creates strided views

;; Selecting a row or column returns a **view** backed by the
;; same array, with a stride that skips over elements.

(let [A (tensor/->tensor [[10 20 30]
                          [40 50 60]] {:datatype :float64})
      row0 (tensor/select A 0 :all)
      arr (.ary-data (dtype/as-array-buffer A))
      _ (aset arr 0 999.0)
      result {:A-00 (tensor/mget A 0 0)
              :row0-0 (double (row0 0))}]
  result)

(kind/test-last
 [(fn [{:keys [A-00 row0-0]}]
    (and (== 999.0 A-00)
         (== 999.0 row0-0)))])

;; The row view saw the same mutation.

;; ## Tensor ↔ DMatrixRMaj: zero-copy both ways

;; `tensor->dmat` and `dmat->tensor` share the same `double[]`.
;; This is how basis achieves zero-overhead interop with EJML.

(let [M (la/matrix [[1 2] [3 4]])
      dm (bt/tensor->dmat M)]
  (identical? (.ary-data (dtype/as-array-buffer M))
              (.data dm)))

(kind/test-last [true?])

;; Mutating the DMatrixRMaj is visible in the tensor:

(let [M (la/matrix [[1 2] [3 4]])
      dm (bt/tensor->dmat M)
      _ (.set dm 0 0 -1.0)]
  {:M-00 (tensor/mget M 0 0)
   :dm-00 (.get dm 0 0)})

(kind/test-last
 [(fn [{:keys [M-00 dm-00]}]
    (and (== -1.0 M-00)
         (== -1.0 dm-00)))])

;; And the other direction — mutating the tensor's array:

(let [M (la/matrix [[1 2] [3 4]])
      dm (bt/tensor->dmat M)
      _ (aset (.data dm) 3 99.0)]
  (tensor/mget M 1 1))

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
      arr (.ary-data (dtype/as-array-buffer ct-data))
      _ (aset arr 1 99.0)]
  (cx/im (ct 0)))

(kind/test-last [(fn [v] (== 99.0 v))])

;; The imaginary part of the first element changed to 99.

;; ## re and im are strided views

;; `cx/re` and `cx/im` return views into the interleaved layout.
;; They share the same backing memory.

(let [ct (cx/complex-tensor
          (tensor/->tensor [[10 40] [20 50] [30 60]] {:datatype :float64}))
      re-view (cx/re ct)
      arr (.ary-data (dtype/as-array-buffer (cx/->tensor ct)))
      _ (aset arr 0 -10.0)]
  (double (re-view 0)))

(kind/test-last [(fn [v] (== -10.0 v))])

;; Mutating the backing array was immediately visible in the `re` view.

;; ## Lazy operations do NOT share memory

;; `dfn/+`, `dfn/*`, etc. return **lazy noncaching readers**.
;; They don't allocate new memory — they recompute on every access.
;; This means they reference the *source* data, but don't share
;; a mutable buffer with it.

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
      arr (.ary-data (dtype/as-array-buffer x))
      _ (aset arr 0 100.0)]
  (vec lazy-sum))

(kind/test-last [(fn [v] (= [110.0 22.0 33.0] v))])

;; A lazy reader doesn't have its own array to mutate.
;; It always reads through to the source.

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
      arr (.ary-data (dtype/as-array-buffer (cx/->tensor ca)))
      _ (aset arr 0 100.0)]
  (vec (dtype/->reader (cx/re lazy-sum))))

(kind/test-last [(fn [v] (= [110.0 22.0] v))])

;; ## dtype/clone breaks sharing

;; `dtype/clone` is the standard way to get an independent copy.
;; After cloning, the two objects have separate backing arrays.

(let [original (la/matrix [[1 2] [3 4]])
      cloned (dtype/clone original)]
  (identical? (.ary-data (dtype/as-array-buffer original))
              (.ary-data (dtype/as-array-buffer cloned))))

(kind/test-last [false?])

;; Mutating the original does not affect the clone:

(let [original (la/matrix [[1 2] [3 4]])
      cloned (dtype/clone original)
      arr (.ary-data (dtype/as-array-buffer original))
      _ (aset arr 0 -999.0)]
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
      orig-arr (.ary-data (dtype/as-array-buffer (cx/->tensor ct-orig)))
      _ (aset orig-arr 0 -1.0)]
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
      arr (.ary-data (dtype/as-array-buffer (cx/->tensor p)))
      _ (aset arr 0 999.0)]
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
      arr (.ary-data (dtype/as-array-buffer big))
      _ (aset arr 0 -1.0)]
  {:big-00 (tensor/mget big 0 0)
   :sub-00 (tensor/mget sub 0 0)})

(kind/test-last
 [(fn [{:keys [big-00 sub-00]}]
    (and (== -1.0 big-00)
         (== 1.0 sub-00)))])

;; ## EJML results are independent

;; EJML operations (`la/mmul`, `la/invert`, `la/transpose`, etc.)
;; allocate new result matrices. The output does not share memory
;; with the input.

(let [E (la/matrix [[1 2] [3 4]])
      Et (la/transpose E)]
  (identical? (.ary-data (dtype/as-array-buffer E))
              (.ary-data (dtype/as-array-buffer Et))))

(kind/test-last [false?])

;; Mutating the input does not affect the transposed result:

(let [E (la/matrix [[1 2] [3 4]])
      Et (la/transpose E)
      arr (.ary-data (dtype/as-array-buffer E))
      _ (aset arr 0 -1.0)]
  {:E-00 (tensor/mget E 0 0)
   :Et-00 (tensor/mget Et 0 0)})

(kind/test-last
 [(fn [{:keys [E-00 Et-00]}]
    (and (== -1.0 E-00)
         (== 1.0 Et-00)))])

;; ## Summary
;;
;; | Operation | Shares memory? | Notes |
;; |:----------|:---------------|:------|
;; | `tensor/reshape` | Yes | Same `double[]`, different shape |
;; | `tensor/select` | Yes | Strided view into same `double[]` |
;; | `tensor->dmat` / `dmat->tensor` | Yes | Zero-copy EJML interop |
;; | `cx/complex-tensor` (1-arity wrap) | Yes | Wraps the tensor directly |
;; | `cx/re` / `cx/im` | Yes | Strided views into interleaved layout |
;; | `dfn/+`, `dfn/*`, etc. | No | Lazy — recompute from sources on access |
;; | `cx/add`, `cx/sub`, `cx/scale` | No | Lazy ComplexTensors |
;; | `dtype/clone` | No | Independent contiguous copy |
;; | `la/submatrix` | No | Always clones |
;; | `la/mmul`, `la/transpose`, etc. | No | EJML allocates new result |
;;
;; Lazy readers don't have their own array, but they **read through**
;; to the source arrays. Mutating a source changes what the lazy reader
;; computes. Use `dtype/clone` to materialize and break the link.
;;
;; **The guideline**: treat all data as immutable. When you need to
;; mutate (e.g., in a performance-critical inner loop), use
;; `dtype/clone` first to ensure you own the backing array.
