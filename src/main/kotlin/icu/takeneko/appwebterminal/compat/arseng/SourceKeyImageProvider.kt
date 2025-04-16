package icu.takeneko.appwebterminal.compat.arseng

import appeng.api.stacks.AEKeyType
import gripe._90.arseng.me.key.SourceKey
import gripe._90.arseng.me.key.SourceKeyType
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider

@ImageProvider(
    value = "arseng:source",
    modid = "arseng"
)
class SourceKeyImageProvider : AEKeyImageProvider<SourceKey> {
    override val keyType: AEKeyType = SourceKeyType.TYPE

    override fun getAllEntries(): Iterable<SourceKey> {
        return listOf(SourceKey.KEY as SourceKey)
    }
}