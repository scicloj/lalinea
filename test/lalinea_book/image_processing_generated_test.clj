(ns
 lalinea-book.image-processing-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.tensor :as dtt]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def
 v3_l37
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


(def v4_l46 (bufimg/tensor->image gradient))


(deftest
 t5_l48
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v4_l46)))


(def
 v7_l53
 (def
  checkerboard
  (let
   [size 200 sq 25]
   (t/compute-tensor
    [size size 3]
    (fn [r c _ch] (if (even? (+ (quot r sq) (quot c sq))) 240 30))
    :uint8))))


(def v8_l60 (bufimg/tensor->image checkerboard))


(deftest
 t9_l62
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v8_l60)))


(def
 v11_l67
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


(def v12_l81 (bufimg/tensor->image circle-img))


(deftest
 t13_l83
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v12_l81)))


(def v15_l96 (bufimg/tensor->image (vis/extract-channel gradient 0)))


(deftest
 t16_l98
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v15_l96)))


(def
 v18_l103
 (let
  [ch (t/select gradient :all :all 0)]
  [(int (ch 0 0)) (int (ch 199 0))]))


(deftest
 t19_l107
 (is ((fn [[top bottom]] (and (< top 10) (> bottom 250))) v18_l103)))


(def
 v21_l114
 (def
  swapped
  (let
   [[h w _c] (t/shape gradient)]
   (t/compute-tensor
    [h w 3]
    (fn [r c ch] (int (gradient r c (case (int ch) 0 2 2 0 ch))))
    :uint8))))


(def v22_l122 (bufimg/tensor->image swapped))


(deftest
 t23_l124
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v22_l122)))


(def
 v25_l134
 (defn
  brighten
  [img factor]
  (->
   img
   (dfn/* factor)
   (dfn/max 0)
   (dfn/min 255)
   (dtype/elemwise-cast :uint8))))


(def v26_l141 (bufimg/tensor->image (brighten circle-img 1.5)))


(deftest
 t27_l143
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v26_l141)))


(def
 v29_l155
 (defn
  to-grayscale
  [img]
  (let
   [r
    (dtt/select img :all :all 0)
    g
    (dtt/select img :all :all 1)
    b
    (dtt/select img :all :all 2)]
   (dfn/+ (dfn/* r 0.299) (dfn/* g 0.587) (dfn/* b 0.114)))))


(def
 v30_l163
 (bufimg/tensor->image
  (vis/matrix->gray-image (to-grayscale gradient))))


(deftest
 t31_l165
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v30_l163)))


(def
 v33_l176
 (def
  blur-kernel
  (el/scale (t/matrix [[1 1 1] [1 1 1] [1 1 1]]) (/ 1.0 9.0))))


(def
 v34_l179
 (def sharpen-kernel (t/matrix [[0 -1 0] [-1 5 -1] [0 -1 0]])))


(def
 v35_l184
 (def edge-kernel (t/matrix [[-1 -1 -1] [-1 8 -1] [-1 -1 -1]])))


(def v37_l191 (el/sum edge-kernel))


(deftest t38_l193 (is ((fn [v] (== 0.0 v)) v37_l191)))


(def
 v40_l202
 (defn
  apply-kernel
  [gray-2d kernel]
  (let
   [[h w]
    (dtype/shape gray-2d)
    [kh kw]
    (t/shape kernel)
    oh
    (- h kh -1)
    ow
    (- w kw -1)]
   (reduce
    (fn
     [acc [dr dc]]
     (let
      [weight
       (double (kernel dr dc))
       shifted
       (dtt/select gray-2d (range dr (+ dr oh)) (range dc (+ dc ow)))]
      (dtype/clone (dfn/+ acc (dfn/* shifted weight)))))
    (dtype/clone (dtt/compute-tensor [oh ow] (fn [_ _] 0.0) :float64))
    (for [dr (range kh) dc (range kw)] [dr dc])))))


(def
 v42_l220
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale checkerboard) blur-kernel))))


(deftest
 t43_l224
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v42_l220)))


(def
 v45_l232
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale checkerboard) edge-kernel))))


(deftest
 t46_l236
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v45_l232)))


(def
 v48_l243
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale circle-img) sharpen-kernel))))


(deftest
 t49_l247
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v48_l243)))


(def v51_l261 (def sobel-x (t/matrix [[-1 0 1] [-2 0 2] [-1 0 1]])))


(def v52_l264 (def sobel-y (t/matrix [[-1 -2 -1] [0 0 0] [1 2 1]])))


(def
 v53_l267
 (defn
  sobel-edges
  [gray-2d]
  (let
   [gx
    (apply-kernel gray-2d sobel-x)
    gy
    (apply-kernel gray-2d sobel-y)]
   (dfn/sqrt (dfn/+ (dfn/sq gx) (dfn/sq gy))))))


(def
 v54_l272
 (bufimg/tensor->image
  (vis/matrix->gray-image (sobel-edges (to-grayscale circle-img)))))


(deftest
 t55_l276
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v54_l272)))
