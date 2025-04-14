package icu.takeneko.appwebterminal.all

import appeng.api.ids.AECreativeTabIds
import appeng.api.stacks.AEKeyTypes
import appeng.client.commands.ClientCommands
import appeng.core.AppEng
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.client.rendering.AEItemKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.support.http.HttpServerLifecycleSupport
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.CommandEvent
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
}

fun onAddRegistries(event: NewRegistryEvent) {
    event.create(
        RegistryBuilder<AEKeyImageProvider<*>>()
            .setName(KeyImageProviderRegistryKey.location())
            .onValidate { _, _, _, key, _ ->
                if (AEKeyTypes.get(key) == null) {
                    throw IllegalStateException("Key $key does not represent an AEKeyType.")
                }
            }.disableSync()
    ) { _KeyImageProviderRegistry = { it } }
}

fun registerImageProviders(event: RegisterEvent) {
    if (event.registryKey == KeyImageProviderRegistryKey) {
        event.register(
            KeyImageProviderRegistryKey,
            AppEng.makeId("i")
        ) {
            AEItemKeyImageProvider
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

fun onServerTickPost(event: ServerTickEvent) {
    if (event.phase != TickEvent.Phase.END) return
    AENetworkSupport.tick()
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