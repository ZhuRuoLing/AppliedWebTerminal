package icu.takeneko.appwebterminal.client.rendering

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource.BufferSource
import kotlin.math.min

interface AEKeyImageProvider<T : AEKey> {
    val keyType: AEKeyType

    fun renderImage(
        aeKey: T,
        poseStack: PoseStack,
        bufferSource: BufferSource,
        guiGraphics: GuiGraphics,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<T>
//        poseStack.translate(
//            canvasSizeX / 2f,
//            canvasSizeY / 2f,
//            500f
//        )
        poseStack.scale(canvasSizeX / 16f, canvasSizeY / 16f, 1f)
        renderer.drawInGui(
            Minecraft.getInstance(),
            guiGraphics,
            0,
            0,
            aeKey,
        )
    }

    fun getAllEntries(): Iterable<T>
}