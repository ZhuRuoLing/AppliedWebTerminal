package icu.takeneko.appwebterminal.util

import com.google.common.collect.HashBasedTable
import icu.takeneko.appwebterminal.resource.CacheProvider
import icu.takeneko.appwebterminal.resource.LanguageFileDownloader
import kotlinx.serialization.json.Json
import kotlin.io.path.exists
import kotlin.io.path.readText


object MinecraftI18nSupport {
    private const val DEFAULT_LANGUAGE = "en_us"
    private val translations = HashBasedTable.create<String, String, String>()
    private val Json = Json {
        ignoreUnknownKeys = true
    }

    private fun requestLanguage(language: String) {
        if (translations.containsRow(language)) {
            return
        }
        val file = CacheProvider.requireFile(
            LanguageFileDownloader.fileNameMapping["minecraft/lang/$language.json"] ?: return
        )
        if (file.exists()) {
            for ((k, v) in Json.decodeFromString<Map<String, String>>(file.readText())) {
                translations.put(language, k, v)
            }
        }
    }

    fun get(language: String, key: String): String {
        requestLanguage(language)
        return translations.get(language, key) ?: key
    }

    fun translate(language: String, key: String, vararg args: Any?): String {
        val content = get(language, key)
        return try {
            String.format(content, *args)
        } catch (_: IllegalArgumentException) {
            content
        }
    }

    fun contains(language: String, key: String): Boolean {
        requestLanguage(language)
        return translations.contains(language, key)
    }

}