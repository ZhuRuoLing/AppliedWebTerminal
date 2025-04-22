package icu.takeneko.appwebterminal.util

object ServerI18nSupport {
    private const val DEFAULT_LANGUAGE = "en_us"
    private val languages = mutableMapOf<String, LanguageInstance>()

    init {
        languages[DEFAULT_LANGUAGE] = LanguageInstance(DEFAULT_LANGUAGE)
    }

    private fun getInstance(language: String): LanguageInstance {
        if (language in languages) return languages[language]!!
        return LanguageInstance(language).also { languages[language] = it }
    }

    fun contains(language: String,key: String): Boolean {
        return key in getInstance(language)
    }

    fun get(language: String, key: String): String? {
        return getInstance(language).get(key)
    }
}