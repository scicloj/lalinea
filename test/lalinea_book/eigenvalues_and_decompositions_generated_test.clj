(ns
 lalinea-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.tensor :as t]
  [scicloj.lalinea.elementwise :as elem]
  [scicloj.lalinea.complex :as cx]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def
 v3_l44
 (vis/arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def
 v5_l53
 (vis/arrow-plot
  [{:label "u", :xy [0 1], :color "#999999"}
   {:label "Au", :xy [1 3], :color "#228833", :dashed? true}
   {:label "v2", :xy [1 1], :color "#cc4422"}
   {:label "Av2", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v7_l61 (def A-eig (t/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v9_l69 (def eig-result (la/eigen A-eig)))


(def v11_l79 (la/real-eigenvalues A-eig))


(deftest
 t12_l81
 (is
  ((fn
    [v]
    (and
     (< (abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (abs (- (nth v 2) 4.0)) 1.0E-10)))
   v11_l79)))


(def
 v14_l92
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


(deftest t15_l100 (is (true? v14_l92)))


(def v17_l113 (def eig-reals (cx/re (:eigenvalues eig-result))))


(def v18_l115 (< (abs (- (la/trace A-eig) (la/sum eig-reals))) 1.0E-10))


(deftest t19_l117 (is (true? v18_l115)))


(def v20_l119 (< (abs (- (la/det A-eig) (la/prod eig-reals))) 1.0E-10))


(deftest t21_l121 (is (true? v20_l119)))


(def v23_l150 (def A-diag (t/matrix [[2 1] [0 3]])))


(def v25_l156 (def eig-diag (la/eigen A-diag)))


(def
 v26_l158
 (def
  P-cols
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by (fn [i] (cx/re ((:eigenvalues eig-diag) i))) (range 2))]
   (t/hstack
    (mapv (fn* [p1__74626#] (nth evecs p1__74626#)) sorted-idx)))))


(def
 v28_l166
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v29_l169 D-result)


(deftest
 t30_l171
 (is
  ((fn
    [d]
    (and
     (< (abs (- (d 0 0) 2.0)) 1.0E-10)
     (< (abs (d 0 1)) 1.0E-10)
     (< (abs (d 1 0)) 1.0E-10)
     (< (abs (- (d 1 1) 3.0)) 1.0E-10)))
   v29_l169)))


(def v32_l187 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v33_l190
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


(def v34_l197 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t35_l199 (is (true? v34_l197)))


(def v37_l220 (def S-sym (t/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v39_l227 (la/close? S-sym (la/transpose S-sym)))


(deftest t40_l229 (is (true? v39_l227)))


(def v41_l231 (def eig-S (la/eigen S-sym)))


(def
 v43_l235
 (< (elem/reduce-max (elem/abs (cx/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t44_l237 (is (true? v43_l235)))


(def v46_l242 (def Q-eig (t/hstack (:eigenvectors eig-S))))


(def v47_l245 (def QtQ (la/mmul (la/transpose Q-eig) Q-eig)))


(def v48_l247 (la/norm (la/sub QtQ (t/eye 3))))


(deftest t49_l249 (is ((fn [d] (< d 1.0E-10)) v48_l247)))


(def v51_l301 (def A-svd (t/matrix [[1 0 1] [0 1 1]])))


(def v52_l305 (def svd-A (la/svd A-svd)))


(def v53_l307 (:S svd-A))


(deftest
 t54_l309
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v53_l307)))


(def v56_l335 (def A-lr (t/matrix [[3 2 2] [2 3 -2]])))


(def v57_l339 (def svd-lr (la/svd A-lr)))


(def v58_l341 (def sigmas (:S svd-lr)))


(def v59_l343 sigmas)


(deftest
 t60_l345
 (is
  ((fn
    [s]
    (and (= 2 (count s)) (every? pos? s) (>= (first s) (second s))))
   v59_l343)))


(def
 v62_l352
 (def
  A-rank1
  (la/scale
   (la/mmul
    (t/submatrix (:U svd-lr) :all [0])
    (t/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v64_l358 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v65_l360 (< (abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t66_l362 (is (true? v65_l360)))


(def v68_l388 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v69_l390
 (every?
  (fn* [p1__74627#] (>= p1__74627# -1.0E-10))
  (cx/re (:eigenvalues (la/eigen ATA)))))


(deftest t70_l392 (is (true? v69_l390)))


(def
 v72_l403
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (t/eye 3))))


(def v73_l406 (def chol-L (la/cholesky spd-mat)))


(def v74_l408 chol-L)


(deftest
 t75_l410
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
   v74_l408)))


(def
 v77_l420
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t78_l422 (is ((fn [d] (< d 1.0E-10)) v77_l420)))


(def v80_l427 (la/cholesky (t/matrix [[1 2] [2 1]])))


(deftest t81_l429 (is (nil? v80_l427)))


(def v83_l441 (def A-final (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v85_l451 (la/close? A-final (la/transpose A-final)))


(deftest t86_l453 (is (true? v85_l451)))


(def v88_l457 (def eig-final (la/eigen A-final)))


(def v89_l459 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v90_l462 final-eigenvalues)


(deftest
 t91_l464
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v90_l462)))


(def
 v93_l472
 (< (abs (- (la/trace A-final) (la/sum final-eigenvalues))) 1.0E-10))


(deftest t94_l476 (is (true? v93_l472)))


(def
 v96_l480
 (< (abs (- (la/det A-final) (la/prod final-eigenvalues))) 1.0E-10))


(deftest t97_l484 (is (true? v96_l480)))


(def v99_l492 (def final-svd (la/svd A-final)))


(def
 v100_l494
 (<
  (elem/reduce-max
   (elem/abs (la/sub (sort (:S final-svd)) final-eigenvalues)))
  1.0E-10))


(deftest t101_l499 (is (true? v100_l494)))


(def v103_l503 (long (la/sum (elem/gt (:S final-svd) 1.0E-10))))


(deftest t104_l505 (is ((fn [r] (= r 3)) v103_l503)))


(def v105_l508 (def A-inv (la/invert A-final)))


(def v106_l510 A-inv)


(deftest t107_l512 (is ((fn [m] (= [3 3] (t/shape m))) v106_l510)))


(def v108_l515 (la/close? (la/mmul A-final A-inv) (t/eye 3)))


(deftest t109_l517 (is (true? v108_l515)))


(def v111_l524 (def chol-final (la/cholesky A-final)))


(def v112_l526 chol-final)


(def
 v113_l528
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t114_l530 (is (true? v113_l528)))
