package palbp.demos.pc.isel.synch

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Supports the producer-consumer pattern.
 * FIFO delivery is guaranteed.
 * The synchronizer has unlimited capacity.
 *
 * Limitations:
 *  - The queue is not bounded and can grow unboundedly, leading to memory exhaustion.
 *  - Too many context-switches may occur when waiting for an item to be available.
 *
 *  Question to ponder about:
 *  - Is it possible for a consumer thread to simultaneously be signaled and canceled?
 *  - Is it possible for the timeout to expire and the thread be signaled at the same time? What happens in that case?
 */
class UnboundedQueue<T> {

    /**
     * The buffer for the items
     */
    private val buffer: LinkedList<T> = LinkedList()


    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    /**
     * The list of threads waiting for an item to be available.
     */
    private val requests: LinkedList<Request<T>> = LinkedList()

    /**
     * Represents a request for an item. The value is set by the producer thread when an item is available.
     */
    private data class Request<T>(var value: T? = null)


    /**
     * Blocks the calling thread until an item is available.
     * @return the item or null if the timeout expires
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun take(timeout: Long, unit: TimeUnit): T? {
        guard.withLock {

            if (buffer.isNotEmpty()) {
                return buffer.removeFirst()
            }

            val myRequest = Request<T>()
            requests.addLast(myRequest)
            var timeLeftNanos = unit.toNanos(timeout)

            try {
                while (true) {
                    timeLeftNanos = condition.awaitNanos(timeLeftNanos)

                    // Have we received an item?
                    val receivedItem = myRequest.value
                    if (receivedItem != null) {
                        return receivedItem
                    }

                    // Has the timeout expired?
                    if (timeLeftNanos <= 0) {
                        return null
                    }
                }
            }
            catch (ie: InterruptedException) {
                // Remove the current thread from the list of waiting threads
                requests.remove(myRequest)
                throw ie
            }
        }
    }

    /**
     * Adds an item to the queue.
     * @param value the item to add
     */
    fun put(value: T): Unit = guard.withLock {
        if (requests.isNotEmpty()) {
            requests.removeFirst().value = value
            condition.signalAll()
        }
        else {
            buffer.addLast(value)
        }
    }
}