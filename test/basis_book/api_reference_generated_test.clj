(ns
 basis-book.api-reference-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [scicloj.basis.transform :as bfft]
  [scicloj.basis.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (kind/doc #'la/matrix))


(def v4_l28 (la/matrix [[1 2] [3 4]]))


(deftest t5_l30 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l28)))


(def v6_l32 (kind/doc #'la/eye))


(def v7_l34 (la/eye 3))


(deftest
 t8_l36
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l34)))


(def v9_l40 (kind/doc #'la/zeros))


(def v10_l42 (la/zeros 2 3))


(deftest
 t11_l44
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l42)))


(def v12_l46 (kind/doc #'la/diag))


(def v13_l48 (la/diag [3 5 7]))


(deftest
 t14_l50
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l48)))


(def v15_l54 (kind/doc #'la/column))


(def v16_l56 (la/column [1 2 3]))


(deftest
 t17_l58
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l56)))


(def v18_l60 (kind/doc #'la/row))


(def v19_l62 (la/row [1 2 3]))


(deftest
 t20_l64
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l62)))


(def v21_l66 (kind/doc #'la/add))


(def
 v22_l68
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l71 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l68)))


(def v24_l73 (kind/doc #'la/sub))


(def
 v25_l75
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l78 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l75)))


(def v27_l80 (kind/doc #'la/scale))


(def v28_l82 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l84 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l82)))


(def v30_l86 (kind/doc #'la/mul))


(def
 v31_l88
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l91
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l88)))


(def v33_l94 (kind/doc #'la/abs))


(def v34_l96 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l98 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l96)))


(def v36_l100 (kind/doc #'la/mmul))


(def v37_l102 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t38_l105
 (is
  ((fn
    [m]
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v37_l102)))


(def v39_l108 (kind/doc #'la/transpose))


(def v40_l110 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l112
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v40_l110)))


(def v42_l114 (kind/doc #'la/submatrix))


(def v43_l116 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t44_l118
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v43_l116)))


(def v45_l120 (kind/doc #'la/trace))


(def v46_l122 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t47_l124 (is ((fn [v] (== 5.0 v)) v46_l122)))


(def v48_l126 (kind/doc #'la/det))


(def v49_l128 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t50_l130 (is ((fn [v] (la/close-scalar? v -2.0)) v49_l128)))


(def v51_l132 (kind/doc #'la/norm))


(def v52_l134 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t53_l136 (is ((fn [v] (la/close-scalar? v 5.0)) v52_l134)))


(def v54_l138 (kind/doc #'la/dot))


(def v55_l140 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t56_l142 (is ((fn [v] (== 32.0 v)) v55_l140)))


(def v57_l144 (kind/doc #'la/close?))


(def v58_l146 (la/close? (la/eye 2) (la/eye 2)))


(deftest t59_l148 (is (true? v58_l146)))


(def v60_l150 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t61_l152 (is (false? v60_l150)))


(def v62_l154 (kind/doc #'la/close-scalar?))


(def v63_l156 (la/close-scalar? 1.00000000001 1.0))


(deftest t64_l158 (is (true? v63_l156)))


(def v65_l160 (kind/doc #'la/invert))


(def
 v66_l162
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t67_l165 (is (true? v66_l162)))


(def v68_l167 (kind/doc #'la/solve))


(def
 v70_l170
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t71_l174
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v70_l170)))


(def v72_l177 (kind/doc #'la/eigen))


(def
 v73_l179
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t74_l183
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v73_l179)))


(def v75_l187 (kind/doc #'la/real-eigenvalues))


(def v76_l189 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t77_l191
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v76_l189)))


(def v78_l194 (kind/doc #'la/svd))


(def
 v79_l196
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


(deftest
 t80_l201
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v79_l196)))


(def v81_l206 (kind/doc #'la/qr))


(def
 v82_l208
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t83_l211 (is (true? v82_l208)))


(def v84_l213 (kind/doc #'la/cholesky))


(def
 v85_l215
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t86_l219 (is (true? v85_l215)))


(def v87_l221 (kind/doc #'la/tensor->dmat))


(def
 v88_l223
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t89_l227 (is (true? v88_l223)))


(def v90_l229 (kind/doc #'la/dmat->tensor))


(def
 v91_l231
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t)))))


(deftest t92_l235 (is (true? v91_l231)))


(def v93_l237 (kind/doc #'la/complex-tensor->zmat))


(def
 v94_l239
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t95_l243 (is (true? v94_l239)))


(def v96_l245 (kind/doc #'la/zmat->complex-tensor))


(def
 v97_l247
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t98_l251 (is (true? v97_l247)))


(def v100_l258 (kind/doc #'cx/complex-tensor))


(def v101_l260 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t102_l262
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v101_l260)))


(def v103_l264 (kind/doc #'cx/complex-tensor-real))


(def v104_l266 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t105_l268
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v104_l266)))


(def v106_l270 (kind/doc #'cx/complex))


(def v107_l272 (cx/complex 3.0 4.0))


(deftest
 t108_l274
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v107_l272)))


(def v109_l278 (kind/doc #'cx/re))


(def v110_l280 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t111_l282 (is (= v110_l280 [1.0 2.0])))


(def v112_l284 (kind/doc #'cx/im))


(def v113_l286 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t114_l288 (is (= v113_l286 [3.0 4.0])))


(def v115_l290 (kind/doc #'cx/complex-shape))


(def
 v116_l292
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t117_l295 (is (= v116_l292 [2 2])))


(def v118_l297 (kind/doc #'cx/scalar?))


(def v119_l299 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t120_l301 (is (true? v119_l299)))


(def v121_l303 (kind/doc #'cx/complex?))


(def v122_l305 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t123_l307 (is (true? v122_l305)))


(def v124_l309 (cx/complex? (la/eye 2)))


(deftest t125_l311 (is (false? v124_l309)))


(def v126_l313 (kind/doc #'cx/->tensor))


(def
 v127_l315
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t128_l317 (is (= v127_l315 [2 2])))


(def v129_l319 (kind/doc #'cx/->double-array))


(def
 v130_l321
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t131_l324 (is (= v130_l321 [1.0 3.0 2.0 4.0])))


(def v132_l326 (kind/doc #'cx/add))


(def
 v133_l328
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t134_l332 (is (= v133_l328 [11.0 22.0])))


(def v135_l334 (kind/doc #'cx/sub))


(def
 v136_l336
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t137_l340 (is (= v136_l336 [9.0 18.0])))


(def v138_l342 (kind/doc #'cx/scale))


(def
 v139_l344
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t140_l347 (is (= v139_l344 [[2.0 4.0] [6.0 8.0]])))


(def v141_l349 (kind/doc #'cx/mul))


(def
 v143_l352
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t144_l357 (is (= v143_l352 [-10.0 10.0])))


(def v145_l359 (kind/doc #'cx/conj))


(def
 v146_l361
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t147_l364 (is (= v146_l361 [-3.0 4.0])))


(def v148_l366 (kind/doc #'cx/abs))


(def
 v150_l369
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t151_l372 (is (true? v150_l369)))


(def v152_l374 (kind/doc #'cx/dot))


(def
 v153_l376
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t154_l381 (is (true? v153_l376)))


(def v155_l383 (kind/doc #'cx/dot-conj))


(def
 v157_l386
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t158_l390 (is (true? v157_l386)))


(def v159_l392 (kind/doc #'cx/sum))


(def
 v160_l394
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t161_l398 (is (= v160_l394 [6.0 15.0])))


(def v163_l405 (kind/doc #'bfft/forward))


(def
 v164_l407
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t165_l411 (is (= v164_l407 [4])))


(def v166_l413 (kind/doc #'bfft/inverse))


(def
 v167_l415
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t168_l419 (is (true? v167_l415)))


(def v169_l421 (kind/doc #'bfft/inverse-real))


(def
 v170_l423
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t171_l427 (is (true? v170_l423)))


(def v172_l429 (kind/doc #'bfft/forward-complex))


(def
 v173_l431
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t174_l435 (is (= v173_l431 [4])))


(def v175_l437 (kind/doc #'bfft/dct-forward))


(def v176_l439 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t177_l441 (is ((fn [v] (= 4 (count v))) v176_l439)))


(def v178_l443 (kind/doc #'bfft/dct-inverse))


(def
 v179_l445
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t180_l449 (is (true? v179_l445)))


(def v181_l451 (kind/doc #'bfft/dst-forward))


(def v182_l453 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t183_l455 (is ((fn [v] (= 4 (count v))) v182_l453)))


(def v184_l457 (kind/doc #'bfft/dst-inverse))


(def
 v185_l459
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t186_l463 (is (true? v185_l459)))


(def v187_l465 (kind/doc #'bfft/dht-forward))


(def v188_l467 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l469 (is ((fn [v] (= 4 (count v))) v188_l467)))


(def v190_l471 (kind/doc #'bfft/dht-inverse))


(def
 v191_l473
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l477 (is (true? v191_l473)))


(def v194_l483 (kind/doc #'vis/arrow-plot))


(def
 v195_l485
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v196_l489 (kind/doc #'vis/graph-plot))


(def
 v197_l491
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v198_l495 (kind/doc #'vis/matrix->gray-image))


(def
 v199_l497
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t200_l502
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v199_l497)))


(def v201_l504 (kind/doc #'vis/extract-channel))


(def
 v202_l506
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t203_l512
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v202_l506)))
