package xelagurd.socialdating.dto

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