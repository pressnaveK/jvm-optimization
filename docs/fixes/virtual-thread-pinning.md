# Fix: Java 21 virtual-thread pinning

## Problem

On Java 21, a virtual thread that blocks inside `synchronized` can pin its carrier. Frequent/long pins can constrain carrier availability and throughput.

## Diagnose

```bash
jcmd <PID> JFR.dump name=lab filename=pinning.jfr
jfr print --events jdk.VirtualThreadPinned,jdk.VirtualThreadSubmitFailed pinning.jfr
```

JFR enables the pinned event with a threshold, so very short pins may not appear. Pinning is not proof that every monitor is problematic; correlate duration/frequency with throughput and latency.

## Repair

The branch uses `ReentrantLock`, allowing a parked virtual thread to unmount on JDK 21. Prefer moving blocking I/O outside critical sections and shortening them. Do not pool virtual threads. Bound scarce resources with semaphores or connection pools, and do not use `ThreadLocal` as a resource pool.

## Verify

Repeat the same load and compare pinned events, carrier utilization, throughput, latency, downstream saturation and errors.