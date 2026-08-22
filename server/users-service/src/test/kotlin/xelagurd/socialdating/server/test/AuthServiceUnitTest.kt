package xelagurd.socialdating.server.test

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import io.jsonwebtoken.Claims
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.security.JwtGenerationUtils
import xelagurd.socialdating.server.service.AuthService

@ExtendWith(MockKExtension::class)
class AuthServiceUnitTest {

    @MockK
    private lateinit var usersRepository: UsersRepository

    @MockK
    private lateinit var jwtUtils: JwtGenerationUtils

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var userDetailsService: UserDetailsService

    @MockK
    private lateinit var authenticationManager: AuthenticationManager

    @InjectMockKs
    private lateinit var authService: AuthService

    private val user = FakeUsersData.users[0]
    private val accessToken = "accessToken"
    private val refreshToken = "refreshToken"

    private fun registrationDetails(email: String? = "email@gmail.com") =
        RegistrationDetails(
            name = "name",
            gender = Gender.MALE,
            username = "username",
            password = "password",
            email = email,
            age = 25,
            city = "city",
            purpose = Purpose.FRIENDS
        )

    private fun mockTokenGeneration() {
        every { jwtUtils.generateAccessToken(user.username, user.role, user.id!!) } returns accessToken
        every { jwtUtils.generateRefreshToken(user.username) } returns refreshToken
    }

    @Test
    fun loginUser_validCredentials_returnsAuthResponse() {
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns user
        every { authenticationManager.authenticate(any()) } returns authentication
        mockTokenGeneration()

        val result = authService.loginUser(LoginDetails(user.username, "password"))

        assertEquals(user, result.user)
        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)

        verify(exactly = 1) { authenticationManager.authenticate(any()) }
        verify(exactly = 1) { jwtUtils.generateAccessToken(user.username, user.role, user.id!!) }
        verify(exactly = 1) { jwtUtils.generateRefreshToken(user.username) }
        confirmVerified(authenticationManager, jwtUtils)
    }

    @Test
    fun registerUser_newUser_savesAndReturnsAuthResponse() {
        val registrationDetails = registrationDetails()
        val savedUserSlot = slot<User>()
        every { usersRepository.findByUsername(registrationDetails.username) } returns null
        every { usersRepository.findByEmail(registrationDetails.email!!) } returns null
        every { passwordEncoder.encode(registrationDetails.password) } returns "encodedPassword"
        every { usersRepository.save(capture(savedUserSlot)) } returns user
        mockTokenGeneration()

        val result = authService.registerUser(registrationDetails)

        assertEquals(user, result.user)
        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
        assertEquals("encodedPassword", savedUserSlot.captured.password)
        assertEquals(Role.USER, savedUserSlot.captured.role)

        verify(exactly = 1) { usersRepository.findByUsername(registrationDetails.username) }
        verify(exactly = 1) { usersRepository.findByEmail(registrationDetails.email!!) }
        verify(exactly = 1) { usersRepository.save(any()) }
        confirmVerified(usersRepository)
    }

    @Test
    fun registerUser_nullEmail_skipsEmailCheckAndSaves() {
        val registrationDetails = registrationDetails(email = null)
        every { usersRepository.findByUsername(registrationDetails.username) } returns null
        every { passwordEncoder.encode(any()) } returns "encodedPassword"
        every { usersRepository.save(any()) } returns user
        mockTokenGeneration()

        val result = authService.registerUser(registrationDetails)

        assertEquals(user, result.user)

        verify(exactly = 1) { usersRepository.findByUsername(registrationDetails.username) }
        verify(exactly = 0) { usersRepository.findByEmail(any()) }
        verify(exactly = 1) { usersRepository.save(any()) }
        confirmVerified(usersRepository)
    }

    @Test
    fun registerUser_existingUsername_throwsIllegalArgument() {
        val registrationDetails = registrationDetails()
        every { usersRepository.findByUsername(registrationDetails.username) } returns user

        assertThrows(IllegalArgumentException::class.java) {
            authService.registerUser(registrationDetails)
        }

        verify(exactly = 1) { usersRepository.findByUsername(registrationDetails.username) }
        confirmVerified(usersRepository)
    }

    @Test
    fun registerUser_existingEmail_throwsIllegalArgument() {
        val registrationDetails = registrationDetails()
        every { usersRepository.findByUsername(registrationDetails.username) } returns null
        every { usersRepository.findByEmail(registrationDetails.email!!) } returns user

        assertThrows(IllegalArgumentException::class.java) {
            authService.registerUser(registrationDetails)
        }

        verify(exactly = 1) { usersRepository.findByUsername(registrationDetails.username) }
        verify(exactly = 1) { usersRepository.findByEmail(registrationDetails.email!!) }
        confirmVerified(usersRepository)
    }

    @Test
    fun refreshToken_validToken_returnsAuthResponse() {
        val refreshTokenDetails = RefreshTokenDetails("token")
        val claims = mockk<Claims>()
        every { jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken) } returns true
        every { jwtUtils.isRefreshTokenValid(refreshTokenDetails.refreshToken) } returns true
        every { jwtUtils.getClaims(refreshTokenDetails.refreshToken) } returns claims
        every { claims.subject } returns user.username
        every { userDetailsService.loadUserByUsername(user.username) } returns user
        mockTokenGeneration()

        val result = authService.refreshToken(refreshTokenDetails)

        assertEquals(user, result.user)
        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)

        verify(exactly = 1) { userDetailsService.loadUserByUsername(user.username) }
        verify(exactly = 1) { jwtUtils.generateAccessToken(user.username, user.role, user.id!!) }
        verify(exactly = 1) { jwtUtils.generateRefreshToken(user.username) }
    }

    @Test
    fun refreshToken_notRefreshToken_throwsBadCredentials() {
        val refreshTokenDetails = RefreshTokenDetails("token")
        every { jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken) } returns false

        assertThrows(BadCredentialsException::class.java) {
            authService.refreshToken(refreshTokenDetails)
        }

        verify(exactly = 1) { jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken) }
    }

    @Test
    fun refreshToken_expiredToken_throwsCredentialsExpired() {
        val refreshTokenDetails = RefreshTokenDetails("token")
        every { jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken) } returns true
        every { jwtUtils.isRefreshTokenValid(refreshTokenDetails.refreshToken) } returns false

        assertThrows(CredentialsExpiredException::class.java) {
            authService.refreshToken(refreshTokenDetails)
        }

        verify(exactly = 1) { jwtUtils.isRefreshToken(refreshTokenDetails.refreshToken) }
        verify(exactly = 1) { jwtUtils.isRefreshTokenValid(refreshTokenDetails.refreshToken) }
    }
}
