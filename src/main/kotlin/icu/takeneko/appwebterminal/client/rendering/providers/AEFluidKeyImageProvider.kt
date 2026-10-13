package icu.takeneko.appwebterminal.client.rendering.providers

import appeng.api.stacks.AEFluidKey
import appeng.api.stacks.AEKeyType
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.core.registries.BuiltInRegistries

object AEFluidKeyImageProvider : AEKeyImageProvider<AEFluidKey> {
    override val keyType: AEKeyType = AEKeyType.fluids()

    override fun getAllEntries(): Iterable<AEFluidKey> =
        BuiltInRegistries.FLUID.entrySet().map { (_, f) ->
            AEFluidKey.of(f)
        }

}