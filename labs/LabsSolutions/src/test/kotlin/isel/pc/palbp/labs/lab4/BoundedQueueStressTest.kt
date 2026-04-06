package isel.pc.palbp.labs.lab4

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Stress tests for BoundedQueue to verify correctness under concurrent load.
 * Tests scenarios where multiple producers and consumers interact with the same BoundedQueue instance.
 */
class BoundedQueueStressTest {

    @Test
    fun `high contention producer-consumer stress test`() {
        // Arrange
        val queue = BoundedQueue<Int>(capacity = 10)
        val numProducers = 10
        val itemsPerProducer = 100
        val totalProducedItems = numProducers * itemsPerProducer
        val numConsumers = 10
        val itemsPerConsumer = 100
        val totalConsumedItems = numConsumers * itemsPerConsumer

        val producedCount = AtomicInteger(0)
        val consumedCount = AtomicInteger(0)
        val consumedItems = Collections.synchronizedList(mutableListOf<Int>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numProducers + numConsumers)

        // Act
        repeat(numProducers) { producerId ->
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerProducer) { itemId ->
                        val item = producerId * 1000 + itemId
                        queue.put(item)
                        producedCount.incrementAndGet()
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        repeat(numConsumers) {
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerConsumer) {
                        val item = queue.take()
                        consumedItems.add(item)
                        consumedCount.incrementAndGet()
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown()
        val operationsCompleted = completionLatch.await(30, TimeUnit.SECONDS)

        // Assert
        assertTrue(actual = operationsCompleted, "All operations should complete")
        assertEquals(expected = totalProducedItems, actual = producedCount.get(), "All items should be produced")
        assertEquals(expected = totalConsumedItems, actual = consumedCount.get(), "All items should be consumed")
        assertEquals(expected = totalConsumedItems, actual = consumedItems.size, "All items should be collected")
        assertEquals(expected = 0, actual = queue.size, "Queue should be empty after all items are consumed")
        assertEquals(expected = consumedItems.toSet().size, actual = consumedItems.size, "No duplicate items should be consumed")
    }

    @Test
    fun `many producers few consumers stress test`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 5) // Small capacity for more blocking
        val numProducers = 20
        val itemsPerProducer = 50
        val totalProducedItems = numProducers * itemsPerProducer
        val numConsumers = 2
        val itemsPerConsumer = totalProducedItems / numConsumers
        val consumedItems = Collections.synchronizedList(mutableListOf<String>())
        val startLatch = CountDownLatch(1)
        val producersCompletionLatch = CountDownLatch(numProducers)
        val consumersCompletionLatch = CountDownLatch(numConsumers)

        // Act
        repeat(numProducers) { producerId ->
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerProducer) { itemId ->
                        val item = "producer-$producerId-item-$itemId"
                        queue.put(item)
                    }
                } finally {
                    producersCompletionLatch.countDown()
                }
            }
        }

        repeat(numConsumers) {
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerConsumer) {
                        val item = queue.take()
                        consumedItems.add(item)
                    }
                } finally {
                    consumersCompletionLatch.countDown()
                }
            }
        }

        startLatch.countDown()
        val consumersCompleted = consumersCompletionLatch.await(30, TimeUnit.SECONDS)
        val producersCompleted = producersCompletionLatch.await(30, TimeUnit.SECONDS)

        // Assert
        assertTrue(actual = consumersCompleted, message = "All producers should complete")
        assertTrue(actual = producersCompleted, message = "All consumers should complete")

        assertEquals(expected = totalProducedItems, actual = consumedItems.size, "All produced items should be consumed")
        assertEquals(expected = consumedItems.toSet().size, actual = consumedItems.size, "No duplicate items should be consumed")
    }

    @Test
    fun `capacity boundary stress test`() {
        // Arrange
        val capacity = 3
        val queue = BoundedQueue<String>(capacity)
        val numProducers = 10
        val itemsPerProducer = 20
        val totalProducedItems = numProducers * itemsPerProducer
        val numConsumers = 10
        val itemsPerConsumer = totalProducedItems / numConsumers
        val consumedItems = Collections.synchronizedList(mutableListOf<String>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numProducers + numConsumers)

        // Act
        repeat(times = numProducers) { producerId ->
            thread {
                startLatch.await()
                try {
                    repeat(times = itemsPerProducer) { itemId ->
                        queue.put(value = "item-$producerId-$itemId")
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        repeat(times = numConsumers) {
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerConsumer) {
                        val item = queue.take()
                        consumedItems.add(item)
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown()
        val operationsCompleted = completionLatch.await(30, TimeUnit.SECONDS)

        // Assert
        assertTrue(actual = operationsCompleted, message = "All operations should complete")
        assertEquals(expected = totalProducedItems, actual = consumedItems.size, message = "All items should be consumed")
        assertEquals(expected = consumedItems.toSet().size, actual = consumedItems.size, message = "No duplicate items")
    }
}
