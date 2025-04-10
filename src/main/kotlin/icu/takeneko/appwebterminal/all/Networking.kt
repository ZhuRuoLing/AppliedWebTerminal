package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.networking.OpenWebTerminalScreenPacket
import icu.takeneko.appwebterminal.networking.UpdateWebTerminalNamePacket
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

private const val version = "1"

val networkingChannel: SimpleChannel = NetworkRegistry.newSimpleChannel(
    AppWebTerminal.location("networking"),
    { version },
    { it == version },
    { it == version }
)

private var id = 0

fun registerNetworking() {
    networkingChannel.registerMessage(
        id++,
        OpenWebTerminalScreenPacket::class.java,
        OpenWebTerminalScreenPacket::encode,
        ::OpenWebTerminalScreenPacket,
        OpenWebTerminalScreenPacket::accept
    )

    networkingChannel.registerMessage(
        id++,
        UpdateWebTerminalNamePacket::class.java,
        UpdateWebTerminalNamePacket::encode,
        ::UpdateWebTerminalNamePacket,
        UpdateWebTerminalNamePacket::accept
    )
}