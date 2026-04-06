package isel.pc.palbp.labs.lab4

import isel.pc.palbp.labs.isNotWaiting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Functional tests for ValueHolder.
 * Tests basic functionality and edge cases.
 */
class ValueHolderFunctionalTest {

    @Test
    fun `getValue should return value when already set`() {
        // Arrange
        val expectedValue = "test-value"
        val holder = ValueHolder(expectedValue)

        // Act
        val actualValue = holder.getValue()

        // Assert
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `getValue should block until value is set by another thread`() {
        // Arrange
        val expectedValue = "async-value"
        val holder = ValueHolder<String>()
        var actualValue: String? = null

        // Act
        val getterThread = thread {
            actualValue = holder.getValue()
        }

        // Ensure getter is waiting with a simple spin wait (adequate for this test but usually not recommended)
        while (getterThread.state != Thread.State.WAITING) { Thread.yield() }

        // Put the value and unblock the getter
        holder.putValue(expectedValue)
        // Wait for the getter to finish
        getterThread.join(1000)

        // Assert
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `getValue with timeout should return null when timeout expires`() {
        // Arrange
        val holder = ValueHolder<String>()
        val timeoutMs = 200L

        // Act
        val startTime = System.currentTimeMillis()
        val result = holder.getValue(timeoutMs, TimeUnit.MILLISECONDS)
        val elapsedTime = System.currentTimeMillis() - startTime

        // Assert
        assertNull(result)
        assert(elapsedTime >= timeoutMs) { "Should wait at least $timeoutMs ms, but waited only $elapsedTime ms" }
        assert(elapsedTime < timeoutMs + 100) { "Should not wait much longer than $timeoutMs ms, but waited $elapsedTime ms" }
    }

    @Test
    fun `getValue with timeout should return value when set before timeout`() {
        // Arrange
        val expectedValue = "timeout-value"
        val holder = ValueHolder<String>()
        val timeoutMs = 1000L
        var actualValue: String? = null

        // Act
        val getterThread = thread {
            actualValue = holder.getValue(timeoutMs, TimeUnit.MILLISECONDS)
        }

        // Ensure getter is waiting with a simple spin wait (adequate for this test but usually not recommended)
        while (getterThread.isNotWaiting()) { Thread.yield() }

        // Put the value and unblock the getter
        holder.putValue(expectedValue)
        // Wait for the getter to finish
        getterThread.join(1000)

        // Assert
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `getValue with timeout should return value immediately when already set`() {
        // Arrange
        val expectedValue = "immediate-value"
        val holder = ValueHolder(expectedValue)

        // Act
        val startTime = System.currentTimeMillis()
        val result = holder.getValue(1000, TimeUnit.MILLISECONDS)
        val elapsedTime = System.currentTimeMillis() - startTime

        // Assert
        assertEquals(expectedValue, result)
        assert(elapsedTime < 100) { "Should return immediately, but took $elapsedTime ms" }
    }

    @Test
    fun `putValue should set value and notify waiting threads`() {
        // Arrange
        val expectedValue = "notify-value"
        val holder = ValueHolder<String>()
        val results = Collections.synchronizedList(mutableListOf<String>())
        val numGetters = 3

        // Act
        val getters = (1..numGetters).map {
            thread {
                val value = holder.getValue()
                results.add(value)
            }
        }

        // Ensure getters are waiting with a simple spin wait (adequate for this test but usually not recommended)
        getters.forEach {
            while (it.isNotWaiting()) { Thread.yield() }
        }

        // Put the value and unblock the getters
        holder.putValue(expectedValue)

        // Wait for all getters to finish
        getters.forEach { it.join(1000) }

        // Assert
        assertEquals(expected = numGetters, actual = results.size)
        results.forEach { assertEquals(expectedValue, it) }
    }

    @Test
    fun `putValue should throw exception when value already set`() {
        // Arrange
        val holder = ValueHolder("initial-value")

        // Act & Assert
        assertThrows<IllegalStateException> {
            holder.putValue("second-value")
        }
    }

    @Test
    fun `getValue should throw InterruptedException when thread is interrupted`() {
        // Arrange
        val holder = ValueHolder<String>()
        var exception: Exception? = null

        // Act
        val getterThread = thread {
            try {
                holder.getValue()
            } catch (e: InterruptedException) {
                exception = e
            }
        }

        // Ensure getter is waiting with a simple spin wait (adequate for this test but usually not recommended)
        while (getterThread.isNotWaiting()) { Thread.yield() }

        // Interrupt the getter thread and wait for it to finish
        getterThread.interrupt()
        getterThread.join(500)

        // Assert
        assert(exception is InterruptedException) { "Expected InterruptedException but got $exception" }
    }

    @Test
    fun `getValue with timeout should throw InterruptedException when thread is interrupted`() {
        // Arrange
        val holder = ValueHolder<String>()
        var exception: Exception? = null

        // Act
        val getterThread = thread {
            try {
                holder.getValue(5000, TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                exception = e
            }
        }

        // Ensure getter is waiting with a simple spin wait (adequate for this test but usually not recommended)
        while (getterThread.isNotWaiting()) { Thread.yield() }

        // Interrupt the getter thread and wait for it to finish
        getterThread.interrupt()
        getterThread.join(500)

        // Assert
        assert(exception is InterruptedException) { "Expected InterruptedException but got $exception" }
    }

    @Test
    fun `getValue with zero timeout should return null immediately`() {
        // Arrange
        val holder = ValueHolder<String>()

        // Act
        val startTime = System.currentTimeMillis()
        val result = holder.getValue(0, TimeUnit.MILLISECONDS)
        val elapsedTime = System.currentTimeMillis() - startTime

        // Assert
        assertNull(result)
        assert(elapsedTime < 50) { "Should return immediately, but took $elapsedTime ms" }
    }

    @Test
    fun `getValue with negative timeout should return null immediately`() {
        // Arrange
        val holder = ValueHolder<String>()

        // Act
        val startTime = System.currentTimeMillis()
        val result = holder.getValue(-100, TimeUnit.MILLISECONDS)
        val elapsedTime = System.currentTimeMillis() - startTime

        // Assert
        assertNull(result)
        assert(elapsedTime < 50) { "Should return immediately, but took $elapsedTime ms" }
    }

    @Test
    fun `multiple getValue calls should all return the same value`() {
        // Arrange
        val expectedValue = "shared-value"
        val holder = ValueHolder<String>()
        val results = Collections.synchronizedList(mutableListOf<String>())
        val getterThreadCount = 5

        // Act
        val getterThreads = (1..getterThreadCount).map {
            thread {
                val value = holder.getValue()
                results.add(value)
            }
        }

        getterThreads.forEach {
            while (it.isNotWaiting()) Thread.yield()
        }

        holder.putValue(expectedValue)

        getterThreads.forEach { it.join(500) }

        // Assert
        assertEquals(getterThreadCount, results.size)
        results.forEach { assertEquals(expectedValue, it) }
    }

    @Test
    fun `timeout should work correctly with proper timing`() {
        // Arrange
        val holder = ValueHolder<String>()
        val timeoutMs = 200L

        // Act - Test timeout precision multiple times
        repeat(3) {
            val startTime = System.currentTimeMillis()
            val result = holder.getValue(timeoutMs, TimeUnit.MILLISECONDS)
            val elapsedTime = System.currentTimeMillis() - startTime

            // Assert
            assertNull(result, "Should timeout on attempt ${it + 1}")
            assert(elapsedTime >= timeoutMs - 50) { 
                "Should wait close to timeout on attempt ${it + 1}: waited ${elapsedTime}ms" 
            }
            assert(elapsedTime <= timeoutMs + 100) { 
                "Should not exceed timeout by much on attempt ${it + 1}: waited ${elapsedTime}ms" 
            }
        }
    }
}
