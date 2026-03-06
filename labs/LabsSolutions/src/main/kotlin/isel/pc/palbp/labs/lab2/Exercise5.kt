package isel.pc.palbp.labs.lab2

import kotlin.system.measureNanoTime

fun main() {
    val values = IntArray(500_000_000) { it + 1 }
    val threadCounts = listOf(1, 2, 4, 8, 16, 32, 64)
    val runsPerConfig = 3

    fun measureAndReport(
        label: String,
        sumFunction: (IntArray, Int) -> Long
    ) {
        println("Measuring $label:")
        val times = mutableMapOf<Int, Double>()
        for (n in threadCounts) {
            val avgTime = (1..runsPerConfig).map {
                measureNanoTime { sumFunction(values, n) }
            }.average() / 1_000_000.0
            times[n] = avgTime
            println("Threads: $n, Avg time: %.2f ms".format(avgTime))
        }
        val t1 = times[1] ?: 1.0
        for (n in threadCounts) {
            println("Speedup S($n) = %.2f".format(t1 / (times[n] ?: 1.0)))
        }
        println()
    }

    measureAndReport("parallelSum (low contention)", ::parallelSum)
    measureAndReport("parallelSumWithContention (high contention)", ::parallelSumWithContention)
}