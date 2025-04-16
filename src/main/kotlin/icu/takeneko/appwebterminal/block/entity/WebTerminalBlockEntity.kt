package icu.takeneko.appwebterminal.block.entity

import appeng.api.networking.GridFlags
import appeng.api.networking.IGrid
import appeng.api.networking.IGridNodeListener
import appeng.api.orientation.BlockOrientation
import appeng.blockentity.grid.AENetworkBlockEntity
import icu.takeneko.appwebterminal.all.MEWebTerminal
import icu.takeneko.appwebterminal.block.WebTerminalBlock
import icu.takeneko.appwebterminal.support.AENetworkAccess
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.util.get
import io.ktor.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.UUID

class WebTerminalBlockEntity(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState
) : AENetworkBlockEntity(blockEntityType, blockPos, blockState), AENetworkAccess {

    private var id: UUID = UUID.randomUUID()
    var displayName: String = "ME Web Terminal"
        private set
    var password: String = "AppliedWebTerminal"
        private set

    private var nonce = generateNonce()
    private var registered = false

    init {
        this.mainNode.setExposedOnSides(Direction.entries.toMutableSet() - blockState[WebTerminalBlock.FACING])
        this.mainNode.setVisualRepresentation(MEWebTerminal.asStack())
        this.mainNode.setIdlePowerUsage(3.0)
        this.mainNode.setFlags(GridFlags.REQUIRE_CHANNEL)
    }

    override fun getGridConnectableSides(orientation: BlockOrientation): Set<Direction> {
        return Direction.entries.toMutableSet() - (level?.getBlockState(worldPosition)
            ?: blockState)[WebTerminalBlock.FACING]
    }

    private fun updateExposedSides() {
        this.mainNode.setExposedOnSides(Direction.entries.toMutableSet() - level!!.getBlockState(worldPosition)[WebTerminalBlock.FACING])
    }

    override fun saveAdditional(data: CompoundTag) {
        super.saveAdditional(data)
        data.putString("Name", displayName)
        data.putUUID("UUID", id)
        data.putString("Password", password)
    }

    override fun loadTag(data: CompoundTag) {
        super.loadTag(data)
        this.displayName = if (data.contains("Name")) data.getString("Name") else "ME Web Terminal"
        this.id = if (data.hasUUID("UUID")) data.getUUID("UUID") else UUID.randomUUID()
        this.password = if (data.contains("Password")) data.getString("Password") else "AppliedWebTerminal"
    }

    override fun onReady() {
        super.onReady()
        updateState()
    }

    override fun onMainNodeStateChanged(reason: IGridNodeListener.State) {
        updateState()
    }

    private fun updateState() {
        level!!.setBlock(
            this.worldPosition,
            level!!.getBlockState(blockPos).setValue(WebTerminalBlock.ONLINE, mainNode.isOnline),
            Block.UPDATE_CLIENTS
        )
        if (!registered && mainNode.isOnline) {
            AENetworkSupport.register(this)
            registered = true
        }
        if (registered && !mainNode.isOnline) {
            AENetworkSupport.remove(this)
            registered = false
        }

    }

    override fun onOrientationChanged(orientation: BlockOrientation) {
        super.onOrientationChanged(orientation)
        updateExposedSides()
    }

    override fun setRemoved() {
        super.setRemoved()
        if (this.level is ServerLevel) {
            AENetworkSupport.remove(this)
        }
    }

    override fun markDirty() {
        setChanged()
    }

    override fun getGrid(): IGrid? {
        return this.mainNode.grid
    }

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

    override fun validateNonce(nonce: String): Boolean {
        return nonce == this.nonce
    }

    override fun getNonce(): String {
        return nonce
    }

    override fun getTerminalName(): String {
        return displayName
    }

    override fun level(): Level? {
        return getLevel()
    }

    override fun getId(): UUID = id

    override fun worldPosition(): BlockPos = worldPosition
}