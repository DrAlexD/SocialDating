package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.repository.StatementsRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        statementsRepository: StatementsRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val existingStatements = statementsRepository.findAll().toList()

            if (existingStatements.isEmpty()) {
                val statements = listOf(
                    Statement(
                        text = "RemoteStatement1",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement2",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement3",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement4",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement5",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement6",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement7",
                        isSupportDefiningTheme = true,
                        definingThemeId = 1,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement8",
                        isSupportDefiningTheme = true,
                        definingThemeId = 2,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement9",
                        isSupportDefiningTheme = true,
                        definingThemeId = 2,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement10",
                        isSupportDefiningTheme = true,
                        definingThemeId = 2,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement11",
                        isSupportDefiningTheme = true,
                        definingThemeId = 2,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement12",
                        isSupportDefiningTheme = true,
                        definingThemeId = 3,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement13",
                        isSupportDefiningTheme = true,
                        definingThemeId = 3,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement14",
                        isSupportDefiningTheme = true,
                        definingThemeId = 3,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement15",
                        isSupportDefiningTheme = true,
                        definingThemeId = 3,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement16",
                        isSupportDefiningTheme = true,
                        definingThemeId = 4,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement17",
                        isSupportDefiningTheme = true,
                        definingThemeId = 4,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement18",
                        isSupportDefiningTheme = true,
                        definingThemeId = 5,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement19",
                        isSupportDefiningTheme = true,
                        definingThemeId = 5,
                        creatorUserId = 1
                    ),
                    Statement(
                        text = "RemoteStatement20",
                        isSupportDefiningTheme = true,
                        definingThemeId = 5,
                        creatorUserId = 1
                    ),
                )

                statementsRepository.saveAll(statements)
            }
        }
    }
}