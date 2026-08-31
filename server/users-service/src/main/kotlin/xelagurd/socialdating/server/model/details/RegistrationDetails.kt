package xelagurd.socialdating.server.model.details

import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.CITY_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.CITY_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.EMAIL_PATTERN
import xelagurd.socialdating.server.model.DefaultDataProperties.NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.PASSWORD_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PASSWORD_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_PATTERN
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.validation.TrimmedSize

data class RegistrationDetails(
    @field:TrimmedSize(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    val name: String,

    val gender: Gender,

    @field:NotBlank
    @field:Size(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX)
    @field:Pattern(regexp = USERNAME_PATTERN, message = "can contain only letters, numbers and underscores")
    val username: String,

    @field:NotBlank
    @field:Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    val password: String,

    @field:TrimmedSize(min = EMAIL_LENGTH_MIN, max = EMAIL_LENGTH_MAX)
    @field:Pattern(regexp = EMAIL_PATTERN, message = "must be a well-formed email address")
    val email: String?,

    @field:Min(AGE_MIN.toLong())
    @field:Max(AGE_MAX.toLong())
    val age: Int,

    @field:TrimmedSize(min = CITY_LENGTH_MIN, max = CITY_LENGTH_MAX)
    val city: String,

    val purpose: Purpose
) {
    fun toUser(passwordEncoder: PasswordEncoder) =
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

    override fun toString() = "RegistrationDetails(name=$name, gender=$gender, username=$username, email=$email, " +
            "age=$age, city=$city, purpose=$purpose)"
}
