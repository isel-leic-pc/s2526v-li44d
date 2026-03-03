package palbp.demos.pc.isel

/**
 * Counts [value] occurrences across [dataset] using multithreaded partitioning
 */
fun count(dataset: List<Int>, value: Int): Int {
    val numThreads = Runtime.getRuntime().availableProcessors()
    val partitionSize = dataset.size / numThreads
    val results = IntArray(numThreads)
    val threads = Array(numThreads) { i ->
        Thread({
            val start = i * partitionSize
            val end = if (i == numThreads - 1) dataset.size else start + partitionSize
            var localCount = 0
            for (j in start until end) {
                if (dataset[j] == value) {
                    localCount += 1
                }
            }
            results[i] = localCount
        }, "count-partition-$i")
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }

    return results.sum()
}