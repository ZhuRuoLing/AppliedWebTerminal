@file:Suppress("unused")

package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.api.KeyImageProviderLoader
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.providers.AEFluidKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.providers.AEItemKeyImageProvider
import net.minecraft.resources.ResourceLocation
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

val extensionProviders = mutableMapOf<ResourceLocation, RegistryObject<AEKeyImageProvider<*>>>()

fun registerKeyImageProviders(modBus: IEventBus) {
    DR.register(modBus)
}