(ns
 lalinea-book.performance-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as dtt]
  [criterium.core :as crit]
  [scicloj.kindly.v4.kind :as kind]
  [lalinea-book.fractals :as fractals]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (def bench-h 300))


(def v4_l34 (def bench-w 400))


(def v5_l35 (def bench-n (* bench-h bench-w)))


(def v6_l36 (def bench-max-iter 50))


(def
 v7_l38
 (defn
  median-ms
  "Extract the sample mean from a Criterium result, in milliseconds."
  [bench-result]
  (* 1000.0 (first (:sample-mean bench-result)))))


(def
 v9_l50
 (def
  bench-functional
  (crit/quick-benchmark
   (fractals/mandelbrot-counts
    -2.0
    0.7
    -1.2
    1.2
    bench-h
    bench-w
    bench-max-iter)
   {})))


(def
 v11_l63
 (def
  bench-imperative
  (crit/quick-benchmark
   (fractals/mandelbrot-counts-imperative
    -2.0
    0.7
    -1.2
    1.2
    bench-h
    bench-w
    bench-max-iter)
   {})))


(def
 v13_l78
 (defn
  mandelbrot-raw-dtype
  [re-min re-max im-min im-max h w max-iter]
  (let
   [h
    (long h)
    w
    (long w)
    max-iter
    (long max-iter)
    n
    (* h w)
    cr
    (dtt/reshape
     (dtype/clone
      (dtt/->tensor
       (double-array
        (for
         [r (range h) c (range w)]
         (+
          (double re-min)
          (*
           (- (double re-max) (double re-min))
           (/ (double c) (double (dec w)))))))
       {:datatype :float64}))
     [n])
    ci
    (dtt/reshape
     (dtype/clone
      (dtt/->tensor
       (double-array
        (for
         [r (range h) c (range w)]
         (+
          (double im-min)
          (*
           (- (double im-max) (double im-min))
           (/ (double r) (double (dec h)))))))
       {:datatype :float64}))
     [n])
    zr
    (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
    zi
    (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
    cnt
    (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))
    tmp
    (dtype/clone (dtt/->tensor (double-array n) {:datatype :float64}))]
   (dotimes
    [_ max-iter]
    (dtype/copy! (dfn/+ (dfn/- (dfn/* zr zr) (dfn/* zi zi)) cr) tmp)
    (dtype/copy! (dfn/+ (dfn/* 2.0 (dfn/* zr zi)) ci) zi)
    (dtype/copy! tmp zr)
    (let
     [mask
      (dtype/elemwise-cast
       (dfn/<= (dfn/+ (dfn/* zr zr) (dfn/* zi zi)) 4.0)
       :float64)]
     (dtype/copy! (dfn/+ cnt mask) cnt)))
   (dtt/reshape cnt [h w]))))


(def
 v14_l116
 (def
  bench-raw-dtype
  (crit/quick-benchmark
   (mandelbrot-raw-dtype
    -2.0
    0.7
    -1.2
    1.2
    bench-h
    bench-w
    bench-max-iter)
   {})))


(def
 v16_l128
 (defn
  mandelbrot-raw-array
  [re-min re-max im-min im-max h w max-iter]
  (let
   [h
    (long h)
    w
    (long w)
    max-iter
    (long max-iter)
    n
    (* h w)
    cr-grid
    (t/clone
     (t/compute-tensor
      [h w]
      (fn
       [r c]
       (+
        (double re-min)
        (*
         (- (double re-max) (double re-min))
         (/ (double c) (double (dec w))))))
      :float64))
    ci-grid
    (t/clone
     (t/compute-tensor
      [h w]
      (fn
       [r c]
       (+
        (double im-min)
        (*
         (- (double im-max) (double im-min))
         (/ (double r) (double (dec h))))))
      :float64))
    cr
    (t/backing-array cr-grid)
    ci
    (t/backing-array ci-grid)
    cnt-grid
    (t/clone (t/zeros h w))
    cnt
    (t/backing-array cnt-grid)
    zr
    (double-array n)
    zi
    (double-array n)]
   (dotimes
    [_ max-iter]
    (dotimes
     [k n]
     (let
      [r
       (aget zr k)
       i
       (aget zi k)
       new-r
       (+ (- (* r r) (* i i)) (aget cr k))
       new-i
       (+ (* 2.0 r i) (aget ci k))]
      (aset zr k new-r)
      (aset zi k new-i)
      (when
       (<= (+ (* new-r new-r) (* new-i new-i)) 4.0)
       (aset cnt k (+ (aget cnt k) 1.0))))))
   cnt-grid)))


(def
 v17_l166
 (def
  bench-raw-array
  (crit/quick-benchmark
   (mandelbrot-raw-array
    -2.0
    0.7
    -1.2
    1.2
    bench-h
    bench-w
    bench-max-iter)
   {})))


(def
 v19_l176
 (let
  [h
   50
   w
   60
   max-iter
   30
   f
   (fractals/mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)
   i
   (fractals/mandelbrot-counts-imperative
    -2.0
    0.7
    -1.2
    1.2
    h
    w
    max-iter)
   d
   (t/->real-tensor
    (mandelbrot-raw-dtype -2.0 0.7 -1.2 1.2 h w max-iter))
   r
   (mandelbrot-raw-array -2.0 0.7 -1.2 1.2 h w max-iter)]
  (and
   (la/close? f i 1.0E-10)
   (la/close? f d 1.0E-10)
   (la/close? f r 1.0E-10))))


(deftest t20_l185 (is (true? v19_l176)))


(def
 v22_l189
 (def
  results
  (let
   [func-ms
    (median-ms bench-functional)
    imp-ms
    (median-ms bench-imperative)
    dtype-ms
    (median-ms bench-raw-dtype)
    raw-ms
    (median-ms bench-raw-array)]
   [{:layer "La Linea functional",
     :description "ComplexTensor, la/mul, la/add, t/clone",
     :median-ms (Math/round func-ms),
     :vs-raw-array (format "%.0f×" (/ func-ms raw-ms))}
    {:layer "Raw dtype-next",
     :description "dfn/*, dfn/+, dtype/copy!",
     :median-ms (Math/round dtype-ms),
     :vs-raw-array (format "%.0f×" (/ dtype-ms raw-ms))}
    {:layer "La Linea imperative",
     :description "t/backing-array, aset/aget",
     :median-ms (Math/round imp-ms),
     :vs-raw-array (format "%.0f×" (/ imp-ms raw-ms))}
    {:layer "Raw double-array",
     :description "^doubles, aset/aget, single-pass",
     :median-ms (Math/round raw-ms),
     :vs-raw-array "1×"}])))


(def v23_l211 (kind/table results))


(deftest
 t25_l215
 (is
  ((fn
    [_]
    (let
     [imp-ms
      (median-ms bench-imperative)
      raw-ms
      (median-ms bench-raw-array)]
     (< (/ imp-ms raw-ms) 2.0)))
   v23_l211)))


(deftest
 t27_l224
 (is
  ((fn
    [_]
    (let
     [imp-ms
      (median-ms bench-imperative)
      raw-ms
      (median-ms bench-raw-array)]
     (< (abs (- (/ imp-ms raw-ms) 1.0)) 0.5)))
   v23_l211)))


(def v29_l238 (def micro-n bench-n))


(def
 v30_l240
 (def
  bench-add-raw
  (let
   [a
    (double-array micro-n)
    b
    (double-array micro-n)
    c
    (double-array micro-n)]
   (crit/quick-benchmark
    (dotimes [k micro-n] (aset c k (+ (aget a k) (aget b k))))
    {}))))


(def
 v31_l249
 (def
  bench-add-copy
  (let
   [a
    (dtype/clone
     (dtt/->tensor (double-array micro-n) {:datatype :float64}))
    b
    (dtype/clone
     (dtt/->tensor (double-array micro-n) {:datatype :float64}))
    c
    (dtype/clone
     (dtt/->tensor (double-array micro-n) {:datatype :float64}))]
   (crit/quick-benchmark (dtype/copy! (dfn/+ a b) c) {}))))


(def
 v32_l257
 (let
  [raw-ns
   (* 1000000.0 (first (:sample-mean bench-add-raw)))
   copy-ns
   (* 1000000.0 (first (:sample-mean bench-add-copy)))]
  {:raw-per-element-ns (/ raw-ns micro-n),
   :copy-per-element-ns (/ copy-ns micro-n),
   :reader-overhead (format "%.0f×" (/ copy-ns raw-ns))}))


(def
 v34_l269
 (def
  bench-add-lalinea
  (let
   [a
    (t/clone (t/zeros bench-h bench-w))
    b
    (t/clone (t/zeros bench-h bench-w))]
   (crit/quick-benchmark (t/clone (la/add a b)) {}))))


(def
 v35_l276
 (let
  [la-ms
   (median-ms bench-add-lalinea)
   copy-ms
   (median-ms bench-add-copy)]
  {:lalinea-add-ms la-ms,
   :dtype-copy-ms copy-ms,
   :ratio (format "%.2f×" (/ la-ms copy-ms))}))


(deftest
 t37_l284
 (is
  ((fn
    [{:keys [lalinea-add-ms dtype-copy-ms]}]
    (< (/ lalinea-add-ms dtype-copy-ms) 2.0))
   v35_l276)))


(def
 v39_l299
 (let
  [func-ms
   (median-ms bench-functional)
   dtype-ms
   (median-ms bench-raw-dtype)
   raw-ms
   (median-ms bench-raw-array)
   imp-ms
   (median-ms bench-imperative)]
  {:allocation-and-complex-ms (Math/round (- func-ms dtype-ms)),
   :reader-dispatch-ms (Math/round (- dtype-ms raw-ms)),
   :lalinea-wrapping-ms (Math/round (- imp-ms raw-ms))}))
