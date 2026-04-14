package palbp.demos.pc.isel.threadpool

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {

    println("Starting demo in thread ${Thread.currentThread().name}")
    val pool = Executors.newFixedThreadPool(4)
    pool.submit {
        println("Hi from thread ${Thread.currentThread().name}")
        Thread.sleep(8000)
    }

    pool.shutdown()
    pool.awaitTermination(10, TimeUnit.SECONDS)
}