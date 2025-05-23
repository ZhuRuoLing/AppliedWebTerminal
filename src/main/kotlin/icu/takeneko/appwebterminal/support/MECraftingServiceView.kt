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
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainer
import icu.takeneko.appwebterminal.api.crafting.CraftingCpuContainerHelper
import icu.takeneko.appwebterminal.support.MECpuStatusBundle.Companion.asStatus
import icu.takeneko.appwebterminal.support.MECraftingStatusBundle.Companion.bundle
import icu.takeneko.appwebterminal.support.http.websocket.MECraftingServiceStatusBundle

class MECraftingServiceView(
    val grid: IGrid,
) {
    private val craftingService: ICraftingService = grid.craftingService
    private var allCpus: ImmutableSet<ICraftingCPU> = craftingService.cpus
    private var cpuId = 1
    private val cpuMap = mutableMapOf<Int, ICraftingCPU>()
    private var selectedCpuId = -1
    private var currentCpu: CraftingCpuContainer? = null
    private val incrementalUpdateHelper = IncrementalUpdateHelper()
    private val cpuUpdateListener = incrementalUpdateHelper::addChange
    private var cpuSchedulingMode = CpuSelectionMode.ANY
    private var craftingStatus: CraftingStatus? = null
    private var cantStoreItems: Boolean = false
    private var fullUpgradeSent = false

    init {
        allCpus.forEach {
            cpuMap[cpuId++] = it
        }
    }

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
            if (currentCpu?.unwrap() !in allCpus) {
                currentCpu = null
                craftingStatus = null
            }
        }
        if (this.currentCpu != null && fullUpgradeSent) {
            this.cpuSchedulingMode = this.currentCpu!!.selectionMode()
            this.cantStoreItems = this.currentCpu!!.cantStoreItems();
            this.craftingStatus = this.currentCpu!!.createCraftingStatus(this.incrementalUpdateHelper)
            this.incrementalUpdateHelper.commitChanges()
        }
    }

    fun selectCpu(id: Int) {
        if (id == -1) {
            this.selectedCpuId = -1
            this.currentCpu = null
            this.currentCpu?.removeUpdateListener(cpuUpdateListener)
            this.incrementalUpdateHelper.reset()
            this.fullUpgradeSent = false
            this.craftingStatus = null
        }
        this.cpuMap[id]?.let {
            this.selectedCpuId = id
            selectCpu(it)
        }
    }

    private fun selectCpu(cpu: ICraftingCPU) {
        this.currentCpu?.removeUpdateListener(cpuUpdateListener)
        this.incrementalUpdateHelper.reset()
        this.fullUpgradeSent = false
        this.craftingStatus = null
        val selection = CraftingCpuContainerHelper.create(cpu)
        if (selection != null) {
            this.currentCpu = selection
            val keyCounter = this.currentCpu!!.getAllItems()
            keyCounter.forEach {
                this.incrementalUpdateHelper.addChange(it.key)
            }
            this.currentCpu!!.addUpdateListener(cpuUpdateListener)
            this.craftingStatus = selection.createCraftingStatus(this.incrementalUpdateHelper)
        } else {
            this.currentCpu = null
        }
    }

    public fun getCpu(id: Int): ICraftingCPU? {
        return cpuMap[id]
    }

    fun createUpdateMessage(): MECraftingServiceStatusBundle {
        val data = MECraftingServiceStatusBundle(
            cpuMap.map { (k, v) -> v.asStatus(k) },
            craftingStatus?.bundle
        )
        if (data.craftingStatus?.entries != null) {
            fullUpgradeSent = true
        }
        return data
    }
}