package palbp.demos.pc.isel.synch

import java.util.LinkedList
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Supports the producer-consumer pattern.
 * No particular delivery guarantee is provided.
 * The synchronizer has unlimited capacity.
 *
 * Limitations:
 *  - The buffer is not bounded and can grow unboundedly, leading to memory exhaustion.
 *  - Consumer threads may starve because there is no order in which consumers are notified of new elements.
 */
class UnboundedBuffer<T> {

    private val buffer: LinkedList<T> = LinkedList()

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    /**
     * Blocks the calling thread until an item is available.
      * @return the item or null if the timeout expires
      * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun take(timeout: Long, unit: TimeUnit): T? {
        guard.withLock {

            // Do we have an item?
            if (buffer.isNotEmpty()) {
                return buffer.removeFirst()
            }

            // Let's wait for it
            var remainingNanos = unit.toNanos(timeout)

            while (true) {
                remainingNanos = condition.awaitNanos(remainingNanos)

                // Do we have an item?
                if (buffer.isNotEmpty()) {
                    return buffer.removeFirst()
                }

                // Has the timeout expired?
                if (remainingNanos <= 0) {
                    return null
                }
            }
        }
    }

    /**
     * Adds an item to the buffer.
     * @param value the item to add
     */
    fun put(value: T): Unit = guard.withLock {
        buffer.addLast(value)
        condition.signal()
    }
}