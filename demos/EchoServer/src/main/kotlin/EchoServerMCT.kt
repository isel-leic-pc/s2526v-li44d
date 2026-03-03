package palbp.demos.pc.isel

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

val serverPort = 9000

fun main() {
    val serverPort = 9000
    // Launches infinite server loop spawning per-client threads
    ServerSocket(serverPort).use { serverSocket ->
        println("Echo server running on port $serverPort")
        var clientCounter = 0
        while (true) {
            val clientSocket = serverSocket.accept()
            clientCounter += 1
            val thread = Thread(
                { handleClient(clientSocket, clientCounter) },
                "client-$clientCounter"
            )
            thread.start()
        }
    }
}

fun handleClient(socket: Socket, clientId: Int) {
    println("Client $clientId connected")
    socket.use { client ->
        val input = client.getInputStream().bufferedReader()
        val output = client.getOutputStream().bufferedWriter()
        try {
            var line: String?
            while (input.readLine().also { line = it } != null) {
                output.write(line)
                output.newLine()
                output.flush()
            }
        } catch (e: Exception) {
            println("Error with client $clientId: ${e.message}")
        } finally {
            println("Client $clientId disconnected")
        }
    }
}
