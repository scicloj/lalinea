(ns
 basis-book.complex-tensors-generated-test
 (:require
  [scicloj.basis.linalg :as la]
  [scicloj.basis.impl.complex :as cx]
  [tech.v3.tensor :as tensor]
  [tech.v3.datatype :as dtype]
  [tech.v3.datatype.functional :as dfn]
  [scicloj.kindly.v4.kind :as kind]
  [clojure.test :refer [deftest is]]))


(def v3_l36 (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0]))


(deftest t4_l38 (is ((fn [v] (= [3] (cx/complex-shape v))) v3_l36)))


(def
 v5_l40
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  {:re (vec (cx/re ct)), :im (vec (cx/im ct))}))


(deftest
 t6_l44
 (is
  ((fn [v] (and (= (:re v) [1.0 2.0 3.0]) (= (:im v) [4.0 5.0 6.0])))
   v5_l40)))


(def v8_l49 (cx/complex-tensor (tensor/->tensor [[1.0 2.0] [3.0 4.0]])))


(deftest
 t9_l51
 (is
  ((fn
    [v]
    (and (= [2] (cx/complex-shape v)) (= [1.0 3.0] (vec (cx/re v)))))
   v8_l49)))


(def v11_l56 (cx/complex-tensor-real [5.0 6.0 7.0]))


(deftest
 t12_l58
 (is ((fn [v] (= [0.0 0.0 0.0] (vec (cx/im v)))) v11_l56)))


(def v14_l62 (cx/complex 3.0 4.0))


(deftest t15_l64 (is ((fn [v] (cx/scalar? v)) v14_l62)))


(def
 v16_l66
 [(cx/re (cx/complex 3.0 4.0)) (cx/im (cx/complex 3.0 4.0))])


(deftest t17_l68 (is (= v16_l66 [3.0 4.0])))


(def
 v19_l72
 (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]]))


(deftest t20_l75 (is ((fn [v] (= [2 2] (cx/complex-shape v))) v19_l72)))


(def
 v22_l83
 (let
  [ct (cx/complex-tensor [1.0 2.0 3.0] [4.0 5.0 6.0])]
  [(cx/re (ct 0)) (cx/im (ct 0))]))


(deftest t23_l86 (is (= v22_l83 [1.0 4.0])))


(def
 v25_l90
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (vec (cx/re (ct 0)))))


(deftest t26_l94 (is (= v25_l90 [1.0 2.0])))


(def
 v28_l98
 (let
  [ct (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  [(cx/re ((ct 1) 1)) (cx/im ((ct 1) 1))]))


(deftest t29_l102 (is (= v28_l98 [4.0 8.0])))


(def
 v31_l110
 (let
  [a
   (cx/complex-tensor [1.0 2.0] [3.0 4.0])
   b
   (cx/complex-tensor [5.0 6.0] [7.0 8.0])]
  {:re (vec (cx/re (cx/mul a b))), :im (vec (cx/im (cx/mul a b)))}))


(deftest
 t33_l117
 (is
  ((fn [v] (and (= (:re v) [-16.0 -20.0]) (= (:im v) [22.0 40.0])))
   v31_l110)))


(def
 v35_l122
 (let
  [ct (cx/conj (cx/complex-tensor [1.0 2.0] [3.0 -4.0]))]
  {:re (vec (cx/re ct)), :im (vec (cx/im ct))}))


(deftest t36_l126 (is ((fn [v] (= (:im v) [-3.0 4.0])) v35_l122)))


(def
 v38_l130
 (let
  [m (cx/abs (cx/complex-tensor [3.0 0.0] [4.0 1.0]))]
  [(double (m 0)) (double (m 1))]))


(deftest
 t40_l135
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (first v) 5.0)) 1.0E-10)
     (< (Math/abs (- (second v) 1.0)) 1.0E-10)))
   v38_l130)))


(def
 v42_l142
 (let
  [a
   (cx/complex-tensor [3.0 1.0] [4.0 2.0])
   [re-aa im-aa]
   (cx/dot-conj a a)]
  {:norm-sq re-aa, :im-part im-aa}))


(deftest
 t44_l148
 (is
  ((fn
    [v]
    (and
     (< (Math/abs (- (:norm-sq v) 30.0)) 1.0E-10)
     (< (Math/abs (:im-part v)) 1.0E-10)))
   v42_l142)))


(def
 v46_l158
 (let
  [A
   (cx/complex-tensor [[1.0 0.0] [0.0 1.0]] [[0.0 0.0] [0.0 0.0]])
   B
   (cx/complex-tensor [[0.0 1.0] [1.0 0.0]] [[0.0 0.0] [0.0 0.0]])]
  (la/mmul A B)))


(deftest
 t47_l164
 (is ((fn [ct] (= [2 2] (cx/complex-shape ct))) v46_l158)))


(def
 v49_l168
 (let
  [A (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[5.0 6.0] [7.0 8.0]])]
  (la/transpose A)))


(deftest
 t50_l172
 (is
  ((fn [ct] (let [r (cx/re ct)] (= 3.0 (tensor/mget r 0 1))))
   v49_l168)))


(def
 v52_l177
 (la/det
  (cx/complex-tensor [[1.0 3.0] [5.0 7.0]] [[2.0 4.0] [6.0 8.0]])))


(deftest
 t54_l182
 (is
  ((fn
    [d]
    (and
     (< (Math/abs (cx/re d)) 1.0E-10)
     (< (Math/abs (- (cx/im d) -16.0)) 1.0E-10)))
   v52_l177)))


(def
 v56_l187
 (let
  [A
   (cx/complex-tensor [[1.0 2.0] [3.0 4.0]] [[0.5 1.0] [1.5 2.5]])
   Ainv
   (la/invert A)
   product
   (la/mmul A Ainv)
   re-part
   (cx/re product)
   im-part
   (cx/im product)]
  (and
   (<
    (dfn/reduce-max
     (dfn/abs
      (dfn/- (dtype/->double-array re-part) (double-array [1 0 0 1]))))
    1.0E-10)
   (<
    (dfn/reduce-max (dfn/abs (dtype/->double-array im-part)))
    1.0E-10))))


(deftest t57_l197 (is (true? v56_l187)))


(def v59_l201 (def a (cx/complex-tensor [1.0 -2.0 3.0] [4.0 5.0 -6.0])))


(def v60_l202 (def b (cx/complex-tensor [-3.0 0.5 2.0] [1.0 -1.5 7.0])))


(def
 v61_l204
 (defn
  approx=
  "Check that two ComplexTensors are approximately equal."
  [x y tol]
  (let
   [re-diff
    (dfn/- (cx/re x) (cx/re y))
    im-diff
    (dfn/- (cx/im x) (cx/im y))]
   (and
    (< (dfn/reduce-max (dfn/abs re-diff)) tol)
    (< (dfn/reduce-max (dfn/abs im-diff)) tol)))))


(def v63_l214 (approx= (cx/mul a b) (cx/mul b a) 1.0E-10))


(deftest t64_l216 (is (true? v63_l214)))


(def v66_l220 (approx= (cx/conj (cx/conj a)) a 1.0E-10))


(deftest t67_l222 (is (true? v66_l220)))


(def
 v69_l226
 (approx=
  (cx/conj (cx/mul a b))
  (cx/mul (cx/conj a) (cx/conj b))
  1.0E-10))


(deftest t70_l230 (is (true? v69_l226)))


(def
 v72_l234
 (let
  [lhs (cx/abs (cx/mul a b)) rhs (dfn/* (cx/abs a) (cx/abs b))]
  (< (dfn/reduce-max (dfn/abs (dfn/- lhs rhs))) 1.0E-10)))


(deftest t73_l238 (is (true? v72_l234)))


(def
 v75_l242
 (let
  [[re-ab im-ab]
   (cx/dot-conj a b)
   [re-aa _]
   (cx/dot-conj a a)
   [re-bb _]
   (cx/dot-conj b b)]
  (<= (- (+ (* re-ab re-ab) (* im-ab im-ab)) 1.0E-10) (* re-aa re-bb))))


(deftest t76_l248 (is (true? v75_l242)))
