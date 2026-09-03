package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Positive

data class DefiningThemeOrderDetails(
    @field:Positive
    val definingThemeId: Int,

    @field:Positive
    val orderNumber: Int
)
