package xelagurd.socialdating

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.dto.Gender
import xelagurd.socialdating.dto.Purpose
import xelagurd.socialdating.dto.User
import xelagurd.socialdating.repository.UsersRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        usersRepository: UsersRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val users = listOf(
                User(
                    1, "Alexander", Gender.MALE, "username1", "password1", "email1@gmail.com",
                    25, "Moscow", Purpose.ALL_AT_ONCE, 50
                )
            )

            usersRepository.saveAll(users)
        }
    }
}