package palbp.demos.pc.isel.imageviewer.processing

object SupportedImageFileTypes {
    val extensions: Set<String> = setOf("png", "jpg", "jpeg")

    fun isSupportedExtension(extension: String): Boolean =
        extension.lowercase() in extensions

    fun isSupportedFileName(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "")
        return isSupportedExtension(extension)
    }
}
