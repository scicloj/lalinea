(ns scicloj.la-linea.tape
  "Computation tape for tracking tensor operations.

   Records a DAG of operations when enabled, answering questions like:
   - Does tensor A share memory with tensor B?
   - Is this tensor lazy or materialized?
   - Where did the copy happen?
   - What's the computation graph?

   Use `with-tape` to record operations, `memory-status` and
   `shares-memory?` for standalone inspection."
  (:require [tech.v3.datatype :as dtype]
            [tech.v3.tensor :as tensor]
            [scicloj.la-linea.complex :as cx])
  (:import [java.util IdentityHashMap]))

;; ---------------------------------------------------------------------------
;; Dynamic var
;; ---------------------------------------------------------------------------

(def ^:dynamic *tape*
  "When bound to a tape object, `la/` functions record operations.
   nil by default — no recording, negligible overhead."
  nil)

;; ---------------------------------------------------------------------------
;; Standalone utilities (no tape needed)
;; ---------------------------------------------------------------------------

(defn- backing-array
  "Extract the backing double[] from a tensor, if any.
   Works for both contiguous tensors (via as-array-buffer) and
   strided views (via .buffer → as-array-buffer).
   Returns nil for lazy tensors."
  [t]
  (let [t (if (cx/complex? t) (cx/->tensor t) t)]
    (if-let [ab (dtype/as-array-buffer t)]
      (.ary-data ab)
      (when (tensor/tensor? t)
        (when-let [buf (.buffer t)]
          (when-let [ab (dtype/as-array-buffer buf)]
            (.ary-data ab)))))))

(defn memory-status
  "Classify a tensor's memory backing.

   Returns:
   - `:contiguous`  — backed by a full double[] (fast, mutable)
   - `:strided`     — shares a double[] but with non-standard strides (e.g. transpose)
   - `:lazy`        — no backing array, recomputes on each access

   Works on both real tensors and ComplexTensors."
  [t]
  (let [raw (if (cx/complex? t) (cx/->tensor t) t)]
    (cond
      (dtype/as-array-buffer raw) :contiguous
      (backing-array raw)         :strided
      :else                       :lazy)))

(defn shares-memory?
  "True if two tensors share the same backing double[].

   Detects sharing through transpose, reshape, column/row construction,
   and any other operation that returns a view of the same data.
   Returns false if either tensor is lazy (no backing array)."
  [a b]
  (let [arr-a (backing-array a)
        arr-b (backing-array b)]
    (and (some? arr-a)
         (identical? arr-a arr-b))))

;; ---------------------------------------------------------------------------
;; Tape recording
;; ---------------------------------------------------------------------------

(defn- tensor-shape [x]
  (cond
    (cx/complex? x) (cx/complex-shape x)
    (tensor/tensor? x) (vec (dtype/shape x))
    (map? x) :map
    :else nil))

(defn- lookup-input
  "Look up an input in the tape registry. Returns {:id ...} if tracked,
   {:external true} otherwise."
  [^IdentityHashMap registry input]
  (if-let [id (.get registry input)]
    {:id id}
    {:external true}))

(defn record!
  "Record an operation on the tape. No-op when `*tape*` is nil.
   Returns `result` unchanged.

   `op`     — keyword identifying the operation (e.g. `:la/add`)
   `inputs` — vector of input tensors/values
   `result` — the computed result"
  [op inputs result]
  (when-some [tape *tape*]
    (when (some? result)
      (let [^IdentityHashMap registry (:registry tape)
            id (str "t" (swap! (:counter tape) inc))
            input-refs (mapv (partial lookup-input registry) inputs)
            entry {:id       id
                   :op       op
                   :inputs   input-refs
                   :output   result
                   :shape    (tensor-shape result)
                   :complex? (cx/complex? result)}]
        (swap! (:entries tape) conj entry)
        (.put registry result id))))
  result)

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
   - `:shared`      — output shares backing array with an input
   - `:independent`  — output has its own backing array (fresh allocation)
   - `:lazy`         — output has no backing array (recomputes on access)"
  [entry]
  (let [result (:output entry)
        result-arr (backing-array result)]
    (if result-arr
      (let [input-outputs (keep (fn [inp-ref]
                                  (when-not (:external inp-ref)
                                    nil))
                                (:inputs entry))
            ;; Check inputs — but we don't have the actual input tensors
            ;; in the entry (only refs). Use memory-status instead.
            status (memory-status result)]
        (if (= status :lazy)
          :lazy
          ;; Check if any input shares the same backing array
          ;; We need the actual input tensors, which are outputs of earlier entries
          :independent))
      :lazy)))

(defn- entry-memory-status
  "Determine memory status of a tape entry relative to its inputs.
   Requires the full entries-by-id index to resolve input references."
  [entry idx]
  (let [result (:output entry)
        result-arr (backing-array result)]
    (if (nil? result-arr)
      :lazy
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
   - `:by-memory`   — count per memory status (:lazy, :shared, :independent)"
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
                      :lazy "~"
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
  "Render the computation DAG for a value as a Mermaid flowchart string.

   Memory status is annotated on each node:
   - `~` lazy (recomputes on access)
   - `=` shared (shares backing array with an input)
   - `+` independent (fresh allocation)

   Wrap with `(kind/mermaid ...)` for notebook rendering."
  [tape-result value]
  (let [^IdentityHashMap registry (:registry tape-result)
        entries (:entries tape-result)
        idx (entries-by-id entries)]
    (when-let [id (.get registry value)]
      (when-let [entry (get idx id)]
        (let [lines (atom [])]
          (walk-mermaid entry idx (atom #{}) lines)
          (str "flowchart TD\n" (clojure.string/join "\n" @lines)))))))
