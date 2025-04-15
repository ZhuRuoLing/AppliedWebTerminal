package icu.takeneko.appwebterminal.client.rendering.providers

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKeyTypes
import appeng.core.AppEng
import com.mojang.blaze3d.vertex.PoseStack
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraftforge.registries.ForgeRegistries

object AEItemKeyImageProvider : AEKeyImageProvider<AEItemKey> {
    private val keyType by lazy {
        AEKeyTypes.get(AppEng.makeId("i"))
    }

    override fun renderImage(
        aeKey: AEItemKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<AEItemKey>
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

    override fun getAllEntries(): Iterable<AEItemKey> = ForgeRegistries.ITEMS.map { AEItemKey.of(it) }
}