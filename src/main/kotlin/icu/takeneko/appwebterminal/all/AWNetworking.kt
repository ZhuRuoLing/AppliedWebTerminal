package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.networking.OpenWebTerminalScreenPacket
import icu.takeneko.appwebterminal.networking.UpdateWebTerminalNamePacket
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object AWNetworking {
    val registrar = PayloadRegistrar("1")
        .playToClient(
            OpenWebTerminalScreenPacket.type,
            OpenWebTerminalScreenPacket.streamCodec,
            OpenWebTerminalScreenPacket::accept
        ).playToServer(
            UpdateWebTerminalNamePacket.type,
            UpdateWebTerminalNamePacket.streamCodec,
            UpdateWebTerminalNamePacket::accept
        )

    fun registerNetworking() = Unit
}