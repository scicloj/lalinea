(ns
 basis-book.api-reference-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [scicloj.basis.transform :as bfft]
  [scicloj.basis.vis :as vis]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tech.v3.libs.buffered-image :as bufimg]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l28 (kind/doc #'la/matrix))


(def v4_l30 (la/matrix [[1 2] [3 4]]))


(deftest t5_l32 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l30)))


(def v6_l34 (kind/doc #'la/eye))


(def v7_l36 (la/eye 3))


(deftest
 t8_l38
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l36)))


(def v9_l42 (kind/doc #'la/zeros))


(def v10_l44 (la/zeros 2 3))


(deftest
 t11_l46
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l44)))


(def v12_l48 (kind/doc #'la/diag))


(def v13_l50 (la/diag [3 5 7]))


(deftest
 t14_l52
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l50)))


(def v15_l56 (kind/doc #'la/column))


(def v16_l58 (la/column [1 2 3]))


(deftest
 t17_l60
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l58)))


(def v18_l62 (kind/doc #'la/row))


(def v19_l64 (la/row [1 2 3]))


(deftest
 t20_l66
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l64)))


(def v22_l70 (kind/doc #'la/add))


(def
 v23_l72
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t24_l75 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v23_l72)))


(def v25_l77 (kind/doc #'la/sub))


(def
 v26_l79
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t27_l82 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v26_l79)))


(def v28_l84 (kind/doc #'la/scale))


(def v29_l86 (la/scale 3.0 (la/matrix [[1 2] [3 4]])))


(deftest t30_l88 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v29_l86)))


(def v31_l90 (kind/doc #'la/mul))


(def
 v32_l92
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t33_l95
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v32_l92)))


(def v34_l98 (kind/doc #'la/abs))


(def v35_l100 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t36_l102 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v35_l100)))


(def v38_l106 (kind/doc #'la/mmul))


(def v39_l108 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t40_l111
 (is
  ((fn
    [m]
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v39_l108)))


(def v42_l116 (kind/doc #'la/transpose))


(def v43_l118 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t44_l120
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v43_l118)))


(def v46_l124 (kind/doc #'la/submatrix))


(def v47_l126 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t48_l128
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v47_l126)))


(def v50_l132 (kind/doc #'la/trace))


(def v51_l134 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t52_l136 (is ((fn [v] (== 5.0 v)) v51_l134)))


(def v53_l138 (kind/doc #'la/det))


(def v54_l140 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t55_l142 (is ((fn [v] (la/close-scalar? v -2.0)) v54_l140)))


(def v56_l144 (kind/doc #'la/norm))


(def v57_l146 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t58_l148 (is ((fn [v] (la/close-scalar? v 5.0)) v57_l146)))


(def v59_l150 (kind/doc #'la/dot))


(def v60_l152 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t61_l154 (is ((fn [v] (== 32.0 v)) v60_l152)))


(def v63_l158 (kind/doc #'la/close?))


(def v64_l160 (la/close? (la/eye 2) (la/eye 2)))


(deftest t65_l162 (is (true? v64_l160)))


(def v66_l164 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t67_l166 (is (false? v66_l164)))


(def v68_l168 (kind/doc #'la/close-scalar?))


(def v69_l170 (la/close-scalar? 1.00000000001 1.0))


(deftest t70_l172 (is (true? v69_l170)))


(def v72_l176 (kind/doc #'la/invert))


(def
 v73_l178
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t74_l181 (is (true? v73_l178)))


(def v75_l183 (kind/doc #'la/solve))


(def
 v77_l186
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t78_l190
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v77_l186)))


(def v80_l195 (kind/doc #'la/eigen))


(def
 v81_l197
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t82_l201
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v81_l197)))


(def v83_l205 (kind/doc #'la/real-eigenvalues))


(def v84_l207 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t85_l209
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v84_l207)))


(def v86_l212 (kind/doc #'la/svd))


(def
 v87_l214
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


(deftest
 t88_l219
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v87_l214)))


(def v89_l224 (kind/doc #'la/qr))


(def
 v90_l226
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t91_l229 (is (true? v90_l226)))


(def v92_l231 (kind/doc #'la/cholesky))


(def
 v93_l233
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t94_l237 (is (true? v93_l233)))


(def v96_l241 (kind/doc #'la/tensor->dmat))


(def
 v97_l243
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t98_l247 (is (true? v97_l243)))


(def v99_l249 (kind/doc #'la/dmat->tensor))


(def
 v100_l251
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t)))))


(deftest t101_l255 (is (true? v100_l251)))


(def v102_l257 (kind/doc #'la/complex-tensor->zmat))


(def
 v103_l259
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t104_l263 (is (true? v103_l259)))


(def v105_l265 (kind/doc #'la/zmat->complex-tensor))


(def
 v106_l267
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t107_l271 (is (true? v106_l267)))


(def v109_l280 (kind/doc #'cx/complex-tensor))


(def v110_l282 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t111_l284
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v110_l282)))


(def v112_l286 (kind/doc #'cx/complex-tensor-real))


(def v113_l288 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t114_l290
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v113_l288)))


(def v115_l292 (kind/doc #'cx/complex))


(def v116_l294 (cx/complex 3.0 4.0))


(deftest
 t117_l296
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v116_l294)))


(def v119_l302 (kind/doc #'cx/re))


(def v120_l304 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t121_l306 (is (= v120_l304 [1.0 2.0])))


(def v122_l308 (kind/doc #'cx/im))


(def v123_l310 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t124_l312 (is (= v123_l310 [3.0 4.0])))


(def v125_l314 (kind/doc #'cx/complex-shape))


(def
 v126_l316
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t127_l319 (is (= v126_l316 [2 2])))


(def v128_l321 (kind/doc #'cx/scalar?))


(def v129_l323 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t130_l325 (is (true? v129_l323)))


(def v131_l327 (kind/doc #'cx/complex?))


(def v132_l329 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t133_l331 (is (true? v132_l329)))


(def v134_l333 (cx/complex? (la/eye 2)))


(deftest t135_l335 (is (false? v134_l333)))


(def v136_l337 (kind/doc #'cx/->tensor))


(def
 v137_l339
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t138_l341 (is (= v137_l339 [2 2])))


(def v139_l343 (kind/doc #'cx/->double-array))


(def
 v140_l345
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t141_l348 (is (= v140_l345 [1.0 3.0 2.0 4.0])))


(def v143_l352 (kind/doc #'cx/add))


(def
 v144_l354
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t145_l358 (is (= v144_l354 [11.0 22.0])))


(def v146_l360 (kind/doc #'cx/sub))


(def
 v147_l362
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t148_l366 (is (= v147_l362 [9.0 18.0])))


(def v149_l368 (kind/doc #'cx/scale))


(def
 v150_l370
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t151_l373 (is (= v150_l370 [[2.0 4.0] [6.0 8.0]])))


(def v152_l375 (kind/doc #'cx/mul))


(def
 v154_l378
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t155_l383 (is (= v154_l378 [-10.0 10.0])))


(def v156_l385 (kind/doc #'cx/conj))


(def
 v157_l387
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t158_l390 (is (= v157_l387 [-3.0 4.0])))


(def v159_l392 (kind/doc #'cx/abs))


(def
 v161_l395
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t162_l398 (is (true? v161_l395)))


(def v163_l400 (kind/doc #'cx/dot))


(def
 v164_l402
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t165_l407 (is (true? v164_l402)))


(def v166_l409 (kind/doc #'cx/dot-conj))


(def
 v168_l412
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t169_l416 (is (true? v168_l412)))


(def v170_l418 (kind/doc #'cx/sum))


(def
 v171_l420
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t172_l424 (is (= v171_l420 [6.0 15.0])))


(def v174_l433 (kind/doc #'bfft/forward))


(def
 v175_l435
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t176_l439 (is (= v175_l435 [4])))


(def v177_l441 (kind/doc #'bfft/inverse))


(def
 v178_l443
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t179_l447 (is (true? v178_l443)))


(def v180_l449 (kind/doc #'bfft/inverse-real))


(def
 v181_l451
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t182_l455 (is (true? v181_l451)))


(def v183_l457 (kind/doc #'bfft/forward-complex))


(def
 v184_l459
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t185_l463 (is (= v184_l459 [4])))


(def v187_l467 (kind/doc #'bfft/dct-forward))


(def v188_l469 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l471 (is ((fn [v] (= 4 (count v))) v188_l469)))


(def v190_l473 (kind/doc #'bfft/dct-inverse))


(def
 v191_l475
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l479 (is (true? v191_l475)))


(def v193_l481 (kind/doc #'bfft/dst-forward))


(def v194_l483 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t195_l485 (is ((fn [v] (= 4 (count v))) v194_l483)))


(def v196_l487 (kind/doc #'bfft/dst-inverse))


(def
 v197_l489
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t198_l493 (is (true? v197_l489)))


(def v199_l495 (kind/doc #'bfft/dht-forward))


(def v200_l497 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t201_l499 (is ((fn [v] (= 4 (count v))) v200_l497)))


(def v202_l501 (kind/doc #'bfft/dht-inverse))


(def
 v203_l503
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t204_l507 (is (true? v203_l503)))


(def v206_l513 (kind/doc #'vis/arrow-plot))


(def
 v207_l515
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v208_l519 (kind/doc #'vis/graph-plot))


(def
 v209_l521
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v210_l525 (kind/doc #'vis/matrix->gray-image))


(def
 v211_l527
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t212_l532
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v211_l527)))


(def v213_l534 (kind/doc #'vis/extract-channel))


(def
 v214_l536
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t215_l542
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v214_l536)))
