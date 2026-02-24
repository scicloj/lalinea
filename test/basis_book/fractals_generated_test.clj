(ns
 basis-book.fractals-generated-test
 (:require
  [scicloj.basis.impl.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l37
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
 v5_l49
 (let
  [g (complex-grid -2.0 1.0 -1.5 1.5 3 3) raw (.tensor g)]
  {:shape (cx/complex-shape g),
   :top-left-re (tensor/mget raw 0 0 0),
   :bottom-right-im (tensor/mget raw 2 2 1)}))


(deftest
 t6_l55
 (is
  ((fn
    [v]
    (and
     (= (:shape v) [3 3])
     (= (:top-left-re v) -2.0)
     (= (:bottom-right-im v) 1.5)))
   v5_l49)))


(def
 v8_l72
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
       [z2 (dtype/clone (cx/add (cx/mul z z) c)) abs-t (cx/abs z2)]
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
 v10_l97
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
 v12_l112
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


(def v13_l117 (bufimg/tensor->image mandelbrot-img))


(deftest
 t14_l119
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v13_l117)))


(def
 v16_l127
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


(def v17_l132 (bufimg/tensor->image mandelbrot-zoom))


(deftest
 t18_l134
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v17_l132)))


(def
 v20_l143
 (def
  julia-counts
  (fn
   [c-re c-im re-min re-max im-min im-max h w max-iter]
   (let
    [z0
     (complex-grid re-min re-max im-min im-max h w)
     c-grid
     (cx/complex-tensor
      (tensor/compute-tensor
       [h w 2]
       (fn [_ _ part] (if (zero? part) c-re c-im))
       :float64))
     counts
     (int-array (* h w) 0)]
    (loop
     [z z0 k 0]
     (if
      (>= k max-iter)
      counts
      (let
       [z2
        (dtype/clone (cx/add (cx/mul z z) c-grid))
        abs-t
        (cx/abs z2)]
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
 v22_l169
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


(def v23_l174 (bufimg/tensor->image julia-dendrite))


(deftest
 t24_l176
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v23_l174)))


(def
 v26_l181
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


(def v27_l186 (bufimg/tensor->image julia-connected))


(deftest
 t28_l188
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v27_l186)))


(def
 v30_l193
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


(def v31_l198 (bufimg/tensor->image julia-rabbit))


(deftest
 t32_l200
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v31_l198)))


(def v34_l216 (def dragon-n 512))


(def
 v35_l218
 (def bit-at (fn [n pos] (bit-and (int (/ n (Math/pow 2 pos))) 1))))


(def
 v36_l222
 (def
  dragon-points
  (let
   [pts
    (mapv
     (fn
      [k]
      (let
       [r (Math/pow (Math/sqrt 2.0) k) theta (* k (/ Math/PI 4))]
       [(* r (Math/cos theta)) (* r (Math/sin theta))]))
     (range 20))
    compute-point
    (fn
     [n]
     (let
      [nbits (count (Integer/toBinaryString (max 1 n)))]
      (loop
       [pos nbits re 0.0 im 0.0]
       (if
        (neg? pos)
        [re im]
        (let
         [b (bit-at n pos) bn (bit-at n (inc pos))]
         (if
          (zero? b)
          (recur (dec pos) re im)
          (let
           [[pr pi]
            (nth pts pos [0 0])
            [tr ti]
            (if (= b bn) [1.0 0.0] [0.0 1.0])
            new-re
            (- (* tr pr) (* ti pi))
            new-im
            (+ (* tr pi) (* ti pr))]
           (recur (dec pos) (+ re new-re) (+ im new-im)))))))))
    points
    (mapv compute-point (range dragon-n))]
   points)))


(def
 v38_l256
 (let
  [pts
   dragon-points
   xs
   (map first pts)
   ys
   (map second pts)
   pad
   1.0
   x-min
   (- (apply min xs) pad)
   y-min
   (- (apply min ys) pad)
   vb-w
   (+ (- (apply max xs) (apply min xs)) (* 2 pad))
   vb-h
   (+ (- (apply max ys) (apply min ys)) (* 2 pad))
   sw
   (* 0.04 (max vb-w vb-h))]
  (kind/hiccup
   [:svg
    {:width 500,
     :height 500,
     :viewBox (str x-min " " y-min " " vb-w " " vb-h),
     :preserveAspectRatio "xMidYMid meet"}
    [:rect
     {:x x-min, :y y-min, :width vb-w, :height vb-h, :fill "#f8f8f8"}]
    [:path
     {:d
      (str
       "M"
       (ffirst pts)
       " "
       (second (first pts))
       (apply str (map (fn [[x y]] (str " L" x " " y)) (rest pts)))),
      :stroke "#2244aa",
      :stroke-width sw,
      :fill "none",
      :stroke-linejoin "round"}]])))


(deftest t39_l279 (is ((fn [_] (vector? dragon-points)) v38_l256)))
