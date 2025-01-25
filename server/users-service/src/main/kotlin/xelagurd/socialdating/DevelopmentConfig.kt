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
            val existingUsers = usersRepository.findAll().toList()

            if (existingUsers.isEmpty()) {
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
                    ),
                    User(
                        name = "Denis",
                        gender = Gender.MALE,
                        username = "username2",
                        password = "password2",
                        email = "email2@gmail.com",
                        age = 27,
                        city = "St. Petersburg",
                        purpose = Purpose.FRIENDS,
                        activity = 50
                    ),
                    User(
                        name = "Andrey",
                        gender = Gender.MALE,
                        username = "username3",
                        password = "password3",
                        email = "email3@gmail.com",
                        age = 28,
                        city = "Moscow",
                        purpose = Purpose.RELATIONSHIPS,
                        activity = 50
                    ),
                )

                usersRepository.saveAll(users)
            }
        }
    }
}