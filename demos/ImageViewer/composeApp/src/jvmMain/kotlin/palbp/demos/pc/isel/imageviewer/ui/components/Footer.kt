package palbp.demos.pc.isel.imageviewer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import imageviewer.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import palbp.demos.pc.isel.imageviewer.viewmodel.FallbackState
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerScreenState

@Composable
fun Footer(
    state: ImageViewerScreenState,
    onDismissError: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("${stringResource(Res.string.footer_status_prefix)} ${statusText(state)}")
            Text("${stringResource(Res.string.footer_file_prefix)} ${currentImageLabel(state)}")
            if (state is ImageViewerScreenState.Error) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${stringResource(Res.string.footer_error_prefix)} ${state.message}")
                    OutlinedButton(onClick = onDismissError) {
                        Text(stringResource(Res.string.footer_dismiss))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FooterPreview() {
    Footer(
        state = ImageViewerScreenState.Error(
            message = "Sample error",
            fallbackState = FallbackState.NoImage,
        ),
        onDismissError = {},
    )
}
