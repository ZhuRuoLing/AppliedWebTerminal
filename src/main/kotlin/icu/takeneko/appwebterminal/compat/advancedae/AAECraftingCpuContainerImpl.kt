package icu.takeneko.appwebterminal.compat.advancedae

import appeng.api.config.CpuSelectionMode
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.stacks.AEKey
import appeng.api.stacks.KeyCounter
import appeng.menu.me.common.IncrementalUpdateHelper
import appeng.menu.me.crafting.CraftingStatus
import appeng.menu.me.crafting.CraftingStatusEntry
import com.google.common.collect.ImmutableList
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainer
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainerHelper
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.LoadingModList
import net.pedroksl.advanced_ae.common.cluster.AdvCraftingCPU
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic
import java.util.function.Consumer

class AAECraftingCpuContainerImpl(private val cpu: AdvCraftingCPU) : CraftingCpuContainer {
    override fun unwrap(): ICraftingCPU = cpu

    override fun selectionMode(): CpuSelectionMode = cpu.selectionMode
    override fun cantStoreItems(): Boolean = cpu.craftingLogic.isCantStoreItems

    override fun createCraftingStatus(helper: IncrementalUpdateHelper): CraftingStatus {
        return createCraftingStatusInternal(helper, cpu.craftingLogic)
    }

    @Suppress("DEPRECATION")
    private fun createCraftingStatusInternal(
        changes: IncrementalUpdateHelper,
        logic: AdvCraftingCPULogic
    ): CraftingStatus {
        val full = changes.isFullUpdate()
        val newEntries = ImmutableList.builder<CraftingStatusEntry>()

        for (what in changes) {
            val storedCount = logic.getStored(what)
            val activeCount = logic.getWaitingFor(what)
            val pendingCount = logic.getPendingOutputs(what)
            var sentStack = what
            if (!full && changes.getSerial(what) != null) {
                sentStack = null
            }

            val entry = CraftingStatusEntry(
                changes.getOrAssignSerial(what),
                sentStack,
                storedCount,
                activeCount,
                pendingCount
            )
            newEntries.add(entry)
            if (entry.isDeleted()) {
                changes.removeSerial(what)
            }
        }

        val elapsedTime = logic.getElapsedTimeTracker().elapsedTime
        val remainingItems = logic.getElapsedTimeTracker().remainingItemCount
        val startItems = logic.getElapsedTimeTracker().startItemCount
        return CraftingStatus(
            full,
            elapsedTime,
            remainingItems,
            startItems,
            newEntries.build()
        )
    }

    override fun removeUpdateListener(cons: Consumer<AEKey>) {
        cpu.craftingLogic.removeListener(cons)
    }

    override fun addUpdateListener(cons: Consumer<AEKey>) {
        cpu.craftingLogic.addListener(cons)
    }

    override fun getAllItems(): KeyCounter {
        val keyCounter = KeyCounter()
        cpu.craftingLogic.getAllItems(keyCounter)
        return keyCounter
    }
}

fun registerAAECompat() {
    if (ModList.get().isLoaded("advanced_ae")) {
        CraftingCpuContainerHelper.registerConstructor(
            AdvCraftingCPU::class.java,
            object : CraftingCpuContainer.Constructor<AdvCraftingCPU>{
                override fun create(cpu: AdvCraftingCPU): CraftingCpuContainer = AAECraftingCpuContainerImpl(cpu)
            }
        )
    }
}