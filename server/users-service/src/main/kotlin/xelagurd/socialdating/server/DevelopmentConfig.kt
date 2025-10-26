package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.repository.UsersRepository

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
                        password = $$"$2a$10$VlL1TDtoHSflx3dUMswP1eJ24xh5IgRVll5JHp9a24mpsArAZQjnm",
                        email = "email1@gmail.com",
                        age = 25,
                        city = "Moscow",
                        purpose = Purpose.ALL_AT_ONCE,
                        activity = 50,
                        role = Role.ADMIN
                    ),
                    User(
                        name = "Denis",
                        gender = Gender.MALE,
                        username = "username2",
                        password = $$"$2a$10$pNja5Q6J680N2HYc2VC7wOza5WoFO6O8e65D9Qwom7OjZkNE3uLe6",
                        email = "email2@gmail.com",
                        age = 27,
                        city = "St. Petersburg",
                        purpose = Purpose.FRIENDS,
                        activity = 50,
                        role = Role.USER
                    ),
                    User(
                        name = "Andrey",
                        gender = Gender.MALE,
                        username = "username3",
                        password = $$"$2a$10$wfn84flRD.QlmGV4.EGVeO9fXm8sDl9u8.0Cq0MwIq67lgTswjL.e",
                        email = "email3@gmail.com",
                        age = 28,
                        city = "Moscow",
                        purpose = Purpose.RELATIONSHIPS,
                        activity = 50,
                        role = Role.USER
                    ),
                )

                usersRepository.saveAll(users)
            }
        }
    }
}