package icu.takeneko.appwebterminal.util

import com.google.common.collect.HashBasedTable
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

object KubejsI18nSupport {
    private const val DEFAULT_LANGUAGE = "en_us"
    private val languages =  HashBasedTable.create<String, String, String>()

    private val Json = Json {
        ignoreUnknownKeys = true
    }

    fun init() {
        val basePath = Path("kubejs", "assets")
        if (!basePath.exists()) return@init
        basePath.listDirectoryEntries().forEach {
            val langDir = it / "lang"
            if (!langDir.exists()) return@init
            langDir.listDirectoryEntries().forEach { langFile ->
                val langName = langFile.nameWithoutExtension
                val map = Json.decodeFromString<Map<String, String>>(langFile.toFile().readText(Charsets.UTF_8))
                map.forEach { k, v ->
                    languages.put(langName, k, v)
                }
            }
        }
    }

    fun get(language: String, key: String): String {
        return languages.get(language, key) ?: ""
    }
}