package isel.pc.palbp.labs.lab1

fun main() {
    println("Main starts")
    val sleeper = Thread({
        println("start")
        Thread.sleep(2000)
        println("end")
    }, "sleeper")

    startAndObserve(target = sleeper, pollIntervalMs = 10)
    println("Main ends")
}
