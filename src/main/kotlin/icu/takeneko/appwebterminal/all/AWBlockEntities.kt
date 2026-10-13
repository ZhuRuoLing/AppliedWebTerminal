package icu.takeneko.appwebterminal.all

import com.tterrag.registrate.util.entry.BlockEntityEntry
import icu.takeneko.appwebterminal.block.entity.WebTerminalBlockEntity
import icu.takeneko.appwebterminal.registrate

object AWBlockEntities {
    val webTerminal: BlockEntityEntry<WebTerminalBlockEntity> =
        registrate.blockEntity("web_terminal", ::WebTerminalBlockEntity)
            .validBlock(AWBlocks.webTerminal)
            .register()

    fun registerBlockEntities() {
        //intentionally empty
    }
}



