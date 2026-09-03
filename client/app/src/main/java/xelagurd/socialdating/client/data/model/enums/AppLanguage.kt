package xelagurd.socialdating.client.data.model.enums

import androidx.annotation.StringRes
import xelagurd.socialdating.client.R

enum class AppLanguage(
    val languageTag: String,
    @param:StringRes override val descriptionRes: Int
) : DescribedEnum {
    SYSTEM("", R.string.language_system),
    ENGLISH("en", R.string.language_english),
    RUSSIAN("ru", R.string.language_russian);

    companion object {
        fun fromLanguageTag(languageTag: String?) =
            entries.firstOrNull { it.languageTag == languageTag } ?: SYSTEM
    }
}
