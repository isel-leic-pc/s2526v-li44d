package palbp.demos.pc.isel

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Thread-safe cache implementation. This implementation is for demonstration purposes only. In actual production
 * code we would need an eviction policy to prevent the cache from growing unboundedly.
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of values maintained by this cache
 * @param compute the function used to compute a value if none is present in the cache for a given key. It is
 * expected to have a high computation cost, otherwise using a cache would be pointless.
 */
class ThreadSafeCache<K, V>(private val compute: (K) -> V) {

    /**
     * The entry in the cache.
     */
    private class CacheEntry<K, V>(private val compute: (K) -> V) {

        /**
         * The cached value if already computed. Null otherwise.
         */
        private var value: V? = null

        /**
         * The lock used to synchronize access to the cached value.
         */
        private val guard: ReentrantLock = ReentrantLock()

        /**
         * Returns the cached value if already computed. Otherwise, computes it and caches it. The lock prevents the
         * computation from being executed concurrently and more than once.
         * @param key the key for which the value is requested
         * @return the cached value, computing it if necessary.
         */
        fun getValue(key: K): V = guard.withLock {
            value ?: compute(key).also { value = it }
        }
    }

    /**
     * The cache itself, guarded by [guard].
     */
    private val cache = HashMap<K, CacheEntry<K, V>>()

    /**
     * The lock used to synchronize access to the cache.
     */
    private val guard = ReentrantLock()

    /**
     * Returns the value for the given key. If the value is not already present in the cache, it is computed and
     * cached.
     * @param key the key for which the value is requested
     * @return the value for the given key
     */
    fun getValue(key: K): V {

        val entry: CacheEntry<K, V> = guard.withLock {
            val res: CacheEntry<K, V>? = cache[key]
            res ?: CacheEntry(compute = compute).also { cache[key] = it }
        }

        return entry.getValue(key)
    }
}