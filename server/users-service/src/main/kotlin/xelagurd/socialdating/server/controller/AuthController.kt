package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.model.additional.AuthResponse
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.service.AuthService

@RestController
@RequestMapping(path = ["/api/v1/users/auth"], produces = ["application/json"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun loginUser(@RequestBody loginDetails: LoginDetails): AuthResponse {
        return authService.loginUser(loginDetails)
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody registrationDetails: RegistrationDetails): AuthResponse {
        return authService.registerUser(registrationDetails)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody refreshTokenDetails: RefreshTokenDetails): AuthResponse {
        return authService.refreshToken(refreshTokenDetails)
    }
}