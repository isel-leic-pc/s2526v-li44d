package palbp.demos.pc.isel

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class SimpleThreadPool(val numberOfThreads: Int = Runtime.getRuntime().availableProcessors()) {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private val workQueue = ArrayDeque<() -> Unit>()
    private val threads: Array<Thread>

    private fun takeWork(): () -> Unit {
        guard.withLock {
            if (workQueue.isNotEmpty()) {
                return workQueue.removeFirst()
            }

            while (true) {
                condition.await()

                if (workQueue.isNotEmpty()) {
                    return workQueue.removeFirst()
                }
            }
        }
    }

    init {
        require(numberOfThreads > 0) { "Number of threads must be greater than 0" }

        threads = Array(size = numberOfThreads) {
            thread {
                while (true) {
                    try {
                        val workItem = takeWork()
                        workItem()
                    }
                    catch (e: InterruptedException) {
                        // Thread was interrupted, exit gracefully
                        break
                    }
                    catch (e: Exception) {
                        // The work item threw an exception, log it and continue processing other items
                        e.printStackTrace()
                    }
                }
            }
        }
    }

     fun submit(task: () -> Unit) = guard.withLock {
         workQueue.addLast(task)
         condition.signal()
     }

    fun shutdown() {
        guard.withLock {
            threads.forEach { it.interrupt() }
        }
    }

    fun awaitTermination() {
        threads.forEach { it.join() }
    }
}