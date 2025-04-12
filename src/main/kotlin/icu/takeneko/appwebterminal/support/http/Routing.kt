package icu.takeneko.appwebterminal.support.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.util.MinecraftI18nSupport
import icu.takeneko.appwebterminal.util.ResourceLocationSerializer
import icu.takeneko.appwebterminal.util.ServerI18nSupport
import icu.takeneko.appwebterminal.util.staticResourceForModContainer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.minecraft.resources.ResourceLocation
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

const val pathParameterName = "static-content-path-parameter"

fun Application.configureRouting() {
    routing {
        staticResourceForModContainer()
        get("/list") {
            return@get call.respond(AENetworkSupport.listAllTerminals())
        }
        get("/settings") {
            return@get call.respond(
                FrontendSettings(
                    AppWebTerminal.config.frontendTitle,
                    AppWebTerminal.config.backendWebsocketEndpoint
                )
            )
        }
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
                val principal = call.principal<JWTPrincipal>()
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
            route("/crafting") {
                get("/craftables") {
                }
                post("/craft") {
                    val request = call.receive<CraftingRequest>()
                }
            }
        }
        get("/translate/{language}/{key}") {
            val language = call.parameters["language"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Expected parameter 'language'"
            )
            val key = call.parameters["key"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Expected parameter 'key'"
            )
            if (ServerI18nSupport.contains(language, key)) {
                return@get call.respond(ServerI18nSupport.get(language, key))
            } else {
                if (MinecraftI18nSupport.contains(language, key)) {
                    return@get call.respond(MinecraftI18nSupport.get(language, key))
                }
                return@get call.respond(ServerI18nSupport.get(language, key))
            }
        }
    }
}

@kotlinx.serialization.Serializable
private data class CraftingRequest(
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val item: ResourceLocation,
    val count: Long
)

@kotlinx.serialization.Serializable
private data class FrontendSettings(val title: String, val webSocketUrl: String)

@kotlinx.serialization.Serializable
private data class UserCredential(val uuid: String, val password: String)

@kotlinx.serialization.Serializable
private data class UserAuthResult(val success: Boolean, val payload: String?)

@kotlinx.serialization.Serializable
private data class ValidateResult(val success: Boolean, val uuid: String?, val expiresAt: String?)