package icu.takeneko.appwebterminal.client

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.all.registerClientCommand
import icu.takeneko.appwebterminal.client.rendering.AEKeyRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RenderFrameEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

@Mod(AppWebTerminal.MOD_ID, dist = [Dist.CLIENT])
object AppWebTerminalClient {
    init {
        FORGE_BUS.addListener(::registerClientCommand)
        FORGE_BUS.addListener(::on)
    }

    fun on(event: RenderLevelStageEvent) {
        if (event.stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            AEKeyRenderer.instance?.next()
        }
    }
}