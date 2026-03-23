package palbp.demos.pc.isel.imageviewer.processing.filters

/**
 * Utility functions for ARGB channel extraction and assembly.
 */
const val MIN_CHANNEL_VALUE: Int = 0
const val MAX_CHANNEL_VALUE: Int = 255

fun alphaOf(argb: Int): Int = (argb ushr 24) and 0xFF

fun redOf(argb: Int): Int = (argb ushr 16) and 0xFF

fun greenOf(argb: Int): Int = (argb ushr 8) and 0xFF

fun blueOf(argb: Int): Int = argb and 0xFF

fun composeArgb(alpha: Int, red: Int, green: Int, blue: Int): Int =
    (clampChannel(alpha) shl 24) or
        (clampChannel(red) shl 16) or
        (clampChannel(green) shl 8) or
        clampChannel(blue)

fun clampChannel(value: Int): Int = value.coerceIn(MIN_CHANNEL_VALUE, MAX_CHANNEL_VALUE)
