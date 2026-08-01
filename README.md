# JVM Optimization Lab (Java 21)

A Spring Boot laboratory for platform engineers to reproduce JVM memory, concurrency, virtual-thread, allocation, and garbage-collection problems, then diagnose them with logs, JFR, Micrometer, Prometheus, Grafana, IntelliJ Profiler, JDK Mission Control, Eclipse MAT, and built-in JDK tools.

## Branch model

- `main`: observability and monitoring instructions.
- `with-issue`: deliberately broken application. Run only in an isolated lab.
- `fix/heap-memory-leak`
- `fix/direct-memory-leak`
- `fix/gc-allocation-pressure`
- `fix/race-condition`
- `fix/deadlock`
- `fix/thread-starvation`
- `fix/virtual-thread-pinning`
- `resolve`: starts from `with-issue` and receives every fix branch.

## Run the lab

```bash
git switch with-issue
mvn clean verify
mkdir -p diagnostics
java \
  -Xms256m -Xmx512m \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=diagnostics \
  -Xlog:gc*,safepoint:file=diagnostics/gc.log:time,uptime,level,tags:filecount=5,filesize=20M \
  -XX:StartFlightRecording=name=lab,settings=profile,disk=true,maxage=30m,maxsize=256m,dumponexit=true,filename=diagnostics/app.jfr \
  -jar target/jvm-optimization-1.0.0.jar --spring.profiles.active=lab
```

Or start the complete stack:

```bash
docker compose up --build
```

Services:

- Spring Boot: http://localhost:8080
- Actuator health: http://localhost:8080/actuator/health
- Prometheus exposition: http://localhost:8080/actuator/prometheus
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (`admin/admin`)

## Baseline first

```bash
jcmd -l
jcmd <PID> VM.version
jcmd <PID> VM.flags
jcmd <PID> GC.heap_info
jcmd <PID> GC.class_histogram > diagnostics/histogram-before.txt
curl -s localhost:8080/actuator/prometheus > diagnostics/metrics-before.txt
```

Record the JDK build, JVM flags, GC, heap/container limits, traffic, latency, errors, CPU, RSS, and timestamps. Never tune from a single snapshot.

## JFR capture

```bash
jcmd <PID> JFR.start name=incident settings=profile duration=10m maxsize=256m filename=diagnostics/incident.jfr
jcmd <PID> JFR.check
jcmd <PID> JFR.dump name=incident filename=diagnostics/incident-now.jfr
jcmd <PID> JFR.stop name=incident filename=diagnostics/incident-final.jfr
jfr summary diagnostics/incident-final.jfr
```

Use `settings=default` for an approved lower-overhead continuous recording and `settings=profile` for bounded investigations. Correlate CPU, allocation, GC, locks, I/O, exceptions, safepoints, and virtual-thread events on one timeline.

## Heap and native-memory evidence

```bash
jcmd <PID> GC.class_histogram
jcmd <PID> GC.heap_dump diagnostics/heap.hprof
jcmd <PID> VM.native_memory baseline
jcmd <PID> VM.native_memory summary.diff scale=MB
```

Native Memory Tracking requires `-XX:NativeMemoryTracking=summary` at JVM startup. Heap dumps can pause and stress a process; capture one replica at a time after considering available disk and memory.

## Thread evidence

```bash
jcmd <PID> Thread.print -l > diagnostics/thread-1.txt
sleep 10
jcmd <PID> Thread.print -l > diagnostics/thread-2.txt
jstack -l <PID> > diagnostics/jstack.txt
```

Several dumps distinguish persistent deadlock/starvation from transient contention.

## Micrometer, Prometheus, and Grafana

Micrometer instruments JVM and application values; it is not durable local log storage. The Prometheus registry exposes samples at `/actuator/prometheus`; Prometheus scrapes and stores them.

Useful PromQL:

```promql
sum(jvm_memory_used_bytes{area="heap"})
rate(jvm_gc_pause_seconds_count[5m])
rate(jvm_gc_memory_allocated_bytes_total[5m])
jvm_threads_live_threads
process_cpu_usage
```

Create panels for request p95/p99, error rate, CPU, container RSS, heap by pool, post-GC live data, allocation rate, GC pauses, live/peak threads, direct buffers, and custom lab gauges. Alert on sustained symptoms plus service impact, not raw heap utilization alone.

## GC comparison

| Goal | Collector | Starting flag |
|---|---|---|
| Balanced throughput/pauses | G1 | `-XX:+UseG1GC` |
| Very low pauses, large heap | ZGC | `-XX:+UseZGC` |
| Low pauses when supported | Shenandoah | `-XX:+UseShenandoahGC` |
| Batch throughput | Parallel | `-XX:+UseParallelGC` |

Compare identical traffic, data, warm-up, CPU/memory limits, and duration. Measure throughput, p50/p95/p99, errors, CPU, allocation rate, live set, pause distribution, cycle frequency, and RSS. Change one major variable at a time.

## External analysis tools

- IntelliJ Profiler: CPU/allocation flame graphs, call tree, threads, line hints.
- IntelliJ Memory tab: instance Diff, new-instance tracking, retained size, referrers.
- JDK Mission Control: JFR timeline and automated rules.
- Eclipse MAT: dominator tree, retained sets, GC-root paths, OQL.
- async-profiler: CPU, allocation, locks, and native stacks.
- VisualVM: overview, sampling, dumps, and plugins.
- Arthas: secured live method/thread/class inspection.
- Cryostat: managed JFR for containers and Kubernetes.
- OpenTelemetry/APM: cross-service latency and dependency correlation.

## Security

Heap dumps, JFR, thread dumps, and Actuator diagnostics can expose tokens, payloads, paths, hostnames, and business data. Keep Actuator diagnostics on a protected management network and never upload production artifacts to public analyzers without approval.
