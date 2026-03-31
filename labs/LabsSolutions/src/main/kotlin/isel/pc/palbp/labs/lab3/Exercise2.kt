package isel.pc.palbp.labs.lab3

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * This demonstrates deadlock-free transfers using global lock ordering.
 */
fun main() {
    val a = SafeSimpleBankAccount(1000)
    val b = SafeSimpleBankAccount(1000)
    val threads = mutableListOf<Thread>()
    repeat(10) {
        threads += Thread {
            repeat(1000) {
                a.transfer(to = b, amount = 1)
            }
        }
        threads += Thread {
            repeat(1000) {
                b.transfer(to = a, amount = 1)
            }
        }
    }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    val total = a.balance + b.balance
    println("Done. A: ${a.balance}, B: ${b.balance}, Total: $total")
}

/**
 * A simple bank account class with deadlock-free transfer.
 * @property balance The account balance.
 */
class SafeSimpleBankAccount(private var _balance: Int) {

    private val guard = ReentrantLock()

    val balance: Int
        get() { guard.withLock { return _balance } }

    /**
     * Transfers [amount] from this account to [to], using global lock ordering.
     * @param to The account to transfer to.
     * @param amount The amount to transfer.
     */
    fun transfer(to: SafeSimpleBankAccount, amount: Int) {
        val (first, second) = if (hashCode() < to.hashCode()) this to to else to to this
        first.guard.withLock {
            Thread.sleep(1)
            second.guard.withLock {
                if (_balance >= amount) {
                    _balance -= amount
                    to._balance += amount
                }
            }
        }
    }
}
