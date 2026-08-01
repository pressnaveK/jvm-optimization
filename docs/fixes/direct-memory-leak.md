# Fix: direct/native memory retention

## Problem

Direct buffers consume native memory outside the Java heap. The issue implementation retains every buffer indefinitely, so container RSS can grow even when heap charts appear acceptable.

## Diagnose

Start with `-XX:NativeMemoryTracking=summary`:

```bash
jcmd <PID> VM.native_memory baseline
curl -X POST 'localhost:8080/lab/memory/direct?mb=100'
jcmd <PID> VM.native_memory summary.diff scale=MB
curl localhost:8080/actuator/metrics/jvm.buffer.memory.used
```

A heap dump contains buffer owner objects, not the native bytes themselves.

## Repair

The branch uses a bounded pool as the teaching fix. Real systems must bound in-flight buffers, close channels/resources, release reference-counted buffers correctly, and apply backpressure. Treat `-XX:MaxDirectMemorySize` as a safety boundary, not the correction.

## Verify

Compare heap, buffer-pool meters, Native Memory Tracking, process RSS, container working set, throughput and errors under the same load. Native memory also includes metaspace, code cache, thread stacks, libraries and JVM structures.