package icu.takeneko.appwebterminal.client.rendering.foundation;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FullyBufferedBufferSource extends MultiBufferSource.BufferSource implements AutoCloseable {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);
    private final Map<RenderType, ByteBufferBuilder> byteBuffers = new HashMap<>();
    private final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
    private final Reference2IntMap<RenderType> indexCountMap = new Reference2IntOpenHashMap<>();

    public FullyBufferedBufferSource() {
        super(null, null);
    }

    private ByteBufferBuilder getByteBuffer(RenderType renderType) {
        return byteBuffers.computeIfAbsent(renderType, it -> new ByteBufferBuilder(786432));
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return bufferBuilders.computeIfAbsent(
            renderType,
            it -> new BufferBuilder(getByteBuffer(it), it.mode, it.format)
        );
    }

    public boolean isEmpty() {
        return !bufferBuilders.isEmpty() && bufferBuilders.values().stream().noneMatch(it -> it.vertices > 0);
    }

    @Override
    public void endBatch(RenderType renderType) {
    }

    @Override
    public void endBatch() {
    }

    @Override
    public void endLastBatch() {
    }

    public Map<RenderType, VertexBuffer> upload() {
        Map<RenderType, VertexBuffer> result = new HashMap<>();
        for (Map.Entry<RenderType, BufferBuilder> entry : bufferBuilders.entrySet()) {
            BufferBuilder builder = entry.getValue();
            MeshData mesh = builder.build();
            if (mesh == null) continue;
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vertexBuffer.bind();
            vertexBuffer.upload(mesh);
            result.put(entry.getKey(), vertexBuffer);
        }
        return result;
    }

    public void close(RenderType renderType) {
        ByteBufferBuilder builder = byteBuffers.get(renderType);
        builder.close();
    }

    public void close() {
        byteBuffers.keySet().forEach(this::close);
    }
}