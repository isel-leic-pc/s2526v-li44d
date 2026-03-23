package palbp.demos.pc.isel.imageviewer.processing

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.CountDownLatch
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import org.jetbrains.skia.Image
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.processing.filters.applyFiltersToPixel

/**
 * JVM thread-based pipeline with row-based data partitioning.
 */
class ThreadPerInvocationImagePipeline : ImagePipeline {

    override fun process(input: ImagePipelineInput): LoadedImage {
        val sourceImage = input.source
        val width = sourceImage.metadata.width
        val height = sourceImage.metadata.height
        val sourcePixels = sourceImage.imageBitmap.toArgbPixels(width = width, height = height)

        val outputPixels = IntArray(sourcePixels.size)
        val workerCount = input.executionSettings.workerCount
        val latch = CountDownLatch(workerCount)

        val rowsPerWorker = height / workerCount
        val remainderRows = height % workerCount
        var nextStartRow = 0

        repeat(workerCount) { workerIndex ->
            val extraRow = if (workerIndex < remainderRows) 1 else 0
            val workerRowCount = rowsPerWorker + extraRow
            val startRow = nextStartRow
            val endExclusiveRow = startRow + workerRowCount
            nextStartRow = endExclusiveRow

            thread {
                try {
                    processRows(
                        sourcePixels = sourcePixels,
                        outputPixels = outputPixels,
                        width = width,
                        startInclusiveRow = startRow,
                        endExclusiveRow = endExclusiveRow,
                        input = input,
                    )
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        return try {
            val imageBitmap = outputPixels.toComposeImageBitmap(width = width, height = height)
            LoadedImage.parseOrThrow(
                metadata = sourceImage.metadata,
                imageBitmap = imageBitmap,
            )
        } catch (cause: Exception) {
            throw ImageProcessingException("Failed to build processed image", cause)
        }
    }
}

private fun processRows(
    sourcePixels: IntArray,
    outputPixels: IntArray,
    width: Int,
    startInclusiveRow: Int,
    endExclusiveRow: Int,
    input: ImagePipelineInput,
) {
    val startIndex = startInclusiveRow * width
    val endIndex = endExclusiveRow * width

    for (pixelIndex in startIndex until endIndex) {
        outputPixels[pixelIndex] = applyFiltersToPixel(
            sourcePixel = sourcePixels[pixelIndex],
            config = input.config,
        )
    }
}

private fun ImageBitmap.toArgbPixels(width: Int, height: Int): IntArray {
    val pixels = IntArray(width * height)
    val pixelMap = toPixelMap()

    for (y in 0 until height) {
        for (x in 0 until width) {
            pixels[y * width + x] = pixelMap[x, y].toArgb()
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
