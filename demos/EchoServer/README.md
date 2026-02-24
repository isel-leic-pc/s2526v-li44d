# Echo Server

A simple TCP echo server implementation in Kotlin that echoes back any data it receives from clients.

## Features

- **Multi-client support**: Handles multiple concurrent client connections using a thread pool
- **Simple protocol**: Echoes back any text sent to it
- **Graceful shutdown**: Type 'stop' in the server console to shutdown
- **Client disconnect**: Clients can type 'quit' or 'exit' to disconnect

## How to Run

### Running the Server

1. Build the project:
   ```bash
   ./gradlew build
   ```

2. Run the server:
   ```bash
   ./gradlew run
   ```

The server will start on port 8080 and wait for client connections.

### Testing with Telnet

You can test the server using telnet:

```bash
telnet localhost 8080
```

Then type any messages and they will be echoed back.

## Server Commands

- Type `stop` in the server console to gracefully shutdown the server

## Client Commands (when using telnet)

- Type `quit` or `exit` to disconnect from the server

## Architecture

- **EchoServer**: Main server class that listens for TCP connections
- Uses Java's `ServerSocket` and `Socket` for network communication
- Thread pool (`Executors.newCachedThreadPool()`) for handling multiple clients
- Buffered I/O for efficient text processing

## Port Configuration

The server runs on port 8080 by default. You can modify this in the `EchoServer` constructor if needed.
