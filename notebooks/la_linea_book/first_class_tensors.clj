;; # Research: First-class tensors in Clojure

;; **Guiding question:** What would make tensors feel familiar and
;; reasonably unsurprising for someone learning Clojure? Could
;; tensors appear in an early lesson alongside vectors and maps?

;; This notebook explores the gap between Clojure's first-class data
;; structures and dtype-next tensors, documents what already works,
;; and identifies remaining friction.

;; ## Setup

(ns la-linea-book.first-class-tensors
  (:require [scicloj.la-linea.linalg :as la]
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

;; Tensor output cannot be pasted back into the REPL. The
;; `#tech.v3.tensor<float64>[2 2]` prefix is informative but
;; not a valid reader form.

;; Attempting to read a tensor's printed form fails:

(try
  (read-string
   (pr-str {:weights (la/matrix [[1 2] [3 4]])}))
  (catch Exception _
    :read-failed))

(kind/test-last [= :read-failed])

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

;; ## Experiment: tagged literals

;; Tagged literals like `#la/m [[1 2] [3 4]]` could make tensors
;; print-readable. Here we prototype the idea.

;; A data reader function:

(defn read-matrix [form]
  (la/matrix form))

(defn read-column [form]
  (la/column form))

;; Simulate reading tagged literals:

(read-matrix [[1 2] [3 4]])

(kind/test-last [(fn [m] (= [2 2] (dtype/shape m)))])

(read-column [1 2 3])

(kind/test-last [(fn [v] (= [3 1] (dtype/shape v)))])

;; A function to produce tagged-literal format:

(defn tensor->tagged-str [t]
  (let [shape (dtype/shape t)]
    (cond
      ;; Column vector [n 1]
      (and (= 2 (count shape)) (= 1 (second shape)))
      (str "#la/v " (vec (dtype/->reader t :float64)))

      ;; Matrix [r c]
      (= 2 (count shape))
      (let [[r c] shape]
        (str "#la/m "
             (vec (for [i (range r)]
                    (vec (for [j (range c)]
                           (tensor/mget t i j)))))))

      :else (str t))))

(tensor->tagged-str (la/matrix [[1 2] [3 4]]))

(kind/test-last [= "#la/m [[1.0 2.0] [3.0 4.0]]"])

(tensor->tagged-str (la/column [5 6 7]))

(kind/test-last [= "#la/v [5.0 6.0 7.0]"])

;; For small tensors, this produces readable output. For large
;; tensors, truncation would be needed (like `*print-length*`).
;; Installing this as a `print-method` would make tensors
;; round-trip through `pr-str` / `read-string`.

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
;; 2. **Printing** — tagged literals (`#la/m`, `#la/v`) would make
;;    small tensors print-readable. Implementation needs a
;;    `data_readers.clj` file and a custom `print-method`. For large
;;    tensors, truncation is needed.
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
;;    round-trip. Fixing print-readability with tagged literals would
;;    make the biggest difference for beginners.
