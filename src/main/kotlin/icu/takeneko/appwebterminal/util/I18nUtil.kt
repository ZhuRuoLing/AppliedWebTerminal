package icu.takeneko.appwebterminal.util

object I18nUtil {
    fun translate(language: String, key: String, vararg args: Any?): String {
        val minecraftI18n = MinecraftI18nSupport.translate(language, key, *args)
        return if (minecraftI18n == key) {
            ServerI18nSupport.translate(language, key, *args)
        } else {
            minecraftI18n
        }
    }
}