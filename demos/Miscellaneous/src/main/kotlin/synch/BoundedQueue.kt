package palbp.demos.pc.isel.synch

import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Supports the producer-consumer pattern.
 * FIFO delivery is guaranteed.
 * The synchronizer has a bounded capacity and will block the calling thread if the buffer is full. This is essential
 * to generate backpressure.
 *
 * Limitations:
 *  - Too many context-switches may occur when waiting for an item to be available or when waiting for space in the
 *    buffer.
 */
class BoundedQueue<T> {

    private val buffer: LinkedList<T> = LinkedList()

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request<T>(var value: T? = null)
    private val requests: LinkedList<Request<T>> = LinkedList()

    fun take(): T {
        guard.withLock {

            // Can proceed? (Fast path)
            TODO()

            while (true) {
                condition.await()

                // Can proceed? (Slow path)
                TODO()
            }
        }
    }

    fun put(value: T): Unit {
        guard.withLock {

            // Can proceed? (Fast path)
            TODO()

            while (true) {
                condition.await()

                // Can proceed? (Slow path)
                TODO()
            }
        }
    }
}