package palbp.demos.pc.isel

fun main() {
    val dataset = (1..200_000_000).toList()
    val start = System.nanoTime()
    println("Starting counting...")
    val count = count(dataset, 5)
    val end = System.nanoTime() - start
    println("Count is $count. Took ${end / 1_000_000} ms")
}