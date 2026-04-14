package palbp.demos.pc.isel

import org.slf4j.Logger
import java.net.ServerSocket
import java.util.concurrent.Executors


/**
 * Runs a home-made thread pool echo server that listens on a specified port.
 */
fun runEchoServerHMTP(port: Int, logger: Logger) {
    val threadPool = SimpleThreadPool()
    logger.info("Starting thread pool echo server on port $port...")
    ServerSocket(port).use { serverSocket ->
        logger.info("Echo server running on port $port")
        var clientCounter = 0
        while (true) {
            val clientSocket = serverSocket.accept()
            clientCounter += 1
            threadPool.submit {
                handleClient(socket = clientSocket, clientId = clientCounter, logger = logger)
            }
        }
    }
}
