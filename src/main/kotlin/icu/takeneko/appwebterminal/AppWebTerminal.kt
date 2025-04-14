package icu.takeneko.appwebterminal

import com.mojang.logging.LogUtils
import dev.toma.configuration.Configuration
import dev.toma.configuration.config.format.ConfigFormats
import icu.takeneko.appwebterminal.all.onBuildCreativeTab
import icu.takeneko.appwebterminal.all.onChunkUnloaded
import icu.takeneko.appwebterminal.all.onCommonSetup
import icu.takeneko.appwebterminal.all.onServerStart
import icu.takeneko.appwebterminal.all.onServerStop
import icu.takeneko.appwebterminal.all.onServerTickPost
import icu.takeneko.appwebterminal.all.registerBlockEntities
import icu.takeneko.appwebterminal.all.registerBlocks
import icu.takeneko.appwebterminal.all.registerNetworking
import icu.takeneko.appwebterminal.config.AppWebTerminalConfig
import icu.takeneko.appwebterminal.data.configureDataGeneration
import icu.takeneko.appwebterminal.resource.LanguageFileDownloader
import icu.takeneko.appwebterminal.util.KRegistrate
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

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
        registerNetworking()
        configureDataGeneration()
        modBus.addListener(::onCommonSetup)
        modBus.addListener(::onBuildCreativeTab)
        MinecraftForge.EVENT_BUS.addListener(::onServerStart)
        MinecraftForge.EVENT_BUS.addListener(::onServerStop)
        MinecraftForge.EVENT_BUS.addListener(::onChunkUnloaded)
        MinecraftForge.EVENT_BUS.addListener(::onServerTickPost)
        LanguageFileDownloader().start()
        LOGGER.info("AppWebTerminal initialized")
    }


}