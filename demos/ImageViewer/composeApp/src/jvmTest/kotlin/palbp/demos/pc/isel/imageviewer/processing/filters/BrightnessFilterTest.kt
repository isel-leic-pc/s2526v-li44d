package palbp.demos.pc.isel.imageviewer.processing.filters

import kotlin.test.Test
import kotlin.test.assertEquals
import palbp.demos.pc.isel.imageviewer.domain.BrightnessDelta

class BrightnessFilterTest {

    @Test
    fun `brightness filter with neutral delta keeps rgb unchanged`() {
        // Arrange
        val sourceRed = 120
        val sourceGreen = 40
        val sourceBlue = 200
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = sourceRed,
            green = sourceGreen,
            blue = sourceBlue,
        )

        // Act
        val result = applyBrightnessToPixel(source, BrightnessDelta.Neutral)

        // Assert
        assertEquals(sourceRed, redOf(result))
        assertEquals(sourceGreen, greenOf(result))
        assertEquals(sourceBlue, blueOf(result))
    }

    @Test
    fun `brightness filter with positive delta increases rgb channels`() {
        // Arrange
        val sourceRed = 10
        val sourceGreen = 20
        val sourceBlue = 30
        val positiveDelta = 15
        val expectedRed = 25
        val expectedGreen = 35
        val expectedBlue = 45
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = sourceRed,
            green = sourceGreen,
            blue = sourceBlue,
        )
        val delta = BrightnessDelta.parseOrThrow(positiveDelta)

        // Act
        val result = applyBrightnessToPixel(source, delta)

        // Assert
        assertEquals(expectedRed, redOf(result))
        assertEquals(expectedGreen, greenOf(result))
        assertEquals(expectedBlue, blueOf(result))
    }

    @Test
    fun `brightness filter with negative delta decreases rgb channels`() {
        // Arrange
        val sourceRed = 80
        val sourceGreen = 60
        val sourceBlue = 40
        val negativeDelta = -20
        val expectedRed = 60
        val expectedGreen = 40
        val expectedBlue = 20
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = sourceRed,
            green = sourceGreen,
            blue = sourceBlue,
        )
        val delta = BrightnessDelta.parseOrThrow(negativeDelta)

        // Act
        val result = applyBrightnessToPixel(source, delta)

        // Assert
        assertEquals(expectedRed, redOf(result))
        assertEquals(expectedGreen, greenOf(result))
        assertEquals(expectedBlue, blueOf(result))
    }

    @Test
    fun `brightness filter clamps rgb channels at upper bound`() {
        // Arrange
        val sourceRed = 250
        val sourceGreen = 251
        val sourceBlue = 252
        val upperBoundDelta = 20
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = sourceRed,
            green = sourceGreen,
            blue = sourceBlue,
        )
        val delta = BrightnessDelta.parseOrThrow(upperBoundDelta)

        // Act
        val result = applyBrightnessToPixel(source, delta)

        // Assert
        assertEquals(MAX_CHANNEL_VALUE, redOf(result))
        assertEquals(MAX_CHANNEL_VALUE, greenOf(result))
        assertEquals(MAX_CHANNEL_VALUE, blueOf(result))
    }

    @Test
    fun `brightness filter clamps rgb channels at lower bound`() {
        // Arrange
        val sourceRed = 5
        val sourceGreen = 4
        val sourceBlue = 3
        val lowerBoundDelta = -20
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = sourceRed,
            green = sourceGreen,
            blue = sourceBlue,
        )
        val delta = BrightnessDelta.parseOrThrow(lowerBoundDelta)

        // Act
        val result = applyBrightnessToPixel(source, delta)

        // Assert
        assertEquals(MIN_CHANNEL_VALUE, redOf(result))
        assertEquals(MIN_CHANNEL_VALUE, greenOf(result))
        assertEquals(MIN_CHANNEL_VALUE, blueOf(result))
    }
}

private const val OPAQUE_ALPHA = 255

private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    composeArgb(alpha = alpha, red = red, green = green, blue = blue)
