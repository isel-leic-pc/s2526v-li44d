package palbp.demos.pc.isel.imageviewer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
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
            onOpen = {
                chooseImageFilePath()?.let { selectedPath ->
                    activeViewModel.requestLoadImage(selectedPath)
                }
            },
            onSaveAs = { /* placeholder for Milestone 4 */ },
            onReset = { activeViewModel.reset() },
            onDismissError = { activeViewModel.dismissError() },
            onSelectProcessingMode = { mode -> selectedProcessingMode = mode },
        )
    }
}

private fun chooseImageFilePath(): String? {
    val dialog = FileDialog(null as Frame?, "Open Image", FileDialog.LOAD).apply {
        filenameFilter = java.io.FilenameFilter { _, name ->
            val extension = name.substringAfterLast('.', "").lowercase()
            extension in setOf("png", "jpg", "jpeg")
        }
        isVisible = true
    }

    val selectedFile = dialog.file ?: return null
    val selectedDirectory = dialog.directory ?: return null
    return File(selectedDirectory, selectedFile).absolutePath
}
