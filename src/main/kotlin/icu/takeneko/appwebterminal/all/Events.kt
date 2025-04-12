package icu.takeneko.appwebterminal.all

import appeng.api.ids.AECreativeTabIds
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.level.ChunkEvent
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

fun onChunkUnloaded(event: ChunkEvent.Unload) {
    if (event.level is ServerLevel) {
        if (event.chunk is LevelChunk) {
            (event.chunk as LevelChunk).blockEntities.forEach { (_, blockEntity) ->
                if (blockEntity is WebTerminalBlockEntity) {
                    AENetworkSupport.remove(blockEntity)
                }
            }
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

fun onBuildCreativeTab(event: BuildCreativeModeTabContentsEvent) {
    if (AECreativeTabIds.MAIN == event.tabKey) {
        event.accept(meWebTerminal.asStack())
    }
}