import palbp.demos.pc.isel.threadsafety.SafeLinkedList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SafeLinkedListTests {

    @Test
    fun `iterate on empty list succeeds`() {
        val sut = SafeLinkedList<Int>()
        var count = 0
        sut.forEach { _ -> count += 1 }
        assertEquals(expected = 0, actual = count)
    }

    @Test
    fun `iterate on non-empty list succeeds`() {
        val sut = SafeLinkedList<Int>()
        val element = 1
        val count = 10
        repeat(times = count) {
            sut.addLast(value = element)
        }
        assertEquals(expected = count, actual = sut.size)
        assertEquals(expected = element * count, actual = sut.sum())
    }

    @Test
    fun `addLast on empty list produces non-empty list`() {
        val sut = SafeLinkedList<Int>()
        val element = 2
        sut.addLast(value = element)
        assertEquals(expected = 1, actual = sut.size)
        assertEquals(expected = element, actual = sut.sum())
    }

    @Test
    fun `addLast on non-empty list adds element to the end`() {
        val sut = SafeLinkedList<Int>()
        val expectedLast = 2
        sut.addLast(value = 1)
        sut.addLast(value = expectedLast)

        var actualLast = 0
        sut.forEach { actualLast = it }
        assertEquals(expected = 2, actual = sut.size)
        assertEquals(expected = expectedLast, actual = actualLast)
    }

    @Test
    fun `removeFirst on empty list throws exception`() {
        val sut = SafeLinkedList<Int>()
        assertFailsWith<NoSuchElementException> { sut.removeFirst() }
    }

    @Test
    fun `removeFirst on non-empty list removes first element`() {
        val sut = SafeLinkedList<Int>()
        val expectedFirst = 10
        sut.addLast(value = expectedFirst)
        sut.addLast(value = 20)
        val actualFirst = sut.removeFirst()
        assertEquals(expected = 1, actual = sut.size)
        assertEquals(expected = expectedFirst, actual = actualFirst)
    }
}