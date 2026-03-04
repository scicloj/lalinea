;; # dtype-next background
;;
;; [dtype-next](https://github.com/cnuernber/dtype-next) is a Clojure
;; library for working with typed numerical data. La Linea builds on
;; its [tensor](https://en.wikipedia.org/wiki/Tensor_(machine_learning)) abstraction — this chapter introduces the
;; key ideas you need to know.

(ns lalinea-book.dtype-next-background
  (:require [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [scicloj.kindly.v4.kind :as kind]))

;; ## Buffers and tensors
;;
;; dtype-next's core abstraction is the **buffer** — a typed, indexed
;; container backed by a Java array, off-heap memory, or a functional
;; reader. A **tensor** is a multi-dimensional view over a buffer.
;; Reshaping or slicing a tensor creates a new view without copying
;; data — the same backing storage is indexed differently.

(def t (dtt/->tensor [[1 2 3]
                      [4 5 6]] {:datatype :float64}))

(dtype/shape t)

(kind/test-last [(fn [s] (= [2 3] s))])

;; Tensors are callable — `(t i j)` reads element `[i,j]`:

[(t 0 1) (t 1 0)]

(kind/test-last [(fn [v] (= [2.0 4.0] v))])

;; ## Lazy and noncaching
;;
;; dtype-next follows a **lazy, noncaching** philosophy.
;; Element-wise operations return lightweight readers that
;; re-evaluate on every access rather than storing results:

(let [a (dtt/->tensor [1 2 3] {:datatype :float64})
      b (dtt/->tensor [10 20 30] {:datatype :float64})
      s (dfn/+ a b)]
  {:values (vec s)
   :has-array? (some? (dtype/as-array-buffer s))})

(kind/test-last
 [(fn [{:keys [values has-array?]}]
    (and (= [11.0 22.0 33.0] values)
         (not has-array?)))])

;; The result `s` has no backing array — it recomputes on every
;; read. This avoids intermediate allocations and lets you compose
;; long pipelines with near-zero overhead.
;;
;; The trade-off: reading the same element twice computes it twice.
;; When you need a concrete array, call `dtype/clone`:

(let [s (dfn/+ (dtt/->tensor [1 2 3] {:datatype :float64})
               (dtt/->tensor [10 20 30] {:datatype :float64}))
      materialized (dtype/clone s)]
  (some? (dtype/as-array-buffer materialized)))

(kind/test-last [true?])

;; ## Why La Linea builds on dtype-next
;;
;; dtype-next tensors and EJML's `DMatrixRMaj` share the same
;; memory layout — a flat `double[]` in row-major order.
;; This lets La Linea pass tensors to EJML with **zero copying**:
;;
;; | Type | Backing | Layout |
;; |:-----|:--------|:-------|
;; | dtype-next tensor `[r c]` | `double[r*c]` | row-major |
;; | EJML `DMatrixRMaj` | `double[r*c]` | row-major |
;;
;; La Linea inherits dtype-next's lazy philosophy: element-wise
;; operations (`el/+`, `el/-`, `el/*`) return lazy readers,
;; while operations that cross into EJML (`la/mmul`, `la/solve`,
;; `la/invert`) materialize at the boundary. You get lazy
;; composition by default, with explicit materialization only
;; where it matters.
;;
;; The next chapter introduces La Linea's tensor API.
