## Lab 4 — Monitors: Timeout and Cancelation

This lab continues the monitor-based synchronizers work from the lectures.

The goal is to complete existing demo implementations by adding support for **timeout** and **cancelation**, and then implement a `ManualResetEvent` inspired by .NET.

### Learning outcomes
By the end of this lab, you should be able to:
* Extend monitor-based synchronizers with timeout semantics;
* Correctly handle cancelation/interrupt while waiting;
* Keep monitor state consistent when timeout/cancelation races with signaling;
* Implement a reusable signal-style synchronizer (`ManualResetEvent`).

#### Constraints (Week 7 scope)
* Start from the existing classes in:
  * `demos/Miscellaneous/src/main/kotlin/synch/ValueHolder.kt`
  * `demos/Miscellaneous/src/main/kotlin/synch/BoundedQueue.kt`
* Use monitor-style reasoning with explicit state and condition predicates;
* Preserve correctness first, then optimize;
* **MOST IMPORTANTLY**, DO NOT USE constructs that were not covered in lectures or not explicitly allowed in the lab.

### Exercise 1

**Goal:** Add timeout and cancelation support to `ValueHolder`.

Starting point:
* `ValueHolder` currently supports blocking `getValue()` and `putValue(value)`.

Task:
1. Add a timed get operation (e.g., `getValue(timeout: Duration): Int?`).
2. Ensure timeout is handled correctly:
   * Return `null` when timeout expires and the value is still unavailable.
   * Recompute remaining wait time after each wake-up.
3. Ensure cancelation is handled correctly:
   * If interrupted while waiting, follow JVM interrupt protocol (`InterruptedException`).
   * Leave state consistent.

Questions to reflect on:
* Why is `while` + predicate still required with timed waiting?
* Which race can occur between timeout expiration and `putValue`?

### Exercise 2

**Goal:** Add timeout and cancelation support to `BoundedQueue`.

Starting point:
* `BoundedQueue<T>` already supports blocking `put` and `take`.

Task:
1. Add timed variants for both operations (e.g., `put(value, timeout)` and `take(timeout)`).
2. For each timed operation:
   * Return failure (`false`/`null`) on timeout.
   * Clean up pending request state on timeout.
3. On cancelation/interrupt:
   * Clean up pending request state before propagating `InterruptedException`.
4. Handle races between producer/consumer signaling and timeout/cancelation.

Questions to reflect on:
* What breaks if timed-out/canceled requests are not removed from request lists?
* What outcomes are valid when signal and timeout happen close together?

### Exercise 3

**Goal:** Implement `ManualResetEvent` (inspired by .NET).

Implement a class with behavior equivalent to `.NET ManualResetEvent`:
* `set()` signals the event and releases all current/future waiters while signaled.
* `reset()` returns the event to non-signaled state.
* `waitOne()` waits indefinitely until signaled.
* `waitOne(timeout: Duration): Boolean` waits up to timeout and reports success/failure.

Requirements:
* Support cancelation/interrupt on waiting operations;
* Use monitor-style condition waiting (`while` + predicate);
* Keep semantics consistent under concurrent `set`/`reset`/`waitOne` calls.

Questions to reflect on:
* What invariants define the signaled/non-signaled states?
* What race conditions are critical between `set`, `reset`, and timed waits?

Tip:
* Build incrementally: indefinite wait -> timed wait -> interrupt/cancelation correctness.
* While designing the synchronizer, keep in mind that `set()` -> `reset()` calls can happen in quick succession.
