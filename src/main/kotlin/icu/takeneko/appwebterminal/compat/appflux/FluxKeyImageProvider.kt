package icu.takeneko.appwebterminal.compat.appflux

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEKeyTypes
import com.glodblock.github.appflux.AppFlux
import com.glodblock.github.appflux.common.me.key.FluxKey
import com.glodblock.github.appflux.common.me.key.type.EnergyType
import com.mojang.blaze3d.vertex.PoseStack
import icu.takeneko.appwebterminal.api.ImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource

@ImageProvider(
    value = "appflux:flux",
    modid = "appflux"
)
class FluxKeyImageProvider : AEKeyImageProvider<FluxKey> {
    private val keyType by lazy {
        AEKeyTypes.get(AppFlux.id("flux"))
    }

    override fun renderImage(
        aeKey: FluxKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<FluxKey>
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

    override fun getAllEntries(): Iterable<FluxKey> {
        return EnergyType.entries.map { FluxKey.of(it)!! }
    }
}