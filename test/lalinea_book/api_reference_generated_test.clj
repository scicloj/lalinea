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


(def v46_l128 (kind/doc #'la/flatten))


(def v47_l130 (la/flatten (la/column [1 2 3])))


(deftest t48_l132 (is ((fn [v] (= [1.0 2.0 3.0] v)) v47_l130)))


(def v49_l134 (kind/doc #'la/hstack))


(def v50_l136 (la/hstack [(la/column [1 2]) (la/column [3 4])]))


(deftest t51_l138 (is ((fn [m] (= [[1.0 3.0] [2.0 4.0]] m)) v50_l136)))


(def v52_l140 (kind/doc #'la/mmul))


(def v53_l142 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t54_l145
 (is
  ((fn
    [m]
    (and (= [2 1] (dtype/shape m)) (== 17.0 (tensor/mget m 0 0))))
   v53_l142)))


(def v55_l148 (kind/doc #'la/transpose))


(def v56_l150 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest t57_l152 (is ((fn [m] (= [3 2] (dtype/shape m))) v56_l150)))


(def v58_l154 (kind/doc #'la/submatrix))


(def v59_l156 (la/submatrix (la/eye 4) :all (range 2)))


(deftest t60_l158 (is ((fn [m] (= [4 2] (dtype/shape m))) v59_l156)))


(def v61_l160 (kind/doc #'la/trace))


(def v62_l162 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t63_l164 (is ((fn [v] (== 5.0 v)) v62_l162)))


(def v64_l166 (kind/doc #'la/det))


(def v65_l168 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t66_l170 (is ((fn [v] (la/close-scalar? v -2.0)) v65_l168)))


(def v67_l172 (kind/doc #'la/norm))


(def v68_l174 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t69_l176 (is ((fn [v] (la/close-scalar? v 5.0)) v68_l174)))


(def v70_l178 (kind/doc #'la/dot))


(def v71_l180 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t72_l182 (is ((fn [v] (== 32.0 v)) v71_l180)))


(def v73_l184 (kind/doc #'la/close?))


(def v74_l186 (la/close? (la/eye 2) (la/eye 2)))


(deftest t75_l188 (is (true? v74_l186)))


(def v76_l190 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t77_l192 (is (false? v76_l190)))


(def v78_l194 (kind/doc #'la/close-scalar?))


(def v79_l196 (la/close-scalar? 1.00000000001 1.0))


(deftest t80_l198 (is (true? v79_l196)))


(def v81_l200 (kind/doc #'la/invert))


(def
 v82_l202
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t83_l205 (is (true? v82_l202)))


(def v84_l207 (kind/doc #'la/solve))


(def
 v86_l210
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t87_l214
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v86_l210)))


(def v88_l217 (kind/doc #'la/eigen))


(def
 v89_l219
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t90_l223
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v89_l219)))


(def v91_l227 (kind/doc #'la/real-eigenvalues))


(def v92_l229 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t93_l231
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v92_l229)))


(def v94_l234 (kind/doc #'la/svd))


(def
 v95_l236
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(dtype/shape U) (count S) (dtype/shape Vt)]))


(deftest
 t96_l241
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v95_l236)))


(def v97_l246 (kind/doc #'la/qr))


(def
 v98_l248
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t99_l251 (is (true? v98_l248)))


(def v100_l253 (kind/doc #'la/cholesky))


(def
 v101_l255
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t102_l259 (is (true? v101_l255)))


(def v103_l261 (kind/doc #'la/tensor->dmat))


(def
 v104_l263
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t105_l267 (is (true? v104_l263)))


(def v106_l269 (kind/doc #'la/dmat->tensor))


(def
 v107_l271
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (dtype/shape t))))


(deftest t108_l275 (is (true? v107_l271)))


(def v109_l277 (kind/doc #'la/complex-tensor->zmat))


(def
 v110_l279
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t111_l283 (is (true? v110_l279)))


(def v112_l285 (kind/doc #'la/zmat->complex-tensor))


(def
 v113_l287
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t114_l291 (is (true? v113_l287)))


(def v115_l293 (kind/doc #'la/ones))


(def v116_l295 (la/ones 2 3))


(deftest t117_l297 (is ((fn [m] (= [2 3] (dtype/shape m))) v116_l295)))


(def v118_l299 (kind/doc #'la/mpow))


(def v119_l301 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t120_l303
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v119_l301)))


(def v121_l305 (kind/doc #'la/rank))


(def v122_l307 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t123_l309 (is ((fn [r] (= 1 r)) v122_l307)))


(def v124_l311 (kind/doc #'la/condition-number))


(def v125_l313 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t126_l315 (is ((fn [v] (> v 1.0)) v125_l313)))


(def v127_l317 (kind/doc #'la/pinv))


(def
 v128_l319
 (let
  [A (la/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (la/eye 2))))


(deftest t129_l322 (is (true? v128_l319)))


(def v130_l324 (kind/doc #'la/lstsq))


(def
 v131_l326
 (let
  [{:keys [x rank]}
   (la/lstsq (la/matrix [[1 1] [1 2] [1 3]]) (la/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (la/column [0 1]))}))


(deftest
 t132_l330
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v131_l326)))


(def v133_l332 (kind/doc #'la/null-space))


(def
 v134_l334
 (let
  [ns (la/null-space (la/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (la/matrix [[1 2] [2 4]]) ns) (la/zeros 2 1))))


(deftest t135_l338 (is (true? v134_l334)))


(def v136_l340 (kind/doc #'la/col-space))


(def
 v137_l342
 (second (dtype/shape (la/col-space (la/matrix [[1 2] [2 4]])))))


(deftest t138_l344 (is ((fn [r] (= 1 r)) v137_l342)))


(def v139_l346 (kind/doc #'la/read-matrix))


(def v140_l348 (la/read-matrix [[1 2] [3 4]]))


(deftest t141_l350 (is ((fn [m] (= [2 2] (dtype/shape m))) v140_l348)))


(def v142_l352 (kind/doc #'la/read-column))


(def v143_l354 (la/read-column [5 6 7]))


(deftest t144_l356 (is ((fn [v] (= [3 1] (dtype/shape v))) v143_l354)))


(def v145_l358 (kind/doc #'la/real-tensor?))


(def v146_l360 (la/real-tensor? (la/matrix [[1 2] [3 4]])))


(deftest t147_l362 (is (true? v146_l360)))


(def v148_l364 (la/real-tensor? [1 2 3]))


(deftest t149_l366 (is (false? v148_l364)))


(def v150_l368 (kind/doc #'la/->real-tensor))


(def v151_l370 (la/->real-tensor (tensor/->tensor [[1 2] [3 4]])))


(deftest t152_l372 (is ((fn [rt] (la/real-tensor? rt)) v151_l370)))


(def v153_l374 (kind/doc #'la/->tensor))


(def v154_l376 (la/->tensor (la/matrix [[1 2] [3 4]])))


(deftest t155_l378 (is ((fn [t] (not (la/real-tensor? t))) v154_l376)))


(def v156_l380 (kind/doc #'la/lift))


(def v158_l383 (la/lift dfn/sqrt (la/matrix [[4 9] [16 25]])))


(deftest
 t159_l385
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (tensor/mget m 0 0) 2.0)
     (la/close-scalar? (tensor/mget m 0 1) 3.0)))
   v158_l383)))


(def v160_l388 (kind/doc #'la/lifted))


(def
 v162_l391
 (let [my-sqrt (la/lifted dfn/sqrt)] (my-sqrt (la/column [4 9 16]))))


(deftest
 t163_l394
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v162_l391)))


(def v165_l401 (kind/doc #'cx/complex-tensor))


(def v166_l403 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t167_l405
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v166_l403)))


(def v168_l407 (kind/doc #'cx/complex-tensor-real))


(def v169_l409 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t170_l411 (is ((fn [ct] (every? zero? (cx/im ct))) v169_l409)))


(def v171_l413 (kind/doc #'cx/complex))


(def v172_l415 (cx/complex 3.0 4.0))


(deftest
 t173_l417
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v172_l415)))


(def v174_l421 (kind/doc #'cx/re))


(def v175_l423 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t176_l425 (is (= v175_l423 [1.0 2.0])))


(def v177_l427 (kind/doc #'cx/im))


(def v178_l429 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t179_l431 (is (= v178_l429 [3.0 4.0])))


(def v180_l433 (kind/doc #'cx/complex-shape))


(def
 v181_l435
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t182_l438 (is (= v181_l435 [2 2])))


(def v183_l440 (kind/doc #'cx/scalar?))


(def v184_l442 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t185_l444 (is (true? v184_l442)))


(def v186_l446 (kind/doc #'cx/complex?))


(def v187_l448 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t188_l450 (is (true? v187_l448)))


(def v189_l452 (cx/complex? (la/eye 2)))


(deftest t190_l454 (is (false? v189_l452)))


(def v191_l456 (kind/doc #'cx/->tensor))


(def
 v192_l458
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t193_l460 (is (= v192_l458 [2 2])))


(def v194_l462 (kind/doc #'cx/->double-array))


(def
 v195_l464
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t196_l467 (is (= v195_l464 [1.0 3.0 2.0 4.0])))


(def v197_l469 (kind/doc #'cx/wrap-tensor))


(def
 v198_l471
 (let
  [raw (tensor/->tensor [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t199_l475
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v198_l471)))


(def v200_l477 (kind/doc #'cx/add))


(def
 v201_l479
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t202_l483 (is (= v201_l479 [11.0 22.0])))


(def v203_l485 (kind/doc #'cx/sub))


(def
 v204_l487
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t205_l491 (is (= v204_l487 [9.0 18.0])))


(def v206_l493 (kind/doc #'cx/scale))


(def
 v207_l495
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t208_l498 (is (= v207_l495 [[2.0 4.0] [6.0 8.0]])))


(def v209_l500 (kind/doc #'cx/mul))


(def
 v211_l503
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t212_l508 (is (= v211_l503 [-10.0 10.0])))


(def v213_l510 (kind/doc #'cx/conj))


(def
 v214_l512
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t215_l515 (is (= v214_l512 [-3.0 4.0])))


(def v216_l517 (kind/doc #'cx/abs))


(def
 v218_l520
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t219_l523 (is (true? v218_l520)))


(def v220_l525 (kind/doc #'cx/dot))


(def
 v221_l527
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t222_l532 (is (true? v221_l527)))


(def v223_l534 (kind/doc #'cx/dot-conj))


(def
 v225_l537
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t226_l541 (is (true? v225_l537)))


(def v227_l543 (kind/doc #'cx/sum))


(def
 v228_l545
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t229_l549 (is (= v228_l545 [6.0 15.0])))


(def v231_l556 (kind/doc #'ft/forward))


(def
 v232_l558
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t233_l562 (is (= v232_l558 [4])))


(def v234_l564 (kind/doc #'ft/inverse))


(def
 v235_l566
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t236_l570 (is (true? v235_l566)))


(def v237_l572 (kind/doc #'ft/inverse-real))


(def
 v238_l574
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t239_l578 (is (true? v238_l574)))


(def v240_l580 (kind/doc #'ft/forward-complex))


(def
 v241_l582
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t242_l586 (is (= v241_l582 [4])))


(def v243_l588 (kind/doc #'ft/dct-forward))


(def v244_l590 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t245_l592 (is ((fn [v] (= 4 (count v))) v244_l590)))


(def v246_l594 (kind/doc #'ft/dct-inverse))


(def
 v247_l596
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t248_l600 (is (true? v247_l596)))


(def v249_l602 (kind/doc #'ft/dst-forward))


(def v250_l604 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t251_l606 (is ((fn [v] (= 4 (count v))) v250_l604)))


(def v252_l608 (kind/doc #'ft/dst-inverse))


(def
 v253_l610
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t254_l614 (is (true? v253_l610)))


(def v255_l616 (kind/doc #'ft/dht-forward))


(def v256_l618 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t257_l620 (is ((fn [v] (= 4 (count v))) v256_l618)))


(def v258_l622 (kind/doc #'ft/dht-inverse))


(def
 v259_l624
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t260_l628 (is (true? v259_l624)))


(def v262_l634 (kind/doc #'tape/memory-status))


(def v263_l636 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t264_l638 (is ((fn [s] (= :contiguous s)) v263_l636)))


(def
 v265_l640
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t266_l642 (is ((fn [s] (= :strided s)) v265_l640)))


(def v267_l644 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t268_l646 (is ((fn [s] (= :lazy s)) v267_l644)))


(def v269_l648 (kind/doc #'tape/memory-relation))


(def
 v270_l650
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t271_l653 (is ((fn [r] (= :shared r)) v270_l650)))


(def
 v272_l655
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t273_l657 (is ((fn [r] (= :independent r)) v272_l655)))


(def
 v274_l659
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t275_l661 (is ((fn [r] (= :unknown-lazy r)) v274_l659)))


(def v276_l663 (kind/doc #'tape/with-tape))


(def
 v277_l665
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v278_l671 (select-keys tape-example [:result :entries]))


(deftest
 t279_l673
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v278_l671)))


(def v280_l676 (kind/doc #'tape/summary))


(def v281_l678 (tape/summary tape-example))


(deftest t282_l680 (is ((fn [s] (= 4 (:total s))) v281_l678)))


(def v283_l682 (kind/doc #'tape/origin))


(def v284_l684 (tape/origin tape-example (:result tape-example)))


(deftest t285_l686 (is ((fn [dag] (= :la/mmul (:op dag))) v284_l684)))


(def v286_l688 (kind/doc #'tape/mermaid))


(def v288_l692 (tape/mermaid tape-example (:result tape-example)))


(def v289_l694 (kind/doc #'tape/detect-memory-status))


(def v291_l699 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t292_l701
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v291_l699)))


(def v294_l709 (kind/doc #'elem/sq))


(def v295_l711 (elem/sq (la/column [2 3 4])))


(deftest
 t296_l713
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v295_l711)))


(def v297_l715 (kind/doc #'elem/sqrt))


(def v298_l717 (elem/sqrt (la/column [4 9 16])))


(deftest
 t299_l719
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v298_l717)))


(def v300_l721 (kind/doc #'elem/exp))


(def
 v301_l723
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t302_l725 (is (true? v301_l723)))


(def v303_l727 (kind/doc #'elem/log))


(def
 v304_l729
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [math/E])) 0 0)
  1.0))


(deftest t305_l731 (is (true? v304_l729)))


(def v306_l733 (kind/doc #'elem/log10))


(def
 v307_l735
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t308_l737 (is (true? v307_l735)))


(def v309_l739 (kind/doc #'elem/sin))


(def
 v310_l741
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ math/PI 2)])) 0 0)
  1.0))


(deftest t311_l743 (is (true? v310_l741)))


(def v312_l745 (kind/doc #'elem/cos))


(def
 v313_l747
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t314_l749 (is (true? v313_l747)))


(def v315_l751 (kind/doc #'elem/tan))


(def
 v316_l753
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ math/PI 4)])) 0 0)
  1.0))


(deftest t317_l755 (is (true? v316_l753)))


(def v318_l757 (kind/doc #'elem/sinh))


(def
 v319_l759
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t320_l761 (is (true? v319_l759)))


(def v321_l763 (kind/doc #'elem/cosh))


(def
 v322_l765
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t323_l767 (is (true? v322_l765)))


(def v324_l769 (kind/doc #'elem/tanh))


(def
 v325_l771
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t326_l773 (is (true? v325_l771)))


(def v327_l775 (kind/doc #'elem/abs))


(def v328_l777 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t329_l779 (is ((fn [v] (== 5.0 v)) v328_l777)))


(def v330_l781 (kind/doc #'elem/sum))


(def v331_l783 (elem/sum (la/column [1 2 3 4])))


(deftest t332_l785 (is ((fn [v] (== 10.0 v)) v331_l783)))


(def v333_l787 (kind/doc #'elem/mean))


(def v334_l789 (elem/mean (la/column [2 4 6])))


(deftest t335_l791 (is ((fn [v] (== 4.0 v)) v334_l789)))


(def v336_l793 (kind/doc #'elem/pow))


(def v337_l795 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t338_l797 (is ((fn [v] (== 8.0 v)) v337_l795)))


(def v339_l799 (kind/doc #'elem/cbrt))


(def
 v340_l801
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t341_l803 (is (true? v340_l801)))


(def v342_l805 (kind/doc #'elem/floor))


(def v343_l807 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t344_l809 (is ((fn [v] (== 2.0 v)) v343_l807)))


(def v345_l811 (kind/doc #'elem/ceil))


(def v346_l813 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t347_l815 (is ((fn [v] (== 3.0 v)) v346_l813)))


(def v348_l817 (kind/doc #'elem/min))


(def
 v349_l819
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t350_l821 (is ((fn [v] (== 3.0 v)) v349_l819)))


(def v351_l823 (kind/doc #'elem/max))


(def
 v352_l825
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t353_l827 (is ((fn [v] (== 5.0 v)) v352_l825)))


(def v354_l829 (kind/doc #'elem/asin))


(def v355_l831 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t356_l833
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v355_l831)))


(def v357_l835 (kind/doc #'elem/acos))


(def v358_l837 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t359_l839
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v358_l837)))


(def v360_l841 (kind/doc #'elem/atan))


(def v361_l843 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t362_l845
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v361_l843)))


(def v363_l847 (kind/doc #'elem/log1p))


(def v364_l849 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t365_l851 (is ((fn [v] (la/close-scalar? v 0.0)) v364_l849)))


(def v366_l853 (kind/doc #'elem/expm1))


(def v367_l855 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t368_l857 (is ((fn [v] (la/close-scalar? v 0.0)) v367_l855)))


(def v369_l859 (kind/doc #'elem/round))


(def v370_l861 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t371_l863 (is ((fn [v] (== 3.0 v)) v370_l861)))


(def v372_l865 (kind/doc #'elem/clip))


(def v373_l867 (la/flatten (elem/clip (la/column [-2 0.5 3]) -1 1)))


(deftest t374_l869 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v373_l867)))


(def v376_l875 (kind/doc #'grad/grad))


(def
 v377_l877
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t378_l883 (is (true? v377_l877)))


(def v380_l889 (kind/doc #'vis/arrow-plot))


(def
 v381_l891
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v382_l895 (kind/doc #'vis/graph-plot))


(def
 v383_l897
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v384_l901 (kind/doc #'vis/matrix->gray-image))


(def
 v385_l903
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t386_l908
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v385_l903)))


(def v387_l910 (kind/doc #'vis/extract-channel))


(def
 v388_l912
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t389_l918
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v388_l912)))
