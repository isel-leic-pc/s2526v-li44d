package palbp.demos.pc.isel

import java.net.ServerSocket

fun main() {
    val port = 12345
    println("Starting single-threaded echo server on port $port...")
    ServerSocket(port).use { serverSocket ->
        while (true) {
            println("Waiting for a client...")
            serverSocket.accept().use { clientSocket ->
                println("Client connected: ${clientSocket.inetAddress.hostAddress}")
                val input = clientSocket.getInputStream().bufferedReader()
                val output = clientSocket.getOutputStream().bufferedWriter()
                input.lineSequence().forEach { line ->
                    println("Received: $line")
                    output.write("Echo: $line\n")
                    output.flush()
                }
                println("Client disconnected.")
            }
        }
    }
}