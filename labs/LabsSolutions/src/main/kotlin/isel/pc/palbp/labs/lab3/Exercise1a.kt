package isel.pc.palbp.labs.lab3

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * This should demonstrate the deadlock anomalies that can occur when using locks.
 * It is a not-so-obvious version of the previous exercise.
 */
fun main() {
    val a = BankAccount(1000)
    val b = BankAccount(1000)
    val threads = mutableListOf<Thread>()
    repeat(10) {
        threads += Thread {
            repeat(1000) {
                a.transferTo(b, 1)
            }
        }
        threads += Thread {
            repeat(1000) {
                b.transferTo(a, 1)
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    println("Done. A: ${a.balance}, B: ${b.balance}")
}

/**
 * A simple bank account class.
 * @property _balance The account balance.
 */
class BankAccount(private var _balance: Int) {
    private val guard = ReentrantLock()

    val balance: Int
        get() = guard.withLock { _balance }

    /**
     * Transfers [amount] from this account to [to].
     * @param to The account to transfer to.
     * @param amount The amount to transfer.
     */
    fun transferTo(to: BankAccount, amount: Int) {
        guard.withLock {
            Thread.sleep(1)
            to.guard.withLock {
                if (_balance >= amount) {
                    _balance -= amount
                    to._balance += amount
                }
            }
        }
    }
}
