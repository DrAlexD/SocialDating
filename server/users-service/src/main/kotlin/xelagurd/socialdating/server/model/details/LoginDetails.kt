package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginDetails(
    @field:NotBlank
    @field:Size(max = 100)
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, numbers and underscores")
    val username: String,

    @field:NotBlank
    @field:Size(max = 100)
    val password: String
)