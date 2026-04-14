package palbp.demos.pc.isel

import org.slf4j.Logger
import java.net.ServerSocket

/**
 * Runs a single-threaded echo server that listens on a specified port.
 * This is not a realistic implementation of a server.
 */
fun runEchoServerST(port: Int, logger: Logger) {
    logger.info("Starting single-threaded echo server on port $port...")
    ServerSocket(port).use { serverSocket ->
        while (true) {
            logger.info("Waiting for a client...")
            serverSocket.accept().use { clientSocket ->
                logger.info("Client connected: ${clientSocket.inetAddress.hostAddress}")
                val input = clientSocket.getInputStream().bufferedReader()
                val output = clientSocket.getOutputStream().bufferedWriter()
                input.lineSequence().forEach { line ->
                    logger.info("Received: $line")
                    with(output) {
                        write("echo> $line")
                        newLine()
                        flush()
                    }
                }
                logger.info("Client disconnected.")
            }
        }
    }
}