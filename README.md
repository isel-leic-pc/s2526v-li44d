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
  - [Laboratory 1](./labs/Lab1.md)

### Week 2 - Threading on the JVM: data parallelism (preview)
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
  - Lecture video (coming soon)
  - [Laboratory 2](./labs/Lab2.md)
