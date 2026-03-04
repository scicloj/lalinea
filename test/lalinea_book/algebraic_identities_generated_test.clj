(ns
 lalinea-book.algebraic-identities-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.math :as math]
  [clojure.test :refer [deftest is]]))


(def v3_l26 (def A (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l30 (def B (t/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l34 (def C (t/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l38 (def I3 (t/eye 3)))


(def v7_l40 (def v (t/column [1 2 3])))


(def v9_l50 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l52 (is (true? v9_l50)))


(def
 v12_l56
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l59 (is (true? v12_l56)))


(def v15_l63 (la/close? (la/add A (t/zeros 3 3)) A))


(deftest t16_l65 (is (true? v15_l63)))


(def v18_l69 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l71 (is (true? v18_l69)))


(def
 v21_l75
 (let
  [alpha 3.5]
  (la/close?
   (la/scale (la/add A B) alpha)
   (la/add (la/scale A alpha) (la/scale B alpha)))))


(deftest t22_l79 (is (true? v21_l75)))


(def
 v24_l83
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale A (* alpha beta))
   (la/scale (la/scale A beta) alpha))))


(deftest t25_l87 (is (true? v24_l83)))


(def
 v27_l98
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l101 (is (true? v27_l98)))


(def v30_l105 (la/close? (la/mmul I3 A) A))


(deftest t31_l107 (is (true? v30_l105)))


(def v33_l111 (la/close? (la/mmul A I3) A))


(deftest t34_l113 (is (true? v33_l111)))


(def
 v36_l117
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l120 (is (true? v36_l117)))


(def
 v39_l124
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l127 (is (true? v39_l124)))


(def
 v42_l131
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul (la/scale A alpha) B))
   (la/close?
    (la/scale (la/mmul A B) alpha)
    (la/mmul A (la/scale B alpha))))))


(deftest t43_l137 (is (true? v42_l131)))


(def v45_l143 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l145 (is (true? v45_l143)))


(def v48_l158 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l160 (is (true? v48_l158)))


(def
 v51_l164
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l167 (is (true? v51_l164)))


(def
 v54_l174
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l177 (is (true? v54_l174)))


(def
 v57_l181
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale A alpha))
   (la/scale (la/transpose A) alpha))))


(deftest t58_l185 (is (true? v57_l181)))


(def
 v60_l197
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale A alpha) (la/scale B beta)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l203 (is (true? v60_l197)))


(def
 v63_l210
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l216 (is (true? v63_l210)))


(def
 v66_l220
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l222 (is (true? v66_l220)))


(def v69_l226 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l228 (is (true? v69_l226)))


(def
 v72_l242
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l245 (is (true? v72_l242)))


(def v75_l249 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l251 (is (true? v75_l249)))


(def v78_l255 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l257 (is (true? v78_l255)))


(def
 v81_l261
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l264 (is (true? v81_l261)))


(def
 v84_l268
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale A alpha))
   (* (math/pow alpha n) (la/det A)))))


(deftest t85_l272 (is (true? v84_l268)))


(def v87_l284 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l286 (is (true? v87_l284)))


(def v90_l290 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l292 (is (true? v90_l290)))


(def v93_l296 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l298 (is (true? v93_l296)))


(def
 v96_l305
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l308 (is (true? v96_l305)))


(def
 v99_l312
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l315 (is (true? v99_l312)))


(def
 v102_l319
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale A alpha))
   (la/scale (la/invert A) (/ 1.0 alpha)))))


(deftest t103_l323 (is (true? v102_l319)))


(def
 v105_l334
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (t/zeros 3 3)) 1.0E-10)))


(deftest t106_l338 (is (true? v105_l334)))


(def
 v108_l342
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale A alpha))
   (* (abs alpha) (la/norm A)))))


(deftest t109_l346 (is (true? v108_l342)))


(def
 v111_l352
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l355 (is (true? v111_l352)))


(def
 v114_l366
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l369 (is (true? v114_l366)))


(def
 v117_l373
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l376 (is (true? v117_l373)))


(def
 v120_l382
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn [i] (every? (fn [j] (< (abs (R i j)) 1.0E-10)) (range 0 i)))
   (range 1 3))))


(deftest t121_l389 (is (true? v120_l382)))


(def
 v123_l400
 (let
  [{:keys [eigenvalues eigenvectors]}
   (la/eigen A)
   reals
   (el/re eigenvalues)]
  (every?
   (fn
    [[i evec]]
    (when
     evec
     (let
      [Av (la/mmul A evec) lam-v (la/scale evec (double (reals i)))]
      (la/close? Av lam-v))))
   (map-indexed vector eigenvectors))))


(deftest t124_l409 (is (true? v123_l400)))


(def
 v126_l413
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (el/sum (el/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l417 (is (true? v126_l413)))


(def
 v129_l421
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (el/prod (el/re eigenvalues))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l425 (is (true? v129_l421)))


(def
 v132_l437
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (t/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l441 (is (true? v132_l437)))


(def
 v135_l445
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l448 (is (true? v135_l445)))


(def
 v138_l452
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l455 (is (true? v138_l452)))


(def
 v141_l459
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (la/real-eigenvalues (la/mmul (la/transpose A) A))
   sv-squared
   (sort > (el/sq S))]
  (la/close?
   (t/->real-tensor sv-squared)
   (t/->real-tensor (reverse AtA-eigs))
   1.0E-8)))


(deftest t142_l465 (is (true? v141_l459)))


(def
 v144_l469
 (let
  [{:keys [S]} (la/svd A) sv-norm (math/sqrt (el/sum (el/mul S S)))]
  (la/close-scalar? (la/norm A) sv-norm)))


(deftest t145_l473 (is (true? v144_l469)))


(def
 v147_l485
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M)))


(deftest t148_l489 (is (true? v147_l485)))


(def v150_l493 (nil? (la/cholesky (t/matrix [[1 2] [2 1]]))))


(deftest t151_l495 (is (true? v150_l493)))


(def
 v153_l505
 (let
  [b (t/column [1 2 3]) x (la/solve A b)]
  (la/close? (la/mmul A x) b)))


(deftest t154_l509 (is (true? v153_l505)))


(def
 v156_l513
 (let
  [b
   (t/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv)))


(deftest t157_l518 (is (true? v156_l513)))


(def
 v159_l527
 (def ca (t/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def
 v160_l528
 (def cb (t/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def v162_l532 (la/close? (el/mul ca cb) (el/mul cb ca)))


(deftest t163_l534 (is (true? v162_l532)))


(def v165_l538 (la/close? (el/conj (el/conj ca)) ca))


(deftest t166_l540 (is (true? v165_l538)))


(def
 v168_l544
 (la/close?
  (el/conj (el/mul ca cb))
  (el/mul (el/conj ca) (el/conj cb))))


(deftest t169_l547 (is (true? v168_l544)))


(def
 v171_l551
 (<
  (el/reduce-max
   (el/abs
    (la/sub (el/abs (el/mul ca cb)) (el/mul (el/abs ca) (el/abs cb)))))
  1.0E-10))


(deftest t172_l556 (is (true? v171_l551)))


(def
 v174_l560
 (let
  [d-ab
   (la/dot ca cb)
   re-ab
   (double (el/re d-ab))
   im-ab
   (double (el/im d-ab))
   re-aa
   (double (el/re (la/dot ca ca)))
   re-bb
   (double (el/re (la/dot cb cb)))]
  (<= (- (+ (* re-ab re-ab) (* im-ab im-ab)) 1.0E-10) (* re-aa re-bb))))


(deftest t175_l568 (is (true? v174_l560)))


(def
 v177_l579
 (let
  [CA
   (t/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CB
   (t/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
   CC
   (t/complex-tensor [[0 1] [2 -1]] [[3 0] [1 1]])]
  (<
   (la/norm
    (la/sub (la/mmul (la/mmul CA CB) CC) (la/mmul CA (la/mmul CB CC))))
   1.0E-10)))


(deftest t178_l586 (is (true? v177_l579)))


(def
 v180_l590
 (let
  [CA
   (t/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t181_l594 (is (true? v180_l590)))


(def
 v183_l598
 (let
  [CA
   (t/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CB
   (t/complex-tensor [[2 0] [1 3]] [[1 -1] [0 2]])
   det-AB
   (la/det (la/mmul CA CB))
   det-A
   (la/det CA)
   det-B
   (la/det CB)
   product
   (el/mul det-A det-B)]
  (< (la/norm (la/sub det-AB product)) 1.0E-10)))


(deftest t184_l606 (is (true? v183_l598)))


(def
 v186_l610
 (let
  [CA
   (t/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (t/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t187_l615 (is (true? v186_l610)))


(def
 v189_l619
 (let
  [CA
   (t/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (t/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t190_l623 (is (true? v189_l619)))
