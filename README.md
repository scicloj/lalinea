# La Linea

**[Linear algebra](https://en.wikipedia.org/wiki/Linear_algebra) with [tensor](https://en.wikipedia.org/wiki/Tensor) abstractions in [Clojure](https://clojure.org/)**

La Linea is a linear algebra library where matrices are [dtype-next](https://github.com/cnuernber/dtype-next) tensors
and [EJML](https://ejml.org/) provides the computational backend. The two share the same
row-major `double[]` memory layout, enabling **zero-copy** interop.

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/lalinea/](https://scicloj.github.io/lalinea/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/lalinea)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/lalinea.svg)](https://clojars.org/org.scicloj/lalinea)|
|License |[MIT](https://github.com/scicloj/lalinea/blob/main/LICENSE)|
|Status |🛠experimental🛠|

## Design

Three namespaces cover most usage:

- **`t/`** (`scicloj.lalinea.tensor`) — construct tensors in either field (real or complex)
- **`el/`** (`scicloj.lalinea.elementwise`) — element-wise math, polymorphic over the field
- **`la/`** (`scicloj.lalinea.linalg`) — linear algebra: products, decompositions, solve

All three are polymorphic — they work uniformly on both real and comples tensors.
Field-aware operations like `el/re`, `el/im`, `el/conj`
are identity on reals and meaningful on complex.

Supporting namespaces: `tape/` (computation recording),
`grad/` (autodiff), `ft/` (FFT bridge), `vis/` (visualization helpers).

## Features

### Real matrices

- **Construction** — `matrix`, `eye`, `zeros`, `ones`, `diag`, `column`, `row`, `submatrix`
- **Arithmetic** — `mmul`, `mpow`, `transpose`
- **Properties** — `trace`, `det`, `norm` (Frobenius), `dot`
- **Analysis** — `rank`, `condition-number`, `pinv` (pseudoinverse), `null-space`, `col-space`
- **Solve** — `solve`, `lstsq` (least squares), `invert`
- **Decompositions** — [eigendecomposition](https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix), [SVD](https://en.wikipedia.org/wiki/Singular_value_decomposition), [QR](https://en.wikipedia.org/wiki/QR_decomposition), [Cholesky](https://en.wikipedia.org/wiki/Cholesky_decomposition)

### Complex matrices

- [ComplexTensor](https://en.wikipedia.org/wiki/Complex_number) — interleaved `[re im]` layout sharing memory with EJML's `ZMatrixRMaj`
- Complex `mmul`, `add`, `sub`, `scale`, conjugate `transpose`, `invert`, `solve`
- Complex `trace`, `det`, `norm`

### Element-wise operations

- `scicloj.lalinea.elementwise` — 35+ tape-aware functions with complex dispatch
- Arithmetic: `+`, `-`, `*`, `/`, `scale`
- Complex-aware: `re`, `im`, `conj`
- Powers: `sq`, `sqrt`, `pow`, `cbrt`
- Exponential: `exp`, `log`, `log10`, `log1p`, `expm1`
- Trigonometric: `sin`, `cos`, `tan`, `asin`, `acos`, `atan`
- Hyperbolic: `sinh`, `cosh`, `tanh`
- Reductions: `abs`, `sum`, `prod`, `mean`, `reduce-max`, `reduce-min`
- Rounding: `floor`, `ceil`, `round`, `clip`
- Comparison: `>`, `<`, `>=`, `<=`, `eq`, `not-eq`, `min`, `max`

### Tagged literals

Real and complex tensors print as readable tagged literals:

```clj
#la/R [:float64 [2 2]
       [[1.000 2.000]
        [3.000 4.000]]]

#la/C [:float64 [2 2]
       [[1.000 + 5.000 i  2.000 + 6.000 i]
        [3.000 + 7.000 i  4.000 + 8.000 i]]]
```

Round-trip through `pr-str` / `read-string`.

### Fourier transforms

- Forward/inverse [DFT](https://en.wikipedia.org/wiki/Discrete_Fourier_transform) bridging real signals to complex spectra, as well as complex-to-complex
- , [DCT](https://en.wikipedia.org/wiki/Discrete_cosine_transform), [DST](https://en.wikipedia.org/wiki/Discrete_sine_transform), [DHT](https://en.wikipedia.org/wiki/Discrete_Hartley_transform)

### Computation tape

- Record `t/`, `la/`, and `el/` operations as a DAG with `tape/with-tape`
- Inspect memory status: `:contiguous`, `:strided`, or `:lazy`
- Detect shared backing arrays between tensors
- Visualize computation graphs as Mermaid flowcharts

### Automatic differentiation

- Reverse-mode autodiff via VJP rules on the computation tape
- Differentiable ops: `el/+`, `el/-`, `el/scale`, `la/mmul`, `la/transpose`, `la/trace`, `la/det`, `la/invert`, `la/norm`, `la/dot`, `el/*`, `el/sq`, `el/sum`
- Compute gradients of scalar functions with respect to matrix inputs

### Zero-copy interop

- dtype-next tensor <-> EJML `DMatrixRMaj` — same `double[]`, no copy
- ComplexTensor <-> EJML `ZMatrixRMaj` — same interleaved `double[]`, no copy
- Mutations through either view are immediately visible in the other
- All `dfn` element-wise operations work directly on matrices (they are tensors)

## Documentation

The [La Linea book](https://scicloj.github.io/lalinea/) is a set of notebook-based chapters covering:

- **Getting started** — quickstart
- **Core concepts** — tensors & EJML interop, complex tensors, Fourier transforms, sharing & mutation, computation tape, automatic differentiation
- **Abstract linear algebra** — vectors & spaces, maps & structure, inner products & orthogonality, eigenvalues & decompositions
- **Applications** — linear systems, Markov chains & PageRank, image processing, fractals, decompositions in action, least squares, spectral graph theory
- **Validation** — algebraic identities
- **Reference** — API reference

Each chapter includes inline tests via
[Clay's test generation](https://scicloj.github.io/clay/clay_book.test_generation.html).

## API

```clojure
(require '[scicloj.lalinea.tensor :as t])           ; tensor construction, structural ops, EJML interop
(require '[scicloj.lalinea.linalg :as la])          ; arithmetic, decompositions, solve
(require '[scicloj.lalinea.elementwise :as el])    ; element-wise math, comparisons, field ops
(require '[scicloj.lalinea.transform :as ft])       ; FFT / DCT / DST / DHT bridge
(require '[scicloj.lalinea.tape :as tape])          ; computation DAG recording
(require '[scicloj.lalinea.grad :as grad])          ; reverse-mode automatic differentiation
(require '[scicloj.lalinea.vis :as vis])            ; visualization helpers
```

## Built on

- [dtype-next](https://github.com/cnuernber/dtype-next) — array/tensor numerics
- [EJML](https://ejml.org/) — efficient Java matrix library (real + complex)
- [fastmath](https://github.com/generateme/fastmath) — transforms (FFT, DCT, DST, DHT)

The [book notebooks](https://scicloj.github.io/lalinea/) also use
[tablecloth](https://github.com/scicloj/tablecloth),
[tableplot](https://github.com/scicloj/tableplot), and
[kindly](https://github.com/scicloj/kindly) (included in the `:dev` and `:test` aliases).

## Development

```bash
clojure -M:dev -m nrepl.cmdline   # start REPL
./run_tests.sh                     # run tests (521 tests, 521 assertions)
clojure -T:build ci                # test + build JAR
```

## License

MIT License

---

Part of the [scicloj](https://scicloj.github.io/) ecosystem for scientific computing in Clojure.
