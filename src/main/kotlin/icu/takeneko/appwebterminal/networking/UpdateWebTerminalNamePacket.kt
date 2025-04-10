package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.support.AENetworkSupport
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

data class UpdateWebTerminalNamePacket(
    val name: String,
    val uuid: UUID
) {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readUUID())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(name)
        buf.writeUUID(uuid)
    }

    fun accept(context: Supplier<NetworkEvent.Context>) {
        context.get().enqueueWork {
            AENetworkSupport.rename(uuid, name)
        }
        context.get().packetHandled = true
    }
}