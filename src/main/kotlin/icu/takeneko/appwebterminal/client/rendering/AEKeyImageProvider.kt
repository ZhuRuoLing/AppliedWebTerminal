package icu.takeneko.appwebterminal.client.rendering

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource.BufferSource
import kotlin.math.min

interface AEKeyImageProvider<T : AEKey> {
    val keyType: AEKeyType
    fun renderImage(
        aeKey: T,
        poseStack: PoseStack,
        bufferSource: BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<T>
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
            (min(canvasSizeX, canvasSizeY) * 0.8).toFloat(),
            LightTexture.FULL_BLOCK,
            Minecraft.getInstance().level
        )
    }

    fun getAllEntries(): Iterable<T>
}