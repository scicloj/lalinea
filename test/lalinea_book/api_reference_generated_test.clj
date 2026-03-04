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


(def v29_l84 (t/reduce-axis (t/matrix [[1 2 3] [4 5 6]]) el/sum 1))


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
 (t/clone (el/+ (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


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


(def v114_l278 (kind/doc #'la/mmul))


(def v115_l280 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t116_l283
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v115_l280)))


(def v117_l286 (kind/doc #'la/transpose))


(def v118_l288 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t119_l290 (is ((fn [m] (= [3 2] (t/shape m))) v118_l288)))


(def v120_l292 (kind/doc #'la/trace))


(def v121_l294 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t122_l296 (is ((fn [v] (== 5.0 v)) v121_l294)))


(def v123_l298 (kind/doc #'la/det))


(def v124_l300 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t125_l302 (is ((fn [v] (la/close-scalar? v -2.0)) v124_l300)))


(def v126_l304 (kind/doc #'la/norm))


(def v127_l306 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t128_l308 (is ((fn [v] (la/close-scalar? v 5.0)) v127_l306)))


(def v129_l310 (kind/doc #'la/dot))


(def v130_l312 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t131_l314 (is ((fn [v] (== 32.0 v)) v130_l312)))


(def v132_l316 (kind/doc #'la/close?))


(def v133_l318 (la/close? (t/eye 2) (t/eye 2)))


(deftest t134_l320 (is (true? v133_l318)))


(def v135_l322 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t136_l324 (is (false? v135_l322)))


(def v137_l326 (kind/doc #'la/close-scalar?))


(def v138_l328 (la/close-scalar? 1.00000000001 1.0))


(deftest t139_l330 (is (true? v138_l328)))


(def v140_l332 (kind/doc #'la/invert))


(def
 v141_l334
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t142_l337 (is (true? v141_l334)))


(def v143_l339 (kind/doc #'la/solve))


(def
 v145_l342
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t146_l346
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v145_l342)))


(def v147_l349 (kind/doc #'la/eigen))


(def
 v148_l351
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t149_l355
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v148_l351)))


(def v150_l359 (kind/doc #'la/real-eigenvalues))


(def v151_l361 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t152_l363
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v151_l361)))


(def v153_l366 (kind/doc #'la/svd))


(def
 v154_l368
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t155_l373
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v154_l368)))


(def v156_l378 (kind/doc #'la/qr))


(def
 v157_l380
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t158_l383 (is (true? v157_l380)))


(def v159_l385 (kind/doc #'la/cholesky))


(def
 v160_l387
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t161_l391 (is (true? v160_l387)))


(def v162_l393 (kind/doc #'la/mpow))


(def v163_l395 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t164_l397
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v163_l395)))


(def v165_l399 (kind/doc #'la/rank))


(def v166_l401 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t167_l403 (is ((fn [r] (= 1 r)) v166_l401)))


(def v168_l405 (kind/doc #'la/condition-number))


(def v169_l407 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t170_l409 (is ((fn [v] (> v 1.0)) v169_l407)))


(def v171_l411 (kind/doc #'la/pinv))


(def
 v172_l413
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t173_l416 (is (true? v172_l413)))


(def v174_l418 (kind/doc #'la/lstsq))


(def
 v175_l420
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t176_l424
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v175_l420)))


(def v177_l426 (kind/doc #'la/null-space))


(def
 v178_l428
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t179_l432 (is (true? v178_l428)))


(def v180_l434 (kind/doc #'la/col-space))


(def
 v181_l436
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t182_l438 (is ((fn [r] (= 1 r)) v181_l436)))


(def v183_l440 (kind/doc #'la/lift))


(def v185_l443 (la/lift el/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t186_l445
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v185_l443)))


(def v187_l448 (kind/doc #'la/lifted))


(def
 v189_l451
 (let [my-sqrt (la/lifted el/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t190_l454
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v189_l451)))


(def v192_l460 (kind/doc #'t/complex-tensor))


(def v193_l462 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t194_l464
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v193_l462)))


(def v195_l466 (kind/doc #'t/complex-tensor-real))


(def v196_l468 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t197_l470 (is ((fn [ct] (every? zero? (el/im ct))) v196_l468)))


(def v198_l472 (kind/doc #'t/complex))


(def v199_l474 (t/complex 3.0 4.0))


(deftest
 t200_l476
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (el/re ct)) (== 4.0 (el/im ct))))
   v199_l474)))


(def v201_l480 (kind/doc #'el/re))


(def v202_l482 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t203_l484 (is (= v202_l482 [1.0 2.0])))


(def v204_l486 (kind/doc #'el/im))


(def v205_l488 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t206_l490 (is (= v205_l488 [3.0 4.0])))


(def v207_l492 (kind/doc #'t/complex-shape))


(def
 v208_l494
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t209_l497 (is (= v208_l494 [2 2])))


(def v210_l499 (kind/doc #'t/scalar?))


(def v211_l501 (t/scalar? (t/complex 3.0 4.0)))


(deftest t212_l503 (is (true? v211_l501)))


(def v213_l505 (kind/doc #'t/complex?))


(def v214_l507 (t/complex? (t/complex 3.0 4.0)))


(deftest t215_l509 (is (true? v214_l507)))


(def v216_l511 (t/complex? (t/eye 2)))


(deftest t217_l513 (is (false? v216_l511)))


(def v218_l515 (kind/doc #'t/->tensor))


(def
 v219_l517
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t220_l519 (is (= v219_l517 [2 2])))


(def v221_l521 (kind/doc #'t/->double-array))


(def
 v222_l523
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t223_l526 (is (= v222_l523 [1.0 3.0 2.0 4.0])))


(def v224_l528 (kind/doc #'t/wrap-tensor))


(def
 v225_l530
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t226_l534
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v225_l530)))


(def
 v228_l541
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (el/re (el/+ a b))))


(deftest t229_l545 (is (= v228_l541 [11.0 22.0])))


(def
 v231_l549
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (el/* a b)]
  [(el/re (c 0)) (el/im (c 0))]))


(deftest t232_l554 (is (= v231_l549 [-10.0 10.0])))


(def v234_l558 (kind/doc #'el/conj))


(def
 v235_l560
 (let
  [ct (el/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (el/im ct)))


(deftest t236_l563 (is (= v235_l560 [-3.0 4.0])))


(def v238_l567 (kind/doc #'la/dot-conj))


(def
 v239_l569
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (el/re result) 30.0)))


(deftest t240_l573 (is (true? v239_l569)))


(def
 v242_l577
 (let
  [m (el/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t243_l580 (is (true? v242_l577)))


(def
 v245_l584
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (el/sum ct)]
  [(el/re s) (el/im s)]))


(deftest t246_l588 (is (= v245_l584 [6.0 15.0])))


(def v248_l594 (kind/doc #'ft/forward))


(def
 v249_l596
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t250_l600 (is (= v249_l596 [4])))


(def v251_l602 (kind/doc #'ft/inverse))


(def
 v252_l604
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (el/re (roundtrip 0)) 1.0)))


(deftest t253_l608 (is (true? v252_l604)))


(def v254_l610 (kind/doc #'ft/inverse-real))


(def
 v255_l612
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t256_l616 (is (true? v255_l612)))


(def v257_l618 (kind/doc #'ft/forward-complex))


(def
 v258_l620
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t259_l624 (is (= v258_l620 [4])))


(def v260_l626 (kind/doc #'ft/dct-forward))


(def v261_l628 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t262_l630 (is ((fn [v] (= 4 (count v))) v261_l628)))


(def v263_l632 (kind/doc #'ft/dct-inverse))


(def
 v264_l634
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t265_l638 (is (true? v264_l634)))


(def v266_l640 (kind/doc #'ft/dst-forward))


(def v267_l642 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t268_l644 (is ((fn [v] (= 4 (count v))) v267_l642)))


(def v269_l646 (kind/doc #'ft/dst-inverse))


(def
 v270_l648
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t271_l652 (is (true? v270_l648)))


(def v272_l654 (kind/doc #'ft/dht-forward))


(def v273_l656 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t274_l658 (is ((fn [v] (= 4 (count v))) v273_l656)))


(def v275_l660 (kind/doc #'ft/dht-inverse))


(def
 v276_l662
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t277_l666 (is (true? v276_l662)))


(def v279_l672 (kind/doc #'tape/memory-status))


(def v280_l674 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t281_l676 (is ((fn [s] (= :contiguous s)) v280_l674)))


(def
 v282_l678
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t283_l680 (is ((fn [s] (= :strided s)) v282_l678)))


(def v284_l682 (tape/memory-status (el/+ (t/eye 2) (t/eye 2))))


(deftest t285_l684 (is ((fn [s] (= :lazy s)) v284_l682)))


(def v286_l686 (kind/doc #'tape/memory-relation))


(def
 v287_l688
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t288_l691 (is ((fn [r] (= :shared r)) v287_l688)))


(def
 v289_l693
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t290_l695 (is ((fn [r] (= :independent r)) v289_l693)))


(def
 v291_l697
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (el/+ (t/eye 2) (t/eye 2))))


(deftest t292_l699 (is ((fn [r] (= :unknown-lazy r)) v291_l697)))


(def v293_l701 (kind/doc #'tape/with-tape))


(def
 v294_l703
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (el/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v295_l709 (select-keys tape-example [:result :entries]))


(deftest
 t296_l711
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v295_l709)))


(def v297_l714 (kind/doc #'tape/summary))


(def v298_l716 (tape/summary tape-example))


(deftest t299_l718 (is ((fn [s] (= 4 (:total s))) v298_l716)))


(def v300_l720 (kind/doc #'tape/origin))


(def v301_l722 (tape/origin tape-example (:result tape-example)))


(deftest t302_l724 (is ((fn [dag] (= :la/mmul (:op dag))) v301_l722)))


(def v303_l726 (kind/doc #'tape/mermaid))


(def v305_l730 (tape/mermaid tape-example (:result tape-example)))


(def v306_l732 (kind/doc #'tape/detect-memory-status))


(def v308_l737 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t309_l739
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v308_l737)))


(def v311_l747 (kind/doc #'el/+))


(def v312_l749 (el/+ (t/column [1 2 3]) (t/column [10 20 30])))


(deftest t313_l751 (is ((fn [v] (== 11.0 (v 0 0))) v312_l749)))


(def v314_l753 (kind/doc #'el/-))


(def v315_l755 (el/- (t/column [10 20 30]) (t/column [1 2 3])))


(deftest t316_l757 (is ((fn [v] (== 9.0 (v 0 0))) v315_l755)))


(def v317_l759 (kind/doc #'el/scale))


(def v318_l761 (el/scale (t/column [2 3 4]) 5.0))


(deftest t319_l763 (is ((fn [v] (== 10.0 (v 0 0))) v318_l761)))


(def v320_l765 (kind/doc #'el/*))


(def v321_l767 (el/* (t/column [2 3 4]) (t/column [10 20 30])))


(deftest t322_l769 (is ((fn [v] (== 20.0 (v 0 0))) v321_l767)))


(def v323_l771 (kind/doc #'el/re))


(def v324_l773 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t325_l775 (is (= v324_l773 [1.0 2.0])))


(def v326_l777 (kind/doc #'el/im))


(def v327_l779 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t328_l781 (is (= v327_l779 [3.0 4.0])))


(def v329_l783 (kind/doc #'el/conj))


(def v330_l785 (let [z (t/complex 3.0 4.0)] (el/im (el/conj z))))


(deftest t331_l788 (is ((fn [v] (la/close-scalar? v -4.0)) v330_l785)))


(def v332_l790 (kind/doc #'el/reduce-*))


(def v333_l792 (el/reduce-* (t/column [2 3 4])))


(deftest t334_l794 (is ((fn [v] (== 24.0 v)) v333_l792)))


(def v335_l796 (kind/doc #'el/sq))


(def v336_l798 (el/sq (t/column [2 3 4])))


(deftest
 t337_l800
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v336_l798)))


(def v338_l802 (kind/doc #'el/sqrt))


(def v339_l804 (el/sqrt (t/column [4 9 16])))


(deftest
 t340_l806
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v339_l804)))


(def v341_l808 (kind/doc #'el/exp))


(def v342_l810 (la/close-scalar? ((el/exp (t/column [0])) 0 0) 1.0))


(deftest t343_l812 (is (true? v342_l810)))


(def v344_l814 (kind/doc #'el/log))


(def
 v345_l816
 (la/close-scalar? ((el/log (t/column [math/E])) 0 0) 1.0))


(deftest t346_l818 (is (true? v345_l816)))


(def v347_l820 (kind/doc #'el/log10))


(def v348_l822 (la/close-scalar? ((el/log10 (t/column [100])) 0 0) 2.0))


(deftest t349_l824 (is (true? v348_l822)))


(def v350_l826 (kind/doc #'el/sin))


(def
 v351_l828
 (la/close-scalar? ((el/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t352_l830 (is (true? v351_l828)))


(def v353_l832 (kind/doc #'el/cos))


(def v354_l834 (la/close-scalar? ((el/cos (t/column [0])) 0 0) 1.0))


(deftest t355_l836 (is (true? v354_l834)))


(def v356_l838 (kind/doc #'el/tan))


(def
 v357_l840
 (la/close-scalar? ((el/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t358_l842 (is (true? v357_l840)))


(def v359_l844 (kind/doc #'el/sinh))


(def v360_l846 (la/close-scalar? ((el/sinh (t/column [0])) 0 0) 0.0))


(deftest t361_l848 (is (true? v360_l846)))


(def v362_l850 (kind/doc #'el/cosh))


(def v363_l852 (la/close-scalar? ((el/cosh (t/column [0])) 0 0) 1.0))


(deftest t364_l854 (is (true? v363_l852)))


(def v365_l856 (kind/doc #'el/tanh))


(def v366_l858 (la/close-scalar? ((el/tanh (t/column [0])) 0 0) 0.0))


(deftest t367_l860 (is (true? v366_l858)))


(def v368_l862 (kind/doc #'el/abs))


(def v369_l864 ((el/abs (t/column [-5])) 0 0))


(deftest t370_l866 (is ((fn [v] (== 5.0 v)) v369_l864)))


(def v371_l868 (kind/doc #'el/sum))


(def v372_l870 (el/sum (t/column [1 2 3 4])))


(deftest t373_l872 (is ((fn [v] (== 10.0 v)) v372_l870)))


(def v374_l874 (kind/doc #'el/mean))


(def v375_l876 (el/mean (t/column [2 4 6])))


(deftest t376_l878 (is ((fn [v] (== 4.0 v)) v375_l876)))


(def v377_l880 (kind/doc #'el/pow))


(def v378_l882 ((el/pow (t/column [2]) 3) 0 0))


(deftest t379_l884 (is ((fn [v] (== 8.0 v)) v378_l882)))


(def v380_l886 (kind/doc #'el/cbrt))


(def v381_l888 (la/close-scalar? ((el/cbrt (t/column [27])) 0 0) 3.0))


(deftest t382_l890 (is (true? v381_l888)))


(def v383_l892 (kind/doc #'el/floor))


(def v384_l894 ((el/floor (t/column [2.7])) 0 0))


(deftest t385_l896 (is ((fn [v] (== 2.0 v)) v384_l894)))


(def v386_l898 (kind/doc #'el/ceil))


(def v387_l900 ((el/ceil (t/column [2.3])) 0 0))


(deftest t388_l902 (is ((fn [v] (== 3.0 v)) v387_l900)))


(def v389_l904 (kind/doc #'el/min))


(def v390_l906 ((el/min (t/column [3]) (t/column [5])) 0 0))


(deftest t391_l908 (is ((fn [v] (== 3.0 v)) v390_l906)))


(def v392_l910 (kind/doc #'el/max))


(def v393_l912 ((el/max (t/column [3]) (t/column [5])) 0 0))


(deftest t394_l914 (is ((fn [v] (== 5.0 v)) v393_l912)))


(def v395_l916 (kind/doc #'el/asin))


(def v396_l918 ((el/asin (t/column [0.5])) 0 0))


(deftest
 t397_l920
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v396_l918)))


(def v398_l922 (kind/doc #'el/acos))


(def v399_l924 ((el/acos (t/column [0.5])) 0 0))


(deftest
 t400_l926
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v399_l924)))


(def v401_l928 (kind/doc #'el/atan))


(def v402_l930 ((el/atan (t/column [1.0])) 0 0))


(deftest
 t403_l932
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v402_l930)))


(def v404_l934 (kind/doc #'el/log1p))


(def v405_l936 ((el/log1p (t/column [0.0])) 0 0))


(deftest t406_l938 (is ((fn [v] (la/close-scalar? v 0.0)) v405_l936)))


(def v407_l940 (kind/doc #'el/expm1))


(def v408_l942 ((el/expm1 (t/column [0.0])) 0 0))


(deftest t409_l944 (is ((fn [v] (la/close-scalar? v 0.0)) v408_l942)))


(def v410_l946 (kind/doc #'el/round))


(def v411_l948 ((el/round (t/column [2.7])) 0 0))


(deftest t412_l950 (is ((fn [v] (== 3.0 v)) v411_l948)))


(def v413_l952 (kind/doc #'el/clip))


(def v414_l954 (t/flatten (el/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t415_l956 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v414_l954)))


(def v416_l958 (kind/doc #'el//))


(def v417_l960 (el// (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t418_l962
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v417_l960)))


(def v420_l966 (el// (t/complex 3.0 4.0) (t/complex 1.0 2.0)))


(deftest
 t421_l968
 (is
  ((fn
    [v]
    (and
     (< (abs (- (el/re v) 2.2)) 1.0E-10)
     (< (abs (- (el/im v) -0.4)) 1.0E-10)))
   v420_l966)))


(def v422_l971 (kind/doc #'el/>))


(def v423_l973 (el/> (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t424_l975
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v423_l973)))


(def v425_l977 (kind/doc #'el/<))


(def v426_l979 (el/< (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t427_l981
 (is ((fn [v] (= [1.0 0.0 0.0] (t/flatten v))) v426_l979)))


(def v428_l983 (kind/doc #'el/>=))


(def v429_l985 (el/>= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t430_l987
 (is ((fn [v] (= [0.0 1.0 1.0] (t/flatten v))) v429_l985)))


(def v431_l989 (kind/doc #'el/<=))


(def v432_l991 (el/<= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t433_l993
 (is ((fn [v] (= [1.0 0.0 1.0] (t/flatten v))) v432_l991)))


(def v434_l995 (kind/doc #'el/eq))


(def v435_l997 (el/eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t436_l999
 (is ((fn [v] (= [0.0 0.0 1.0] (t/flatten v))) v435_l997)))


(def v437_l1001 (kind/doc #'el/not-eq))


(def v438_l1003 (el/not-eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t439_l1005
 (is ((fn [v] (= [1.0 1.0 0.0] (t/flatten v))) v438_l1003)))


(def v440_l1007 (kind/doc #'el/reduce-max))


(def v441_l1009 (el/reduce-max (t/column [3 7 2 9 1])))


(deftest t442_l1011 (is ((fn [v] (== 9.0 v)) v441_l1009)))


(def v443_l1013 (kind/doc #'el/reduce-min))


(def v444_l1015 (el/reduce-min (t/column [3 7 2 9 1])))


(deftest t445_l1017 (is ((fn [v] (== 1.0 v)) v444_l1015)))


(def v447_l1023 (kind/doc #'grad/grad))


(def
 v448_l1025
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))]
  (la/close?
   (grad/grad tape-result (:result tape-result) A)
   (el/scale A 2))))


(deftest t449_l1032 (is (true? v448_l1025)))


(def v451_l1038 (kind/doc #'vis/arrow-plot))


(def
 v452_l1040
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v453_l1044 (kind/doc #'vis/graph-plot))


(def
 v454_l1046
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v455_l1050 (kind/doc #'vis/matrix->gray-image))


(def
 v456_l1052
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t457_l1057
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v456_l1052)))


(def v458_l1059 (kind/doc #'vis/extract-channel))


(def
 v459_l1061
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t460_l1067
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v459_l1061)))
