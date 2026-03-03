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
   (cx/complex-shape (:eigenvalues result))]))


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


(def v210_l492 (kind/doc #'cx/complex-tensor))


(def v211_l494 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t212_l496
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v211_l494)))


(def v213_l498 (kind/doc #'cx/complex-tensor-real))


(def v214_l500 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest t215_l502 (is ((fn [ct] (every? zero? (cx/im ct))) v214_l500)))


(def v216_l504 (kind/doc #'cx/complex))


(def v217_l506 (cx/complex 3.0 4.0))


(deftest
 t218_l508
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v217_l506)))


(def v219_l512 (kind/doc #'cx/re))


(def v220_l514 (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t221_l516 (is (= v220_l514 [1.0 2.0])))


(def v222_l518 (kind/doc #'cx/im))


(def v223_l520 (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t224_l522 (is (= v223_l520 [3.0 4.0])))


(def v225_l524 (kind/doc #'cx/complex-shape))


(def
 v226_l526
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t227_l529 (is (= v226_l526 [2 2])))


(def v228_l531 (kind/doc #'cx/scalar?))


(def v229_l533 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t230_l535 (is (true? v229_l533)))


(def v231_l537 (kind/doc #'cx/complex?))


(def v232_l539 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t233_l541 (is (true? v232_l539)))


(def v234_l543 (cx/complex? (t/eye 2)))


(deftest t235_l545 (is (false? v234_l543)))


(def v236_l547 (kind/doc #'cx/->tensor))


(def
 v237_l549
 (t/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t238_l551 (is (= v237_l549 [2 2])))


(def v239_l553 (kind/doc #'cx/->double-array))


(def
 v240_l555
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (cx/->double-array ct))))


(deftest t241_l558 (is (= v240_l555 [1.0 3.0 2.0 4.0])))


(def v242_l560 (kind/doc #'cx/wrap-tensor))


(def
 v243_l562
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (cx/wrap-tensor raw)]
  [(cx/complex? ct) (cx/complex-shape ct)]))


(deftest
 t244_l566
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v243_l562)))


(def v245_l568 (kind/doc #'cx/add))


(def
 v246_l570
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (cx/re (cx/add a b))))


(deftest t247_l574 (is (= v246_l570 [11.0 22.0])))


(def v248_l576 (kind/doc #'cx/sub))


(def
 v249_l578
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (cx/re (cx/sub a b))))


(deftest t250_l582 (is (= v249_l578 [9.0 18.0])))


(def v251_l584 (kind/doc #'cx/scale))


(def
 v252_l586
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(cx/re ct) (cx/im ct)]))


(deftest t253_l589 (is (= v252_l586 [[2.0 4.0] [6.0 8.0]])))


(def v254_l591 (kind/doc #'cx/mul))


(def
 v256_l594
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t257_l599 (is (= v256_l594 [-10.0 10.0])))


(def v258_l601 (kind/doc #'cx/conj))


(def
 v259_l603
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (cx/im ct)))


(deftest t260_l606 (is (= v259_l603 [-3.0 4.0])))


(def v261_l608 (kind/doc #'cx/abs))


(def
 v263_l611
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t264_l614 (is (true? v263_l611)))


(def v265_l616 (kind/doc #'cx/dot))


(def
 v266_l618
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t267_l623 (is (true? v266_l618)))


(def v268_l625 (kind/doc #'cx/dot-conj))


(def
 v270_l628
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t271_l632 (is (true? v270_l628)))


(def v272_l634 (kind/doc #'cx/sum))


(def
 v273_l636
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t274_l640 (is (= v273_l636 [6.0 15.0])))


(def v276_l647 (kind/doc #'ft/forward))


(def
 v277_l649
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t278_l653 (is (= v277_l649 [4])))


(def v279_l655 (kind/doc #'ft/inverse))


(def
 v280_l657
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t281_l661 (is (true? v280_l657)))


(def v282_l663 (kind/doc #'ft/inverse-real))


(def
 v283_l665
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t284_l669 (is (true? v283_l665)))


(def v285_l671 (kind/doc #'ft/forward-complex))


(def
 v286_l673
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t287_l677 (is (= v286_l673 [4])))


(def v288_l679 (kind/doc #'ft/dct-forward))


(def v289_l681 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t290_l683 (is ((fn [v] (= 4 (count v))) v289_l681)))


(def v291_l685 (kind/doc #'ft/dct-inverse))


(def
 v292_l687
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t293_l691 (is (true? v292_l687)))


(def v294_l693 (kind/doc #'ft/dst-forward))


(def v295_l695 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t296_l697 (is ((fn [v] (= 4 (count v))) v295_l695)))


(def v297_l699 (kind/doc #'ft/dst-inverse))


(def
 v298_l701
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t299_l705 (is (true? v298_l701)))


(def v300_l707 (kind/doc #'ft/dht-forward))


(def v301_l709 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t302_l711 (is ((fn [v] (= 4 (count v))) v301_l709)))


(def v303_l713 (kind/doc #'ft/dht-inverse))


(def
 v304_l715
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t305_l719 (is (true? v304_l715)))


(def v307_l725 (kind/doc #'tape/memory-status))


(def v308_l727 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t309_l729 (is ((fn [s] (= :contiguous s)) v308_l727)))


(def
 v310_l731
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t311_l733 (is ((fn [s] (= :strided s)) v310_l731)))


(def v312_l735 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t313_l737 (is ((fn [s] (= :lazy s)) v312_l735)))


(def v314_l739 (kind/doc #'tape/memory-relation))


(def
 v315_l741
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t316_l744 (is ((fn [r] (= :shared r)) v315_l741)))


(def
 v317_l746
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t318_l748 (is ((fn [r] (= :independent r)) v317_l746)))


(def
 v319_l750
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t320_l752 (is ((fn [r] (= :unknown-lazy r)) v319_l750)))


(def v321_l754 (kind/doc #'tape/with-tape))


(def
 v322_l756
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v323_l762 (select-keys tape-example [:result :entries]))


(deftest
 t324_l764
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v323_l762)))


(def v325_l767 (kind/doc #'tape/summary))


(def v326_l769 (tape/summary tape-example))


(deftest t327_l771 (is ((fn [s] (= 4 (:total s))) v326_l769)))


(def v328_l773 (kind/doc #'tape/origin))


(def v329_l775 (tape/origin tape-example (:result tape-example)))


(deftest t330_l777 (is ((fn [dag] (= :la/mmul (:op dag))) v329_l775)))


(def v331_l779 (kind/doc #'tape/mermaid))


(def v333_l783 (tape/mermaid tape-example (:result tape-example)))


(def v334_l785 (kind/doc #'tape/detect-memory-status))


(def v336_l790 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t337_l792
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v336_l790)))


(def v339_l800 (kind/doc #'elem/sq))


(def v340_l802 (elem/sq (t/column [2 3 4])))


(deftest
 t341_l804
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v340_l802)))


(def v342_l806 (kind/doc #'elem/sqrt))


(def v343_l808 (elem/sqrt (t/column [4 9 16])))


(deftest
 t344_l810
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v343_l808)))


(def v345_l812 (kind/doc #'elem/exp))


(def v346_l814 (la/close-scalar? ((elem/exp (t/column [0])) 0 0) 1.0))


(deftest t347_l816 (is (true? v346_l814)))


(def v348_l818 (kind/doc #'elem/log))


(def
 v349_l820
 (la/close-scalar? ((elem/log (t/column [math/E])) 0 0) 1.0))


(deftest t350_l822 (is (true? v349_l820)))


(def v351_l824 (kind/doc #'elem/log10))


(def
 v352_l826
 (la/close-scalar? ((elem/log10 (t/column [100])) 0 0) 2.0))


(deftest t353_l828 (is (true? v352_l826)))


(def v354_l830 (kind/doc #'elem/sin))


(def
 v355_l832
 (la/close-scalar? ((elem/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t356_l834 (is (true? v355_l832)))


(def v357_l836 (kind/doc #'elem/cos))


(def v358_l838 (la/close-scalar? ((elem/cos (t/column [0])) 0 0) 1.0))


(deftest t359_l840 (is (true? v358_l838)))


(def v360_l842 (kind/doc #'elem/tan))


(def
 v361_l844
 (la/close-scalar? ((elem/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t362_l846 (is (true? v361_l844)))


(def v363_l848 (kind/doc #'elem/sinh))


(def v364_l850 (la/close-scalar? ((elem/sinh (t/column [0])) 0 0) 0.0))


(deftest t365_l852 (is (true? v364_l850)))


(def v366_l854 (kind/doc #'elem/cosh))


(def v367_l856 (la/close-scalar? ((elem/cosh (t/column [0])) 0 0) 1.0))


(deftest t368_l858 (is (true? v367_l856)))


(def v369_l860 (kind/doc #'elem/tanh))


(def v370_l862 (la/close-scalar? ((elem/tanh (t/column [0])) 0 0) 0.0))


(deftest t371_l864 (is (true? v370_l862)))


(def v372_l866 (kind/doc #'elem/abs))


(def v373_l868 ((elem/abs (t/column [-5])) 0 0))


(deftest t374_l870 (is ((fn [v] (== 5.0 v)) v373_l868)))


(def v375_l872 (kind/doc #'elem/sum))


(def v376_l874 (elem/sum (t/column [1 2 3 4])))


(deftest t377_l876 (is ((fn [v] (== 10.0 v)) v376_l874)))


(def v378_l878 (kind/doc #'elem/mean))


(def v379_l880 (elem/mean (t/column [2 4 6])))


(deftest t380_l882 (is ((fn [v] (== 4.0 v)) v379_l880)))


(def v381_l884 (kind/doc #'elem/pow))


(def v382_l886 ((elem/pow (t/column [2]) 3) 0 0))


(deftest t383_l888 (is ((fn [v] (== 8.0 v)) v382_l886)))


(def v384_l890 (kind/doc #'elem/cbrt))


(def v385_l892 (la/close-scalar? ((elem/cbrt (t/column [27])) 0 0) 3.0))


(deftest t386_l894 (is (true? v385_l892)))


(def v387_l896 (kind/doc #'elem/floor))


(def v388_l898 ((elem/floor (t/column [2.7])) 0 0))


(deftest t389_l900 (is ((fn [v] (== 2.0 v)) v388_l898)))


(def v390_l902 (kind/doc #'elem/ceil))


(def v391_l904 ((elem/ceil (t/column [2.3])) 0 0))


(deftest t392_l906 (is ((fn [v] (== 3.0 v)) v391_l904)))


(def v393_l908 (kind/doc #'elem/min))


(def v394_l910 ((elem/min (t/column [3]) (t/column [5])) 0 0))


(deftest t395_l912 (is ((fn [v] (== 3.0 v)) v394_l910)))


(def v396_l914 (kind/doc #'elem/max))


(def v397_l916 ((elem/max (t/column [3]) (t/column [5])) 0 0))


(deftest t398_l918 (is ((fn [v] (== 5.0 v)) v397_l916)))


(def v399_l920 (kind/doc #'elem/asin))


(def v400_l922 ((elem/asin (t/column [0.5])) 0 0))


(deftest
 t401_l924
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v400_l922)))


(def v402_l926 (kind/doc #'elem/acos))


(def v403_l928 ((elem/acos (t/column [0.5])) 0 0))


(deftest
 t404_l930
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v403_l928)))


(def v405_l932 (kind/doc #'elem/atan))


(def v406_l934 ((elem/atan (t/column [1.0])) 0 0))


(deftest
 t407_l936
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v406_l934)))


(def v408_l938 (kind/doc #'elem/log1p))


(def v409_l940 ((elem/log1p (t/column [0.0])) 0 0))


(deftest t410_l942 (is ((fn [v] (la/close-scalar? v 0.0)) v409_l940)))


(def v411_l944 (kind/doc #'elem/expm1))


(def v412_l946 ((elem/expm1 (t/column [0.0])) 0 0))


(deftest t413_l948 (is ((fn [v] (la/close-scalar? v 0.0)) v412_l946)))


(def v414_l950 (kind/doc #'elem/round))


(def v415_l952 ((elem/round (t/column [2.7])) 0 0))


(deftest t416_l954 (is ((fn [v] (== 3.0 v)) v415_l952)))


(def v417_l956 (kind/doc #'elem/clip))


(def v418_l958 (t/flatten (elem/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t419_l960 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v418_l958)))


(def v420_l962 (kind/doc #'elem/div))


(def v421_l964 (elem/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t422_l966
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v421_l964)))


(def v423_l968 (kind/doc #'elem/gt))


(def v424_l970 (elem/gt (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t425_l972
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v424_l970)))


(def v426_l974 (kind/doc #'elem/reduce-max))


(def v427_l976 (elem/reduce-max (t/column [3 7 2 9 1])))


(deftest t428_l978 (is ((fn [v] (== 9.0 v)) v427_l976)))


(def v429_l980 (kind/doc #'elem/reduce-min))


(def v430_l982 (elem/reduce-min (t/column [3 7 2 9 1])))


(deftest t431_l984 (is ((fn [v] (== 1.0 v)) v430_l982)))


(def v433_l990 (kind/doc #'grad/grad))


(def
 v434_l992
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))
   grads
   (grad/grad tape-result (:result tape-result))]
  (la/close? (.get grads A) (la/scale A 2))))


(deftest t435_l998 (is (true? v434_l992)))


(def v437_l1004 (kind/doc #'vis/arrow-plot))


(def
 v438_l1006
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v439_l1010 (kind/doc #'vis/graph-plot))


(def
 v440_l1012
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v441_l1016 (kind/doc #'vis/matrix->gray-image))


(def
 v442_l1018
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t443_l1023
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v442_l1018)))


(def v444_l1025 (kind/doc #'vis/extract-channel))


(def
 v445_l1027
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t446_l1033
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v445_l1027)))
