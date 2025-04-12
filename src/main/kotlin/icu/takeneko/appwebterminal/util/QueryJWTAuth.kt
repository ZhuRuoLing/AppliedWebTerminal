package icu.takeneko.appwebterminal.util

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.impl.JWTParser
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.Base64

internal val queryJWTKey: Any = "queryJWTAuth"

fun AuthenticationConfig.queryJwt(name: String, config: QueryParamJWTAuthenticationProvider.Config.() -> Unit) {
    val provider = QueryParamJWTAuthenticationProvider.Config(name).apply(config).build()
    register(provider)
}

class QueryParamJWTAuthenticationProvider internal constructor(config: Config) : AuthenticationProvider(config) {
    private val verifier: ((String) -> JWTVerifier?) = config.verifier
    private val authenticationFunction = config.authenticationFunction
    private val challengeFunction: suspend JWTChallengeContext.() -> Unit = config.challenge


    class Config(name: String) : AuthenticationProvider.Config(name) {
        internal var authenticationFunction: AuthenticationFunction<JWTCredential> = {
            throw NotImplementedError("JWT auth validate function is not specified.")
        }

        internal var verifier: ((String) -> JWTVerifier?) = { null }

        internal var challenge: suspend JWTChallengeContext.() -> Unit = {
            call.respond(
                HttpStatusCode.Unauthorized, "token expired"
            )
        }

        fun verifier(verifier: JWTVerifier) {
            this.verifier = { verifier }
        }

        fun verifier(verifier: (String) -> JWTVerifier?) {
            this.verifier = verifier
        }

        fun validate(validate: suspend ApplicationCall.(JWTCredential) -> Any?) {
            authenticationFunction = validate
        }

        fun challenge(block: suspend JWTChallengeContext.() -> Unit) {
            challenge = block
        }

        fun build(): QueryParamJWTAuthenticationProvider {
            return QueryParamJWTAuthenticationProvider(this)
        }

    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val token = call.request.queryParameters["token"]

        if (token != null) {
            try {
                val jwtVerifier = verifier(token)
                if (jwtVerifier == null) {
                    context.queryChallenge(AuthenticationFailedCause.InvalidCredentials, challengeFunction)
                    return
                }
                val principal = verifyAndValidate(call, jwtVerifier, token, authenticationFunction)
                if (principal != null) {
                    context.principal(name, principal)
                    return
                }
                context.queryChallenge(AuthenticationFailedCause.InvalidCredentials, challengeFunction)
                return
            } catch (cause: Throwable) {
                val message = cause.message ?: cause.javaClass.simpleName
                context.error(queryJWTKey, AuthenticationFailedCause.Error(message))
            }
        } else {
            context.queryChallenge(AuthenticationFailedCause.InvalidCredentials, challengeFunction)
        }
    }
}

internal fun AuthenticationContext.queryChallenge(
    cause: AuthenticationFailedCause,
    challengeFunction: suspend JWTChallengeContext.() -> Unit
) {
    this.challenge(queryJWTKey, cause) { challenge, c ->
        challengeFunction(JWTChallengeContext(c))
        if (!challenge.completed && call.response.status() != null) {
            challenge.complete()
        }
    }
}

internal suspend fun verifyAndValidate(
    call: ApplicationCall,
    jwtVerifier: JWTVerifier?,
    token: String,
    validate: suspend ApplicationCall.(JWTCredential) -> Any?
): Any? {
    val jwt = try {
        token.let { jwtVerifier?.verify(it) }
    } catch (cause: JWTVerificationException) {
        null
    } ?: return null

    val payload = JWTParser().parsePayload(String(Base64.getUrlDecoder().decode(jwt.payload)))
    val credentials = JWTCredential(payload)
    val principal = validate(call, credentials)

    return principal
}