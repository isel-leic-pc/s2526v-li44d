package palbp.demos.pc.isel.imageviewer.domain

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LoadedImageDomainTest {

    @Test
    fun `ImageMetadata parseOrThrow rejects blank file name`() {
        assertFailsWith<IllegalArgumentException> {
            ImageMetadata.parseOrThrow(fileName = "  ", width = 1, height = 1)
        }
    }

    @Test
    fun `ImageMetadata parseOrThrow rejects non-positive dimensions`() {
        assertFailsWith<IllegalArgumentException> {
            ImageMetadata.parseOrThrow(fileName = "img.png", width = 0, height = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            ImageMetadata.parseOrThrow(fileName = "img.png", width = 1, height = -1)
        }
    }

    @Test
    fun `ImageMetadata parse returns null for invalid data`() {
        val metadata = ImageMetadata.parse(fileName = "", width = 1, height = 1)
        assertNull(metadata)
    }

    @Test
    fun `LoadedImage parseOrThrow rejects metadata and bitmap dimension mismatch`() {
        val metadata = ImageMetadata.parseOrThrow(fileName = "img.png", width = 2, height = 2)
        val bitmap = ImageBitmap(width = 3, height = 2)

        assertFailsWith<IllegalArgumentException> {
            LoadedImage.parseOrThrow(metadata = metadata, imageBitmap = bitmap)
        }
    }

    @Test
    fun `LoadedImage parse succeeds for consistent metadata and bitmap`() {
        val metadata = ImageMetadata.parseOrThrow(fileName = "img.png", width = 2, height = 2)
        val bitmap = ImageBitmap(width = 2, height = 2)

        val loadedImage = LoadedImage.parse(metadata = metadata, imageBitmap = bitmap)

        assertNotNull(loadedImage)
    }

    @Test
    fun `ImageMetadata copy preserves invariants`() {
        val metadata = ImageMetadata.parseOrThrow(fileName = "img.png", width = 2, height = 2)

        assertFailsWith<IllegalArgumentException> {
            metadata.copy(width = 0)
        }
    }
}
