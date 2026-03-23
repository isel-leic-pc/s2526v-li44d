package palbp.demos.pc.isel.imageviewer.processing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import palbp.demos.pc.isel.imageviewer.viewmodel.createPlaceholderLoadedImage
import palbp.demos.pc.isel.imageviewer.domain.ProcessingConfig

class ImagePipelineContractTest {

    @Test
    fun `pipeline execution settings parseOrThrow rejects non-positive worker count`() {
        assertFailsWith<IllegalArgumentException> {
            PipelineExecutionSettings.parseOrThrow(workerCount = 0)
        }
    }

    @Test
    fun `pipeline execution settings parseOrThrow accepts positive worker count`() {
        val settings = PipelineExecutionSettings.parseOrThrow(workerCount = 2)

        assertEquals(2, settings.workerCount)
    }

    @Test
    fun `pipeline execution settings parseOrThrow rejects worker count above maximum`() {
        val invalidWorkerCount = PipelineExecutionSettings.MaxWorkerCount + 1

        assertFailsWith<IllegalArgumentException> {
            PipelineExecutionSettings.parseOrThrow(workerCount = invalidWorkerCount)
        }
    }

    @Test
    fun `pipeline execution settings parse returns null for invalid worker count`() {
        val settings = PipelineExecutionSettings.parse(workerCount = -1)

        assertNull(settings)
    }

    @Test
    fun `pipeline execution settings parse returns value for valid worker count`() {
        val settings = PipelineExecutionSettings.parse(workerCount = 1)

        assertNotNull(settings)
        assertEquals(1, settings.workerCount)
    }

    @Test
    fun `pipeline execution settings default worker count is positive`() {
        val defaultSettings = PipelineExecutionSettings.Default

        assertTrue(defaultSettings.workerCount >= PipelineExecutionSettings.MinWorkerCount)
        assertTrue(defaultSettings.workerCount <= PipelineExecutionSettings.MaxWorkerCount)
    }

    @Test
    fun `image pipeline input defaults to default execution settings`() {
        val input = ImagePipelineInput(
            source = createPlaceholderLoadedImage("sample.png"),
            config = ProcessingConfig.Default,
        )

        assertEquals(PipelineExecutionSettings.Default, input.executionSettings)
    }
}
