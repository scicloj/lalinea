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


(def v8_l66 (la/real-eigenvalues A-eig))


(deftest
 t9_l68
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v8_l66)))


(def
 v11_l79
 (every?
  (fn
   [i]
   (let
    [lam
     (cx/re ((:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale lam ev)))
     1.0E-10)))
  (range 3)))


(deftest t12_l87 (is (true? v11_l79)))


(def v14_l100 (def eig-reals (cx/re (:eigenvalues eig-result))))


(def
 v15_l102
 (< (Math/abs (- (la/trace A-eig) (dfn/sum eig-reals))) 1.0E-10))


(deftest t16_l104 (is (true? v15_l102)))


(def
 v17_l106
 (< (Math/abs (- (la/det A-eig) (reduce * (seq eig-reals)))) 1.0E-10))


(deftest t18_l108 (is (true? v17_l106)))


(def v20_l137 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v22_l143 (def eig-diag (la/eigen A-diag)))


(def
 v23_l145
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


(def v25_l156 (def P-cols (la/transpose P-diag)))


(def
 v27_l160
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v28_l163 D-result)


(deftest
 t29_l165
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v28_l163)))


(def v31_l181 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v32_l184
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


(def v33_l191 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t34_l193 (is (true? v33_l191)))


(def v36_l214 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v38_l221 (la/close? S-sym (la/transpose S-sym)))


(deftest t39_l223 (is (true? v38_l221)))


(def v40_l225 (def eig-S (la/eigen S-sym)))


(def
 v42_l229
 (< (dfn/reduce-max (dfn/abs (cx/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t43_l231 (is (true? v42_l229)))


(def
 v45_l236
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v46_l241 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v47_l243 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t48_l245 (is ((fn [d] (< d 1.0E-10)) v47_l243)))


(def v50_l284 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v51_l288 (def svd-A (la/svd A-svd)))


(def v52_l290 (vec (:S svd-A)))


(deftest
 t53_l292
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v52_l290)))


(def v55_l318 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v56_l322 (def svd-lr (la/svd A-lr)))


(def v57_l324 (def sigmas (vec (:S svd-lr))))


(def v58_l326 sigmas)


(def
 v60_l330
 (def
  A-rank1
  (la/scale
   (first sigmas)
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all)))))


(def v62_l337 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v63_l339 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t64_l341 (is (true? v63_l339)))


(def v66_l367 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v67_l369
 (every?
  (fn* [p1__119008#] (>= p1__119008# -1.0E-10))
  (cx/re (:eigenvalues (la/eigen ATA)))))


(deftest t68_l371 (is (true? v67_l369)))


(def
 v70_l382
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v71_l385 (def chol-L (la/cholesky spd-mat)))


(def
 v73_l389
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t74_l391 (is ((fn [d] (< d 1.0E-10)) v73_l389)))


(def v76_l396 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t77_l398 (is (nil? v76_l396)))


(def v79_l410 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v81_l420 (la/close? A-final (la/transpose A-final)))


(deftest t82_l422 (is (true? v81_l420)))


(def v84_l426 (def eig-final (la/eigen A-final)))


(def v85_l428 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v86_l431 final-eigenvalues)


(deftest
 t87_l433
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v86_l431)))


(def
 v89_l441
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t90_l445 (is (true? v89_l441)))


(def
 v92_l449
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t93_l453 (is (true? v92_l449)))


(def v95_l457 (def final-svd (la/svd A-final)))


(def
 v96_l459
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t97_l464 (is (true? v96_l459)))


(def
 v99_l468
 (count
  (filter
   (fn* [p1__119009#] (> p1__119009# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t100_l470 (is ((fn [r] (= r 3)) v99_l468)))


(def v101_l473 (def A-inv (la/invert A-final)))


(def v102_l475 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t103_l477 (is (true? v102_l475)))


(def v105_l481 (def chol-final (la/cholesky A-final)))


(def
 v106_l483
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t107_l485 (is (true? v106_l483)))
