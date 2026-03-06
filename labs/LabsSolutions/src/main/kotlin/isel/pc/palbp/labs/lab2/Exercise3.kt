package isel.pc.palbp.labs.lab2

fun flawedParallelSum(values: IntArray, nBlocks: Int): Long {
    require(nBlocks > 0)
    val partials = LongArray(size = nBlocks)
    val blockSize = values.size / nBlocks
    val threads = Array(size = nBlocks) { block ->
        val start = block * blockSize
        val end = if (block == nBlocks - 1) values.size else (block + 1) * blockSize
        Thread {
            var sum = 0L
            for (i in start until end) {
                sum += values[i]
            }
            partials[block] = sum
        }
    }
    threads.forEach { it.start() }
    // ERROR: Join is missing
    Thread.sleep(10)
    return partials.sum()
}

fun main() {
    val values = IntArray(500_000_000) { 1 }
    val nBlocks = 8
    val sum = flawedParallelSum(values, nBlocks)
    println("Sum of values: $sum")
}

