# basis

**[Linear algebra](https://en.wikipedia.org/wiki/Linear_algebra) with [tensor](https://en.wikipedia.org/wiki/Tensor) abstractions in [Clojure](https://clojure.org/)**

basis is a linear algebra library where matrices are [dtype-next](https://github.com/cnuernber/dtype-next) tensors
and [EJML](https://ejml.org/) provides the computational backend. The two share the same
row-major `double[]` memory layout, enabling **zero-copy** interop — no allocation,
no copying, just different views of the same data.

## General info
|||
|-|-|
|Website | [https://scicloj.github.io/basis/](https://scicloj.github.io/basis/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/basis)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/basis.svg)](https://clojars.org/org.scicloj/basis)|
|License |[MIT](https://github.com/scicloj/basis/blob/main/LICENSE)|
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

## Installation

Add to your `deps.edn`:

```clojure
{:deps {org.scicloj/basis {:mvn/version "0.1.0"}}}
```

## Documentation

The [basis book](https://scicloj.github.io/basis/) is a set of notebook-based chapters covering:

- **Getting started** — quickstart
- **Core concepts** — tensors & EJML interop, complex tensors, Fourier transforms, abstract linear algebra, sharing & mutation
- **Applications** — linear systems, Markov chains & PageRank, image processing, fractals, decompositions in action, least squares, spectral graph theory
- **Validation** — algebraic identities

Each chapter includes inline tests via `kind/test-last`.

## API

Most users need only two namespaces:

```clojure
(require '[scicloj.basis.linalg :as la])
(require '[scicloj.basis.transform :as xf])
```

`scicloj.basis.linalg` is the main API — matrix construction, arithmetic,
decompositions, inverse, and solve. `scicloj.basis.transform` bridges
Fastmath's transform API to ComplexTensors.

For complex tensor operations:

```clojure
(require '[scicloj.basis.complex :as cx])
```

A convenience namespace re-exports the most common functions:

```clojure
(require '[scicloj.basis.api :as basis])
```

## Built on

- [dtype-next](https://github.com/cnuernber/dtype-next) — array/tensor numerics
- [EJML](https://ejml.org/) — efficient Java matrix library (real + complex)
- [fastmath](https://github.com/generateme/fastmath) — transforms (FFT, DCT, DST, DHT)

The [book notebooks](https://scicloj.github.io/basis/) also use
[tablecloth](https://github.com/scicloj/tablecloth),
[tableplot](https://github.com/scicloj/tableplot), and
[kindly](https://github.com/scicloj/kindly) (included in the `:dev` and `:test` aliases).

## Development

```bash
clojure -M:dev -m nrepl.cmdline   # start REPL
./run_tests.sh                     # run tests (268 tests, 268 assertions)
clojure -T:build ci                # test + build JAR
```

## License

MIT License — see LICENSE file.

---

Part of the [scicloj](https://scicloj.github.io/) ecosystem for scientific computing in Clojure.
