package palbp.demos.pc.isel

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

const val INC_PER_THREAD = 10_000_000
const val THREAD_COUNT = 4


class SafeCounter {
    private val guard: ReentrantLock = ReentrantLock()
    private var count = 0

    fun inc() {
        guard.withLock {
            count += 1

        }
    }

    fun getCount(): Int = guard.withLock { count }

    fun dec() {
        guard.withLock {
            count -= 1
        }
    }
}

fun main() {

    val count = SafeCounter()

    val threads = Array(size = THREAD_COUNT) {
        thread {
            repeat(times = INC_PER_THREAD) {
                count.inc()
            }
        }
    }


    threads.forEach { it.join() }
    println(count)
}