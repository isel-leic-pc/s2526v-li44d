package palbp.demos.pc.isel.imageviewer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import imageviewer.composeapp.generated.resources.Res
import imageviewer.composeapp.generated.resources.topbar_open
import imageviewer.composeapp.generated.resources.topbar_reset
import imageviewer.composeapp.generated.resources.topbar_save_as_png
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopBar(
    canOpen: Boolean,
    canReset: Boolean,
    onOpen: () -> Unit,
    onSaveAs: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(onClick = onOpen, enabled = canOpen) {
            Text(stringResource(Res.string.topbar_open))
        }
        OutlinedButton(onClick = onSaveAs, enabled = false) {
            Text(stringResource(Res.string.topbar_save_as_png))
        }
        OutlinedButton(onClick = onReset, enabled = canReset) {
            Text(stringResource(Res.string.topbar_reset))
        }
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBar(
        canOpen = true,
        canReset = true,
        onOpen = {},
        onSaveAs = {},
        onReset = {},
    )
}
