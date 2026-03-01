;; # Fractals with Complex Tensors
;;
;; Fractals like the [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set)
;; and [Julia sets](https://en.wikipedia.org/wiki/Julia_set)
;; are defined by iterating a complex function
;; $z_{n+1} = z_n^2 + c$ and checking whether the orbit
;; escapes to infinity.
;;
;; This chapter computes fractals using **ComplexTensor** arithmetic.
;; The iteration loop is imperative (each step depends on the
;; previous), but within each step the computation is **vectorized**
;; across the entire complex plane — `la/mul` and `la/add` operate on
;; every grid point simultaneously.

(ns la-linea-book.fractals
  (:require
   ;; La Linea (https://github.com/scicloj/la-linea):
   [scicloj.la-linea.linalg :as la]
   ;; Complex tensors — interleaved [re im] layout:
   [scicloj.la-linea.complex :as cx]
   ;; Tensor creation and indexing (https://github.com/cnuernber/dtype-next):
   [tech.v3.tensor :as tensor]
   ;; Low-level buffer operations:
   [tech.v3.datatype :as dtype]
   ;; Element-wise array math:
   [tech.v3.datatype.functional :as dfn]
   ;; Tensor ↔ BufferedImage conversion:
   [tech.v3.libs.buffered-image :as bufimg]
   ;; Visualization annotations (https://scicloj.github.io/kindly-noted/):
   [scicloj.kindly.v4.kind :as kind]))

;; ## Building a complex plane grid
;;
;; We represent the complex plane as a ComplexTensor of shape
;; `[height width]`, where each element is a complex number
;; $x + yi$. `tensor/compute-tensor` fills the underlying
;; `[h w 2]` real tensor with the real and imaginary parts.

(def complex-grid
  (fn [re-min re-max im-min im-max h w]
    (cx/complex-tensor
     (tensor/compute-tensor [h w 2]
                            (fn [r c part]
                              (if (zero? part)
                                (+ re-min (* (- re-max re-min) (/ c (double (dec w)))))
                                (+ im-min (* (- im-max im-min) (/ r (double (dec h)))))))
                            :float64))))

;; A small test grid — the four corners should span the range:

(let [g (complex-grid -2.0 1.0 -1.5 1.5 3 3)
      raw (.tensor g)]
  {:shape (cx/complex-shape g)
   :top-left-re (tensor/mget raw 0 0 0)
   :bottom-right-im (tensor/mget raw 2 2 1)})

(kind/test-last
 [(fn [v] (and (= (:shape v) [3 3])
               (= (:top-left-re v) -2.0)
               (= (:bottom-right-im v) 1.5)))])

;; ## Mandelbrot set
;;
;; The Mandelbrot set is the set of $c$ values for which the
;; orbit of $z_0 = 0$ under $z_{n+1} = z_n^2 + c$ does not
;; escape. We color each pixel by **how many iterations**
;; it takes to escape $|z| > 2$.
;;
;; The inner loop is imperative: we maintain a mutable `counts`
;; array and update it at each iteration. But the complex
;; arithmetic — squaring and adding the entire grid — is
;; vectorized through ComplexTensor operations.

(def mandelbrot-counts
  (fn [re-min re-max im-min im-max h w max-iter]
    (let [c (complex-grid re-min re-max im-min im-max h w)
          counts (int-array (* h w) 0)
          zero-grid (cx/complex-tensor
                     (tensor/compute-tensor [h w 2]
                                            (fn [_ _ _] 0.0) :float64))]
      (loop [z zero-grid k 0]
        (if (>= k max-iter)
          counts
          (let [z2 (dtype/clone (la/add (la/mul z z) c))
                abs-t (la/abs z2)]
            (dotimes [r h]
              (dotimes [col w]
                (when (< (tensor/mget abs-t r col) 2.0)
                  (let [idx (+ (* r w) col)]
                    (aset counts idx (inc (aget counts idx)))))))
            (recur z2 (inc k))))))))

;; ### Rendering
;;
;; Map iteration counts to colors. Points inside the set
;; (count = max-iter) are black; others are colored by
;; how quickly they escape.

(def counts->image
  (fn [counts h w max-iter]
    (tensor/compute-tensor [h w 3]
                           (fn [r c ch]
                             (let [cnt (aget counts (+ (* r w) c))]
                               (if (= cnt max-iter) 0
                                   (let [t (/ (double cnt) max-iter)]
                                     (case (int ch)
                                       0 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.0)))))))
                                       1 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.33)))))))
                                       2 (int (* 255 (* 0.5 (+ 1.0 (Math/cos (* 2.0 Math/PI (+ t 0.67))))))))))))
                           :uint8)))

;; ### The classic view

(def mandelbrot-img
  (let [h 300 w 400 max-iter 50
        counts (mandelbrot-counts -2.0 0.7 -1.2 1.2 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image mandelbrot-img)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Zooming in
;;
;; The Mandelbrot set has infinite detail at every scale.
;; Here's a zoom into the "Seahorse Valley":

(def mandelbrot-zoom
  (let [h 300 w 400 max-iter 100
        counts (mandelbrot-counts -0.77 -0.73 0.05 0.08 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image mandelbrot-zoom)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Julia sets
;;
;; A [Julia set](https://en.wikipedia.org/wiki/Julia_set) uses a
;; **fixed** $c$ and varies the starting point $z_0$. Each choice
;; of $c$ produces a different fractal.

(def julia-counts
  (fn [c-re c-im re-min re-max im-min im-max h w max-iter]
    (let [z0 (complex-grid re-min re-max im-min im-max h w)
          c-grid (cx/complex-tensor
                  (tensor/compute-tensor [h w 2]
                                         (fn [_ _ part] (if (zero? part) c-re c-im))
                                         :float64))
          counts (int-array (* h w) 0)]
      (loop [z z0 k 0]
        (if (>= k max-iter)
          counts
          (let [z2 (dtype/clone (la/add (la/mul z z) c-grid))
                abs-t (la/abs z2)]
            (dotimes [r h]
              (dotimes [col w]
                (when (< (tensor/mget abs-t r col) 2.0)
                  (let [idx (+ (* r w) col)]
                    (aset counts idx (inc (aget counts idx)))))))
            (recur z2 (inc k))))))))

;; ### Gallery of Julia sets
;;
;; Different values of $c$ produce different shapes.

;; $c = -0.7 + 0.27i$ — a "dendrite" Julia set:

(def julia-dendrite
  (let [h 300 w 300 max-iter 80
        counts (julia-counts -0.7 0.27 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-dendrite)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; $c = 0.355 + 0.355i$ — a connected Julia set:

(def julia-connected
  (let [h 300 w 300 max-iter 80
        counts (julia-counts 0.355 0.355 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-connected)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; $c = -0.4 + 0.6i$ — a "rabbit" Julia set:

(def julia-rabbit
  (let [h 300 w 300 max-iter 80
        counts (julia-counts -0.4 0.6 -1.5 1.5 -1.5 1.5 h w max-iter)]
    (counts->image counts h w max-iter)))

(bufimg/tensor->image julia-rabbit)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ## Newton's fractal
;;
;; [Newton's method](https://en.wikipedia.org/wiki/Newton%27s_method)
;; finds roots of equations by iterating
;; $z_{n+1} = z_n - f(z_n) / f'(z_n)$.
;; In the complex plane, each starting point converges to one of
;; the roots — and the **boundaries between convergence regions**
;; form a fractal.
;;
;; We apply Newton's method to $f(z) = z^3 - 1$, whose roots
;; are the three cube roots of unity:
;; $\omega_0 = 1$,
;; $\omega_1 = e^{2\pi i/3}$,
;; $\omega_2 = e^{4\pi i/3}$.
;; Since $f'(z) = 3z^2$, the iteration is:
;;
;; $$z_{n+1} = z - \frac{z^3 - 1}{3z^2}$$
;;
;; Like Mandelbrot and Julia sets, the iteration is vectorized
;; across the entire grid — `la/mul`, `la/sub`, and `la/scale`
;; operate on every grid point simultaneously.

;; ### Complex division
;;
;; Newton's method requires dividing one complex grid by another.
;; We build element-wise complex division from real/imaginary parts:
;; $\frac{a}{b} = \frac{a\bar{b}}{|b|^2}$.

(def complex-div
  (fn [a b]
    (let [ar (cx/re a) ai (cx/im a)
          br (cx/re b) bi (cx/im b)
          shape (dtype/shape ar)
          denom (dfn/+ (dfn/* br br) (dfn/* bi bi))]
      (cx/complex-tensor
       (tensor/reshape (dfn// (dfn/+ (dfn/* ar br) (dfn/* ai bi)) denom) shape)
       (tensor/reshape (dfn// (dfn/- (dfn/* ai br) (dfn/* ar bi)) denom) shape)))))

;;  Verify: (3+4i)/(1+2i) = (11-2i)/5 = 2.2-0.4i

(let [a (cx/complex-tensor (la/matrix [[3]]) (la/matrix [[4]]))
      b (cx/complex-tensor (la/matrix [[1]]) (la/matrix [[2]]))
      result (complex-div a b)]
  (and (< (Math/abs (- (tensor/mget (cx/re result) 0 0) 2.2)) 1e-10)
       (< (Math/abs (- (tensor/mget (cx/im result) 0 0) -0.4)) 1e-10)))

(kind/test-last [true?])

;; ### Newton iteration

(def newton-roots
  (fn [re-min re-max im-min im-max h w max-iter]
    (let [z0 (complex-grid re-min re-max im-min im-max h w)
          ;; Constant grid of 1 + 0i
          one (cx/complex-tensor
               (tensor/compute-tensor [h w 2]
                                      (fn [_ _ part] (if (zero? part) 1.0 0.0))
                                      :float64))
          ;; The three cube roots of unity
          roots [(cx/complex 1.0 0.0)
                 (cx/complex (Math/cos (/ (* 2.0 Math/PI) 3.0))
                             (Math/sin (/ (* 2.0 Math/PI) 3.0)))
                 (cx/complex (Math/cos (/ (* 4.0 Math/PI) 3.0))
                             (Math/sin (/ (* 4.0 Math/PI) 3.0)))]
          root-idx (int-array (* h w) -1)]
      ;; Iterate Newton's method
      (loop [z (dtype/clone z0) k 0]
        (if (>= k max-iter)
          ;; Classify each point by nearest root
          (let [z-final z]
            (dotimes [r h]
              (dotimes [c w]
                (let [idx (+ (* r w) c)
                      zr (tensor/mget (.tensor ^scicloj.la_linea.complex.ComplexTensor z-final) r c 0)
                      zi (tensor/mget (.tensor ^scicloj.la_linea.complex.ComplexTensor z-final) r c 1)
                      best (reduce (fn [best-i i]
                                     (let [root (nth roots i)
                                           dr (- zr (double (cx/re root)))
                                           di (- zi (double (cx/im root)))
                                           d (+ (* dr dr) (* di di))
                                           root-best (nth roots best-i)
                                           dbr (- zr (double (cx/re root-best)))
                                           dbi (- zi (double (cx/im root-best)))
                                           db (+ (* dbr dbr) (* dbi dbi))]
                                       (if (< d db) i best-i)))
                                   0 [1 2])]
                  (aset root-idx idx best))))
            root-idx)
          ;; z_{n+1} = z - (z³ - 1) / (3z²)
          (let [z2 (la/mul z z)
                z3 (la/mul z z2)
                fz (la/sub z3 one)
                fpz (la/scale z2 3.0)
                z-next (dtype/clone (la/sub z (complex-div fz fpz)))]
            (recur z-next (inc k))))))))

;; ### Rendering
;;
;; Each pixel is colored by which root it converged to.
;; The three basins get distinct hues; the fractal structure
;; emerges at the boundaries.

(def root-colors
  [[230 50 50]    ;; root 0 (1+0i) — red
   [50 180 50]    ;; root 1 — green
   [50 80 220]])  ;; root 2 — blue

(def roots->image
  (fn [root-idx h w]
    (tensor/compute-tensor [h w 3]
                           (fn [r c ch]
                             (let [idx (aget root-idx (+ (* r w) c))]
                               (if (neg? idx) 0
                                   (nth (nth root-colors idx) ch))))
                           :uint8)))

;; ### The full view

(def newton-img
  (let [h 300 w 300 max-iter 30
        root-idx (newton-roots -2.0 2.0 -2.0 2.0 h w max-iter)]
    (roots->image root-idx h w)))

(bufimg/tensor->image newton-img)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])

;; ### Zooming in
;;
;; The boundary between basins has fractal detail at every scale.
;; Here is a zoom into the region near $z = 0$:

(def newton-zoom
  (let [h 300 w 300 max-iter 50
        root-idx (newton-roots -0.5 0.5 -0.5 0.5 h w max-iter)]
    (roots->image root-idx h w)))

(bufimg/tensor->image newton-zoom)

(kind/test-last
 [(fn [img] (= java.awt.image.BufferedImage (type img)))])
