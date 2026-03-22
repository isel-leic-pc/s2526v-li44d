package palbp.demos.pc.isel.imageviewer.processing

import androidx.compose.ui.graphics.toComposeImageBitmap
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.extension
import kotlin.io.path.name
import org.jetbrains.skia.Image
import palbp.demos.pc.isel.imageviewer.domain.ImageMetadata
import palbp.demos.pc.isel.imageviewer.domain.LoadedImage

/**
 * Loads PNG/JPEG images from the local file system into Compose-compatible image data.
 */
class LocalFileImageLoader : ImageLoader {

    @Throws(ImageLoadException::class)
    override fun loadBlocking(imagePath: String): LoadedImage {
        val path = Paths.get(imagePath)
        val fileName = path.name

        ensureSupportedExtension(path)

        val encodedBytes = try {
            Files.readAllBytes(path)
        } catch (_: NoSuchFileException) {
            throw ImageLoadException(
                kind = ImageLoadErrorKind.FILE_NOT_FOUND,
                fileName = fileName,
            )
        } catch (ex: Exception) {
            throw ImageLoadException(
                kind = ImageLoadErrorKind.FILE_READ_FAILED,
                fileName = fileName,
                cause = ex,
            )
        }

        val decoded = runCatching { Image.makeFromEncoded(encodedBytes) }
            .getOrElse { cause ->
                throw ImageLoadException(
                    kind = ImageLoadErrorKind.DECODE_FAILED,
                    fileName = fileName,
                    cause = cause,
                )
            }

        val imageBitmap = decoded.toComposeImageBitmap()
        val metadata = ImageMetadata.parseOrThrow(
            fileName = fileName,
            width = imageBitmap.width,
            height = imageBitmap.height,
        )

        return LoadedImage.parseOrThrow(
            metadata = metadata,
            imageBitmap = imageBitmap,
        )
    }

    @Throws(ImageLoadException::class)
    private fun ensureSupportedExtension(path: Path) {
        val extension = path.extension.lowercase()
        if (SupportedImageFileTypes.isSupportedExtension(extension)) return
        throw ImageLoadException(
            kind = ImageLoadErrorKind.UNSUPPORTED_FORMAT,
            fileName = path.name,
        )
    }
}

enum class ImageLoadErrorKind {
    UNSUPPORTED_FORMAT,
    FILE_NOT_FOUND,
    FILE_READ_FAILED,
    DECODE_FAILED,
}

class ImageLoadException(
    val kind: ImageLoadErrorKind,
    val fileName: String? = null,
    cause: Throwable? = null,
) : Exception(defaultMessage(kind = kind, fileName = fileName), cause)

private fun defaultMessage(kind: ImageLoadErrorKind, fileName: String?): String = when (kind) {
    ImageLoadErrorKind.UNSUPPORTED_FORMAT ->
        "Unsupported image format. Please select a PNG or JPEG file."
    ImageLoadErrorKind.FILE_NOT_FOUND ->
        "File not found: ${fileName ?: "<unknown>"}"
    ImageLoadErrorKind.FILE_READ_FAILED ->
        "Unable to read file: ${fileName ?: "<unknown>"}"
    ImageLoadErrorKind.DECODE_FAILED ->
        "Unable to decode image file: ${fileName ?: "<unknown>"}"
}
