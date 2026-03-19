package palbp.demos.pc.isel.imageviewer.processing

import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocalFileImageLoaderTest {

    private val loader = LocalFileImageLoader()

    @Test
    fun `loadBlocking loads png file and returns metadata`() = withTempDir { tempDir ->
        val path = tempDir.resolve("sample.png")
        writeSolidImage(path = path, format = "png", width = 3, height = 2)

        val loaded = loader.loadBlocking(path.toString())

        assertEquals("sample.png", loaded.metadata.fileName)
        assertEquals(3, loaded.metadata.width)
        assertEquals(2, loaded.metadata.height)
        assertTrue(loaded.imageBitmap.width > 0)
        assertTrue(loaded.imageBitmap.height > 0)
    }

    @Test
    fun `loadBlocking loads jpg file and returns metadata`() = withTempDir { tempDir ->
        val path = tempDir.resolve("sample.jpg")
        writeSolidImage(path = path, format = "jpg", width = 5, height = 4)

        val loaded = loader.loadBlocking(path.toString())

        assertEquals("sample.jpg", loaded.metadata.fileName)
        assertEquals(5, loaded.metadata.width)
        assertEquals(4, loaded.metadata.height)
    }

    @Test
    fun `loadBlocking rejects unsupported extension`() = withTempDir { tempDir ->
        val path = tempDir.resolve("sample.gif")
        writeSolidImage(path = path, format = "png", width = 1, height = 1)

        val error = assertFailsWith<ImageLoadException> {
            loader.loadBlocking(path.toString())
        }

        assertEquals(ImageLoadErrorKind.UNSUPPORTED_FORMAT, error.kind)
        assertEquals("sample.gif", error.fileName)
    }

    @Test
    fun `loadBlocking fails when file does not exist`() = withTempDir { tempDir ->
        val path = tempDir.resolve("missing.png")

        val error = assertFailsWith<ImageLoadException> {
            loader.loadBlocking(path.toString())
        }

        assertEquals(ImageLoadErrorKind.FILE_NOT_FOUND, error.kind)
        assertEquals("missing.png", error.fileName)
    }

    @Test
    fun `loadBlocking fails on decode error for corrupted image`() = withTempDir { tempDir ->
        val path = tempDir.resolve("broken.png")
        path.writeBytes(byteArrayOf(0x01, 0x02, 0x03))

        val error = assertFailsWith<ImageLoadException> {
            loader.loadBlocking(path.toString())
        }

        assertEquals(ImageLoadErrorKind.DECODE_FAILED, error.kind)
        assertEquals("broken.png", error.fileName)
        assertNotNull(error.cause)
    }
}

private inline fun withTempDir(block: (Path) -> Unit) {
    val tempDir = createTempDirectory("local-file-image-loader-test")
    try {
        block(tempDir)
    } finally {
        tempDir.toFile().deleteRecursively()
    }
}

private fun writeSolidImage(path: Path, format: String, width: Int, height: Int) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color(12, 34, 56)
    graphics.fillRect(0, 0, width, height)
    graphics.dispose()

    val encoded = ImageIO.write(image, format, path.toFile())
    check(encoded) { "No writer found for format '$format'" }
}
