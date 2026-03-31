package isel.pc.palbp.labs.lab3

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * This should demonstrate the deadlock anomalies that can occur when using locks.
 */
fun main() {
    val a = Account(1000)
    val b = Account(1000)
    val threads = mutableListOf<Thread>()
    repeat(10) {
        threads += Thread {
            repeat(1000) {
                transferAtoB(a, b, 1)
            }
        }
        threads += Thread {
            repeat(1000) {
                transferBtoA(a, b, 1)
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    // If no deadlocks occur, are the balances correct?
    println("Done. A: ${a.balance}, B: ${b.balance}")
}

/**
 * A simple account class.
 * @property balance The account balance.
 * @property guard The lock used to synchronize access to the account.
 */
class Account(var balance: Int) {
    val guard = ReentrantLock()
}

fun transferAtoB(a: Account, b: Account, amount: Int) {
    a.guard.withLock {
        // A question to ponder upon:
        // Why using a delay between lock acquisitions (even a tiny one) increases deadlock probability?
        Thread.sleep(1)
        b.guard.withLock {
            if (a.balance >= amount) {
                a.balance -= amount
                b.balance += amount
            }
        }
    }
}

fun transferBtoA(a: Account, b: Account, amount: Int) {
    b.guard.withLock {
        // A question to ponder upon:
        // Why using a delay between lock acquisitions (even a tiny one) increases deadlock probability?
        Thread.sleep(1)
        a.guard.withLock {
            if (b.balance >= amount) {
                b.balance -= amount
                a.balance += amount
            }
        }
    }
}
