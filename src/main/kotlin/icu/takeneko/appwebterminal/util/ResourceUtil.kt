package icu.takeneko.appwebterminal.util

import icu.takeneko.appwebterminal.AppWebTerminal.MOD_ID
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import net.neoforged.fml.ModList
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.extension

const val pathParameterName = "static-content-path-parameter"

private val modResource by lazy {
    CachedModFile(ModList.get().getModFileById(MOD_ID).file)
}

fun Routing.staticResourceForModContainer() {
    createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Success(quality = RouteSelectorEvaluation.qualityTailcard)
    }).apply {
        route("/") {
            route("{$pathParameterName...}") {
                get {
                    var relativePath = call.parameters.getAll(pathParameterName)
                        ?.joinToString(File.separator) ?: return@get
                    if (relativePath.isEmpty()) {
                        relativePath = "index.html"
                    }
                    val requestedPath = "frontend/$relativePath"
                    val resource = modResource.getResource(requestedPath)
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                    val contentType = ContentType.defaultForFileExtension(Path(requestedPath).extension)
                    return@get call.respondBytes(contentType, HttpStatusCode.OK) {
                        resource
                    }
                }
            }
        }
    }
}

