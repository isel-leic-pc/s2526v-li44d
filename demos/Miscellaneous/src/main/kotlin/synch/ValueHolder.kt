package palbp.demos.pc.isel.synch

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ValueHolder(private var value: Int? = null) {

    private val guard = ReentrantLock()
    private val condition = guard.newCondition()

    /**
     * Blocks the calling thread until the value is available.
     */
    fun getValue(): Int {
        guard.withLock {

            val observedValue: Int? = value
            if (observedValue != null) {
                return observedValue
            }

            while (true) {

                condition.await()

                val observedValue = value
                if (observedValue != null) {
                    return observedValue
                }

            }
        }
    }

    /**
     * Sets the value and notifies any waiting threads.
     */
    fun putValue(value: Int): Unit = guard.withLock {

        check(this.value != null) { "Value already set" }

        this.value = value
        condition.signalAll()
    }
}