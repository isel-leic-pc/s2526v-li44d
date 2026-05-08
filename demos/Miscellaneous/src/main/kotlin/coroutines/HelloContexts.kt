package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

/**
 * Hello contexts - Demo used to illustrate the concept of contexts and their role in the coroutines' library.
 *
 * 1. Start by describing the concept of a context.
 * 2. Next, present the `CoroutineName` context element as an example using the starting code provided.
 * 3. Create a new coroutine using the launch coroutine builder function:
 *      3.1. - Not providing a new `CoroutineName` context element to illustrate inheritance of context elements
 *      3.2. - Providing a new `CoroutineName` context element (i.e. "Parent"), to illustrate overriding of context elements
 *      3.3. - Elaborate by launching two new coroutines as children of the current one (i.e. "Child1" and "Child2")
 * 4. Proceed to the Hello Structured Concurrency demo
 */

private val logger = LoggerFactory.getLogger("Hello Contexts")

fun main() = runBlocking(CoroutineName("Main")) {
    logger.info("${coroutineContext[CoroutineName]?.name} starts.")

    launch(CoroutineName("Child1")) {
        logger.info("${coroutineContext[CoroutineName]?.name} starts.")
        delay(5.seconds)
        logger.info("${coroutineContext[CoroutineName]?.name} ends.")
    }

    launch(CoroutineName("Child2")) {
        logger.info("${coroutineContext[CoroutineName]?.name} starts.")
        delay(3.seconds)
        logger.info("${coroutineContext[CoroutineName]?.name} ends.")
    }

    logger.info("${coroutineContext[CoroutineName]?.name} ends.")
}