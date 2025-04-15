package icu.takeneko.appwebterminal.compat.arseng

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEKeyTypes
import com.glodblock.github.appflux.AppFlux
import com.mojang.blaze3d.vertex.PoseStack
import gripe._90.arseng.ArsEnergistique
import gripe._90.arseng.me.key.SourceKey
import gripe._90.arseng.me.key.SourceKeyType
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource

@ImageProvider(
    value = "arseng:source",
    modid = "arseng"
)
object SourceKeyImageProvider : AEKeyImageProvider<SourceKey> {
    private val keyType by lazy {
        SourceKeyType.TYPE
    }

    override fun renderImage(
        aeKey: SourceKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<SourceKey>
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

    override fun getAllEntries(): Iterable<SourceKey> {
        return listOf(SourceKey.KEY as SourceKey)
    }
}