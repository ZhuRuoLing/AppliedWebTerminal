package icu.takeneko.appwebterminal.util

import java.util.IllegalFormatException

object I18nUtil {
    val languageProviders = listOf<(String, String) -> String?>(
        { lang, key -> KubejsI18nSupport.get(lang, key) },
        { lang, key -> MinecraftI18nSupport.get(lang, key) },
        { lang, key -> ServerI18nSupport.get(lang, key) }
    )

    fun get(language: String, key: String): String {
        val content = languageProviders.asSequence()
            .map { it(language, key) }
            .filterNotNull()
            .firstOrNull()
        return content ?: key
    }

    fun translate(language: String, key: String, vararg args: Any?): String {
        val content = get(language, key)
        return if (args.isNotEmpty()) {
            try {
                content.format(*args)
            } catch (e: IllegalFormatException) {
                key
            }
        } else {
            content
        }
    }
}