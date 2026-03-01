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


(deftest t5_l35 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l33)))


(def v6_l37 (kind/doc #'la/eye))


(def v7_l39 (la/eye 3))


(deftest
 t8_l41
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l39)))


(def v9_l45 (kind/doc #'la/zeros))


(def v10_l47 (la/zeros 2 3))


(deftest
 t11_l49
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l47)))


(def v12_l51 (kind/doc #'la/diag))


(def v13_l53 (la/diag [3 5 7]))


(deftest
 t14_l55
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l53)))


(def v15_l59 (kind/doc #'la/column))


(def v16_l61 (la/column [1 2 3]))


(deftest
 t17_l63
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l61)))


(def v18_l65 (kind/doc #'la/row))


(def v19_l67 (la/row [1 2 3]))


(deftest
 t20_l69
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l67)))


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
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v43_l119)))


(def v45_l125 (kind/doc #'la/transpose))


(def v46_l127 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t47_l129
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v46_l127)))


(def v48_l131 (kind/doc #'la/submatrix))


(def v49_l133 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t50_l135
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v49_l133)))


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
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


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
  (= [2 2] (vec (dtype/shape t)))))


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


(def v106_l275 (kind/doc #'cx/complex-tensor))


(def v107_l277 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t108_l279
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v107_l277)))


(def v109_l281 (kind/doc #'cx/complex-tensor-real))


(def v110_l283 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t111_l285
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v110_l283)))


(def v112_l287 (kind/doc #'cx/complex))


(def v113_l289 (cx/complex 3.0 4.0))


(deftest
 t114_l291
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v113_l289)))


(def v115_l295 (kind/doc #'cx/re))


(def v116_l297 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t117_l299 (is (= v116_l297 [1.0 2.0])))


(def v118_l301 (kind/doc #'cx/im))


(def v119_l303 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t120_l305 (is (= v119_l303 [3.0 4.0])))


(def v121_l307 (kind/doc #'cx/complex-shape))


(def
 v122_l309
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t123_l312 (is (= v122_l309 [2 2])))


(def v124_l314 (kind/doc #'cx/scalar?))


(def v125_l316 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t126_l318 (is (true? v125_l316)))


(def v127_l320 (kind/doc #'cx/complex?))


(def v128_l322 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t129_l324 (is (true? v128_l322)))


(def v130_l326 (cx/complex? (la/eye 2)))


(deftest t131_l328 (is (false? v130_l326)))


(def v132_l330 (kind/doc #'cx/->tensor))


(def
 v133_l332
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t134_l334 (is (= v133_l332 [2 2])))


(def v135_l336 (kind/doc #'cx/->double-array))


(def
 v136_l338
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t137_l341 (is (= v136_l338 [1.0 3.0 2.0 4.0])))


(def v138_l343 (kind/doc #'cx/add))


(def
 v139_l345
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t140_l349 (is (= v139_l345 [11.0 22.0])))


(def v141_l351 (kind/doc #'cx/sub))


(def
 v142_l353
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t143_l357 (is (= v142_l353 [9.0 18.0])))


(def v144_l359 (kind/doc #'cx/scale))


(def
 v145_l361
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t146_l364 (is (= v145_l361 [[2.0 4.0] [6.0 8.0]])))


(def v147_l366 (kind/doc #'cx/mul))


(def
 v149_l369
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t150_l374 (is (= v149_l369 [-10.0 10.0])))


(def v151_l376 (kind/doc #'cx/conj))


(def
 v152_l378
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t153_l381 (is (= v152_l378 [-3.0 4.0])))


(def v154_l383 (kind/doc #'cx/abs))


(def
 v156_l386
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t157_l389 (is (true? v156_l386)))


(def v158_l391 (kind/doc #'cx/dot))


(def
 v159_l393
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t160_l398 (is (true? v159_l393)))


(def v161_l400 (kind/doc #'cx/dot-conj))


(def
 v163_l403
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t164_l407 (is (true? v163_l403)))


(def v165_l409 (kind/doc #'cx/sum))


(def
 v166_l411
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t167_l415 (is (= v166_l411 [6.0 15.0])))


(def v169_l422 (kind/doc #'bfft/forward))


(def
 v170_l424
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t171_l428 (is (= v170_l424 [4])))


(def v172_l430 (kind/doc #'bfft/inverse))


(def
 v173_l432
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t174_l436 (is (true? v173_l432)))


(def v175_l438 (kind/doc #'bfft/inverse-real))


(def
 v176_l440
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t177_l444 (is (true? v176_l440)))


(def v178_l446 (kind/doc #'bfft/forward-complex))


(def
 v179_l448
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t180_l452 (is (= v179_l448 [4])))


(def v181_l454 (kind/doc #'bfft/dct-forward))


(def v182_l456 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t183_l458 (is ((fn [v] (= 4 (count v))) v182_l456)))


(def v184_l460 (kind/doc #'bfft/dct-inverse))


(def
 v185_l462
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t186_l466 (is (true? v185_l462)))


(def v187_l468 (kind/doc #'bfft/dst-forward))


(def v188_l470 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l472 (is ((fn [v] (= 4 (count v))) v188_l470)))


(def v190_l474 (kind/doc #'bfft/dst-inverse))


(def
 v191_l476
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l480 (is (true? v191_l476)))


(def v193_l482 (kind/doc #'bfft/dht-forward))


(def v194_l484 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t195_l486 (is ((fn [v] (= 4 (count v))) v194_l484)))


(def v196_l488 (kind/doc #'bfft/dht-inverse))


(def
 v197_l490
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t198_l494 (is (true? v197_l490)))


(def v200_l500 (kind/doc #'tape/memory-status))


(def v201_l502 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t202_l504 (is ((fn [s] (= :contiguous s)) v201_l502)))


(def
 v203_l506
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t204_l508 (is ((fn [s] (= :strided s)) v203_l506)))


(def v205_l510 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t206_l512 (is ((fn [s] (= :lazy s)) v205_l510)))


(def v207_l514 (kind/doc #'tape/shares-memory?))


(def
 v208_l516
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/shares-memory? A (la/transpose A))))


(deftest t209_l519 (is (true? v208_l516)))


(def v210_l521 (tape/shares-memory? (la/eye 2) (la/eye 2)))


(deftest t211_l523 (is (false? v210_l521)))


(def v212_l525 (kind/doc #'tape/with-tape))


(def
 v213_l527
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v214_l533 (select-keys tape-example [:result :entries]))


(deftest
 t215_l535
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v214_l533)))


(def v216_l538 (kind/doc #'tape/summary))


(def v217_l540 (tape/summary tape-example))


(deftest t218_l542 (is ((fn [s] (= 4 (:total s))) v217_l540)))


(def v219_l544 (kind/doc #'tape/origin))


(def v220_l546 (tape/origin tape-example (:result tape-example)))


(deftest t221_l548 (is ((fn [dag] (= :la/mmul (:op dag))) v220_l546)))


(def v222_l550 (kind/doc #'tape/mermaid))


(def v224_l554 (tape/mermaid tape-example (:result tape-example)))


(def v225_l556 (kind/doc #'tape/detect-memory-status))


(def v227_l561 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t228_l563
 (is ((fn [v] (every? #{:independent :lazy :shared} v)) v227_l561)))


(def v230_l571 (kind/doc #'elem/sq))


(def v231_l573 (elem/sq (la/column [2 3 4])))


(deftest
 t232_l575
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v231_l573)))


(def v233_l577 (kind/doc #'elem/sqrt))


(def v234_l579 (elem/sqrt (la/column [4 9 16])))


(deftest
 t235_l581
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v234_l579)))


(def v236_l583 (kind/doc #'elem/exp))


(def
 v237_l585
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t238_l587 (is (true? v237_l585)))


(def v239_l589 (kind/doc #'elem/log))


(def
 v240_l591
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [(Math/E)])) 0 0)
  1.0))


(deftest t241_l593 (is (true? v240_l591)))


(def v242_l595 (kind/doc #'elem/log10))


(def
 v243_l597
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t244_l599 (is (true? v243_l597)))


(def v245_l601 (kind/doc #'elem/sin))


(def
 v246_l603
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ Math/PI 2)])) 0 0)
  1.0))


(deftest t247_l605 (is (true? v246_l603)))


(def v248_l607 (kind/doc #'elem/cos))


(def
 v249_l609
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t250_l611 (is (true? v249_l609)))


(def v251_l613 (kind/doc #'elem/tan))


(def
 v252_l615
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ Math/PI 4)])) 0 0)
  1.0))


(deftest t253_l617 (is (true? v252_l615)))


(def v254_l619 (kind/doc #'elem/sinh))


(def
 v255_l621
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t256_l623 (is (true? v255_l621)))


(def v257_l625 (kind/doc #'elem/cosh))


(def
 v258_l627
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t259_l629 (is (true? v258_l627)))


(def v260_l631 (kind/doc #'elem/tanh))


(def
 v261_l633
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t262_l635 (is (true? v261_l633)))


(def v263_l637 (kind/doc #'elem/abs))


(def v264_l639 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t265_l641 (is ((fn [v] (== 5.0 v)) v264_l639)))


(def v266_l643 (kind/doc #'elem/sum))


(def v267_l645 (elem/sum (la/column [1 2 3 4])))


(deftest t268_l647 (is ((fn [v] (== 10.0 v)) v267_l645)))


(def v269_l649 (kind/doc #'elem/mean))


(def v270_l651 (elem/mean (la/column [2 4 6])))


(deftest t271_l653 (is ((fn [v] (== 4.0 v)) v270_l651)))


(def v272_l655 (kind/doc #'elem/pow))


(def v273_l657 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t274_l659 (is ((fn [v] (== 8.0 v)) v273_l657)))


(def v275_l661 (kind/doc #'elem/cbrt))


(def
 v276_l663
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t277_l665 (is (true? v276_l663)))


(def v278_l667 (kind/doc #'elem/floor))


(def v279_l669 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t280_l671 (is ((fn [v] (== 2.0 v)) v279_l669)))


(def v281_l673 (kind/doc #'elem/ceil))


(def v282_l675 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t283_l677 (is ((fn [v] (== 3.0 v)) v282_l675)))


(def v284_l679 (kind/doc #'elem/min))


(def
 v285_l681
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t286_l683 (is ((fn [v] (== 3.0 v)) v285_l681)))


(def v287_l685 (kind/doc #'elem/max))


(def
 v288_l687
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t289_l689 (is ((fn [v] (== 5.0 v)) v288_l687)))


(def v291_l695 (kind/doc #'grad/grad))


(def
 v292_l697
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t293_l703 (is (true? v292_l697)))


(def v295_l709 (kind/doc #'vis/arrow-plot))


(def
 v296_l711
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v297_l715 (kind/doc #'vis/graph-plot))


(def
 v298_l717
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v299_l721 (kind/doc #'vis/matrix->gray-image))


(def
 v300_l723
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t301_l728
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v300_l723)))


(def v302_l730 (kind/doc #'vis/extract-channel))


(def
 v303_l732
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t304_l738
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v303_l732)))
