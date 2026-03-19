package palbp.demos.pc.isel.imageviewer.viewmodel

import kotlin.concurrent.thread
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.processing.ImageLoader
import palbp.demos.pc.isel.imageviewer.processing.LocalFileImageLoader

/**
 * Thread-based ViewModel implementation.
 */
class ThreadsImageViewerViewModel(
    imageLoader: ImageLoader = LocalFileImageLoader(),
) : BaseImageViewerViewModel(imageLoader = imageLoader) {
    private val loader = imageLoader

    override fun executeLoad(
        imagePath: String,
        onSuccess: (LoadedImage) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        thread {
            runCatching { loader.loadBlocking(imagePath) }
                .onSuccess(onSuccess)
                .onFailure { onFailure(it.message ?: "Image load failed") }
        }
    }
}
