(ns
 lalinea-book.image-processing-generated-test
 (:require
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
 v3_l36
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


(def v4_l45 (bufimg/tensor->image gradient))


(deftest
 t5_l47
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v4_l45)))


(def
 v7_l52
 (def
  checkerboard
  (let
   [size 200 sq 25]
   (t/compute-tensor
    [size size 3]
    (fn [r c _ch] (if (even? (+ (quot r sq) (quot c sq))) 240 30))
    :uint8))))


(def v8_l59 (bufimg/tensor->image checkerboard))


(deftest
 t9_l61
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v8_l59)))


(def
 v11_l66
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


(def v12_l80 (bufimg/tensor->image circle-img))


(deftest
 t13_l82
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v12_l80)))


(def v15_l95 (bufimg/tensor->image (vis/extract-channel gradient 0)))


(deftest
 t16_l97
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v15_l95)))


(def
 v18_l102
 (let
  [ch (t/select gradient :all :all 0)]
  [(int (ch 0 0)) (int (ch 199 0))]))


(deftest
 t19_l106
 (is ((fn [[top bottom]] (and (< top 10) (> bottom 250))) v18_l102)))


(def
 v21_l113
 (def
  swapped
  (let
   [[h w _c] (t/shape gradient)]
   (t/compute-tensor
    [h w 3]
    (fn [r c ch] (int (gradient r c (case (int ch) 0 2 2 0 ch))))
    :uint8))))


(def v22_l121 (bufimg/tensor->image swapped))


(deftest
 t23_l123
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v22_l121)))


(def
 v25_l133
 (defn
  brighten
  [img factor]
  (->
   img
   (dfn/* factor)
   (dfn/max 0)
   (dfn/min 255)
   (dtype/elemwise-cast :uint8))))


(def v26_l140 (bufimg/tensor->image (brighten circle-img 1.5)))


(deftest
 t27_l142
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v26_l140)))


(def
 v29_l154
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
 v30_l162
 (bufimg/tensor->image
  (vis/matrix->gray-image (to-grayscale gradient))))


(deftest
 t31_l164
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v30_l162)))


(def
 v33_l175
 (def
  blur-kernel
  (el/scale (t/matrix [[1 1 1] [1 1 1] [1 1 1]]) (/ 1.0 9.0))))


(def
 v34_l178
 (def sharpen-kernel (t/matrix [[0 -1 0] [-1 5 -1] [0 -1 0]])))


(def
 v35_l183
 (def edge-kernel (t/matrix [[-1 -1 -1] [-1 8 -1] [-1 -1 -1]])))


(def v37_l190 (el/sum edge-kernel))


(deftest t38_l192 (is ((fn [v] (== 0.0 v)) v37_l190)))


(def
 v40_l201
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
 v42_l219
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale checkerboard) blur-kernel))))


(deftest
 t43_l223
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v42_l219)))


(def
 v45_l231
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale checkerboard) edge-kernel))))


(deftest
 t46_l235
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v45_l231)))


(def
 v48_l242
 (bufimg/tensor->image
  (vis/matrix->gray-image
   (apply-kernel (to-grayscale circle-img) sharpen-kernel))))


(deftest
 t49_l246
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v48_l242)))


(def v51_l260 (def sobel-x (t/matrix [[-1 0 1] [-2 0 2] [-1 0 1]])))


(def v52_l263 (def sobel-y (t/matrix [[-1 -2 -1] [0 0 0] [1 2 1]])))


(def
 v53_l266
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
 v54_l271
 (bufimg/tensor->image
  (vis/matrix->gray-image (sobel-edges (to-grayscale circle-img)))))


(deftest
 t55_l275
 (is ((fn [img] (= java.awt.image.BufferedImage (type img))) v54_l271)))
