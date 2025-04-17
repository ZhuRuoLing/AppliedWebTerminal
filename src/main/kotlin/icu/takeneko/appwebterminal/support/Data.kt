package icu.takeneko.appwebterminal.support

import appeng.api.networking.crafting.CraftingJobStatus
import appeng.api.networking.crafting.ICraftingCPU
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import appeng.api.stacks.GenericStack
import appeng.api.stacks.KeyCounter
import appeng.menu.me.crafting.CraftingStatus
import appeng.menu.me.crafting.CraftingStatusEntry
import icu.takeneko.appwebterminal.support.AEKeyObject.Companion.serializable
import icu.takeneko.appwebterminal.support.MECraftingJobStatusBundle.Companion.serializable
import icu.takeneko.appwebterminal.support.MECraftingStatusEntry.Companion.bundle
import icu.takeneko.appwebterminal.support.MEStack.Companion.meStack
import icu.takeneko.appwebterminal.util.ComponentSerializer
import icu.takeneko.appwebterminal.util.ResourceLocationSerializer
import icu.takeneko.appwebterminal.util.strip
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.util.Objects

@kotlinx.serialization.Serializable
data class MECraftingStatusBundle(
    val fullStatus: Boolean,
    val elapsedTime: Long,
    val remainingItemCount: Long,
    val startItemCount: Long,
    val entries: List<MECraftingStatusEntry>
) {
    companion object {
        val CraftingStatus.bundle: MECraftingStatusBundle?
            get() = if(this.entries.isEmpty()) null else MECraftingStatusBundle(
                this.isFullStatus,
                this.elapsedTime,
                this.remainingItemCount,
                this.startItemCount,
                this.entries.map { it.bundle }
            )
    }
}

@kotlinx.serialization.Serializable
data class MECraftingStatusEntry(
    val serial: Long,
    val what: AEKeyObject?,
    val storedAmount: Long,
    val activeAmount: Long,
    val pendingAmount: Long
) {
    companion object {
        val CraftingStatusEntry.bundle: MECraftingStatusEntry
            get() = MECraftingStatusEntry(
                this.serial,
                this.what?.serializable(),
                this.storedAmount,
                this.activeAmount,
                this.pendingAmount
            )
    }
}

@kotlinx.serialization.Serializable
data class MECpuStatusBundle(
    val id: Int,
    @kotlinx.serialization.Serializable(with = ComponentSerializer::class)
    val name: Component?,
    val busy: Boolean,
    val storageSize: Long,
    val coProcessorCount: Int,
    val craftingStatus: MECraftingJobStatusBundle?
) {
    companion object {
        fun ICraftingCPU.asStatus(id: Int): MECpuStatusBundle {
            return MECpuStatusBundle(
                id,
                this.name,
                this.isBusy,
                this.availableStorage,
                this.coProcessors,
                this.jobStatus?.serializable()
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class MECraftingJobStatusBundle(
    val crafting: MEStack,
    val totalItems: Long,
    val progress: Long,
    val elapsedTimeNanos: Long
) {
    companion object {
        fun CraftingJobStatus.serializable() = MECraftingJobStatusBundle(
            this.crafting.meStack,
            this.totalItems,
            this.progress,
            this.elapsedTimeNanos
        )
    }
}

@kotlinx.serialization.Serializable
data class MEStack(
    val what: AEKeyObject,
    val amount: Long,
    var craftable: Boolean = false,
) {
    companion object {
        val GenericStack.meStack: MEStack
            get() = MEStack(this.what.serializable(), this.amount)
        val KeyCounter.meStacks: List<MEStack>
            get() = this.map { MEStack(it.key.serializable(), it.longValue) }
    }
}

@kotlinx.serialization.Serializable
data class AEKeyTypeObject(
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val id: ResourceLocation,
    @kotlinx.serialization.Serializable(with = ComponentSerializer::class)
    val description: Component,
) {
    companion object {
        fun AEKeyType.serializable(): AEKeyTypeObject {
            return AEKeyTypeObject(
                this.id,
                this.description.strip()
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class AEKeyObject(
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val id: ResourceLocation,
    @kotlinx.serialization.Serializable(with = ComponentSerializer::class)
    val displayName: Component,
    @kotlinx.serialization.Serializable(with = ResourceLocationSerializer::class)
    val type: ResourceLocation,
) {
    companion object {
        fun AEKey.serializable(): AEKeyObject {
            return AEKeyObject(
                this.id,
                this.displayName.strip(),
                this.type.id
            )
        }
    }

    fun myHash(): Int {
        return Objects.hash(this.id, this.type)
    }
}

@kotlinx.serialization.Serializable
data class TerminalInfo(val name: String, val uuid: String)

@kotlinx.serialization.Serializable
data class PageMeta(val total: Int, val page: Int, val limit: Int, val totalPages: Int)
