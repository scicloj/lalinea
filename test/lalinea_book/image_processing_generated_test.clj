(ns
 lalinea-book.image-processing-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l29
 (def
  gradient
  (t/compute-tensor
   [200 200 3]
   (fn
    [r c ch]
    (case
     (int ch)
     0
     (int (* 255 (/ r 200.0)))
     1
     (int (* 255 (/ c 200.0)))
     2
     128))
   :uint8)))


(def v4_l38 (bufimg/tensor->image gradient))


(deftest
 t5_l40
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v4_l38)))


(def
 v7_l45
 (def
  checkerboard
  (let
   [size 200 sq 25]
   (t/compute-tensor
    [size size 3]
    (fn [r c _ch] (if (even? (+ (quot r sq) (quot c sq))) 240 30))
    :uint8))))


(def v8_l52 (bufimg/tensor->image checkerboard))


(deftest
 t9_l54
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v8_l52)))


(def
 v11_l59
 (def
  circle-img
  (let
   [size 200 cx 100 cy 100 radius 60]
   (t/compute-tensor
    [size size 3]
    (fn
     [r c ch]
     (let
      [dr
       (- r cy)
       dc
       (- c cx)
       dist
       (math/sqrt (+ (* dr dr) (* dc dc)))]
      (if (<= dist radius) (case (int ch) 0 50 1 180 2 220) 20)))
    :uint8))))


(def v12_l73 (bufimg/tensor->image circle-img))


(deftest
 t13_l75
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v12_l73)))


(def v15_l88 (bufimg/tensor->image (vis/extract-channel gradient 0)))


(deftest
 t16_l90
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v15_l88)))


(def
 v18_l95
 (let
  [ch (t/select gradient :all :all 0)]
  [(int (ch 0 0)) (int (ch 199 0))]))


(deftest
 t19_l99
 (is ((fn [[top bottom]] (and (< top 10) (> bottom 250))) v18_l95)))


(def
 v21_l106
 (def
  swapped
  (let
   [[h w _c] (t/shape gradient)]
   (t/compute-tensor
    [h w 3]
    (fn [r c ch] (int (gradient r c (case (int ch) 0 2 2 0 ch))))
    :uint8))))


(def v22_l114 (bufimg/tensor->image swapped))


(deftest
 t23_l116
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v22_l114)))


(def
 v25_l129
 (def
  brighten
  (fn
   [img]
   (->
    img
    (t/elemwise-cast :int16)
    (la/scale 1.5)
    (el/max 0)
    (el/min 255)
    (t/elemwise-cast :uint8)
    t/->tensor
    (t/reshape (t/shape img))))))


(def v26_l140 (bufimg/tensor->image (brighten circle-img)))


(deftest
 t27_l142
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v26_l140)))


(def
 v29_l154
 (def
  to-grayscale
  (fn
   [img]
   (let
    [[h w _c]
     (t/shape img)
     n
     (* h w)
     pixels
     (t/reshape (t/->tensor (t/elemwise-cast img :float64)) [n 3])
     weights
     (t/column [0.299 0.587 0.114])
     gray-flat
     (la/mmul pixels weights)]
    (t/compute-tensor
     [h w 3]
     (fn
      [r c _ch]
      (let
       [idx (+ (* r w) c)]
       (int (max 0 (min 255 (gray-flat idx 0))))))
     :uint8)))))


(def v30_l169 (bufimg/tensor->image (to-grayscale gradient)))


(deftest
 t31_l171
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v30_l169)))


(def
 v33_l183
 (def
  blur-kernel
  (la/scale (t/matrix [[1 1 1] [1 1 1] [1 1 1]]) (/ 1.0 9.0))))


(def
 v34_l186
 (def sharpen-kernel (t/matrix [[0 -1 0] [-1 5 -1] [0 -1 0]])))


(def
 v35_l191
 (def edge-kernel (t/matrix [[-1 -1 -1] [-1 8 -1] [-1 -1 -1]])))


(def v37_l198 (el/sum edge-kernel))


(deftest t38_l200 (is ((fn [v] (== 0.0 v)) v37_l198)))


(def
 v40_l209
 (def
  apply-kernel
  (fn
   [gray-img kernel]
   (let
    [[h w _c]
     (t/shape gray-img)
     k-arr
     (t/->double-array kernel)
     ch0
     (t/->double-array (t/select gray-img :all :all 0))
     out
     (double-array (* h w))]
    (dotimes
     [r (- h 2)]
     (dotimes
      [c (- w 2)]
      (let
       [ri
        (inc r)
        ci
        (inc c)
        val
        (loop
         [kr 0 acc 0.0]
         (if
          (>= kr 3)
          acc
          (recur
           (inc kr)
           (loop
            [kc 0 a acc]
            (if
             (>= kc 3)
             a
             (recur
              (inc kc)
              (+
               a
               (*
                (aget k-arr (+ (* kr 3) kc))
                (aget ch0 (+ (* (+ r kr) w) (+ c kc)))))))))))]
       (aset out (+ (* ri w) ci) val))))
    (t/compute-tensor
     [h w 3]
     (fn [r c _ch] (int (max 0 (min 255 (aget out (+ (* r w) c))))))
     :uint8)))))


(def
 v42_l236
 (bufimg/tensor->image (apply-kernel checkerboard blur-kernel)))


(deftest
 t43_l238
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v42_l236)))


(def
 v45_l246
 (bufimg/tensor->image (apply-kernel checkerboard edge-kernel)))


(deftest
 t46_l248
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v45_l246)))


(def
 v48_l255
 (bufimg/tensor->image (apply-kernel circle-img sharpen-kernel)))


(deftest
 t49_l257
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v48_l255)))


(def v51_l268 (def sobel-x (t/matrix [[-1 0 1] [-2 0 2] [-1 0 1]])))


(def v52_l271 (def sobel-y (t/matrix [[-1 -2 -1] [0 0 0] [1 2 1]])))


(def
 v53_l274
 (def
  sobel-edges
  (fn
   [gray-img]
   (let
    [[h w _c]
     (t/shape gray-img)
     sx-arr
     (t/->double-array sobel-x)
     sy-arr
     (t/->double-array sobel-y)
     ch0
     (t/->double-array (t/select gray-img :all :all 0))
     out
     (double-array (* h w))]
    (dotimes
     [r (- h 2)]
     (dotimes
      [c (- w 2)]
      (let
       [ri
        (inc r)
        ci
        (inc c)
        gx
        (loop
         [kr 0 acc 0.0]
         (if
          (>= kr 3)
          acc
          (recur
           (inc kr)
           (loop
            [kc 0 a acc]
            (if
             (>= kc 3)
             a
             (recur
              (inc kc)
              (+
               a
               (*
                (aget sx-arr (+ (* kr 3) kc))
                (aget ch0 (+ (* (+ r kr) w) (+ c kc)))))))))))
        gy
        (loop
         [kr 0 acc 0.0]
         (if
          (>= kr 3)
          acc
          (recur
           (inc kr)
           (loop
            [kc 0 a acc]
            (if
             (>= kc 3)
             a
             (recur
              (inc kc)
              (+
               a
               (*
                (aget sy-arr (+ (* kr 3) kc))
                (aget ch0 (+ (* (+ r kr) w) (+ c kc)))))))))))
        mag
        (math/sqrt (+ (* gx gx) (* gy gy)))]
       (aset out (+ (* ri w) ci) mag))))
    (t/compute-tensor
     [h w 3]
     (fn [r c _ch] (int (min 255 (aget out (+ (* r w) c)))))
     :uint8)))))


(def v54_l309 (bufimg/tensor->image (sobel-edges circle-img)))


(deftest
 t55_l311
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v54_l309)))
