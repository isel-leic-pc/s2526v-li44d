package palbp.demos.pc.isel.imageviewer.viewmodel

import palbp.demos.pc.isel.imageviewer.domain.LoadedImage

/**
 * Screen state machine for the current ImageViewer loading flow.
 */
sealed interface ImageViewerScreenState {
    /** Initial state: no image is currently loaded. */
    data object NoImage : ImageViewerScreenState

    /**
     * Image loading is in progress.
     *
     * [fallbackState] is used if loading fails and the UI enters [Error].
     */
    data class LoadingImage(
        val fallbackState: FallbackState,
    ) : ImageViewerScreenState

    /** Image is available for display. */
    data class Ready(
        val loadedImage: LoadedImage,
    ) : ImageViewerScreenState

    /**
     * Loading failed.
     *
     * [fallbackState] captures the state the user can return to via `dismissError`.
     */
    data class Error(
        val message: String,
        val fallbackState: FallbackState,
    ) : ImageViewerScreenState
}

/**
 * Fallback destination after an error is dismissed.
 */
sealed interface FallbackState {
    data object NoImage : FallbackState

    data class Ready(
        val loadedImage: LoadedImage,
    ) : FallbackState
}
