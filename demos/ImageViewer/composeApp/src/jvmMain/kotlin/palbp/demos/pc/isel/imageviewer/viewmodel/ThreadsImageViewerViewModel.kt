package palbp.demos.pc.isel.imageviewer.viewmodel

import kotlin.concurrent.thread

/**
 * Thread-based ViewModel implementation.
 */
class ThreadsImageViewerViewModel(
    imageLoader: ImageLoader = ImageLoader { it },
) : BaseImageViewerViewModel(imageLoader = imageLoader) {
    private val loader = imageLoader

    override fun executeLoad(
        imageName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        thread {
            runCatching { loader.loadBlocking(imageName) }
                .onSuccess(onSuccess)
                .onFailure { onFailure(it.message ?: "Image load failed") }
        }
    }
}
