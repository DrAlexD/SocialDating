package xelagurd.socialdating.server.validation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class ValidationConfig {

    // the messages are resolved by the bundles instead of the default interpolation, so that the resolution never
    // falls back to the system locale and stays the same in tests and in production, the bundle of the validation
    // provider is included too, otherwise its own messages would still fall back
    @Bean
    fun defaultValidator() =
        LocalValidatorFactoryBean().apply {
            setValidationMessageSource(
                ResourceBundleMessageSource().apply {
                    setBasenames(BASENAME, PROVIDER_BASENAME)
                    setDefaultEncoding(Charsets.UTF_8.name())
                    setFallbackToSystemLocale(false)
                }
            )
        }

    private companion object {
        const val BASENAME = "ValidationMessages"
        const val PROVIDER_BASENAME = "org.hibernate.validator.ValidationMessages"
    }
}
