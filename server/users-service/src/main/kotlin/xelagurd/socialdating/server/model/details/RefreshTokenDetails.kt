package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank

data class RefreshTokenDetails(
    @field:NotBlank
    val refreshToken: String
)