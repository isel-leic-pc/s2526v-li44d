package isel.pc.palbp.labs.lab3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Collections
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class SafeLinkedListSafetyTests {

    @Test
    fun `concurrent addLast operations maintain correct size`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val numThreads = 4
        val elementsPerThread = 100
        val ready = CyclicBarrier(numThreads)
        val done = CountDownLatch(numThreads)
        
        // Act
        repeat(times = numThreads) { idx ->
            thread {
                ready.await()
                repeat(elementsPerThread) { i ->
                    list.addLast(idx * 1000 + i)
                }
                done.countDown()
            }
        }
        done.await()

        // Assert
        assertEquals(numThreads * elementsPerThread, list.size)
    }

    @Test
    fun `concurrent removeFirst operations do not produce duplicates`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val totalElements = 1000
        repeat(totalElements) { list.addLast(it) }
        val removedElements = Collections.synchronizedList(mutableListOf<Int>())
        val numThreads = 4
        val ready = CyclicBarrier(numThreads)
        val done = CountDownLatch(numThreads)
        
        // Act: starts threads that remove elements concurrently until the list is empty
        repeat(numThreads) {
            thread {
                try {
                    ready.await()
                    while (true) {
                        val element = list.removeFirst()
                        removedElements.add(element)
                    }
                }
                catch (_: NoSuchElementException) { }
                done.countDown()
            }
        }
        done.await()

        // Assert that there are no duplicates in removedElements
        assertEquals(removedElements.size, removedElements.toSet().size)
    }

    @Test
    fun `concurrent removeFirst operations consume all elements exactly once`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val totalElements = 1000
        repeat(totalElements) { list.addLast(it) }
        val removedElements = Collections.synchronizedList(mutableListOf<Int>())
        val numThreads = 4
        val ready = CyclicBarrier(numThreads)
        val done = CountDownLatch(numThreads)

        // Act: starts threads that remove elements concurrently until the list is empty
        repeat(numThreads) {
            thread {
                ready.await()
                try {
                    while (true) {
                        val element = list.removeFirst()
                        removedElements.add(element)
                    }
                }
                catch (_: NoSuchElementException) { }
                done.countDown()
            }
        }
        done.await()

        // Assert
        assertEquals(totalElements, removedElements.size)
    }

    @Test
    fun `concurrent mixed operations maintain size consistency`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val operationsCount = 1000
        val threadCount = 4
        val operationsPerThread = operationsCount / threadCount
        val addCount = AtomicInteger(0)
        val removeCount = AtomicInteger(0)
        val ready = CyclicBarrier(threadCount)
        val done = CountDownLatch(threadCount)
        
        // Act
        repeat(times = threadCount) { threadId ->
            thread {
                ready.await()
                repeat(times = operationsPerThread) { i ->
                    if ((threadId + i) % 2 == 0) {
                        list.addLast(threadId * 1000 + i)
                        addCount.incrementAndGet()
                    } else {
                        try {
                            list.removeFirst()
                            removeCount.incrementAndGet()
                        } catch (_: NoSuchElementException) {
                            // Expected when list is empty
                        }
                    }
                }
                done.countDown()
            }
        }
        done.await()
        
        // Assert that the size is consistent with the number of operations
        assertEquals(expected = addCount.get() - removeCount.get(), actual = list.size)
    }
}
