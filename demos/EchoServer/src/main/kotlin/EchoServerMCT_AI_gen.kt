package palbp.demos.pc.isel

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val serverPort = 9000
    // Talk about this
    val clientCounter = AtomicInteger(1)

    ServerSocket(serverPort).use { serverSocket ->
        println("Echo server running on port $serverPort")
        while (true) {
            val clientSocket = serverSocket.accept()
            val clientId = clientCounter.getAndIncrement()
            val thread = Thread(
                {
                    handleClient(clientSocket, clientId)
                },
                "client-$clientId"
            )
            thread.start()
        }
    }
}

fun handleClientAIGen(socket: Socket, clientId: Int) {
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
