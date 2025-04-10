package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.block.LateInitSupported
import icu.takeneko.appwebterminal.registrate
import net.minecraft.core.registries.Registries
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

fun onCommonSetup(event: FMLCommonSetupEvent) {
    registrate.getAll(Registries.BLOCK).forEach {
        val block = it.get()
        if (block is LateInitSupported) {
            block.lateInit()
        }
    }
}