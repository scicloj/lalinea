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
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l33 (kind/doc #'la/matrix))


(def v4_l35 (la/matrix [[1 2] [3 4]]))


(deftest t5_l37 (is ((fn [m] (= [2 2] (dtype/shape m))) v4_l35)))


(def v6_l39 (kind/doc #'la/eye))


(def v7_l41 (la/eye 3))


(deftest
 t8_l43
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l41)))


(def v9_l47 (kind/doc #'la/zeros))


(def v10_l49 (la/zeros 2 3))


(deftest t11_l51 (is ((fn [m] (= [2 3] (dtype/shape m))) v10_l49)))


(def v12_l53 (kind/doc #'la/diag))


(def v13_l55 (la/diag [3 5 7]))


(deftest
 t14_l57
 (is
  ((fn
    [m]
    (and
     (= [3 3] (dtype/shape m))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l55)))


(def v15_l61 (kind/doc #'la/column))


(def v16_l63 (la/column [1 2 3]))


(deftest t17_l65 (is ((fn [v] (= [3 1] (dtype/shape v))) v16_l63)))


(def v18_l67 (kind/doc #'la/row))


(def v19_l69 (la/row [1 2 3]))


(deftest t20_l71 (is ((fn [v] (= [1 3] (dtype/shape v))) v19_l69)))


(def v21_l73 (kind/doc #'la/add))


(def
 v22_l75
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l78 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l75)))


(def v24_l80 (kind/doc #'la/sub))


(def
 v25_l82
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l85 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l82)))


(def v27_l87 (kind/doc #'la/scale))


(def v28_l89 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t29_l91 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l89)))


(def v30_l93 (kind/doc #'la/mul))


(def
 v31_l95
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l98
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l95)))


(def v33_l101 (kind/doc #'la/abs))


(def v34_l103 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l105 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l103)))


(def v36_l107 (kind/doc #'la/sq))


(def v37_l109 (la/sq (la/matrix [[2 3] [4 5]])))


(deftest t38_l111 (is ((fn [m] (== 4.0 (tensor/mget m 0 0))) v37_l109)))


(def v39_l113 (kind/doc #'la/sum))


(def v40_l115 (la/sum (la/matrix [[1 2] [3 4]])))


(deftest t41_l117 (is ((fn [v] (== 10.0 v)) v40_l115)))


(def v42_l119 (kind/doc #'la/reduce-axis))


(def v44_l122 (la/reduce-axis (la/matrix [[1 2 3] [4 5 6]]) dfn/sum 1))


(deftest
 t45_l124
 (is
  ((fn
    [v]
    (and
     (= [2] (dtype/shape v))
     (la/close-scalar? (v 0) 6.0)
     (la/close-scalar? (v 1) 15.0)))
   v44_l122)))


(def v46_l128 (kind/doc #'la/mmul))


(def v47_l130 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t48_l133
 (is
  ((fn
    [m]
    (and (= [2 1] (dtype/shape m)) (== 17.0 (tensor/mget m 0 0))))
   v47_l130)))


(def v49_l136 (kind/doc #'la/transpose))


(def v50_l138 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest t51_l140 (is ((fn [m] (= [3 2] (dtype/shape m))) v50_l138)))


(def v52_l142 (kind/doc #'la/submatrix))


(def v53_l144 (la/submatrix (la/eye 4) :all (range 2)))


(deftest t54_l146 (is ((fn [m] (= [4 2] (dtype/shape m))) v53_l144)))


(def v55_l148 (kind/doc #'la/trace))


(def v56_l150 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t57_l152 (is ((fn [v] (== 5.0 v)) v56_l150)))


(def v58_l154 (kind/doc #'la/det))


(def v59_l156 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t60_l158 (is ((fn [v] (la/close-scalar? v -2.0)) v59_l156)))


(def v61_l160 (kind/doc #'la/norm))


(def v62_l162 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t63_l164 (is ((fn [v] (la/close-scalar? v 5.0)) v62_l162)))


(def v64_l166 (kind/doc #'la/dot))


(def v65_l168 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t66_l170 (is ((fn [v] (== 32.0 v)) v65_l168)))


(def v67_l172 (kind/doc #'la/close?))


(def v68_l174 (la/close? (la/eye 2) (la/eye 2)))


(deftest t69_l176 (is (true? v68_l174)))


(def v70_l178 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t71_l180 (is (false? v70_l178)))


(def v72_l182 (kind/doc #'la/close-scalar?))


(def v73_l184 (la/close-scalar? 1.00000000001 1.0))


(deftest t74_l186 (is (true? v73_l184)))


(def v75_l188 (kind/doc #'la/invert))


(def
 v76_l190
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t77_l193 (is (true? v76_l190)))


(def v78_l195 (kind/doc #'la/solve))


(def
 v80_l198
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t81_l202
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v80_l198)))


(def v82_l205 (kind/doc #'la/eigen))


(def
 v83_l207
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t84_l211
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v83_l207)))


(def v85_l215 (kind/doc #'la/real-eigenvalues))


(def v86_l217 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t87_l219
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v86_l217)))


(def v88_l222 (kind/doc #'la/svd))


(def
 v89_l224
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(dtype/shape U) (count S) (dtype/shape Vt)]))


(deftest
 t90_l229
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v89_l224)))


(def v91_l234 (kind/doc #'la/qr))


(def
 v92_l236
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t93_l239 (is (true? v92_l236)))


(def v94_l241 (kind/doc #'la/cholesky))


(def
 v95_l243
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t96_l247 (is (true? v95_l243)))


(def v97_l249 (kind/doc #'la/tensor->dmat))


(def
 v98_l251
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t99_l255 (is (true? v98_l251)))


(def v100_l257 (kind/doc #'la/dmat->tensor))


(def
 v101_l259
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (dtype/shape t))))


(deftest t102_l263 (is (true? v101_l259)))


(def v103_l265 (kind/doc #'la/complex-tensor->zmat))


(def
 v104_l267
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t105_l271 (is (true? v104_l267)))


(def v106_l273 (kind/doc #'la/zmat->complex-tensor))


(def
 v107_l275
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t108_l279 (is (true? v107_l275)))


(def v109_l281 (kind/doc #'la/ones))


(def v110_l283 (la/ones 2 3))


(deftest t111_l285 (is ((fn [m] (= [2 3] (dtype/shape m))) v110_l283)))


(def v112_l287 (kind/doc #'la/mpow))


(def v113_l289 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t114_l291
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v113_l289)))


(def v115_l293 (kind/doc #'la/rank))


(def v116_l295 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t117_l297 (is ((fn [r] (= 1 r)) v116_l295)))


(def v118_l299 (kind/doc #'la/condition-number))


(def v119_l301 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t120_l303 (is ((fn [v] (> v 1.0)) v119_l301)))


(def v121_l305 (kind/doc #'la/pinv))


(def
 v122_l307
 (let
  [A (la/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (la/eye 2))))


(deftest t123_l310 (is (true? v122_l307)))


(def v124_l312 (kind/doc #'la/lstsq))


(def
 v125_l314
 (let
  [{:keys [x rank]}
   (la/lstsq (la/matrix [[1 1] [1 2] [1 3]]) (la/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (la/column [0 1]))}))


(deftest
 t126_l318
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v125_l314)))


(def v127_l320 (kind/doc #'la/null-space))


(def
 v128_l322
 (let
  [ns (la/null-space (la/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (la/matrix [[1 2] [2 4]]) ns) (la/zeros 2 1))))


(deftest t129_l326 (is (true? v128_l322)))


(def v130_l328 (kind/doc #'la/col-space))


(def
 v131_l330
 (second (dtype/shape (la/col-space (la/matrix [[1 2] [2 4]])))))


(deftest t132_l332 (is ((fn [r] (= 1 r)) v131_l330)))


(def v133_l334 (kind/doc #'la/read-matrix))


(def v134_l336 (la/read-matrix [[1 2] [3 4]]))


(deftest t135_l338 (is ((fn [m] (= [2 2] (dtype/shape m))) v134_l336)))


(def v136_l340 (kind/doc #'la/read-column))


(def v137_l342 (la/read-column [5 6 7]))


(deftest t138_l344 (is ((fn [v] (= [3 1] (dtype/shape v))) v137_l342)))


(def v139_l346 (kind/doc #'la/real-tensor?))


(def v140_l348 (la/real-tensor? (la/matrix [[1 2] [3 4]])))


(deftest t141_l350 (is (true? v140_l348)))


(def v142_l352 (la/real-tensor? [1 2 3]))


(deftest t143_l354 (is (false? v142_l352)))


(def v144_l356 (kind/doc #'la/->real-tensor))


(def v145_l358 (la/->real-tensor (tensor/->tensor [[1 2] [3 4]])))


(deftest t146_l360 (is ((fn [rt] (la/real-tensor? rt)) v145_l358)))


(def v147_l362 (kind/doc #'la/->tensor))


(def v148_l364 (la/->tensor (la/matrix [[1 2] [3 4]])))


(deftest t149_l366 (is ((fn [t] (not (la/real-tensor? t))) v148_l364)))


(def v150_l368 (kind/doc #'la/lift))


(def v152_l371 (la/lift dfn/sqrt (la/matrix [[4 9] [16 25]])))


(deftest
 t153_l373
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (tensor/mget m 0 0) 2.0)
     (la/close-scalar? (tensor/mget m 0 1) 3.0)))
   v152_l371)))


(def v154_l376 (kind/doc #'la/lifted))


(def
 v156_l379
 (let [my-sqrt (la/lifted dfn/sqrt)] (my-sqrt (la/column [4 9 16]))))


(deftest
 t157_l382
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v156_l379)))


(def v159_l389 (kind/doc #'cx/complex-tensor))


(def v160_l391 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t161_l393
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v160_l391)))


(def v162_l395 (kind/doc #'cx/complex-tensor-real))


(def v163_l397 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t164_l399 (is ((fn [ct] (every? zero? (cx/im ct))) v163_l397)))


(def v165_l401 (kind/doc #'cx/complex))


(def v166_l403 (cx/complex 3.0 4.0))


(deftest
 t167_l405
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v166_l403)))


(def v168_l409 (kind/doc #'cx/re))


(def v169_l411 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t170_l413 (is (= v169_l411 [1.0 2.0])))


(def v171_l415 (kind/doc #'cx/im))


(def v172_l417 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t173_l419 (is (= v172_l417 [3.0 4.0])))


(def v174_l421 (kind/doc #'cx/complex-shape))


(def
 v175_l423
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t176_l426 (is (= v175_l423 [2 2])))


(def v177_l428 (kind/doc #'cx/scalar?))


(def v178_l430 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t179_l432 (is (true? v178_l430)))


(def v180_l434 (kind/doc #'cx/complex?))


(def v181_l436 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t182_l438 (is (true? v181_l436)))


(def v183_l440 (cx/complex? (la/eye 2)))


(deftest t184_l442 (is (false? v183_l440)))


(def v185_l444 (kind/doc #'cx/->tensor))


(def
 v186_l446
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t187_l448 (is (= v186_l446 [2 2])))


(def v188_l450 (kind/doc #'cx/->double-array))


(def
 v189_l452
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t190_l455 (is (= v189_l452 [1.0 3.0 2.0 4.0])))


(def v191_l457 (kind/doc #'cx/wrap-tensor))


(def
 v192_l459
 (let
  [raw (tensor/->tensor [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t193_l463
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v192_l459)))


(def v194_l465 (kind/doc #'cx/add))


(def
 v195_l467
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t196_l471 (is (= v195_l467 [11.0 22.0])))


(def v197_l473 (kind/doc #'cx/sub))


(def
 v198_l475
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t199_l479 (is (= v198_l475 [9.0 18.0])))


(def v200_l481 (kind/doc #'cx/scale))


(def
 v201_l483
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t202_l486 (is (= v201_l483 [[2.0 4.0] [6.0 8.0]])))


(def v203_l488 (kind/doc #'cx/mul))


(def
 v205_l491
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t206_l496 (is (= v205_l491 [-10.0 10.0])))


(def v207_l498 (kind/doc #'cx/conj))


(def
 v208_l500
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t209_l503 (is (= v208_l500 [-3.0 4.0])))


(def v210_l505 (kind/doc #'cx/abs))


(def
 v212_l508
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t213_l511 (is (true? v212_l508)))


(def v214_l513 (kind/doc #'cx/dot))


(def
 v215_l515
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t216_l520 (is (true? v215_l515)))


(def v217_l522 (kind/doc #'cx/dot-conj))


(def
 v219_l525
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t220_l529 (is (true? v219_l525)))


(def v221_l531 (kind/doc #'cx/sum))


(def
 v222_l533
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t223_l537 (is (= v222_l533 [6.0 15.0])))


(def v225_l544 (kind/doc #'ft/forward))


(def
 v226_l546
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t227_l550 (is (= v226_l546 [4])))


(def v228_l552 (kind/doc #'ft/inverse))


(def
 v229_l554
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t230_l558 (is (true? v229_l554)))


(def v231_l560 (kind/doc #'ft/inverse-real))


(def
 v232_l562
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t233_l566 (is (true? v232_l562)))


(def v234_l568 (kind/doc #'ft/forward-complex))


(def
 v235_l570
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t236_l574 (is (= v235_l570 [4])))


(def v237_l576 (kind/doc #'ft/dct-forward))


(def v238_l578 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t239_l580 (is ((fn [v] (= 4 (count v))) v238_l578)))


(def v240_l582 (kind/doc #'ft/dct-inverse))


(def
 v241_l584
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t242_l588 (is (true? v241_l584)))


(def v243_l590 (kind/doc #'ft/dst-forward))


(def v244_l592 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t245_l594 (is ((fn [v] (= 4 (count v))) v244_l592)))


(def v246_l596 (kind/doc #'ft/dst-inverse))


(def
 v247_l598
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t248_l602 (is (true? v247_l598)))


(def v249_l604 (kind/doc #'ft/dht-forward))


(def v250_l606 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t251_l608 (is ((fn [v] (= 4 (count v))) v250_l606)))


(def v252_l610 (kind/doc #'ft/dht-inverse))


(def
 v253_l612
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t254_l616 (is (true? v253_l612)))


(def v256_l622 (kind/doc #'tape/memory-status))


(def v257_l624 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t258_l626 (is ((fn [s] (= :contiguous s)) v257_l624)))


(def
 v259_l628
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t260_l630 (is ((fn [s] (= :strided s)) v259_l628)))


(def v261_l632 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t262_l634 (is ((fn [s] (= :lazy s)) v261_l632)))


(def v263_l636 (kind/doc #'tape/memory-relation))


(def
 v264_l638
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t265_l641 (is ((fn [r] (= :shared r)) v264_l638)))


(def
 v266_l643
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t267_l645 (is ((fn [r] (= :independent r)) v266_l643)))


(def
 v268_l647
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t269_l649 (is ((fn [r] (= :unknown-lazy r)) v268_l647)))


(def v270_l651 (kind/doc #'tape/with-tape))


(def
 v271_l653
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v272_l659 (select-keys tape-example [:result :entries]))


(deftest
 t273_l661
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v272_l659)))


(def v274_l664 (kind/doc #'tape/summary))


(def v275_l666 (tape/summary tape-example))


(deftest t276_l668 (is ((fn [s] (= 4 (:total s))) v275_l666)))


(def v277_l670 (kind/doc #'tape/origin))


(def v278_l672 (tape/origin tape-example (:result tape-example)))


(deftest t279_l674 (is ((fn [dag] (= :la/mmul (:op dag))) v278_l672)))


(def v280_l676 (kind/doc #'tape/mermaid))


(def v282_l680 (tape/mermaid tape-example (:result tape-example)))


(def v283_l682 (kind/doc #'tape/detect-memory-status))


(def v285_l687 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t286_l689
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v285_l687)))


(def v288_l697 (kind/doc #'elem/sq))


(def v289_l699 (elem/sq (la/column [2 3 4])))


(deftest
 t290_l701
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v289_l699)))


(def v291_l703 (kind/doc #'elem/sqrt))


(def v292_l705 (elem/sqrt (la/column [4 9 16])))


(deftest
 t293_l707
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v292_l705)))


(def v294_l709 (kind/doc #'elem/exp))


(def
 v295_l711
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t296_l713 (is (true? v295_l711)))


(def v297_l715 (kind/doc #'elem/log))


(def
 v298_l717
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [math/E])) 0 0)
  1.0))


(deftest t299_l719 (is (true? v298_l717)))


(def v300_l721 (kind/doc #'elem/log10))


(def
 v301_l723
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t302_l725 (is (true? v301_l723)))


(def v303_l727 (kind/doc #'elem/sin))


(def
 v304_l729
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ math/PI 2)])) 0 0)
  1.0))


(deftest t305_l731 (is (true? v304_l729)))


(def v306_l733 (kind/doc #'elem/cos))


(def
 v307_l735
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t308_l737 (is (true? v307_l735)))


(def v309_l739 (kind/doc #'elem/tan))


(def
 v310_l741
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ math/PI 4)])) 0 0)
  1.0))


(deftest t311_l743 (is (true? v310_l741)))


(def v312_l745 (kind/doc #'elem/sinh))


(def
 v313_l747
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t314_l749 (is (true? v313_l747)))


(def v315_l751 (kind/doc #'elem/cosh))


(def
 v316_l753
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t317_l755 (is (true? v316_l753)))


(def v318_l757 (kind/doc #'elem/tanh))


(def
 v319_l759
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t320_l761 (is (true? v319_l759)))


(def v321_l763 (kind/doc #'elem/abs))


(def v322_l765 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t323_l767 (is ((fn [v] (== 5.0 v)) v322_l765)))


(def v324_l769 (kind/doc #'elem/sum))


(def v325_l771 (elem/sum (la/column [1 2 3 4])))


(deftest t326_l773 (is ((fn [v] (== 10.0 v)) v325_l771)))


(def v327_l775 (kind/doc #'elem/mean))


(def v328_l777 (elem/mean (la/column [2 4 6])))


(deftest t329_l779 (is ((fn [v] (== 4.0 v)) v328_l777)))


(def v330_l781 (kind/doc #'elem/pow))


(def v331_l783 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t332_l785 (is ((fn [v] (== 8.0 v)) v331_l783)))


(def v333_l787 (kind/doc #'elem/cbrt))


(def
 v334_l789
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t335_l791 (is (true? v334_l789)))


(def v336_l793 (kind/doc #'elem/floor))


(def v337_l795 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t338_l797 (is ((fn [v] (== 2.0 v)) v337_l795)))


(def v339_l799 (kind/doc #'elem/ceil))


(def v340_l801 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t341_l803 (is ((fn [v] (== 3.0 v)) v340_l801)))


(def v342_l805 (kind/doc #'elem/min))


(def
 v343_l807
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t344_l809 (is ((fn [v] (== 3.0 v)) v343_l807)))


(def v345_l811 (kind/doc #'elem/max))


(def
 v346_l813
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t347_l815 (is ((fn [v] (== 5.0 v)) v346_l813)))


(def v348_l817 (kind/doc #'elem/asin))


(def v349_l819 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t350_l821
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v349_l819)))


(def v351_l823 (kind/doc #'elem/acos))


(def v352_l825 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t353_l827
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v352_l825)))


(def v354_l829 (kind/doc #'elem/atan))


(def v355_l831 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t356_l833
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v355_l831)))


(def v357_l835 (kind/doc #'elem/log1p))


(def v358_l837 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t359_l839 (is ((fn [v] (la/close-scalar? v 0.0)) v358_l837)))


(def v360_l841 (kind/doc #'elem/expm1))


(def v361_l843 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t362_l845 (is ((fn [v] (la/close-scalar? v 0.0)) v361_l843)))


(def v363_l847 (kind/doc #'elem/round))


(def v364_l849 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t365_l851 (is ((fn [v] (== 3.0 v)) v364_l849)))


(def v366_l853 (kind/doc #'elem/clip))


(def
 v367_l855
 (vec (dtype/->reader (elem/clip (la/column [-2 0.5 3]) -1 1))))


(deftest t368_l857 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v367_l855)))


(def v370_l863 (kind/doc #'grad/grad))


(def
 v371_l865
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t372_l871 (is (true? v371_l865)))


(def v374_l877 (kind/doc #'vis/arrow-plot))


(def
 v375_l879
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v376_l883 (kind/doc #'vis/graph-plot))


(def
 v377_l885
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v378_l889 (kind/doc #'vis/matrix->gray-image))


(def
 v379_l891
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t380_l896
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v379_l891)))


(def v381_l898 (kind/doc #'vis/extract-channel))


(def
 v382_l900
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t383_l906
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v382_l900)))
