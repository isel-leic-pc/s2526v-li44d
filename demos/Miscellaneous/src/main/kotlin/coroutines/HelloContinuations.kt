package palbp.demos.pc.isel.coroutines

import kotlin.coroutines.*

val theLastContinuation = Continuation<Unit>(EmptyCoroutineContext) {
    println("The last continuation was resumed with $it")
}

var theContinuation: Continuation<Unit>? = null

suspend fun myCoroutineBody() {

    println("Hello from myCoroutineBody")

    suspendCoroutine { cont ->
        println("Suspending myCoroutineBody")
        theContinuation = cont
    }

    println("Continuing myCoroutineBody")
}

fun main() {

    ::myCoroutineBody.startCoroutine(theLastContinuation)

    val shouldRun = readln()
    if (shouldRun == "run")
        theContinuation?.resume(Unit)

}