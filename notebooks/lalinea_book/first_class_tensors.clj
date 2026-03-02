;; # Research: First-class tensors in Clojure

;; **Guiding question:** What would make tensors feel familiar and
;; reasonably unsurprising for someone learning Clojure? Could
;; tensors appear in an early lesson alongside vectors and maps?

;; This notebook explores the gap between Clojure's first-class data
;; structures and dtype-next tensors, documents what already works,
;; and identifies remaining friction.

;; ## Setup

(ns lalinea-book.first-class-tensors
  (:require [scicloj.lalinea.linalg :as la]
            [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as dfn]
            [scicloj.kindly.v4.kind :as kind]))

;; ## What already works

;; ### Structural equality and hashing

;; dtype-next tensors support structural equality:

(= (la/column [1 2 3]) (la/column [1 2 3]))

(kind/test-last [true?])

;; Different objects with the same content are equal:

(identical? (la/column [1 2 3]) (la/column [1 2 3]))

(kind/test-last [false?])

;; This works for matrices too:

(= (la/matrix [[1 2] [3 4]]) (la/matrix [[1 2] [3 4]]))

(kind/test-last [true?])

;; And even for lazy tensors (results of arithmetic):

(= (la/add (la/eye 2) (la/eye 2))
   (la/scale (la/eye 2) 2))

(kind/test-last [true?])

;; Hashing is consistent — tensors can be map keys:

(get {(la/column [1 2 3]) :found}
     (la/column [1 2 3]))

(kind/test-last [= :found])

;; And set members:

(count (hash-set (la/column [1 2 3])
                 (la/column [1 2 3])
                 (la/column [4 5 6])))

(kind/test-last [= 2])

;; This is a pleasant surprise — dtype-next provides structural
;; equality and hashing out of the box.

;; ### Nesting in data structures

;; Tensors nest naturally in Clojure maps:

(def model
  {:weights (la/matrix [[0.5 0.3] [0.2 0.8]])
   :bias (la/column [0.1 0.2])
   :name "simple-model"})

(:name model)

(kind/test-last [= "simple-model"])

(la/close? (:bias model) (la/column [0.1 0.2]))

(kind/test-last [true?])

;; ### Vectorized arithmetic

;; La Linea provides vectorized operations that feel natural:

(la/add (la/column [1 2 3]) (la/column [10 20 30]))

(kind/test-last
 [(fn [v] (la/close? v (la/column [11 22 33])))])

(la/scale (la/matrix [[1 2] [3 4]]) 2)

(kind/test-last
 [(fn [m] (la/close? m (la/matrix [[2 4] [6 8]])))])

;; Chaining with `->` threads cleanly:

(-> (la/matrix [[1 0] [0 1]])
    (la/scale 3)
    (la/add (la/eye 2)))

(kind/test-last [(fn [m] (la/close? m (la/scale (la/eye 2) 4)))])

;; ## What does not work

;; ### Printing is not readable

;; Tensors print with a type prefix:

(la/matrix [[1 2] [3 4]])

;; Vectors print readably — you can paste them back:

(pr-str [[1 2] [3 4]])

(kind/test-last [= "[[1 2] [3 4]]"])

;; La Linea tensors print as `#la/R` tagged literals — a readable
;; format that round-trips through `pr-str` / `read-string`:

(read-string
 (pr-str {:weights (la/matrix [[1 2] [3 4]])}))

(kind/test-last [(fn [v] (= (:weights v) (la/matrix [[1 2] [3 4]])))])

;; ### Immutability

;; Clojure vectors are persistent — operations return new values:

(def original [1 2 3])
(def appended (conj original 4))

(= original [1 2 3])

(kind/test-last [true?])

;; Tensors are mutable. Zero-copy views share backing memory:

(def mat (la/matrix [[1 2] [3 4]]))
(def mat-t (la/transpose mat))

;; Mutating `mat` through the backing array also changes `mat-t`:

(let [arr (dtype/->double-array mat)]
  (aset arr 0 99.0)
  [(tensor/mget mat 0 0) (tensor/mget mat-t 0 0)])

(kind/test-last [= [99.0 99.0]])

;; This is correct behavior for zero-copy views, but different
;; from what Clojure users expect. La Linea copies at the EJML
;; boundary, leaving the user in control of when copies happen.

;; ### Destructuring

;; 1D tensors support sequential destructuring:

(let [[a b c] (tensor/->tensor [10.0 20.0 30.0] :datatype :float64)]
  (+ a b c))

(kind/test-last [(fn [v] (== 60.0 v))])

;; Column vectors `[n 1]` destructure to row sub-tensors (not
;; scalars), which prevents direct arithmetic on the elements.
;; For column vectors, use indexed access:

(let [v (la/column [10 20 30])]
  (+ (tensor/mget v 0 0)
     (tensor/mget v 1 0)
     (tensor/mget v 2 0)))

(kind/test-last [(fn [v] (== 60.0 v))])

;; ### Core function interop

;; `reduce` works on 1D tensors:

(reduce + 0 (tensor/->tensor [1.0 2.0 3.0 4.0] :datatype :float64))

(kind/test-last [(fn [v] (== 10.0 v))])

;; This boxes every element. The fast path is `dfn/sum`:

(dfn/sum (la/column [1 2 3 4]))

(kind/test-last [(fn [v] (== 10.0 v))])

;; ## Tagged literals

;; La Linea prints tensors as tagged literals: `#la/R` for real,
;; `#la/C` for complex. These round-trip through `pr-str` /
;; `read-string`:

(pr-str (la/matrix [[1 2] [3 4]]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

(pr-str (la/column [5 6 7]))

(kind/test-last [(fn [s] (clojure.string/starts-with? s "#la/R"))])

;; Reading back produces equal tensors:

(= (la/matrix [[1 2] [3 4]])
   (read-string (pr-str (la/matrix [[1 2] [3 4]]))))

(kind/test-last [true?])

;; ## Experiment: constructor ergonomics

;; Could `(la/v 1 2 3)` feel as natural as `[1 2 3]`?

(defn v [& xs]
  (la/column xs))

(defn m [& rows]
  (la/matrix rows))

(v 1 2 3)

(kind/test-last [(fn [c] (= [3 1] (dtype/shape c)))])

(m [1 2] [3 4])

(kind/test-last [(fn [mat] (= [2 2] (dtype/shape mat)))])

;; The variadic constructors are concise for literal construction.
;; The current `la/column` and `la/matrix` take sequences, which
;; works better for programmatic use and threading:
;;
;; ```clojure
;; (->> data
;;      (map process-row)
;;      la/matrix)
;; ```
;;
;; Both styles can coexist.

;; ## Comparison with R

;; R's atomic vectors are first-class:
;;
;; ```r
;; x <- c(1, 2, 3)           # create a numeric vector
;; x                          # [1] 1 2 3  — readable
;; x == c(1, 2, 3)            # TRUE TRUE TRUE — structural
;; x[1:2]                     # indexing
;; x + c(10, 20, 30)          # vectorized arithmetic
;; list(w = x, b = 1.0)       # nesting in lists
;; ```
;;
;; R's vectors feel natural because they are the basic data type.
;; In Clojure, vectors and maps are the basic types. Tensors are a
;; library addition. But the gap is smaller than expected:
;;
;; - **Equality and hashing**: already work (dtype-next provides them)
;; - **Arithmetic**: La Linea + dfn provide vectorized ops
;; - **Nesting**: tensors inside maps works today
;; - **Printing**: solvable with tagged literals
;;
;; The remaining gaps:
;; - **Immutability**: fundamental tension with performance
;; - **Print-readability**: needs tagged literals or custom print-method
;; - **Core function interop**: `map`/`reduce` box elements

;; ## Findings

;; 1. **Equality and hashing** — already work. This was the most
;;    important gap, and dtype-next has closed it. Tensors can be
;;    map keys and set members.
;;
;; 2. **Printing** — tagged literals (`#la/R`, `#la/C`) now make
;;    tensors print-readable. Implemented via `data_readers.clj` and
;;    custom `print-method` in `impl/print.clj`. Large tensors truncate
;;    with `...` (non-readable).
;;
;; 3. **Constructors** — `(la/v 1 2 3)` is ergonomic for interactive
;;    use. The sequence-based `la/column` is better for programmatic
;;    use. Both can coexist.
;;
;; 4. **Nesting** — works today. Fix printing and tensors become
;;    natural values inside maps and data structures.
;;
;; 5. **Immutability** — the deepest gap. Copy-on-write is feasible
;;    for small tensors but not for large ones. La Linea's
;;    "copy at the boundary" philosophy is a practical compromise.
;;
;; 6. **Beginner experience** — the situation is better than expected.
;;    Equality and arithmetic already work. The main friction point
;;    is printing: tensor output looks unfamiliar and does not
;;    round-trip. The `#la/R` / `#la/C` tagged literals now solve this.
