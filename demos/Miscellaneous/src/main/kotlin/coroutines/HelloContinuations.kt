package palbp.demos.pc.isel.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext

val theLastContinuation = Continuation<Unit>(EmptyCoroutineContext) {
    println("The last continuation was resumed with $it")
}

