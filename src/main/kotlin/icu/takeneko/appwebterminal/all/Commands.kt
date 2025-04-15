package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.util.LiteralCommand
import icu.takeneko.appwebterminal.util.execute
import icu.takeneko.appwebterminal.util.literal
import icu.takeneko.appwebterminal.util.sendFeedback
import net.minecraft.network.chat.Component
import net.minecraftforge.client.event.RegisterClientCommandsEvent

val AppWebTerminalCommand = LiteralCommand("appwebterminal") {
    literal("renderResources") {
        literal("immediate") {

        }
        literal("saved") {

        }
        literal("listRegistered") {
            execute {
                KeyImageProviderRegistry.keys.forEach {
                    sendFeedback(Component.literal(it.toString()))
                }
                return@execute 1
            }
        }
    }
}

fun registerClientCommand(event: RegisterClientCommandsEvent) {
    event.dispatcher.register(AppWebTerminalCommand.node)
}