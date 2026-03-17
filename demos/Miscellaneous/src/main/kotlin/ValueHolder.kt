package palbp.demos.pc.isel

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ValueHolder(var value: Int? = null) {

    private val guard = ReentrantLock()

    /**
     * Blocks the calling thread until the value is available.
     */
    fun getValue(): Int = guard.withLock {

        val observedValue = value
        if (observedValue != null) {
            return observedValue
        }

        TODO("wait for value")
    }

    /**
     * Sets the value and notifies any waiting threads.
     */
    fun putValue(value: Int): Unit = guard.withLock {
        if (this.value != null) {
            throw IllegalStateException("Value already set")
        }

        this.value = value
        TODO("notify all waiting threads")
    }
}