package icu.takeneko.appwebterminal.compat.appmek

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEKeyTypes
import com.glodblock.github.appflux.AppFlux
import com.mojang.blaze3d.vertex.PoseStack
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import me.ramidzkh.mekae2.AppliedMekanistics
import me.ramidzkh.mekae2.ae2.MekanismKey
import mekanism.api.MekanismAPI
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource

@ImageProvider(
    value = "appmek:chemical",
    modid = "appmek"
)
object ChemicalKeyImageProvider : AEKeyImageProvider<MekanismKey> {
    private val keyType by lazy {
        AEKeyTypes.get(AppliedMekanistics.id("chemical"))
    }

    override fun renderImage(
        aeKey: MekanismKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<MekanismKey>
        poseStack.translate(
            canvasSizeX / 2f,
            canvasSizeY / 2f,
            500f
        )
        poseStack.scale(1f, -1f, 10f)
        renderer.drawOnBlockFace(
            poseStack,
            bufferSource,
            aeKey,
            200f,
            LightTexture.FULL_BLOCK,
            Minecraft.getInstance().level
        )
    }

    override fun getAllEntries(): Iterable<MekanismKey> {
        return (MekanismAPI.gasRegistry()
            + MekanismAPI.infuseTypeRegistry()
            + MekanismAPI.pigmentRegistry()
            + MekanismAPI.slurryRegistry()).map { MekanismKey.of(it.getStack(1000))!! }
    }
}