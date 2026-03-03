(ns
 lalinea-book.algebraic-identities-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.complex :as cx]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l27 (def A (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l31 (def B (t/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l35 (def C (t/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l39 (def I3 (t/eye 3)))


(def v7_l41 (def v (t/column [1 2 3])))


(def v9_l51 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l53 (is (true? v9_l51)))


(def
 v12_l57
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l60 (is (true? v12_l57)))


(def v15_l64 (la/close? (la/add A (t/zeros 3 3)) A))


(deftest t16_l66 (is (true? v15_l64)))


(def v18_l70 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l72 (is (true? v18_l70)))


(def
 v21_l76
 (let
  [alpha 3.5]
  (la/close?
   (la/scale (la/add A B) alpha)
   (la/add (la/scale A alpha) (la/scale B alpha)))))


(deftest t22_l80 (is (true? v21_l76)))


(def
 v24_l84
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale A (* alpha beta))
   (la/scale (la/scale A beta) alpha))))


(deftest t25_l88 (is (true? v24_l84)))


(def
 v27_l99
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l102 (is (true? v27_l99)))


(def v30_l106 (la/close? (la/mmul I3 A) A))


(deftest t31_l108 (is (true? v30_l106)))


(def v33_l112 (la/close? (la/mmul A I3) A))


(deftest t34_l114 (is (true? v33_l112)))


(def
 v36_l118
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l121 (is (true? v36_l118)))


(def
 v39_l125
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l128 (is (true? v39_l125)))


(def
 v42_l132
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul (la/scale A alpha) B))
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul A (la/scale B alpha))))))


(deftest t43_l138 (is (true? v42_l132)))


(def v45_l144 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l146 (is (true? v45_l144)))


(def v48_l159 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l161 (is (true? v48_l159)))


(def
 v51_l165
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l168 (is (true? v51_l165)))


(def
 v54_l175
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l178 (is (true? v54_l175)))


(def
 v57_l182
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale A alpha))
   (la/scale (la/transpose A) alpha))))


(deftest t58_l186 (is (true? v57_l182)))


(def
 v60_l198
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale A alpha) (la/scale B beta)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l204 (is (true? v60_l198)))


(def
 v63_l211
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l217 (is (true? v63_l211)))


(def
 v66_l221
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l223 (is (true? v66_l221)))


(def v69_l227 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l229 (is (true? v69_l227)))


(def
 v72_l243
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l246 (is (true? v72_l243)))


(def v75_l250 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l252 (is (true? v75_l250)))


(def v78_l256 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l258 (is (true? v78_l256)))


(def
 v81_l262
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l265 (is (true? v81_l262)))


(def
 v84_l269
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale A alpha))
   (* (math/pow alpha n) (la/det A)))))


(deftest t85_l273 (is (true? v84_l269)))


(def v87_l285 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l287 (is (true? v87_l285)))


(def v90_l291 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l293 (is (true? v90_l291)))


(def v93_l297 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l299 (is (true? v93_l297)))


(def
 v96_l306
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l309 (is (true? v96_l306)))


(def
 v99_l313
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l316 (is (true? v99_l313)))


(def
 v102_l320
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale A alpha))
   (la/scale (la/invert A) (/ 1.0 alpha)))))


(deftest t103_l324 (is (true? v102_l320)))


(def
 v105_l335
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (t/zeros 3 3)) 1.0E-10)))


(deftest t106_l339 (is (true? v105_l335)))


(def
 v108_l343
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale A alpha))
   (* (abs alpha) (la/norm A)))))


(deftest t109_l347 (is (true? v108_l343)))


(def
 v111_l353
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l356 (is (true? v111_l353)))


(def
 v114_l367
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l370 (is (true? v114_l367)))


(def
 v117_l374
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l377 (is (true? v117_l374)))


(def
 v120_l383
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn [i] (every? (fn [j] (< (abs (R i j)) 1.0E-10)) (range 0 i)))
   (range 1 3))))


(deftest t121_l390 (is (true? v120_l383)))


(def
 v123_l401
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


(deftest t124_l410 (is (true? v123_l401)))


(def
 v126_l414
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (la/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l418 (is (true? v126_l414)))


(def
 v129_l422
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (la/prod (cx/re eigenvalues))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l426 (is (true? v129_l422)))


(def
 v132_l438
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (t/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l442 (is (true? v132_l438)))


(def
 v135_l446
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l449 (is (true? v135_l446)))


(def
 v138_l453
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l456 (is (true? v138_l453)))


(def
 v141_l460
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (la/real-eigenvalues (la/mmul (la/transpose A) A))
   sv-squared
   (sort > (la/sq S))]
  (la/close?
   (t/->real-tensor sv-squared)
   (t/->real-tensor (reverse AtA-eigs))
   1.0E-8)))


(deftest t142_l466 (is (true? v141_l460)))


(def
 v144_l470
 (let
  [{:keys [S]} (la/svd A) sv-norm (math/sqrt (la/sum (la/mul S S)))]
  (la/close-scalar? (la/norm A) sv-norm)))


(deftest t145_l474 (is (true? v144_l470)))


(def
 v147_l486
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M)))


(deftest t148_l490 (is (true? v147_l486)))


(def v150_l494 (nil? (la/cholesky (t/matrix [[1 2] [2 1]]))))


(deftest t151_l496 (is (true? v150_l494)))


(def
 v153_l506
 (let
  [b (t/column [1 2 3]) x (la/solve A b)]
  (la/close? (la/mmul A x) b)))


(deftest t154_l510 (is (true? v153_l506)))


(def
 v156_l514
 (let
  [b
   (t/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv)))


(deftest t157_l519 (is (true? v156_l514)))


(def
 v159_l528
 (def ca (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def
 v160_l529
 (def cb (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def v162_l533 (la/close? (la/mul ca cb) (la/mul cb ca)))


(deftest t163_l535 (is (true? v162_l533)))


(def v165_l539 (la/close? (cx/conj (cx/conj ca)) ca))


(deftest t166_l541 (is (true? v165_l539)))


(def
 v168_l545
 (la/close?
  (cx/conj (la/mul ca cb))
  (la/mul (cx/conj ca) (cx/conj cb))))


(deftest t169_l548 (is (true? v168_l545)))


(def
 v171_l552
 (<
  (elem/reduce-max
   (elem/abs
    (la/sub (la/abs (la/mul ca cb)) (la/mul (la/abs ca) (la/abs cb)))))
  1.0E-10))


(deftest t172_l557 (is (true? v171_l552)))


(def
 v174_l561
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


(deftest t175_l569 (is (true? v174_l561)))


(def
 v177_l580
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


(deftest t178_l587 (is (true? v177_l580)))


(def
 v180_l591
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t181_l595 (is (true? v180_l591)))


(def
 v183_l599
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


(deftest t184_l607 (is (true? v183_l599)))


(def
 v186_l611
 (let
  [CA
   (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (cx/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t187_l616 (is (true? v186_l611)))


(def
 v189_l620
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t190_l624 (is (true? v189_l620)))
