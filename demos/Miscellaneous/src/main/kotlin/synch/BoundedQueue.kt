package palbp.demos.pc.isel.synch

import java.util.*
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
class BoundedQueue<T>(val capacity: Int = 1024) {

    private val buffer: LinkedList<T> = LinkedList()

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request<T>(var value: T? = null)
    private val requests: LinkedList<Request<T>> = LinkedList()

    /**
     * Returns the current number of elements in the queue.
     * Thread-safe: acquires the guard lock to read the buffer size.
     */
    val size: Int
        get() = guard.withLock { buffer.size }

    /**
     * Takes an element from the queue, blocking if the queue is empty.
     * @return the element
     */
    fun take(): T {
        guard.withLock {

            // Can proceed? (Fast path)
            if (buffer.isNotEmpty()) {
                val item = buffer.removeFirst()
                // Maybe signal blocked producer
                if (requests.isNotEmpty()) {
                    val prodReq = requests.removeFirst()
                    val item = prodReq.value
                    prodReq.value = null
                    checkNotNull(item)
                    buffer.addLast(item)
                    condition.signalAll()
                }
                return item
            }

            val myRequest = Request<T>()
            requests.addLast(myRequest)

            while (true) {
                condition.await()

                val myValue = myRequest.value
                if (myValue != null) {
                    return myValue
                }
            }
        }
    }

    /**
     * Puts an element into the queue, blocking if the queue is full.
     * @param value the element to be added to the queue
     */
    fun put(value: T): Unit {
        guard.withLock {

            // Can proceed? (Fast path)
            if (buffer.size != capacity) {
                // Will a consumer be signaled?

                if (requests.isNotEmpty()) {
                    // Buffer must be empty.
                    requests.removeFirst().value = value
                    condition.signalAll()
                } else {
                    buffer.addLast(value)
                }
                return
            }

            val myRequest = Request(value)
            requests.addLast(myRequest)

            while (true) {
                condition.await()

                // Can proceed? (Slow path)
                if (myRequest.value == null) {
                    return
                }
            }
        }
    }
}