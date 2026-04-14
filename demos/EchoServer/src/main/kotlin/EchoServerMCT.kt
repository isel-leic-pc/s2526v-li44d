package palbp.demos.pc.isel

import org.slf4j.Logger
import java.net.ServerSocket
import kotlin.concurrent.thread

/**
 * A multithreaded echo server that listens on a specified port and spawns a new thread for each client connection.
 * This approach has several limitations:
 * - It creates a new thread per client. This can lead to a high number of threads, which can cause performance issues.
 * - Because the work is I/O bound, the thread is mostly blocked waiting for I/O operations.
 */
fun runEchoServerMT(port: Int, logger: Logger) {
    logger.info("Starting multi-threaded echo server on port $port...")
    // Launches infinite server loop spawning per-client threads
    ServerSocket(port).use { serverSocket ->
        logger.info("Echo server running on port $port")
        var clientCounter = 0
        while (true) {
            val clientSocket = serverSocket.accept()
            clientCounter += 1
            thread(name = "client-$clientCounter") {
                handleClient(socket = clientSocket, clientId = clientCounter, logger = logger)
            }
        }
    }
}
