## Lab 2 — Threading on the JVM: Data Parallelism

This lab focuses on the class of problems classified as **data parallelism**. The main goal is to help to understand how we can obtain **deterministic outcomes** even though concurrent execution is **non-deterministic** and interleavings vary across runs, as observed in [Lab 1](./Lab1.md). We will achieve this by structuring our computations according to the **fork–join pattern** (also known as **embarrassingly-parallel + reduce**), and by using **synchronization with completion** using `join()`.

### Learning outcomes
By the end of this lab, you should be able to:
* Coordinate threads using `join()` and relate it to the `WAITING` thread state;
* Structure CPU-bound work as **data parallelism**: partition input --> local computation --> deterministic reduce;
* Reason about how deterministic outcomes can be achieved despite non-deterministic scheduling in the class of data parallel problems;
* Measure (aproximately) and reason about speedup limits (Amdahl’s law) in simple experiments.

#### Constraints (Week 2 scope)
* Use the `Thread` class for thread creation and management. Alternativelly, you may use the builder function `thread { ... }` from the Kotlin standard library;
* Use `join()` for synchronization by completion;
* **MOST IMPORTANTLY**, DO NOT USE constructs that were not covered in the lectures or were not explicitly allowed by the lab instructions. Remember that the goal of this lab is to understand the subjects under study and that the use of advanced constructs may hinder that understanding.

### Exercise 1

**Goal:** Observe behavior on synchronization with thread completion.

1. Create a thread named _worker_ that simulates work by sleeping for around 2 seconds and then terminates;
2. Create a thread named _waiter_ that calls `worker.join()` and then prints a message such as "worker finished";
3. Use your `startAndObserve` function from [Lab 1](./Lab1.md) to observe the lifecycle of _waiter_.

Questions to reflect on:
* Which thread state does _waiter_ enter while waiting for _worker_ to terminate?
* Why is `join()` a useful synchronization primitive to ensure deterministic outcomes?

Tip:
* Start observing _waiter_ before starting it, so that you can observe `NEW`.

### Exercise 2

**Goal:** Implement an embarrassingly-parallel + reduce algorithm.

Implement the function `fun parallelSum(values: IntArray, nBlocks: Int): Long` that returns the sum of all elements in `values`.

Requirements:
* Partition the array into `nBlocks` disjoint ranges of approximately equal size;
* Each thread computes a **local sum** for its assigned range;
* Store local sums in an array `partials`, one slot per block;
* The calling thread must synchronize with the completion of all workers by calling `join()` on each worker before reducing `partials`.

Questions to reflect on:
* Why is the result deterministic even though the workers may finish in different orders?
* Where does shared memory exist in this solution, and why is it safe here?

Tip:
* Use `Long` for partials to avoid overflow when summing many `Int` values.

### Exercise 3

**Goal:** Demonstrate that reducing before completion yields incorrect and non-deterministic results.

Starting from your solution to Exercise 2:

1. Create an intentionally incorrect version that reduces `partials` **without joining** all threads first.
2. Run the program multiple times.

Questions to reflect on:
* Do you observe different results across runs? Why?
* What does this experiment tell you about `join()` as a correctness boundary?

Tip:
* Add a small sleep (e.g., 1–5ms) between starting the workers and reducing `partials` to increase the likelihood of observing non-deterministic results.
* Try to use a large input array (e.g., 100-500 million elements) to increase the chance of observing non-determinism due to longer worker execution times.

### Exercise 4

**Goal:** Apply data parallelism to compute a histogram (reduce on an array).

Given a grayscale image represented as a `ByteArray` (values 0..255), implement:

* `sequentialHistogram(pixels: ByteArray): IntArray` (size 256)
* `parallelHistogram(pixels: ByteArray, nThreads: Int): IntArray`

Requirements for the parallel version:
* Each worker computes a local histogram `IntArray(256)` for its range of pixels.
* The main thread `join()`s all workers.
* Reduce by summing local histograms into a global histogram.

Sanity check:
* The sum of all bins must equal `pixels.size`.

Questions to reflect on:
* Why is the histogram computation a good example of embarrassingly-parallel + reduce?
* Why is it important that each worker uses a local histogram instead of updating a shared one?

### Exercise 5

**Goal:** Measure paralelization speedup and relate it to Amdahl’s law.

Using either `parallelSum` (Exercise 2) or `parallelHistogram` (Exercise 4):

1. Measure execution time for `nThreads = 1, 2, 4, 8, ...` (choose values appropriate for your machine).
2. Compute the speedup `S(n) = T(1) / T(n)`.
3. Compare your observed speedup with what Amdahl’s law suggests.

Questions to reflect on:
* Does speedup keep improving as `nThreads` increases? Why not?
* In your implementation, what parts are inherently sequential?
* What overheads may limit speedup even when the algorithm is embarrassingly parallel?

Tip:
* Use a large enough input so that `T(1)` is at least a few hundred milliseconds, otherwise measurement noise will dominate.
* Consider running each configuration multiple times and averaging results to reduce noise.
* Change the implementation of the `parallelSum` function to accumulate partial sums directly into the the partials array without using a local variable, and observe how this affects speedup due to increased contention on shared memory.
