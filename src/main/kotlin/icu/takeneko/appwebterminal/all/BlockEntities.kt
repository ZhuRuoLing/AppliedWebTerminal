package icu.takeneko.appwebterminal.all

import com.tterrag.registrate.util.entry.BlockEntityEntry
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.registrate

val MEWebTerminalBlockEntity: BlockEntityEntry<WebTerminalBlockEntity> =
    registrate.blockEntity<WebTerminalBlockEntity>("web_terminal", ::WebTerminalBlockEntity)
        .validBlock(MEWebTerminal)
        .register()

fun registerBlockEntities() {

}