package xelagurd.socialdating.server

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListenerConfigurer
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class DefiningThemesKafkaValidationConfig(
    private val validator: LocalValidatorFactoryBean
) : KafkaListenerConfigurer {

    override fun configureKafkaListeners(registrar: KafkaListenerEndpointRegistrar) {
        registrar.setValidator(validator)
    }
}
