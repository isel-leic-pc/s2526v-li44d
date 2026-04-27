package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {

    runBlocking {

        launch {
            repeat(100_000_000) { i ->
                if (i % 100_000 == 0) {
                    println("First $i")
                }
            }
        }

        launch {
            repeat(100_000_000) { i ->
                if (i % 100_000 == 0) {
                    println("Second $i")
                }
            }
        }
    }

    println("Done")
}