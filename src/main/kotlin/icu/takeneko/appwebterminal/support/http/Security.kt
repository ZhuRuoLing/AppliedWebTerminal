package icu.takeneko.appwebterminal.support.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.mojang.datafixers.kinds.App
import icu.takeneko.appwebterminal.support.AENetworkSupport
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
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

fun Route.authenticationByQueryParam(
    config: RouteAuthenticationConfig,
    configure: Route.() -> Unit
): Route {
    val authenticatedRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Transparent
    })
    authenticatedRoute.install(createInterceptor(config))
    authenticatedRoute.configure()
    return authenticatedRoute
}

class RouteAuthenticationConfig private constructor(
    var paramKey: String = "",
    var challenge: suspend (ApplicationCall) -> Unit = {},
    var validator: (Payload) -> JWTPrincipal? = { null }
) {
    constructor(fn: RouteAuthenticationConfig.() -> Unit) : this() {
        this.fn()
    }

    fun challenge(fn: suspend (ApplicationCall) -> Unit) {
        this.challenge = fn
    }

    fun validate(fn: (Payload) -> JWTPrincipal?) {
        this.validator = fn
    }
}

fun createInterceptor(config: RouteAuthenticationConfig): RouteScopedPlugin<RouteAuthenticationConfig> =
    createRouteScopedPlugin(
        "authenticateByQuery",
        { config }
    ) {
        val paramKey = pluginConfig.paramKey
        on(object : Hook<suspend ApplicationCall.() -> Unit> {
            val Phase = PipelinePhase("Authenticate")

            override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall) -> Unit) {
                pipeline.insertPhaseAfter(ApplicationCallPipeline.Plugins, Phase)
                pipeline.intercept(Phase) { handler(call) }
            }
        }) {
            if (isHandled) return@on
            val token = request.queryParameters[paramKey] ?: return@on pluginConfig.challenge(this)
            try {
                val jwt = jwtVerifier.verify(token)
                val validateResult = config.validator(jwt)
                if (validateResult != null) {
                    this.authentication.principal(null, validateResult)
                } else {
                    pluginConfig.challenge(this)
                }
            } catch (e: Exception) {
                return@on pluginConfig.challenge(this)
            }
        }
    }

fun validateJwt(payload: Payload): Boolean {
    val usernameClaim = payload.getClaim("username")
    val nonceClaim = payload.getClaim("nonce")
    if (usernameClaim.isNull || nonceClaim.isNull) return false
    return try {
        val uuid = UUID.fromString(usernameClaim.asString())
        val nonce = nonceClaim.asString()
        AENetworkSupport.validateNonce(uuid, nonce)
    } catch (e: IllegalArgumentException) {
        logger.warn("Could not validate jwt token: ", e)
        false
    }
}
