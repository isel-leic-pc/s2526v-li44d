package palbp.demos.pc.isel.imageviewer.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage

/**
 * Coroutine-based ViewModel implementation.
 */
class CoroutinesImageViewerViewModel(
    imageLoader: ImageLoader = ImageLoader(::createPlaceholderLoadedImage),
) : BaseImageViewerViewModel(imageLoader = imageLoader) {
    private val loader = imageLoader
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun executeLoad(
        imageName: String,
        onSuccess: (LoadedImage) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        scope.launch {
            runCatching { loader.loadBlocking(imageName) }
                .onSuccess(onSuccess)
                .onFailure { onFailure(it.message ?: "Image load failed") }
        }
    }
}
