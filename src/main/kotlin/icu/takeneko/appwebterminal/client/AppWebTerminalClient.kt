package icu.takeneko.appwebterminal.client

import icu.takeneko.appwebterminal.all.registerClientCommand
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

object AppWebTerminalClient {
    init {
        FORGE_BUS.addListener(::registerClientCommand)
    }
}