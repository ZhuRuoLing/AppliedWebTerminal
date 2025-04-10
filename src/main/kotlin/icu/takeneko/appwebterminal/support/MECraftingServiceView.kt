package icu.takeneko.appwebterminal.support

import appeng.api.config.CpuSelectionMode
import appeng.api.networking.IGrid
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.networking.crafting.ICraftingService
import appeng.api.stacks.KeyCounter
import appeng.me.cluster.implementations.CraftingCPUCluster
import appeng.menu.me.common.IncrementalUpdateHelper
import appeng.menu.me.crafting.CraftingStatus
import com.google.common.collect.ImmutableSet

class MECraftingServiceView(
    val grid: IGrid,
) {
    private val craftingService: ICraftingService = grid.craftingService
    private var allCpus: ImmutableSet<ICraftingCPU> = craftingService.cpus
    private var cpuId = 1
    private val cpuMap = mutableMapOf<Int, ICraftingCPU>()
    private var selectedCpuId = -1
    private var currentCpu: CraftingCPUCluster? = null
    private val incrementalUpdateHelper = IncrementalUpdateHelper()
    private val cpuUpdateListener = incrementalUpdateHelper::addChange
    private var cpuSchedulingMode = CpuSelectionMode.ANY
    private var craftingStatus: CraftingStatus? = null
    private var cantStoreItems: Boolean = false

    fun tick() {
        if (allCpus != craftingService.cpus) {
            allCpus = craftingService.cpus
            allCpus.forEach {
                if (it !in cpuMap.values) {
                    cpuMap[cpuId++] = it
                }
            }
            cpuMap.entries.toList().forEach { (k, v) ->
                if (v !in allCpus) {
                    cpuMap.remove(k)
                }
            }
            if (currentCpu !in allCpus) {
                currentCpu = null
            }
        }
        if (this.currentCpu != null) {
            this.cpuSchedulingMode = this.currentCpu!!.selectionMode
            this.cantStoreItems = this.currentCpu!!.craftingLogic.isCantStoreItems;
            this.craftingStatus = CraftingStatus.create(this.incrementalUpdateHelper, this.currentCpu!!.craftingLogic)
            this.incrementalUpdateHelper.commitChanges()
        }
        updateRemote()
    }

    private fun updateRemote() {
    }

    fun selectCpu(id: Int) {
        if (id == -1) {
            this.selectedCpuId = -1
            this.currentCpu = null
        }
        this.cpuMap[id]?.let {
            this.selectedCpuId = id
            selectCpu(it)
        }
    }

    private fun selectCpu(cpu: ICraftingCPU) {
        if (cpu != currentCpu) {
            this.currentCpu?.craftingLogic?.removeListener(cpuUpdateListener)
            this.incrementalUpdateHelper.reset()
            if (cpu is CraftingCPUCluster) {
                this.currentCpu = cpu
                val keyCounter = KeyCounter()
                this.currentCpu!!.craftingLogic.getAllItems(keyCounter)
                keyCounter.forEach {
                    this.incrementalUpdateHelper.addChange(it.key)
                }
                this.currentCpu!!.craftingLogic.addListener(cpuUpdateListener)
            } else {
                this.currentCpu = null
            }
        }
    }
}