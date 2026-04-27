package palbp.demos.pc.isel.coroutines

import kotlin.time.Duration

/**
 * Implementing our homemade `delay` function to illustrate the concept of suspension and resumption
 * while scheduling continuations on a scheduled excutor service
 * (first in a single-threaded scheduled executor, then on a multithreaded one)
 */

suspend fun delay(duration: Duration): Unit = TODO()

fun main() {
    TODO()
}
