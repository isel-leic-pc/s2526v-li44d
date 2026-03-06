package isel.pc.palbp.labs.lab1


fun main() {
    val iterations = 1000
    runLight(iterations)
}

private fun runLight(iterations: Int) {
    val runLight: (Int) -> Unit = { count ->
        for (i in 1..count) {
            if (i % 100 == 0) println("${Thread.currentThread().name}$i")
            Thread.sleep(1)
        }
    }
    val threadA2 = Thread({ runLight(iterations) }, "A2")
    val threadB2 = Thread({ runLight(iterations) }, "B2")

    threadA2.start()
    threadB2.start()
    threadA2.join()
    threadB2.join()
}

private fun runHeavy(iterations: Int) {
    // Variant 1: Heavy instrumentation (print every iteration)
    val runHeavy: (Int) -> Unit = { count ->
        for (i in 1..count) {
            println("${Thread.currentThread().name}$i")
            Thread.sleep(1)
        }
    }
    val threadA1 = Thread({ runHeavy(iterations) }, "A1")
    val threadB1 = Thread({ runHeavy(iterations) }, "B1")

    threadA1.start()
    threadB1.start()
    threadA1.join()
    threadB1.join()
}
