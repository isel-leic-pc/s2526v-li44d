package palbp.demos.pc.isel.imageviewer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import imageviewer.composeapp.generated.resources.Res
import imageviewer.composeapp.generated.resources.window_title
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.window_title),
    ) {
        App()
    }
}
