package xelagurd.socialdating

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.repository.StatementsRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        statementsRepository: StatementsRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val statements = listOf(
                Statement(text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement4", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement5", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement6", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement7", isSupportDefiningTheme = true, definingThemeId = 1, userId = 1),
                Statement(text = "RemoteStatement8", isSupportDefiningTheme = true, definingThemeId = 2, userId = 1),
                Statement(text = "RemoteStatement9", isSupportDefiningTheme = true, definingThemeId = 2, userId = 1),
                Statement(text = "RemoteStatement10", isSupportDefiningTheme = true, definingThemeId = 2, userId = 1),
                Statement(text = "RemoteStatement11", isSupportDefiningTheme = true, definingThemeId = 2, userId = 1),
                Statement(text = "RemoteStatement12", isSupportDefiningTheme = true, definingThemeId = 3, userId = 1),
                Statement(text = "RemoteStatement13", isSupportDefiningTheme = true, definingThemeId = 3, userId = 1),
                Statement(text = "RemoteStatement14", isSupportDefiningTheme = true, definingThemeId = 3, userId = 1),
                Statement(text = "RemoteStatement15", isSupportDefiningTheme = true, definingThemeId = 3, userId = 1),
                Statement(text = "RemoteStatement16", isSupportDefiningTheme = true, definingThemeId = 4, userId = 1),
                Statement(text = "RemoteStatement17", isSupportDefiningTheme = true, definingThemeId = 4, userId = 1),
                Statement(text = "RemoteStatement18", isSupportDefiningTheme = true, definingThemeId = 5, userId = 1),
                Statement(text = "RemoteStatement19", isSupportDefiningTheme = true, definingThemeId = 5, userId = 1),
                Statement(text = "RemoteStatement20", isSupportDefiningTheme = true, definingThemeId = 5, userId = 1),
            )

            statementsRepository.saveAll(statements)
        }
    }
}