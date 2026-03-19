package palbp.demos.pc.isel.imageviewer.viewmodel

import kotlin.concurrent.thread
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage

/**
 * Thread-based ViewModel implementation.
 */
class ThreadsImageViewerViewModel(
    imageLoader: ImageLoader = ImageLoader(::createPlaceholderLoadedImage),
) : BaseImageViewerViewModel(imageLoader = imageLoader) {
    private val loader = imageLoader

    override fun executeLoad(
        imageName: String,
        onSuccess: (LoadedImage) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        thread {
            runCatching { loader.loadBlocking(imageName) }
                .onSuccess(onSuccess)
                .onFailure { onFailure(it.message ?: "Image load failed") }
        }
    }
}
