package icu.takeneko.appwebterminal.support

import org.slf4j.LoggerFactory
import java.util.*

object AENetworkSupport {
    private val accessors: MutableMap<UUID, AENetworkAccess> = mutableMapOf()
    private val logger = LoggerFactory.getLogger("AE Network Support")

    fun register(accessor: AENetworkAccess) {
        accessors[accessor.getId()] = accessor
    }

    fun remove(accessor: AENetworkAccess) {
        accessors.remove(accessor.getId())
    }

    fun update(uuid: UUID, name: String, password: String) {
        logger.info("Renaming $uuid into $name")
        logger.info("Updating $uuid password into $password")
        accessors[uuid]?.update(name, password)
        accessors[uuid]?.markDirty()
    }

    fun requestSessionReset(accessor: AENetworkAccess) {

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