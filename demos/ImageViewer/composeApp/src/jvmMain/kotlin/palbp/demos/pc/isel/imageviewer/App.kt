package palbp.demos.pc.isel.imageviewer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import palbp.demos.pc.isel.imageviewer.ui.AppScreen
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerViewModel

@Composable
fun App() {

    val viewModel = remember { ImageViewerViewModel() }

    MaterialTheme {
        AppScreen(
            state = viewModel.state,
            onOpen = { viewModel.requestLoadImage("sample-image.png") },
            onSaveAs = { /* placeholder for Milestone 4 */ },
            onReset = { viewModel.reset() },
            onDismissError = { viewModel.dismissError() },
        )
    }
}
