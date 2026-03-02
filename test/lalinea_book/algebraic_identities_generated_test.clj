(ns
 lalinea-book.algebraic-identities-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l30 (def A (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l34 (def B (la/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l38 (def C (la/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l42 (def I3 (la/eye 3)))


(def v7_l44 (def v (la/column [1 2 3])))


(def v9_l54 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l56 (is (true? v9_l54)))


(def
 v12_l60
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l63 (is (true? v12_l60)))


(def v15_l67 (la/close? (la/add A (la/zeros 3 3)) A))


(deftest t16_l69 (is (true? v15_l67)))


(def v18_l73 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l75 (is (true? v18_l73)))


(def
 v21_l79
 (let
  [alpha 3.5]
  (la/close?
   (la/scale (la/add A B) alpha)
   (la/add (la/scale A alpha) (la/scale B alpha)))))


(deftest t22_l83 (is (true? v21_l79)))


(def
 v24_l87
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale A (* alpha beta))
   (la/scale (la/scale A beta) alpha))))


(deftest t25_l91 (is (true? v24_l87)))


(def
 v27_l102
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l105 (is (true? v27_l102)))


(def v30_l109 (la/close? (la/mmul I3 A) A))


(deftest t31_l111 (is (true? v30_l109)))


(def v33_l115 (la/close? (la/mmul A I3) A))


(deftest t34_l117 (is (true? v33_l115)))


(def
 v36_l121
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l124 (is (true? v36_l121)))


(def
 v39_l128
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l131 (is (true? v39_l128)))


(def
 v42_l135
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul (la/scale A alpha) B))
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul A (la/scale B alpha))))))


(deftest t43_l141 (is (true? v42_l135)))


(def v45_l147 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l149 (is (true? v45_l147)))


(def v48_l162 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l164 (is (true? v48_l162)))


(def
 v51_l168
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l171 (is (true? v51_l168)))


(def
 v54_l178
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l181 (is (true? v54_l178)))


(def
 v57_l185
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale A alpha))
   (la/scale (la/transpose A) alpha))))


(deftest t58_l189 (is (true? v57_l185)))


(def
 v60_l201
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale A alpha) (la/scale B beta)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l207 (is (true? v60_l201)))


(def
 v63_l214
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l220 (is (true? v63_l214)))


(def
 v66_l224
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l226 (is (true? v66_l224)))


(def v69_l230 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l232 (is (true? v69_l230)))


(def
 v72_l246
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l249 (is (true? v72_l246)))


(def v75_l253 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l255 (is (true? v75_l253)))


(def v78_l259 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l261 (is (true? v78_l259)))


(def
 v81_l265
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l268 (is (true? v81_l265)))


(def
 v84_l272
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale A alpha))
   (* (math/pow alpha n) (la/det A)))))


(deftest t85_l276 (is (true? v84_l272)))


(def v87_l288 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l290 (is (true? v87_l288)))


(def v90_l294 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l296 (is (true? v90_l294)))


(def v93_l300 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l302 (is (true? v93_l300)))


(def
 v96_l309
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l312 (is (true? v96_l309)))


(def
 v99_l316
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l319 (is (true? v99_l316)))


(def
 v102_l323
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale A alpha))
   (la/scale (la/invert A) (/ 1.0 alpha)))))


(deftest t103_l327 (is (true? v102_l323)))


(def
 v105_l338
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (la/zeros 3 3)) 1.0E-10)))


(deftest t106_l342 (is (true? v105_l338)))


(def
 v108_l346
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale A alpha))
   (* (abs alpha) (la/norm A)))))


(deftest t109_l350 (is (true? v108_l346)))


(def
 v111_l356
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l359 (is (true? v111_l356)))


(def
 v114_l370
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l373 (is (true? v114_l370)))


(def
 v117_l377
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l380 (is (true? v117_l377)))


(def
 v120_l386
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn [i] (every? (fn [j] (< (abs (R i j)) 1.0E-10)) (range 0 i)))
   (range 1 3))))


(deftest t121_l393 (is (true? v120_l386)))


(def
 v123_l404
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


(deftest t124_l413 (is (true? v123_l404)))


(def
 v126_l417
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (dfn/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l421 (is (true? v126_l417)))


(def
 v129_l425
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (la/prod (cx/re eigenvalues))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l429 (is (true? v129_l425)))


(def
 v132_l441
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (la/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l445 (is (true? v132_l441)))


(def
 v135_l449
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l452 (is (true? v135_l449)))


(def
 v138_l456
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l459 (is (true? v138_l456)))


(def
 v141_l463
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (la/real-eigenvalues (la/mmul (la/transpose A) A))
   sv-squared
   (sort > (la/sq S))]
  (la/close?
   (la/->real-tensor sv-squared)
   (la/->real-tensor (reverse AtA-eigs))
   1.0E-8)))


(deftest t142_l469 (is (true? v141_l463)))


(def
 v144_l473
 (let
  [{:keys [S]} (la/svd A) sv-norm (math/sqrt (dfn/sum (dfn/* S S)))]
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
