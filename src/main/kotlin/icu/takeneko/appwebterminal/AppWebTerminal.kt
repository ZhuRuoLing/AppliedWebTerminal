package icu.takeneko.appwebterminal

import com.mojang.logging.LogUtils
import icu.takeneko.appwebterminal.all.onCommonSetup
import icu.takeneko.appwebterminal.all.registerBlockEntities
import icu.takeneko.appwebterminal.all.registerBlocks
import icu.takeneko.appwebterminal.all.registerNetworking
import icu.takeneko.appwebterminal.data.configureDataGeneration
import icu.takeneko.appwebterminal.util.KRegistrate
import net.minecraft.resources.ResourceLocation
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

    fun location(location: String): ResourceLocation = ResourceLocation(MOD_ID, location)

    init {
        registerBlockEntities()
        registerBlocks()
        registerNetworking()
        configureDataGeneration()
        modBus.addListener(::onCommonSetup)
        LOGGER.info("AppWebTerminal initialized")
    }


}