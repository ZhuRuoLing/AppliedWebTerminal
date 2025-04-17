package icu.takeneko.appwebterminal

import com.mojang.logging.LogUtils
import dev.toma.configuration.Configuration
import dev.toma.configuration.config.format.ConfigFormats
import icu.takeneko.appwebterminal.all.onAddRegistries
import icu.takeneko.appwebterminal.all.onChunkUnloaded
import icu.takeneko.appwebterminal.all.onCommonSetup
import icu.takeneko.appwebterminal.all.onRegister
import icu.takeneko.appwebterminal.all.onServerStart
import icu.takeneko.appwebterminal.all.onServerStop
import icu.takeneko.appwebterminal.all.onServerTickPost
import icu.takeneko.appwebterminal.all.registerBlockEntities
import icu.takeneko.appwebterminal.all.registerBlocks
import icu.takeneko.appwebterminal.all.registerClientCommand
import icu.takeneko.appwebterminal.all.registerCreativeTab
import icu.takeneko.appwebterminal.all.registerItems
import icu.takeneko.appwebterminal.all.registerKeyImageProviders
import icu.takeneko.appwebterminal.all.registerNetworking
import icu.takeneko.appwebterminal.api.KeyImageProviderLoader
import icu.takeneko.appwebterminal.client.onLoadShaders
import icu.takeneko.appwebterminal.client.onRenderLevelPost
import icu.takeneko.appwebterminal.config.AppWebTerminalConfig
import icu.takeneko.appwebterminal.data.configureDataGeneration
import icu.takeneko.appwebterminal.resource.LanguageFileDownloader
import icu.takeneko.appwebterminal.util.KRegistrate
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runWhenOn

val registrate = KRegistrate.create(AppWebTerminal.MOD_ID)


@Mod(AppWebTerminal.MOD_ID)
object AppWebTerminal {
    const val MOD_ID = "appwebterminal"
    private val LOGGER: Logger = LogUtils.getLogger()
    val modBus: IEventBus = MOD_BUS
    internal val configHolder = Configuration.registerConfig(AppWebTerminalConfig::class.java, ConfigFormats.YAML)
    val config: AppWebTerminalConfig
        get() = configHolder.configInstance

    fun location(location: String): ResourceLocation = ResourceLocation(MOD_ID, location)

    init {
        registerBlockEntities()
        registerBlocks()
        registerItems()
        registerCreativeTab()
        registerNetworking()
        configureDataGeneration()
        registerKeyImageProviders(modBus)
        modBus.addListener(::onCommonSetup)
        modBus.addListener(::onAddRegistries)
        modBus.addListener(::onRegister)
        MinecraftForge.EVENT_BUS.addListener(::onServerStart)
        MinecraftForge.EVENT_BUS.addListener(::onServerStop)
        MinecraftForge.EVENT_BUS.addListener(::onChunkUnloaded)
        MinecraftForge.EVENT_BUS.addListener(::onServerTickPost)
        LanguageFileDownloader().start()
        KeyImageProviderLoader.compileContents()
        LOGGER.info("AppWebTerminal initialized")
        runWhenOn(Dist.CLIENT) {
            FORGE_BUS.addListener(::registerClientCommand)
            FORGE_BUS.addListener(::onRenderLevelPost)
            modBus.addListener(::onLoadShaders)
        }
    }


}