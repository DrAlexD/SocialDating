package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.UserDefiningTheme

data class UserDefiningThemeDetails(
    val value: Int,
    val interest: Int,
    val userCategoryId: Int,
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