package icu.takeneko.appwebterminal.block.part

import appeng.api.networking.IGrid
import appeng.api.networking.IGridNodeListener
import appeng.api.parts.IPartCollisionHelper
import appeng.api.parts.IPartItem
import appeng.api.parts.IPartModel
import appeng.items.parts.PartModels
import appeng.parts.AEBasePart
import appeng.parts.PartModel
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.networking.OpenWebTerminalScreenPacket
import icu.takeneko.appwebterminal.support.AENetworkAccess
import icu.takeneko.appwebterminal.support.AENetworkSupport
import io.ktor.util.generateNonce
import io.ktor.util.generateNonceBlocking
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import java.util.UUID

class WebTerminalPart(item: IPartItem<*>) : AEBasePart(item), AENetworkAccess {

    private var id: UUID = UUID.randomUUID()
    var displayName: String = "ME Web Terminal"
        private set
    var password: String = "AppliedWebTerminal"
        private set

    private var nonce = generateNonceBlocking()
    private var registered = false

    override fun getBoxes(bch: IPartCollisionHelper) {
        bch.addBox(2.0, 2.0, 14.0, 14.0, 14.0, 16.0)
        bch.addBox(4.0, 4.0, 13.0, 12.0, 12.0, 14.0)
    }

    override fun onClicked(player: Player, pos: Vec3): Boolean {
        if (player.level().isClientSide) {
            return true
        }
        PacketDistributor.sendToPlayer(
            player as ServerPlayer, OpenWebTerminalScreenPacket(
                displayName,
                id,
                worldPosition(),
                password,
                mainNode.isOnline
            )
        )
        return true
    }

    override fun onPlacement(player: Player?) {
        super.onPlacement(player)
        val ownerName = player?.gameProfile?.name
        if (ownerName != null) {
            this.displayName = "${ownerName}'s Web Terminal"
            markDirty()
        }
    }

    override fun writeToNBT(data: CompoundTag, registries: HolderLookup.Provider) {
        super.writeToNBT(data, registries)
        data.putString("Name", displayName)
        data.putUUID("UUID", id)
        data.putString("Password", password)
    }

    override fun writeToStream(data: RegistryFriendlyByteBuf) {
        super.writeToStream(data)
        data.writeUtf(displayName)
        data.writeUUID(id)
        data.writeUtf(password)
    }

    override fun readFromNBT(data: CompoundTag, registries: HolderLookup.Provider) {
        super.readFromNBT(data, registries)
        this.displayName = if (data.contains("Name")) data.getString("Name") else "ME Web Terminal"
        this.id = if (data.hasUUID("UUID")) data.getUUID("UUID") else UUID.randomUUID()
        this.password = if (data.contains("Password")) data.getString("Password") else "AppliedWebTerminal"
    }

    override fun readFromStream(data: RegistryFriendlyByteBuf): Boolean {
        super.readFromStream(data)
        this.displayName = data.readUtf()
        this.id = data.readUUID()
        this.password = data.readUtf()
        return true
    }

    override fun getStaticModels(): IPartModel {
        if (isActive && isPowered) {
            return MODELS_ON
        }
        return MODELS_OFF
    }

    override fun onMainNodeStateChanged(reason: IGridNodeListener.State) {
        super.onMainNodeStateChanged(reason)
        if (!registered && mainNode.isOnline) {
            AENetworkSupport.register(this)
            registered = true
        }
        if (registered && !mainNode.isOnline) {
            AENetworkSupport.remove(this)
            registered = false
        }
    }

    override fun removeFromWorld() {
        super.removeFromWorld()
        AENetworkSupport.remove(this)
    }

    companion object {
        @JvmStatic
        @PartModels
        val MODEL_OFF = AppWebTerminal.location("part/web_terminal_off")

        @JvmStatic
        @PartModels
        val MODEL_ON = AppWebTerminal.location("part/web_terminal_on")

        @JvmStatic
        val MODELS_ON = PartModel(MODEL_ON)

        @JvmStatic
        val MODELS_OFF = PartModel(MODEL_OFF)
    }

    override fun getId(): UUID = id

    override fun worldPosition(): BlockPos = blockEntity.blockPos

    override fun markDirty() {
        blockEntity.setChanged()
    }

    override fun getGrid(): IGrid? = this.mainNode.grid

    override fun auth(password: String): Boolean {
        return password == this.password
    }

    override fun update(displayName: String, password: String): Boolean {
        this.displayName = displayName
        val oldPassword = this.password
        this.password = password
        return if (oldPassword != password) {
            AENetworkSupport.requestSessionReset(this)
            this.nonce = generateNonce()
            true
        } else {
            false
        }
    }

    override fun validateNonce(nonce: String): Boolean = this.nonce == nonce

    override fun getNonce(): String = nonce

    override fun getTerminalName(): String = displayName

    override fun level(): Level? = this.blockEntity.level
}