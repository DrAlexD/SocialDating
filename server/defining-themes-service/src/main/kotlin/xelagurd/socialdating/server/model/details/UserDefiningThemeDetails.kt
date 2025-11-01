package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import xelagurd.socialdating.server.model.UserDefiningTheme

data class UserDefiningThemeDetails(
    @field:PositiveOrZero
    @field:Max(100)
    val value: Int,

    @field:PositiveOrZero
    @field:Max(100)
    val interest: Int,

    @field:Positive
    val userId: Int,

    @field:Positive
    val definingThemeId: Int
) {
    fun toUserDefiningTheme() =
        UserDefiningTheme(
            value = value,
            interest = interest,
            userId = userId,
            definingThemeId = definingThemeId
        )
}