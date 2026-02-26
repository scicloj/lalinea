(ns scicloj.la-linea.impl.tensor
  "Zero-copy interop between dtype-next tensors and EJML's DMatrixRMaj.

   Both structures use the same memory layout: row-major flat
   double[] for an r×c matrix. This namespace provides functions
   to convert between them without copying data.

   dtype-next tensors serve as the matrix type — no new deftype
   is introduced for real matrices."
  (:require [tech.v3.tensor :as tensor]
            [tech.v3.datatype :as dtype])
  (:import [org.ejml.data DMatrixRMaj]))

;; ---------------------------------------------------------------------------
;; Zero-copy conversion
;; ---------------------------------------------------------------------------

(defn tensor->dmat
  "Zero-copy: [r c] tensor -> DMatrixRMaj sharing the same double[].

   The tensor must be rank 2 with :float64 datatype and contiguous
   row-major storage. Mutations through either view are visible
   in the other."
  ^DMatrixRMaj [tensor]
  (let [shape (dtype/shape tensor)
        _ (when-not (= 2 (count shape))
            (throw (ex-info (str "Expected rank-2 tensor, got shape " (vec shape))
                            {:shape (vec shape)})))
        [r c] shape
        ab (dtype/as-array-buffer tensor)
        arr (if ab
              (.ary-data ab)
              (dtype/->double-array tensor))
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
    (tensor/reshape (tensor/ensure-tensor arr) [r c])))

(defn matrix
  "Create an [r c] tensor from nested sequences.
   The tensor is backed by a contiguous double[]."
  [rows]
  (tensor/->tensor rows {:datatype :float64}))

(defn row-vector
  "Create a [1 c] tensor from a flat sequence."
  [xs]
  (let [buf (dtype/make-container :float64 xs)]
    (tensor/reshape (tensor/ensure-tensor buf) [1 (count buf)])))

(defn col-vector
  "Create a [r 1] tensor from a flat sequence."
  [xs]
  (let [buf (dtype/make-container :float64 xs)]
    (tensor/reshape (tensor/ensure-tensor buf) [(count buf) 1])))

(defn eye
  "Create an n x n identity matrix as a tensor."
  [n]
  (tensor/compute-tensor [n n]
                         (fn [i j] (if (== i j) 1.0 0.0))
                         :float64))

(defn zeros
  "Create an r x c zero matrix as a tensor."
  [r c]
  (tensor/compute-tensor [r c] (fn [_ _] 0.0) :float64))
