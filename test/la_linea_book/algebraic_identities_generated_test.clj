(ns
 la-linea-book.algebraic-identities-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l29 (def A (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l33 (def B (la/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l37 (def C (la/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l41 (def I3 (la/eye 3)))


(def v7_l43 (def v (la/column [1 2 3])))


(def v9_l53 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l55 (is (true? v9_l53)))


(def
 v12_l59
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l62 (is (true? v12_l59)))


(def v15_l66 (la/close? (la/add A (la/zeros 3 3)) A))


(deftest t16_l68 (is (true? v15_l66)))


(def v18_l72 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l74 (is (true? v18_l72)))


(def
 v21_l78
 (let
  [alpha 3.5]
  (la/close?
   (la/scale (la/add A B) alpha)
   (la/add (la/scale A alpha) (la/scale B alpha)))))


(deftest t22_l82 (is (true? v21_l78)))


(def
 v24_l86
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale A (* alpha beta))
   (la/scale (la/scale A beta) alpha))))


(deftest t25_l90 (is (true? v24_l86)))


(def
 v27_l101
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l104 (is (true? v27_l101)))


(def v30_l108 (la/close? (la/mmul I3 A) A))


(deftest t31_l110 (is (true? v30_l108)))


(def v33_l114 (la/close? (la/mmul A I3) A))


(deftest t34_l116 (is (true? v33_l114)))


(def
 v36_l120
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l123 (is (true? v36_l120)))


(def
 v39_l127
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l130 (is (true? v39_l127)))


(def
 v42_l134
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul (la/scale A alpha) B))
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul A (la/scale B alpha))))))


(deftest t43_l140 (is (true? v42_l134)))


(def v45_l146 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l148 (is (true? v45_l146)))


(def v48_l161 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l163 (is (true? v48_l161)))


(def
 v51_l167
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l170 (is (true? v51_l167)))


(def
 v54_l177
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l180 (is (true? v54_l177)))


(def
 v57_l184
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale A alpha))
   (la/scale (la/transpose A) alpha))))


(deftest t58_l188 (is (true? v57_l184)))


(def
 v60_l200
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale A alpha) (la/scale B beta)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l206 (is (true? v60_l200)))


(def
 v63_l213
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l219 (is (true? v63_l213)))


(def
 v66_l223
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l225 (is (true? v66_l223)))


(def v69_l229 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l231 (is (true? v69_l229)))


(def
 v72_l245
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l248 (is (true? v72_l245)))


(def v75_l252 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l254 (is (true? v75_l252)))


(def v78_l258 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l260 (is (true? v78_l258)))


(def
 v81_l264
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l267 (is (true? v81_l264)))


(def
 v84_l271
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale A alpha))
   (* (Math/pow alpha n) (la/det A)))))


(deftest t85_l275 (is (true? v84_l271)))


(def v87_l287 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l289 (is (true? v87_l287)))


(def v90_l293 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l295 (is (true? v90_l293)))


(def v93_l299 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l301 (is (true? v93_l299)))


(def
 v96_l308
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l311 (is (true? v96_l308)))


(def
 v99_l315
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l318 (is (true? v99_l315)))


(def
 v102_l322
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale A alpha))
   (la/scale (la/invert A) (/ 1.0 alpha)))))


(deftest t103_l326 (is (true? v102_l322)))


(def
 v105_l337
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (la/zeros 3 3)) 1.0E-10)))


(deftest t106_l341 (is (true? v105_l337)))


(def
 v108_l345
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale A alpha))
   (* (Math/abs alpha) (la/norm A)))))


(deftest t109_l349 (is (true? v108_l345)))


(def
 v111_l355
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l358 (is (true? v111_l355)))


(def
 v114_l369
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l372 (is (true? v114_l369)))


(def
 v117_l376
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l379 (is (true? v117_l376)))


(def
 v120_l385
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn
    [i]
    (every?
     (fn [j] (< (Math/abs (tensor/mget R i j)) 1.0E-10))
     (range 0 i)))
   (range 1 3))))


(deftest t121_l392 (is (true? v120_l385)))


(def
 v123_l403
 (let
  [{:keys [eigenvalues eigenvectors]}
   (la/eigen A)
   reals
   (cx/re eigenvalues)]
  (every?
   (fn
    [[i evec]]
    (when
     evec
     (let
      [Av (la/mmul A evec) lam-v (la/scale evec (double (reals i)))]
      (la/close? Av lam-v))))
   (map-indexed vector eigenvectors))))


(deftest t124_l412 (is (true? v123_l403)))


(def
 v126_l416
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (dfn/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l420 (is (true? v126_l416)))


(def
 v129_l424
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (reduce * (seq (cx/re eigenvalues)))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l428 (is (true? v129_l424)))


(def
 v132_l440
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (la/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l444 (is (true? v132_l440)))


(def
 v135_l448
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l451 (is (true? v135_l448)))


(def
 v138_l455
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l458 (is (true? v138_l455)))


(def
 v141_l462
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (la/real-eigenvalues (la/mmul (la/transpose A) A))
   sv-squared
   (sort > (map (fn* [p1__70615#] (* p1__70615# p1__70615#)) S))]
  (every?
   identity
   (map
    (fn [a b] (< (Math/abs (- a b)) 1.0E-8))
    sv-squared
    (reverse (seq AtA-eigs))))))


(deftest t142_l469 (is (true? v141_l462)))


(def
 v144_l473
 (let
  [{:keys [S]} (la/svd A) sv-norm (Math/sqrt (dfn/sum (dfn/* S S)))]
  (la/close-scalar? (la/norm A) sv-norm)))


(deftest t145_l477 (is (true? v144_l473)))


(def
 v147_l489
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M)))


(deftest t148_l493 (is (true? v147_l489)))


(def v150_l497 (nil? (la/cholesky (la/matrix [[1 2] [2 1]]))))


(deftest t151_l499 (is (true? v150_l497)))


(def
 v153_l509
 (let
  [b (la/column [1 2 3]) x (la/solve A b)]
  (la/close? (la/mmul A x) b)))


(deftest t154_l513 (is (true? v153_l509)))


(def
 v156_l517
 (let
  [b
   (la/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv)))


(deftest t157_l522 (is (true? v156_l517)))


(def
 v159_l531
 (def ca (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def
 v160_l532
 (def cb (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def v162_l536 (la/close? (la/mul ca cb) (la/mul cb ca)))


(deftest t163_l538 (is (true? v162_l536)))


(def v165_l542 (la/close? (cx/conj (cx/conj ca)) ca))


(deftest t166_l544 (is (true? v165_l542)))


(def
 v168_l548
 (la/close?
  (cx/conj (la/mul ca cb))
  (la/mul (cx/conj ca) (cx/conj cb))))


(deftest t169_l551 (is (true? v168_l548)))


(def
 v171_l555
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/- (la/abs (la/mul ca cb)) (dfn/* (la/abs ca) (la/abs cb)))))
  1.0E-10))


(deftest t172_l560 (is (true? v171_l555)))


(def
 v174_l564
 (let
  [d-ab
   (la/dot ca cb)
   re-ab
   (double (cx/re d-ab))
   im-ab
   (double (cx/im d-ab))
   re-aa
   (double (cx/re (la/dot ca ca)))
   re-bb
   (double (cx/re (la/dot cb cb)))]
  (<= (- (+ (* re-ab re-ab) (* im-ab im-ab)) 1.0E-10) (* re-aa re-bb))))


(deftest t175_l572 (is (true? v174_l564)))


(def
 v177_l583
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CB
   (cx/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
   CC
   (cx/complex-tensor [[0 1] [2 -1]] [[3 0] [1 1]])]
  (<
   (la/norm
    (la/sub (la/mmul (la/mmul CA CB) CC) (la/mmul CA (la/mmul CB CC))))
   1.0E-10)))


(deftest t178_l590 (is (true? v177_l583)))


(def
 v180_l594
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t181_l598 (is (true? v180_l594)))


(def
 v183_l602
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CB
   (cx/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
   det-AB
   (la/det (la/mmul CA CB))
   det-A
   (la/det CA)
   det-B
   (la/det CB)
   product
   (la/mul det-A det-B)]
  (< (la/norm (la/sub det-AB product)) 1.0E-10)))


(deftest t184_l610 (is (true? v183_l602)))


(def
 v186_l614
 (let
  [CA
   (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (cx/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t187_l619 (is (true? v186_l614)))


(def
 v189_l623
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t190_l627 (is (true? v189_l623)))
