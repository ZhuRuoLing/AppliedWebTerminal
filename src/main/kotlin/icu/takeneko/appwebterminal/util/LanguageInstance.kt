package icu.takeneko.appwebterminal.util

import com.mojang.logging.LogUtils
import kotlinx.serialization.json.Json
import net.minecraftforge.fml.ModList
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText

private val Json = Json {
    ignoreUnknownKeys = true
}

private val logger = LogUtils.getLogger()

class LanguageInstance(
    val language: String
) {
    private val translations = mutableMapOf<String, String>()

    init {
        ModList.get().modFiles
            .mapNotNull {
                it.file.findResource("assets")?.listDirectoryEntries()
            }.flatMap {
                it.map { it1 -> it1.resolve("lang").resolve("$language.json") }.filter { it1 -> it1.exists() }
            }.mapNotNull {
                try {
                    icu.takeneko.appwebterminal.util.Json.decodeFromString<Map<String, String>>(it.readText())
                } catch (e: Exception) {
                    logger.error("Error while decoding language", e)
                    null
                }
            }.flatMap {
                it.entries
            }.forEach { (k, v) ->
                translations[k] = v
            }
    }

    operator fun contains(key: String) = key in translations

    fun getOrDefault(key: String, default: String = key) = translations[key] ?: default

    fun get(key: String) = translations[key]
}