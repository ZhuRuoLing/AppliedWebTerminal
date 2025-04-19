package icu.takeneko.appwebterminal.client.rendering.foundation

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.VertexBuffer
import com.mojang.blaze3d.vertex.VertexConsumer
import it.unimi.dsi.fastutil.objects.Reference2IntMap
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
import net.minecraft.MethodsReturnNonnullByDefault
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.MemoryAllocator
import java.util.function.Function
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
class FullyBufferedBufferSource : MultiBufferSource.BufferSource(null, null) {
    private val bufferBuilders: MutableMap<RenderType, BufferBuilder> = mutableMapOf<RenderType, BufferBuilder>()
    val indexCountMap: Reference2IntMap<RenderType> = Reference2IntOpenHashMap<RenderType>()

    override fun getBuffer(renderType: RenderType): VertexConsumer {
        return bufferBuilders.computeIfAbsent(
            renderType
        ) { BufferBuilder(15720).also { it.begin(renderType.mode(), renderType.format()) } }
    }

    val isEmpty: Boolean
        get() = !bufferBuilders.isEmpty()
            && bufferBuilders.values.stream().noneMatch { it.isCurrentBatchEmpty() }

    override fun endBatch(renderType: RenderType) {
    }

    override fun endBatch() {
    }

    override fun endLastBatch() {
    }

    fun upload(): Map<RenderType, VertexBuffer> {
        val result = mutableMapOf<RenderType, VertexBuffer>()
        bufferBuilders.forEach { (key, value) ->
            if (value.isCurrentBatchEmpty) return@forEach
            val mesh = value.end()
            val vertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)
            vertexBuffer.bind()
            vertexBuffer.upload(mesh)
            result[key] = vertexBuffer
        }
        return result
    }
}
