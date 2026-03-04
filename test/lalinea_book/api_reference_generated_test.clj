(ns
 lalinea-book.api-reference-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
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
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (t/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t48_l130 (is (true? v47_l126)))


(def v49_l132 (kind/doc #'t/zmat->complex-tensor))


(def
 v50_l134
 (let
  [zm
   (t/complex-tensor->zmat (t/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (t/zmat->complex-tensor zm)]
  (t/complex? ct)))


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


(def v66_l168 (kind/doc #'t/compute-tensor))


(def
 v67_l170
 (t/compute-tensor [2 3] (fn [i j] (+ (* 10.0 i) j)) :float64))


(deftest
 t68_l172
 (is ((fn [m] (and (= [2 3] (t/shape m)) (== 12.0 (m 1 2)))) v67_l170)))


(def v69_l175 (kind/doc #'t/shape))


(def v70_l177 (t/shape (t/matrix [[1 2 3] [4 5 6]])))


(deftest t71_l179 (is ((fn [s] (= [2 3] s)) v70_l177)))


(def v72_l181 (kind/doc #'t/reshape))


(def v73_l183 (t/reshape (t/matrix [[1 2] [3 4]]) [4]))


(deftest t74_l185 (is ((fn [v] (= [1.0 2.0 3.0 4.0] v)) v73_l183)))


(def v75_l187 (kind/doc #'t/select))


(def v77_l190 (t/select (t/matrix [[1 2] [3 4] [5 6]]) 0 :all))


(deftest t78_l192 (is ((fn [v] (= [1.0 2.0] v)) v77_l190)))


(def v79_l194 (kind/doc #'t/clone))


(def
 v81_l197
 (t/clone
  (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest
 t82_l200
 (is ((fn [m] (= [[11.0 22.0] [33.0 44.0]] m)) v81_l197)))


(def v83_l202 (kind/doc #'t/make-reader))


(def v84_l204 (t/make-reader :float64 5 (* idx idx)))


(deftest t85_l206 (is ((fn [r] (= 16.0 (r 4))) v84_l204)))


(def v86_l208 (kind/doc #'t/make-container))


(def v87_l210 (t/make-container :float64 4))


(deftest t88_l212 (is ((fn [c] (= 4 (count c))) v87_l210)))


(def v89_l214 (kind/doc #'t/elemwise-cast))


(def v90_l216 (t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32))


(deftest
 t91_l218
 (is
  ((fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))
   v90_l216)))


(def v92_l220 (kind/doc #'t/mset!))


(def
 v93_l222
 (let
  [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0)))


(deftest t94_l226 (is ((fn [v] (== 99.0 v)) v93_l222)))


(def v95_l228 (kind/doc #'t/set-value!))


(def
 v96_l230
 (let
  [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1)))


(deftest t97_l234 (is ((fn [v] (== 42.0 v)) v96_l230)))


(def v98_l236 (kind/doc #'t/->double-array))


(def
 v99_l238
 (let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))] (alength arr)))


(deftest t100_l241 (is ((fn [n] (= 4 n)) v99_l238)))


(def v101_l243 (kind/doc #'t/backing-array))


(def
 v102_l245
 (let
  [A (t/matrix [[1 2] [3 4]]) B (t/clone A)]
  [(some? (t/backing-array A))
   (identical? (t/backing-array A) (t/backing-array B))]))


(deftest t103_l251 (is (= v102_l245 [true false])))


(def v104_l253 (kind/doc #'t/->reader))


(def v105_l255 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t106_l258 (is ((fn [v] (== 30.0 v)) v105_l255)))


(def v107_l260 (kind/doc #'t/array-buffer))


(def v108_l262 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t109_l264 (is (true? v108_l262)))


(def v111_l271 (kind/doc #'la/add))


(def
 v112_l273
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t113_l276 (is ((fn [m] (== 11.0 (m 0 0))) v112_l273)))


(def v114_l278 (kind/doc #'la/sub))


(def
 v115_l280
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t116_l283 (is ((fn [m] (== 9.0 (m 0 0))) v115_l280)))


(def v117_l285 (kind/doc #'la/scale))


(def v118_l287 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t119_l289 (is ((fn [m] (== 6.0 (m 0 1))) v118_l287)))


(def v120_l291 (kind/doc #'la/mul))


(def
 v121_l293
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t122_l296
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v121_l293)))


(def v123_l299 (kind/doc #'la/abs))


(def v124_l301 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t125_l303 (is ((fn [m] (== 3.0 (m 0 0))) v124_l301)))


(def v126_l305 (kind/doc #'la/sq))


(def v127_l307 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t128_l309 (is ((fn [m] (== 4.0 (m 0 0))) v127_l307)))


(def v129_l311 (kind/doc #'la/sum))


(def v130_l313 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t131_l315 (is ((fn [v] (== 10.0 v)) v130_l313)))


(def v132_l317 (kind/doc #'la/prod))


(def v133_l319 (la/prod (t/matrix [2 3 4])))


(deftest t134_l321 (is ((fn [v] (== 24.0 v)) v133_l319)))


(def v135_l323 (kind/doc #'la/mmul))


(def v136_l325 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t137_l328
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v136_l325)))


(def v138_l331 (kind/doc #'la/transpose))


(def v139_l333 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t140_l335 (is ((fn [m] (= [3 2] (t/shape m))) v139_l333)))


(def v141_l337 (kind/doc #'la/trace))


(def v142_l339 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t143_l341 (is ((fn [v] (== 5.0 v)) v142_l339)))


(def v144_l343 (kind/doc #'la/det))


(def v145_l345 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t146_l347 (is ((fn [v] (la/close-scalar? v -2.0)) v145_l345)))


(def v147_l349 (kind/doc #'la/norm))


(def v148_l351 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t149_l353 (is ((fn [v] (la/close-scalar? v 5.0)) v148_l351)))


(def v150_l355 (kind/doc #'la/dot))


(def v151_l357 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t152_l359 (is ((fn [v] (== 32.0 v)) v151_l357)))


(def v153_l361 (kind/doc #'la/close?))


(def v154_l363 (la/close? (t/eye 2) (t/eye 2)))


(deftest t155_l365 (is (true? v154_l363)))


(def v156_l367 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t157_l369 (is (false? v156_l367)))


(def v158_l371 (kind/doc #'la/close-scalar?))


(def v159_l373 (la/close-scalar? 1.00000000001 1.0))


(deftest t160_l375 (is (true? v159_l373)))


(def v161_l377 (kind/doc #'la/invert))


(def
 v162_l379
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t163_l382 (is (true? v162_l379)))


(def v164_l384 (kind/doc #'la/solve))


(def
 v166_l387
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t167_l391
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v166_l387)))


(def v168_l394 (kind/doc #'la/eigen))


(def
 v169_l396
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t170_l400
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v169_l396)))


(def v171_l404 (kind/doc #'la/real-eigenvalues))


(def v172_l406 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t173_l408
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v172_l406)))


(def v174_l411 (kind/doc #'la/svd))


(def
 v175_l413
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t176_l418
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v175_l413)))


(def v177_l423 (kind/doc #'la/qr))


(def
 v178_l425
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t179_l428 (is (true? v178_l425)))


(def v180_l430 (kind/doc #'la/cholesky))


(def
 v181_l432
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t182_l436 (is (true? v181_l432)))


(def v183_l438 (kind/doc #'la/mpow))


(def v184_l440 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t185_l442
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v184_l440)))


(def v186_l444 (kind/doc #'la/rank))


(def v187_l446 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t188_l448 (is ((fn [r] (= 1 r)) v187_l446)))


(def v189_l450 (kind/doc #'la/condition-number))


(def v190_l452 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t191_l454 (is ((fn [v] (> v 1.0)) v190_l452)))


(def v192_l456 (kind/doc #'la/pinv))


(def
 v193_l458
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t194_l461 (is (true? v193_l458)))


(def v195_l463 (kind/doc #'la/lstsq))


(def
 v196_l465
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t197_l469
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v196_l465)))


(def v198_l471 (kind/doc #'la/null-space))


(def
 v199_l473
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t200_l477 (is (true? v199_l473)))


(def v201_l479 (kind/doc #'la/col-space))


(def
 v202_l481
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t203_l483 (is ((fn [r] (= 1 r)) v202_l481)))


(def v204_l485 (kind/doc #'la/lift))


(def v206_l488 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t207_l490
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v206_l488)))


(def v208_l493 (kind/doc #'la/lifted))


(def
 v210_l496
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t211_l499
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v210_l496)))


(def v213_l505 (kind/doc #'t/complex-tensor))


(def v214_l507 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t215_l509
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v214_l507)))


(def v216_l511 (kind/doc #'t/complex-tensor-real))


(def v217_l513 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t218_l515 (is ((fn [ct] (every? zero? (la/im ct))) v217_l513)))


(def v219_l517 (kind/doc #'t/complex))


(def v220_l519 (t/complex 3.0 4.0))


(deftest
 t221_l521
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (la/re ct)) (== 4.0 (la/im ct))))
   v220_l519)))


(def v222_l525 (kind/doc #'la/re))


(def v223_l527 (la/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t224_l529 (is (= v223_l527 [1.0 2.0])))


(def v225_l531 (kind/doc #'la/im))


(def v226_l533 (la/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t227_l535 (is (= v226_l533 [3.0 4.0])))


(def v228_l537 (kind/doc #'t/complex-shape))


(def
 v229_l539
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t230_l542 (is (= v229_l539 [2 2])))


(def v231_l544 (kind/doc #'t/scalar?))


(def v232_l546 (t/scalar? (t/complex 3.0 4.0)))


(deftest t233_l548 (is (true? v232_l546)))


(def v234_l550 (kind/doc #'t/complex?))


(def v235_l552 (t/complex? (t/complex 3.0 4.0)))


(deftest t236_l554 (is (true? v235_l552)))


(def v237_l556 (t/complex? (t/eye 2)))


(deftest t238_l558 (is (false? v237_l556)))


(def v239_l560 (kind/doc #'t/->tensor))


(def
 v240_l562
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t241_l564 (is (= v240_l562 [2 2])))


(def v242_l566 (kind/doc #'t/->double-array))


(def
 v243_l568
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t244_l571 (is (= v243_l568 [1.0 3.0 2.0 4.0])))


(def v245_l573 (kind/doc #'t/wrap-tensor))


(def
 v246_l575
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t247_l579
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v246_l575)))


(def
 v249_l586
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (la/re (la/add a b))))


(deftest t250_l590 (is (= v249_l586 [11.0 22.0])))


(def
 v252_l594
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (la/mul a b)]
  [(la/re (c 0)) (la/im (c 0))]))


(deftest t253_l599 (is (= v252_l594 [-10.0 10.0])))


(def v255_l603 (kind/doc #'la/conj))


(def
 v256_l605
 (let
  [ct (la/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (la/im ct)))


(deftest t257_l608 (is (= v256_l605 [-3.0 4.0])))


(def v259_l612 (kind/doc #'la/dot-conj))


(def
 v260_l614
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (la/re result) 30.0)))


(deftest t261_l618 (is (true? v260_l614)))


(def
 v263_l622
 (let
  [m (la/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t264_l625 (is (true? v263_l622)))


(def
 v266_l629
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (la/sum ct)]
  [(la/re s) (la/im s)]))


(deftest t267_l633 (is (= v266_l629 [6.0 15.0])))


(def v269_l639 (kind/doc #'ft/forward))


(def
 v270_l641
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t271_l645 (is (= v270_l641 [4])))


(def v272_l647 (kind/doc #'ft/inverse))


(def
 v273_l649
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (la/re (roundtrip 0)) 1.0)))


(deftest t274_l653 (is (true? v273_l649)))


(def v275_l655 (kind/doc #'ft/inverse-real))


(def
 v276_l657
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t277_l661 (is (true? v276_l657)))


(def v278_l663 (kind/doc #'ft/forward-complex))


(def
 v279_l665
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t280_l669 (is (= v279_l665 [4])))


(def v281_l671 (kind/doc #'ft/dct-forward))


(def v282_l673 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t283_l675 (is ((fn [v] (= 4 (count v))) v282_l673)))


(def v284_l677 (kind/doc #'ft/dct-inverse))


(def
 v285_l679
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t286_l683 (is (true? v285_l679)))


(def v287_l685 (kind/doc #'ft/dst-forward))


(def v288_l687 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t289_l689 (is ((fn [v] (= 4 (count v))) v288_l687)))


(def v290_l691 (kind/doc #'ft/dst-inverse))


(def
 v291_l693
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t292_l697 (is (true? v291_l693)))


(def v293_l699 (kind/doc #'ft/dht-forward))


(def v294_l701 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t295_l703 (is ((fn [v] (= 4 (count v))) v294_l701)))


(def v296_l705 (kind/doc #'ft/dht-inverse))


(def
 v297_l707
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t298_l711 (is (true? v297_l707)))


(def v300_l717 (kind/doc #'tape/memory-status))


(def v301_l719 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t302_l721 (is ((fn [s] (= :contiguous s)) v301_l719)))


(def
 v303_l723
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t304_l725 (is ((fn [s] (= :strided s)) v303_l723)))


(def v305_l727 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t306_l729 (is ((fn [s] (= :lazy s)) v305_l727)))


(def v307_l731 (kind/doc #'tape/memory-relation))


(def
 v308_l733
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t309_l736 (is ((fn [r] (= :shared r)) v308_l733)))


(def
 v310_l738
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t311_l740 (is ((fn [r] (= :independent r)) v310_l738)))


(def
 v312_l742
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t313_l744 (is ((fn [r] (= :unknown-lazy r)) v312_l742)))


(def v314_l746 (kind/doc #'tape/with-tape))


(def
 v315_l748
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v316_l754 (select-keys tape-example [:result :entries]))


(deftest
 t317_l756
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v316_l754)))


(def v318_l759 (kind/doc #'tape/summary))


(def v319_l761 (tape/summary tape-example))


(deftest t320_l763 (is ((fn [s] (= 4 (:total s))) v319_l761)))


(def v321_l765 (kind/doc #'tape/origin))


(def v322_l767 (tape/origin tape-example (:result tape-example)))


(deftest t323_l769 (is ((fn [dag] (= :la/mmul (:op dag))) v322_l767)))


(def v324_l771 (kind/doc #'tape/mermaid))


(def v326_l775 (tape/mermaid tape-example (:result tape-example)))


(def v327_l777 (kind/doc #'tape/detect-memory-status))


(def v329_l782 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t330_l784
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v329_l782)))


(def v332_l792 (kind/doc #'elem/sq))


(def v333_l794 (elem/sq (t/column [2 3 4])))


(deftest
 t334_l796
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v333_l794)))


(def v335_l798 (kind/doc #'elem/sqrt))


(def v336_l800 (elem/sqrt (t/column [4 9 16])))


(deftest
 t337_l802
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v336_l800)))


(def v338_l804 (kind/doc #'elem/exp))


(def v339_l806 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t340_l808 (is (true? v339_l806)))


(def v341_l810 (kind/doc #'elem/log))


(def
 v342_l812
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t343_l814 (is (true? v342_l812)))


(def v344_l816 (kind/doc #'elem/log10))


(def
 v345_l818
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t346_l820 (is (true? v345_l818)))


(def v347_l822 (kind/doc #'elem/sin))


(def
 v348_l824
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t349_l826 (is (true? v348_l824)))


(def v350_l828 (kind/doc #'elem/cos))


(def v351_l830 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t352_l832 (is (true? v351_l830)))


(def v353_l834 (kind/doc #'elem/tan))


(def
 v354_l836
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t355_l838 (is (true? v354_l836)))


(def v356_l840 (kind/doc #'elem/sinh))


(def v357_l842 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t358_l844 (is (true? v357_l842)))


(def v359_l846 (kind/doc #'elem/cosh))


(def v360_l848 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t361_l850 (is (true? v360_l848)))


(def v362_l852 (kind/doc #'elem/tanh))


(def v363_l854 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t364_l856 (is (true? v363_l854)))


(def v365_l858 (kind/doc #'elem/abs))


(def v366_l860 ((elem/abs (t/column [-5])) 0 0))


(deftest t367_l862 (is ((fn [v] (== 5.0 v)) v366_l860)))


(def v368_l864 (kind/doc #'elem/sum))


(def v369_l866 (elem/sum (t/column [1 2 3 4])))


(deftest t370_l868 (is ((fn [v] (== 10.0 v)) v369_l866)))


(def v371_l870 (kind/doc #'elem/mean))


(def v372_l872 (elem/mean (t/column [2 4 6])))


(deftest t373_l874 (is ((fn [v] (== 4.0 v)) v372_l872)))


(def v374_l876 (kind/doc #'elem/pow))


(def v375_l878 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t376_l880 (is ((fn [v] (== 8.0 v)) v375_l878)))


(def v377_l882 (kind/doc #'elem/cbrt))


(def v378_l884 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t379_l886 (is (true? v378_l884)))


(def v380_l888 (kind/doc #'elem/floor))


(def v381_l890 ((elem/floor (t/column [2.7])) 0 0))


(deftest t382_l892 (is ((fn [v] (== 2.0 v)) v381_l890)))


(def v383_l894 (kind/doc #'elem/ceil))


(def v384_l896 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t385_l898 (is ((fn [v] (== 3.0 v)) v384_l896)))


(def v386_l900 (kind/doc #'elem/min))


(def v387_l902 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t388_l904 (is ((fn [v] (== 3.0 v)) v387_l902)))


(def v389_l906 (kind/doc #'elem/max))


(def v390_l908 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t391_l910 (is ((fn [v] (== 5.0 v)) v390_l908)))


(def v392_l912 (kind/doc #'elem/asin))


(def v393_l914 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t394_l916
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v393_l914)))


(def v395_l918 (kind/doc #'elem/acos))


(def v396_l920 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t397_l922
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v396_l920)))


(def v398_l924 (kind/doc #'elem/atan))


(def v399_l926 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t400_l928
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v399_l926)))


(def v401_l930 (kind/doc #'elem/log1p))


(def v402_l932 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t403_l934 (is ((fn [v] (la/close-scalar? v 0.0)) v402_l932)))


(def v404_l936 (kind/doc #'elem/expm1))


(def v405_l938 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t406_l940 (is ((fn [v] (la/close-scalar? v 0.0)) v405_l938)))


(def v407_l942 (kind/doc #'elem/round))


(def v408_l944 ((elem/round (t/column [2.7])) 0 0))


(deftest t409_l946 (is ((fn [v] (== 3.0 v)) v408_l944)))


(def v410_l948 (kind/doc #'elem/clip))


(def v411_l950 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t412_l952 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v411_l950)))


(def v413_l954 (kind/doc #'elem/div))


(def v414_l956 (elem/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t415_l958
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v414_l956)))


(def v416_l960 (kind/doc #'elem/gt))


(def v417_l962 (elem/gt (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t418_l964
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v417_l962)))


(def v419_l966 (kind/doc #'elem/reduce-max))


(def v420_l968 (elem/reduce-max (t/column [3 7 2 9 1])))


(deftest t421_l970 (is ((fn [v] (== 9.0 v)) v420_l968)))


(def v422_l972 (kind/doc #'elem/reduce-min))


(def v423_l974 (elem/reduce-min (t/column [3 7 2 9 1])))


(deftest t424_l976 (is ((fn [v] (== 1.0 v)) v423_l974)))


(def v426_l982 (kind/doc #'grad/grad))


(def
 v427_l984
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t428_l990 (is (true? v427_l984)))


(def v430_l996 (kind/doc #'vis/arrow-plot))


(def
 v431_l998
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v432_l1002 (kind/doc #'vis/graph-plot))


(def
 v433_l1004
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v434_l1008 (kind/doc #'vis/matrix->gray-image))


(def
 v435_l1010
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t436_l1015
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v435_l1010)))


(def v437_l1017 (kind/doc #'vis/extract-channel))


(def
 v438_l1019
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t439_l1025
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v438_l1019)))
