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


(def v123_l298 (kind/doc #'la/mmul))


(def v124_l300 (la/mmul (t/matrix [[1 2] [3 4]]) (t/column [5 6])))


(deftest
 t125_l303
 (is
  ((fn [m] (and (= [2 1] (t/shape m)) (== 17.0 (m 0 0)))) v124_l300)))


(def v126_l306 (kind/doc #'la/transpose))


(def v127_l308 (la/transpose (t/matrix [[1 2 3] [4 5 6]])))


(deftest t128_l310 (is ((fn [m] (= [3 2] (t/shape m))) v127_l308)))


(def v129_l312 (kind/doc #'la/trace))


(def v130_l314 (la/trace (t/matrix [[1 2] [3 4]])))


(deftest t131_l316 (is ((fn [v] (== 5.0 v)) v130_l314)))


(def v132_l318 (kind/doc #'la/det))


(def v133_l320 (la/det (t/matrix [[1 2] [3 4]])))


(deftest t134_l322 (is ((fn [v] (la/close-scalar? v -2.0)) v133_l320)))


(def v135_l324 (kind/doc #'la/norm))


(def v136_l326 (la/norm (t/matrix [[3 0] [0 4]])))


(deftest t137_l328 (is ((fn [v] (la/close-scalar? v 5.0)) v136_l326)))


(def v138_l330 (kind/doc #'la/dot))


(def v139_l332 (la/dot (t/column [1 2 3]) (t/column [4 5 6])))


(deftest t140_l334 (is ((fn [v] (== 32.0 v)) v139_l332)))


(def v141_l336 (kind/doc #'la/close?))


(def v142_l338 (la/close? (t/eye 2) (t/eye 2)))


(deftest t143_l340 (is (true? v142_l338)))


(def v144_l342 (la/close? (t/eye 2) (t/zeros 2 2)))


(deftest t145_l344 (is (false? v144_l342)))


(def v146_l346 (kind/doc #'la/close-scalar?))


(def v147_l348 (la/close-scalar? 1.00000000001 1.0))


(deftest t148_l350 (is (true? v147_l348)))


(def v149_l352 (kind/doc #'la/invert))


(def
 v150_l354
 (let
  [A (t/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (t/eye 2))))


(deftest t151_l357 (is (true? v150_l354)))


(def v152_l359 (kind/doc #'la/solve))


(def
 v154_l362
 (let [A (t/matrix [[2 1] [1 3]]) b (t/column [5 7])] (la/solve A b)))


(deftest
 t155_l366
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (x 0 0) 1.6)
     (la/close-scalar? (x 1 0) 1.8)))
   v154_l362)))


(def v156_l369 (kind/doc #'la/eigen))


(def
 v157_l371
 (let
  [result (la/eigen (t/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (t/complex-shape (:eigenvalues result))]))


(deftest
 t158_l375
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v157_l371)))


(def v159_l379 (kind/doc #'la/real-eigenvalues))


(def v160_l381 (la/real-eigenvalues (t/matrix [[2 1] [1 2]])))


(deftest
 t161_l383
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v160_l381)))


(def v162_l386 (kind/doc #'la/svd))


(def
 v163_l388
 (let
  [{:keys [U S Vt]} (la/svd (t/matrix [[1 0] [0 2] [0 0]]))]
  [(t/shape U) (count S) (t/shape Vt)]))


(deftest
 t164_l393
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v163_l388)))


(def v165_l398 (kind/doc #'la/qr))


(def
 v166_l400
 (let
  [{:keys [Q R]} (la/qr (t/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (t/matrix [[1 1] [1 2] [0 1]]))))


(deftest t167_l403 (is (true? v166_l400)))


(def v168_l405 (kind/doc #'la/cholesky))


(def
 v169_l407
 (let
  [A (t/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t170_l411 (is (true? v169_l407)))


(def v171_l413 (kind/doc #'la/mpow))


(def v172_l415 (la/mpow (t/matrix [[1 1] [0 1]]) 5))


(deftest
 t173_l417
 (is ((fn [m] (la/close? m (t/matrix [[1 5] [0 1]]))) v172_l415)))


(def v174_l419 (kind/doc #'la/rank))


(def v175_l421 (la/rank (t/matrix [[1 2] [2 4]])))


(deftest t176_l423 (is ((fn [r] (= 1 r)) v175_l421)))


(def v177_l425 (kind/doc #'la/condition-number))


(def v178_l427 (la/condition-number (t/matrix [[2 1] [1 3]])))


(deftest t179_l429 (is ((fn [v] (> v 1.0)) v178_l427)))


(def v180_l431 (kind/doc #'la/pinv))


(def
 v181_l433
 (let
  [A (t/matrix [[2 1] [1 3]])]
  (la/close? (la/mmul A (la/pinv A)) (t/eye 2))))


(deftest t182_l436 (is (true? v181_l433)))


(def v183_l438 (kind/doc #'la/lstsq))


(def
 v184_l440
 (let
  [{:keys [x rank]}
   (la/lstsq (t/matrix [[1 1] [1 2] [1 3]]) (t/column [1 2 3]))]
  {:rank rank, :close? (la/close? x (t/column [0 1]))}))


(deftest
 t185_l444
 (is ((fn [m] (and (= 2 (:rank m)) (:close? m))) v184_l440)))


(def v186_l446 (kind/doc #'la/null-space))


(def
 v187_l448
 (let
  [ns (la/null-space (t/matrix [[1 2] [2 4]]))]
  (la/close? (la/mmul (t/matrix [[1 2] [2 4]]) ns) (t/zeros 2 1))))


(deftest t188_l452 (is (true? v187_l448)))


(def v189_l454 (kind/doc #'la/col-space))


(def
 v190_l456
 (second (t/shape (la/col-space (t/matrix [[1 2] [2 4]])))))


(deftest t191_l458 (is ((fn [r] (= 1 r)) v190_l456)))


(def v192_l460 (kind/doc #'la/lift))


(def v194_l463 (la/lift el/sqrt (t/matrix [[4 9] [16 25]])))


(deftest
 t195_l465
 (is
  ((fn
    [m]
    (and
     (la/close-scalar? (m 0 0) 2.0)
     (la/close-scalar? (m 0 1) 3.0)))
   v194_l463)))


(def v196_l468 (kind/doc #'la/lifted))


(def
 v198_l471
 (let [my-sqrt (la/lifted el/sqrt)] (my-sqrt (t/column [4 9 16]))))


(deftest
 t199_l474
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v198_l471)))


(def v201_l480 (kind/doc #'t/complex-tensor))


(def v202_l482 (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t203_l484
 (is ((fn [ct] (= [3] (t/complex-shape ct))) v202_l482)))


(def v204_l486 (kind/doc #'t/complex-tensor-real))


(def v205_l488 (t/complex-tensor-real [5.0 6.0 7.0]))


(deftest t206_l490 (is ((fn [ct] (every? zero? (el/im ct))) v205_l488)))


(def v207_l492 (kind/doc #'t/complex))


(def v208_l494 (t/complex 3.0 4.0))


(deftest
 t209_l496
 (is
  ((fn
    [ct]
    (and (t/scalar? ct) (== 3.0 (el/re ct)) (== 4.0 (el/im ct))))
   v208_l494)))


(def v210_l500 (kind/doc #'el/re))


(def v211_l502 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t212_l504 (is (= v211_l502 [1.0 2.0])))


(def v213_l506 (kind/doc #'el/im))


(def v214_l508 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t215_l510 (is (= v214_l508 [3.0 4.0])))


(def v216_l512 (kind/doc #'t/complex-shape))


(def
 v217_l514
 (t/complex-shape
  (t/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t218_l517 (is (= v217_l514 [2 2])))


(def v219_l519 (kind/doc #'t/scalar?))


(def v220_l521 (t/scalar? (t/complex 3.0 4.0)))


(deftest t221_l523 (is (true? v220_l521)))


(def v222_l525 (kind/doc #'t/complex?))


(def v223_l527 (t/complex? (t/complex 3.0 4.0)))


(deftest t224_l529 (is (true? v223_l527)))


(def v225_l531 (t/complex? (t/eye 2)))


(deftest t226_l533 (is (false? v225_l531)))


(def v227_l535 (kind/doc #'t/->tensor))


(def
 v228_l537
 (t/shape (t/->tensor (t/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t229_l539 (is (= v228_l537 [2 2])))


(def v230_l541 (kind/doc #'t/->double-array))


(def
 v231_l543
 (let
  [ct (t/complex-tensor [1.0 2.0] [3.0 4.0])]
  (seq (t/->double-array ct))))


(deftest t232_l546 (is (= v231_l543 [1.0 3.0 2.0 4.0])))


(def v233_l548 (kind/doc #'t/wrap-tensor))


(def
 v234_l550
 (let
  [raw (t/matrix [[1.0 2.0] [3.0 4.0]]) ct (t/wrap-tensor raw)]
  [(t/complex? ct) (t/complex-shape ct)]))


(deftest
 t235_l554
 (is ((fn [[c? shape]] (and c? (= [2] shape))) v234_l550)))


(def
 v237_l561
 (let
  [a
   (t/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (t/complex-tensor [10.0 20.0] [30.0 40.0])]
  (el/re (la/add a b))))


(deftest t238_l565 (is (= v237_l561 [11.0 22.0])))


(def
 v240_l569
 (let
  [a
   (t/complex-tensor [1.0] [3.0])
   b
   (t/complex-tensor [2.0] [4.0])
   c
   (el/mul a b)]
  [(el/re (c 0)) (el/im (c 0))]))


(deftest t241_l574 (is (= v240_l569 [-10.0 10.0])))


(def v243_l578 (kind/doc #'el/conj))


(def
 v244_l580
 (let
  [ct (el/conj (t/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (el/im ct)))


(deftest t245_l583 (is (= v244_l580 [-3.0 4.0])))


(def v247_l587 (kind/doc #'la/dot-conj))


(def
 v248_l589
 (let
  [a (t/complex-tensor [3.0 1.0] [4.0 2.0]) result (la/dot-conj a a)]
  (la/close-scalar? (el/re result) 30.0)))


(deftest t249_l593 (is (true? v248_l589)))


(def
 v251_l597
 (let
  [m (el/abs (t/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t252_l600 (is (true? v251_l597)))


(def
 v254_l604
 (let
  [ct (t/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (el/sum ct)]
  [(el/re s) (el/im s)]))


(deftest t255_l608 (is (= v254_l604 [6.0 15.0])))


(def v257_l614 (kind/doc #'ft/forward))


(def
 v258_l616
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (ft/forward signal)]
  (t/complex-shape spectrum)))


(deftest t259_l620 (is (= v258_l616 [4])))


(def v260_l622 (kind/doc #'ft/inverse))


(def
 v261_l624
 (let
  [spectrum
   (ft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (ft/inverse spectrum)]
  (la/close-scalar? (el/re (roundtrip 0)) 1.0)))


(deftest t262_l628 (is (true? v261_l624)))


(def v263_l630 (kind/doc #'ft/inverse-real))


(def
 v264_l632
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/inverse-real (ft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t265_l636 (is (true? v264_l632)))


(def v266_l638 (kind/doc #'ft/forward-complex))


(def
 v267_l640
 (let
  [ct
   (t/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (ft/forward-complex ct)]
  (t/complex-shape spectrum)))


(deftest t268_l644 (is (= v267_l640 [4])))


(def v269_l646 (kind/doc #'ft/dct-forward))


(def v270_l648 (ft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t271_l650 (is ((fn [v] (= 4 (count v))) v270_l648)))


(def v272_l652 (kind/doc #'ft/dct-inverse))


(def
 v273_l654
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dct-inverse (ft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t274_l658 (is (true? v273_l654)))


(def v275_l660 (kind/doc #'ft/dst-forward))


(def v276_l662 (ft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t277_l664 (is ((fn [v] (= 4 (count v))) v276_l662)))


(def v278_l666 (kind/doc #'ft/dst-inverse))


(def
 v279_l668
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dst-inverse (ft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t280_l672 (is (true? v279_l668)))


(def v281_l674 (kind/doc #'ft/dht-forward))


(def v282_l676 (ft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t283_l678 (is ((fn [v] (= 4 (count v))) v282_l676)))


(def v284_l680 (kind/doc #'ft/dht-inverse))


(def
 v285_l682
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (ft/dht-inverse (ft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t286_l686 (is (true? v285_l682)))


(def v288_l692 (kind/doc #'tape/memory-status))


(def v289_l694 (tape/memory-status (t/matrix [[1 2] [3 4]])))


(deftest t290_l696 (is ((fn [s] (= :contiguous s)) v289_l694)))


(def
 v291_l698
 (tape/memory-status (la/transpose (t/matrix [[1 2] [3 4]]))))


(deftest t292_l700 (is ((fn [s] (= :strided s)) v291_l698)))


(def v293_l702 (tape/memory-status (la/add (t/eye 2) (t/eye 2))))


(deftest t294_l704 (is ((fn [s] (= :lazy s)) v293_l702)))


(def v295_l706 (kind/doc #'tape/memory-relation))


(def
 v296_l708
 (let
  [A (t/matrix [[1 2] [3 4]])]
  (tape/memory-relation A (la/transpose A))))


(deftest t297_l711 (is ((fn [r] (= :shared r)) v296_l708)))


(def
 v298_l713
 (tape/memory-relation
  (t/matrix [[1 0] [0 1]])
  (t/matrix [[5 6] [7 8]])))


(deftest t299_l715 (is ((fn [r] (= :independent r)) v298_l713)))


(def
 v300_l717
 (tape/memory-relation
  (t/matrix [[1 2] [3 4]])
  (la/add (t/eye 2) (t/eye 2))))


(deftest t301_l719 (is ((fn [r] (= :unknown-lazy r)) v300_l717)))


(def v302_l721 (kind/doc #'tape/with-tape))


(def
 v303_l723
 (def
  tape-example
  (tape/with-tape
   (let
    [A (t/matrix [[1 2] [3 4]]) B (la/scale A 2.0)]
    (la/mmul B (la/transpose A))))))


(def v304_l729 (select-keys tape-example [:result :entries]))


(deftest
 t305_l731
 (is
  ((fn [m] (and (contains? m :result) (contains? m :entries)))
   v304_l729)))


(def v306_l734 (kind/doc #'tape/summary))


(def v307_l736 (tape/summary tape-example))


(deftest t308_l738 (is ((fn [s] (= 4 (:total s))) v307_l736)))


(def v309_l740 (kind/doc #'tape/origin))


(def v310_l742 (tape/origin tape-example (:result tape-example)))


(deftest t311_l744 (is ((fn [dag] (= :la/mmul (:op dag))) v310_l742)))


(def v312_l746 (kind/doc #'tape/mermaid))


(def v314_l750 (tape/mermaid tape-example (:result tape-example)))


(def v315_l752 (kind/doc #'tape/detect-memory-status))


(def v317_l757 (mapv tape/detect-memory-status (:entries tape-example)))


(deftest
 t318_l759
 (is
  ((fn [v] (every? #{:independent :reads-through :shared} v))
   v317_l757)))


(def v320_l768 (kind/doc #'el/add))


(def v321_l770 (el/add (t/column [1 2 3]) (t/column [10 20 30])))


(deftest t322_l772 (is ((fn [v] (== 11.0 (v 0 0))) v321_l770)))


(def v323_l774 (kind/doc #'el/sub))


(def v324_l776 (el/sub (t/column [10 20 30]) (t/column [1 2 3])))


(deftest t325_l778 (is ((fn [v] (== 9.0 (v 0 0))) v324_l776)))


(def v326_l780 (kind/doc #'el/scale))


(def v327_l782 (el/scale (t/column [2 3 4]) 5.0))


(deftest t328_l784 (is ((fn [v] (== 10.0 (v 0 0))) v327_l782)))


(def v329_l786 (kind/doc #'el/mul))


(def v330_l788 (el/mul (t/column [2 3 4]) (t/column [10 20 30])))


(deftest t331_l790 (is ((fn [v] (== 20.0 (v 0 0))) v330_l788)))


(def v332_l792 (kind/doc #'el/re))


(def v333_l794 (el/re (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t334_l796 (is (= v333_l794 [1.0 2.0])))


(def v335_l798 (kind/doc #'el/im))


(def v336_l800 (el/im (t/complex-tensor [1.0 2.0] [3.0 4.0])))


(deftest t337_l802 (is (= v336_l800 [3.0 4.0])))


(def v338_l804 (kind/doc #'el/conj))


(def v339_l806 (let [z (t/complex 3.0 4.0)] (el/im (el/conj z))))


(deftest t340_l809 (is ((fn [v] (la/close-scalar? v -4.0)) v339_l806)))


(def v341_l811 (kind/doc #'el/prod))


(def v342_l813 (el/prod (t/column [2 3 4])))


(deftest t343_l815 (is ((fn [v] (== 24.0 v)) v342_l813)))


(def v344_l817 (kind/doc #'el/sq))


(def v345_l819 (el/sq (t/column [2 3 4])))


(deftest
 t346_l821
 (is ((fn [v] (la/close-scalar? (v 0 0) 4.0)) v345_l819)))


(def v347_l823 (kind/doc #'el/sqrt))


(def v348_l825 (el/sqrt (t/column [4 9 16])))


(deftest
 t349_l827
 (is ((fn [v] (la/close-scalar? (v 0 0) 2.0)) v348_l825)))


(def v350_l829 (kind/doc #'el/exp))


(def v351_l831 (la/close-scalar? ((el/exp (t/column [0])) 0 0) 1.0))


(deftest t352_l833 (is (true? v351_l831)))


(def v353_l835 (kind/doc #'el/log))


(def
 v354_l837
 (la/close-scalar? ((el/log (t/column [math/E])) 0 0) 1.0))


(deftest t355_l839 (is (true? v354_l837)))


(def v356_l841 (kind/doc #'el/log10))


(def v357_l843 (la/close-scalar? ((el/log10 (t/column [100])) 0 0) 2.0))


(deftest t358_l845 (is (true? v357_l843)))


(def v359_l847 (kind/doc #'el/sin))


(def
 v360_l849
 (la/close-scalar? ((el/sin (t/column [(/ math/PI 2)])) 0 0) 1.0))


(deftest t361_l851 (is (true? v360_l849)))


(def v362_l853 (kind/doc #'el/cos))


(def v363_l855 (la/close-scalar? ((el/cos (t/column [0])) 0 0) 1.0))


(deftest t364_l857 (is (true? v363_l855)))


(def v365_l859 (kind/doc #'el/tan))


(def
 v366_l861
 (la/close-scalar? ((el/tan (t/column [(/ math/PI 4)])) 0 0) 1.0))


(deftest t367_l863 (is (true? v366_l861)))


(def v368_l865 (kind/doc #'el/sinh))


(def v369_l867 (la/close-scalar? ((el/sinh (t/column [0])) 0 0) 0.0))


(deftest t370_l869 (is (true? v369_l867)))


(def v371_l871 (kind/doc #'el/cosh))


(def v372_l873 (la/close-scalar? ((el/cosh (t/column [0])) 0 0) 1.0))


(deftest t373_l875 (is (true? v372_l873)))


(def v374_l877 (kind/doc #'el/tanh))


(def v375_l879 (la/close-scalar? ((el/tanh (t/column [0])) 0 0) 0.0))


(deftest t376_l881 (is (true? v375_l879)))


(def v377_l883 (kind/doc #'el/abs))


(def v378_l885 ((el/abs (t/column [-5])) 0 0))


(deftest t379_l887 (is ((fn [v] (== 5.0 v)) v378_l885)))


(def v380_l889 (kind/doc #'el/sum))


(def v381_l891 (el/sum (t/column [1 2 3 4])))


(deftest t382_l893 (is ((fn [v] (== 10.0 v)) v381_l891)))


(def v383_l895 (kind/doc #'el/mean))


(def v384_l897 (el/mean (t/column [2 4 6])))


(deftest t385_l899 (is ((fn [v] (== 4.0 v)) v384_l897)))


(def v386_l901 (kind/doc #'el/pow))


(def v387_l903 ((el/pow (t/column [2]) 3) 0 0))


(deftest t388_l905 (is ((fn [v] (== 8.0 v)) v387_l903)))


(def v389_l907 (kind/doc #'el/cbrt))


(def v390_l909 (la/close-scalar? ((el/cbrt (t/column [27])) 0 0) 3.0))


(deftest t391_l911 (is (true? v390_l909)))


(def v392_l913 (kind/doc #'el/floor))


(def v393_l915 ((el/floor (t/column [2.7])) 0 0))


(deftest t394_l917 (is ((fn [v] (== 2.0 v)) v393_l915)))


(def v395_l919 (kind/doc #'el/ceil))


(def v396_l921 ((el/ceil (t/column [2.3])) 0 0))


(deftest t397_l923 (is ((fn [v] (== 3.0 v)) v396_l921)))


(def v398_l925 (kind/doc #'el/min))


(def v399_l927 ((el/min (t/column [3]) (t/column [5])) 0 0))


(deftest t400_l929 (is ((fn [v] (== 3.0 v)) v399_l927)))


(def v401_l931 (kind/doc #'el/max))


(def v402_l933 ((el/max (t/column [3]) (t/column [5])) 0 0))


(deftest t403_l935 (is ((fn [v] (== 5.0 v)) v402_l933)))


(def v404_l937 (kind/doc #'el/asin))


(def v405_l939 ((el/asin (t/column [0.5])) 0 0))


(deftest
 t406_l941
 (is ((fn [v] (la/close-scalar? v (math/asin 0.5))) v405_l939)))


(def v407_l943 (kind/doc #'el/acos))


(def v408_l945 ((el/acos (t/column [0.5])) 0 0))


(deftest
 t409_l947
 (is ((fn [v] (la/close-scalar? v (math/acos 0.5))) v408_l945)))


(def v410_l949 (kind/doc #'el/atan))


(def v411_l951 ((el/atan (t/column [1.0])) 0 0))


(deftest
 t412_l953
 (is ((fn [v] (la/close-scalar? v (math/atan 1.0))) v411_l951)))


(def v413_l955 (kind/doc #'el/log1p))


(def v414_l957 ((el/log1p (t/column [0.0])) 0 0))


(deftest t415_l959 (is ((fn [v] (la/close-scalar? v 0.0)) v414_l957)))


(def v416_l961 (kind/doc #'el/expm1))


(def v417_l963 ((el/expm1 (t/column [0.0])) 0 0))


(deftest t418_l965 (is ((fn [v] (la/close-scalar? v 0.0)) v417_l963)))


(def v419_l967 (kind/doc #'el/round))


(def v420_l969 ((el/round (t/column [2.7])) 0 0))


(deftest t421_l971 (is ((fn [v] (== 3.0 v)) v420_l969)))


(def v422_l973 (kind/doc #'el/clip))


(def v423_l975 (t/flatten (el/clip (t/column [-2 0.5 3]) -1 1)))


(deftest t424_l977 (is ((fn [v] (= [-1.0 0.5 1.0] v)) v423_l975)))


(def v425_l979 (kind/doc #'el/div))


(def v426_l981 (el/div (t/column [10 20 30]) (t/column [2 4 5])))


(deftest
 t427_l983
 (is ((fn [v] (= [5.0 5.0 6.0] (t/flatten v))) v426_l981)))


(def v429_l987 (el/div (t/complex 3.0 4.0) (t/complex 1.0 2.0)))


(deftest
 t430_l989
 (is
  ((fn
    [v]
    (and
     (< (abs (- (el/re v) 2.2)) 1.0E-10)
     (< (abs (- (el/im v) -0.4)) 1.0E-10)))
   v429_l987)))


(def v431_l992 (kind/doc #'el/>))


(def v432_l994 (el/> (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t433_l996
 (is ((fn [v] (= [0.0 1.0 0.0] (t/flatten v))) v432_l994)))


(def v434_l998 (kind/doc #'el/<))


(def v435_l1000 (el/< (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t436_l1002
 (is ((fn [v] (= [1.0 0.0 0.0] (t/flatten v))) v435_l1000)))


(def v437_l1004 (kind/doc #'el/>=))


(def v438_l1006 (el/>= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t439_l1008
 (is ((fn [v] (= [0.0 1.0 1.0] (t/flatten v))) v438_l1006)))


(def v440_l1010 (kind/doc #'el/<=))


(def v441_l1012 (el/<= (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t442_l1014
 (is ((fn [v] (= [1.0 0.0 1.0] (t/flatten v))) v441_l1012)))


(def v443_l1016 (kind/doc #'el/eq))


(def v444_l1018 (el/eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t445_l1020
 (is ((fn [v] (= [0.0 0.0 1.0] (t/flatten v))) v444_l1018)))


(def v446_l1022 (kind/doc #'el/not-eq))


(def v447_l1024 (el/not-eq (t/column [1 5 3]) (t/column [2 4 3])))


(deftest
 t448_l1026
 (is ((fn [v] (= [1.0 1.0 0.0] (t/flatten v))) v447_l1024)))


(def v449_l1028 (kind/doc #'el/reduce-max))


(def v450_l1030 (el/reduce-max (t/column [3 7 2 9 1])))


(deftest t451_l1032 (is ((fn [v] (== 9.0 v)) v450_l1030)))


(def v452_l1034 (kind/doc #'el/reduce-min))


(def v453_l1036 (el/reduce-min (t/column [3 7 2 9 1])))


(deftest t454_l1038 (is ((fn [v] (== 1.0 v)) v453_l1036)))


(def v456_l1044 (kind/doc #'grad/grad))


(def
 v457_l1046
 (let
  [A
   (t/matrix [[1 2] [3 4]])
   tape-result
   (tape/with-tape (la/trace (la/mmul (la/transpose A) A)))]
  (la/close?
   (grad/grad tape-result (:result tape-result) A)
   (la/scale A 2))))


(deftest t458_l1053 (is (true? v457_l1046)))


(def v460_l1059 (kind/doc #'vis/arrow-plot))


(def
 v461_l1061
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v462_l1065 (kind/doc #'vis/graph-plot))


(def
 v463_l1067
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v464_l1071 (kind/doc #'vis/matrix->gray-image))


(def
 v465_l1073
 (let
  [m
   (t/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t466_l1078
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v465_l1073)))


(def v467_l1080 (kind/doc #'vis/extract-channel))


(def
 v468_l1082
 (let
  [img
   (t/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t469_l1088
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v468_l1082)))
