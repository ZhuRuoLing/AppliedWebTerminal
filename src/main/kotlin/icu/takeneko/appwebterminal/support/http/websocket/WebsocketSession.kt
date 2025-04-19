package icu.takeneko.appwebterminal.support.http.websocket

import appeng.api.networking.IGrid
import icu.takeneko.appwebterminal.support.AENetworkAccess
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.MECraftingServiceView
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import icu.takeneko.appwebterminal.util.DispatchedSerializer
import io.ktor.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.UUID

class WebsocketSession(
    private val session: DefaultWebSocketSession,
    private val grid: IGrid,
    val owner: AENetworkAccess
) {
    private val Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }
    internal var updateInterval = 20
    internal var updateCountdown = 0
    internal var craftingServiceView = MECraftingServiceView(grid)
    private val logger = LoggerFactory.getLogger("WebsocketSession")
    private val serializer = DispatchedSerializer(
        "type",
        mapOf(
            "update_interval" to SetUpdateInterval.serializer(),
            "select_cpu" to SelectCpu.serializer(),
            "status" to MECraftingServiceStatusBundle.serializer(),
            "cancel_job" to CancelJob.serializer(),
        ),
        String.serializer(),
        Protocol::type
    )

    fun tick() {
        if (updateCountdown-- <= 0) {
            craftingServiceView.tick()
            updateCountdown = updateInterval
            val message = craftingServiceView.createUpdateMessage()
            send(message)
        }
    }

    suspend fun accept() {
        AENetworkSupport.notifySessionStarted(this)
        try {
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    logger.debug("Received $text.")
                    try {
                        val data = Json.decodeFromString(serializer, text)
                        data.accept(this)
                    } catch (e: Throwable) {
                        logger.error("Error handling websocket packet:", e)
                    }
                }
            }
        } catch (_: CancellationException) {

        } finally {
            AENetworkSupport.notifySessionTerminated(this)
        }

    }

    private fun <T> send(message: T) where T : Protocol {
        val context = HttpServerLifecycleSupport.serverInstance?.server?.application?.coroutineContext
            ?: return
        CoroutineScope(context).launch {
            try {
                logger.debug("Sending {}", message)
                sendSuspend(message)
            } catch (_: CancellationException) {

            }
        }
    }

    private suspend fun <T> sendSuspend(message: T) where T : Protocol {
        session.send(Json.encodeToString(serializer, message))
    }

    fun close() {
        val context = HttpServerLifecycleSupport.serverInstance?.server?.application?.coroutineContext
            ?: return
        CoroutineScope(context).launch {
            try {
                session.close(CloseReason(CloseReason.Codes.NORMAL, "Connection closed."))
            } catch (_: CancellationException) {

            }

        }
    }
}

fun main() {

}