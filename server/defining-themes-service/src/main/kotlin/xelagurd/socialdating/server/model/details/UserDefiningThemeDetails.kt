package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import xelagurd.socialdating.server.model.UserDefiningTheme

data class UserDefiningThemeDetails(
    @field:Min(value = 0)
    @field:Max(value = 100)
    val value: Int,

    @field:Min(value = 0)
    @field:Max(value = 100)
    val interest: Int,

    @field:Min(value = 1)
    val userCategoryId: Int,

    @field:Min(value = 1)
    val definingThemeId: Int
) {
    fun toUserDefiningTheme(): UserDefiningTheme =
        UserDefiningTheme(
            value = value,
            interest = interest,
            userCategoryId = userCategoryId,
            definingThemeId = definingThemeId
        )
}