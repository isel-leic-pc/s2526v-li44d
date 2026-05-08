package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

/**
 * Hello Structured Concurrency - Demo used to illustrate the hierarchical relationship between coroutines.
 *
 * 1. Start by creating two root coroutines, each with two child coroutines. Execute and observe. Use the launch
 * coroutine builder function. Make sure that students see that coroutines completion depends on the completion of
 * their children. This can be done by making the runBlocking Job synchronize with the root Jobs.
 * 2. Present the state diagram of a coroutine. (successful completion)
 * 3. Cancel one of the root coroutines. Execute and observe.
 * 4. Cancel one of the child coroutines. Execute and observe.
 * 5. Revisit the state diagram of a coroutine. (cancellation-related states are now included too)
 * 6. Make one of the child coroutines generate an exception. Execute and observe.
 * 7. Proceed to the Hello Scopes demo
 */

private val logger = LoggerFactory.getLogger("Hello Structured Concurrency")

fun main() {
    logger.info("main starts")
    runBlocking(CoroutineName("Root")) {
        logger.info("${coroutineContext[CoroutineName]?.name} starts")

        val parent1Job = launch(CoroutineName("Parent1")) {
            logger.info("${coroutineContext[CoroutineName]?.name} starts")
            launch(CoroutineName("Parent1::Child1")) {
                delay(10.seconds)
                logger.info("${coroutineContext[CoroutineName]?.name} will fail")
                throw Exception("Child1 failed")
            }

            launch(CoroutineName("Parent1::Child2")) {
                delay(20.seconds)
                logger.info("${coroutineContext[CoroutineName]?.name} will end")
            }

            logger.info("${coroutineContext[CoroutineName]?.name} ends")
        }

        parent1Job.invokeOnCompletion { logger.info("Parent1 Job completed with result: $it") }

        parent1Job.join()
        logger.info("${coroutineContext[CoroutineName]?.name} ends")
    }
    logger.info("main ends")
}