package icu.takeneko.appwebterminal.support.http.routing

import io.ktor.server.application.*

fun Application.configureRouting() {
    configureFrontendSupportRouting()
    configureAEServiceRouting()
    configureSecuritySupportRouting()
}