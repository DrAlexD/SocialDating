package xelagurd.socialdating.server

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import xelagurd.socialdating.server.FakeUsersData.toUsersWithNullIds
import xelagurd.socialdating.server.repository.UsersRepository

@Profile("!test")
@Configuration
class DevelopmentConfig {

    @Bean
    fun dataLoader(
        usersRepository: UsersRepository
    ) = CommandLineRunner {
        val existingUsers = usersRepository.findAll().toList()

        if (existingUsers.isEmpty()) {
            usersRepository.saveAll(FakeUsersData.users.toUsersWithNullIds())
        }
    }
}