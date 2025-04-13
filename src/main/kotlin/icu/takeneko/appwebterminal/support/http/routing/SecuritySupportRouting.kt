package icu.takeneko.appwebterminal.support.http.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.plugins.Principal
import icu.takeneko.appwebterminal.support.http.plugins.jwtAudience
import icu.takeneko.appwebterminal.support.http.plugins.jwtSecret
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

fun Application.configureSecuritySupportRouting() {
    routing {
        post("/login") {
            val user = call.receive<UserCredential>()
            return@post try {
                val uuid = UUID.fromString(user.uuid)
                if (AENetworkSupport.auth(uuid, user.password)) {
                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer("AppliedWebTerminal")
                        .withClaim("uuid", user.uuid)
                        .withClaim("nonce", AENetworkSupport.getNonce(uuid))
                        .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(UserAuthResult(success = true, payload = token))
                } else {
                    call.respond(UserAuthResult(success = false, payload = "Authentication failed."))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(UserAuthResult(success = false, payload = e.message))
            }
        }
        authenticate("jwt") {
            get("/validate") {
                val principal = call.principal<Principal>()
                return@get call.respond(
                    if (principal != null) {
                        val uuid = principal.payload.getClaim("uuid").asString()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                        val expiresAt = principal.expiresAt?.toInstant()?.atZone(ZoneOffset.UTC)?.format(formatter)
                        ValidateResult(true, uuid, expiresAt)
                    } else {
                        ValidateResult(false, null, null)
                    }
                )
            }
        }

    }
}

@kotlinx.serialization.Serializable
private data class ValidateResult(val success: Boolean, val uuid: String?, val expiresAt: String?)

@kotlinx.serialization.Serializable
private data class UserCredential(val uuid: String, val password: String)

@kotlinx.serialization.Serializable
private data class UserAuthResult(val success: Boolean, val payload: String?)