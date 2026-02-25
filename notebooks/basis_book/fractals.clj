;; # Fractals with Complex Tensors
;;
;; Fractals like the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set)
;; and [Julia sets](https://en.wikipedia.org/wiki/Julia_set)
;; are defined by iterating a complex function
;; $z_{n+1} = z_n^2 + c$ and checking whether the orbit
;; escapes to infinity.
;;
;; This chapter computes fractals using **ComplexTensor** arithmetic.
;; The iteration loop is imperative (each step depends on the
;; previous), but within each step the computation is **vectorized**
;; across the entire complex plane — `la/mul` and `la/add` operate on
;; every grid point simultaneously.

(ns basis-book.fractals
  (:require
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.basis.linalg :as la]
   [scicloj.basis.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Tensor ↔ BufferedImage conversion:
   [tech.v3.libs.buffered-image :as bufimg]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Building a complex plane grid
;;
;; We represent the complex plane as a ComplexTensor of shape
;; `[height width]`, where each element is a complex number
;; $x + yi$. `tensor/compute-tensor` fills the underlying
;; `[h w 2]` real tensor with the real and imaginary parts.

(def complex-grid
  (fn [re-min re-max im-min im-max h w]
    (cx/complex-tensor
     (tensor/compute-tensor [h w 2]
                            (fn [r c part]
                              (if (zero? part)
                                (+ re-min (* (- re-max re-min) (/ c (double (dec w)))))
                                (+ im-min (* (- im-max im-min) (/ r (double (dec h)))))))
                            :float64))))

;; A small test grid — the four corners should span the range:

(let [g (complex-grid -2.0 1.0 -1.5 1.5 3 3)
      raw (.tensor g)]
  {:shape (cx/complex-shape g)
   :top-left-re (tensor/mget raw 0 0 0)
   :bottom-right-im (tensor/mget raw 2 2 1)})

(kind/test-last
 [(fn [v] (and (= (:shape v) [3 3])
               (= (:top-left-re v) -2.0)
               (= (:bottom-right-im v) 1.5)))])

;; ## Mandelbrot set
;;
;; The Mandelbrot set is the set of $c$ values for which the
;; orbit of $z_0 = 0$ under $z_{n+1} = z_n^2 + c$ does not
;; escape. We color each pixel by **how many iterations**
;; it takes to escape $|z| > 2$.
;;
;; The inner loop is imperative: we maintain a mutable `counts`
;; array and update it at each iteration. But the complex
;; arithmetic — squaring and adding the entire grid — is
;; vectorized through ComplexTensor operations.

(def mandelbrot-counts
  (fn [re-min re-max im-min im-max h w max-iter]
    (let [c (complex-grid re-min re-max im-min im-max h w)
          counts (int-array (* h w) 0)
          zero-grid (cx/complex-tensor
                     (tensor/compute-tensor [h w 2]
                                            (fn [_ _ _] 0.0) :float64))]
      (loop [z zero-grid k 0]
        (if (>= k max-iter)
          counts
          (let [z2 (dtype/clone (la/add (la/mul z z) c))
                abs-t (la/abs z2)]
            (dotimes [r h]
              (dotimes [col w]
                (when (< (tensor/mget abs-t r col) 2.0)
                  (let [idx (+ (* r w) col)]
                    (aset counts idx (inc (aget counts idx)))))))
            (recur z2 (inc k))))))))

;; ### Rendering
;;
;; Map iteration counts to colors. Points inside the set
;; (count = max-iter) are black; others are colored by
;; how quickly they escape.

(def counts->image
  (fn [counts h w max-iter]
    (tensor/compute-tensor [h w 3]
                           (fn [r c ch]
                             (let [cnt (aget counts (+ (* r w) c))]
                               (if (= cnt max-iter) 0
                                   (let [t (/ (double cnt) max-iter)]
                                     (case (int ch)
                                       0 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.0)))))))
                                       1 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.33)))))))
                                       2 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.67))))))))))))
                           :uint8)))

;; ### The classic view

(def mandelbrot-img
  (let [h 300 w 400 max-iter 50
        counts (mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image mandelbrot-img)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Zooming in
;;
;; The Mandelbrot set has infinite detail at every scale.
;; Here's a zoom into the "Seahorse Valley":

(def mandelbrot-zoom
  (let [h 300 w 400 max-iter 100
        counts (mandelbrot-counts -0.77 -0.73 0.05 0.08 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image mandelbrot-zoom)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Julia sets
;;
;; A [Julia set](https://en.wikipedia.org/wiki/Julia_set) uses a
;; **fixed** $c$ and varies the starting point $z_0$. Each choice
;; of $c$ produces a different fractal.

(def julia-counts
  (fn [c-re c-im re-min re-max im-min im-max h w max-iter]
    (let [z0 (complex-grid re-min re-max im-min im-max h w)
          c-grid (cx/complex-tensor
                  (tensor/compute-tensor [h w 2]
                                         (fn [_ _ part] (if (zero? part) c-re c-im))
                                         :float64))
          counts (int-array (* h w) 0)]
      (loop [z z0 k 0]
        (if (>= k max-iter)
          counts
          (let [z2 (dtype/clone (la/add (la/mul z z) c-grid))
                abs-t (la/abs z2)]
            (dotimes [r h]
              (dotimes [col w]
                (when (< (tensor/mget abs-t r col) 2.0)
                  (let [idx (+ (* r w) col)]
                    (aset counts idx (inc (aget counts idx)))))))
            (recur z2 (inc k))))))))

;; ### Gallery of Julia sets
;;
;; Different values of $c$ produce strikingly different shapes.

;; $c = -0.7 + 0.27i$ — a "dendrite" Julia set:

(def julia-dendrite
  (let [h 300 w 300 max-iter 80
        counts (julia-counts -0.7 0.27 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-dendrite)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; $c = 0.355 + 0.355i$ — a connected Julia set:

(def julia-connected
  (let [h 300 w 300 max-iter 80
        counts (julia-counts 0.355 0.355 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-connected)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; $c = -0.4 + 0.6i$ — a "rabbit" Julia set:

(def julia-rabbit
  (let [h 300 w 300 max-iter 80
        counts (julia-counts -0.4 0.6 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-rabbit)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Dragon curve
;;
;; The [dragon curve](https://en.wikipedia.org/wiki/Dragon_curve)
;; can be constructed from complex arithmetic. The key idea
;; (inspired by [this approach](https://rosettacode.org/wiki/Dragon_curve#Version_#1.)):
;; powers of $(1+i)$ give a sequence of points that spiral
;; outward, and the binary representation of each integer
;; determines how to combine these points into the dragon.
;;
;; We compute all dragon points at once by iterating over
;; the bit positions. This is a functional accumulation
;; with no mutable state.

(def dragon-n 512)

(def bit-at
  (fn [n pos]
    (bit-and (int (/ n (Math/pow 2 pos))) 1)))

(def dragon-points
  (let [;; Precompute powers of (1+i)
        pts (mapv (fn [k]
                    (let [r (Math/pow (Math/sqrt 2.0) k)
                          theta (* k (/ Math/PI 4))]
                      [(* r (Math/cos theta))
                       (* r (Math/sin theta))]))
                  (range 20))
        ;; For each n, compute the dragon point by combining
        ;; bits and powers
        compute-point
        (fn [n]
          (let [nbits (count (Integer/toBinaryString (max 1 n)))]
            (loop [pos nbits re 0.0 im 0.0]
              (if (neg? pos)
                [re im]
                (let [b (bit-at n pos)
                      bn (bit-at n (inc pos))]
                  (if (zero? b)
                    (recur (dec pos) re im)
                    (let [[pr pi] (nth pts pos [0 0])
                          ;; Turn based on bit flip
                          [tr ti] (if (= b bn) [1.0 0.0] [0.0 1.0])
                          ;; Complex multiply: turn * pt
                          new-re (- (* tr pr) (* ti pi))
                          new-im (+ (* tr pi) (* ti pr))]
                      (recur (dec pos)
                             (+ re new-re)
                             (+ im new-im)))))))))
        points (mapv compute-point (range dragon-n))]
    points))

;; Render the dragon curve as an SVG path:

(let [pts dragon-points
      xs (map first pts)
      ys (map second pts)
      pad 1.0
      x-min (- (apply min xs) pad)
      y-min (- (apply min ys) pad)
      vb-w (+ (- (apply max xs) (apply min xs)) (* 2 pad))
      vb-h (+ (- (apply max ys) (apply min ys)) (* 2 pad))
      sw (* 0.04 (max vb-w vb-h))]
  (kind/hiccup
   [:svg {:width 500 :height 500
          :viewBox (str x-min " " y-min " " vb-w " " vb-h)
          :preserveAspectRatio "xMidYMid meet"}
    [:rect {:x x-min :y y-min :width vb-w :height vb-h
            :fill "#f8f8f8"}]
    [:path {:d (str "M" (ffirst pts) " " (second (first pts))
                    (apply str (map (fn [[x y]] (str " L" x " " y))
                                    (rest pts))))
            :stroke "#2244aa"
            :stroke-width sw
            :fill "none"
            :stroke-linejoin "round"}]]))

(kind/test-last
 [(fn [_] (vector? dragon-points))])
