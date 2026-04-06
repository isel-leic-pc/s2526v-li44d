package isel.pc.palbp.labs

fun Thread.isWaiting() = state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING
fun Thread.isNotWaiting() = isWaiting().not()

fun Iterable<Thread>.waitForAllWaiting(timeoutMillis: Long = 1000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < deadline) {
        if (all { it.isWaiting() }) return
        Thread.yield()
    }
    throw AssertionError("Not all threads are waiting in the expected timeframe")
}

fun Thread.waitForWaiting(timeoutMillis: Long = 1000) {
    val deadline = System.currentTimeMillis() + timeoutMillis
    while (System.currentTimeMillis() < deadline) {
        if (isWaiting()) return
    }
    throw AssertionError("Thread is not waiting in the expected timeframe")
}