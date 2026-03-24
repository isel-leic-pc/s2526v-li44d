package palbp.demos.pc.isel.pattern

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

fun doSomething(idx: Int) {
    Thread.sleep(1000)
    if (idx == 5)
        throw Exception()
}

/**
 * Illustrates the Fork-Join pattern, in this case using a CountDownLatch.
 */
fun main() {
    val count = 10
    val latch = CountDownLatch(count)

    repeat(times = count) {
        thread {
            println("Hello from thread $it")
            doSomething(it)
            latch.countDown()
        }
    }

    println("Waiting for all threads to finish")
    latch.await(5L, TimeUnit.SECONDS)
    println("All threads finished")
}