package palbp.demos.pc.isel.coroutines

import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Implementing our homemade `delay` function to illustrate the concept of suspension and resumption
 * while scheduling continuations on a scheduled excutor service
 * (first in a single-threaded scheduled executor, then on a multithreaded one)
 */

private val logger = LoggerFactory.getLogger("Lets Delay demo")

private var continuations = LinkedBlockingQueue<Continuation<Unit>>()
private val scheduler = Executors.newScheduledThreadPool(2)


private suspend fun delay(duration: Duration): Unit {
    suspendCoroutine {
        scheduler.schedule(
            { it.resume(Unit) },
            duration.inWholeMilliseconds,
            TimeUnit.MILLISECONDS
        )
    }
}


private suspend fun coroutineBody1() {
    val messages = listOf("Hello", "World")
    messages.forEach {
        logger.info(it)
        delay(2.seconds)
    }
}

private suspend fun coroutineBody2() {
    val messages = listOf("Olá", "Mundo")
    messages.forEach {
        logger.info(it)
        delay(2.seconds)
    }
}

private fun schedulerLoop(functions: List<suspend () -> Unit>) {
    val noOpCompletion = Continuation<Unit>(EmptyCoroutineContext) {
        logger.info("Continuation completed with result: $it")
    }

    functions.forEach {
        it.startCoroutine(noOpCompletion)
    }

    while(true) {
        val next = continuations.take()
        next.resume(Unit)
    }
}

fun main() {
    logger.info("main starts")

    scheduler.execute {
        schedulerLoop(
            functions = listOf(::coroutineBody1, ::coroutineBody2),
        )
    }

    logger.info("main ends")
}
