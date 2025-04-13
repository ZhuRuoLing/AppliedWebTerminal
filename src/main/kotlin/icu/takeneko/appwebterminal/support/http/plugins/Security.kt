package icu.takeneko.appwebterminal.support.http.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.util.queryJwt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import java.util.UUID

const val jwtAudience = "WebTerminalFrontend"
val jwtSecret = generateNonce()
private val logger = LoggerFactory.getLogger("configureSecurity")

private val jwtVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
    .withAudience(jwtAudience)
    .withIssuer("AppliedWebTerminal")
    .build()

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    logger.info("Using $jwtSecret as JWT secret.")
    install(Authentication) {
        jwt("jwt") {
            realm = "AppliedWebTerminal"
            verifier(jwtVerifier)
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience) && validateJwt(credential.payload))
                    Principal(
                        credential.payload,
                        UUID.fromString(credential.payload.getClaim("uuid").asString()),
                        credential.payload.getClaim("nonce").asString()
                    )
                else
                    null
            }
            challenge { defaultScheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired"
                )
            }
        }
        queryJwt("query_jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer("AppliedWebTerminal")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience) && validateJwt(credential.payload))
                    Principal(
                        credential.payload,
                        UUID.fromString(credential.payload.getClaim("uuid").asString()),
                        credential.payload.getClaim("nonce").asString()
                    )
                else
                    null
            }
            challenge {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired"
                )
            }
        }
    }
}

fun validateJwt(payload: Payload): Boolean {
    val uuidClaim = payload.getClaim("uuid")
    val nonceClaim = payload.getClaim("nonce")
    if (uuidClaim.isNull || nonceClaim.isNull) return false
    return try {
        val uuid = UUID.fromString(uuidClaim.asString())
        val nonce = nonceClaim.asString()
        AENetworkSupport.validateNonce(uuid, nonce)
    } catch (e: IllegalArgumentException) {
        logger.warn("Could not validate jwt token: ", e)
        false
    }
}

class Principal(
    payload: Payload,
    val uuid: UUID,
    val nonce: String
) : JWTPayloadHolder(payload)