;; # Image Processing with Tensors
;;
;; An image is a tensor — a 3D array of shape `[height width channels]`.
;; This chapter builds image processing tools using dtype-next tensors,
;; element-wise `dfn` operations, and `t/matrix` for convolution kernels.
;; All images are synthetic: no external files needed.

(ns lalinea-book.image-processing
  (:require
   ;; La Linea (https://github.com/scicloj/lalinea):
   [scicloj.lalinea.linalg :as la]
   [scicloj.lalinea.tensor :as t]
   [scicloj.lalinea.elementwise :as elem]
   ;; Tensor ↔ BufferedImage conversion:
   [tech.v3.libs.buffered-image :as bufimg]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]
   ;; Visualization helpers:
   [scicloj.lalinea.vis :as vis]
   [clojure.math :as math]))

;; ## Synthetic test images
;;
;; `t/compute-tensor` creates a tensor by calling a function
;; at every position — the image equivalent of `make-reader`.

;; ### Color gradient

(def gradient
  (t/compute-tensor [200 200 3]
                    (fn [r c ch]
                      (case (int ch)
                        0 (int (* 255 (/ r 200.0)))   ;; red: top→bottom
                        1 (int (* 255 (/ c 200.0)))   ;; green: left→right
                        2 128))                        ;; blue: constant
                    :uint8))

(bufimg/tensor->image gradient)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Checkerboard

(def checkerboard
  (let [size 200 sq 25]
    (t/compute-tensor [size size 3]
                      (fn [r c _ch]
                        (if (even? (+ (quot r sq) (quot c sq))) 240 30))
                      :uint8)))

(bufimg/tensor->image checkerboard)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Circle on dark background

(def circle-img
  (let [size 200 cx 100 cy 100 radius 60]
    (t/compute-tensor [size size 3]
                      (fn [r c ch]
                        (let [dr (- r cy) dc (- c cx)
                              dist (math/sqrt (+ (* dr dr) (* dc dc)))]
                          (if (<= dist radius)
                            (case (int ch)
                              0 50             ;; dark red
                              1 180            ;; bright green
                              2 220)           ;; bright blue
                            20)))              ;; dark background
                      :uint8)))

(bufimg/tensor->image circle-img)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Channel manipulation
;;
;; Tensors support zero-copy slicing. `t/select` extracts
;; a slice along any axis without copying data.

;; ### Extract individual channels
;;
;; `vis/extract-channel` extracts one channel from an `[h w 3]`
;; image and replicates it into a grayscale `[h w 3]` tensor.

(bufimg/tensor->image (vis/extract-channel gradient 0))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; The red channel of the gradient increases top to bottom:

(let [ch (t/select gradient :all :all 0)]
  [(int (ch 0 0))
   (int (ch 199 0))])

(kind/test-last
 [(fn [[top bottom]] (and (< top 10) (> bottom 250)))])

;; ### Swap channels
;;
;; Rearranging the last axis swaps colors:

(def swapped
  (let [[h w _c] (t/shape gradient)]
    (t/compute-tensor [h w 3]
                      (fn [r c ch]
        ;; Swap R↔B
                        (int (gradient r c (case (int ch) 0 2 2 0 ch))))
                      :uint8)))

(bufimg/tensor->image swapped)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Brightness and contrast
;;
;; Since the image tensor is backed by typed data, element-wise
;; operations with `dfn` transform every pixel at once.
;;
;; **Brightness**: add a constant.
;; **Contrast**: multiply by a factor.
;;
;; We must clamp to [0, 255] and cast back to `:uint8`.

(def brighten
  (fn [img]
    (-> img
        (t/elemwise-cast :int16)
        (la/mul 1.5)
        (elem/max 0)
        (elem/min 255)
        (t/elemwise-cast :uint8)
        t/->tensor
        (t/reshape (t/shape img)))))

(bufimg/tensor->image (brighten circle-img))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Grayscale via matrix multiply
;;
;; Converting RGB to grayscale is a dot product:
;;
;; $\text{gray} = 0.299 R + 0.587 G + 0.114 B$
;;
;; We reshape the image from `[h w 3]` to `[h*w, 3]`, multiply
;; by the weight column, then reshape back. This uses `la/mmul`.

(def to-grayscale
  (fn [img]
    (let [[h w _c] (t/shape img)
          n (* h w)
          pixels (t/reshape (t/->tensor
                             (t/elemwise-cast img :float64))
                            [n 3])
          weights (t/column [0.299 0.587 0.114])
          gray-flat (la/mmul pixels weights)]
      (t/compute-tensor [h w 3]
                        (fn [r c _ch]
                          (let [idx (+ (* r w) c)]
                            (int (max 0 (min 255 (gray-flat idx 0))))))
                        :uint8))))

(bufimg/tensor->image (to-grayscale gradient))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Convolution kernels
;;
;; [Image convolution](https://en.wikipedia.org/wiki/Kernel_(image_processing))
;; slides a small matrix (kernel) over the image, computing a
;; weighted sum at each position. We define kernels as `t/matrix`
;; and apply them by direct computation.

;; ### Kernel definitions

(def blur-kernel
  (la/scale (t/matrix [[1 1 1] [1 1 1] [1 1 1]]) (/ 1.0 9.0)))

(def sharpen-kernel
  (t/matrix [[0 -1  0]
             [-1  5 -1]
             [0 -1  0]]))

(def edge-kernel
  (t/matrix [[-1 -1 -1]
             [-1  8 -1]
             [-1 -1 -1]]))

;; Edge detection kernels sum to zero (no DC response):

(la/sum edge-kernel)

(kind/test-last [(fn [v] (== 0.0 v))])

;; ### Applying a kernel
;;
;; For each output pixel, extract a 3*3 patch and compute
;; the element-wise product with the kernel, then sum.
;; This is imperative — we loop over every pixel and write
;; the result into an output array.

(def apply-kernel
  (fn [gray-img kernel]
    (let [[h w _c] (t/shape gray-img)
          k-arr (t/->double-array kernel)
          ch0   (t/->double-array (t/select gray-img :all :all 0))
          out   (double-array (* h w))]
      (dotimes [r (- h 2)]
        (dotimes [c (- w 2)]
          (let [ri (inc r)
                ci (inc c)
                val (loop [kr 0 acc 0.0]
                      (if (>= kr 3) acc
                          (recur (inc kr)
                                 (loop [kc 0 a acc]
                                   (if (>= kc 3) a
                                       (recur (inc kc)
                                              (+ a (* (aget k-arr (+ (* kr 3) kc))
                                                      (aget ch0 (+ (* (+ r kr) w)
                                                                   (+ c kc)))))))))))]
            (aset out (+ (* ri w) ci) val))))
      (t/compute-tensor [h w 3]
                        (fn [r c _ch]
                          (int (max 0 (min 255 (aget out (+ (* r w) c))))))
                        :uint8))))

;; ### Box blur

(bufimg/tensor->image (apply-kernel checkerboard blur-kernel))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Edge detection
;;
;; Edges in the checkerboard appear at the transitions between
;; light and dark squares:

(bufimg/tensor->image (apply-kernel checkerboard edge-kernel))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Sharpen
;;
;; Sharpening enhances edges while preserving the original signal:

(bufimg/tensor->image (apply-kernel circle-img sharpen-kernel))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Sobel edge detection
;;
;; The [Sobel operator](https://en.wikipedia.org/wiki/Sobel_operator)
;; computes horizontal and vertical gradients separately, then
;; combines them:
;;
;; $G = \sqrt{G_x^2 + G_y^2}$

(def sobel-x
  (t/matrix [[-1 0 1] [-2 0 2] [-1 0 1]]))

(def sobel-y
  (t/matrix [[-1 -2 -1] [0 0 0] [1 2 1]]))

(def sobel-edges
  (fn [gray-img]
    (let [[h w _c] (t/shape gray-img)
          sx-arr (t/->double-array sobel-x)
          sy-arr (t/->double-array sobel-y)
          ch0    (t/->double-array (t/select gray-img :all :all 0))
          out    (double-array (* h w))]
      (dotimes [r (- h 2)]
        (dotimes [c (- w 2)]
          (let [ri (inc r) ci (inc c)
                gx (loop [kr 0 acc 0.0]
                     (if (>= kr 3) acc
                         (recur (inc kr)
                                (loop [kc 0 a acc]
                                  (if (>= kc 3) a
                                      (recur (inc kc)
                                             (+ a (* (aget sx-arr (+ (* kr 3) kc))
                                                     (aget ch0 (+ (* (+ r kr) w)
                                                                  (+ c kc)))))))))))
                gy (loop [kr 0 acc 0.0]
                     (if (>= kr 3) acc
                         (recur (inc kr)
                                (loop [kc 0 a acc]
                                  (if (>= kc 3) a
                                      (recur (inc kc)
                                             (+ a (* (aget sy-arr (+ (* kr 3) kc))
                                                     (aget ch0 (+ (* (+ r kr) w)
                                                                  (+ c kc)))))))))))
                mag (math/sqrt (+ (* gx gx) (* gy gy)))]
            (aset out (+ (* ri w) ci) mag))))
      (t/compute-tensor [h w 3]
                        (fn [r c _ch]
                          (int (min 255 (aget out (+ (* r w) c)))))
                        :uint8))))

(bufimg/tensor->image (sobel-edges circle-img))

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])
