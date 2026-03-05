# Changelog

All notable changes to La Linea will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] — Initial alpha

Real and complex linear algebra with tensor abstractions,
backed by dtype-next and EJML.

### Added
- **Tensor construction** (`t/`) — `matrix`, `eye`, `zeros`, `ones`, `diag`, `column`, `row`, `compute-matrix`, `compute-tensor`, `reshape`, `select`, `submatrix`, `flatten`, `hstack`, `reduce-axis`
- **Complex tensors** (`t/`) — `complex-tensor`, `complex-tensor-real`, `complex`, `wrap-tensor`, interleaved `[re im]` layout with zero-copy EJML `ZMatrixRMaj` interop
- **Linear algebra** (`la/`) — `mmul`, `dot`, `dot-conj`, `mpow`, `transpose`, `trace`, `det`, `norm`, `rank`, `condition-number`, `solve`, `invert`, `lstsq`, `pinv`, `null-space`, `col-space`
- **Decompositions** (`la/`) — `eigen`, `real-eigenvalues`, `svd`, `qr`, `cholesky`
- **Element-wise ops** (`el/`) — arithmetic (`+`, `-`, `*`, `/`, `scale`), complex field (`re`, `im`, `conj`), powers, transcendental, reductions, comparisons, argops, sorting
- **Fourier transforms** (`ft/`) — forward/inverse FFT, DCT, DST, DHT; zero-copy bridge between Fastmath and ComplexTensor
- **Computation tape** (`tape/`) — DAG recording, memory inspection, Mermaid visualization
- **Automatic differentiation** (`grad/`) — reverse-mode autodiff via VJP rules
- **Visualization** (`vis/`) — arrow plots, graph plots, grayscale/channel image helpers
- **Tagged literals** — `#la/R` and `#la/C` for round-trip `pr-str` / `read-string`
- **Zero-copy interop** — dtype-next tensor <-> EJML `DMatrixRMaj` / `ZMatrixRMaj`
- **Polymorphic dispatch** — `la/`, `el/`, and `t/` functions work uniformly on both real and complex inputs
