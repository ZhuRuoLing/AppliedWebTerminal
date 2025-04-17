package icu.takeneko.appwebterminal.client.rendering.foundation

import com.mojang.blaze3d.shaders.ProgramManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import io.ktor.websocket.Frame
import net.minecraft.client.renderer.ShaderInstance
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

abstract class PostProcess(
    protected var xSize: Int,
    protected var ySize: Int
) {

    protected val projectionMatrix: Matrix4f = Matrix4f().setOrtho(
        0f,
        xSize.toFloat(),
        0f,
        ySize.toFloat(),
        0.1f,
        1000f
    )

    protected val mvMat = Matrix4f()

    abstract fun apply(inputTexture: Int)

    open fun resize(xSize: Int, ySize: Int){
        this.xSize = xSize
        this.ySize = ySize
        projectionMatrix.setOrtho(
            0f,
            xSize.toFloat(),
            0f,
            ySize.toFloat(),
            0.1f,
            1000f
        )
    }

    protected inline fun processOnce(
        shader: ShaderInstance,
        inputTexture: Int,
        writeFramebuffer: FrameBuffer,
        crossinline uniformSetter: ShaderInstance.() -> Unit
    ) {
        RenderSystem.setShader { shader }
        shader.setSampler("DiffuseSampler", inputTexture)
        shader.uniformSetter()
        RenderSystem.depthFunc(GL11.GL_ALWAYS)
        RenderSystem.disableDepthTest()
        val bufferBuilder: BufferBuilder = Tesselator.getInstance().builder
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION)
        bufferBuilder.vertex(0.0, 0.0, 500.0).endVertex()
        bufferBuilder.vertex(xSize.toDouble(), 0.0, 500.0).endVertex()
        bufferBuilder.vertex(xSize.toDouble(), ySize.toDouble(), 500.0).endVertex()
        bufferBuilder.vertex(0.0, ySize.toDouble(), 500.0).endVertex()

        shader.apply()
        writeFramebuffer.bindWrite(true)
        BufferUploader.draw(bufferBuilder.end())
        writeFramebuffer.unbindWrite()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)
        RenderSystem.enableDepthTest()
        RenderSystem.setShader { null }
        shader.clear()
    }
}