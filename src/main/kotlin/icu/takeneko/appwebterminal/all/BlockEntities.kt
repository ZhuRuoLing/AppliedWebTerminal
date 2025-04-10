package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.registrate

val meWebTerminalBlockEntity =
    registrate.blockEntity<WebTerminalBlockEntity>("web_terminal", ::WebTerminalBlockEntity)
        .validBlock(meWebTerminal)
        .register()

fun registerBlockEntities() {

}