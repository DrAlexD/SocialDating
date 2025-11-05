package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeCategoriesData.withNullIds
import xelagurd.socialdating.server.repository.CategoriesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        categoriesRepository: CategoriesRepository
    ) = CommandLineRunner {
        val existingCategories = categoriesRepository.findAll().toList()

        if (existingCategories.isEmpty()) {
            categoriesRepository.saveAll(FakeCategoriesData.categories.withNullIds())
        }
    }
}