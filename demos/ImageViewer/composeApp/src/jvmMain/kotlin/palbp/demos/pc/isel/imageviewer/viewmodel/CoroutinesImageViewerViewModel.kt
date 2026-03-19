package palbp.demos.pc.isel.imageviewer.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Coroutine-based ViewModel implementation.
 */
class CoroutinesImageViewerViewModel(
    imageLoader: ImageLoader = ImageLoader { it },
) : BaseImageViewerViewModel(imageLoader = imageLoader) {
    private val loader = imageLoader
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun executeLoad(
        imageName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        scope.launch {
            runCatching { loader.loadBlocking(imageName) }
                .onSuccess(onSuccess)
                .onFailure { onFailure(it.message ?: "Image load failed") }
        }
    }
}
