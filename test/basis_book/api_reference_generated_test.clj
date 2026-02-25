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


(def v3_l27 (kind/doc #'la/matrix))


(def v4_l29 (la/matrix [[1 2] [3 4]]))


(deftest t5_l31 (is ((fn [m] (= [2 2] (vec (dtype/shape m)))) v4_l29)))


(def v6_l33 (kind/doc #'la/eye))


(def v7_l35 (la/eye 3))


(deftest
 t8_l37
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 1.0 (tensor/mget m 0 0))
     (== 0.0 (tensor/mget m 0 1))))
   v7_l35)))


(def v9_l41 (kind/doc #'la/zeros))


(def v10_l43 (la/zeros 2 3))


(deftest
 t11_l45
 (is ((fn [m] (= [2 3] (vec (dtype/shape m)))) v10_l43)))


(def v12_l47 (kind/doc #'la/diag))


(def v13_l49 (la/diag [3 5 7]))


(deftest
 t14_l51
 (is
  ((fn
    [m]
    (and
     (= [3 3] (vec (dtype/shape m)))
     (== 5.0 (tensor/mget m 1 1))
     (== 0.0 (tensor/mget m 0 1))))
   v13_l49)))


(def v15_l55 (kind/doc #'la/column))


(def v16_l57 (la/column [1 2 3]))


(deftest
 t17_l59
 (is ((fn [v] (= [3 1] (vec (dtype/shape v)))) v16_l57)))


(def v18_l61 (kind/doc #'la/row))


(def v19_l63 (la/row [1 2 3]))


(deftest
 t20_l65
 (is ((fn [v] (= [1 3] (vec (dtype/shape v)))) v19_l63)))


(def v21_l68 (kind/doc #'la/add))


(def
 v22_l70
 (la/add (la/matrix [[1 2] [3 4]]) (la/matrix [[10 20] [30 40]])))


(deftest t23_l73 (is ((fn [m] (== 11.0 (tensor/mget m 0 0))) v22_l70)))


(def v24_l75 (kind/doc #'la/sub))


(def
 v25_l77
 (la/sub (la/matrix [[10 20] [30 40]]) (la/matrix [[1 2] [3 4]])))


(deftest t26_l80 (is ((fn [m] (== 9.0 (tensor/mget m 0 0))) v25_l77)))


(def v27_l82 (kind/doc #'la/scale))


(def v28_l84 (la/scale 3.0 (la/matrix [[1 2] [3 4]])))


(deftest t29_l86 (is ((fn [m] (== 6.0 (tensor/mget m 0 1))) v28_l84)))


(def v30_l88 (kind/doc #'la/mul))


(def
 v31_l90
 (la/mul (la/matrix [[2 3] [4 5]]) (la/matrix [[10 20] [30 40]])))


(deftest
 t32_l93
 (is
  ((fn
    [m]
    (and (== 20.0 (tensor/mget m 0 0)) (== 60.0 (tensor/mget m 0 1))))
   v31_l90)))


(def v33_l96 (kind/doc #'la/abs))


(def v34_l98 (la/abs (la/matrix [[-3 2] [-1 4]])))


(deftest t35_l100 (is ((fn [m] (== 3.0 (tensor/mget m 0 0))) v34_l98)))


(def v36_l103 (kind/doc #'la/mmul))


(def v37_l105 (la/mmul (la/matrix [[1 2] [3 4]]) (la/column [5 6])))


(deftest
 t38_l108
 (is
  ((fn
    [m]
    (and
     (= [2 1] (vec (dtype/shape m)))
     (== 17.0 (tensor/mget m 0 0))))
   v37_l105)))


(def v39_l112 (kind/doc #'la/transpose))


(def v40_l114 (la/transpose (la/matrix [[1 2 3] [4 5 6]])))


(deftest
 t41_l116
 (is ((fn [m] (= [3 2] (vec (dtype/shape m)))) v40_l114)))


(def v42_l119 (kind/doc #'la/submatrix))


(def v43_l121 (la/submatrix (la/eye 4) :all (range 2)))


(deftest
 t44_l123
 (is ((fn [m] (= [4 2] (vec (dtype/shape m)))) v43_l121)))


(def v45_l126 (kind/doc #'la/trace))


(def v46_l128 (la/trace (la/matrix [[1 2] [3 4]])))


(deftest t47_l130 (is ((fn [v] (== 5.0 v)) v46_l128)))


(def v48_l132 (kind/doc #'la/det))


(def v49_l134 (la/det (la/matrix [[1 2] [3 4]])))


(deftest t50_l136 (is ((fn [v] (la/close-scalar? v -2.0)) v49_l134)))


(def v51_l138 (kind/doc #'la/norm))


(def v52_l140 (la/norm (la/matrix [[3 0] [0 4]])))


(deftest t53_l142 (is ((fn [v] (la/close-scalar? v 5.0)) v52_l140)))


(def v54_l144 (kind/doc #'la/dot))


(def v55_l146 (la/dot (la/column [1 2 3]) (la/column [4 5 6])))


(deftest t56_l148 (is ((fn [v] (== 32.0 v)) v55_l146)))


(def v57_l151 (kind/doc #'la/close?))


(def v58_l153 (la/close? (la/eye 2) (la/eye 2)))


(deftest t59_l155 (is (true? v58_l153)))


(def v60_l157 (la/close? (la/eye 2) (la/zeros 2 2)))


(deftest t61_l159 (is (false? v60_l157)))


(def v62_l161 (kind/doc #'la/close-scalar?))


(def v63_l163 (la/close-scalar? 1.00000000001 1.0))


(deftest t64_l165 (is (true? v63_l163)))


(def v65_l168 (kind/doc #'la/invert))


(def
 v66_l170
 (let
  [A (la/matrix [[1 2] [3 5]])]
  (la/close? (la/mmul A (la/invert A)) (la/eye 2))))


(deftest t67_l173 (is (true? v66_l170)))


(def v68_l175 (kind/doc #'la/solve))


(def
 v70_l178
 (let [A (la/matrix [[2 1] [1 3]]) b (la/column [5 7])] (la/solve A b)))


(deftest
 t71_l182
 (is
  ((fn
    [x]
    (and
     (la/close-scalar? (tensor/mget x 0 0) 1.6)
     (la/close-scalar? (tensor/mget x 1 0) 1.8)))
   v70_l178)))


(def v72_l186 (kind/doc #'la/eigen))


(def
 v73_l188
 (let
  [result (la/eigen (la/matrix [[2 1] [1 2]]))]
  [(count (:eigenvectors result))
   (cx/complex-shape (:eigenvalues result))]))


(deftest
 t74_l192
 (is
  ((fn [[n-evecs ev-shape]] (and (= 2 n-evecs) (= [2] ev-shape)))
   v73_l188)))


(def v75_l196 (kind/doc #'la/real-eigenvalues))


(def v76_l198 (la/real-eigenvalues (la/matrix [[2 1] [1 2]])))


(deftest
 t77_l200
 (is
  ((fn
    [evs]
    (and
     (la/close-scalar? (evs 0) 1.0)
     (la/close-scalar? (evs 1) 3.0)))
   v76_l198)))


(def v78_l203 (kind/doc #'la/svd))


(def
 v79_l205
 (let
  [{:keys [U S Vt]} (la/svd (la/matrix [[1 0] [0 2] [0 0]]))]
  [(vec (dtype/shape U)) (count S) (vec (dtype/shape Vt))]))


(deftest
 t80_l210
 (is
  ((fn
    [[u-shape n-s vt-shape]]
    (and (= [3 3] u-shape) (= 2 n-s) (= [2 2] vt-shape)))
   v79_l205)))


(def v81_l215 (kind/doc #'la/qr))


(def
 v82_l217
 (let
  [{:keys [Q R]} (la/qr (la/matrix [[1 1] [1 2] [0 1]]))]
  (la/close? (la/mmul Q R) (la/matrix [[1 1] [1 2] [0 1]]))))


(deftest t83_l220 (is (true? v82_l217)))


(def v84_l222 (kind/doc #'la/cholesky))


(def
 v85_l224
 (let
  [A (la/matrix [[4 2] [2 3]]) L (la/cholesky A)]
  (la/close? (la/mmul L (la/transpose L)) A)))


(deftest t86_l228 (is (true? v85_l224)))


(def v87_l231 (kind/doc #'la/tensor->dmat))


(def
 v88_l233
 (let
  [t (la/matrix [[1 2] [3 4]]) dm (la/tensor->dmat t)]
  (= org.ejml.data.DMatrixRMaj (type dm))))


(deftest t89_l237 (is (true? v88_l233)))


(def v90_l239 (kind/doc #'la/dmat->tensor))


(def
 v91_l241
 (let
  [dm (la/tensor->dmat (la/eye 2)) t (la/dmat->tensor dm)]
  (= [2 2] (vec (dtype/shape t)))))


(deftest t92_l245 (is (true? v91_l241)))


(def v93_l247 (kind/doc #'la/complex-tensor->zmat))


(def
 v94_l249
 (let
  [ct
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   zm
   (la/complex-tensor->zmat ct)]
  (= org.ejml.data.ZMatrixRMaj (type zm))))


(deftest t95_l253 (is (true? v94_l249)))


(def v96_l255 (kind/doc #'la/zmat->complex-tensor))


(def
 v97_l257
 (let
  [zm
   (la/complex-tensor->zmat (cx/complex-tensor [1.0 2.0] [3.0 4.0]))
   ct
   (la/zmat->complex-tensor zm)]
  (cx/complex? ct)))


(deftest t98_l261 (is (true? v97_l257)))


(def v100_l269 (kind/doc #'cx/complex-tensor))


(def v101_l271 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest
 t102_l273
 (is ((fn [ct] (= [3] (cx/complex-shape ct))) v101_l271)))


(def v103_l275 (kind/doc #'cx/complex-tensor-real))


(def v104_l277 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t105_l279
 (is ((fn [ct] (every? zero? (seq (cx/im ct)))) v104_l277)))


(def v106_l281 (kind/doc #'cx/complex))


(def v107_l283 (cx/complex 3.0 4.0))


(deftest
 t108_l285
 (is
  ((fn
    [ct]
    (and (cx/scalar? ct) (== 3.0 (cx/re ct)) (== 4.0 (cx/im ct))))
   v107_l283)))


(def v109_l290 (kind/doc #'cx/re))


(def v110_l292 (vec (cx/re (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t111_l294 (is (= v110_l292 [1.0 2.0])))


(def v112_l296 (kind/doc #'cx/im))


(def v113_l298 (vec (cx/im (cx/complex-tensor [1.0 2.0] [3.0 4.0]))))


(deftest t114_l300 (is (= v113_l298 [3.0 4.0])))


(def v115_l302 (kind/doc #'cx/complex-shape))


(def
 v116_l304
 (cx/complex-shape
  (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])))


(deftest t117_l307 (is (= v116_l304 [2 2])))


(def v118_l309 (kind/doc #'cx/scalar?))


(def v119_l311 (cx/scalar? (cx/complex 3.0 4.0)))


(deftest t120_l313 (is (true? v119_l311)))


(def v121_l315 (kind/doc #'cx/complex?))


(def v122_l317 (cx/complex? (cx/complex 3.0 4.0)))


(deftest t123_l319 (is (true? v122_l317)))


(def v124_l321 (cx/complex? (la/eye 2)))


(deftest t125_l323 (is (false? v124_l321)))


(def v126_l325 (kind/doc #'cx/->tensor))


(def
 v127_l327
 (vec
  (dtype/shape (cx/->tensor (cx/complex-tensor [1.0 2.0] [3.0 4.0])))))


(deftest t128_l329 (is (= v127_l327 [2 2])))


(def v129_l331 (kind/doc #'cx/->double-array))


(def
 v130_l333
 (let
  [ct (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/->double-array ct))))


(deftest t131_l336 (is (= v130_l333 [1.0 3.0 2.0 4.0])))


(def v132_l339 (kind/doc #'cx/add))


(def
 v133_l341
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])]
  (vec (cx/re (cx/add a b)))))


(deftest t134_l345 (is (= v133_l341 [11.0 22.0])))


(def v135_l347 (kind/doc #'cx/sub))


(def
 v136_l349
 (let
  [a
   (cx/complex-tensor [10.0 20.0] [30.0 40.0])
   b
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])]
  (vec (cx/re (cx/sub a b)))))


(deftest t137_l353 (is (= v136_l349 [9.0 18.0])))


(def v138_l355 (kind/doc #'cx/scale))


(def
 v139_l357
 (let
  [ct (cx/scale (cx/complex-tensor [1.0 2.0] [3.0 4.0]) 2.0)]
  [(vec (cx/re ct)) (vec (cx/im ct))]))


(deftest t140_l360 (is (= v139_l357 [[2.0 4.0] [6.0 8.0]])))


(def v141_l362 (kind/doc #'cx/mul))


(def
 v143_l365
 (let
  [a
   (cx/complex-tensor [1.0] [3.0])
   b
   (cx/complex-tensor [2.0] [4.0])
   c
   (cx/mul a b)]
  [(cx/re (c 0)) (cx/im (c 0))]))


(deftest t144_l370 (is (= v143_l365 [-10.0 10.0])))


(def v145_l372 (kind/doc #'cx/conj))


(def
 v146_l374
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  (vec (cx/im ct))))


(deftest t147_l377 (is (= v146_l374 [-3.0 4.0])))


(def v148_l379 (kind/doc #'cx/abs))


(def
 v150_l382
 (let
  [m (cx/abs (cx/complex-tensor [3.0] [4.0]))]
  (la/close-scalar? (double (m 0)) 5.0)))


(deftest t151_l385 (is (true? v150_l382)))


(def v152_l387 (kind/doc #'cx/dot))


(def
 v153_l389
 (let
  [a
   (cx/complex-tensor [1.0 0.0] [0.0 1.0])
   b
   (cx/complex-tensor [0.0 1.0] [1.0 0.0])
   result
   (cx/dot a b)]
  (la/close-scalar? (cx/im result) 2.0)))


(deftest t154_l394 (is (true? v153_l389)))


(def v155_l396 (kind/doc #'cx/dot-conj))


(def
 v157_l399
 (let
  [a (cx/complex-tensor [3.0 1.0] [4.0 2.0]) result (cx/dot-conj a a)]
  (la/close-scalar? (cx/re result) 30.0)))


(deftest t158_l403 (is (true? v157_l399)))


(def v159_l405 (kind/doc #'cx/sum))


(def
 v160_l407
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]) s (cx/sum ct)]
  [(cx/re s) (cx/im s)]))


(deftest t161_l411 (is (= v160_l407 [6.0 15.0])))


(def v163_l419 (kind/doc #'bfft/forward))


(def
 v164_l421
 (let
  [signal [1.0 0.0 0.0 0.0] spectrum (bfft/forward signal)]
  (cx/complex-shape spectrum)))


(deftest t165_l425 (is (= v164_l421 [4])))


(def v166_l427 (kind/doc #'bfft/inverse))


(def
 v167_l429
 (let
  [spectrum
   (bfft/forward [1.0 2.0 3.0 4.0])
   roundtrip
   (bfft/inverse spectrum)]
  (la/close-scalar? (cx/re (roundtrip 0)) 1.0)))


(deftest t168_l433 (is (true? v167_l429)))


(def v169_l435 (kind/doc #'bfft/inverse-real))


(def
 v170_l437
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/inverse-real (bfft/forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t171_l441 (is (true? v170_l437)))


(def v172_l443 (kind/doc #'bfft/forward-complex))


(def
 v173_l445
 (let
  [ct
   (cx/complex-tensor-real [1.0 0.0 0.0 0.0])
   spectrum
   (bfft/forward-complex ct)]
  (cx/complex-shape spectrum)))


(deftest t174_l449 (is (= v173_l445 [4])))


(def v175_l452 (kind/doc #'bfft/dct-forward))


(def v176_l454 (bfft/dct-forward [1.0 2.0 3.0 4.0]))


(deftest t177_l456 (is ((fn [v] (= 4 (count v))) v176_l454)))


(def v178_l458 (kind/doc #'bfft/dct-inverse))


(def
 v179_l460
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dct-inverse (bfft/dct-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t180_l464 (is (true? v179_l460)))


(def v181_l466 (kind/doc #'bfft/dst-forward))


(def v182_l468 (bfft/dst-forward [1.0 2.0 3.0 4.0]))


(deftest t183_l470 (is ((fn [v] (= 4 (count v))) v182_l468)))


(def v184_l472 (kind/doc #'bfft/dst-inverse))


(def
 v185_l474
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dst-inverse (bfft/dst-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t186_l478 (is (true? v185_l474)))


(def v187_l480 (kind/doc #'bfft/dht-forward))


(def v188_l482 (bfft/dht-forward [1.0 2.0 3.0 4.0]))


(deftest t189_l484 (is ((fn [v] (= 4 (count v))) v188_l482)))


(def v190_l486 (kind/doc #'bfft/dht-inverse))


(def
 v191_l488
 (let
  [signal
   [1.0 2.0 3.0 4.0]
   roundtrip
   (bfft/dht-inverse (bfft/dht-forward signal))]
  (la/close-scalar? (roundtrip 0) 1.0)))


(deftest t192_l492 (is (true? v191_l488)))


(def v194_l498 (kind/doc #'vis/arrow-plot))


(def
 v195_l500
 (vis/arrow-plot
  [{:xy [2 1], :color "#2266cc", :label "u"}
   {:xy [-1 1.5], :color "#cc4422", :label "v"}]
  {:width 250}))


(def v196_l504 (kind/doc #'vis/graph-plot))


(def
 v197_l506
 (vis/graph-plot
  [[0 0] [1 0] [0.5 0.87]]
  [[0 1] [1 2] [2 0]]
  {:width 250, :labels ["A" "B" "C"]}))


(def v198_l510 (kind/doc #'vis/matrix->gray-image))


(def
 v199_l512
 (let
  [m
   (tensor/compute-tensor
    [50 50]
    (fn [r c] (* 255.0 (/ (+ r c) 100.0)))
    :float64)]
  (bufimg/tensor->image (vis/matrix->gray-image m))))


(deftest
 t200_l517
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v199_l512)))


(def v201_l519 (kind/doc #'vis/extract-channel))


(def
 v202_l521
 (let
  [img
   (tensor/compute-tensor
    [50 50 3]
    (fn [r c ch] (case (int ch) 0 (int (* 255 (/ r 50.0))) 1 128 2 64))
    :uint8)]
  (bufimg/tensor->image (vis/extract-channel img 0))))


(deftest
 t203_l527
 (is
  ((fn [img] (= java.awt.image.BufferedImage (type img))) v202_l521)))
