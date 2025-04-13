package icu.takeneko.appwebterminal.util

import icu.takeneko.appwebterminal.AppWebTerminal.MOD_ID
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.minecraftforge.fml.ModList
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

