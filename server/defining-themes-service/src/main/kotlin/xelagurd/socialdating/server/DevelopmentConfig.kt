package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeDefiningThemesData.withNullIds
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        definingThemesRepository: DefiningThemesRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val existingDefiningThemes = definingThemesRepository.findAll().toList()

            if (existingDefiningThemes.isEmpty()) {
                definingThemesRepository.saveAll(FakeDefiningThemesData.definingThemes.withNullIds())
            }
        }
    }
}