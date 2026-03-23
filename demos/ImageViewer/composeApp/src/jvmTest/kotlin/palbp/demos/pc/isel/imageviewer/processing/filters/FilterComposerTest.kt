package palbp.demos.pc.isel.imageviewer.processing.filters

import palbp.demos.pc.isel.imageviewer.domain.BrightnessDelta
import palbp.demos.pc.isel.imageviewer.domain.BrightnessFilterConfig
import palbp.demos.pc.isel.imageviewer.domain.GrayscaleFilterConfig
import palbp.demos.pc.isel.imageviewer.domain.ProcessingConfig
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotSame

class FilterComposerTest {

    @Test
    fun `composer keeps source pixels unchanged when both filters are disabled`() {
        // Arrange
        val firstPixelRed = 10
        val firstPixelGreen = 20
        val firstPixelBlue = 30
        val secondPixelRed = 40
        val secondPixelGreen = 50
        val secondPixelBlue = 60
        val source = intArrayOf(
            argb(alpha = OPAQUE_ALPHA, red = firstPixelRed, green = firstPixelGreen, blue = firstPixelBlue),
            argb(alpha = OPAQUE_ALPHA, red = secondPixelRed, green = secondPixelGreen, blue = secondPixelBlue),
        )

        // Act
        val result = applyFilters(sourcePixels = source, config = ProcessingConfig.Default)

        // Assert
        assertContentEquals(source, result)
    }

    @Test
    fun `composer applies grayscale then brightness in deterministic order`() {
        // Arrange
        val sourceRed = 10
        val sourceGreen = 100
        val sourceBlue = 200
        val deltaValue = 20
        val source = intArrayOf(
            argb(
                alpha = OPAQUE_ALPHA,
                red = sourceRed,
                green = sourceGreen,
                blue = sourceBlue,
            ),
        )
        val config = ProcessingConfig(
            grayscale = GrayscaleFilterConfig(enabled = true),
            brightness = BrightnessFilterConfig(
                enabled = true,
                delta = BrightnessDelta.parseOrThrow(deltaValue),
            ),
        )
        val expected = IntArray(1) { index ->
            val gray = applyGrayscaleToPixel(source[index])
            applyBrightnessToPixel(gray, BrightnessDelta.parseOrThrow(deltaValue))
        }

        // Act
        val result = applyFilters(sourcePixels = source, config = config)

        // Assert
        assertContentEquals(expected, result)
    }

    @Test
    fun `composer returns new array instance`() {
        // Arrange
        val sourceRed = 1
        val sourceGreen = 2
        val sourceBlue = 3
        val source = intArrayOf(
            argb(
                alpha = OPAQUE_ALPHA,
                red = sourceRed,
                green = sourceGreen,
                blue = sourceBlue,
            ),
        )

        // Act
        val result = applyFilters(sourcePixels = source, config = ProcessingConfig.Default)

        // Assert
        assertNotSame(source, result)
    }
}

private const val OPAQUE_ALPHA = 255

private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    composeArgb(alpha = alpha, red = red, green = green, blue = blue)
