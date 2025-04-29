package icu.takeneko.appwebterminal.api.crafting

import appeng.api.networking.crafting.ICraftingCPU

object CraftingCpuContainerHelper {
    private val constructors =
        mutableMapOf<Class<out ICraftingCPU>, CraftingCpuContainer.Constructor<ICraftingCPU>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> registerConstructor(
        clazz: Class<T>,
        constructor: CraftingCpuContainer.Constructor<T>
    ) where T : ICraftingCPU {
        constructors.put(clazz, constructor as CraftingCpuContainer.Constructor<ICraftingCPU>)
    }

    fun create(
        instance: ICraftingCPU
    ): CraftingCpuContainer? {
        val clazz = instance.javaClass as Class<out ICraftingCPU>
        return constructors[clazz]?.create(instance)
    }

}