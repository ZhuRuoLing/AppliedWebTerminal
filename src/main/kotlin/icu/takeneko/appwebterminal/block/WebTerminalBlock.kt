package icu.takeneko.appwebterminal.block

import appeng.api.orientation.IOrientationStrategy
import appeng.api.orientation.OrientationStrategies
import appeng.block.AEBaseEntityBlock
import icu.takeneko.appwebterminal.all.meWebTerminalBlockEntity
import icu.takeneko.appwebterminal.all.networkingChannel
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.networking.OpenWebTerminalScreenPacket
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.PacketDistributor

class WebTerminalBlock(properties: Properties) : AEBaseEntityBlock<WebTerminalBlockEntity>(properties),
    LateInitSupported {
    init {
        registerDefaultState(stateDefinition.any().setValue(ONLINE, false).setValue(FACING, Direction.UP))
    }

    override fun lateInit() {
        setBlockEntity(
            WebTerminalBlockEntity::class.java,
            meWebTerminalBlockEntity.get(),
            null,
            null
        )
    }

    override fun getOrientationStrategy(): IOrientationStrategy {
        return OrientationStrategies.facing()
    }

    override fun onActivated(
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        heldItem: ItemStack?,
        hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val be = level.getBlockEntity(pos) as WebTerminalBlockEntity
        networkingChannel.send(
            PacketDistributor.PLAYER.with { player as ServerPlayer },
            OpenWebTerminalScreenPacket(be.displayName, be.getId(), pos, be.password, be.mainNode.isOnline)
        )
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(ONLINE, FACING)
    }

    companion object {
        val ONLINE: BooleanProperty = BooleanProperty.create("online")
        val FACING: DirectionProperty = BlockStateProperties.FACING
    }
}