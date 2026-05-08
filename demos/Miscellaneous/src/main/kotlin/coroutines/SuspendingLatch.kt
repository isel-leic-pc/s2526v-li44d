package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


/**
 *
 */
class SuspendingLatch {

    private var open = false
    private val continuations = mutableListOf<Continuation<Unit>>()
    private val guard = Mutex()

    suspend fun open() {
        guard.withLock {
            open = true

            while (continuations.isNotEmpty()) {
                continuations.removeFirst().resume(Unit)
            }
        }
    }

    suspend fun await() {
        guard.lock()
        if (open) {
            guard.unlock()
            return
        }

        // Suspend until open
        suspendCoroutine { cont ->
            continuations.add(cont)
            guard.unlock()
        }
    }
}

fun main() = runBlocking {
    val latch = SuspendingLatch()

    launch {
        repeat(10) {
            println(it)
            delay(1.seconds)
        }
        latch.open()
    }

    latch.await()
    println("Done")
}