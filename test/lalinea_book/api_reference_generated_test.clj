(ns
 lalinea-book.api-reference-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.complex :as cx]
  [scicloj.lalinea.transform :as ft]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.grad :as grad]
  [scicloj.lalinea.vis :as vis]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (kind/doc #'t/matrix))


(def v4_l31 (t/matrix [[1 2] [3 4]]))


(deftest t5_l33 (is ((fn [m] (= [2 2] (t/shape m))) v4_l31)))


(def v6_l35 (kind/doc #'t/eye))


(def v7_l37 (t/eye 3))


(deftest
 t8_l39
 (is
  ((fn
    [m]
    (and (= [3 3] (t/shape m)) (== 1.0 (m 0 0)) (== 0.0 (m 0 1))))
   v7_l37)))


(def v9_l43 (kind/doc #'t/zeros))


(def v10_l45 (t/zeros 2 3))


(deftest t11_l47 (is ((fn [m] (= [2 3] (t/shape m))) v10_l45)))


(def v12_l49 (kind/doc #'t/diag))


(def v13_l51 (t/diag [3 5 7]))


(deftest
 t14_l53
 (is
  ((fn
    [m]
    (and (= [3 3] (t/shape m)) (== 5.0 (m 1 1)) (== 0.0 (m 0 1))))
   v13_l51)))


(def v16_l59 (t/diag (t/matrix [[1 2 3] [4 5 6] [7 8 9]])))


(deftest t17_l61 (is ((fn [v] (= [1.0 5.0 9.0] v)) v16_l59)))


(def v18_l63 (kind/doc #'t/column))


(def v19_l65 (t/column [1 2 3]))


(deftest t20_l67 (is ((fn [v] (= [3 1] (t/shape v))) v19_l65)))


(def v21_l69 (kind/doc #'t/row))


(def v22_l71 (t/row [1 2 3]))


(deftest t23_l73 (is ((fn [v] (= [1 3] (t/shape v))) v22_l71)))


(def v24_l76 (kind/doc #'t/compute-matrix))


(def v25_l78 (t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0))))


(deftest t26_l80 (is ((fn [m] (= (t/eye 3) m)) v25_l78)))


(def v27_l82 (kind/doc #'t/reduce-axis))


(def v29_l85 (t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) la/sum 1))


(deftest
 t30_l87
 (is
  ((fn
    [v]
    (and
     (= [2] (t/shape v))
     (la/close-scalar? (v 0) 6.0)
     (la/close-scalar? (v 1) 15.0)))
   v29_l85)))


(def v31_l91 (kind/doc #'t/flatten))


(def v32_l93 (t/flatten (t/column [1 2 3])))


(deftest t33_l95 (is ((fn [v] (= [1.0 2.0 3.0] v)) v32_l93)))


(def v34_l97 (kind/doc #'t/hstack))


(def v35_l99 (t/hstack [(t/column [1 2]) (t/column [3 4])]))


(deftest t36_l101 (is ((fn [m] (= [[1.0 3.0] [2.0 4.0]] m)) v35_l99)))


(def v37_l103 (kind/doc #'t/submatrix))


(def v38_l105 (t/submatrix (t/eye 4) :all (range 2)))


(deftest t39_l107 (is ((fn [m] (= [4 2] (t/shape m))) v38_l105)))


(def v40_l109 (kind/doc #'t/tensor->dmat))


(def
 v41_l111
 (let
  [t (t/matrix [[1 2] [3 4]]) dm (t/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t42_l115 (is (true? v41_l111)))


(def v43_l117 (kind/doc #'t/dmat->tensor))


(def
 v44_l119
 (let
  [dm (t/tensor->dmat (t/eye 2)) t (t/dmat->tensor dm)]
  (= [2 2] (t/shape t))))


(deftest t45_l123 (is (true? v44_l119)))


(def v46_l125 (kind/doc #'t/complex-tensor->zmat))


(def
 v47_l127
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t48_l131 (is (true? v47_l127)))


(def v49_l133 (kind/doc #'t/zmat->complex-tensor))


(def
 v50_l135
 (let
  [zm
   (t/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (t/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t51_l139 (is (true? v50_l135)))


(def v52_l141 (kind/doc #'t/ones))


(def v53_l143 (t/ones 2 3))


(deftest t54_l145 (is ((fn [m] (= [2 3] (t/shape m))) v53_l143)))


(def v55_l147 (kind/doc #'t/real-tensor?))


(def v56_l149 (t/real-tensor? (t/matrix [[1 2] [3 4]])))


(deftest t57_l151 (is (true? v56_l149)))


(def v58_l153 (t/real-tensor? [1 2 3]))


(deftest t59_l155 (is (false? v58_l153)))


(def v60_l157 (kind/doc #'t/->real-tensor))


(def v61_l159 (t/->real-tensor (t/matrix [[1 2] [3 4]])))


(deftest t62_l161 (is ((fn [rt] (t/real-tensor? rt)) v61_l159)))


(def v63_l163 (kind/doc #'t/->tensor))


(def v64_l165 (t/->tensor (t/matrix [[1 2] [3 4]])))


(deftest t65_l167 (is ((fn [t] (not (t/real-tensor? t))) v64_l165)))


(def v67_l171 (kind/doc #'la/add))


(def
 v68_l173
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t69_l176 (is ((fn [m] (== 11.0 (m 0 0))) v68_l173)))


(def v70_l178 (kind/doc #'la/sub))


(def
 v71_l180
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t72_l183 (is ((fn [m] (== 9.0 (m 0 0))) v71_l180)))


(def v73_l185 (kind/doc #'la/scale))


(def v74_l187 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t75_l189 (is ((fn [m] (== 6.0 (m 0 1))) v74_l187)))


(def v76_l191 (kind/doc #'la/mul))


(def
 v77_l193
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t78_l196
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v77_l193)))


(def v79_l199 (kind/doc #'la/abs))


(def v80_l201 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t81_l203 (is ((fn [m] (== 3.0 (m 0 0))) v80_l201)))


(def v82_l205 (kind/doc #'la/sq))


(def v83_l207 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t84_l209 (is ((fn [m] (== 4.0 (m 0 0))) v83_l207)))


(def v85_l211 (kind/doc #'la/sum))


(def v86_l213 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t87_l215 (is ((fn [v] (== 10.0 v)) v86_l213)))


(def v88_l217 (kind/doc #'la/prod))


(def v89_l219 (la/prod (t/matrix [2 3 4])))


(deftest t90_l221 (is ((fn [v] (== 24.0 v)) v89_l219)))


(def v91_l223 (kind/doc #'la/mmul))


(def v92_l225 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t93_l228
 (is ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v92_l225)))


(def v94_l231 (kind/doc #'la/transpose))


(def v95_l233 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t96_l235 (is ((fn [m] (= [3 2] (t/shape m))) v95_l233)))


(def v97_l237 (kind/doc #'la/trace))


(def v98_l239 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t99_l241 (is ((fn [v] (== 5.0 v)) v98_l239)))


(def v100_l243 (kind/doc #'la/det))


(def v101_l245 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t102_l247 (is ((fn [v] (la/close-scalar? v -2.0)) v101_l245)))


(def v103_l249 (kind/doc #'la/norm))


(def v104_l251 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t105_l253 (is ((fn [v] (la/close-scalar? v 5.0)) v104_l251)))


(def v106_l255 (kind/doc #'la/dot))


(def v107_l257 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t108_l259 (is ((fn [v] (== 32.0 v)) v107_l257)))


(def v109_l261 (kind/doc #'la/close?))


(def v110_l263 (la/close? (t/eye 2) (t/eye 2)))


(deftest t111_l265 (is (true? v110_l263)))


(def v112_l267 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t113_l269 (is (false? v112_l267)))


(def v114_l271 (kind/doc #'la/close-scalar?))


(def v115_l273 (la/close-scalar? 1.00000000001 1.0))


(deftest t116_l275 (is (true? v115_l273)))


(def v117_l277 (kind/doc #'la/invert))


(def
 v118_l279
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t119_l282 (is (true? v118_l279)))


(def v120_l284 (kind/doc #'la/solve))


(def
 v122_l287
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t123_l291
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v122_l287)))


(def v124_l294 (kind/doc #'la/eigen))


(def
 v125_l296
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t126_l300
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v125_l296)))


(def v127_l304 (kind/doc #'la/real-eigenvalues))


(def v128_l306 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t129_l308
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v128_l306)))


(def v130_l311 (kind/doc #'la/svd))


(def
 v131_l313
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t132_l318
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v131_l313)))


(def v133_l323 (kind/doc #'la/qr))


(def
 v134_l325
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t135_l328 (is (true? v134_l325)))


(def v136_l330 (kind/doc #'la/cholesky))


(def
 v137_l332
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t138_l336 (is (true? v137_l332)))


(def v139_l338 (kind/doc #'la/mpow))


(def v140_l340 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t141_l342
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v140_l340)))


(def v142_l344 (kind/doc #'la/rank))


(def v143_l346 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t144_l348 (is ((fn [r] (= 1 r)) v143_l346)))


(def v145_l350 (kind/doc #'la/condition-number))


(def v146_l352 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t147_l354 (is ((fn [v] (> v 1.0)) v146_l352)))


(def v148_l356 (kind/doc #'la/pinv))


(def
 v149_l358
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t150_l361 (is (true? v149_l358)))


(def v151_l363 (kind/doc #'la/lstsq))


(def
 v152_l365
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t153_l369
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v152_l365)))


(def v154_l371 (kind/doc #'la/null-space))


(def
 v155_l373
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t156_l377 (is (true? v155_l373)))


(def v157_l379 (kind/doc #'la/col-space))


(def
 v158_l381
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t159_l383 (is ((fn [r] (= 1 r)) v158_l381)))


(def v160_l385 (kind/doc #'la/lift))


(def v162_l388 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t163_l390
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v162_l388)))


(def v164_l393 (kind/doc #'la/lifted))


(def
 v166_l396
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t167_l399
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v166_l396)))


(def v169_l406 (kind/doc #'cx/complex-tensor))


(def v170_l408 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t171_l410
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v170_l408)))


(def v172_l412 (kind/doc #'cx/complex-tensor-real))


(def v173_l414 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t174_l416 (is ((fn [ct] (every? zero? (cx/im ct))) v173_l414)))


(def v175_l418 (kind/doc #'cx/complex))


(def v176_l420 (cx/complex 3.0 4.0))


(deftest
 t177_l422
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v176_l420)))


(def v178_l426 (kind/doc #'cx/re))


(def v179_l428 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t180_l430 (is (= v179_l428 [1.0 2.0])))


(def v181_l432 (kind/doc #'cx/im))


(def v182_l434 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t183_l436 (is (= v182_l434 [3.0 4.0])))


(def v184_l438 (kind/doc #'cx/complex-shape))


(def
 v185_l440
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t186_l443 (is (= v185_l440 [2 2])))


(def v187_l445 (kind/doc #'cx/scalar?))


(def v188_l447 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t189_l449 (is (true? v188_l447)))


(def v190_l451 (kind/doc #'cx/complex?))


(def v191_l453 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t192_l455 (is (true? v191_l453)))


(def v193_l457 (cx/complex? (t/eye 2)))


(deftest t194_l459 (is (false? v193_l457)))


(def v195_l461 (kind/doc #'cx/->tensor))


(def
 v196_l463
 (t/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t197_l465 (is (= v196_l463 [2 2])))


(def v198_l467 (kind/doc #'cx/->double-array))


(def
 v199_l469
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t200_l472 (is (= v199_l469 [1.0 3.0 2.0 4.0])))


(def v201_l474 (kind/doc #'cx/wrap-tensor))


(def
 v202_l476
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t203_l480
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v202_l476)))


(def v204_l482 (kind/doc #'cx/add))


(def
 v205_l484
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t206_l488 (is (= v205_l484 [11.0 22.0])))


(def v207_l490 (kind/doc #'cx/sub))


(def
 v208_l492
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t209_l496 (is (= v208_l492 [9.0 18.0])))


(def v210_l498 (kind/doc #'cx/scale))


(def
 v211_l500
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t212_l503 (is (= v211_l500 [[2.0 4.0] [6.0 8.0]])))


(def v213_l505 (kind/doc #'cx/mul))


(def
 v215_l508
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t216_l513 (is (= v215_l508 [-10.0 10.0])))


(def v217_l515 (kind/doc #'cx/conj))


(def
 v218_l517
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t219_l520 (is (= v218_l517 [-3.0 4.0])))


(def v220_l522 (kind/doc #'cx/abs))


(def
 v222_l525
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t223_l528 (is (true? v222_l525)))


(def v224_l530 (kind/doc #'cx/dot))


(def
 v225_l532
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t226_l537 (is (true? v225_l532)))


(def v227_l539 (kind/doc #'cx/dot-conj))


(def
 v229_l542
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t230_l546 (is (true? v229_l542)))


(def v231_l548 (kind/doc #'cx/sum))


(def
 v232_l550
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t233_l554 (is (= v232_l550 [6.0 15.0])))


(def v235_l561 (kind/doc #'ft/forward))


(def
 v236_l563
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t237_l567 (is (= v236_l563 [4])))


(def v238_l569 (kind/doc #'ft/inverse))


(def
 v239_l571
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t240_l575 (is (true? v239_l571)))


(def v241_l577 (kind/doc #'ft/inverse-real))


(def
 v242_l579
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t243_l583 (is (true? v242_l579)))


(def v244_l585 (kind/doc #'ft/forward-complex))


(def
 v245_l587
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t246_l591 (is (= v245_l587 [4])))


(def v247_l593 (kind/doc #'ft/dct-forward))


(def v248_l595 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t249_l597 (is ((fn [v] (= 4 (count v))) v248_l595)))


(def v250_l599 (kind/doc #'ft/dct-inverse))


(def
 v251_l601
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t252_l605 (is (true? v251_l601)))


(def v253_l607 (kind/doc #'ft/dst-forward))


(def v254_l609 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t255_l611 (is ((fn [v] (= 4 (count v))) v254_l609)))


(def v256_l613 (kind/doc #'ft/dst-inverse))


(def
 v257_l615
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t258_l619 (is (true? v257_l615)))


(def v259_l621 (kind/doc #'ft/dht-forward))


(def v260_l623 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t261_l625 (is ((fn [v] (= 4 (count v))) v260_l623)))


(def v262_l627 (kind/doc #'ft/dht-inverse))


(def
 v263_l629
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t264_l633 (is (true? v263_l629)))


(def v266_l639 (kind/doc #'tape/memory-status))


(def v267_l641 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t268_l643 (is ((fn [s] (= :contiguous s)) v267_l641)))


(def
 v269_l645
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t270_l647 (is ((fn [s] (= :strided s)) v269_l645)))


(def v271_l649 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t272_l651 (is ((fn [s] (= :lazy s)) v271_l649)))


(def v273_l653 (kind/doc #'tape/memory-relation))


(def
 v274_l655
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t275_l658 (is ((fn [r] (= :shared r)) v274_l655)))


(def
 v276_l660
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t277_l662 (is ((fn [r] (= :independent r)) v276_l660)))


(def
 v278_l664
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t279_l666 (is ((fn [r] (= :unknown-lazy r)) v278_l664)))


(def v280_l668 (kind/doc #'tape/with-tape))


(def
 v281_l670
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v282_l676 (select-keys tape-example [:result :entries]))


(deftest
 t283_l678
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v282_l676)))


(def v284_l681 (kind/doc #'tape/summary))


(def v285_l683 (tape/summary tape-example))


(deftest t286_l685 (is ((fn [s] (= 4 (:total s))) v285_l683)))


(def v287_l687 (kind/doc #'tape/origin))


(def v288_l689 (tape/origin tape-example (:result tape-example)))


(deftest t289_l691 (is ((fn [dag] (= :la/mmul (:op dag))) v288_l689)))


(def v290_l693 (kind/doc #'tape/mermaid))


(def v292_l697 (tape/mermaid tape-example (:result tape-example)))


(def v293_l699 (kind/doc #'tape/detect-memory-status))


(def v295_l704 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t296_l706
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v295_l704)))


(def v298_l714 (kind/doc #'elem/sq))


(def v299_l716 (elem/sq (t/column [2 3 4])))


(deftest
 t300_l718
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v299_l716)))


(def v301_l720 (kind/doc #'elem/sqrt))


(def v302_l722 (elem/sqrt (t/column [4 9 16])))


(deftest
 t303_l724
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v302_l722)))


(def v304_l726 (kind/doc #'elem/exp))


(def v305_l728 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t306_l730 (is (true? v305_l728)))


(def v307_l732 (kind/doc #'elem/log))


(def
 v308_l734
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t309_l736 (is (true? v308_l734)))


(def v310_l738 (kind/doc #'elem/log10))


(def
 v311_l740
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t312_l742 (is (true? v311_l740)))


(def v313_l744 (kind/doc #'elem/sin))


(def
 v314_l746
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t315_l748 (is (true? v314_l746)))


(def v316_l750 (kind/doc #'elem/cos))


(def v317_l752 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t318_l754 (is (true? v317_l752)))


(def v319_l756 (kind/doc #'elem/tan))


(def
 v320_l758
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t321_l760 (is (true? v320_l758)))


(def v322_l762 (kind/doc #'elem/sinh))


(def v323_l764 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t324_l766 (is (true? v323_l764)))


(def v325_l768 (kind/doc #'elem/cosh))


(def v326_l770 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t327_l772 (is (true? v326_l770)))


(def v328_l774 (kind/doc #'elem/tanh))


(def v329_l776 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t330_l778 (is (true? v329_l776)))


(def v331_l780 (kind/doc #'elem/abs))


(def v332_l782 ((elem/abs (t/column [-5])) 0 0))


(deftest t333_l784 (is ((fn [v] (== 5.0 v)) v332_l782)))


(def v334_l786 (kind/doc #'elem/sum))


(def v335_l788 (elem/sum (t/column [1 2 3 4])))


(deftest t336_l790 (is ((fn [v] (== 10.0 v)) v335_l788)))


(def v337_l792 (kind/doc #'elem/mean))


(def v338_l794 (elem/mean (t/column [2 4 6])))


(deftest t339_l796 (is ((fn [v] (== 4.0 v)) v338_l794)))


(def v340_l798 (kind/doc #'elem/pow))


(def v341_l800 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t342_l802 (is ((fn [v] (== 8.0 v)) v341_l800)))


(def v343_l804 (kind/doc #'elem/cbrt))


(def v344_l806 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t345_l808 (is (true? v344_l806)))


(def v346_l810 (kind/doc #'elem/floor))


(def v347_l812 ((elem/floor (t/column [2.7])) 0 0))


(deftest t348_l814 (is ((fn [v] (== 2.0 v)) v347_l812)))


(def v349_l816 (kind/doc #'elem/ceil))


(def v350_l818 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t351_l820 (is ((fn [v] (== 3.0 v)) v350_l818)))


(def v352_l822 (kind/doc #'elem/min))


(def v353_l824 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t354_l826 (is ((fn [v] (== 3.0 v)) v353_l824)))


(def v355_l828 (kind/doc #'elem/max))


(def v356_l830 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t357_l832 (is ((fn [v] (== 5.0 v)) v356_l830)))


(def v358_l834 (kind/doc #'elem/asin))


(def v359_l836 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t360_l838
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v359_l836)))


(def v361_l840 (kind/doc #'elem/acos))


(def v362_l842 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t363_l844
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v362_l842)))


(def v364_l846 (kind/doc #'elem/atan))


(def v365_l848 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t366_l850
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v365_l848)))


(def v367_l852 (kind/doc #'elem/log1p))


(def v368_l854 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t369_l856 (is ((fn [v] (la/close-scalar? v 0.0)) v368_l854)))


(def v370_l858 (kind/doc #'elem/expm1))


(def v371_l860 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t372_l862 (is ((fn [v] (la/close-scalar? v 0.0)) v371_l860)))


(def v373_l864 (kind/doc #'elem/round))


(def v374_l866 ((elem/round (t/column [2.7])) 0 0))


(deftest t375_l868 (is ((fn [v] (== 3.0 v)) v374_l866)))


(def v376_l870 (kind/doc #'elem/clip))


(def v377_l872 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t378_l874 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v377_l872)))


(def v380_l880 (kind/doc #'grad/grad))


(def
 v381_l882
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t382_l888 (is (true? v381_l882)))


(def v384_l894 (kind/doc #'vis/arrow-plot))


(def
 v385_l896
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v386_l900 (kind/doc #'vis/graph-plot))


(def
 v387_l902
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v388_l906 (kind/doc #'vis/matrix->gray-image))


(def
 v389_l908
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t390_l913
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v389_l908)))


(def v391_l915 (kind/doc #'vis/extract-channel))


(def
 v392_l917
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t393_l923
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v392_l917)))
