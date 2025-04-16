package icu.takeneko.appwebterminal.compat.appbot

import appbot.ae2.ManaKey
import appbot.ae2.ManaKeyType
import appeng.api.stacks.AEKeyType
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider

@ImageProvider(
    value = "botania:mana",
    modid = "appbot"
)
class ManaKeyImageProvider : AEKeyImageProvider<ManaKey> {
    override val keyType: AEKeyType = ManaKeyType.TYPE

    override fun getAllEntries(): Iterable<ManaKey> {
        return listOf(ManaKey.KEY as ManaKey)
    }
}
