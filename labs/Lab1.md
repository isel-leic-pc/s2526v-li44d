## Lab 1 — Introduction to Threading on the JVM

This lab serves as an introduction to threading on the JVM, covering the basics of thread creation, lifecycle, and the implications of concurrent execution (i.e. non-determinism and interleaving). 


### Learning outcomes
By the end of this lab, you should be able to:
* Create and start threads on the JVM using the `Thread` class;
* Observe and record a thread’s lifecycle using `getState()` and `isAlive()`. Note that Java methods that begin with `is` and `get` are exposed as properties in Kotlin, so you can use `state` and `isAlive` instead;
* Explain non-determinism and interleaving based on your own observations;
* Relate observations to a simplified thread state diagram: `NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED`;
* Apply basic threading to improve a server from single-client to multi-client handling.

#### Constraints (week 1 scope)
* Focus on the `Thread` class for thread creation and management;
* Use `Thread.sleep()` to simulate work and influence observed thread states;
* **MOST IMPORTANTLY**, DO NOT USE constructs that were not covered in the lecture or were not explicitly allowed by the lab instructions. Remember that the goal of this lab is to understand the basics of threading on the JVM, and using advanced constructs may hinder that understanding.

### Exercise 1

**Goal:** build a minimal tool to observe thread state transitions.

Implement a `startAndObserve` function that, given a target `Thread`:
* prints the target thread initial state before starting it
* starts the target thread
* polls its state with `target.state` every `P` milliseconds
* prints the observed state on the console whenever it changes from the previous observation, including the thread's name and the timestamp of the observation relative to the start of the observation (in ms)
* stops the observation when the thread reaches the `TERMINATED` state

Suggested print format: `<timestamp__in_ms> <threadName> <state>`

Questions to reflect on:
* What value of `P` did you choose, and why?
* Why can polling miss short-lived states?

Implementation notes:
* You may use `System.nanoTime()` or `System.currentTimeMillis()` to get the current time;
* You may set a thread name either by passing it to the `Thread` constructor or by setting its `name` property after creation; (personally, I prefer the former as it ensures the thread's name remains consistent throughout its lifecycle)

### Exercise 2

**Goal:** Use the `startAndObserve` function to observe the lifecycle of a thread that simulates work by sleeping for a certain duration.

1. Create a thread named _sleeper_ that:
    * prints "start"
    * calls `Thread.sleep(2000)` to sleep for 2 seconds
    * prints "end" and terminates
    
2. From main, use `startAndObserve` to observe _sleeper_ throughout its lifecycle.

3. Run the program multiple times and observe the output.

Questions to reflect on:
  * Which states did you observe, and in what order?
  * Did you always observe the `RUNNABLE` state? If not, why might that happen?

Tip:
 * Experiment with different values of `P` in `startAndObserve` to see how it affects the observed states. 

### Exercise 3

**Goal:** Experience that execution order is not guaranteed and that thread execution can be interleaved in different ways across runs.

1.	Create two threads, A and B. Each thread:
    * prints a short sequence of messages to the console (e.g., "A1" up to "A20" and "B1" up to "B20")
    * optionally sleeps a tiny amount of time between prints (e.g., 5ms)
2.	Run the program multiple times, experimenting with different sleep durations (including no sleep at all) and observe the output.

Questions to reflect on:
* Consider two different outputs (short excerpts are enough). Why do they differ? 
* What does interleaving mean in this context?

### Exercise 4

**Goal:** Understand that instrumentation (i.e. console I/O) can change the behavior you observe.

1. Start from your solution to Exercise 3.
2. Run two variants and compare results:
   * **Variant 1 (heavy instrumentation):** print on *every* iteration (e.g., print "A1", "A2", ..., "A20" and similarly for B).
   * **Variant 2 (light instrumentation):** print much less frequently (e.g., only every 10 iterations), while keeping the same amount of work.
     * Example: instead of printing on every iteration, keep a counter and print only when `i % 10 == 0`.
3. Run each variant multiple times.

Questions to reflect on:
* Does the observed interleaving change between the two variants? In what way?
* Why can console output influence scheduling and timing?

Tip:
* If your loop becomes "too fast" and you get very few interleavings, try significantly increasing the number of iterations.

### Exercise 5

**Goal:** Apply basic threading to remove the single-client bottleneck from the Echo Server presented in the lecture.

Start from the single-threaded blocking echo server implementation available [here]() (or an equivalent one).

Task:
1. Modify the server so that it keeps accepting clients in a loop.
2. For each accepted client socket, create a **dedicated thread** to handle that client.
   * The main server thread should return immediately to `accept()` so it can serve other clients.
3. Each client-handling thread should:
   * read data from its client
   * echo it back to the same client
   * terminate when the client disconnects, releasing any resources associated with that client

What to test:
* Run the server;
* Open **two terminals** and connect with `nc` (netcat) or `telnet`;
* Confirm that both clients can interact with the server at the same time;
* Confirm that one idle client does not prevent the other client from being served.

Questions to reflect on:
* Where does blocking happen in your server?
* Based on the thread state diagram discussed in class, what state do you expect a client-handling thread to be in while it is waiting for input?

Tip:
* Give each client thread a meaningful name (e.g., `client-1`, `client-2`) to make debugging and logs easier.