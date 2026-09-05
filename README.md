# HyperLogLog

A Java implementation of the **HyperLogLog** algorithm for approximate cardinality
estimation, together with Python scripts used to empirically evaluate its accuracy.
This was built as an assignment for the *Applied Algorithms* course.

## Report

[Click here](assignment2_report.pdf) for the full write-up.

## The algorithm

HyperLogLog answers a simple question cheaply: *"how many distinct elements are in
this stream?"* — without storing the elements themselves. A naive exact answer
requires memory proportional to the number of distinct elements (e.g. a hash set).
HyperLogLog instead estimates the cardinality using a small, fixed amount of memory
(a handful of registers), trading perfect accuracy for massive space savings.

It works in three steps:

1. **Hash.** Every incoming element `x` is passed through a hash function `h(x)` that
   spreads values uniformly over a bit string. This implementation builds `h` from a
   family of 32 random linear (XOR/parity) functions over `GF(2)`: bit `i` of the hash
   is the parity of `A[i] & x`, where `A[i]` are fixed random 32-bit masks.

2. **Bucket and count leading zeros.** The hash is split in two parts:
   - The first `p = log2(m)` bits (via a second hash `f(x)`) select one of `m`
     registers/buckets.
   - The remaining bits are scanned for the position of the leftmost `1` bit,
     `ρ(x)`. Each register keeps the **maximum** `ρ` value ever seen for elements
     that landed in it. Intuitively, seeing a run of `k` leading zeros is a `2^-k`
     probability event, so the largest run observed in a bucket hints at how many
     distinct items passed through it.

3. **Estimate.** The cardinality is derived from the harmonic mean of `2^register`
   across all `m` registers, scaled by a bias-correction constant `α_m`:

   ```
   E = α_m * m² / Σ(2^-register[j])
   ```

   Two corrections are applied at the extremes of the range:
   - **Small range correction:** if the raw estimate is small and some registers are
     still empty, the estimate is replaced by linear counting:
     `m * ln(m / V)`, where `V` is the number of empty registers.
   - **Large range correction:** if the estimate approaches the hash space limit
     (`2^32`), it is corrected for hash collisions:
     `-2^32 * ln(1 - E / 2^32)`.

Accuracy improves with more registers (`m`): the expected relative error is
approximately `1.04 / √m`. This repo evaluates three configurations —
`m = 256, 1024, 4096` — against streams of exactly 1,000,000 distinct 32-bit
integers, comparing the estimate to the known true cardinality.

## Repository layout

```
hyperloglog/                          Gradle project (Java implementation + unit tests)
  app/src/main/java/hyperloglog/
    HyperLogLog.java                  Core algorithm (h, f, ρ, cardinality estimation)
    HyperLogLogExperiment.java        Quick sanity-check experiment
    HashFunctionExperiment.java       Dumps ρ(h(x)) for hash-quality analysis
  app/src/test/java/hyperloglog/
    HyperLogLogTest.java              Unit tests against fixture data in app/data/
  app/data/                           Test fixtures (hash, ρ, register, threshold cases)

experiment.py                         Drives the built jar to gather cardinality estimates (-> results.csv)
errorEstimation.py                    Computes error/σ statistics and error histograms from results.csv
hashFunctionEvaluation.py             Checks empirical vs. theoretical ρ distribution (-> hash_distribution.pdf)
createTexTable.py                     Renders sigma_table_results.csv as a LaTeX table (-> table.tex)

results.csv, sigma_table_results.csv  Experiment output data
*_histogram.pdf, hash_distribution.pdf  Generated plots
assignment2_report.pdf                Full write-up
```

## Running it

### Prerequisites

- JDK 11+ (the Gradle wrapper is included, no local Gradle install needed)
- Python 3 with `numpy` and `matplotlib` (only needed for the analysis scripts)

### Build and test the Java project

```bash
cd hyperloglog
./gradlew build      # compiles and runs the JUnit test suite
./gradlew test        # run just the tests
```

On Windows use `gradlew.bat` instead of `./gradlew`.

### Run the estimator directly

The jar reads one integer per line from stdin (decimal or `0x`-prefixed hex) and
prints the estimated number of distinct values. Pick the register count via the
first argument (`hyperloglog256`, `hyperloglog1024`, or `hyperloglog4096`):

```bash
./gradlew jar                                     # produces app/build/libs/app.jar
java -jar app/build/libs/app.jar hyperloglog1024 < path/to/numbers.txt
```

### Reproduce the experiments

Run these from the repository root, after building the jar as above:

```bash
python experiment.py            # generates 100 trials x {256,1024,4096} -> results.csv
python errorEstimation.py       # error/σ stats and histograms from results.csv
python hashFunctionEvaluation.py  # requires pHashOutput.txt (see HashFunctionExperiment)
python createTexTable.py        # sigma_table_results.csv -> table.tex
```
