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


(def v16_l63 (la/diag (la/matrix [[1 2 3] [4 5 6] [7 8 9]])))


(deftest t17_l65 (is ((fn [v] (= [1.0 5.0 9.0] v)) v16_l63)))


(def v18_l67 (kind/doc #'la/column))


(def v19_l69 (la/column [1 2 3]))


(deftest t20_l71 (is ((fn [v] (= [3 1] (dtype/shape v))) v19_l69)))


(def v21_l73 (kind/doc #'la/row))


(def v22_l75 (la/row [1 2 3]))


(deftest t23_l77 (is ((fn [v] (= [1 3] (dtype/shape v))) v22_l75)))


(def v24_l79 (kind/doc #'la/add))


(def
 v25_l81
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t26_l84 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v25_l81)))


(def v27_l86 (kind/doc #'la/sub))


(def
 v28_l88
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t29_l91 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v28_l88)))


(def v30_l93 (kind/doc #'la/scale))


(def v31_l95 (la/scale (la/matrix [[1 2] [3 4]]) 3.0))


(deftest t32_l97 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v31_l95)))


(def v33_l99 (kind/doc #'la/mul))


(def
 v34_l101
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t35_l104
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v34_l101)))


(def v36_l107 (kind/doc #'la/abs))


(def v37_l109 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t38_l111 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v37_l109)))


(def v39_l113 (kind/doc #'la/sq))


(def v40_l115 (la/sq (la/matrix [[2 3] [4 5]])))


(deftest t41_l117 (is ((fn [m] (== 4.0 (tensor/mget m 0 0))) v40_l115)))


(def v42_l119 (kind/doc #'la/sum))


(def v43_l121 (la/sum (la/matrix [[1 2] [3 4]])))


(deftest t44_l123 (is ((fn [v] (== 10.0 v)) v43_l121)))


(def v45_l125 (kind/doc #'la/prod))


(def v46_l127 (la/prod (la/->real-tensor [2 3 4])))


(deftest t47_l129 (is ((fn [v] (== 24.0 v)) v46_l127)))


(def v48_l131 (kind/doc #'la/compute-matrix))


(def v49_l133 (la/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0))))


(deftest t50_l135 (is ((fn [m] (= (la/eye 3) m)) v49_l133)))


(def v51_l137 (kind/doc #'la/reduce-axis))


(def v53_l140 (la/reduce-axis (la/matrix [[1 2 3] [4 5 6]]) dfn/sum 1))


(deftest
 t54_l142
 (is
  ((fn
    [v]
    (and
     (= [2] (dtype/shape v))
     (la/close-scalar? (v 0) 6.0)
     (la/close-scalar? (v 1) 15.0)))
   v53_l140)))


(def v55_l146 (kind/doc #'la/flatten))


(def v56_l148 (la/flatten (la/column [1 2 3])))


(deftest t57_l150 (is ((fn [v] (= [1.0 2.0 3.0] v)) v56_l148)))


(def v58_l152 (kind/doc #'la/hstack))


(def v59_l154 (la/hstack [(la/column [1 2]) (la/column [3 4])]))


(deftest t60_l156 (is ((fn [m] (= [[1.0 3.0] [2.0 4.0]] m)) v59_l154)))


(def v61_l158 (kind/doc #'la/mmul))


(def v62_l160 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t63_l163
 (is
  ((fn
    [m]
    (and (= [2 1] (dtype/shape m)) (== 17.0 (tensor/mget m 0 0))))
   v62_l160)))


(def v64_l166 (kind/doc #'la/transpose))


(def v65_l168 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest t66_l170 (is ((fn [m] (= [3 2] (dtype/shape m))) v65_l168)))


(def v67_l172 (kind/doc #'la/submatrix))


(def v68_l174 (la/submatrix (la/eye 4) :all (range 2)))


(deftest t69_l176 (is ((fn [m] (= [4 2] (dtype/shape m))) v68_l174)))


(def v70_l178 (kind/doc #'la/trace))


(def v71_l180 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t72_l182 (is ((fn [v] (== 5.0 v)) v71_l180)))


(def v73_l184 (kind/doc #'la/det))


(def v74_l186 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t75_l188 (is ((fn [v] (la/close-scalar? v -2.0)) v74_l186)))


(def v76_l190 (kind/doc #'la/norm))


(def v77_l192 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t78_l194 (is ((fn [v] (la/close-scalar? v 5.0)) v77_l192)))


(def v79_l196 (kind/doc #'la/dot))


(def v80_l198 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t81_l200 (is ((fn [v] (== 32.0 v)) v80_l198)))


(def v82_l202 (kind/doc #'la/close?))


(def v83_l204 (la/close? (la/eye 2) (la/eye 2)))


(deftest t84_l206 (is (true? v83_l204)))


(def v85_l208 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t86_l210 (is (false? v85_l208)))


(def v87_l212 (kind/doc #'la/close-scalar?))


(def v88_l214 (la/close-scalar? 1.00000000001 1.0))


(deftest t89_l216 (is (true? v88_l214)))


(def v90_l218 (kind/doc #'la/invert))


(def
 v91_l220
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t92_l223 (is (true? v91_l220)))


(def v93_l225 (kind/doc #'la/solve))


(def
 v95_l228
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t96_l232
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v95_l228)))


(def v97_l235 (kind/doc #'la/eigen))


(def
 v98_l237
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t99_l241
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v98_l237)))


(def v100_l245 (kind/doc #'la/real-eigenvalues))


(def v101_l247 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t102_l249
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v101_l247)))


(def v103_l252 (kind/doc #'la/svd))


(def
 v104_l254
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(dtype/shape U) (count S) (dtype/shape Vt)]))


(deftest
 t105_l259
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v104_l254)))


(def v106_l264 (kind/doc #'la/qr))


(def
 v107_l266
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t108_l269 (is (true? v107_l266)))


(def v109_l271 (kind/doc #'la/cholesky))


(def
 v110_l273
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t111_l277 (is (true? v110_l273)))


(def v112_l279 (kind/doc #'la/tensor->dmat))


(def
 v113_l281
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t114_l285 (is (true? v113_l281)))


(def v115_l287 (kind/doc #'la/dmat->tensor))


(def
 v116_l289
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (dtype/shape t))))


(deftest t117_l293 (is (true? v116_l289)))


(def v118_l295 (kind/doc #'la/complex-tensor->zmat))


(def
 v119_l297
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t120_l301 (is (true? v119_l297)))


(def v121_l303 (kind/doc #'la/zmat->complex-tensor))


(def
 v122_l305
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t123_l309 (is (true? v122_l305)))


(def v124_l311 (kind/doc #'la/ones))


(def v125_l313 (la/ones 2 3))


(deftest t126_l315 (is ((fn [m] (= [2 3] (dtype/shape m))) v125_l313)))


(def v127_l317 (kind/doc #'la/mpow))


(def v128_l319 (la/mpow (la/matrix [[1 1] [0 1]]) 5))


(deftest
 t129_l321
 (is ((fn [m] (la/close? m (la/matrix [[1 5] [0 1]]))) v128_l319)))


(def v130_l323 (kind/doc #'la/rank))


(def v131_l325 (la/rank (la/matrix [[1 2] [2 4]])))


(deftest t132_l327 (is ((fn [r] (= 1 r)) v131_l325)))


(def v133_l329 (kind/doc #'la/condition-number))


(def v134_l331 (la/condition-number (la/matrix [[2 1] [1 3]])))


(deftest t135_l333 (is ((fn [v] (> v 1.0)) v134_l331)))


(def v136_l335 (kind/doc #'la/pinv))


(def
 v137_l337
 (let
  [A (la/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (la/eye 2))))


(deftest t138_l340 (is (true? v137_l337)))


(def v139_l342 (kind/doc #'la/lstsq))


(def
 v140_l344
 (let
  [{:keys [x rank]}
   (la/lstsq (la/matrix [[1 1] [1 2] [1 3]]) (la/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (la/column [0 1]))}))


(deftest
 t141_l348
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v140_l344)))


(def v142_l350 (kind/doc #'la/null-space))


(def
 v143_l352
 (let
  [ns (la/null-space (la/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (la/matrix [[1 2] [2 4]]) ns) (la/zeros 2 1))))


(deftest t144_l356 (is (true? v143_l352)))


(def v145_l358 (kind/doc #'la/col-space))


(def
 v146_l360
 (second (dtype/shape (la/col-space (la/matrix [[1 2] [2 4]])))))


(deftest t147_l362 (is ((fn [r] (= 1 r)) v146_l360)))


(def v148_l364 (kind/doc #'la/read-matrix))


(def v149_l366 (la/read-matrix [[1 2] [3 4]]))


(deftest t150_l368 (is ((fn [m] (= [2 2] (dtype/shape m))) v149_l366)))


(def v151_l370 (kind/doc #'la/read-column))


(def v152_l372 (la/read-column [5 6 7]))


(deftest t153_l374 (is ((fn [v] (= [3 1] (dtype/shape v))) v152_l372)))


(def v154_l376 (kind/doc #'la/real-tensor?))


(def v155_l378 (la/real-tensor? (la/matrix [[1 2] [3 4]])))


(deftest t156_l380 (is (true? v155_l378)))


(def v157_l382 (la/real-tensor? [1 2 3]))


(deftest t158_l384 (is (false? v157_l382)))


(def v159_l386 (kind/doc #'la/->real-tensor))


(def v160_l388 (la/->real-tensor (tensor/->tensor [[1 2] [3 4]])))


(deftest t161_l390 (is ((fn [rt] (la/real-tensor? rt)) v160_l388)))


(def v162_l392 (kind/doc #'la/->tensor))


(def v163_l394 (la/->tensor (la/matrix [[1 2] [3 4]])))


(deftest t164_l396 (is ((fn [t] (not (la/real-tensor? t))) v163_l394)))


(def v165_l398 (kind/doc #'la/lift))


(def v167_l401 (la/lift dfn/sqrt (la/matrix [[4 9] [16 25]])))


(deftest
 t168_l403
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (tensor/mget m 0 0) 2.0)
     (la/close-scalar? (tensor/mget m 0 1) 3.0)))
   v167_l401)))


(def v169_l406 (kind/doc #'la/lifted))


(def
 v171_l409
 (let [my-sqrt (la/lifted dfn/sqrt)] (my-sqrt (la/column [4 9 16]))))


(deftest
 t172_l412
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v171_l409)))


(def v174_l419 (kind/doc #'cx/complex-tensor))


(def v175_l421 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t176_l423
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v175_l421)))


(def v177_l425 (kind/doc #'cx/complex-tensor-real))


(def v178_l427 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t179_l429 (is ((fn [ct] (every? zero? (cx/im ct))) v178_l427)))


(def v180_l431 (kind/doc #'cx/complex))


(def v181_l433 (cx/complex 3.0 4.0))


(deftest
 t182_l435
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v181_l433)))


(def v183_l439 (kind/doc #'cx/re))


(def v184_l441 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t185_l443 (is (= v184_l441 [1.0 2.0])))


(def v186_l445 (kind/doc #'cx/im))


(def v187_l447 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t188_l449 (is (= v187_l447 [3.0 4.0])))


(def v189_l451 (kind/doc #'cx/complex-shape))


(def
 v190_l453
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t191_l456 (is (= v190_l453 [2 2])))


(def v192_l458 (kind/doc #'cx/scalar?))


(def v193_l460 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t194_l462 (is (true? v193_l460)))


(def v195_l464 (kind/doc #'cx/complex?))


(def v196_l466 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t197_l468 (is (true? v196_l466)))


(def v198_l470 (cx/complex? (la/eye 2)))


(deftest t199_l472 (is (false? v198_l470)))


(def v200_l474 (kind/doc #'cx/->tensor))


(def
 v201_l476
 (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t202_l478 (is (= v201_l476 [2 2])))


(def v203_l480 (kind/doc #'cx/->double-array))


(def
 v204_l482
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t205_l485 (is (= v204_l482 [1.0 3.0 2.0 4.0])))


(def v206_l487 (kind/doc #'cx/wrap-tensor))


(def
 v207_l489
 (let
  [raw (tensor/->tensor [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t208_l493
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v207_l489)))


(def v209_l495 (kind/doc #'cx/add))


(def
 v210_l497
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t211_l501 (is (= v210_l497 [11.0 22.0])))


(def v212_l503 (kind/doc #'cx/sub))


(def
 v213_l505
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t214_l509 (is (= v213_l505 [9.0 18.0])))


(def v215_l511 (kind/doc #'cx/scale))


(def
 v216_l513
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t217_l516 (is (= v216_l513 [[2.0 4.0] [6.0 8.0]])))


(def v218_l518 (kind/doc #'cx/mul))


(def
 v220_l521
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t221_l526 (is (= v220_l521 [-10.0 10.0])))


(def v222_l528 (kind/doc #'cx/conj))


(def
 v223_l530
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t224_l533 (is (= v223_l530 [-3.0 4.0])))


(def v225_l535 (kind/doc #'cx/abs))


(def
 v227_l538
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t228_l541 (is (true? v227_l538)))


(def v229_l543 (kind/doc #'cx/dot))


(def
 v230_l545
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t231_l550 (is (true? v230_l545)))


(def v232_l552 (kind/doc #'cx/dot-conj))


(def
 v234_l555
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t235_l559 (is (true? v234_l555)))


(def v236_l561 (kind/doc #'cx/sum))


(def
 v237_l563
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t238_l567 (is (= v237_l563 [6.0 15.0])))


(def v240_l574 (kind/doc #'ft/forward))


(def
 v241_l576
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t242_l580 (is (= v241_l576 [4])))


(def v243_l582 (kind/doc #'ft/inverse))


(def
 v244_l584
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t245_l588 (is (true? v244_l584)))


(def v246_l590 (kind/doc #'ft/inverse-real))


(def
 v247_l592
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t248_l596 (is (true? v247_l592)))


(def v249_l598 (kind/doc #'ft/forward-complex))


(def
 v250_l600
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t251_l604 (is (= v250_l600 [4])))


(def v252_l606 (kind/doc #'ft/dct-forward))


(def v253_l608 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t254_l610 (is ((fn [v] (= 4 (count v))) v253_l608)))


(def v255_l612 (kind/doc #'ft/dct-inverse))


(def
 v256_l614
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t257_l618 (is (true? v256_l614)))


(def v258_l620 (kind/doc #'ft/dst-forward))


(def v259_l622 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t260_l624 (is ((fn [v] (= 4 (count v))) v259_l622)))


(def v261_l626 (kind/doc #'ft/dst-inverse))


(def
 v262_l628
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t263_l632 (is (true? v262_l628)))


(def v264_l634 (kind/doc #'ft/dht-forward))


(def v265_l636 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t266_l638 (is ((fn [v] (= 4 (count v))) v265_l636)))


(def v267_l640 (kind/doc #'ft/dht-inverse))


(def
 v268_l642
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t269_l646 (is (true? v268_l642)))


(def v271_l652 (kind/doc #'tape/memory-status))


(def v272_l654 (tape/memory-status (la/matrix [[1 2] [3 4]])))


(deftest t273_l656 (is ((fn [s] (= :contiguous s)) v272_l654)))


(def
 v274_l658
 (tape/memory-status (la/transpose (la/matrix [[1 2] [3 4]]))))


(deftest t275_l660 (is ((fn [s] (= :strided s)) v274_l658)))


(def v276_l662 (tape/memory-status (la/add (la/eye 2) (la/eye 2))))


(deftest t277_l664 (is ((fn [s] (= :lazy s)) v276_l662)))


(def v278_l666 (kind/doc #'tape/memory-relation))


(def
 v279_l668
 (let
  [A (la/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t280_l671 (is ((fn [r] (= :shared r)) v279_l668)))


(def
 v281_l673
 (tape/memory-relation
  (la/matrix [[1 0] [0 1]])
  (la/matrix [[5 6] [7 8]])))


(deftest t282_l675 (is ((fn [r] (= :independent r)) v281_l673)))


(def
 v283_l677
 (tape/memory-relation
  (la/matrix [[1 2] [3 4]])
  (la/add (la/eye 2) (la/eye 2))))


(deftest t284_l679 (is ((fn [r] (= :unknown-lazy r)) v283_l677)))


(def v285_l681 (kind/doc #'tape/with-tape))


(def
 v286_l683
 (def
  tape-example
  (tape/with-tape
   (let
    [A (la/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v287_l689 (select-keys tape-example [:result :entries]))


(deftest
 t288_l691
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v287_l689)))


(def v289_l694 (kind/doc #'tape/summary))


(def v290_l696 (tape/summary tape-example))


(deftest t291_l698 (is ((fn [s] (= 4 (:total s))) v290_l696)))


(def v292_l700 (kind/doc #'tape/origin))


(def v293_l702 (tape/origin tape-example (:result tape-example)))


(deftest t294_l704 (is ((fn [dag] (= :la/mmul (:op dag))) v293_l702)))


(def v295_l706 (kind/doc #'tape/mermaid))


(def v297_l710 (tape/mermaid tape-example (:result tape-example)))


(def v298_l712 (kind/doc #'tape/detect-memory-status))


(def v300_l717 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t301_l719
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v300_l717)))


(def v303_l727 (kind/doc #'elem/sq))


(def v304_l729 (elem/sq (la/column [2 3 4])))


(deftest
 t305_l731
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 4.0)) v304_l729)))


(def v306_l733 (kind/doc #'elem/sqrt))


(def v307_l735 (elem/sqrt (la/column [4 9 16])))


(deftest
 t308_l737
 (is ((fn [v] (la/close-scalar? (tensor/mget v 0 0) 2.0)) v307_l735)))


(def v309_l739 (kind/doc #'elem/exp))


(def
 v310_l741
 (la/close-scalar? (tensor/mget (elem/exp (la/column [0])) 0 0) 1.0))


(deftest t311_l743 (is (true? v310_l741)))


(def v312_l745 (kind/doc #'elem/log))


(def
 v313_l747
 (la/close-scalar?
  (tensor/mget (elem/log (la/column [math/E])) 0 0)
  1.0))


(deftest t314_l749 (is (true? v313_l747)))


(def v315_l751 (kind/doc #'elem/log10))


(def
 v316_l753
 (la/close-scalar?
  (tensor/mget (elem/log10 (la/column [100])) 0 0)
  2.0))


(deftest t317_l755 (is (true? v316_l753)))


(def v318_l757 (kind/doc #'elem/sin))


(def
 v319_l759
 (la/close-scalar?
  (tensor/mget (elem/sin (la/column [(/ math/PI 2)])) 0 0)
  1.0))


(deftest t320_l761 (is (true? v319_l759)))


(def v321_l763 (kind/doc #'elem/cos))


(def
 v322_l765
 (la/close-scalar? (tensor/mget (elem/cos (la/column [0])) 0 0) 1.0))


(deftest t323_l767 (is (true? v322_l765)))


(def v324_l769 (kind/doc #'elem/tan))


(def
 v325_l771
 (la/close-scalar?
  (tensor/mget (elem/tan (la/column [(/ math/PI 4)])) 0 0)
  1.0))


(deftest t326_l773 (is (true? v325_l771)))


(def v327_l775 (kind/doc #'elem/sinh))


(def
 v328_l777
 (la/close-scalar? (tensor/mget (elem/sinh (la/column [0])) 0 0) 0.0))


(deftest t329_l779 (is (true? v328_l777)))


(def v330_l781 (kind/doc #'elem/cosh))


(def
 v331_l783
 (la/close-scalar? (tensor/mget (elem/cosh (la/column [0])) 0 0) 1.0))


(deftest t332_l785 (is (true? v331_l783)))


(def v333_l787 (kind/doc #'elem/tanh))


(def
 v334_l789
 (la/close-scalar? (tensor/mget (elem/tanh (la/column [0])) 0 0) 0.0))


(deftest t335_l791 (is (true? v334_l789)))


(def v336_l793 (kind/doc #'elem/abs))


(def v337_l795 (tensor/mget (elem/abs (la/column [-5])) 0 0))


(deftest t338_l797 (is ((fn [v] (== 5.0 v)) v337_l795)))


(def v339_l799 (kind/doc #'elem/sum))


(def v340_l801 (elem/sum (la/column [1 2 3 4])))


(deftest t341_l803 (is ((fn [v] (== 10.0 v)) v340_l801)))


(def v342_l805 (kind/doc #'elem/mean))


(def v343_l807 (elem/mean (la/column [2 4 6])))


(deftest t344_l809 (is ((fn [v] (== 4.0 v)) v343_l807)))


(def v345_l811 (kind/doc #'elem/pow))


(def v346_l813 (tensor/mget (elem/pow (la/column [2]) 3) 0 0))


(deftest t347_l815 (is ((fn [v] (== 8.0 v)) v346_l813)))


(def v348_l817 (kind/doc #'elem/cbrt))


(def
 v349_l819
 (la/close-scalar? (tensor/mget (elem/cbrt (la/column [27])) 0 0) 3.0))


(deftest t350_l821 (is (true? v349_l819)))


(def v351_l823 (kind/doc #'elem/floor))


(def v352_l825 (tensor/mget (elem/floor (la/column [2.7])) 0 0))


(deftest t353_l827 (is ((fn [v] (== 2.0 v)) v352_l825)))


(def v354_l829 (kind/doc #'elem/ceil))


(def v355_l831 (tensor/mget (elem/ceil (la/column [2.3])) 0 0))


(deftest t356_l833 (is ((fn [v] (== 3.0 v)) v355_l831)))


(def v357_l835 (kind/doc #'elem/min))


(def
 v358_l837
 (tensor/mget (elem/min (la/column [3]) (la/column [5])) 0 0))


(deftest t359_l839 (is ((fn [v] (== 3.0 v)) v358_l837)))


(def v360_l841 (kind/doc #'elem/max))


(def
 v361_l843
 (tensor/mget (elem/max (la/column [3]) (la/column [5])) 0 0))


(deftest t362_l845 (is ((fn [v] (== 5.0 v)) v361_l843)))


(def v363_l847 (kind/doc #'elem/asin))


(def v364_l849 (tensor/mget (elem/asin (la/column [0.5])) 0 0))


(deftest
 t365_l851
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v364_l849)))


(def v366_l853 (kind/doc #'elem/acos))


(def v367_l855 (tensor/mget (elem/acos (la/column [0.5])) 0 0))


(deftest
 t368_l857
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v367_l855)))


(def v369_l859 (kind/doc #'elem/atan))


(def v370_l861 (tensor/mget (elem/atan (la/column [1.0])) 0 0))


(deftest
 t371_l863
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v370_l861)))


(def v372_l865 (kind/doc #'elem/log1p))


(def v373_l867 (tensor/mget (elem/log1p (la/column [0.0])) 0 0))


(deftest t374_l869 (is ((fn [v] (la/close-scalar? v 0.0)) v373_l867)))


(def v375_l871 (kind/doc #'elem/expm1))


(def v376_l873 (tensor/mget (elem/expm1 (la/column [0.0])) 0 0))


(deftest t377_l875 (is ((fn [v] (la/close-scalar? v 0.0)) v376_l873)))


(def v378_l877 (kind/doc #'elem/round))


(def v379_l879 (tensor/mget (elem/round (la/column [2.7])) 0 0))


(deftest t380_l881 (is ((fn [v] (== 3.0 v)) v379_l879)))


(def v381_l883 (kind/doc #'elem/clip))


(def v382_l885 (la/flatten (elem/clip (la/column [-2 0.5 3]) -1 1)))


(deftest t383_l887 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v382_l885)))


(def v385_l893 (kind/doc #'grad/grad))


(def
 v386_l895
 (let
  [A
   (la/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t387_l901 (is (true? v386_l895)))


(def v389_l907 (kind/doc #'vis/arrow-plot))


(def
 v390_l909
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v391_l913 (kind/doc #'vis/graph-plot))


(def
 v392_l915
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v393_l919 (kind/doc #'vis/matrix->gray-image))


(def
 v394_l921
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t395_l926
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v394_l921)))


(def v396_l928 (kind/doc #'vis/extract-channel))


(def
 v397_l930
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t398_l936
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v397_l930)))
