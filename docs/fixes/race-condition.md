# Fix: race condition

## Problem

`counter++` is a non-atomic read-modify-write operation. Interleaving workers overwrite updates, violating the expected-count invariant.

## Diagnose

Call `/lab/race` repeatedly and compare expected with actual. Thread dumps and JFR can show scheduling/contended locks but generally cannot prove arbitrary data races. Correctness needs an invariant and concurrency stress testing.

## Repair

This branch uses `AtomicInteger.incrementAndGet()`. Choose synchronization by semantics:

- `AtomicInteger`: one exact atomic counter.
- `LongAdder`: highly contended statistics where instantaneous exactness is unnecessary.
- Lock: multi-field invariants.
- Immutable messages, ownership or confinement: avoid shared mutation.

## Verify

Use JCStress for formal concurrency cases and a repeated load test for the application invariant. Verify exact results and compare CPU/contention. A single passing execution is not proof.