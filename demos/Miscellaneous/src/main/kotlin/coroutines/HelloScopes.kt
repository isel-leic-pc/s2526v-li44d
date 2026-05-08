package palbp.demos.pc.isel.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Hello scopes - Demo used to illustrate the concept of scopes and their role in the coroutines' library.
 *
 * 1. Start by describing the concept of a scope, relating it with its context.
 * 2. Illustrate the use of the `coroutineScope`by using it to launch two parallel jobs.
 *  2.1 - The jobs' results are not required, only whether they are completed or not. (i.e., using `launch`)
 *  2.2 - The jobs' results are required ((i.e., using `async` and `Deferred`))
 */

suspend fun launchParallelJobs(): Unit = TODO()

fun main() = runBlocking {
}