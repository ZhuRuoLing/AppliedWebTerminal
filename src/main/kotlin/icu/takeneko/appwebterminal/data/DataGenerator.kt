package icu.takeneko.appwebterminal.data

import com.tterrag.registrate.providers.ProviderType
import com.tterrag.registrate.providers.RegistrateLangProvider
import dev.toma.configuration.config.value.ConfigValue
import dev.toma.configuration.config.value.ObjectValue
import icu.takeneko.appwebterminal.AppWebTerminal
import icu.takeneko.appwebterminal.registrate

fun configureDataGeneration() {
    registrate.addDataGenerator(ProviderType.LANG, ::handleLang)
}

fun handleLang(langProvider: RegistrateLangProvider){
    langProvider.add("appwebterminal.screen.title", "ME Web Terminal")
    langProvider.add("appwebterminal.button.done", "Done")
    langProvider.add("appwebterminal.hint.name", "Name: ")
    langProvider.add("appwebterminal.hint.password", "Password: ")
    dfs(langProvider, mutableSetOf(), AppWebTerminal.configHolder.valueMap)
}

private fun dfs(provider: RegistrateLangProvider, added: MutableSet<String>, map: Map<String, ConfigValue<*>>) {
    for ((_, value) in map) {
        val id = value.id
        if (added.add(id)) {
            provider.add("config.${AppWebTerminal.MOD_ID}.option.$id", id)
        }
        if (value is ObjectValue) {
            dfs(provider, added, value.get())
        }
    }
}