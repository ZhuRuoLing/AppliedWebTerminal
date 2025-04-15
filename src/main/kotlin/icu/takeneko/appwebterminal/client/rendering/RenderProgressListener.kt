package icu.takeneko.appwebterminal.client.rendering

import net.minecraft.resources.ResourceLocation

interface RenderProgressListener {
    fun notifyTotalCount(size: Int)

    fun notifyProgress(current: Int, name: ResourceLocation)
}