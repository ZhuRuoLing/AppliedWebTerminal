package icu.takeneko.appwebterminal.client.rendering.providers

import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKeyType
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraftforge.registries.ForgeRegistries

object AEItemKeyImageProvider : AEKeyImageProvider<AEItemKey> {
    override val keyType: AEKeyType = AEKeyType.items()

    override fun getAllEntries(): Iterable<AEItemKey> = ForgeRegistries.ITEMS.map { AEItemKey.of(it) }
}