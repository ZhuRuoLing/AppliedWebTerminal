package icu.takeneko.appwebterminal.block.entity

import appeng.api.inventories.InternalInventory
import appeng.api.networking.IGrid
import appeng.api.networking.IGridNodeListener
import appeng.api.networking.pathing.ControllerState
import appeng.api.orientation.BlockOrientation
import appeng.blockentity.ServerTickingBlockEntity
import appeng.blockentity.grid.AENetworkPowerBlockEntity
import icu.takeneko.appwebterminal.all.meWebTerminal
import icu.takeneko.appwebterminal.block.WebTerminalBlock
import icu.takeneko.appwebterminal.support.AENetworkAccess
import icu.takeneko.appwebterminal.support.AENetworkSupport
import icu.takeneko.appwebterminal.util.get
import io.ktor.util.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class WebTerminalBlockEntity : AENetworkPowerBlockEntity, ServerTickingBlockEntity, AENetworkAccess {

    private var id: UUID = UUID.randomUUID()
    var displayName: String = "ME Web Terminal"
        private set
    var password: String = "AppliedWebTerminal"
        private set

    private var nonce = generateNonce()

    constructor(
        blockEntityType: BlockEntityType<*>,
        blockPos: BlockPos,
        blockState: BlockState
    ) : super(blockEntityType, blockPos, blockState) {
        this.mainNode.setExposedOnSides(Direction.entries.toMutableSet() - blockState[WebTerminalBlock.FACING])
        this.mainNode.setVisualRepresentation(meWebTerminal.asStack())
        this.mainNode.setIdlePowerUsage(3.0)
        AENetworkSupport.register(this)
    }

    override fun getGridConnectableSides(orientation: BlockOrientation): Set<Direction> {
        return Direction.entries.toMutableSet() - (level?.getBlockState(worldPosition)
            ?: blockState)[WebTerminalBlock.FACING]
    }

    private fun updateExposedSides() {
        this.mainNode.setExposedOnSides(Direction.entries.toMutableSet() - level!!.getBlockState(worldPosition)[WebTerminalBlock.FACING])
    }

    override fun onChangeInventory(p0: InternalInventory, p1: Int) {
    }

    override fun saveAdditional(data: CompoundTag) {
        super.saveAdditional(data)
        data.putString("Name", displayName)
        data.putUUID("UUID", id)
        data.putString("Password", password)
    }

    override fun loadTag(data: CompoundTag) {
        super.loadTag(data)
        AENetworkSupport.remove(this)
        this.displayName = if (data.contains("Name")) data.getString("Name") else "ME Web Terminal"
        this.id = if (data.hasUUID("UUID")) data.getUUID("UUID") else UUID.randomUUID()
        this.password = if (data.contains("Password")) data.getString("Password") else "AppliedWebTerminal"
        AENetworkSupport.register(this)
    }

    override fun onReady() {
        super.onReady()
        updateState()
    }

    override fun onMainNodeStateChanged(reason: IGridNodeListener.State) {
        updateState()
    }

    private fun updateState() {
        if (mainNode.isReady) {
            val grid = this.mainNode.grid
            val online = if (grid != null) {
                grid.energyService.isNetworkPowered && grid.pathingService.controllerState != ControllerState.CONTROLLER_CONFLICT
            } else {
                false
            }
            level!!.setBlock(
                this.worldPosition,
                level!!.getBlockState(blockPos).setValue(WebTerminalBlock.ONLINE, online),
                Block.UPDATE_CLIENTS
            )
        }
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        AENetworkSupport.remove(this)
    }

    override fun onOrientationChanged(orientation: BlockOrientation) {
        super.onOrientationChanged(orientation)
        updateExposedSides()
    }

    override fun getInternalInventory(): InternalInventory = InternalInventory.empty()

    override fun serverTick() {
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

    override fun update(displayName: String, password: String) {
        this.displayName = displayName
        val oldPassword = this.password
        this.password = password
        this.nonce = generateNonce()
        if (oldPassword != password){
            AENetworkSupport.requestSessionReset(this)
        }
    }

    override fun validateNonce(nonce: String): Boolean {
        return nonce == this.nonce
    }

    override fun getNonce(): String {
        return nonce
    }

    override fun getId(): UUID = id

    override fun worldPosition(): BlockPos = worldPosition
}