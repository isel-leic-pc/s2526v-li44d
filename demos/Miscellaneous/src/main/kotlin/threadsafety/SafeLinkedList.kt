package palbp.demos.pc.isel.threadsafety

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Thread-safe linked list implementation.
 */
class SafeLinkedList<T> : Iterable<T> {

    /**
     * The list node.
     * @param value the value stored in the node
     * @param next the next node in the list
     */
    private data class Node<T>(val value: T, var next: Node<T>? = null)

    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var elementCount = 0
    private val guard = ReentrantLock()

    /**
     * Adds the given value to the end of the list.
     * @param value the value to be added
     */
    fun addLast(value: T) {
        val newNode = Node(value, next = null)
        guard.withLock {
            elementCount += 1
            val observedTail = tail
            if (observedTail != null) {
                observedTail.next = newNode
                tail = newNode
            }
            if (head == null) {
                head = newNode
                tail = newNode
            }
        }
    }

    /**
     * Removes the first element from the list.
     * @throws NoSuchElementException if the list is empty
     * @return the first element in the list
     */
    @Throws(NoSuchElementException::class)
    fun removeFirst(): T = guard.withLock {
        val observedHead = head ?: throw NoSuchElementException()
        head = observedHead.next
        elementCount -= 1
        observedHead.value
    }

    /**
     * Returns the number of elements in the list.
     */
    val size: Int
        get() = guard.withLock { elementCount }

    /**
     * Returns an iterator over the elements in the list.
     */
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var current: Node<T>? = guard.withLock { head }

        /**
         * Returns the next element in the list.
         * @throws NoSuchElementException if there are no more elements
         * @return the next element in the list
         */
        @Throws(NoSuchElementException::class)
        override fun next(): T = guard.withLock {
            val observedCurrent = current ?: throw NoSuchElementException()
            observedCurrent.value.also {
                current = observedCurrent.next
            }
        }

        /**
         * Cheks if there are more elements in the list.
         * @return true if there are more elements in the list, false otherwise
         */
        override fun hasNext(): Boolean = guard.withLock { current != null }
    }
}