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


(def v101_l243 (kind/doc #'t/->reader))


(def v102_l245 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t103_l248 (is ((fn [v] (== 30.0 v)) v102_l245)))


(def v104_l250 (kind/doc #'t/array-buffer))


(def v105_l252 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t106_l254 (is (true? v105_l252)))


(def v108_l257 (kind/doc #'la/add))


(def
 v109_l259
 (la/add (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]])))


(deftest t110_l262 (is ((fn [m] (== 11.0 (m 0 0))) v109_l259)))


(def v111_l264 (kind/doc #'la/sub))


(def
 v112_l266
 (la/sub (t/matrix [[10 20] [30 40]]) (t/matrix [[1 2] [3 4]])))


(deftest t113_l269 (is ((fn [m] (== 9.0 (m 0 0))) v112_l266)))


(def v114_l271 (kind/doc #'la/scale))


(def v115_l273 (la/scale (t/matrix [[1 2] [3 4]]) 3.0))


(deftest t116_l275 (is ((fn [m] (== 6.0 (m 0 1))) v115_l273)))


(def v117_l277 (kind/doc #'la/mul))


(def
 v118_l279
 (la/mul (t/matrix [[2 3] [4 5]]) (t/matrix [[10 20] [30 40]])))


(deftest
 t119_l282
 (is ((fn [m] (and (== 20.0 (m 0 0)) (== 60.0 (m 0 1)))) v118_l279)))


(def v120_l285 (kind/doc #'la/abs))


(def v121_l287 (la/abs (t/matrix [[-3 2] [-1 4]])))


(deftest t122_l289 (is ((fn [m] (== 3.0 (m 0 0))) v121_l287)))


(def v123_l291 (kind/doc #'la/sq))


(def v124_l293 (la/sq (t/matrix [[2 3] [4 5]])))


(deftest t125_l295 (is ((fn [m] (== 4.0 (m 0 0))) v124_l293)))


(def v126_l297 (kind/doc #'la/sum))


(def v127_l299 (la/sum (t/matrix [[1 2] [3 4]])))


(deftest t128_l301 (is ((fn [v] (== 10.0 v)) v127_l299)))


(def v129_l303 (kind/doc #'la/prod))


(def v130_l305 (la/prod (t/matrix [2 3 4])))


(deftest t131_l307 (is ((fn [v] (== 24.0 v)) v130_l305)))


(def v132_l309 (kind/doc #'la/mmul))


(def v133_l311 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t134_l314
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v133_l311)))


(def v135_l317 (kind/doc #'la/transpose))


(def v136_l319 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t137_l321 (is ((fn [m] (= [3 2] (t/shape m))) v136_l319)))


(def v138_l323 (kind/doc #'la/trace))


(def v139_l325 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t140_l327 (is ((fn [v] (== 5.0 v)) v139_l325)))


(def v141_l329 (kind/doc #'la/det))


(def v142_l331 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t143_l333 (is ((fn [v] (la/close-scalar? v -2.0)) v142_l331)))


(def v144_l335 (kind/doc #'la/norm))


(def v145_l337 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t146_l339 (is ((fn [v] (la/close-scalar? v 5.0)) v145_l337)))


(def v147_l341 (kind/doc #'la/dot))


(def v148_l343 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t149_l345 (is ((fn [v] (== 32.0 v)) v148_l343)))


(def v150_l347 (kind/doc #'la/close?))


(def v151_l349 (la/close? (t/eye 2) (t/eye 2)))


(deftest t152_l351 (is (true? v151_l349)))


(def v153_l353 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t154_l355 (is (false? v153_l353)))


(def v155_l357 (kind/doc #'la/close-scalar?))


(def v156_l359 (la/close-scalar? 1.00000000001 1.0))


(deftest t157_l361 (is (true? v156_l359)))


(def v158_l363 (kind/doc #'la/invert))


(def
 v159_l365
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t160_l368 (is (true? v159_l365)))


(def v161_l370 (kind/doc #'la/solve))


(def
 v163_l373
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t164_l377
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v163_l373)))


(def v165_l380 (kind/doc #'la/eigen))


(def
 v166_l382
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t167_l386
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v166_l382)))


(def v168_l390 (kind/doc #'la/real-eigenvalues))


(def v169_l392 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t170_l394
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v169_l392)))


(def v171_l397 (kind/doc #'la/svd))


(def
 v172_l399
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t173_l404
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v172_l399)))


(def v174_l409 (kind/doc #'la/qr))


(def
 v175_l411
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t176_l414 (is (true? v175_l411)))


(def v177_l416 (kind/doc #'la/cholesky))


(def
 v178_l418
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t179_l422 (is (true? v178_l418)))


(def v180_l424 (kind/doc #'la/mpow))


(def v181_l426 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t182_l428
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v181_l426)))


(def v183_l430 (kind/doc #'la/rank))


(def v184_l432 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t185_l434 (is ((fn [r] (= 1 r)) v184_l432)))


(def v186_l436 (kind/doc #'la/condition-number))


(def v187_l438 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t188_l440 (is ((fn [v] (> v 1.0)) v187_l438)))


(def v189_l442 (kind/doc #'la/pinv))


(def
 v190_l444
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t191_l447 (is (true? v190_l444)))


(def v192_l449 (kind/doc #'la/lstsq))


(def
 v193_l451
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t194_l455
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v193_l451)))


(def v195_l457 (kind/doc #'la/null-space))


(def
 v196_l459
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t197_l463 (is (true? v196_l459)))


(def v198_l465 (kind/doc #'la/col-space))


(def
 v199_l467
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t200_l469 (is ((fn [r] (= 1 r)) v199_l467)))


(def v201_l471 (kind/doc #'la/lift))


(def v203_l474 (la/lift elem/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t204_l476
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v203_l474)))


(def v205_l479 (kind/doc #'la/lifted))


(def
 v207_l482
 (let [my-sqrt (la/lifted elem/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t208_l485
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v207_l482)))


(def v210_l491 (kind/doc #'t/complex-tensor))


(def v211_l493 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t212_l495
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v211_l493)))


(def v213_l497 (kind/doc #'t/complex-tensor-real))


(def v214_l499 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t215_l501 (is ((fn [ct] (every? zero? (la/im ct))) v214_l499)))


(def v216_l503 (kind/doc #'t/complex))


(def v217_l505 (t/complex 3.0 4.0))


(deftest
 t218_l507
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (la/re ct)) (== 4.0 (la/im ct))))
   v217_l505)))


(def v219_l511 (kind/doc #'la/re))


(def v220_l513 (la/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t221_l515 (is (= v220_l513 [1.0 2.0])))


(def v222_l517 (kind/doc #'la/im))


(def v223_l519 (la/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t224_l521 (is (= v223_l519 [3.0 4.0])))


(def v225_l523 (kind/doc #'t/complex-shape))


(def
 v226_l525
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t227_l528 (is (= v226_l525 [2 2])))


(def v228_l530 (kind/doc #'t/scalar?))


(def v229_l532 (t/scalar? (t/complex 3.0 4.0)))


(deftest t230_l534 (is (true? v229_l532)))


(def v231_l536 (kind/doc #'t/complex?))


(def v232_l538 (t/complex? (t/complex 3.0 4.0)))


(deftest t233_l540 (is (true? v232_l538)))


(def v234_l542 (t/complex? (t/eye 2)))


(deftest t235_l544 (is (false? v234_l542)))


(def v236_l546 (kind/doc #'t/->tensor))


(def
 v237_l548
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t238_l550 (is (= v237_l548 [2 2])))


(def v239_l552 (kind/doc #'t/->double-array))


(def
 v240_l554
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t241_l557 (is (= v240_l554 [1.0 3.0 2.0 4.0])))


(def v242_l559 (kind/doc #'t/wrap-tensor))


(def
 v243_l561
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t244_l565
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v243_l561)))


(def
 v246_l572
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (la/re (la/add a b))))


(deftest t247_l576 (is (= v246_l572 [11.0 22.0])))


(def
 v249_l580
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (la/mul a b)]
  [(la/re (c 0)) (la/im (c 0))]))


(deftest t250_l585 (is (= v249_l580 [-10.0 10.0])))


(def v252_l589 (kind/doc #'la/conj))


(def
 v253_l591
 (let
  [ct (la/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (la/im ct)))


(deftest t254_l594 (is (= v253_l591 [-3.0 4.0])))


(def v256_l598 (kind/doc #'la/dot-conj))


(def
 v257_l600
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (la/re result) 30.0)))


(deftest t258_l604 (is (true? v257_l600)))


(def
 v260_l608
 (let
  [m (la/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t261_l611 (is (true? v260_l608)))


(def
 v263_l615
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (la/sum ct)]
  [(la/re s) (la/im s)]))


(deftest t264_l619 (is (= v263_l615 [6.0 15.0])))


(def v266_l625 (kind/doc #'ft/forward))


(def
 v267_l627
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t268_l631 (is (= v267_l627 [4])))


(def v269_l633 (kind/doc #'ft/inverse))


(def
 v270_l635
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (la/re (roundtrip 0)) 1.0)))


(deftest t271_l639 (is (true? v270_l635)))


(def v272_l641 (kind/doc #'ft/inverse-real))


(def
 v273_l643
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t274_l647 (is (true? v273_l643)))


(def v275_l649 (kind/doc #'ft/forward-complex))


(def
 v276_l651
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t277_l655 (is (= v276_l651 [4])))


(def v278_l657 (kind/doc #'ft/dct-forward))


(def v279_l659 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t280_l661 (is ((fn [v] (= 4 (count v))) v279_l659)))


(def v281_l663 (kind/doc #'ft/dct-inverse))


(def
 v282_l665
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t283_l669 (is (true? v282_l665)))


(def v284_l671 (kind/doc #'ft/dst-forward))


(def v285_l673 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t286_l675 (is ((fn [v] (= 4 (count v))) v285_l673)))


(def v287_l677 (kind/doc #'ft/dst-inverse))


(def
 v288_l679
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t289_l683 (is (true? v288_l679)))


(def v290_l685 (kind/doc #'ft/dht-forward))


(def v291_l687 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t292_l689 (is ((fn [v] (= 4 (count v))) v291_l687)))


(def v293_l691 (kind/doc #'ft/dht-inverse))


(def
 v294_l693
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t295_l697 (is (true? v294_l693)))


(def v297_l703 (kind/doc #'tape/memory-status))


(def v298_l705 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t299_l707 (is ((fn [s] (= :contiguous s)) v298_l705)))


(def
 v300_l709
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t301_l711 (is ((fn [s] (= :strided s)) v300_l709)))


(def v302_l713 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t303_l715 (is ((fn [s] (= :lazy s)) v302_l713)))


(def v304_l717 (kind/doc #'tape/memory-relation))


(def
 v305_l719
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t306_l722 (is ((fn [r] (= :shared r)) v305_l719)))


(def
 v307_l724
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t308_l726 (is ((fn [r] (= :independent r)) v307_l724)))


(def
 v309_l728
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t310_l730 (is ((fn [r] (= :unknown-lazy r)) v309_l728)))


(def v311_l732 (kind/doc #'tape/with-tape))


(def
 v312_l734
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v313_l740 (select-keys tape-example [:result :entries]))


(deftest
 t314_l742
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v313_l740)))


(def v315_l745 (kind/doc #'tape/summary))


(def v316_l747 (tape/summary tape-example))


(deftest t317_l749 (is ((fn [s] (= 4 (:total s))) v316_l747)))


(def v318_l751 (kind/doc #'tape/origin))


(def v319_l753 (tape/origin tape-example (:result tape-example)))


(deftest t320_l755 (is ((fn [dag] (= :la/mmul (:op dag))) v319_l753)))


(def v321_l757 (kind/doc #'tape/mermaid))


(def v323_l761 (tape/mermaid tape-example (:result tape-example)))


(def v324_l763 (kind/doc #'tape/detect-memory-status))


(def v326_l768 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t327_l770
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v326_l768)))


(def v329_l778 (kind/doc #'elem/sq))


(def v330_l780 (elem/sq (t/column [2 3 4])))


(deftest
 t331_l782
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v330_l780)))


(def v332_l784 (kind/doc #'elem/sqrt))


(def v333_l786 (elem/sqrt (t/column [4 9 16])))


(deftest
 t334_l788
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v333_l786)))


(def v335_l790 (kind/doc #'elem/exp))


(def v336_l792 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t337_l794 (is (true? v336_l792)))


(def v338_l796 (kind/doc #'elem/log))


(def
 v339_l798
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t340_l800 (is (true? v339_l798)))


(def v341_l802 (kind/doc #'elem/log10))


(def
 v342_l804
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t343_l806 (is (true? v342_l804)))


(def v344_l808 (kind/doc #'elem/sin))


(def
 v345_l810
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t346_l812 (is (true? v345_l810)))


(def v347_l814 (kind/doc #'elem/cos))


(def v348_l816 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t349_l818 (is (true? v348_l816)))


(def v350_l820 (kind/doc #'elem/tan))


(def
 v351_l822
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t352_l824 (is (true? v351_l822)))


(def v353_l826 (kind/doc #'elem/sinh))


(def v354_l828 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t355_l830 (is (true? v354_l828)))


(def v356_l832 (kind/doc #'elem/cosh))


(def v357_l834 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t358_l836 (is (true? v357_l834)))


(def v359_l838 (kind/doc #'elem/tanh))


(def v360_l840 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t361_l842 (is (true? v360_l840)))


(def v362_l844 (kind/doc #'elem/abs))


(def v363_l846 ((elem/abs (t/column [-5])) 0 0))


(deftest t364_l848 (is ((fn [v] (== 5.0 v)) v363_l846)))


(def v365_l850 (kind/doc #'elem/sum))


(def v366_l852 (elem/sum (t/column [1 2 3 4])))


(deftest t367_l854 (is ((fn [v] (== 10.0 v)) v366_l852)))


(def v368_l856 (kind/doc #'elem/mean))


(def v369_l858 (elem/mean (t/column [2 4 6])))


(deftest t370_l860 (is ((fn [v] (== 4.0 v)) v369_l858)))


(def v371_l862 (kind/doc #'elem/pow))


(def v372_l864 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t373_l866 (is ((fn [v] (== 8.0 v)) v372_l864)))


(def v374_l868 (kind/doc #'elem/cbrt))


(def v375_l870 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t376_l872 (is (true? v375_l870)))


(def v377_l874 (kind/doc #'elem/floor))


(def v378_l876 ((elem/floor (t/column [2.7])) 0 0))


(deftest t379_l878 (is ((fn [v] (== 2.0 v)) v378_l876)))


(def v380_l880 (kind/doc #'elem/ceil))


(def v381_l882 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t382_l884 (is ((fn [v] (== 3.0 v)) v381_l882)))


(def v383_l886 (kind/doc #'elem/min))


(def v384_l888 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t385_l890 (is ((fn [v] (== 3.0 v)) v384_l888)))


(def v386_l892 (kind/doc #'elem/max))


(def v387_l894 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t388_l896 (is ((fn [v] (== 5.0 v)) v387_l894)))


(def v389_l898 (kind/doc #'elem/asin))


(def v390_l900 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t391_l902
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v390_l900)))


(def v392_l904 (kind/doc #'elem/acos))


(def v393_l906 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t394_l908
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v393_l906)))


(def v395_l910 (kind/doc #'elem/atan))


(def v396_l912 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t397_l914
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v396_l912)))


(def v398_l916 (kind/doc #'elem/log1p))


(def v399_l918 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t400_l920 (is ((fn [v] (la/close-scalar? v 0.0)) v399_l918)))


(def v401_l922 (kind/doc #'elem/expm1))


(def v402_l924 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t403_l926 (is ((fn [v] (la/close-scalar? v 0.0)) v402_l924)))


(def v404_l928 (kind/doc #'elem/round))


(def v405_l930 ((elem/round (t/column [2.7])) 0 0))


(deftest t406_l932 (is ((fn [v] (== 3.0 v)) v405_l930)))


(def v407_l934 (kind/doc #'elem/clip))


(def v408_l936 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t409_l938 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v408_l936)))


(def v410_l940 (kind/doc #'elem/div))


(def v411_l942 (elem/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t412_l944
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v411_l942)))


(def v413_l946 (kind/doc #'elem/gt))


(def v414_l948 (elem/gt (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t415_l950
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v414_l948)))


(def v416_l952 (kind/doc #'elem/reduce-max))


(def v417_l954 (elem/reduce-max (t/column [3 7 2 9 1])))


(deftest t418_l956 (is ((fn [v] (== 9.0 v)) v417_l954)))


(def v419_l958 (kind/doc #'elem/reduce-min))


(def v420_l960 (elem/reduce-min (t/column [3 7 2 9 1])))


(deftest t421_l962 (is ((fn [v] (== 1.0 v)) v420_l960)))


(def v423_l968 (kind/doc #'grad/grad))


(def
 v424_l970
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t425_l976 (is (true? v424_l970)))


(def v427_l982 (kind/doc #'vis/arrow-plot))


(def
 v428_l984
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v429_l988 (kind/doc #'vis/graph-plot))


(def
 v430_l990
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v431_l994 (kind/doc #'vis/matrix->gray-image))


(def
 v432_l996
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t433_l1001
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v432_l996)))


(def v434_l1003 (kind/doc #'vis/extract-channel))


(def
 v435_l1005
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t436_l1011
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v435_l1005)))
