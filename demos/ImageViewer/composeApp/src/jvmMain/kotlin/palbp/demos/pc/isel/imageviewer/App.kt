package palbp.demos.pc.isel.imageviewer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import palbp.demos.pc.isel.imageviewer.ui.AppScreen
import palbp.demos.pc.isel.imageviewer.viewmodel.CoroutinesImageViewerViewModel
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerViewModel
import palbp.demos.pc.isel.imageviewer.viewmodel.ProcessingMode
import palbp.demos.pc.isel.imageviewer.viewmodel.ThreadsImageViewerViewModel

@Composable
fun App() {

    var selectedProcessingMode by remember { mutableStateOf(ProcessingMode.Threads) }
    val activeViewModel: ImageViewerViewModel = remember(selectedProcessingMode) {
        when (selectedProcessingMode) {
            ProcessingMode.Threads -> ThreadsImageViewerViewModel()
            ProcessingMode.Coroutines -> CoroutinesImageViewerViewModel()
        }
    }

    MaterialTheme {
        AppScreen(
            state = activeViewModel.state,
            selectedProcessingMode = selectedProcessingMode,
            onOpen = { activeViewModel.requestLoadImage("sample-image.png") },
            onSaveAs = { /* placeholder for Milestone 4 */ },
            onReset = { activeViewModel.reset() },
            onDismissError = { activeViewModel.dismissError() },
            onSelectProcessingMode = { mode -> selectedProcessingMode = mode },
        )
    }
}
