(ns
 lalinea-book.eigenvalues-and-decompositions-generated-test
 (:require
  [scicloj.lalinea.linalg :as la]
  [scicloj.lalinea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [scicloj.lalinea.vis :as vis]
  [clojure.test :refer [deftest is]]))


(def
 v3_l48
 (vis/arrow-plot
  [{:label "v₁", :xy [1 0], :color "#2266cc"}
   {:label "Av₁=2v₁", :xy [2 0], :color "#2266cc", :dashed? true}
   {:label "v₂", :xy [1 1], :color "#cc4422"}
   {:label "Av₂=3v₂", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def
 v5_l57
 (vis/arrow-plot
  [{:label "u", :xy [0 1], :color "#999999"}
   {:label "Au", :xy [1 3], :color "#228833", :dashed? true}
   {:label "v2", :xy [1 1], :color "#cc4422"}
   {:label "Av2", :xy [3 3], :color "#cc4422", :dashed? true}]
  {}))


(def v7_l65 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v9_l73 (def eig-result (la/eigen A-eig)))


(def v11_l83 (la/real-eigenvalues A-eig))


(deftest
 t12_l85
 (is
  ((fn
    [v]
    (and
     (< (abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (abs (- (nth v 2) 4.0)) 1.0E-10)))
   v11_l83)))


(def
 v14_l96
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


(deftest t15_l104 (is (true? v14_l96)))


(def v17_l117 (def eig-reals (cx/re (:eigenvalues eig-result))))


(def
 v18_l119
 (< (abs (- (la/trace A-eig) (dfn/sum eig-reals))) 1.0E-10))


(deftest t19_l121 (is (true? v18_l119)))


(def v20_l123 (< (abs (- (la/det A-eig) (reduce * eig-reals))) 1.0E-10))


(deftest t21_l125 (is (true? v20_l123)))


(def v23_l154 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v25_l160 (def eig-diag (la/eigen A-diag)))


(def
 v26_l162
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


(def v28_l173 (def P-cols (la/transpose P-diag)))


(def
 v30_l177
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v31_l180 D-result)


(deftest
 t32_l182
 (is
  ((fn
    [d]
    (and
     (< (abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (abs (tensor/mget d 0 1)) 1.0E-10)
     (< (abs (tensor/mget d 1 0)) 1.0E-10)
     (< (abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v31_l180)))


(def v34_l198 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v35_l201
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


(def v36_l208 (la/close? A-diag-sq A-diag-sq-via-eigen))


(deftest t37_l210 (is (true? v36_l208)))


(def v39_l231 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v41_l238 (la/close? S-sym (la/transpose S-sym)))


(deftest t42_l240 (is (true? v41_l238)))


(def v43_l242 (def eig-S (la/eigen S-sym)))


(def
 v45_l246
 (< (dfn/reduce-max (dfn/abs (cx/im (:eigenvalues eig-S)))) 1.0E-10))


(deftest t46_l248 (is (true? v45_l246)))


(def
 v48_l253
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v49_l258 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v50_l260 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t51_l262 (is ((fn [d] (< d 1.0E-10)) v50_l260)))


(def v53_l314 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v54_l318 (def svd-A (la/svd A-svd)))


(def v55_l320 (:S svd-A))


(deftest
 t56_l322
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v55_l320)))


(def v58_l348 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v59_l352 (def svd-lr (la/svd A-lr)))


(def v60_l354 (def sigmas (:S svd-lr)))


(def v61_l356 sigmas)


(deftest
 t62_l358
 (is
  ((fn
    [s]
    (and (= 2 (count s)) (every? pos? s) (>= (first s) (second s))))
   v61_l356)))


(def
 v64_l365
 (def
  A-rank1
  (la/scale
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all))
   (first sigmas))))


(def v66_l371 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v67_l373 (< (abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t68_l375 (is (true? v67_l373)))


(def v70_l401 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v71_l403
 (every?
  (fn* [p1__73691#] (>= p1__73691# -1.0E-10))
  (cx/re (:eigenvalues (la/eigen ATA)))))


(deftest t72_l405 (is (true? v71_l403)))


(def
 v74_l416
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v75_l419 (def chol-L (la/cholesky spd-mat)))


(def v76_l421 chol-L)


(deftest
 t77_l423
 (is
  ((fn
    [L]
    (let
     [[r c] (dtype/shape L)]
     (and
      (= r c)
      (every?
       (fn
        [i]
        (every?
         (fn [j] (< (abs (tensor/mget L i j)) 1.0E-10))
         (range (inc i) c)))
       (range r)))))
   v76_l421)))


(def
 v79_l433
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t80_l435 (is ((fn [d] (< d 1.0E-10)) v79_l433)))


(def v82_l440 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t83_l442 (is (nil? v82_l440)))


(def v85_l454 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v87_l464 (la/close? A-final (la/transpose A-final)))


(deftest t88_l466 (is (true? v87_l464)))


(def v90_l470 (def eig-final (la/eigen A-final)))


(def v91_l472 (def final-eigenvalues (la/real-eigenvalues A-final)))


(def v92_l475 final-eigenvalues)


(deftest
 t93_l477
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v92_l475)))


(def
 v95_l485
 (< (abs (- (la/trace A-final) (dfn/sum final-eigenvalues))) 1.0E-10))


(deftest t96_l489 (is (true? v95_l485)))


(def
 v98_l493
 (< (abs (- (la/det A-final) (reduce * final-eigenvalues))) 1.0E-10))


(deftest t99_l497 (is (true? v98_l493)))


(def v101_l505 (def final-svd (la/svd A-final)))


(def
 v102_l507
 (<
  (dfn/reduce-max
   (dfn/abs (dfn/- (sort (:S final-svd)) final-eigenvalues)))
  1.0E-10))


(deftest t103_l512 (is (true? v102_l507)))


(def v105_l516 (long (dfn/sum (dfn/> (:S final-svd) 1.0E-10))))


(deftest t106_l518 (is ((fn [r] (= r 3)) v105_l516)))


(def v107_l521 (def A-inv (la/invert A-final)))


(def v108_l523 A-inv)


(deftest t109_l525 (is ((fn [m] (= [3 3] (dtype/shape m))) v108_l523)))


(def v110_l528 (la/close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t111_l530 (is (true? v110_l528)))


(def v113_l537 (def chol-final (la/cholesky A-final)))


(def v114_l539 chol-final)


(def
 v115_l541
 (la/close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t116_l543 (is (true? v115_l541)))
