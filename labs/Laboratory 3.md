## Lab 3 — Thread safety

This lab focuses on thread safety attained through mutual exclusion using **`ReentrantLock`**. It focuses on scenarios not yet covered: **deadlocks** and **fail-fast iteration with versioning**.

### Learning outcomes
By the end of this lab, you should be able to:
* Reproduce and explain a deadlock caused by inconsistent lock order;
* Eliminate deadlocks by enforcing lock ordering;
* Implement a simple fail-fast iterator using versioning (`modCount`);
* Validate lock-protected invariants with small experiments.

#### Constraints (Week 3 scope)
* Use `Thread` plus `start()`/`join()`;
* Use `ReentrantLock` and `withLock`;
* Keep implementations minimal and explicit.

### Exercise 1

**Goal:** Reproduce a deadlock.

Model two accounts, each with:
* `balance`
* `lock: ReentrantLock`

Implement transfer operations:
* `transferAtoB(amount)` locks A then B
* `transferBtoA(amount)` locks B then A

Experiment:
1. Launch many threads doing A->B transfers.
2. Launch many threads doing B->A transfers.
3. Add a tiny delay between acquiring first and second lock to increase deadlock probability.
4. Observe that the program may stop making progress.

Questions to reflect on:
* Why can this deadlock?
* Which lock acquisition pattern is creating circular waiting?

### Exercise 2

**Goal:** Remove the deadlock with lock ordering.

Create a single `transfer(from, to, amount)` that:
1. Determines a global lock order (e.g., by account id).
2. Always acquires locks in that order.
3. Performs the transfer and releases both locks.

Validation:
* Run the same stress scenario from Exercise 1.
* Program should complete.
* Invariant must hold: `sum(all balances)` remains constant.

Questions to reflect on:
* Why does the program always complete?

### Exercise 3

**Goal:** Implement fail-fast iteration with versioning.

Starting point:
* Use the `SafeLinkedList<T>` from `demos/Miscellaneous/src/main/kotlin/SafeLinkedList.kt`.
* Create a variant (e.g., `VersionedSafeLinkedList<T>`) based on that implementation.

Changes to make:
* Add `modCount: Long` protected by the same lock.
* Increment `modCount` on every structural modification (`addLast`, `removeFirst`).
* In `iterator()`, capture `expectedModCount` at creation time.

Iterator behavior:
* `hasNext` and `next` MUST check `expectedModCount` against current `modCount`.
* If a structural modification is detected, iterator MUST fail by throwing `ConcurrentModificationException`.
* A structural modification is any operation that changes list size (`addLast`, `removeFirst`).

Questions to reflect on:
* Why is this called fail-fast and not fail-safe?
* What does versioning detect, and what does it not prevent?
* How does this behavior differ from the _weakly consistent_ iterator required in Assignment 1 (`ThreadSafeNonBlockingQueue`)?

### Exercise 4

**Goal:** Run a fail-fast experiment.

Experiment setup:
1. Thread T1 iterates over `VersionedSafeLinkedList` with small delays between `next()` calls.
2. Thread T2 performs structural modifications (`addLast`/`removeFirst`) during iteration.

Observe:
* T1 should throw `ConcurrentModificationException`.

Then run a control case:
* No concurrent modification while iterating; iterator should finish normally.

Questions to reflect on:
* In this fail-fast iterator, what can happen between `hasNext() == true` and `next()`? Compare with Requirement 1, where `next()` MUST succeed after `hasNext()` returns `true`.
* Requirement 1 accepts a _weakly consistent_ iterator that MAY return elements already removed from the queue. How is that tradeoff different from fail-fast behavior?
