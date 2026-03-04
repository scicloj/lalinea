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


(def v73_l184 (t/shape (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t74_l186 (is (= v73_l184 [2])))


(def v75_l188 (kind/doc #'t/reshape))


(def v76_l190 (t/reshape (t/matrix [[1 2] [3 4]]) [4]))


(deftest t77_l192 (is ((fn [v] (= [1.0 2.0 3.0 4.0] v)) v76_l190)))


(def v78_l194 (kind/doc #'t/select))


(def v80_l197 (t/select (t/matrix [[1 2] [3 4] [5 6]]) 0 :all))


(deftest t81_l199 (is ((fn [v] (= [1.0 2.0] v)) v80_l197)))


(def v82_l201 (kind/doc #'t/clone))


(def
 v84_l204
 (t/clone
  (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest
 t85_l207
 (is ((fn [m] (= [[11.0 22.0] [33.0 44.0]] m)) v84_l204)))


(def v86_l209 (kind/doc #'t/make-reader))


(def v87_l211 (t/make-reader :float64 5 (* idx idx)))


(deftest t88_l213 (is ((fn [r] (= 16.0 (r 4))) v87_l211)))


(def v89_l215 (kind/doc #'t/make-container))


(def v90_l217 (t/make-container :float64 4))


(deftest t91_l219 (is ((fn [c] (= 4 (count c))) v90_l217)))


(def v92_l221 (kind/doc #'t/elemwise-cast))


(def v93_l223 (t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32))


(deftest
 t94_l225
 (is
  ((fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))
   v93_l223)))


(def v95_l227 (kind/doc #'t/mset!))


(def
 v96_l229
 (let
  [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0)))


(deftest t97_l233 (is ((fn [v] (== 99.0 v)) v96_l229)))


(def v98_l235 (kind/doc #'t/set-value!))


(def
 v99_l237
 (let
  [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1)))


(deftest t100_l241 (is ((fn [v] (== 42.0 v)) v99_l237)))


(def v101_l243 (kind/doc #'t/->double-array))


(def
 v102_l245
 (let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))] (alength arr)))


(deftest t103_l248 (is ((fn [n] (= 4 n)) v102_l245)))


(def v104_l250 (kind/doc #'t/backing-array))


(def
 v105_l252
 (let
  [A (t/matrix [[1 2] [3 4]]) B (t/clone A)]
  [(some? (t/backing-array A))
   (identical? (t/backing-array A) (t/backing-array B))]))


(deftest t106_l258 (is (= v105_l252 [true false])))


(def v107_l260 (kind/doc #'t/->reader))


(def v108_l262 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t109_l265 (is ((fn [v] (== 30.0 v)) v108_l262)))


(def v110_l267 (kind/doc #'t/array-buffer))


(def v111_l269 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t112_l271 (is (true? v111_l269)))


(def v114_l278 (kind/doc #'la/add))


(def
 v115_l280
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t116_l283 (is ((fn [m] (== 11.0 (m 0 0))) v115_l280)))


(def v117_l285 (kind/doc #'la/sub))


(def
 v118_l287
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t119_l290 (is ((fn [m] (== 9.0 (m 0 0))) v118_l287)))


(def v120_l292 (kind/doc #'la/scale))


(def v121_l294 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t122_l296 (is ((fn [m] (== 6.0 (m 0 1))) v121_l294)))


(def v123_l298 (kind/doc #'la/mul))


(def
 v124_l300
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t125_l303
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v124_l300)))


(def v126_l306 (kind/doc #'la/abs))


(def v127_l308 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t128_l310 (is ((fn [m] (== 3.0 (m 0 0))) v127_l308)))


(def v129_l312 (kind/doc #'la/sq))


(def v130_l314 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t131_l316 (is ((fn [m] (== 4.0 (m 0 0))) v130_l314)))


(def v132_l318 (kind/doc #'la/sum))


(def v133_l320 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t134_l322 (is ((fn [v] (== 10.0 v)) v133_l320)))


(def v135_l324 (kind/doc #'la/prod))


(def v136_l326 (la/prod (t/matrix [2 3 4])))


(deftest t137_l328 (is ((fn [v] (== 24.0 v)) v136_l326)))


(def v138_l330 (kind/doc #'la/mmul))


(def v139_l332 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t140_l335
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v139_l332)))


(def v141_l338 (kind/doc #'la/transpose))


(def v142_l340 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t143_l342 (is ((fn [m] (= [3 2] (t/shape m))) v142_l340)))


(def v144_l344 (kind/doc #'la/trace))


(def v145_l346 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t146_l348 (is ((fn [v] (== 5.0 v)) v145_l346)))


(def v147_l350 (kind/doc #'la/det))


(def v148_l352 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t149_l354 (is ((fn [v] (la/close-scalar? v -2.0)) v148_l352)))


(def v150_l356 (kind/doc #'la/norm))


(def v151_l358 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t152_l360 (is ((fn [v] (la/close-scalar? v 5.0)) v151_l358)))


(def v153_l362 (kind/doc #'la/dot))


(def v154_l364 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t155_l366 (is ((fn [v] (== 32.0 v)) v154_l364)))


(def v156_l368 (kind/doc #'la/close?))


(def v157_l370 (la/close? (t/eye 2) (t/eye 2)))


(deftest t158_l372 (is (true? v157_l370)))


(def v159_l374 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t160_l376 (is (false? v159_l374)))


(def v161_l378 (kind/doc #'la/close-scalar?))


(def v162_l380 (la/close-scalar? 1.00000000001 1.0))


(deftest t163_l382 (is (true? v162_l380)))


(def v164_l384 (kind/doc #'la/invert))


(def
 v165_l386
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t166_l389 (is (true? v165_l386)))


(def v167_l391 (kind/doc #'la/solve))


(def
 v169_l394
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t170_l398
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v169_l394)))


(def v171_l401 (kind/doc #'la/eigen))


(def
 v172_l403
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t173_l407
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v172_l403)))


(def v174_l411 (kind/doc #'la/real-eigenvalues))


(def v175_l413 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t176_l415
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v175_l413)))


(def v177_l418 (kind/doc #'la/svd))


(def
 v178_l420
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t179_l425
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v178_l420)))


(def v180_l430 (kind/doc #'la/qr))


(def
 v181_l432
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t182_l435 (is (true? v181_l432)))


(def v183_l437 (kind/doc #'la/cholesky))


(def
 v184_l439
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t185_l443 (is (true? v184_l439)))


(def v186_l445 (kind/doc #'la/mpow))


(def v187_l447 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t188_l449
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v187_l447)))


(def v189_l451 (kind/doc #'la/rank))


(def v190_l453 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t191_l455 (is ((fn [r] (= 1 r)) v190_l453)))


(def v192_l457 (kind/doc #'la/condition-number))


(def v193_l459 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t194_l461 (is ((fn [v] (> v 1.0)) v193_l459)))


(def v195_l463 (kind/doc #'la/pinv))


(def
 v196_l465
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t197_l468 (is (true? v196_l465)))


(def v198_l470 (kind/doc #'la/lstsq))


(def
 v199_l472
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t200_l476
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v199_l472)))


(def v201_l478 (kind/doc #'la/null-space))


(def
 v202_l480
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t203_l484 (is (true? v202_l480)))


(def v204_l486 (kind/doc #'la/col-space))


(def
 v205_l488
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t206_l490 (is ((fn [r] (= 1 r)) v205_l488)))


(def v207_l492 (kind/doc #'la/lift))


(def v209_l495 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t210_l497
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v209_l495)))


(def v211_l500 (kind/doc #'la/lifted))


(def
 v213_l503
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t214_l506
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v213_l503)))


(def v216_l512 (kind/doc #'t/complex-tensor))


(def v217_l514 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t218_l516
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v217_l514)))


(def v219_l518 (kind/doc #'t/complex-tensor-real))


(def v220_l520 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t221_l522 (is ((fn [ct] (every? zero? (la/im ct))) v220_l520)))


(def v222_l524 (kind/doc #'t/complex))


(def v223_l526 (t/complex 3.0 4.0))


(deftest
 t224_l528
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (la/re ct)) (== 4.0 (la/im ct))))
   v223_l526)))


(def v225_l532 (kind/doc #'la/re))


(def v226_l534 (la/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t227_l536 (is (= v226_l534 [1.0 2.0])))


(def v228_l538 (kind/doc #'la/im))


(def v229_l540 (la/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t230_l542 (is (= v229_l540 [3.0 4.0])))


(def v231_l544 (kind/doc #'t/complex-shape))


(def
 v232_l546
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t233_l549 (is (= v232_l546 [2 2])))


(def v234_l551 (kind/doc #'t/scalar?))


(def v235_l553 (t/scalar? (t/complex 3.0 4.0)))


(deftest t236_l555 (is (true? v235_l553)))


(def v237_l557 (kind/doc #'t/complex?))


(def v238_l559 (t/complex? (t/complex 3.0 4.0)))


(deftest t239_l561 (is (true? v238_l559)))


(def v240_l563 (t/complex? (t/eye 2)))


(deftest t241_l565 (is (false? v240_l563)))


(def v242_l567 (kind/doc #'t/->tensor))


(def
 v243_l569
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t244_l571 (is (= v243_l569 [2 2])))


(def v245_l573 (kind/doc #'t/->double-array))


(def
 v246_l575
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t247_l578 (is (= v246_l575 [1.0 3.0 2.0 4.0])))


(def v248_l580 (kind/doc #'t/wrap-tensor))


(def
 v249_l582
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t250_l586
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v249_l582)))


(def
 v252_l593
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (la/re (la/add a b))))


(deftest t253_l597 (is (= v252_l593 [11.0 22.0])))


(def
 v255_l601
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (la/mul a b)]
  [(la/re (c 0)) (la/im (c 0))]))


(deftest t256_l606 (is (= v255_l601 [-10.0 10.0])))


(def v258_l610 (kind/doc #'la/conj))


(def
 v259_l612
 (let
  [ct (la/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (la/im ct)))


(deftest t260_l615 (is (= v259_l612 [-3.0 4.0])))


(def v262_l619 (kind/doc #'la/dot-conj))


(def
 v263_l621
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (la/re result) 30.0)))


(deftest t264_l625 (is (true? v263_l621)))


(def
 v266_l629
 (let
  [m (la/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t267_l632 (is (true? v266_l629)))


(def
 v269_l636
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (la/sum ct)]
  [(la/re s) (la/im s)]))


(deftest t270_l640 (is (= v269_l636 [6.0 15.0])))


(def v272_l646 (kind/doc #'ft/forward))


(def
 v273_l648
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t274_l652 (is (= v273_l648 [4])))


(def v275_l654 (kind/doc #'ft/inverse))


(def
 v276_l656
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (la/re (roundtrip 0)) 1.0)))


(deftest t277_l660 (is (true? v276_l656)))


(def v278_l662 (kind/doc #'ft/inverse-real))


(def
 v279_l664
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t280_l668 (is (true? v279_l664)))


(def v281_l670 (kind/doc #'ft/forward-complex))


(def
 v282_l672
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t283_l676 (is (= v282_l672 [4])))


(def v284_l678 (kind/doc #'ft/dct-forward))


(def v285_l680 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t286_l682 (is ((fn [v] (= 4 (count v))) v285_l680)))


(def v287_l684 (kind/doc #'ft/dct-inverse))


(def
 v288_l686
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t289_l690 (is (true? v288_l686)))


(def v290_l692 (kind/doc #'ft/dst-forward))


(def v291_l694 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t292_l696 (is ((fn [v] (= 4 (count v))) v291_l694)))


(def v293_l698 (kind/doc #'ft/dst-inverse))


(def
 v294_l700
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t295_l704 (is (true? v294_l700)))


(def v296_l706 (kind/doc #'ft/dht-forward))


(def v297_l708 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t298_l710 (is ((fn [v] (= 4 (count v))) v297_l708)))


(def v299_l712 (kind/doc #'ft/dht-inverse))


(def
 v300_l714
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t301_l718 (is (true? v300_l714)))


(def v303_l724 (kind/doc #'tape/memory-status))


(def v304_l726 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t305_l728 (is ((fn [s] (= :contiguous s)) v304_l726)))


(def
 v306_l730
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t307_l732 (is ((fn [s] (= :strided s)) v306_l730)))


(def v308_l734 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t309_l736 (is ((fn [s] (= :lazy s)) v308_l734)))


(def v310_l738 (kind/doc #'tape/memory-relation))


(def
 v311_l740
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t312_l743 (is ((fn [r] (= :shared r)) v311_l740)))


(def
 v313_l745
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t314_l747 (is ((fn [r] (= :independent r)) v313_l745)))


(def
 v315_l749
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t316_l751 (is ((fn [r] (= :unknown-lazy r)) v315_l749)))


(def v317_l753 (kind/doc #'tape/with-tape))


(def
 v318_l755
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v319_l761 (select-keys tape-example [:result :entries]))


(deftest
 t320_l763
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v319_l761)))


(def v321_l766 (kind/doc #'tape/summary))


(def v322_l768 (tape/summary tape-example))


(deftest t323_l770 (is ((fn [s] (= 4 (:total s))) v322_l768)))


(def v324_l772 (kind/doc #'tape/origin))


(def v325_l774 (tape/origin tape-example (:result tape-example)))


(deftest t326_l776 (is ((fn [dag] (= :la/mmul (:op dag))) v325_l774)))


(def v327_l778 (kind/doc #'tape/mermaid))


(def v329_l782 (tape/mermaid tape-example (:result tape-example)))


(def v330_l784 (kind/doc #'tape/detect-memory-status))


(def v332_l789 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t333_l791
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v332_l789)))


(def v335_l799 (kind/doc #'elem/sq))


(def v336_l801 (elem/sq (t/column [2 3 4])))


(deftest
 t337_l803
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v336_l801)))


(def v338_l805 (kind/doc #'elem/sqrt))


(def v339_l807 (elem/sqrt (t/column [4 9 16])))


(deftest
 t340_l809
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v339_l807)))


(def v341_l811 (kind/doc #'elem/exp))


(def v342_l813 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t343_l815 (is (true? v342_l813)))


(def v344_l817 (kind/doc #'elem/log))


(def
 v345_l819
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t346_l821 (is (true? v345_l819)))


(def v347_l823 (kind/doc #'elem/log10))


(def
 v348_l825
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t349_l827 (is (true? v348_l825)))


(def v350_l829 (kind/doc #'elem/sin))


(def
 v351_l831
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t352_l833 (is (true? v351_l831)))


(def v353_l835 (kind/doc #'elem/cos))


(def v354_l837 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t355_l839 (is (true? v354_l837)))


(def v356_l841 (kind/doc #'elem/tan))


(def
 v357_l843
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t358_l845 (is (true? v357_l843)))


(def v359_l847 (kind/doc #'elem/sinh))


(def v360_l849 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t361_l851 (is (true? v360_l849)))


(def v362_l853 (kind/doc #'elem/cosh))


(def v363_l855 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t364_l857 (is (true? v363_l855)))


(def v365_l859 (kind/doc #'elem/tanh))


(def v366_l861 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t367_l863 (is (true? v366_l861)))


(def v368_l865 (kind/doc #'elem/abs))


(def v369_l867 ((elem/abs (t/column [-5])) 0 0))


(deftest t370_l869 (is ((fn [v] (== 5.0 v)) v369_l867)))


(def v371_l871 (kind/doc #'elem/sum))


(def v372_l873 (elem/sum (t/column [1 2 3 4])))


(deftest t373_l875 (is ((fn [v] (== 10.0 v)) v372_l873)))


(def v374_l877 (kind/doc #'elem/mean))


(def v375_l879 (elem/mean (t/column [2 4 6])))


(deftest t376_l881 (is ((fn [v] (== 4.0 v)) v375_l879)))


(def v377_l883 (kind/doc #'elem/pow))


(def v378_l885 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t379_l887 (is ((fn [v] (== 8.0 v)) v378_l885)))


(def v380_l889 (kind/doc #'elem/cbrt))


(def v381_l891 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t382_l893 (is (true? v381_l891)))


(def v383_l895 (kind/doc #'elem/floor))


(def v384_l897 ((elem/floor (t/column [2.7])) 0 0))


(deftest t385_l899 (is ((fn [v] (== 2.0 v)) v384_l897)))


(def v386_l901 (kind/doc #'elem/ceil))


(def v387_l903 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t388_l905 (is ((fn [v] (== 3.0 v)) v387_l903)))


(def v389_l907 (kind/doc #'elem/min))


(def v390_l909 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t391_l911 (is ((fn [v] (== 3.0 v)) v390_l909)))


(def v392_l913 (kind/doc #'elem/max))


(def v393_l915 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t394_l917 (is ((fn [v] (== 5.0 v)) v393_l915)))


(def v395_l919 (kind/doc #'elem/asin))


(def v396_l921 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t397_l923
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v396_l921)))


(def v398_l925 (kind/doc #'elem/acos))


(def v399_l927 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t400_l929
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v399_l927)))


(def v401_l931 (kind/doc #'elem/atan))


(def v402_l933 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t403_l935
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v402_l933)))


(def v404_l937 (kind/doc #'elem/log1p))


(def v405_l939 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t406_l941 (is ((fn [v] (la/close-scalar? v 0.0)) v405_l939)))


(def v407_l943 (kind/doc #'elem/expm1))


(def v408_l945 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t409_l947 (is ((fn [v] (la/close-scalar? v 0.0)) v408_l945)))


(def v410_l949 (kind/doc #'elem/round))


(def v411_l951 ((elem/round (t/column [2.7])) 0 0))


(deftest t412_l953 (is ((fn [v] (== 3.0 v)) v411_l951)))


(def v413_l955 (kind/doc #'elem/clip))


(def v414_l957 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t415_l959 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v414_l957)))


(def v416_l961 (kind/doc #'elem/div))


(def v417_l963 (elem/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t418_l965
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v417_l963)))


(def v419_l967 (kind/doc #'elem/gt))


(def v420_l969 (elem/gt (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t421_l971
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v420_l969)))


(def v422_l973 (kind/doc #'elem/reduce-max))


(def v423_l975 (elem/reduce-max (t/column [3 7 2 9 1])))


(deftest t424_l977 (is ((fn [v] (== 9.0 v)) v423_l975)))


(def v425_l979 (kind/doc #'elem/reduce-min))


(def v426_l981 (elem/reduce-min (t/column [3 7 2 9 1])))


(deftest t427_l983 (is ((fn [v] (== 1.0 v)) v426_l981)))


(def v429_l989 (kind/doc #'grad/grad))


(def
 v430_l991
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))]
  (la/close?
   (grad/grad tape-result (:result tape-result) A)
   (la/scale A 2))))


(deftest t431_l998 (is (true? v430_l991)))


(def v433_l1004 (kind/doc #'vis/arrow-plot))


(def
 v434_l1006
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v435_l1010 (kind/doc #'vis/graph-plot))


(def
 v436_l1012
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v437_l1016 (kind/doc #'vis/matrix->gray-image))


(def
 v438_l1018
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t439_l1023
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v438_l1018)))


(def v440_l1025 (kind/doc #'vis/extract-channel))


(def
 v441_l1027
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t442_l1033
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v441_l1027)))
