package isel.pc.palbp.labs.lab4

import isel.pc.palbp.labs.isNotWaiting
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Stress tests for ValueHolder to verify correctness under a concurrent load.
 * Tests scenarios where multiple threads interact with the same ValueHolder instance.
 */
class ValueHolderStressTest {

    @Test
    @Timeout(10)
    fun `multiple waiters should all receive value when set`() {
        // Arrange
        val numWaiters = 100
        val expectedValue = "stress-value"
        val holder = ValueHolder<String>()
        val results = Collections.synchronizedList(mutableListOf<String>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numWaiters)

        // Act
        val waiters = (1..numWaiters).map {
            thread {
                startLatch.await() // Synchronized start
                try {
                    val value = holder.getValue()
                    results.add(value)
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown() // Start all waiters
        
        // Wait for all waiters to be in waiting state
        waiters.forEach { waiter ->
            while (waiter.isNotWaiting()) { Thread.yield() }
        }
        
        holder.putValue(expectedValue)

        // Wait for all waiters to complete
        assertTrue(completionLatch.await(5, TimeUnit.SECONDS), "All waiters should complete within timeout")
        waiters.forEach { it.join(100) }

        // Assert
        assertEquals(numWaiters, results.size, "All waiters should receive the value")
        results.forEach { assertEquals(expectedValue, it) }
    }

    @Test
    @Timeout(10)
    fun `interrupt handling under stress`() {
        // Arrange
        val numOperations = 50
        val interruptedCount = AtomicInteger(0)
        val startLatch = CountDownLatch(1)
        val readyLatch = CountDownLatch(numOperations)

        // Act
        val operations = (1..numOperations).map {
            thread {
                startLatch.await()
                readyLatch.countDown()
                try {
                    val holder = ValueHolder<String>()
                    holder.getValue() // This will block indefinitely
                } catch (e: InterruptedException) {
                    interruptedCount.incrementAndGet()
                }
            }
        }

        startLatch.countDown()
        assertTrue(readyLatch.await(2, TimeUnit.SECONDS), "All threads should be ready")
        
        // Wait for threads to enter waiting state
        operations.forEach { operation ->
            while (operation.isNotWaiting()) { Thread.yield() }
        }

        // Interrupt all threads
        operations.forEach { it.interrupt() }
        operations.forEach { it.join(500) }

        // Assert
        assertEquals(numOperations, interruptedCount.get(), "All operations should be interrupted")
    }

    @Test
    @Timeout(10)
    fun `mixed getValue and getValue with timeout operations`() {
        // Arrange
        val holder = ValueHolder<String>()
        val expectedValue = "mixed-value"
        val numBlockingGetters = 25
        val numTimedGetters = 25
        val blockingResults = Collections.synchronizedList(mutableListOf<String>())
        val timedResults = Collections.synchronizedList(mutableListOf<String?>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numBlockingGetters + numTimedGetters)

        // Act - Start blocking getters
        val blockingGetters = (1..numBlockingGetters).map {
            thread {
                startLatch.await()
                try {
                    val value = holder.getValue()
                    blockingResults.add(value)
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        // Start timed getters with long timeout
        val timedGetters = (1..numTimedGetters).map {
            thread {
                startLatch.await()
                try {
                    val value = holder.getValue(10, TimeUnit.SECONDS)
                    timedResults.add(value)
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown()

        // Wait for all threads to be waiting
        (blockingGetters + timedGetters).forEach { getter ->
            while (getter.isNotWaiting()) { Thread.yield() }
        }

        // Set the value
        holder.putValue(expectedValue)

        // Wait for all to complete
        assertTrue(completionLatch.await(5, TimeUnit.SECONDS), "All operations should complete")
        (blockingGetters + timedGetters).forEach { it.join(100) }

        // Assert
        assertEquals(numBlockingGetters, blockingResults.size, "All blocking getters should succeed")
        assertEquals(numTimedGetters, timedResults.size, "All timed getters should succeed")
        blockingResults.forEach { assertEquals(expectedValue, it) }
        timedResults.forEach { assertEquals(expectedValue, it) }
    }
}
