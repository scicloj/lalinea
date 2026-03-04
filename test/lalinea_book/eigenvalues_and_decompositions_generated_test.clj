(ns
 lalinea-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as el]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def
 v3_l45
 (vis/arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def
 v5_l54
 (vis/arrow-plot
  [{:label "u", :xy [0 1], :color "#999999"}
   {:label "Au", :xy [1 3], :color "#228833", :dashed? true}
   {:label "v2", :xy [1 1], :color "#cc4422"}
   {:label "Av2", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v7_l62 (def A-eig (t/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v9_l70 (def eig-result (la/eigen A-eig)))


(def v11_l84 (la/real-eigenvalues A-eig))


(deftest
 t12_l86
 (is
  ((fn
    [v]
    (and
     (< (abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (abs (- (nth v 2) 4.0)) 1.0E-10)))
   v11_l84)))


(def
 v14_l97
 (every?
  (fn
   [i]
   (let
    [lam
     (el/re ((:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (< (la/norm (el/- (la/mmul A-eig ev) (el/scale ev lam))) 1.0E-10)))
  (range 3)))


(deftest t15_l105 (is (true? v14_l97)))


(def v17_l118 (def eig-reals (el/re (:eigenvalues eig-result))))


(def v18_l120 (< (abs (- (la/trace A-eig) (el/sum eig-reals))) 1.0E-10))


(deftest t19_l122 (is (true? v18_l120)))


(def
 v20_l124
 (< (abs (- (la/det A-eig) (el/reduce-* eig-reals))) 1.0E-10))


(deftest t21_l126 (is (true? v20_l124)))


(def v23_l155 (def A-diag (t/matrix [[2 1] [0 3]])))


(def v25_l161 (def eig-diag (la/eigen A-diag)))


(def
 v26_l163
 (def
  P-cols
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by (fn [i] (el/re ((:eigenvalues eig-diag) i))) (range 2))]
   (t/hstack
    (mapv (fn* [p1__66271#] (nth evecs p1__66271#)) sorted-idx)))))


(def
 v28_l171
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v29_l174 D-result)


(deftest
 t30_l176
 (is
  ((fn
    [d]
    (and
     (< (abs (- (d 0 0) 2.0)) 1.0E-10)
     (< (abs (d 0 1)) 1.0E-10)
     (< (abs (d 1 0)) 1.0E-10)
     (< (abs (- (d 1 1) 3.0)) 1.0E-10)))
   v29_l174)))


(def v32_l192 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v33_l195
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


(def v34_l202 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t35_l204 (is (true? v34_l202)))


(def v37_l225 (def S-sym (t/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v39_l232 (la/close? S-sym (la/transpose S-sym)))


(deftest t40_l234 (is (true? v39_l232)))


(def v41_l236 (def eig-S (la/eigen S-sym)))


(def
 v43_l240
 (< (el/reduce-max (el/abs (el/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t44_l242 (is (true? v43_l240)))


(def v46_l247 (def Q-eig (t/hstack (:eigenvectors eig-S))))


(def v47_l250 (def QtQ (la/mmul (la/transpose Q-eig) Q-eig)))


(def v48_l252 (la/norm (el/- QtQ (t/eye 3))))


(deftest t49_l254 (is ((fn [d] (< d 1.0E-10)) v48_l252)))


(def v51_l306 (def A-svd (t/matrix [[1 0 1] [0 1 1]])))


(def v52_l310 (def svd-A (la/svd A-svd)))


(def v53_l312 (:S svd-A))


(deftest
 t54_l314
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v53_l312)))


(def v56_l340 (def A-lr (t/matrix [[3 2 2] [2 3 -2]])))


(def v57_l344 (def svd-lr (la/svd A-lr)))


(def v58_l346 (def sigmas (:S svd-lr)))


(def v59_l348 sigmas)


(deftest
 t60_l350
 (is
  ((fn
    [s]
    (and (= 2 (count s)) (every? pos? s) (>= (first s) (second s))))
   v59_l348)))


(def
 v62_l357
 (def
  A-rank1
  (el/scale
   (la/mmul
    (t/submatrix (:U svd-lr) :all [0])
    (t/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v64_l363 (def approx-err (la/norm (el/- A-lr A-rank1))))


(def v65_l365 (< (abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t66_l367 (is (true? v65_l365)))


(def v68_l393 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v69_l395
 (every?
  (fn* [p1__66272#] (>= p1__66272# -1.0E-10))
  (el/re (:eigenvalues (la/eigen ATA)))))


(deftest t70_l397 (is (true? v69_l395)))


(def
 v72_l408
 (def spd-mat (el/+ (la/mmul (la/transpose A-eig) A-eig) (t/eye 3))))


(def v73_l411 (def chol-L (la/cholesky spd-mat)))


(def v74_l413 chol-L)


(deftest
 t75_l415
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
   v74_l413)))


(def
 v77_l425
 (la/norm (el/- (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t78_l427 (is ((fn [d] (< d 1.0E-10)) v77_l425)))


(def v80_l432 (la/cholesky (t/matrix [[1 2] [2 1]])))


(deftest t81_l434 (is (nil? v80_l432)))


(def v83_l446 (def A-final (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v85_l456 (la/close? A-final (la/transpose A-final)))


(deftest t86_l458 (is (true? v85_l456)))


(def v88_l462 (def eig-final (la/eigen A-final)))


(def v89_l464 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v90_l467 final-eigenvalues)


(deftest
 t91_l469
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v90_l467)))


(def
 v93_l477
 (< (abs (- (la/trace A-final) (el/sum final-eigenvalues))) 1.0E-10))


(deftest t94_l481 (is (true? v93_l477)))


(def
 v96_l485
 (< (abs (- (la/det A-final) (el/reduce-* final-eigenvalues))) 1.0E-10))


(deftest t97_l489 (is (true? v96_l485)))


(def v99_l497 (def final-svd (la/svd A-final)))


(def
 v100_l499
 (<
  (el/reduce-max
   (el/abs (el/- (sort (:S final-svd)) final-eigenvalues)))
  1.0E-10))


(deftest t101_l504 (is (true? v100_l499)))


(def v103_l508 (long (el/sum (el/> (:S final-svd) 1.0E-10))))


(deftest t104_l510 (is ((fn [r] (= r 3)) v103_l508)))


(def v105_l513 (def A-inv (la/invert A-final)))


(def v106_l515 A-inv)


(deftest t107_l517 (is ((fn [m] (= [3 3] (t/shape m))) v106_l515)))


(def v108_l520 (la/close? (la/mmul A-final A-inv) (t/eye 3)))


(deftest t109_l522 (is (true? v108_l520)))


(def v111_l529 (def chol-final (la/cholesky A-final)))


(def v112_l531 chol-final)


(def
 v113_l533
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t114_l535 (is (true? v113_l533)))
