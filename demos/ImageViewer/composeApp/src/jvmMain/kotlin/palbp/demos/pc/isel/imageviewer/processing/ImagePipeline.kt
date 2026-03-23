package palbp.demos.pc.isel.imageviewer.processing

import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import palbp.demos.pc.isel.imageviewer.domain.ProcessingConfig

/**
 * Pipeline contract for image processing.
 */
fun interface ImagePipeline {

    /**
     * Processes the given [input] and returns the result.
     * @throws ImageProcessingException if processing fails.
     */
    @Throws(ImageProcessingException::class)
    fun process(input: ImagePipelineInput): LoadedImage
}

/**
 * Input payload consumed by [ImagePipeline] implementations.
 */
data class ImagePipelineInput(
    val source: LoadedImage,
    val config: ProcessingConfig,
    val executionSettings: PipelineExecutionSettings = PipelineExecutionSettings.Default,
)

/**
 * Execution tuning parameters for pipeline implementations.
 */
data class PipelineExecutionSettings(
    val workerCount: Int,
) {
    init {
        require(workerCount in MinWorkerCount..MaxWorkerCount) {
            "workerCount must be in [$MinWorkerCount, $MaxWorkerCount], but was $workerCount"
        }
    }

    companion object {
        const val MinWorkerCount: Int = 1
        val AvailableProcessorCount: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        val MaxWorkerCount: Int = AvailableProcessorCount * 8

        val Default: PipelineExecutionSettings = PipelineExecutionSettings(
            workerCount = AvailableProcessorCount,
        )

        fun parse(workerCount: Int): PipelineExecutionSettings? =
            runCatching { parseOrThrow(workerCount) }.getOrNull()

        fun parseOrThrow(workerCount: Int): PipelineExecutionSettings =
            PipelineExecutionSettings(workerCount = workerCount)
    }
}

/**
 * Exception thrown by [ImagePipeline] implementations when processing fails.
 */
class ImageProcessingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
