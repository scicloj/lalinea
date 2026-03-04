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
 (let [m (t/matrix [[1 2] [3 4]])] (identical? m (t/clone m))))


(deftest t85_l207 (is (false? v84_l204)))


(def v86_l209 (kind/doc #'t/concrete?))


(def v88_l212 (t/concrete? (t/matrix [[1 2] [3 4]])))


(deftest t89_l214 (is (true? v88_l212)))


(def
 v91_l217
 (t/concrete?
  (el/+ (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest t92_l220 (is (false? v91_l217)))


(def v93_l222 (kind/doc #'t/materialize))


(def
 v95_l225
 (let [m (t/matrix [[1 2] [3 4]])] (identical? m (t/materialize m))))


(deftest t96_l228 (is (true? v95_l225)))


(def
 v98_l231
 (t/materialize
  (el/+ (t/matrix [[1 2] [3 4]]) (t/matrix [[10 20] [30 40]]))))


(deftest
 t99_l234
 (is ((fn [m] (= [[11.0 22.0] [33.0 44.0]] m)) v98_l231)))


(def v100_l236 (kind/doc #'t/make-reader))


(def v101_l238 (t/make-reader :float64 5 (* idx idx)))


(deftest t102_l240 (is ((fn [r] (= 16.0 (r 4))) v101_l238)))


(def v103_l242 (kind/doc #'t/make-container))


(def v104_l244 (t/make-container :float64 4))


(deftest t105_l246 (is ((fn [c] (= 4 (count c))) v104_l244)))


(def v106_l248 (kind/doc #'t/elemwise-cast))


(def v107_l250 (t/elemwise-cast (t/matrix [[1 2] [3 4]]) :int32))


(deftest
 t108_l252
 (is
  ((fn [m] (= :int32 (tech.v3.datatype/elemwise-datatype m)))
   v107_l250)))


(def v109_l254 (kind/doc #'t/mset!))


(def
 v110_l256
 (let
  [m (t/clone (t/matrix [[1 2] [3 4]]))]
  (t/mset! m 0 0 99.0)
  (m 0 0)))


(deftest t111_l260 (is ((fn [v] (== 99.0 v)) v110_l256)))


(def v112_l262 (kind/doc #'t/set-value!))


(def
 v113_l264
 (let
  [buf (t/make-container :float64 3)]
  (t/set-value! buf 1 42.0)
  (buf 1)))


(deftest t114_l268 (is ((fn [v] (== 42.0 v)) v113_l264)))


(def v115_l270 (kind/doc #'t/->double-array))


(def
 v116_l272
 (let [arr (t/->double-array (t/matrix [[1 2] [3 4]]))] (alength arr)))


(deftest t117_l275 (is ((fn [n] (= 4 n)) v116_l272)))


(def v118_l277 (kind/doc #'t/backing-array))


(def
 v119_l279
 (let
  [A (t/matrix [[1 2] [3 4]]) B (t/clone A)]
  [(some? (t/backing-array A))
   (identical? (t/backing-array A) (t/backing-array B))]))


(deftest t120_l285 (is (= v119_l279 [true false])))


(def v121_l287 (kind/doc #'t/->reader))


(def v122_l289 (let [rdr (t/->reader (t/column [10 20 30]))] (rdr 2)))


(deftest t123_l292 (is ((fn [v] (== 30.0 v)) v122_l289)))


(def v124_l294 (kind/doc #'t/array-buffer))


(def v125_l296 (some? (t/array-buffer (t/clone (t/eye 3)))))


(deftest t126_l298 (is (true? v125_l296)))


(def v128_l305 (kind/doc #'la/mmul))


(def v129_l307 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t130_l310
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v129_l307)))


(def v131_l313 (kind/doc #'la/transpose))


(def v132_l315 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t133_l317 (is ((fn [m] (= [3 2] (t/shape m))) v132_l315)))


(def v134_l319 (kind/doc #'la/trace))


(def v135_l321 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t136_l323 (is ((fn [v] (== 5.0 v)) v135_l321)))


(def v137_l325 (kind/doc #'la/det))


(def v138_l327 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t139_l329 (is ((fn [v] (la/close-scalar? v -2.0)) v138_l327)))


(def v140_l331 (kind/doc #'la/norm))


(def v141_l333 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t142_l335 (is ((fn [v] (la/close-scalar? v 5.0)) v141_l333)))


(def v143_l337 (kind/doc #'la/dot))


(def v144_l339 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t145_l341 (is ((fn [v] (== 32.0 v)) v144_l339)))


(def v146_l343 (kind/doc #'la/close?))


(def v147_l345 (la/close? (t/eye 2) (t/eye 2)))


(deftest t148_l347 (is (true? v147_l345)))


(def v149_l349 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t150_l351 (is (false? v149_l349)))


(def v151_l353 (kind/doc #'la/close-scalar?))


(def v152_l355 (la/close-scalar? 1.00000000001 1.0))


(deftest t153_l357 (is (true? v152_l355)))


(def v154_l359 (kind/doc #'la/invert))


(def
 v155_l361
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t156_l364 (is (true? v155_l361)))


(def v157_l366 (kind/doc #'la/solve))


(def
 v159_l369
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t160_l373
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v159_l369)))


(def v161_l376 (kind/doc #'la/eigen))


(def
 v162_l378
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t163_l382
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v162_l378)))


(def v164_l386 (kind/doc #'la/real-eigenvalues))


(def v165_l388 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t166_l390
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v165_l388)))


(def v167_l393 (kind/doc #'la/svd))


(def
 v168_l395
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t169_l400
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v168_l395)))


(def v170_l405 (kind/doc #'la/qr))


(def
 v171_l407
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t172_l410 (is (true? v171_l407)))


(def v173_l412 (kind/doc #'la/cholesky))


(def
 v174_l414
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t175_l418 (is (true? v174_l414)))


(def v176_l420 (kind/doc #'la/mpow))


(def v177_l422 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t178_l424
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v177_l422)))


(def v179_l426 (kind/doc #'la/rank))


(def v180_l428 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t181_l430 (is ((fn [r] (= 1 r)) v180_l428)))


(def v182_l432 (kind/doc #'la/condition-number))


(def v183_l434 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t184_l436 (is ((fn [v] (> v 1.0)) v183_l434)))


(def v185_l438 (kind/doc #'la/pinv))


(def
 v186_l440
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t187_l443 (is (true? v186_l440)))


(def v188_l445 (kind/doc #'la/lstsq))


(def
 v189_l447
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t190_l451
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v189_l447)))


(def v191_l453 (kind/doc #'la/null-space))


(def
 v192_l455
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t193_l459 (is (true? v192_l455)))


(def v194_l461 (kind/doc #'la/col-space))


(def
 v195_l463
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t196_l465 (is ((fn [r] (= 1 r)) v195_l463)))


(def v197_l467 (kind/doc #'la/lift))


(def v199_l470 (la/lift el/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t200_l472
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v199_l470)))


(def v201_l475 (kind/doc #'la/lifted))


(def
 v203_l478
 (let [my-sqrt (la/lifted el/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t204_l481
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v203_l478)))


(def v206_l487 (kind/doc #'t/complex-tensor))


(def v207_l489 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t208_l491
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v207_l489)))


(def v209_l493 (kind/doc #'t/complex-tensor-real))


(def v210_l495 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t211_l497 (is ((fn [ct] (every? zero? (el/im ct))) v210_l495)))


(def v212_l499 (kind/doc #'t/complex))


(def v213_l501 (t/complex 3.0 4.0))


(deftest
 t214_l503
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (el/re ct)) (== 4.0 (el/im ct))))
   v213_l501)))


(def v215_l507 (kind/doc #'el/re))


(def v216_l509 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t217_l511 (is (= v216_l509 [1.0 2.0])))


(def v218_l513 (kind/doc #'el/im))


(def v219_l515 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t220_l517 (is (= v219_l515 [3.0 4.0])))


(def v221_l519 (kind/doc #'t/complex-shape))


(def
 v222_l521
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t223_l524 (is (= v222_l521 [2 2])))


(def v224_l526 (kind/doc #'t/scalar?))


(def v225_l528 (t/scalar? (t/complex 3.0 4.0)))


(deftest t226_l530 (is (true? v225_l528)))


(def v227_l532 (kind/doc #'t/complex?))


(def v228_l534 (t/complex? (t/complex 3.0 4.0)))


(deftest t229_l536 (is (true? v228_l534)))


(def v230_l538 (t/complex? (t/eye 2)))


(deftest t231_l540 (is (false? v230_l538)))


(def v232_l542 (kind/doc #'t/->tensor))


(def
 v233_l544
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t234_l546 (is (= v233_l544 [2 2])))


(def v235_l548 (kind/doc #'t/->double-array))


(def
 v236_l550
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t237_l553 (is (= v236_l550 [1.0 3.0 2.0 4.0])))


(def v238_l555 (kind/doc #'t/wrap-tensor))


(def
 v239_l557
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t240_l561
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v239_l557)))


(def
 v242_l568
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (el/re (el/+ a b))))


(deftest t243_l572 (is (= v242_l568 [11.0 22.0])))


(def
 v245_l576
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (el/* a b)]
  [(el/re (c 0)) (el/im (c 0))]))


(deftest t246_l581 (is (= v245_l576 [-10.0 10.0])))


(def v248_l585 (kind/doc #'el/conj))


(def
 v249_l587
 (let
  [ct (el/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (el/im ct)))


(deftest t250_l590 (is (= v249_l587 [-3.0 4.0])))


(def v252_l594 (kind/doc #'la/dot-conj))


(def
 v253_l596
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (el/re result) 30.0)))


(deftest t254_l600 (is (true? v253_l596)))


(def
 v256_l604
 (let
  [m (el/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t257_l607 (is (true? v256_l604)))


(def
 v259_l611
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (el/sum ct)]
  [(el/re s) (el/im s)]))


(deftest t260_l615 (is (= v259_l611 [6.0 15.0])))


(def v262_l621 (kind/doc #'ft/forward))


(def
 v263_l623
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t264_l627 (is (= v263_l623 [4])))


(def v265_l629 (kind/doc #'ft/inverse))


(def
 v266_l631
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (el/re (roundtrip 0)) 1.0)))


(deftest t267_l635 (is (true? v266_l631)))


(def v268_l637 (kind/doc #'ft/inverse-real))


(def
 v269_l639
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t270_l643 (is (true? v269_l639)))


(def v271_l645 (kind/doc #'ft/forward-complex))


(def
 v272_l647
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t273_l651 (is (= v272_l647 [4])))


(def v274_l653 (kind/doc #'ft/dct-forward))


(def v275_l655 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t276_l657 (is ((fn [v] (= 4 (count v))) v275_l655)))


(def v277_l659 (kind/doc #'ft/dct-inverse))


(def
 v278_l661
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t279_l665 (is (true? v278_l661)))


(def v280_l667 (kind/doc #'ft/dst-forward))


(def v281_l669 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t282_l671 (is ((fn [v] (= 4 (count v))) v281_l669)))


(def v283_l673 (kind/doc #'ft/dst-inverse))


(def
 v284_l675
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t285_l679 (is (true? v284_l675)))


(def v286_l681 (kind/doc #'ft/dht-forward))


(def v287_l683 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t288_l685 (is ((fn [v] (= 4 (count v))) v287_l683)))


(def v289_l687 (kind/doc #'ft/dht-inverse))


(def
 v290_l689
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t291_l693 (is (true? v290_l689)))


(def v293_l699 (kind/doc #'tape/memory-status))


(def v294_l701 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t295_l703 (is ((fn [s] (= :contiguous s)) v294_l701)))


(def
 v296_l705
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t297_l707 (is ((fn [s] (= :strided s)) v296_l705)))


(def v298_l709 (tape/memory-status (el/+ (t/eye 2) (t/eye 2))))


(deftest t299_l711 (is ((fn [s] (= :lazy s)) v298_l709)))


(def v300_l713 (kind/doc #'tape/memory-relation))


(def
 v301_l715
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t302_l718 (is ((fn [r] (= :shared r)) v301_l715)))


(def
 v303_l720
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t304_l722 (is ((fn [r] (= :independent r)) v303_l720)))


(def
 v305_l724
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (el/+ (t/eye 2) (t/eye 2))))


(deftest t306_l726 (is ((fn [r] (= :unknown-lazy r)) v305_l724)))


(def v307_l728 (kind/doc #'tape/with-tape))


(def
 v308_l730
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (el/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v309_l736 (select-keys tape-example [:result :entries]))


(deftest
 t310_l738
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v309_l736)))


(def v311_l741 (kind/doc #'tape/summary))


(def v312_l743 (tape/summary tape-example))


(deftest t313_l745 (is ((fn [s] (= 4 (:total s))) v312_l743)))


(def v314_l747 (kind/doc #'tape/origin))


(def v315_l749 (tape/origin tape-example (:result tape-example)))


(deftest t316_l751 (is ((fn [dag] (= :la/mmul (:op dag))) v315_l749)))


(def v317_l753 (kind/doc #'tape/mermaid))


(def v319_l757 (tape/mermaid tape-example (:result tape-example)))


(def v320_l759 (kind/doc #'tape/detect-memory-status))


(def v322_l764 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t323_l766
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v322_l764)))


(def v325_l774 (kind/doc #'el/+))


(def v326_l776 (el/+ (t/column [1 2 3]) (t/column [10 20 30])))


(deftest t327_l778 (is ((fn [v] (== 11.0 (v 0 0))) v326_l776)))


(def v328_l780 (kind/doc #'el/-))


(def v329_l782 (el/- (t/column [10 20 30]) (t/column [1 2 3])))


(deftest t330_l784 (is ((fn [v] (== 9.0 (v 0 0))) v329_l782)))


(def v331_l786 (kind/doc #'el/scale))


(def v332_l788 (el/scale (t/column [2 3 4]) 5.0))


(deftest t333_l790 (is ((fn [v] (== 10.0 (v 0 0))) v332_l788)))


(def v334_l792 (kind/doc #'el/*))


(def v335_l794 (el/* (t/column [2 3 4]) (t/column [10 20 30])))


(deftest t336_l796 (is ((fn [v] (== 20.0 (v 0 0))) v335_l794)))


(def v337_l798 (kind/doc #'el/re))


(def v338_l800 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t339_l802 (is (= v338_l800 [1.0 2.0])))


(def v340_l804 (kind/doc #'el/im))


(def v341_l806 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t342_l808 (is (= v341_l806 [3.0 4.0])))


(def v343_l810 (kind/doc #'el/conj))


(def v344_l812 (let [z (t/complex 3.0 4.0)] (el/im (el/conj z))))


(deftest t345_l815 (is ((fn [v] (la/close-scalar? v -4.0)) v344_l812)))


(def v346_l817 (kind/doc #'el/reduce-*))


(def v347_l819 (el/reduce-* (t/column [2 3 4])))


(deftest t348_l821 (is ((fn [v] (== 24.0 v)) v347_l819)))


(def v349_l823 (kind/doc #'el/sq))


(def v350_l825 (el/sq (t/column [2 3 4])))


(deftest
 t351_l827
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v350_l825)))


(def v352_l829 (kind/doc #'el/sqrt))


(def v353_l831 (el/sqrt (t/column [4 9 16])))


(deftest
 t354_l833
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v353_l831)))


(def v355_l835 (kind/doc #'el/exp))


(def v356_l837 (la/close-scalar? ((el/exp (t/column [0])) 0 0) 1.0))


(deftest t357_l839 (is (true? v356_l837)))


(def v358_l841 (kind/doc #'el/log))


(def
 v359_l843
 (la/close-scalar? ((el/log (t/column [math/E])) 0 0) 1.0))


(deftest t360_l845 (is (true? v359_l843)))


(def v361_l847 (kind/doc #'el/log10))


(def v362_l849 (la/close-scalar? ((el/log10 (t/column [100])) 0 0) 2.0))


(deftest t363_l851 (is (true? v362_l849)))


(def v364_l853 (kind/doc #'el/sin))


(def
 v365_l855
 (la/close-scalar? ((el/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t366_l857 (is (true? v365_l855)))


(def v367_l859 (kind/doc #'el/cos))


(def v368_l861 (la/close-scalar? ((el/cos (t/column [0])) 0 0) 1.0))


(deftest t369_l863 (is (true? v368_l861)))


(def v370_l865 (kind/doc #'el/tan))


(def
 v371_l867
 (la/close-scalar? ((el/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t372_l869 (is (true? v371_l867)))


(def v373_l871 (kind/doc #'el/sinh))


(def v374_l873 (la/close-scalar? ((el/sinh (t/column [0])) 0 0) 0.0))


(deftest t375_l875 (is (true? v374_l873)))


(def v376_l877 (kind/doc #'el/cosh))


(def v377_l879 (la/close-scalar? ((el/cosh (t/column [0])) 0 0) 1.0))


(deftest t378_l881 (is (true? v377_l879)))


(def v379_l883 (kind/doc #'el/tanh))


(def v380_l885 (la/close-scalar? ((el/tanh (t/column [0])) 0 0) 0.0))


(deftest t381_l887 (is (true? v380_l885)))


(def v382_l889 (kind/doc #'el/abs))


(def v383_l891 ((el/abs (t/column [-5])) 0 0))


(deftest t384_l893 (is ((fn [v] (== 5.0 v)) v383_l891)))


(def v385_l895 (kind/doc #'el/sum))


(def v386_l897 (el/sum (t/column [1 2 3 4])))


(deftest t387_l899 (is ((fn [v] (== 10.0 v)) v386_l897)))


(def v388_l901 (kind/doc #'el/mean))


(def v389_l903 (el/mean (t/column [2 4 6])))


(deftest t390_l905 (is ((fn [v] (== 4.0 v)) v389_l903)))


(def v391_l907 (kind/doc #'el/pow))


(def v392_l909 ((el/pow (t/column [2]) 3) 0 0))


(deftest t393_l911 (is ((fn [v] (== 8.0 v)) v392_l909)))


(def v394_l913 (kind/doc #'el/cbrt))


(def v395_l915 (la/close-scalar? ((el/cbrt (t/column [27])) 0 0) 3.0))


(deftest t396_l917 (is (true? v395_l915)))


(def v397_l919 (kind/doc #'el/floor))


(def v398_l921 ((el/floor (t/column [2.7])) 0 0))


(deftest t399_l923 (is ((fn [v] (== 2.0 v)) v398_l921)))


(def v400_l925 (kind/doc #'el/ceil))


(def v401_l927 ((el/ceil (t/column [2.3])) 0 0))


(deftest t402_l929 (is ((fn [v] (== 3.0 v)) v401_l927)))


(def v403_l931 (kind/doc #'el/min))


(def v404_l933 ((el/min (t/column [3]) (t/column [5])) 0 0))


(deftest t405_l935 (is ((fn [v] (== 3.0 v)) v404_l933)))


(def v406_l937 (kind/doc #'el/max))


(def v407_l939 ((el/max (t/column [3]) (t/column [5])) 0 0))


(deftest t408_l941 (is ((fn [v] (== 5.0 v)) v407_l939)))


(def v409_l943 (kind/doc #'el/asin))


(def v410_l945 ((el/asin (t/column [0.5])) 0 0))


(deftest
 t411_l947
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v410_l945)))


(def v412_l949 (kind/doc #'el/acos))


(def v413_l951 ((el/acos (t/column [0.5])) 0 0))


(deftest
 t414_l953
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v413_l951)))


(def v415_l955 (kind/doc #'el/atan))


(def v416_l957 ((el/atan (t/column [1.0])) 0 0))


(deftest
 t417_l959
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v416_l957)))


(def v418_l961 (kind/doc #'el/log1p))


(def v419_l963 ((el/log1p (t/column [0.0])) 0 0))


(deftest t420_l965 (is ((fn [v] (la/close-scalar? v 0.0)) v419_l963)))


(def v421_l967 (kind/doc #'el/expm1))


(def v422_l969 ((el/expm1 (t/column [0.0])) 0 0))


(deftest t423_l971 (is ((fn [v] (la/close-scalar? v 0.0)) v422_l969)))


(def v424_l973 (kind/doc #'el/round))


(def v425_l975 ((el/round (t/column [2.7])) 0 0))


(deftest t426_l977 (is ((fn [v] (== 3.0 v)) v425_l975)))


(def v427_l979 (kind/doc #'el/clip))


(def v428_l981 (t/flatten (el/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t429_l983 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v428_l981)))


(def v430_l985 (kind/doc #'el//))


(def v431_l987 (el// (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t432_l989
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v431_l987)))


(def v434_l993 (el// (t/complex 3.0 4.0) (t/complex 1.0 2.0)))


(deftest
 t435_l995
 (is
  ((fn
    [v]
    (and
     (< (abs (- (el/re v) 2.2)) 1.0E-10)
     (< (abs (- (el/im v) -0.4)) 1.0E-10)))
   v434_l993)))


(def v436_l998 (kind/doc #'el/>))


(def v437_l1000 (el/> (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t438_l1002
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v437_l1000)))


(def v439_l1004 (kind/doc #'el/<))


(def v440_l1006 (el/< (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t441_l1008
 (is ((fn [v] (= [1.0 0.0 0.0] (t/flatten v))) v440_l1006)))


(def v442_l1010 (kind/doc #'el/>=))


(def v443_l1012 (el/>= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t444_l1014
 (is ((fn [v] (= [0.0 1.0 1.0] (t/flatten v))) v443_l1012)))


(def v445_l1016 (kind/doc #'el/<=))


(def v446_l1018 (el/<= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t447_l1020
 (is ((fn [v] (= [1.0 0.0 1.0] (t/flatten v))) v446_l1018)))


(def v448_l1022 (kind/doc #'el/eq))


(def v449_l1024 (el/eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t450_l1026
 (is ((fn [v] (= [0.0 0.0 1.0] (t/flatten v))) v449_l1024)))


(def v451_l1028 (kind/doc #'el/not-eq))


(def v452_l1030 (el/not-eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t453_l1032
 (is ((fn [v] (= [1.0 1.0 0.0] (t/flatten v))) v452_l1030)))


(def v454_l1034 (kind/doc #'el/reduce-max))


(def v455_l1036 (el/reduce-max (t/column [3 7 2 9 1])))


(deftest t456_l1038 (is ((fn [v] (== 9.0 v)) v455_l1036)))


(def v457_l1040 (kind/doc #'el/reduce-min))


(def v458_l1042 (el/reduce-min (t/column [3 7 2 9 1])))


(deftest t459_l1044 (is ((fn [v] (== 1.0 v)) v458_l1042)))


(def v461_l1050 (kind/doc #'grad/grad))


(def
 v462_l1052
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))]
  (la/close?
   (grad/grad tape-result (:result tape-result) A)
   (el/scale A 2))))


(deftest t463_l1059 (is (true? v462_l1052)))


(def v465_l1065 (kind/doc #'vis/arrow-plot))


(def
 v466_l1067
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v467_l1071 (kind/doc #'vis/graph-plot))


(def
 v468_l1073
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v469_l1077 (kind/doc #'vis/matrix->gray-image))


(def
 v470_l1079
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t471_l1084
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v470_l1079)))


(def v472_l1086 (kind/doc #'vis/extract-channel))


(def
 v473_l1088
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t474_l1094
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v473_l1088)))
