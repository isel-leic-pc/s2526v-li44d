package isel.pc.palbp.labs.lab4

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ValueHolder<T>(private var value: T? = null) {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    /**
     * Indefinitely blocks the calling thread until the value is available.
     * @return the value
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Throws(InterruptedException::class)
    fun getValue(): T {
        guard.withLock {

            // Return if value is already available
            value?.let { return it }

            while (true) {

                condition.await()

                // Return if value is already available
                value?.let { return it }
            }
        }
    }

    @Throws(InterruptedException::class)
    fun getValue(timeout: Long, unit: TimeUnit): T? {
        guard.withLock {

            // Return if value is already available
            value?.let { return it }

            var remainingNanos = unit.toNanos(timeout)

            while (true) {
                remainingNanos = condition.awaitNanos(remainingNanos)

                // Return if value is already available
                value?.let { return it }

                // Return if timeout expired
                if (remainingNanos <= 0) {
                    return null
                }
            }
        }
    }

    /**
     * Sets the value and notifies any waiting threads.
     */
    fun putValue(value: T): Unit = guard.withLock {

        check(this.value == null) { "Value already set" }

        this.value = value
        condition.signalAll()
    }
}