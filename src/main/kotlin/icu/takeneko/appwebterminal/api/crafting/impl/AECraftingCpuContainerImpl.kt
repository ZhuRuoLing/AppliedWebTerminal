package icu.takeneko.appwebterminal.api.crafting.impl

import appeng.api.config.CpuSelectionMode
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.stacks.AEKey
import appeng.api.stacks.KeyCounter
import appeng.me.cluster.implementations.CraftingCPUCluster
import appeng.menu.me.common.IncrementalUpdateHelper
import appeng.menu.me.crafting.CraftingStatus
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainer
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainerHelper
import java.util.function.Consumer

class AECraftingCpuContainerImpl(private val cluster: CraftingCPUCluster) : CraftingCpuContainer {

    override fun unwrap(): ICraftingCPU = cluster

    override fun selectionMode(): CpuSelectionMode = cluster.selectionMode

    override fun cantStoreItems(): Boolean = cluster.craftingLogic.isCantStoreItems

    override fun createCraftingStatus(helper: IncrementalUpdateHelper): CraftingStatus {
        return CraftingStatus.create(helper, cluster.craftingLogic)
    }

    override fun removeUpdateListener(cons: Consumer<AEKey>) {
        cluster.craftingLogic.removeListener(cons)
    }

    override fun addUpdateListener(cons: Consumer<AEKey>) {
        cluster.craftingLogic.addListener(cons)
    }

    override fun getAllItems(): KeyCounter {
        val counter = KeyCounter()
        cluster.craftingLogic.getAllItems(counter)
        return counter
    }

    companion object {
        fun register() =
            CraftingCpuContainerHelper.registerConstructor(
                CraftingCPUCluster::class.java,
                object : CraftingCpuContainer.Constructor<CraftingCPUCluster> {
                    override fun create(cpu: CraftingCPUCluster) = AECraftingCpuContainerImpl(cpu)
                }
            )
    }
}