package icu.takeneko.appwebterminal.support

import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import appeng.api.stacks.GenericStack
import appeng.menu.me.crafting.CraftingStatus
import appeng.menu.me.crafting.CraftingStatusEntry
import icu.takeneko.appwebterminal.support.MECraftingStatusEntry.Companion.bundle
import icu.takeneko.appwebterminal.util.ComponentSerializer
import icu.takeneko.appwebterminal.util.ResourceLocationSerializer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@kotlinx.serialization.Serializable
data class MENetworkStatusBundle(
    val cpus: List<MECpuStatusBundle>,
    val craftingStatus: MECraftingStatusBundle?
)

@kotlinx.serialization.Serializable
data class MECraftingStatusBundle(
    val fullStatus: Boolean,
    val elapsedTime: Long,
    val remainingItemCount: Long,
    val startItemCount: Long,
    val entries: List<MECraftingStatusEntry>
) {
    companion object {
        val CraftingStatus.bundle: MECraftingStatusBundle
            get() = MECraftingStatusBundle(
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
    val what: String,
    val displayName: String,
    val storedAmount: Long,
    val activeAmount: Long,
    val pendingAmount: Long
) {
    companion object {
        val CraftingStatusEntry.bundle: MECraftingStatusEntry
            get() = MECraftingStatusEntry(
                this.serial,
                this.what.id.toString(),
                this.what.displayName.string,
                this.storedAmount,
                this.activeAmount,
                this.pendingAmount
            )
    }
}

@kotlinx.serialization.Serializable
data class MECpuStatusBundle(
    val id: Int,
    val name: String?,
    val busy: Boolean,
    val storageSize: Int,
    val coProcessorCount: Int,
    val craftingStatus: MECraftingJobStatusBundle?
)

@kotlinx.serialization.Serializable
data class MECraftingJobStatusBundle(
    val crafting: MEStack,
    val totalItems: Long,
    val progress: Long,
    val elapsedTimeNanos: Long
)

@kotlinx.serialization.Serializable
data class MEStack(
    val id: String,
    val amount: Long
) {
    companion object {
        val GenericStack.meStack: MEStack
            get() = MEStack(this.what.id.toString(), this.amount)
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
                this.description
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
                this.displayName,
                this.type.id
            )
        }
    }
}

@kotlinx.serialization.Serializable
data class TerminalInfo(val name: String, val uuid: String)
