package icu.takeneko.appwebterminal.client.rendering.providers

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEFluidKey
import appeng.api.stacks.AEKeyTypes
import appeng.core.AppEng
import com.mojang.blaze3d.vertex.PoseStack
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraftforge.registries.ForgeRegistries
import org.slf4j.LoggerFactory

object AEFluidKeyImageProvider : AEKeyImageProvider<AEFluidKey> {
    private val logger = LoggerFactory.getLogger("AEFluidKeyImageProvider")
    private val keyType by lazy {
        AEKeyTypes.get(AppEng.makeId("f"))
    }

    override fun renderImage(
        aeKey: AEFluidKey,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource.BufferSource,
        canvasSizeX: Int,
        canvasSizeY: Int
    ) {
        try {
            @Suppress("UNCHECKED_CAST")
            val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<AEFluidKey>
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
        } catch (e: Throwable) {
            logger.error("An exception was thrown while rendering $aeKey", e)
        }
    }

    override fun getAllEntries(): Iterable<AEFluidKey> {
        return ForgeRegistries.FLUIDS.values.map { AEFluidKey.of(it) }
    }
}