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
                    name = "Alexander",
                    gender = Gender.MALE,
                    username = "username1",
                    password = "password1",
                    email = "email1@gmail.com",
                    age = 25,
                    city = "Moscow",
                    purpose = Purpose.ALL_AT_ONCE,
                    activity = 50
                )
            )

            usersRepository.saveAll(users)
        }
    }
}