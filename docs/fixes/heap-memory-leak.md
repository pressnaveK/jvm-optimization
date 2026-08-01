# Fix: Java heap memory leak

## Problem

The issue branch keeps every allocated array in a singleton-owned list. Those strong references remain reachable for the process lifetime, so GC cannot reclaim them.

## Prove the cause

```bash
jcmd <PID> GC.class_histogram > before.txt
curl -X POST 'localhost:8080/lab/memory/heap?mb=100'
jcmd <PID> GC.class_histogram > after.txt
jcmd <PID> GC.heap_dump diagnostics/heap.hprof
```

Graph the post-GC old-generation floor, not merely the sawtooth. In MAT or IntelliJ inspect the dominator tree, retained size, and path from `byte[]` to the singleton field.

## Repair

The repair enforces a hard maximum. Production code should normally use a maintained cache such as Caffeine with maximum weight/size, expiry, removal metrics, and explicit ownership. Also remove listeners, callbacks and `ThreadLocal` values at lifecycle end.

## Verify

Repeat identical traffic long enough to pass several old-generation cycles. Confirm the live set stabilizes, entry gauge never exceeds the maximum, latency remains within SLO, and no OOM/container kill occurs. Increasing `-Xmx` alone is not a leak repair.