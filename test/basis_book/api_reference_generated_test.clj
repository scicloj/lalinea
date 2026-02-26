(ns
 basis-book.api-reference-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [scicloj.basis.transform :as bfft]
  [scicloj.basis.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l25 (kind/doc #'la/matrix))


(def v4_l27 (la/matrix [[1 2] [3 4]]))


(deftest t5_l29 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l27)))


(def v6_l31 (kind/doc #'la/eye))


(def v7_l33 (la/eye 3))


(deftest
 t8_l35
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l33)))


(def v9_l39 (kind/doc #'la/zeros))


(def v10_l41 (la/zeros 2 3))


(deftest
 t11_l43
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l41)))


(def v12_l45 (kind/doc #'la/diag))


(def v13_l47 (la/diag [3 5 7]))


(deftest
 t14_l49
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l47)))


(def v15_l53 (kind/doc #'la/column))


(def v16_l55 (la/column [1 2 3]))


(deftest
 t17_l57
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l55)))


(def v18_l59 (kind/doc #'la/row))


(def v19_l61 (la/row [1 2 3]))


(deftest
 t20_l63
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l61)))


(def v21_l65 (kind/doc #'la/add))


(def
 v22_l67
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l70 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l67)))


(def v24_l72 (kind/doc #'la/sub))


(def
 v25_l74
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l77 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l74)))


(def v27_l79 (kind/doc #'la/scale))


(def v28_l81 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l83 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l81)))


(def v30_l85 (kind/doc #'la/mul))


(def
 v31_l87
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l90
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l87)))


(def v33_l93 (kind/doc #'la/abs))


(def v34_l95 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l97 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l95)))


(def v36_l99 (kind/doc #'la/mmul))


(def v37_l101 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t38_l104
 (is
  ((fn
    [m]
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v37_l101)))


(def v39_l107 (kind/doc #'la/transpose))


(def v40_l109 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l111
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v40_l109)))


(def v42_l113 (kind/doc #'la/submatrix))


(def v43_l115 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t44_l117
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v43_l115)))


(def v45_l119 (kind/doc #'la/trace))


(def v46_l121 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t47_l123 (is ((fn [v] (== 5.0 v)) v46_l121)))


(def v48_l125 (kind/doc #'la/det))


(def v49_l127 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t50_l129 (is ((fn [v] (la/close-scalar? v -2.0)) v49_l127)))


(def v51_l131 (kind/doc #'la/norm))


(def v52_l133 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t53_l135 (is ((fn [v] (la/close-scalar? v 5.0)) v52_l133)))


(def v54_l137 (kind/doc #'la/dot))


(def v55_l139 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t56_l141 (is ((fn [v] (== 32.0 v)) v55_l139)))


(def v57_l143 (kind/doc #'la/close?))


(def v58_l145 (la/close? (la/eye 2) (la/eye 2)))


(deftest t59_l147 (is (true? v58_l145)))


(def v60_l149 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t61_l151 (is (false? v60_l149)))


(def v62_l153 (kind/doc #'la/close-scalar?))


(def v63_l155 (la/close-scalar? 1.00000000001 1.0))


(deftest t64_l157 (is (true? v63_l155)))


(def v65_l159 (kind/doc #'la/invert))


(def
 v66_l161
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t67_l164 (is (true? v66_l161)))


(def v68_l166 (kind/doc #'la/solve))


(def
 v70_l169
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t71_l173
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v70_l169)))


(def v72_l176 (kind/doc #'la/eigen))


(def
 v73_l178
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t74_l182
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v73_l178)))


(def v75_l186 (kind/doc #'la/real-eigenvalues))


(def v76_l188 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t77_l190
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v76_l188)))


(def v78_l193 (kind/doc #'la/svd))


(def
 v79_l195
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


(deftest
 t80_l200
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v79_l195)))


(def v81_l205 (kind/doc #'la/qr))


(def
 v82_l207
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t83_l210 (is (true? v82_l207)))


(def v84_l212 (kind/doc #'la/cholesky))


(def
 v85_l214
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t86_l218 (is (true? v85_l214)))


(def v87_l220 (kind/doc #'la/tensor->dmat))


(def
 v88_l222
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t89_l226 (is (true? v88_l222)))


(def v90_l228 (kind/doc #'la/dmat->tensor))


(def
 v91_l230
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t)))))


(deftest t92_l234 (is (true? v91_l230)))


(def v93_l236 (kind/doc #'la/complex-tensor->zmat))


(def
 v94_l238
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t95_l242 (is (true? v94_l238)))


(def v96_l244 (kind/doc #'la/zmat->complex-tensor))


(def
 v97_l246
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t98_l250 (is (true? v97_l246)))


(def v100_l257 (kind/doc #'cx/complex-tensor))


(def v101_l259 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t102_l261
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v101_l259)))


(def v103_l263 (kind/doc #'cx/complex-tensor-real))


(def v104_l265 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t105_l267
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v104_l265)))


(def v106_l269 (kind/doc #'cx/complex))


(def v107_l271 (cx/complex 3.0 4.0))


(deftest
 t108_l273
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v107_l271)))


(def v109_l277 (kind/doc #'cx/re))


(def v110_l279 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t111_l281 (is (= v110_l279 [1.0 2.0])))


(def v112_l283 (kind/doc #'cx/im))


(def v113_l285 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t114_l287 (is (= v113_l285 [3.0 4.0])))


(def v115_l289 (kind/doc #'cx/complex-shape))


(def
 v116_l291
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t117_l294 (is (= v116_l291 [2 2])))


(def v118_l296 (kind/doc #'cx/scalar?))


(def v119_l298 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t120_l300 (is (true? v119_l298)))


(def v121_l302 (kind/doc #'cx/complex?))


(def v122_l304 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t123_l306 (is (true? v122_l304)))


(def v124_l308 (cx/complex? (la/eye 2)))


(deftest t125_l310 (is (false? v124_l308)))


(def v126_l312 (kind/doc #'cx/->tensor))


(def
 v127_l314
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t128_l316 (is (= v127_l314 [2 2])))


(def v129_l318 (kind/doc #'cx/->double-array))


(def
 v130_l320
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t131_l323 (is (= v130_l320 [1.0 3.0 2.0 4.0])))


(def v132_l325 (kind/doc #'cx/add))


(def
 v133_l327
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t134_l331 (is (= v133_l327 [11.0 22.0])))


(def v135_l333 (kind/doc #'cx/sub))


(def
 v136_l335
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t137_l339 (is (= v136_l335 [9.0 18.0])))


(def v138_l341 (kind/doc #'cx/scale))


(def
 v139_l343
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t140_l346 (is (= v139_l343 [[2.0 4.0] [6.0 8.0]])))


(def v141_l348 (kind/doc #'cx/mul))


(def
 v143_l351
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t144_l356 (is (= v143_l351 [-10.0 10.0])))


(def v145_l358 (kind/doc #'cx/conj))


(def
 v146_l360
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t147_l363 (is (= v146_l360 [-3.0 4.0])))


(def v148_l365 (kind/doc #'cx/abs))


(def
 v150_l368
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t151_l371 (is (true? v150_l368)))


(def v152_l373 (kind/doc #'cx/dot))


(def
 v153_l375
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t154_l380 (is (true? v153_l375)))


(def v155_l382 (kind/doc #'cx/dot-conj))


(def
 v157_l385
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t158_l389 (is (true? v157_l385)))


(def v159_l391 (kind/doc #'cx/sum))


(def
 v160_l393
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t161_l397 (is (= v160_l393 [6.0 15.0])))


(def v163_l404 (kind/doc #'bfft/forward))


(def
 v164_l406
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t165_l410 (is (= v164_l406 [4])))


(def v166_l412 (kind/doc #'bfft/inverse))


(def
 v167_l414
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t168_l418 (is (true? v167_l414)))


(def v169_l420 (kind/doc #'bfft/inverse-real))


(def
 v170_l422
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t171_l426 (is (true? v170_l422)))


(def v172_l428 (kind/doc #'bfft/forward-complex))


(def
 v173_l430
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t174_l434 (is (= v173_l430 [4])))


(def v175_l436 (kind/doc #'bfft/dct-forward))


(def v176_l438 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t177_l440 (is ((fn [v] (= 4 (count v))) v176_l438)))


(def v178_l442 (kind/doc #'bfft/dct-inverse))


(def
 v179_l444
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t180_l448 (is (true? v179_l444)))


(def v181_l450 (kind/doc #'bfft/dst-forward))


(def v182_l452 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t183_l454 (is ((fn [v] (= 4 (count v))) v182_l452)))


(def v184_l456 (kind/doc #'bfft/dst-inverse))


(def
 v185_l458
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t186_l462 (is (true? v185_l458)))


(def v187_l464 (kind/doc #'bfft/dht-forward))


(def v188_l466 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l468 (is ((fn [v] (= 4 (count v))) v188_l466)))


(def v190_l470 (kind/doc #'bfft/dht-inverse))


(def
 v191_l472
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l476 (is (true? v191_l472)))


(def v194_l482 (kind/doc #'vis/arrow-plot))


(def
 v195_l484
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v196_l488 (kind/doc #'vis/graph-plot))


(def
 v197_l490
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v198_l494 (kind/doc #'vis/matrix->gray-image))


(def
 v199_l496
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t200_l501
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v199_l496)))


(def v201_l503 (kind/doc #'vis/extract-channel))


(def
 v202_l505
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t203_l511
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v202_l505)))
