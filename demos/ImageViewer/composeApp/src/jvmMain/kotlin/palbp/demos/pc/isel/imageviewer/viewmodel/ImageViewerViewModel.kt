package palbp.demos.pc.isel.imageviewer.viewmodel

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

/**
 * Blocking image loading abstraction used by ViewModel implementations.
 */
fun interface ImageLoader {
    @Throws(Exception::class)
    fun loadBlocking(imageName: String): String
}
