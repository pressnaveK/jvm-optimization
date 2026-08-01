# Fix: platform-thread starvation

## Problem

A tiny fixed pool accepts an unbounded queue of long blocking tasks. Work waits indefinitely, latency grows, and overload is hidden in the queue.

## Diagnose

Correlate executor active/queued/completed/rejected meters, request latency, downstream latency, connection pools and several thread dumps. Many WAITING threads are not automatically faulty; identify what they await.

## Repair

The teaching fix uses a bounded queue and explicit rejection. Production design also needs downstream timeouts, cancellation, bulkheads for different workloads, backpressure and concurrency limits aligned to downstream capacity. Increasing thread count can amplify database/API overload.

## Verify

Overload the endpoint deliberately. Confirm memory remains bounded, rejection is visible, successful latency stays within the chosen budget, cancellation works, and recovery is quick after load stops.