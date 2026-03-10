package palbp.demos.pc.isel

import kotlin.concurrent.thread

fun main() {

    val threadCount = 4
    val list = SafeLinkedList<Int>()

    val threads = Array(size = threadCount) {
        thread {
            repeat(times = 1_000_000) {
                list.addLast(1)
            }
        }
    }

    threads.forEach { it.join() }

    println(list.size)
    println(list.sum())
}