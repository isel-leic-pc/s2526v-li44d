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

class SafeLinkedListFunctionalityTests {

    // ========== Basic Functionality Tests ==========

    @Test
    fun `empty list has size zero`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        
        // Act
        val size = list.size
        
        // Assert
        assertEquals(0, size)
    }

    @Test
    fun `adding single element increases size to one`() {
        // Arrange
        val list = SafeLinkedList<String>()
        
        // Act
        list.addLast("hello")
        
        // Assert
        assertEquals(1, list.size)
    }

    @Test
    fun `removeFirst returns the correct element`() {
        // Arrange
        val expectedElement = "hello"
        val list = SafeLinkedList<String>()
        list.addLast(expectedElement)
        
        // Act
        val element = list.removeFirst()
        
        // Assert
        assertEquals(expectedElement, element)
    }

    @Test
    fun `removeFirst on empty list throws NoSuchElementException`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        
        // Act & Assert
        assertThrows<NoSuchElementException> { list.removeFirst() }
    }

    // ========== FIFO Ordering Tests ==========

    @Test
    fun `elements are removed in FIFO order`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val expectedOrder = listOf(1, 2, 3, 4, 5)
        expectedOrder.forEach { list.addLast(it) }
        
        // Act
        val actualOrder = mutableListOf<Int>()
        repeat(expectedOrder.size) {
            actualOrder.add(list.removeFirst())
        }
        
        // Assert
        assertEquals(expectedOrder, actualOrder)
    }

    @Test
    fun `size reflects number of elements added`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val elementsToAdd = 5
        
        // Act
        repeat(elementsToAdd) { list.addLast(it) }
        
        // Assert
        assertEquals(elementsToAdd, list.size)
    }

    @Test
    fun `size reflects number of elements remaining after removals`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val addedElements = 5
        repeat(addedElements) { list.addLast(it) }
        val elementsToRemove = 3
        
        // Act
        repeat(elementsToRemove) { list.removeFirst() }
        
        // Assert
        assertEquals(expected = addedElements - elementsToRemove, actual = list.size)
    }

    // ========== Iterator Basic Functionality Tests ==========

    @Test
    fun `iterator on empty list has no elements`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        
        // Act
        val iterator = list.iterator()
        
        // Assert
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `iterator next() on empty list throws NoSuchElementException`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val iterator = list.iterator()
        
        // Act & Assert
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `iterator traverses all elements in insertion order`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val expectedValues = listOf(1, 2, 3, 4, 5)
        expectedValues.forEach { list.addLast(it) }
        
        // Act
        val actualValues = mutableListOf<Int>()
        for (value in list) {
            actualValues.add(value)
        }
        
        // Assert
        assertEquals(expectedValues, actualValues)
    }

    @Test
    fun `iterator hasNext returns false after all elements consumed`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        list.addLast(1)
        val iterator = list.iterator()
        
        // Act
        iterator.next() // consume the only element
        
        // Assert
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `iterator next() throws when hasNext returns false`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        list.addLast(1)
        val iterator = list.iterator()
        iterator.next() // consume the only element
        
        // Act & Assert
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    // ========== Snapshot Semantics Tests ==========

    @Test
    fun `iterator does not see elements added after creation`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val initialElements = listOf(1, 2)
        initialElements.forEach { list.addLast(it) }
        val iterator = list.iterator()
        
        // Act
        list.addLast(3)
        list.addLast(4)
        val iteratedValues = mutableListOf<Int>()
        while (iterator.hasNext()) {
            iteratedValues.add(iterator.next())
        }
        
        // Assert
        assertEquals(initialElements, iteratedValues)
    }

    @Test
    fun `iterator sees all elements present at creation despite removals`() {
        // Arrange
        val list = SafeLinkedList<Int>()
        val originalElements = listOf(1, 2, 3, 4, 5)
        originalElements.forEach { list.addLast(it) }
        val iterator = list.iterator()
        
        // Act
        list.removeFirst()
        list.removeFirst()
        val iteratedValues = mutableListOf<Int>()
        while (iterator.hasNext()) {
            iteratedValues.add(iterator.next())
        }
        
        // Assert
        assertEquals(originalElements, iteratedValues)
    }
}
