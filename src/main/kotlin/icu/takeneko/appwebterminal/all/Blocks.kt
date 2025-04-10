package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.block.WebTerminalBlock
import icu.takeneko.appwebterminal.registrate
import icu.takeneko.appwebterminal.util.get
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile

val meWebTerminal = registrate.block<WebTerminalBlock>("web_terminal", ::WebTerminalBlock)
    .blockstate { dataGenContext, registrateBlockstateProvider ->
        registrateBlockstateProvider.directionalBlock(dataGenContext.get()) {
            UncheckedModelFile(
                AppWebTerminal.location(
                    if (it[WebTerminalBlock.ONLINE])
                        "block/web_terminal"
                    else
                        "block/web_terminal_offline"
                )
            )
        }
    }
    .lang("ME Web Terminal")
    .item()
    .model { dataGenContext, registrateItemModelProvider ->
        registrateItemModelProvider.getBuilder(dataGenContext.name)
            .parent(UncheckedModelFile(AppWebTerminal.location("block/web_terminal")))
    }
    .build()
    .register()


fun registerBlocks() {
    //intentionally empty
}