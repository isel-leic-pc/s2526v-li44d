package palbp.demos.pc.isel

import org.slf4j.Logger
import java.net.Socket

/**
 * Handles a client connection by reading and writing data to the client socket.
 * This function is designed to be run in a separate thread for each client connection.
 *
 * @param socket The client socket. It is automatically closed when the function returns.
 * @param clientId The client ID.
 */
fun handleClient(socket: Socket, clientId: Int, logger: Logger) {

    logger.info("Client $clientId connected")
    socket.use { client ->
        val input = client.getInputStream().bufferedReader()
        val output = client.getOutputStream().bufferedWriter()
        try {
            var line: String?
            while (input.readLine().also { line = it } != null) {
                with(output) {
                    write("echo> $line")
                    newLine()
                    flush()
                }
            }
        } catch (e: Exception) {
            logger.error("Error with client $clientId: ${e.message}")
        } finally {
            logger.info("Client $clientId disconnected")
        }
    }
}