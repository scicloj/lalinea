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


(def v3_l28 (def A (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l32 (def B (t/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l36 (def C (t/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l40 (def I3 (t/eye 3)))


(def v7_l42 (def v (t/column [1 2 3])))


(def v9_l52 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l54 (is (true? v9_l52)))


(def
 v12_l58
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l61 (is (true? v12_l58)))


(def v15_l65 (la/close? (la/add A (t/zeros 3 3)) A))


(deftest t16_l67 (is (true? v15_l65)))


(def v18_l71 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l73 (is (true? v18_l71)))


(def
 v21_l77
 (let
  [alpha 3.5]
  (la/close?
   (la/scale (la/add A B) alpha)
   (la/add (la/scale A alpha) (la/scale B alpha)))))


(deftest t22_l81 (is (true? v21_l77)))


(def
 v24_l85
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale A (* alpha beta))
   (la/scale (la/scale A beta) alpha))))


(deftest t25_l89 (is (true? v24_l85)))


(def
 v27_l100
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l103 (is (true? v27_l100)))


(def v30_l107 (la/close? (la/mmul I3 A) A))


(deftest t31_l109 (is (true? v30_l107)))


(def v33_l113 (la/close? (la/mmul A I3) A))


(deftest t34_l115 (is (true? v33_l113)))


(def
 v36_l119
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l122 (is (true? v36_l119)))


(def
 v39_l126
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l129 (is (true? v39_l126)))


(def
 v42_l133
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul (la/scale A alpha) B))
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul A (la/scale B alpha))))))


(deftest t43_l139 (is (true? v42_l133)))


(def v45_l145 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l147 (is (true? v45_l145)))


(def v48_l160 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l162 (is (true? v48_l160)))


(def
 v51_l166
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l169 (is (true? v51_l166)))


(def
 v54_l176
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l179 (is (true? v54_l176)))


(def
 v57_l183
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale A alpha))
   (la/scale (la/transpose A) alpha))))


(deftest t58_l187 (is (true? v57_l183)))


(def
 v60_l199
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale A alpha) (la/scale B beta)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l205 (is (true? v60_l199)))


(def
 v63_l212
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l218 (is (true? v63_l212)))


(def
 v66_l222
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l224 (is (true? v66_l222)))


(def v69_l228 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l230 (is (true? v69_l228)))


(def
 v72_l244
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l247 (is (true? v72_l244)))


(def v75_l251 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l253 (is (true? v75_l251)))


(def v78_l257 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l259 (is (true? v78_l257)))


(def
 v81_l263
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l266 (is (true? v81_l263)))


(def
 v84_l270
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale A alpha))
   (* (math/pow alpha n) (la/det A)))))


(deftest t85_l274 (is (true? v84_l270)))


(def v87_l286 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l288 (is (true? v87_l286)))


(def v90_l292 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l294 (is (true? v90_l292)))


(def v93_l298 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l300 (is (true? v93_l298)))


(def
 v96_l307
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l310 (is (true? v96_l307)))


(def
 v99_l314
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l317 (is (true? v99_l314)))


(def
 v102_l321
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale A alpha))
   (la/scale (la/invert A) (/ 1.0 alpha)))))


(deftest t103_l325 (is (true? v102_l321)))


(def
 v105_l336
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (t/zeros 3 3)) 1.0E-10)))


(deftest t106_l340 (is (true? v105_l336)))


(def
 v108_l344
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale A alpha))
   (* (abs alpha) (la/norm A)))))


(deftest t109_l348 (is (true? v108_l344)))


(def
 v111_l354
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l357 (is (true? v111_l354)))


(def
 v114_l368
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l371 (is (true? v114_l368)))


(def
 v117_l375
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l378 (is (true? v117_l375)))


(def
 v120_l384
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn [i] (every? (fn [j] (< (abs (R i j)) 1.0E-10)) (range 0 i)))
   (range 1 3))))


(deftest t121_l391 (is (true? v120_l384)))


(def
 v123_l402
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


(deftest t124_l411 (is (true? v123_l402)))


(def
 v126_l415
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (la/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l419 (is (true? v126_l415)))


(def
 v129_l423
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (la/prod (cx/re eigenvalues))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l427 (is (true? v129_l423)))


(def
 v132_l439
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (t/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l443 (is (true? v132_l439)))


(def
 v135_l447
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l450 (is (true? v135_l447)))


(def
 v138_l454
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l457 (is (true? v138_l454)))


(def
 v141_l461
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


(deftest t142_l467 (is (true? v141_l461)))


(def
 v144_l471
 (let
  [{:keys [S]} (la/svd A) sv-norm (math/sqrt (la/sum (la/mul S S)))]
  (la/close-scalar? (la/norm A) sv-norm)))


(deftest t145_l475 (is (true? v144_l471)))


(def
 v147_l487
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M)))


(deftest t148_l491 (is (true? v147_l487)))


(def v150_l495 (nil? (la/cholesky (t/matrix [[1 2] [2 1]]))))


(deftest t151_l497 (is (true? v150_l495)))


(def
 v153_l507
 (let
  [b (t/column [1 2 3]) x (la/solve A b)]
  (la/close? (la/mmul A x) b)))


(deftest t154_l511 (is (true? v153_l507)))


(def
 v156_l515
 (let
  [b
   (t/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv)))


(deftest t157_l520 (is (true? v156_l515)))


(def
 v159_l529
 (def ca (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def
 v160_l530
 (def cb (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def v162_l534 (la/close? (la/mul ca cb) (la/mul cb ca)))


(deftest t163_l536 (is (true? v162_l534)))


(def v165_l540 (la/close? (cx/conj (cx/conj ca)) ca))


(deftest t166_l542 (is (true? v165_l540)))


(def
 v168_l546
 (la/close?
  (cx/conj (la/mul ca cb))
  (la/mul (cx/conj ca) (cx/conj cb))))


(deftest t169_l549 (is (true? v168_l546)))


(def
 v171_l553
 (<
  (elem/reduce-max
   (elem/abs
    (la/sub (la/abs (la/mul ca cb)) (la/mul (la/abs ca) (la/abs cb)))))
  1.0E-10))


(deftest t172_l558 (is (true? v171_l553)))


(def
 v174_l562
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


(deftest t175_l570 (is (true? v174_l562)))


(def
 v177_l581
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


(deftest t178_l588 (is (true? v177_l581)))


(def
 v180_l592
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t181_l596 (is (true? v180_l592)))


(def
 v183_l600
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


(deftest t184_l608 (is (true? v183_l600)))


(def
 v186_l612
 (let
  [CA
   (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (cx/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t187_l617 (is (true? v186_l612)))


(def
 v189_l621
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t190_l625 (is (true? v189_l621)))
