package icu.takeneko.appwebterminal.support

import org.slf4j.LoggerFactory
import java.util.UUID

object AENetworkSupport {
    private val accessors: MutableMap<UUID, AENetworkAccess> = mutableMapOf()
    private val logger = LoggerFactory.getLogger("AE Network Support")

    fun register(accessor: AENetworkAccess) {
        logger.info("Registering $accessor(${accessor.getId()})")
        accessors[accessor.getId()] = accessor
    }

    fun remove(accessor: AENetworkAccess) {
        if (accessor !in accessors.values) return
        logger.info("Unegistering $accessor(${accessor.getId()})")
        accessors.remove(accessor.getId())
    }

    fun update(uuid: UUID, name: String, password: String) {
        accessors[uuid]?.update(name, password)
        accessors[uuid]?.markDirty()
        logger.info("Renaming $uuid into $name")
        logger.info("Updating $uuid password into $password")
        logger.info("Updating $uuid nonce into ${accessors[uuid]?.getNonce()}")
    }

    fun requestSessionReset(accessor: AENetworkAccess) {

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
}