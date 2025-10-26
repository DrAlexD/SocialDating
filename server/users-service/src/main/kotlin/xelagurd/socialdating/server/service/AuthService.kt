package xelagurd.socialdating.server.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.additional.AuthResponse
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
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

        if (registrationDetails.password != registrationDetails.repeatedPassword) {
            throw IllegalArgumentException("Passwords do not match")
        }

        val user = usersRepository.save(registrationDetails.toUser(passwordEncoder))

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthResponse(user, accessToken, refreshToken)
    }

    fun refreshToken(refreshTokenDetails: RefreshTokenDetails): AuthResponse {
        if (!jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken)) {
            throw BadCredentialsException("Token is not a valid refresh token")
        }

        if (!jwtUtils.isRefreshTokenValid(refreshTokenDetails.refreshToken)) {
            throw CredentialsExpiredException("Expired refresh token")
        }

        val claims = jwtUtils.getClaims(refreshTokenDetails.refreshToken)
        val username = claims.subject

        val userDetails = userDetailsService.loadUserByUsername(username)
        val user = userDetails as User

        val accessToken = jwtUtils.generateAccessToken(username, user.role)
        val refreshToken = jwtUtils.generateRefreshToken(username)

        return AuthResponse(user, accessToken, refreshToken)
    }
}