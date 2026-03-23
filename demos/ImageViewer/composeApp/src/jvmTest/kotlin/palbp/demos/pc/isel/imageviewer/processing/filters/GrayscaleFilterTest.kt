package palbp.demos.pc.isel.imageviewer.processing.filters

import kotlin.test.Test
import kotlin.test.assertEquals

class GrayscaleFilterTest {

    @Test
    fun `grayscale filter sets rgb channels to same luminance value`() {
        // Arrange
        val inputRed = 120
        val inputGreen = 200
        val inputBlue = 40
        val source = argb(
            alpha = OPAQUE_ALPHA,
            red = inputRed,
            green = inputGreen,
            blue = inputBlue,
        )

        // Act
        val result = applyGrayscaleToPixel(source)

        // Assert
        val red = redOf(result)
        val green = greenOf(result)
        val blue = blueOf(result)
        assertEquals(red, green)
        assertEquals(green, blue)
    }

    @Test
    fun `grayscale filter preserves alpha channel`() {
        // Arrange
        val semiTransparentAlpha = 120
        val inputRed = 120
        val inputGreen = 200
        val inputBlue = 40
        val source = argb(
            alpha = semiTransparentAlpha,
            red = inputRed,
            green = inputGreen,
            blue = inputBlue,
        )

        // Act
        val result = applyGrayscaleToPixel(source)

        // Assert
        assertEquals(semiTransparentAlpha, alphaOf(result))
    }
}

private const val OPAQUE_ALPHA = 255

private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    composeArgb(alpha = alpha, red = red, green = green, blue = blue)
