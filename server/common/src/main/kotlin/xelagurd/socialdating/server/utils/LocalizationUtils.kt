package xelagurd.socialdating.server.utils

import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.model.enums.AppLanguage.ENGLISH
import xelagurd.socialdating.server.model.enums.AppLanguage.RUSSIAN

object LocalizationUtils {

    fun localize(english: String?, russian: String?, language: AppLanguage): String =
        when (language) {
            ENGLISH -> english ?: russian
            RUSSIAN -> russian ?: english
        } ?: throw IllegalStateException("Localized text is missing for both languages")

    fun englishOrNull(text: String) = text.takeIf { AppLanguage.detect(it) == ENGLISH }

    fun russianOrNull(text: String) = text.takeIf { AppLanguage.detect(it) == RUSSIAN }
}
