package palbp.demos.pc.isel.synch

import java.util.LinkedList
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
 */
class UnboundedQueue<T> {

    private val buffer: LinkedList<T> = LinkedList()


    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    private data class Request<T>(var value: T? = null)
    private val requests: LinkedList<Request<T>> = LinkedList()

    fun take(): T {
        guard.withLock {

            if (buffer.isNotEmpty()) {
                return buffer.removeFirst()
            }

            val myRequest = Request<T>()
            requests.addLast(myRequest)

            while (true) {
                condition.await()

                val receivedItem = myRequest.value
                if (receivedItem != null) {
                    return receivedItem
                }
            }
        }
    }

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