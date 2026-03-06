package isel.pc.palbp.labs.lab2

fun parallelSum(values: IntArray, nBlocks: Int): Long {
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
    threads.forEach { it.join() }
    return partials.sum()
}

/**
 * This implementation is flawed because of false sharing. Each partial is directly accumulated
 * on the partials array. This leads to too many cache invalidations, degrading performance.
 * The accumulation should be done in a per-thread private variable and, finally, published to the
 * partials array.
 */
fun parallelSumWithContention(values: IntArray, nBlocks: Int): Long {
    require(nBlocks > 0)
    val partials = LongArray(size = nBlocks)
    val blockSize = values.size / nBlocks
    val threads = Array(size = nBlocks) { block ->
        val start = block * blockSize
        val end = if (block == nBlocks - 1) values.size else (block + 1) * blockSize
        Thread {
            for (i in start until end) {
                partials[block] += values[i]
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    return partials.sum()
}


fun main() {
    val values = IntArray(500_000_000) { 1 } // Array from 1 to 1,000,000
    val nBlocks = 8
    val sum = parallelSum(values, nBlocks)
    println("Sum of values: $sum")
}

