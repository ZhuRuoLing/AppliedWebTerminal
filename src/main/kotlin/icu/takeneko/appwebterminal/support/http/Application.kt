package icu.takeneko.appwebterminal.support.http

import icu.takeneko.appwebterminal.support.http.plugins.configureHTTP
import icu.takeneko.appwebterminal.support.http.plugins.configureMonitoring
import icu.takeneko.appwebterminal.support.http.plugins.configureSecurity
import icu.takeneko.appwebterminal.support.http.plugins.configureSerialization
import icu.takeneko.appwebterminal.support.http.plugins.configureSockets
import icu.takeneko.appwebterminal.support.http.routing.configureRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory

class HttpServer(
    private val port: Int
) : Thread("WebTerminalHttpServer") {
    val logger = LoggerFactory.getLogger("HttpServer")
    val server = embeddedServer(
        CIO,
        port = port,
        host = "0.0.0.0",
        module = Application::module
    )

    override fun run() {
        try {
            logger.info("Starting server at port $port")
            server.start(true)
        } catch (e: Throwable) {
            if (e !is InterruptedException) {
                logger.error("Unable to launch http server.", e)
            }
        }
    }

    fun gracefullyStop() {
        logger.info("Stopping http server because minecraft server is stopping.")
        server.stop()
    }
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
    configureSockets()
}
