package icu.takeneko.appwebterminal.client.rendering

import appeng.api.client.AEKeyRenderHandler
import appeng.api.client.AEKeyRendering
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKeyTypes
import appeng.core.AppEng
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.Item
import net.minecraftforge.registries.ForgeRegistries

object AEItemKeyImageProvider : AEKeyImageProvider<AEItemKey, Item> {
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
        val renderer = AEKeyRendering.getOrThrow(keyType) as AEKeyRenderHandler<AEItemKey>
        poseStack.translate(
            canvasSizeX / 2f,
            canvasSizeY / 2f,
            0f
        )
        renderer.drawOnBlockFace(
            poseStack,
            bufferSource,
            aeKey,
            1f,
            LightTexture.FULL_BLOCK,
            Minecraft.getInstance().level
        )
    }

    override fun getAllEntries(): Iterable<Item> = ForgeRegistries.ITEMS

    override fun objectToKey(element: Item): AEItemKey = AEItemKey.of(element)
}