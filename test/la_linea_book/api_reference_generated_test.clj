(ns
 la-linea-book.api-reference-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [scicloj.la-linea.transform :as bfft]
  [scicloj.la-linea.tape :as tape]
  [scicloj.la-linea.elementwise :as elem]
  [scicloj.la-linea.grad :as grad]
  [scicloj.la-linea.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (kind/doc #'la/matrix))


(def v4_l33 (la/matrix [[1 2] [3 4]]))


(deftest t5_l35 (is ((fn [m] (= [2 2] (dtype/shape m))) v4_l33)))


(def v6_l37 (kind/doc #'la/eye))


(def v7_l39 (la/eye 3))


(deftest
 t8_l41
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l39)))


(def v9_l45 (kind/doc #'la/zeros))


(def v10_l47 (la/zeros 2 3))


(deftest t11_l49 (is ((fn [m] (= [2 3] (dtype/shape m))) v10_l47)))


(def v12_l51 (kind/doc #'la/diag))


(def v13_l53 (la/diag [3 5 7]))


(deftest
 t14_l55
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l53)))


(def v15_l59 (kind/doc #'la/column))


(def v16_l61 (la/column [1 2 3]))


(deftest t17_l63 (is ((fn [v] (= [3 1] (dtype/shape v))) v16_l61)))


(def v18_l65 (kind/doc #'la/row))


(def v19_l67 (la/row [1 2 3]))


(deftest t20_l69 (is ((fn [v] (= [1 3] (dtype/shape v))) v19_l67)))


(def v21_l71 (kind/doc #'la/add))


(def
 v22_l73
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l76 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l73)))


(def v24_l78 (kind/doc #'la/sub))


(def
 v25_l80
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l83 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l80)))


(def v27_l85 (kind/doc #'la/scale))


(def v28_l87 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l89 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l87)))


(def v30_l91 (kind/doc #'la/mul))


(def
 v31_l93
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l96
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l93)))


(def v33_l99 (kind/doc #'la/abs))


(def v34_l101 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l103 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l101)))


(def v36_l105 (kind/doc #'la/sq))


(def v37_l107 (la/sq (la/matrix [[2 3] [4 5]])))


(deftest t38_l109 (is ((fn [m] (== 4.0 (tensor/mget m 0 0))) v37_l107)))


(def v39_l111 (kind/doc #'la/sum))


(def v40_l113 (la/sum (la/matrix [[1 2] [3 4]])))


(deftest t41_l115 (is ((fn [v] (== 10.0 v)) v40_l113)))


(def v42_l117 (kind/doc #'la/mmul))


(def v43_l119 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t44_l122
 (is
  ((fn
    [m]
    (and (= [2 1] (dtype/shape m)) (== 17.0 (tensor/mget m 0 0))))
   v43_l119)))


(def v45_l125 (kind/doc #'la/transpose))


(def v46_l127 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest t47_l129 (is ((fn [m] (= [3 2] (dtype/shape m))) v46_l127)))


(def v48_l131 (kind/doc #'la/submatrix))


(def v49_l133 (la/submatrix (la/eye 4) :all (range 2)))


(deftest t50_l135 (is ((fn [m] (= [4 2] (dtype/shape m))) v49_l133)))


(def v51_l137 (kind/doc #'la/trace))


(def v52_l139 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t53_l141 (is ((fn [v] (== 5.0 v)) v52_l139)))


(def v54_l143 (kind/doc #'la/det))


(def v55_l145 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t56_l147 (is ((fn [v] (la/close-scalar? v -2.0)) v55_l145)))


(def v57_l149 (kind/doc #'la/norm))


(def v58_l151 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t59_l153 (is ((fn [v] (la/close-scalar? v 5.0)) v58_l151)))


(def v60_l155 (kind/doc #'la/dot))


(def v61_l157 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t62_l159 (is ((fn [v] (== 32.0 v)) v61_l157)))


(def v63_l161 (kind/doc #'la/close?))


(def v64_l163 (la/close? (la/eye 2) (la/eye 2)))


(deftest t65_l165 (is (true? v64_l163)))


(def v66_l167 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t67_l169 (is (false? v66_l167)))


(def v68_l171 (kind/doc #'la/close-scalar?))


(def v69_l173 (la/close-scalar? 1.00000000001 1.0))


(deftest t70_l175 (is (true? v69_l173)))


(def v71_l177 (kind/doc #'la/invert))


(def
 v72_l179
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t73_l182 (is (true? v72_l179)))


(def v74_l184 (kind/doc #'la/solve))


(def
 v76_l187
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t77_l191
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v76_l187)))


(def v78_l194 (kind/doc #'la/eigen))


(def
 v79_l196
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t80_l200
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v79_l196)))


(def v81_l204 (kind/doc #'la/real-eigenvalues))


(def v82_l206 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t83_l208
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v82_l206)))


(def v84_l211 (kind/doc #'la/svd))


(def
 v85_l213
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(dtype/shape U) (count S) (dtype/shape Vt)]))


(deftest
 t86_l218
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v85_l213)))


(def v87_l223 (kind/doc #'la/qr))


(def
 v88_l225
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t89_l228 (is (true? v88_l225)))


(def v90_l230 (kind/doc #'la/cholesky))


(def
 v91_l232
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t92_l236 (is (true? v91_l232)))


(def v93_l238 (kind/doc #'la/tensor->dmat))


(def
 v94_l240
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t95_l244 (is (true? v94_l240)))


(def v96_l246 (kind/doc #'la/dmat->tensor))


(def
 v97_l248
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (dtype/shape t))))


(deftest t98_l252 (is (true? v97_l248)))


(def v99_l254 (kind/doc #'la/complex-tensor->zmat))


(def
 v100_l256
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t101_l260 (is (true? v100_l256)))


(def v102_l262 (kind/doc #'la/zmat->complex-tensor))


(def
 v103_l264
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t104_l268 (is (true? v103_l264)))


(def v105_l270 (kind/doc #'la/ones))


(def v106_l272 (la/ones 2 3))


(deftest t107_l274 (is ((fn [m] (= [2 3] (dtype/shape m))) v106_l272)))


(def v108_l276 (kind/doc #'la/mpow))


(def v109_l278 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t110_l280
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v109_l278)))


(def v111_l282 (kind/doc #'la/rank))


(def v112_l284 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t113_l286 (is ((fn [r] (= 1 r)) v112_l284)))


(def v114_l288 (kind/doc #'la/condition-number))


(def v115_l290 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t116_l292 (is ((fn [v] (> v 1.0)) v115_l290)))


(def v117_l294 (kind/doc #'la/pinv))


(def
 v118_l296
 (let
  [A (la/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (la/eye 2))))


(deftest t119_l299 (is (true? v118_l296)))


(def v120_l301 (kind/doc #'la/lstsq))


(def
 v121_l303
 (let
  [{:keys [x rank]}
   (la/lstsq (la/matrix [[1 1] [1 2] [1 3]]) (la/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (la/column [0 1]))}))


(deftest
 t122_l307
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v121_l303)))


(def v123_l309 (kind/doc #'la/null-space))


(def
 v124_l311
 (let
  [ns (la/null-space (la/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (la/matrix [[1 2] [2 4]]) ns) (la/zeros 2 1))))


(deftest t125_l315 (is (true? v124_l311)))


(def v126_l317 (kind/doc #'la/col-space))


(def
 v127_l319
 (second (dtype/shape (la/col-space (la/matrix [[1 2] [2 4]])))))


(deftest t128_l321 (is ((fn [r] (= 1 r)) v127_l319)))


(def v129_l323 (kind/doc #'la/read-matrix))


(def v130_l325 (la/read-matrix [[1 2] [3 4]]))


(deftest t131_l327 (is ((fn [m] (= [2 2] (dtype/shape m))) v130_l325)))


(def v132_l329 (kind/doc #'la/read-column))


(def v133_l331 (la/read-column [5 6 7]))


(deftest t134_l333 (is ((fn [v] (= [3 1] (dtype/shape v))) v133_l331)))


(def v136_l340 (kind/doc #'cx/complex-tensor))


(def v137_l342 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t138_l344
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v137_l342)))


(def v139_l346 (kind/doc #'cx/complex-tensor-real))


(def v140_l348 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t141_l350 (is ((fn [ct] (every? zero? (cx/im ct))) v140_l348)))


(def v142_l352 (kind/doc #'cx/complex))


(def v143_l354 (cx/complex 3.0 4.0))


(deftest
 t144_l356
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v143_l354)))


(def v145_l360 (kind/doc #'cx/re))


(def v146_l362 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t147_l364 (is (= v146_l362 [1.0 2.0])))


(def v148_l366 (kind/doc #'cx/im))


(def v149_l368 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t150_l370 (is (= v149_l368 [3.0 4.0])))


(def v151_l372 (kind/doc #'cx/complex-shape))


(def
 v152_l374
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t153_l377 (is (= v152_l374 [2 2])))


(def v154_l379 (kind/doc #'cx/scalar?))


(def v155_l381 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t156_l383 (is (true? v155_l381)))


(def v157_l385 (kind/doc #'cx/complex?))


(def v158_l387 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t159_l389 (is (true? v158_l387)))


(def v160_l391 (cx/complex? (la/eye 2)))


(deftest t161_l393 (is (false? v160_l391)))


(def v162_l395 (kind/doc #'cx/->tensor))


(def
 v163_l397
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t164_l399 (is (= v163_l397 [2 2])))


(def v165_l401 (kind/doc #'cx/->double-array))


(def
 v166_l403
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t167_l406 (is (= v166_l403 [1.0 3.0 2.0 4.0])))


(def v168_l408 (kind/doc #'cx/add))


(def
 v169_l410
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t170_l414 (is (= v169_l410 [11.0 22.0])))


(def v171_l416 (kind/doc #'cx/sub))


(def
 v172_l418
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t173_l422 (is (= v172_l418 [9.0 18.0])))


(def v174_l424 (kind/doc #'cx/scale))


(def
 v175_l426
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t176_l429 (is (= v175_l426 [[2.0 4.0] [6.0 8.0]])))


(def v177_l431 (kind/doc #'cx/mul))


(def
 v179_l434
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t180_l439 (is (= v179_l434 [-10.0 10.0])))


(def v181_l441 (kind/doc #'cx/conj))


(def
 v182_l443
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t183_l446 (is (= v182_l443 [-3.0 4.0])))


(def v184_l448 (kind/doc #'cx/abs))


(def
 v186_l451
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t187_l454 (is (true? v186_l451)))


(def v188_l456 (kind/doc #'cx/dot))


(def
 v189_l458
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t190_l463 (is (true? v189_l458)))


(def v191_l465 (kind/doc #'cx/dot-conj))


(def
 v193_l468
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t194_l472 (is (true? v193_l468)))


(def v195_l474 (kind/doc #'cx/sum))


(def
 v196_l476
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t197_l480 (is (= v196_l476 [6.0 15.0])))


(def v199_l487 (kind/doc #'bfft/forward))


(def
 v200_l489
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t201_l493 (is (= v200_l489 [4])))


(def v202_l495 (kind/doc #'bfft/inverse))


(def
 v203_l497
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t204_l501 (is (true? v203_l497)))


(def v205_l503 (kind/doc #'bfft/inverse-real))


(def
 v206_l505
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t207_l509 (is (true? v206_l505)))


(def v208_l511 (kind/doc #'bfft/forward-complex))


(def
 v209_l513
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t210_l517 (is (= v209_l513 [4])))


(def v211_l519 (kind/doc #'bfft/dct-forward))


(def v212_l521 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t213_l523 (is ((fn [v] (= 4 (count v))) v212_l521)))


(def v214_l525 (kind/doc #'bfft/dct-inverse))


(def
 v215_l527
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t216_l531 (is (true? v215_l527)))


(def v217_l533 (kind/doc #'bfft/dst-forward))


(def v218_l535 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t219_l537 (is ((fn [v] (= 4 (count v))) v218_l535)))


(def v220_l539 (kind/doc #'bfft/dst-inverse))


(def
 v221_l541
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t222_l545 (is (true? v221_l541)))


(def v223_l547 (kind/doc #'bfft/dht-forward))


(def v224_l549 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t225_l551 (is ((fn [v] (= 4 (count v))) v224_l549)))


(def v226_l553 (kind/doc #'bfft/dht-inverse))


(def
 v227_l555
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t228_l559 (is (true? v227_l555)))


(def v230_l565 (kind/doc #'tape/memory-status))


(def v231_l567 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t232_l569 (is ((fn [s] (= :contiguous s)) v231_l567)))


(def
 v233_l571
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t234_l573 (is ((fn [s] (= :strided s)) v233_l571)))


(def v235_l575 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t236_l577 (is ((fn [s] (= :lazy s)) v235_l575)))


(def v237_l579 (kind/doc #'tape/memory-relation))


(def
 v238_l581
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t239_l584 (is ((fn [r] (= :shared r)) v238_l581)))


(def
 v240_l586
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t241_l588 (is ((fn [r] (= :independent r)) v240_l586)))


(def
 v242_l590
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t243_l592 (is ((fn [r] (= :unknown-lazy r)) v242_l590)))


(def v244_l594 (kind/doc #'tape/with-tape))


(def
 v245_l596
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v246_l602 (select-keys tape-example [:result :entries]))


(deftest
 t247_l604
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v246_l602)))


(def v248_l607 (kind/doc #'tape/summary))


(def v249_l609 (tape/summary tape-example))


(deftest t250_l611 (is ((fn [s] (= 4 (:total s))) v249_l609)))


(def v251_l613 (kind/doc #'tape/origin))


(def v252_l615 (tape/origin tape-example (:result tape-example)))


(deftest t253_l617 (is ((fn [dag] (= :la/mmul (:op dag))) v252_l615)))


(def v254_l619 (kind/doc #'tape/mermaid))


(def v256_l623 (tape/mermaid tape-example (:result tape-example)))


(def v257_l625 (kind/doc #'tape/detect-memory-status))


(def v259_l630 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t260_l632
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v259_l630)))


(def v262_l640 (kind/doc #'elem/sq))


(def v263_l642 (elem/sq (la/column [2 3 4])))


(deftest
 t264_l644
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v263_l642)))


(def v265_l646 (kind/doc #'elem/sqrt))


(def v266_l648 (elem/sqrt (la/column [4 9 16])))


(deftest
 t267_l650
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v266_l648)))


(def v268_l652 (kind/doc #'elem/exp))


(def
 v269_l654
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t270_l656 (is (true? v269_l654)))


(def v271_l658 (kind/doc #'elem/log))


(def
 v272_l660
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [(Math/E)])) 0 0)
  1.0))


(deftest t273_l662 (is (true? v272_l660)))


(def v274_l664 (kind/doc #'elem/log10))


(def
 v275_l666
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t276_l668 (is (true? v275_l666)))


(def v277_l670 (kind/doc #'elem/sin))


(def
 v278_l672
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ Math/PI 2)])) 0 0)
  1.0))


(deftest t279_l674 (is (true? v278_l672)))


(def v280_l676 (kind/doc #'elem/cos))


(def
 v281_l678
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t282_l680 (is (true? v281_l678)))


(def v283_l682 (kind/doc #'elem/tan))


(def
 v284_l684
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ Math/PI 4)])) 0 0)
  1.0))


(deftest t285_l686 (is (true? v284_l684)))


(def v286_l688 (kind/doc #'elem/sinh))


(def
 v287_l690
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t288_l692 (is (true? v287_l690)))


(def v289_l694 (kind/doc #'elem/cosh))


(def
 v290_l696
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t291_l698 (is (true? v290_l696)))


(def v292_l700 (kind/doc #'elem/tanh))


(def
 v293_l702
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t294_l704 (is (true? v293_l702)))


(def v295_l706 (kind/doc #'elem/abs))


(def v296_l708 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t297_l710 (is ((fn [v] (== 5.0 v)) v296_l708)))


(def v298_l712 (kind/doc #'elem/sum))


(def v299_l714 (elem/sum (la/column [1 2 3 4])))


(deftest t300_l716 (is ((fn [v] (== 10.0 v)) v299_l714)))


(def v301_l718 (kind/doc #'elem/mean))


(def v302_l720 (elem/mean (la/column [2 4 6])))


(deftest t303_l722 (is ((fn [v] (== 4.0 v)) v302_l720)))


(def v304_l724 (kind/doc #'elem/pow))


(def v305_l726 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t306_l728 (is ((fn [v] (== 8.0 v)) v305_l726)))


(def v307_l730 (kind/doc #'elem/cbrt))


(def
 v308_l732
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t309_l734 (is (true? v308_l732)))


(def v310_l736 (kind/doc #'elem/floor))


(def v311_l738 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t312_l740 (is ((fn [v] (== 2.0 v)) v311_l738)))


(def v313_l742 (kind/doc #'elem/ceil))


(def v314_l744 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t315_l746 (is ((fn [v] (== 3.0 v)) v314_l744)))


(def v316_l748 (kind/doc #'elem/min))


(def
 v317_l750
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t318_l752 (is ((fn [v] (== 3.0 v)) v317_l750)))


(def v319_l754 (kind/doc #'elem/max))


(def
 v320_l756
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t321_l758 (is ((fn [v] (== 5.0 v)) v320_l756)))


(def v322_l760 (kind/doc #'elem/asin))


(def v323_l762 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t324_l764
 (is ((fn [v] (la/close-scalar? v (Math/asin 0.5))) v323_l762)))


(def v325_l766 (kind/doc #'elem/acos))


(def v326_l768 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t327_l770
 (is ((fn [v] (la/close-scalar? v (Math/acos 0.5))) v326_l768)))


(def v328_l772 (kind/doc #'elem/atan))


(def v329_l774 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t330_l776
 (is ((fn [v] (la/close-scalar? v (Math/atan 1.0))) v329_l774)))


(def v331_l778 (kind/doc #'elem/log1p))


(def v332_l780 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t333_l782 (is ((fn [v] (la/close-scalar? v 0.0)) v332_l780)))


(def v334_l784 (kind/doc #'elem/expm1))


(def v335_l786 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t336_l788 (is ((fn [v] (la/close-scalar? v 0.0)) v335_l786)))


(def v337_l790 (kind/doc #'elem/round))


(def v338_l792 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t339_l794 (is ((fn [v] (== 3.0 v)) v338_l792)))


(def v340_l796 (kind/doc #'elem/clip))


(def
 v341_l798
 (vec (dtype/->reader (elem/clip (la/column [-2 0.5 3]) -1 1))))


(deftest t342_l800 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v341_l798)))


(def v344_l806 (kind/doc #'grad/grad))


(def
 v345_l808
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t346_l814 (is (true? v345_l808)))


(def v348_l820 (kind/doc #'vis/arrow-plot))


(def
 v349_l822
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v350_l826 (kind/doc #'vis/graph-plot))


(def
 v351_l828
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v352_l832 (kind/doc #'vis/matrix->gray-image))


(def
 v353_l834
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t354_l839
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v353_l834)))


(def v355_l841 (kind/doc #'vis/extract-channel))


(def
 v356_l843
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t357_l849
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v356_l843)))
