package icu.takeneko.appwebterminal.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.all.BlurPostProcessInstance
import icu.takeneko.appwebterminal.client.all.BlurShader
import icu.takeneko.appwebterminal.client.all.TexturedRoundRect
import icu.takeneko.appwebterminal.client.all.blurShaderLoaded
import icu.takeneko.appwebterminal.client.all.createPostProcess
import icu.takeneko.appwebterminal.client.all.roundRectShaderLoaded
import icu.takeneko.appwebterminal.client.gui.WebTerminalScreen
import icu.takeneko.appwebterminal.client.rendering.AEKeyRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.client.event.RenderLevelStageEvent


fun onLoadShaders(event: RegisterShadersEvent) {
    print("11111")
    val res = event.resourceProvider
    event.registerShader(
        ShaderInstance(
            res,
            AppWebTerminal.location("blur"),
            DefaultVertexFormat.POSITION
        )
    ) {
        BlurShader = it
        blurShaderLoaded = true
        createPostProcess()
    }

    event.registerShader(
        ShaderInstance(
            res,
            AppWebTerminal.location("rendertype_textured_round_rect"),
            DefaultVertexFormat.POSITION
        )
    ) {
        TexturedRoundRect = it
        roundRectShaderLoaded = true
        createPostProcess()
    }
}

fun onRenderLevelPost(event: RenderLevelStageEvent) {
    if (event.stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
        if (Minecraft.getInstance().screen is WebTerminalScreen) {
            val mainRenderTarget = Minecraft.getInstance().mainRenderTarget
            BlurPostProcessInstance?.apply(mainRenderTarget.frameBufferId)
            mainRenderTarget.bindWrite(true)
        }
        AEKeyRenderer.instance?.next()
    }
}