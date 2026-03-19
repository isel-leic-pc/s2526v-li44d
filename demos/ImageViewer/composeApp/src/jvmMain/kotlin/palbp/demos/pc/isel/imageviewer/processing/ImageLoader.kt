package palbp.demos.pc.isel.imageviewer.processing

import palbp.demos.pc.isel.imageviewer.domain.LoadedImage

/**
 * Blocking image loading abstraction used by ViewModel implementations.
 */
fun interface ImageLoader {
    @Throws(Exception::class)
    fun loadBlocking(imagePath: String): LoadedImage
}

