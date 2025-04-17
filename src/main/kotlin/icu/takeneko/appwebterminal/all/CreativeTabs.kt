package icu.takeneko.appwebterminal.all

import icu.takeneko.appwebterminal.registrate

val appWebCreativeTab = registrate
    .defaultCreativeTab("applied_web_terminal") {
        it.icon(MEWebTerminal::asStack)
        it.displayItems { parameters, output ->
            output.accept(MEWebTerminal)
            output.accept(MEWebTerminalPartItem)
        }
    }
    .register()

fun registerCreativeTab() {

}