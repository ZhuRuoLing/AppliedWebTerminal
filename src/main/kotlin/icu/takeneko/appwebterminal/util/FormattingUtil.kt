package icu.takeneko.appwebterminal.util

import com.google.common.base.CaseFormat
import java.util.*

fun String.toEnglishName(): String {
    return this.lowercase(Locale.ROOT).split("_")
        .joinToString(separator = " ") { a ->
            a.replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(Locale.getDefault())
                } else {
                    it.toString()
                }
            }
        }
}

fun String.toLowerCaseUnder(): String {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)
}