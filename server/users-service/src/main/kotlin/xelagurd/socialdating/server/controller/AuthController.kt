package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.additional.AuthResponse
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.service.AuthService

@RestController
@RequestMapping(path = ["/users/auth"], produces = ["application/json"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun loginUser(@RequestBody @Valid loginDetails: LoginDetails): AuthResponse {
        return authService.loginUser(loginDetails)
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid registrationDetails: RegistrationDetails): AuthResponse {
        return authService.registerUser(registrationDetails)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid refreshTokenDetails: RefreshTokenDetails): AuthResponse {
        return authService.refreshToken(refreshTokenDetails)
    }
}