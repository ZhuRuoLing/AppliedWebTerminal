package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.client.gui.WebTerminalScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

data class OpenWebTerminalScreenPacket(
    val initialName: String,
    val uuid: UUID,
    val blockPos: BlockPos
) {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readUUID(), buf.readBlockPos())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(initialName)
        buf.writeUUID(uuid)
        buf.writeBlockPos(blockPos)
    }

    fun accept(context: Supplier<NetworkEvent.Context>) {
        context.get().enqueueWork {
            Minecraft.getInstance().setScreen(WebTerminalScreen(initialName, uuid))
        }
        context.get().packetHandled = true
    }
}