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


(def v136_l341 (kind/doc #'cx/complex-tensor))


(def v137_l343 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t138_l345
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v137_l343)))


(def v139_l347 (kind/doc #'cx/complex-tensor-real))


(def v140_l349 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t141_l351 (is ((fn [ct] (every? zero? (cx/im ct))) v140_l349)))


(def v142_l353 (kind/doc #'cx/complex))


(def v143_l355 (cx/complex 3.0 4.0))


(deftest
 t144_l357
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v143_l355)))


(def v145_l361 (kind/doc #'cx/re))


(def v146_l363 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t147_l365 (is (= v146_l363 [1.0 2.0])))


(def v148_l367 (kind/doc #'cx/im))


(def v149_l369 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t150_l371 (is (= v149_l369 [3.0 4.0])))


(def v151_l373 (kind/doc #'cx/complex-shape))


(def
 v152_l375
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t153_l378 (is (= v152_l375 [2 2])))


(def v154_l380 (kind/doc #'cx/scalar?))


(def v155_l382 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t156_l384 (is (true? v155_l382)))


(def v157_l386 (kind/doc #'cx/complex?))


(def v158_l388 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t159_l390 (is (true? v158_l388)))


(def v160_l392 (cx/complex? (la/eye 2)))


(deftest t161_l394 (is (false? v160_l392)))


(def v162_l396 (kind/doc #'cx/->tensor))


(def
 v163_l398
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t164_l400 (is (= v163_l398 [2 2])))


(def v165_l402 (kind/doc #'cx/->double-array))


(def
 v166_l404
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t167_l407 (is (= v166_l404 [1.0 3.0 2.0 4.0])))


(def v168_l409 (kind/doc #'cx/add))


(def
 v169_l411
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t170_l415 (is (= v169_l411 [11.0 22.0])))


(def v171_l417 (kind/doc #'cx/sub))


(def
 v172_l419
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t173_l423 (is (= v172_l419 [9.0 18.0])))


(def v174_l425 (kind/doc #'cx/scale))


(def
 v175_l427
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t176_l430 (is (= v175_l427 [[2.0 4.0] [6.0 8.0]])))


(def v177_l432 (kind/doc #'cx/mul))


(def
 v179_l435
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t180_l440 (is (= v179_l435 [-10.0 10.0])))


(def v181_l442 (kind/doc #'cx/conj))


(def
 v182_l444
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t183_l447 (is (= v182_l444 [-3.0 4.0])))


(def v184_l449 (kind/doc #'cx/abs))


(def
 v186_l452
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t187_l455 (is (true? v186_l452)))


(def v188_l457 (kind/doc #'cx/dot))


(def
 v189_l459
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t190_l464 (is (true? v189_l459)))


(def v191_l466 (kind/doc #'cx/dot-conj))


(def
 v193_l469
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t194_l473 (is (true? v193_l469)))


(def v195_l475 (kind/doc #'cx/sum))


(def
 v196_l477
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t197_l481 (is (= v196_l477 [6.0 15.0])))


(def v199_l488 (kind/doc #'ft/forward))


(def
 v200_l490
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t201_l494 (is (= v200_l490 [4])))


(def v202_l496 (kind/doc #'ft/inverse))


(def
 v203_l498
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t204_l502 (is (true? v203_l498)))


(def v205_l504 (kind/doc #'ft/inverse-real))


(def
 v206_l506
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t207_l510 (is (true? v206_l506)))


(def v208_l512 (kind/doc #'ft/forward-complex))


(def
 v209_l514
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t210_l518 (is (= v209_l514 [4])))


(def v211_l520 (kind/doc #'ft/dct-forward))


(def v212_l522 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t213_l524 (is ((fn [v] (= 4 (count v))) v212_l522)))


(def v214_l526 (kind/doc #'ft/dct-inverse))


(def
 v215_l528
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t216_l532 (is (true? v215_l528)))


(def v217_l534 (kind/doc #'ft/dst-forward))


(def v218_l536 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t219_l538 (is ((fn [v] (= 4 (count v))) v218_l536)))


(def v220_l540 (kind/doc #'ft/dst-inverse))


(def
 v221_l542
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t222_l546 (is (true? v221_l542)))


(def v223_l548 (kind/doc #'ft/dht-forward))


(def v224_l550 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t225_l552 (is ((fn [v] (= 4 (count v))) v224_l550)))


(def v226_l554 (kind/doc #'ft/dht-inverse))


(def
 v227_l556
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t228_l560 (is (true? v227_l556)))


(def v230_l566 (kind/doc #'tape/memory-status))


(def v231_l568 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t232_l570 (is ((fn [s] (= :contiguous s)) v231_l568)))


(def
 v233_l572
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t234_l574 (is ((fn [s] (= :strided s)) v233_l572)))


(def v235_l576 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t236_l578 (is ((fn [s] (= :lazy s)) v235_l576)))


(def v237_l580 (kind/doc #'tape/memory-relation))


(def
 v238_l582
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t239_l585 (is ((fn [r] (= :shared r)) v238_l582)))


(def
 v240_l587
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t241_l589 (is ((fn [r] (= :independent r)) v240_l587)))


(def
 v242_l591
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t243_l593 (is ((fn [r] (= :unknown-lazy r)) v242_l591)))


(def v244_l595 (kind/doc #'tape/with-tape))


(def
 v245_l597
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v246_l603 (select-keys tape-example [:result :entries]))


(deftest
 t247_l605
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v246_l603)))


(def v248_l608 (kind/doc #'tape/summary))


(def v249_l610 (tape/summary tape-example))


(deftest t250_l612 (is ((fn [s] (= 4 (:total s))) v249_l610)))


(def v251_l614 (kind/doc #'tape/origin))


(def v252_l616 (tape/origin tape-example (:result tape-example)))


(deftest t253_l618 (is ((fn [dag] (= :la/mmul (:op dag))) v252_l616)))


(def v254_l620 (kind/doc #'tape/mermaid))


(def v256_l624 (tape/mermaid tape-example (:result tape-example)))


(def v257_l626 (kind/doc #'tape/detect-memory-status))


(def v259_l631 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t260_l633
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v259_l631)))


(def v262_l641 (kind/doc #'elem/sq))


(def v263_l643 (elem/sq (la/column [2 3 4])))


(deftest
 t264_l645
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v263_l643)))


(def v265_l647 (kind/doc #'elem/sqrt))


(def v266_l649 (elem/sqrt (la/column [4 9 16])))


(deftest
 t267_l651
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v266_l649)))


(def v268_l653 (kind/doc #'elem/exp))


(def
 v269_l655
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t270_l657 (is (true? v269_l655)))


(def v271_l659 (kind/doc #'elem/log))


(def
 v272_l661
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [math/E])) 0 0)
  1.0))


(deftest t273_l663 (is (true? v272_l661)))


(def v274_l665 (kind/doc #'elem/log10))


(def
 v275_l667
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t276_l669 (is (true? v275_l667)))


(def v277_l671 (kind/doc #'elem/sin))


(def
 v278_l673
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ math/PI 2)])) 0 0)
  1.0))


(deftest t279_l675 (is (true? v278_l673)))


(def v280_l677 (kind/doc #'elem/cos))


(def
 v281_l679
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t282_l681 (is (true? v281_l679)))


(def v283_l683 (kind/doc #'elem/tan))


(def
 v284_l685
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ math/PI 4)])) 0 0)
  1.0))


(deftest t285_l687 (is (true? v284_l685)))


(def v286_l689 (kind/doc #'elem/sinh))


(def
 v287_l691
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t288_l693 (is (true? v287_l691)))


(def v289_l695 (kind/doc #'elem/cosh))


(def
 v290_l697
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t291_l699 (is (true? v290_l697)))


(def v292_l701 (kind/doc #'elem/tanh))


(def
 v293_l703
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t294_l705 (is (true? v293_l703)))


(def v295_l707 (kind/doc #'elem/abs))


(def v296_l709 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t297_l711 (is ((fn [v] (== 5.0 v)) v296_l709)))


(def v298_l713 (kind/doc #'elem/sum))


(def v299_l715 (elem/sum (la/column [1 2 3 4])))


(deftest t300_l717 (is ((fn [v] (== 10.0 v)) v299_l715)))


(def v301_l719 (kind/doc #'elem/mean))


(def v302_l721 (elem/mean (la/column [2 4 6])))


(deftest t303_l723 (is ((fn [v] (== 4.0 v)) v302_l721)))


(def v304_l725 (kind/doc #'elem/pow))


(def v305_l727 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t306_l729 (is ((fn [v] (== 8.0 v)) v305_l727)))


(def v307_l731 (kind/doc #'elem/cbrt))


(def
 v308_l733
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t309_l735 (is (true? v308_l733)))


(def v310_l737 (kind/doc #'elem/floor))


(def v311_l739 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t312_l741 (is ((fn [v] (== 2.0 v)) v311_l739)))


(def v313_l743 (kind/doc #'elem/ceil))


(def v314_l745 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t315_l747 (is ((fn [v] (== 3.0 v)) v314_l745)))


(def v316_l749 (kind/doc #'elem/min))


(def
 v317_l751
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t318_l753 (is ((fn [v] (== 3.0 v)) v317_l751)))


(def v319_l755 (kind/doc #'elem/max))


(def
 v320_l757
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t321_l759 (is ((fn [v] (== 5.0 v)) v320_l757)))


(def v322_l761 (kind/doc #'elem/asin))


(def v323_l763 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t324_l765
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v323_l763)))


(def v325_l767 (kind/doc #'elem/acos))


(def v326_l769 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t327_l771
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v326_l769)))


(def v328_l773 (kind/doc #'elem/atan))


(def v329_l775 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t330_l777
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v329_l775)))


(def v331_l779 (kind/doc #'elem/log1p))


(def v332_l781 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t333_l783 (is ((fn [v] (la/close-scalar? v 0.0)) v332_l781)))


(def v334_l785 (kind/doc #'elem/expm1))


(def v335_l787 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t336_l789 (is ((fn [v] (la/close-scalar? v 0.0)) v335_l787)))


(def v337_l791 (kind/doc #'elem/round))


(def v338_l793 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t339_l795 (is ((fn [v] (== 3.0 v)) v338_l793)))


(def v340_l797 (kind/doc #'elem/clip))


(def
 v341_l799
 (vec (dtype/->reader (elem/clip (la/column [-2 0.5 3]) -1 1))))


(deftest t342_l801 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v341_l799)))


(def v344_l807 (kind/doc #'grad/grad))


(def
 v345_l809
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t346_l815 (is (true? v345_l809)))


(def v348_l821 (kind/doc #'vis/arrow-plot))


(def
 v349_l823
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v350_l827 (kind/doc #'vis/graph-plot))


(def
 v351_l829
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v352_l833 (kind/doc #'vis/matrix->gray-image))


(def
 v353_l835
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t354_l840
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v353_l835)))


(def v355_l842 (kind/doc #'vis/extract-channel))


(def
 v356_l844
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t357_l850
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v356_l844)))
