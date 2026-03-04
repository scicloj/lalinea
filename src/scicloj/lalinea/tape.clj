(ns scicloj.lalinea.tape
  "Computation tape for tracking tensor operations.

   Records a DAG of operations when enabled, answering questions like:
   - Does tensor A share memory with tensor B?
   - Is this tensor lazy or materialized?
   - Where did the copy happen?
   - What's the computation graph?

   Use `with-tape` to record operations, `memory-status` and
   `memory-relation` for standalone inspection."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.tensor :as dtt]
            [scicloj.lalinea.impl.complex-tensor :as ct]
            [scicloj.lalinea.impl.real-tensor :as rt]
            [scicloj.lalinea.impl.buffer :as buf]
            [scicloj.kindly.v4.kind :as kind])
  (:import [java.util IdentityHashMap]))

;; ---------------------------------------------------------------------------
;; Dynamic var
;; ---------------------------------------------------------------------------

(def ^:dynamic *tape*
  "When bound to a tape object, `la/`, `t/`, and `elem/` functions record operations.
   nil by default — no recording, negligible overhead."
  nil)

(def ^:dynamic *inside-record*
  "True when inside a `record!` body. Prevents nested recording —
   Prevents nested recording when operations delegate internally.
   The outermost caller wins."
  false)

;; ---------------------------------------------------------------------------
;; Standalone utilities (no tape needed)
;; ---------------------------------------------------------------------------

(defn- backing-array
  "Extract the backing double[] from a tensor, if any."
  [t]
  (buf/backing-array t))

(defn memory-status
  "Classify a tensor's memory backing.

   Returns:
   - `:contiguous`  — backed by a full double[] (fast, mutable)
   - `:strided`     — shares a double[] but with non-standard strides (e.g. transpose)
   - `:lazy`        — no backing array, recomputes on each access

   Works on both real tensors and ComplexTensors."
  [t]
  (let [raw (cond (ct/complex? t) (ct/->tensor t)
                  (rt/real-tensor? t) (rt/->tensor t)
                  :else t)]
    (cond
      (dtype/as-array-buffer raw) :contiguous
      (backing-array raw)         :strided
      :else                       :lazy)))

(defn memory-relation
  "Classify the memory relationship between two tensors.

   Returns:
   - `:shared`       — same backing double[]
   - `:independent`  — separate backing arrays
   - `:unknown-lazy` — at least one is lazy; relationship indeterminate

   For lazy tensors, the relationship depends on whether the lazy
   reader reads through to the other tensor's backing array — but
   dtype-next does not expose this dependency chain. Use the tape
   (`detect-memory-status`) for the full picture."
  [a b]
  (let [arr-a (backing-array a)
        arr-b (backing-array b)]
    (cond
      (and arr-a arr-b (identical? arr-a arr-b)) :shared
      (and arr-a arr-b)                          :independent
      :else                                      :unknown-lazy)))

;; ---------------------------------------------------------------------------
;; Tape recording
;; ---------------------------------------------------------------------------

(defn- tensor-shape [x]
  (cond
    (ct/complex? x) (ct/complex-shape x)
    (rt/real-tensor? x) (vec (dtype/shape x))
    (dtt/tensor? x) (vec (dtype/shape x))
    (map? x) :map
    :else nil))

(defn- lookup-input
  "Look up an input in the tape registry. Returns {:id ...} if tracked,
   {:external true} otherwise. Tensor-typed externals are registered
   in the registry so they get stable IDs across multiple uses."
  [^IdentityHashMap registry ^clojure.lang.Atom counter input]
  (if-let [id (.get registry input)]
    {:id id}
    (if (or (dtt/tensor? input) (ct/complex? input) (rt/real-tensor? input))
      (let [id (str "x" (swap! counter inc))]
        (.put registry input id)
        {:id id :external true})
      {:external true})))

(defn do-record!
  "Internal: record an entry on the active tape. Called by the `record!` macro."
  [op inputs result]
  (when-some [tape *tape*]
    (when (some? result)
      (let [^IdentityHashMap registry (:registry tape)
            counter (:counter tape)
            id (str "t" (swap! counter inc))
            input-refs (mapv (partial lookup-input registry counter) inputs)
            entry {:id            id
                   :op            op
                   :inputs        input-refs
                   :input-tensors inputs
                   :output        result
                   :shape         (tensor-shape result)
                   :complex?      (ct/complex? result)}]
        (swap! (:entries tape) conj entry)
        (.put registry result id))))
  result)

(defmacro record!
  "Record an operation on the tape. No-op when `*tape*` is nil.
   Prevents nested recording: inner operations called from within
   the body are suppressed (the outermost caller wins).

   `op`     — keyword identifying the operation (e.g. `:la/add`)
   `inputs` — vector of input tensors/values
   `body`   — expression(s) computing the result"
  [op inputs & body]
  `(if (and *tape* (not *inside-record*))
     (let [inputs# ~inputs
           result# (binding [*inside-record* true] (do ~@body))]
       (do-record! ~op inputs# result#)
       result#)
     (do ~@body)))

(defmacro with-tape
  "Execute body while recording a computation tape.
   Returns `{:result <body-result> :entries <tape-entries>}`."
  [& body]
  `(let [tape# {:entries  (atom [])
                :registry (IdentityHashMap.)
                :counter  (atom 0)}]
     (binding [*tape* tape#]
       (let [result# (do ~@body)]
         {:result  result#
          :entries @(:entries tape#)
          :registry (:registry tape#)}))))

;; ---------------------------------------------------------------------------
;; Tape queries
;; ---------------------------------------------------------------------------

(defn- entries-by-id
  "Index tape entries by ID for fast lookup."
  [entries]
  (into {} (map (juxt :id identity)) entries))

(defn detect-memory-status
  "Classify a tape entry's output relative to its inputs.

   Returns:
   - `:shared`        — output shares backing array with an input
   - `:independent`   — output has its own backing array (fresh allocation)
   - `:reads-through` — output has no backing array; reads from inputs on access"
  [entry]
  (let [result (:output entry)
        result-arr (backing-array result)]
    (if (nil? result-arr)
      :reads-through
      (let [input-arrs (keep backing-array (:input-tensors entry))]
        (if (some #(identical? result-arr %) input-arrs)
          :shared
          :independent)))))

(defn- entry-memory-status
  "Determine memory status of a tape entry relative to its inputs.
   Requires the full entries-by-id index to resolve input references."
  [entry idx]
  (let [result (:output entry)
        result-arr (backing-array result)]
    (if (nil? result-arr)
      :reads-through
      (let [input-arrs (keep (fn [inp-ref]
                               (when-let [id (:id inp-ref)]
                                 (when-let [inp-entry (get idx id)]
                                   (backing-array (:output inp-entry)))))
                             (:inputs entry))]
        (if (some #(identical? result-arr %) input-arrs)
          :shared
          :independent)))))

(defn summary
  "Aggregate stats about a tape result.

   Returns a map with:
   - `:total`       — total operations recorded
   - `:by-op`       — count per operation type
   - `:by-memory`   — count per memory status (:reads-through, :shared, :independent)"
  [tape-result]
  (let [entries (:entries tape-result)
        idx (entries-by-id entries)
        memory-statuses (mapv #(entry-memory-status % idx) entries)]
    {:total     (count entries)
     :by-op     (frequencies (map :op entries))
     :by-memory (frequencies memory-statuses)}))

(defn- walk-origin
  "Recursively build the origin DAG for a tape entry."
  [entry idx seen]
  (if (@seen (:id entry))
    {:ref (:id entry)}
    (do
      (swap! seen conj (:id entry))
      (let [children (mapv (fn [inp-ref]
                             (if-let [id (:id inp-ref)]
                               (if-let [inp-entry (get idx id)]
                                 (walk-origin inp-entry idx seen)
                                 {:id id :external true})
                               {:external true}))
                           (:inputs entry))]
        (cond-> {:id    (:id entry)
                 :op    (:op entry)
                 :shape (:shape entry)}
          (seq children)
          (assoc :inputs children))))))

(defn origin
  "Walk the tape backwards from a value to build its computation DAG.

   Returns a nested map: each node has `:id`, `:op`, `:shape`, and `:inputs`.
   Inputs that were not recorded on the tape appear as `{:external true}`.
   Shared nodes (DAG diamonds) appear as `{:ref <id>}` after the first occurrence."
  [tape-result value]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries (:entries tape-result)
        idx (entries-by-id entries)]
    (when-let [id (.get registry value)]
      (when-let [entry (get idx id)]
        (walk-origin entry idx (atom #{}))))))

(defn- mermaid-node-label [entry idx]
  (let [status (entry-memory-status entry idx)
        status-icon (case status
                      :reads-through "~"
                      :shared "="
                      :independent "+"
                      "")]
    (format "%s %s%s"
            (name (:op entry))
            (str (:shape entry))
            (when (seq status-icon) (str " " status-icon)))))

(defn- walk-mermaid
  "Build Mermaid lines for a tape entry's DAG."
  [entry idx seen lines]
  (when-not (@seen (:id entry))
    (swap! seen conj (:id entry))
    (let [label (mermaid-node-label entry idx)]
      (swap! lines conj (str "  " (:id entry) "[\"" label "\"]"))
      (doseq [inp-ref (:inputs entry)]
        (if-let [id (:id inp-ref)]
          (if-let [inp-entry (get idx id)]
            (do
              (walk-mermaid inp-entry idx seen lines)
              (swap! lines conj (str "  " id " --> " (:id entry))))
            (let [ext-id (str id "-ext")]
              (swap! lines conj (str "  " ext-id "[/\"external\"/]"))
              (swap! lines conj (str "  " ext-id " --> " (:id entry)))))
          (let [ext-id (str "ext" (count @lines))]
            (swap! lines conj (str "  " ext-id "[/\"external\"/]"))
            (swap! lines conj (str "  " ext-id " --> " (:id entry)))))))))

(defn mermaid
  "Render the computation DAG for a value as a Mermaid diagram.

   Memory status is annotated on each node:
   - `~` reads-through (lazy, recomputes on access)
   - `=` shared (shares backing array with an input)
   - `+` independent (fresh allocation)

   Returns a `kind/mermaid` value, renderable in notebooks."
  [tape-result value]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries (:entries tape-result)
        idx (entries-by-id entries)]
    (when-let [id (.get registry value)]
      (when-let [entry (get idx id)]
        (let [lines (atom [])]
          (walk-mermaid entry idx (atom #{}) lines)
          (kind/mermaid (str "flowchart TD\n" (clojure.string/join "\n" @lines))))))))
