(ns
 basis-book.abstract-linear-algebra-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l34 (def u (la/column [3 1])))


(def v4_l35 (def v (la/column [1 2])))


(def v6_l46 (la/add u v))


(deftest
 t7_l48
 (is
  ((fn
    [r]
    (and (= 4.0 (tensor/mget r 0 0)) (= 3.0 (tensor/mget r 1 0))))
   v6_l46)))


(def v9_l57 (la/scale 2.0 u))


(deftest
 t10_l59
 (is
  ((fn
    [r]
    (and (= 6.0 (tensor/mget r 0 0)) (= 2.0 (tensor/mget r 1 0))))
   v9_l57)))


(def v12_l65 (la/scale -1.0 u))


(deftest
 t13_l67
 (is
  ((fn
    [r]
    (and (= -3.0 (tensor/mget r 0 0)) (= -1.0 (tensor/mget r 1 0))))
   v12_l65)))


(def v15_l97 (def w-ax (la/column [-1 4])))


(def v16_l98 (def zero2 (la/column [0 0])))


(def
 v18_l102
 (def close? (fn [a b] (< (la/norm (la/sub a b)) 1.0E-10))))


(def v20_l107 (close? (la/add u v) (la/add v u)))


(deftest t21_l109 (is (true? v20_l107)))


(def
 v23_l113
 (close? (la/add (la/add u v) w-ax) (la/add u (la/add v w-ax))))


(deftest t24_l116 (is (true? v23_l113)))


(def v26_l120 (close? (la/add u zero2) u))


(deftest t27_l122 (is (true? v26_l120)))


(def v29_l126 (close? (la/add u (la/scale -1.0 u)) zero2))


(deftest t30_l128 (is (true? v29_l126)))


(def v32_l132 (close? (la/scale 2.0 (la/scale 3.0 u)) (la/scale 6.0 u)))


(deftest t33_l135 (is (true? v32_l132)))


(def v35_l139 (close? (la/scale 1.0 u) u))


(deftest t36_l141 (is (true? v35_l139)))


(def
 v38_l145
 (close?
  (la/scale 5.0 (la/add u v))
  (la/add (la/scale 5.0 u) (la/scale 5.0 v))))


(deftest t39_l148 (is (true? v38_l145)))


(def
 v41_l152
 (close?
  (la/scale (+ 2.0 3.0) u)
  (la/add (la/scale 2.0 u) (la/scale 3.0 u))))


(deftest t42_l155 (is (true? v41_l152)))


(def v44_l180 (la/add (la/scale 2.0 u) (la/scale -1.0 v)))


(deftest
 t45_l182
 (is
  ((fn
    [r]
    (and (= 5.0 (tensor/mget r 0 0)) (= 0.0 (tensor/mget r 1 0))))
   v44_l180)))


(def
 v47_l199
 (let
  [params
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] {:a a, :b b})
   xs
   (mapv (fn [{:keys [a b]}] (+ (* a 3.0) (* b 1.0))) params)
   ys
   (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def
 v49_l215
 (let
  [params
   (for [a (range -2.0 2.1 0.5) b (range -2.0 2.1 0.5)] {:a a, :b b})
   xs
   (mapv (fn [{:keys [a b]}] (+ (* a 1.0) (* b 2.0))) params)
   ys
   (mapv (fn [{:keys [a b]}] (+ (* a 2.0) (* b 4.0))) params)]
  (->
   (tc/dataset {:x xs, :y ys})
   (plotly/base {:=x :x, :=y :y})
   (plotly/layer-point {:=mark-size 6})
   plotly/plot)))


(def v51_l252 (la/det (la/matrix [[3 1] [1 2]])))


(deftest t52_l255 (is ((fn [d] (> (Math/abs d) 1.0E-10)) v51_l252)))


(def v54_l260 (la/det (la/matrix [[3 6] [1 2]])))


(deftest t55_l263 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v54_l260)))


(def v57_l269 (la/det (la/matrix [[1 0 0] [0 1 0] [0 0 1]])))


(deftest
 t58_l273
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v57_l269)))


(def v60_l279 (la/det (la/matrix [[1 0 1] [0 1 1] [0 0 0]])))


(deftest t61_l283 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v60_l279)))


(def v63_l303 (def e1 (la/column [1 0 0])))


(def v64_l304 (def e2 (la/column [0 1 0])))


(def v65_l305 (def e3 (la/column [0 0 1])))


(def v67_l315 (def w (la/column [5 -3 7])))


(def
 v69_l319
 (close?
  w
  (la/add
   (la/scale 5.0 e1)
   (la/add (la/scale -3.0 e2) (la/scale 7.0 e3)))))


(deftest t70_l324 (is (true? v69_l319)))


(def v72_l368 (def R90 (la/matrix [[0 -1] [1 0]])))


(def v74_l374 (la/mmul R90 (la/column [1 0])))


(deftest
 t75_l376
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (tensor/mget r 0 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget r 1 0) 1.0)) 1.0E-10)))
   v74_l374)))


(def v77_l382 (la/mmul R90 (la/column [0 1])))


(deftest
 t78_l384
 (is
  ((fn
    [r]
    (and
     (< (Math/abs (- (tensor/mget r 0 0) -1.0)) 1.0E-10)
     (< (Math/abs (tensor/mget r 1 0)) 1.0E-10)))
   v77_l382)))


(def
 v80_l392
 (close?
  (la/mmul R90 (la/add u v))
  (la/add (la/mmul R90 u) (la/mmul R90 v))))


(deftest t81_l395 (is (true? v80_l392)))


(def
 v83_l399
 (close? (la/mmul R90 (la/scale 3.0 u)) (la/scale 3.0 (la/mmul R90 u))))


(deftest t84_l402 (is (true? v83_l399)))


(def v86_l406 (def stretch-mat (la/matrix [[3 0] [0 1]])))


(def
 v88_l413
 (let
  [angles
   (mapv
    (fn* [p1__74405#] (* 2.0 Math/PI (/ p1__74405# 40.0)))
    (range 41))
   circle-x
   (mapv (fn* [p1__74406#] (Math/cos p1__74406#)) angles)
   circle-y
   (mapv (fn* [p1__74407#] (Math/sin p1__74407#)) angles)
   stretched
   (mapv
    (fn
     [cx cy]
     (let
      [out (la/mmul stretch-mat (la/column [cx cy]))]
      [(tensor/mget out 0 0) (tensor/mget out 1 0)]))
    circle-x
    circle-y)]
  (->
   (tc/dataset
    {:x (mapv first stretched),
     :y (mapv second stretched),
     :shape (repeat 41 "stretched")})
   (tc/concat
    (tc/dataset
     {:x circle-x, :y circle-y, :shape (repeat 41 "original")}))
   (plotly/base {:=x :x, :=y :y, :=color :shape})
   (plotly/layer-line)
   plotly/plot)))


(def v90_l438 (def proj-xy (la/matrix [[1 0 0] [0 1 0] [0 0 0]])))


(def v92_l445 (la/mmul proj-xy (la/column [5 3 7])))


(deftest
 t93_l447
 (is
  ((fn
    [r]
    (and
     (= 5.0 (tensor/mget r 0 0))
     (= 3.0 (tensor/mget r 1 0))
     (= 0.0 (tensor/mget r 2 0))))
   v92_l445)))


(def v95_l455 (la/det proj-xy))


(deftest t96_l457 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v95_l455)))


(def v98_l465 (def shear-mat (la/matrix [[1 2] [0 1]])))


(def v99_l469 (la/det shear-mat))


(deftest
 t100_l471
 (is ((fn [d] (< (Math/abs (- d 1.0)) 1.0E-10)) v99_l469)))


(def v102_l482 (def AB (la/mmul stretch-mat R90)))


(def v103_l483 (def BA (la/mmul R90 stretch-mat)))


(def v104_l485 (la/norm (la/sub AB BA)))


(deftest t105_l487 (is ((fn [d] (> d 0.1)) v104_l485)))


(def v107_l530 (def M (la/matrix [[1 2 3] [4 5 9] [7 8 15]])))


(def v109_l538 (la/mmul M (la/column [1 1 -1])))


(deftest t110_l540 (is ((fn [r] (< (la/norm r) 1.0E-10)) v109_l538)))


(def v112_l551 (la/mmul M (la/scale 7.0 (la/column [1 1 -1]))))


(deftest t113_l553 (is ((fn [r] (< (la/norm r) 1.0E-10)) v112_l551)))


(def v115_l568 (def sv-M (vec (:S (la/svd M)))))


(def v116_l570 sv-M)


(deftest t117_l572 (is ((fn [v] (= 3 (count v))) v116_l570)))


(def
 v118_l575
 (def
  rank-M
  (count (filter (fn* [p1__74408#] (> p1__74408# 1.0E-10)) sv-M))))


(def v119_l577 rank-M)


(deftest t120_l579 (is ((fn [r] (= r 2)) v119_l577)))


(def v122_l601 (def svd-M (la/svd M)))


(def
 v123_l603
 (def
  null-basis
  (let
   [sv
    (vec (:S svd-M))
    Vt
    (:Vt svd-M)
    null-idx
    (vec (keep-indexed (fn [i s] (when (< s 1.0E-10) i)) sv))]
   (la/submatrix (la/transpose Vt) :all null-idx))))


(def v125_l611 (la/norm (la/mmul M null-basis)))


(deftest t126_l613 (is ((fn [d] (< d 1.0E-10)) v125_l611)))


(def v128_l627 (def A-full (la/matrix [[2 1] [1 3]])))


(def
 v129_l629
 (count
  (filter
   (fn* [p1__74409#] (> p1__74409# 1.0E-10))
   (vec (:S (la/svd A-full))))))


(deftest t130_l631 (is ((fn [r] (= r 2)) v129_l629)))


(def v132_l636 (la/solve A-full (la/column [5 7])))


(deftest t133_l638 (is ((fn [x] (some? x)) v132_l636)))


(def v135_l643 (la/solve M (la/column [1 2 3])))


(deftest t136_l645 (is (nil? v135_l643)))


(def
 v138_l669
 (def
  row-space-basis
  (let
   [sv
    (vec (:S svd-M))
    Vt
    (:Vt svd-M)
    row-idx
    (vec (keep-indexed (fn [i s] (when (> s 1.0E-10) i)) sv))]
   (la/submatrix (la/transpose Vt) :all row-idx))))


(def v140_l677 (la/mmul (la/transpose row-space-basis) null-basis))


(deftest t141_l679 (is ((fn [r] (< (la/norm r) 1.0E-10)) v140_l677)))


(def v143_l697 (def a3 (la/column [1 2 3])))


(def v144_l698 (def b3 (la/column [4 5 6])))


(def v145_l700 (def dot-ab (dfn/sum (dfn/* a3 b3))))


(def v146_l703 dot-ab)


(deftest
 t147_l705
 (is ((fn [d] (< (Math/abs (- d 32.0)) 1.0E-10)) v146_l703)))


(def v149_l714 (la/norm a3))


(deftest
 t150_l716
 (is
  ((fn [d] (< (Math/abs (- d (Math/sqrt 14.0))) 1.0E-10)) v149_l714)))


(def v152_l722 (dfn/sum (dfn/* (la/column [1 0]) (la/column [0 1]))))


(deftest t153_l724 (is ((fn [d] (< (Math/abs d) 1.0E-10)) v152_l722)))


(def v155_l743 (def W-proj (la/matrix [[1 0] [0 1] [1 1]])))


(def
 v157_l751
 (def
  P-proj
  (la/mmul
   W-proj
   (la/mmul
    (la/invert (la/mmul (la/transpose W-proj) W-proj))
    (la/transpose W-proj)))))


(def v159_l759 (close? (la/mmul P-proj P-proj) P-proj))


(deftest t160_l761 (is (true? v159_l759)))


(def v162_l765 (def point3d (la/column [1 2 3])))


(def v163_l767 (def projected-pt (la/mmul P-proj point3d)))


(def v164_l769 projected-pt)


(def v166_l775 (def resid (la/sub point3d projected-pt)))


(def v167_l777 (la/mmul (la/transpose W-proj) resid))


(deftest t168_l779 (is ((fn [r] (< (la/norm r) 1.0E-10)) v167_l777)))


(def v170_l802 (def a-gs (la/column [1 1 0])))


(def v171_l803 (def b-gs (la/column [1 0 1])))


(def v173_l807 (def q1-gs (la/scale (/ 1.0 (la/norm a-gs)) a-gs)))


(def v174_l809 q1-gs)


(def v176_l813 (def proj-b-on-q1 (dfn/sum (dfn/* q1-gs b-gs))))


(def
 v177_l816
 (def orthogonal-part (la/sub b-gs (la/scale proj-b-on-q1 q1-gs))))


(def
 v179_l821
 (def
  q2-gs
  (la/scale (/ 1.0 (la/norm orthogonal-part)) orthogonal-part)))


(def v180_l824 q2-gs)


(def
 v182_l828
 {:q1-norm (la/norm q1-gs),
  :q2-norm (la/norm q2-gs),
  :dot (dfn/sum (dfn/* q1-gs q2-gs))})


(deftest
 t183_l832
 (is
  ((fn
    [m]
    (and
     (< (Math/abs (- (:q1-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (- (:q2-norm m) 1.0)) 1.0E-10)
     (< (Math/abs (:dot m)) 1.0E-10)))
   v182_l828)))


(def v185_l848 (def A-qr (la/matrix [[1 1] [1 0] [0 1]])))


(def v186_l852 (def qr-result (la/qr A-qr)))


(def v188_l856 (def ncols-qr (second (dtype/shape A-qr))))


(def
 v189_l857
 (def Q-thin (la/submatrix (:Q qr-result) :all (range ncols-qr))))


(def
 v190_l858
 (def R-thin (la/submatrix (:R qr-result) (range ncols-qr) :all)))


(def
 v192_l862
 (la/norm (la/sub (la/mmul (la/transpose Q-thin) Q-thin) (la/eye 2))))


(deftest t193_l864 (is ((fn [d] (< d 1.0E-10)) v192_l862)))


(def v195_l869 (la/norm (la/sub (la/mmul Q-thin R-thin) A-qr)))


(deftest t196_l871 (is ((fn [d] (< d 1.0E-10)) v195_l869)))


(def v198_l894 (def A-eig (la/matrix [[4 1 2] [0 3 1] [0 0 2]])))


(def v200_l902 (def eig-result (la/eigen A-eig)))


(def v201_l904 (sort (mapv first (:eigenvalues eig-result))))


(deftest
 t202_l906
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (nth v 0) 2.0)) 1.0E-10)
     (< (Math/abs (- (nth v 1) 3.0)) 1.0E-10)
     (< (Math/abs (- (nth v 2) 4.0)) 1.0E-10)))
   v201_l904)))


(def
 v204_l917
 (every?
  (fn
   [i]
   (let
    [lam
     (first (nth (:eigenvalues eig-result) i))
     ev
     (nth (:eigenvectors eig-result) i)]
    (<
     (la/norm (la/sub (la/mmul A-eig ev) (la/scale lam ev)))
     1.0E-10)))
  (range 3)))


(deftest t205_l925 (is (true? v204_l917)))


(def v207_l938 (def eig-reals (mapv first (:eigenvalues eig-result))))


(def
 v208_l940
 (< (Math/abs (- (la/trace A-eig) (reduce + eig-reals))) 1.0E-10))


(deftest t209_l942 (is (true? v208_l940)))


(def
 v210_l944
 (< (Math/abs (- (la/det A-eig) (reduce * eig-reals))) 1.0E-10))


(deftest t211_l946 (is (true? v210_l944)))


(def v213_l975 (def A-diag (la/matrix [[2 1] [0 3]])))


(def v215_l981 (def eig-diag (la/eigen A-diag)))


(def
 v216_l983
 (def
  P-diag
  (let
   [evecs
    (:eigenvectors eig-diag)
    sorted-idx
    (sort-by
     (fn [i] (first (nth (:eigenvalues eig-diag) i)))
     (range 2))]
   (la/matrix
    (mapv
     (fn [j] (vec (dtype/->reader (nth evecs (nth sorted-idx j)))))
     (range 2))))))


(def v218_l994 (def P-cols (la/transpose P-diag)))


(def
 v220_l998
 (def D-result (la/mmul (la/invert P-cols) (la/mmul A-diag P-cols))))


(def v221_l1001 D-result)


(deftest
 t222_l1003
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (- (tensor/mget d 0 0) 2.0)) 1.0E-10)
     (< (Math/abs (tensor/mget d 0 1)) 1.0E-10)
     (< (Math/abs (tensor/mget d 1 0)) 1.0E-10)
     (< (Math/abs (- (tensor/mget d 1 1) 3.0)) 1.0E-10)))
   v221_l1001)))


(def v224_l1019 (def A-diag-sq (la/mmul A-diag A-diag)))


(def
 v225_l1022
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


(def v226_l1029 (close? A-diag-sq A-diag-sq-via-eigen))


(deftest t227_l1031 (is (true? v226_l1029)))


(def v229_l1052 (def S-sym (la/matrix [[4 2 0] [2 5 1] [0 1 3]])))


(def v231_l1059 (close? S-sym (la/transpose S-sym)))


(deftest t232_l1061 (is (true? v231_l1059)))


(def v233_l1063 (def eig-S (la/eigen S-sym)))


(def
 v235_l1067
 (every? (fn [[_ im]] (< (Math/abs im) 1.0E-10)) (:eigenvalues eig-S)))


(deftest t236_l1070 (is (true? v235_l1067)))


(def
 v238_l1075
 (def
  Q-eig
  (let
   [evecs (:eigenvectors eig-S)]
   (la/matrix
    (mapv (fn [i] (vec (dtype/->reader (nth evecs i)))) (range 3))))))


(def v239_l1080 (def QtQ (la/mmul Q-eig (la/transpose Q-eig))))


(def v240_l1082 (la/norm (la/sub QtQ (la/eye 3))))


(deftest t241_l1084 (is ((fn [d] (< d 1.0E-10)) v240_l1082)))


(def v243_l1123 (def A-svd (la/matrix [[1 0 1] [0 1 1]])))


(def v244_l1127 (def svd-A (la/svd A-svd)))


(def v245_l1129 (vec (:S svd-A)))


(deftest
 t246_l1131
 (is ((fn [s] (and (= 2 (count s)) (every? pos? s))) v245_l1129)))


(def v248_l1157 (def A-lr (la/matrix [[3 2 2] [2 3 -2]])))


(def v249_l1161 (def svd-lr (la/svd A-lr)))


(def v250_l1163 (def sigmas (vec (:S svd-lr))))


(def v251_l1165 sigmas)


(def
 v253_l1169
 (def
  A-rank1
  (la/scale
   (first sigmas)
   (la/mmul
    (la/submatrix (:U svd-lr) :all [0])
    (la/submatrix (:Vt svd-lr) [0] :all)))))


(def v255_l1176 (def approx-err (la/norm (la/sub A-lr A-rank1))))


(def v256_l1178 (< (Math/abs (- approx-err (second sigmas))) 1.0E-10))


(deftest t257_l1180 (is (true? v256_l1178)))


(def v259_l1206 (def ATA (la/mmul (la/transpose A-svd) A-svd)))


(def
 v260_l1208
 (every? (fn [[re _]] (>= re -1.0E-10)) (:eigenvalues (la/eigen ATA))))


(deftest t261_l1211 (is (true? v260_l1208)))


(def
 v263_l1222
 (def spd-mat (la/add (la/mmul (la/transpose A-eig) A-eig) (la/eye 3))))


(def v264_l1225 (def chol-L (la/cholesky spd-mat)))


(def
 v266_l1229
 (la/norm (la/sub (la/mmul chol-L (la/transpose chol-L)) spd-mat)))


(deftest t267_l1231 (is ((fn [d] (< d 1.0E-10)) v266_l1229)))


(def v269_l1236 (la/cholesky (la/matrix [[1 2] [2 1]])))


(deftest t270_l1238 (is (nil? v269_l1236)))


(def v272_l1250 (def A-final (la/matrix [[2 1 0] [1 3 1] [0 1 2]])))


(def v274_l1260 (close? A-final (la/transpose A-final)))


(deftest t275_l1262 (is (true? v274_l1260)))


(def v277_l1266 (def eig-final (la/eigen A-final)))


(def
 v278_l1268
 (def final-eigenvalues (sort (mapv first (:eigenvalues eig-final)))))


(def v279_l1271 final-eigenvalues)


(deftest
 t280_l1273
 (is ((fn [v] (and (= 3 (count v)) (every? pos? v))) v279_l1271)))


(def
 v282_l1281
 (<
  (Math/abs (- (la/trace A-final) (reduce + final-eigenvalues)))
  1.0E-10))


(deftest t283_l1285 (is (true? v282_l1281)))


(def
 v285_l1289
 (<
  (Math/abs (- (la/det A-final) (reduce * final-eigenvalues)))
  1.0E-10))


(deftest t286_l1293 (is (true? v285_l1289)))


(def v288_l1297 (def final-svd (la/svd A-final)))


(def
 v289_l1299
 (<
  (dfn/reduce-max
   (dfn/abs
    (dfn/-
     (double-array (sort (vec (:S final-svd))))
     (double-array final-eigenvalues))))
  1.0E-10))


(deftest t290_l1304 (is (true? v289_l1299)))


(def
 v292_l1308
 (count
  (filter
   (fn* [p1__74410#] (> p1__74410# 1.0E-10))
   (vec (:S final-svd)))))


(deftest t293_l1310 (is ((fn [r] (= r 3)) v292_l1308)))


(def v294_l1313 (def A-inv (la/invert A-final)))


(def v295_l1315 (close? (la/mmul A-final A-inv) (la/eye 3)))


(deftest t296_l1317 (is (true? v295_l1315)))


(def v298_l1321 (def chol-final (la/cholesky A-final)))


(def
 v299_l1323
 (close? (la/mmul chol-final (la/transpose chol-final)) A-final))


(deftest t300_l1325 (is (true? v299_l1323)))
