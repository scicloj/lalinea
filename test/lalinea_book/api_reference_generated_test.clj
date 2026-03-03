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


(def v24_l75 (kind/doc #'t/compute-matrix))


(def v25_l77 (t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0))))


(deftest t26_l79 (is ((fn [m] (= (t/eye 3) m)) v25_l77)))


(def v27_l81 (kind/doc #'t/reduce-axis))


(def v29_l84 (t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) la/sum 1))


(deftest
 t30_l86
 (is
  ((fn
    [v]
    (and
     (= [2] (t/shape v))
     (la/close-scalar? (v 0) 6.0)
     (la/close-scalar? (v 1) 15.0)))
   v29_l84)))


(def v31_l90 (kind/doc #'t/flatten))


(def v32_l92 (t/flatten (t/column [1 2 3])))


(deftest t33_l94 (is ((fn [v] (= [1.0 2.0 3.0] v)) v32_l92)))


(def v34_l96 (kind/doc #'t/hstack))


(def v35_l98 (t/hstack [(t/column [1 2]) (t/column [3 4])]))


(deftest t36_l100 (is ((fn [m] (= [[1.0 3.0] [2.0 4.0]] m)) v35_l98)))


(def v37_l102 (kind/doc #'t/submatrix))


(def v38_l104 (t/submatrix (t/eye 4) :all (range 2)))


(deftest t39_l106 (is ((fn [m] (= [4 2] (t/shape m))) v38_l104)))


(def v40_l108 (kind/doc #'t/tensor->dmat))


(def
 v41_l110
 (let
  [t (t/matrix [[1 2] [3 4]]) dm (t/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t42_l114 (is (true? v41_l110)))


(def v43_l116 (kind/doc #'t/dmat->tensor))


(def
 v44_l118
 (let
  [dm (t/tensor->dmat (t/eye 2)) t (t/dmat->tensor dm)]
  (= [2 2] (t/shape t))))


(deftest t45_l122 (is (true? v44_l118)))


(def v46_l124 (kind/doc #'t/complex-tensor->zmat))


(def
 v47_l126
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t48_l130 (is (true? v47_l126)))


(def v49_l132 (kind/doc #'t/zmat->complex-tensor))


(def
 v50_l134
 (let
  [zm
   (t/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (t/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t51_l138 (is (true? v50_l134)))


(def v52_l140 (kind/doc #'t/ones))


(def v53_l142 (t/ones 2 3))


(deftest t54_l144 (is ((fn [m] (= [2 3] (t/shape m))) v53_l142)))


(def v55_l146 (kind/doc #'t/real-tensor?))


(def v56_l148 (t/real-tensor? (t/matrix [[1 2] [3 4]])))


(deftest t57_l150 (is (true? v56_l148)))


(def v58_l152 (t/real-tensor? [1 2 3]))


(deftest t59_l154 (is (false? v58_l152)))


(def v60_l156 (kind/doc #'t/->real-tensor))


(def v61_l158 (t/->real-tensor (t/matrix [[1 2] [3 4]])))


(deftest t62_l160 (is ((fn [rt] (t/real-tensor? rt)) v61_l158)))


(def v63_l162 (kind/doc #'t/->tensor))


(def v64_l164 (t/->tensor (t/matrix [[1 2] [3 4]])))


(deftest t65_l166 (is ((fn [t] (not (t/real-tensor? t))) v64_l164)))


(def v67_l170 (kind/doc #'la/add))


(def
 v68_l172
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t69_l175 (is ((fn [m] (== 11.0 (m 0 0))) v68_l172)))


(def v70_l177 (kind/doc #'la/sub))


(def
 v71_l179
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t72_l182 (is ((fn [m] (== 9.0 (m 0 0))) v71_l179)))


(def v73_l184 (kind/doc #'la/scale))


(def v74_l186 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t75_l188 (is ((fn [m] (== 6.0 (m 0 1))) v74_l186)))


(def v76_l190 (kind/doc #'la/mul))


(def
 v77_l192
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t78_l195
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v77_l192)))


(def v79_l198 (kind/doc #'la/abs))


(def v80_l200 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t81_l202 (is ((fn [m] (== 3.0 (m 0 0))) v80_l200)))


(def v82_l204 (kind/doc #'la/sq))


(def v83_l206 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t84_l208 (is ((fn [m] (== 4.0 (m 0 0))) v83_l206)))


(def v85_l210 (kind/doc #'la/sum))


(def v86_l212 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t87_l214 (is ((fn [v] (== 10.0 v)) v86_l212)))


(def v88_l216 (kind/doc #'la/prod))


(def v89_l218 (la/prod (t/matrix [2 3 4])))


(deftest t90_l220 (is ((fn [v] (== 24.0 v)) v89_l218)))


(def v91_l222 (kind/doc #'la/mmul))


(def v92_l224 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t93_l227
 (is ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v92_l224)))


(def v94_l230 (kind/doc #'la/transpose))


(def v95_l232 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t96_l234 (is ((fn [m] (= [3 2] (t/shape m))) v95_l232)))


(def v97_l236 (kind/doc #'la/trace))


(def v98_l238 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t99_l240 (is ((fn [v] (== 5.0 v)) v98_l238)))


(def v100_l242 (kind/doc #'la/det))


(def v101_l244 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t102_l246 (is ((fn [v] (la/close-scalar? v -2.0)) v101_l244)))


(def v103_l248 (kind/doc #'la/norm))


(def v104_l250 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t105_l252 (is ((fn [v] (la/close-scalar? v 5.0)) v104_l250)))


(def v106_l254 (kind/doc #'la/dot))


(def v107_l256 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t108_l258 (is ((fn [v] (== 32.0 v)) v107_l256)))


(def v109_l260 (kind/doc #'la/close?))


(def v110_l262 (la/close? (t/eye 2) (t/eye 2)))


(deftest t111_l264 (is (true? v110_l262)))


(def v112_l266 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t113_l268 (is (false? v112_l266)))


(def v114_l270 (kind/doc #'la/close-scalar?))


(def v115_l272 (la/close-scalar? 1.00000000001 1.0))


(deftest t116_l274 (is (true? v115_l272)))


(def v117_l276 (kind/doc #'la/invert))


(def
 v118_l278
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t119_l281 (is (true? v118_l278)))


(def v120_l283 (kind/doc #'la/solve))


(def
 v122_l286
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t123_l290
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v122_l286)))


(def v124_l293 (kind/doc #'la/eigen))


(def
 v125_l295
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t126_l299
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v125_l295)))


(def v127_l303 (kind/doc #'la/real-eigenvalues))


(def v128_l305 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t129_l307
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v128_l305)))


(def v130_l310 (kind/doc #'la/svd))


(def
 v131_l312
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t132_l317
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v131_l312)))


(def v133_l322 (kind/doc #'la/qr))


(def
 v134_l324
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t135_l327 (is (true? v134_l324)))


(def v136_l329 (kind/doc #'la/cholesky))


(def
 v137_l331
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t138_l335 (is (true? v137_l331)))


(def v139_l337 (kind/doc #'la/mpow))


(def v140_l339 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t141_l341
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v140_l339)))


(def v142_l343 (kind/doc #'la/rank))


(def v143_l345 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t144_l347 (is ((fn [r] (= 1 r)) v143_l345)))


(def v145_l349 (kind/doc #'la/condition-number))


(def v146_l351 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t147_l353 (is ((fn [v] (> v 1.0)) v146_l351)))


(def v148_l355 (kind/doc #'la/pinv))


(def
 v149_l357
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t150_l360 (is (true? v149_l357)))


(def v151_l362 (kind/doc #'la/lstsq))


(def
 v152_l364
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t153_l368
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v152_l364)))


(def v154_l370 (kind/doc #'la/null-space))


(def
 v155_l372
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t156_l376 (is (true? v155_l372)))


(def v157_l378 (kind/doc #'la/col-space))


(def
 v158_l380
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t159_l382 (is ((fn [r] (= 1 r)) v158_l380)))


(def v160_l384 (kind/doc #'la/lift))


(def v162_l387 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t163_l389
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v162_l387)))


(def v164_l392 (kind/doc #'la/lifted))


(def
 v166_l395
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t167_l398
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v166_l395)))


(def v169_l405 (kind/doc #'cx/complex-tensor))


(def v170_l407 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t171_l409
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v170_l407)))


(def v172_l411 (kind/doc #'cx/complex-tensor-real))


(def v173_l413 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t174_l415 (is ((fn [ct] (every? zero? (cx/im ct))) v173_l413)))


(def v175_l417 (kind/doc #'cx/complex))


(def v176_l419 (cx/complex 3.0 4.0))


(deftest
 t177_l421
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v176_l419)))


(def v178_l425 (kind/doc #'cx/re))


(def v179_l427 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t180_l429 (is (= v179_l427 [1.0 2.0])))


(def v181_l431 (kind/doc #'cx/im))


(def v182_l433 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t183_l435 (is (= v182_l433 [3.0 4.0])))


(def v184_l437 (kind/doc #'cx/complex-shape))


(def
 v185_l439
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t186_l442 (is (= v185_l439 [2 2])))


(def v187_l444 (kind/doc #'cx/scalar?))


(def v188_l446 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t189_l448 (is (true? v188_l446)))


(def v190_l450 (kind/doc #'cx/complex?))


(def v191_l452 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t192_l454 (is (true? v191_l452)))


(def v193_l456 (cx/complex? (t/eye 2)))


(deftest t194_l458 (is (false? v193_l456)))


(def v195_l460 (kind/doc #'cx/->tensor))


(def
 v196_l462
 (t/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t197_l464 (is (= v196_l462 [2 2])))


(def v198_l466 (kind/doc #'cx/->double-array))


(def
 v199_l468
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t200_l471 (is (= v199_l468 [1.0 3.0 2.0 4.0])))


(def v201_l473 (kind/doc #'cx/wrap-tensor))


(def
 v202_l475
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t203_l479
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v202_l475)))


(def v204_l481 (kind/doc #'cx/add))


(def
 v205_l483
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t206_l487 (is (= v205_l483 [11.0 22.0])))


(def v207_l489 (kind/doc #'cx/sub))


(def
 v208_l491
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t209_l495 (is (= v208_l491 [9.0 18.0])))


(def v210_l497 (kind/doc #'cx/scale))


(def
 v211_l499
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t212_l502 (is (= v211_l499 [[2.0 4.0] [6.0 8.0]])))


(def v213_l504 (kind/doc #'cx/mul))


(def
 v215_l507
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t216_l512 (is (= v215_l507 [-10.0 10.0])))


(def v217_l514 (kind/doc #'cx/conj))


(def
 v218_l516
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t219_l519 (is (= v218_l516 [-3.0 4.0])))


(def v220_l521 (kind/doc #'cx/abs))


(def
 v222_l524
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t223_l527 (is (true? v222_l524)))


(def v224_l529 (kind/doc #'cx/dot))


(def
 v225_l531
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t226_l536 (is (true? v225_l531)))


(def v227_l538 (kind/doc #'cx/dot-conj))


(def
 v229_l541
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t230_l545 (is (true? v229_l541)))


(def v231_l547 (kind/doc #'cx/sum))


(def
 v232_l549
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t233_l553 (is (= v232_l549 [6.0 15.0])))


(def v235_l560 (kind/doc #'ft/forward))


(def
 v236_l562
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t237_l566 (is (= v236_l562 [4])))


(def v238_l568 (kind/doc #'ft/inverse))


(def
 v239_l570
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t240_l574 (is (true? v239_l570)))


(def v241_l576 (kind/doc #'ft/inverse-real))


(def
 v242_l578
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t243_l582 (is (true? v242_l578)))


(def v244_l584 (kind/doc #'ft/forward-complex))


(def
 v245_l586
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t246_l590 (is (= v245_l586 [4])))


(def v247_l592 (kind/doc #'ft/dct-forward))


(def v248_l594 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t249_l596 (is ((fn [v] (= 4 (count v))) v248_l594)))


(def v250_l598 (kind/doc #'ft/dct-inverse))


(def
 v251_l600
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t252_l604 (is (true? v251_l600)))


(def v253_l606 (kind/doc #'ft/dst-forward))


(def v254_l608 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t255_l610 (is ((fn [v] (= 4 (count v))) v254_l608)))


(def v256_l612 (kind/doc #'ft/dst-inverse))


(def
 v257_l614
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t258_l618 (is (true? v257_l614)))


(def v259_l620 (kind/doc #'ft/dht-forward))


(def v260_l622 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t261_l624 (is ((fn [v] (= 4 (count v))) v260_l622)))


(def v262_l626 (kind/doc #'ft/dht-inverse))


(def
 v263_l628
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t264_l632 (is (true? v263_l628)))


(def v266_l638 (kind/doc #'tape/memory-status))


(def v267_l640 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t268_l642 (is ((fn [s] (= :contiguous s)) v267_l640)))


(def
 v269_l644
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t270_l646 (is ((fn [s] (= :strided s)) v269_l644)))


(def v271_l648 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t272_l650 (is ((fn [s] (= :lazy s)) v271_l648)))


(def v273_l652 (kind/doc #'tape/memory-relation))


(def
 v274_l654
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t275_l657 (is ((fn [r] (= :shared r)) v274_l654)))


(def
 v276_l659
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t277_l661 (is ((fn [r] (= :independent r)) v276_l659)))


(def
 v278_l663
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t279_l665 (is ((fn [r] (= :unknown-lazy r)) v278_l663)))


(def v280_l667 (kind/doc #'tape/with-tape))


(def
 v281_l669
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v282_l675 (select-keys tape-example [:result :entries]))


(deftest
 t283_l677
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v282_l675)))


(def v284_l680 (kind/doc #'tape/summary))


(def v285_l682 (tape/summary tape-example))


(deftest t286_l684 (is ((fn [s] (= 4 (:total s))) v285_l682)))


(def v287_l686 (kind/doc #'tape/origin))


(def v288_l688 (tape/origin tape-example (:result tape-example)))


(deftest t289_l690 (is ((fn [dag] (= :la/mmul (:op dag))) v288_l688)))


(def v290_l692 (kind/doc #'tape/mermaid))


(def v292_l696 (tape/mermaid tape-example (:result tape-example)))


(def v293_l698 (kind/doc #'tape/detect-memory-status))


(def v295_l703 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t296_l705
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v295_l703)))


(def v298_l713 (kind/doc #'elem/sq))


(def v299_l715 (elem/sq (t/column [2 3 4])))


(deftest
 t300_l717
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v299_l715)))


(def v301_l719 (kind/doc #'elem/sqrt))


(def v302_l721 (elem/sqrt (t/column [4 9 16])))


(deftest
 t303_l723
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v302_l721)))


(def v304_l725 (kind/doc #'elem/exp))


(def v305_l727 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t306_l729 (is (true? v305_l727)))


(def v307_l731 (kind/doc #'elem/log))


(def
 v308_l733
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t309_l735 (is (true? v308_l733)))


(def v310_l737 (kind/doc #'elem/log10))


(def
 v311_l739
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t312_l741 (is (true? v311_l739)))


(def v313_l743 (kind/doc #'elem/sin))


(def
 v314_l745
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t315_l747 (is (true? v314_l745)))


(def v316_l749 (kind/doc #'elem/cos))


(def v317_l751 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t318_l753 (is (true? v317_l751)))


(def v319_l755 (kind/doc #'elem/tan))


(def
 v320_l757
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t321_l759 (is (true? v320_l757)))


(def v322_l761 (kind/doc #'elem/sinh))


(def v323_l763 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t324_l765 (is (true? v323_l763)))


(def v325_l767 (kind/doc #'elem/cosh))


(def v326_l769 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t327_l771 (is (true? v326_l769)))


(def v328_l773 (kind/doc #'elem/tanh))


(def v329_l775 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t330_l777 (is (true? v329_l775)))


(def v331_l779 (kind/doc #'elem/abs))


(def v332_l781 ((elem/abs (t/column [-5])) 0 0))


(deftest t333_l783 (is ((fn [v] (== 5.0 v)) v332_l781)))


(def v334_l785 (kind/doc #'elem/sum))


(def v335_l787 (elem/sum (t/column [1 2 3 4])))


(deftest t336_l789 (is ((fn [v] (== 10.0 v)) v335_l787)))


(def v337_l791 (kind/doc #'elem/mean))


(def v338_l793 (elem/mean (t/column [2 4 6])))


(deftest t339_l795 (is ((fn [v] (== 4.0 v)) v338_l793)))


(def v340_l797 (kind/doc #'elem/pow))


(def v341_l799 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t342_l801 (is ((fn [v] (== 8.0 v)) v341_l799)))


(def v343_l803 (kind/doc #'elem/cbrt))


(def v344_l805 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t345_l807 (is (true? v344_l805)))


(def v346_l809 (kind/doc #'elem/floor))


(def v347_l811 ((elem/floor (t/column [2.7])) 0 0))


(deftest t348_l813 (is ((fn [v] (== 2.0 v)) v347_l811)))


(def v349_l815 (kind/doc #'elem/ceil))


(def v350_l817 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t351_l819 (is ((fn [v] (== 3.0 v)) v350_l817)))


(def v352_l821 (kind/doc #'elem/min))


(def v353_l823 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t354_l825 (is ((fn [v] (== 3.0 v)) v353_l823)))


(def v355_l827 (kind/doc #'elem/max))


(def v356_l829 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t357_l831 (is ((fn [v] (== 5.0 v)) v356_l829)))


(def v358_l833 (kind/doc #'elem/asin))


(def v359_l835 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t360_l837
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v359_l835)))


(def v361_l839 (kind/doc #'elem/acos))


(def v362_l841 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t363_l843
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v362_l841)))


(def v364_l845 (kind/doc #'elem/atan))


(def v365_l847 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t366_l849
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v365_l847)))


(def v367_l851 (kind/doc #'elem/log1p))


(def v368_l853 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t369_l855 (is ((fn [v] (la/close-scalar? v 0.0)) v368_l853)))


(def v370_l857 (kind/doc #'elem/expm1))


(def v371_l859 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t372_l861 (is ((fn [v] (la/close-scalar? v 0.0)) v371_l859)))


(def v373_l863 (kind/doc #'elem/round))


(def v374_l865 ((elem/round (t/column [2.7])) 0 0))


(deftest t375_l867 (is ((fn [v] (== 3.0 v)) v374_l865)))


(def v376_l869 (kind/doc #'elem/clip))


(def v377_l871 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t378_l873 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v377_l871)))


(def v380_l879 (kind/doc #'grad/grad))


(def
 v381_l881
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t382_l887 (is (true? v381_l881)))


(def v384_l893 (kind/doc #'vis/arrow-plot))


(def
 v385_l895
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v386_l899 (kind/doc #'vis/graph-plot))


(def
 v387_l901
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v388_l905 (kind/doc #'vis/matrix->gray-image))


(def
 v389_l907
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t390_l912
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v389_l907)))


(def v391_l914 (kind/doc #'vis/extract-channel))


(def
 v392_l916
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t393_l922
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v392_l916)))
