package xelagurd.socialdating.server.model.dto

data class UserDefiningThemeDto(
    val id: Int,
    val value: Int,
    val interest: Int,
    val userId: Int,
    val definingThemeId: Int
)
