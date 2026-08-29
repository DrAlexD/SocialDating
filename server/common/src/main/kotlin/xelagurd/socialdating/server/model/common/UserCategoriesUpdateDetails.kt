package xelagurd.socialdating.server.model.common

data class UserCategoriesUpdateDetails(
    val userId: Int,
    val categories: List<CategoryUpdateDetails>
)
