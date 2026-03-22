package palbp.demos.pc.isel.imageviewer.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FilterConfigDomainTest {

    @Test
    fun `brightness delta parseOrThrow accepts minimum boundary`() {
        val delta = BrightnessDelta.parseOrThrow(BrightnessDelta.MinValue)

        assertEquals(BrightnessDelta.MinValue, delta.value)
    }

    @Test
    fun `brightness delta parseOrThrow accepts maximum boundary`() {
        val delta = BrightnessDelta.parseOrThrow(BrightnessDelta.MaxValue)

        assertEquals(BrightnessDelta.MaxValue, delta.value)
    }

    @Test
    fun `brightness delta parseOrThrow rejects below minimum`() {
        assertFailsWith<IllegalArgumentException> {
            BrightnessDelta.parseOrThrow(BrightnessDelta.MinValue - 1)
        }
    }

    @Test
    fun `brightness delta parseOrThrow rejects above maximum`() {
        assertFailsWith<IllegalArgumentException> {
            BrightnessDelta.parseOrThrow(BrightnessDelta.MaxValue + 1)
        }
    }

    @Test
    fun `brightness delta parse returns null for invalid input`() {
        val delta = BrightnessDelta.parse(BrightnessDelta.MaxValue + 1)

        assertNull(delta)
    }

    @Test
    fun `brightness delta parse returns value for valid input`() {
        val delta = BrightnessDelta.parse(42)

        assertNotNull(delta)
        assertEquals(42, delta.value)
    }

    @Test
    fun `processing config default disables grayscale and brightness and keeps neutral brightness`() {
        val config = ProcessingConfig.Default

        assertEquals(false, config.grayscale.enabled)
        assertEquals(false, config.brightness.enabled)
        assertEquals(BrightnessDelta.Neutral, config.brightness.delta)
    }
}
