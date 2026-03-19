package palbp.demos.pc.isel.imageviewer.domain

import androidx.compose.ui.graphics.ImageBitmap

data class ImageMetadata(
    val fileName: String,
    val width: Int,
    val height: Int,
) {
    init {
        require(fileName.isNotBlank()) { "fileName must not be blank" }
        require(width > 0) { "width must be greater than zero" }
        require(height > 0) { "height must be greater than zero" }
    }

    companion object {
        fun parse(fileName: String, width: Int, height: Int): ImageMetadata? =
            runCatching { parseOrThrow(fileName, width, height) }.getOrNull()

        fun parseOrThrow(fileName: String, width: Int, height: Int): ImageMetadata =
            ImageMetadata(fileName = fileName, width = width, height = height)
    }
}

class LoadedImage private constructor(
    val metadata: ImageMetadata,
    val imageBitmap: ImageBitmap,
) {
    companion object {
        fun parse(metadata: ImageMetadata, imageBitmap: ImageBitmap): LoadedImage? =
            runCatching { parseOrThrow(metadata, imageBitmap) }.getOrNull()

        fun parseOrThrow(metadata: ImageMetadata, imageBitmap: ImageBitmap): LoadedImage {
            require(imageBitmap.width > 0) { "imageBitmap width must be greater than zero" }
            require(imageBitmap.height > 0) { "imageBitmap height must be greater than zero" }
            require(metadata.width == imageBitmap.width) {
                "metadata width must match imageBitmap width"
            }
            require(metadata.height == imageBitmap.height) {
                "metadata height must match imageBitmap height"
            }
            return LoadedImage(metadata = metadata, imageBitmap = imageBitmap)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoadedImage) return false
        return metadata == other.metadata && imageBitmap == other.imageBitmap
    }

    override fun hashCode(): Int {
        var result = metadata.hashCode()
        result = 31 * result + imageBitmap.hashCode()
        return result
    }

    override fun toString(): String =
        "LoadedImage(metadata=$metadata, imageBitmap=$imageBitmap)"
}
