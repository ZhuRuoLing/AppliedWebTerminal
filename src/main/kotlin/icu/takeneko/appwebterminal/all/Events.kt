package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.api.KeyImageProviderLoader
import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import icu.takeneko.appwebterminal.util.KubejsI18nSupport
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.event.level.ChunkEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.NewRegistryEvent
import net.minecraftforge.registries.RegisterEvent
import net.minecraftforge.registries.RegistryBuilder

fun onCommonSetup(event: FMLCommonSetupEvent) {
    registrate.getAll(Registries.BLOCK).forEach {
        val block = it.get()
        if (block is LateInitSupported) {
            block.lateInit()
        }
    }
    KubejsI18nSupport.init()
}

fun onAddRegistries(event: NewRegistryEvent) {
    event.create(
        RegistryBuilder<AEKeyImageProvider<*>>().setName(KeyImageProviderRegistryKey.location())
    ) { _KeyImageProviderRegistry = { it } }
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

fun onServerTickPost(event: ServerTickEvent) {
    if (event.phase != TickEvent.Phase.END) return
    AENetworkSupport.tick()
}

fun onRegister(event: RegisterEvent) {
    if (event.registryKey == KeyImageProviderRegistryKey) {
        KeyImageProviderLoader.providers.forEach { t, u ->
            event.register(
                KeyImageProviderRegistryKey,
                t,
                u
            )
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
