(ns
 la-linea-book.image-processing-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.la-linea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l33
 (def
  gradient
  (tensor/compute-tensor
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


(def v4_l42 (bufimg/tensor->image gradient))


(deftest
 t5_l44
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v4_l42)))


(def
 v7_l49
 (def
  checkerboard
  (let
   [size 200 sq 25]
   (tensor/compute-tensor
    [size size 3]
    (fn [r c _ch] (if (even? (+ (quot r sq) (quot c sq))) 240 30))
    :uint8))))


(def v8_l56 (bufimg/tensor->image checkerboard))


(deftest
 t9_l58
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v8_l56)))


(def
 v11_l63
 (def
  circle-img
  (let
   [size 200 cx 100 cy 100 radius 60]
   (tensor/compute-tensor
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


(def v12_l77 (bufimg/tensor->image circle-img))


(deftest
 t13_l79
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v12_l77)))


(def v15_l92 (bufimg/tensor->image (vis/extract-channel gradient 0)))


(deftest
 t16_l94
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v15_l92)))


(def
 v18_l99
 (let
  [ch (tensor/select gradient :all :all 0)]
  [(int (tensor/mget ch 0 0)) (int (tensor/mget ch 199 0))]))


(deftest
 t19_l103
 (is ((fn [[top bottom]] (and (< top 10) (> bottom 250))) v18_l99)))


(def
 v21_l110
 (def
  swapped
  (let
   [[h w _c] (dtype/shape gradient)]
   (tensor/compute-tensor
    [h w 3]
    (fn
     [r c ch]
     (int (tensor/mget gradient r c (case (int ch) 0 2 2 0 ch))))
    :uint8))))


(def v22_l118 (bufimg/tensor->image swapped))


(deftest
 t23_l120
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v22_l118)))


(def
 v25_l133
 (def
  brighten
  (fn
   [img]
   (->
    img
    (dtype/elemwise-cast :int16)
    (dfn/* 1.5)
    (dfn/max 0)
    (dfn/min 255)
    (dtype/elemwise-cast :uint8)
    tensor/ensure-tensor
    (tensor/reshape (dtype/shape img))))))


(def v26_l144 (bufimg/tensor->image (brighten circle-img)))


(deftest
 t27_l146
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v26_l144)))


(def
 v29_l158
 (def
  to-grayscale
  (fn
   [img]
   (let
    [[h w _c]
     (dtype/shape img)
     n
     (* h w)
     pixels
     (tensor/reshape
      (tensor/ensure-tensor (dtype/elemwise-cast img :float64))
      [n 3])
     weights
     (la/column [0.299 0.587 0.114])
     gray-flat
     (la/mmul pixels weights)]
    (tensor/compute-tensor
     [h w 3]
     (fn
      [r c _ch]
      (let
       [idx (+ (* r w) c)]
       (int (max 0 (min 255 (tensor/mget gray-flat idx 0))))))
     :uint8)))))


(def v30_l173 (bufimg/tensor->image (to-grayscale gradient)))


(deftest
 t31_l175
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v30_l173)))


(def
 v33_l187
 (def
  blur-kernel
  (la/scale (la/matrix [[1 1 1] [1 1 1] [1 1 1]]) (/ 1.0 9.0))))


(def
 v34_l190
 (def sharpen-kernel (la/matrix [[0 -1 0] [-1 5 -1] [0 -1 0]])))


(def
 v35_l195
 (def edge-kernel (la/matrix [[-1 -1 -1] [-1 8 -1] [-1 -1 -1]])))


(def v37_l202 (dfn/sum edge-kernel))


(deftest t38_l204 (is ((fn [v] (== 0.0 v)) v37_l202)))


(def
 v40_l213
 (def
  apply-kernel
  (fn
   [gray-img kernel]
   (let
    [[h w _c]
     (dtype/shape gray-img)
     k-arr
     (dtype/->double-array kernel)
     ch0
     (dtype/->double-array (tensor/select gray-img :all :all 0))
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
    (tensor/compute-tensor
     [h w 3]
     (fn [r c _ch] (int (max 0 (min 255 (aget out (+ (* r w) c))))))
     :uint8)))))


(def
 v42_l240
 (bufimg/tensor->image (apply-kernel checkerboard blur-kernel)))


(deftest
 t43_l242
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v42_l240)))


(def
 v45_l250
 (bufimg/tensor->image (apply-kernel checkerboard edge-kernel)))


(deftest
 t46_l252
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v45_l250)))


(def
 v48_l259
 (bufimg/tensor->image (apply-kernel circle-img sharpen-kernel)))


(deftest
 t49_l261
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v48_l259)))


(def v51_l272 (def sobel-x (la/matrix [[-1 0 1] [-2 0 2] [-1 0 1]])))


(def v52_l275 (def sobel-y (la/matrix [[-1 -2 -1] [0 0 0] [1 2 1]])))


(def
 v53_l278
 (def
  sobel-edges
  (fn
   [gray-img]
   (let
    [[h w _c]
     (dtype/shape gray-img)
     sx-arr
     (dtype/->double-array sobel-x)
     sy-arr
     (dtype/->double-array sobel-y)
     ch0
     (dtype/->double-array (tensor/select gray-img :all :all 0))
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
    (tensor/compute-tensor
     [h w 3]
     (fn [r c _ch] (int (min 255 (aget out (+ (* r w) c)))))
     :uint8)))))


(def v54_l313 (bufimg/tensor->image (sobel-edges circle-img)))


(deftest
 t55_l315
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v54_l313)))
