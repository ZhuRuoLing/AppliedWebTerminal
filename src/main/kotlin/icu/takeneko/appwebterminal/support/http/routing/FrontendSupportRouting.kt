package icu.takeneko.appwebterminal.support.http.routing

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.util.MinecraftI18nSupport
import icu.takeneko.appwebterminal.util.ServerI18nSupport
import icu.takeneko.appwebterminal.util.staticResourceForModContainer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.notExists

fun Application.configureFrontendSupportRouting() {
    routing {
        staticResourceForModContainer()
        get("/settings") {
            return@get call.respond(
                FrontendSettings(
                    AppWebTerminal.config.frontendTitle,
                    AppWebTerminal.config.backendWebsocketEndpoint
                )
            )
        }
        get("/aeResource/{keyType}/{key}") {
            val keyType = call.parameters["keyType"] ?: return@get
            val key = call.parameters["key"] ?: return@get
            val filePath = Path("./aeKeyResources") /
                keyType.replace(":", "_") /
                "$key.png".replace(":", "_")
            println(filePath.normalize().toString().startsWith("aeKeyResources"))
            println(filePath.normalize())
            if (filePath.notExists() || !filePath.normalize().toString().startsWith("aeKeyResources")) {
                return@get call.respond(HttpStatusCode.NotFound)
            }
            return@get call.respondFile(filePath.toFile())
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
private data class FrontendSettings(val title: String, val webSocketUrl: String)