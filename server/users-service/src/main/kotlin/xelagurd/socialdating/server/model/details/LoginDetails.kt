package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.PASSWORD_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PASSWORD_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.USERNAME_PATTERN

data class LoginDetails(
    @field:NotBlank
    @field:Size(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX)
    @field:Pattern(regexp = USERNAME_PATTERN, message = "can contain only letters, numbers and underscores")
    val username: String,

    @field:NotBlank
    @field:Size(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    val password: String
) {
    override fun toString() = "LoginDetails(username=$username)"
}