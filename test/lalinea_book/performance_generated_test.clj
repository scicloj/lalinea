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
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (def bench-h 300))


(def v4_l32 (def bench-w 400))


(def v5_l33 (def bench-n (* bench-h bench-w)))


(def v6_l34 (def bench-max-iter 50))


(def
 v7_l36
 (defn
  median-ms
  "Extract the sample mean from a Criterium result, in milliseconds."
  [bench-result]
  (* 1000.0 (first (:sample-mean bench-result)))))


(def
 v9_l44
 (defn
  real-imag-grids
  [re-min re-max im-min im-max h w]
  [(t/clone
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
     :float64))]))


(def
 v11_l67
 (def
  complex-grid
  (fn
   [re-min re-max im-min im-max h w]
   (t/complex-tensor
    (t/compute-tensor
     [h w 2]
     (fn
      [r c part]
      (if
       (zero? part)
       (+ re-min (* (- re-max re-min) (/ c (double (dec w)))))
       (+ im-min (* (- im-max im-min) (/ r (double (dec h)))))))
     :float64)))))


(def
 v12_l77
 (def
  mandelbrot-counts
  (fn
   [re-min re-max im-min im-max h w max-iter]
   (let
    [c
     (complex-grid re-min re-max im-min im-max h w)
     zero-grid
     (t/complex-tensor
      (t/compute-tensor [h w 2] (fn [_ _ _] 0.0) :float64))]
    (loop
     [z zero-grid counts (t/zeros h w) k 0]
     (if
      (>= k max-iter)
      counts
      (let
       [z2
        (t/clone (la/add (la/mul z z) c))
        mask
        (elem/le (la/abs z2) 2.0)]
       (recur z2 (t/clone (la/add counts mask)) (inc k)))))))))


(def
 v13_l90
 (def
  bench-functional
  (crit/quick-benchmark
   (mandelbrot-counts -2.0 0.7 -1.2 1.2 bench-h bench-w bench-max-iter)
   {})))


(def
 v15_l108
 (defn
  mandelbrot-counts-copy
  [re-min re-max im-min im-max h w max-iter]
  (let
   [h
    (long h)
    w
    (long w)
    max-iter
    (long max-iter)
    [cr ci]
    (real-imag-grids re-min re-max im-min im-max h w)
    zr
    (t/clone (t/zeros h w))
    zi
    (t/clone (t/zeros h w))
    counts
    (t/clone (t/zeros h w))
    tmp
    (t/clone (t/zeros h w))]
   (dotimes
    [_ max-iter]
    (dtype/copy!
     (t/->tensor (la/add (la/sub (la/mul zr zr) (la/mul zi zi)) cr))
     (t/->tensor tmp))
    (dtype/copy!
     (t/->tensor (la/add (la/scale (la/mul zr zi) 2.0) ci))
     (t/->tensor zi))
    (dtype/copy! (t/->tensor tmp) (t/->tensor zr))
    (let
     [mag2
      (la/add (la/mul zr zr) (la/mul zi zi))
      mask
      (elem/le mag2 4.0)]
     (dtype/copy!
      (t/->tensor (la/add counts mask))
      (t/->tensor counts))))
   counts)))


(def
 v16_l131
 (def
  bench-copy
  (crit/quick-benchmark
   (mandelbrot-counts-copy
    -2.0
    0.7
    -1.2
    1.2
    bench-h
    bench-w
    bench-max-iter)
   {})))


(def
 v18_l145
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
 v19_l183
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
 v21_l196
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
    [cr-grid ci-grid]
    (real-imag-grids re-min re-max im-min im-max h w)
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
 v22_l219
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
 v24_l229
 (let
  [h
   50
   w
   60
   max-iter
   30
   f
   (mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)
   c
   (mandelbrot-counts-copy -2.0 0.7 -1.2 1.2 h w max-iter)
   d
   (t/->real-tensor
    (mandelbrot-raw-dtype -2.0 0.7 -1.2 1.2 h w max-iter))
   r
   (mandelbrot-raw-array -2.0 0.7 -1.2 1.2 h w max-iter)]
  (and
   (la/close? f c 1.0E-10)
   (la/close? f d 1.0E-10)
   (la/close? f r 1.0E-10))))


(deftest t25_l238 (is (true? v24_l229)))


(def
 v27_l242
 (def
  results
  (let
   [func-ms
    (median-ms bench-functional)
    copy-ms
    (median-ms bench-copy)
    dtype-ms
    (median-ms bench-raw-dtype)
    raw-ms
    (median-ms bench-raw-array)]
   [{:layer "La Linea functional",
     :description "ComplexTensor, la/mul, la/add, t/clone",
     :median-ms (math/round func-ms),
     :vs-raw-array (format "%.1f×" (/ func-ms raw-ms))}
    {:layer "La Linea with copy!",
     :description "la/mul, la/add, dtype/copy!",
     :median-ms (math/round copy-ms),
     :vs-raw-array (format "%.1f×" (/ copy-ms raw-ms))}
    {:layer "Raw dtype-next",
     :description "dfn/*, dfn/+, dtype/copy!",
     :median-ms (math/round dtype-ms),
     :vs-raw-array (format "%.1f×" (/ dtype-ms raw-ms))}
    {:layer "Raw double-array",
     :description "^doubles, aset/aget, single-pass",
     :median-ms (math/round raw-ms),
     :vs-raw-array "1×"}])))


(def v28_l264 (kind/table results))
