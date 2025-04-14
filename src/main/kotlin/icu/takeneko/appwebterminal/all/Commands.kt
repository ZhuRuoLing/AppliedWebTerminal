package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.util.LiteralCommand
import icu.takeneko.appwebterminal.util.literal
import net.minecraftforge.client.ClientCommandHandler

val AppWebTerminalCommand = LiteralCommand("appwebterminal") {
    literal("render") {

    }
}

fun register() {
    ClientCommandHandler.getDispatcher().register(AppWebTerminalCommand.node)
}