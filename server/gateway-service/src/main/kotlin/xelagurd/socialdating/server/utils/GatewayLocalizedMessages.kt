package xelagurd.socialdating.server.utils

import java.util.Locale
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpHeaders

// the gateway does not depend on the common module, so it carries its own small bundle, and the language is taken
// from the request headers, because the reactive stack does not fill LocaleContextHolder
class GatewayLocalizedMessages {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasename(BASENAME)
        setDefaultEncoding(Charsets.UTF_8.name())
        setFallbackToSystemLocale(false)
    }

    fun get(key: String, headers: HttpHeaders): String =
        messageSource.getMessage(key, emptyArray(), resolveLocale(headers))

    fun getOrNull(key: String, headers: HttpHeaders): String? =
        messageSource.getMessage(key, emptyArray(), null, resolveLocale(headers))

    private fun resolveLocale(headers: HttpHeaders): Locale {
        val language = runCatching { headers.acceptLanguageAsLocales.firstOrNull()?.language }.getOrNull()

        return if (language == RUSSIAN.language) RUSSIAN else ENGLISH
    }

    private companion object {
        const val BASENAME = "GatewayMessages"

        val ENGLISH: Locale = Locale.forLanguageTag("en")
        val RUSSIAN: Locale = Locale.forLanguageTag("ru")
    }
}
