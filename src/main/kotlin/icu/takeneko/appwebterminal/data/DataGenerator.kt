package icu.takeneko.appwebterminal.data

import com.tterrag.registrate.providers.ProviderType
import com.tterrag.registrate.providers.RegistrateLangProvider
import icu.takeneko.appwebterminal.registrate

fun configureDataGeneration() {
    registrate.addDataGenerator(ProviderType.LANG, ::handleLang)
}

fun handleLang(langProvider: RegistrateLangProvider){
    langProvider.add("appwebterminal.screen.title", "ME Web Terminal")
    langProvider.add("appwebterminal.button.done", "Done")
    langProvider.add("appwebterminal.hint.name", "Name: ")
}