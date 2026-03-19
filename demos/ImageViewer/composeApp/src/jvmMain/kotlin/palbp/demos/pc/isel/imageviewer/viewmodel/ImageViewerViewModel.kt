package palbp.demos.pc.isel.imageviewer.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import palbp.demos.pc.isel.imageviewer.domain.ImageMetadata
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.processing.ImageLoader

/**
 * Public contract shared by all processing-mode-specific ViewModel implementations.
 *
 * The state machine and public API are intentionally identical across implementations.
 */
interface ImageViewerViewModel {
    val state: ImageViewerScreenState

    fun requestLoadImage(imageName: String)

    fun reset()

    fun dismissError()
}

fun createPlaceholderLoadedImage(imageName: String): LoadedImage =
    LoadedImage.parseOrThrow(
        metadata = ImageMetadata.parseOrThrow(
            fileName = imageName,
            width = 1,
            height = 1,
        ),
        imageBitmap = ImageBitmap(width = 1, height = 1),
    )
