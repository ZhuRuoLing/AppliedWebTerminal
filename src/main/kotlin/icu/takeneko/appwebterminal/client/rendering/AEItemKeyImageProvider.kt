package icu.takeneko.appwebterminal.client.rendering

import appeng.api.stacks.AEItemKey
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource

object AEItemKeyImageProvider:AEKeyImageProvider<AEItemKey> {
    override fun renderImage(
        aeKey: AEItemKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        x: Int,
        y: Int
    ) {

    }
}