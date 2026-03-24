package palbp.demos.pc.isel.pattern

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread


fun main() {

    val queue: BlockingQueue<Int> = LinkedBlockingQueue()

    repeat(times = 10) {
        thread {
            println("thread $it is producing")
            queue.put(it)
        }
    }

    while (true) {
        val element = queue.take()
        println("main thread got $element")
    }
}