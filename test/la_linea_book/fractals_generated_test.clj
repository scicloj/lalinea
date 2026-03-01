(ns
 la-linea-book.fractals-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l39
 (def
  complex-grid
  (fn
   [re-min re-max im-min im-max h w]
   (cx/complex-tensor
    (tensor/compute-tensor
     [h w 2]
     (fn
      [r c part]
      (if
       (zero? part)
       (+ re-min (* (- re-max re-min) (/ c (double (dec w)))))
       (+ im-min (* (- im-max im-min) (/ r (double (dec h)))))))
     :float64)))))


(def
 v5_l51
 (let
  [g (complex-grid -2.0 1.0 -1.5 1.5 3 3) raw (.tensor g)]
  {:shape (cx/complex-shape g),
   :top-left-re (tensor/mget raw 0 0 0),
   :bottom-right-im (tensor/mget raw 2 2 1)}))


(deftest
 t6_l57
 (is
  ((fn
    [v]
    (and
     (= (:shape v) [3 3])
     (= (:top-left-re v) -2.0)
     (= (:bottom-right-im v) 1.5)))
   v5_l51)))


(def
 v8_l74
 (def
  mandelbrot-counts
  (fn
   [re-min re-max im-min im-max h w max-iter]
   (let
    [c
     (complex-grid re-min re-max im-min im-max h w)
     counts
     (int-array (* h w) 0)
     zero-grid
     (cx/complex-tensor
      (tensor/compute-tensor [h w 2] (fn [_ _ _] 0.0) :float64))]
    (loop
     [z zero-grid k 0]
     (if
      (>= k max-iter)
      counts
      (let
       [z2 (dtype/clone (la/add (la/mul z z) c)) abs-t (la/abs z2)]
       (dotimes
        [r h]
        (dotimes
         [col w]
         (when
          (< (tensor/mget abs-t r col) 2.0)
          (let
           [idx (+ (* r w) col)]
           (aset counts idx (inc (aget counts idx)))))))
       (recur z2 (inc k)))))))))


(def
 v10_l99
 (def
  counts->image
  (fn
   [counts h w max-iter]
   (tensor/compute-tensor
    [h w 3]
    (fn
     [r c ch]
     (let
      [cnt (aget counts (+ (* r w) c))]
      (if
       (= cnt max-iter)
       0
       (let
        [t (/ (double cnt) max-iter)]
        (case
         (int ch)
         0
         (int
          (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.0)))))))
         1
         (int
          (*
           255
           (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.33)))))))
         2
         (int
          (*
           255
           (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.67))))))))))))
    :uint8))))


(def
 v12_l114
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


(def v13_l119 (bufimg/tensor->image mandelbrot-img))


(deftest
 t14_l121
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v13_l119)))


(def
 v16_l129
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
