package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.jsonwebtoken.MalformedJwtException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.controller.AuthController
import xelagurd.socialdating.server.model.additional.AuthResponse
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.service.AuthService

@WebMvcTest(AuthController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class AuthControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var authService: AuthService

    private val objectMapper = jacksonObjectMapper()

    private val user = FakeUsersData.userResponses[0]
    private val authResponse = AuthResponse(user, "accessToken", "refreshToken")

    private val loginDetails = LoginDetails(username = "username1", password = "password")
    private val registrationDetails = RegistrationDetails(
        name = "name",
        gender = Gender.MALE,
        username = "username1",
        password = "password",
        email = "email@gmail.com",
        age = 25,
        city = "city",
        purpose = Purpose.FRIENDS
    )
    private val refreshTokenDetails = RefreshTokenDetails(refreshToken = "someRefreshToken")

    @Test
    fun loginUser_validData_ok() {
        every { authService.loginUser(any()) } returns authResponse

        mockMvc.perform(
            post("/users/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.refreshToken").value("refreshToken"))

        verify(exactly = 1) { authService.loginUser(any()) }
        confirmVerified(authService)
    }

    @Test
    fun registerUser_validData_ok() {
        every { authService.registerUser(any()) } returns authResponse

        mockMvc.perform(
            post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { authService.registerUser(any()) }
        confirmVerified(authService)
    }

    @Test
    fun registerUser_existingUsername_badRequest() {
        every { authService.registerUser(any()) } throws
                IllegalArgumentException("User with this username already exists")

        mockMvc.perform(
            post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDetails))
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 1) { authService.registerUser(any()) }
        confirmVerified(authService)
    }

    @Test
    fun refreshToken_validData_ok() {
        every { authService.refreshToken(any()) } returns authResponse

        mockMvc.perform(
            post("/users/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { authService.refreshToken(any()) }
        confirmVerified(authService)
    }

    @Test
    fun refreshToken_notRefreshToken_unauthorized() {
        every { authService.refreshToken(any()) } throws
                BadCredentialsException("Token is not a valid refresh token")

        mockMvc.perform(
            post("/users/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenDetails))
        )
            .andExpect(status().isUnauthorized)

        verify(exactly = 1) { authService.refreshToken(any()) }
        confirmVerified(authService)
    }

    @Test
    fun refreshToken_invalidJwt_unauthorized() {
        every { authService.refreshToken(any()) } throws MalformedJwtException("Invalid JWT token")

        mockMvc.perform(
            post("/users/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenDetails))
        )
            .andExpect(status().isUnauthorized)

        verify(exactly = 1) { authService.refreshToken(any()) }
        confirmVerified(authService)
    }
}
