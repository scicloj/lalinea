(ns
 lalinea-book.api-reference-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.transform :as ft]
  [scicloj.lalinea.tape :as tape]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.lalinea.grad :as grad]
  [scicloj.lalinea.vis :as vis]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (kind/doc #'t/matrix))


(def v4_l32 (t/matrix [[1 2] [3 4]]))


(deftest t5_l34 (is ((fn [m] (= [2 2] (t/shape m))) v4_l32)))


(def v6_l36 (kind/doc #'t/eye))


(def v7_l38 (t/eye 3))


(deftest
 t8_l40
 (is
  ((fn
    [m]
    (and (= [3 3] (t/shape m)) (== 1.0 (m 0 0)) (== 0.0 (m 0 1))))
   v7_l38)))


(def v9_l44 (kind/doc #'t/zeros))


(def v10_l46 (t/zeros 2 3))


(deftest t11_l48 (is ((fn [m] (= [2 3] (t/shape m))) v10_l46)))


(def v12_l50 (kind/doc #'t/ones))


(def v13_l52 (t/ones 2 3))


(deftest t14_l54 (is ((fn [m] (= [2 3] (t/shape m))) v13_l52)))


(def v15_l56 (kind/doc #'t/diag))


(def v16_l58 (t/diag [3 5 7]))


(deftest
 t17_l60
 (is
  ((fn
    [m]
    (and (= [3 3] (t/shape m)) (== 5.0 (m 1 1)) (== 0.0 (m 0 1))))
   v16_l58)))


(def v19_l66 (t/diag (t/matrix [[1 2 3] [4 5 6] [7 8 9]])))


(deftest t20_l68 (is ((fn [v] (= [1.0 5.0 9.0] v)) v19_l66)))


(def v21_l70 (kind/doc #'t/column))


(def v22_l72 (t/column [1 2 3]))


(deftest t23_l74 (is ((fn [v] (= [3 1] (t/shape v))) v22_l72)))


(def v24_l76 (kind/doc #'t/row))


(def v25_l78 (t/row [1 2 3]))


(deftest t26_l80 (is ((fn [v] (= [1 3] (t/shape v))) v25_l78)))


(def v27_l82 (kind/doc #'t/compute-matrix))


(def v28_l84 (t/compute-matrix 3 3 (fn [i j] (if (== i j) 1.0 0.0))))


(deftest t29_l86 (is ((fn [m] (= (t/eye 3) m)) v28_l84)))


(def v30_l88 (kind/doc #'t/compute-tensor))


(def
 v31_l90
 (t/compute-tensor [2 3] (fn [i j] (+ (* 10.0 i) j)) :float64))


(deftest
 t32_l92
 (is ((fn [m] (and (= [2 3] (t/shape m)) (== 12.0 (m 1 2)))) v31_l90)))


(def v33_l95 (kind/doc #'t/complex-tensor))


(def v34_l97 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t35_l99 (is ((fn [ct] (= [3] (t/complex-shape ct))) v34_l97)))


(def v36_l101 (kind/doc #'t/complex-tensor-real))


(def v37_l103 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t38_l105 (is ((fn [ct] (every? zero? (el/im ct))) v37_l103)))


(def v39_l107 (kind/doc #'t/complex))


(def v40_l109 (t/complex 3.0 4.0))


(deftest
 t41_l111
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (el/re ct)) (== 4.0 (el/im ct))))
   v40_l109)))


(def v42_l115 (kind/doc #'t/wrap-tensor))


(def
 v43_l117
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t44_l121
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v43_l117)))


(def v45_l123 (kind/doc #'t/real-tensor?))


(def v46_l125 (t/real-tensor? (t/matrix [[1 2] [3 4]])))


(deftest t47_l127 (is (true? v46_l125)))


(def v48_l129 (t/real-tensor? [1 2 3]))


(deftest t49_l131 (is (false? v48_l129)))


(def v50_l133 (kind/doc #'t/complex?))


(def v51_l135 (t/complex? (t/complex 3.0 4.0)))


(deftest t52_l137 (is (true? v51_l135)))


(def v53_l139 (t/complex? (t/eye 2)))


(deftest t54_l141 (is (false? v53_l139)))


(def v55_l143 (kind/doc #'t/scalar?))


(def v56_l145 (t/scalar? (t/complex 3.0 4.0)))


(deftest t57_l147 (is (true? v56_l145)))


(def v58_l149 (kind/doc #'t/shape))


(def v59_l151 (t/shape (t/matrix [[1 2 3] [4 5 6]])))


(deftest t60_l153 (is ((fn [s] (= [2 3] s)) v59_l151)))


(def v62_l158 (t/shape (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t63_l160 (is (= v62_l158 [2])))


(def v64_l162 (kind/doc #'t/complex-shape))


(def
 v65_l164
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t66_l167 (is (= v65_l164 [2 2])))


(def v67_l169 (kind/doc #'t/reshape))


(def v68_l171 (t/reshape (t/matrix [[1 2] [3 4]]) [4]))


(deftest t69_l173 (is ((fn [v] (= [1.0 2.0 3.0 4.0] v)) v68_l171)))


(def v70_l175 (kind/doc #'t/select))


(def v72_l178 (t/select (t/matrix [[1 2] [3 4] [5 6]]) 0 :all))


(deftest t73_l180 (is ((fn [v] (= [1.0 2.0] v)) v72_l178)))


(def v74_l182 (kind/doc #'t/submatrix))


(def v75_l184 (t/submatrix (t/eye 4) :all (range 2)))


(deftest t76_l186 (is ((fn [m] (= [4 2] (t/shape m))) v75_l184)))


(def v77_l188 (kind/doc #'t/flatten))


(def v78_l190 (t/flatten (t/column [1 2 3])))


(deftest t79_l192 (is ((fn [v] (= [1.0 2.0 3.0] v)) v78_l190)))


(def v80_l194 (kind/doc #'t/hstack))


(def v81_l196 (t/hstack [(t/column [1 2]) (t/column [3 4])]))


(deftest t82_l198 (is ((fn [m] (= [[1.0 3.0] [2.0 4.0]] m)) v81_l196)))


(def v83_l200 (kind/doc #'t/reduce-axis))


(def v85_l203 (t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) el/sum 1))


(deftest
 t86_l205
 (is
  ((fn
    [v]
    (and
     (= [2] (t/shape v))
     (la/close-scalar? (v 0) 6.0)
     (la/close-scalar? (v 1) 15.0)))
   v85_l203)))


(def v87_l209 (kind/doc #'t/clone))


(def
 v89_l212
 (let [m (t/matrix [[1 2] [3 4]])] (identical? m (t/clone m))))


(deftest t90_l215 (is (false? v89_l212)))


(def v91_l217 (kind/doc #'t/concrete?))


(def v93_l220 (t/concrete? (t/matrix [[1 2] [3 4]])))


(deftest t94_l222 (is (true? v93_l220)))


(def
 v96_l225
 (t/concrete?
  (el/+ (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest t97_l228 (is (false? v96_l225)))


(def v98_l230 (kind/doc #'t/materialize))


(def
 v100_l233
 (let [m (t/matrix [[1 2] [3 4]])] (identical? m (t/materialize m))))


(deftest t101_l236 (is (true? v100_l233)))


(def
 v103_l239
 (t/materialize
  (el/+ (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest
 t104_l242
 (is ((fn [m] (= [[11.0 22.0] [33.0 44.0]] m)) v103_l239)))


(def v105_l244 (kind/doc #'t/->tensor))


(def v106_l246 (t/->tensor (t/matrix [[1 2] [3 4]])))


(deftest t107_l248 (is ((fn [t] (not (t/real-tensor? t))) v106_l246)))


(def
 v109_l252
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t110_l254 (is (= v109_l252 [2 2])))


(def v111_l256 (kind/doc #'t/->real-tensor))


(def v112_l258 (t/->real-tensor (t/matrix [[1 2] [3 4]])))


(deftest t113_l260 (is ((fn [rt] (t/real-tensor? rt)) v112_l258)))


(def v114_l262 (kind/doc #'t/->double-array))


(def
 v115_l264
 (let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))] (alength arr)))


(deftest t116_l267 (is ((fn [n] (= 4 n)) v115_l264)))


(def
 v118_l271
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t119_l274 (is (= v118_l271 [1.0 3.0 2.0 4.0])))


(def v120_l276 (kind/doc #'t/backing-array))


(def
 v121_l278
 (let
  [A (t/matrix [[1 2] [3 4]]) B (t/clone A)]
  [(some? (t/backing-array A))
   (identical? (t/backing-array A) (t/backing-array B))]))


(deftest t122_l284 (is (= v121_l278 [true false])))


(def v123_l286 (kind/doc #'t/->reader))


(def v124_l288 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t125_l291 (is ((fn [v] (== 30.0 v)) v124_l288)))


(def v126_l293 (kind/doc #'t/array-buffer))


(def v127_l295 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t128_l297 (is (true? v127_l295)))


(def v129_l299 (kind/doc #'t/make-reader))


(def v130_l301 (t/make-reader :float64 5 (* idx idx)))


(deftest t131_l303 (is ((fn [r] (= 16.0 (r 4))) v130_l301)))


(def v132_l305 (kind/doc #'t/make-container))


(def v133_l307 (t/make-container :float64 4))


(deftest t134_l309 (is ((fn [c] (= 4 (count c))) v133_l307)))


(def v135_l311 (kind/doc #'t/elemwise-cast))


(def v136_l313 (t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32))


(deftest
 t137_l315
 (is
  ((fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))
   v136_l313)))


(def v138_l317 (kind/doc #'t/mset!))


(def
 v139_l319
 (let
  [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0)))


(deftest t140_l323 (is ((fn [v] (== 99.0 v)) v139_l319)))


(def v141_l325 (kind/doc #'t/set-value!))


(def
 v142_l327
 (let
  [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1)))


(deftest t143_l331 (is ((fn [v] (== 42.0 v)) v142_l327)))


(def v144_l333 (kind/doc #'t/tensor->dmat))


(def
 v145_l335
 (let
  [t (t/matrix [[1 2] [3 4]]) dm (t/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t146_l339 (is (true? v145_l335)))


(def v147_l341 (kind/doc #'t/dmat->tensor))


(def
 v148_l343
 (let
  [dm (t/tensor->dmat (t/eye 2)) t (t/dmat->tensor dm)]
  (= [2 2] (t/shape t))))


(deftest t149_l347 (is (true? v148_l343)))


(def v150_l349 (kind/doc #'t/complex-tensor->zmat))


(def
 v151_l351
 (let
  [ct
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t152_l355 (is (true? v151_l351)))


(def v153_l357 (kind/doc #'t/zmat->complex-tensor))


(def
 v154_l359
 (let
  [zm
   (t/complex-tensor->zmat (t/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (t/zmat->complex-tensor zm)]
  (t/complex? ct)))


(deftest t155_l363 (is (true? v154_l359)))


(def v157_l370 (kind/doc #'la/mmul))


(def v158_l372 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t159_l375
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v158_l372)))


(def v160_l378 (kind/doc #'la/dot))


(def v161_l380 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t162_l382 (is ((fn [v] (== 32.0 v)) v161_l380)))


(def v163_l384 (kind/doc #'la/dot-conj))


(def
 v165_l388
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (el/re result) 30.0)))


(deftest t166_l392 (is (true? v165_l388)))


(def v167_l394 (kind/doc #'la/mpow))


(def v168_l396 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t169_l398
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v168_l396)))


(def v170_l400 (kind/doc #'la/transpose))


(def v171_l402 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t172_l404 (is ((fn [m] (= [3 2] (t/shape m))) v171_l402)))


(def v173_l406 (kind/doc #'la/trace))


(def v174_l408 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t175_l410 (is ((fn [v] (== 5.0 v)) v174_l408)))


(def v176_l412 (kind/doc #'la/det))


(def v177_l414 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t178_l416 (is ((fn [v] (la/close-scalar? v -2.0)) v177_l414)))


(def v179_l418 (kind/doc #'la/norm))


(def v180_l420 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t181_l422 (is ((fn [v] (la/close-scalar? v 5.0)) v180_l420)))


(def v182_l424 (kind/doc #'la/rank))


(def v183_l426 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t184_l428 (is ((fn [r] (= 1 r)) v183_l426)))


(def v185_l430 (kind/doc #'la/condition-number))


(def v186_l432 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t187_l434 (is ((fn [v] (> v 1.0)) v186_l432)))


(def v188_l436 (kind/doc #'la/solve))


(def
 v190_l439
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t191_l443
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v190_l439)))


(def v192_l446 (kind/doc #'la/invert))


(def
 v193_l448
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t194_l451 (is (true? v193_l448)))


(def v195_l453 (kind/doc #'la/lstsq))


(def
 v196_l455
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t197_l459
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v196_l455)))


(def v198_l461 (kind/doc #'la/pinv))


(def
 v199_l463
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t200_l466 (is (true? v199_l463)))


(def v201_l468 (kind/doc #'la/eigen))


(def
 v202_l470
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t203_l474
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v202_l470)))


(def v204_l478 (kind/doc #'la/real-eigenvalues))


(def v205_l480 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t206_l482
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v205_l480)))


(def v207_l485 (kind/doc #'la/svd))


(def
 v208_l487
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t209_l492
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v208_l487)))


(def v210_l497 (kind/doc #'la/qr))


(def
 v211_l499
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t212_l502 (is (true? v211_l499)))


(def v213_l504 (kind/doc #'la/cholesky))


(def
 v214_l506
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t215_l510 (is (true? v214_l506)))


(def v216_l512 (kind/doc #'la/null-space))


(def
 v217_l514
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t218_l518 (is (true? v217_l514)))


(def v219_l520 (kind/doc #'la/col-space))


(def
 v220_l522
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t221_l524 (is ((fn [r] (= 1 r)) v220_l522)))


(def v222_l526 (kind/doc #'la/close?))


(def v223_l528 (la/close? (t/eye 2) (t/eye 2)))


(deftest t224_l530 (is (true? v223_l528)))


(def v225_l532 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t226_l534 (is (false? v225_l532)))


(def v227_l536 (kind/doc #'la/close-scalar?))


(def v228_l538 (la/close-scalar? 1.00000000001 1.0))


(deftest t229_l540 (is (true? v228_l538)))


(def v230_l542 (kind/doc #'la/lift))


(def v232_l545 (la/lift el/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t233_l547
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v232_l545)))


(def v234_l550 (kind/doc #'la/lifted))


(def
 v236_l553
 (let [my-sqrt (la/lifted el/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t237_l556
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v236_l553)))


(def v239_l564 (kind/doc #'el/+))


(def v240_l566 (el/+ (t/column [1 2 3]) (t/column [10 20 30])))


(deftest t241_l568 (is ((fn [v] (== 11.0 (v 0 0))) v240_l566)))


(def
 v243_l572
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (el/re (el/+ a b))))


(deftest t244_l576 (is (= v243_l572 [11.0 22.0])))


(def v245_l578 (kind/doc #'el/-))


(def v246_l580 (el/- (t/column [10 20 30]) (t/column [1 2 3])))


(deftest t247_l582 (is ((fn [v] (== 9.0 (v 0 0))) v246_l580)))


(def v248_l584 (kind/doc #'el/scale))


(def v249_l586 (el/scale (t/column [2 3 4]) 5.0))


(deftest t250_l588 (is ((fn [v] (== 10.0 (v 0 0))) v249_l586)))


(def v251_l590 (kind/doc #'el/*))


(def v252_l592 (el/* (t/column [2 3 4]) (t/column [10 20 30])))


(deftest t253_l594 (is ((fn [v] (== 20.0 (v 0 0))) v252_l592)))


(def
 v255_l598
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (el/* a b)]
  [(el/re (c 0)) (el/im (c 0))]))


(deftest t256_l603 (is (= v255_l598 [-10.0 10.0])))


(def v257_l605 (kind/doc #'el//))


(def v258_l607 (el// (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t259_l609
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v258_l607)))


(def v261_l613 (el// (t/complex 3.0 4.0) (t/complex 1.0 2.0)))


(deftest
 t262_l615
 (is
  ((fn
    [v]
    (and
     (< (abs (- (el/re v) 2.2)) 1.0E-10)
     (< (abs (- (el/im v) -0.4)) 1.0E-10)))
   v261_l613)))


(def v263_l618 (kind/doc #'el/re))


(def v264_l620 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t265_l622 (is (= v264_l620 [1.0 2.0])))


(def v266_l624 (kind/doc #'el/im))


(def v267_l626 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t268_l628 (is (= v267_l626 [3.0 4.0])))


(def v269_l630 (kind/doc #'el/conj))


(def v270_l632 (let [z (t/complex 3.0 4.0)] (el/im (el/conj z))))


(deftest t271_l635 (is ((fn [v] (la/close-scalar? v -4.0)) v270_l632)))


(def v272_l637 (kind/doc #'el/sq))


(def v273_l639 (el/sq (t/column [2 3 4])))


(deftest
 t274_l641
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v273_l639)))


(def v275_l643 (kind/doc #'el/sqrt))


(def v276_l645 (el/sqrt (t/column [4 9 16])))


(deftest
 t277_l647
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v276_l645)))


(def v278_l649 (kind/doc #'el/pow))


(def v279_l651 ((el/pow (t/column [2]) 3) 0 0))


(deftest t280_l653 (is ((fn [v] (== 8.0 v)) v279_l651)))


(def v281_l655 (kind/doc #'el/cbrt))


(def v282_l657 (la/close-scalar? ((el/cbrt (t/column [27])) 0 0) 3.0))


(deftest t283_l659 (is (true? v282_l657)))


(def v284_l661 (kind/doc #'el/exp))


(def v285_l663 (la/close-scalar? ((el/exp (t/column [0])) 0 0) 1.0))


(deftest t286_l665 (is (true? v285_l663)))


(def v287_l667 (kind/doc #'el/log))


(def
 v288_l669
 (la/close-scalar? ((el/log (t/column [math/E])) 0 0) 1.0))


(deftest t289_l671 (is (true? v288_l669)))


(def v290_l673 (kind/doc #'el/log10))


(def v291_l675 (la/close-scalar? ((el/log10 (t/column [100])) 0 0) 2.0))


(deftest t292_l677 (is (true? v291_l675)))


(def v293_l679 (kind/doc #'el/log1p))


(def v294_l681 ((el/log1p (t/column [0.0])) 0 0))


(deftest t295_l683 (is ((fn [v] (la/close-scalar? v 0.0)) v294_l681)))


(def v296_l685 (kind/doc #'el/expm1))


(def v297_l687 ((el/expm1 (t/column [0.0])) 0 0))


(deftest t298_l689 (is ((fn [v] (la/close-scalar? v 0.0)) v297_l687)))


(def v299_l691 (kind/doc #'el/sin))


(def
 v300_l693
 (la/close-scalar? ((el/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t301_l695 (is (true? v300_l693)))


(def v302_l697 (kind/doc #'el/cos))


(def v303_l699 (la/close-scalar? ((el/cos (t/column [0])) 0 0) 1.0))


(deftest t304_l701 (is (true? v303_l699)))


(def v305_l703 (kind/doc #'el/tan))


(def
 v306_l705
 (la/close-scalar? ((el/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t307_l707 (is (true? v306_l705)))


(def v308_l709 (kind/doc #'el/asin))


(def v309_l711 ((el/asin (t/column [0.5])) 0 0))


(deftest
 t310_l713
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v309_l711)))


(def v311_l715 (kind/doc #'el/acos))


(def v312_l717 ((el/acos (t/column [0.5])) 0 0))


(deftest
 t313_l719
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v312_l717)))


(def v314_l721 (kind/doc #'el/atan))


(def v315_l723 ((el/atan (t/column [1.0])) 0 0))


(deftest
 t316_l725
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v315_l723)))


(def v317_l727 (kind/doc #'el/sinh))


(def v318_l729 (la/close-scalar? ((el/sinh (t/column [0])) 0 0) 0.0))


(deftest t319_l731 (is (true? v318_l729)))


(def v320_l733 (kind/doc #'el/cosh))


(def v321_l735 (la/close-scalar? ((el/cosh (t/column [0])) 0 0) 1.0))


(deftest t322_l737 (is (true? v321_l735)))


(def v323_l739 (kind/doc #'el/tanh))


(def v324_l741 (la/close-scalar? ((el/tanh (t/column [0])) 0 0) 0.0))


(deftest t325_l743 (is (true? v324_l741)))


(def v326_l745 (kind/doc #'el/floor))


(def v327_l747 ((el/floor (t/column [2.7])) 0 0))


(deftest t328_l749 (is ((fn [v] (== 2.0 v)) v327_l747)))


(def v329_l751 (kind/doc #'el/ceil))


(def v330_l753 ((el/ceil (t/column [2.3])) 0 0))


(deftest t331_l755 (is ((fn [v] (== 3.0 v)) v330_l753)))


(def v332_l757 (kind/doc #'el/round))


(def v333_l759 ((el/round (t/column [2.7])) 0 0))


(deftest t334_l761 (is ((fn [v] (== 3.0 v)) v333_l759)))


(def v335_l763 (kind/doc #'el/clip))


(def v336_l765 (t/flatten (el/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t337_l767 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v336_l765)))


(def v338_l769 (kind/doc #'el/abs))


(def v339_l771 ((el/abs (t/column [-5])) 0 0))


(deftest t340_l773 (is ((fn [v] (== 5.0 v)) v339_l771)))


(def
 v342_l777
 (let
  [m (el/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t343_l780 (is (true? v342_l777)))


(def v344_l782 (kind/doc #'el/sum))


(def v345_l784 (el/sum (t/column [1 2 3 4])))


(deftest t346_l786 (is ((fn [v] (== 10.0 v)) v345_l784)))


(def
 v348_l790
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (el/sum ct)]
  [(el/re s) (el/im s)]))


(deftest t349_l794 (is (= v348_l790 [6.0 15.0])))


(def v350_l796 (kind/doc #'el/mean))


(def v351_l798 (el/mean (t/column [2 4 6])))


(deftest t352_l800 (is ((fn [v] (== 4.0 v)) v351_l798)))


(def v353_l802 (kind/doc #'el/reduce-*))


(def v354_l804 (el/reduce-* (t/column [2 3 4])))


(deftest t355_l806 (is ((fn [v] (== 24.0 v)) v354_l804)))


(def v356_l808 (kind/doc #'el/reduce-max))


(def v357_l810 (el/reduce-max (t/column [3 7 2 9 1])))


(deftest t358_l812 (is ((fn [v] (== 9.0 v)) v357_l810)))


(def v359_l814 (kind/doc #'el/reduce-min))


(def v360_l816 (el/reduce-min (t/column [3 7 2 9 1])))


(deftest t361_l818 (is ((fn [v] (== 1.0 v)) v360_l816)))


(def v362_l820 (kind/doc #'el/>))


(def v363_l822 (el/> (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t364_l824
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v363_l822)))


(def v365_l826 (kind/doc #'el/<))


(def v366_l828 (el/< (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t367_l830
 (is ((fn [v] (= [1.0 0.0 0.0] (t/flatten v))) v366_l828)))


(def v368_l832 (kind/doc #'el/>=))


(def v369_l834 (el/>= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t370_l836
 (is ((fn [v] (= [0.0 1.0 1.0] (t/flatten v))) v369_l834)))


(def v371_l838 (kind/doc #'el/<=))


(def v372_l840 (el/<= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t373_l842
 (is ((fn [v] (= [1.0 0.0 1.0] (t/flatten v))) v372_l840)))


(def v374_l844 (kind/doc #'el/eq))


(def v375_l846 (el/eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t376_l848
 (is ((fn [v] (= [0.0 0.0 1.0] (t/flatten v))) v375_l846)))


(def v377_l850 (kind/doc #'el/not-eq))


(def v378_l852 (el/not-eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t379_l854
 (is ((fn [v] (= [1.0 1.0 0.0] (t/flatten v))) v378_l852)))


(def v380_l856 (kind/doc #'el/min))


(def v381_l858 ((el/min (t/column [3]) (t/column [5])) 0 0))


(deftest t382_l860 (is ((fn [v] (== 3.0 v)) v381_l858)))


(def v383_l862 (kind/doc #'el/max))


(def v384_l864 ((el/max (t/column [3]) (t/column [5])) 0 0))


(deftest t385_l866 (is ((fn [v] (== 5.0 v)) v384_l864)))


(def v386_l868 (kind/doc #'el/argmax))


(def v387_l870 (el/argmax (t/column [3 7 2 9 1])))


(deftest t388_l872 (is ((fn [v] (== 3 v)) v387_l870)))


(def v389_l874 (kind/doc #'el/argmin))


(def v390_l876 (el/argmin (t/column [3 7 2 9 1])))


(deftest t391_l878 (is ((fn [v] (== 4 v)) v390_l876)))


(def v392_l880 (kind/doc #'el/argsort))


(def v394_l884 (el/argsort (t/column [3 7 2 9 1])))


(deftest t395_l886 (is ((fn [v] (= [4 2 0 1 3] v)) v394_l884)))


(def v397_l890 (el/argsort > (t/column [3 7 2 9 1])))


(deftest t398_l892 (is ((fn [v] (= [3 1 0 2 4] v)) v397_l890)))


(def v399_l894 (kind/doc #'el/sort))


(def v401_l898 (el/sort (t/column [3 7 2 9 1])))


(deftest
 t402_l900
 (is ((fn [v] (= [1.0 2.0 3.0 7.0 9.0] (t/flatten v))) v401_l898)))


(def v404_l904 (el/sort > (t/column [3 7 2 9 1])))


(deftest
 t405_l906
 (is ((fn [v] (= [9.0 7.0 3.0 2.0 1.0] (t/flatten v))) v404_l904)))


(def v407_l916 (kind/doc #'ft/dft-fwd))


(def
 v408_l918
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/dft-fwd signal)]
  (t/complex-shape spectrum)))


(deftest t409_l922 (is (= v408_l918 [4])))


(def v410_l924 (kind/doc #'ft/dft-inv))


(def
 v411_l926
 (let
  [spectrum
   (ft/dft-fwd [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/dft-inv spectrum)]
  (la/close-scalar? (el/re (roundtrip 0)) 1.0)))


(deftest t412_l930 (is (true? v411_l926)))


(def v413_l932 (kind/doc #'ft/dft-inv-real))


(def
 v414_l934
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dft-inv-real (ft/dft-fwd signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t415_l938 (is (true? v414_l934)))


(def v416_l940 (kind/doc #'ft/dft-fwd-complex))


(def
 v417_l942
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/dft-fwd-complex ct)]
  (t/complex-shape spectrum)))


(deftest t418_l946 (is (= v417_l942 [4])))


(def v419_l948 (kind/doc #'ft/dft-fwd-2d))


(def
 v420_l950
 (let
  [A (t/matrix [[1 2] [3 4]]) spectrum (ft/dft-fwd-2d A)]
  (t/complex-shape spectrum)))


(deftest t421_l954 (is (= v420_l950 [2 2])))


(def v422_l956 (kind/doc #'ft/dft-inv-2d))


(def
 v423_l958
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   roundtrip
   (ft/dft-inv-2d (ft/dft-fwd-2d A))]
  (la/close-scalar? (el/re ((roundtrip 0) 0)) 1.0)))


(deftest t424_l962 (is (true? v423_l958)))


(def v425_l964 (kind/doc #'ft/dft-inv-real-2d))


(def
 v426_l966
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   roundtrip
   (ft/dft-inv-real-2d (ft/dft-fwd-2d A))]
  (la/close? roundtrip A)))


(deftest t427_l970 (is (true? v426_l966)))


(def v428_l972 (kind/doc #'ft/dft-fwd-complex-2d))


(def
 v429_l974
 (let
  [ct
   (t/complex-tensor-real [[1 2] [3 4]])
   spectrum
   (ft/dft-fwd-complex-2d ct)]
  (t/complex-shape spectrum)))


(deftest t430_l978 (is (= v429_l974 [2 2])))


(def v431_l980 (kind/doc #'ft/dct-fwd))


(def v432_l982 (ft/dct-fwd [1.0 2.0 3.0 4.0]))


(deftest t433_l984 (is ((fn [v] (= 4 (count v))) v432_l982)))


(def v434_l986 (kind/doc #'ft/dct-inv))


(def
 v435_l988
 (let
  [signal [1.0 2.0 3.0 4.0] roundtrip (ft/dct-inv (ft/dct-fwd signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t436_l992 (is (true? v435_l988)))


(def v437_l994 (kind/doc #'ft/dst-fwd))


(def v438_l996 (ft/dst-fwd [1.0 2.0 3.0 4.0]))


(deftest t439_l998 (is ((fn [v] (= 4 (count v))) v438_l996)))


(def v440_l1000 (kind/doc #'ft/dst-inv))


(def
 v441_l1002
 (let
  [signal [1.0 2.0 3.0 4.0] roundtrip (ft/dst-inv (ft/dst-fwd signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t442_l1006 (is (true? v441_l1002)))


(def v443_l1008 (kind/doc #'ft/dht-fwd))


(def v444_l1010 (ft/dht-fwd [1.0 2.0 3.0 4.0]))


(deftest t445_l1012 (is ((fn [v] (= 4 (count v))) v444_l1010)))


(def v446_l1014 (kind/doc #'ft/dht-inv))


(def
 v447_l1016
 (let
  [signal [1.0 2.0 3.0 4.0] roundtrip (ft/dht-inv (ft/dht-fwd signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t448_l1020 (is (true? v447_l1016)))


(def v450_l1026 (kind/doc #'tape/memory-status))


(def v451_l1028 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t452_l1030 (is ((fn [s] (= :contiguous s)) v451_l1028)))


(def
 v453_l1032
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t454_l1034 (is ((fn [s] (= :strided s)) v453_l1032)))


(def v455_l1036 (tape/memory-status (el/+ (t/eye 2) (t/eye 2))))


(deftest t456_l1038 (is ((fn [s] (= :lazy s)) v455_l1036)))


(def v457_l1040 (kind/doc #'tape/memory-relation))


(def
 v458_l1042
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t459_l1045 (is ((fn [r] (= :shared r)) v458_l1042)))


(def
 v460_l1047
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t461_l1049 (is ((fn [r] (= :independent r)) v460_l1047)))


(def
 v462_l1051
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (el/+ (t/eye 2) (t/eye 2))))


(deftest t463_l1053 (is ((fn [r] (= :unknown-lazy r)) v462_l1051)))


(def v464_l1055 (kind/doc #'tape/with-tape))


(def
 v465_l1057
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (el/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v466_l1063 (select-keys tape-example [:result :entries]))


(deftest
 t467_l1065
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v466_l1063)))


(def v468_l1068 (kind/doc #'tape/summary))


(def v469_l1070 (tape/summary tape-example))


(deftest t470_l1072 (is ((fn [s] (= 4 (:total s))) v469_l1070)))


(def v471_l1074 (kind/doc #'tape/origin))


(def v472_l1076 (tape/origin tape-example (:result tape-example)))


(deftest t473_l1078 (is ((fn [dag] (= :la/mmul (:op dag))) v472_l1076)))


(def v474_l1080 (kind/doc #'tape/mermaid))


(def v476_l1084 (tape/mermaid tape-example (:result tape-example)))


(def v477_l1086 (kind/doc #'tape/detect-memory-status))


(def
 v479_l1091
 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t480_l1093
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v479_l1091)))


(def v482_l1099 (kind/doc #'grad/grad))


(def
 v483_l1101
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))]
  (la/close?
   (grad/grad tape-result (:result tape-result) A)
   (el/scale A 2))))


(deftest t484_l1108 (is (true? v483_l1101)))


(def v486_l1114 (kind/doc #'vis/arrow-plot))


(def
 v487_l1116
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 600}))


(def v488_l1120 (kind/doc #'vis/graph-plot))


(def
 v489_l1122
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 600, :labels ["A" "B" "C"]}))


(def v490_l1126 (kind/doc #'vis/matrix->gray-image))


(def
 v491_l1128
 (let
  [m
   (t/compute-tensor
    [200 200]
    (fn [r c] (* 255.0 (/ (+ r c) 400.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t492_l1133
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v491_l1128)))


(def v493_l1135 (kind/doc #'vis/extract-channel))


(def
 v494_l1137
 (let
  [img
   (t/compute-tensor
    [200 200 3]
    (fn
     [r c ch]
     (case (int ch) 0 (int (* 255 (/ r 200.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t495_l1143
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v494_l1137)))
