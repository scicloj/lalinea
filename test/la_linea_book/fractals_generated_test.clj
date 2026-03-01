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


(def v17_l134 (bufimg/tensor->image mandelbrot-zoom))


(deftest
 t18_l136
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v17_l134)))


(def
 v20_l145
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
        (dtype/clone (la/add (la/mul z z) c-grid))
        abs-t
        (la/abs z2)]
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
 v22_l171
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


(def v23_l176 (bufimg/tensor->image julia-dendrite))


(deftest
 t24_l178
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v23_l176)))


(def
 v26_l183
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


(def v27_l188 (bufimg/tensor->image julia-connected))


(deftest
 t28_l190
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v27_l188)))


(def
 v30_l195
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


(def v31_l200 (bufimg/tensor->image julia-rabbit))


(deftest
 t32_l202
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v31_l200)))


(def
 v34_l233
 (def
  complex-div
  (fn
   [a b]
   (let
    [ar
     (cx/re a)
     ai
     (cx/im a)
     br
     (cx/re b)
     bi
     (cx/im b)
     shape
     (dtype/shape ar)
     denom
     (dfn/+ (dfn/* br br) (dfn/* bi bi))]
    (cx/complex-tensor
     (tensor/reshape
      (dfn// (dfn/+ (dfn/* ar br) (dfn/* ai bi)) denom)
      shape)
     (tensor/reshape
      (dfn// (dfn/- (dfn/* ai br) (dfn/* ar bi)) denom)
      shape))))))


(def
 v36_l245
 (let
  [a
   (cx/complex-tensor (la/matrix [[3]]) (la/matrix [[4]]))
   b
   (cx/complex-tensor (la/matrix [[1]]) (la/matrix [[2]]))
   result
   (complex-div a b)]
  (and
   (< (Math/abs (- (tensor/mget (cx/re result) 0 0) 2.2)) 1.0E-10)
   (< (Math/abs (- (tensor/mget (cx/im result) 0 0) -0.4)) 1.0E-10))))


(deftest t37_l251 (is (true? v36_l245)))


(def
 v39_l255
 (def
  newton-roots
  (fn
   [re-min re-max im-min im-max h w max-iter]
   (let
    [z0
     (complex-grid re-min re-max im-min im-max h w)
     one
     (cx/complex-tensor
      (tensor/compute-tensor
       [h w 2]
       (fn [_ _ part] (if (zero? part) 1.0 0.0))
       :float64))
     roots
     [(cx/complex 1.0 0.0)
      (cx/complex
       (Math/cos (/ (* 2.0 Math/PI) 3.0))
       (Math/sin (/ (* 2.0 Math/PI) 3.0)))
      (cx/complex
       (Math/cos (/ (* 4.0 Math/PI) 3.0))
       (Math/sin (/ (* 4.0 Math/PI) 3.0)))]
     root-idx
     (int-array (* h w) -1)]
    (loop
     [z (dtype/clone z0) k 0]
     (if
      (>= k max-iter)
      (let
       [z-final z]
       (dotimes
        [r h]
        (dotimes
         [c w]
         (let
          [idx
           (+ (* r w) c)
           zr
           (tensor/mget (.tensor z-final) r c 0)
           zi
           (tensor/mget (.tensor z-final) r c 1)
           best
           (reduce
            (fn
             [best-i i]
             (let
              [root
               (nth roots i)
               dr
               (- zr (double (cx/re root)))
               di
               (- zi (double (cx/im root)))
               d
               (+ (* dr dr) (* di di))
               root-best
               (nth roots best-i)
               dbr
               (- zr (double (cx/re root-best)))
               dbi
               (- zi (double (cx/im root-best)))
               db
               (+ (* dbr dbr) (* dbi dbi))]
              (if (< d db) i best-i)))
            0
            [1 2])]
          (aset root-idx idx best))))
       root-idx)
      (let
       [z2
        (la/mul z z)
        z3
        (la/mul z z2)
        fz
        (la/sub z3 one)
        fpz
        (la/scale z2 3.0)
        z-next
        (dtype/clone (la/sub z (complex-div fz fpz)))]
       (recur z-next (inc k)))))))))


(def v41_l307 (def root-colors [[230 50 50] [50 180 50] [50 80 220]]))


(def
 v42_l312
 (def
  roots->image
  (fn
   [root-idx h w]
   (tensor/compute-tensor
    [h w 3]
    (fn
     [r c ch]
     (let
      [idx (aget root-idx (+ (* r w) c))]
      (if (neg? idx) 0 (nth (nth root-colors idx) ch))))
    :uint8))))


(def
 v44_l323
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


(def v45_l328 (bufimg/tensor->image newton-img))


(deftest
 t46_l330
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v45_l328)))


(def
 v48_l338
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


(def v49_l343 (bufimg/tensor->image newton-zoom))


(deftest
 t50_l345
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v49_l343)))
