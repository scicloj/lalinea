(ns
 la-linea-book.api-reference-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [scicloj.la-linea.transform :as bfft]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l27 (kind/doc #'la/matrix))


(def v4_l29 (la/matrix [[1 2] [3 4]]))


(deftest t5_l31 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l29)))


(def v6_l33 (kind/doc #'la/eye))


(def v7_l35 (la/eye 3))


(deftest
 t8_l37
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l35)))


(def v9_l41 (kind/doc #'la/zeros))


(def v10_l43 (la/zeros 2 3))


(deftest
 t11_l45
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l43)))


(def v12_l47 (kind/doc #'la/diag))


(def v13_l49 (la/diag [3 5 7]))


(deftest
 t14_l51
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l49)))


(def v15_l55 (kind/doc #'la/column))


(def v16_l57 (la/column [1 2 3]))


(deftest
 t17_l59
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l57)))


(def v18_l61 (kind/doc #'la/row))


(def v19_l63 (la/row [1 2 3]))


(deftest
 t20_l65
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l63)))


(def v21_l67 (kind/doc #'la/add))


(def
 v22_l69
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l72 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l69)))


(def v24_l74 (kind/doc #'la/sub))


(def
 v25_l76
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l79 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l76)))


(def v27_l81 (kind/doc #'la/scale))


(def v28_l83 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l85 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l83)))


(def v30_l87 (kind/doc #'la/mul))


(def
 v31_l89
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l92
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l89)))


(def v33_l95 (kind/doc #'la/abs))


(def v34_l97 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l99 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l97)))


(def v36_l101 (kind/doc #'la/mmul))


(def v37_l103 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t38_l106
 (is
  ((fn
    [m]
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v37_l103)))


(def v39_l109 (kind/doc #'la/transpose))


(def v40_l111 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l113
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v40_l111)))


(def v42_l115 (kind/doc #'la/submatrix))


(def v43_l117 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t44_l119
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v43_l117)))


(def v45_l121 (kind/doc #'la/trace))


(def v46_l123 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t47_l125 (is ((fn [v] (== 5.0 v)) v46_l123)))


(def v48_l127 (kind/doc #'la/det))


(def v49_l129 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t50_l131 (is ((fn [v] (la/close-scalar? v -2.0)) v49_l129)))


(def v51_l133 (kind/doc #'la/norm))


(def v52_l135 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t53_l137 (is ((fn [v] (la/close-scalar? v 5.0)) v52_l135)))


(def v54_l139 (kind/doc #'la/dot))


(def v55_l141 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t56_l143 (is ((fn [v] (== 32.0 v)) v55_l141)))


(def v57_l145 (kind/doc #'la/close?))


(def v58_l147 (la/close? (la/eye 2) (la/eye 2)))


(deftest t59_l149 (is (true? v58_l147)))


(def v60_l151 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t61_l153 (is (false? v60_l151)))


(def v62_l155 (kind/doc #'la/close-scalar?))


(def v63_l157 (la/close-scalar? 1.00000000001 1.0))


(deftest t64_l159 (is (true? v63_l157)))


(def v65_l161 (kind/doc #'la/invert))


(def
 v66_l163
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t67_l166 (is (true? v66_l163)))


(def v68_l168 (kind/doc #'la/solve))


(def
 v70_l171
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t71_l175
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v70_l171)))


(def v72_l178 (kind/doc #'la/eigen))


(def
 v73_l180
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t74_l184
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v73_l180)))


(def v75_l188 (kind/doc #'la/real-eigenvalues))


(def v76_l190 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t77_l192
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v76_l190)))


(def v78_l195 (kind/doc #'la/svd))


(def
 v79_l197
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


(deftest
 t80_l202
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v79_l197)))


(def v81_l207 (kind/doc #'la/qr))


(def
 v82_l209
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t83_l212 (is (true? v82_l209)))


(def v84_l214 (kind/doc #'la/cholesky))


(def
 v85_l216
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t86_l220 (is (true? v85_l216)))


(def v87_l222 (kind/doc #'la/tensor->dmat))


(def
 v88_l224
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t89_l228 (is (true? v88_l224)))


(def v90_l230 (kind/doc #'la/dmat->tensor))


(def
 v91_l232
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t)))))


(deftest t92_l236 (is (true? v91_l232)))


(def v93_l238 (kind/doc #'la/complex-tensor->zmat))


(def
 v94_l240
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t95_l244 (is (true? v94_l240)))


(def v96_l246 (kind/doc #'la/zmat->complex-tensor))


(def
 v97_l248
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t98_l252 (is (true? v97_l248)))


(def v100_l259 (kind/doc #'cx/complex-tensor))


(def v101_l261 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t102_l263
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v101_l261)))


(def v103_l265 (kind/doc #'cx/complex-tensor-real))


(def v104_l267 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t105_l269
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v104_l267)))


(def v106_l271 (kind/doc #'cx/complex))


(def v107_l273 (cx/complex 3.0 4.0))


(deftest
 t108_l275
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v107_l273)))


(def v109_l279 (kind/doc #'cx/re))


(def v110_l281 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t111_l283 (is (= v110_l281 [1.0 2.0])))


(def v112_l285 (kind/doc #'cx/im))


(def v113_l287 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t114_l289 (is (= v113_l287 [3.0 4.0])))


(def v115_l291 (kind/doc #'cx/complex-shape))


(def
 v116_l293
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t117_l296 (is (= v116_l293 [2 2])))


(def v118_l298 (kind/doc #'cx/scalar?))


(def v119_l300 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t120_l302 (is (true? v119_l300)))


(def v121_l304 (kind/doc #'cx/complex?))


(def v122_l306 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t123_l308 (is (true? v122_l306)))


(def v124_l310 (cx/complex? (la/eye 2)))


(deftest t125_l312 (is (false? v124_l310)))


(def v126_l314 (kind/doc #'cx/->tensor))


(def
 v127_l316
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t128_l318 (is (= v127_l316 [2 2])))


(def v129_l320 (kind/doc #'cx/->double-array))


(def
 v130_l322
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t131_l325 (is (= v130_l322 [1.0 3.0 2.0 4.0])))


(def v132_l327 (kind/doc #'cx/add))


(def
 v133_l329
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t134_l333 (is (= v133_l329 [11.0 22.0])))


(def v135_l335 (kind/doc #'cx/sub))


(def
 v136_l337
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t137_l341 (is (= v136_l337 [9.0 18.0])))


(def v138_l343 (kind/doc #'cx/scale))


(def
 v139_l345
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t140_l348 (is (= v139_l345 [[2.0 4.0] [6.0 8.0]])))


(def v141_l350 (kind/doc #'cx/mul))


(def
 v143_l353
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t144_l358 (is (= v143_l353 [-10.0 10.0])))


(def v145_l360 (kind/doc #'cx/conj))


(def
 v146_l362
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t147_l365 (is (= v146_l362 [-3.0 4.0])))


(def v148_l367 (kind/doc #'cx/abs))


(def
 v150_l370
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t151_l373 (is (true? v150_l370)))


(def v152_l375 (kind/doc #'cx/dot))


(def
 v153_l377
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t154_l382 (is (true? v153_l377)))


(def v155_l384 (kind/doc #'cx/dot-conj))


(def
 v157_l387
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t158_l391 (is (true? v157_l387)))


(def v159_l393 (kind/doc #'cx/sum))


(def
 v160_l395
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t161_l399 (is (= v160_l395 [6.0 15.0])))


(def v163_l406 (kind/doc #'bfft/forward))


(def
 v164_l408
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t165_l412 (is (= v164_l408 [4])))


(def v166_l414 (kind/doc #'bfft/inverse))


(def
 v167_l416
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t168_l420 (is (true? v167_l416)))


(def v169_l422 (kind/doc #'bfft/inverse-real))


(def
 v170_l424
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t171_l428 (is (true? v170_l424)))


(def v172_l430 (kind/doc #'bfft/forward-complex))


(def
 v173_l432
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t174_l436 (is (= v173_l432 [4])))


(def v175_l438 (kind/doc #'bfft/dct-forward))


(def v176_l440 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t177_l442 (is ((fn [v] (= 4 (count v))) v176_l440)))


(def v178_l444 (kind/doc #'bfft/dct-inverse))


(def
 v179_l446
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t180_l450 (is (true? v179_l446)))


(def v181_l452 (kind/doc #'bfft/dst-forward))


(def v182_l454 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t183_l456 (is ((fn [v] (= 4 (count v))) v182_l454)))


(def v184_l458 (kind/doc #'bfft/dst-inverse))


(def
 v185_l460
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t186_l464 (is (true? v185_l460)))


(def v187_l466 (kind/doc #'bfft/dht-forward))


(def v188_l468 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l470 (is ((fn [v] (= 4 (count v))) v188_l468)))


(def v190_l472 (kind/doc #'bfft/dht-inverse))


(def
 v191_l474
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l478 (is (true? v191_l474)))


(def v194_l484 (kind/doc #'tape/memory-status))


(def v195_l486 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t196_l488 (is ((fn [s] (= :contiguous s)) v195_l486)))


(def
 v197_l490
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t198_l492 (is ((fn [s] (= :strided s)) v197_l490)))


(def v199_l494 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t200_l496 (is ((fn [s] (= :lazy s)) v199_l494)))


(def v201_l498 (kind/doc #'tape/shares-memory?))


(def
 v202_l500
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/shares-memory? A (la/transpose A))))


(deftest t203_l503 (is (true? v202_l500)))


(def v204_l505 (tape/shares-memory? (la/eye 2) (la/eye 2)))


(deftest t205_l507 (is (false? v204_l505)))


(def v206_l509 (kind/doc #'tape/with-tape))


(def
 v207_l511
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v208_l517 (select-keys tape-example [:result :entries]))


(deftest
 t209_l519
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v208_l517)))


(def v210_l522 (kind/doc #'tape/summary))


(def v211_l524 (tape/summary tape-example))


(deftest t212_l526 (is ((fn [s] (= 4 (:total s))) v211_l524)))


(def v213_l528 (kind/doc #'tape/origin))


(def v214_l530 (tape/origin tape-example (:result tape-example)))


(deftest t215_l532 (is ((fn [dag] (= :la/mmul (:op dag))) v214_l530)))


(def v216_l534 (kind/doc #'tape/mermaid))


(def
 v218_l539
 (kind/mermaid (tape/mermaid tape-example (:result tape-example))))


(def v219_l541 (kind/doc #'tape/detect-memory-status))


(def v221_l546 (mapv tape/detect-memory-status (:entries tape-example)))


(def v223_l552 (kind/doc #'vis/arrow-plot))


(def
 v224_l554
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v225_l558 (kind/doc #'vis/graph-plot))


(def
 v226_l560
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v227_l564 (kind/doc #'vis/matrix->gray-image))


(def
 v228_l566
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t229_l571
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v228_l566)))


(def v230_l573 (kind/doc #'vis/extract-channel))


(def
 v231_l575
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t232_l581
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v231_l575)))
