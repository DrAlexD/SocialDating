package xelagurd.socialdating.server.utils

import org.springframework.context.support.ResourceBundleMessageSource
import xelagurd.socialdating.server.model.enums.AppLanguage

class LocalizedMessages {

    // the bundle is built here instead of relying on the autoconfigured MessageSource, so that the resolution
    // never falls back to the system locale and stays the same in tests and in production
    private val messageSource = ResourceBundleMessageSource().apply {
        setBasename(BASENAME)
        setDefaultEncoding(Charsets.UTF_8.name())
        setFallbackToSystemLocale(false)
    }

    fun get(key: String, vararg args: Any): String =
        messageSource.getMessage(key, args, AppLanguage.current().locale)

    private companion object {
        const val BASENAME = "Messages"
    }
}
