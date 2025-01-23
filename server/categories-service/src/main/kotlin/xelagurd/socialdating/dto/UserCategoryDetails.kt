package xelagurd.socialdating.dto

data class UserCategoryDetails(
    var interest: Int,
    var userId: Int,
    var categoryId: Int
) {
    fun toUserCategory(): UserCategory =
        UserCategory(
            interest = interest,
            userId = userId,
            categoryId = categoryId
        )
}