package xelagurd.socialdating.server.test

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import xelagurd.socialdating.server.repository.UsersRepository

/**
 * Provides the authentication beans of [xelagurd.socialdating.server.security.AuthSecurityConfig],
 * which is disabled in the test profile.
 */
@TestConfiguration
class AuthTestConfig(
    private val usersRepository: UsersRepository
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12)

    @Bean
    fun userDetailsService(): UserDetailsService =
        UserDetailsService { username ->
            usersRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User didn't found by username: $username")
        }

    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager =
        authenticationConfiguration.authenticationManager
}
