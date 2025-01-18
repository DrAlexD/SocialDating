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
            categoriesRepository.save(Category(name = "RemoteCategory1"))
            categoriesRepository.save(Category(name = "RemoteCategory2"))
            categoriesRepository.save(Category(name = "RemoteCategory3"))
            categoriesRepository.save(Category(name = "RemoteCategory4"))
            categoriesRepository.save(Category(name = "RemoteCategory5"))
            categoriesRepository.save(Category(name = "RemoteCategory6"))
            categoriesRepository.save(Category(name = "RemoteCategory7"))
            categoriesRepository.save(Category(name = "RemoteCategory8"))
            categoriesRepository.save(Category(name = "RemoteCategory9"))
            categoriesRepository.save(Category(name = "RemoteCategory10"))
            categoriesRepository.save(Category(name = "RemoteCategory11"))
            categoriesRepository.save(Category(name = "RemoteCategory12"))
            categoriesRepository.save(Category(name = "RemoteCategory13"))
            categoriesRepository.save(Category(name = "RemoteCategory14"))
            categoriesRepository.save(Category(name = "RemoteCategory15"))
            categoriesRepository.save(Category(name = "RemoteCategory16"))
            categoriesRepository.save(Category(name = "RemoteCategory17"))
            categoriesRepository.save(Category(name = "RemoteCategory18"))
            categoriesRepository.save(Category(name = "RemoteCategory19"))
            categoriesRepository.save(Category(name = "RemoteCategory20"))
        }
    }
}