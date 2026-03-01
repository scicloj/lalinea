(ns
 la-linea-book.fastmath-implementations-generated-test
 (:require
  [scicloj.la-linea.linalg :as la]
  [scicloj.la-linea.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [fastmath.ml.regression :as reg]
  [fastmath.interpolation.gp :as gp]
  [fastmath.interpolation.rbf :as rbf]
  [fastmath.interpolation.kriging :as kriging]
  [fastmath.distance :as dist]
  [fastmath.ml.regression.contrast :as contrast]
  [fastmath.signal :as signal]
  [fastmath.calculus.finite :as finite]
  [fastmath.calculus.quadrature :as quad]
  [fastmath.polynomials :as poly]
  [fastmath.kernel :as k]
  [fastmath.random :as frand]
  [fastmath.core :as m]
  [tablecloth.api :as tc]
  [scicloj.tableplot.v1.plotly :as plotly]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def
 v3_l48
 (def
  bench-fn
  (fn
   [f n]
   (dotimes [_ 5] (f))
   (let
    [start (System/nanoTime)]
    (dotimes [_ n] (f))
    (/ (- (System/nanoTime) start) (* n 1000000.0))))))


(def v5_l67 (def rng (frand/rng :mersenne 42)))


(def v6_l69 (def n-obs 200))


(def
 v7_l71
 (def
  ols-xs
  (mapv
   (fn [_] [(frand/drandom rng 0.0 10.0) (frand/drandom rng 0.0 5.0)])
   (range n-obs))))


(def v8_l76 (def true-beta [3.0 -1.5]))


(def
 v9_l78
 (def
  ols-ys
  (mapv
   (fn
    [[x1 x2]]
    (+ 2.0 (* 3.0 x1) (* -1.5 x2) (frand/grandom rng 0.0 0.5)))
   ols-xs)))


(def v11_l88 (def fm-model (reg/lm ols-ys ols-xs)))


(def v12_l90 (def fm-intercept (:intercept fm-model)))


(def v13_l91 (def fm-beta (:beta fm-model)))


(def v14_l93 [fm-intercept fm-beta])


(def
 v16_l100
 (def
  la-ols
  (fn
   [xs ys]
   (let
    [n
     (count ys)
     p
     (count (first xs))
     pp1
     (inc p)
     flat
     (->>
      xs
      (mapcat (fn [row] (cons 1.0 row)))
      (dtype/make-container :float64))
     X
     (tensor/reshape flat [n pp1])
     y
     (la/column ys)
     {:keys [U S Vt]}
     (la/svd X)
     S-inv
     (la/diag (dfn// 1.0 S))
     Ut-thin
     (la/submatrix U :all (range pp1))
     beta
     (la/mmul
      (la/mmul (la/transpose Vt) S-inv)
      (la/mmul (la/transpose Ut-thin) y))]
    {:intercept (tensor/mget beta 0 0),
     :beta (mapv (fn [i] (tensor/mget beta (inc i) 0)) (range p))}))))


(def v17_l122 (def la-result (la-ols ols-xs ols-ys)))


(def v18_l124 la-result)


(def
 v20_l131
 (def
  la-ols-normal
  (fn
   [xs ys]
   (let
    [n
     (count ys)
     p
     (count (first xs))
     pp1
     (inc p)
     flat
     (->>
      xs
      (mapcat (fn [row] (cons 1.0 row)))
      (dtype/make-container :float64))
     X
     (tensor/reshape flat [n pp1])
     y
     (la/column ys)
     Xt
     (la/transpose X)
     beta
     (la/mmul (la/invert (la/mmul Xt X)) (la/mmul Xt y))]
    {:intercept (tensor/mget beta 0 0),
     :beta (mapv (fn [i] (tensor/mget beta (inc i) 0)) (range p))}))))


(def v21_l148 (def la-normal-result (la-ols-normal ols-xs ols-ys)))


(def v22_l150 la-normal-result)


(def
 v24_l154
 (let
  [fm-coeffs
   (cons fm-intercept fm-beta)
   la-svd-coeffs
   (cons (:intercept la-result) (:beta la-result))]
  (every?
   (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-6))
   (map vector fm-coeffs la-svd-coeffs))))


(deftest t25_l159 (is (true? v24_l154)))


(def
 v26_l161
 (let
  [fm-coeffs
   (cons fm-intercept fm-beta)
   la-ne-coeffs
   (cons (:intercept la-normal-result) (:beta la-normal-result))]
  (every?
   (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-6))
   (map vector fm-coeffs la-ne-coeffs))))


(deftest t27_l166 (is (true? v26_l161)))


(def
 v29_l174
 (let
  [n-iter 200]
  (tc/dataset
   {:method ["fastmath lm" "la-linea SVD" "la-linea normal eq."],
    :ms
    [(bench-fn (fn* [] (reg/lm ols-ys ols-xs)) n-iter)
     (bench-fn (fn* [] (la-ols ols-xs ols-ys)) n-iter)
     (bench-fn (fn* [] (la-ols-normal ols-xs ols-ys)) n-iter)]})))


(def v31_l190 (def gp-n 50))


(def
 v32_l192
 (def gp-xs (mapv (fn [i] (/ (double i) gp-n)) (range gp-n))))


(def
 v33_l193
 (def
  gp-ys
  (mapv
   (fn
    [x]
    (+ (Math/sin (* 2.0 Math/PI x)) (frand/grandom rng 0.0 0.1)))
   gp-xs)))


(def v35_l200 (def gp-kernel (k/kernel :gaussian 0.2)))


(def
 v36_l202
 (def
  fm-gp
  (gp/gaussian-process
   (mapv vector gp-xs)
   gp-ys
   {:kernel gp-kernel, :kscale 1.0, :noise 0.01})))


(def
 v37_l208
 (def fm-gp-preds (mapv (fn [x] (gp/predict fm-gp [x])) gp-xs)))


(def
 v39_l216
 (def
  la-gp
  (fn
   [xs ys kernel kscale noise]
   (let
    [n
     (count xs)
     flat
     (->>
      (for [xj xs xi xs] (* (double kscale) (double (kernel xi xj))))
      (dtype/make-container :float64))
     K
     (dtype/clone (tensor/reshape flat [n n]))
     _
     (dotimes
      [i n]
      (tensor/mset!
       K
       i
       i
       (+ (double (tensor/mget K i i)) (double noise))))
     y-col
     (la/column ys)
     w
     (la/solve K y-col)]
    {:K K, :w w, :xs xs, :kernel kernel, :kscale kscale}))))


(def
 v40_l235
 (def
  la-gp-predict
  (fn
   [{:keys [w xs kernel kscale]} x-new]
   (let
    [k-vec
     (la/column
      (mapv
       (fn [xi] (* (double kscale) (double (kernel x-new xi))))
       xs))]
    (tensor/mget (la/mmul (la/transpose w) k-vec) 0 0)))))


(def
 v41_l244
 (def la-gp-model (la-gp (mapv vector gp-xs) gp-ys gp-kernel 1.0 0.01)))


(def
 v42_l247
 (def
  la-gp-preds
  (mapv (fn [x] (la-gp-predict la-gp-model [x])) gp-xs)))


(def
 v44_l252
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-gp-preds la-gp-preds)))


(deftest t45_l256 (is (true? v44_l252)))


(def
 v47_l260
 (let
  [xvs (mapv vector gp-xs) n-iter 100]
  (tc/dataset
   {:method ["fastmath GP" "la-linea GP"],
    :ms
    [(bench-fn
      (fn*
       []
       (gp/gaussian-process
        xvs
        gp-ys
        {:kernel gp-kernel, :kscale 1.0, :noise 0.01, :L? false}))
      n-iter)
     (bench-fn
      (fn* [] (la-gp xvs gp-ys gp-kernel 1.0 0.01))
      n-iter)]})))


(def
 v49_l277
 (def
  gp-scaling
  (let
   [sizes [20 50 100 200] kernel (k/kernel :gaussian 0.2)]
   (mapcat
    (fn
     [n]
     (let
      [xs
       (mapv (fn [i] [(/ (double i) n)]) (range n))
       ys
       (mapv (fn [[x]] (Math/sin (* 2.0 Math/PI (double x)))) xs)
       n-iter
       (max 5 (quot 2000 (* n n)))]
      [{:n n,
        :method "fastmath",
        :ms
        (bench-fn
         (fn*
          []
          (gp/gaussian-process
           xs
           ys
           {:kernel kernel, :kscale 1.0, :noise 0.01, :L? false}))
         n-iter)}
       {:n n,
        :method "la-linea",
        :ms
        (bench-fn (fn* [] (la-gp xs ys kernel 1.0 0.01)) n-iter)}]))
    sizes))))


(def
 v50_l297
 (->
  (tc/dataset gp-scaling)
  (plotly/base {:=x :n, :=y :ms, :=color :method})
  (plotly/layer-point {:=mark-size 8})
  (plotly/layer-line)
  plotly/plot))


(deftest t51_l303 (is ((fn [_] true) v50_l297)))


(def v53_l313 (def rbf-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]))


(def
 v54_l314
 (def rbf-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41]))


(def v56_l318 (def rbf-kernel (k/rbf :gaussian)))


(def v57_l320 (def fm-rbf (rbf/rbf rbf-xs rbf-ys rbf-kernel)))


(def v58_l322 (def rbf-test-pts [0.5 1.5 2.5 3.5 4.5]))


(def v59_l324 (def fm-rbf-preds (mapv fm-rbf rbf-test-pts)))


(def v60_l326 fm-rbf-preds)


(def
 v62_l330
 (def
  la-rbf
  (fn
   [xs ys kernel]
   (let
    [n
     (count xs)
     flat
     (->>
      (for [x1 xs x2 xs] (kernel (dist/euclidean-1d x1 x2)))
      (dtype/make-container :float64))
     Phi
     (tensor/reshape flat [n n])
     y-col
     (la/column ys)
     w
     (la/solve Phi y-col)]
    {:w w, :xs xs, :kernel kernel}))))


(def
 v63_l343
 (def
  la-rbf-predict
  (fn
   [{:keys [w xs kernel]} x-new]
   (let
    [phi-vec
     (la/column
      (mapv (fn [xi] (kernel (dist/euclidean-1d xi x-new))) xs))]
    (tensor/mget (la/mmul (la/transpose w) phi-vec) 0 0)))))


(def v64_l351 (def la-rbf-model (la-rbf rbf-xs rbf-ys rbf-kernel)))


(def
 v65_l353
 (def
  la-rbf-preds
  (mapv
   (fn* [p1__38604#] (la-rbf-predict la-rbf-model p1__38604#))
   rbf-test-pts)))


(def v66_l355 la-rbf-preds)


(def
 v68_l359
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-rbf-preds la-rbf-preds)))


(deftest t69_l363 (is (true? v68_l359)))


(def
 v71_l367
 (let
  [n-iter 2000]
  (tc/dataset
   {:method ["fastmath RBF" "la-linea RBF"],
    :ms
    [(bench-fn (fn* [] (rbf/rbf rbf-xs rbf-ys rbf-kernel)) n-iter)
     (bench-fn (fn* [] (la-rbf rbf-xs rbf-ys rbf-kernel)) n-iter)]})))


(def
 v73_l379
 (def kriging-xs [0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0]))


(def
 v74_l380
 (def kriging-ys [0.0 0.84 0.91 0.14 -0.76 -0.96 -0.28 0.66 0.99 0.41]))


(def
 v76_l386
 (def
  auto-variogram
  (let
   [emp (fastmath.kernel.variogram/empirical kriging-xs kriging-ys)]
   (fastmath.kernel.variogram/fit emp :gaussian))))


(def
 v77_l390
 (def
  fm-kriging
  (kriging/kriging
   kriging-xs
   kriging-ys
   auto-variogram
   {:polynomial-terms (constantly [1.0])})))


(def v78_l394 (def kriging-test-pts [0.5 1.5 2.5 3.5 4.5]))


(def v79_l396 (def fm-kriging-preds (mapv fm-kriging kriging-test-pts)))


(def v80_l398 fm-kriging-preds)


(def
 v82_l405
 (def
  la-kriging
  (fn
   [xs ys variogram polynomial-terms]
   (let
    [n
     (count xs)
     pt-fn
     (or polynomial-terms (constantly [1.0]))
     flat
     (->>
      (for [x1 xs x2 xs] (variogram (dist/euclidean-1d x1 x2)))
      (dtype/make-container :float64))
     V
     (tensor/reshape flat [n n])
     pterms
     (mapv pt-fn xs)
     ptsize
     (count (first pterms))
     total
     (+ n ptsize)
     aug-flat
     (double-array (* total total))
     aug
     (tensor/reshape (tensor/ensure-tensor aug-flat) [total total])
     _
     (dotimes
      [i n]
      (dotimes
       [j n]
       (aset aug-flat (+ (* i total) j) (double (tensor/mget V i j)))))
     _
     (dotimes
      [i n]
      (dotimes
       [j ptsize]
       (let
        [v (double (nth (nth pterms i) j))]
        (aset aug-flat (+ (* i total) n j) v)
        (aset aug-flat (+ (* (+ n j) total) i) v))))
     y-aug
     (la/column (concat ys (repeat ptsize 0.0)))
     w-full
     (la/solve aug y-aug)
     w
     (la/submatrix w-full (range n) :all)
     c
     (la/submatrix w-full (range n total) :all)]
    {:w w, :c c, :xs xs, :variogram variogram, :pt-fn pt-fn}))))


(def
 v83_l440
 (def
  la-kriging-predict
  (fn
   [{:keys [w c xs variogram pt-fn]} x-new]
   (let
    [v-vec
     (mapv (fn [xi] (variogram (dist/euclidean-1d xi x-new))) xs)
     p-vec
     (pt-fn x-new)]
    (+
     (double (la/dot (la/column v-vec) w))
     (double (la/dot (la/column p-vec) c)))))))


(def
 v84_l447
 (def
  la-kriging-model
  (la-kriging kriging-xs kriging-ys auto-variogram (constantly [1.0]))))


(def
 v85_l450
 (def
  la-kriging-preds
  (mapv
   (fn* [p1__38605#] (la-kriging-predict la-kriging-model p1__38605#))
   kriging-test-pts)))


(def v86_l453 la-kriging-preds)


(def
 v88_l457
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-6))
  (map vector fm-kriging-preds la-kriging-preds)))


(deftest t89_l461 (is (true? v88_l457)))


(def
 v91_l465
 (let
  [n-iter 1000]
  (tc/dataset
   {:method ["fastmath kriging" "la-linea kriging"],
    :ms
    [(bench-fn
      (fn*
       []
       (kriging/kriging
        kriging-xs
        kriging-ys
        auto-variogram
        {:polynomial-terms (constantly [1.0])}))
      n-iter)
     (bench-fn
      (fn*
       []
       (la-kriging
        kriging-xs
        kriging-ys
        auto-variogram
        (constantly [1.0])))
      n-iter)]})))


(def v93_l484 (def mah-cov [[2.0 0.5 0.3] [0.5 1.0 0.2] [0.3 0.2 1.5]]))


(def v94_l488 (def mah-x [3.0 1.0 2.0]))


(def v95_l489 (def mah-mean [1.0 0.0 0.5]))


(def v97_l493 (def fm-mah (dist/mahalanobis mah-x mah-mean mah-cov)))


(def v98_l495 fm-mah)


(def
 v100_l499
 (def
  la-mahalanobis
  (fn
   [x mean cov]
   (let
    [S-inv
     (la/invert (la/matrix cov))
     diff
     (la/column (mapv (fn [a b] (- (double a) (double b))) x mean))
     d-sq
     (tensor/mget
      (la/mmul (la/transpose diff) (la/mmul S-inv diff))
      0
      0)]
    (Math/sqrt d-sq)))))


(def v101_l509 (def la-mah (la-mahalanobis mah-x mah-mean mah-cov)))


(def v102_l511 la-mah)


(def v104_l515 (< (Math/abs (- fm-mah la-mah)) 1.0E-10))


(deftest t105_l517 (is (true? v104_l515)))


(def
 v107_l525
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath Mahalanobis" "la-linea Mahalanobis"],
    :ms
    [(bench-fn
      (fn* [] (dist/mahalanobis mah-x mah-mean mah-cov))
      n-iter)
     (bench-fn
      (fn* [] (la-mahalanobis mah-x mah-mean mah-cov))
      n-iter)]})))


(def v109_l538 (def levels [:low :medium :high :very-high]))


(def
 v110_l540
 (def
  fm-helmert-contrasts
  (contrast/mean-contrasts (contrast/helmert levels) false)))


(def v111_l543 fm-helmert-contrasts)


(def
 v113_l549
 (def
  la-mean-contrasts
  (fn
   [coding]
   (let
    [{:keys [names levels mapping]}
     coding
     rows
     (mapv (fn [l] (vec (cons 1.0 (mapping l)))) levels)
     M
     (la/matrix rows)
     M-inv
     (la/invert M)
     nms
     (vec (cons :$intercept names))
     n-levels
     (count levels)
     inv-rows
     (mapv
      (fn [i] (mapv (fn [j] (tensor/mget M-inv i j)) (range n-levels)))
      (range (count nms)))]
    (zipmap nms inv-rows)))))


(def
 v114_l565
 (def
  la-helmert-contrasts
  (la-mean-contrasts (contrast/helmert levels))))


(def v115_l568 la-helmert-contrasts)


(def
 v117_l572
 (every?
  (fn
   [k]
   (let
    [fm-row
     (vec (fm-helmert-contrasts k))
     la-row
     (vec (la-helmert-contrasts k))]
    (every?
     (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-8))
     (map vector fm-row la-row))))
  (keys fm-helmert-contrasts)))


(deftest t118_l580 (is (true? v117_l572)))


(def
 v120_l584
 (let
  [helmert-coding (contrast/helmert levels) n-iter 5000]
  (tc/dataset
   {:method ["fastmath mean-contrasts" "la-linea mean-contrasts"],
    :ms
    [(bench-fn
      (fn* [] (contrast/mean-contrasts helmert-coding false))
      n-iter)
     (bench-fn (fn* [] (la-mean-contrasts helmert-coding)) n-iter)]})))


(def
 v122_l597
 (def
  sg-signal
  (mapv
   (fn [i] (+ (Math/sin (* 0.1 i)) (frand/grandom rng 0.0 0.2)))
   (range 100))))


(def v124_l605 (def fm-sg (signal/savgol-filter 7 3 0)))


(def v125_l607 (def fm-sg-result (fm-sg sg-signal)))


(def
 v127_l613
 (def
  la-savgol-coeffs
  (fn
   [length order derivative]
   (let
    [fc
     (quot (dec length) 2)
     rows
     (mapv
      (fn
       [v]
       (mapv
        (fn [p] (Math/pow (double v) (double p)))
        (range (inc order))))
      (range (- fc) (inc fc)))
     V
     (la/matrix rows)
     {:keys [U S Vt]}
     (la/svd V)
     k
     (count S)
     S-inv
     (la/diag (dfn// 1.0 (tensor/select S (range k))))
     Ut-thin
     (la/submatrix U :all (range k))
     V-pinv
     (la/mmul (la/mmul (la/transpose Vt) S-inv) (la/transpose Ut-thin))
     coeffs
     (mapv (fn [j] (tensor/mget V-pinv derivative j)) (range length))]
    coeffs))))


(def v128_l634 (def la-sg-coeffs (la-savgol-coeffs 7 3 0)))


(def v129_l636 la-sg-coeffs)


(def
 v131_l643
 (let
  [fm-coeffs
   (->
    (for
     [v (range -3 4)]
     (mapv
      (fn* [p1__38606#] (Math/pow (double v) (double p1__38606#)))
      (range 4)))
    (m/seq->double-double-array)
    (org.apache.commons.math3.linear.Array2DRowRealMatrix.)
    (org.apache.commons.math3.linear.SingularValueDecomposition.)
    (.getSolver)
    (.getInverse)
    (.getRow 0)
    (vec))]
  (every?
   (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-10))
   (map vector fm-coeffs la-sg-coeffs))))


(deftest t132_l657 (is (true? v131_l643)))


(def
 v134_l663
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath savgol-filter" "la-linea savgol"],
    :ms
    [(bench-fn (fn* [] (signal/savgol-filter 7 3 0)) n-iter)
     (bench-fn (fn* [] (la-savgol-coeffs 7 3 0)) n-iter)]})))


(def v136_l675 (def fm-fd (finite/fd-coeffs 1 [-1 0 1])))


(def v137_l677 fm-fd)


(def
 v139_l681
 (def
  la-fd-coeffs
  (fn
   [n offsets]
   (let
    [noff
     (count offsets)
     rows
     (mapv
      (fn [i] (mapv (fn [j] (Math/pow (double j) (double i))) offsets))
      (range noff))
     M
     (la/matrix rows)
     rhs
     (la/column
      (mapv
       (fn
        [id]
        (if
         (== (long id) (long n))
         (double (reduce * (range 1 (inc (long n)))))
         0.0))
       (range noff)))
     w
     (la/solve M rhs)]
    [offsets (mapv (fn [i] (tensor/mget w i 0)) (range noff))]))))


(def v140_l700 (def la-fd (la-fd-coeffs 1 [-1 0 1])))


(def v141_l702 la-fd)


(def
 v143_l706
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-10))
  (map vector (second fm-fd) (second la-fd))))


(deftest t144_l710 (is (true? v143_l706)))


(def v146_l714 (def fm-fd2 (finite/fd-coeffs 2 [-2 -1 0 1 2])))


(def v147_l715 (def la-fd2 (la-fd-coeffs 2 [-2 -1 0 1 2])))


(def
 v148_l717
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-10))
  (map vector (second fm-fd2) (second la-fd2))))


(deftest t149_l721 (is (true? v148_l717)))


(def
 v151_l725
 (let
  [offsets [-2 -1 0 1 2] n-iter 5000]
  (tc/dataset
   {:method ["fastmath fd-coeffs" "la-linea fd-coeffs"],
    :ms
    [(bench-fn (fn* [] (finite/fd-coeffs 2 offsets)) n-iter)
     (bench-fn (fn* [] (la-fd-coeffs 2 offsets)) n-iter)]})))


(def v153_l746 (def quad-n 5))


(def
 v154_l748
 (def
  jacobi-b
  (mapv
   (fn
    [j]
    (let [j (double (inc j))] (/ j (Math/sqrt (dec (* 4.0 j j))))))
   (range (dec quad-n)))))


(def
 v155_l754
 (def fm-quad-nodes (quad/get-quadrature-points jacobi-b quad-n)))


(def v156_l757 fm-quad-nodes)


(def
 v158_l763
 (def
  la-quad-nodes
  (fn
   [b n]
   (let
    [m
     (count b)
     sz
     (inc m)
     arr
     (double-array (* sz sz))
     _
     (dotimes
      [i m]
      (let
       [v (double (b i))]
       (aset arr (+ (* i sz) (inc i)) v)
       (aset arr (+ (* (inc i) sz) i) v)))
     T
     (tensor/reshape (tensor/ensure-tensor arr) [sz sz])
     evals
     (la/real-eigenvalues T)]
    (vec (reverse (take-last n evals)))))))


(def v159_l779 (def la-nodes (la-quad-nodes jacobi-b quad-n)))


(def v160_l781 la-nodes)


(def
 v162_l785
 (every?
  (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-10))
  (map vector (sort fm-quad-nodes) (sort la-nodes))))


(deftest t163_l789 (is (true? v162_l785)))


(def
 v165_l793
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath quadrature" "la-linea quadrature"],
    :ms
    [(bench-fn
      (fn* [] (quad/get-quadrature-points jacobi-b quad-n))
      n-iter)
     (bench-fn (fn* [] (la-quad-nodes jacobi-b quad-n)) n-iter)]})))


(def v167_l806 (def fm-ince-c (poly/ince-C-coeffs 4 2 0.5 :none)))


(def v168_l808 (vec fm-ince-c))


(def
 v170_l814
 (def
  la-ince-c-coeffs-even
  (fn
   [p m e]
   (let
    [n
     (quot p 2)
     N
     (inc n)
     order
     (quot m 2)
     arr
     (double-array (* N N))
     _
     (dotimes [i N] (aset arr (+ (* i N) i) (* 4.0 i i)))
     _
     (dotimes
      [i n]
      (let
       [ip (inc i)]
       (aset arr (+ (* i N) ip) (* e (+ n ip)))
       (aset arr (+ (* ip N) i) (* e (- n i)))))
     _
     (aset arr N (* 2.0 (aget arr N)))
     M
     (tensor/reshape (tensor/ensure-tensor arr) [N N])
     {:keys [eigenvalues eigenvectors]}
     (la/eigen M)
     reals
     (cx/re eigenvalues)
     indexed
     (map-indexed (fn [i _] [i (double (reals i))]) (range N))
     sorted-idx
     (map first (sort-by second indexed))
     target-idx
     (nth sorted-idx order)
     ev
     (nth eigenvectors target-idx)
     coeffs
     (mapv (fn [i] (tensor/mget ev i 0)) (range N))
     sgn
     (if (neg? (reduce + coeffs)) -1.0 1.0)]
    (mapv (fn [v] (* sgn v)) coeffs)))))


(def v171_l844 (def la-ince-c (la-ince-c-coeffs-even 4 2 0.5)))


(def v172_l846 la-ince-c)


(def
 v174_l853
 (let
  [normalize
   (fn
    [v]
    (let
     [norm
      (Math/sqrt
       (reduce
        +
        (map (fn* [p1__38607#] (* p1__38607# p1__38607#)) v)))]
     (mapv (fn* [p1__38608#] (/ (double p1__38608#) norm)) v)))
   fm-normed
   (normalize (vec fm-ince-c))
   la-normed
   (normalize la-ince-c)]
  (every?
   (fn [[a b]] (< (Math/abs (- (double a) (double b))) 1.0E-10))
   (map vector fm-normed la-normed))))


(deftest t175_l862 (is (true? v174_l853)))


(def
 v177_l866
 (let
  [n-iter 5000]
  (tc/dataset
   {:method ["fastmath ince-C" "la-linea ince-C"],
    :ms
    [(bench-fn (fn* [] (poly/ince-C-coeffs 4 2 0.5 :none)) n-iter)
     (bench-fn (fn* [] (la-ince-c-coeffs-even 4 2 0.5)) n-iter)]})))
