package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeStatementsData.toStatementsWithNullIds
import xelagurd.socialdating.server.FakeStatementsData.toUserStatementsWithNullIds
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.repository.UserStatementsRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        statementsRepository: StatementsRepository,
        userStatementsRepository: UserStatementsRepository
    ) = CommandLineRunner {
        val existingStatements = statementsRepository.findAll().toList()

        if (existingStatements.isEmpty()) {
            statementsRepository.deleteAllInBatch()
            statementsRepository.saveAll(FakeStatementsData.statements.toStatementsWithNullIds())
            userStatementsRepository.saveAll(FakeStatementsData.userStatements.toUserStatementsWithNullIds())
        }
    }
}