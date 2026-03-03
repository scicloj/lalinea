(ns
 lalinea-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def
 v3_l43
 (vis/arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def
 v5_l52
 (vis/arrow-plot
  [{:label "u", :xy [0 1], :color "#999999"}
   {:label "Au", :xy [1 3], :color "#228833", :dashed? true}
   {:label "v2", :xy [1 1], :color "#cc4422"}
   {:label "Av2", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v7_l60 (def A-eig (t/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v9_l68 (def eig-result (la/eigen A-eig)))


(def v11_l78 (la/real-eigenvalues A-eig))


(deftest
 t12_l80
 (is
  ((fn
    [v]
    (and
     (< (abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (abs (- (nth v 2) 4.0)) 1.0E-10)))
   v11_l78)))


(def
 v14_l91
 (every?
  (fn
   [i]
   (let
    [lam
     (la/re ((:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale ev lam)))
     1.0E-10)))
  (range 3)))


(deftest t15_l99 (is (true? v14_l91)))


(def v17_l112 (def eig-reals (la/re (:eigenvalues eig-result))))


(def v18_l114 (< (abs (- (la/trace A-eig) (la/sum eig-reals))) 1.0E-10))


(deftest t19_l116 (is (true? v18_l114)))


(def v20_l118 (< (abs (- (la/det A-eig) (la/prod eig-reals))) 1.0E-10))


(deftest t21_l120 (is (true? v20_l118)))


(def v23_l149 (def A-diag (t/matrix [[2 1] [0 3]])))


(def v25_l155 (def eig-diag (la/eigen A-diag)))


(def
 v26_l157
 (def
  P-cols
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by (fn [i] (la/re ((:eigenvalues eig-diag) i))) (range 2))]
   (t/hstack
    (mapv (fn* [p1__121705#] (nth evecs p1__121705#)) sorted-idx)))))


(def
 v28_l165
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v29_l168 D-result)


(deftest
 t30_l170
 (is
  ((fn
    [d]
    (and
     (< (abs (- (d 0 0) 2.0)) 1.0E-10)
     (< (abs (d 0 1)) 1.0E-10)
     (< (abs (d 1 0)) 1.0E-10)
     (< (abs (- (d 1 1) 3.0)) 1.0E-10)))
   v29_l168)))


(def v32_l186 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v33_l189
 (def
  A-diag-sq-via-eigen
  (let
   [Pinv
    (la/invert P-cols)
    D2
    (t/diag
     (t/make-reader
      :float64
      2
      (let [lam (D-result idx idx)] (* lam lam))))]
   (la/mmul P-cols (la/mmul D2 Pinv)))))


(def v34_l196 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t35_l198 (is (true? v34_l196)))


(def v37_l219 (def S-sym (t/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v39_l226 (la/close? S-sym (la/transpose S-sym)))


(deftest t40_l228 (is (true? v39_l226)))


(def v41_l230 (def eig-S (la/eigen S-sym)))


(def
 v43_l234
 (< (elem/reduce-max (elem/abs (la/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t44_l236 (is (true? v43_l234)))


(def v46_l241 (def Q-eig (t/hstack (:eigenvectors eig-S))))


(def v47_l244 (def QtQ (la/mmul (la/transpose Q-eig) Q-eig)))


(def v48_l246 (la/norm (la/sub QtQ (t/eye 3))))


(deftest t49_l248 (is ((fn [d] (< d 1.0E-10)) v48_l246)))


(def v51_l300 (def A-svd (t/matrix [[1 0 1] [0 1 1]])))


(def v52_l304 (def svd-A (la/svd A-svd)))


(def v53_l306 (:S svd-A))


(deftest
 t54_l308
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v53_l306)))


(def v56_l334 (def A-lr (t/matrix [[3 2 2] [2 3 -2]])))


(def v57_l338 (def svd-lr (la/svd A-lr)))


(def v58_l340 (def sigmas (:S svd-lr)))


(def v59_l342 sigmas)


(deftest
 t60_l344
 (is
  ((fn
    [s]
    (and (= 2 (count s)) (every? pos? s) (>= (first s) (second s))))
   v59_l342)))


(def
 v62_l351
 (def
  A-rank1
  (la/scale
   (la/mmul
    (t/submatrix (:U svd-lr) :all [0])
    (t/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v64_l357 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v65_l359 (< (abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t66_l361 (is (true? v65_l359)))


(def v68_l387 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v69_l389
 (every?
  (fn* [p1__121706#] (>= p1__121706# -1.0E-10))
  (la/re (:eigenvalues (la/eigen ATA)))))


(deftest t70_l391 (is (true? v69_l389)))


(def
 v72_l402
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (t/eye 3))))


(def v73_l405 (def chol-L (la/cholesky spd-mat)))


(def v74_l407 chol-L)


(deftest
 t75_l409
 (is
  ((fn
    [L]
    (let
     [[r c] (t/shape L)]
     (and
      (= r c)
      (every?
       (fn
        [i]
        (every? (fn [j] (< (abs (L i j)) 1.0E-10)) (range (inc i) c)))
       (range r)))))
   v74_l407)))


(def
 v77_l419
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t78_l421 (is ((fn [d] (< d 1.0E-10)) v77_l419)))


(def v80_l426 (la/cholesky (t/matrix [[1 2] [2 1]])))


(deftest t81_l428 (is (nil? v80_l426)))


(def v83_l440 (def A-final (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v85_l450 (la/close? A-final (la/transpose A-final)))


(deftest t86_l452 (is (true? v85_l450)))


(def v88_l456 (def eig-final (la/eigen A-final)))


(def v89_l458 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v90_l461 final-eigenvalues)


(deftest
 t91_l463
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v90_l461)))


(def
 v93_l471
 (< (abs (- (la/trace A-final) (la/sum final-eigenvalues))) 1.0E-10))


(deftest t94_l475 (is (true? v93_l471)))


(def
 v96_l479
 (< (abs (- (la/det A-final) (la/prod final-eigenvalues))) 1.0E-10))


(deftest t97_l483 (is (true? v96_l479)))


(def v99_l491 (def final-svd (la/svd A-final)))


(def
 v100_l493
 (<
  (elem/reduce-max
   (elem/abs (la/sub (sort (:S final-svd)) final-eigenvalues)))
  1.0E-10))


(deftest t101_l498 (is (true? v100_l493)))


(def v103_l502 (long (la/sum (elem/gt (:S final-svd) 1.0E-10))))


(deftest t104_l504 (is ((fn [r] (= r 3)) v103_l502)))


(def v105_l507 (def A-inv (la/invert A-final)))


(def v106_l509 A-inv)


(deftest t107_l511 (is ((fn [m] (= [3 3] (t/shape m))) v106_l509)))


(def v108_l514 (la/close? (la/mmul A-final A-inv) (t/eye 3)))


(deftest t109_l516 (is (true? v108_l514)))


(def v111_l523 (def chol-final (la/cholesky A-final)))


(def v112_l525 chol-final)


(def
 v113_l527
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t114_l529 (is (true? v113_l527)))
