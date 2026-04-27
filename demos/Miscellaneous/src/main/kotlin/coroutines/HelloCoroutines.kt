package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

private val logger: Logger = LoggerFactory.getLogger("HelloCoroutines")

fun main() {

    logger.info("main starts")

    runBlocking {

        logger.info("Launching first coroutine from runBlocking")
        launch {
            logger.info("Hello from first coroutine")
            delay(2.seconds)
            logger.info("First coroutine is done")
        }

        logger.info("Launching second coroutine from runBlocking")
        launch {
            logger.info("Hello from second coroutine")
            delay(4.seconds)
            logger.info("Second coroutine is done")
        }

        logger.info("runBlocking ends")
    }

    Thread.sleep(10000)
    logger.info("main ends")
}
