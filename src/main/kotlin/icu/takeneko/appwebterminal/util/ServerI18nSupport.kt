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

    fun get(language: String, key: String, allowDefaultTranslation: Boolean): String {
        if (allowDefaultTranslation) {
            val default = getInstance(DEFAULT_LANGUAGE).getOrDefault(key)
            if (language == DEFAULT_LANGUAGE) {
                return default
            }
            return getInstance(language).getOrDefault(key, default)
        }
        return getInstance(language).getOrDefault(key, key)
    }

    fun get(language: String, key: String): String {
        val default = getInstance(DEFAULT_LANGUAGE).getOrDefault(key)
        if (language == DEFAULT_LANGUAGE) {
            return default
        }
        return getInstance(language).getOrDefault(key, default)
    }

    fun translate(language: String, key: String, vararg args: Any?): String {
        val content = get(language, key)
        return try {
            String.format(content, *args)
        } catch (_: IllegalArgumentException) {
            content
        }
    }
}