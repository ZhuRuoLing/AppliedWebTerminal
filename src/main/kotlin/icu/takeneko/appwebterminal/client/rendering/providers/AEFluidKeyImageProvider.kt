package icu.takeneko.appwebterminal.client.rendering.providers

import appeng.api.stacks.AEFluidKey
import appeng.api.stacks.AEKeyType
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraftforge.registries.ForgeRegistries

object AEFluidKeyImageProvider : AEKeyImageProvider<AEFluidKey> {
    override val keyType: AEKeyType = AEKeyType.fluids()

    override fun getAllEntries(): Iterable<AEFluidKey> {
        return ForgeRegistries.FLUIDS.values.map { AEFluidKey.of(it) }
    }
}