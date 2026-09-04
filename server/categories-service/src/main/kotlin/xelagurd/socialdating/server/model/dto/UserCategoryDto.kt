package xelagurd.socialdating.server.model.dto

data class UserCategoryDto(
    val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int
)
