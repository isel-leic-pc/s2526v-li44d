package palbp.demos.pc.isel.imageviewer.viewmodel

import palbp.demos.pc.isel.imageviewer.domain.LoadedImage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val DEFAULT_TIMEOUT_MS = 1_000L
private const val POLL_INTERVAL_MS = 10L

private typealias ScriptedAction = () -> LoadedImage

class ImageViewerViewModelTest {

    @Test
    fun `initial state is NoImage`() {
        // Arrange
        val vm = ThreadsImageViewerViewModel()

        // Act
        val state = vm.state

        // Assert
        assertEquals(ImageViewerScreenState.NoImage, state)
    }

    @Test
    fun `requestLoadImage from NoImage enters LoadingImage with NoImage fallback`() {
        // Arrange
        val photoImage = "photo.png"
        val loadedPhoto = sampleLoadedImage(photoImage)
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val loader = ScriptedImageLoader(
            {
                started.countDown()
                assertTrue(release.await(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                loadedPhoto
            },
        )
        val vm = ThreadsImageViewerViewModel(imageLoader = loader)

        // Act
        vm.requestLoadImage(photoImage)

        // Assert
        assertTrue(started.await(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS))
        awaitStateEquals(
            vm = vm,
            expected = ImageViewerScreenState.LoadingImage(fallbackState = FallbackState.NoImage),
        )
        release.countDown()
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(loadedPhoto))
    }

    @Test
    fun `requestLoadImage success transitions to Ready`() {
        // Arrange
        val photoImage = "photo.png"
        val loadedPhoto = sampleLoadedImage(photoImage)
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader({ loadedPhoto }),
        )

        // Act
        vm.requestLoadImage(photoImage)

        // Assert
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(loadedPhoto))
    }

    @Test
    fun `requestLoadImage from Ready enters LoadingImage with Ready fallback`() {
        // Arrange
        val beforeImage = "before.png"
        val afterImage = "after.png"
        val beforeLoaded = sampleLoadedImage(beforeImage)
        val afterLoaded = sampleLoadedImage(afterImage)
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader(
                { beforeLoaded },
                {
                    started.countDown()
                    assertTrue(release.await(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                    afterLoaded
                },
            ),
        )
        vm.requestLoadImage(beforeImage)
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(beforeLoaded))

        // Act
        vm.requestLoadImage(afterImage)

        // Assert
        assertTrue(started.await(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS))
        awaitStateEquals(
            vm = vm,
            expected = ImageViewerScreenState.LoadingImage(
                fallbackState = FallbackState.Ready(beforeLoaded),
            ),
        )
        release.countDown()
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(afterLoaded))
    }

    @Test
    fun `requestLoadImage failure transitions to Error with captured fallback`() {
        // Arrange
        val beforeImage = "before.png"
        val newImage = "new.png"
        val loadFailureMessage = "read failed"
        val beforeLoaded = sampleLoadedImage(beforeImage)
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader(
                { beforeLoaded },
                { error(loadFailureMessage) },
            ),
        )
        vm.requestLoadImage(beforeImage)
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(beforeLoaded))

        // Act
        vm.requestLoadImage(newImage)

        // Assert
        awaitStateEquals(
            vm = vm,
            expected = ImageViewerScreenState.Error(
                message = loadFailureMessage,
                fallbackState = FallbackState.Ready(beforeLoaded),
            ),
        )
    }

    @Test
    fun `dismissError with Ready fallback returns to Ready`() {
        // Arrange
        val beforeImage = "before.png"
        val nextImage = "next.png"
        val genericFailureMessage = "boom"
        val beforeLoaded = sampleLoadedImage(beforeImage)
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader(
                { beforeLoaded },
                { error(genericFailureMessage) },
            ),
        )
        vm.requestLoadImage(beforeImage)
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(beforeLoaded))
        vm.requestLoadImage(nextImage)
        awaitStateEquals(
            vm = vm,
            expected = ImageViewerScreenState.Error(
                message = genericFailureMessage,
                fallbackState = FallbackState.Ready(beforeLoaded),
            ),
        )

        // Act
        vm.dismissError()

        // Assert
        assertEquals(ImageViewerScreenState.Ready(beforeLoaded), vm.state)
    }

    @Test
    fun `dismissError with NoImage fallback returns to NoImage`() {
        // Arrange
        val firstImage = "first.png"
        val genericFailureMessage = "boom"
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader({ error(genericFailureMessage) }),
        )
        vm.requestLoadImage(firstImage)
        awaitStateEquals(
            vm = vm,
            expected = ImageViewerScreenState.Error(
                message = genericFailureMessage,
                fallbackState = FallbackState.NoImage,
            ),
        )

        // Act
        vm.dismissError()

        // Assert
        assertEquals(ImageViewerScreenState.NoImage, vm.state)
    }

    @Test
    fun `reset from Ready transitions to NoImage`() {
        // Arrange
        val sampleImage = "img.png"
        val sampleLoaded = sampleLoadedImage(sampleImage)
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader({ sampleLoaded }),
        )
        vm.requestLoadImage(sampleImage)
        awaitStateEquals(vm = vm, expected = ImageViewerScreenState.Ready(sampleLoaded))

        // Act
        vm.reset()

        // Assert
        assertEquals(ImageViewerScreenState.NoImage, vm.state)
    }

    @Test
    fun `reset from Error transitions to NoImage`() {
        // Arrange
        val sampleImage = "img.png"
        val genericFailureMessage = "boom"
        val vm = ThreadsImageViewerViewModel(
            imageLoader = ScriptedImageLoader({ error(genericFailureMessage) }),
        )
        vm.requestLoadImage(sampleImage)
        awaitState(vm = vm) { it is ImageViewerScreenState.Error }

        // Act
        vm.reset()

        // Assert
        assertEquals(ImageViewerScreenState.NoImage, vm.state)
    }

    @Test
    fun `reset from NoImage throws IllegalStateException`() {
        // Arrange
        val vm = ThreadsImageViewerViewModel()

        // Act + Assert
        assertFailsWith<IllegalStateException> {
            vm.reset()
        }
    }
}

private fun awaitStateEquals(
    vm: ImageViewerViewModel,
    expected: ImageViewerScreenState,
    timeoutMs: Long = DEFAULT_TIMEOUT_MS,
) {
    awaitState(vm = vm, timeoutMs = timeoutMs) { actualState ->
        actualState == expected
    }
}

private fun awaitState(
    vm: ImageViewerViewModel,
    timeoutMs: Long = DEFAULT_TIMEOUT_MS,
    predicate: (ImageViewerScreenState) -> Boolean,
): ImageViewerScreenState {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        val currentState = vm.state
        if (predicate(currentState)) return currentState
        Thread.sleep(POLL_INTERVAL_MS)
    }
    error("Timed out waiting for expected state. Current state: ${vm.state}")
}

private class ScriptedImageLoader(vararg scriptedActions: ScriptedAction) : ImageLoader {
    private val actions = LinkedBlockingQueue<ScriptedAction>()

    init {
        scriptedActions.forEach { actions.put(it) }
    }

    fun enqueue(action: ScriptedAction) {
        actions.put(action)
    }

    override fun loadBlocking(imageName: String): LoadedImage {
        val action = actions.poll(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            ?: error("No scripted loader action available for image '$imageName'")
        return action()
    }
}

private fun sampleLoadedImage(fileName: String): LoadedImage = createPlaceholderLoadedImage(fileName)
