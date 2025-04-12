package icu.takeneko.appwebterminal.support.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.util.staticResourceForModContainer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Date
import java.util.UUID

const val pathParameterName = "static-content-path-parameter"

fun Application.configureRouting() {

    routing {
        get("/list") {
            return@get call.respond(AENetworkSupport.listAllTerminals())
        }
        staticResourceForModContainer()
        authenticate("jwt") {
            get("/ping") {
                return@get call.respond("pong")
            }
            get("/settings") {
                return@get call.respond(
                    FrontendSettings(
                        AppWebTerminal.config.frontendTitle,
                        AppWebTerminal.config.backendWebsocketEndpoint
                    )
                )
            }
        }
        post("/login") {
            val user = call.receive<UserCredential>()
            return@post try {
                val uuid = UUID.fromString(user.uuid)
                if (AENetworkSupport.auth(uuid, user.password)) {
                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer("AppliedWebTerminal")
                        .withClaim("username", user.uuid)
                        .withClaim("nonce", AENetworkSupport.getNonce(uuid))
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(UserAuthResult(success = true, payload = token))
                } else {
                    call.respond(UserAuthResult(success = false, payload = "Authentication failed."))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(UserAuthResult(success = false, payload = e.message))
            }
        }
    }
}

@kotlinx.serialization.Serializable
private data class FrontendSettings(val title: String, val webSocketUrl: String)

@kotlinx.serialization.Serializable
private data class UserCredential(val uuid: String, val password: String)

@kotlinx.serialization.Serializable
private data class UserAuthResult(val success: Boolean, val payload: String?)