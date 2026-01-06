package icu.takeneko.appwebterminal.util

import com.google.common.base.CaseFormat
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentContents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
import java.util.Locale

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

fun Component.toLocalizedString(lang: String): String {
    val contents = this.contents
    var rt = when (contents) {
        is LiteralContents -> {
            contents.text()
        }

        is TranslatableContents -> {

            val key = contents.key
            val args = contents.args.map {
                if (it is Component) {
                   return@map it.toLocalizedString(lang)
                }
                return@map it.toString()
            }
            I18nUtil.translate(lang, key, *args.toTypedArray())
        }

        else -> {
            ""
        }
    }
    this.siblings.forEach {
        rt += it.toLocalizedString(lang)
    }
    return rt
}


fun Component.strip(): MutableComponent {
    val rt: MutableComponent
    if (neededContents(this.contents)) {
        rt = MutableComponent.create(this.contents)
    } else {
        rt = MutableComponent.create(ComponentContents.EMPTY)
    }
    rt.style = this.style.withHoverEvent(null).withClickEvent(null).withInsertion(null)
    this.siblings.forEach { c -> rt.append(c.strip()) }
    return rt
}

private fun neededContents(contents: ComponentContents): Boolean {
    if (contents == ComponentContents.EMPTY) return true
    if (contents is LiteralContents) return true
    if (contents is TranslatableContents) return true
    return false
}
