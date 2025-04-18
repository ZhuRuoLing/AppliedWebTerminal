package icu.takeneko.appwebterminal.support

import appeng.api.networking.IGrid
import appeng.api.networking.security.IActionHost
import icu.takeneko.appwebterminal.support.http.websocket.WebsocketSession
import kotlinx.coroutines.runBlocking
import net.minecraft.world.level.Level
import org.slf4j.LoggerFactory
import java.util.UUID

object AENetworkSupport {
    val accessors: MutableMap<UUID, AENetworkAccess> = mutableMapOf()
    private val logger = LoggerFactory.getLogger("AE Network Support")
    private val sessions = mutableMapOf<AENetworkAccess, MutableList<WebsocketSession>>()

    fun register(accessor: AENetworkAccess) {
        logger.info("Registering $accessor(${accessor.getId()})")
        accessors[accessor.getId()] = accessor
    }

    fun remove(accessor: AENetworkAccess) {
        if (accessor !in accessors.values) return
        logger.info("Unegistering $accessor(${accessor.getId()})")
        accessors.remove(accessor.getId())
    }

    fun notifySessionStarted(session: WebsocketSession) {
        logger.info("Starting new websocket session of {}({}).", session.owner, session.owner.getId())
        sessions.computeIfAbsent(session.owner) { mutableListOf() } += session
    }

    fun notifySessionTerminated(session: WebsocketSession) {
        logger.info("Terminating websocket session $session of ${session.owner}")
        sessions.computeIfAbsent(session.owner) { mutableListOf() } -= session
    }

    fun tick() {
        synchronized(sessions) {
            sessions.values.flatten().forEach {
                it.tick()
            }
        }
    }

    fun update(uuid: UUID, name: String, password: String) {
        if ((accessors[uuid] ?: return).update(name, password)) {
            logger.info("Updating $uuid password into $password")
            logger.info("Updating $uuid nonce into ${accessors[uuid]?.getNonce()}")
        }
        accessors[uuid]?.markDirty()
        logger.info("Renaming $uuid into $name")
    }

    fun requestSessionReset(accessor: AENetworkAccess) {
        val sessions = sessions.computeIfAbsent(accessor) { mutableListOf() }.toList()
        sessions.forEach { it.close() }
        this.sessions.computeIfAbsent(accessor) { mutableListOf() }.clear()
    }

    fun listAllTerminals(): List<TerminalInfo> {
        return accessors.map { TerminalInfo(it.value.getTerminalName(), it.value.getId().toString()) }
    }

    fun validateNonce(uuid: UUID, nonce: String): Boolean {
        return (accessors[uuid] ?: return false).validateNonce(nonce)
    }

    fun reset() {
        accessors.clear()
    }

    fun auth(uuid: UUID, password: String): Boolean {
        return if (uuid in accessors) {
            accessors[uuid]!!.auth(password)
        } else {
            false
        }
    }

    fun getNonce(uuid: UUID): String {
        return (accessors[uuid] ?: throw IllegalArgumentException("No such accessor owns uuid $uuid")).getNonce()
    }

    fun getActionHost(uuid: UUID): IActionHost? {
        return (accessors[uuid] ?: return null) as? IActionHost
    }

    fun getGrid(uuid: UUID): IGrid? {
        return (accessors[uuid] ?: return null).getGrid()
    }

    fun getLevel(uuid: UUID): Level? {
        return (accessors[uuid] ?: return null).level()
    }
}