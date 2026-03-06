package isel.pc.palbp.labs.lab1

fun startAndObserve(target: Thread, pollIntervalMs: Long = 50) {
    val startTime = System.currentTimeMillis()
    var lastState = target.state

    println("0 ms ${target.name} $lastState")
    target.start()
    while (true) {
        val currentState = target.state
        val timestamp = System.currentTimeMillis() - startTime
        if (currentState != lastState) {
            println("$timestamp ms ${target.name} $currentState")
            lastState = currentState
        }
        if (currentState == Thread.State.TERMINATED) break
        Thread.sleep(pollIntervalMs)
    }
}
