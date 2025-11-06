package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_HIGH_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_LOW_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP

@Configuration
class StartupValidationConfig {

    @Bean
    @Order(0)
    fun startupValidator() = CommandLineRunner {
        if (DEFINING_THEME_VALUE_INITIAL - DEFINING_THEME_VALUE_LOW_BORDER != DEFINING_THEME_VALUE_HIGH_BORDER - DEFINING_THEME_VALUE_INITIAL) {
            throw IllegalStateException(
                "DEFINING_THEME_VALUE_INITIAL, DEFINING_THEME_VALUE_LOW_BORDER," +
                        "DEFINING_THEME_VALUE_HIGH_BORDER are not properly initialized"
            )
        }

        if (DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT >= DEFINING_THEME_VALUE_HIGH_BORDER - DEFINING_THEME_VALUE_INITIAL) {
            throw IllegalStateException(
                "DEFINING_THEME_VALUE_STEP, DEFINING_THEME_VALUE_COEFFICIENT are not properly initialized"
            )
        }
    }
}
