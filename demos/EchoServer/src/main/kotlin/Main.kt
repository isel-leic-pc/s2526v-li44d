package palbp.demos.pc.isel

import org.slf4j.LoggerFactory


fun main() {
    val port = 12345

    val logger = LoggerFactory.getLogger("palbp.demos.pc.isel.EchoServer")

    // runEchoServerST(port, logger)
    // runEchoServerMT(port, logger)
    // runEchoServerTP(port, logger)
    runEchoServerHMTP(port, logger)
}

