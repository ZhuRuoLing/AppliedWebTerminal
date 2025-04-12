package icu.takeneko.appwebterminal.util

import com.google.common.base.CaseFormat
import org.apache.commons.lang3.StringUtils
import java.util.Locale
import java.util.stream.Collectors

fun String.toEnglishName(): String {
    return this.lowercase(Locale.ROOT).split("_").stream()
        .map { StringUtils.capitalize(it) }
        .collect(Collectors.joining(" "))
}

fun String.toLowerCaseUnder(): String {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)
}