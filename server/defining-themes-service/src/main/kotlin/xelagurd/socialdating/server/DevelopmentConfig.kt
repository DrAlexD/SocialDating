package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeDefiningThemesData.toDefiningThemesWithNullIds
import xelagurd.socialdating.server.FakeDefiningThemesData.toUserDefiningThemesWithNullIds
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        definingThemesRepository: DefiningThemesRepository,
        userDefiningThemesRepository: UserDefiningThemesRepository
    ) = CommandLineRunner {
        val existingDefiningThemes = definingThemesRepository.findAll().toList()

        if (existingDefiningThemes.isEmpty()) {
            definingThemesRepository.saveAll(FakeDefiningThemesData.definingThemes.toDefiningThemesWithNullIds())
            userDefiningThemesRepository.saveAll(FakeDefiningThemesData.userDefiningThemes.toUserDefiningThemesWithNullIds())
        }
    }
}