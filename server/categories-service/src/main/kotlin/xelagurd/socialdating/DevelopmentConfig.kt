package xelagurd.socialdating

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.UserCategory
import xelagurd.socialdating.repository.CategoriesRepository
import xelagurd.socialdating.repository.UserCategoriesRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        categoriesRepository: CategoriesRepository,
        userCategoriesRepository: UserCategoriesRepository
    ): CommandLineRunner {
        return CommandLineRunner {
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

            val userCategories = listOf(
                UserCategory(interest = 10, userId = 1, categoryId = 1),
                UserCategory(interest = 5, userId = 1, categoryId = 1),
                UserCategory(interest = 30, userId = 1, categoryId = 1),
            )

            userCategoriesRepository.saveAll(userCategories)
        }
    }
}