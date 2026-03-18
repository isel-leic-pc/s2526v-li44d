package palbp.demos.pc.isel.imageviewer.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import imageviewer.composeapp.generated.resources.Res
import imageviewer.composeapp.generated.resources.controls_filters_placeholder
import imageviewer.composeapp.generated.resources.controls_mode_placeholder
import imageviewer.composeapp.generated.resources.controls_title
import imageviewer.composeapp.generated.resources.preview_original_title
import imageviewer.composeapp.generated.resources.preview_placeholder
import imageviewer.composeapp.generated.resources.preview_processed_title
import org.jetbrains.compose.resources.stringResource
import palbp.demos.pc.isel.imageviewer.viewmodel.ImageViewerScreenState

@Composable
fun ColumnScope.MainContent(state: ImageViewerScreenState) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PreviewCard(
                title = stringResource(Res.string.preview_original_title),
                text = currentImageLabel(state),
                modifier = Modifier.weight(1f),
            )
            PreviewCard(
                title = stringResource(Res.string.preview_processed_title),
                text = stringResource(Res.string.preview_placeholder),
                modifier = Modifier.weight(1f),
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
                Text(stringResource(Res.string.controls_mode_placeholder))
                Text(stringResource(Res.string.controls_filters_placeholder))
            }
        }
    }
}

@Composable
private fun PreviewCard(
    title: String,
    text: String,
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
                Text(text)
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
        MainContent(state = ImageViewerScreenState.Ready("sample-image.png"))
    }
}
