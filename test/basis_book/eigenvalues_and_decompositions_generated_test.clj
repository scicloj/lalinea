(ns
 basis-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.basis.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def
 v3_l48
 (vis/arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v5_l56 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v7_l64 (def eig-result (la/eigen A-eig)))


(def v9_l74 (la/real-eigenvalues A-eig))


(deftest
 t10_l76
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v9_l74)))


(def
 v12_l87
 (every?
  (fn
   [i]
   (let
    [lam
     (cx/re ((:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale ev lam)))
     1.0E-10)))
  (range 3)))


(deftest t13_l95 (is (true? v12_l87)))


(def v15_l108 (def eig-reals (cx/re (:eigenvalues eig-result))))


(def
 v16_l110
 (< (Math/abs (- (la/trace A-eig) (dfn/sum eig-reals))) 1.0E-10))


(deftest t17_l112 (is (true? v16_l110)))


(def
 v18_l114
 (< (Math/abs (- (la/det A-eig) (reduce * (seq eig-reals)))) 1.0E-10))


(deftest t19_l116 (is (true? v18_l114)))


(def v21_l145 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v23_l151 (def eig-diag (la/eigen A-diag)))


(def
 v24_l153
 (def
  P-diag
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by (fn [i] (cx/re ((:eigenvalues eig-diag) i))) (range 2))]
   (la/matrix
    (mapv
     (fn [j] (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
     (range 2))))))


(def v26_l164 (def P-cols (la/transpose P-diag)))


(def
 v28_l168
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v29_l171 D-result)


(deftest
 t30_l173
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v29_l171)))


(def v32_l189 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v33_l192
 (def
  A-diag-sq-via-eigen
  (let
   [Pinv
    (la/invert P-cols)
    D2
    (la/diag
     (dtype/make-reader
      :float64
      2
      (let [lam (tensor/mget D-result idx idx)] (* lam lam))))]
   (la/mmul P-cols (la/mmul D2 Pinv)))))


(def v34_l199 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t35_l201 (is (true? v34_l199)))


(def v37_l222 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v39_l229 (la/close? S-sym (la/transpose S-sym)))


(deftest t40_l231 (is (true? v39_l229)))


(def v41_l233 (def eig-S (la/eigen S-sym)))


(def
 v43_l237
 (< (dfn/reduce-max (dfn/abs (cx/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t44_l239 (is (true? v43_l237)))


(def
 v46_l244
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v47_l249 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v48_l251 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t49_l253 (is ((fn [d] (< d 1.0E-10)) v48_l251)))


(def v51_l305 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v52_l309 (def svd-A (la/svd A-svd)))


(def v53_l311 (vec (:S svd-A)))


(deftest
 t54_l313
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v53_l311)))


(def v56_l339 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v57_l343 (def svd-lr (la/svd A-lr)))


(def v58_l345 (def sigmas (vec (:S svd-lr))))


(def v59_l347 sigmas)


(def
 v61_l351
 (def
  A-rank1
  (la/scale
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v63_l357 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v64_l359 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t65_l361 (is (true? v64_l359)))


(def v67_l387 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v68_l389
 (every?
  (fn* [p1__74429#] (>= p1__74429# -1.0E-10))
  (cx/re (:eigenvalues (la/eigen ATA)))))


(deftest t69_l391 (is (true? v68_l389)))


(def
 v71_l402
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v72_l405 (def chol-L (la/cholesky spd-mat)))


(def
 v74_l409
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t75_l411 (is ((fn [d] (< d 1.0E-10)) v74_l409)))


(def v77_l416 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t78_l418 (is (nil? v77_l416)))


(def v80_l430 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v82_l440 (la/close? A-final (la/transpose A-final)))


(deftest t83_l442 (is (true? v82_l440)))


(def v85_l446 (def eig-final (la/eigen A-final)))


(def v86_l448 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v87_l451 final-eigenvalues)


(deftest
 t88_l453
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v87_l451)))


(def
 v90_l461
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t91_l465 (is (true? v90_l461)))


(def
 v93_l469
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t94_l473 (is (true? v93_l469)))


(def v96_l477 (def final-svd (la/svd A-final)))


(def
 v97_l479
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t98_l484 (is (true? v97_l479)))


(def
 v100_l488
 (count
  (filter
   (fn* [p1__74430#] (> p1__74430# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t101_l490 (is ((fn [r] (= r 3)) v100_l488)))


(def v102_l493 (def A-inv (la/invert A-final)))


(def v103_l495 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t104_l497 (is (true? v103_l495)))


(def v106_l501 (def chol-final (la/cholesky A-final)))


(def
 v107_l503
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t108_l505 (is (true? v107_l503)))
