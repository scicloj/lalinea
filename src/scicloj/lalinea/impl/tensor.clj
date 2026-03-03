(ns scicloj.lalinea.impl.tensor
  "Zero-copy interop between dtype-next tensors and EJML's DMatrixRMaj.

   Both structures use the same memory layout: row-major flat
   double[] for an r×c matrix. This namespace provides functions
   to convert between them without copying data.

   dtype-next tensors serve as the matrix type — no new deftype
   is introduced for real matrices."
  (:require [tech.v3.tensor :as dtt]
            [tech.v3.datatype :as dtype])
  (:import [org.ejml.data DMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; Zero-copy conversion
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: [r c] tensor -> DMatrixRMaj sharing the same double[].

   The tensor must be rank 2 with :float64 datatype. Contiguous
   tensors share the same array; lazy or strided tensors are copied.
   Mutations through the returned DMatrixRMaj are visible in the
   original for contiguous tensors."
  ^DMatrixRMaj [tensor]
  (let [shape (dtype/shape tensor)
        _ (when-not (= 2 (count shape))
            (throw (ex-info (str "Expected rank-2 tensor, got shape " (vec shape))
                            {:shape (vec shape)})))
        [r c] shape
        arr (dtype/->double-array tensor)
        dm (DMatrixRMaj. (int r) (int c))]
    (.setData dm arr)
    dm))

(defn dmat->tensor
  "Zero-copy: DMatrixRMaj -> [r c] tensor sharing the same double[].

   Mutations through either view are visible in the other."
  [^DMatrixRMaj dm]
  (let [r (.numRows dm)
        c (.numCols dm)
        arr (.data dm)]
    (dtt/reshape (dtt/ensure-tensor arr) [r c])))

(defn matrix
  "Create an [r c] tensor from nested sequences or pass through
   an existing float64 rank-2 tensor unchanged.
   For nested sequences, allocates a contiguous double[]."
  [rows]
  (if (and (dtt/tensor? rows)
           (= :float64 (dtype/elemwise-datatype rows))
           (= 2 (count (dtype/shape rows))))
    rows
    (dtt/->tensor rows {:datatype :float64})))

(defn- ->float64-reader
  "Coerce to a float64 reader. Zero-copy for arrays/buffers/tensors;
   realizes seqs into a container (seqs have no backing to view)."
  [xs]
  (if (dtype/as-reader xs)
    (dtype/->reader xs :float64)
    (dtype/make-container :float64 xs)))

(defn row-vector
  "Create a [1 c] tensor from a flat sequence.
   Zero-copy when the input is already a float64 buffer or array;
   wraps lazily otherwise. Copies are deferred to the EJML boundary."
  [xs]
  (let [r (->float64-reader xs)
        n (dtype/ecount r)]
    (dtt/reshape (dtt/ensure-tensor r) [1 n])))

(defn col-vector
  "Create a [r 1] tensor from a flat sequence.
   Zero-copy when the input is already a float64 buffer or array;
   wraps lazily otherwise. Copies are deferred to the EJML boundary."
  [xs]
  (let [r (->float64-reader xs)
        n (dtype/ecount r)]
    (dtt/reshape (dtt/ensure-tensor r) [n 1])))

(defn eye
  "Create an n x n identity matrix as a tensor."
  [n]
  (dtt/compute-tensor [n n]
                         (fn [i j] (if (== i j) 1.0 0.0))
                         :float64))

(defn zeros
  "Create an r x c zero matrix as a tensor."
  [r c]
  (dtt/compute-tensor [r c] (fn [_ _] 0.0) :float64))
