package palbp.demos.pc.isel.coroutines

import org.slf4j.LoggerFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Implementing our homemade `yield` function to illustrate the concept of suspension and resumption
 * while scheduling continuations on the same thread
 * (first in the main thread and then on a single-threaded executor)
 */

private val logger = LoggerFactory.getLogger("Lets Yield demo")

private var continuations = mutableListOf<Continuation<Unit>>()
private val noOpCompletion = Continuation<Unit>(EmptyCoroutineContext) {
    logger.info("Continuation completed with result: $it")
}

suspend fun yield(): Unit = TODO()

fun main() {
    TODO()
}