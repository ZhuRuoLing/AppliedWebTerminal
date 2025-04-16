package icu.takeneko.appwebterminal.compat.appflux

import com.glodblock.github.appflux.common.me.key.FluxKey
import com.glodblock.github.appflux.common.me.key.type.EnergyType
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider

@ImageProvider(
    value = "appflux:flux",
    modid = "appflux"
)
class FluxKeyImageProvider : AEKeyImageProvider<FluxKey> {
    override val keyType: FluxKeyType = FluxKeyType.TYPE

    override fun getAllEntries(): Iterable<FluxKey> {
        return EnergyType.entries.map { FluxKey.of(it)!! }
    }
}