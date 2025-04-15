package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraftforge.registries.IForgeRegistry

val KeyImageProviderRegistryKey: ResourceKey<Registry<AEKeyImageProvider<*>>> =
    ResourceKey.createRegistryKey<AEKeyImageProvider<*>>(AppWebTerminal.location("key_image_providers"))

@Suppress("ObjectPropertyName")
internal var _KeyImageProviderRegistry: (() -> IForgeRegistry<AEKeyImageProvider<*>>)? = null

val KeyImageProviderRegistry: IForgeRegistry<AEKeyImageProvider<*>>
    get() = _KeyImageProviderRegistry!!()