package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.repository.CategoriesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        categoriesRepository: CategoriesRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val existingCategories = categoriesRepository.findAll().toList()

            if (existingCategories.isEmpty()) {
                val categories = listOf(
                    Category(name = "RemoteCategory1"),
                    Category(name = "RemoteCategory2"),
                    Category(name = "RemoteCategory3"),
                    Category(name = "RemoteCategory4"),
                    Category(name = "RemoteCategory5"),
                    Category(name = "RemoteCategory6"),
                    Category(name = "RemoteCategory7"),
                    Category(name = "RemoteCategory8"),
                    Category(name = "RemoteCategory9"),
                    Category(name = "RemoteCategory10"),
                    Category(name = "RemoteCategory11"),
                    Category(name = "RemoteCategory12"),
                    Category(name = "RemoteCategory13"),
                    Category(name = "RemoteCategory14"),
                    Category(name = "RemoteCategory15"),
                    Category(name = "RemoteCategory16"),
                    Category(name = "RemoteCategory17"),
                    Category(name = "RemoteCategory18"),
                    Category(name = "RemoteCategory19"),
                    Category(name = "RemoteCategory20"),
                )

                categoriesRepository.saveAll(categories)
            }
        }
    }
}