package palbp.demos.pc.isel.imageviewer.domain

import androidx.compose.ui.graphics.ImageBitmap

data class ImageMetadata(
    val fileName: String,
    val width: Int,
    val height: Int,
)

data class LoadedImage(
    val metadata: ImageMetadata,
    val imageBitmap: ImageBitmap,
)
