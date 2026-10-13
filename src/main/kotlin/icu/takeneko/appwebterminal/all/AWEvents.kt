package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.config.AppWebTerminalConfig
import icu.takeneko.appwebterminal.api.KeyImageProviderLoader
import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.resource.LanguageFileDownloader
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import icu.takeneko.appwebterminal.util.KubejsI18nSupport
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.level.ChunkEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent

fun onCommonSetup(event: FMLCommonSetupEvent) {
    registrate.getAll(Registries.BLOCK).forEach {
        val block = it.get()
        if (block is LateInitSupported) {
            block.lateInit()
        }
    }
    KubejsI18nSupport.init()
    event.enqueueWork {
        LanguageFileDownloader(AppWebTerminalConfig.ASSETS_SOURCE.get()).start()
    }
}

fun onAddRegistries(event: NewRegistryEvent) {
    event.register(AWBuiltinRegistries.keyImageProvider)
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

fun onServerTickPost(e: ServerTickEvent.Pre) {
    AENetworkSupport.tick()
}

fun onRegister(event: RegisterEvent) {
    if (event.registryKey == AWRegistries.keyImageProvider) {
        KeyImageProviderLoader.providers.forEach { t, u ->
            event.register(
                AWRegistries.keyImageProvider,
                t,
                u
            )
        }
    }
}

fun onServerStart(event: ServerStartedEvent) {
    HttpServerLifecycleSupport.launch(AppWebTerminalConfig.HTTP_PORT.get())
}

fun onServerStop(event: ServerStoppedEvent) {
    HttpServerLifecycleSupport.stop()
    AENetworkSupport.reset()
}
