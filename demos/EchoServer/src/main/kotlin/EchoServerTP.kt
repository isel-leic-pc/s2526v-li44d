package palbp.demos.pc.isel

import org.slf4j.Logger
import java.net.ServerSocket
import java.util.concurrent.Executors


/**
 * Runs a thread pool echo server that listens on a specified port.
 * This approach has several limitations:
 * - It creates a fixed number of threads based on the number of available processors.
 */
fun runEchoServerTP(port: Int, logger: Logger) {
    val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    logger.info("Starting thread pool echo server on port $port...")
    ServerSocket(port).use { serverSocket ->
        logger.info("Echo server running on port $port")
        var clientCounter = 0
        while (true) {
            val clientSocket = serverSocket.accept()
            clientCounter += 1
            threadPool.execute {
                handleClient(socket = clientSocket, clientId = clientCounter, logger = logger)
            }
        }
    }
}
