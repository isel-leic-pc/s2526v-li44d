package isel.pc.palbp.labs.lab3

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Thread-safe linked list implementation.
 */
class SafeLinkedList<T> : Iterable<T> {

    /**
     * The list node.
     */
    private data class Node<T>(val value: T?) {

        var next: Node<T> = this
        var prev: Node<T> = this
    }

    private var sentinel: Node<T> = Node(null)

    private var elementCount = 0
    private val guard = ReentrantLock()

    /**
     * Adds the given value to the end of the list.
     * @param value the value to be added
     */
    fun addLast(value: T) {
        guard.withLock {
            val newNode = Node(value)
            elementCount += 1
            // Insert between last node and sentinel
            newNode.prev = sentinel.prev
            newNode.next = sentinel
            sentinel.prev.next = newNode
            sentinel.prev = newNode
        }
    }

    /**
     * Removes the first element from the list.
     * @throws NoSuchElementException if the list is empty
     * @return the first element in the list
     */
    @Throws(NoSuchElementException::class)
    fun removeFirst(): T = guard.withLock {
        if (elementCount == 0) throw NoSuchElementException()
        val toRemove = sentinel.next
        sentinel.next = toRemove.next
        toRemove.next.prev = sentinel
        elementCount -= 1
        toRemove.value ?: throw NoSuchElementException()
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
        private var current: Node<T>
        private val last: Node<T>

        init {
            guard.withLock {
                current = sentinel.next
                last = sentinel.prev
            }
        }

        /**
         * Returns the next element in the list.
         * @throws NoSuchElementException if there are no more elements
         * @return the next element in the list
         */
        @Throws(NoSuchElementException::class)
        override fun next(): T = guard.withLock {
            if (!hasNext()) throw NoSuchElementException()
            checkNotNull(current.value).also { current = current.next }
        }

        /**
         * Checks if there are more elements in the list.
         * @return true if there are more elements in the list, false otherwise
         */
        override fun hasNext(): Boolean = guard.withLock { current.prev !== last }
    }
}