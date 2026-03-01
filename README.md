# La Linea

**[Linear algebra](https://en.wikipedia.org/wiki/Linear_algebra) with [tensor](https://en.wikipedia.org/wiki/Tensor) abstractions in [Clojure](https://clojure.org/)**

La Linea is a linear algebra library where matrices are [dtype-next](https://github.com/cnuernber/dtype-next) tensors
and [EJML](https://ejml.org/) provides the computational backend. The two share the same
row-major `double[]` memory layout, enabling **zero-copy** interop.

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/la-linea/](https://scicloj.github.io/la-linea/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/la-linea)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/la-linea.svg)](https://clojars.org/org.scicloj/la-linea)|
|License |[MIT](https://github.com/scicloj/la-linea/blob/main/LICENSE)|
|Status |🛠alpha🛠|

## Features

### Real matrices

- **Construction** — `matrix`, `eye`, `zeros`, `diag`, `column`, `row`, `submatrix`
- **Arithmetic** — `mmul`, `add`, `sub`, `scale`, `transpose`
- **Properties** — `trace`, `det`, `norm` (Frobenius)
- **Inverse and solve** — `invert`, `solve` (returns `nil` for singular matrices)
- **Decompositions** — [eigendecomposition](https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix), [SVD](https://en.wikipedia.org/wiki/Singular_value_decomposition), [QR](https://en.wikipedia.org/wiki/QR_decomposition), [Cholesky](https://en.wikipedia.org/wiki/Cholesky_decomposition)

### Complex matrices

- [ComplexTensor](https://en.wikipedia.org/wiki/Complex_number) — interleaved `[re im]` layout sharing memory with EJML's `ZMatrixRMaj`
- Complex `mmul`, `add`, `sub`, `scale`, conjugate `transpose`, `invert`, `solve`
- Complex `trace`, `det`, `norm`

### Fourier transforms

- Forward/inverse [FFT](https://en.wikipedia.org/wiki/Fast_Fourier_transform) bridging real signals to ComplexTensor spectra
- Complex-to-complex FFT
- [DCT](https://en.wikipedia.org/wiki/Discrete_cosine_transform), [DST](https://en.wikipedia.org/wiki/Discrete_sine_transform), [DHT](https://en.wikipedia.org/wiki/Discrete_Hartley_transform)

### Zero-copy interop

- dtype-next tensor ↔ EJML `DMatrixRMaj` — same `double[]`, no copy
- ComplexTensor ↔ EJML `ZMatrixRMaj` — same interleaved `double[]`, no copy
- Mutations through either view are immediately visible in the other
- All `dfn` element-wise operations work directly on matrices (they are tensors)

### Computation tape

- Record `la/` operations as a DAG with `tape/with-tape`
- Inspect memory status: `:contiguous`, `:strided`, or `:lazy`
- Detect shared backing arrays between tensors
- Visualize computation graphs as Mermaid flowcharts

## Installation

Add to your `deps.edn`:

```clojure
{:deps {org.scicloj/la-linea {:mvn/version "0.1.0"}}}
```

## Documentation

The [La Linea book](https://scicloj.github.io/la-linea/) is a set of notebook-based chapters covering:

- **Getting started** — quickstart
- **Core concepts** — tensors & EJML interop, complex tensors, Fourier transforms, abstract linear algebra, sharing & mutation, computation tape
- **Applications** — linear systems, Markov chains & PageRank, image processing, fractals, decompositions in action, least squares, spectral graph theory
- **Validation** — algebraic identities

Each chapter includes inline tests via `kind/test-last`.

## API

```clojure
(require '[scicloj.la-linea.linalg :as la])      ; matrix construction, arithmetic, decompositions, solve
(require '[scicloj.la-linea.complex :as cx])      ; complex tensor operations
(require '[scicloj.la-linea.transform :as xf])    ; FFT / DCT / DST / DHT bridge
(require '[scicloj.la-linea.tape :as tape])      ; computation DAG recording, memory inspection
(require '[scicloj.la-linea.vis :as vis])         ; visualization helpers
```

## Built on

- [dtype-next](https://github.com/cnuernber/dtype-next) — array/tensor numerics
- [EJML](https://ejml.org/) — efficient Java matrix library (real + complex)
- [fastmath](https://github.com/generateme/fastmath) — transforms (FFT, DCT, DST, DHT)

The [book notebooks](https://scicloj.github.io/la-linea/) also use
[tablecloth](https://github.com/scicloj/tablecloth),
[tableplot](https://github.com/scicloj/tableplot), and
[kindly](https://github.com/scicloj/kindly) (included in the `:dev` and `:test` aliases).

## Development

```bash
clojure -M:dev -m nrepl.cmdline   # start REPL
./run_tests.sh                     # run tests (403 tests, 403 assertions)
clojure -T:build ci                # test + build JAR
```

## License

MIT License — see LICENSE file.

---

Part of the [scicloj](https://scicloj.github.io/) ecosystem for scientific computing in Clojure.
