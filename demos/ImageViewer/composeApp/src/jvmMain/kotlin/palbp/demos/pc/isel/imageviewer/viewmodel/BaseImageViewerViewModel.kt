package palbp.demos.pc.isel.imageviewer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.processing.ImageLoader

/**
 * Shared state-machine implementation.
 *
 * Implementations differ only in how they execute asynchronous loading.
 */
abstract class BaseImageViewerViewModel(
    private val imageLoader: ImageLoader,
) : ImageViewerViewModel {

    private var mutableState by mutableStateOf<ImageViewerScreenState>(ImageViewerScreenState.NoImage)

    final override val state: ImageViewerScreenState
        get() = mutableState

    final override fun requestLoadImage(imagePath: String) {
        val fallbackState = when (val currentState = state) {
            ImageViewerScreenState.NoImage -> FallbackState.NoImage
            is ImageViewerScreenState.Ready -> FallbackState.Ready(currentState.loadedImage)
            else -> invalidTransition("requestLoadImage")
        }

        mutableState = ImageViewerScreenState.LoadingImage(fallbackState = fallbackState)
        executeLoad(
            imagePath = imagePath,
            onSuccess = { loadedImage ->
                Snapshot.withMutableSnapshot {
                    onLoadImageSuccess(loadedImage)
                }
            },
            onFailure = { message ->
                Snapshot.withMutableSnapshot {
                    onLoadImageError(message = message, fallbackState = fallbackState)
                }
            },
        )
    }

    final override fun reset() {
        mutableState = when (state) {
            is ImageViewerScreenState.Ready,
            is ImageViewerScreenState.Error,
            -> ImageViewerScreenState.NoImage
            else -> invalidTransition("reset")
        }
    }

    final override fun dismissError() {
        mutableState = when (val currentState = state) {
            is ImageViewerScreenState.Error -> when (val fallback = currentState.fallbackState) {
                FallbackState.NoImage -> ImageViewerScreenState.NoImage
                is FallbackState.Ready -> ImageViewerScreenState.Ready(loadedImage = fallback.loadedImage)
            }

            else -> invalidTransition("dismissError")
        }
    }

    protected abstract fun executeLoad(
        imagePath: String,
        onSuccess: (LoadedImage) -> Unit,
        onFailure: (String) -> Unit,
    )

    private fun onLoadImageSuccess(loadedImage: LoadedImage) {
        mutableState = when (state) {
            is ImageViewerScreenState.LoadingImage -> ImageViewerScreenState.Ready(loadedImage = loadedImage)
            else -> invalidTransition("onLoadImageSuccess")
        }
    }

    private fun onLoadImageError(message: String, fallbackState: FallbackState) {
        mutableState = when (state) {
            is ImageViewerScreenState.LoadingImage -> ImageViewerScreenState.Error(
                message = message,
                fallbackState = fallbackState,
            )

            else -> invalidTransition("onLoadImageError")
        }
    }

    private fun invalidTransition(operation: String): Nothing {
        error("Invalid transition: $operation from state $state")
    }
}
