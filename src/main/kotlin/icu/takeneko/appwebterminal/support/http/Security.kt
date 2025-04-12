package icu.takeneko.appwebterminal.support.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import icu.takeneko.appwebterminal.support.AENetworkSupport
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
fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain

    logger.info("Using $jwtSecret as JWT secret.")
    authentication {
        jwt("jwt") {
            realm = "AppliedWebTerminal"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer("AppliedWebTerminal")
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience) && validateJwt(credential))
                    JWTPrincipal(credential.payload)
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
    }
}

fun validateJwt(credential: JWTCredential): Boolean {
    val username = credential.payload.getClaim("username")
    val nonceClaim = credential.payload.getClaim("nonce")
    if (username.isNull || nonceClaim.isNull) return false
    return try {
        val uuid = UUID.fromString(username.asString())
        val nonce = username.asString()
        AENetworkSupport.validateNonce(uuid, nonce)
    } catch (e: IllegalArgumentException) {
        logger.warn("Could not validate jwt token: ", e)
        false
    }
}
