package icu.takeneko.appwebterminal.client.all

import icu.takeneko.appwebterminal.client.rendering.foundation.BlurPostProcess
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance

internal lateinit var TexturedRoundRect: ShaderInstance
internal lateinit var BlurShader: ShaderInstance

internal var roundRectShaderLoaded = false
internal var blurShaderLoaded = false

internal var BlurPostProcessInstance: BlurPostProcess? = null

internal fun createPostProcess() {
    if (roundRectShaderLoaded && blurShaderLoaded) {
        val window = Minecraft.getInstance().window
        BlurPostProcessInstance = BlurPostProcess(window.width, window.height)
    }
}