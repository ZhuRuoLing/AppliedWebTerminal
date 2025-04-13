package icu.takeneko.appwebterminal.support.http.routing

import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.plugins.Principal
import icu.takeneko.appwebterminal.util.ResourceLocationSerializer
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.minecraft.resources.ResourceLocation

fun Application.configureAEServiceRouting() {
    routing {
        get("/list") {
            return@get call.respond(AENetworkSupport.listAllTerminals())
        }
        authenticate("jwt") {
            route("/crafting") {
                get("/craftables") {
                    val principal = call.principal<Principal>()
                }
                post("/craft") {
                    val request = call.receive<CraftingRequest>()
                }
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