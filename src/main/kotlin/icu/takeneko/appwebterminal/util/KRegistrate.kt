package icu.takeneko.appwebterminal.util

import com.tterrag.registrate.Registrate
import net.minecraftforge.eventbus.api.IEventBus
import thedarkcolour.kotlinforforge.forge.MOD_BUS

class KRegistrate(modid: String) : Registrate(modid) {

    private lateinit var modEventBus: IEventBus

    override fun getModEventBus(): IEventBus {
        return modEventBus
    }

    override fun registerEventListeners(bus: IEventBus): Registrate {
        this.modEventBus = bus;
        return super.registerEventListeners(bus)
    }

    companion object {
        fun create(modid: String): Registrate {
            return KRegistrate(modid).apply {
                this.registerEventListeners(MOD_BUS)
            }
        }
    }
}