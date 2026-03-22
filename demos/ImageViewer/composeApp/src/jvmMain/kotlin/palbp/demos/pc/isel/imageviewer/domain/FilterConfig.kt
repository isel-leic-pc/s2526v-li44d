package palbp.demos.pc.isel.imageviewer.domain

/**
 * Immutable processing configuration used by the ViewModel -> pipeline boundary.
 *
 * This milestone includes only grayscale and brightness. The container type is
 * intentionally explicit, so upcoming milestones can extend it with new filters.
 */
data class ProcessingConfig(
    val grayscale: GrayscaleFilterConfig,
    val brightness: BrightnessFilterConfig,
) {
    companion object {
        val Default: ProcessingConfig = ProcessingConfig(
            grayscale = GrayscaleFilterConfig.Default,
            brightness = BrightnessFilterConfig.Default,
        )
    }
}

/**
 * On/off configuration for grayscale processing.
 */
data class GrayscaleFilterConfig(
    val enabled: Boolean,
) {
    companion object {
        val Default: GrayscaleFilterConfig = GrayscaleFilterConfig(enabled = false)
    }
}

/**
 * Brightness delta in sRGB channel integer space.
 *
 * The accepted range keeps computations bounded for byte-based channel clamping.
 */
@JvmInline
value class BrightnessDelta private constructor(val value: Int) {
    companion object {
        const val MinValue: Int = -255
        const val MaxValue: Int = 255
        val Neutral: BrightnessDelta = BrightnessDelta(0)

        fun parse(value: Int): BrightnessDelta? =
            runCatching { parseOrThrow(value) }.getOrNull()

        fun parseOrThrow(value: Int): BrightnessDelta {
            require(value in MinValue..MaxValue) {
                "brightness delta must be in [$MinValue, $MaxValue], but was $value"
            }
            return BrightnessDelta(value)
        }
    }
}

/**
 * Configuration for brightness processing.
 */
data class BrightnessFilterConfig(
    val enabled: Boolean,
    val delta: BrightnessDelta,
) {
    companion object {
        val Default: BrightnessFilterConfig = BrightnessFilterConfig(
            enabled = false,
            delta = BrightnessDelta.Neutral,
        )
    }
}
