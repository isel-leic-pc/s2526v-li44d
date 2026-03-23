package palbp.demos.pc.isel.imageviewer.processing.filters

/**
 * Grayscale conversion using integer luminance approximation.
 */
fun applyGrayscaleToPixel(argb: Int): Int {
    val alpha = alphaOf(argb)
    val red = redOf(argb)
    val green = greenOf(argb)
    val blue = blueOf(argb)
    val luminance = ((77 * red) + (150 * green) + (29 * blue)) shr 8
    return composeArgb(
        alpha = alpha,
        red = luminance,
        green = luminance,
        blue = luminance,
    )
}
