# Fix: allocation pressure and GC tuning

## Problem

The issue branch allocates an array and an unnecessary copy on every iteration. The objects are reclaimable, so this is allocation pressure rather than a retention leak.

## Diagnose

Trigger the endpoint while recording JFR. Inspect Allocation by Class, Allocation by Thread, allocation stacks, TLAB events, young-GC frequency, promotion, pauses and safepoints. A stable post-GC floor differentiates churn from a leak.

## Repair

The teaching fix reuses one buffer and removes the copy. In production, target proven stacks: accidental copies, boxing, temporary collections, oversized serialization buffers and unbounded batches.

## Tune after code correction

Compare G1, ZGC and Parallel GC with identical traffic, data, warm-up, CPU/memory limits and duration. Measure throughput, p50/p95/p99, errors, CPU, allocation rate, live set, pause distribution and RSS. Change one major variable at a time. `MaxGCPauseMillis` is a goal, not a guarantee.