;; # Tensors
;;
;; La Linea wraps dtype-next tensors in **RealTensors** — thin
;; wrappers that add type identity, custom printing, and structural
;; equality while preserving zero-copy interop. This chapter covers
;; the `scicloj.lalinea.tensor` namespace (alias `t/`).

(ns lalinea-book.tensors
  (:require
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.elementwise :as el]
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math])
  (:import [org.ejml.data DMatrixRMaj]))

;; ## Construction

;; `t/matrix` creates a RealTensor from nested sequences or
;; an existing tensor:

(t/matrix [[1 2 3]
           [4 5 6]])

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

;; `t/column` and `t/row` create column `[n 1]` and row `[1 n]`
;; vectors:

(t/column [1 2 3])

(kind/test-last [(fn [c] (= [3 1] (t/shape c)))])

(t/row [1 2 3])

(kind/test-last [(fn [r] (= [1 3] (t/shape r)))])

;; `t/eye`, `t/zeros`, and `t/ones` create standard matrices:

(t/eye 3)

(kind/test-last [(fn [I] (= 1.0 (I 0 0)))])

(t/zeros 2 3)

(kind/test-last [(fn [Z] (= 0.0 (Z 1 2)))])

;; `t/compute-matrix` constructs from a function of `(i, j)`:

(t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0)))

(kind/test-last [(fn [m] (and (= 1.0 (m 2 2))
                              (= 0.0 (m 0 1))))])

;; `t/compute-tensor` creates a lazy tensor of arbitrary shape:

(t/compute-tensor [3 3]
                  (fn [i j] (if (== i j) 1.0 0.0))
                  :float64)

(kind/test-last [(fn [m] (= 1.0 (m 1 1)))])

;; ## Printed form
;;
;; RealTensors print as `#la/R` tagged literals.
;; This representation round-trips through `pr-str` / `read-string`:

(t/matrix [[1 2] [3 4]])

(kind/test-last [(fn [m] (= m (read-string (pr-str m))))])

;; ## Element access

;; Tensors are callable — `(m i j)` reads element `[i,j]`:

(let [m (t/matrix [[10 20] [30 40]])]
  [(m 0 1) (m 1 0)])

(kind/test-last [(fn [v] (= [20.0 30.0] v))])

;; ## Structural operations

;; ### Shape and reshape

(t/shape (t/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [s] (= [2 3] s))])

;; `t/reshape` is zero-copy — it wraps the same backing array
;; in a different shape:

(t/reshape (t/matrix [1 2 3 4 5 6]) [2 3])

(kind/test-last [(fn [m] (= [2 3] (t/shape m)))])

;; ### Select and submatrix

;; `t/select` slices along dimensions. It returns a view:

(let [A (t/matrix [[1 2 3]
                   [4 5 6]])]
  (t/select A 0 :all))

(kind/test-last [(fn [r] (= [1.0 2.0 3.0] r))])

;; `t/submatrix` extracts a contiguous submatrix (always a copy):

(let [A (t/matrix [[1 2 3] [4 5 6] [7 8 9]])]
  (t/submatrix A (range 2) (range 2)))

(kind/test-last [(fn [s] (= [2 2] (t/shape s)))])

;; ### Flatten

;; `t/flatten` reshapes to 1D:

(t/flatten (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [v] (= [4] (t/shape v)))])

;; ## Materialization

;; Lazy results (from `el/+`, `el/sqrt`, etc.) recompute on
;; every access. Call `t/materialize` to ensure a concrete array
;; (no-op if already concrete), or `t/clone` to force a fresh copy:

(let [a (t/matrix [1 4 9])
      s (el/sqrt a)
      cloned (t/clone s)]
  {:lazy-array? (some? (t/array-buffer s))
   :cloned-array? (some? (t/array-buffer cloned))})

(kind/test-last
 [(fn [{:keys [lazy-array? cloned-array?]}]
    (and (not lazy-array?)
         cloned-array?))])

;; ## Mutation: t/mset!

;; `t/mset!` mutates a tensor element in place:

(let [m (t/matrix [[1 2] [3 4]])]
  (t/mset! m 0 1 99.0)
  (m 0 1))

(kind/test-last [= 99.0])

;; Only works on tensors backed by a concrete array.
;; Lazy tensors cannot be mutated.

;; ## Element-wise operations
;;
;; The `scicloj.lalinea.elementwise` namespace (alias `el/`)
;; provides tape-aware element-wise functions:

(let [x (t/matrix [[1 4] [9 16]])]
  (el/sqrt x))

(kind/test-last [(fn [r] (= 2.0 (r 0 1)))])

;; `el/+`, `el/-`, `el/scale`, and `el/*` also work
;; element-wise on matching-shape tensors:

(let [a (t/matrix [[1 2] [3 4]])
      b (t/matrix [[10 20] [30 40]])]
  (el/+ a b))

(kind/test-last [(fn [r] (= 44.0 (r 1 1)))])

;; ## EJML interop
;;
;; `t/tensor->dmat` and `t/dmat->tensor` convert between
;; RealTensors and EJML's `DMatrixRMaj` — zero-copy both ways.

(let [m (t/matrix [[1.0 2.0] [3.0 4.0]])
      dm (t/tensor->dmat m)]
  {:identical? (identical? (t/->double-array m)
                           (.data ^DMatrixRMaj dm))
   :rows (.numRows ^DMatrixRMaj dm)
   :cols (.numCols ^DMatrixRMaj dm)})

(kind/test-last [(fn [v] (and (:identical? v)
                              (= (:rows v) 2)
                              (= (:cols v) 2)))])

;; Mutations through either view are immediately visible in the other:

(let [m (t/matrix [[1.0 0.0] [0.0 1.0]])
      dm (t/tensor->dmat m)]
  (.set ^DMatrixRMaj dm 0 1 99.0)
  (m 0 1))

(kind/test-last [= 99.0])

;; ## Matrix operations (preview)
;;
;; The `scicloj.lalinea.linalg` namespace (alias `la/`) provides
;; a functional API for matrix operations. A taste:

;; Matrix multiply: $A \cdot I = A$

(la/mmul (t/matrix [[1 2] [3 4]])
         (t/eye 2))

(kind/test-last [(fn [m] (= 1.0 (m 0 0)))])

;; Inverse:

(la/invert (t/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [m] (= [2 2] (t/shape m)))])

;; Frobenius norm: $\|A\|_F = \sqrt{\sum a_{ij}^2}$

(la/norm (t/matrix [[1 2 3] [4 5 6]]))

(kind/test-last [(fn [v] (< (abs (- v (math/sqrt 91.0))) 1e-10))])
