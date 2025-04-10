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

    fun rename(uuid: UUID, name: String) {
        logger.info("Renaming $uuid into $name")
        accessors[uuid]?.displayName = name
        accessors[uuid]?.markDirty()
    }
}