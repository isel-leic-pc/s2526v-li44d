package palbp.demos.pc.isel.imageviewer.ui.components

import androidx.compose.runtime.Composable
import imageviewer.composeapp.generated.resources.Res
import imageviewer.composeapp.generated.resources.file_none
import imageviewer.composeapp.generated.resources.status_error
import imageviewer.composeapp.generated.resources.status_loading
import imageviewer.composeapp.generated.resources.status_no_image
import imageviewer.composeapp.generated.resources.status_ready
import org.jetbrains.compose.resources.stringResource
import palbp.demos.pc.isel.imageviewer.viewmodel.FallbackState
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerScreenState

@Composable
fun statusText(state: ImageViewerScreenState): String = when (state) {
    ImageViewerScreenState.NoImage -> stringResource(Res.string.status_no_image)
    is ImageViewerScreenState.LoadingImage -> stringResource(Res.string.status_loading)
    is ImageViewerScreenState.Ready -> stringResource(Res.string.status_ready)
    is ImageViewerScreenState.Error -> stringResource(Res.string.status_error)
}

@Composable
fun currentImageLabel(state: ImageViewerScreenState): String = when (state) {
    ImageViewerScreenState.NoImage -> stringResource(Res.string.file_none)
    is ImageViewerScreenState.Ready -> state.loadedImage.metadata.fileName
    is ImageViewerScreenState.LoadingImage -> when (val fb = state.fallbackState) {
        FallbackState.NoImage -> stringResource(Res.string.file_none)
        is FallbackState.Ready -> fb.loadedImage.metadata.fileName
    }

    is ImageViewerScreenState.Error -> when (val fb = state.fallbackState) {
        FallbackState.NoImage -> stringResource(Res.string.file_none)
        is FallbackState.Ready -> fb.loadedImage.metadata.fileName
    }
}
