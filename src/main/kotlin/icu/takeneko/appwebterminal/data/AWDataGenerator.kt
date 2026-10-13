package icu.takeneko.appwebterminal.data

import com.tterrag.registrate.providers.ProviderType
import com.tterrag.registrate.providers.RegistrateLangProvider
import icu.takeneko.appwebterminal.registrate

object AWDataGenerator {
    fun configureDataGeneration() {
        registrate.addDataGenerator(ProviderType.LANG, ::handleLang)
    }

    fun handleLang(langProvider: RegistrateLangProvider) {
        langProvider.add("appwebterminal.screen.title", "ME Web Terminal")
        langProvider.add("appwebterminal.button.done", "Done")
        langProvider.add("appwebterminal.hint.name", "Name: ")
        langProvider.add("appwebterminal.hint.password", "Password: ")

        langProvider.add("appwebterminal.message.render_complete", "Render complete.")
        langProvider.add("appwebterminal.message.rendering", "A renderer is currently working.")
        langProvider.add("appwebterminal.message.started", "Started renderer.")

        langProvider.add("appwebterminal.gui.me_network_online", "Online")
        langProvider.add("appwebterminal.gui.me_network_offline", "Offline")
    }
}
