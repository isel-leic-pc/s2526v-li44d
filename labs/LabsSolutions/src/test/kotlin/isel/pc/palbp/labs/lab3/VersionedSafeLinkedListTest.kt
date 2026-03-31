package isel.pc.palbp.labs.lab3

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.util.ConcurrentModificationException

class VersionedSafeLinkedListTest {

    // ========== Basic Functionality Tests ==========

    @Test
    fun `empty list has size zero`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()

        // Act
        val size = list.size

        // Assert
        assertEquals(0, size)
    }

    @Test
    fun `adding single element increases size to one`() {
        // Arrange
        val list = VersionedSafeLinkedList<String>()

        // Act
        list.addLast("hello")

        // Assert
        assertEquals(1, list.size)
    }

    @Test
    fun `removeFirst returns the correct element`() {
        // Arrange
        val list = VersionedSafeLinkedList<String>()
        list.addLast("hello")

        // Act
        val element = list.removeFirst()

        // Assert
        assertEquals("hello", element)
    }

    @Test
    fun `elements are removed in FIFO order`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
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
    fun `iterator traverses all elements in insertion order`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
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

    // ========== Fail-Fast Behavior Tests ==========

    @Test
    fun `iterator throws ConcurrentModificationException when list is modified after creation`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        val iterator = list.iterator()
        
        // Act
        list.addLast(3) // Modify list after iterator creation
        
        // Assert
        assertThrows<ConcurrentModificationException> {
            iterator.hasNext()
        }
    }

    @Test
    fun `iterator throws ConcurrentModificationException on next() after modification`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        val iterator = list.iterator()
        
        // Act
        list.removeFirst() // Modify list after iterator creation
        
        // Assert
        assertThrows<ConcurrentModificationException> {
            iterator.next()
        }
    }

    @Test
    fun `iterator works correctly when no modifications occur`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        list.addLast(3)
        
        // Act
        val iterator = list.iterator()
        val values = mutableListOf<Int>()
        while (iterator.hasNext()) {
            values.add(iterator.next())
        }
        
        // Assert
        assertEquals(listOf(1, 2, 3), values)
    }

    @Test
    fun `iterator fails fast on addLast modification`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        val iterator = list.iterator()
        
        // Act
        list.addLast(2) // Structural modification
        
        // Assert
        assertThrows<ConcurrentModificationException> {
            iterator.hasNext()
        }
    }

    @Test
    fun `iterator fails fast on removeFirst modification`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        val iterator = list.iterator()
        
        // Act
        list.removeFirst() // Structural modification
        
        // Assert
        assertThrows<ConcurrentModificationException> {
            iterator.next()
        }
    }

    @Test
    fun `multiple iterators created before modifications work independently`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        val iterator1 = list.iterator()
        val iterator2 = list.iterator()
        
        // Act & Assert
        // Both iterators should work since they were created before any modifications
        assertTrue(iterator1.hasNext())
        assertTrue(iterator2.hasNext())
        assertEquals(1, iterator1.next())
        assertEquals(1, iterator2.next())
    }

    @Test
    fun `iterator created after modification sees updated state`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        
        // Act
        list.addLast(3) // Modify list
        val iterator = list.iterator() // Create iterator after modification
        val values = mutableListOf<Int>()
        while (iterator.hasNext()) {
            values.add(iterator.next())
        }
        
        // Assert
        assertEquals(listOf(1, 2, 3), values) // Should see all elements including the added one
    }

    @Test
    fun `iterator fails immediately on first operation after modification`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        val iterator = list.iterator()
        
        // Act
        list.addLast(3) // Modify list
        
        // Assert
        // Should fail on the first operation (hasNext or next)
        assertThrows<ConcurrentModificationException> {
            iterator.hasNext()
        }
    }

    @Test
    fun `partial iteration then modification causes failure on continuation`() {
        // Arrange
        val list = VersionedSafeLinkedList<Int>()
        list.addLast(1)
        list.addLast(2)
        list.addLast(3)
        val iterator = list.iterator()
        
        // Act
        assertEquals(1, iterator.next()) // Get first element successfully
        list.addLast(4) // Modify list
        
        // Assert
        assertThrows<ConcurrentModificationException> {
            iterator.next() // Should fail on subsequent call
        }
    }
}
