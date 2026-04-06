package isel.pc.palbp.labs.lab4

import isel.pc.palbp.labs.waitForAllWaiting
import isel.pc.palbp.labs.waitForWaiting
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Functional tests for the BoundedQueue.
 */
class BoundedQueueFunctionalTest {

    @Test
    fun `put and take should work with empty queue`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 5)
        val expectedValue = "test-item"

        // Act
        queue.put(expectedValue)
        val actualValue = queue.take()

        // Assert
        assertEquals(expectedValue, actualValue)
        assertEquals(0, queue.size)
    }

    @Test
    fun `take should block until item is available`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 5)
        val expectedValue = "async-item"
        var actualValue: String? = null

        // Act
        val takerThread = thread {
            actualValue = queue.take()
        }

        // Wait for taker to be waiting
        takerThread.waitForWaiting(1000)

        queue.put(expectedValue)
        takerThread.join(1000)

        // Assert
        assertEquals(expectedValue, actualValue)
        assertEquals(0, queue.size)
    }

    @Test
    fun `put should block when queue is at capacity`() {
        // Arrange
        val capacity = 2
        val queue = BoundedQueue<String>(capacity)
        var putCompleted = false
        // Fill queue to capacity
        queue.put("item1")
        queue.put("item2")

        // Act

        // This put should block
        val putterThread = thread {
            queue.put("item3")
            putCompleted = true
        }

        // Wait for putter to be waiting
        putterThread.waitForWaiting(1000)

        // Take one item to free space
        val item = queue.take()
        putterThread.join(1000)

        // Assert
        assertEquals("item1", item) // FIFO order
        assertEquals(true, putCompleted)
        assertEquals(capacity, queue.size)
    }

    @Test
    fun `FIFO order should be maintained`() {
        // Arrange
        val queue = BoundedQueue<Int>(capacity = 10)
        val items = listOf(1, 2, 3, 4, 5)

        // Act
        items.forEach { queue.put(it) }
        val retrievedItems = mutableListOf<Int>()
        repeat(items.size) {
            queue.take()?.let { retrievedItems.add(it) }
        }

        // Assert
        assertEquals(items, retrievedItems)
        assertEquals(0, queue.size)
    }

    @Test
    fun `multiple producers should work correctly`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 100)
        val numProducers = 5
        val itemsPerProducer = 20
        val allItems = Collections.synchronizedList(mutableListOf<String>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numProducers)

        // Act
        repeat(times = numProducers) { producerId ->
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerProducer) { itemId ->
                        val item = "producer-$producerId-item-$itemId"
                        queue.put(item)
                        allItems.add(item)
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown()

        completionLatch.await()

        // Take all items
        val retrievedItems = mutableListOf<String>()
        repeat(numProducers * itemsPerProducer) {
            queue.take()?.let { retrievedItems.add(it) }
        }

        // Assert
        assertEquals(allItems.size, retrievedItems.size)
        // Each produced item should be retrieved exactly once
        assertEquals(allItems.toSet(), retrievedItems.toSet())
    }

    @Test
    fun `multiple consumers should work correctly`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 100)
        val numConsumers = 5
        val itemsPerConsumer = 20
        val totalItems = itemsPerConsumer * numConsumers
        val retrievedItems = Collections.synchronizedList(mutableListOf<String>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(numConsumers)

        // Pre-populate queue
        val expectedItems = (0 until totalItems).map { "item-$it" }
        expectedItems.forEach { queue.put(it) }

        // Partition the work among consumers
        repeat(times = numConsumers) {
            thread {
                startLatch.await()
                try {
                    repeat(itemsPerConsumer) {
                        queue.take()?.let { retrievedItems.add(it) }
                        Thread.yield() // Increase chance of interleaving
                    }
                } finally {
                    completionLatch.countDown()
                }
            }
        }

        startLatch.countDown()

        completionLatch.await()

        // Assert
        assertEquals(totalItems, retrievedItems.size)
        assertEquals(expectedItems.toSet(), retrievedItems.toSet())
    }

    @Test
    fun `producer-consumer pattern should work correctly`() {
        // Arrange
        val queue = BoundedQueue<Int>(capacity = 5)
        val numItems = 50
        val producedItems = Collections.synchronizedList(mutableListOf<Int>())
        val consumedItems = Collections.synchronizedList(mutableListOf<Int>())
        val startLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(2)

        // Act
        thread {
            startLatch.await()
            try {
                repeat(numItems) { i ->
                    queue.put(i)
                    producedItems.add(i)
                }
            } finally {
                completionLatch.countDown()
            }
        }

        thread {
            startLatch.await()
            try {
                repeat(numItems) {
                    queue.take()?.let { consumedItems.add(it) }
                }
            } finally {
                completionLatch.countDown()
            }
        }

        startLatch.countDown()
        completionLatch.await()

        // Assert
        assertEquals(numItems, producedItems.size)
        assertEquals(numItems, consumedItems.size)
        assertEquals(producedItems, consumedItems) // Should maintain FIFO order
    }

    @Test
    fun `queue with capacity 1 should work correctly`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 1)
        var putStep2Completed = false

        // Act:
        // The first put should succeed immediately
        queue.put("item1")

        // The second put should block
        val putterThread = thread {
            queue.put("item2")
            putStep2Completed = true
        }

        // Wait for putter to be waiting
        putterThread.waitForWaiting(1000)

        // Take first item
        val item1 = queue.take()

        // Now second put should complete
        putterThread.join(1000)

        // Take second item
        val item2 = queue.take()

        // Assert
        assertEquals(true, putStep2Completed)
        assertEquals("item1", item1)
        assertEquals("item2", item2)
        assertEquals(0, queue.size)
    }


    @Test
    fun `alternating put and take operations should work correctly`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 3)
        val operations = 20

        // Act & Assert
        repeat(operations) { i ->
            val item = "item-$i"
            queue.put(item)
            val retrieved = queue.take()
            assertEquals(item, retrieved, "Item $i should match")
        }
    }

    @Test
    fun `queue should handle null values correctly`() {
        // Arrange
        val queue = BoundedQueue<String?>(capacity = 5)

        // Act
        val nonNullItem = "not-null"
        queue.put(null)
        queue.put(nonNullItem)
        queue.put(null)

        val item1 = queue.take()
        val item2 = queue.take()
        val item3 = queue.take()
        assertEquals(null, item1)
        assertEquals(nonNullItem, item2)
        assertEquals(null, item3)
    }

    @Test
    fun `blocked takers should be notified when item becomes available`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 5)
        val numTakers = 3
        val results = Collections.synchronizedList(mutableListOf<String>())

        // Act
        val takers = (1..numTakers).map { takerId ->
            thread {
                queue.take()?.let { results.add("taker-$takerId-got-$it") }
            }
        }

        // Wait for all takers to be waiting
        takers.waitForAllWaiting(1000)

        // Put items one by one
        repeat(numTakers) { i ->
            queue.put("item-$i")
        }

        takers.forEach { it.join(1000) }

        // Assert
        assertEquals(numTakers, results.size)
        assertEquals(0, queue.size)
        // Each taker should have received exactly one item
        results.forEach { result ->
            assertNotNull(result)
        }
    }

    @Test
    fun `blocked putters should be notified when space becomes available`() {
        // Arrange
        val capacity = 2
        val queue = BoundedQueue<String>(capacity)
        val numPutters = 3
        val results = Collections.synchronizedList(mutableListOf<String>())

        // Fill queue to capacity
        queue.put("existing-1")
        queue.put("existing-2")

        // Act
        val putters = (1..numPutters).map { putterId ->
            thread {
                queue.put("putter-$putterId-item")
                results.add("putter-$putterId-completed")
            }
        }

        // Wait for all putters to be waiting
        putters.waitForAllWaiting(1000)

        // Take items to free space
        repeat(capacity + numPutters) {
            queue.take()
        }

        putters.forEach { it.join(1000) }

        // Assert
        assertEquals(numPutters, results.size)
        assertEquals(0, queue.size)
        results.forEach { result ->
            assertNotNull(result)
        }
    }

    @Test
    fun `take should return null after timeout if queue is empty`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 2)

        // Act
        val result = queue.take(timeout = 100)

        // Assert
        assertEquals(expected = null, actual = result)
        assertEquals(expected = 0, actual = queue.size)
    }

    @Test
    fun `put should return false after timeout if queue is full`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 1)
        queue.put("item1")

        // Act
        val result = queue.put("item2", timeout = 100)

        // Assert
        assertEquals(expected = false, actual = result)
        assertEquals(expected = 1, actual = queue.size)
    }

    @Test
    fun `take should throw InterruptedException if interrupted while waiting`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 1)
        var wasInterrupted = false
        val thread = thread {
            try {
                queue.take(timeout = 10000)
                // Should not reach here
                wasInterrupted = false
            } catch (_: InterruptedException) {
                wasInterrupted = true
            }
        }

        thread.waitForWaiting(timeoutMillis = 1000)

        // Act
        thread.interrupt()
        thread.join(1000)

        // Assert
        assertEquals(expected = true, actual = wasInterrupted)
        assertEquals(expected = 0, actual = queue.size)
    }

    @Test
    fun `put should throw InterruptedException if interrupted while waiting`() {
        // Arrange
        val queue = BoundedQueue<String>(capacity = 1)
        queue.put("item1")
        var wasInterrupted = false
        val thread = thread {
            try {
                queue.put(value = "item2", timeout = 10000)
                // Should not reach here
                wasInterrupted = false
            } catch (_: InterruptedException) {
                wasInterrupted = true
            }
        }

        thread.waitForWaiting(timeoutMillis = 1000)

        // Act
        thread.interrupt()
        thread.join(1000)

        // Assert
        assertEquals(expected = true, actual = wasInterrupted)
        assertEquals(expected = 1, actual = queue.size)
    }
}
