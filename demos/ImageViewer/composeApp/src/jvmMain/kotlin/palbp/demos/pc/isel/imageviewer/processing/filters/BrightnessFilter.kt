package palbp.demos.pc.isel.imageviewer.processing.filters

import palbp.demos.pc.isel.imageviewer.domain.BrightnessDelta

/**
 * Brightness filter that offsets sRGB channels by a signed delta.
 */
fun applyBrightnessToPixel(argb: Int, delta: BrightnessDelta): Int {
    val alpha = alphaOf(argb)
    val red = redOf(argb) + delta.value
    val green = greenOf(argb) + delta.value
    val blue = blueOf(argb) + delta.value

    return composeArgb(
        alpha = alpha,
        red = red,
        green = green,
        blue = blue,
    )
}
