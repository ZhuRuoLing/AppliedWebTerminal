package icu.takeneko.appwebterminal

// TODO(compat-excluded): restore together with build.gradle's compileKotlin exclude
// import icu.takeneko.appwebterminal.compat.advancedae.registerAAECompat
import com.mojang.logging.LogUtils
import icu.takeneko.appwebterminal.all.AWBlockEntities
import icu.takeneko.appwebterminal.all.AWBlocks
import icu.takeneko.appwebterminal.all.AWCreativeTabs
import icu.takeneko.appwebterminal.all.AWItems
import icu.takeneko.appwebterminal.all.AWNetworking
import icu.takeneko.appwebterminal.all.onAddRegistries
import icu.takeneko.appwebterminal.all.onChunkUnloaded
import icu.takeneko.appwebterminal.all.onCommonSetup
import icu.takeneko.appwebterminal.all.onRegister
import icu.takeneko.appwebterminal.all.onServerStart
import icu.takeneko.appwebterminal.all.onServerStop
import icu.takeneko.appwebterminal.all.onServerTickPost
import icu.takeneko.appwebterminal.all.registerKeyImageProviders
import icu.takeneko.appwebterminal.api.KeyImageProviderLoader
import icu.takeneko.appwebterminal.api.crafting.impl.AECraftingCpuContainerImpl
import icu.takeneko.appwebterminal.config.AppWebTerminalConfig
import icu.takeneko.appwebterminal.data.AWDataGenerator
import icu.takeneko.appwebterminal.util.KRegistrate
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

val registrate = KRegistrate.create(AppWebTerminal.MOD_ID)


@Mod(AppWebTerminal.MOD_ID)
object AppWebTerminal {
    const val MOD_ID = "appwebterminal"
    private val LOGGER: Logger = LogUtils.getLogger()
    val modBus: IEventBus = MOD_BUS

    fun location(location: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, location)

    init {
        LOADING_CONTEXT.activeContainer.registerConfig(ModConfig.Type.COMMON, AppWebTerminalConfig.SPEC)
        this.setupRegistration()
        this.setupEvents()

        AWDataGenerator.configureDataGeneration()
        KeyImageProviderLoader.configure()
        AECraftingCpuContainerImpl.register()

        // TODO(compat-excluded): restore together with build.gradle's compileKotlin exclude
        // registerAAECompat()

        LOGGER.info("Ciallo～(∠・ω< )⌒★")
    }

    fun setupRegistration() {
        AWBlocks.registerBlocks()
        AWBlockEntities.registerBlockEntities()
        AWItems.registerItems()
        AWCreativeTabs.registerCreativeTab()
        AWNetworking.registerNetworking()
        registerKeyImageProviders(modBus)
    }

    fun setupEvents() {
        modBus.addListener(::onCommonSetup)
        modBus.addListener(::onAddRegistries)
        modBus.addListener(::onRegister)

        NeoForge.EVENT_BUS.addListener(::onServerStart)
        NeoForge.EVENT_BUS.addListener(::onServerStop)
        NeoForge.EVENT_BUS.addListener(::onChunkUnloaded)
        NeoForge.EVENT_BUS.addListener(::onServerTickPost)
    }
}