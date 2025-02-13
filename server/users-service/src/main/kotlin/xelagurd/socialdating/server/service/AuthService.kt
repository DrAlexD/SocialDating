package xelagurd.socialdating.server.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.additional.AuthResponse
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.security.JwtUtils

@Service
class AuthService(
    private val usersRepository: UsersRepository,
    private val jwtUtils: JwtUtils,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: UserDetailsService,
    private val authenticationManager: AuthenticationManager
) {

    fun loginUser(loginDetails: LoginDetails): AuthResponse {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginDetails.username, loginDetails.password)
        )
        val user = auth.principal as User

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthResponse(user, accessToken, refreshToken)
    }

    fun registerUser(registrationDetails: RegistrationDetails): AuthResponse {
        if (usersRepository.findByUsername(registrationDetails.username) != null) {
            throw IllegalArgumentException("User with username ${registrationDetails.username} already exists")
        }

        val user = usersRepository.save(registrationDetails.toUser(passwordEncoder))

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthResponse(user, accessToken, refreshToken)
    }

    fun refresh(refreshToken: String): String {
        val claims = jwtUtils.getClaims(refreshToken)
        val username = claims.subject

        val userDetails = userDetailsService.loadUserByUsername(username)

        val accessToken = jwtUtils.generateAccessToken(username, (userDetails as User).role)

        return accessToken
    }
}