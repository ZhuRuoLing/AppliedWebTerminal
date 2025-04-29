package icu.takeneko.appwebterminal.api.crafting

import appeng.api.config.CpuSelectionMode
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.stacks.AEKey
import appeng.api.stacks.KeyCounter
import appeng.menu.me.common.IncrementalUpdateHelper
import appeng.menu.me.crafting.CraftingStatus
import java.util.function.Consumer

interface CraftingCpuContainer {

    fun unwrap(): ICraftingCPU

    fun selectionMode(): CpuSelectionMode

    fun cantStoreItems(): Boolean

    fun createCraftingStatus(helper: IncrementalUpdateHelper): CraftingStatus

    fun removeUpdateListener(cons: Consumer<AEKey>)

    fun addUpdateListener(cons: Consumer<AEKey>)

    fun getAllItems(): KeyCounter

    @FunctionalInterface
    interface Constructor<T> where T : ICraftingCPU {
        fun create(cpu: T): CraftingCpuContainer
    }
}