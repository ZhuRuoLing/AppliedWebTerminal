package icu.takeneko.appwebterminal.support.http.websocket

import icu.takeneko.appwebterminal.support.MECpuStatusBundle
import icu.takeneko.appwebterminal.support.MECraftingStatusBundle

interface Protocol {
    fun type(): String
    fun accept(session: WebsocketSession) {
    }
}

@kotlinx.serialization.Serializable
data class SetUpdateInterval(val value: Int) : Protocol {
    override fun type(): String = "update_interval"

    override fun accept(session: WebsocketSession) {
        session.updateInterval = value
        session.updateCountdown = 0
    }
}

@kotlinx.serialization.Serializable
data class SelectCpu(val cpuId: Int) : Protocol {
    override fun type(): String = "select_cpu"

    override fun accept(session: WebsocketSession) {
        session.craftingServiceView.selectCpu(cpuId)
    }
}

@kotlinx.serialization.Serializable
data class CancelJob(val cpuId: Int) : Protocol {
    override fun type(): String = "cancel_job"
    override fun accept(session: WebsocketSession) {
        session.craftingServiceView.getCpu(cpuId)?.cancelJob()
    }
}

@kotlinx.serialization.Serializable
data class MECraftingServiceStatusBundle(
    val cpus: List<MECpuStatusBundle>,
    var craftingStatus: MECraftingStatusBundle?
) : Protocol {
    override fun type(): String = "status"
}