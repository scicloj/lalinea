;; # Performance
;;
;; **Prerequisites**: [Fractals](fractals.html) (for the Mandelbrot
;; set background).
;;
;; Where does the time go when we compute a Mandelbrot set?
;; This chapter benchmarks four approaches at different
;; abstraction levels and identifies where overhead lives
;; at each layer.
;;
;; All benchmarks use [Criterium](https://github.com/hugoduncan/criterium)
;; for statistically sound measurements.

(ns lalinea-book.performance
  (:require
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.elementwise :as el]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.tensor :as dtt]
   [criterium.core :as crit]
   [scicloj.kindly.v4.kind :as kind]
   [clojure.math :as math]))

;; ## Setup
;;
;; We use a 300 × 400 grid with 50 iterations — the same
;; parameters as the classic Mandelbrot view.

(def bench-h 300)
(def bench-w 400)
(def bench-n (* bench-h bench-w))
(def bench-max-iter 50)

(defn mean-ms
  "Extract the sample mean from a Criterium result, in milliseconds."
  [bench-result]
  (* 1e3 (first (:sample-mean bench-result))))

;; A helper to build a complex plane grid as separate real and
;; imaginary tensors (used by the non-ComplexTensor layers):

(defn real-imag-grids
  [re-min re-max im-min im-max h w]
  [(t/clone (t/compute-tensor [h w]
                              (fn [r c]
                                (+ (double re-min)
                                   (* (- (double re-max) (double re-min))
                                      (/ (double c) (double (dec w))))))
                              :float64))
   (t/clone (t/compute-tensor [h w]
                              (fn [r c]
                                (+ (double im-min)
                                   (* (- (double im-max) (double im-min))
                                      (/ (double r) (double (dec h))))))
                              :float64))])

;; ## The four layers
;;
;; ### Layer 1: La Linea functional
;;
;; ComplexTensor arithmetic with `el/mul`, `la/add`, `el/<=`.
;; Each iteration allocates new tensors via `t/clone`.
;; This is the code from the [Fractals](fractals.html) chapter.

(def complex-grid
  (fn [re-min re-max im-min im-max h w]
    (t/complex-tensor
     (t/compute-tensor [h w 2]
                       (fn [r c part]
                         (if (zero? part)
                           (+ re-min (* (- re-max re-min) (/ c (double (dec w)))))
                           (+ im-min (* (- im-max im-min) (/ r (double (dec h)))))))
                       :float64))))

(def mandelbrot-counts
  (fn [re-min re-max im-min im-max h w max-iter]
    (let [c (complex-grid re-min re-max im-min im-max h w)
          zero-grid (t/complex-tensor
                     (t/compute-tensor [h w 2]
                                       (fn [_ _ _] 0.0) :float64))]
      (loop [z zero-grid counts (t/zeros h w) k 0]
        (if (>= k max-iter)
          counts
          (let [z2 (t/clone (la/add (el/mul z z) c))
                mask (el/<= (el/abs z2) 2.0)]
            (recur z2 (t/clone (la/add counts mask)) (inc k))))))))

(def bench-functional
  (crit/quick-benchmark
   (mandelbrot-counts -2.0 0.7 -1.2 1.2
                      bench-h bench-w bench-max-iter)
   {}))

;; ### Layer 2: La Linea with `copy!`
;;
;; Same La Linea operations — `el/mul`, `la/add`, `la/sub`,
;; `el/abs`, `el/<=` — but instead of allocating new tensors
;; each iteration, we `dtype/copy!` into pre-allocated buffers.
;; This isolates the **allocation cost** (Layer 1 vs Layer 2).
;;
;; We split real and imaginary parts into separate tensors
;; and implement $z^2 + c$ component-wise:
;; $\text{re}(z^2 + c) = z_r^2 - z_i^2 + c_r$,
;; $\text{im}(z^2 + c) = 2 z_r z_i + c_i$.

(defn mandelbrot-counts-copy
  [re-min re-max im-min im-max h w max-iter]
  (let [h (long h) w (long w) max-iter (long max-iter)
        [cr ci] (real-imag-grids re-min re-max im-min im-max h w)
        zr     (t/clone (t/zeros h w))
        zi     (t/clone (t/zeros h w))
        counts (t/clone (t/zeros h w))
        tmp    (t/clone (t/zeros h w))]
    (dotimes [_ max-iter]
      ;; zr_new = zr² - zi² + cr
      (dtype/copy! (t/->tensor (la/add (la/sub (el/mul zr zr) (el/mul zi zi)) cr))
                   (t/->tensor tmp))
      ;; zi_new = 2·zr·zi + ci
      (dtype/copy! (t/->tensor (la/add (la/scale (el/mul zr zi) 2.0) ci))
                   (t/->tensor zi))
      (dtype/copy! (t/->tensor tmp) (t/->tensor zr))
      ;; counts += (|z|² ≤ 4) mask
      (let [mag2 (la/add (el/mul zr zr) (el/mul zi zi))
            mask (el/<= mag2 4.0)]
        (dtype/copy! (t/->tensor (la/add counts mask))
                     (t/->tensor counts))))
    counts))

(def bench-copy
  (crit/quick-benchmark
   (mandelbrot-counts-copy -2.0 0.7 -1.2 1.2
                           bench-h bench-w bench-max-iter)
   {}))

;; ### Layer 3: dtype-next with `copy!`
;;
;; No La Linea at all — pure dtype-next tensors with `dfn/*`,
;; `dfn/+`, `dfn/-` and `dtype/copy!`. This isolates the
;; **La Linea dispatch cost** (Layer 2 vs Layer 3): tape check,
;; `ensure-tensor` unwrapping, `complex?` type test,
;; `RealTensor` wrapping.

(defn mandelbrot-raw-dtype
  [re-min re-max im-min im-max h w max-iter]
  (let [h (long h) w (long w) max-iter (long max-iter)
        n (* h w)
        cr (dtt/reshape
            (dtype/clone
             (dtt/->tensor
              (double-array
               (for [r (range h) c (range w)]
                 (+ (double re-min)
                    (* (- (double re-max) (double re-min))
                       (/ (double c) (double (dec w)))))))
              {:datatype :float64}))
            [n])
        ci (dtt/reshape
            (dtype/clone
             (dtt/->tensor
              (double-array
               (for [r (range h) c (range w)]
                 (+ (double im-min)
                    (* (- (double im-max) (double im-min))
                       (/ (double r) (double (dec h)))))))
              {:datatype :float64}))
            [n])
        zr  (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
        zi  (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
        cnt (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
        tmp (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))]
    (dotimes [_ max-iter]
      (dtype/copy! (dfn/+ (dfn/- (dfn/* zr zr) (dfn/* zi zi)) cr) tmp)
      (dtype/copy! (dfn/+ (dfn/* 2.0 (dfn/* zr zi)) ci) zi)
      (dtype/copy! tmp zr)
      (let [mask (dtype/elemwise-cast
                  (dfn/<= (dfn/+ (dfn/* zr zr) (dfn/* zi zi)) 4.0)
                  :float64)]
        (dtype/copy! (dfn/+ cnt mask) cnt)))
    (dtt/reshape cnt [h w])))

(def bench-raw-dtype
  (crit/quick-benchmark
   (mandelbrot-raw-dtype -2.0 0.7 -1.2 1.2
                         bench-h bench-w bench-max-iter)
   {}))

;; ### Layer 4: Raw `double-array`
;;
;; Pure JVM primitives with `^doubles` type hints. A single
;; `dotimes` loop computes all the math for one pixel in one
;; pass — the CPU traverses memory only once per iteration.
;; This isolates the **reader protocol cost** (Layer 3 vs Layer 4).

(defn mandelbrot-raw-array
  [re-min re-max im-min im-max h w max-iter]
  (let [h (long h) w (long w) max-iter (long max-iter)
        n (* h w)
        [cr-grid ci-grid] (real-imag-grids re-min re-max im-min im-max h w)
        ^doubles cr (t/backing-array cr-grid)
        ^doubles ci (t/backing-array ci-grid)
        cnt-grid (t/clone (t/zeros h w))
        ^doubles cnt (t/backing-array cnt-grid)
        ^doubles zr (double-array n)
        ^doubles zi (double-array n)]
    (dotimes [_ max-iter]
      (dotimes [k n]
        (let [r (aget zr k)
              i (aget zi k)
              new-r (+ (- (* r r) (* i i)) (aget cr k))
              new-i (+ (* 2.0 r i) (aget ci k))]
          (aset zr k new-r)
          (aset zi k new-i)
          (when (<= (+ (* new-r new-r) (* new-i new-i)) 4.0)
            (aset cnt k (+ (aget cnt k) 1.0))))))
    cnt-grid))

(def bench-raw-array
  (crit/quick-benchmark
   (mandelbrot-raw-array -2.0 0.7 -1.2 1.2
                         bench-h bench-w bench-max-iter)
   {}))

;; ### Correctness check
;;
;; All four versions must produce the same counts.

(let [h 50 w 60 max-iter 30
      f (mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)
      c (mandelbrot-counts-copy -2.0 0.7 -1.2 1.2 h w max-iter)
      d (t/->real-tensor (mandelbrot-raw-dtype -2.0 0.7 -1.2 1.2 h w max-iter))
      r (mandelbrot-raw-array -2.0 0.7 -1.2 1.2 h w max-iter)]
  (and (la/close? f c 1e-10)
       (la/close? f d 1e-10)
       (la/close? f r 1e-10)))

(kind/test-last [true?])

;; ### Results

(def results
  (let [func-ms  (mean-ms bench-functional)
        copy-ms  (mean-ms bench-copy)
        dtype-ms (mean-ms bench-raw-dtype)
        raw-ms   (mean-ms bench-raw-array)]
    [{:layer "La Linea functional"
      :description "ComplexTensor, el/mul, la/add, t/clone"
      :median-ms (math/round func-ms)
      :vs-raw-array (format "%.1f×" (/ func-ms raw-ms))}
     {:layer "La Linea with copy!"
      :description "el/mul, la/add, dtype/copy!"
      :median-ms (math/round copy-ms)
      :vs-raw-array (format "%.1f×" (/ copy-ms raw-ms))}
     {:layer "Raw dtype-next"
      :description "dfn/*, dfn/+, dtype/copy!"
      :median-ms (math/round dtype-ms)
      :vs-raw-array (format "%.1f×" (/ dtype-ms raw-ms))}
     {:layer "Raw double-array"
      :description "^doubles, aset/aget, single-pass"
      :median-ms (math/round raw-ms)
      :vs-raw-array "1×"}]))

(kind/table results)

;; ## What this means
;;
;; Comparing adjacent layers isolates specific costs:
;;
;; - **Layer 1 vs 2** (functional vs copy!): the cost of
;;   **allocation** — `t/clone` on every iteration vs reusing
;;   pre-allocated buffers.
;;
;; - **Layer 2 vs 3** (La Linea copy! vs dtype-next copy!):
;;   the cost of **La Linea's dispatch** — tape recording check,
;;   `ensure-tensor` unwrapping, `complex?` type test, and
;;   `RealTensor` wrapping. These are constant-time per call.
;;
;; - **Layer 3 vs 4** (dtype-next copy! vs raw array): the cost
;;   of **reader protocol dispatch** and **memory passes**.
;;   Each `dfn/*` or `dfn/+` returns a lazy reader that is
;;   materialized by `dtype/copy!` in parallel across cores.
;;   The raw-array version fuses all operations into a single
;;   pass over memory.
;;
;; On modern JVMs (Java 25+), the JIT compiler devirtualizes
;; reader dispatch effectively — the per-element overhead of
;; going through `DoubleReader.readDouble` is small.
;; `dtype/copy!` also parallelizes across available cores,
;; which can offset the multi-pass cost.
;;
;; **The right default is Layer 1** — La Linea's functional API.
;; It is the simplest, most readable, and least error-prone.
;; Drop to lower layers only when profiling identifies the
;; inner loop as a bottleneck.

