package icu.takeneko.appwebterminal.networking

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.client.gui.WebTerminalScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

data class OpenWebTerminalScreenPacket(
    val initialName: String,
    val uuid: UUID,
    val blockPos: BlockPos,
    val password: String,
    val isOnline: Boolean
) : CustomPacketPayload {

    fun accept(context: IPayloadContext) {
        Minecraft.getInstance().setScreen(WebTerminalScreen(initialName, uuid, password, isOnline))
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = type

    companion object {
        val id = AppWebTerminal.location("open_screen")
        val type = CustomPacketPayload.Type<OpenWebTerminalScreenPacket>(id)

        val streamCodec: StreamCodec<in FriendlyByteBuf, OpenWebTerminalScreenPacket> =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                OpenWebTerminalScreenPacket::initialName,
                UUIDUtil.STREAM_CODEC,
                OpenWebTerminalScreenPacket::uuid,
                BlockPos.STREAM_CODEC,
                OpenWebTerminalScreenPacket::blockPos,
                ByteBufCodecs.STRING_UTF8,
                OpenWebTerminalScreenPacket::password,
                ByteBufCodecs.BOOL,
                OpenWebTerminalScreenPacket::isOnline,
                ::OpenWebTerminalScreenPacket
            )
    }
}