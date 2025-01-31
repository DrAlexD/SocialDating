package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.UserCategory

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