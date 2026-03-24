package palbp.demos.pc.isel.synch

import java.util.LinkedList
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


    fun take(): T {
        guard.withLock {

            if (buffer.isNotEmpty()) {
                return buffer.removeFirst()
            }

            while (true) {
                condition.await()

                if (buffer.isNotEmpty()) {
                    return buffer.removeFirst()
                }
            }
        }
    }

    fun put(value: T): Unit = guard.withLock {

        buffer.addLast(value)
        condition.signal()
    }
}