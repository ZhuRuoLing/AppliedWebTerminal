package icu.takeneko.appwebterminal.compat.appmek

import appeng.api.stacks.AEKeyType
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import me.ramidzkh.mekae2.ae2.MekanismKey
import me.ramidzkh.mekae2.ae2.MekanismKeyType
import mekanism.api.MekanismAPI

@ImageProvider(
    value = "appmek:chemical",
    modid = "appmek"
)
class ChemicalKeyImageProvider : AEKeyImageProvider<MekanismKey> {
    override val keyType: AEKeyType = MekanismKeyType.TYPE

    override fun getAllEntries(): Iterable<MekanismKey> {
        return (MekanismAPI.gasRegistry()
            + MekanismAPI.infuseTypeRegistry()
            + MekanismAPI.pigmentRegistry()
            + MekanismAPI.slurryRegistry()).mapNotNull {
                MekanismKey.of(it.getStack(1000))
            }
    }
}