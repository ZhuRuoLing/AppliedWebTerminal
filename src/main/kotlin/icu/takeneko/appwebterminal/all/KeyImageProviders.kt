@file:Suppress("unused")

package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.providers.AEFluidKeyImageProvider
import icu.takeneko.appwebterminal.client.rendering.providers.AEItemKeyImageProvider
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

private val DR = DeferredRegister.create(AWRegistries.keyImageProvider, AppWebTerminal.MOD_ID)

val itemKeyImageProvider: DeferredHolder<out AEKeyImageProvider<*>, AEItemKeyImageProvider> = DR.register(
    "item",
    Supplier { AEItemKeyImageProvider }
)

val fluidKeyImageProvider: DeferredHolder<out AEKeyImageProvider<*>, AEFluidKeyImageProvider> = DR.register(
    "fluid",
    Supplier { AEFluidKeyImageProvider }
)

val extensionProviders =
    mutableMapOf<ResourceLocation, DeferredHolder<out AEKeyImageProvider<*>, AEKeyImageProvider<*>>>()

fun registerKeyImageProviders(modBus: IEventBus) {
    DR.register(modBus)
}