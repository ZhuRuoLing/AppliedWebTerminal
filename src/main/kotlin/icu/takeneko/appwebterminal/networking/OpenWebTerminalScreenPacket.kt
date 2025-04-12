package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.client.gui.WebTerminalScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.UUID
import java.util.function.Supplier

data class OpenWebTerminalScreenPacket(
    val initialName: String,
    val uuid: UUID,
    val blockPos: BlockPos,
    val password: String,
    val isOnline: Boolean
) {
    constructor(buf: FriendlyByteBuf) : this(
        buf.readUtf(),
        buf.readUUID(),
        buf.readBlockPos(),
        buf.readUtf(),
        buf.readBoolean()
    )

    fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(initialName)
        buf.writeUUID(uuid)
        buf.writeBlockPos(blockPos)
        buf.writeUtf(password)
        buf.writeBoolean(isOnline)
    }

    fun accept(context: Supplier<NetworkEvent.Context>) {
        context.get().enqueueWork {
            Minecraft.getInstance().setScreen(WebTerminalScreen(initialName, uuid, password, isOnline))
        }
        context.get().packetHandled = true
    }
}