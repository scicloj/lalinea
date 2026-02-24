(ns
 basis-book.algebraic-identities-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.impl.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l25 (def A (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v4_l29 (def B (la/matrix [[1 -1 2] [0 2 1] [3 0 1]])))


(def v5_l33 (def C (la/matrix [[-1 4 2] [3 0 1] [2 1 5]])))


(def v6_l37 (def I3 (la/eye 3)))


(def v7_l39 (def v (la/column [1 2 3])))


(def
 v9_l43
 (def
  close?
  (fn [x y] (let [diff (la/sub x y)] (< (la/norm diff) 1.0E-10)))))


(def
 v10_l48
 (def
  close-scalar?
  (fn [a b] (< (Math/abs (- (double a) (double b))) 1.0E-10))))


(def v12_l60 (close? (la/add A B) (la/add B A)))


(deftest t13_l62 (is (true? v12_l60)))


(def v15_l66 (close? (la/add (la/add A B) C) (la/add A (la/add B C))))


(deftest t16_l69 (is (true? v15_l66)))


(def v18_l73 (close? (la/add A (la/zeros 3 3)) A))


(deftest t19_l75 (is (true? v18_l73)))


(def v21_l79 (< (la/norm (la/sub A A)) 1.0E-10))


(deftest t22_l81 (is (true? v21_l79)))


(def
 v24_l85
 (let
  [alpha 3.5]
  (close?
   (la/scale alpha (la/add A B))
   (la/add (la/scale alpha A) (la/scale alpha B)))))


(deftest t25_l89 (is (true? v24_l85)))


(def
 v27_l93
 (let
  [alpha 2.0 beta 3.0]
  (close?
   (la/scale (* alpha beta) A)
   (la/scale alpha (la/scale beta A)))))


(deftest t28_l97 (is (true? v27_l93)))


(def
 v30_l108
 (close? (la/mmul (la/mmul A B) C) (la/mmul A (la/mmul B C))))


(deftest t31_l111 (is (true? v30_l108)))


(def v33_l115 (close? (la/mmul I3 A) A))


(deftest t34_l117 (is (true? v33_l115)))


(def v36_l121 (close? (la/mmul A I3) A))


(deftest t37_l123 (is (true? v36_l121)))


(def
 v39_l127
 (close? (la/mmul A (la/add B C)) (la/add (la/mmul A B) (la/mmul A C))))


(deftest t40_l130 (is (true? v39_l127)))


(def
 v42_l134
 (close? (la/mmul (la/add A B) C) (la/add (la/mmul A C) (la/mmul B C))))


(deftest t43_l137 (is (true? v42_l134)))


(def
 v45_l141
 (let
  [alpha 2.5]
  (and
   (close?
    (la/scale alpha (la/mmul A B))
    (la/mmul (la/scale alpha A) B))
   (close?
    (la/scale alpha (la/mmul A B))
    (la/mmul A (la/scale alpha B))))))


(deftest t46_l147 (is (true? v45_l141)))


(def v48_l154 (> (la/norm (la/sub (la/mmul A B) (la/mmul B A))) 0.01))


(deftest t49_l156 (is (true? v48_l154)))


(def v51_l169 (close? (la/transpose (la/transpose A)) A))


(deftest t52_l171 (is (true? v51_l169)))


(def
 v54_l175
 (close?
  (la/transpose (la/add A B))
  (la/add (la/transpose A) (la/transpose B))))


(deftest t55_l178 (is (true? v54_l175)))


(def
 v57_l185
 (close?
  (la/transpose (la/mmul A B))
  (la/mmul (la/transpose B) (la/transpose A))))


(deftest t58_l188 (is (true? v57_l185)))


(def
 v60_l192
 (let
  [alpha 4.0]
  (close?
   (la/transpose (la/scale alpha A))
   (la/scale alpha (la/transpose A)))))


(deftest t61_l196 (is (true? v60_l192)))


(def
 v63_l208
 (let
  [alpha 2.0 beta 3.0]
  (close-scalar?
   (la/trace (la/add (la/scale alpha A) (la/scale beta B)))
   (+ (* alpha (la/trace A)) (* beta (la/trace B))))))


(deftest t64_l214 (is (true? v63_l208)))


(def
 v66_l221
 (let
  [trABC
   (la/trace (la/mmul A (la/mmul B C)))
   trBCA
   (la/trace (la/mmul B (la/mmul C A)))
   trCAB
   (la/trace (la/mmul C (la/mmul A B)))]
  (and (close-scalar? trABC trBCA) (close-scalar? trBCA trCAB))))


(deftest t67_l227 (is (true? v66_l221)))


(def v69_l231 (close-scalar? (la/trace (la/transpose A)) (la/trace A)))


(deftest t70_l233 (is (true? v69_l231)))


(def v72_l237 (close-scalar? (la/trace I3) 3.0))


(deftest t73_l239 (is (true? v72_l237)))


(def
 v75_l253
 (close-scalar? (la/det (la/mmul A B)) (* (la/det A) (la/det B))))


(deftest t76_l256 (is (true? v75_l253)))


(def v78_l260 (close-scalar? (la/det (la/transpose A)) (la/det A)))


(deftest t79_l262 (is (true? v78_l260)))


(def v81_l266 (close-scalar? (la/det I3) 1.0))


(deftest t82_l268 (is (true? v81_l266)))


(def v84_l272 (close-scalar? (la/det (la/invert A)) (/ 1.0 (la/det A))))


(deftest t85_l275 (is (true? v84_l272)))


(def
 v87_l279
 (let
  [alpha 2.0 n 3]
  (close-scalar?
   (la/det (la/scale alpha A))
   (* (Math/pow alpha n) (la/det A)))))


(deftest t88_l283 (is (true? v87_l279)))


(def v90_l295 (close? (la/mmul A (la/invert A)) I3))


(deftest t91_l297 (is (true? v90_l295)))


(def v93_l301 (close? (la/mmul (la/invert A) A) I3))


(deftest t94_l303 (is (true? v93_l301)))


(def v96_l307 (close? (la/invert (la/invert A)) A))


(deftest t97_l309 (is (true? v96_l307)))


(def
 v99_l316
 (close?
  (la/invert (la/mmul A B))
  (la/mmul (la/invert B) (la/invert A))))


(deftest t100_l319 (is (true? v99_l316)))


(def
 v102_l323
 (close? (la/transpose (la/invert A)) (la/invert (la/transpose A))))


(deftest t103_l326 (is (true? v102_l323)))


(def
 v105_l330
 (let
  [alpha 2.0]
  (close?
   (la/invert (la/scale alpha A))
   (la/scale (/ 1.0 alpha) (la/invert A)))))


(deftest t106_l334 (is (true? v105_l330)))


(def
 v108_l345
 (and
  (>= (la/norm A) 0)
  (> (la/norm A) 0)
  (< (la/norm (la/zeros 3 3)) 1.0E-10)))


(deftest t109_l349 (is (true? v108_l345)))


(def
 v111_l353
 (let
  [alpha -2.5]
  (close-scalar?
   (la/norm (la/scale alpha A))
   (* (Math/abs alpha) (la/norm A)))))


(deftest t112_l357 (is (true? v111_l353)))


(def
 v114_l363
 (close-scalar?
  (* (la/norm A) (la/norm A))
  (la/trace (la/mmul (la/transpose A) A))))


(deftest t115_l366 (is (true? v114_l363)))


(def v117_l377 (let [{:keys [Q R]} (la/qr A)] (close? (la/mmul Q R) A)))


(deftest t118_l380 (is (true? v117_l377)))


(def
 v120_l384
 (let [{:keys [Q]} (la/qr A)] (close? (la/mmul (la/transpose Q) Q) I3)))


(deftest t121_l387 (is (true? v120_l384)))


(def
 v123_l393
 (let
  [{:keys [R]} (la/qr A)]
  (every?
   (fn
    [i]
    (every?
     (fn [j] (< (Math/abs (tensor/mget R i j)) 1.0E-10))
     (range 0 i)))
   (range 1 3))))


(deftest t124_l400 (is (true? v123_l393)))


(def
 v126_l411
 (let
  [{:keys [eigenvalues eigenvectors]} (la/eigen A)]
  (every?
   (fn
    [[[lam-re _lam-im] evec]]
    (when
     evec
     (let
      [Av (la/mmul A evec) lam-v (la/scale lam-re evec)]
      (close? Av lam-v))))
   (map vector eigenvalues eigenvectors))))


(deftest t127_l419 (is (true? v126_l411)))


(def
 v129_l423
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-sum
   (reduce + (map first eigenvalues))]
  (close-scalar? (la/trace A) eig-sum)))


(deftest t130_l427 (is (true? v129_l423)))


(def
 v132_l431
 (let
  [{:keys [eigenvalues]}
   (la/eigen A)
   eig-prod
   (reduce * (map first eigenvalues))]
  (close-scalar? (la/det A) eig-prod)))


(deftest t133_l435 (is (true? v132_l431)))


(def
 v135_l447
 (let
  [{:keys [U S Vt]} (la/svd A) Sigma (la/diag S)]
  (close? (la/mmul U (la/mmul Sigma Vt)) A)))


(deftest t136_l451 (is (true? v135_l447)))


(def
 v138_l455
 (let
  [{:keys [U]} (la/svd A)]
  (close? (la/mmul (la/transpose U) U) I3)))


(deftest t139_l458 (is (true? v138_l455)))


(def
 v141_l462
 (let
  [{:keys [Vt]} (la/svd A)]
  (close? (la/mmul Vt (la/transpose Vt)) I3)))


(deftest t142_l465 (is (true? v141_l462)))


(def
 v144_l469
 (let
  [{:keys [S]}
   (la/svd A)
   AtA-eigs
   (->>
    (:eigenvalues (la/eigen (la/mmul (la/transpose A) A)))
    (map first)
    sort
    reverse
    vec)
   sv-squared
   (mapv (fn* [p1__239326#] (* p1__239326# p1__239326#)) (sort > S))]
  (every?
   identity
   (map (fn [a b] (< (Math/abs (- a b)) 1.0E-8)) sv-squared AtA-eigs))))


(deftest t145_l480 (is (true? v144_l469)))


(def
 v147_l484
 (let
  [{:keys [S]}
   (la/svd A)
   sv-norm
   (Math/sqrt
    (reduce
     +
     (map (fn* [p1__239327#] (* p1__239327# p1__239327#)) S)))]
  (close-scalar? (la/norm A) sv-norm)))


(deftest t148_l488 (is (true? v147_l484)))


(def
 v150_l500
 (let
  [M (la/add (la/mmul (la/transpose A) A) I3) L (la/cholesky M)]
  (close? (la/mmul L (la/transpose L)) M)))


(deftest t151_l504 (is (true? v150_l500)))


(def v153_l508 (nil? (la/cholesky (la/matrix [[1 2] [2 1]]))))


(deftest t154_l510 (is (true? v153_l508)))


(def
 v156_l520
 (let
  [b (la/column [1 2 3]) x (la/solve A b)]
  (close? (la/mmul A x) b)))


(deftest t157_l524 (is (true? v156_l520)))


(def
 v159_l528
 (let
  [b
   (la/column [1 2 3])
   x-solve
   (la/solve A b)
   x-inv
   (la/mmul (la/invert A) b)]
  (close? x-solve x-inv)))


(deftest t160_l533 (is (true? v159_l528)))


(def
 v162_l545
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


(deftest t163_l552 (is (true? v162_l545)))


(def
 v165_l556
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   AAdag
   (la/mmul CA (la/transpose CA))]
  (< (la/norm (la/sub AAdag (la/transpose AAdag))) 1.0E-10)))


(deftest t166_l560 (is (true? v165_l556)))


(def
 v168_l564
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
   (cx/mul det-A det-B)]
  (< (la/norm (la/sub det-AB product)) 1.0E-10)))


(deftest t169_l572 (is (true? v168_l564)))


(def
 v171_l576
 (let
  [CA
   (cx/complex-tensor [[2 1] [1 3]] [[1 0] [0 1]])
   Cb
   (cx/complex-tensor [[1] [2]] [[1] [0]])
   Cx
   (la/solve CA Cb)]
  (< (la/norm (la/sub (la/mmul CA Cx) Cb)) 1.0E-10)))


(deftest t172_l581 (is (true? v171_l576)))


(def
 v174_l585
 (let
  [CA
   (cx/complex-tensor [[1 2] [3 4]] [[0.5 1] [1.5 2]])
   CI
   (cx/complex-tensor [[1 0] [0 1]] [[0 0] [0 0]])]
  (< (la/norm (la/sub (la/mmul CA (la/invert CA)) CI)) 1.0E-10)))


(deftest t175_l589 (is (true? v174_l585)))
