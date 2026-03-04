(ns
 lalinea-book.fractals-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l34
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
 v5_l46
 (let
  [g (complex-grid -2.0 1.0 -1.5 1.5 3 3) raw (t/->tensor g)]
  {:shape (t/complex-shape g),
   :top-left-re (raw 0 0 0),
   :bottom-right-im (raw 2 2 1)}))


(deftest
 t6_l52
 (is
  ((fn
    [v]
    (and
     (= (:shape v) [3 3])
     (= (:top-left-re v) -2.0)
     (= (:bottom-right-im v) 1.5)))
   v5_l46)))


(def
 v8_l68
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
       [z2 (t/clone (el/+ (el/* z z) c)) mask (el/<= (el/abs z2) 2.0)]
       (recur z2 (t/clone (el/+ counts mask)) (inc k)))))))))


(def
 v10_l87
 (def
  counts->image
  (fn
   [counts h w max-iter]
   (t/compute-tensor
    [h w 3]
    (fn
     [r c ch]
     (let
      [cnt (long (counts r c))]
      (if
       (= cnt max-iter)
       0
       (let
        [t (/ (double cnt) max-iter)]
        (case
         (int ch)
         0
         (int
          (* 255 (* 0.5 (+ 1.0 (math/cos (* 2.0 math/PI (+ t 0.0)))))))
         1
         (int
          (*
           255
           (* 0.5 (+ 1.0 (math/cos (* 2.0 math/PI (+ t 0.33)))))))
         2
         (int
          (*
           255
           (* 0.5 (+ 1.0 (math/cos (* 2.0 math/PI (+ t 0.67))))))))))))
    :uint8))))


(def
 v12_l102
 (def
  mandelbrot-img
  (let
   [h
    300
    w
    400
    max-iter
    50
    counts
    (mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)]
   (counts->image counts h w max-iter))))


(def v13_l107 (bufimg/tensor->image mandelbrot-img))


(deftest
 t14_l109
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v13_l107)))


(def
 v16_l117
 (def
  mandelbrot-zoom
  (let
   [h
    300
    w
    400
    max-iter
    100
    counts
    (mandelbrot-counts -0.77 -0.73 0.05 0.08 h w max-iter)]
   (counts->image counts h w max-iter))))


(def v17_l122 (bufimg/tensor->image mandelbrot-zoom))


(deftest
 t18_l124
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v17_l122)))


(def
 v20_l133
 (def
  julia-counts
  (fn
   [c-re c-im re-min re-max im-min im-max h w max-iter]
   (let
    [z0
     (complex-grid re-min re-max im-min im-max h w)
     c-grid
     (t/complex-tensor
      (t/compute-tensor
       [h w 2]
       (fn [_ _ part] (if (zero? part) c-re c-im))
       :float64))]
    (loop
     [z z0 counts (t/zeros h w) k 0]
     (if
      (>= k max-iter)
      counts
      (let
       [z2
        (t/clone (el/+ (el/* z z) c-grid))
        mask
        (el/<= (el/abs z2) 2.0)]
       (recur z2 (t/clone (el/+ counts mask)) (inc k)))))))))


(def
 v22_l153
 (def
  julia-dendrite
  (let
   [h
    300
    w
    300
    max-iter
    80
    counts
    (julia-counts -0.7 0.27 -1.5 1.5 -1.5 1.5 h w max-iter)]
   (counts->image counts h w max-iter))))


(def v23_l158 (bufimg/tensor->image julia-dendrite))


(deftest
 t24_l160
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v23_l158)))


(def
 v26_l165
 (def
  julia-connected
  (let
   [h
    300
    w
    300
    max-iter
    80
    counts
    (julia-counts 0.355 0.355 -1.5 1.5 -1.5 1.5 h w max-iter)]
   (counts->image counts h w max-iter))))


(def v27_l170 (bufimg/tensor->image julia-connected))


(deftest
 t28_l172
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v27_l170)))


(def
 v30_l177
 (def
  julia-rabbit
  (let
   [h
    300
    w
    300
    max-iter
    80
    counts
    (julia-counts -0.4 0.6 -1.5 1.5 -1.5 1.5 h w max-iter)]
   (counts->image counts h w max-iter))))


(def v31_l182 (bufimg/tensor->image julia-rabbit))


(deftest
 t32_l184
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v31_l182)))


(def
 v34_l208
 (def
  newton-roots
  (fn
   [re-min re-max im-min im-max h w max-iter]
   (let
    [z0
     (complex-grid re-min re-max im-min im-max h w)
     one
     (t/complex-tensor
      (t/compute-tensor
       [h w 2]
       (fn [_ _ part] (if (zero? part) 1.0 0.0))
       :float64))
     roots
     [(t/complex 1.0 0.0)
      (t/complex
       (math/cos (/ (* 2.0 math/PI) 3.0))
       (math/sin (/ (* 2.0 math/PI) 3.0)))
      (t/complex
       (math/cos (/ (* 4.0 math/PI) 3.0))
       (math/sin (/ (* 4.0 math/PI) 3.0)))]]
    (let
     [z-final
      (loop
       [z (t/clone z0) k 0]
       (if
        (>= k max-iter)
        z
        (let
         [z2
          (el/* z z)
          z3
          (el/* z z2)
          fz
          (el/- z3 one)
          fpz
          (el/scale z2 3.0)]
         (recur (t/clone (el/- z (el// fz fpz))) (inc k)))))
      dists
      (mapv
       (fn
        [root]
        (let
         [root-grid
          (t/complex-tensor
           (t/compute-tensor
            [h w 2]
            (fn
             [_ _ part]
             (if
              (zero? part)
              (double (el/re root))
              (double (el/im root))))
            :float64))]
         (el/abs (el/- z-final root-grid))))
       roots)]
     (t/compute-matrix
      h
      w
      (fn
       [r c]
       (let
        [d0 ((dists 0) r c) d1 ((dists 1) r c) d2 ((dists 2) r c)]
        (cond
         (and (<= d0 d1) (<= d0 d2))
         0.0
         (and (<= d1 d0) (<= d1 d2))
         1.0
         :else
         2.0)))))))))


(def v36_l259 (def root-colors [[230 50 50] [50 180 50] [50 80 220]]))


(def
 v37_l264
 (def
  roots->image
  (fn
   [root-idx h w]
   (t/compute-tensor
    [h w 3]
    (fn
     [r c ch]
     (let
      [idx (long (root-idx r c))]
      (if (neg? idx) 0 (nth (nth root-colors idx) ch))))
    :uint8))))


(def
 v39_l275
 (def
  newton-img
  (let
   [h
    300
    w
    300
    max-iter
    30
    root-idx
    (newton-roots -2.0 2.0 -2.0 2.0 h w max-iter)]
   (roots->image root-idx h w))))


(def v40_l280 (bufimg/tensor->image newton-img))


(deftest
 t41_l282
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v40_l280)))


(def
 v43_l289
 (def
  newton-zoom
  (let
   [h
    300
    w
    300
    max-iter
    50
    root-idx
    (newton-roots -0.5 0.5 -0.5 0.5 h w max-iter)]
   (roots->image root-idx h w))))


(def v44_l294 (bufimg/tensor->image newton-zoom))


(deftest
 t45_l296
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v44_l294)))
