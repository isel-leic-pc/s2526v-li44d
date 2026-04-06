# ISEL - Concurrent Programming
## LEIC44D - Summer of 2025/2026

## Course Information

**Professor:** 
- [Paulo Pereira](https://palbp.github.io/index.html), office F.0.21

**Schedule:**
- Tuesday, 14:00-15:30, G.0.13 (LS1)
- Friday, 14:00-17:00, G.0.20 (LS5)

**Moodle**
 - [Course Information](https://2526moodle.isel.pt/course/view.php?id=10537)
 - [LEIC44D Course Section](https://2526moodle.isel.pt/course/view.php?id=10704)

## Course outline
### Week 1 - Threading on the JVM: introduction
- Threading on the JVM
	- Motivations
	- Core execution model
- Creating threads: the `Thread` class
- Thread states and lifecycle (initial version)
	- NEW, RUNNABLE (Running vs Ready), TIMED_WAITING, TERMINATED
- Implications of the execution model
	- Non-determinism
	- Interleaving of thread execution
- For reference:
	- [Lecture video](https://www.youtube.com/watch?v=B5s09qoZxYU)
	- [Laboratory 1](Laboratory%201.md)

### Week 2 - Threading on the JVM: data parallelism
- Review
	- Week 1 recap 
	- Lab 1 Exercise 5 solution discussion (our first concurrent echo server)
- Orchestrating thread execution
	- Thread coordination and synchronization
	- Synchronization by completion: `join()` and the `WAITING` state
- Computing with threads:
	- Memory areas revisited: global (statics), heap and stack (thread-confined vs shared data)
	- Data parallelism (i.e. fork–join pattern, or embarrassingly-parallel + reduce)
	- Speedup limits: Amdahl's law
	- Deterministic outcomes despite non-deterministic scheduling (isolate + join + reduce)
- Compute bound vs I/O bound tasks
	- Discussion of the limitations of the "one thread per client" approach for our echo server
- For reference:
	- [Lecture video](https://www.youtube.com/watch?v=I1AuBdQSM-Y&list=PL8XxoCaL3dBi0fxcQdYiMydEYaQkOfzi5&index=2)
	- [Laboratory 2](Laboratory%202.md)

### Week 3 - Threading on the JVM: thread safety
- Thread safety
	- What thread safety means
	- Correctness goals under concurrent execution
- Approaches to thread safety
	- Immutability (no mutations)
	- Thread confinement (no sharing)
	- Mutual exclusion (shared mutable state with synchronization)
- Protecting compound actions over shared state
	- Mutual exclusion and invariants
	- Explicit locks with `ReentrantLock` (main focus)
	- Intrinsic locks with `synchronized` (brief coverage)
- Thread-safety hazards (race conditions)
	- Compound actions on shared mutable state
		- Read-modify-write
		- Check-and-act
	- Lost updates
		- Examples: loss of increments, loss of list insertions
- For reference:
	- [Lecture video 1](https://www.youtube.com/live/AVloVHlMffs?si=GAijPwvK4SaqXBRM)
	- [Lecture video 2](https://www.youtube.com/watch?v=8W7yFgDsXuc)
	- [Assignment 1](./assignments/first-assignment.adoc)

### Week 4 - Threading on the JVM: Synchronization
- Part 1: Threading on the JVM: synchronization
	- Synchronization on the JVM
		- Data synchronization
		- Control synchronization
	- Control synchronization: synchronizers
		- Purpose and motivation
		- Example 1: `ValueHolder`
- Part 2: Threading on the JVM: monitors
	- Building custom synchronizers using Lampson and Redell monitors
		- Purpose and motivation
		- Lampson and Redell semantics
	- Guarded blocks and condition predicates
	- Demo: Implementing a `Latch` synchronizer (without support for timeout or cancelation)
- For reference:
	- [Lecture video](https://www.youtube.com/live/y8y7SxmH324?si=Up9JyhpS1O7TrMLJ)
	- [Laboratory 3](Laboratory%203.md)

### Week 5 - Threading on the JVM: monitors (continued)
- Building custom synchronizers using Lampson and Redell monitors, continued
	- Review: Recap of the simple `ValueHolder` synchronizer from last week
* The _Delegated Execution_ pattern (a.k.a. _kernel style_ approach)
* Demos: 
	* producer-consumer with `UnboundedBuffer`, `UnboundedQueue` and `BlockingQueue` (discussion of variants and the importance of bounded capacity to achieve back-pressure)
	* `ManualResetEvent` to illustrate another variant of the same pattern with space optimizations 
	* No support for timeout or cancelation this week. That is a subject for next week

* For reference:
	- [Lecture video](https://www.youtube.com/watch?v=Yqg-jGvYiJw&list=PL8XxoCaL3dBi0fxcQdYiMydEYaQkOfzi5&index=9)
	- [Assignment 1](./assignments/first-assignment.adoc)

### Week 6 - Threading on the JVM: monitors (timeout and cancelation)
- 
- Building custom synchronizers using Lampson and Redell monitors, continued
	- Review: Recap of the _Delegated Execution_ pattern (a.k.a. _kernel style_ approach)
- Monitor based solutions
	- Why timeout and cancelation support matter
	- Correct timeout handling
		- Absolute deadline vs relative timeout
		- Recompute remaining time after each wake-up
	- Correct cancelation handling
		- Interrupt protocol
		- Cancelation of pending requests and state cleanup
		- Avoiding leaks/stale waiters in request lists
- Demos:
	- `BlockingQueue` with support no support for cancelation or timeout and with no optimizations
	- `UnboundedQueue` with support for cancelation and timeout with no optimizations
	- `UnboundedQueue` with support for cancelation and timeout
- Optimization of monitor-based solutions
	- Multiple conditions (e.g., one per batch)
	- Per-thread conditions for targeted wake-up
	- Tradeoffs: time (i.e. reduced unnecessary wake-ups/context switches) vs space (memory consumption)
	- Optimizing the `BlockingQueue` and `ManualResetEvent` implementations
- Validation strategy
	- Functional tests for success/timeout/cancelation paths
	- Stress tests for races between signal, timeout, and cancelation
- For reference:
	- [Lecture video](https://www.youtube.com/live/RXe_xaZllHw?si=8HNsz_wJYC2vxNc4)
	- [Assignment 1](./assignments/first-assignment.adoc)
	- [Laboratory 4](labs/Laboratory%204.md)
