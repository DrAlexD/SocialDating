package xelagurd.socialdating.server.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.ExpiredTokenException
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.exception.InvalidTokenException
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.model.dto.AuthDto
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.security.JwtGenerationUtils

@Service
class AuthService(
    private val usersRepository: UsersRepository,
    private val jwtUtils: JwtGenerationUtils,
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsService: UserDetailsService,
    private val authenticationManager: AuthenticationManager
) {

    fun loginUser(loginDetails: LoginDetails): AuthDto {
        val auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginDetails.username,
                loginDetails.password
            )
        )
        val user = auth.principal as User

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role, user.id!!)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthDto(user.toUserDto(), accessToken, refreshToken)
    }

    fun registerUser(registrationDetails: RegistrationDetails): AuthDto {
        if (usersRepository.findByUsername(registrationDetails.username) != null) {
            throw InvalidDataException("error.user.usernameAlreadyExists")
        }

        if (registrationDetails.email != null && usersRepository.findByEmail(registrationDetails.email) != null) {
            throw InvalidDataException("error.user.emailAlreadyExists")
        }

        val user = usersRepository.save(registrationDetails.toUser(passwordEncoder))

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role, user.id!!)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthDto(user.toUserDto(), accessToken, refreshToken)
    }

    fun refreshToken(refreshTokenDetails: RefreshTokenDetails): AuthDto {
        if (!jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken)) {
            throw InvalidTokenException()
        }

        if (!jwtUtils.isRefreshTokenValid(refreshTokenDetails.refreshToken)) {
            throw ExpiredTokenException()
        }

        val claims = jwtUtils.getClaims(refreshTokenDetails.refreshToken)
        val username = claims.subject

        val userDetails = userDetailsService.loadUserByUsername(username)
        val user = userDetails as User

        val accessToken = jwtUtils.generateAccessToken(user.username, user.role, user.id!!)
        val refreshToken = jwtUtils.generateRefreshToken(user.username)

        return AuthDto(user.toUserDto(), accessToken, refreshToken)
    }
}