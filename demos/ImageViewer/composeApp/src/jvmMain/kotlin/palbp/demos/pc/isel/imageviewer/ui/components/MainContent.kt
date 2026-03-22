package palbp.demos.pc.isel.imageviewer.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import imageviewer.composeapp.generated.resources.Res
import imageviewer.composeapp.generated.resources.controls_filters_placeholder
import imageviewer.composeapp.generated.resources.controls_mode_coroutines
import imageviewer.composeapp.generated.resources.controls_mode_threads
import imageviewer.composeapp.generated.resources.controls_processing_mode_label
import imageviewer.composeapp.generated.resources.controls_title
import imageviewer.composeapp.generated.resources.preview_placeholder
import imageviewer.composeapp.generated.resources.preview_show_original
import imageviewer.composeapp.generated.resources.preview_show_processed
import imageviewer.composeapp.generated.resources.preview_title
import imageviewer.composeapp.generated.resources.preview_toggle_hint
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.Image
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerScreenState
import palbp.demos.pc.isel.imageviewer.viewmodel.ProcessingMode
import palbp.demos.pc.isel.imageviewer.viewmodel.createPlaceholderLoadedImage

@Composable
fun ColumnScope.MainContent(
    state: ImageViewerScreenState,
    selectedProcessingMode: ProcessingMode,
    onSelectProcessingMode: (ProcessingMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { /* placeholder for future preview switch */ }, enabled = false) {
                    Text(stringResource(Res.string.preview_show_original))
                }
                TextButton(onClick = { /* placeholder for future preview switch */ }, enabled = false) {
                    Text(stringResource(Res.string.preview_show_processed))
                }
            }
            PreviewCard(
                title = stringResource(Res.string.preview_title),
                state = state,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(Res.string.preview_toggle_hint),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Card(
            modifier = Modifier
                .width(320.dp)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(Res.string.controls_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(stringResource(Res.string.controls_processing_mode_label))
                ProcessingModeOption(
                    mode = ProcessingMode.Threads,
                    selectedMode = selectedProcessingMode,
                    onSelectProcessingMode = onSelectProcessingMode,
                    label = stringResource(Res.string.controls_mode_threads),
                )
                ProcessingModeOption(
                    mode = ProcessingMode.Coroutines,
                    selectedMode = selectedProcessingMode,
                    onSelectProcessingMode = onSelectProcessingMode,
                    label = stringResource(Res.string.controls_mode_coroutines),
                )
                Spacer(Modifier.weight(1f))
                Text(stringResource(Res.string.controls_filters_placeholder))
            }
        }
    }
}

@Composable
private fun PreviewCard(
    title: String,
    state: ImageViewerScreenState,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is ImageViewerScreenState.Ready -> Image(
                        bitmap = state.loadedImage.imageBitmap,
                        contentDescription = state.loadedImage.metadata.fileName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )

                    ImageViewerScreenState.NoImage,
                    is ImageViewerScreenState.LoadingImage,
                    is ImageViewerScreenState.Error,
                    -> Text(
                        "${currentImageLabel(state)}\n${stringResource(Res.string.preview_placeholder)}",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainContentPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
    ) {
        MainContent(
            state = ImageViewerScreenState.Ready(createPlaceholderLoadedImage("preview-image.png")),
            selectedProcessingMode = ProcessingMode.Threads,
            onSelectProcessingMode = {},
        )
    }
}

@Composable
private fun ProcessingModeOption(
    mode: ProcessingMode,
    selectedMode: ProcessingMode,
    onSelectProcessingMode: (ProcessingMode) -> Unit,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RadioButton(
            selected = selectedMode == mode,
            onClick = { onSelectProcessingMode(mode) },
        )
        Text(label)
    }
}
