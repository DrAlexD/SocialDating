package xelagurd.socialdating

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.repository.CategoriesRepository

@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        categoriesRepository: CategoriesRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            categoriesRepository.save(Category(name = "Category1"))
            categoriesRepository.save(Category(name = "Category2"))
            categoriesRepository.save(Category(name = "Category3"))
            categoriesRepository.save(Category(name = "Category4"))
            categoriesRepository.save(Category(name = "Category5"))
        }
    }
}