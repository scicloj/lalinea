;; # Performance
;;
;; **Prerequisites**: [Fractals](fractals.html) (defines the
;; Mandelbrot functions used here).
;;
;; Where does the time go when we compute a Mandelbrot set?
;; This chapter benchmarks four approaches at different
;; abstraction levels — from La Linea's functional API down
;; to raw JVM primitive arrays — and identifies where
;; overhead lives at each layer.
;;
;; All benchmarks use [Criterium](https://github.com/hugoduncan/criterium)
;; for statistically sound measurements.

(ns lalinea-book.performance
  (:require
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.elementwise :as elem]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfn]
   [tech.v3.tensor :as dtt]
   [criterium.core :as crit]
   [scicloj.kindly.v4.kind :as kind]
   ;; Fractal implementations from the previous chapter:
   [lalinea-book.fractals :as fractals]))

;; ## Setup
;;
;; We use a 300 × 400 grid with 50 iterations — the same
;; parameters as the classic Mandelbrot view.

(def bench-h 300)
(def bench-w 400)
(def bench-n (* bench-h bench-w))
(def bench-max-iter 50)

(defn median-ms
  "Extract the sample mean from a Criterium result, in milliseconds."
  [bench-result]
  (* 1e3 (first (:sample-mean bench-result))))

;; ## The four layers
;;
;; ### Layer 1: La Linea functional
;;
;; ComplexTensor arithmetic with `la/mul`, `la/add`, `elem/le`.
;; Each iteration allocates new tensors via `t/clone`.

(def bench-functional
  (crit/quick-benchmark
   (fractals/mandelbrot-counts -2.0 0.7 -1.2 1.2
                               bench-h bench-w bench-max-iter)
   {}))

;; ### Layer 2: La Linea imperative
;;
;; Uses La Linea for grid construction (`t/compute-tensor`,
;; `t/clone`) and result wrapping (`t/->real-tensor`), but
;; drops to `t/backing-array` and raw `double[]` primitives
;; for the inner loop. Zero allocation in the hot path.

(def bench-imperative
  (crit/quick-benchmark
   (fractals/mandelbrot-counts-imperative
    -2.0 0.7 -1.2 1.2
    bench-h bench-w bench-max-iter)
   {}))

;; ### Layer 3: Raw dtype-next
;;
;; No La Linea at all — pure dtype-next tensors with `dfn/*`,
;; `dfn/+`, `dfn/-` and `dtype/copy!`. Separate `zr` and `zi`
;; flat tensors, `tmp` buffer for the aliasing issue.
;; This isolates whether La Linea adds overhead on top of
;; dtype-next.

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

(defn mandelbrot-raw-array
  [re-min re-max im-min im-max h w max-iter]
  (let [h (long h) w (long w) max-iter (long max-iter)
        n (* h w)
        cr-grid (t/clone
                 (t/compute-tensor
                  [h w]
                  (fn [r c]
                    (+ (double re-min)
                       (* (- (double re-max) (double re-min))
                          (/ (double c) (double (dec w))))))
                  :float64))
        ci-grid (t/clone
                 (t/compute-tensor
                  [h w]
                  (fn [r c]
                    (+ (double im-min)
                       (* (- (double im-max) (double im-min))
                          (/ (double r) (double (dec h))))))
                  :float64))
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
      f (fractals/mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)
      i (fractals/mandelbrot-counts-imperative -2.0 0.7 -1.2 1.2 h w max-iter)
      d (t/->real-tensor (mandelbrot-raw-dtype -2.0 0.7 -1.2 1.2 h w max-iter))
      r (mandelbrot-raw-array -2.0 0.7 -1.2 1.2 h w max-iter)]
  (and (la/close? f i 1e-10)
       (la/close? f d 1e-10)
       (la/close? f r 1e-10)))

(kind/test-last [true?])

;; ### Results

(def results
  (let [func-ms  (median-ms bench-functional)
        imp-ms   (median-ms bench-imperative)
        dtype-ms (median-ms bench-raw-dtype)
        raw-ms   (median-ms bench-raw-array)]
    [{:layer "La Linea functional"
      :description "ComplexTensor, la/mul, la/add, t/clone"
      :median-ms (Math/round func-ms)
      :vs-raw-array (format "%.0f×" (/ func-ms raw-ms))}
     {:layer "Raw dtype-next"
      :description "dfn/*, dfn/+, dtype/copy!"
      :median-ms (Math/round dtype-ms)
      :vs-raw-array (format "%.0f×" (/ dtype-ms raw-ms))}
     {:layer "La Linea imperative"
      :description "t/backing-array, aset/aget"
      :median-ms (Math/round imp-ms)
      :vs-raw-array (format "%.0f×" (/ imp-ms raw-ms))}
     {:layer "Raw double-array"
      :description "^doubles, aset/aget, single-pass"
      :median-ms (Math/round raw-ms)
      :vs-raw-array "1×"}]))

(kind/table results)

;; La Linea imperative and raw double-array should be within 2×:

(kind/test-last
 [(fn [_]
    (let [imp-ms (median-ms bench-imperative)
          raw-ms (median-ms bench-raw-array)]
      (< (/ imp-ms raw-ms) 2.0)))])

;; La Linea imperative and raw double-array should be within 1.5×
;; of each other — La Linea's setup/wrapping adds negligible overhead:

(kind/test-last
 [(fn [_]
    (let [imp-ms (median-ms bench-imperative)
          raw-ms (median-ms bench-raw-array)]
      (< (abs (- (/ imp-ms raw-ms) 1.0)) 0.5)))])

;; ## Where the time goes
;;
;; ### Reader dispatch
;;
;; A single element-wise addition on 120,000 elements.
;; The `dfn/+` path goes through dtype-next's reader protocol;
;; the raw path uses direct primitive array access.

(def micro-n bench-n)

(def bench-add-raw
  (let [a (double-array micro-n)
        b (double-array micro-n)
        c (double-array micro-n)]
    (crit/quick-benchmark
     (dotimes [k micro-n]
       (aset c k (+ (aget a k) (aget b k))))
     {})))

(def bench-add-copy
  (let [a (dtype/clone (dtt/->tensor (double-array micro-n) {:datatype :float64}))
        b (dtype/clone (dtt/->tensor (double-array micro-n) {:datatype :float64}))
        c (dtype/clone (dtt/->tensor (double-array micro-n) {:datatype :float64}))]
    (crit/quick-benchmark
     (dtype/copy! (dfn/+ a b) c)
     {})))

(let [raw-ns (* 1e6 (first (:sample-mean bench-add-raw)))
      copy-ns (* 1e6 (first (:sample-mean bench-add-copy)))]
  {:raw-per-element-ns (/ raw-ns micro-n)
   :copy-per-element-ns (/ copy-ns micro-n)
   :reader-overhead (format "%.0f×" (/ copy-ns raw-ns))})

;; ### La Linea dispatch overhead
;;
;; `la/add` + `t/clone` versus raw `dfn/+` + `dtype/copy!`
;; on the same data. The difference measures La Linea's dispatch
;; layers: tape check, `ensure-tensor`, `complex?`, `RealTensor` wrap.

(def bench-add-lalinea
  (let [a (t/clone (t/zeros bench-h bench-w))
        b (t/clone (t/zeros bench-h bench-w))]
    (crit/quick-benchmark
     (t/clone (la/add a b))
     {})))

(let [la-ms (median-ms bench-add-lalinea)
      copy-ms (median-ms bench-add-copy)]
  {:lalinea-add-ms la-ms
   :dtype-copy-ms copy-ms
   :ratio (format "%.2f×" (/ la-ms copy-ms))})

;; The ratio should be close to 1.0 — La Linea adds negligible cost:

(kind/test-last
 [(fn [{:keys [lalinea-add-ms dtype-copy-ms]}]
    (< (/ lalinea-add-ms dtype-copy-ms) 2.0))])

;; ### Overhead breakdown
;;
;; Comparing pairs of benchmarks isolates each overhead source:
;;
;; - **Allocation + ComplexTensor**: functional vs raw dtype-next
;;   (both use lazy readers; functional also allocates per iteration)
;; - **Reader dispatch + memory passes**: raw dtype-next vs raw array
;;   (the core dtype-next per-element overhead)
;; - **La Linea wrapping**: La Linea imperative vs raw array
;;   (setup, `t/backing-array`, `t/->real-tensor`)

(let [func-ms (median-ms bench-functional)
      dtype-ms (median-ms bench-raw-dtype)
      raw-ms (median-ms bench-raw-array)
      imp-ms (median-ms bench-imperative)]
  {:allocation-and-complex-ms (Math/round (- func-ms dtype-ms))
   :reader-dispatch-ms (Math/round (- dtype-ms raw-ms))
   :lalinea-wrapping-ms (Math/round (- imp-ms raw-ms))})

;; ## What this means
;;
;; **La Linea adds no measurable overhead on top of dtype-next.**
;; The dispatch layers — tape recording check, `ensure-tensor`
;; unwrapping, `complex?` type test, `RealTensor` wrapping —
;; are constant-time per call and negligible compared to the
;; per-element work.
;;
;; **The dominant cost is dtype-next's reader protocol dispatch.**
;; Each `dfn/*` or `dfn/+` wraps a lazy reader whose `.read(idx)`
;; method goes through protocol dispatch on every element access.
;; Raw `aget`/`aset` with `^doubles` hints compile to direct
;; JVM bytecode — no dispatch, no boxing.
;;
;; **Loop fusion matters.** The raw-array version does all the
;; math for one pixel in a single loop body: read zr, zi, cr, ci;
;; compute new values; write zr, zi, counts. One pass over memory.
;; The `dtype/copy!` approach makes four separate passes per
;; iteration (one per `copy!` call), loading and storing each
;; array multiple times.
;;
;; **When to drop to raw arrays.** La Linea's functional API is
;; the right default — it's simple, correct, and the allocation
;; overhead is modest. Drop to `t/backing-array` + raw primitives
;; only for tight inner loops where profiling shows the per-element
;; reader cost is the bottleneck. The pattern is:
;;
;; 1. Build grids with `t/compute-tensor`, `t/clone`
;; 2. Get `^doubles` via `t/backing-array`
;; 3. `dotimes` loop with `aget`/`aset`
;; 4. Wrap result with `t/->real-tensor`
