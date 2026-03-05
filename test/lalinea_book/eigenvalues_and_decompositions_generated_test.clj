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
    (el/argsort (el/re (:eigenvalues eig-diag)))]
   (t/hstack
    (mapv (fn* [p1__66994#] (nth evecs p1__66994#)) sorted-idx)))))


(def
 v28_l170
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v29_l173 D-result)


(deftest
 t30_l175
 (is
  ((fn
    [d]
    (and
     (< (abs (- (d 0 0) 2.0)) 1.0E-10)
     (< (abs (d 0 1)) 1.0E-10)
     (< (abs (d 1 0)) 1.0E-10)
     (< (abs (- (d 1 1) 3.0)) 1.0E-10)))
   v29_l173)))


(def v32_l191 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v33_l194
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


(def v34_l201 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t35_l203 (is (true? v34_l201)))


(def v37_l224 (def S-sym (t/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v39_l231 (la/close? S-sym (la/transpose S-sym)))


(deftest t40_l233 (is (true? v39_l231)))


(def v41_l235 (def eig-S (la/eigen S-sym)))


(def
 v43_l239
 (< (el/reduce-max (el/abs (el/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t44_l241 (is (true? v43_l239)))


(def v46_l246 (def Q-eig (t/hstack (:eigenvectors eig-S))))


(def v47_l249 (def QtQ (la/mmul (la/transpose Q-eig) Q-eig)))


(def v48_l251 (la/norm (el/- QtQ (t/eye 3))))


(deftest t49_l253 (is ((fn [d] (< d 1.0E-10)) v48_l251)))


(def v51_l305 (def A-svd (t/matrix [[1 0 1] [0 1 1]])))


(def v52_l309 (def svd-A (la/svd A-svd)))


(def v53_l311 (:S svd-A))


(deftest
 t54_l313
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v53_l311)))


(def v56_l339 (def A-lr (t/matrix [[3 2 2] [2 3 -2]])))


(def v57_l343 (def svd-lr (la/svd A-lr)))


(def v58_l345 (def sigmas (:S svd-lr)))


(def v59_l347 sigmas)


(deftest
 t60_l349
 (is
  ((fn
    [s]
    (and (= 2 (count s)) (every? pos? s) (>= (first s) (second s))))
   v59_l347)))


(def
 v62_l356
 (def
  A-rank1
  (el/scale
   (la/mmul
    (t/submatrix (:U svd-lr) :all [0])
    (t/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v64_l362 (def approx-err (la/norm (el/- A-lr A-rank1))))


(def v65_l364 (< (abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t66_l366 (is (true? v65_l364)))


(def v68_l392 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v69_l394
 (every?
  (fn* [p1__66995#] (>= p1__66995# -1.0E-10))
  (el/re (:eigenvalues (la/eigen ATA)))))


(deftest t70_l396 (is (true? v69_l394)))


(def
 v72_l407
 (def spd-mat (el/+ (la/mmul (la/transpose A-eig) A-eig) (t/eye 3))))


(def v73_l410 (def chol-L (la/cholesky spd-mat)))


(def v74_l412 chol-L)


(deftest
 t75_l414
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
   v74_l412)))


(def
 v77_l424
 (la/norm (el/- (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t78_l426 (is ((fn [d] (< d 1.0E-10)) v77_l424)))


(def v80_l431 (la/cholesky (t/matrix [[1 2] [2 1]])))


(deftest t81_l433 (is (nil? v80_l431)))


(def v83_l445 (def A-final (t/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v85_l455 (la/close? A-final (la/transpose A-final)))


(deftest t86_l457 (is (true? v85_l455)))


(def v88_l461 (def eig-final (la/eigen A-final)))


(def v89_l463 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v90_l466 final-eigenvalues)


(deftest
 t91_l468
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v90_l466)))


(def
 v93_l476
 (< (abs (- (la/trace A-final) (el/sum final-eigenvalues))) 1.0E-10))


(deftest t94_l480 (is (true? v93_l476)))


(def
 v96_l484
 (< (abs (- (la/det A-final) (el/reduce-* final-eigenvalues))) 1.0E-10))


(deftest t97_l488 (is (true? v96_l484)))


(def v99_l496 (def final-svd (la/svd A-final)))


(def
 v100_l498
 (<
  (el/reduce-max
   (el/abs (el/- (el/sort (:S final-svd)) final-eigenvalues)))
  1.0E-10))


(deftest t101_l503 (is (true? v100_l498)))


(def v103_l507 (long (el/sum (el/> (:S final-svd) 1.0E-10))))


(deftest t104_l509 (is ((fn [r] (= r 3)) v103_l507)))


(def v105_l512 (def A-inv (la/invert A-final)))


(def v106_l514 A-inv)


(deftest t107_l516 (is ((fn [m] (= [3 3] (t/shape m))) v106_l514)))


(def v108_l519 (la/close? (la/mmul A-final A-inv) (t/eye 3)))


(deftest t109_l521 (is (true? v108_l519)))


(def v111_l528 (def chol-final (la/cholesky A-final)))


(def v112_l530 chol-final)


(def
 v113_l532
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t114_l534 (is (true? v113_l532)))
