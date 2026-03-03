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


(def v12_l50 (kind/doc #'t/diag))


(def v13_l52 (t/diag [3 5 7]))


(deftest
 t14_l54
 (is
  ((fn
    [m]
    (and (= [3 3] (t/shape m)) (== 5.0 (m 1 1)) (== 0.0 (m 0 1))))
   v13_l52)))


(def v16_l60 (t/diag (t/matrix [[1 2 3] [4 5 6] [7 8 9]])))


(deftest t17_l62 (is ((fn [v] (= [1.0 5.0 9.0] v)) v16_l60)))


(def v18_l64 (kind/doc #'t/column))


(def v19_l66 (t/column [1 2 3]))


(deftest t20_l68 (is ((fn [v] (= [3 1] (t/shape v))) v19_l66)))


(def v21_l70 (kind/doc #'t/row))


(def v22_l72 (t/row [1 2 3]))


(deftest t23_l74 (is ((fn [v] (= [1 3] (t/shape v))) v22_l72)))


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


(def v66_l169 (kind/doc #'t/compute-tensor))


(def
 v67_l171
 (t/compute-tensor [2 3] (fn [i j] (+ (* 10.0 i) j)) :float64))


(deftest
 t68_l173
 (is ((fn [m] (and (= [2 3] (t/shape m)) (== 12.0 (m 1 2)))) v67_l171)))


(def v69_l176 (kind/doc #'t/shape))


(def v70_l178 (t/shape (t/matrix [[1 2 3] [4 5 6]])))


(deftest t71_l180 (is ((fn [s] (= [2 3] s)) v70_l178)))


(def v72_l182 (kind/doc #'t/reshape))


(def v73_l184 (t/reshape (t/matrix [[1 2] [3 4]]) [4]))


(deftest t74_l186 (is ((fn [v] (= [1.0 2.0 3.0 4.0] v)) v73_l184)))


(def v75_l188 (kind/doc #'t/select))


(def v77_l191 (t/select (t/matrix [[1 2] [3 4] [5 6]]) 0 :all))


(deftest t78_l193 (is ((fn [v] (= [1.0 2.0] v)) v77_l191)))


(def v79_l195 (kind/doc #'t/clone))


(def
 v81_l198
 (t/clone
  (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest
 t82_l201
 (is ((fn [m] (= [[11.0 22.0] [33.0 44.0]] m)) v81_l198)))


(def v83_l203 (kind/doc #'t/make-reader))


(def v84_l205 (t/make-reader :float64 5 (* idx idx)))


(deftest t85_l207 (is ((fn [r] (= 16.0 (r 4))) v84_l205)))


(def v86_l209 (kind/doc #'t/make-container))


(def v87_l211 (t/make-container :float64 4))


(deftest t88_l213 (is ((fn [c] (= 4 (count c))) v87_l211)))


(def v89_l215 (kind/doc #'t/elemwise-cast))


(def v90_l217 (t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32))


(deftest
 t91_l219
 (is
  ((fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))
   v90_l217)))


(def v92_l221 (kind/doc #'t/mset!))


(def
 v93_l223
 (let
  [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0)))


(deftest t94_l227 (is ((fn [v] (== 99.0 v)) v93_l223)))


(def v95_l229 (kind/doc #'t/set-value!))


(def
 v96_l231
 (let
  [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1)))


(deftest t97_l235 (is ((fn [v] (== 42.0 v)) v96_l231)))


(def v98_l237 (kind/doc #'t/->double-array))


(def
 v99_l239
 (let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))] (alength arr)))


(deftest t100_l242 (is ((fn [n] (= 4 n)) v99_l239)))


(def v101_l244 (kind/doc #'t/->reader))


(def v102_l246 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t103_l249 (is ((fn [v] (== 30.0 v)) v102_l246)))


(def v104_l251 (kind/doc #'t/array-buffer))


(def v105_l253 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t106_l255 (is (true? v105_l253)))


(def v108_l258 (kind/doc #'la/add))


(def
 v109_l260
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t110_l263 (is ((fn [m] (== 11.0 (m 0 0))) v109_l260)))


(def v111_l265 (kind/doc #'la/sub))


(def
 v112_l267
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t113_l270 (is ((fn [m] (== 9.0 (m 0 0))) v112_l267)))


(def v114_l272 (kind/doc #'la/scale))


(def v115_l274 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t116_l276 (is ((fn [m] (== 6.0 (m 0 1))) v115_l274)))


(def v117_l278 (kind/doc #'la/mul))


(def
 v118_l280
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t119_l283
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v118_l280)))


(def v120_l286 (kind/doc #'la/abs))


(def v121_l288 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t122_l290 (is ((fn [m] (== 3.0 (m 0 0))) v121_l288)))


(def v123_l292 (kind/doc #'la/sq))


(def v124_l294 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t125_l296 (is ((fn [m] (== 4.0 (m 0 0))) v124_l294)))


(def v126_l298 (kind/doc #'la/sum))


(def v127_l300 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t128_l302 (is ((fn [v] (== 10.0 v)) v127_l300)))


(def v129_l304 (kind/doc #'la/prod))


(def v130_l306 (la/prod (t/matrix [2 3 4])))


(deftest t131_l308 (is ((fn [v] (== 24.0 v)) v130_l306)))


(def v132_l310 (kind/doc #'la/mmul))


(def v133_l312 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t134_l315
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v133_l312)))


(def v135_l318 (kind/doc #'la/transpose))


(def v136_l320 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t137_l322 (is ((fn [m] (= [3 2] (t/shape m))) v136_l320)))


(def v138_l324 (kind/doc #'la/trace))


(def v139_l326 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t140_l328 (is ((fn [v] (== 5.0 v)) v139_l326)))


(def v141_l330 (kind/doc #'la/det))


(def v142_l332 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t143_l334 (is ((fn [v] (la/close-scalar? v -2.0)) v142_l332)))


(def v144_l336 (kind/doc #'la/norm))


(def v145_l338 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t146_l340 (is ((fn [v] (la/close-scalar? v 5.0)) v145_l338)))


(def v147_l342 (kind/doc #'la/dot))


(def v148_l344 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t149_l346 (is ((fn [v] (== 32.0 v)) v148_l344)))


(def v150_l348 (kind/doc #'la/close?))


(def v151_l350 (la/close? (t/eye 2) (t/eye 2)))


(deftest t152_l352 (is (true? v151_l350)))


(def v153_l354 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t154_l356 (is (false? v153_l354)))


(def v155_l358 (kind/doc #'la/close-scalar?))


(def v156_l360 (la/close-scalar? 1.00000000001 1.0))


(deftest t157_l362 (is (true? v156_l360)))


(def v158_l364 (kind/doc #'la/invert))


(def
 v159_l366
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t160_l369 (is (true? v159_l366)))


(def v161_l371 (kind/doc #'la/solve))


(def
 v163_l374
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t164_l378
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v163_l374)))


(def v165_l381 (kind/doc #'la/eigen))


(def
 v166_l383
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t167_l387
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v166_l383)))


(def v168_l391 (kind/doc #'la/real-eigenvalues))


(def v169_l393 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t170_l395
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v169_l393)))


(def v171_l398 (kind/doc #'la/svd))


(def
 v172_l400
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t173_l405
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v172_l400)))


(def v174_l410 (kind/doc #'la/qr))


(def
 v175_l412
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t176_l415 (is (true? v175_l412)))


(def v177_l417 (kind/doc #'la/cholesky))


(def
 v178_l419
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t179_l423 (is (true? v178_l419)))


(def v180_l425 (kind/doc #'la/mpow))


(def v181_l427 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t182_l429
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v181_l427)))


(def v183_l431 (kind/doc #'la/rank))


(def v184_l433 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t185_l435 (is ((fn [r] (= 1 r)) v184_l433)))


(def v186_l437 (kind/doc #'la/condition-number))


(def v187_l439 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t188_l441 (is ((fn [v] (> v 1.0)) v187_l439)))


(def v189_l443 (kind/doc #'la/pinv))


(def
 v190_l445
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t191_l448 (is (true? v190_l445)))


(def v192_l450 (kind/doc #'la/lstsq))


(def
 v193_l452
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t194_l456
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v193_l452)))


(def v195_l458 (kind/doc #'la/null-space))


(def
 v196_l460
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t197_l464 (is (true? v196_l460)))


(def v198_l466 (kind/doc #'la/col-space))


(def
 v199_l468
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t200_l470 (is ((fn [r] (= 1 r)) v199_l468)))


(def v201_l472 (kind/doc #'la/lift))


(def v203_l475 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t204_l477
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v203_l475)))


(def v205_l480 (kind/doc #'la/lifted))


(def
 v207_l483
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t208_l486
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v207_l483)))


(def v210_l493 (kind/doc #'cx/complex-tensor))


(def v211_l495 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t212_l497
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v211_l495)))


(def v213_l499 (kind/doc #'cx/complex-tensor-real))


(def v214_l501 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t215_l503 (is ((fn [ct] (every? zero? (cx/im ct))) v214_l501)))


(def v216_l505 (kind/doc #'cx/complex))


(def v217_l507 (cx/complex 3.0 4.0))


(deftest
 t218_l509
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v217_l507)))


(def v219_l513 (kind/doc #'cx/re))


(def v220_l515 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t221_l517 (is (= v220_l515 [1.0 2.0])))


(def v222_l519 (kind/doc #'cx/im))


(def v223_l521 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t224_l523 (is (= v223_l521 [3.0 4.0])))


(def v225_l525 (kind/doc #'cx/complex-shape))


(def
 v226_l527
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t227_l530 (is (= v226_l527 [2 2])))


(def v228_l532 (kind/doc #'cx/scalar?))


(def v229_l534 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t230_l536 (is (true? v229_l534)))


(def v231_l538 (kind/doc #'cx/complex?))


(def v232_l540 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t233_l542 (is (true? v232_l540)))


(def v234_l544 (cx/complex? (t/eye 2)))


(deftest t235_l546 (is (false? v234_l544)))


(def v236_l548 (kind/doc #'cx/->tensor))


(def
 v237_l550
 (t/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t238_l552 (is (= v237_l550 [2 2])))


(def v239_l554 (kind/doc #'cx/->double-array))


(def
 v240_l556
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t241_l559 (is (= v240_l556 [1.0 3.0 2.0 4.0])))


(def v242_l561 (kind/doc #'cx/wrap-tensor))


(def
 v243_l563
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t244_l567
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v243_l563)))


(def v245_l569 (kind/doc #'cx/add))


(def
 v246_l571
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t247_l575 (is (= v246_l571 [11.0 22.0])))


(def v248_l577 (kind/doc #'cx/sub))


(def
 v249_l579
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t250_l583 (is (= v249_l579 [9.0 18.0])))


(def v251_l585 (kind/doc #'cx/scale))


(def
 v252_l587
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t253_l590 (is (= v252_l587 [[2.0 4.0] [6.0 8.0]])))


(def v254_l592 (kind/doc #'cx/mul))


(def
 v256_l595
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t257_l600 (is (= v256_l595 [-10.0 10.0])))


(def v258_l602 (kind/doc #'cx/conj))


(def
 v259_l604
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t260_l607 (is (= v259_l604 [-3.0 4.0])))


(def v261_l609 (kind/doc #'cx/abs))


(def
 v263_l612
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t264_l615 (is (true? v263_l612)))


(def v265_l617 (kind/doc #'cx/dot))


(def
 v266_l619
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t267_l624 (is (true? v266_l619)))


(def v268_l626 (kind/doc #'cx/dot-conj))


(def
 v270_l629
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t271_l633 (is (true? v270_l629)))


(def v272_l635 (kind/doc #'cx/sum))


(def
 v273_l637
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t274_l641 (is (= v273_l637 [6.0 15.0])))


(def v276_l648 (kind/doc #'ft/forward))


(def
 v277_l650
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t278_l654 (is (= v277_l650 [4])))


(def v279_l656 (kind/doc #'ft/inverse))


(def
 v280_l658
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t281_l662 (is (true? v280_l658)))


(def v282_l664 (kind/doc #'ft/inverse-real))


(def
 v283_l666
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t284_l670 (is (true? v283_l666)))


(def v285_l672 (kind/doc #'ft/forward-complex))


(def
 v286_l674
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t287_l678 (is (= v286_l674 [4])))


(def v288_l680 (kind/doc #'ft/dct-forward))


(def v289_l682 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t290_l684 (is ((fn [v] (= 4 (count v))) v289_l682)))


(def v291_l686 (kind/doc #'ft/dct-inverse))


(def
 v292_l688
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t293_l692 (is (true? v292_l688)))


(def v294_l694 (kind/doc #'ft/dst-forward))


(def v295_l696 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t296_l698 (is ((fn [v] (= 4 (count v))) v295_l696)))


(def v297_l700 (kind/doc #'ft/dst-inverse))


(def
 v298_l702
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t299_l706 (is (true? v298_l702)))


(def v300_l708 (kind/doc #'ft/dht-forward))


(def v301_l710 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t302_l712 (is ((fn [v] (= 4 (count v))) v301_l710)))


(def v303_l714 (kind/doc #'ft/dht-inverse))


(def
 v304_l716
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t305_l720 (is (true? v304_l716)))


(def v307_l726 (kind/doc #'tape/memory-status))


(def v308_l728 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t309_l730 (is ((fn [s] (= :contiguous s)) v308_l728)))


(def
 v310_l732
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t311_l734 (is ((fn [s] (= :strided s)) v310_l732)))


(def v312_l736 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t313_l738 (is ((fn [s] (= :lazy s)) v312_l736)))


(def v314_l740 (kind/doc #'tape/memory-relation))


(def
 v315_l742
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t316_l745 (is ((fn [r] (= :shared r)) v315_l742)))


(def
 v317_l747
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t318_l749 (is ((fn [r] (= :independent r)) v317_l747)))


(def
 v319_l751
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t320_l753 (is ((fn [r] (= :unknown-lazy r)) v319_l751)))


(def v321_l755 (kind/doc #'tape/with-tape))


(def
 v322_l757
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v323_l763 (select-keys tape-example [:result :entries]))


(deftest
 t324_l765
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v323_l763)))


(def v325_l768 (kind/doc #'tape/summary))


(def v326_l770 (tape/summary tape-example))


(deftest t327_l772 (is ((fn [s] (= 4 (:total s))) v326_l770)))


(def v328_l774 (kind/doc #'tape/origin))


(def v329_l776 (tape/origin tape-example (:result tape-example)))


(deftest t330_l778 (is ((fn [dag] (= :la/mmul (:op dag))) v329_l776)))


(def v331_l780 (kind/doc #'tape/mermaid))


(def v333_l784 (tape/mermaid tape-example (:result tape-example)))


(def v334_l786 (kind/doc #'tape/detect-memory-status))


(def v336_l791 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t337_l793
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v336_l791)))


(def v339_l801 (kind/doc #'elem/sq))


(def v340_l803 (elem/sq (t/column [2 3 4])))


(deftest
 t341_l805
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v340_l803)))


(def v342_l807 (kind/doc #'elem/sqrt))


(def v343_l809 (elem/sqrt (t/column [4 9 16])))


(deftest
 t344_l811
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v343_l809)))


(def v345_l813 (kind/doc #'elem/exp))


(def v346_l815 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t347_l817 (is (true? v346_l815)))


(def v348_l819 (kind/doc #'elem/log))


(def
 v349_l821
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t350_l823 (is (true? v349_l821)))


(def v351_l825 (kind/doc #'elem/log10))


(def
 v352_l827
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t353_l829 (is (true? v352_l827)))


(def v354_l831 (kind/doc #'elem/sin))


(def
 v355_l833
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t356_l835 (is (true? v355_l833)))


(def v357_l837 (kind/doc #'elem/cos))


(def v358_l839 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t359_l841 (is (true? v358_l839)))


(def v360_l843 (kind/doc #'elem/tan))


(def
 v361_l845
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t362_l847 (is (true? v361_l845)))


(def v363_l849 (kind/doc #'elem/sinh))


(def v364_l851 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t365_l853 (is (true? v364_l851)))


(def v366_l855 (kind/doc #'elem/cosh))


(def v367_l857 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t368_l859 (is (true? v367_l857)))


(def v369_l861 (kind/doc #'elem/tanh))


(def v370_l863 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t371_l865 (is (true? v370_l863)))


(def v372_l867 (kind/doc #'elem/abs))


(def v373_l869 ((elem/abs (t/column [-5])) 0 0))


(deftest t374_l871 (is ((fn [v] (== 5.0 v)) v373_l869)))


(def v375_l873 (kind/doc #'elem/sum))


(def v376_l875 (elem/sum (t/column [1 2 3 4])))


(deftest t377_l877 (is ((fn [v] (== 10.0 v)) v376_l875)))


(def v378_l879 (kind/doc #'elem/mean))


(def v379_l881 (elem/mean (t/column [2 4 6])))


(deftest t380_l883 (is ((fn [v] (== 4.0 v)) v379_l881)))


(def v381_l885 (kind/doc #'elem/pow))


(def v382_l887 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t383_l889 (is ((fn [v] (== 8.0 v)) v382_l887)))


(def v384_l891 (kind/doc #'elem/cbrt))


(def v385_l893 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t386_l895 (is (true? v385_l893)))


(def v387_l897 (kind/doc #'elem/floor))


(def v388_l899 ((elem/floor (t/column [2.7])) 0 0))


(deftest t389_l901 (is ((fn [v] (== 2.0 v)) v388_l899)))


(def v390_l903 (kind/doc #'elem/ceil))


(def v391_l905 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t392_l907 (is ((fn [v] (== 3.0 v)) v391_l905)))


(def v393_l909 (kind/doc #'elem/min))


(def v394_l911 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t395_l913 (is ((fn [v] (== 3.0 v)) v394_l911)))


(def v396_l915 (kind/doc #'elem/max))


(def v397_l917 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t398_l919 (is ((fn [v] (== 5.0 v)) v397_l917)))


(def v399_l921 (kind/doc #'elem/asin))


(def v400_l923 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t401_l925
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v400_l923)))


(def v402_l927 (kind/doc #'elem/acos))


(def v403_l929 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t404_l931
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v403_l929)))


(def v405_l933 (kind/doc #'elem/atan))


(def v406_l935 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t407_l937
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v406_l935)))


(def v408_l939 (kind/doc #'elem/log1p))


(def v409_l941 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t410_l943 (is ((fn [v] (la/close-scalar? v 0.0)) v409_l941)))


(def v411_l945 (kind/doc #'elem/expm1))


(def v412_l947 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t413_l949 (is ((fn [v] (la/close-scalar? v 0.0)) v412_l947)))


(def v414_l951 (kind/doc #'elem/round))


(def v415_l953 ((elem/round (t/column [2.7])) 0 0))


(deftest t416_l955 (is ((fn [v] (== 3.0 v)) v415_l953)))


(def v417_l957 (kind/doc #'elem/clip))


(def v418_l959 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t419_l961 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v418_l959)))


(def v420_l963 (kind/doc #'elem/div))


(def v421_l965 (elem/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t422_l967
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v421_l965)))


(def v423_l969 (kind/doc #'elem/gt))


(def v424_l971 (elem/gt (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t425_l973
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v424_l971)))


(def v426_l975 (kind/doc #'elem/reduce-max))


(def v427_l977 (elem/reduce-max (t/column [3 7 2 9 1])))


(deftest t428_l979 (is ((fn [v] (== 9.0 v)) v427_l977)))


(def v429_l981 (kind/doc #'elem/reduce-min))


(def v430_l983 (elem/reduce-min (t/column [3 7 2 9 1])))


(deftest t431_l985 (is ((fn [v] (== 1.0 v)) v430_l983)))


(def v433_l991 (kind/doc #'grad/grad))


(def
 v434_l993
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t435_l999 (is (true? v434_l993)))


(def v437_l1005 (kind/doc #'vis/arrow-plot))


(def
 v438_l1007
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v439_l1011 (kind/doc #'vis/graph-plot))


(def
 v440_l1013
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v441_l1017 (kind/doc #'vis/matrix->gray-image))


(def
 v442_l1019
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t443_l1024
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v442_l1019)))


(def v444_l1026 (kind/doc #'vis/extract-channel))


(def
 v445_l1028
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t446_l1034
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v445_l1028)))
