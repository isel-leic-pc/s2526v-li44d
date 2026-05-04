package palbp.demos.pc.isel.coroutines

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * Implementing our homemade `yield` function to illustrate the concept of suspension and resumption
 * while scheduling continuations on the same thread
 * (first in the main thread and then on a single-threaded executor)
 */

private val logger = LoggerFactory.getLogger("Lets Yield demo")

private var continuations = mutableListOf<Continuation<Unit>>()

private suspend fun yield(): Unit {
    suspendCoroutine {
        continuations.add(it)
    }
}


private suspend fun coroutineBody1() {
    val messages = listOf("Hello", "World")
    messages.forEach {
        logger.info(it)
        yield()
    }
}

private suspend fun coroutineBody2() {
    val messages = listOf("Olá", "Mundo")
    messages.forEach {
        logger.info(it)
        yield()
    }
}

private suspend fun coroutineBody3() {
    val messages = listOf("Bonjour", "Le monde")
    messages.forEach {
        logger.info(it)
        yield()
    }
}

private fun schedulerLoop(functions: List<suspend () -> Unit>) {
    val noOpCompletion = Continuation<Unit>(EmptyCoroutineContext) {
        logger.info("Continuation completed with result: $it")
    }

    functions.forEach {
        it.startCoroutine(noOpCompletion)
    }

    while(continuations.isNotEmpty()) {
        val next = continuations.removeFirst()
        next.resume(Unit)
    }
}

private val scheduler = Executors.newFixedThreadPool(3)

fun main() {
    logger.info("main starts")

    scheduler.execute {
        schedulerLoop(
            functions = listOf(::coroutineBody1, ::coroutineBody2, ::coroutineBody3),
        )
    }

    logger.info("main ends")
}