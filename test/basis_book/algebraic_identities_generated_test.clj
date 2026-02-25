(ns
 basis-book.algebraic-identities-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l31 (def A (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l35 (def B (la/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l39 (def C (la/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l43 (def I3 (la/eye 3)))


(def v7_l45 (def v (la/column [1 2 3])))


(def v9_l55 (la/close? (la/add A B) (la/add B A)))


(deftest t10_l57 (is (true? v9_l55)))


(def
 v12_l61
 (la/close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t13_l64 (is (true? v12_l61)))


(def v15_l68 (la/close? (la/add A (la/zeros 3 3)) A))


(deftest t16_l70 (is (true? v15_l68)))


(def v18_l74 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t19_l76 (is (true? v18_l74)))


(def
 v21_l80
 (let
  [alpha 3.5]
  (la/close?
   (la/scale alpha (la/add A B))
   (la/add (la/scale alpha A) (la/scale alpha B)))))


(deftest t22_l84 (is (true? v21_l80)))


(def
 v24_l88
 (let
  [alpha 2.0 beta 3.0]
  (la/close?
   (la/scale (* alpha beta) A)
   (la/scale alpha (la/scale beta A)))))


(deftest t25_l92 (is (true? v24_l88)))


(def
 v27_l103
 (la/close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t28_l106 (is (true? v27_l103)))


(def v30_l110 (la/close? (la/mmul I3 A) A))


(deftest t31_l112 (is (true? v30_l110)))


(def v33_l116 (la/close? (la/mmul A I3) A))


(deftest t34_l118 (is (true? v33_l116)))


(def
 v36_l122
 (la/close?
  (la/mmul A (la/add B C))
  (la/add (la/mmul A B) (la/mmul A C))))


(deftest t37_l125 (is (true? v36_l122)))


(def
 v39_l129
 (la/close?
  (la/mmul (la/add A B) C)
  (la/add (la/mmul A C) (la/mmul B C))))


(deftest t40_l132 (is (true? v39_l129)))


(def
 v42_l136
 (let
  [alpha 2.5]
  (and
   (la/close?
    (la/scale alpha (la/mmul A B))
    (la/mmul (la/scale alpha A) B))
   (la/close?
    (la/scale alpha (la/mmul A B))
    (la/mmul A (la/scale alpha B))))))


(deftest t43_l142 (is (true? v42_l136)))


(def v45_l149 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t46_l151 (is (true? v45_l149)))


(def v48_l164 (la/close? (la/transpose (la/transpose A)) A))


(deftest t49_l166 (is (true? v48_l164)))


(def
 v51_l170
 (la/close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t52_l173 (is (true? v51_l170)))


(def
 v54_l180
 (la/close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t55_l183 (is (true? v54_l180)))


(def
 v57_l187
 (let
  [alpha 4.0]
  (la/close?
   (la/transpose (la/scale alpha A))
   (la/scale alpha (la/transpose A)))))


(deftest t58_l191 (is (true? v57_l187)))


(def
 v60_l203
 (let
  [alpha 2.0 beta 3.0]
  (la/close-scalar?
   (la/trace (la/add (la/scale alpha A) (la/scale beta B)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t61_l209 (is (true? v60_l203)))


(def
 v63_l216
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (la/close-scalar? trABC trBCA) (la/close-scalar? trBCA trCAB))))


(deftest t64_l222 (is (true? v63_l216)))


(def
 v66_l226
 (la/close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t67_l228 (is (true? v66_l226)))


(def v69_l232 (la/close-scalar? (la/trace I3) 3.0))


(deftest t70_l234 (is (true? v69_l232)))


(def
 v72_l248
 (la/close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t73_l251 (is (true? v72_l248)))


(def v75_l255 (la/close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t76_l257 (is (true? v75_l255)))


(def v78_l261 (la/close-scalar? (la/det I3) 1.0))


(deftest t79_l263 (is (true? v78_l261)))


(def
 v81_l267
 (la/close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t82_l270 (is (true? v81_l267)))


(def
 v84_l274
 (let
  [alpha 2.0 n 3]
  (la/close-scalar?
   (la/det (la/scale alpha A))
   (* (Math/pow alpha n) (la/det A)))))


(deftest t85_l278 (is (true? v84_l274)))


(def v87_l290 (la/close? (la/mmul A (la/invert A)) I3))


(deftest t88_l292 (is (true? v87_l290)))


(def v90_l296 (la/close? (la/mmul (la/invert A) A) I3))


(deftest t91_l298 (is (true? v90_l296)))


(def v93_l302 (la/close? (la/invert (la/invert A)) A))


(deftest t94_l304 (is (true? v93_l302)))


(def
 v96_l311
 (la/close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t97_l314 (is (true? v96_l311)))


(def
 v99_l318
 (la/close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t100_l321 (is (true? v99_l318)))


(def
 v102_l325
 (let
  [alpha 2.0]
  (la/close?
   (la/invert (la/scale alpha A))
   (la/scale (/ 1.0 alpha) (la/invert A)))))


(deftest t103_l329 (is (true? v102_l325)))


(def
 v105_l340
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (la/zeros 3 3)) 1.0E-10)))


(deftest t106_l344 (is (true? v105_l340)))


(def
 v108_l348
 (let
  [alpha -2.5]
  (la/close-scalar?
   (la/norm (la/scale alpha A))
   (* (Math/abs alpha) (la/norm A)))))


(deftest t109_l352 (is (true? v108_l348)))


(def
 v111_l358
 (la/close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t112_l361 (is (true? v111_l358)))


(def
 v114_l372
 (let [{:keys [Q R]} (la/qr A)] (la/close? (la/mmul Q R) A)))


(deftest t115_l375 (is (true? v114_l372)))


(def
 v117_l379
 (let
  [{:keys [Q]} (la/qr A)]
  (la/close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t118_l382 (is (true? v117_l379)))


(def
 v120_l388
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn
    [i]
    (every?
     (fn [j] (< (Math/abs (tensor/mget R i j)) 1.0E-10))
     (range 0 i)))
   (range 1 3))))


(deftest t121_l395 (is (true? v120_l388)))


(def
 v123_l406
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
      [Av (la/mmul A evec) lam-v (la/scale (double (reals i)) evec)]
      (la/close? Av lam-v))))
   (map-indexed vector eigenvectors))))


(deftest t124_l415 (is (true? v123_l406)))


(def
 v126_l419
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (dfn/sum (cx/re eigenvalues))]
  (la/close-scalar? (la/trace A) eig-sum)))


(deftest t127_l423 (is (true? v126_l419)))


(def
 v129_l427
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (reduce * (seq (cx/re eigenvalues)))]
  (la/close-scalar? (la/det A) eig-prod)))


(deftest t130_l431 (is (true? v129_l427)))


(def
 v132_l443
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (la/diag S)]
  (la/close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t133_l447 (is (true? v132_l443)))


(def
 v135_l451
 (let
  [{:keys [U]} (la/svd A)]
  (la/close? (la/mmul (la/transpose U) U) I3)))


(deftest t136_l454 (is (true? v135_l451)))


(def
 v138_l458
 (let
  [{:keys [Vt]} (la/svd A)]
  (la/close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t139_l461 (is (true? v138_l458)))


(def
 v141_l465
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (la/real-eigenvalues (la/mmul (la/transpose A) A))
   sv-squared
   (sort > (map (fn* [p1__97689#] (* p1__97689# p1__97689#)) S))]
  (every?
   identity
   (map
    (fn [a b] (< (Math/abs (- a b)) 1.0E-8))
    sv-squared
    (reverse (seq AtA-eigs))))))


(deftest t142_l472 (is (true? v141_l465)))


(def
 v144_l476
 (let
  [{:keys [S]}
   (la/svd A)
   sv-norm
   (Math/sqrt
    (reduce + (map (fn* [p1__97690#] (* p1__97690# p1__97690#)) S)))]
  (la/close-scalar? (la/norm A) sv-norm)))


(deftest t145_l480 (is (true? v144_l476)))


(def
 v147_l492
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (la/close? (la/mmul L (la/transpose L)) M)))


(deftest t148_l496 (is (true? v147_l492)))


(def v150_l500 (nil? (la/cholesky (la/matrix [[1 2] [2 1]]))))


(deftest t151_l502 (is (true? v150_l500)))


(def
 v153_l512
 (let
  [b (la/column [1 2 3]) x (la/solve A b)]
  (la/close? (la/mmul A x) b)))


(deftest t154_l516 (is (true? v153_l512)))


(def
 v156_l520
 (let
  [b
   (la/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (la/close? x-solve x-inv)))


(deftest t157_l525 (is (true? v156_l520)))


(def
 v159_l534
 (def ca (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def
 v160_l535
 (def cb (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def v162_l539 (la/close? (la/mul ca cb) (la/mul cb ca)))


(deftest t163_l541 (is (true? v162_l539)))


(def v165_l545 (la/close? (cx/conj (cx/conj ca)) ca))


(deftest t166_l547 (is (true? v165_l545)))


(def
 v168_l551
 (la/close?
  (cx/conj (la/mul ca cb))
  (la/mul (cx/conj ca) (cx/conj cb))))


(deftest t169_l554 (is (true? v168_l551)))


(def
 v171_l558
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/- (la/abs (la/mul ca cb)) (dfn/* (la/abs ca) (la/abs cb)))))
  1.0E-10))


(deftest t172_l563 (is (true? v171_l558)))


(def
 v174_l567
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


(deftest t175_l575 (is (true? v174_l567)))


(def
 v177_l586
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


(deftest t178_l593 (is (true? v177_l586)))


(def
 v180_l597
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t181_l601 (is (true? v180_l597)))


(def
 v183_l605
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


(deftest t184_l613 (is (true? v183_l605)))


(def
 v186_l617
 (let
  [CA
   (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (cx/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t187_l622 (is (true? v186_l617)))


(def
 v189_l626
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t190_l630 (is (true? v189_l626)))
