package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.model.DefiningTheme
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
                val definingThemes = listOf(
                    DefiningTheme(
                        name = "RemoteDefiningTheme1",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 1
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme2",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 1
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme3",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 1
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme4",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 2
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme5",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 3
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme6",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 5
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme7",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 1
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme8",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 2
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme9",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 3
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme10",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 4
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme11",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 1
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme12",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 2
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme13",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 6
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme14",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 7
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme15",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 2
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme16",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 3
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme17",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 5
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme18",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 9
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme19",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 10
                    ),
                    DefiningTheme(
                        name = "RemoteDefiningTheme20",
                        fromOpinion = "No",
                        toOpinion = "Yes",
                        categoryId = 8
                    ),
                )

                definingThemesRepository.saveAll(definingThemes)
            }
        }
    }
}