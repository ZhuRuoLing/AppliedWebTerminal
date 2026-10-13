package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.client.rendering.AEKeyImageProvider
import net.minecraft.core.Registry
import net.neoforged.neoforge.registries.RegistryBuilder

object AWBuiltinRegistries {

    val keyImageProvider: Registry<AEKeyImageProvider<*>> = RegistryBuilder(AWRegistries.keyImageProvider)
        .sync(false)
        .create()
}