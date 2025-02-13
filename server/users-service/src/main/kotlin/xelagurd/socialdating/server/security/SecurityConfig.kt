package xelagurd.socialdating.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import xelagurd.socialdating.server.repository.UsersRepository

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val usersRepository: UsersRepository,
    private val jwtUtils: JwtUtils
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { username ->
        usersRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found by username: $username")
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authProvider = DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailsService())
            setPasswordEncoder(passwordEncoder())
        }

        return ProviderManager(authProvider)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .logout { it.disable() }
            .addFilterBefore(
                JwtFilter(jwtUtils, userDetailsService()),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .requestCache { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/**").permitAll()
                it.requestMatchers("/api/v1/users/auth/**").permitAll()
                it.anyRequest().authenticated()
            }

        return http.build()
    }
}