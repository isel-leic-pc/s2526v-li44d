package palbp.demos.pc.isel.imageviewer.processing.filters

import palbp.demos.pc.isel.imageviewer.domain.ProcessingConfig

/**
 * Applies filters that are enabled in deterministic order: grayscale, then brightness.
 */
fun applyFilters(sourcePixels: IntArray, config: ProcessingConfig): IntArray {
    var result = sourcePixels.copyOf()

    if (config.grayscale.enabled) {
        result = IntArray(result.size) { index ->
            applyGrayscaleToPixel(result[index])
        }
    }

    if (config.brightness.enabled) {
        val delta = config.brightness.delta
        result = IntArray(result.size) { index ->
            applyBrightnessToPixel(result[index], delta)
        }
    }

    return result
}

/**
 * Applies filters to a single pixel in deterministic order: grayscale, then brightness.
 */
fun applyFiltersToPixel(sourcePixel: Int, config: ProcessingConfig): Int {
    var result = sourcePixel

    if (config.grayscale.enabled) {
        result = applyGrayscaleToPixel(result)
    }

    if (config.brightness.enabled) {
        result = applyBrightnessToPixel(result, config.brightness.delta)
    }

    return result
}
