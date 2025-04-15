@file:Suppress("unused")

package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.rendering.providers.AEFluidKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.providers.AEItemKeyImageProvider
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

private val DR = DeferredRegister.create(KeyImageProviderRegistryKey, AppWebTerminal.MOD_ID)

val itemKeyImageProvider: RegistryObject<AEItemKeyImageProvider> = DR.register("item") {
    AEItemKeyImageProvider
}

val fluidKeyImageProvider: RegistryObject<AEFluidKeyImageProvider> = DR.register("fluid") {
    AEFluidKeyImageProvider
}

fun registerKeyImageProviders(modBus: IEventBus) {
    DR.register(modBus)
}