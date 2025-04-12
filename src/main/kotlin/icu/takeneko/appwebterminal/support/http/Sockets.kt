package icu.takeneko.appwebterminal.support.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        authenticationByQueryParam(RouteAuthenticationConfig {
            paramKey = "token"
            challenge {
                it.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired"
                )
            }
            validate {
                if (it.audience.contains(jwtAudience) && validateJwt(it))
                    JWTPrincipal(it)
                else
                    null
            }
        }) {
            webSocket("/ws") { // websocketSession
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                }
            }
        }

    }
}
