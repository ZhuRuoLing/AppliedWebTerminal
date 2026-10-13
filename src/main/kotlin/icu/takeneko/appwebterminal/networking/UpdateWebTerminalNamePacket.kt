package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.support.AENetworkSupport
import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

data class UpdateWebTerminalNamePacket(
    val name: String,
    val uuid: UUID,
    val password: String
): CustomPacketPayload {

    fun accept(context: IPayloadContext) {
        AENetworkSupport.update(uuid, name, password)
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = type

    companion object {
        val id = AppWebTerminal.location("update_name")
        val type = CustomPacketPayload.Type<UpdateWebTerminalNamePacket>(id)

        val streamCodec: StreamCodec<in FriendlyByteBuf, UpdateWebTerminalNamePacket> =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                UpdateWebTerminalNamePacket::name,
                UUIDUtil.STREAM_CODEC,
                UpdateWebTerminalNamePacket::uuid,
                ByteBufCodecs.STRING_UTF8,
                UpdateWebTerminalNamePacket::password,
                ::UpdateWebTerminalNamePacket
            )
    }
}