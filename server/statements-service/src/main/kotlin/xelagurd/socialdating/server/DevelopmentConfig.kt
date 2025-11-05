package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeStatementsData.withNullIds
import xelagurd.socialdating.server.repository.StatementsRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        statementsRepository: StatementsRepository
    ) = CommandLineRunner {
        val existingStatements = statementsRepository.findAll().toList()

        if (existingStatements.isEmpty()) {
            statementsRepository.deleteAllInBatch()
            statementsRepository.saveAll(FakeStatementsData.statements.withNullIds())
        }
    }
}