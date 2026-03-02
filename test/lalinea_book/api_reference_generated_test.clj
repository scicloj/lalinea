(ns
 lalinea-book.api-reference-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [scicloj.lalinea.transform :as ft]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.grad :as grad]
  [scicloj.lalinea.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l32 (kind/doc #'la/matrix))


(def v4_l34 (la/matrix [[1 2] [3 4]]))


(deftest t5_l36 (is ((fn [m] (= [2 2] (dtype/shape m))) v4_l34)))


(def v6_l38 (kind/doc #'la/eye))


(def v7_l40 (la/eye 3))


(deftest
 t8_l42
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l40)))


(def v9_l46 (kind/doc #'la/zeros))


(def v10_l48 (la/zeros 2 3))


(deftest t11_l50 (is ((fn [m] (= [2 3] (dtype/shape m))) v10_l48)))


(def v12_l52 (kind/doc #'la/diag))


(def v13_l54 (la/diag [3 5 7]))


(deftest
 t14_l56
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l54)))


(def v15_l60 (kind/doc #'la/column))


(def v16_l62 (la/column [1 2 3]))


(deftest t17_l64 (is ((fn [v] (= [3 1] (dtype/shape v))) v16_l62)))


(def v18_l66 (kind/doc #'la/row))


(def v19_l68 (la/row [1 2 3]))


(deftest t20_l70 (is ((fn [v] (= [1 3] (dtype/shape v))) v19_l68)))


(def v21_l72 (kind/doc #'la/add))


(def
 v22_l74
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l77 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l74)))


(def v24_l79 (kind/doc #'la/sub))


(def
 v25_l81
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l84 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l81)))


(def v27_l86 (kind/doc #'la/scale))


(def v28_l88 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l90 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l88)))


(def v30_l92 (kind/doc #'la/mul))


(def
 v31_l94
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l97
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l94)))


(def v33_l100 (kind/doc #'la/abs))


(def v34_l102 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l104 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l102)))


(def v36_l106 (kind/doc #'la/sq))


(def v37_l108 (la/sq (la/matrix [[2 3] [4 5]])))


(deftest t38_l110 (is ((fn [m] (== 4.0 (tensor/mget m 0 0))) v37_l108)))


(def v39_l112 (kind/doc #'la/sum))


(def v40_l114 (la/sum (la/matrix [[1 2] [3 4]])))


(deftest t41_l116 (is ((fn [v] (== 10.0 v)) v40_l114)))


(def v42_l118 (kind/doc #'la/mmul))


(def v43_l120 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t44_l123
 (is
  ((fn
    [m]
    (and (= [2 1] (dtype/shape m)) (== 17.0 (tensor/mget m 0 0))))
   v43_l120)))


(def v45_l126 (kind/doc #'la/transpose))


(def v46_l128 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest t47_l130 (is ((fn [m] (= [3 2] (dtype/shape m))) v46_l128)))


(def v48_l132 (kind/doc #'la/submatrix))


(def v49_l134 (la/submatrix (la/eye 4) :all (range 2)))


(deftest t50_l136 (is ((fn [m] (= [4 2] (dtype/shape m))) v49_l134)))


(def v51_l138 (kind/doc #'la/trace))


(def v52_l140 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t53_l142 (is ((fn [v] (== 5.0 v)) v52_l140)))


(def v54_l144 (kind/doc #'la/det))


(def v55_l146 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t56_l148 (is ((fn [v] (la/close-scalar? v -2.0)) v55_l146)))


(def v57_l150 (kind/doc #'la/norm))


(def v58_l152 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t59_l154 (is ((fn [v] (la/close-scalar? v 5.0)) v58_l152)))


(def v60_l156 (kind/doc #'la/dot))


(def v61_l158 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t62_l160 (is ((fn [v] (== 32.0 v)) v61_l158)))


(def v63_l162 (kind/doc #'la/close?))


(def v64_l164 (la/close? (la/eye 2) (la/eye 2)))


(deftest t65_l166 (is (true? v64_l164)))


(def v66_l168 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t67_l170 (is (false? v66_l168)))


(def v68_l172 (kind/doc #'la/close-scalar?))


(def v69_l174 (la/close-scalar? 1.00000000001 1.0))


(deftest t70_l176 (is (true? v69_l174)))


(def v71_l178 (kind/doc #'la/invert))


(def
 v72_l180
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t73_l183 (is (true? v72_l180)))


(def v74_l185 (kind/doc #'la/solve))


(def
 v76_l188
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t77_l192
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v76_l188)))


(def v78_l195 (kind/doc #'la/eigen))


(def
 v79_l197
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t80_l201
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v79_l197)))


(def v81_l205 (kind/doc #'la/real-eigenvalues))


(def v82_l207 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t83_l209
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v82_l207)))


(def v84_l212 (kind/doc #'la/svd))


(def
 v85_l214
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(dtype/shape U) (count S) (dtype/shape Vt)]))


(deftest
 t86_l219
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v85_l214)))


(def v87_l224 (kind/doc #'la/qr))


(def
 v88_l226
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t89_l229 (is (true? v88_l226)))


(def v90_l231 (kind/doc #'la/cholesky))


(def
 v91_l233
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t92_l237 (is (true? v91_l233)))


(def v93_l239 (kind/doc #'la/tensor->dmat))


(def
 v94_l241
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t95_l245 (is (true? v94_l241)))


(def v96_l247 (kind/doc #'la/dmat->tensor))


(def
 v97_l249
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (dtype/shape t))))


(deftest t98_l253 (is (true? v97_l249)))


(def v99_l255 (kind/doc #'la/complex-tensor->zmat))


(def
 v100_l257
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t101_l261 (is (true? v100_l257)))


(def v102_l263 (kind/doc #'la/zmat->complex-tensor))


(def
 v103_l265
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t104_l269 (is (true? v103_l265)))


(def v105_l271 (kind/doc #'la/ones))


(def v106_l273 (la/ones 2 3))


(deftest t107_l275 (is ((fn [m] (= [2 3] (dtype/shape m))) v106_l273)))


(def v108_l277 (kind/doc #'la/mpow))


(def v109_l279 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t110_l281
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v109_l279)))


(def v111_l283 (kind/doc #'la/rank))


(def v112_l285 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t113_l287 (is ((fn [r] (= 1 r)) v112_l285)))


(def v114_l289 (kind/doc #'la/condition-number))


(def v115_l291 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t116_l293 (is ((fn [v] (> v 1.0)) v115_l291)))


(def v117_l295 (kind/doc #'la/pinv))


(def
 v118_l297
 (let
  [A (la/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (la/eye 2))))


(deftest t119_l300 (is (true? v118_l297)))


(def v120_l302 (kind/doc #'la/lstsq))


(def
 v121_l304
 (let
  [{:keys [x rank]}
   (la/lstsq (la/matrix [[1 1] [1 2] [1 3]]) (la/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (la/column [0 1]))}))


(deftest
 t122_l308
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v121_l304)))


(def v123_l310 (kind/doc #'la/null-space))


(def
 v124_l312
 (let
  [ns (la/null-space (la/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (la/matrix [[1 2] [2 4]]) ns) (la/zeros 2 1))))


(deftest t125_l316 (is (true? v124_l312)))


(def v126_l318 (kind/doc #'la/col-space))


(def
 v127_l320
 (second (dtype/shape (la/col-space (la/matrix [[1 2] [2 4]])))))


(deftest t128_l322 (is ((fn [r] (= 1 r)) v127_l320)))


(def v129_l324 (kind/doc #'la/read-matrix))


(def v130_l326 (la/read-matrix [[1 2] [3 4]]))


(deftest t131_l328 (is ((fn [m] (= [2 2] (dtype/shape m))) v130_l326)))


(def v132_l330 (kind/doc #'la/read-column))


(def v133_l332 (la/read-column [5 6 7]))


(deftest t134_l334 (is ((fn [v] (= [3 1] (dtype/shape v))) v133_l332)))


(def v135_l336 (kind/doc #'la/real-tensor?))


(def v136_l338 (la/real-tensor? (la/matrix [[1 2] [3 4]])))


(deftest t137_l340 (is (true? v136_l338)))


(def v138_l342 (la/real-tensor? [1 2 3]))


(deftest t139_l344 (is (false? v138_l342)))


(def v140_l346 (kind/doc #'la/->real-tensor))


(def v141_l348 (la/->real-tensor (tensor/->tensor [[1 2] [3 4]])))


(deftest t142_l350 (is ((fn [rt] (la/real-tensor? rt)) v141_l348)))


(def v143_l352 (kind/doc #'la/->tensor))


(def v144_l354 (la/->tensor (la/matrix [[1 2] [3 4]])))


(deftest t145_l356 (is ((fn [t] (not (la/real-tensor? t))) v144_l354)))


(def v147_l363 (kind/doc #'cx/complex-tensor))


(def v148_l365 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t149_l367
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v148_l365)))


(def v150_l369 (kind/doc #'cx/complex-tensor-real))


(def v151_l371 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t152_l373 (is ((fn [ct] (every? zero? (cx/im ct))) v151_l371)))


(def v153_l375 (kind/doc #'cx/complex))


(def v154_l377 (cx/complex 3.0 4.0))


(deftest
 t155_l379
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v154_l377)))


(def v156_l383 (kind/doc #'cx/re))


(def v157_l385 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t158_l387 (is (= v157_l385 [1.0 2.0])))


(def v159_l389 (kind/doc #'cx/im))


(def v160_l391 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t161_l393 (is (= v160_l391 [3.0 4.0])))


(def v162_l395 (kind/doc #'cx/complex-shape))


(def
 v163_l397
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t164_l400 (is (= v163_l397 [2 2])))


(def v165_l402 (kind/doc #'cx/scalar?))


(def v166_l404 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t167_l406 (is (true? v166_l404)))


(def v168_l408 (kind/doc #'cx/complex?))


(def v169_l410 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t170_l412 (is (true? v169_l410)))


(def v171_l414 (cx/complex? (la/eye 2)))


(deftest t172_l416 (is (false? v171_l414)))


(def v173_l418 (kind/doc #'cx/->tensor))


(def
 v174_l420
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t175_l422 (is (= v174_l420 [2 2])))


(def v176_l424 (kind/doc #'cx/->double-array))


(def
 v177_l426
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t178_l429 (is (= v177_l426 [1.0 3.0 2.0 4.0])))


(def v179_l431 (kind/doc #'cx/add))


(def
 v180_l433
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t181_l437 (is (= v180_l433 [11.0 22.0])))


(def v182_l439 (kind/doc #'cx/sub))


(def
 v183_l441
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t184_l445 (is (= v183_l441 [9.0 18.0])))


(def v185_l447 (kind/doc #'cx/scale))


(def
 v186_l449
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t187_l452 (is (= v186_l449 [[2.0 4.0] [6.0 8.0]])))


(def v188_l454 (kind/doc #'cx/mul))


(def
 v190_l457
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t191_l462 (is (= v190_l457 [-10.0 10.0])))


(def v192_l464 (kind/doc #'cx/conj))


(def
 v193_l466
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t194_l469 (is (= v193_l466 [-3.0 4.0])))


(def v195_l471 (kind/doc #'cx/abs))


(def
 v197_l474
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t198_l477 (is (true? v197_l474)))


(def v199_l479 (kind/doc #'cx/dot))


(def
 v200_l481
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t201_l486 (is (true? v200_l481)))


(def v202_l488 (kind/doc #'cx/dot-conj))


(def
 v204_l491
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t205_l495 (is (true? v204_l491)))


(def v206_l497 (kind/doc #'cx/sum))


(def
 v207_l499
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t208_l503 (is (= v207_l499 [6.0 15.0])))


(def v210_l510 (kind/doc #'ft/forward))


(def
 v211_l512
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t212_l516 (is (= v211_l512 [4])))


(def v213_l518 (kind/doc #'ft/inverse))


(def
 v214_l520
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t215_l524 (is (true? v214_l520)))


(def v216_l526 (kind/doc #'ft/inverse-real))


(def
 v217_l528
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t218_l532 (is (true? v217_l528)))


(def v219_l534 (kind/doc #'ft/forward-complex))


(def
 v220_l536
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t221_l540 (is (= v220_l536 [4])))


(def v222_l542 (kind/doc #'ft/dct-forward))


(def v223_l544 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t224_l546 (is ((fn [v] (= 4 (count v))) v223_l544)))


(def v225_l548 (kind/doc #'ft/dct-inverse))


(def
 v226_l550
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t227_l554 (is (true? v226_l550)))


(def v228_l556 (kind/doc #'ft/dst-forward))


(def v229_l558 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t230_l560 (is ((fn [v] (= 4 (count v))) v229_l558)))


(def v231_l562 (kind/doc #'ft/dst-inverse))


(def
 v232_l564
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t233_l568 (is (true? v232_l564)))


(def v234_l570 (kind/doc #'ft/dht-forward))


(def v235_l572 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t236_l574 (is ((fn [v] (= 4 (count v))) v235_l572)))


(def v237_l576 (kind/doc #'ft/dht-inverse))


(def
 v238_l578
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t239_l582 (is (true? v238_l578)))


(def v241_l588 (kind/doc #'tape/memory-status))


(def v242_l590 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t243_l592 (is ((fn [s] (= :contiguous s)) v242_l590)))


(def
 v244_l594
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t245_l596 (is ((fn [s] (= :strided s)) v244_l594)))


(def v246_l598 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t247_l600 (is ((fn [s] (= :lazy s)) v246_l598)))


(def v248_l602 (kind/doc #'tape/memory-relation))


(def
 v249_l604
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t250_l607 (is ((fn [r] (= :shared r)) v249_l604)))


(def
 v251_l609
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t252_l611 (is ((fn [r] (= :independent r)) v251_l609)))


(def
 v253_l613
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t254_l615 (is ((fn [r] (= :unknown-lazy r)) v253_l613)))


(def v255_l617 (kind/doc #'tape/with-tape))


(def
 v256_l619
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v257_l625 (select-keys tape-example [:result :entries]))


(deftest
 t258_l627
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v257_l625)))


(def v259_l630 (kind/doc #'tape/summary))


(def v260_l632 (tape/summary tape-example))


(deftest t261_l634 (is ((fn [s] (= 4 (:total s))) v260_l632)))


(def v262_l636 (kind/doc #'tape/origin))


(def v263_l638 (tape/origin tape-example (:result tape-example)))


(deftest t264_l640 (is ((fn [dag] (= :la/mmul (:op dag))) v263_l638)))


(def v265_l642 (kind/doc #'tape/mermaid))


(def v267_l646 (tape/mermaid tape-example (:result tape-example)))


(def v268_l648 (kind/doc #'tape/detect-memory-status))


(def v270_l653 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t271_l655
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v270_l653)))


(def v273_l663 (kind/doc #'elem/sq))


(def v274_l665 (elem/sq (la/column [2 3 4])))


(deftest
 t275_l667
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v274_l665)))


(def v276_l669 (kind/doc #'elem/sqrt))


(def v277_l671 (elem/sqrt (la/column [4 9 16])))


(deftest
 t278_l673
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v277_l671)))


(def v279_l675 (kind/doc #'elem/exp))


(def
 v280_l677
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t281_l679 (is (true? v280_l677)))


(def v282_l681 (kind/doc #'elem/log))


(def
 v283_l683
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [math/E])) 0 0)
  1.0))


(deftest t284_l685 (is (true? v283_l683)))


(def v285_l687 (kind/doc #'elem/log10))


(def
 v286_l689
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t287_l691 (is (true? v286_l689)))


(def v288_l693 (kind/doc #'elem/sin))


(def
 v289_l695
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ math/PI 2)])) 0 0)
  1.0))


(deftest t290_l697 (is (true? v289_l695)))


(def v291_l699 (kind/doc #'elem/cos))


(def
 v292_l701
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t293_l703 (is (true? v292_l701)))


(def v294_l705 (kind/doc #'elem/tan))


(def
 v295_l707
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ math/PI 4)])) 0 0)
  1.0))


(deftest t296_l709 (is (true? v295_l707)))


(def v297_l711 (kind/doc #'elem/sinh))


(def
 v298_l713
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t299_l715 (is (true? v298_l713)))


(def v300_l717 (kind/doc #'elem/cosh))


(def
 v301_l719
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t302_l721 (is (true? v301_l719)))


(def v303_l723 (kind/doc #'elem/tanh))


(def
 v304_l725
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t305_l727 (is (true? v304_l725)))


(def v306_l729 (kind/doc #'elem/abs))


(def v307_l731 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t308_l733 (is ((fn [v] (== 5.0 v)) v307_l731)))


(def v309_l735 (kind/doc #'elem/sum))


(def v310_l737 (elem/sum (la/column [1 2 3 4])))


(deftest t311_l739 (is ((fn [v] (== 10.0 v)) v310_l737)))


(def v312_l741 (kind/doc #'elem/mean))


(def v313_l743 (elem/mean (la/column [2 4 6])))


(deftest t314_l745 (is ((fn [v] (== 4.0 v)) v313_l743)))


(def v315_l747 (kind/doc #'elem/pow))


(def v316_l749 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t317_l751 (is ((fn [v] (== 8.0 v)) v316_l749)))


(def v318_l753 (kind/doc #'elem/cbrt))


(def
 v319_l755
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t320_l757 (is (true? v319_l755)))


(def v321_l759 (kind/doc #'elem/floor))


(def v322_l761 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t323_l763 (is ((fn [v] (== 2.0 v)) v322_l761)))


(def v324_l765 (kind/doc #'elem/ceil))


(def v325_l767 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t326_l769 (is ((fn [v] (== 3.0 v)) v325_l767)))


(def v327_l771 (kind/doc #'elem/min))


(def
 v328_l773
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t329_l775 (is ((fn [v] (== 3.0 v)) v328_l773)))


(def v330_l777 (kind/doc #'elem/max))


(def
 v331_l779
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t332_l781 (is ((fn [v] (== 5.0 v)) v331_l779)))


(def v333_l783 (kind/doc #'elem/asin))


(def v334_l785 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t335_l787
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v334_l785)))


(def v336_l789 (kind/doc #'elem/acos))


(def v337_l791 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t338_l793
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v337_l791)))


(def v339_l795 (kind/doc #'elem/atan))


(def v340_l797 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t341_l799
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v340_l797)))


(def v342_l801 (kind/doc #'elem/log1p))


(def v343_l803 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t344_l805 (is ((fn [v] (la/close-scalar? v 0.0)) v343_l803)))


(def v345_l807 (kind/doc #'elem/expm1))


(def v346_l809 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t347_l811 (is ((fn [v] (la/close-scalar? v 0.0)) v346_l809)))


(def v348_l813 (kind/doc #'elem/round))


(def v349_l815 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t350_l817 (is ((fn [v] (== 3.0 v)) v349_l815)))


(def v351_l819 (kind/doc #'elem/clip))


(def
 v352_l821
 (vec (dtype/->reader (elem/clip (la/column [-2 0.5 3]) -1 1))))


(deftest t353_l823 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v352_l821)))


(def v355_l829 (kind/doc #'grad/grad))


(def
 v356_l831
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t357_l837 (is (true? v356_l831)))


(def v359_l843 (kind/doc #'vis/arrow-plot))


(def
 v360_l845
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v361_l849 (kind/doc #'vis/graph-plot))


(def
 v362_l851
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v363_l855 (kind/doc #'vis/matrix->gray-image))


(def
 v364_l857
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t365_l862
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v364_l857)))


(def v366_l864 (kind/doc #'vis/extract-channel))


(def
 v367_l866
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t368_l872
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v367_l866)))
