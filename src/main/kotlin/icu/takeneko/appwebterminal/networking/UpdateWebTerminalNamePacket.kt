package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.support.AENetworkSupport
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.UUID
import java.util.function.Supplier

data class UpdateWebTerminalNamePacket(
    val name: String,
    val uuid: UUID,
    val password: String
) {
    constructor(buf: FriendlyByteBuf) : this(buf.readUtf(), buf.readUUID(), buf.readUtf())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(name)
        buf.writeUUID(uuid)
        buf.writeUtf(password)
    }

    fun accept(context: Supplier<NetworkEvent.Context>) {
        context.get().enqueueWork {
            AENetworkSupport.update(uuid, name, password)
        }
        context.get().packetHandled = true
    }
}