package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.LENGTH_MAX

data class LoginDetails(
    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, numbers and underscores")
    val username: String,

    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val password: String
)