package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import net.minecraft.core.registries.Registries
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

fun onCommonSetup(event: FMLCommonSetupEvent) {
    registrate.getAll(Registries.BLOCK).forEach {
        val block = it.get()
        if (block is LateInitSupported) {
            block.lateInit()
        }
    }
}

fun onServerStart(event: ServerStartedEvent) {
    HttpServerLifecycleSupport.launch(AppWebTerminal.config.httpPort)
}

fun onServerStop(event: ServerStoppedEvent) {
    HttpServerLifecycleSupport.stop()
    AENetworkSupport.reset()
}