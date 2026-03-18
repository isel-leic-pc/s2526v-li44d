package palbp.demos.pc.isel.imageviewer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import kotlin.concurrent.thread

/**
 * ViewModel that owns the current screen state machine for image loading.
 *
 * State is exposed via Compose snapshot state through [state].
 */
class ImageViewerViewModel(
    private val imageLoader: ImageLoader = ImageLoader { it },
) {
    /** Current state machine node for the screen. */
    var state by mutableStateOf<ImageViewerScreenState>(ImageViewerScreenState.NoImage)
        private set

    /**
     * Requests image loading.
     *
     * Allowed transitions:
     * - [ImageViewerScreenState.NoImage] -> [ImageViewerScreenState.LoadingImage]
     * - [ImageViewerScreenState.Ready] -> [ImageViewerScreenState.LoadingImage]
     *
     * The actual load runs asynchronously on a dedicated thread.
     */
    private fun loadImageAsync(imageName: String, fallBackState: FallbackState) {
        state =
            ImageViewerScreenState.LoadingImage(
            fallbackState = fallBackState,
        )
        thread {
            val result = runCatching { imageLoader.loadBlocking(imageName) }
            result.onSuccess { loadedImageName ->
                Snapshot.withMutableSnapshot {
                    onLoadImageSuccess(loadedImageName)
                }
            }.onFailure { failure ->
                Snapshot.withMutableSnapshot {
                    onLoadImageError(failure.message ?: "Image load failed", fallBackState)
                }
            }
        }
    }

    /**
     * Public operation used by the UI to start loading an image.
     */
    fun requestLoadImage(imageName: String) {
        when (val currentState = state) {
            ImageViewerScreenState.NoImage -> loadImageAsync(imageName, FallbackState.NoImage)
            is ImageViewerScreenState.Ready -> loadImageAsync(imageName, FallbackState.Ready(currentState.imageName))

            else -> invalidTransition("requestLoadImage")
        }

    }

    /**
     * Resets the screen to [ImageViewerScreenState.NoImage].
     *
     * Allowed from [ImageViewerScreenState.Ready] and [ImageViewerScreenState.Error].
     */
    fun reset() {
        state = when (state) {
            is ImageViewerScreenState.Ready,
            is ImageViewerScreenState.Error,
                -> ImageViewerScreenState.NoImage

            else -> invalidTransition("reset")
        }
    }

    /**
     * Dismisses an error and returns to the captured fallback state.
     */
    fun dismissError() {
        state = when (val currentState = state) {
            is ImageViewerScreenState.Error -> when (val fallback = currentState.fallbackState) {
                FallbackState.NoImage -> ImageViewerScreenState.NoImage
                is FallbackState.Ready -> ImageViewerScreenState.Ready(imageName = fallback.imageName)
            }

            else -> invalidTransition("dismissError")
        }
    }

    /** Internal transition applied when async loading succeeds. */
    private fun onLoadImageSuccess(imageName: String) {
        state = when (state) {
            is ImageViewerScreenState.LoadingImage -> ImageViewerScreenState.Ready(imageName = imageName)
            else -> invalidTransition("onLoadImageSuccess")
        }
    }

    /** Internal transition applied when async loading fails. */
    private fun onLoadImageError(message: String, fallBackState: FallbackState) {
        state = when (state) {
            is ImageViewerScreenState.LoadingImage -> ImageViewerScreenState.Error(
                message = message,
                fallbackState = fallBackState,
            )

            else -> invalidTransition("onLoadImageError")
        }
    }

    private fun invalidTransition(operation: String): Nothing {
        error("Invalid transition: $operation from state $state")
    }
}

/**
 * Blocking image loading abstraction used by [ImageViewerViewModel].
 *
 * The ViewModel executes this function asynchronously on a thread.
 */
fun interface ImageLoader {
    @Throws(Exception::class)
    fun loadBlocking(imageName: String): String
}
