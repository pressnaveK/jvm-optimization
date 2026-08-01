# Fix: deadlock

## Problem

The issue branch acquires locks in opposite orders: A then B and B then A. Each thread can own one lock and wait forever for the other.

## Prove it

```bash
jcmd <PID> Thread.print -l > thread-1.txt
sleep 10
jcmd <PID> Thread.print -l > thread-2.txt
jstack -l <PID> > jstack.txt
```

Prove the same lock cycle persists across dumps. Temporary contention is not deadlock.

## Repair

The branch applies one global order: always A then B. Reduce nested locking and critical-section duration. Where semantics permit, use `ReentrantLock.tryLock(timeout)` with cleanup in `finally`. Prefer immutable or single-owner state.

## Recovery and verification

Restart may recover service availability but does not fix the cause. Re-run concurrent load, inspect repeated dumps/JFR monitor events, and add a regression test that exercises competing paths.