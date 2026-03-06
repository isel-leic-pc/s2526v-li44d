package isel.pc.palbp.labs.lab2

import isel.pc.palbp.labs.lab1.startAndObserve

fun main() {
    println("Main waits for instrumentation to start")
    Thread.sleep(30000)

    println("Main starts")
    val worker = Thread({
        Thread.sleep(2000)
    }, "worker")

    val waiter = Thread({
        worker.start()
        worker.join()
        println("worker finished")
    }, "waiter")

    startAndObserve(waiter)
}
