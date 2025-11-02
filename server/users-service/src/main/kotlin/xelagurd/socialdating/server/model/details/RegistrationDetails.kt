package xelagurd.socialdating.server.model.details

import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role

data class RegistrationDetails(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    val gender: Gender,

    @field:NotBlank
    @field:Size(max = 100)
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, numbers and underscores")
    val username: String,

    @field:NotBlank
    @field:Size(max = 100)
    val password: String,

    @field:NotBlank
    @field:Size(max = 100)
    val repeatedPassword: String,

    @field:Size(max = 100)
    @field:Email
    val email: String?,

    @field:Min(18)
    @field:Max(99)
    val age: Int,

    @field:NotBlank
    @field:Size(max = 100)
    val city: String,

    val purpose: Purpose
) {
    fun toUser(passwordEncoder: PasswordEncoder): User =
        User(
            name = name,
            gender = gender,
            username = username,
            password = passwordEncoder.encode(password),
            email = email,
            age = age,
            city = city,
            purpose = purpose,
            role = Role.USER
        )
}