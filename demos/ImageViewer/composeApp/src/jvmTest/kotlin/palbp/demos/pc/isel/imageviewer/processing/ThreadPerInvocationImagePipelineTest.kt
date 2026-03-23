package palbp.demos.pc.isel.imageviewer.processing

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertContentEquals
import palbp.demos.pc.isel.imageviewer.domain.BrightnessDelta
import palbp.demos.pc.isel.imageviewer.domain.BrightnessFilterConfig
import palbp.demos.pc.isel.imageviewer.domain.GrayscaleFilterConfig
import palbp.demos.pc.isel.imageviewer.domain.ImageMetadata
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.domain.ProcessingConfig
import palbp.demos.pc.isel.imageviewer.processing.filters.applyFilters
import palbp.demos.pc.isel.imageviewer.processing.filters.composeArgb
import org.jetbrains.skia.Image

class ThreadPerInvocationImagePipelineTest {

    private val pipeline = ThreadPerInvocationImagePipeline()

    @Test
    fun `pipeline keeps pixels unchanged when all filters are disabled`() {
        // Arrange
        val width = 2
        val height = 2
        val sourcePixels = intArrayOf(
            argb(255, 10, 20, 30),
            argb(255, 40, 50, 60),
            argb(255, 70, 80, 90),
            argb(255, 100, 110, 120),
        )
        val sourceImage = createLoadedImage(width, height, sourcePixels)
        val input = ImagePipelineInput(
            source = sourceImage,
            config = ProcessingConfig.Default,
            executionSettings = PipelineExecutionSettings.parseOrThrow(workerCount = 2),
        )

        // Act
        val result = pipeline.process(input)

        // Assert
        assertContentEquals(sourcePixels, result.extractPixels())
    }

    @Test
    fun `pipeline output matches filter composer output for same input and config`() {
        // Arrange
        val width = 3
        val height = 2
        val sourcePixels = intArrayOf(
            argb(255, 10, 100, 200),
            argb(255, 20, 110, 210),
            argb(255, 30, 120, 220),
            argb(255, 40, 130, 230),
            argb(255, 50, 140, 240),
            argb(255, 60, 150, 250),
        )
        val config = ProcessingConfig(
            grayscale = GrayscaleFilterConfig(enabled = true),
            brightness = BrightnessFilterConfig(
                enabled = true,
                delta = BrightnessDelta.parseOrThrow(15),
            ),
        )
        val sourceImage = createLoadedImage(width, height, sourcePixels)
        val expected = applyFilters(sourcePixels = sourcePixels, config = config)
        val input = ImagePipelineInput(
            source = sourceImage,
            config = config,
            executionSettings = PipelineExecutionSettings.parseOrThrow(workerCount = 4),
        )

        // Act
        val result = pipeline.process(input)

        // Assert
        assertContentEquals(expected, result.extractPixels())
    }

    @Test
    fun `pipeline does not mutate source loaded image pixels`() {
        // Arrange
        val width = 2
        val height = 2
        val sourcePixels = intArrayOf(
            argb(255, 15, 25, 35),
            argb(255, 45, 55, 65),
            argb(255, 75, 85, 95),
            argb(255, 105, 115, 125),
        )
        val sourceImage = createLoadedImage(width, height, sourcePixels)
        val beforeProcess = sourceImage.extractPixels()
        val input = ImagePipelineInput(
            source = sourceImage,
            config = ProcessingConfig(
                grayscale = GrayscaleFilterConfig(enabled = true),
                brightness = BrightnessFilterConfig(
                    enabled = true,
                    delta = BrightnessDelta.parseOrThrow(10),
                ),
            ),
            executionSettings = PipelineExecutionSettings.parseOrThrow(workerCount = 2),
        )

        // Act
        pipeline.process(input)
        val afterProcess = sourceImage.extractPixels()

        // Assert
        assertContentEquals(beforeProcess, afterProcess)
    }
}

private fun createLoadedImage(width: Int, height: Int, pixels: IntArray): LoadedImage {
    val metadata = ImageMetadata.parseOrThrow(
        fileName = "pipeline-test.png",
        width = width,
        height = height,
    )
    val imageBitmap = pixels.toComposeImageBitmap(width = width, height = height)
    return LoadedImage.parseOrThrow(metadata = metadata, imageBitmap = imageBitmap)
}

private fun LoadedImage.extractPixels(): IntArray {
    val pixels = IntArray(metadata.width * metadata.height)
    val pixelMap = imageBitmap.toPixelMap()

    for (y in 0 until metadata.height) {
        for (x in 0 until metadata.width) {
            pixels[y * metadata.width + x] = pixelMap[x, y].toArgb()
        }
    }

    return pixels
}

private fun IntArray.toComposeImageBitmap(width: Int, height: Int) =
    toBufferedImage(width = width, height = height)
        .toEncodedPngBytes()
        .let { bytes -> Image.makeFromEncoded(bytes).toComposeImageBitmap() }

private fun IntArray.toBufferedImage(width: Int, height: Int): BufferedImage =
    BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
        setRGB(0, 0, width, height, this@toBufferedImage, 0, width)
    }

private fun BufferedImage.toEncodedPngBytes(): ByteArray =
    ByteArrayOutputStream().use { output ->
        check(ImageIO.write(this, "png", output)) { "No PNG writer available" }
        output.toByteArray()
    }

private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    composeArgb(alpha = alpha, red = red, green = green, blue = blue)
