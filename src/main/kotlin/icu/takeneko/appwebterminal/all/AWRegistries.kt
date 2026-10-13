package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object AWRegistries {
    val keyImageProvider: ResourceKey<Registry<AEKeyImageProvider<*>>> =
        ResourceKey.createRegistryKey(AppWebTerminal.location("key_image_providers"))
}

