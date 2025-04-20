package icu.takeneko.appwebterminal.client.rendering.foundation

import com.mojang.blaze3d.systems.RenderSystem
import icu.takeneko.appwebterminal.client.all.BlurShader
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.ShaderInstance

class BlurPostProcess(xSize: Int, ySize: Int) : PostProcess(xSize, ySize) {

    private val swap1 = FrameBuffer(xSize, ySize)
    private val swap2 = FrameBuffer(xSize, ySize)
    val output = FrameBuffer(xSize, ySize)
    private val blurShader = BlurShader


    override fun resize(xSize: Int, ySize: Int) {
        super.resize(xSize, ySize)
        swap1.resize(xSize, ySize)
        swap2.resize(xSize, ySize)
        output.resize(xSize, ySize)
    }

    override fun apply(inputTexture: Int) {
        FogRenderer.levelFogColor()
        val background = RenderSystem.getShaderFogColor()
        swap1.setClearColor(background[0], background[1], background[2], background[3])
        swap2.setClearColor(background[0], background[1], background[2], background[3])
        output.setClearColor(background[0], background[1], background[2], background[3])
        swap1.clear()
        swap2.clear()
        output.clear()
        processOnce(
            blurShader,
            inputTexture,
            swap1,
        ) {
            applyCommonUniforms(this)
            safeGetUniform("BlurDir").set(0f, 1f)
        }
        processOnce(
            blurShader,
            swap1.colorTextureId,
            swap2,
        ) {
            applyCommonUniforms(this)
            safeGetUniform("BlurDir").set(1f, 0f)
        }
        processOnce(
            blurShader,
            swap2.colorTextureId,
            swap1,
        ) {
            applyCommonUniforms(this)
            safeGetUniform("BlurDir").set(0f, 1f)
        }
        processOnce(
            blurShader,
            swap1.colorTextureId,
            swap2,
        ) {
            applyCommonUniforms(this)
            safeGetUniform("BlurDir").set(1f, 0f)
        }
        output.copyColorsFrom(swap2.framebufferId)
    }

    fun applyCommonUniforms(instance: ShaderInstance) {
        instance.safeGetUniform("ProjMat").set(projectionMatrix)
        instance.safeGetUniform("InSize").set(xSize.toFloat(), ySize.toFloat())
        instance.safeGetUniform("OutSize").set(xSize.toFloat(), ySize.toFloat())
    }
}