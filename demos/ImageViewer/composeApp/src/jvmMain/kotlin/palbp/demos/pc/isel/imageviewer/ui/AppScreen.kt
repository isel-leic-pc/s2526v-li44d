package palbp.demos.pc.isel.imageviewer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import palbp.demos.pc.isel.imageviewer.ui.components.Footer
import palbp.demos.pc.isel.imageviewer.ui.components.MainContent
import palbp.demos.pc.isel.imageviewer.ui.components.TopBar
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerScreenState
import palbp.demos.pc.isel.imageviewer.viewmodel.ProcessingMode

@Composable
@Preview
fun AppScreen(
    state: ImageViewerScreenState = ImageViewerScreenState.NoImage,
    selectedProcessingMode: ProcessingMode = ProcessingMode.Threads,
    onOpen: () -> Unit = {},
    onSaveAs: () -> Unit = {},
    onReset: () -> Unit = {},
    onDismissError: () -> Unit = {},
    onSelectProcessingMode: (ProcessingMode) -> Unit = {},
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TopBar(
                canOpen = state is ImageViewerScreenState.NoImage || state is ImageViewerScreenState.Ready,
                canReset = state is ImageViewerScreenState.Ready || state is ImageViewerScreenState.Error,
                onOpen = onOpen,
                onSaveAs = onSaveAs,
                onReset = onReset,
            )

            MainContent(
                state = state,
                selectedProcessingMode = selectedProcessingMode,
                onSelectProcessingMode = onSelectProcessingMode,
            )

            Footer(
                state = state,
                onDismissError = onDismissError,
            )
        }
    }
}
