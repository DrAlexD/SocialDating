package xelagurd.socialdating.server.model.enums

import java.util.Locale
import org.springframework.context.i18n.LocaleContextHolder

enum class AppLanguage(val languageTag: String) {
    ENGLISH("en"),
    RUSSIAN("ru");

    val locale: Locale = Locale.forLanguageTag(languageTag)

    companion object {
        fun current() = fromLanguageTag(LocaleContextHolder.getLocaleContext()?.locale?.language)

        fun fromLanguageTag(languageTag: String?) =
            entries.firstOrNull { it.languageTag == languageTag } ?: ENGLISH

        fun detect(text: String) =
            if (text.any { it in 'а'..'я' || it in 'А'..'Я' || it == 'ё' || it == 'Ё' }) RUSSIAN else ENGLISH
    }
}
