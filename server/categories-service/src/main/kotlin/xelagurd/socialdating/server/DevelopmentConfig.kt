package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeCategoriesData.toCategoriesWithNullIds
import xelagurd.socialdating.server.FakeCategoriesData.toUserCategoriesWithNullIds
import xelagurd.socialdating.server.repository.CategoriesRepository
import xelagurd.socialdating.server.repository.UserCategoriesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        categoriesRepository: CategoriesRepository,
        userCategoriesRepository: UserCategoriesRepository
    ) = CommandLineRunner {
        val existingCategories = categoriesRepository.findAll().toList()

        if (existingCategories.isEmpty()) {
            categoriesRepository.saveAll(FakeCategoriesData.categories.toCategoriesWithNullIds())
            userCategoriesRepository.saveAll(FakeCategoriesData.userCategories.toUserCategoriesWithNullIds())
        }
    }
}