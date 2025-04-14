package icu.takeneko.appwebterminal.client.rendering

import appeng.api.stacks.AEKey
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource.BufferSource

interface AEKeyImageProvider<T : AEKey> {
    fun renderImage(
        aeKey: T,
        poseStack: PoseStack,
        bufferSource: BufferSource,
        x: Int,
        y: Int
    )
}